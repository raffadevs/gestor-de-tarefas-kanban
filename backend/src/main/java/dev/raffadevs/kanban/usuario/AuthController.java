package dev.raffadevs.kanban.usuario;

import dev.raffadevs.kanban.usuario.dto.LoginRequest;
import dev.raffadevs.kanban.usuario.dto.RefreshRequest;
import dev.raffadevs.kanban.usuario.dto.RegistrarRequest;
import dev.raffadevs.kanban.usuario.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro, login e renovação de tokens")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    @Operation(summary = "Cria uma conta e retorna os tokens de acesso")
    public ResponseEntity<TokenResponse> registrar(@Valid @RequestBody RegistrarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica com e-mail e senha")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Troca um refresh token válido por um novo par de tokens")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.renovar(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoga o refresh token informado")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
