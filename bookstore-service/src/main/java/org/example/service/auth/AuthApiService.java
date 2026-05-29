package org.example.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.database.AuthRepository;
import org.example.model.*;
import org.example.model.LoginUserRequest;
import org.example.model.LoginUserResponse;
import org.example.model.RegisterUserRequest;
import org.example.model.UserDto;
import org.example.service.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthApiService implements AuthApiInterface {

    private final AuthRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Override
    public LoginUserResponse loginUser(LoginUserRequest request) {
        AuthEntity user = repository.findByEmail(request.getEmail()).orElseThrow();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        LoginUserResponse response = new LoginUserResponse();
        response.setAccessToken(token);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        response.setUser(userDto);
        return response;
    }

    @Override
    public void postLogout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public void registerUser(RegisterUserRequest registerUserRequest) {
        AuthEntity user = new AuthEntity();
        user.setEmail(registerUserRequest.getEmail());

        user.setPassword(encoder.encode(registerUserRequest.getPassword()));

        repository.save(user);
    }
}
