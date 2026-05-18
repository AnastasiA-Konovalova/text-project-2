package org.example.controller.auth;

import org.example.api.AuthApi;
import org.example.model.PostRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiImpl implements AuthApi {

    @Override
    public ResponseEntity<Void> postLogin(PostRegisterRequest postRegisterRequest) {
        return ResponseEntity.ok()
                .header("Set-Cookie", "JSESSIONID")
                .build();
    }

    @Override
    public ResponseEntity<Void> postLogout() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> postRegister(PostRegisterRequest postRegisterRequest) {
        return ResponseEntity.ok().build();
    }
}