package com.registry.exception;

public enum GlobalErrorCodes implements ErrorCodes {

  DEFAULT_GLOBAL_ERROR_CODE("GB0000"),      // status 500
  UNKNOWN_SERVER_ERROR_CODE("GB0001"),      // status 500
  NOT_FOUND_CODE("GB0002"),                 // status 404
  BAD_REQUEST_CODE("GB0003"),               // status 400
  ACCESS_DENIED_CODE("GB0004"),             // status 403
  AUTH_ERROR_CODE("GB0005"),                // status 401
  INVALID_USERNAME_PASSWORD_CODE("GB0006"), // status 400
  INVALID_TOKEN_CODE("GB0007"),             // status 401
  SERVICE_UNAVAILABLE("GB0008");            // status 503

  String errorCode;

  GlobalErrorCodes(String errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public String getCode() {
    return errorCode;
  }
}
