-- Adiciona audit para deleção na tabela de intervalo
CREATE TRIGGER TG_FUNC_AUDIT_INTERVALO
    AFTER DELETE
    ON INTERVALO
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

-- Adiciona índice para otimizar a deleção na tabela de inconsistências.
CREATE INDEX MARCACAO_INCONSISTENCIA_COD_MARCACAO_INCONSISTENTE_INDEX
    ON MARCACAO_INCONSISTENCIA (COD_MARCACAO_INCONSISTENTE);

-- Adiciona índice para otimizar a deleção na tabela de histórico.
CREATE INDEX MARCACAO_HISTORICO_COD_MARCACAO_INDEX
    ON MARCACAO_HISTORICO (COD_MARCACAO);

-- Cria function de suporte para deleção de intervalos e dependências.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_INTERVALO_DELETA_INTERVALO_BY_CODIGO(F_COD_UNIDADE_INTERVALO BIGINT,
                                                                             F_CPF BIGINT,
                                                                             F_COD_INTERVALO BIGINT,
                                                                             OUT MENSAGEM_SUCESSO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_COLABORADOR BIGINT   := (SELECT C.CODIGO
                                   FROM COLABORADOR C
                                            JOIN INTERVALO I ON I.CODIGO = F_COD_INTERVALO AND I.CPF_COLABORADOR = C.CPF
                                       AND C.CPF = F_CPF
                                       AND I.COD_UNIDADE = F_COD_UNIDADE_INTERVALO);
    V_CODS_AJUSTE     BIGINT[] := (SELECT ARRAY_AGG(MH.COD_AJUSTE)
                                   FROM MARCACAO_HISTORICO MH
                                   WHERE MH.COD_MARCACAO = F_COD_INTERVALO);
BEGIN
    --Verifica se o colaborador pertence a essa unidade.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_INTERVALO);

    -- Existe a possibilidade do colaborador ter registrado um intervalo em uma unidade e posteriormente ter sido
    -- transferido para outra, por este motivo utilizamos o código da unidade do intervalo para aumentar a segurança.

    -- Verifica se o intervalo a ser deletado foi registrado pelo colaborador informado e na unidade informada.
    IF V_COD_COLABORADOR IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar este intervalo para este colaborador nesta unidade.');
    END IF;

    DELETE
    FROM MARCACAO_INCONSISTENCIA MI
    WHERE MI.COD_MARCACAO_INCONSISTENTE = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
    WHERE MVIF.COD_MARCACAO_INICIO = F_COD_INTERVALO
       OR MVIF.COD_MARCACAO_FIM = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_INICIO MI
    WHERE MI.COD_MARCACAO_INICIO = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_FIM MF
    WHERE MF.COD_MARCACAO_FIM = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_HISTORICO MH
    WHERE MH.COD_MARCACAO = F_COD_INTERVALO;

    DELETE
    FROM INTERVALO I
    WHERE I.CODIGO = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_AJUSTE MJ
    WHERE MJ.COD_COLABORADOR_AJUSTE = V_COD_COLABORADOR
      AND MJ.CODIGO = ANY(V_CODS_AJUSTE);

    SELECT 'O intervalo foi apagado com sucesso.' INTO MENSAGEM_SUCESSO;
END;
$$;