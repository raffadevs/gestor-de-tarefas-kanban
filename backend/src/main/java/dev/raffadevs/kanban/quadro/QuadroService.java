package dev.raffadevs.kanban.quadro;

import dev.raffadevs.kanban.common.RecursoNaoEncontradoException;
import dev.raffadevs.kanban.quadro.dto.ConvidarMembroRequest;
import dev.raffadevs.kanban.quadro.dto.CriarQuadroRequest;
import dev.raffadevs.kanban.quadro.dto.MembroResponse;
import dev.raffadevs.kanban.quadro.dto.QuadroResponse;
import dev.raffadevs.kanban.usuario.Usuario;
import dev.raffadevs.kanban.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuadroService {

    private final QuadroRepository quadroRepository;
    private final MembroQuadroRepository membroQuadroRepository;
    private final UsuarioRepository usuarioRepository;
    private final QuadroAuthorizationService authorizationService;

    public QuadroService(QuadroRepository quadroRepository, MembroQuadroRepository membroQuadroRepository,
                          UsuarioRepository usuarioRepository, QuadroAuthorizationService authorizationService) {
        this.quadroRepository = quadroRepository;
        this.membroQuadroRepository = membroQuadroRepository;
        this.usuarioRepository = usuarioRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public QuadroResponse criar(CriarQuadroRequest request, Long donoId) {
        Quadro quadro = quadroRepository.save(new Quadro(request.nome(), donoId));
        membroQuadroRepository.save(new MembroQuadro(quadro.getId(), donoId, Papel.DONO));
        return QuadroResponse.from(quadro, Papel.DONO);
    }

    @Transactional(readOnly = true)
    public List<QuadroResponse> listarMeusQuadros(Long usuarioId) {
        List<Quadro> quadros = quadroRepository.buscarQuadrosDoUsuario(usuarioId);
        return quadros.stream()
                .map(quadro -> QuadroResponse.from(quadro, papelDoUsuario(quadro.getId(), usuarioId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public QuadroResponse buscarQuadro(Long quadroId, Long usuarioId) {
        MembroQuadro membro = authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.LEITOR);
        Quadro quadro = buscarQuadroOuFalhar(quadroId);
        return QuadroResponse.from(quadro, membro.getPapel());
    }

    @Transactional
    public MembroResponse convidarMembro(Long quadroId, Long atorId, ConvidarMembroRequest request) {
        authorizationService.exigirPapelMinimo(quadroId, atorId, Papel.DONO);
        buscarQuadroOuFalhar(quadroId);

        Usuario convidado = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhum usuário encontrado com o e-mail '" + request.email() + "'"));

        MembroQuadro membro = membroQuadroRepository.findById_QuadroIdAndId_UsuarioId(quadroId, convidado.getId())
                .map(existente -> {
                    existente.setPapel(request.papel());
                    return existente;
                })
                .orElseGet(() -> new MembroQuadro(quadroId, convidado.getId(), request.papel()));

        membroQuadroRepository.save(membro);
        return new MembroResponse(convidado.getId(), convidado.getNome(), convidado.getEmail(), membro.getPapel());
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> listarMembros(Long quadroId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.LEITOR);

        List<MembroQuadro> membros = membroQuadroRepository.findById_QuadroId(quadroId);
        Map<Long, Usuario> usuariosPorId = usuarioRepository
                .findAllById(membros.stream().map(m -> m.getId().getUsuarioId()).toList())
                .stream()
                .collect(Collectors.toMap(Usuario::getId, u -> u));

        return membros.stream()
                .map(m -> {
                    Usuario usuario = usuariosPorId.get(m.getId().getUsuarioId());
                    return new MembroResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), m.getPapel());
                })
                .toList();
    }

    @Transactional
    public void excluir(Long quadroId, Long usuarioId) {
        authorizationService.exigirPapelMinimo(quadroId, usuarioId, Papel.DONO);
        quadroRepository.deleteById(quadroId);
    }

    Quadro buscarQuadroOuFalhar(Long quadroId) {
        return quadroRepository.findById(quadroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quadro não encontrado"));
    }

    private Papel papelDoUsuario(Long quadroId, Long usuarioId) {
        return membroQuadroRepository.findById_QuadroIdAndId_UsuarioId(quadroId, usuarioId)
                .map(MembroQuadro::getPapel)
                .orElseThrow(() -> new IllegalStateException("Inconsistência: quadro sem o membro esperado"));
    }
}
