package pl.exceptionhandled.bookingapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.exceptionhandled.bookingapi.common.exception.EmailIsAlreadyTakenException;
import pl.exceptionhandled.bookingapi.security.dto.LoginRequest;
import pl.exceptionhandled.bookingapi.security.dto.LoginResponse;
import pl.exceptionhandled.bookingapi.security.dto.RegisterRequest;
import pl.exceptionhandled.bookingapi.security.dto.RegisterResponse;
import pl.exceptionhandled.bookingapi.user.Role;
import pl.exceptionhandled.bookingapi.user.User;
import pl.exceptionhandled.bookingapi.user.UserRepository;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        var normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(normalizedEmail)){
            throw new EmailIsAlreadyTakenException(normalizedEmail);
        }

        var hashedPassword = passwordEncoder.encode(request.password());

        var user = User.builder()
                .email(normalizedEmail)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();

        User savedUser;

        try {
            savedUser = userRepository.saveAndFlush(user);
            // samo save() moze tylko dodac encje do persistence context, faktyczny insert
            // dzieje sie dopiero przy flush pod koneic transakcji a wyjatek wtedy moze poleciec po wyjsciu z try catch
        } catch (DataIntegrityViolationException e) {
            throw new EmailIsAlreadyTakenException(normalizedEmail);
        }

        return new RegisterResponse(savedUser.getId(), savedUser.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        var normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        return new LoginResponse(jwtService.generateToken(authentication));
    }
}
