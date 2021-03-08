CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "DATA/HORA AFERIÇÃO"        TEXT,
                "QUEM AFERIU?"              CHARACTER VARYING,
                "UNIDADE ALOCADO"           CHARACTER VARYING,
                "PNEU"                      CHARACTER VARYING,
                "MARCA"                     CHARACTER VARYING,
                "MODELO"                    CHARACTER VARYING,
                "MEDIDAS"                   TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT,
                "TIPO DE MEDIÇÃO"           TEXT,
                "VIDA"                      TEXT,
                "DOT"                       CHARACTER VARYING,
                "FORMA DE COLETA DOS DADOS" TEXT
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
    PNEU_NUNCA_AFERIDO            TEXT := 'Nunca Aferido';
    PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
    RETURN QUERY
        SELECT COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
                        PNEU_NUNCA_AFERIDO)                                                  AS ULTIMA_AFERICAO,
               C.NOME,
               U.NOME                                                                        AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE                                                              AS COD_PNEU,
               MAP.NOME                                                                      AS NOME_MARCA,
               MP.NOME                                                                       AS NOME_MODELO,
               ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)          AS MEDIDAS,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                            AV.ALTURA_SULCO_INTERNO))                        AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',') :: TEXT AS PRESSAO,
               A.TIPO_MEDICAO_COLETADA :: TEXT                                               AS TIPO_MEDICAO,
               P.VIDA_ATUAL::TEXT                                                            AS VIDA_ATUAL,
               COALESCE(P.DOT, '-')                                                          AS DOT,
               COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)                               AS FORMA_COLETA_DADOS
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
                 JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
                 JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD
                           ON TAFCD.FORMA_COLETA_DADOS::TEXT = A.FORMA_COLETA_DADOS::TEXT
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
          AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;