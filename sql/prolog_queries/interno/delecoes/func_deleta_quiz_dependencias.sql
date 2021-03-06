CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_QUIZ_DEPENDENCIAS(F_COD_UNIDADES BIGINT[], F_COD_QUIZ BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM QUIZ_RESPOSTAS QR
    WHERE QR.COD_QUIZ = ANY (F_COD_QUIZ);

    DELETE
    FROM QUIZ_ALTERNATIVA_PERGUNTA QAP
    WHERE QAP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_PERGUNTAS QP
    WHERE QP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_MODELO_TREINAMENTO QMT
    WHERE QMT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ Q
    WHERE Q.CODIGO = ANY (F_COD_QUIZ);
END;
$$;