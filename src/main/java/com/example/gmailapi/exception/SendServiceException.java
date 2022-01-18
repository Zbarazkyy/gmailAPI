package com.example.gmailapi.exception;

public class SendServiceException extends RuntimeException{
  public SendServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
