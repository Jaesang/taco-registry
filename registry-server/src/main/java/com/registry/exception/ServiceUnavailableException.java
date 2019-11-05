package com.registry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.registry.exception.GlobalErrorCodes.SERVICE_UNAVAILABLE;


/**
 * 서버가 요청을 처리할 준비가 되지 않을 시 발생
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "Service Unavailable")
public class ServiceUnavailableException extends BaseException {

  public ServiceUnavailableException(String message) {
    super(SERVICE_UNAVAILABLE, message);
  }

  public ServiceUnavailableException(Throwable cause) {
    super(SERVICE_UNAVAILABLE, cause);
  }

  public ServiceUnavailableException(String message, Throwable cause) {
    super(SERVICE_UNAVAILABLE, message, cause);
  }
}
