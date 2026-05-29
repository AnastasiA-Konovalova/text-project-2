package org.example.model;

public class AuthDto {

    public record AuthRequest(
            String login,
            String password
    ) {
    }
    public record AuthResponse(
            String token
    ) {
    }
}
