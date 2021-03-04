CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"            TEXT,
                "DATA"               TEXT,
                "QTD CHECKS SAÍDA"   BIGINT,
                "ADERÊNCIA SAÍDA"    TEXT,
                "QTD CHECKS RETORNO" BIGINT,
                "ADERÊNCIA RETORNO"  TEXT,
                "TOTAL DE CHECKS"    BIGINT,
                "TOTAL DE VIAGENS"   BIGINT,
                "ADERÊNCIA DIA"      TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT DADOS.NOME_UNIDADE                AS NOME_UNIDADE,
       TO_CHAR(DADOS.DATA, 'DD/MM/YYYY') AS DATA,
       DADOS.CHECKS_SAIDA                AS QTD_CHECKS_SAIDA,
       TRUNC((DADOS.CHECKS_SAIDA :: FLOAT / DADOS.TOTAL_VIAGENS) * 100) ||
       '%'                               AS ADERENCIA_SAIDA,
       DADOS.CHECKS_RETORNO              AS QTD_CHECKS_RETORNO,
       TRUNC((DADOS.CHECKS_RETORNO :: FLOAT / DADOS.TOTAL_VIAGENS) * 100) ||
       '%'                               AS ADERENCIA_RETORNO,
       DADOS.TOTAL_CHECKS                AS TOTAL_CHECKS,
       DADOS.TOTAL_VIAGENS               AS TOTAL_MAPAS,
       TRUNC(((DADOS.CHECKS_SAIDA + DADOS.CHECKS_RETORNO) :: FLOAT / (DADOS.TOTAL_VIAGENS * 2)) * 100) ||
       '%'                               AS ADERENCIA_DIA
FROM (SELECT U.NOME                                                       AS NOME_UNIDADE,
             (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
             SUM(CASE
                     WHEN C.TIPO = 'S'
                         THEN 1
                     ELSE 0 END)                                          AS CHECKS_SAIDA,
             SUM(CASE
                     WHEN C.TIPO = 'R'
                         THEN 1
                     ELSE 0 END)                                          AS CHECKS_RETORNO,
             COUNT(C.DATA_HORA :: DATE)                                   AS TOTAL_CHECKS,
             DIA_ESCALA.TOTAL_VIAGENS                                     AS TOTAL_VIAGENS
      FROM CHECKLIST C
               JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
               LEFT JOIN (SELECT ED.DATA         AS DATA_ESCALA,
                                 COUNT(ED.PLACA) AS TOTAL_VIAGENS
                          FROM ESCALA_DIARIA ED
                                   JOIN VEICULO V ON V.PLACA = ED.PLACA
                          WHERE ED.COD_UNIDADE = ANY (ARRAY [F_COD_UNIDADES])
                            AND ED.DATA >= F_DATA_INICIAL
                            AND ED.DATA <= F_DATA_FINAL
                          GROUP BY ED.DATA
                          ORDER BY ED.DATA ASC) AS DIA_ESCALA
                         ON DIA_ESCALA.DATA_ESCALA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, DATA, DIA_ESCALA.TOTAL_VIAGENS
      ORDER BY U.NOME, (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE) AS DADOS
$$;