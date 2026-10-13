package dev.raffadevs.kanban.usuario;

import dev.raffadevs.kanban.security.JwtService;
import dev.raffadevs.kanban.security.RefreshTokenService;
import dev.raffadevs.kanban.usuario.dto.LoginRequest;
import dev.raffadevs.kanban.usuario.dto.RegistrarRequest;
import dev.raffadevs.kanban.usuario.dto.TokenResponse;
import dev.raffadevs.kanban.usuario.dto.UsuarioResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtService jwtService,
                        RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public TokenResponse registrar(RegistrarRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaExisteException(request.email());
        }

        Usuario usuario = new Usuario(request.nome(), request.email(), passwordEncoder.encode(request.senha()));
        usuarioRepository.save(usuario);

        return emitirTokens(usuario);
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));

        return emitirTokens(usuario);
    }

    @Transactional
    public TokenResponse renovar(String refreshTokenBruto) {
        RefreshTokenService.RotacaoResultado rotacao = refreshTokenService.validarERotacionar(refreshTokenBruto);

        Usuario usuario = usuarioRepository.findById(rotacao.usuarioId())
                .orElseThrow(() -> new IllegalStateException("Usuário do refresh token não encontrado"));

        String accessToken = jwtService.gerarAccessToken(new UsuarioDetails(usuario));
        return new TokenResponse(accessToken, rotacao.novoRefreshToken(), UsuarioResponse.from(usuario));
    }

    public void logout(String refreshTokenBruto) {
        refreshTokenService.revogar(refreshTokenBruto);
    }

    private TokenResponse emitirTokens(Usuario usuario) {
        UsuarioDetails details = new UsuarioDetails(usuario);
        String accessToken = jwtService.gerarAccessToken(details);
        String refreshToken = refreshTokenService.gerarPara(usuario.getId());
        return new TokenResponse(accessToken, refreshToken, UsuarioResponse.from(usuario));
    }
}
