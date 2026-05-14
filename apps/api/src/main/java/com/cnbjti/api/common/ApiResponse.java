package com.cnbjti.api.common;

import java.util.UUID;

public record ApiResponse<T>(String code, String message, T data, String requestId) {

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>("OK", "success", data, newRequestId());
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(code, message, null, newRequestId());
  }

  private static String newRequestId() {
    return "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }
}
