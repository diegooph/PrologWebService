BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--################################# CRIA FUNCTION DE LISTAGEM DE COLABORADOR BY UNIDADES ###############################
--######################################################################################################################
--######################################################################################################################
-- Melhora descrição das permissões.
UPDATE public.permissao SET descricao = 'APENAS A EQUIPE DO COLABORADOR' WHERE codigo = 0;
UPDATE public.permissao SET descricao = 'TODAS AS EQUIPES DA UNIDADE' WHERE codigo = 1;
UPDATE public.permissao SET descricao = 'UNIDADES E EQUIPES DE UMA REGIONAL ESPECÍFICA' WHERE codigo = 2;
UPDATE public.permissao SET descricao = 'TODAS AS UNIDADES/REGIONAIS/EQUIPES DA EMPRESA' WHERE codigo = 3;

CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_RELATORIO_LISTAGEM_COLABORADORES_BY_UNIDADE(
  F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    UNIDADE                     TEXT,
    CPF                         TEXT,
    COLABORADOR                 TEXT,
    "DATA NASCIMENTO"           TEXT,
    PIS                         TEXT,
    CARGO                       TEXT,
    SETOR                       TEXT,
    EQUIPE                      TEXT,
    "STATUS"                    TEXT,
    "DATA ADMISSÃO"             TEXT,
    "DATA DEMISSÃO"             TEXT,
    "QTD PERMISSÕES ASSOCIADAS" BIGINT,
    "MATRÍCULA AMBEV"           TEXT,
    "MATRÍCULA TRANSPORTADORA"  TEXT,
    "NÍVEL ACESSO INFORMAÇÃO"   TEXT,
    "DATA/HORA CADASTRO"        TEXT
  )
LANGUAGE PLPGSQL
AS $$

BEGIN
  RETURN QUERY
  SELECT
    U.NOME :: TEXT                                                                        AS NOME_UNIDADE,
    LPAD(CO.CPF :: TEXT, 11, '0')                                                         AS CPF_COLABORADOR,
    CO.NOME :: TEXT                                                                       AS NOME_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_NASCIMENTO, 'DD/MM/YYYY'), '-')                              AS DATA_NASCIMENTO_COLABORADOR,
    COALESCE(LPAD(CO.PIS :: TEXT, 12, '0'), '-')                                          AS PIS_COLABORADOR,
    F.NOME :: TEXT                                                                        AS NOME_CARGO,
    SE.NOME :: TEXT                                                                       AS NOME_SETOR,
    E.NOME :: TEXT                                                                        AS NOME_EQUIPE,
    F_IF(CO.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)                             AS STATUS_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_ADMISSAO, 'DD/MM/YYYY'), '-')                                AS DATA_ADMISSAO_COLABORADOR,
    COALESCE(TO_CHAR(CO.DATA_DEMISSAO, 'DD/MM/YYYY'), '-')                                AS DATA_DEMISSAO_COLABORADOR,
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    -- CONSIDERAMOS APENAS AS PERMISSÕES DE PILARES LIBERADOS PARA A UNIDADE DO COLABORADOR.
                    AND CFP.COD_PILAR_PROLOG IN (SELECT UPP.COD_PILAR
                                                 FROM UNIDADE_PILAR_PROLOG UPP
                                                 WHERE UPP.COD_UNIDADE = CO.COD_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS,
    COALESCE(CO.MATRICULA_AMBEV :: TEXT,
             '-')                                                                         AS MATRICULA_AMBEV_COLABORADOR,
    COALESCE(CO.MATRICULA_TRANS :: TEXT,
             '-')                                                                         AS MATRICULA_TRANSPORTADORA_COLABORADOR,
    PE.DESCRICAO :: TEXT                                                                  AS DESCRICAO_PERMISSAO,
    COALESCE(TO_CHAR(CO.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(CO.COD_UNIDADE),
                     'DD/MM/YYYY HH24:MI'),
             '-')                                                                         AS DATA_HORA_CADASTRO_COLABORADOR
  FROM COLABORADOR CO
    JOIN UNIDADE U
      ON CO.COD_UNIDADE = U.CODIGO
    JOIN FUNCAO F
      ON CO.COD_FUNCAO = F.CODIGO
    JOIN SETOR SE
      ON CO.COD_UNIDADE = SE.COD_UNIDADE AND CO.COD_SETOR = SE.CODIGO
    JOIN EQUIPE E
      ON CO.COD_EQUIPE = E.CODIGO
    JOIN PERMISSAO PE
      ON CO.COD_PERMISSAO = PE.CODIGO
    JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON CO.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
         AND CO.COD_UNIDADE = CFP.COD_UNIDADE
  WHERE CO.COD_UNIDADE = ANY (F_COD_UNIDADES)
  GROUP BY
    U.NOME,
    CO.CPF,
    CO.NOME,
    F.NOME,
    SE.NOME,
    E.NOME,
    CO.STATUS_ATIVO,
    CO.DATA_ADMISSAO,
    CO.DATA_DEMISSAO,
    CO.MATRICULA_AMBEV,
    CO.MATRICULA_TRANS,
    PE.DESCRICAO,
    CO.DATA_HORA_CADASTRO
  ORDER BY
    U.NOME,
    CO.NOME,
    F.NOME,
    CO.STATUS_ATIVO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- ATUALIZA O NOME DO DIAGRAMA PARA 'CARRETA SINGLE'
UPDATE VEICULO_DIAGRAMA SET NOME = 'CARRETA SINGLE', URL_IMAGEM = 'WWW.GOOGLE.COM/CARRETA-SINGLE' WHERE CODIGO = 11;

--######################################################################################################################
--######################################################################################################################

DROP FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(
F_COD_UNIDADES BIGINT [],
F_DATA_ATUAL TIMESTAMP WITHOUT TIME ZONE );

-- ADICIONA PLACA E POSIÇÃO DO PNEU NO RELATÓRIO DE VALIDADE DE DOTS.
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(
  F_COD_UNIDADES BIGINT [],
  F_DATA_ATUAL   TIMESTAMP WITHOUT TIME ZONE)
  RETURNS TABLE(
    "UNIDADE"         TEXT,
    "COD PNEU"        TEXT,
    "PLACA"           TEXT,
    "POSIÇÃO"         TEXT,
    "DOT CADASTRADO"  TEXT,
    "DOT FORMATADO"   TEXT,
    "DOT VÁLIDO"      TEXT,
    "TEMPO DE USO"    TEXT,
    "TEMPO RESTANTE"  TEXT,
    "DATA VENCIMENTO" TEXT,
    "VENCIDO"         TEXT,
    "DATA GERAÇÃO"    TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATE_FORMAT        TEXT := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
  DIA_MES_ANO_FORMAT TEXT := 'DD/MM/YYYY';
  DATA_HORA_FORMAT   TEXT := 'DD/MM/YYYY HH24:MI';
  DATE_CONVERTER     TEXT := 'YYYYWW';
  PREFIXO_ANO        TEXT := SUBSTRING(F_DATA_ATUAL::TEXT, 1, 2);
BEGIN
  RETURN QUERY

  WITH INFORMACOES_PNEU AS (
      SELECT
        P.CODIGO_CLIENTE                               AS COD_PNEU,
        P.DOT                                          AS DOT_CADASTRADO,
        -- Remove letras, characteres especiais e espaços do dot.
        -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
        TRIM(REGEXP_REPLACE(P.DOT, '[^0-9]', '', 'g')) AS DOT_LIMPO,
        P.COD_UNIDADE                                  AS COD_UNIDADE,
        U.NOME                                         AS UNIDADE,
        VP.PLACA                                       AS PLACA_APLICADO,
        PONU.NOMENCLATURA                              AS POSICAO_PNEU
      FROM PNEU P
        JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
        LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
        LEFT JOIN VEICULO V ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
        LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
          ON U.CODIGO = PONU.COD_UNIDADE AND V.COD_TIPO = PONU.COD_TIPO_VEICULO AND VP.POSICAO = PONU.POSICAO_PROLOG
      WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
  ),

      DATA_DOT AS (
        SELECT
          IP.COD_PNEU,
          -- Transforma o DOT_FORMATADO em data
          CASE WHEN (CHAR_LENGTH(IP.DOT_LIMPO) = 4)
            THEN
              TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(IP.DOT_LIMPO, 3, 4)), (SUBSTRING(IP.DOT_LIMPO, 1, 2))),
                      DATE_CONVERTER)
          ELSE NULL END AS DOT_EM_DATA
        FROM INFORMACOES_PNEU IP
    ),

      VENCIMENTO_DOT AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
          -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
          -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
          -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
          CASE WHEN DD.DOT_EM_DATA <= (F_DATA_ATUAL::DATE)
            THEN DD.DOT_EM_DATA + INTERVAL '5 DAYS 5 YEARS' ELSE NULL END AS DATA_VENCIMENTO
        FROM DATA_DOT DD
    ),

      CALCULOS AS (
        SELECT
          DD.COD_PNEU,
          -- Verifica se o dot é válido
          -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
          -- podemos considerar inválidos os que possuem vencimento = null.
          CASE WHEN VD.DATA_VENCIMENTO IS NULL THEN 'INVÁLIDO' ELSE 'VÁLIDO' END     AS DOT_VALIDO,
          -- Cálculo tempo de uso
          CASE WHEN VD.DATA_VENCIMENTO IS NULL
            THEN NULL
          ELSE
            TO_CHAR(AGE((F_DATA_ATUAL :: DATE), DD.DOT_EM_DATA), DATE_FORMAT) END    AS TEMPO_DE_USO,
          -- Cálculo dias restantes
          TO_CHAR(AGE(VD.DATA_VENCIMENTO, F_DATA_ATUAL), DATE_FORMAT)                AS TEMPO_RESTANTE,
          -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
          F_IF(((VD.DATA_VENCIMENTO::DATE) - (F_DATA_ATUAL::DATE)) < 0, TRUE, FALSE) AS VENCIDO
        FROM DATA_DOT DD
          JOIN VENCIMENTO_DOT VD ON DD.COD_PNEU = VD.COD_PNEU
    )
  SELECT
    IP.UNIDADE::TEXT,
    IP.COD_PNEU::TEXT,
    COALESCE(IP.PLACA_APLICADO::TEXT, '-'),
    COALESCE(IP.POSICAO_PNEU::TEXT, '-'),
    COALESCE(IP.DOT_CADASTRADO::TEXT, '-'),
    COALESCE(IP.DOT_LIMPO::TEXT, '-'),
    CA.DOT_VALIDO,
    COALESCE(CA.TEMPO_DE_USO, '-'),
    COALESCE(CA.TEMPO_RESTANTE, '-'),
    COALESCE(TO_CHAR(VD.DATA_VENCIMENTO, DIA_MES_ANO_FORMAT)::TEXT, '-'),
    F_IF(CA.VENCIDO, 'SIM' :: TEXT, 'NÃO' :: TEXT),
    TO_CHAR(F_DATA_ATUAL, DATA_HORA_FORMAT)::TEXT
  FROM
    INFORMACOES_PNEU IP
    JOIN VENCIMENTO_DOT VD ON IP.COD_PNEU = VD.COD_PNEU
    JOIN CALCULOS CA ON CA.COD_PNEU = VD.COD_PNEU AND CA.COD_PNEU = IP.COD_PNEU
  ORDER BY VD.DATA_VENCIMENTO ASC, IP.PLACA_APLICADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- Adiciona observações ao relatório de movimentações.
DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE);

CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"               TEXT,
    "DATA E HORA"           TEXT,
    "CPF DO RESPONSÁVEL"    TEXT,
    "NOME"                  TEXT,
    "PNEU"                  TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "MEDIDAS"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "ORIGEM"                TEXT,
    "PLACA DE ORIGEM"       TEXT,
    "POSIÇÃO DE ORIGEM"     TEXT,
    "DESTINO"               TEXT,
    "PLACA DE DESTINO"      TEXT,
    "POSIÇÃO DE DESTINO"    TEXT,
    "RECAPADORA DESTINO"    TEXT,
    "CÓDIGO COLETA"         TEXT,
    "OBS. MOVIMENTAÇÃO"     TEXT,
    "OBS. GERAL"            TEXT)
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
  COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
  COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
  COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
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

--######################################################################################################################
--######################################################################################################################
-- Adiciona data/hora de início e fim de resolução ao relatório de estratificação de O.S.
DROP VIEW ESTRATIFICACAO_OS;
CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
  SELECT
    cos.codigo                                                       AS cod_os,
    realizador.nome                                                  AS nome_realizador_checklist,
    c.placa_veiculo,
    c.km_veiculo                                                     AS km,
    timezone(tz_unidade(cos.cod_unidade), c.data_hora)               AS data_hora,
    c.tipo                                                           AS tipo_checklist,
    cp.codigo                                                        AS cod_pergunta,
    cp.ordem                                                         AS ordem_pergunta,
    cp.pergunta,
    cp.single_choice,
    NULL :: unknown                                                  AS url_imagem,
    cap.prioridade,
    CASE cap.prioridade
    WHEN 'CRITICA' :: text
      THEN 1
    WHEN 'ALTA' :: text
      THEN 2
    WHEN 'BAIXA' :: text
      THEN 3
    ELSE NULL :: integer
    END                                                              AS prioridade_ordem,
    cap.codigo                                                       AS cod_alternativa,
    cap.alternativa,
    prio.prazo,
    cr.resposta,
    v.cod_tipo,
    cos.cod_unidade,
    cos.status                                                       AS status_os,
    cos.cod_checklist,
    tz_unidade(cos.cod_unidade)                                      AS time_zone_unidade,
    cosi.status_resolucao                                            AS status_item,
    mecanico.nome                                                    AS nome_mecanico,
    cosi.cpf_mecanico,
    cosi.tempo_realizacao,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
    COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
    COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
    cosi.km                                                          AS km_fechamento,
    cosi.qt_apontamentos,
    cosi.feedback_conserto,
    cosi.codigo
  FROM (((((((((checklist c
    JOIN colaborador realizador ON ((realizador.cpf = c.cpf_colaborador)))
    JOIN veiculo v ON (((v.placa) :: text = (c.placa_veiculo) :: text)))
    JOIN checklist_ordem_servico cos ON (((c.codigo = cos.cod_checklist) AND (c.cod_unidade = cos.cod_unidade))))
    JOIN checklist_ordem_servico_itens cosi ON (((cos.codigo = cosi.cod_os) AND (cos.cod_unidade = cosi.cod_unidade))))
    JOIN checklist_perguntas cp ON ((((cp.cod_unidade = cos.cod_unidade) AND (cp.codigo = cosi.cod_pergunta)) AND
                                     (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
    JOIN checklist_alternativa_pergunta cap ON ((
      (((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND
       (cap.cod_pergunta = cp.codigo)) AND (cap.codigo = cosi.cod_alternativa))))
    JOIN checklist_alternativa_prioridade prio ON (((prio.prioridade) :: text = (cap.prioridade) :: text)))
    JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND
                                        (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND
                                       (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND
                                     (cr.cod_alternativa = cap.codigo))))
    LEFT JOIN colaborador mecanico ON ((mecanico.cpf = cosi.cpf_mecanico)));

comment on view estratificacao_os
is 'View que compila as informações das OS e seus itens';


DROP FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_STATUS_OS     TEXT,
  F_STATUS_ITEM   TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(
  F_COD_UNIDADES  BIGINT [],
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
    "DATA INÍCIO RESOLUÇÃO"        TEXT,
    "DATA FIM RESOLUÇÃO"           TEXT,
    "DATA RESOLIVDO PROLOG"        TEXT,
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
  TO_CHAR(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
  (CASE WHEN STATUS_OS = 'A'
    THEN 'ABERTA'
   ELSE 'FECHADA' END)                                                      AS STATUS_OS,
  PLACA_VEICULO                                                             AS PLACA,
  PERGUNTA                                                                  AS PERGUNTA,
  ALTERNATIVA                                                               AS ALTERNATIVA,
  PRIORIDADE                                                                AS PRIORIDADE,
  PRAZO                                                                     AS PRAZO_EM_HORAS,
  RESPOSTA                                                                  AS DESCRICAO,
  CASE WHEN STATUS_ITEM = 'P'
    THEN 'PENDENTE'
  ELSE 'RESOLVIDO' END                                                      AS STATUS_ITEM,
  COALESCE(TO_CHAR(
               DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI'),
           '-')                                                             AS DATA_INICIO_RESOLUCAO,
  COALESCE(TO_CHAR(
               DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI'),
           '-')                                                             AS DATA_FIM_RESOLUCAO,
  TO_CHAR(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_RESOLVIDO_PROLOG,
  NOME_MECANICO                                                             AS MECANICO,
  FEEDBACK_CONSERTO                                                         AS DESCRICAO_CONSERTO,
  TEMPO_REALIZACAO / 1000 /
  60                                                                        AS TEMPO_CONSERTO_MINUTOS,
  KM                                                                        AS KM_ABERTURA,
  KM_FECHAMENTO                                                             AS KM_FECHAMENTO,
  COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                               AS KM_PERCORRIDO,
  NOME_REALIZADOR_CHECKLIST                                                 AS MOTORISTA,
  CASE WHEN TIPO_CHECKLIST = 'S'
    THEN 'SAÍDA'
  ELSE 'RETORNO' END                                                        AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
  JOIN UNIDADE U
    ON EO.COD_UNIDADE = U.CODIGO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND EO.STATUS_OS LIKE F_STATUS_OS
      AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
      AND EO.DATA_HORA :: DATE >= F_DATA_INICIAL
      AND EO.DATA_HORA :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;