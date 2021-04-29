BEGIN TRANSACTION ;
--######################################################################################################################
--######################################################################################################################
--#################### CONSTRAINT PARA GARANTIR QUE TODOS OS SULCOS SERÃO NULL OU NOT NULL #############################
--######################################################################################################################
--######################################################################################################################
ALTER TABLE PNEU
  ADD CONSTRAINT TODOS_SULCOS_NULL_OU_TODOS_NOT_NULL CHECK (
  (altura_sulco_externo,
   altura_sulco_central_externo,
   altura_sulco_central_interno,
   altura_sulco_interno) IS NOT NULL
  OR
  (altura_sulco_externo,
   altura_sulco_central_externo,
   altura_sulco_central_interno,
   altura_sulco_interno) IS NULL);
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--#################### ADICIONA INFOS NO RELATÓRIO DE DADOS GERAIS DAS MOVIMENTAÇÕES ###################################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE);

CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"                 TEXT,
    "DATA E HORA"             TEXT,
    "CPF DO RESPONSÁVEL"      TEXT,
    "NOME"                    TEXT,
    "PNEU"                    TEXT,
    "MARCA"                   TEXT,
    "MODELO"                  TEXT,
    "BANDA APLICADA"          TEXT,
    "MEDIDAS"                 TEXT,
    "SULCO INTERNO"           TEXT,
    "SULCO CENTRAL INTERNO"   TEXT,
    "SULCO CENTRAL EXTERNO"   TEXT,
    "SULCO EXTERNO"           TEXT,
    "PRESSÃO ATUAL (PSI)"     TEXT,
    "VIDA ATUAL"              TEXT,
    "ORIGEM"                  TEXT,
    "PLACA DE ORIGEM"         TEXT,
    "POSIÇÃO DE ORIGEM"       TEXT,
    "DESTINO"                 TEXT,
    "PLACA DE DESTINO"        TEXT,
    "POSIÇÃO DE DESTINO"      TEXT,
    "RECAPADORA DESTINO"      TEXT,
    "CÓDIGO COLETA"           TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME,
  TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
  LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
  C.NOME,
  P.CODIGO_CLIENTE                                                                                  AS PNEU,
  MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
  MP.NOME                                                                                           AS NOME_MODELO_PNEU,
  F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME)                      AS BANDA_APLICADA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')             AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                      AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                      AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')             AS SULCO_EXTERNO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
  PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
  O.TIPO_ORIGEM                                                                                     AS ORIGEM,
  COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
  COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
  D.TIPO_DESTINO                                                                                    AS DESTINO,
  COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
  COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
  COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
  COALESCE(D.COD_COLETA, '-')                                                                       AS COD_COLETA_RECAPADORA
FROM
  MOVIMENTACAO_PROCESSO MOVP
  JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
  JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
  JOIN PNEU P ON P.CODIGO = M.COD_PNEU
  JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
  JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
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
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_ORIGEM
    ON NOMENCLATURA_ORIGEM.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_ORIGEM.COD_TIPO_VEICULO = VORIGEM.COD_TIPO
       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

  -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
  LEFT JOIN VEICULO VDESTINO
    ON D.PLACA = VDESTINO.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_DESTINO
    ON NOMENCLATURA_DESTINO.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_DESTINO.COD_TIPO_VEICULO = VDESTINO.COD_TIPO
       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;