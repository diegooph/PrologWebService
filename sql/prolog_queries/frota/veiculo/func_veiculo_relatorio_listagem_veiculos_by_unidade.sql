CREATE OR REPLACE FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                UNIDADE                  TEXT,
                PLACA                    TEXT,
                "IDENTIFICADOR FROTA"    TEXT,
                MARCA                    TEXT,
                MODELO                   TEXT,
                TIPO                     TEXT,
                "DIAGRAMA VINCULADO?"    TEXT,
                "KM ATUAL"               TEXT,
                STATUS                   TEXT,
                "DATA/HORA CADASTRO"     TEXT,
                "VEÍCULO COMPLETO"       TEXT,
                "QTD PNEUS VINCULADOS"   TEXT,
                "QTD POSIÇÕES DIAGRAMA"  TEXT,
                "QTD POSIÇÕES SEM PNEUS" TEXT,
                "QTD ESTEPES"            TEXT
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    ESTEPES            INTEGER := 900;
    POSICOES_SEM_PNEUS INTEGER = 0;
    SIM                TEXT    := 'SIM';
    NAO                TEXT    := 'NÃO';
BEGIN
    RETURN QUERY
        -- Calcula a quantidade de pneus e estepes que estão vinculados na placa.
        WITH QTD_PNEUS_VINCULADOS_PLACA AS (
            SELECT V.PLACA,
                   COUNT(V.PLACA)
                   FILTER (WHERE VP.POSICAO < ESTEPES)  AS QTD_PNEUS_VINCULADOS,
                   COUNT(VP.COD_VEICULO)
                   FILTER (WHERE VP.POSICAO >= ESTEPES) AS QTD_ESTEPES_VINCULADOS,
                   VT.COD_DIAGRAMA
            FROM VEICULO V
                     JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
                     LEFT JOIN VEICULO_PNEU VP ON V.CODIGO = VP.COD_VEICULO AND V.COD_UNIDADE = VP.COD_UNIDADE
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
            GROUP BY V.PLACA,
                     VT.COD_DIAGRAMA
        ),

             -- Calcula a quantidade de posições nos diagramas que existem no prolog.
             QTD_POSICOES_DIAGRAMA AS (
                 SELECT VDE.COD_DIAGRAMA,
                        SUM(VDE.QT_PNEUS) AS QTD_POSICOES_DIAGRAMA
                 FROM VEICULO_DIAGRAMA_EIXOS VDE
                 GROUP BY COD_DIAGRAMA
             )

        SELECT U.NOME :: TEXT                                                     AS UNIDADE,
               V.PLACA :: TEXT                                                    AS PLACA,
               V.IDENTIFICADOR_FROTA :: TEXT                                      AS IDENTIFICADOR_FROTA,
               MA.NOME :: TEXT                                                    AS MARCA,
               MO.NOME :: TEXT                                                    AS MODELO,
               VT.NOME :: TEXT                                                    AS TIPO,
               CASE
                   WHEN QPVP.COD_DIAGRAMA IS NULL
                       THEN 'NÃO'
                   ELSE 'SIM' END                                                 AS POSSUI_DIAGRAMA,
               V.KM :: TEXT                                                       AS KM_ATUAL,
               F_IF(V.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)           AS STATUS,
               COALESCE(TO_CHAR(V.DATA_HORA_CADASTRO, 'DD/MM/YYYY HH24:MI'), '-') AS DATA_HORA_CADASTRO,
               -- Caso a quantidade de posições sem pneus seja 0 é porque o veículo está com todos os pneus - veículo completo.
               CASE
                   WHEN (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) = POSICOES_SEM_PNEUS
                       THEN SIM
                   ELSE NAO END                                                   AS VEICULO_COMPLETO,
               QPVP.QTD_PNEUS_VINCULADOS :: TEXT                                  AS QTD_PNEUS_VINCULADOS,
               QSD.QTD_POSICOES_DIAGRAMA :: TEXT                                  AS QTD_POSICOES_DIAGRAMA,
               -- Calcula a quantidade de posições sem pneus.
               (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) :: TEXT    AS QTD_POSICOES_SEM_PNEUS,
               QPVP.QTD_ESTEPES_VINCULADOS :: TEXT                                AS QTD_ESTEPES_VINCULADOS
        FROM VEICULO V
                 JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
                 JOIN MODELO_VEICULO MO ON V.COD_MODELO = MO.CODIGO
                 JOIN MARCA_VEICULO MA ON MO.COD_MARCA = MA.CODIGO
                 JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
                 RIGHT JOIN QTD_PNEUS_VINCULADOS_PLACA QPVP ON QPVP.PLACA = V.PLACA
                 LEFT JOIN QTD_POSICOES_DIAGRAMA QSD ON QSD.COD_DIAGRAMA = QPVP.COD_DIAGRAMA
        ORDER BY U.NOME ASC,
                 STATUS ASC,
                 V.PLACA ASC,
                 MA.NOME ASC,
                 MO.NOME ASC,
                 VT.NOME ASC,
                 QTD_POSICOES_SEM_PNEUS DESC;
END;
$$;