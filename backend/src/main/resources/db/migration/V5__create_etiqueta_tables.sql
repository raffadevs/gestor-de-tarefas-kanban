CREATE TABLE etiqueta (
    id        BIGSERIAL PRIMARY KEY,
    quadro_id BIGINT      NOT NULL REFERENCES quadro (id) ON DELETE CASCADE,
    nome      VARCHAR(40) NOT NULL,
    cor       VARCHAR(7)  NOT NULL
);

CREATE TABLE cartao_etiqueta (
    cartao_id   BIGINT NOT NULL REFERENCES cartao (id) ON DELETE CASCADE,
    etiqueta_id BIGINT NOT NULL REFERENCES etiqueta (id) ON DELETE CASCADE,
    PRIMARY KEY (cartao_id, etiqueta_id)
);

CREATE INDEX idx_etiqueta_quadro_id ON etiqueta (quadro_id);
