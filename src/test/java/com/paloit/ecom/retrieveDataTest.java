package com.paloit.ecom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paloit.ecom.entity.OrderDetail;
import com.paloit.ecom.entity.ResponseMessageFileRetreive;
import com.paloit.ecom.repository.OrderRepository;
import com.paloit.ecom.utility.CsvUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class retrieveDataTest {

	private static final Logger logger = LoggerFactory.getLogger(retrieveDataTest.class);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	OrderRepository orderRepository;

	@Test
	@DisplayName("Test case to retrieve empty data")
	public void emptyResultTest() throws Exception{
		Mockito.when(orderRepository.findAll()).thenReturn(null);
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/ecom/csv/retrieve").contentType(MediaType.APPLICATION_JSON)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileRetreive response = objectMapper.readValue(contentAsString, ResponseMessageFileRetreive.class);
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
		Assertions.assertEquals("No data is available in the table", response.getMessage());
	}

	@Test
	@DisplayName("Test case to retrieve empty data")
	public void retrieveResultTest() throws Exception{
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
		Mockito.when(orderRepository.findAll()).thenReturn(orderDetailList);
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/ecom/csv/retrieve").contentType(MediaType.APPLICATION_JSON)).andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		ResponseMessageFileRetreive response = objectMapper.readValue(contentAsString, ResponseMessageFileRetreive.class);
		Assertions.assertEquals(HttpStatus.OK, response.getHttpStatus());
		Assertions.assertEquals("Retrieved all the data from the table", response.getMessage());
		Assertions.assertEquals(987654321, response.getOrderDetailList().get(0).getOrderId());
		Assertions.assertEquals("S9874567T", response.getOrderDetailList().get(1).getNricId());
	}
}
