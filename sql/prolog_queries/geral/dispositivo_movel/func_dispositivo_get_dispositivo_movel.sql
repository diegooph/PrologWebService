-- Sobre:
--
-- Esta função retorna um dispositivo móvel e todos os seus IMEIs, com base no código da empresa e no código do
-- dispositivo.
-- Devido a estrutura adotada, o agrupamento pelo dispositivo deverá ser realizado no WS.
--
-- Histórico:
-- 2019-07-25 -> Function criada (wvinim - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_GET_DISPOSITIVO_MOVEL(F_COD_EMPRESA BIGINT, F_COD_DISPOSITIVO BIGINT)
  RETURNS TABLE(
    COD_EMPRESA     BIGINT,
    COD_MARCA       BIGINT,
    MARCA           TEXT,
    COD_DISPOSITIVO BIGINT,
    MODELO          TEXT,
    DESCRICAO       TEXT,
    COD_IMEI        BIGINT,
    IMEI            TEXT
  )
LANGUAGE SQL
AS $$
SELECT DM.COD_EMPRESA    AS COD_EMPRESA,
       DM.COD_MARCA      AS COD_MARCA,
       MD.NOME :: TEXT   AS MARCA,
       DM.CODIGO         AS COD_DISPOSITIVO,
       DM.MODELO :: TEXT AS MODELO,
       DM.DESCRICAO      AS DESCRICAO,
       DI.CODIGO         AS COD_IMEI,
       DI.IMEI :: TEXT   AS IMEI
FROM DISPOSITIVO_MOVEL_IMEI DI
       LEFT JOIN DISPOSITIVO_MOVEL DM ON DM.CODIGO = DI.COD_DISPOSITIVO
       LEFT JOIN DISPOSITIVO_MOVEL_MARCA_PROLOG MD ON MD.CODIGO = DM.COD_MARCA
WHERE DI.COD_EMPRESA = F_COD_EMPRESA
  AND DM.CODIGO = F_COD_DISPOSITIVO
ORDER BY DM.CODIGO, DI.CODIGO;
$$;