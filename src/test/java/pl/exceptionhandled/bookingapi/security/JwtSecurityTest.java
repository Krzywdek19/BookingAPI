package pl.exceptionhandled.bookingapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtSecurityTest {
    private static final String SECRET =
            "c2VjdXJlLXRlc3Qta2V5LWZvci1ib29raW5nLWFwaS0zMg==";

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void shouldAuthenticateUserRoleFromJwt() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", MacAlgorithm.HS256.getName())
                .subject("user@example.com")
                .claim("roles", List.of("ROLE_USER"))
                .build();

        Authentication authentication = converter.convert(jwt);

        assertEquals("user@example.com", authentication.getName());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {
        var decoder = decoder(SECRET);
        var otherEncoder = encoder(
                "YW5vdGhlci1zZWN1cmUta2V5LWZvci1ib29raW5nLWFwaQ=="
        );
        String token = otherEncoder.encode(JwtEncoderParameters.from(validClaims())).getTokenValue();

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        var decoder = decoder(SECRET);
        var encoder = encoder(SECRET);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("booking-api")
                .subject("user@example.com")
                .issuedAt(Instant.now().minusSeconds(120))
                .expiresAt(Instant.now().minusSeconds(60))
                .claim("roles", List.of("ROLE_USER"))
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        assertThrows(JwtValidationException.class, () -> decoder.decode(token));
    }

    private JwtDecoder decoder(String secret) {
        return securityConfig.jwtDecoder(securityConfig.jwtSecretKey(secret));
    }

    private JwtEncoder encoder(String secret) {
        return securityConfig.jwtEncoder(securityConfig.jwtSecretKey(secret));
    }

    private JwtClaimsSet validClaims() {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer("booking-api")
                .subject("user@example.com")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("roles", List.of("ROLE_USER"))
                .build();
    }
}
