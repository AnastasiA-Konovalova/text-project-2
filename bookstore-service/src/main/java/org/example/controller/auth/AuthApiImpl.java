package org.example.controller.auth;

import org.example.api.AuthApi;
import org.example.model.LoginUserRequest;
import org.example.model.LoginUserResponse;
import org.example.model.RegisterUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiImpl implements AuthApi {


    @Override
    public ResponseEntity<LoginUserResponse> loginUser(LoginUserRequest loginUserRequest) {
        LoginUserResponse loginUserResponse = new LoginUserResponse();
        return ResponseEntity.ok(loginUserResponse);

    }

    @Override
    public ResponseEntity<Void> postLogout() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> registerUser(RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok().build();
    }
}