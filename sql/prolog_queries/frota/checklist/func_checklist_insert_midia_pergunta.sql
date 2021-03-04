-- Sobre:
--
-- Essa function insere as mídias das perguntas OK na realização de um checklist.
--
-- Histórico:
-- 2020-07-07 -> Function criada (luiz_fp - PL-2705).
-- 2020-07-13 -> Criação do arquivo específico e documentação (wvinim - PL-2824).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_MIDIA_PERGUNTA(F_UUID_MIDIA UUID,
                                                                F_COD_CHECKLIST BIGINT,
                                                                F_COD_PERGUNTA BIGINT,
                                                                F_URL_MIDIA TEXT)
    RETURNS VOID
    LANGUAGE SQL
AS
$$
INSERT INTO CHECKLIST_RESPOSTAS_MIDIAS_PERGUNTAS_OK(UUID,
                                                    COD_CHECKLIST,
                                                    COD_PERGUNTA,
                                                    URL_MIDIA,
                                                    TIPO_MIDIA)
VALUES (F_UUID_MIDIA,
        F_COD_CHECKLIST,
        F_COD_PERGUNTA,
        TRIM(F_URL_MIDIA),
        'IMAGEM')
ON CONFLICT ON CONSTRAINT PK_CHECKLIST_RESPOSTAS_MIDIAS_PERGUNTAS_OK DO NOTHING;
$$;