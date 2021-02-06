-- Sobre:
--
-- A lógica aplicada nessa function é a seguinte:
-- Altera o km do fechamento da OS.
--
-- Précondições:
-- FUNC_GARANTE_UNIDADE_EXISTE criada.
--
-- Histórico:
-- 2020-04-29 -> Function criada (thaisksf - PL-2663).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_OS_ALTERA_KM_FECHAMENTO_OS(F_COD_UNIDADE BIGINT,
                                                                             F_COD_OS BIGINT,
                                                                             F_KM_ATUAL BIGINT,
                                                                             F_KM_CORRETO BIGINT,
                                                                             OUT F_AVISO_KM_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_UPDATES  BIGINT;
    V_COD_ITENS_OS BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- GARANTE QUE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE EXISTE ITENS NA ORDEM DE SERVIÇO.
    IF NOT EXISTS(SELECT COSI.COD_OS
                  FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                  WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
                    AND COSI.COD_OS = F_COD_OS
                    AND COSI.KM = F_KM_ATUAL)
    THEN
        RAISE EXCEPTION
            'Não existem itens de ordem de serviço com esses dados:
            código da unidade = % | código da ordem de serviço = % | km = % .', F_COD_UNIDADE, F_COD_OS, F_KM_ATUAL;
    ELSE
        V_COD_ITENS_OS := (SELECT array_agg(COSI.CODIGO)
                           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                           WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
                             AND COSI.COD_OS = F_COD_OS
                             AND COSI.KM = F_KM_ATUAL);
    END IF;

    -- REALIZA A ALTERAÇÃO DO KM.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET KM = F_KM_CORRETO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_OS = F_COD_OS
      AND KM = F_KM_ATUAL;

    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
    IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível modificar o km da OS % na tabela checklist_ordem_servico_itens.', F_COD_OS;
    ELSE
        SELECT 'O km da OS foi modificado de ' || F_KM_ATUAL ||
               ' para ' || F_KM_CORRETO || ' nos tens:' || V_COD_ITENS_OS::TEXT
        INTO F_AVISO_KM_ALTERADO;
    END IF;
END;
$$;