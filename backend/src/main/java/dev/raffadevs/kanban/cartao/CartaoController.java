package dev.raffadevs.kanban.cartao;

import dev.raffadevs.kanban.cartao.dto.AtualizarCartaoRequest;
import dev.raffadevs.kanban.cartao.dto.CartaoResponse;
import dev.raffadevs.kanban.cartao.dto.CriarCartaoRequest;
import dev.raffadevs.kanban.cartao.dto.MoverCartaoRequest;
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
@RequestMapping("/api/quadros/{quadroId}/cartoes")
@Tag(name = "Cartões", description = "Cartões de tarefa, com etiquetas, responsável e prazo")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping
    @Operation(summary = "Cria um cartão no final de uma coluna (requer papel EDITOR+)")
    public ResponseEntity<CartaoResponse> criar(@PathVariable Long quadroId,
                                                 @Valid @RequestBody CriarCartaoRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartaoService.criar(quadroId, request, usuario.id()));
    }

    @GetMapping
    @Operation(summary = "Lista os cartões do quadro, com filtros opcionais")
    public ResponseEntity<List<CartaoResponse>> listar(
            @PathVariable Long quadroId,
            @RequestParam(required = false) Long etiquetaId,
            @RequestParam(required = false) Long responsavelId,
            @RequestParam(name = "prazoVencido", defaultValue = "false") boolean prazoVencido,
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(
                cartaoService.listarPorQuadro(quadroId, usuario.id(), etiquetaId, responsavelId, prazoVencido));
    }

    @PatchMapping("/{cartaoId}")
    @Operation(summary = "Atualiza título, descrição, responsável e prazo (requer papel EDITOR+)")
    public ResponseEntity<CartaoResponse> atualizar(@PathVariable Long quadroId, @PathVariable Long cartaoId,
                                                      @Valid @RequestBody AtualizarCartaoRequest request,
                                                      @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(cartaoService.atualizar(quadroId, cartaoId, request, usuario.id()));
    }

    @PatchMapping("/{cartaoId}/mover")
    @Operation(summary = "Move o cartão para outra coluna e/ou posição (requer papel EDITOR+)")
    public ResponseEntity<CartaoResponse> mover(@PathVariable Long quadroId, @PathVariable Long cartaoId,
                                                 @Valid @RequestBody MoverCartaoRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(cartaoService.mover(quadroId, cartaoId, request, usuario.id()));
    }

    @PutMapping("/{cartaoId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Adiciona uma etiqueta ao cartão (requer papel EDITOR+)")
    public ResponseEntity<CartaoResponse> adicionarEtiqueta(@PathVariable Long quadroId, @PathVariable Long cartaoId,
                                                              @PathVariable Long etiquetaId,
                                                              @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(cartaoService.adicionarEtiqueta(quadroId, cartaoId, etiquetaId, usuario.id()));
    }

    @DeleteMapping("/{cartaoId}/etiquetas/{etiquetaId}")
    @Operation(summary = "Remove uma etiqueta do cartão (requer papel EDITOR+)")
    public ResponseEntity<CartaoResponse> removerEtiqueta(@PathVariable Long quadroId, @PathVariable Long cartaoId,
                                                            @PathVariable Long etiquetaId,
                                                            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(cartaoService.removerEtiqueta(quadroId, cartaoId, etiquetaId, usuario.id()));
    }

    @DeleteMapping("/{cartaoId}")
    @Operation(summary = "Exclui um cartão (requer papel EDITOR+)")
    public ResponseEntity<Void> excluir(@PathVariable Long quadroId, @PathVariable Long cartaoId,
                                         @AuthenticationPrincipal AuthenticatedUser usuario) {
        cartaoService.excluir(quadroId, cartaoId, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
