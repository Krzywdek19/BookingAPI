package pl.exceptionhandled.bookingapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.exceptionhandled.bookingapi.security.dto.RegisterRequest;
import pl.exceptionhandled.bookingapi.security.dto.RegisterResponse;
import pl.exceptionhandled.bookingapi.security.dto.LoginRequest;
import pl.exceptionhandled.bookingapi.security.dto.LoginResponse;
import pl.exceptionhandled.bookingapi.common.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class AuthenticationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthenticationService authenticationService;


    @Test
    public void registerShouldReturn201WhenValidRequest() throws Exception {
        String email = "test@example.com";
        String password = "testPassword";
        var request = new RegisterRequest(email, password);
        var response = new RegisterResponse(1L, email);


        when(authenticationService.registerUser(any()))
                .thenReturn(response);

        mockMvc
                .perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void invalidRegisterRequestShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email","password":"short"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    @Test
    void loginShouldReturn200WhenCredentialsAreValid() throws Exception {
        when(authenticationService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@example.com","password":"correct-password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("token"));
    }

    @Test
    void loginShouldReturn401WhenCredentialsAreInvalid() throws Exception {
        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@example.com","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
