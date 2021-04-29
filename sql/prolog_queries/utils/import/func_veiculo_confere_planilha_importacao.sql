CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_CONFERE_PLANILHA_IMPORTACAO(F_COD_UNIDADE BIGINT,
                                                                                F_JSON_VEICULOS JSONB)
    RETURNS TABLE
            (
                "PLACA NA PLANILHA"           TEXT,
                "PLACA DUPLICADA NA PLANILHA" TEXT,
                "PLACA PLANILHA FORMATADA"    TEXT,
                "POSSUI 7 DIGITOS?"           TEXT,
                "PLACA_NA_UNIDADE/EMPRESA"    TEXT,
                "STATUS_ATIVO_BANCO"          BOOLEAN,
                "KM VEICULO"                  BIGINT,
                "MARCA PLANILHA"              TEXT,
                "MARCA BANCO"                 TEXT,
                "COD_MARCA"                   TEXT,
                "MODELO PLANILHA"             TEXT,
                "MODELO BANCO"                TEXT,
                "COD_MODELO"                  TEXT,
                "DIAGRAMA PLANILHA"           TEXT,
                "DIAGRAMA BANCO"              TEXT,
                "COD_DIAGRAMA"                TEXT,
                "TIPO PLANILHA"               TEXT,
                "TIPO BANCO"                  TEXT,
                "COD_TIPO"                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA CONSTANT            BIGINT := (SELECT U.COD_EMPRESA
                                                 FROM UNIDADE U
                                                 WHERE U.CODIGO = F_COD_UNIDADE);
    F_SIMILARIDADE CONSTANT           REAL   = 0.4;
    F_SIMILIARIDADE_DIAGRAMA CONSTANT REAL   = 0.5;
    F_SEM_SIMILARIDADE CONSTANT       REAL   = 0.0;
    NAO_ENCONTRADO CONSTANT           TEXT   := '-';
BEGIN
    CREATE TEMP TABLE IF NOT EXISTS TABLE_JSON
    (
        CODIGO             BIGSERIAL,
        PLACA              TEXT,
        PLACA_FORMATADA    TEXT,
        KM                 BIGINT,
        MARCA              TEXT,
        MARCA_FORMATADA    TEXT,
        MODELO             TEXT,
        MODELO_FORMATADO   TEXT,
        TIPO               TEXT,
        TIPO_FORMATADO     TEXT,
        DIAGRAMA           TEXT,
        DIAGRAMA_FORMATADO TEXT
    ) ON COMMIT DELETE ROWS;
    INSERT
    INTO TABLE_JSON (PLACA,
                     PLACA_FORMATADA,
                     KM,
                     MARCA,
                     MARCA_FORMATADA,
                     MODELO,
                     MODELO_FORMATADO,
                     TIPO,
                     TIPO_FORMATADO,
                     DIAGRAMA,
                     DIAGRAMA_FORMATADO)
    SELECT (SRC ->> 'placa') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> 'placa')) :: TEXT,
           (SRC ->> 'km') :: BIGINT,
           (SRC ->> 'marca') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('marca')) :: TEXT,
           (SRC ->> 'modelo') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('modelo')) :: TEXT,
           (SRC ->> 'tipo') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('tipo')) :: TEXT,
           (SRC ->> 'diagrama') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('diagrama')) :: TEXT
    FROM JSONB_ARRAY_ELEMENTS(F_JSON_VEICULOS) SRC;

    RETURN QUERY
        -- PROCURA PLACAS DUPLICADAS.
        WITH PROCURA_PLACAS_DUPLICADAS AS (
            SELECT TJ.PLACA_FORMATADA,
                   COUNT(TJ.PLACA_FORMATADA) AS PLACAS_DUPLICADAS
            FROM TABLE_JSON TJ
            GROUP BY TJ.PLACA_FORMATADA
        ),

             -- VERIFICAÇÕES PLACA.
             VERIFICACOES_PLACA AS (
                 SELECT TJ.CODIGO,
                        TJ.PLACA AS PLACA_PLANILHA,
                        CASE
                            WHEN (PPD.PLACAS_DUPLICADAS > 1)
                                THEN 'SIM'
                            ELSE NAO_ENCONTRADO
                            END  AS PLACAS_DUPLICADAS,
                        TJ.PLACA_FORMATADA,
                        V.PLACA  AS PLACA_VEICULO,
                        V.STATUS_ATIVO,
                        CASE
                            WHEN (V.PLACA IS NULL)
                                THEN NAO_ENCONTRADO
                            -- Vai retornar assim: ("Unidade Teste", "Zalf Sistemas").
                            ELSE (U.NOME, E.NOME) :: TEXT
                            END  AS PLACA_NA_UNIDADE_EMPRESA,
                        CASE
                            WHEN LENGTH(TJ.PLACA_FORMATADA) <> 7
                                THEN 'NÃO POSSUI'
                            ELSE 'SIM'
                            END  AS PLACA_7_DIGITOS,
                        TJ.KM
                 FROM TABLE_JSON TJ
                          JOIN PROCURA_PLACAS_DUPLICADAS PPD
                               ON PPD.PLACA_FORMATADA = TJ.PLACA_FORMATADA
                          LEFT JOIN VEICULO V ON TJ.PLACA_FORMATADA ILIKE V.PLACA
                          LEFT JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
             ),

             -- PROCURA SIMILARIDADE DE MARCA DE VEÍCULO NO BANCO.
             SIMILARIDADE_MARCAS_VEICULOS AS (
                 SELECT DISTINCT ON (TJ.MARCA_FORMATADA) TJ.MARCA                                                  AS MARCA_PLANILHA,
                                                         TJ.MARCA_FORMATADA                                        AS MARCA_PLANILHA_FORMATADA,
                                                         MAV.NOME                                                  AS MARCA_VEICULO_BANCO,
                                                         MAV.CODIGO                                                AS COD_MARCA_BANCO,
                                                         MAX(FUNC_GERA_SIMILARIDADE(TJ.MARCA_FORMATADA, MAV.NOME)) AS SIMILARIEDADE_MARCA
                 FROM TABLE_JSON TJ
                          FULL JOIN MARCA_VEICULO MAV ON TRUE
                 GROUP BY TJ.MARCA_FORMATADA, TJ.MARCA, MAV.NOME, MAV.CODIGO
                 ORDER BY TJ.MARCA_FORMATADA, SIMILARIEDADE_MARCA DESC
             ),

             -- REMOVE MARCAS_MODELOS IGUAIS.
             DISTINCT_MARCAS_MODELOS_VEICULOS AS (
                 SELECT DISTINCT ON (CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO_FORMATADO)) SMV.COD_MARCA_BANCO,
                                                                                       TJ.MARCA                                         AS MARCA_PLANILHA,
                                                                                       TJ.MODELO                                        AS MODELO_PLANILHA,
                                                                                       CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO)           AS MARCA_MODELO_PLANILHA,
                                                                                       CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO_FORMATADO) AS MARCA_MODELO_PLANILHA_FORMATADA
                 FROM TABLE_JSON TJ
                          JOIN SIMILARIDADE_MARCAS_VEICULOS SMV ON TJ.MARCA = SMV.MARCA_PLANILHA
             ),

             -- PROCURA SIMILARIEDADE DE MARCA_MODELO DE VEÍCULO NO BANCO.
             SIMILARIEDADE_MARCAS_MODELOS_VEICULOS AS (
                 SELECT DISTINCT ON (DMMV.MARCA_MODELO_PLANILHA_FORMATADA) DMMV.MARCA_PLANILHA                  AS MARCA_PLANILHA,
                                                                           DMMV.MODELO_PLANILHA                 AS MODELO_PLANILHA,
                                                                           DMMV.MARCA_MODELO_PLANILHA           AS MARCA_MODELO_PLANILHA,
                                                                           DMMV.MARCA_MODELO_PLANILHA_FORMATADA AS MARCA_MODELO_PLANILHA_FORMATADA,
                                                                           MAV.NOME                             AS MARCA_VEICULO_BANCO,
                                                                           MAV.CODIGO                           AS COD_MARCA_VEICULO,
                                                                           MOV.NOME                             AS MODELO_VEICULO_BANCO,
                                                                           MOV.CODIGO                           AS COD_MODELO_VEICULO,
                                                                           CASE
                                                                               WHEN DMMV.COD_MARCA_BANCO = MAV.CODIGO
                                                                                   THEN
                                                                                   MAX(FUNC_GERA_SIMILARIDADE(DMMV.MODELO_PLANILHA, MOV.NOME))
                                                                               ELSE F_SEM_SIMILARIDADE
                                                                               END                              AS SIMILARIEDADE_MARCA_MODELO
                 FROM DISTINCT_MARCAS_MODELOS_VEICULOS DMMV
                          FULL JOIN MARCA_VEICULO MAV ON TRUE
                          JOIN MODELO_VEICULO MOV ON MAV.CODIGO = MOV.COD_MARCA
                 WHERE MOV.COD_EMPRESA = F_COD_EMPRESA
                 GROUP BY DMMV.MARCA_MODELO_PLANILHA_FORMATADA, DMMV.MARCA_MODELO_PLANILHA, DMMV.MARCA_PLANILHA,
                          DMMV.COD_MARCA_BANCO,
                          DMMV.MODELO_PLANILHA, MAV.NOME,
                          MAV.CODIGO, MOV.NOME,
                          MOV.CODIGO
                 ORDER BY DMMV.MARCA_MODELO_PLANILHA_FORMATADA, SIMILARIEDADE_MARCA_MODELO DESC
             ),

             -- PROCURA DIAGRAMA NO BANCO.
             SIMILARIEDADE_DIAGRAMA AS (
                 SELECT DISTINCT ON (TJ.DIAGRAMA_FORMATADO ) TJ.DIAGRAMA                                                 AS DIAGRAMA_PLANILHA,
                                                             TJ.DIAGRAMA_FORMATADO                                       AS DIAGRAMA_PLANILHA_FORMATADO,
                                                             VD.NOME                                                     AS DIAGRAMA_BANCO,
                                                             VD.CODIGO                                                   AS COD_DIAGRAMA_BANCO,
                                                             MAX(FUNC_GERA_SIMILARIDADE(TJ.DIAGRAMA_FORMATADO, VD.NOME)) AS SIMILARIEDADE_DIAGRAMA
                 FROM TABLE_JSON TJ
                          FULL JOIN VEICULO_DIAGRAMA VD ON TRUE
                 GROUP BY TJ.DIAGRAMA_FORMATADO, TJ.DIAGRAMA, VD.NOME, VD.CODIGO
                 ORDER BY TJ.DIAGRAMA_FORMATADO, SIMILARIEDADE_DIAGRAMA DESC
             ),

             -- REMOVE TIPOS IGUAIS.
             DISTINCT_TIPOS_DIAGRAMA_VEICULOS AS (
                 SELECT DISTINCT ON (CONCAT(SD.COD_DIAGRAMA_BANCO, TJ.TIPO_FORMATADO)) SD.COD_DIAGRAMA_BANCO,
                                                                                       TJ.DIAGRAMA                                      AS DIAGRAMA_PLANILHA,
                                                                                       TJ.TIPO                                          AS TIPO_PLANILHA,
                                                                                       CONCAT(TJ.TIPO, TJ.DIAGRAMA)                     AS TIPO_DIAGRAMA_PLANILHA,
                                                                                       CONCAT(TJ.TIPO_FORMATADO, TJ.DIAGRAMA_FORMATADO) AS TIPO_DIAGRAMA_PLANILHA_FORMATADO
                 FROM TABLE_JSON TJ
                          JOIN SIMILARIEDADE_DIAGRAMA SD ON TJ.DIAGRAMA = SD.DIAGRAMA_PLANILHA
             ),

             -- PROCURA SIMILARIDADE DE TIPO_DIAGRAMA DE VEÍCULO NO BANCO.
             SIMILARIDADE_TIPOS_DIAGRAMA_VEICULOS AS (
                 SELECT DISTINCT ON (DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO) DTDV.DIAGRAMA_PLANILHA                AS DIAGRAMA_PLANILHA,
                                                                            DTDV.TIPO_PLANILHA                    AS TIPO_PLANILHA,
                                                                            DTDV.TIPO_DIAGRAMA_PLANILHA           AS TIPO_DIAGRAMA_PLANILHA,
                                                                            DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO AS TIPO_DIAGRAMA_PLANILHA_FORMATADO,
                                                                            VD.NOME                               AS DIAGRAMA_VEICULO_BANCO,
                                                                            VD.CODIGO                             AS COD_DIAGRAMA_VEICULO,
                                                                            VT.NOME                               AS TIPO_BANCO,
                                                                            VT.CODIGO                             AS COD_TIPO_VEICULO,
                                                                            CASE
                                                                                WHEN DTDV.COD_DIAGRAMA_BANCO = VD.CODIGO
                                                                                    THEN MAX(FUNC_GERA_SIMILARIDADE(DTDV.TIPO_PLANILHA, VT.NOME))
                                                                                ELSE F_SEM_SIMILARIDADE
                                                                                END                               AS SIMILARIEDADE_TIPO_DIAGRAMA
                 FROM DISTINCT_TIPOS_DIAGRAMA_VEICULOS DTDV
                          FULL JOIN VEICULO_TIPO VT ON TRUE
                          LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 WHERE VT.COD_EMPRESA = F_COD_EMPRESA
                 GROUP BY DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO,
                          DTDV.TIPO_DIAGRAMA_PLANILHA,
                          DTDV.DIAGRAMA_PLANILHA,
                          DTDV.COD_DIAGRAMA_BANCO,
                          DTDV.TIPO_PLANILHA,
                          VT.NOME,
                          VT.CODIGO,
                          VD.NOME,
                          VD.CODIGO
                 ORDER BY DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO, SIMILARIEDADE_TIPO_DIAGRAMA DESC
             )

        SELECT VP.PLACA_PLANILHA,
               VP.PLACAS_DUPLICADAS,
               VP.PLACA_FORMATADA,
               VP.PLACA_7_DIGITOS,
               VP.PLACA_NA_UNIDADE_EMPRESA :: TEXT,
               F_IF(VP.PLACA_NA_UNIDADE_EMPRESA != NAO_ENCONTRADO, VP.STATUS_ATIVO, NULL),
               VP.KM,
               SMV.MARCA_PLANILHA,
               F_IF(SMV.SIMILARIEDADE_MARCA >= F_SIMILARIDADE, SMV.MARCA_VEICULO_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(SMV.SIMILARIEDADE_MARCA >= F_SIMILARIDADE, SMV.COD_MARCA_BANCO :: TEXT, NAO_ENCONTRADO),
               SMMV.MODELO_PLANILHA,
               F_IF(SMMV.SIMILARIEDADE_MARCA_MODELO >= F_SIMILARIDADE, SMMV.MODELO_VEICULO_BANCO :: TEXT,
                    NAO_ENCONTRADO),
               F_IF(SMMV.SIMILARIEDADE_MARCA_MODELO >= F_SIMILARIDADE, SMMV.COD_MODELO_VEICULO :: TEXT, NAO_ENCONTRADO),
               SD.DIAGRAMA_PLANILHA,
               F_IF(SD.SIMILARIEDADE_DIAGRAMA >= F_SIMILIARIDADE_DIAGRAMA, SD.DIAGRAMA_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(SD.SIMILARIEDADE_DIAGRAMA >= F_SIMILIARIDADE_DIAGRAMA, SD.COD_DIAGRAMA_BANCO :: TEXT,
                    NAO_ENCONTRADO) AS COD_DIAGRAMA,
               STDV.TIPO_PLANILHA,
               F_IF(STDV.SIMILARIEDADE_TIPO_DIAGRAMA >= F_SIMILARIDADE, STDV.TIPO_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(STDV.SIMILARIEDADE_TIPO_DIAGRAMA >= F_SIMILARIDADE, STDV.COD_TIPO_VEICULO :: TEXT,
                    NAO_ENCONTRADO) AS COD_TIPO
        FROM VERIFICACOES_PLACA VP
                 JOIN TABLE_JSON TJ ON TJ.CODIGO = VP.CODIGO
                 JOIN SIMILARIDADE_MARCAS_VEICULOS SMV ON TJ.MARCA = SMV.MARCA_PLANILHA
                 JOIN SIMILARIEDADE_MARCAS_MODELOS_VEICULOS SMMV ON SMV.MARCA_PLANILHA = SMMV.MARCA_PLANILHA
                 JOIN SIMILARIEDADE_DIAGRAMA SD ON TJ.DIAGRAMA = SD.DIAGRAMA_PLANILHA
                 JOIN SIMILARIDADE_TIPOS_DIAGRAMA_VEICULOS STDV ON SD.DIAGRAMA_PLANILHA = STDV.DIAGRAMA_PLANILHA;
    DROP TABLE TABLE_JSON;
END;
$$;