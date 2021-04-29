CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_INSERE(F_COD_EMPRESA_MOTIVO BIGINT,
                                                        F_DESCRICAO_MOTIVO TEXT,
                                                        F_ATIVO_MOTIVO BOOLEAN,
                                                        F_COD_AUXILIAR_MOTIVO TEXT,
                                                        F_DATA_HORA_INSERCAO_MOTIVO TIMESTAMP WITH TIME ZONE,
                                                        F_COD_COLABORADOR_AUTENTICADO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MOTIVO BIGINT;
BEGIN
    INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO (COD_EMPRESA,
                                               MOTIVO,
                                               ATIVO,
                                               COD_AUXILIAR,
                                               DATA_HORA_ULTIMA_ALTERACAO,
                                               COD_COLABORADOR_ULTIMA_ALTERACAO)
    VALUES (F_COD_EMPRESA_MOTIVO,
            F_DESCRICAO_MOTIVO,
            F_ATIVO_MOTIVO,
            F_COD_AUXILIAR_MOTIVO,
            F_DATA_HORA_INSERCAO_MOTIVO,
            F_COD_COLABORADOR_AUTENTICADO) RETURNING CODIGO INTO V_COD_MOTIVO;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao inserir o motivo, tente novamente.');
    END IF;

    RETURN V_COD_MOTIVO;
END;
$$;
