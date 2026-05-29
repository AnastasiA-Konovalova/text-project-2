package org.example.service.auth;

import org.example.model.LoginUserRequest;
import org.example.model.LoginUserResponse;
import org.example.model.RegisterUserRequest;

public interface AuthApiInterface {

    LoginUserResponse loginUser(LoginUserRequest loginUserRequest);

    void postLogout();

    void registerUser(RegisterUserRequest registerUserRequest);
}
