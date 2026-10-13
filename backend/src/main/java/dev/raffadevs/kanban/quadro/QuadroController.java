package dev.raffadevs.kanban.quadro;

import dev.raffadevs.kanban.quadro.dto.ConvidarMembroRequest;
import dev.raffadevs.kanban.quadro.dto.CriarQuadroRequest;
import dev.raffadevs.kanban.quadro.dto.MembroResponse;
import dev.raffadevs.kanban.quadro.dto.QuadroResponse;
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
@RequestMapping("/api/quadros")
@Tag(name = "Quadros", description = "Quadros Kanban e seus membros")
public class QuadroController {

    private final QuadroService quadroService;

    public QuadroController(QuadroService quadroService) {
        this.quadroService = quadroService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo quadro (o criador vira DONO)")
    public ResponseEntity<QuadroResponse> criar(@Valid @RequestBody CriarQuadroRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quadroService.criar(request, usuario.id()));
    }

    @GetMapping
    @Operation(summary = "Lista os quadros dos quais o usuário autenticado é membro")
    public ResponseEntity<List<QuadroResponse>> listarMeusQuadros(@AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(quadroService.listarMeusQuadros(usuario.id()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes de um quadro")
    public ResponseEntity<QuadroResponse> buscar(@PathVariable Long id,
                                                  @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(quadroService.buscarQuadro(id, usuario.id()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um quadro (requer papel DONO)")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser usuario) {
        quadroService.excluir(id, usuario.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membros")
    @Operation(summary = "Convida (ou atualiza o papel de) um membro pelo e-mail (requer papel DONO)")
    public ResponseEntity<MembroResponse> convidarMembro(@PathVariable Long id,
                                                          @Valid @RequestBody ConvidarMembroRequest request,
                                                          @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quadroService.convidarMembro(id, usuario.id(), request));
    }

    @GetMapping("/{id}/membros")
    @Operation(summary = "Lista os membros de um quadro")
    public ResponseEntity<List<MembroResponse>> listarMembros(@PathVariable Long id,
                                                                @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(quadroService.listarMembros(id, usuario.id()));
    }
}
