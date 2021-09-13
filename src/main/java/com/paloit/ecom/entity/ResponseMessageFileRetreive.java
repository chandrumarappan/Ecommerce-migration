package com.paloit.ecom.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseMessageFileRetreive {

  private HttpStatus httpStatus;
  private String message;
  private List<OrderDetail> orderDetailList;

  public ResponseMessageFileRetreive() {

  }

  public ResponseMessageFileRetreive(HttpStatus httpStatus, String message, List<OrderDetail> orderDetailList) {
    this.httpStatus = httpStatus;
    this.message = message;
    this.orderDetailList = orderDetailList;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<OrderDetail> getOrderDetailList() {
    return orderDetailList;
  }

  public void setOrderDetailList(List<OrderDetail> orderDetailList) {
    this.orderDetailList = orderDetailList;
  }
}
