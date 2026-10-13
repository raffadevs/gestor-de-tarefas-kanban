CREATE TABLE coluna (
    id        BIGSERIAL PRIMARY KEY,
    quadro_id BIGINT         NOT NULL REFERENCES quadro (id) ON DELETE CASCADE,
    nome      VARCHAR(80)    NOT NULL,
    posicao   NUMERIC(12, 2) NOT NULL
);

CREATE INDEX idx_coluna_quadro_id ON coluna (quadro_id);
