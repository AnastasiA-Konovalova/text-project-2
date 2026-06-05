package org.example.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.database.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Override
    public LoginUserResponse loginUser(LoginUserRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow();
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
        UserEntity user = new UserEntity();

        user.setName(registerUserRequest.getName());
        user.setSurname(registerUserRequest.getSurname());
        user.setMiddleName(registerUserRequest.getMiddleName());
        user.setEmail(registerUserRequest.getEmail());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        user.setPassword(encoder.encode(registerUserRequest.getPassword()));

        userRepository.save(user);
    }
}