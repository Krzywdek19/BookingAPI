package pl.exceptionhandled.bookingapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    @Value("${app.jwt.expiration}")
    private long expiration;


    public String generateToken(Authentication authentication) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("booking-api")
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .claim(
                        "roles",
                        authentication.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
