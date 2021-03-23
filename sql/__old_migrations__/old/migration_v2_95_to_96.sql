BEGIN TRANSACTION ;

-- codigo,nome
-- 144,Rafard
-- 178,São Francisco
-- 177,Santa Helena
-- 140,Bom Retiro
-- 149,Costa Pinto

-- Colaboradores responsáveis por realizar o checklist.
-- Patio Ubr	    15599882071 	Ass De Frota Jr	  UBR	   Operação			10/11/1990 --- 28369
-- Patio Copi	    03563367000 	Ass De Frota Jr	  COPI	 Operacional	09/01/1993 --- 28371
-- Patio Rafard	  26288712035 	Ass De Frota Jr	  Rafard Operacional	05/03/1987 --- 28372
-- Patio Ush	    08600313066 	Ass De Frota Jr	  USH	   Operacional	20/04/1986 --- 28374
-- Patio Iasf	    48221423025 	Ass De Frota Jr	  IASF	 Operacional	07/05/1994 --- 28375
--
-- ALTER TABLE CHECKLIST_DATA
--   ADD COLUMN DATA_HORA_IMPORTADO_PROLOG TIMESTAMP WITH TIME ZONE;

-- Busca dos checks por dia, por tipo e por unidade
-- WITH DADOS AS (
--     SELECT
--       V.PLACA                                                                                    AS PLACA_VEICULO,
--       COUNT(C.CODIGO)
--         FILTER (WHERE C.TIPO = 'S'
--                       AND (C.DATA_HORA AT TIME ZONE 'America/Sao_Paulo') :: DATE = '2019-06-01') AS TOTAL_CHECKS_SAIDA,
--       COUNT(C.CODIGO)
--         FILTER (WHERE C.TIPO = 'R'
--                       AND (C.DATA_HORA AT TIME ZONE 'America/Sao_Paulo') :: DATE = '2019-06-01') AS TOTAL_CHECKS_RETORNO
--     FROM CHECKLIST C
--       RIGHT JOIN VEICULO V ON C.PLACA_VEICULO = V.PLACA
--       -- Veículo pode ter sido transferido após o check, por isso verificamos os dois codUnidade
--     WHERE (V.COD_UNIDADE = 144 OR C.COD_UNIDADE = 144)
--     GROUP BY V.PLACA)
--
-- SELECT
--   (SELECT V.CODIGO
--    FROM VEICULO V
--    WHERE V.PLACA = D.PLACA_VEICULO)                                        AS COD_VEICULO,
--   (SELECT V.KM
--    FROM VEICULO V
--    WHERE V.PLACA = D.PLACA_VEICULO)                                        AS KM_ATUAL_VEICULO,
--   (SELECT V.COD_TIPO
--    FROM VEICULO V
--    WHERE V.PLACA = D.PLACA_VEICULO) = ANY (ARRAY [555, 576])               AS USAR_MODELO_CHECK_CAVALO,
--   -- Só podemos inserir checks para o veículo na unidade buscada se ele ainda for da unidade.
--   (SELECT EXISTS(SELECT V.CODIGO
--                  FROM VEICULO V
--                  WHERE V.PLACA = D.PLACA_VEICULO AND V.COD_UNIDADE = 144)) AS PODE_INSERIR_CHECKS_VEICULO,
--   D.*
-- FROM DADOS D;




DROP VIEW CHECKLIST CASCADE;
CREATE OR REPLACE VIEW CHECKLIST AS
  SELECT
    C.COD_UNIDADE,
    C.COD_CHECKLIST_MODELO,
    C.CODIGO,
    C.DATA_HORA,
    C.DATA_HORA_IMPORTADO_PROLOG,
    C.CPF_COLABORADOR,
    C.PLACA_VEICULO,
    C.TIPO,
    C.TEMPO_REALIZACAO,
    C.KM_VEICULO,
    C.DATA_HORA_SINCRONIZACAO,
    C.FONTE_DATA_HORA_REALIZACAO,
    C.VERSAO_APP_MOMENTO_REALIZACAO,
    C.VERSAO_APP_MOMENTO_SINCRONIZACAO,
    C.DEVICE_ID,
    C.DEVICE_IMEI,
    C.DEVICE_UPTIME_REALIZACAO_MILLIS,
    C.DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
    C.FOI_OFFLINE
  FROM CHECKLIST_DATA C
  WHERE C.DELETADO = FALSE;


-- Não usamos a func do arquivo de tracking e nem atualizamos lá por conta das alterações já feitas na funcionalidade
-- de checks de diferentes unidades.
DROP FUNCTION func_checklist_get_all_checklists_realizados(f_cod_unidade bigint, f_cod_equipe bigint, f_cod_tipo_veiculo bigint, f_placa_veiculo character varying, f_data_inicial date, f_data_final date, f_timezone text, f_limit integer, f_offset bigint);
create function func_checklist_get_all_checklists_realizados(f_cod_unidade bigint, f_cod_equipe bigint, f_cod_tipo_veiculo bigint, f_placa_veiculo character varying, f_data_inicial date, f_data_final date, f_timezone text, f_limit integer, f_offset bigint)
  returns TABLE(
    codigo bigint,
    data_hora timestamp without time zone,
    data_hora_importado_prolog timestamp without time zone,
    cod_checklist_modelo bigint,
    km_veiculo bigint,
    tempo_realizacao bigint,
    cpf_colaborador bigint,
    placa_veiculo character varying,
    tipo character,
    nome character varying,
    total_itens_ok bigint,
    total_itens_nok bigint)
language plpgsql
as $$
DECLARE
  RESPOSTA_OK VARCHAR(2) := 'OK';
  F_HAS_EQUIPE INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 1 ELSE 0 END;
  F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 1 ELSE 0 END;
  F_HAS_PLACA_VEICULO INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO                                                     AS CODIGO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE                          AS DATA_HORA,
    C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE         AS DATA_HORA_IMPORTADO_PROLOG,
    C.COD_CHECKLIST_MODELO                                       AS COD_CHECKLIST_MODELO,
    C.KM_VEICULO                                                 AS KM_VEICULO,
    C.TEMPO_REALIZACAO                                           AS TEMPO_REALIZACAO,
    C.CPF_COLABORADOR                                            AS CPF_COLABORADOR,
    C.PLACA_VEICULO                                              AS PLACA_VEICULO,
    C.TIPO                                                       AS TIPO,
    CO.NOME                                                      AS NOME_COLABORADOR,
    (SELECT COUNT(*) - COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_OK,
    (SELECT COUNT(CASE WHEN T.NOK > 0
      THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_NOK
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
    JOIN EQUIPE E
      ON E.CODIGO = CO.COD_EQUIPE
    JOIN VEICULO V
      ON V.PLACA = C.PLACA_VEICULO
  WHERE (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL
        AND C.COD_UNIDADE = F_COD_UNIDADE
        AND (F_HAS_EQUIPE = 1 OR E.CODIGO = F_COD_EQUIPE)
        AND (F_HAS_COD_TIPO_VEICULO = 1 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
        AND (F_HAS_PLACA_VEICULO = 1 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
  ORDER BY C.DATA_HORA_SINCRONIZACAO DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;





-- Não usamos a func do arquivo de tracking e nem atualizamos lá por conta das alterações já feitas na funcionalidade
-- de checks de diferentes unidades.
DROP FUNCTION func_checklist_get_realizados_by_colaborador(f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_timezone text, f_limit integer, f_offset bigint);
create function func_checklist_get_realizados_by_colaborador(f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_timezone text, f_limit integer, f_offset bigint)
  returns TABLE(
    codigo bigint,
    data_hora timestamp without time zone,
    DATA_HORA_IMPORTADO_PROLOG timestamp without time zone,
    cod_checklist_modelo bigint,
    km_veiculo bigint,
    tempo_realizacao bigint,
    cpf_colaborador bigint,
    placa_veiculo character varying,
    tipo character,
    nome character varying,
    total_itens_ok bigint,
    total_itens_nok bigint)
language plpgsql
as $$
DECLARE
  RESPOSTA_OK        VARCHAR(2) := 'OK';
  F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
  F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE AS DATA_HORA,
    C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
    C.COD_CHECKLIST_MODELO,
    C.KM_VEICULO,
    C.TEMPO_REALIZACAO,
    C.CPF_COLABORADOR,
    C.PLACA_VEICULO,
    C.TIPO,
    CO.NOME,
    (SELECT COUNT(*) - COUNT(CASE WHEN T.NOK > 0 THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_OK,
    (SELECT COUNT(CASE WHEN T.NOK > 0 THEN 1 END) AS QTD_OK
     FROM
       (SELECT COUNT(
                   CASE
                   WHEN CR.RESPOSTA != RESPOSTA_OK -- Diferente de OK pois pode ser uma resposta outros
                     THEN 1
                   END) AS NOK
        FROM CHECKLIST_RESPOSTAS CR
        WHERE CR.COD_CHECKLIST = C.CODIGO
        GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA) AS T) AS TOTAL_ITENS_NOK
  FROM CHECKLIST C
    JOIN COLABORADOR CO
      ON CO.CPF = C.CPF_COLABORADOR
  WHERE C.CPF_COLABORADOR = F_CPF_COLABORADOR
        AND (F_HAS_DATA_INICIAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL)
        AND (F_HAS_DATA_FINAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL)
  ORDER BY C.DATA_HORA DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;






DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
F_COD_UNIDADES BIGINT [],
F_PLACA_VEICULO TEXT,
F_DATA_INICIAL DATE,
F_DATA_FINAL DATE );
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "DATA REALIZAÇÃO"             TEXT,
    "DATA IMPORTADO"              TEXT,
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
WITH CHECKLITS AS (
    SELECT
      C.CODIGO                                                                          AS COD_CHECKLIST,
      U.CODIGO                                                                          AS COD_UNIDADE,
      U.NOME                                                                            AS NOME_UNIDADE,
      C.DATA_HORA                                                                       AS DATA_HORA_REALIZACAO,
      C.DATA_HORA_SINCRONIZACAO                                                         AS DATA_HORA_SINCRONIZACAO,
      TO_CHAR(C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE), 'DD/MM/YYYY HH24:MI') AS DATA_REALIZACAO_CHECK,
      TO_CHAR(C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE),
              'DD/MM/YYYY HH24:MI')                                                     AS DATA_IMPORTADO,
      CO.NOME                                                                           AS NOME_COLABORADOR,
      C.PLACA_VEICULO                                                                   AS PLACA_VEICULO,
      C.KM_VEICULO                                                                      AS KM_VEICULO,
      C.TEMPO_REALIZACAO / 1000                                                         AS TEMPO_REALIZACAO_SEGUNDOS,
      F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                            AS TIPO_CHECKLIST,
      COUNT(C.CODIGO)                                                                   AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
      JOIN CHECKLIST_PERGUNTAS CP
        ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
      JOIN COLABORADOR CO
        ON C.CPF_COLABORADOR = CO.CPF
      JOIN UNIDADE U
        ON C.COD_UNIDADE = U.CODIGO
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY
      C.CODIGO,
      U.CODIGO,
      CO.CPF,
      CO.NOME,
      C.DATA_HORA,
      C.DATA_HORA_IMPORTADO_PROLOG,
      C.DATA_HORA_SINCRONIZACAO,
      C.COD_UNIDADE,
      C.PLACA_VEICULO,
      C.KM_VEICULO,
      C.TEMPO_REALIZACAO,
      C.TIPO),

    RESPOSTAS_NOK AS (
      SELECT
        CR.COD_CHECKLIST AS COD_CHECKLIST,
        COUNT(CASE WHEN CR.RESPOSTA <> 'OK'
          THEN 1 END)    AS TOTAL_NOK,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'BAIXA'
          THEN 1 END)    AS TOTAL_BAIXAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'ALTA'
          THEN 1 END)    AS TOTAL_ALTAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'CRITICA'
          THEN 1 END)    AS TOTAL_CRITICAS
      FROM CHECKLIST_RESPOSTAS CR
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CR.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON CR.COD_CHECKLIST = C.CODIGO
      WHERE
        CR.RESPOSTA <> 'OK'
        AND C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY CR.COD_CHECKLIST
  )

SELECT
  C.NOME_UNIDADE,
  C.DATA_REALIZACAO_CHECK,
  COALESCE(C.DATA_IMPORTADO, '-'),
  C.NOME_COLABORADOR,
  C.PLACA_VEICULO,
  C.KM_VEICULO,
  C.TEMPO_REALIZACAO_SEGUNDOS,
  C.TIPO_CHECKLIST,
  C.TOTAL_PERGUNTAS,
  COALESCE(RN.TOTAL_NOK, 0),
  COALESCE(RN.TOTAL_BAIXAS, 0),
  COALESCE(RN.TOTAL_ALTAS, 0),
  COALESCE(RN.TOTAL_CRITICAS, 0)
FROM CHECKLITS C
  LEFT JOIN RESPOSTAS_NOK RN
    ON C.COD_CHECKLIST = RN.COD_CHECKLIST
ORDER BY
  C.NOME_UNIDADE,
  C.DATA_HORA_SINCRONIZACAO DESC;
$$;





CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(
  F_COD_UNIDADES BIGINT [],
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
  TRUNC((DADOS.CHECKS_SAIDA :: FLOAT / DADOS.TOTAL_MAPAS) * 100) ||
  '%'                                                                                                  AS ADERENCIA_SAIDA,
  DADOS.CHECKS_RETORNO                                                                                 AS QTD_CHECKS_RETORNO,
  TRUNC((DADOS.CHECKS_RETORNO :: FLOAT / DADOS.TOTAL_MAPAS) * 100) ||
  '%'                                                                                                  AS ADERENCIA_RETORNO,
  DADOS.TOTAL_CHECKS                                                                                   AS TOTAL_CHECKS,
  DADOS.TOTAL_MAPAS                                                                                    AS TOTAL_MAPAS,
  TRUNC(((DADOS.CHECKS_SAIDA + DADOS.CHECKS_RETORNO) :: FLOAT / (DADOS.TOTAL_MAPAS * 2)) * 100) || '%' AS ADERENCIA_DIA
FROM (SELECT
        U.NOME                                                       AS NOME_UNIDADE,
        (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
        SUM(CASE WHEN C.TIPO = 'S'
          THEN 1
            ELSE 0 END)                                              AS CHECKS_SAIDA,
        SUM(CASE WHEN C.TIPO = 'R'
          THEN 1
            ELSE 0 END)                                              AS CHECKS_RETORNO,
        COUNT(C.DATA_HORA :: DATE)                                   AS TOTAL_CHECKS,
        DIA_MAPAS.TOTAL_MAPAS_DIA                                    AS TOTAL_MAPAS
      FROM CHECKLIST C
        JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
        LEFT JOIN (SELECT
                     M.DATA        AS DATA_MAPA,
                     COUNT(M.MAPA) AS TOTAL_MAPAS_DIA
                   FROM MAPA M
                     JOIN VEICULO V ON V.PLACA = M.PLACA
                   WHERE M.COD_UNIDADE = ANY (ARRAY [F_COD_UNIDADES])
                         AND M.DATA >= F_DATA_INICIAL
                         AND M.DATA <= F_DATA_FINAL
                   GROUP BY M.DATA
                   ORDER BY M.DATA ASC) AS DIA_MAPAS
          ON DIA_MAPAS.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, DATA, DIA_MAPAS.TOTAL_MAPAS_DIA
      ORDER BY U.NOME, (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE) AS DADOS
$$;


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

END TRANSACTION ;

