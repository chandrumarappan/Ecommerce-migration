package com.paloit.ecom.service;

import com.paloit.ecom.entity.OrderDetail;
import com.paloit.ecom.entity.ResponseMessageFileRetreive;
import com.paloit.ecom.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class CsvResultRetreiveService {

    private static final Logger logger = LoggerFactory.getLogger(CsvResultRetreiveService.class);

    private ResponseMessageFileRetreive responseMessageFileRetreive = null;

    @Autowired
    OrderRepository orderRepository;

    public ResponseEntity<ResponseMessageFileRetreive> retrieveAllOrder() {
        List<OrderDetail> orderDetailList = retrieveResult();
        if(orderDetailList == null || orderDetailList.isEmpty()) {
            logger.error("No data available in the table");
            responseMessageFileRetreive = new ResponseMessageFileRetreive(HttpStatus.NOT_FOUND, "No data is available in the table", orderDetailList);
        } else {
            logger.error("Retrieved all the data from the table");
            responseMessageFileRetreive = new ResponseMessageFileRetreive(HttpStatus.OK, "Retrieved all the data from the table", orderDetailList);
        }
        return new ResponseEntity<>(responseMessageFileRetreive, responseMessageFileRetreive.getHttpStatus());
    }

    @GetMapping
    public List<OrderDetail> retrieveResult() {
        logger.info("Retrieving data from the table");
        return orderRepository.findAll();
    }
}
