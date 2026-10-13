CREATE TABLE cartao (
    id             BIGSERIAL PRIMARY KEY,
    coluna_id      BIGINT         NOT NULL REFERENCES coluna (id) ON DELETE CASCADE,
    titulo         VARCHAR(200)   NOT NULL,
    descricao      TEXT,
    responsavel_id BIGINT         REFERENCES usuario (id),
    prazo          TIMESTAMPTZ,
    posicao        NUMERIC(12, 2) NOT NULL,
    criado_em      TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_cartao_coluna_id ON cartao (coluna_id);
