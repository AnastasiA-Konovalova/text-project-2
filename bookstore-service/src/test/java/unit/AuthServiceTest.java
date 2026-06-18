package unit;

import org.example.database.UserRepository;
import org.example.model.*;
import org.example.model.LoginUserRequest;
import org.example.model.LoginUserResponse;
import org.example.model.RegisterUserRequest;
import org.example.service.auth.AuthApiService;
import org.example.service.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthApiService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Test
    void postLoginUser_shouldReturnLoginUserResponseDto() {
        Integer userId = 1;

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("email@email.ru");
        request.setPassword("password");

        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setEmail("email@email.ru");
        entity.setPassword("encodedPassword");

        when(userRepository.findByEmail("email@email.ru")).thenReturn(Optional.of(entity));
        when(jwtService.generateToken("email@email.ru")).thenReturn("jwt-token");
        when(encoder.matches("password", "encodedPassword")).thenReturn(true);

        LoginUserResponse response = authService.loginUser(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertNotNull(response.getUser());
        assertEquals(1, response.getUser().getId());
        assertEquals("email@email.ru", response.getUser().getEmail());

        verify(userRepository).findByEmail("email@email.ru");
        verify(encoder).matches("password", "encodedPassword");
        verify(jwtService).generateToken("email@email.ru");
    }

    @Test
    void postRegisterUser() {
        Integer userId = 1;

        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("email@email.ru");
        request.setPassword("password");

        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setEmail("email@email.ru");
        entity.setPassword("encodedPassword");

        when(userRepository.save(any())).thenReturn(entity);

        authService.registerUser(request);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void postLogout_shouldClearSecurityContext() {
        Authentication authentication = mock(Authentication.class);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        authService.postLogout();

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
