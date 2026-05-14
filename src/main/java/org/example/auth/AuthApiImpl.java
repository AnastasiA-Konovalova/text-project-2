package org.example.auth;

import org.example.api.AuthApi;
import org.example.model.AuthRegisterPostRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiImpl implements AuthApi {

    @Override
    public ResponseEntity<Void> authRegisterPost(AuthRegisterPostRequest authRegisterPostRequest) {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> authLogoutPost() {
        return ResponseEntity.ok()
                .header("Set-Cookie", "JSESSIONID")
                .build();
    }

    @Override
    public ResponseEntity<Void> authLoginPost(AuthRegisterPostRequest authRegisterPostRequest) {
        return ResponseEntity.ok().build();
    }


}
