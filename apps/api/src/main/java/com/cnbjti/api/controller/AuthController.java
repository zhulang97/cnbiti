package com.cnbjti.api.controller;

import com.cnbjti.api.common.ApiResponse;
import com.cnbjti.api.model.ApiModels.LoginRequest;
import com.cnbjti.api.model.ApiModels.LoginResponse;
import com.cnbjti.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(authService.login(request.username(), request.password()));
  }
}
