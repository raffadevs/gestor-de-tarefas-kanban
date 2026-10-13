package dev.raffadevs.kanban.etiqueta;

import dev.raffadevs.kanban.etiqueta.dto.CriarEtiquetaRequest;
import dev.raffadevs.kanban.etiqueta.dto.EtiquetaResponse;
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
@RequestMapping("/api/quadros/{quadroId}/etiquetas")
@Tag(name = "Etiquetas", description = "Etiquetas coloridas por quadro, aplicáveis a cartões")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    public EtiquetaController(EtiquetaService etiquetaService) {
        this.etiquetaService = etiquetaService;
    }

    @PostMapping
    @Operation(summary = "Cria uma etiqueta no quadro (requer papel EDITOR+)")
    public ResponseEntity<EtiquetaResponse> criar(@PathVariable Long quadroId,
                                                   @Valid @RequestBody CriarEtiquetaRequest request,
                                                   @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etiquetaService.criar(quadroId, request, usuario.id()));
    }

    @GetMapping
    @Operation(summary = "Lista as etiquetas de um quadro")
    public ResponseEntity<List<EtiquetaResponse>> listar(@PathVariable Long quadroId,
                                                           @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(etiquetaService.listar(quadroId, usuario.id()));
    }

    @DeleteMapping("/{etiquetaId}")
    @Operation(summary = "Exclui uma etiqueta (requer papel EDITOR+)")
    public ResponseEntity<Void> excluir(@PathVariable Long quadroId, @PathVariable Long etiquetaId,
                                         @AuthenticationPrincipal AuthenticatedUser usuario) {
        etiquetaService.excluir(quadroId, etiquetaId, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
