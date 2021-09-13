package com.paloit.ecom.exception;

import com.paloit.ecom.entity.ResponseMessageFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadExceptionAdvice.class);

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ResponseMessageFileUpload> handleMaxSizeException(MaxUploadSizeExceededException maxUploadSizeExceededException) {

    logger.error("Uploaded file size exceeds the maximum size", maxUploadSizeExceededException);
    ResponseMessageFileUpload responseMessageFileUpload = new ResponseMessageFileUpload(HttpStatus.EXPECTATION_FAILED, "File is too large");
    return new ResponseEntity<> (responseMessageFileUpload, responseMessageFileUpload.getHttpStatus());
  }
}