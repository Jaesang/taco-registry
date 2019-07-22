package com.registry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.registry.exception.GlobalErrorCodes.ACCESS_DENIED_CODE;


/**
 * 접근 거부 오류시 발생 (Role처리 오류)
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access denied")
public class AccessDeniedException extends BaseException {

  public AccessDeniedException(String message) {
    super(ACCESS_DENIED_CODE, message);
  }

  public AccessDeniedException(Throwable cause) {
    super(ACCESS_DENIED_CODE, cause);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(ACCESS_DENIED_CODE, message, cause);
  }
}
