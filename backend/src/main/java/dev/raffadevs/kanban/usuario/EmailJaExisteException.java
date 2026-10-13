package dev.raffadevs.kanban.usuario;

public class EmailJaExisteException extends RuntimeException {

    public EmailJaExisteException(String email) {
        super("Já existe uma conta com o e-mail '" + email + "'");
    }
}
