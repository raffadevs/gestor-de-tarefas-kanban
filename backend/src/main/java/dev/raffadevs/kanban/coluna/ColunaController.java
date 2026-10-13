package dev.raffadevs.kanban.coluna;

import dev.raffadevs.kanban.coluna.dto.AtualizarColunaRequest;
import dev.raffadevs.kanban.coluna.dto.ColunaResponse;
import dev.raffadevs.kanban.coluna.dto.CriarColunaRequest;
import dev.raffadevs.kanban.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quadros/{quadroId}/colunas")
@Tag(name = "Colunas", description = "Colunas de um quadro (ex.: A fazer, Fazendo, Feito)")
public class ColunaController {

    private final ColunaService colunaService;

    public ColunaController(ColunaService colunaService) {
        this.colunaService = colunaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma coluna no final do quadro (requer papel EDITOR+)")
    public ResponseEntity<ColunaResponse> criar(@PathVariable Long quadroId,
                                                 @Valid @RequestBody CriarColunaRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colunaService.criar(quadroId, request, usuario.id()));
    }

    @GetMapping
    @Operation(summary = "Lista as colunas de um quadro, em ordem")
    public ResponseEntity<List<ColunaResponse>> listar(@PathVariable Long quadroId,
                                                         @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(colunaService.listar(quadroId, usuario.id()));
    }

    @PatchMapping("/{colunaId}")
    @Operation(summary = "Renomeia uma coluna (requer papel EDITOR+)")
    public ResponseEntity<ColunaResponse> renomear(@PathVariable Long quadroId, @PathVariable Long colunaId,
                                                    @Valid @RequestBody AtualizarColunaRequest request,
                                                    @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(colunaService.renomear(quadroId, colunaId, request, usuario.id()));
    }

    @DeleteMapping("/{colunaId}")
    @Operation(summary = "Exclui uma coluna e seus cartões (requer papel EDITOR+)")
    public ResponseEntity<Void> excluir(@PathVariable Long quadroId, @PathVariable Long colunaId,
                                         @AuthenticationPrincipal AuthenticatedUser usuario) {
        colunaService.excluir(quadroId, colunaId, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
