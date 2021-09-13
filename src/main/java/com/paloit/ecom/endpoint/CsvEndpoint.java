package com.paloit.ecom.endpoint;

import com.paloit.ecom.entity.ResponseMessageFileRetreive;
import com.paloit.ecom.entity.ResponseMessageFileUpload;
import com.paloit.ecom.service.CsvFileUploadService;
import com.paloit.ecom.service.CsvResultRetreiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/ecom/csv")
public class CsvEndpoint {

  private static final Logger logger = LoggerFactory.getLogger(CsvEndpoint.class);

  @Autowired
  CsvFileUploadService csvFileUploadService;

  @Autowired
  private CsvResultRetreiveService csvResultRetreiveService;

  @PostMapping(value ="/upload")
  @ResponseBody
  public ResponseEntity<ResponseMessageFileUpload> uploadFile(@RequestParam("csvFile") MultipartFile file) {
    return csvFileUploadService.fileLoading(file);
  }

  @GetMapping("/retrieve")
  public ResponseEntity<ResponseMessageFileRetreive> getAllOrders() {
    return csvResultRetreiveService.retrieveAllOrder();
  }
}
