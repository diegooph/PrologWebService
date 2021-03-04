-- 2020-09-21 -> Corrige joins no relatório removendo cod_unidade (luiz_fp - PS-1247).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                F_DATA_INICIAL DATE,
                                                                F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "CÓDIGO AFERIÇÃO"           TEXT,
                "UNIDADE"                   TEXT,
                "DATA E HORA"               TEXT,
                "CPF DO RESPONSÁVEL"        TEXT,
                "NOME COLABORADOR"          TEXT,
                "PNEU"                      TEXT,
                "STATUS ATUAL"              TEXT,
                "VALOR COMPRA"              TEXT,
                "MARCA DO PNEU"             TEXT,
                "MODELO DO PNEU"            TEXT,
                "QTD SULCOS MODELO"         TEXT,
                "VIDA ATUAL"                TEXT,
                "VALOR VIDA ATUAL"          TEXT,
                "BANDA APLICADA"            TEXT,
                "QTD SULCOS BANDA"          TEXT,
                "DIMENSÃO"                  TEXT,
                "DOT"                       TEXT,
                "DATA E HORA CADASTRO"      TEXT,
                "POSIÇÃO PNEU"              TEXT,
                "PLACA"                     TEXT,
                "IDENTIFICADOR FROTA"       TEXT,
                "VIDA MOMENTO AFERIÇÃO"     TEXT,
                "KM NO MOMENTO DA AFERIÇÃO" TEXT,
                "KM ATUAL"                  TEXT,
                "MARCA DO VEÍCULO"          TEXT,
                "MODELO DO VEÍCULO"         TEXT,
                "TIPO DE MEDIÇÃO COLETADA"  TEXT,
                "TIPO DA AFERIÇÃO"          TEXT,
                "TEMPO REALIZAÇÃO (MM:SS)"  TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT,
                "FORMA DE COLETA DOS DADOS" TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO :: TEXT                                                                 AS COD_AFERICAO,
       U.NOME                                                                           AS UNIDADE,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI')                                                    AS DATA_HORA_AFERICAO,
       LPAD(C.CPF :: TEXT, 11, '0')                                                     AS CPF_COLABORADOR,
       C.NOME                                                                           AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                 AS CODIGO_CLIENTE_PNEU,
       P.STATUS                                                                         AS STATUS_ATUAL_PNEU,
       ROUND(P.VALOR :: NUMERIC, 2) :: TEXT                                             AS VALOR_COMPRA,
       MAP.NOME                                                                         AS MARCA_PNEU,
       MP.NOME                                                                          AS MODELO_PNEU,
       MP.QT_SULCOS :: TEXT                                                             AS QTD_SULCOS_MODELO,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                              AS VIDA_ATUAL,
       COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                            AS VALOR_VIDA_ATUAL,
       F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado') AS BANDA_APLICADA,
       COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                            AS QTD_SULCOS_BANDA,
       DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                  AS DIMENSAO,
       P.DOT                                                                            AS DOT,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    AS DATA_HORA_CADASTRO,
       COALESCE(PPNE.NOMENCLATURA, '-')                                                 AS POSICAO,
       COALESCE(A.PLACA_VEICULO :: TEXT, '-')                                                   AS PLACA,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                             AS IDENTIFICADOR_FROTA,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                  AS VIDA_MOMENTO_AFERICAO,
       COALESCE(A.KM_VEICULO :: TEXT, '-')                                              AS KM_MOMENTO_AFERICAO,
       COALESCE(V.KM :: TEXT, '-')                                                      AS KM_ATUAL,
       COALESCE(M2.NOME, '-')                                                           AS MARCA_VEICULO,
       COALESCE(MV.NOME, '-')                                                           AS MODELO_VEICULO,
       A.TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA,
       TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')            AS TEMPO_REALIZACAO_MINUTOS,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                  AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                          AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                          AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                  AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                           AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')            AS PRESSAO,
       COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
         JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
         JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
         JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD ON TAFCD.FORMA_COLETA_DADOS = A.FORMA_COLETA_DADOS::TEXT

         -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

         -- Se foi aferição de pneu avulso, pode não possuir placa.
         LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO

         LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT ON E.CODIGO = VT.COD_EMPRESA AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN MODELO_VEICULO MV
                   ON MV.CODIGO = V.COD_MODELO
         LEFT JOIN MARCA_VEICULO M2
                   ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;