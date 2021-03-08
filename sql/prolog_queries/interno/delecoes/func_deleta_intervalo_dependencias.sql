-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta intervalo de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_INTERVALO_DEPENDENCIAS(F_COD_UNIDADES BIGINT[], F_COD_MARCACOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MARCACOES_VINCULO_INICIO_FIM BIGINT[] := (SELECT ARRAY_AGG(MVIF.CODIGO)
                                                    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                                                    WHERE (MVIF.COD_MARCACAO_INICIO = ANY (F_COD_MARCACOES))
                                                       OR (MVIF.COD_MARCACAO_FIM = ANY (F_COD_MARCACOES)));
BEGIN
    DELETE
    FROM MARCACAO_INCONSISTENCIA MI
    WHERE MI.COD_MARCACAO_VINCULO_INICIO_FIM = ANY (V_COD_MARCACOES_VINCULO_INICIO_FIM);

    DELETE
    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
    WHERE MVIF.CODIGO = ANY (V_COD_MARCACOES_VINCULO_INICIO_FIM);

    DELETE
    FROM MARCACAO_INICIO MI
    WHERE MI.COD_MARCACAO_INICIO = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_FIM MF
    WHERE MF.COD_MARCACAO_FIM = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_HISTORICO MH
    WHERE MH.COD_MARCACAO = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA MTDCJBL
    WHERE MTDCJBL.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM MARCACAO_TIPO_JORNADA MTJ
    WHERE MTJ.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO I
    WHERE I.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_UNIDADE IU
    WHERE IU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_TIPO_CARGO ITC
    WHERE ITC.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_TIPO IT
    WHERE IT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM COMERCIAL.UNIDADE_CARGO_CONTROLE_JORNADA UCCJ
    WHERE UCCJ.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM MARCACAO_AJUSTE MJ
    WHERE MJ.COD_UNIDADE_AJUSTE = ANY (F_COD_UNIDADES);
END;
$$;