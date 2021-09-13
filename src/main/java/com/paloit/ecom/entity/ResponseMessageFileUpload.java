package com.paloit.ecom.entity;


import org.springframework.http.HttpStatus;

public class ResponseMessageFileUpload {

  private HttpStatus httpStatus;
  private String message;

  public ResponseMessageFileUpload() {

  }

  public ResponseMessageFileUpload(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
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
}
