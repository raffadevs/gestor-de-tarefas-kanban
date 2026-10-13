package dev.raffadevs.kanban.etiqueta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarEtiquetaRequest(
        @NotBlank(message = "O nome da etiqueta é obrigatório")
        @Size(max = 40)
        String nome,

        @NotBlank(message = "A cor é obrigatória")
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "A cor deve estar no formato hexadecimal, ex.: #5b4ff2")
        String cor
) {
}
