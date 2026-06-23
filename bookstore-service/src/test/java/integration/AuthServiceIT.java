package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.Main;
import org.example.database.*;
import org.example.model.LoginUserRequest;
import org.example.model.RegisterUserRequest;
import org.example.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class, properties = {
        "JWT_SECRET=test-secret-key-test-secret-key-test-secret-key"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    private LoginUserRequest loginUserRequest;

    private RegisterUserRequest registerUserRequest;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        userRepository.deleteAll();

        userEntity = new UserEntity();
        userEntity.setName("Ivan");
        userEntity.setSurname("Ivanov");
        userEntity.setMiddleName(null);
        userEntity.setEmail("test@email.ru");
        userEntity.setPassword(passwordEncoder.encode("1234567L"));
        userEntity.setPhoneNumber("86451236680");

        userRepository.save(userEntity);

        loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail("test@email.ru");
        loginUserRequest.setPassword("1234567L");

        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setName("Petr");
        registerUserRequest.setSurname("Ivanov");
        registerUserRequest.setEmail("text@test.ru");
        registerUserRequest.setPassword("987654321");
        registerUserRequest.setPhoneNumber("8453246625");
    }

    @Test
    void loginUser_SuccessLogIn() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@email.ru"));
    }

    @Test
    void loginUser_UnsuccessfulLogIn_WithoutEmail() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "email": null,
          "password": "123456"
        }
        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_SuccessRegister() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_UnsuccessfulRegister_WithoutEmail() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setName("Petr");
        registerUserRequest.setSurname("Ivanov");
        registerUserRequest.setPassword("987654321");
        registerUserRequest.setPhoneNumber("8453246625");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postLogout_SuccessLogOut() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "email1@mail.ru",
                        null,
                        List.of()
                )
        );
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());
    }
}
