CREATE TABLE quadro (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(120) NOT NULL,
    dono_id   BIGINT       NOT NULL REFERENCES usuario (id),
    criado_em TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE membro_quadro (
    quadro_id  BIGINT      NOT NULL REFERENCES quadro (id) ON DELETE CASCADE,
    usuario_id BIGINT      NOT NULL REFERENCES usuario (id) ON DELETE CASCADE,
    papel      VARCHAR(10) NOT NULL CHECK (papel IN ('DONO', 'EDITOR', 'LEITOR')),
    entrou_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (quadro_id, usuario_id)
);
