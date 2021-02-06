DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(BIGINT[],DATE,DATE);
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                    F_DATA_INICIAL DATE,
                                                                    F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                TEXT,
                "DATA E HORA"            TEXT,
                "CPF DO RESPONSÁVEL"     TEXT,
                "NOME"                   TEXT,
                "PNEU"                   TEXT,
                "MARCA"                  TEXT,
                "MODELO"                 TEXT,
                "BANDA APLICADA"         TEXT,
                "MEDIDAS"                TEXT,
                "SULCO INTERNO"          TEXT,
                "SULCO CENTRAL INTERNO"  TEXT,
                "SULCO CENTRAL EXTERNO"  TEXT,
                "SULCO EXTERNO"          TEXT,
                "MENOR SULCO"            TEXT,
                "PRESSÃO ATUAL (PSI)"    TEXT,
                "VIDA ATUAL"             TEXT,
                "ORIGEM"                 TEXT,
                "PLACA DE ORIGEM"        TEXT,
                "POSIÇÃO DE ORIGEM"      TEXT,
                "DESTINO"                TEXT,
                "PLACA DE DESTINO"       TEXT,
                "POSIÇÃO DE DESTINO"     TEXT,
                "MOTIVO DA MOVIMENTAÇÃO" TEXT,
                "KM MOVIMENTAÇÃO"        TEXT,
                "RECAPADORA DESTINO"     TEXT,
                "CÓDIGO COLETA"          TEXT,
                "SERVIÇOS APLICADOS"     TEXT,
                "OBS. MOVIMENTAÇÃO"      TEXT,
                "OBS. GERAL"             TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME,
       TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
       LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
       C.NOME,
       P.CODIGO_CLIENTE                                                                                  AS PNEU,
       MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
       MP.NOME                                                                                           AS NOME_MODELO_PNEU,
       F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado',
            MARB.NOME || ' - ' || MODB.NOME)                                                             AS BANDA_APLICADA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                                    AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                            AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                            AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                                    AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                                             AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
       PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
       O.TIPO_ORIGEM                                                                                     AS ORIGEM,
       COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
       COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
       D.TIPO_DESTINO                                                                                    AS DESTINO,
       COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
       COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
       COALESCE(MMM.MOTIVO, '-')                                                                         AS MOTIVO_DA_MOVIMENTACAO,
       COALESCE(VORIGEM.KM, VDESTINO.KM) :: TEXT                                                         AS KM_COLETADO_MOVIMENTACAO,
       COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
       COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
       CASE
           WHEN O.TIPO_ORIGEM = 'ANALISE' AND D.TIPO_DESTINO <> 'DESCARTE'
               THEN
               (SELECT COALESCE(STRING_AGG(TRIM(PTS.NOME), ',')::TEXT, '-')
                FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO MPSR
                         JOIN PNEU_SERVICO_REALIZADO PSR ON MPSR.COD_SERVICO_REALIZADO = PSR.CODIGO
                         JOIN PNEU_TIPO_SERVICO PTS ON PSR.COD_TIPO_SERVICO = PTS.CODIGO
                WHERE MPSR.COD_MOVIMENTACAO = M.CODIGO)
           ELSE
               '-' :: TEXT
           END                                                                                           AS SERVICOS_APLICADOS,
       COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
       COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
FROM MOVIMENTACAO_PROCESSO MOVP
         JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
         JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
         JOIN PNEU P ON P.CODIGO = M.COD_PNEU
         JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
         JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL

    -- Terá recapadora apenas se foi movido para análise.
         LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
         LEFT JOIN VEICULO VORIGEM
                   ON O.PLACA = VORIGEM.PLACA
         LEFT JOIN VEICULO_TIPO VTORIGEM ON E.CODIGO = VTORIGEM.COD_EMPRESA AND VTORIGEM.CODIGO = VORIGEM.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDORIGEM ON VTORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_ORIGEM
                   ON NOMENCLATURA_ORIGEM.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_ORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
                       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

    -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
         LEFT JOIN VEICULO VDESTINO
                   ON D.PLACA = VDESTINO.PLACA
         LEFT JOIN VEICULO_TIPO VTDESTINO ON E.CODIGO = VTDESTINO.COD_EMPRESA AND VTDESTINO.CODIGO = VDESTINO.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDDESTINO ON VTDESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_DESTINO
                   ON NOMENCLATURA_DESTINO.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_DESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
                       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

    -- Joins para buscar o motivo da movimentação.
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_RESPOSTA MMMR ON MMMR.COD_MOVIMENTACAO = M.CODIGO
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMR.COD_MOTIVO_MOVIMENTO
WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC
$$;