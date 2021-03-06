CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                       F_COD_CHECKLISTS BIGINT[],
                                                                       F_COD_CHECKLISTS_MODELO BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_ITENS_OS     BIGINT[] := (SELECT ARRAY_AGG(COSID.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                                    WHERE COSID.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_COD_OS           BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_CODIGO_OS_PROLOG BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO_PROLOG)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
BEGIN
    -- Tornando a constraint deferível
    SET CONSTRAINTS FK_CHECKLIST_MODELO_CHECKLIST_MODELO_VERSAO DEFERRED;

    -- Deleção de checklists realizados.

    DELETE
    FROM CHECKLIST_RESPOSTAS_NOK CRN
    WHERE CRN.COD_CHECKLIST = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA COSIDT
    WHERE COSIDT.COD_ITEM_OS_PROLOG = ANY (V_COD_ITENS_OS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS COSIA
    WHERE COSIA.COD_CHECKLIST_REALIZADO = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
    WHERE COSID.CODIGO = ANY (V_COD_ITENS_OS)
      AND COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA COSDT
    WHERE COSDT.COD_OS_PROLOG = ANY (V_CODIGO_OS_PROLOG);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
    WHERE COSD.CODIGO = ANY (V_COD_OS)
      AND COSD.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_DATA CD
    WHERE CD.CODIGO = ANY (F_COD_CHECKLISTS);

    -- Deleção de modelos.

    -- DROPA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    DROP RULE ALTERNATIVA_CHECK_DELETE_PROTECT ON CHECKLIST_ALTERNATIVA_PERGUNTA_DATA;
    -- DELETA ALTERNATIVA.
    DELETE
    FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
    WHERE CAPD.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CAPD.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);
    -- RECRIA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    CREATE RULE ALTERNATIVA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_ALTERNATIVA_PERGUNTA_DATA DO INSTEAD NOTHING;

    -- DROPA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    DROP RULE PERGUNTA_CHECK_DELETE_PROTECT ON CHECKLIST_PERGUNTAS_DATA;
    -- DELETA PERGUNTA.
    DELETE
    FROM CHECKLIST_PERGUNTAS_DATA CP
    WHERE CP.COD_UNIDADE = ANY (F_COD_UNIDADES);
    -- RECRIA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    CREATE RULE PERGUNTA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_PERGUNTAS_DATA DO INSTEAD NOTHING;

    -- DELETA MODELO DE VERSÃO DE CHECK
    DELETE
    FROM CHECKLIST_MODELO_VERSAO CMV
    WHERE CMV.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);

    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
    WHERE CMVT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_FUNCAO CMF
    WHERE CMF.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_DATA CMD
    WHERE CMD.CODIGO = ANY (F_COD_CHECKLISTS_MODELO);
END;
$$;