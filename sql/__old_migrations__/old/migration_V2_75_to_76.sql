BEGIN TRANSACTION ;





-- RELATÓRIOS DE MOVIMENTAÇÕES E AFERIÇÕES




-- ########################################################################################################
-- ########################################################################################################
-- ################ FUNCTION PARA GERAR RELATÓRIO DE DADOS GERAIS DAS AFERIÇÕES ###########################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE)
  RETURNS TABLE (
  "UNIDADE" TEXT,
  "DATA E HORA" TEXT,
  "CPF DO RESPONSÁVEL" TEXT,
  "NOME COLABORADOR" TEXT,
  "CÓDIGO DA AFERIÇÃO" BIGINT,
  "PLACA" TEXT,
  "KM NO MOMENTO DA AFERIÇÃO" BIGINT,
  "KM ATUAL" BIGINT,
  "MARCA DO VEÍCULO" TEXT,
  "MODELO DO VEÍCULO" TEXT,
  "PNEU" TEXT,
  "MARCA DO PNEU" TEXT,
  "MODELO DO PNEU" TEXT,
  "DIMENSÃO" TEXT,
  "TIPO DE MEDIÇÃO COLETADA" TEXT,
  "TIPO DA AFERIÇÃO" TEXT,
  "TEMPO DE REALIZAÇÃO" BIGINT,
  "SULCO INTERNO" TEXT,
  "SULCO CENTRAL INTERNO" TEXT,
  "SULCO CENTRAL EXTERNO" TEXT,
  "SULCO EXTERNO" TEXT,
  "PRESSÃO" TEXT,
  "POSIÇÃO PNEU" TEXT
  ) AS
$FUNC$
SELECT
  U.NOME AS UNIDADE,
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT DATA_HORA,
  LPAD(C.CPF :: TEXT, 11, '0'),
  C.NOME,
  A.CODIGO AS CODIGO_AFERICAO,
  A.PLACA_VEICULO AS PLACA,
  A.KM_VEICULO AS KM_MOMENTO_AFERICAO,
  V.KM AS KM_ATUAL,
  M2.NOME AS MARCA_VEICULO,
  MV.NOME AS MODELO_VEICULO,
  P.CODIGO_CLIENTE,
  MAP.NOME AS MARCA_PNEU,
  MP.NOME AS MODELO_PNEU,
  DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO AS DIMENSAO,
  A.TIPO_MEDICAO_COLETADA,
  A.TIPO_PROCESSO_COLETA,
  (A.TEMPO_REALIZACAO) / 60000 AS TEMPO_REALIZACAO_MINUTOS,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT,
           '-'), '.', ',')                                    AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT,
           '-'), '.', ',')                                   AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT,
           '-') , '.', ',')                                   AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT,
           '-') , '.', ',')                                   AS SULCO_EXTERNO,
  REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-') , '.', ','),
  PONU.NOMENCLATURA AS POSICAO
FROM
  AFERICAO A JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
  JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
  JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
  JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO AND MV.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_VEICULO M2 ON MV.COD_MARCA = M2.CODIGO
  JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU ON U.CODIGO = PONU.COD_UNIDADE AND PONU.COD_TIPO_VEICULO = V.COD_TIPO AND AV.POSICAO = PONU.POSICAO_PROLOG
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$FUNC$ LANGUAGE SQL;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################ FUNCTION PARA GERAR RELATÓRIO DE DADOS GERAIS DAS MOVIMENTAÇÕES #######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE)
  RETURNS TABLE (
  "UNIDADE" TEXT,
  "DATA E HORA" TEXT,
  "CPF DO RESPONSÁVEL" TEXT,
  "NOME" TEXT,
  "PNEU" TEXT,
  "CÓDIGO DO PROCESSO" BIGINT,
  "CÓDIGO DA MOVIMENTAÇÃO" BIGINT,
  "ORIGEM" TEXT,
  "PLACA DE ORIGEM" TEXT,
  "DESTINO" TEXT,
  "PLACA DE DESTINO" TEXT
  ) AS
$FUNC$
SELECT
  U.NOME,
  TO_CHAR((MP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
  LPAD(MP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
  C2.NOME,
  P.CODIGO_CLIENTE,
  MP.CODIGO AS CODIGO_PROCESSO,
  M2.CODIGO AS COD_MOVIMENTACAO,
  O.TIPO_ORIGEM AS ORIGEM,
  COALESCE(O.PLACA, '-') AS PLACA_ORIGEM,
  D2.TIPO_DESTINO AS DESTINO,
  COALESCE(D2.PLACA, '-') AS PLACA_DESTINO
FROM
  MOVIMENTACAO_PROCESSO MP
  JOIN MOVIMENTACAO M2 ON MP.CODIGO = M2.COD_MOVIMENTACAO_PROCESSO AND MP.COD_UNIDADE = M2.COD_UNIDADE
  JOIN MOVIMENTACAO_DESTINO D2 ON M2.CODIGO = D2.COD_MOVIMENTACAO
  JOIN PNEU P ON P.CODIGO = M2.COD_PNEU
  JOIN MOVIMENTACAO_ORIGEM O ON M2.CODIGO = O.COD_MOVIMENTACAO
  JOIN UNIDADE U ON U.CODIGO = MP.COD_UNIDADE
  JOIN COLABORADOR C2 ON MP.CPF_RESPONSAVEL = C2.CPF
WHERE MP.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (MP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MP.DATA_HORA DESC;
$FUNC$ LANGUAGE SQL;
-- ########################################################################################################
-- ########################################################################################################







-- ALTERAÇÕES PARA PERMITIR FECHAMENTO DE MÚLTIPLAS ORDENS DE SERVIÇOS DE CHECKLIST






-- ########################################################################################################
-- ########################################################################################################
-- ################ ADICIONA COLUNA NA ESTRATIFICACAO_OS PARA FACILITAR ORDENAMENTO POR PRIORIDADE ########
-- ########################################################################################################
-- ########################################################################################################
drop view estratificacao_os;
create view estratificacao_os as
  SELECT
    os.codigo AS cod_os,
    os.cod_unidade,
    os.status AS status_os,
    os.cod_checklist,
    cp.codigo AS cod_pergunta,
    cp.ordem AS ordem_pergunta,
    cp.pergunta,
    cp.single_choice,
    NULL::unknown AS url_imagem,
    cp.prioridade,
    CASE CP.PRIORIDADE
      WHEN 'CRITICA' THEN 1
      WHEN 'ALTA' THEN 2
      WHEN 'BAIXA' THEN 3
    END AS PRIORIDADE_ORDEM,
    c.placa_veiculo,
    c.km_veiculo AS km,
    v.cod_tipo,
    cap.codigo AS cod_alternativa,
    cap.alternativa,
    cr.resposta,
    cosi.status_resolucao AS status_item,
    co.nome AS nome_mecanico,
    cosi.cpf_mecanico,
    timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(os.cod_unidade) func_get_time_zone_unidade(timezone)), c.data_hora) AS data_hora,
    ppc.prazo,
    cosi.tempo_realizacao,
    timezone(( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(os.cod_unidade) func_get_time_zone_unidade(timezone)), cosi.data_hora_conserto) AS data_hora_conserto,
    cosi.km AS km_fechamento,
    cosi.qt_apontamentos,
    cosi.feedback_conserto,
    ( SELECT func_get_time_zone_unidade.timezone
           FROM func_get_time_zone_unidade(os.cod_unidade) func_get_time_zone_unidade(timezone)) AS time_zone_unidade,
    cosi.codigo,
    colab.nome AS nome_realizador_checklist,
    c.tipo AS tipo_checklist
   FROM (((((((((checklist c
     JOIN colaborador colab ON ((colab.cpf = c.cpf_colaborador)))
     JOIN veiculo v ON (((v.placa)::text = (c.placa_veiculo)::text)))
     JOIN checklist_ordem_servico os ON (((c.codigo = os.cod_checklist) AND (c.cod_unidade = os.cod_unidade))))
     JOIN checklist_ordem_servico_itens cosi ON (((os.codigo = cosi.cod_os) AND (os.cod_unidade = cosi.cod_unidade))))
     JOIN checklist_perguntas cp ON ((((cp.cod_unidade = os.cod_unidade) AND (cp.codigo = cosi.cod_pergunta)) AND (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
     JOIN prioridade_pergunta_checklist ppc ON (((ppc.prioridade)::text = (cp.prioridade)::text)))
     JOIN checklist_alternativa_pergunta cap ON (((((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND (cap.cod_pergunta = cp.codigo)) AND (cap.codigo = cosi.cod_alternativa))))
     JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND (cr.cod_alternativa = cap.codigo))))
     LEFT JOIN colaborador co ON ((co.cpf = cosi.cpf_mecanico)));

comment on view estratificacao_os
is 'View que compila as informações das OS e seus itens';
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################ CORRIGE FUNCTION DE CONTAGEM DE ITENS DE ORDENS DE SERVICO ############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_QTD_ITENS_OS(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_ITENS_OS_ABERTOS BOOLEAN,
  F_LIMIT            INTEGER,
  F_OFFSET           INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                    TEXT,
    KM_ATUAL_VEICULO                 BIGINT,
    TOTAL_ITENS_ABERTOS              BIGINT,
    ITENS_PRIORIDADE_CRITICA_ABERTOS BIGINT,
    ITENS_PRIORIDADE_ALTA_ABERTOS    BIGINT,
    ITENS_PRIORIDADE_BAIXA_ABERTOS   BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_ITEM_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  TIPO_ITEM_PRIORIDADE_ALTA    TEXT := 'ALTA';
  TIPO_ITEM_PRIORIDADE_BAIXA   TEXT := 'BAIXA';
  STATUS_ITEM_OS               CHAR := CASE WHEN F_ITENS_OS_ABERTOS THEN 'P' ELSE 'R' END;
BEGIN
  RETURN QUERY
  SELECT
    V.PLACA::TEXT        AS PLACA_VEICULO,
    V.KM                 AS KM_ATUAL_VEICULO,
    COUNT(CP.PRIORIDADE) AS TOTAL_ITENS_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_CRITICA
      THEN 1 END)        AS ITENS_PRIORIDADE_CRITICA_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_ALTA
      THEN 1 END)        AS ITENS_PRIORIDADE_ALTA_ABERTOS,
    COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_BAIXA
      THEN 1 END)        AS ITENS_PRIORIDADE_BAIXA_ABERTOS
  FROM VEICULO V
    JOIN CHECKLIST C
      ON V.PLACA = C.PLACA_VEICULO
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS
         AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON COSI.COD_PERGUNTA = CP.CODIGO
  WHERE V.COD_UNIDADE = F_COD_UNIDADE
        AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_OS
        AND CASE WHEN F_COD_TIPO_VEICULO IS NOT NULL THEN V.COD_TIPO = F_COD_TIPO_VEICULO ELSE TRUE END
        AND CASE WHEN F_PLACA_VEICULO IS NOT NULL THEN V.PLACA = F_PLACA_VEICULO ELSE TRUE END
  GROUP BY V.PLACA
  ORDER BY
    ITENS_PRIORIDADE_CRITICA_ABERTOS DESC,
    ITENS_PRIORIDADE_ALTA_ABERTOS DESC,
    ITENS_PRIORIDADE_BAIXA_ABERTOS DESC,
    PLACA_VEICULO ASC
  LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################






-- COMPONENTES DE DASHBOARD DO CHECKLIST





-- ########################################################################################################
-- ########################################################################################################
-- ################ CRIA NOVO COMPONENTE DE GRÁFICO EM LINHA PARA MOSTRAR CHECKS REALIZADOS ###############
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO PUBLIC.DASHBOARD_COMPONENTE_TIPO (
  CODIGO,
  IDENTIFICADOR_TIPO,
  NOME,
  DESCRICAO,
  MAXIMO_BLOCOS_HORIZONTAIS,
  MAXIMO_BLOCOS_VERTICAIS,
  MINIMO_BLOCOS_HORIZONTAIS,
  MINIMO_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO)
VALUES (
  7,
  'GRAFICO_LINHAS_HORIZONTAIS',
  'Gráfico de Linhas Horizontais',
  'Todos os componentes desse tipo serão representados como um gráfico de linhas horizontais',
  3,
  2,
  1,
  1,
  NOW(),
  NOW());

INSERT INTO public.dashboard_componente (
  CODIGO,
  TITULO,
  SUBTITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS,
  LABEL_EIXO_X,
  LABEL_EIXO_Y,
  ATIVO)
VALUES (
  14,
  'Checklists realizados / dia',
  'Separado em saída e retorno nos últimos 30 dias',
  'Mostra a quantidade de checklists realizados nos últimos 30 dias, separado em saída e retorno',
  2,
  1,
  NOW(),
  NOW(),
  1,
  7,
  '/dashboards/checklists/quantidade-checklists-ultimos-30-dias',
  'Dias',
  'Quantidade de checklists',
  true);

INSERT INTO PUBLIC.DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  14,
  121,
  1);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_QTD_POR_TIPO(
  F_COD_UNIDADES                 BIGINT [],
  F_DATA_HOJE_UTC                DATE,
  F_DIAS_RETROATIVOS_PARA_BUSCAR INTEGER)
  RETURNS TABLE(
    DATA                     DATE,
    DATA_FORMATADA           TEXT,
    TOTAL_CHECKLISTS_SAIDA   BIGINT,
    TOTAL_CHECKLISTS_RETORNO BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL       DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL         DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
  CHECK_TIPO_SAIDA   CHAR := 'S';
  CHECK_TIPO_RETORNO CHAR := 'R';
BEGIN
  RETURN QUERY

  WITH DIAS AS (
      SELECT G.DAY :: DATE AS DATA
      FROM GENERATE_SERIES(DATA_INICIAL, DATA_FINAL, '1 DAY') G(DAY)
      ORDER BY DATA
  ),

  CHECKS_DIA AS (
    SELECT
      (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
      SUM(CASE WHEN C.TIPO = CHECK_TIPO_SAIDA THEN 1 ELSE 0 END) TOTAL_CHECKLISTS_SAIDA,
      SUM(CASE WHEN C.TIPO = CHECK_TIPO_RETORNO THEN 1 ELSE 0 END) TOTAL_CHECKLISTS_RETORNO
    FROM CHECKLIST C
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
    GROUP BY DATA
    ORDER BY DATA
  )

  SELECT
    D.DATA                        AS DATA,
    TO_CHAR(D.DATA, 'DD/MM') AS DATA_FORMATADA,
    CD.TOTAL_CHECKLISTS_SAIDA     AS TOTAL_CHECKLISTS_SAIDA,
    CD.TOTAL_CHECKLISTS_RETORNO   AS TOTAL_CHECKLISTS_RETORNO
  FROM DIAS D
    LEFT JOIN CHECKS_DIA CD ON D.DATA = CD.DATA
  ORDER BY D.DATA;
END;
$$;

-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################ CRIA COMPONENTE PARA BUSCAR A QTD DE ITENS DE OS POR PRIORIDADE #######################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO public.dashboard_componente (
  CODIGO,
  TITULO,
  SUBTITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS,
  ATIVO)
VALUES (
  15,
  'Total de itens de O.S. em aberto',
  'Separado por prioridade',
  'Mostra a quantidade de itens de O.S. em aberto, separado por prioridade',
  1,
  1,
  NOW(),
  NOW(),
  1,
  1,
  '/dashboards/checklists/ordens-servico/quantidade-itens-os-abertos-por-prioridade',
  true);

INSERT INTO PUBLIC.DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  15,
  121,
  1);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE(
  F_COD_UNIDADES BIGINT [],
  F_STATUS_ITENS TEXT)
  RETURNS TABLE(
    PRIORIDADE TEXT,
    QUANTIDADE BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH PRIORIDADES AS (
      SELECT UNNEST('{CRITICA, ALTA, BAIXA}' :: TEXT []) AS PRIORIDADE
  )

  SELECT
    P.PRIORIDADE      AS PRIORIDADE,
    COUNT(COSI.CODIGO) AS QUANTIDADE
  FROM CHECKLIST_PERGUNTAS CP
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON CP.CODIGO = COSI.COD_PERGUNTA
         AND COSI.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND COSI.STATUS_RESOLUCAO = F_STATUS_ITENS
    RIGHT JOIN PRIORIDADES P
      ON CP.PRIORIDADE = P.PRIORIDADE
  GROUP BY P.PRIORIDADE
  ORDER BY CASE P.PRIORIDADE
           WHEN 'CRITICA'
             THEN 1
           WHEN 'ALTA'
             THEN 2
           WHEN 'BAIXA'
             THEN 3
           END;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################ CRIA COMPONENTE PARA BUSCAR AS PLACAS COM MAIOR QTD DE ITENS CRÍTICOS ABERTOS ########
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO public.dashboard_componente (
  CODIGO,
  TITULO,
  SUBTITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS,
  ATIVO)
VALUES (
  16,
  'Placas com mais itens de O.S. em aberto',
  'As 15 placas com mais itens pendentes de conserto',
  'Mostra a quantidade de placas com a maior quantidade de itens de O.S. em aberto, dentre todas as unidades filtradas',
  1,
  1,
  NOW(),
  NOW(),
  1,
  5,
  '/dashboards/checklists/ordens-servico/placas-maior-quantidade-itens-os-abertos',
  true);

INSERT INTO PUBLIC.DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  16,
  121,
  1);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PLACAS_MAIOR_QTD_ITENS_ABERTOS(
  F_COD_UNIDADES             BIGINT [],
  F_TOTAL_PLACAS_PARA_BUSCAR INTEGER)
  RETURNS TABLE(
    NOME_UNIDADE                      VARCHAR,
    PLACA                             VARCHAR,
    QUANTIDADE_ITENS_ABERTOS          BIGINT,
    QUANTIDADE_ITENS_CRITICOS_ABERTOS BIGINT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITENS_ABERTOS CHAR := 'P';
  PRIORIDADE_CRITICA   VARCHAR := 'CRITICA';
BEGIN
  RETURN QUERY
  WITH PLACAS AS (
      SELECT
        C.PLACA_VEICULO    AS PLACA_VEICULO,
        COUNT(COSI.CODIGO) AS QUANTIDADE_ITENS_ABERTOS,
        COUNT(CASE WHEN CP.PRIORIDADE = PRIORIDADE_CRITICA THEN 1 END) AS QUANTIDADE_ITENS_CRITICOS_ABERTOS
      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON COSI.COD_OS = COS.CODIGO AND COSI.COD_UNIDADE = COS.COD_UNIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA = CP.CODIGO
        JOIN CHECKLIST C
          ON C.CODIGO = COS.COD_CHECKLIST
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND COSI.STATUS_RESOLUCAO = STATUS_ITENS_ABERTOS
      GROUP BY C.PLACA_VEICULO
      LIMIT F_TOTAL_PLACAS_PARA_BUSCAR
  )

  SELECT
    U.NOME AS NOME_UNIDADE,
    P.PLACA_VEICULO,
    P.QUANTIDADE_ITENS_ABERTOS,
    P.QUANTIDADE_ITENS_CRITICOS_ABERTOS
  FROM PLACAS P
    JOIN VEICULO V ON V.PLACA = P.PLACA_VEICULO
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
  ORDER BY P.QUANTIDADE_ITENS_ABERTOS DESC, P.PLACA_VEICULO ASC
  LIMIT F_TOTAL_PLACAS_PARA_BUSCAR;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################






-- MÚLTIPLAS UNIDADES NOS RELATÓRIOS DO CHECKLIST







DROP FUNCTION FUNC_RELATORIO_ADERENCIA_CHECKLIST_DIARIA(F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_COD_UNIDADE BIGINT );
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"            TEXT,
    "DATA"               TEXT,
    "QTD CHECKS SAÍDA"   BIGINT,
    "ADERÊNCIA SAÍDA"    TEXT,
    "QTD CHECKS RETORNO" BIGINT,
    "ADERÊNCIA RETORNO"  TEXT,
    "TOTAL DE CHECKS"    BIGINT,
    "TOTAL DE MAPAS"     BIGINT,
    "ADERÊNCIA DIA"      TEXT)
LANGUAGE SQL
AS $$
SELECT
  DADOS.NOME_UNIDADE                                                                                   AS NOME_UNIDADE,
  TO_CHAR(DADOS.DATA, 'DD/MM/YYYY')                                                                    AS DATA,
  DADOS.CHECKS_SAIDA                                                                                   AS QTD_CHECKS_SAIDA,
  TRUNC((DADOS.CHECKS_SAIDA::FLOAT / DADOS.TOTAL_MAPAS) * 100) || '%'                                  AS ADERENCIA_SAIDA,
  DADOS.CHECKS_RETORNO                                                                                 AS QTD_CHECKS_RETORNO,
  TRUNC((DADOS.CHECKS_RETORNO::FLOAT / DADOS.TOTAL_MAPAS) * 100) || '%'                                AS ADERENCIA_RETORNO,
  DADOS.TOTAL_CHECKS                                                                                   AS TOTAL_CHECKS,
  DADOS.TOTAL_MAPAS                                                                                    AS TOTAL_MAPAS,
  TRUNC(((DADOS.CHECKS_SAIDA + DADOS.CHECKS_RETORNO)::FLOAT / (DADOS.TOTAL_MAPAS * 2)) * 100) || '%'   AS ADERENCIA_DIA
FROM (SELECT
        U.NOME                                                       AS NOME_UNIDADE,
        (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE   AS DATA,
        SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)                AS CHECKS_SAIDA,
        SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)                AS CHECKS_RETORNO,
        COUNT(C.DATA_HORA::DATE)                                     AS TOTAL_CHECKS,
        DIA_MAPAS.TOTAL_MAPAS_DIA                                    AS TOTAL_MAPAS
      FROM CHECKLIST C
        JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
        LEFT JOIN (SELECT
                     M.DATA        AS DATA_MAPA,
                     COUNT(M.MAPA) AS TOTAL_MAPAS_DIA
                   FROM MAPA M
                     JOIN VEICULO V ON V.PLACA = M.PLACA
                   WHERE M.COD_UNIDADE = ANY (ARRAY[5])
                         AND M.DATA >= F_DATA_INICIAL
                         AND M.DATA <= F_DATA_FINAL
                   GROUP BY M.DATA
                   ORDER BY M.DATA ASC) AS DIA_MAPAS
          ON DIA_MAPAS.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, DATA, DIA_MAPAS.TOTAL_MAPAS_DIA
      ORDER BY U.NOME, (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE) AS DADOS
$$;


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"        TEXT,
    "DATA"           TEXT,
    "PLACA"          TEXT,
    "CHECKS SAÍDA"   BIGINT,
    "CHECKS RETORNO" BIGINT)
LANGUAGE SQL
AS $$
WITH MAPAS AS (
    SELECT
      M.DATA AS DATA_MAPA,
      M.MAPA,
      M.PLACA
    FROM MAPA M
      JOIN VEICULO V ON V.PLACA = M.PLACA
    WHERE M.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND M.DATA >= F_DATA_INICIAL
          AND M.DATA <= F_DATA_FINAL
    ORDER BY M.DATA ASC),

    CHECKS AS (SELECT
                 C.COD_UNIDADE                                                AS COD_UNIDADE,
                 (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE   AS DATA,
                 C.PLACA_VEICULO                                              AS PLACA_VEICULO,
                 SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)                AS CHECKS_SAIDA,
                 SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)                AS CHECKS_RETORNO
               FROM CHECKLIST C
                 JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                 LEFT JOIN MAPAS AS M
                   ON M.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE
                      AND M.PLACA = C.PLACA_VEICULO
               WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
                     AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
                     AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
               GROUP BY COD_UNIDADE, DATA, PLACA_VEICULO
               ORDER BY COD_UNIDADE, DATA, PLACA_VEICULO)

SELECT
  (SELECT NOME
   FROM UNIDADE U
   WHERE U.CODIGO = C.COD_UNIDADE) AS NOME_UNIDADE,
  TO_CHAR(C.DATA, 'DD/MM/YYYY') AS DATA,
  C.PLACA_VEICULO,
  C.CHECKS_SAIDA,
  C.CHECKS_RETORNO
FROM CHECKS C
ORDER BY NOME_UNIDADE, DATA
$$;


DROP FUNCTION FUNC_RELATORIO_MEDIA_TEMPO_REALIZACAO_CHECKLIST(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE);
CREATE FUNCTION FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"                              TEXT,
    "NOME"                                 TEXT,
    "FUNÇÃO"                               TEXT,
    "CHECKS SAÍDA"                         BIGINT,
    "CHECKS RETORNO"                       BIGINT,
    "TOTAL"                                BIGINT,
    "MÉDIA TEMPO DE REALIZAÇÃO (SEGUNDOS)" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                              AS NOME_UNIDADE,
  CO.NOME                                                             AS NOME,
  F.NOME                                                              AS FUNCAO,
  SUM(CASE WHEN C.TIPO = 'S' THEN 1 ELSE 0 END)                       AS CHECKS_SAIDA,
  SUM(CASE WHEN C.TIPO = 'R' THEN 1 ELSE 0 END)                       AS CHECKS_RETORNO,
  COUNT(C.TIPO)                                                       AS TOTAL_CHECKS,
  ROUND(AVG(C.TEMPO_REALIZACAO) / 1000)                               AS MEDIA_SEGUNDOS_REALIZACAO
FROM CHECKLIST C
  JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR
  JOIN FUNCAO F ON F.CODIGO = CO.COD_FUNCAO AND F.COD_EMPRESA = CO.COD_EMPRESA
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CO.CPF, F.CODIGO
ORDER BY U.NOME, CO.NOME
$$;


DROP FUNCTION FUNC_RELATORIO_CHECKLIST_RESUMO_REALIZADOS(
F_COD_UNIDADE BIGINT,
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE,
F_PLACA_VEICULO TEXT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT[],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "DATA"                        TEXT,
    "HORA"                        TEXT,
    "COLABORADOR"                 TEXT,
    "PLACA"                       TEXT,
    "KM"                          BIGINT,
    "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
    "TIPO"                        TEXT,
    "TOTAL DE PERGUNTAS"          BIGINT,
    "TOTAL NOK"                   BIGINT,
    "PRIORIDADE BAIXA"            BIGINT,
    "PRIORIDADE ALTA"             BIGINT,
    "PRIORIDADE CRÍTICA"          BIGINT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                AS NOME_UNIDADE,
  TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE, 'DD/MM/YYYY')     AS DATA_REALIZACAO_CHECK,
  TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::TIME, 'HH24:MI')        AS HORARIO_REALIZACAO_CHECK,
  CO.NOME                                                                               AS NOME_COLABORADOR,
  C.PLACA_VEICULO                                                                       AS PLACA_VEICULO,
  C.KM_VEICULO                                                                          AS KM_VEICULO,
  C.TEMPO_REALIZACAO / 1000                                                             AS TEMPO_REALIZACAO_SEGUNDOS,
  CASE WHEN C.TIPO = 'S' THEN 'Saída' ELSE 'Retorno' END                                AS TIPO_CHECKLIST,
  SOMATORIO_TOTAL_PERGUNTAS.TOTAL                                                       AS TOTAL_PERGUNTAS,
  COUNT(CHECKLIST_PERGUNTA_PRIORIDADE.COD_CHECKLIST)                                    AS TOTAL_NOK,
  SUM(CASE WHEN CHECKLIST_PERGUNTA_PRIORIDADE.PRIORIDADE = 'BAIXA' THEN 1 ELSE 0 END)   AS TOTAL_BAIXAS,
  SUM(CASE WHEN CHECKLIST_PERGUNTA_PRIORIDADE.PRIORIDADE = 'ALTA' THEN 1 ELSE 0 END)    AS TOTAL_ALTAS,
  SUM(CASE WHEN CHECKLIST_PERGUNTA_PRIORIDADE.PRIORIDADE = 'CRITICA' THEN 1 ELSE 0 END) AS TOTAL_CRITICAS
FROM CHECKLIST C
  JOIN (SELECT
          TOTAL_PERGUNTAS.COD_UNIDADE,
          TOTAL_PERGUNTAS.COD_CHECKLIST,
          COUNT(TOTAL_PERGUNTAS.COD_PERGUNTA) AS TOTAL
        FROM (SELECT DISTINCT
                CR.COD_UNIDADE,
                CR.COD_CHECKLIST,
                CR.COD_PERGUNTA
              FROM CHECKLIST_RESPOSTAS CR
              GROUP BY CR.COD_UNIDADE, CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS TOTAL_PERGUNTAS
        GROUP BY TOTAL_PERGUNTAS.COD_UNIDADE, TOTAL_PERGUNTAS.COD_CHECKLIST) AS SOMATORIO_TOTAL_PERGUNTAS
    ON SOMATORIO_TOTAL_PERGUNTAS.COD_UNIDADE = C.COD_UNIDADE
       AND SOMATORIO_TOTAL_PERGUNTAS.COD_CHECKLIST = C.CODIGO
  JOIN COLABORADOR CO ON C.CPF_COLABORADOR = CO.CPF
  JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
  LEFT JOIN (SELECT
               CR.COD_UNIDADE,
               CR.COD_CHECKLIST AS COD_CHECKLIST,
               CR.COD_PERGUNTA,
               CP.PRIORIDADE
             FROM CHECKLIST_RESPOSTAS CR
               JOIN CHECKLIST_PERGUNTAS CP
                 ON CP.COD_UNIDADE = CR.COD_UNIDADE
                    AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO
                    AND CP.CODIGO = CR.COD_PERGUNTA
             WHERE CR.RESPOSTA <> 'OK'
             GROUP BY CR.COD_UNIDADE, CR.COD_CHECKLIST, CR.COD_PERGUNTA, CP.PRIORIDADE) AS CHECKLIST_PERGUNTA_PRIORIDADE
    ON CHECKLIST_PERGUNTA_PRIORIDADE.COD_UNIDADE = C.COD_UNIDADE
       AND CHECKLIST_PERGUNTA_PRIORIDADE.COD_CHECKLIST = C.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, DATA_REALIZACAO_CHECK, HORARIO_REALIZACAO_CHECK, CO.CPF, PLACA_VEICULO,
  KM_VEICULO, TEMPO_REALIZACAO_SEGUNDOS, TIPO_CHECKLIST, TOTAL_PERGUNTAS, C.DATA_HORA
ORDER BY U.NOME, C.DATA_HORA DESC
$$;


DROP FUNCTION FUNC_RELATORIO_CHECKLIST_EXTRATO_RESPOSTAS_NOK(
F_COD_UNIDADE BIGINT,
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE,
F_PLACA_VEICULO CHARACTER VARYING);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(
  F_COD_UNIDADES  BIGINT[],
  F_PLACA_VEICULO CHARACTER VARYING,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"          TEXT,
    "CODIGO CHECKLIST" BIGINT,
    "DATA"             CHARACTER VARYING,
    "PLACA"            CHARACTER VARYING,
    "TIPO"             TEXT,
    "KM"               BIGINT,
    "NOME"             CHARACTER VARYING,
    "PERGUNTA"         CHARACTER VARYING,
    "ALTERNATIVA"      CHARACTER VARYING,
    "RESPOSTA"         CHARACTER VARYING,
    "PRIORIDADE"       CHARACTER VARYING,
    "PRAZO EM HORAS"   INTEGER)
LANGUAGE SQL
AS $$
SELECT
  U.NOME       AS NOME_UNIDADE,
  C.CODIGO,
  TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI'),
  C.PLACA_VEICULO,
  CASE WHEN C.TIPO = 'S' THEN 'Saída' ELSE 'Retorno' END,
  C.KM_VEICULO AS KM,
  CO.NOME      AS REALIZADOR,
  CP.PERGUNTA,
  CAP.ALTERNATIVA,
  CR.RESPOSTA,
  CP.PRIORIDADE,
  PPC.PRAZO
FROM CHECKLIST C
  JOIN VEICULO V
    ON V.PLACA= C.PLACA_VEICULO
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_UNIDADE = C.COD_UNIDADE
       AND CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN PRIORIDADE_PERGUNTA_CHECKLIST PPC
    ON PPC.PRIORIDADE::TEXT = CP.PRIORIDADE::TEXT
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_UNIDADE = CP.COD_UNIDADE
       AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
       AND CAP.COD_PERGUNTA = CP.CODIGO
  JOIN CHECKLIST_RESPOSTAS CR
    ON C.COD_UNIDADE = CR.COD_UNIDADE
       AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
       AND CR.COD_CHECKLIST = C.CODIGO
       AND CR.COD_PERGUNTA = CP.CODIGO
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
  JOIN COLABORADOR CO
    ON CO.CPF = C.CPF_COLABORADOR
  JOIN UNIDADE U
    ON C.COD_UNIDADE = U.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CR.RESPOSTA <> 'OK'
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA DESC, C.CODIGO ASC
$$;


--
-- FUNCTIONS DE RELATÓRIOS DE ORDENS DE SERVIÇOS
--
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE               TEXT,
    PERGUNTA              TEXT,
    ALTERNATIVA           TEXT,
    PRIORIDADE            TEXT,
    "TOTAL MARCAÇÕES NOK" BIGINT,
    "TOTAL REALIZAÇÕES"   BIGINT,
    "PROPORÇÃO"           TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                          AS NOME_UNIDADE,
  CP.PERGUNTA                                                     AS PERGUNTA,
  CAP.ALTERNATIVA                                                 AS ALTERNATIVA,
  CP.PRIORIDADE                                                   AS PRIORIDADE,
  SUM(CASE WHEN CR.RESPOSTA <> 'OK' THEN 1 ELSE 0 END)            AS TOTAL_MARCACOES_NOK,
  COUNT(CP.PERGUNTA)                                              AS TOTAL_REALIZACOES,
  TRUNC(((SUM(CASE WHEN CR.RESPOSTA <> 'OK' THEN 1 ELSE 0 END)
          / COUNT(CP.PERGUNTA)::FLOAT) * 100)::NUMERIC, 1) || '%' AS PROPORCAO
FROM CHECKLIST C
  JOIN CHECKLIST_RESPOSTAS CR
    ON C.COD_UNIDADE = CR.COD_UNIDADE
       AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_UNIDADE = C.COD_UNIDADE
       AND CP.CODIGO = CR.COD_PERGUNTA
       AND CP.COD_CHECKLIST_MODELO = CR.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_UNIDADE = CP.COD_UNIDADE
       AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
       AND CAP.COD_PERGUNTA = CP.CODIGO
       AND CAP.CODIGO = CR.COD_ALTERNATIVA
       AND CR.COD_CHECKLIST = C.CODIGO
       AND CR.COD_PERGUNTA = CP.CODIGO
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE))::DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CP.CODIGO, CAP.CODIGO, PRIORIDADE
ORDER BY U.NOME, PROPORCAO DESC
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_MEDIA_TEMPO_CONSERTO_ITEM(
  F_COD_UNIDADES BIGINT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE                                      TEXT,
    PERGUNTA                                     TEXT,
    ALTERNATIVA                                  TEXT,
    PRIORIDADE                                   TEXT,
    "PRAZO CONSERTO EM HORAS"                    INTEGER,
    "QTD APONTADOS"                              BIGINT,
    "TOTAL ITENS RESOLVIDOS"                       BIGINT,
    "QTD RESOLVIDOS DENTRO PRAZO"                BIGINT,
    "MÉDIA TEMPO CONSERTO EM HORAS / EM MINUTOS" TEXT,
    "PORCENTAGEM"                                TEXT)
LANGUAGE SQL
AS $$
SELECT
  NOME_UNIDADE                                                               AS NOME_UNIDADE,
  PERGUNTA                                                                   AS PERGUNTA,
  ALTERNATIVA                                                                AS ALTERNATIVA,
  PRIORIDADE                                                                 AS PRIORIDADE,
  PRAZO_CONSERTO_EM_HORAS                                                    AS PRAZO_CONSERTO_HORAS,
  QTD_APONTADOS                                                              AS QTD_APONTADOS,
  TOTAL_ITENS_RESOLVIDOS                                                     AS TOTAL_ITENS_RESOLVIDOS,
  QTD_RESOLVIDOS_DENTRO_PRAZO                                                AS QTD_RESOLVIDOS_DENTRO_PRAZO,
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 3600) || ' / ' ||
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 60)                                  AS MD_TEMPO_CONSERTO_HORAS_MINUTOS,
  ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS::FLOAT) * 100) || '%'   AS PORCENTAGEM
FROM
  (SELECT
     U.NOME                                       AS NOME_UNIDADE,
     EO.PERGUNTA,
     EO.ALTERNATIVA,
     EO.PRIORIDADE,
     EO.PRAZO                                     AS PRAZO_CONSERTO_EM_HORAS,
     COUNT(EO.PERGUNTA)                           AS QTD_APONTADOS,
     SUM(CASE WHEN EO.CPF_MECANICO IS NOT NULL
       THEN 1 ELSE 0 END)                         AS TOTAL_ITENS_RESOLVIDOS,
     SUM(CASE WHEN (TO_SECONDS(EO.DATA_HORA_CONSERTO - EO.DATA_HORA)) <= EO.PRAZO
       THEN 1 ELSE 0 END)                         AS QTD_RESOLVIDOS_DENTRO_PRAZO,
     TRUNC(EXTRACT(EPOCH FROM AVG(EO.DATA_HORA_CONSERTO -
                                  EO.DATA_HORA))) AS MEDIA_TEMPO_CONSERTO_SEGUNDOS
   FROM ESTRATIFICACAO_OS EO
     JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
   WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE)::DATE >= F_DATA_INICIAL
         AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE)::DATE <= F_DATA_FINAL
   GROUP BY U.CODIGO, EO.PERGUNTA, EO.ALTERNATIVA, EO.PRIORIDADE, EO.PRAZO) AS DADOS
ORDER BY DADOS.NOME_UNIDADE, ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS::FLOAT) * 100) DESC
$$;


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PRODUTIVIDADE_MECANICOS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE              TEXT,
    "MECÂNICO"           TEXT,
    CONSERTOS            BIGINT,
    HORAS                NUMERIC,
    "HORAS POR CONSERTO" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  Q.NOME_UNIDADE,
  C.NOME AS MECANICO,
  Q.CONSERTOS,
  Q.HORAS,
  Q.HORAS_POR_CONSERTO
FROM (SELECT
        U.NOME                                 AS NOME_UNIDADE,
        CPF_MECANICO                           AS CPF_MECANICO,
        COUNT(CPF_MECANICO)                    AS CONSERTOS,
        SUM(TEMPO_REALIZACAO / 3600000)        AS HORAS,
        ROUND(AVG(TEMPO_REALIZACAO / 3600000)) AS HORAS_POR_CONSERTO
      FROM ESTRATIFICACAO_OS EO
        JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
      WHERE EO.TEMPO_REALIZACAO IS NOT NULL
            AND EO.TEMPO_REALIZACAO > 0
            AND EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
            AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, EO.CPF_MECANICO) AS Q
  JOIN COLABORADOR C ON Q.CPF_MECANICO = C.CPF
ORDER BY NOME_UNIDADE, MECANICO;
$$;


DROP FUNCTION FUNC_RELATORIO_ESTRATIFICACAO_OS(
F_COD_UNIDADE BIGINT,
F_PLACA_VEICULO TEXT,
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE,
F_ZONE_ID TEXT,
F_STATUS_OS TEXT,
F_STATUS_ITEM TEXT);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(
  F_COD_UNIDADES  BIGINT[],
  F_PLACA_VEICULO TEXT,
  F_STATUS_OS     TEXT,
  F_STATUS_ITEM   TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    UNIDADE                        TEXT,
    "CÓDICO OS"                    BIGINT,
    "ABERTURA OS"                  TEXT,
    "DATA LIMITE CONSERTO"         TEXT,
    "STATUS OS"                    TEXT,
    "PLACA"                        TEXT,
    "PERGUNTA"                     TEXT,
    "ALTERNATIVA"                  TEXT,
    "PRIORIDADE"                   TEXT,
    "PRAZO EM HORAS"               INTEGER,
    "DESCRIÇÃO"                    TEXT,
    "STATUS ITEM"                  TEXT,
    "DATA CONSERTO"                TEXT,
    "MECÂNICO"                     TEXT,
    "DESCRIÇÃO CONSERTO"           TEXT,
    "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
    "KM ABERTURA"                  BIGINT,
    "KM FECHAMENTO"                BIGINT,
    "KM PERCORRIDO"                TEXT,
    "MOTORISTA"                    TEXT,
    "TIPO DO CHECKLIST"            TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                    AS NOME_UNIDADE,
  COD_OS                                                                    AS CODIGO_OS,
  TO_CHAR(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
  TO_CHAR(DATA_HORA + (PRAZO || ' HOUR')::INTERVAL, 'DD/MM/YYYY HH24:MI')   AS DATA_LIMITE_CONSERTO,
  (CASE WHEN STATUS_OS = 'A' THEN 'ABERTA' ELSE 'FECHADA' END)              AS STATUS_OS,
  PLACA_VEICULO                                                             AS PLACA,
  PERGUNTA                                                                  AS PERGUNTA,
  ALTERNATIVA                                                               AS ALTERNATIVA,
  PRIORIDADE                                                                AS PRIORIDADE,
  PRAZO                                                                     AS PRAZO_EM_HORAS,
  RESPOSTA                                                                  AS DESCRICAO,
  CASE WHEN STATUS_ITEM = 'P' THEN 'PENDENTE' ELSE 'RESOLVIDO' END          AS STATUS_ITEM,
  TO_CHAR(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_CONSERTO,
  NOME_MECANICO                                                             AS MECANICO,
  FEEDBACK_CONSERTO                                                         AS DESCRICAO_CONSERTO,
  TEMPO_REALIZACAO / 1000 / 60                                              AS TEMPO_CONSERTO_MINUTOS,
  KM                                                                        AS KM_ABERTURA,
  KM_FECHAMENTO                                                             AS KM_FECHAMENTO,
  COALESCE((KM_FECHAMENTO - KM)::TEXT, '-')                                 AS KM_PERCORRIDO,
  NOME_REALIZADOR_CHECKLIST                                                 AS MOTORISTA,
  CASE WHEN TIPO_CHECKLIST = 'S' THEN 'SAÍDA' ELSE 'RETORNO' END            AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
  JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND EO.STATUS_OS LIKE F_STATUS_OS
      AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
      AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE)::DATE >= F_DATA_INICIAL
      AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE)::DATE <= F_DATA_FINAL
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;
-- ########################################################################################################
-- ########################################################################################################





-- OUTROS






-- ########################################################################################################
-- ########################################################################################################
-- ################ REMOVE PERMISSÃO DE GSD - REALIZAR DO PROLOG ##########################################
-- ########################################################################################################
-- ########################################################################################################
DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE COD_FUNCAO_PROLOG = 20 AND COD_PILAR_PROLOG = 2;
DELETE FROM FUNCAO_PROLOG_V11 WHERE CODIGO = 20 AND COD_PILAR = 2;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################ FUNCTION PARA PERMISSÕES DE CARGOS DE UMA UNIDADE PARA OUTRA ##########################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION array_distinct(
      anyarray, -- input array
      boolean DEFAULT false -- flag to ignore nulls
) RETURNS anyarray AS $f$
      SELECT array_agg(DISTINCT x)
      FROM unnest($1) t(x)
      WHERE CASE WHEN $2 THEN x IS NOT NULL ELSE true END;
$f$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION FUNC_CARGOS_COPIA_PERMISSOES_CARGOS_ENTRE_UNIDADES(
      F_COD_UNIDADE_ORIGEM_COPIA_PERMISSOES_CARGOS BIGINT,
      F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS     BIGINT [],
  OUT AVISO_PERMISSOES_COPIADAS                    TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- REMOVE UNIDADES DUPLICADAS DO ARRAY DE DESTINO.
  F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS := ARRAY_DISTINCT(F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS);

  -- VERIFICA SE A UNIDADE DE ORIGEM NÃO ESTÁ ENTRE AS DE DESTINO.
  IF F_COD_UNIDADE_ORIGEM_COPIA_PERMISSOES_CARGOS = ANY (F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS)
  THEN RAISE EXCEPTION 'O código da unidade de origem não pode constar nas unidades de destino!';
  END IF;

  -- VERIFICA SE TODAS AS UNIDADES DE DESTINO PERTENCEM A MESMA EMPRESA.
  IF (SELECT COUNT(DISTINCT U.COD_EMPRESA)
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS)) > 1
  THEN RAISE EXCEPTION 'Só é possível copiar as permissões para unidades da mesma empresa!';
  END IF;

  -- VERIFICA SE A EMPRESA DA UNIDADE DE ORIGEM É A MESMA DAS UNIDADES DE DESTINO.
  IF (SELECT DISTINCT U.COD_EMPRESA
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS)) != (SELECT U.COD_EMPRESA
                                                                           FROM UNIDADE U
                                                                           WHERE U.CODIGO =
                                                                                 F_COD_UNIDADE_ORIGEM_COPIA_PERMISSOES_CARGOS)
  THEN RAISE EXCEPTION 'A empresa da unidade de origem precisa ser a mesma das unidades de destino!';
  END IF;

  -- DELETA TODOS AS PERMISSÕES DOS CARGOS DAS UNIDADES DE DESTINO.
  DELETE FROM CARGO_FUNCAO_PROLOG_V11 CFP
  WHERE CFP.COD_UNIDADE = ANY (F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS);

  -- COPIA AS PERMISSÕES DOS CARGOS DA UNIDADE DE ORIGEM PARA TODAS AS DE DESTINO.
  INSERT INTO CARGO_FUNCAO_PROLOG_V11
  (COD_UNIDADE,
   COD_FUNCAO_COLABORADOR,
   COD_FUNCAO_PROLOG,
   COD_PILAR_PROLOG)
    SELECT
      UNNEST(ARRAY [F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS]) AS COD_UNIDADE,
      COD_FUNCAO_COLABORADOR,
      COD_FUNCAO_PROLOG,
      COD_PILAR_PROLOG
    FROM CARGO_FUNCAO_PROLOG_V11
    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM_COPIA_PERMISSOES_CARGOS
    ORDER BY COD_UNIDADE, COD_FUNCAO_COLABORADOR;

  SELECT 'PERMISSÕES COPIADAS COM SUCESSO DA UNIDADE '
         || F_COD_UNIDADE_ORIGEM_COPIA_PERMISSOES_CARGOS
         || ' PARA A(S) UNIDADE(S) '
         || ARRAY_TO_STRING(F_COD_UNIDADES_DESTINO_PERMISSOES_CARGOS, ', ')
  INTO AVISO_PERMISSOES_COPIADAS;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;