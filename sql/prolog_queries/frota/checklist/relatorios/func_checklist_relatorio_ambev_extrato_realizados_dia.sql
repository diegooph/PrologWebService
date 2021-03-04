-- Sobre:
-- Busca um extrato dos checklists realizados no dia e que se enquadram nos parâmetros de filtro utilizados.
--
-- Parâmetros:
-- F_COD_UNIDADES: Um array com os códigos das unidades das quais queremos buscar os checklists realizados.
-- F_DATA_INICIAL: Data inicial pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou após serão
--                 buscados.
-- F_DATA_FINAL: Data final pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou antes serão
--               buscados.
--
-- Todos os parâmetros são obrigatórios.
--
-- Histórico:
-- 2020-03-25 -> Criada documentação da function (wvinim - PL-2546).
-- 2020-03-25 -> Adiciona a coluna de tipo de veículo (wvinim - PL-2546).
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
    ORDER BY M.DATA ASC),
     CHECKS AS (SELECT C.COD_UNIDADE                                              AS COD_UNIDADE,
                       (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE AS DATA,
                       C.PLACA_VEICULO                                            AS PLACA_VEICULO,
                       VT.NOME                                                    AS TIPO_VEICULO,
                       SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)              AS CHECKS_SAIDA,
                       SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)              AS CHECKS_RETORNO
                FROM CHECKLIST C
                         JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                         JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO
                         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
                         LEFT JOIN MAPAS AS M
                                   ON M.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE
                                       AND M.PLACA = C.PLACA_VEICULO
                WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
                  AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
                GROUP BY C.COD_UNIDADE, DATA, C.PLACA_VEICULO, VT.NOME
                ORDER BY C.COD_UNIDADE, DATA, C.PLACA_VEICULO, VT.NOME)

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