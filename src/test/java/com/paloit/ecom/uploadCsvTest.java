package com.paloit.ecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paloit.ecom.entity.OrderDetail;
import com.paloit.ecom.entity.ResponseMessageFileUpload;
import com.paloit.ecom.repository.OrderRepository;
import com.paloit.ecom.utility.CsvUtility;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class uploadCsvTest {

	private static final Logger logger = LoggerFactory.getLogger(uploadCsvTest.class);

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderRepository orderRepository;

	private ObjectMapper objectMapper;
	private MockMultipartHttpServletRequestBuilder multipartRequest;

	@BeforeEach
	public void init() {
		objectMapper = new ObjectMapper();
		multipartRequest = MockMvcRequestBuilders.multipart("/ecom/csv/upload");
	}

	@Test
	@DisplayName("Test case without request param missing")
	public void noFileProvidedTest() throws Exception{

		mockMvc.perform(multipartRequest)
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Test case with no file content type")
	public void noFileUploadedType() throws Exception{

		byte[] content = null;
		MockMultipartFile sampleFile = new MockMultipartFile("csvFile", content);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getHttpStatus());
		Assertions.assertEquals("No file is uploaded", response.getMessage());
	}

	@Test
	@DisplayName("Test case with wrong file format")
	public void fileWrongFormatTest() throws Exception{

		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.txt",
				"text/plain",
				"This is the file content".getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getHttpStatus());
		Assertions.assertEquals("File is not in csv format", response.getMessage());
	}

	@Test
	@DisplayName("Test case with empty file")
	public void emptyFileTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				"".getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("File does not have data", response.getMessage());
	}

	@Test
	@DisplayName("Test case with wrong column length")
	public void invalidColumnTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				"Id,Title,Description,Published".getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("File header does not match with expected column count", response.getMessage());
	}

	@Test
	@DisplayName("Test case with no data except header")
	public void noDataExceptHeaderTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				"Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit".getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("File does not have any data except header", response.getMessage());
	}

	@Test
	@DisplayName("Test case with empty field in the header")
	public void emptyColumnHeaderTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				"Region,,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit".getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("File header in the CSV file should not be empty", response.getMessage());
	}

	@Test
	@DisplayName("Test case with wrong data column length")
	public void wrongDataColumnLengthTest() throws Exception{
		String header = "Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit";
		String data = "Sub-Saharan,Fruits,Offline,M,7/27/2012,443368995,7/28/2012,1593,9.33,6.92,14862.69,11023.56,3839.13";
		String input = header + System.lineSeparator() + data;
		InputStream inputStream = new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8")));
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				inputStream
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("Data in the column should not be empty", response.getMessage());
	}

	@Test
	@DisplayName("Test case with an empty data")
	public void emptyDataInCellTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				("Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit" +
						"\n" +
						"Sub-Saharan,South Africa,,Offline,M,7/27/2012,443368995,7/28/2012,1593,9.33,6.92,14862.69,11023.56,3839.13").getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("Data column should not be empty in csv file", response.getMessage());
	}

	@Test
	@DisplayName("Test case with an parsing error")
	public void dateParsingTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				("Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit" +
						"\n" +
						"Sub-Saharan,South Africa,Fruits,Offline,M,7/27/2012,443368995,7/28/2012,1593,9.33,6.92,14862.69,11023.56,3839.13"
						+"\n" +
						"Middle East and North Africa,Morocco,Clothes,Online,M,19/14/2013,667593514,10/19/2013,4611,109.28,35.84,503890.08,165258.24,338631.84").getBytes()
		);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.EXPECTATION_FAILED, response.getHttpStatus());
		Assertions.assertEquals("Data in the csv file is not in the expected format", response.getMessage());
	}

	@Ignore @Test
	@DisplayName("Test with the csv file import and load into table")
	public void processAndLoadTest() throws Exception{
		MockMultipartFile sampleFile = new MockMultipartFile(
				"csvFile",
				"sampleFile.csv",
				"text/csv",
				("Region,Country,Item Type,Sales Channel,Order Priority,Order Date,Order ID,Ship Date,Units Sold,Unit Price,Unit Cost,Total Revenue,Total Cost,Total Profit" +
						"\n" +
						"Sub-Saharan,South Africa,Fruits,Offline,M,7/27/2012,987654321,7/28/2012,1593,9.33,6.92,14862.69,11023.56,3839.13"
						+"\n" +
						"Middle East and North Africa,Morocco,Clothes,Online,M,9/14/2013,654347892,10/19/2013,4611,109.28,35.84,503890.08,165258.24,338631.84").getBytes()
		);
		OrderDetail orderDetail = new OrderDetail(987654321, "S1234567T", "Middle East and North Africa",
				"South Africa", "Morocco", "Clothes", 'M', LocalDate.parse("7/27/2012",
				CsvUtility.DATE_FORMAT), LocalDate.parse("7/28/2012", CsvUtility.DATE_FORMAT),
				1593,9.33,6.92, Float.parseFloat("14862.69"), Float.parseFloat("11023.56"),
				Float.parseFloat("3839.13"));
		OrderDetail orderDetail2 = new OrderDetail(654347892, "S9874567T", "Sub-Saharan","South Africa",
				"Clothes", "Online", 'M', LocalDate.parse("9/14/2013",
				CsvUtility.DATE_FORMAT),LocalDate.parse("10/19/2013", CsvUtility.DATE_FORMAT),
				4611,109.28,35.84, Float.parseFloat("503890.08"), Float.parseFloat("165258.24"),
				Float.parseFloat("338631.84"));
		List<OrderDetail> orderDetailList = new ArrayList<>();
		orderDetailList.add(orderDetail);
		orderDetailList.add(orderDetail2);
		Mockito.when(orderRepository.saveAll(orderDetailList)).thenReturn(orderDetailList);
		MvcResult mvcResult = mockMvc.perform(multipartRequest.file(sampleFile)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileUpload response = objectMapper.readValue(contentAsString, ResponseMessageFileUpload.class);
		Assertions.assertEquals(HttpStatus.OK, response.getHttpStatus());
		Assertions.assertEquals("File is loaded into the table", response.getMessage());
	}
}
