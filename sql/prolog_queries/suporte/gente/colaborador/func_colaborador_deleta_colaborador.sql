CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_CPF_COLABORADOR BIGINT,
                                                                       F_COD_COLABORADOR BIGINT,
                                                                       F_COD_UNIDADE BIGINT,
                                                                       F_MOTIVO_DELECAO TEXT,
                                                                       OUT AVISO_COLABORADOR_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    F_COD_EMPRESA          BIGINT := (SELECT U.COD_EMPRESA
                                      FROM UNIDADE U
                                      WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica integridade de colaborador com unidade.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(F_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Deleta colaborador.
    UPDATE COLABORADOR_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CPF = F_CPF_COLABORADOR;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %',
            F_COD_COLABORADOR, F_CPF_COLABORADOR, F_COD_UNIDADE;
    END IF;

    -- Desloga colaborador.
    DELETE FROM TOKEN_AUTENTICACAO WHERE COD_COLABORADOR = F_COD_COLABORADOR AND CPF_COLABORADOR = F_CPF_COLABORADOR;

    SELECT 'COLABORADOR COM CÓDIGO: '
               || F_COD_COLABORADOR
               || ', CPF: '
               || F_CPF_COLABORADOR
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ' DELETADO COM SUCESSO!'
    INTO AVISO_COLABORADOR_DELETADO;
END
$$;