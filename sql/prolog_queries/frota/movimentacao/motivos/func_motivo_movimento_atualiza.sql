CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_ATUALIZA(F_COD_MOTIVO BIGINT,
                                                          F_DESCRICAO_MOTIVO CITEXT,
                                                          F_ATIVO_MOTIVO BOOLEAN,
                                                          F_COD_AUXILIAR_MOTIVO TEXT,
                                                          F_DATA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE,
                                                          F_COD_COLABORADOR_ALTERACAO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_DESCRICAO_ANTERIOR                          CITEXT;
    V_ATIVO_ANTERIOR                              BOOLEAN;
    V_COD_AUXILIAR_ANTERIOR                       TEXT;
    V_ULTIMA_ATUALIZACAO_ANTERIOR                 TIMESTAMP WITH TIME ZONE;
    V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR BIGINT;
BEGIN

    SELECT MOTIVO,
           ATIVO,
           COD_AUXILIAR,
           DATA_HORA_ULTIMA_ALTERACAO,
           COD_COLABORADOR_ULTIMA_ALTERACAO
    INTO V_DESCRICAO_ANTERIOR,
        V_ATIVO_ANTERIOR,
        V_COD_AUXILIAR_ANTERIOR,
        V_ULTIMA_ATUALIZACAO_ANTERIOR,
        V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR
    FROM MOVIMENTACAO_MOTIVO_MOVIMENTO
    WHERE CODIGO = F_COD_MOTIVO;

    IF TRIM(V_DESCRICAO_ANTERIOR) != (F_DESCRICAO_MOTIVO)
        OR V_ATIVO_ANTERIOR != F_ATIVO_MOTIVO
        OR V_COD_AUXILIAR_ANTERIOR IS NULL AND F_COD_AUXILIAR_MOTIVO IS NOT NULL
        OR V_COD_AUXILIAR_ANTERIOR IS NOT NULL AND F_COD_AUXILIAR_MOTIVO IS NULL
        OR TRIM(V_COD_AUXILIAR_ANTERIOR) != TRIM(F_COD_AUXILIAR_MOTIVO) THEN

        UPDATE MOVIMENTACAO_MOTIVO_MOVIMENTO
        SET MOTIVO                           = F_DESCRICAO_MOTIVO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_ULTIMA_ALTERACAO,
            COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ALTERACAO,
            ATIVO                            = F_ATIVO_MOTIVO,
            COD_AUXILIAR                     = F_COD_AUXILIAR_MOTIVO
        WHERE CODIGO = F_COD_MOTIVO;

        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados do motivo, tente novamente.');
        END IF;

        INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_HISTORICO(COD_MOTIVO,
                                                            DESCRICAO_MOTIVO,
                                                            ATIVO_MOTIVO,
                                                            COD_AUXILIAR,
                                                            DATA_HORA_ALTERACAO_ANTERIOR,
                                                            COD_COLABORADOR_ALTERACAO_ANTERIOR)
        VALUES (F_COD_MOTIVO,
                V_DESCRICAO_ANTERIOR,
                V_ATIVO_ANTERIOR,
                V_COD_AUXILIAR_ANTERIOR,
                V_ULTIMA_ATUALIZACAO_ANTERIOR,
                V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR);
    END IF;
END;
$$;