package com.paloit.ecom.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

import com.paloit.ecom.entity.OrderDetail;
import com.paloit.ecom.entity.ResponseMessageFileUpload;
import com.paloit.ecom.repository.OrderRepository;
import com.paloit.ecom.utility.CsvUtility;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvFileUploadService {

  private static final Logger logger = LoggerFactory.getLogger(CsvFileUploadService.class);

  private ResponseMessageFileUpload responseMessageFileUpload = null;

  @Autowired
  OrderRepository orderRepository;

  public ResponseEntity<ResponseMessageFileUpload> fileLoading(MultipartFile multipartFile) {
    logger.info("Executing multipartFile load method");
    if (isValidateFile(multipartFile)) {
      List<OrderDetail> orderDetail = getOrderDetail(multipartFile);
      if(orderDetail !=  null && !orderDetail.isEmpty()) {
        insertIntoTable(orderDetail);
        logger.info("File is uploaded and loaded into the table successfully");
        responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.OK, "File is loaded into the table");
      }
    }
    return new ResponseEntity<> (responseMessageFileUpload, responseMessageFileUpload.getHttpStatus());
  }

  private boolean isValidateFile(MultipartFile multipartFile) {

    logger.info("Validating the multipartFile");
    if(multipartFile.getOriginalFilename().isEmpty()) {
      logger.error("No file is uploaded [{}]", multipartFile.isEmpty());
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.NOT_ACCEPTABLE, "No file is uploaded");
      return false;
    }
    if (multipartFile.getContentType() == null || !multipartFile.getContentType().equals(CsvUtility.CONTENT_TYPE)) {
      logger.error("File is not in csv format [{}]", multipartFile.getContentType());
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.NOT_ACCEPTABLE, "File is not in csv format");
      return false;
    }
    if (multipartFile.isEmpty()) {
      logger.error("File is empty [{}]", multipartFile.getResource().exists());
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.PRECONDITION_FAILED, "File does not have data");
      return false;
    }
    return true;
  }

  public List<OrderDetail> getOrderDetail(MultipartFile multipartFile) {
      List<OrderDetail> orderDetail = recordParsingtoObject(multipartFile);
      return orderDetail;
  }

  public List<OrderDetail> recordParsingtoObject(MultipartFile multipartFile) {
    List<OrderDetail> list_orderDetail = null;
    int rowCount = 0;
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream(), "UTF-8"));
         CSVParser csvParser = new CSVParser(bufferedReader,
                 CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim().withAllowMissingColumnNames(false));) {

      List<CSVRecord> csvRecords = validateFileContent(csvParser);
      if(csvRecords != null && !csvRecords.isEmpty()) {
        logger.info("Parsing the file content");
        list_orderDetail = new ArrayList<OrderDetail>();
        for (CSVRecord csvRecord : csvRecords) {
          rowCount ++;
          if(!isColumnDataLengthMisMatch(csvRecord, rowCount) || isEmptyData(csvRecord, rowCount)) {
            return null;
          }
          OrderDetail orderDetails = new OrderDetail(
                  Long.parseLong(csvRecord.get("Order ID")),
                  CsvUtility.getRandomNricId(),
                  csvRecord.get("Region"),
                  csvRecord.get("Country"),
                  csvRecord.get("Item Type"),
                  csvRecord.get("Sales Channel"),
                  csvRecord.get("Order Priority").charAt(0),
                  LocalDate.parse(csvRecord.get("Order Date"), CsvUtility.DATE_FORMAT),
                  LocalDate.parse(csvRecord.get("Ship Date"), CsvUtility.DATE_FORMAT),
                  Integer.parseInt(csvRecord.get("Units Sold")),
                  Double.parseDouble(csvRecord.get("Unit Price")),
                  Double.parseDouble(csvRecord.get("Unit Cost")),
                  Float.parseFloat(csvRecord.get("Total Revenue")),
                  Float.parseFloat(csvRecord.get("Total Cost")),
                  Float.parseFloat(csvRecord.get("Total Profit"))
          );
          list_orderDetail.add(orderDetails);
        }
        logger.info("Total number of row processed - {}", rowCount);
      }
    } catch(IllegalArgumentException illegalAccessException) {
      list_orderDetail = null;
      logger.error("File header has empty cell", illegalAccessException);
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.EXPECTATION_FAILED, "File header in the CSV file should not be empty");
    } catch (Exception ioException) {
      list_orderDetail = null;
      logger.error("Data in the csv file is not in the correct format at the row number {}", ioException, rowCount);
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.EXPECTATION_FAILED,"Data in the csv file is not in the expected format");
    }
    return list_orderDetail;
  }

  private boolean isColumnDataLengthMisMatch(CSVRecord csvRecord, int rowCount) {
    if(!csvRecord.isConsistent()) {
      logger.error("Column data [{}] does not match with expected column count at the row number {}", csvRecord.size(), rowCount);
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.EXPECTATION_FAILED, "Data in the column should not be empty");
      return false;
    }
    return true;
  }

  private boolean isEmptyData(CSVRecord csvRecord, int rowCount) {
    Iterator<String> iterator = csvRecord.iterator();
    while (iterator.hasNext()) {
      String element = iterator.next().trim();
      if (element == null || element.isEmpty()) {
        logger.error("Data column has empty cell at the row number {}", rowCount);
        responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.EXPECTATION_FAILED, "Data column should not be empty in csv file");
        return true;
      }
    }
    return false;
  }

  private List<CSVRecord> validateFileContent(CSVParser csvParser) {
    List<CSVRecord> csvRecords = null;
    logger.info("Validating the file content");
    if (csvParser.getHeaderNames().size() != CsvUtility.HEADER) {
      logger.error("Uploaded file header [{}] does not match with the expected column count", csvParser.getHeaderNames().size());
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.PRECONDITION_FAILED, "File header does not match with expected column count");
      return csvRecords;
    }
    try {
      csvRecords = csvParser.getRecords();
      if (csvRecords == null || csvRecords.isEmpty()) {
        throw new IOException("File does not have any data except header");
      }
    }  catch (IOException ioException) {
      responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.PRECONDITION_FAILED, ioException.getMessage());
      return csvRecords;
    }
    return csvRecords;
  }

  @PostMapping
  public void insertIntoTable(List<OrderDetail> orderDetail) {
    logger.info("Loading into the table");
    orderRepository.saveAll(orderDetail);
  }
}
