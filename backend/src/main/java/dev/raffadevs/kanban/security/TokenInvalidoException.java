package dev.raffadevs.kanban.security;

public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException() {
        super("Refresh token inválido, expirado ou revogado");
    }
}
