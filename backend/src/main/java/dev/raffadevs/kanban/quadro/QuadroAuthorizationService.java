package dev.raffadevs.kanban.quadro;

import dev.raffadevs.kanban.common.AcessoNegadoException;
import org.springframework.stereotype.Service;

@Service
public class QuadroAuthorizationService {

    private final MembroQuadroRepository membroQuadroRepository;

    public QuadroAuthorizationService(MembroQuadroRepository membroQuadroRepository) {
        this.membroQuadroRepository = membroQuadroRepository;
    }

    /**
     * Garante que o usuário é membro do quadro com papel igual ou acima de
     * {@code minimo}, lançando {@link AcessoNegadoException} (403) caso
     * contrário — seja por não ser membro, seja por ter papel insuficiente.
     */
    public MembroQuadro exigirPapelMinimo(Long quadroId, Long usuarioId, Papel minimo) {
        MembroQuadro membro = membroQuadroRepository.findById_QuadroIdAndId_UsuarioId(quadroId, usuarioId)
                .orElseThrow(() -> new AcessoNegadoException("Você não é membro deste quadro"));

        if (!membro.getPapel().atendeNoMinimo(minimo)) {
            throw new AcessoNegadoException("Papel insuficiente para esta ação");
        }
        return membro;
    }
}
