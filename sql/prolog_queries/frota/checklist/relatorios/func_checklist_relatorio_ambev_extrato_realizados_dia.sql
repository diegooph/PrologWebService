CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(F_COD_UNIDADES BIGINT[],
                                                                                 F_DATA_INICIAL DATE,
                                                                                 F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"         TEXT,
                "DATA"            TEXT,
                "PLACA"           TEXT,
                "TIPO DE VEÍCULO" TEXT,
                "CHECKS SAÍDA"    BIGINT,
                "CHECKS RETORNO"  BIGINT
            )
    LANGUAGE SQL
AS
$$
WITH MAPAS AS (
    SELECT M.DATA AS DATA_MAPA,
           M.MAPA,
           M.PLACA
    FROM MAPA M
             JOIN VEICULO V ON V.PLACA = M.PLACA
    WHERE M.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND M.DATA >= F_DATA_INICIAL
      AND M.DATA <= F_DATA_FINAL
    ORDER BY M.DATA),
     CHECKS AS (SELECT C.COD_UNIDADE                                              AS COD_UNIDADE,
                       (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE AS DATA,
                       V.PLACA                                                    AS PLACA_VEICULO,
                       VT.NOME                                                    AS TIPO_VEICULO,
                       SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)              AS CHECKS_SAIDA,
                       SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)              AS CHECKS_RETORNO
                FROM CHECKLIST C
                         JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                         JOIN VEICULO V ON V.CODIGO = C.COD_VEICULO
                         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
                         LEFT JOIN MAPAS AS M
                                   ON M.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE
                                       AND M.PLACA = V.PLACA
                WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
                GROUP BY C.COD_UNIDADE, DATA, V.PLACA, VT.NOME
                ORDER BY C.COD_UNIDADE, DATA, V.PLACA, VT.NOME)

SELECT (SELECT NOME
        FROM UNIDADE U
        WHERE U.CODIGO = C.COD_UNIDADE) AS NOME_UNIDADE,
       TO_CHAR(C.DATA, 'DD/MM/YYYY')    AS DATA,
       C.PLACA_VEICULO,
       C.TIPO_VEICULO,
       C.CHECKS_SAIDA,
       C.CHECKS_RETORNO
FROM CHECKS C
ORDER BY NOME_UNIDADE, DATA
$$;