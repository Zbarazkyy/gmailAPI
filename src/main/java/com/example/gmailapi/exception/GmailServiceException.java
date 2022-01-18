package com.example.gmailapi.exception;

public class GmailServiceException extends RuntimeException {
  public GmailServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
