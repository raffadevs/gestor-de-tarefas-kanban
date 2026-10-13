package dev.raffadevs.kanban.usuario.dto;

import dev.raffadevs.kanban.usuario.Usuario;

public record UsuarioResponse(Long id, String nome, String email) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
