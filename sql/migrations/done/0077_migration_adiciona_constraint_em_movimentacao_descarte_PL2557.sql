-- PL-2557
ALTER TABLE MOVIMENTACAO_DESTINO
    ADD CONSTRAINT CHECK_MOVIMENTACAO_DESTINO_COD_MOTIVO_DESCARTE_NOT_NULL
        CHECK (((TIPO_DESTINO)::TEXT = 'DESCARTE'::TEXT AND COD_MOTIVO_DESCARTE IS NOT NULL)
            OR (TIPO_DESTINO:: TEXT = 'ANALISE':: TEXT AND COD_MOTIVO_DESCARTE IS NULL)
            OR (TIPO_DESTINO:: TEXT = 'ESTOQUE':: TEXT AND COD_MOTIVO_DESCARTE IS NULL)
            OR (TIPO_DESTINO:: TEXT = 'EM_USO':: TEXT AND COD_MOTIVO_DESCARTE IS NULL))
        NOT VALID;