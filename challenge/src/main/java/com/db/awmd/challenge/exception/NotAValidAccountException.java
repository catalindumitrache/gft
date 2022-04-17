package com.db.awmd.challenge.exception;

public class NotAValidAccountException extends RuntimeException {

  public NotAValidAccountException(String message) {
    super(message);
  }
}
