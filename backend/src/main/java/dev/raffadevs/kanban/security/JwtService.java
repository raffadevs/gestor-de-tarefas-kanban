package dev.raffadevs.kanban.security;

import dev.raffadevs.kanban.usuario.UsuarioDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Emite e valida apenas o access token (JWT, stateless, curta duração).
 * O refresh token é opaco e gerenciado por {@link dev.raffadevs.kanban.security.RefreshTokenService}.
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {

    private final SecretKey chave;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.chave = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String gerarAccessToken(UsuarioDetails usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("uid", usuario.getId())
                .claim("nome", usuario.getNome())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }

    public Optional<Claims> validarEExtrairClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
