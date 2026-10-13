package dev.raffadevs.kanban.cartao;

import dev.raffadevs.kanban.cartao.dto.AtualizarCartaoRequest;
import dev.raffadevs.kanban.cartao.dto.CartaoResponse;
import dev.raffadevs.kanban.cartao.dto.CriarCartaoRequest;
import dev.raffadevs.kanban.cartao.dto.MoverCartaoRequest;
import dev.raffadevs.kanban.coluna.Coluna;
import dev.raffadevs.kanban.coluna.ColunaRepository;
import dev.raffadevs.kanban.coluna.ColunaService;
import dev.raffadevs.kanban.common.RecursoNaoEncontradoException;
import dev.raffadevs.kanban.etiqueta.Etiqueta;
import dev.raffadevs.kanban.etiqueta.EtiquetaRepository;
import dev.raffadevs.kanban.quadro.Papel;
import dev.raffadevs.kanban.quadro.QuadroAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class CartaoService {

    private static final BigDecimal INCREMENTO_POSICAO = BigDecimal.valueOf(1000);

    private final CartaoRepository cartaoRepository;
    private final ColunaRepository colunaRepository;
    private final ColunaService colunaService;
    private final EtiquetaRepository etiquetaRepository;
    private final QuadroAuthorizationService authorizationService;

    public CartaoService(CartaoRepository cartaoRepository, ColunaRepository colunaRepository,
                          ColunaService colunaService, EtiquetaRepository etiquetaRepository,
                          QuadroAuthorizationService authorizationService) {
        this.cartaoRepository = cartaoRepository;
        this.colunaRepository = colunaRepository;
        this.colunaService = colunaService;
        this.etiquetaRepository = etiquetaRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public CartaoResponse criar(Long quadroId, CriarCartaoRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        colunaService.buscarDoQuadroOuFalhar(quadroId, request.colunaId());

        BigDecimal posicao = cartaoRepository.buscarMaiorPosicao(request.colunaId())
                .map(maior -> maior.add(INCREMENTO_POSICAO))
                .orElse(INCREMENTO_POSICAO);

        Cartao cartao = new Cartao(request.colunaId(), request.titulo(), request.descricao(),
                request.responsavelId(), request.prazo(), posicao);
        cartaoRepository.save(cartao);
        return CartaoResponse.from(cartao);
    }

    @Transactional(readOnly = true)
    public List<CartaoResponse> listarPorQuadro(Long quadroId, Long usuarioId, Long etiquetaId,
                                                 Long responsavelId, boolean apenasPrazoVencido) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.LEITOR);

        List<Long> colunaIds = colunaRepository.findByQuadroIdOrderByPosicaoAsc(quadroId).stream()
                .map(Coluna::getId)
                .toList();

        Instant agora = Instant.now();
        return cartaoRepository.buscarPorColunas(colunaIds).stream()
                .filter(c -> etiquetaId == null || c.getEtiquetas().stream().anyMatch(e -> e.getId().equals(etiquetaId)))
                .filter(c -> responsavelId == null || responsavelId.equals(c.getResponsavelId()))
                .filter(c -> !apenasPrazoVencido || (c.getPrazo() != null && c.getPrazo().isBefore(agora)))
                .map(CartaoResponse::from)
                .toList();
    }

    @Transactional
    public CartaoResponse atualizar(Long quadroId, Long cartaoId, AtualizarCartaoRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Cartao cartao = buscarDoQuadroOuFalhar(quadroId, cartaoId);
        cartao.atualizar(request.titulo(), request.descricao(), request.responsavelId(), request.prazo());
        return CartaoResponse.from(cartao);
    }

    /**
     * Move o cartão para uma posição específica (índice 0-based) dentro da
     * coluna de destino. A nova posição numérica é o ponto médio entre os
     * vizinhos naquele índice, então apenas este cartão é gravado — a coluna
     * inteira nunca é reordenada.
     */
    @Transactional
    public CartaoResponse mover(Long quadroId, Long cartaoId, MoverCartaoRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Cartao cartao = buscarDoQuadroOuFalhar(quadroId, cartaoId);
        colunaService.buscarDoQuadroOuFalhar(quadroId, request.colunaDestinoId());

        List<Cartao> cartoesDestino = cartaoRepository.findByColunaIdOrderByPosicaoAsc(request.colunaDestinoId())
                .stream()
                .filter(c -> !c.getId().equals(cartaoId))
                .toList();

        BigDecimal novaPosicao = calcularPosicao(cartoesDestino, request.indiceDestino());
        cartao.mover(request.colunaDestinoId(), novaPosicao);
        return CartaoResponse.from(cartao);
    }

    @Transactional
    public CartaoResponse adicionarEtiqueta(Long quadroId, Long cartaoId, Long etiquetaId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Cartao cartao = buscarDoQuadroOuFalhar(quadroId, cartaoId);
        Etiqueta etiqueta = buscarEtiquetaDoQuadroOuFalhar(quadroId, etiquetaId);
        cartao.getEtiquetas().add(etiqueta);
        return CartaoResponse.from(cartao);
    }

    @Transactional
    public CartaoResponse removerEtiqueta(Long quadroId, Long cartaoId, Long etiquetaId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Cartao cartao = buscarDoQuadroOuFalhar(quadroId, cartaoId);
        cartao.getEtiquetas().removeIf(e -> e.getId().equals(etiquetaId));
        return CartaoResponse.from(cartao);
    }

    @Transactional
    public void excluir(Long quadroId, Long cartaoId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Cartao cartao = buscarDoQuadroOuFalhar(quadroId, cartaoId);
        cartaoRepository.delete(cartao);
    }

    private BigDecimal calcularPosicao(List<Cartao> cartoesOrdenados, int indiceDestino) {
        int indice = Math.min(indiceDestino, cartoesOrdenados.size());

        BigDecimal anterior = indice > 0 ? cartoesOrdenados.get(indice - 1).getPosicao() : null;
        BigDecimal seguinte = indice < cartoesOrdenados.size() ? cartoesOrdenados.get(indice).getPosicao() : null;

        if (anterior == null && seguinte == null) {
            return INCREMENTO_POSICAO;
        }
        if (anterior == null) {
            return seguinte.subtract(INCREMENTO_POSICAO);
        }
        if (seguinte == null) {
            return anterior.add(INCREMENTO_POSICAO);
        }
        return anterior.add(seguinte).divide(BigDecimal.valueOf(2));
    }

    private Cartao buscarDoQuadroOuFalhar(Long quadroId, Long cartaoId) {
        Cartao cartao = cartaoRepository.findById(cartaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cartão não encontrado"));
        colunaService.buscarDoQuadroOuFalhar(quadroId, cartao.getColunaId());
        return cartao;
    }

    private Etiqueta buscarEtiquetaDoQuadroOuFalhar(Long quadroId, Long etiquetaId) {
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etiqueta não encontrada"));
        if (!etiqueta.getQuadroId().equals(quadroId)) {
            throw new RecursoNaoEncontradoException("Etiqueta não encontrada neste quadro");
        }
        return etiqueta;
    }
}
