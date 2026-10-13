package dev.raffadevs.kanban.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Refresh tokens são opacos (não JWT): um valor aleatório de alta entropia é
 * entregue ao cliente, e apenas o hash SHA-256 dele fica no banco — assim, um
 * vazamento do banco não expõe tokens utilizáveis. Cada uso rotaciona o
 * token (o antigo é revogado e um novo é emitido), limitando o estrago de um
 * token roubado e reutilizado.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository, JwtProperties jwtProperties) {
        this.repository = repository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String gerarPara(Long usuarioId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String tokenBruto = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Instant expiraEm = Instant.now().plus(jwtProperties.getRefreshTokenDays(), ChronoUnit.DAYS);
        repository.save(new RefreshToken(usuarioId, hash(tokenBruto), expiraEm));
        return tokenBruto;
    }

    @Transactional
    public RotacaoResultado validarERotacionar(String tokenBruto) {
        RefreshToken token = repository.findByTokenHash(hash(tokenBruto))
                .filter(RefreshToken::estaValido)
                .orElseThrow(TokenInvalidoException::new);

        token.revogar();
        repository.save(token);

        String novoToken = gerarPara(token.getUsuarioId());
        return new RotacaoResultado(token.getUsuarioId(), novoToken);
    }

    @Transactional
    public void revogar(String tokenBruto) {
        repository.findByTokenHash(hash(tokenBruto)).ifPresent(token -> {
            token.revogar();
            repository.save(token);
        });
    }

    private String hash(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] resultado = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resultado);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public record RotacaoResultado(Long usuarioId, String novoRefreshToken) {
    }
}
