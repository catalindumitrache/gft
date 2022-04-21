package com.db.awmd.challenge.exception;

public class ZeroAmountException extends RuntimeException {

  public ZeroAmountException(String message) {
    super(message);
  }
}
