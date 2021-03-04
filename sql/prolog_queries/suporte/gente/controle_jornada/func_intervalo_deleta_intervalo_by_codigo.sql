-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta um intervalo e suas dependências com base no CPF e no código de intervalo.
--
-- Histórico:
-- 2020-07-15 -> Function criada (wvinim - PL-2825).
-- 2020-07-20 -> Corrige erro na function na deleção de históricos (wvinim - PL-2825).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
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
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
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