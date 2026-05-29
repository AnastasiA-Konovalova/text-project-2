package org.example.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.api.AuthApi;
import org.example.model.LoginUserRequest;
import org.example.model.LoginUserResponse;
import org.example.model.RegisterUserRequest;
import org.example.service.auth.AuthApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthApiImpl implements AuthApi {

    private final AuthApiService authService;

    @Override
    public ResponseEntity<LoginUserResponse> loginUser(LoginUserRequest loginUserRequest) {
        return ResponseEntity.ok(authService.loginUser(loginUserRequest));
    }

    @Override
    public ResponseEntity<Void> postLogout() {
        authService.postLogout();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> registerUser(RegisterUserRequest registerUserRequest) {
        authService.registerUser(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}