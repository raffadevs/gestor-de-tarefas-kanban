package dev.raffadevs.kanban.coluna;

import dev.raffadevs.kanban.coluna.dto.AtualizarColunaRequest;
import dev.raffadevs.kanban.coluna.dto.ColunaResponse;
import dev.raffadevs.kanban.coluna.dto.CriarColunaRequest;
import dev.raffadevs.kanban.common.RecursoNaoEncontradoException;
import dev.raffadevs.kanban.quadro.Papel;
import dev.raffadevs.kanban.quadro.QuadroAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ColunaService {

    private static final BigDecimal INCREMENTO_POSICAO = BigDecimal.valueOf(1000);

    private final ColunaRepository colunaRepository;
    private final QuadroAuthorizationService authorizationService;

    public ColunaService(ColunaRepository colunaRepository, QuadroAuthorizationService authorizationService) {
        this.colunaRepository = colunaRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ColunaResponse criar(Long quadroId, CriarColunaRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);

        BigDecimal proximaPosicao = colunaRepository.buscarMaiorPosicao(quadroId)
                .map(maior -> maior.add(INCREMENTO_POSICAO))
                .orElse(INCREMENTO_POSICAO);

        Coluna coluna = colunaRepository.save(new Coluna(quadroId, request.nome(), proximaPosicao));
        return ColunaResponse.from(coluna);
    }

    @Transactional(readOnly = true)
    public List<ColunaResponse> listar(Long quadroId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.LEITOR);
        return colunaRepository.findByQuadroIdOrderByPosicaoAsc(quadroId).stream()
                .map(ColunaResponse::from)
                .toList();
    }

    @Transactional
    public ColunaResponse renomear(Long quadroId, Long colunaId, AtualizarColunaRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Coluna coluna = buscarDoQuadroOuFalhar(quadroId, colunaId);
        coluna.setNome(request.nome());
        return ColunaResponse.from(coluna);
    }

    @Transactional
    public void excluir(Long quadroId, Long colunaId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Coluna coluna = buscarDoQuadroOuFalhar(quadroId, colunaId);
        colunaRepository.delete(coluna);
    }

    public Coluna buscarDoQuadroOuFalhar(Long quadroId, Long colunaId) {
        Coluna coluna = colunaRepository.findById(colunaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Coluna não encontrada"));
        if (!coluna.getQuadroId().equals(quadroId)) {
            throw new RecursoNaoEncontradoException("Coluna não encontrada neste quadro");
        }
        return coluna;
    }
}
