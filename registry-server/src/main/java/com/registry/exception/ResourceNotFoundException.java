package com.registry.exception;

import com.google.common.base.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.registry.exception.GlobalErrorCodes.NOT_FOUND_CODE;

/**
 * API 또는 Resource 가 존재하지 않을 경우 활용
 */
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
public class ResourceNotFoundException extends BaseException {

  public ResourceNotFoundException(String resource) {
    this(resource, null);
  }

  public ResourceNotFoundException(String resource, Throwable cause) {
    super(NOT_FOUND_CODE, String.format("Resource(%s) not Found", Preconditions.checkNotNull(resource)), cause);
  }

  public ResourceNotFoundException(Throwable cause) {
    super(NOT_FOUND_CODE, cause);
  }
}
