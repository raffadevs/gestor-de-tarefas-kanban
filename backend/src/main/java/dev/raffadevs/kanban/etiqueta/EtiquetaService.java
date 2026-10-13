package dev.raffadevs.kanban.etiqueta;

import dev.raffadevs.kanban.common.RecursoNaoEncontradoException;
import dev.raffadevs.kanban.etiqueta.dto.CriarEtiquetaRequest;
import dev.raffadevs.kanban.etiqueta.dto.EtiquetaResponse;
import dev.raffadevs.kanban.quadro.Papel;
import dev.raffadevs.kanban.quadro.QuadroAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final QuadroAuthorizationService authorizationService;

    public EtiquetaService(EtiquetaRepository etiquetaRepository, QuadroAuthorizationService authorizationService) {
        this.etiquetaRepository = etiquetaRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public EtiquetaResponse criar(Long quadroId, CriarEtiquetaRequest request, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Etiqueta etiqueta = etiquetaRepository.save(new Etiqueta(quadroId, request.nome(), request.cor()));
        return EtiquetaResponse.from(etiqueta);
    }

    @Transactional(readOnly = true)
    public List<EtiquetaResponse> listar(Long quadroId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.LEITOR);
        return etiquetaRepository.findByQuadroId(quadroId).stream().map(EtiquetaResponse::from).toList();
    }

    @Transactional
    public void excluir(Long quadroId, Long etiquetaId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.EDITOR);
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Etiqueta não encontrada"));
        if (!etiqueta.getQuadroId().equals(quadroId)) {
            throw new RecursoNaoEncontradoException("Etiqueta não encontrada neste quadro");
        }
        etiquetaRepository.delete(etiqueta);
    }
}
