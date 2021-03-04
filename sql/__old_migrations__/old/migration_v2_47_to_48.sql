-- Essa migração deve ser executada quando o WS versão 48 for publicado.
BEGIN TRANSACTION;

-- ########################################################################################################
-- ADICIONA O CÓDIGO DA UNIDADE À TABELA DE AFERIÇÃO
ALTER TABLE public.afericao
  ADD cod_unidade BIGINT NULL;

UPDATE public.afericao
SET cod_unidade = (SELECT DISTINCT av.cod_unidade
                   FROM afericao_valores av
                   WHERE av.cod_afericao = codigo);

-- RODAR COMANDO ABAIXO APENAS DEPOIS DE ATUALIZAR O WS
ALTER TABLE public.afericao
  ALTER COLUMN cod_unidade SET NOT NULL;
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##########################            SESSION FUNCTIONS             ####################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
DROP FUNCTION func_relatorio_checklist_extrato_respostas_nok( BIGINT, DATE, DATE, CHARACTER VARYING );
CREATE OR REPLACE FUNCTION func_relatorio_checklist_extrato_respostas_nok(f_cod_unidade   BIGINT, f_data_inicial DATE,
                                                                          f_data_final    DATE,
                                                                          f_placa_veiculo CHARACTER VARYING)
  RETURNS TABLE("CODIGO CHECKLIST" BIGINT, "DATA" CHARACTER VARYING, "PLACA" CHARACTER VARYING, "KM" BIGINT,
                "NOME"             CHARACTER VARYING, "PERGUNTA" CHARACTER VARYING, "ALTERNATIVA" CHARACTER VARYING, "RESPOSTA" CHARACTER VARYING,
                "PRIORIDADE"       CHARACTER VARYING, "PRAZO EM HORAS" INTEGER)
LANGUAGE SQL
AS $$
SELECT
  c.codigo,
  to_char((c.data_hora AT TIME ZONE (SELECT TIMEZONE
                                     FROM func_get_time_zone_unidade(f_cod_unidade))), 'DD/MM/YYYY HH24:MI'),
  c.placa_veiculo,
  c.km_veiculo AS km,
  co.nome      AS realizador,
  cp.pergunta,
  cap.alternativa,
  cr.resposta,
  cp.prioridade,
  ppc.prazo
FROM (((checklist c
  JOIN veiculo v ON (((v.placa) :: TEXT = (c.placa_veiculo) :: TEXT)))
  JOIN checklist_perguntas cp
    ON ((((cp.cod_unidade = c.cod_unidade) AND (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
  JOIN prioridade_pergunta_checklist ppc ON (((ppc.prioridade) :: TEXT = (cp.prioridade) :: TEXT)))
  JOIN checklist_alternativa_pergunta cap
    ON (((((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND
          (cap.cod_pergunta = cp.codigo))))
  JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND
                                      (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND
                                     (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND
                                   (cr.cod_alternativa = cap.codigo))))
  JOIN colaborador co ON ((co.cpf = c.cpf_colaborador))
WHERE C.cod_unidade = f_cod_unidade
      AND (c.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
      BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND cr.resposta <> 'OK'
  and c.placa_veiculo like f_placa_veiculo
ORDER BY c.data_hora DESC, c.codigo ASC
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_pneus_descartados( BIGINT, DATE, DATE );
CREATE OR REPLACE FUNCTION func_relatorio_pneus_descartados(f_cod_unidade BIGINT, f_data_inicial DATE,
                                                            f_data_final  DATE)
  RETURNS TABLE("RESPONSÁVEL PELO DESCARTE" TEXT, "DATA/HORA DO DESCARTE" TEXT, "CÓDIGO DO PNEU" TEXT, "MARCA DO PNEU" TEXT, "MODELO DO PNEU" TEXT, "MARCA DA BANDA" TEXT, "MODELO DA BANDA" TEXT, "DIMENSÃO DO PNEU" TEXT, "ÚLTIMA PRESSÃO" NUMERIC, "TOTAL DE VIDAS" INTEGER, "ALTURA SULCO INTERNO" NUMERIC, "ALTURA SULCO CENTRAL INTERNO" NUMERIC, "ALTURA SULCO CENTRAL EXTERNO" NUMERIC, "ALTURA SULCO EXTERNO" NUMERIC, "DOT" TEXT, "MOTIVO DO DESCARTE" TEXT, "FOTO 1" TEXT, "FOTO 2" TEXT, "FOTO 3" TEXT)
LANGUAGE SQL
AS $$
SELECT
  C.NOME                        AS "RESPONSÁVEL PELO DESCARTE",
  TO_CHAR(MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                                     FROM func_get_time_zone_unidade(f_cod_unidade)),
          'DD/MM/YYYY HH24:MI') AS "DATA/HORA DESCARTE",
  P.CODIGO                      AS "CÓDIGO DO PNEU",
  MAP.NOME                      AS "MARCA DO PNEU",
  MOP.NOME                      AS "MODELO DO PNEU",
  MAB.NOME                      AS "MARCA DA BANDA",
  MOB.NOME                      AS "MODELO DA BANDA",
  'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' ||
  DP.ARO                        AS "DIMENSÃO DO PNEU",
  ROUND(P.PRESSAO_ATUAL :: NUMERIC,
        2)                      AS "ÚLTIMA PRESSÃO",
  P.VIDA_ATUAL                  AS "TOTAL DE VIDAS",
  ROUND(P.ALTURA_SULCO_INTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO CENTRAL INTERNO",
  ROUND(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO CENTRAL EXTERNO",
  ROUND(P.ALTURA_SULCO_EXTERNO :: NUMERIC,
        2)                      AS "ALTURA SULCO EXTERNO",
  P.DOT                         AS "DOT",
  MMDE.MOTIVO                   AS "MOTIVO DO DESCARTE",
  MD.URL_IMAGEM_DESCARTE_1      AS "FOTO 1",
  MD.URL_IMAGEM_DESCARTE_2      AS "FOTO 2",
  MD.URL_IMAGEM_DESCARTE_3      AS "FOTO 3"
FROM PNEU P
  JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
  JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
  LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
  LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
  LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
  LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
  LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
  LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
    ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
WHERE P.COD_UNIDADE = F_COD_UNIDADE
      AND P.STATUS = 'DESCARTE'
      AND M.COD_PNEU = P.CODIGO
      AND MD.TIPO_DESTINO = 'DESCARTE'
      AND (MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
          >= (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (MP.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
          <= (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)));
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_pneu_aderencia_afericao( BIGINT, DATE, DATE );
CREATE OR REPLACE FUNCTION func_relatorio_pneu_aderencia_afericao(f_cod_unidade BIGINT, f_data_inicial DATE,
                                                                  f_data_final  DATE)
  RETURNS TABLE("PLACA" CHARACTER VARYING, "QT AFERIÇÕES DE PRESSÃO" BIGINT, "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT, "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT, "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT, "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META" BIGINT, "ADERÊNCIA AFERIÇÕES DE PRESSÃO" TEXT, "QT AFERIÇÕES DE SULCO" BIGINT, "MAX DIAS ENTRE AFERIÇÕES DE SULCO" TEXT, "MIN DIAS ENTRE AFERIÇÕES DE SULCO" TEXT, "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO" TEXT, "QTD AFERIÇÕES DE SULCO DENTRO DA META" BIGINT, "ADERÊNCIA AFERIÇÕES DE SULCO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  V.placa,
  coalesce(CALCULO_PRESSAO.qtd_afericoes, 0),
  coalesce(CALCULO_PRESSAO.max_dias_entre_afericoes, '0'),
  coalesce(CALCULO_PRESSAO.min_dias_entre_afericoes, '0'),
  coalesce(CALCULO_PRESSAO.md_dias_entre_afericoes, '0'),
  coalesce(CALCULO_PRESSAO.qtd_afericoes_dentro_meta, 0),
  coalesce(CALCULO_PRESSAO.aderencia, '0'),
  coalesce(CALCULO_SULCO.qtd_afericoes, 0),
  coalesce(CALCULO_SULCO.max_dias_entre_afericoes, '0'),
  coalesce(CALCULO_SULCO.min_dias_entre_afericoes, '0'),
  coalesce(CALCULO_SULCO.md_dias_entre_afericoes, '0'),
  coalesce(CALCULO_SULCO.qtd_afericoes_dentro_meta, 0),
  coalesce(CALCULO_SULCO.aderencia, '0')
FROM VEICULO V
  LEFT JOIN
  (SELECT
     CALCULO_AFERICAO_PRESSAO.PLACA,
     COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)                              AS qtd_afericoes,
     CASE WHEN
       MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN MAX(
           CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) :: TEXT
     ELSE '-' END                                                       AS max_dias_entre_afericoes,
     CASE WHEN
       MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN MIN(
           CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) :: TEXT
     ELSE '-' END                                                       AS min_dias_entre_afericoes,
     CASE WHEN
       MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN trunc(CASE WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
         THEN
           SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
           SUM(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
             THEN 1
               ELSE 0 END)
                  END) :: TEXT
     ELSE '-' END                                                       AS md_dias_entre_afericoes,
     sum(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
       THEN 1
         ELSE 0 END)                                                    AS qtd_afericoes_dentro_meta,
     TRUNC(sum(CASE WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
       THEN 1
               ELSE 0 END) / COUNT(
               CALCULO_AFERICAO_PRESSAO.PLACA) :: NUMERIC * 100) || '%' AS aderencia
   FROM
     (SELECT
        A.placa_veiculo            AS PLACA,
        A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                  FROM func_get_time_zone_unidade(f_cod_unidade)),
        A.tipo_afericao,
        R.periodo_afericao_pressao AS PERIODO_AFERICAO,
        CASE WHEN A.placa_veiculo = lag(A.PLACA_VEICULO)
        OVER (
          ORDER BY placa_veiculo, data_hora )
          THEN
            EXTRACT(DAYS FROM A.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                                                        FROM func_get_time_zone_unidade(f_cod_unidade))
                                                       - lag(A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                                                       FROM func_get_time_zone_unidade(
                                                                                           f_cod_unidade)))
                                                       OVER (
                                                         ORDER BY placa_veiculo, data_hora ))
        END                        AS DIAS_ENTRE_AFERICOES
      FROM afericao A
        JOIN VEICULO V ON V.placa = A.placa_veiculo
        JOIN empresa_restricao_pneu R ON R.cod_unidade = V.cod_unidade
      WHERE v.cod_unidade = f_cod_unidade AND (A.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
      BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))) AND
            (a.tipo_afericao = 'PRESSAO' OR A.tipo_afericao = 'SULCO_PRESSAO')
      ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
   GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO ON CALCULO_PRESSAO.PLACA = V.placa
  LEFT JOIN
  (SELECT
     CALCULO_AFERICAO_SULCO.PLACA,
     COUNT(CALCULO_AFERICAO_SULCO.PLACA)                              AS qtd_afericoes,
     CASE WHEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) :: TEXT
     ELSE '-' END                                                     AS max_dias_entre_afericoes,
     CASE WHEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) :: TEXT
     ELSE '-' END                                                     AS min_dias_entre_afericoes,
     CASE WHEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
       THEN trunc(CASE WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
         THEN
           SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
           SUM(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
             THEN 1
               ELSE 0 END)
                  END) :: TEXT
     ELSE '-' END                                                     AS md_dias_entre_afericoes,
     sum(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
       THEN 1
         ELSE 0 END)                                                  AS qtd_afericoes_dentro_meta,
     TRUNC(sum(CASE WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <= CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
       THEN 1
               ELSE 0 END) / COUNT(
               CALCULO_AFERICAO_SULCO.PLACA) :: NUMERIC * 100) || '%' AS aderencia
   FROM
     (SELECT
        A.placa_veiculo            AS PLACA,
        A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                  FROM func_get_time_zone_unidade(f_cod_unidade)),
        A.tipo_afericao,
        R.periodo_afericao_pressao AS PERIODO_AFERICAO,
        CASE WHEN A.placa_veiculo = lag(A.PLACA_VEICULO)
        OVER (
          ORDER BY placa_veiculo, data_hora )
          THEN
            EXTRACT(DAYS FROM A.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                                                        FROM func_get_time_zone_unidade(f_cod_unidade))
                                                       - lag(A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                                                       FROM func_get_time_zone_unidade(
                                                                                           f_cod_unidade)))
                                                       OVER (
                                                         ORDER BY placa_veiculo, data_hora ))
        ELSE 0
        END                        AS DIAS_ENTRE_AFERICOES
      FROM afericao A
        JOIN VEICULO V ON V.placa = A.placa_veiculo
        JOIN empresa_restricao_pneu R ON R.cod_unidade = V.cod_unidade
      WHERE v.cod_unidade = f_cod_unidade
            AND (A.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
                >= (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
            AND (A.data_hora::DATE AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
                <= (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
            AND (a.tipo_afericao = 'SULCO' OR A.tipo_afericao = 'SULCO_PRESSAO')
      ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
   GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO ON CALCULO_SULCO.PLACA = V.placa
WHERE V.cod_unidade = f_cod_unidade AND v.status_ativo IS TRUE
ORDER BY 1
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_pneu_extrato_servicos_fechados(BIGINT, DATE, DATE);
CREATE OR REPLACE FUNCTION func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE("DATA AFERIÇÃO" text, "DATA RESOLUÇÃO" text, "HORAS PARA RESOLVER" double precision, "MINUTOS PARA RESOLVER" double precision, "PLACA" text, "KM AFERIÇÃO" bigint, "KM CONSERTO" bigint, "KM PERCORRIDO" bigint, "COD PNEU" character varying, "PRESSÃO RECOMENDADA" real, "PRESSÃO AFERIÇÃO" text, "DISPERSÃO RECOMENDADA x AFERIÇÃO" text, "PRESSÃO INSERIDA" text, "DISPERSÃO RECOMENDADA x INSERIDA" text, "POSIÇÃO" text, "SERVIÇO" text, "MECÂNICO" text, "PROBLEMA APONTADO(INSPEÇÃO)" text)
LANGUAGE SQL
AS $$
SELECT
  to_char((A.data_hora AT TIME ZONE (SELECT TIMEZONE
                                     FROM func_get_time_zone_unidade(f_cod_unidade))),
          'DD/MM/YYYY HH24:MM:SS') AS data_hora_afericao,
  to_char((AM.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                FROM func_get_time_zone_unidade(f_cod_unidade))),
          'DD/MM/YYYY HH24:MM:SS') AS data_hora_resolucao,
  trunc(extract(EPOCH FROM ((am.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                                  FROM func_get_time_zone_unidade(f_cod_unidade)))
                            - (a.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                         FROM func_get_time_zone_unidade(f_cod_unidade))))) /
        3600)                      AS horas_resolucao,
  trunc(extract(EPOCH FROM ((am.data_hora_resolucao AT TIME ZONE (SELECT TIMEZONE
                                                                  FROM func_get_time_zone_unidade(f_cod_unidade)))
                            - (a.data_hora AT TIME ZONE (SELECT TIMEZONE
                                                         FROM func_get_time_zone_unidade(f_cod_unidade))))) /
        60)                        AS minutos_resolucao,
  A.placa_veiculo,
  A.km_veiculo                     AS KM_AFERICAO,
  AM.km_momento_conserto,
  am.km_momento_conserto -
  a.km_veiculo                     AS km_percorrido,
  AV.cod_pneu,
  P.pressao_recomendada,
  replace(round(AV.psi :: NUMERIC, 2) :: TEXT, '.',
          ',')                     AS PSI_AFERICAO,
  replace(round((((av.psi / p.pressao_recomendada) - 1) * 100) :: NUMERIC, 2) || '%', '.',
          ',')                     AS dispersao_pressao_antes,
  replace(round(AM.psi_apos_conserto :: NUMERIC, 2) :: TEXT, '.',
          ',')                     AS psi_pos_conserto,
  replace(round((((am.psi_apos_conserto / p.pressao_recomendada) - 1) * 100) :: NUMERIC, 2) || '%', '.',
          ',')                     AS dispersao_pressao_depois,
  pon.nomenclatura                 AS posicao,
  AM.tipo_servico,
  initcap(
      c.nome)                      AS nome_mecanico,
  coalesce(aa.alternativa,
           '-')                    AS problema_apontado
FROM
  AFERICAO_MANUTENCAO AM
  JOIN AFERICAO_VALORES AV ON AM.cod_unidade = AV.cod_unidade AND AM.cod_afericao = AV.cod_afericao
                              AND AM.cod_pneu = AV.cod_pneu
  JOIN AFERICAO A ON A.codigo = AV.cod_afericao
  JOIN COLABORADOR C ON am.cpf_mecanico = c.cpf
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND
                 P.cod_unidade = AV.cod_unidade
  JOIN VEICULO_PNEU VP ON vp.cod_pneu = p.codigo AND vp.cod_unidade = p.cod_unidade
  LEFT JOIN afericao_alternativa_manutencao_inspecao aa ON aa.codigo = am.cod_alternativa
  JOIN VEICULO V ON V.PLACA = VP.placa
  JOIN pneu_ordem_nomenclatura_unidade pon on pon.cod_unidade = p.cod_unidade and pon.cod_tipo_veiculo = v.cod_tipo
  and pon.posicao_prolog = av.posicao
WHERE AV.cod_unidade = f_cod_unidade and am.cpf_mecanico is not null
      and (a.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::date
          >= (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      and (a.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::date
          <= (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
ORDER BY a.data_hora desc
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_checklist_resumo_realizados( BIGINT, DATE, DATE, TEXT );
CREATE OR REPLACE FUNCTION func_relatorio_checklist_resumo_realizados(f_cod_unidade BIGINT, f_data_inicial DATE,
                                                                      f_data_final  DATE, f_placa_veiculo TEXT)
  RETURNS TABLE("DATA" TEXT, "HORA" TIME, "COLABORADOR" TEXT, "PLACA" TEXT, "KM" BIGINT, "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT, "TIPO" TEXT, "TOTAL DE PERGUNTAS" BIGINT, "TOTAL NOK" BIGINT, "PRIORIDADE BAIXA" BIGINT, "PRIORIDADE ALTA" BIGINT, "PRIORIDADE CRÍTICA" BIGINT)
LANGUAGE SQL
AS $$
SELECT
  to_char((C.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                                     FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade))) :: DATE, 'DD/MM/YYYY'),
  (C.DATA_HORA AT TIME ZONE (SELECT TIMEZONE
                             FROM FUNC_GET_TIME_ZONE_UNIDADE(CO.COD_UNIDADE))) :: TIME,
  CO.NOME,
  C.PLACA_VEICULO,
  C.km_veiculo,
  C.tempo_realizacao / 1000,
  CASE WHEN C.tipo = 'S'
    THEN 'Saída'
  ELSE 'Retorno' END,
  somatorio_total_perguntas.total,
  count(checklist_pergunta_prioridade.COD_CHECKLIST) AS total_nok,
  SUM(CASE WHEN checklist_pergunta_prioridade.prioridade = 'BAIXA'
    THEN 1
      ELSE 0 END)                                    AS TOTAL_BAIXAS,
  SUM(CASE WHEN checklist_pergunta_prioridade.prioridade = 'ALTA'
    THEN 1
      ELSE 0 END)                                    AS TOTAL_ALTAS,
  SUM(CASE WHEN checklist_pergunta_prioridade.prioridade = 'CRITICA'
    THEN 1
      ELSE 0 END)                                    AS TOTAL_CRITICAS
FROM CHECKLIST C
  JOIN COLABORADOR CO ON C.cpf_colaborador = CO.CPF
  JOIN
  (SELECT
     total_perguntas.cod_unidade,
     total_perguntas.cod_checklist,
     count(total_perguntas.cod_pergunta) AS total
   FROM
     (SELECT DISTINCT
        cr.cod_unidade,
        cr.cod_checklist,
        cr.cod_pergunta
      FROM checklist_respostas cr
      GROUP BY 1, 2, cr.cod_pergunta) AS total_perguntas
   GROUP BY 1, 2) AS somatorio_total_perguntas ON somatorio_total_perguntas.cod_unidade = c.cod_unidade AND
                                                  somatorio_total_perguntas.cod_checklist = c.codigo
  LEFT JOIN
  (SELECT
     cr.cod_unidade,
     cr.cod_checklist AS COD_CHECKLIST,
     cr.cod_pergunta,
     cp.prioridade
   FROM CHECKLIST_RESPOSTAS CR
     JOIN checklist_perguntas CP ON CP.cod_unidade = CR.cod_unidade
                                    AND CP.cod_checklist_modelo = CR.cod_checklist_modelo
                                    AND CP.codigo = CR.cod_pergunta
   WHERE cr.resposta <> 'OK'
   GROUP BY 1, 2, 3, 4) AS checklist_pergunta_prioridade
    ON checklist_pergunta_prioridade.cod_unidade = c.cod_unidade AND
       checklist_pergunta_prioridade.COD_CHECKLIST = c.codigo
WHERE c.cod_unidade = f_cod_unidade
      and c.placa_veiculo like f_placa_veiculo
      and (c.data_hora AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade)))::date
      BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade)))
      and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade)))
-- WHERE c.cod_unidade = 7 and c.placa_veiculo like '%' and c.data_hora::date BETWEEN '2017-08-22' and '2017-08-22'
GROUP BY c.data_hora, 2, 3, 4, 5, 6, 7, 8
ORDER BY c.data_hora DESC
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_extrato_relatos(date, date, bigint, text);
CREATE OR REPLACE FUNCTION func_relatorio_extrato_relatos(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_equipe text)
  RETURNS TABLE("CÓDIGO" bigint, "DATA DO ENVIO" text, "INVÁLIDO" character, "ENVIADO" character, "CLASSIFICADO" character, "FECHADO" character, "ALTERNATIVA" text, "DESCRIÇÃO" text, "COD_PDV" integer, "COLABORADOR" text, "DATA CLASSIFICAÇÃO" text, "TEMPO PARA CLASSIFICAÇÃO (DIAS)" integer, "CLASSIFICADO POR" text, "DATA FECHAMENTO" text, "TEMPO PARA FECHAMENTO (DIAS)" integer, "FECHADO POR" text, "OBS FECHAMENTO" text, "LATITUDE" text, "LONGITUDE" text, "LINK MAPS" text, "FOTO 1" text, "FOTO 2" text, "FOTO 3" text)
LANGUAGE SQL
AS $$
SELECT r.codigo as cod_relato,
  to_char(r.data_hora_database AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)), 'DD/MM/YYYY HH24:MI') AS data_envio,
  case when r.status = 'INVALIDO' THEN 'X' ELSE '' END AS invalido,
  'X'::CHAR AS enviado,
  case when r.status = 'PENDENTE_FECHAMENTO' OR r.status = 'FECHADO' THEN 'X' ELSE '' END AS classificado,
  case when r.status = 'FECHADO' THEN 'X' ELSE '' END AS fechado,
  ra.alternativa as alternativa_selecionada,
  r.resposta_outros as descricao,
  r.cod_pdv as cod_pdv,
  relator.nome as colaborador_envio,
  to_char(r.data_hora_classificacao AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)), 'DD/MM/YYYY HH24:MI') AS data_classificacao,
  extract(day from (r.data_hora_classificacao AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
                   - (r.data_hora_database AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))))::INT as dias_para_classificacao,
  classificador.nome AS colaborador_classificacao,
  to_char(r.data_hora_fechamento AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)), 'DD/MM/YYYY HH24:MI') AS data_fechamento,
  extract(DAY FROM (r.data_hora_fechamento AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
                   - (r.data_hora_database AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))))::INT AS dias_para_fechamento,
  fechamento.nome AS colaborador_fechamento,
  r.feedback_fechamento,
  r.latitude,
  r.longitude,
  'http://maps.google.com/?q=' || r.latitude || ',' || r.longitude as link_maps,
  r.url_foto_1,
  r.url_foto_2,
  r.url_foto_3
FROM relato r JOIN colaborador relator ON relator.cpf = r.cpf_colaborador
LEFT JOIN colaborador classificador ON classificador.cpf = r.cpf_classificacao
LEFT JOIN colaborador fechamento ON fechamento.cpf = r.cpf_fechamento
LEFT JOIN relato_alternativa ra ON ra.cod_unidade = r.cod_unidade AND r.cod_alternativa = ra.codigo
JOIN unidade u ON u.codigo = relator.cod_unidade
JOIN funcao f ON f.codigo = relator.cod_funcao AND f.cod_empresa = u.cod_empresa
JOIN equipe e ON e.codigo = relator.cod_equipe AND e.cod_unidade = relator.cod_unidade
WHERE r.cod_unidade = f_cod_unidade
      and ((r.data_hora_database AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
           >= (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))))
      AND ((r.data_hora_database AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::DATE
           <= (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))))
      AND (e.nome like f_equipe)
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_marcacao_ponto_realizados(bigint, date, date, text);
CREATE OR REPLACE FUNCTION func_relatorio_marcacao_ponto_realizados(f_cod_unidade bigint, f_data_inicial date, f_data_final date, f_cpf text)
  RETURNS TABLE("NOME" text, "CARGO" text, "SETOR" text, "INTERVALO" text, "INICIO INTERVALO" text, "FIM INTERVALO" text, "TEMPO DECORRIDO (MINUTOS)" text, "TEMPO RECOMENDADO (MINUTOS)" bigint, "CUMPRIU TEMPO MÍNIMO" text)
LANGUAGE SQL
AS $$
SELECT
  C.NOME AS NOME_COLABORADOR,
  F.NOME AS CARGO,
  S.nome AS SETOR,
  IT.NOME AS INTERVALO,
  COALESCE(TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)), 'DD/MM/YYYY HH24:mi:ss'), '') AS DATA_HORA_INICIO,
  COALESCE(TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)), 'DD/MM/YYYY HH24:mi:ss'), '') AS DATA_HORA_FIM,
  COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)::TEXT, '') AS TEMPO_DECORRIDO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS,
  CASE WHEN I.DATA_HORA_FIM IS NULL OR I.data_hora_inicio IS NULL
    THEN ''
  WHEN IT.TEMPO_RECOMENDADO_MINUTOS > (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
    THEN
      'NÃO'
  ELSE 'SIM' END AS                                             CUMPRIU_TEMPO_MINIMO
FROM
  INTERVALO I
  JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
  JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE AND C.cod_empresa = U.cod_empresa
  JOIN FUNCAO F ON F.cod_empresa = U.cod_empresa AND F.CODIGO = C.cod_funcao
  JOIN SETOR S ON S.cod_unidade = C.cod_unidade AND S.codigo = C.cod_setor
  WHERE I.COD_UNIDADE = f_cod_unidade
        and ((i.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))::date
             BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
             and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
  or (i.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))::date
             BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
             and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade))))
        and i.CPF_COLABORADOR::TEXT LIKE f_cpf
ORDER BY I.DATA_HORA_INICIO, C.NOME
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_media_tempo_realizacao_checklist(bigint, date, date);
CREATE OR REPLACE FUNCTION func_relatorio_media_tempo_realizacao_checklist(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE("NOME" text, "FUNÇÃO" text, "CHECKS SAÍDA" bigint, "CHECKS RETORNO" bigint, "TOTAL" bigint, "MÉDIA TEMPO DE REALIZAÇÃO (SEGUNDOS)" numeric)
LANGUAGE SQL
AS $$
SELECT co.nome as "NOME",
f.nome as "FUNÇÃO",
 sum ( case when c.tipo = 'S' then 1 else 0 end ) as "CHECKS SAÍDA",
 sum ( case when c.tipo = 'R' then 1 else 0 end ) as "CHECKS RETORNO",
 count(c.tipo) as "TOTAL CHECKS",
round(avg(c.tempo_realizacao)/1000) as "MÉDIA SEGUNDOS REALIZAÇÃO"
FROM checklist c
 JOIN colaborador co on co.cpf = c.cpf_colaborador
 JOIN funcao f on f.codigo = co.cod_funcao and f.cod_empresa = co.cod_empresa
WHERE c.cod_unidade = f_cod_unidade and (c.data_hora AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))::date
BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
 GROUP BY co.nome, f.nome
ORDER BY co.nome
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_previsao_troca(date, date, bigint, character varying);
CREATE OR REPLACE FUNCTION func_relatorio_previsao_troca(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_status_pneu character varying)
  RETURNS TABLE("COD PNEU" text, "STATUS" text, "VIDA ATUAL" integer, "MARCA" text, "MODELO" text, "MEDIDAS" text, "QTD DE AFERIÇÕES" bigint, "DTA 1a AFERIÇÃO" text, "DTA ÚLTIMA AFERIÇÃO" text, "DIAS ATIVO" integer, "MÉDIA KM POR DIA" numeric, "MAIOR MEDIÇÃO VIDA" numeric, "MENOR SULCO ATUAL" numeric, "MILIMETROS GASTOS" numeric, "KMS POR MILIMETRO" numeric, "VALOR VIDA" real, "VALOR ACUMULADO" real, "VALOR POR KM VIDA ATUAL" numeric, "VALOR POR KM ACUMULADO" numeric, "KMS A PERCORRER" numeric, "DIAS RESTANTES" double precision, "PREVISÃO DE TROCA" text, "DESTINO" text)
LANGUAGE SQL
AS $$
SELECT VAP."COD PNEU", VAP."STATUS PNEU", VAP."VIDA ATUAL", VAP."MARCA", VAP."MODELO", VAP."MEDIDAS", VAP."QTD DE AFERIÇÕES", VAP."DTA 1a AFERIÇÃO",
VAP."DTA ÚLTIMA AFERIÇÃO", VAP."DIAS ATIVO", VAP."MÉDIA KM POR DIA", VAP."MAIOR MEDIÇÃO VIDA", VAP."MENOR SULCO ATUAL",
VAP."MILIMETROS GASTOS", VAP."KMS POR MILIMETRO", VAP.valor_vida_atual, vap.valor_acumulado,VAP."VALOR POR KM", VAP."VALOR POR KM ACUMULADO" ,
VAP."KMS A PERCORRER", VAP."DIAS RESTANTES", TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'), VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL VAP
WHERE VAP.cod_unidade = f_cod_unidade
      AND VAP."PREVISÃO DE TROCA" BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND VAP."STATUS PNEU" LIKE f_status_pneu
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_consolidado_produtividade(date, date, bigint);
CREATE OR REPLACE FUNCTION func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint)
  RETURNS TABLE("MATRICULA AMBEV" integer, "COLABORADOR" text, "FUNÇÃO" text, "CXS ENTREGUES" integer, "JORNADAS BATIDAS" bigint, "RESULTADO JORNADA" text, "DEV PDV" text, "META DEV PDV" text, "RECEBE BÔNUS" text, "VALOR BÔNUS" text, "Nº FATOR 1" bigint, "Nº FATOR 2" bigint, "Nº ROTAS" bigint, "VALOR ROTA" text, "Nº RECARGAS" bigint, "VALOR RECARGA" text, "Nº ELD" bigint, "DIFERENÇA ELD" text, "Nº AS" bigint, "VALOR AS" text, "Nº MAPAS TOTAL" bigint, "VALOR TOTAL" text)
LANGUAGE SQL
AS $$
SELECT
  matricula_ambev,
  initcap(nome_colaborador) AS "COLABORADOR",
  funcao AS "FUNÇÃO",
  trunc(sum(cxentreg))::INT        AS "CXS ENTREGUES",
  sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
    then 1 else 0 end ) as qtde_jornada_batida,
  trunc((sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_horas
    then 1 else 0 end )::float / count(meta_jornada_liquida_horas))*100) || '%' as porcentagem_jornada,
  REPLACE(round( ((sum(entregasnaorealizadas + entregasparciais))::numeric / sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric)*100, 2)::TEXT, '.', ',') || '%' as "DEV PDV",
  REPLACE(round((meta_dev_pdv * 100)::numeric, 2)::TEXT, '.', ',') || '%' AS "META DEV PDV",
  CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv THEN
    'SIM' ELSE 'NÃO' END as "RECEBE BÔNUS",
  REPLACE(  (CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_motorista THEN
    PCI.bonus_motorista
             WHEN round(1 - sum(entregascompletas)/sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_ajudante THEN
               PCI.bonus_ajudante
             ELSE 0 END)::TEXT, '.', ',') as "VALOR BÔNUS",
  sum(CASE WHEN fator = 1 then 1 else 0 end) as "Nº FATOR 1",
  sum(CASE WHEN fator = 2 then 1 else 0 end) as "Nº FATOR 2",
  sum(CASE WHEN valor_rota > 0 THEN 1 else 0 END) as "Nº ROTAS",
  REPLACE('R$ ' || trunc(sum(valor_rota)::NUMERIC, 2),'.', ',') AS "VALOR ROTA",
  sum(CASE WHEN valor_recarga > 0 THEN 1 else 0 END) as "Nº RECARGAS",
  REPLACE('R$ ' || trunc(sum(valor_recarga) :: NUMERIC, 2),'.', ',') AS "VALOR RECARGA",
  sum(CASE WHEN valor_diferenca_eld > 0 THEN 1 else 0 END) as "Nº ELD",
  REPLACE('R$ ' || trunc(sum(valor_DIFERENCA_ELD) :: NUMERIC, 2), '.', ',') AS "DIFERENÇA ELD" ,
  sum(CASE WHEN valor_as > 0 THEN 1 else 0 END) as "Nº AS",
  REPLACE('R$ ' || trunc(sum(valor_AS) :: NUMERIC, 2), '.', ',') AS "VALOR AS",
  sum(CASE WHEN valor > 0 THEN 1 else 0 END) as "Nº MAPAS TOTAL",
  REPLACE('R$ ' ||trunc(((CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_motorista THEN
    PCI.bonus_motorista
                          WHEN round(1 - sum(entregascompletas)/sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_ajudante THEN
                            PCI.bonus_ajudante
                          ELSE 0 END) +
                         sum(valor)) :: NUMERIC, 2), '.', ',') AS "VALOR TOTAL"
FROM view_produtividade_extrato vpe
  LEFT JOIN pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
-- WHERE vpe.cod_unidade = 11 AND data BETWEEN '2017-06-21' AND '2017-07-20'
WHERE vpe.cod_unidade = f_cod_unidade
      AND vpe.data BETWEEN (f_dt_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_dt_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
GROUP BY matricula_ambev, nome_colaborador, vpe.cod_funcao,funcao, meta_dev_pdv, PCI.cod_cargo_ajudante, PCI.cod_cargo_motorista, PCI.bonus_ajudante, PCI.bonus_motorista
ORDER BY nome_colaborador;
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_acessos_produtividade_estratificado(bigint, date, date, text);
CREATE OR REPLACE FUNCTION func_relatorio_acessos_produtividade_estratificado(f_cod_unidade bigint, f_data_inicial date, f_data_final date, f_cpf text)
  RETURNS TABLE("NOME" text, "CARGO" text, "EQUIPE" text, "DATA DO ACESSO" text, "PERÍODO CONSULTADO" text)
LANGUAGE SQL
AS $$
SELECT C.NOME, F.NOME, E.nome, TO_CHAR(AP.data_hora_consulta, 'DD/MM/YYYY HH24:MI'), AP.mes_ano_consultado
FROM acessos_produtividade AP JOIN COLABORADOR C ON C.CPF = AP.cpf_colaborador
  JOIN EQUIPE E ON E.CODIGO = C.cod_equipe AND E.cod_unidade = C.COD_UNIDADE
  JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND F.COD_EMPRESA = C.cod_empresa
WHERE AP.cpf_colaborador :: TEXT LIKE f_cpf
      AND AP.data_hora_consulta :: DATE BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND AP.cod_unidade = f_cod_unidade
ORDER BY AP.data_hora_consulta
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_get_produtividade_consolidado_colaboradores(date, date, bigint, text, text);
CREATE OR REPLACE FUNCTION func_get_produtividade_consolidado_colaboradores(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_equipe text, f_funcao text)
  RETURNS TABLE(cpf bigint, matricula_ambev integer, nome text, data_nascimento date, funcao text, mapas bigint, caixas real, valor double precision)
LANGUAGE SQL
AS $$
SELECT cpf,
  matricula_ambev,
  nome_colaborador AS nome,
  data_nascimento,
  funcao,
  count(mapa) as mapas,
  sum(cxentreg) as caixas,
  sum(valor) as valor
FROM VIEW_PRODUTIVIDADE_EXTRATO
WHERE data between (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      and cod_unidade = f_cod_unidade
      and nome_equipe like f_equipe
      and cod_funcao::text like f_funcao
GROUP BY 1,2,3,4,5
order by funcao, valor desc, nome
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_mapa_estratificado(bigint, date, date);
CREATE OR REPLACE FUNCTION func_relatorio_mapa_estratificado(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE(data character varying, placa character varying, mapa integer, "matric motorista" integer, "nome motorista" text, "matric ajudante 1" integer, "nome ajudante 1" text, "matric ajudante 2" integer, "nome ajudante 2" text, entregas integer, cxcarreg real, cxentreg real, transp integer, entrega character varying, cargaatual text, frota text, custospot real, regiao integer, veiculo integer, veiculoindisp real, placaindisp real, frotaindisp real, tipoindisp integer, ocupacao real, cxrota real, cxas real, veicbm real, rshow integer, entrvol character varying, hrsai timestamp without time zone, hrentr timestamp without time zone, kmsai integer, kmentr integer, custovariavel real, lucro real, lucrounit real, valorfrete real, tipoimposto character varying, percimposto real, valorimposto real, valorfaturado real, valorunitcxentregue real, valorpgcxentregsemimp real, valorpgcxentregcomimp real, tempoprevistoroad time without time zone, kmprevistoroad real, valorunitpontomot real, valorunitpontoajd real, valorequipeentrmot real, valorequipeentrajd real, custovariavelcedbz real, lucrounitcedbz real, lucrovariavelcxentregueffcedbz real, tempointerno time without time zone, valordropdown real, veiccaddd character varying, kmlaco real, kmdeslocamento real, tempolaco time without time zone, tempodeslocamento time without time zone, sitmulticdd real, unborigem integer, valorctedifere character varying, qtnfcarregadas integer, qtnfentregues integer, inddevcx real, inddevnf real, fator real, recarga character varying, hrmatinal time without time zone, hrjornadaliq time without time zone, hrmetajornada time without time zone, vlbateujornmot real, vlnaobateujornmot real, vlrecargamot real, vlbateujornaju real, vlnaobateujornaju real, vlrecargaaju real, vltotalmapa real, qthlcarregados real, qthlentregues real, indicedevhl real, regiao2 character varying, qtnfcarreggeral integer, qtnfentreggeral integer, capacidadeveiculokg real, pesocargakg real, capacveiculocx integer, entregascompletas integer, entregasparciais integer, entregasnaorealizadas integer, codfilial integer, nomefilial character varying, codsupervtrs integer, nomesupervtrs character varying, codspot integer, nomespot text, equipcarregados integer, equipdevolvidos integer, equiprecolhidos integer, cxentregtracking real, hrcarreg timestamp without time zone, hrpcfisica timestamp without time zone, hrpcfinanceira timestamp without time zone, stmapa character varying, totalapontamentostracking bigint, apontamentosok bigint, apontamentosnok bigint, aderencia double precision)
LANGUAGE SQL
AS $$
SELECT
  to_char(m.data, 'DD/MM/YYYY'),
  placa,
  mapa,
  matricmotorista,
  coalesce(MOTORISTA.nome, '-') AS NOME_MOTORISTA,
  matricajud1,
  coalesce(AJUDANTE1.nome, '-') AS NOME_AJUDANTE1,
  matricajud2,
  coalesce(AJUDANTE2.nome, '-') AS NOME_AJUDANTE2,
  entregas,
  cxcarreg,
  cxentreg,
  transp,
  entrega,
  cargaatual,
  frota,
  custospot,
  regiao,
  veiculo,
  veiculoindisp,
  placaindisp,
  frotaindisp,
  tipoindisp,
  ocupacao,
  cxrota,
  cxas,
  veicbm,
  rshow,
  entrvol,
  hrsai,
  hrentr,
  kmsai,
  kmentr,
  custovariavel,
  lucro,
  lucrounit,
  valorfrete,
  tipoimposto,
  percimposto,
  valorimposto,
  valorfaturado,
  valorunitcxentregue,
  valorpgcxentregsemimp,
  valorpgcxentregcomimp,
  tempoprevistoroad,
  kmprevistoroad,
  valorunitpontomot,
  valorunitpontoajd,
  valorequipeentrmot,
  valorequipeentrajd,
  custovariavelcedbz,
  lucrounitcedbz,
  lucrovariavelcxentregueffcedbz,
  tempointerno,
  valordropdown,
  veiccaddd,
  kmlaco,
  kmdeslocamento,
  tempolaco,
  tempodeslocamento,
  sitmulticdd,
  unborigem,
  valorctedifere,
  qtnfcarregadas,
  qtnfentregues,
  inddevcx,
  inddevnf,
  fator,
  recarga,
  hrmatinal,
  hrjornadaliq,
  hrmetajornada,
  vlbateujornmot,
  vlnaobateujornmot,
  vlrecargamot,
  vlbateujornaju,
  vlnaobateujornaju,
  vlrecargaaju,
  vltotalmapa,
  qthlcarregados,
  qthlentregues,
  indicedevhl,
  regiao2,
  qtnfcarreggeral,
  qtnfentreggeral,
  capacidadeveiculokg,
  pesocargakg,
  capacveiculocx,
  entregascompletas,
  entregasparciais,
  entregasnaorealizadas,
  codfilial,
  nomefilial,
  codsupervtrs,
  nomesupervtrs,
  codspot,
  nomespot,
  equipcarregados,
  equipdevolvidos,
  equiprecolhidos,
  cxentregtracking,
  hrcarreg,
  hrpcfisica,
  hrpcfinanceira,
  stmapa,
  TRACKING.TOTAL_APONTAMENTOS,
  TRACKING.APONTAMENTOS_OK,
  TRACKING.TOTAL_APONTAMENTOS - TRACKING.APONTAMENTOS_OK                                AS apontamentos_nok,
  TRUNC((TRACKING.APONTAMENTOS_OK :: FLOAT / TRACKING.TOTAL_APONTAMENTOS) * 100)
FROM MAPA M
  JOIN unidade_funcao_produtividade UFP ON UFP.cod_unidade = M.cod_unidade
  LEFT JOIN colaborador MOTORISTA
    ON MOTORISTA.cod_unidade = M.cod_unidade AND MOTORISTA.cod_funcao = UFP.cod_funcao_motorista
       AND MOTORISTA.matricula_ambev = M.matricmotorista
  LEFT JOIN colaborador AJUDANTE1
    ON AJUDANTE1.cod_unidade = M.cod_unidade AND AJUDANTE1.cod_funcao = UFP.cod_funcao_ajudante
       AND AJUDANTE1.matricula_ambev = M.matricajud1
  LEFT JOIN colaborador AJUDANTE2
    ON AJUDANTE2.cod_unidade = M.cod_unidade AND AJUDANTE2.cod_funcao = UFP.cod_funcao_ajudante
       AND AJUDANTE2.matricula_ambev = M.matricajud2
  LEFT JOIN (SELECT
               T.MAPA                         AS TRACKING_MAPA,
               T.código_transportadora           TRACKING_UNIDADE,
               COUNT(T.disp_apont_cadastrado) AS TOTAL_APONTAMENTOS,
               SUM(CASE WHEN T.disp_apont_cadastrado <= UM.meta_raio_tracking
                 THEN 1
                   ELSE 0 END)                AS APONTAMENTOS_OK
             FROM TRACKING T
               JOIN UNIDADE_METAS UM ON UM.COD_UNIDADE = T.código_transportadora
             GROUP BY 1, 2) AS TRACKING ON TRACKING_MAPA = M.MAPA AND TRACKING_UNIDADE = M.cod_unidade
WHERE M.COD_UNIDADE = f_cod_unidade
      AND M.data BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
      AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(f_cod_unidade)))
ORDER BY M.MAPA
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_extrato_mapas_indicadores(date, date, text, text, text, bigint, text);
CREATE OR REPLACE FUNCTION func_relatorio_extrato_mapas_indicadores(f_data_inicial date, f_data_final date, f_cpf text, f_cod_unidade text, f_cod_equipe text, f_cod_empresa bigint, f_cod_regional text)
    RETURNS TABLE("DATA" text, "EQUIPE" text, "NOME" text, "FUNÇÃO" text, "MAPA" integer, "PLACA" text, "CAIXAS CARREGADAS" real, "HL CARREGADOS" real, "HL ENTREGUES" real, "HL DEVOLVIDOS" numeric, "RESULTADO DEV HL" text, "META DEV HL" text, "BATEU DEV HL" text, "NF CARREGADAS" integer, "NF ENTREGUES" integer, "NF DEVOLVIDAS" integer, "RESULTADO DEV NF" text, "META DEV NF" text, "BATEU DEV NF" text, "ENTREGAS CARREGADAS" integer, "ENTREGAS REALIZADAS" integer, "ENTREGAS DEVOLVIDAS" integer, "RESULTADO DEV PDV" text, "META DEV PDV" text, "BATEU DEV PDV" text, "KM PREVISTO" real, "KM PERCORRIDO" integer, "KM DISPERSO" numeric, "RESULTADO DISP KM" text, "META DISP KM" text, "BATEU DISP KM" text, "HORARIO MATINAL" time without time zone, "HORARIO SAÍDA" text, "TEMPO DE LARGADA" time without time zone, "META TEMPO DE LARGADA" text, "BATEU TML" text, "HR SAÍDA" text, "HR ENTR" text, "TEMPO EM ROTA" text, "META TEMPO EM ROTA" text, "BATEU TEMPO EM ROTA" text, "TEMPO INTERNO" text, "META TEMPO INTERNO" text, "BATEU TEMPO INTERNO" text, "JORNADA" text, "META JORNADA" text, "BATEU JORNADA" text, "TEMPO PREVISTO" text, "TEMPO REALIZADO" text, "DISPERSÃO" text, "META DISPERSÃO DE TEMPO" text, "BATEU DISPERSÃO DE TEMPO" text, "TOTAL ENTREGAS" bigint, "APONTAMENTOS OK" bigint, "APONTAMENTOS NOK" bigint, "ADERENCIA TRACKING" text, "META TRACKING" text, "BATEU TRACKING" text, "TOTAL DE GOLS" integer)
LANGUAGE SQL
AS $$
SELECT to_char(v.data, 'DD/MM/YYYY'),
    v.equipe,
    v.nome,
    v.funcao,
    v.mapa,
    v.placa,
    v.cxcarreg,
    --     dev HL
    v.qthlcarregados,
    v.qthlentregues,
    v.qthldevolvidos,
    trunc((v.resultado_devolucao_hectolitro * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_hl * 100)::numeric, 2) || '%',
    v.BATEU_DEV_HL,
    --     dev NF
    v.qtnfcarregadas,
    v.qtnfentregues,
    v.qtnfdevolvidas,
    trunc((v.resultado_devolucao_nf * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_nf * 100)::numeric, 2) || '%',
    v.BATEU_DEV_NF,
    --   dev PDV
    v.entregas_carregadas,
    v.entregascompletas,
    v.entregasparciais + v.entregasnaorealizadas,
    trunc((v.resultado_devolucao_pdv * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_pdv * 100)::numeric, 2) || '%',
    v.BATEU_DEV_PDV,
    --   dispersão KM
    v.kmprevistoroad,
    v.km_percorrido,
    trunc((v.km_percorrido - v.kmprevistoroad)::numeric, 2),
    trunc((v.resultado_dispersao_km * 100)::numeric, 2) || '%',
    trunc((v.meta_dispersao_km * 100)::numeric, 2) || '%',
    v.BATEU_DISPERSAO_KM,
    --     tempo de largada
    v.hrmatinal,
    v.hrsai,
    v.TEMPO_LARGADA,
    to_char((v.meta_tempo_largada_horas || ' second')::interval, 'HH24:MI:SS'),
    v.BATEU_TEMPO_LARGADA,
    --     tempo em rota
    v.hrsai,
    v.hrentr,
    v.tempo_rota,
    to_char((v.meta_tempo_rota_horas || ' second')::interval, 'HH24:MI:SS'),
    v.BATEU_TEMPO_ROTA,
    --   tempo interno
    case when v.RESULTADO_TEMPO_INTERNO_SEGUNDOS > 0 then
        to_char((v.RESULTADO_TEMPO_INTERNO_SEGUNDOS || ' second')::interval, 'HH24:MI:SS') else 0::text end,
    case when v.meta_tempo_interno_horas > 0 then
        to_char((v.meta_tempo_interno_horas || ' second')::interval, 'HH24:MI:SS') else 0::text end,
    v.BATEU_TEMPO_INTERNO,
    --     jornada
    case when (v.RESULTADO_TEMPO_INTERNO_SEGUNDOS + v.RESULTADO_tempo_largada_SEGUNDOS + v.RESULTADO_TEMPO_ROTA_SEGUNDOS) > 0 then
        to_char(((v.RESULTADO_TEMPO_INTERNO_SEGUNDOS + v.RESULTADO_tempo_largada_SEGUNDOS + v.RESULTADO_TEMPO_ROTA_SEGUNDOS) || ' second')::interval, 'HH24:MI:SS') else 0::text end,
    case when v.meta_jornada_liquida_horas > 0 then
        to_char((v.meta_jornada_liquida_horas || ' second')::interval, 'HH24:MI:SS') else 0::text end,
    v.BATEU_JORNADA,
    --     dispersao de tempo
    case when v.tempoprevistoroad > 0 then
        to_char((v.tempoprevistoroad || ' second')::interval, 'HH24:MI:SS') else 0::text end,
    v.tempo_rota,
    trunc((v.RESULTADO_DISPERSAO_TEMPO * 100)::numeric, 2) || '%',
    trunc(trunc((v.meta_dispersao_tempo)::numeric, 3) * 100, 2) || '%',
    v.BATEU_DISPERSAO_TEMPO,
    --     tracking
    v.TOTAL_TRACKING,
    v.APONTAMENTOS_OK,
    v.APONTAMENTOS_NOK,
    trunc((v.RESULTADO_TRACKING * 100)::numeric, 2) || '%',
    trunc(trunc((v.meta_tracking)::numeric, 3) * 100, 2) || '%',
    v.BATEU_TRACKING,
    (v.gol_dev_nf +
     v.gol_dev_pdv +
     v.gol_dev_hl +
     v.gol_tracking +
     v.gol_dispersao_tempo +
     v.gol_jornada +
     v.gol_tempo_interno +
     v.gol_tempo_largada +
     v.gol_tempo_rota +
     v.gol_dispersao_km) as total_gols
FROM VIEW_EXTRATO_INDICADORES V
WHERE
    v.cod_empresa = f_cod_empresa
    AND V.cod_regional::text like f_cod_regional
    AND v.cod_unidade::text like f_cod_unidade
    AND v.cod_equipe::text like f_cod_equipe
    AND v.cpf::text LIKE f_cpf
    AND v.data between (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(v.cod_unidade)))
    and (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(v.cod_unidade)))
$$;
-- ########################################################################################################

-- ########################################################################################################
DROP FUNCTION func_relatorio_consolidado_mapas_indicadores(date, date, text, text, text, bigint, text);
CREATE OR REPLACE FUNCTION func_relatorio_consolidado_mapas_indicadores(f_data_inicial date, f_data_final date, f_cpf text, f_cod_unidade text, f_cod_equipe text, f_cod_empresa bigint, f_cod_regional text)
  RETURNS TABLE("NOME" text, "EQUIPE" text, "FUNÇÃO" text, "TOTAL MAPAS REALIZADOS" bigint, "HL CARREGADOS" numeric, "HL ENTREGUES" numeric, "HL DEVOLVIDOS" numeric, "RESULTADO DEV HL" text, "META DEV HL" text, "BATEU DEV HL" text, "GOLS DEV HL" bigint, "NF CARREGADAS" bigint, "NF ENTREGUES" bigint, "NF DEVOLVIDAS" bigint, "RESULTADO DEV NF" text, "META DEV NF" text, "BATEU DEV NF" text, "GOLS DEV NF" bigint, "PDVS CARREGADOS" bigint, "PDVS ENTREGUES" bigint, "RESULTADO DEV PDV" text, "META DEV PDV" text, "BATEU DEV PDV" text, "GOLS DEV PDV" bigint, "KM PLANEJADO" numeric, "KM PERCORRIDO" bigint, "KM DISPERSO" numeric, "RESULTADO DISPERSAO KM" text, "META DISPERSAO KM" text, "BATEU DISPERSAO KM" text, "GOLS DISPERSAO KM" bigint, "MEDIA TEMPO LARGADA" text, "MAPAS VÁLIDOS TEMPO LARGADA" bigint, "MAPAS BATERAM TEMPO LARGADA" bigint, "RESULTADO TEMPO LARGADA" text, "META TEMPO LARGADA" text, "BATEU TEMPO LARGADA" text, "GOLS TEMPO LARGADA" bigint, "MEDIA TEMPO EM ROTA" text, "MAPAS VÁLIDOS TEMPO EM ROTA" bigint, "MAPAS BATERAM TEMPO EM ROTA" bigint, "RESULTADO TEMPO EM ROTA" text, "META TEMPO EM ROTA" text, "BATEU TEMPO EM ROTA" text, "GOLS TEMPO EM ROTA" bigint, "MEDIA TEMPO INTERNO" text, "MAPAS VÁLIDOS TEMPO INTERNO" bigint, "MAPAS BATERAM TEMPO INTERNO" bigint, "RESULTADO TEMPO INTERNO" text, "META TEMPO INTERNO" text, "BATEU TEMPO INTERNO" text, "GOLS TEMPO INTERNO" bigint, "MEDIA JORNADA" text, "MAPAS VÁLIDOS JORNADA" bigint, "MAPAS BATERAM JORNADA" bigint, "RESULTADO JORNADA" text, "META JORNADA" text, "BATEU JORNADA" text, "GOLS JORNADA" bigint, "MEDIA TEMPO PLANEJADO" text, "MAPAS VÁLIDOS DISPERSAO TEMPO" bigint, "MAPAS BATERAM DISP TEMPO" bigint, "RESULTADO DISP TEMPO" text, "META DISP TEMPO" text, "BATEU DISP TEMPO" text, "GOLS DISP TEMPO" bigint, "TOTAL TRACKING" numeric, "TOTAL OK" numeric, "TOTAL NOK" numeric, "RESULTADO TRACKING" text, "META TRACKING" text, "BATEU TRACKING" text, "GOLS TRACKING" bigint, "TOTAL DE GOLS GERAL" bigint)
LANGUAGE SQL
AS $$
select
  dados.nome,
  dados.equipe,
  dados.funcao,
  dados.total_mapas,
  trunc(dados.hl_carregados_total::numeric, 2) as hl_carregados_total,
  trunc((dados.hl_carregados_total - dados.hl_devolvidos_total)::numeric, 2) as hl_entregues_total,
  trunc(dados.hl_devolvidos_total::numeric, 2) as hl_devolvidos_total,
  case when dados.hl_carregados_total > 0 then
    trunc(((dados.hl_devolvidos_total / dados.hl_carregados_total) * 100)::numeric, 2) else 0 end || '%' as resultado_dev_hl,
  trunc((dados.meta_dev_hl * 100)::numeric, 2) || '%' as meta_dev_hl,
  case when dados.hl_carregados_total > 0 and ((dados.hl_devolvidos_total / dados.hl_carregados_total) <= dados.meta_dev_hl) then 'SIM' else 'NÃO' end as bateu_dev_hl,
  dados.gols_dev_hl,
  dados.nf_carregadas_total,
  dados.nf_carregadas_total - dados.nf_devolvidas_total as nf_entregues_total,
  dados.nf_devolvidas_total,
  case when dados.nf_carregadas_total > 0 then
    trunc(((dados.nf_devolvidas_total::float / dados.nf_carregadas_total)*100)::numeric, 2) else 0 end || '%' as resultado_dev_nf,
  trunc((dados.meta_dev_nf * 100)::numeric, 2) || '%' as meta_dev_nf,
  case when dados.nf_carregadas_total > 0 and  ((dados.nf_devolvidas_total / dados.nf_carregadas_total) <= dados.meta_dev_nf) then 'SIM' else 'NÃO' end,
  dados.gols_dev_nf,
  dados.pdv_carregados_total,
  dados.pdv_carregados_total - dados.pdv_devolvidos_total as pdv_entregues,
  case when dados.pdv_carregados_total > 0 then
    trunc(((dados.pdv_devolvidos_total / dados.pdv_carregados_total::float)*100)::numeric, 2) else 0 end || '%' as resultado_dev_pdv,
  trunc((dados.meta_dev_pdv * 100)::numeric, 2) || '%' as meta_dev_pdv,
  case when dados.pdv_carregados_total > 0  and ((dados.pdv_devolvidos_total / dados.pdv_carregados_total) <= dados.meta_dev_pdv) then 'SIM' else 'NÃO' end as bateu_dev_pdv,
  dados.gols_dev_pdv,
  trunc(dados.km_planejado_total::numeric, 2),
  dados.km_percorrido_total,
  trunc((dados.km_percorrido_total - dados.km_planejado_total)::numeric, 2) as km_disperso,
  trunc(((CASE WHEN dados.km_planejado_total > 0
    THEN (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
          ELSE 0 END) * 100)::numeric, 2) || '% ' AS resultado_disp_km,
  trunc((dados.meta_dispersao_km * 100)::numeric, 2) || '%' as meta_disp_km,
  case when (CASE WHEN dados.km_planejado_total > 0
    THEN (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
             ELSE 0 END) <= dados.meta_dev_pdv then 'SIM' else 'NÃO' end as bateu_disp_km,
  dados.gols_dispersao_km,
  dados.media_tempo_largada,
  dados.total_mapas_validos_tempo_largada,
  dados.total_mapas_bateu_tempo_largada,
  case when dados.total_mapas_validos_tempo_largada > 0 then
    trunc(((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) *100)::numeric, 2) else 0 end || '%' as resultado_tempo_largada,
  trunc((dados.meta_tempo_largada_mapas * 100)::numeric, 2) || '%' as meta_tempo_largada,
  case when dados.total_mapas_validos_tempo_largada > 0 and ((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) >= dados.meta_tempo_largada_mapas)
    then 'SIM' else 'NÃO' end as bateu_tempo_largada,
  dados.gols_tempo_largada,
  dados.media_tempo_rota,
  dados.total_mapas,
  dados.total_mapas_bateu_tempo_rota,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_tempo_rota,
  trunc((dados.meta_tempo_rota_mapas * 100)::numeric, 2) || '%' as meta_tempo_rota,
  case when dados.total_mapas > 0 and  ((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float) >= dados.meta_tempo_rota_mapas) then 'SIM' else 'NÃO' end as bateu_tempo_rota,
  dados.gols_tempo_ROTA,
  dados.media_tempo_interno,
  dados.total_mapas_validos_tempo_interno,
  dados.total_mapas_bateu_tempo_interno,
  case when dados.total_mapas_validos_tempo_interno > 0 then
    trunc(((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float)*100)::numeric, 2) else 0 end || '%' as resultado_tempo_interno,
  trunc((dados.meta_tempo_interno_mapas * 100)::numeric, 2) || '%' as meta_tempo_interno_mapas,
  case when dados.total_mapas_validos_tempo_interno > 0 and ((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float) >= dados.meta_tempo_interno_mapas) then 'SIM' else 'NÃO' end as bateu_tempo_interno,
  dados.gols_tempo_interno,
  dados.media_jornada,
  dados.total_mapas,
  dados.total_mapas_bateu_jornada,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateu_jornada / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_jornada,
  trunc((dados.meta_jornada_liquida_mapas * 100)::numeric, 2) || '%' as meta_jornada_liquida_mapas,
  case when dados.total_mapas > 0 and ((dados.total_mapas_bateu_jornada / dados.total_mapas::float) >= dados.meta_jornada_liquida_mapas) then 'SIM' else 'NÃO' end as bateu_jornada,
  dados.gols_jornada,
  dados.media_tempo_planejado,
  dados.total_mapas,
  dados.total_mapas_bateram_dispersao_tempo,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_dispersao_tempo,
  trunc((dados.meta_dispersao_tempo * 100)::numeric, 2) || '%' as meta_dispersao_tempo,
  case when dados.total_mapas > 0 and ((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float) >= dados.meta_dispersao_tempo) then 'SIM' ELSE 'NÃO' END as bateu_dispersao_tempo,
  dados.gols_dispersao_tempo,
  dados.total_apontamentos,
  dados.total_apontamentos_ok,
  dados.total_apontamentos - dados.total_apontamentos_ok as total_apontamentos_nok,
  case when dados.total_apontamentos > 0 then
    trunc(((dados.total_apontamentos_ok / dados.total_apontamentos::float) * 100)::numeric, 2) else 0 end || '%' as resultado_tracking,
  trunc(trunc((dados.meta_tracking)::numeric, 3) * 100, 2) || '%' as meta_tracking,
  case when dados.total_apontamentos > 0 and ((dados.total_apontamentos_ok / dados.total_apontamentos::float) >= dados.meta_tracking) then 'SIM' ELSE 'NÃO' END as bateu_tracking,
  dados.gols_tracking,
  (dados.gols_dev_nf +
   dados.gols_dev_pdv +
   dados.gols_dev_hl +
   dados.gols_tracking +
   dados.gols_dispersao_tempo +
   dados.gols_jornada +
   dados.gols_tempo_interno +
   dados.gols_tempo_largada +
   dados.gols_tempo_rota +
   dados.gols_dispersao_km) as total_gols
from
(select
m.nome,
m.equipe,
m.funcao,
count(m.MAPA) as total_mapas,
-- CaixaViagem
sum(m.cxcarreg) as carregadas_total,
-- Dev Hl
sum(m.qthlcarregados) as hl_carregados_total,
sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,
sum(case when m.bateu_dev_hl = 'SIM' then 1 else 0 end) as total_mapas_bateu_dev_hl,
-- Dev Nf
sum(m.qtnfcarregadas) nf_carregadas_total,
sum(qtnfcarregadas - qtnfentregues) as nf_devolvidas_total,
sum(case when m.bateu_dev_nf = 'SIM' then 1 else 0 end) as total_mapas_bateu_dev_nf,
-- Dev Pdv
sum(m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais) as pdv_carregados_total,
sum(m.entregasnaorealizadas + m.entregasparciais) as pdv_devolvidos_total,
sum(case when m.bateu_dev_pdv = 'SIM' then 1 else 0 end) as total_mapas_bateu_dev_pdv,
-- Dispersão Km
sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad
else 0 end) as km_planejado_total,
sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)
else 0 end) as km_percorrido_total,
sum(case when m.bateu_dispersao_km = 'SIM' then 1 else 0 end) as total_mapas_bateu_disp_km,
-- Dispersão de tempo
sum(case when m.bateu_dispersao_tempo = 'SIM' then 1
else 0 END) as total_mapas_bateram_dispersao_tempo,
to_char((avg(m.resultado_tempo_rota_segundos) || ' second')::interval, 'HH24:MI:SS') as media_tempo_realizado,
to_char((avg(m.tempoprevistoroad) || ' second')::INTERVAL, 'HH24:MI:SS') as media_tempo_planejado,
-- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta
to_char((AVG(m.RESULTADO_tempo_largada_SEGUNDOS + m.RESULTADO_TEMPO_ROTA_SEGUNDOS + m.RESULTADO_TEMPO_INTERNO_SEGUNDOS) || ' second')::INTERVAL, 'HH24:MI:SS') as media_jornada,
sum(case when m.bateu_jornada = 'SIM' then 1 else 0 end) as total_mapas_bateu_jornada,
--Tempo Interno
sum(case when m.bateu_tempo_interno = 'SIM' then 1 else 0 end) as total_mapas_bateu_tempo_interno,
sum(case when to_char((m.tempo_interno || ' second')::INTERVAL, 'HH24:MI:SS')::TIME <= '05:00' and m.resultado_tempo_interno_segundos > 0 then 1
else 0
end) as total_mapas_validos_tempo_interno,
to_char((avg(m.resultado_tempo_interno_segundos) || ' second')::interval, 'HH24:MI:SS') as media_tempo_interno,
-- Tempo largada
sum(case when m.bateu_tempo_largada = 'SIM' then 1 else 0 end) as total_mapas_bateu_tempo_largada,
sum(case when
(case when m.hr_sai::time < m.hrmatinal then to_char((M.meta_tempo_largada_horas || ' second')::interval, 'HH24:MI:SS')::TIME
else (m.hr_sai - m.hrmatinal)::time
end) <= '05:00' then 1
else 0 end) as total_mapas_validos_tempo_largada,
to_char((AVG(resultado_tempo_largada_segundos) || ' second')::INTERVAL, 'HH24:MI:SS') media_tempo_largada,
-- Tempo Rota
SUM(CASE WHEN M.bateu_tempo_rota = 'SIM' THEN 1 ELSE 0 END) as total_mapas_bateu_tempo_rota,
TO_CHAR((AVG(M.resultado_tempo_rota_segundos) || ' second')::interval, 'HH24:MI:SS') media_tempo_rota,
-- Tracking
sum(M.apontamentos_ok) as total_apontamentos_ok,
sum(M.total_tracking) as total_apontamentos,
m.meta_tracking, m.meta_tempo_rota_horas as meta_tempo_rota_horas,m.meta_tempo_rota_mapas,m.meta_caixa_viagem,
m.meta_dev_hl,m.meta_dev_pdv,m.meta_dispersao_km,m.meta_dispersao_tempo,m.meta_jornada_liquida_horas as meta_jornada_liquida_horas,
m.meta_jornada_liquida_mapas,m.meta_raio_tracking, m.meta_tempo_interno_horas as meta_tempo_interno_horas,m.meta_tempo_interno_mapas,m.meta_tempo_largada_horas as meta_tempo_largada_horas,
m.meta_tempo_largada_mapas, m.meta_dev_nf,
  sum(m.gol_dev_pdv) as gols_dev_pdv,
  sum(m.gol_dev_nf) as gols_dev_nf,
  sum(m.gol_dev_hl) as gols_dev_hl,
  sum(m.gol_jornada) as gols_jornada,
  sum(m.gol_tempo_interno) as gols_tempo_interno,
  sum(m.gol_tempo_rota) as gols_tempo_rota,
  sum(m.gol_dispersao_tempo) as gols_dispersao_tempo,
  sum(m.gol_dispersao_km) as gols_dispersao_km,
  sum(m.gol_tracking)as gols_tracking,
  sum(m.gol_tempo_largada) as gols_tempo_largada
from view_extrato_indicadores m
WHERE
  m.cod_empresa = f_cod_empresa
  and m.cod_regional::text like f_cod_regional
  and m.cod_unidade::text like f_cod_unidade
  and m.cod_equipe::text like f_cod_equipe
  and m.cpf::text like f_cpf
  and M.DATA BETWEEN (f_data_inicial AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(m.cod_unidade)))
  AND (f_data_final AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(m.cod_unidade)))
GROUP BY m.equipe, m.cpf, m.nome, m.cod_unidade, m.meta_tracking,
  m.meta_tempo_rota_horas, m.meta_tempo_rota_mapas, m.meta_caixa_viagem, m.meta_dev_hl, m.meta_dev_pdv,
  m.meta_dispersao_km, m.meta_dispersao_tempo, m.meta_jornada_liquida_horas, m.meta_jornada_liquida_mapas,
  m.meta_raio_tracking, m.meta_tempo_interno_horas, m.meta_tempo_interno_mapas, m.meta_tempo_largada_horas,
  m.meta_tempo_largada_mapas, m.meta_dev_nf, m.funcao) as dados
order by total_gols DESC
$$;
-- ########################################################################################################

-- ########################################################################################################
-- ALTERA ESTRUTURA DA TABELA DE INTERVALO
-- Desse modo já criamos a nova tabela de intervalo com o a PK com nome correto.
ALTER INDEX PUBLIC.PK_INTERVALO
RENAME TO PK_INTERVALO_ANTIGA;

-- Renomeia nova tabela criada dos intervalos
ALTER TABLE PUBLIC.INTERVALO RENAME TO INTERVALO_TABELA_ANTIGA;

CREATE TABLE IF NOT EXISTS PUBLIC.INTERVALO (
  CODIGO                          BIGSERIAL NOT NULL,
  COD_UNIDADE                     BIGINT    NOT NULL
    CONSTRAINT FK_INTERVALO_UNIDADE
    REFERENCES UNIDADE,
  COD_TIPO_INTERVALO              BIGINT    NOT NULL,
  CPF_COLABORADOR                 BIGINT    NOT NULL
    CONSTRAINT FK_INTERVALO_COLABORADOR
    REFERENCES COLABORADOR,
  DATA_HORA                       TIMESTAMP WITH TIME ZONE,
  TIPO_MARCACAO                   VARCHAR(255) CONSTRAINT TIPO_MARCACAO CHECK (TIPO_MARCACAO IN
                                                                               ('MARCACAO_INICIO', 'MARCACAO_FIM')),
  FONTE_DATA_HORA                 VARCHAR(255) CONSTRAINT FONTE_DATA_HORA CHECK (FONTE_DATA_HORA IN
                                                                                 ('REDE_CELULAR', 'LOCAL_CELULAR', 'SERVIDOR')),
  JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
  JUSTIFICATIVA_ESTOURO           TEXT,
  LATITUDE_MARCACAO               TEXT,
  LONGITUDE_MARCACAO              TEXT,
  VALIDO                          BOOLEAN DEFAULT TRUE,
  CONSTRAINT PK_INTERVALO
  PRIMARY KEY (COD_UNIDADE, CODIGO),
  CONSTRAINT UNIQUE_DATA_HORA_MARCACAO_INTERVALO
  UNIQUE (COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, TIPO_MARCACAO, DATA_HORA),
  CONSTRAINT FK_INTERVALO_INTERVALO_TIPO
  FOREIGN KEY (COD_TIPO_INTERVALO, COD_UNIDADE) REFERENCES INTERVALO_TIPO (CODIGO, COD_UNIDADE)
);

-- INSERT DAS MARCAÇÕES DE INÍCIO
INSERT INTO PUBLIC.INTERVALO (COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, DATA_HORA,
                                          FONTE_DATA_HORA, TIPO_MARCACAO, LATITUDE_MARCACAO,
                                          LONGITUDE_MARCACAO)
  SELECT
    I.COD_UNIDADE,
    I.COD_TIPO_INTERVALO,
    I.CPF_COLABORADOR,
    I.DATA_HORA_INICIO,
    I.FONTE_DATA_HORA_INICIO,
    'MARCACAO_INICIO',
    I.LATITUDE_INICIO,
    I.LONGITUDE_INICIO
  FROM PUBLIC.INTERVALO_TABELA_ANTIGA I
  WHERE DATA_HORA_INICIO IS NOT NULL;

-- INSERT DAS MARCAÇÕES DE FIM
INSERT INTO PUBLIC.INTERVALO (COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, DATA_HORA, JUSTIFICATIVA_ESTOURO,
                                          FONTE_DATA_HORA, TIPO_MARCACAO, JUSTIFICATIVA_TEMPO_RECOMENDADO, LATITUDE_MARCACAO,
                                          LONGITUDE_MARCACAO)
  SELECT
    I.COD_UNIDADE,
    I.COD_TIPO_INTERVALO,
    I.CPF_COLABORADOR,
    I.DATA_HORA_FIM,
    I.JUSTIFICATIVA_ESTOURO,
    I.FONTE_DATA_HORA_FIM,
    'MARCACAO_FIM',
    I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
    I.LATITUDE_FIM,
    I.LONGITUDE_FIM
  FROM PUBLIC.INTERVALO_TABELA_ANTIGA I
  WHERE DATA_HORA_FIM IS NOT NULL;

-- Deleta a tabela atual dos intervalos e as views que dependem dela.
DROP VIEW PUBLIC.VIEW_INTERVALO_MAPA_COLABORADOR;
DROP VIEW PUBLIC.VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS;

create or replace function func_intervalos_agrupados(f_cod_unidade bigint, f_cpf_colaborador bigint, f_cod_tipo_intervalo bigint, f_data_inicio date) returns TABLE(cod_unidade bigint, cod_tipo_intervalo bigint, cpf_colaborador bigint, data_hora_inicio timestamp with time zone, data_hora_fim timestamp with time zone, fonte_data_hora_inicio text, fonte_data_hora_fim text, justificativa_tempo_recomendado text, justificativa_estouro text, latitude_marcacao_inicio text, latitude_marcacao_fim text, longitude_marcacao_inicio text, longitude_marcacao_fim text)
LANGUAGE SQL
AS $$
SELECT
  COD_UNIDADE,
  COD_TIPO_INTERVALO,
  CPF_COLABORADOR,
  DATA_HORA_INICIO,
  DATA_HORA_FIM,
  FONTE_DATA_HORA_INICIO,
  FONTE_DATA_HORA_FIM,
  JUSTIFICATIVA_TEMPO_RECOMENDADO,
  JUSTIFICATIVA_ESTOURO,
  LATITUDE_MARCACAO_INICIO,
  LATITUDE_MARCACAO_FIM,
  LONGITUDE_MARCACAO_INICIO,
  LONGITUDE_MARCACAO_FIM
FROM (
       SELECT
         I.COD_UNIDADE                     AS COD_UNIDADE,
         I.COD_TIPO_INTERVALO              AS COD_TIPO_INTERVALO,
         I.CPF_COLABORADOR                 AS CPF_COLABORADOR,
         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_INICIO'
           THEN
             I.DATA_HORA
         END                               AS DATA_HORA_INICIO,
         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_INICIO'
           THEN
             CASE WHEN (SELECT I1.TIPO_MARCACAO
                        FROM INTERVALO I1
                        WHERE
                          I1.COD_UNIDADE = I.cod_unidade
                          AND I1.CPF_COLABORADOR = I.cpf_colaborador
                          AND I1.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                          AND I1.DATA_HORA > I.DATA_HORA
                        ORDER BY I1.DATA_HORA
                        LIMIT 1) = 'MARCACAO_INICIO'
               THEN NULL
             ELSE
               (SELECT I2.DATA_HORA
                FROM INTERVALO I2
                WHERE
                  I2.COD_UNIDADE = I.cod_unidade
                  AND I2.CPF_COLABORADOR = I.cpf_colaborador
                  AND I2.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                  AND I2.DATA_HORA > I.DATA_HORA
                  AND I2.TIPO_MARCACAO = 'MARCACAO_FIM'
                ORDER BY I2.DATA_HORA
                LIMIT 1)
             END
         END                               AS DATA_HORA_FIM,

         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_INICIO'
           THEN
             I.FONTE_DATA_HORA
         END                               AS FONTE_DATA_HORA_INICIO,

         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_INICIO'
           THEN
             CASE WHEN (SELECT I1.TIPO_MARCACAO
                        FROM INTERVALO I1
                        WHERE
                          I1.COD_UNIDADE = I.cod_unidade
                          AND I1.CPF_COLABORADOR = I.cpf_colaborador
                          AND I1.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                          AND I1.DATA_HORA > I.DATA_HORA
                        ORDER BY I1.DATA_HORA
                        LIMIT 1) = 'MARCACAO_INICIO'
               THEN NULL
             ELSE
               (SELECT I2.FONTE_DATA_HORA
                FROM INTERVALO I2
                WHERE
                  I2.COD_UNIDADE = I.cod_unidade
                  AND I2.CPF_COLABORADOR = I.cpf_colaborador
                  AND I2.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                  AND I2.DATA_HORA > I.DATA_HORA
                  AND I2.TIPO_MARCACAO = 'MARCACAO_FIM'
                ORDER BY I2.DATA_HORA
                LIMIT 1)
             END
         END                               AS FONTE_DATA_HORA_FIM,

         I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
         I.JUSTIFICATIVA_ESTOURO           AS JUSTIFICATIVA_ESTOURO,
         I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO_INICIO,
         I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO_INICIO,
         I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO_FIM,
         I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO_FIM
       FROM INTERVALO I
       WHERE CASE WHEN f_cod_unidade IS NULL
         THEN TRUE
             ELSE I.COD_UNIDADE = f_cod_unidade END
             AND CASE WHEN f_cpf_colaborador IS NULL
         THEN TRUE
                 ELSE I.CPF_COLABORADOR = f_cpf_colaborador END
             AND CASE WHEN f_cod_tipo_intervalo IS NULL
         THEN TRUE
                 ELSE I.COD_TIPO_INTERVALO = f_cod_tipo_intervalo END
             AND CASE WHEN f_data_inicio IS NULL
         THEN TRUE
                 ELSE I.DATA_HORA::DATE = f_data_inicio END
       UNION ALL
       SELECT
         I.COD_UNIDADE                     AS COD_UNIDADE,
         I.COD_TIPO_INTERVALO              AS COD_TIPO_INTERVALO,
         I.CPF_COLABORADOR                 AS CPF_COLABORADOR,
         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_FIM'
           THEN
             CASE WHEN (SELECT I1.TIPO_MARCACAO
                        FROM INTERVALO I1
                        WHERE
                          I1.COD_UNIDADE = I.cod_unidade
                          AND I1.CPF_COLABORADOR = I.cpf_colaborador
                          AND I1.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                          AND I1.DATA_HORA < I.DATA_HORA
                        ORDER BY I1.DATA_HORA DESC
                        LIMIT 1) = 'MARCACAO_FIM'
               THEN NULL
             ELSE
               (SELECT I2.DATA_HORA
                FROM INTERVALO I2
                WHERE
                  I2.COD_UNIDADE = I.cod_unidade
                  AND I2.CPF_COLABORADOR = I.cpf_colaborador
                  AND I2.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                  AND I2.DATA_HORA < I.DATA_HORA
                  AND I2.TIPO_MARCACAO = 'MARCACAO_INICIO'
                ORDER BY I2.DATA_HORA DESC
                LIMIT 1)
             END
         END                               AS DATA_HORA_INICIO,
         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_FIM'
           THEN
             I.DATA_HORA
         END                               AS DATA_HORA_FIM,


         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_FIM'
           THEN
             CASE WHEN (SELECT I1.TIPO_MARCACAO
                        FROM INTERVALO I1
                        WHERE
                          I1.COD_UNIDADE = I.cod_unidade
                          AND I1.CPF_COLABORADOR = I.cpf_colaborador
                          AND I1.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                          AND I1.DATA_HORA < I.DATA_HORA
                        ORDER BY I1.DATA_HORA DESC
                        LIMIT 1) = 'MARCACAO_FIM'
               THEN NULL
             ELSE
               (SELECT I2.FONTE_DATA_HORA
                FROM INTERVALO I2
                WHERE
                  I2.COD_UNIDADE = I.cod_unidade
                  AND I2.CPF_COLABORADOR = I.cpf_colaborador
                  AND I2.COD_TIPO_INTERVALO = I.COD_TIPO_INTERVALO
                  AND I2.DATA_HORA < I.DATA_HORA
                  AND I2.TIPO_MARCACAO = 'MARCACAO_INICIO'
                ORDER BY I2.DATA_HORA DESC
                LIMIT 1)
             END
         END                               AS FONTE_DATA_HORA_INICIO,


         CASE
         WHEN
           I.TIPO_MARCACAO = 'MARCACAO_FIM'
           THEN
             I.FONTE_DATA_HORA
         END                               AS FONTE_DATA_HORA_FIM,

         I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
         I.JUSTIFICATIVA_ESTOURO           AS JUSTIFICATIVA_ESTOURO,
         I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO_INICIO,
         I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO_INICIO,
         I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO_FIM,
         I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO_FIM
       FROM INTERVALO I
       WHERE CASE WHEN f_cod_unidade IS NULL
         THEN TRUE
             ELSE I.COD_UNIDADE = f_cod_unidade END
             AND CASE WHEN f_cpf_colaborador IS NULL
         THEN TRUE
                 ELSE I.CPF_COLABORADOR = f_cpf_colaborador END
             AND CASE WHEN f_cod_tipo_intervalo IS NULL
         THEN TRUE
                 ELSE I.COD_TIPO_INTERVALO = f_cod_tipo_intervalo END
             AND CASE WHEN f_data_inicio IS NULL
         THEN TRUE
                 ELSE I.DATA_HORA::DATE = f_data_inicio END
     ) intervalos
WHERE (data_hora_inicio IS NOT NULL OR data_hora_fim IS NOT NULL)
GROUP BY COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, DATA_HORA_INICIO, DATA_HORA_FIM, FONTE_DATA_HORA_INICIO,
  FONTE_DATA_HORA_FIM, JUSTIFICATIVA_TEMPO_RECOMENDADO, JUSTIFICATIVA_ESTOURO, LATITUDE_MARCACAO_INICIO,
  LATITUDE_MARCACAO_FIM, LONGITUDE_MARCACAO_INICIO, LONGITUDE_MARCACAO_FIM
ORDER BY COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM)
$$;


-- View que faz a junção dos mapas com os intervalos de cada colaborador que saiu naquele mapa
CREATE VIEW view_extrato_mapas_versus_intervalos AS SELECT m.data,
    m.mapa,
    m.cod_unidade,
    (m.fator + (1)::double precision) AS intervalos_previstos,
    ((
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) AS intervalos_realizados,
    mot.cpf AS cpf_motorista,
    mot.nome AS nome_motorista,
    COALESCE(to_char(((int_mot.data_hora_inicio)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS inicio_intervalo_mot,
    COALESCE(to_char(((int_mot.data_hora_fim)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS fim_intervalo_mot,
    COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_mot,
        CASE
            WHEN (int_mot.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS mot_cumpriu_tempo_minimo,
    aj1.cpf AS cpf_aj1,
    COALESCE(aj1.nome, '-'::character varying) AS nome_aj1,
    COALESCE(to_char(((int_aj1.data_hora_inicio)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS inicio_intervalo_aj1,
    COALESCE(to_char(((int_aj1.data_hora_fim)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS fim_intervalo_aj1,
    COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj1,
        CASE
            WHEN (int_aj1.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj1_cumpriu_tempo_minimo,
    aj2.cpf AS cpf_aj2,
    COALESCE(aj2.nome, '-'::character varying) AS nome_aj2,
    COALESCE(to_char(((int_aj2.data_hora_inicio)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS inicio_intervalo_aj2,
    COALESCE(to_char(((int_aj2.data_hora_fim)::time without time zone)::interval, 'HH:MI'::text), '-'::text) AS fim_intervalo_aj2,
    COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj2,
        CASE
            WHEN (int_aj2.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj2_cumpriu_tempo_minimo
   FROM (((((((mapa m
     JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
     JOIN colaborador mot ON ((((mot.cod_unidade = m.cod_unidade) AND (mot.cod_funcao = ufp.cod_funcao_motorista)) AND (mot.matricula_ambev = m.matricmotorista))))
     LEFT JOIN colaborador aj1 ON ((((aj1.cod_unidade = m.cod_unidade) AND (aj1.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj1.matricula_ambev = m.matricajud1))))
     LEFT JOIN colaborador aj2 ON ((((aj2.cod_unidade = m.cod_unidade) AND (aj2.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj2.matricula_ambev = m.matricajud2))))
     LEFT JOIN LATERAL func_intervalos_agrupados(m.cod_unidade, mot.cpf, NULL, m.data) int_mot ON TRUE
     LEFT JOIN LATERAL func_intervalos_agrupados(m.cod_unidade, aj1.cpf, NULL, m.data) int_aj1 ON TRUE
     LEFT JOIN LATERAL func_intervalos_agrupados(m.cod_unidade, aj2.cpf, NULL, m.data) int_aj2 ON TRUE)))
  ORDER BY m.mapa DESC;

-- View que lista cada mapa realizado por um colaborador e o intervalo realizado para aquele mapa
CREATE VIEW view_intervalo_mapa_colaborador AS SELECT
                                                 view_extrato_mapas_versus_intervalos.data,
                                                 view_extrato_mapas_versus_intervalos.mapa,
                                                 view_extrato_mapas_versus_intervalos.cod_unidade,
                                                 view_extrato_mapas_versus_intervalos.cpf_motorista               AS cpf,
                                                 view_extrato_mapas_versus_intervalos.inicio_intervalo_mot        AS inicio_intervalo,
                                                 view_extrato_mapas_versus_intervalos.fim_intervalo_mot           AS fim_intervalo,
                                                 view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_mot AS tempo_decorrido_minutos,
                                                 view_extrato_mapas_versus_intervalos.mot_cumpriu_tempo_minimo    AS cumpriu_tempo_minimo
                                               FROM view_extrato_mapas_versus_intervalos
                                               UNION
                                               SELECT
                                                 view_extrato_mapas_versus_intervalos.data,
                                                 view_extrato_mapas_versus_intervalos.mapa,
                                                 view_extrato_mapas_versus_intervalos.cod_unidade,
                                                 view_extrato_mapas_versus_intervalos.cpf_aj1                     AS cpf,
                                                 view_extrato_mapas_versus_intervalos.inicio_intervalo_aj1        AS inicio_intervalo,
                                                 view_extrato_mapas_versus_intervalos.fim_intervalo_aj1           AS fim_intervalo,
                                                 view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj1 AS tempo_decorrido_minutos,
                                                 view_extrato_mapas_versus_intervalos.aj1_cumpriu_tempo_minimo    AS cumpriu_tempo_minimo
                                               FROM view_extrato_mapas_versus_intervalos
                                               UNION
                                               SELECT
                                                 view_extrato_mapas_versus_intervalos.data,
                                                 view_extrato_mapas_versus_intervalos.mapa,
                                                 view_extrato_mapas_versus_intervalos.cod_unidade,
                                                 view_extrato_mapas_versus_intervalos.cpf_aj2                     AS cpf,
                                                 view_extrato_mapas_versus_intervalos.inicio_intervalo_aj2        AS inicio_intervalo,
                                                 view_extrato_mapas_versus_intervalos.fim_intervalo_aj2           AS fim_intervalo,
                                                 view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj2 AS tempo_decorrido_minutos,
                                                 view_extrato_mapas_versus_intervalos.aj2_cumpriu_tempo_minimo    AS cumpriu_tempo_minimo
                                               FROM view_extrato_mapas_versus_intervalos;

-- Cria view dos intervalos. Responsável por gerar um código único de marcação por unidade.
CREATE OR REPLACE VIEW PUBLIC.VIEW_INTERVALO AS
  SELECT
    row_number()
    OVER (
      PARTITION BY I.COD_UNIDADE
      ORDER BY I.CODIGO ) AS CODIGO_MARCACAO_POR_UNIDADE,
    *
  FROM INTERVALO I;

-- Adiciona campo de PIS na tabela de colaborador
ALTER TABLE public.colaborador ADD pis VARCHAR(11) NULL;
-- ########################################################################################################

-- ########################################################################################################
-- Cria permissão para visualizar relatórios sobre os treinamentos
INSERT INTO public.funcao_prolog_v11 (codigo, funcao, cod_pilar) VALUES (343, 'Visualizar relatórios sobre os treinamentos', 3);

CREATE OR REPLACE FUNCTION func_relatorio_treinamento_visualizados_por_colaborador(f_data_inicial    DATE,
                                                                                   f_data_final      DATE,
                                                                                   f_time_zone_datas TEXT,
                                                                                   f_cod_unidade     BIGINT)
  RETURNS TABLE(
    "CPF COLABORADOR" TEXT,
    "NOME COLABORADOR" TEXT,
    "TÍTULO TREINAMENTO" TEXT,
    "DESCRIÇÃO TREINAMENTO" TEXT,
    "DATA/HORA VISUALIZAÇÃO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  LPAD(tc.cpf_colaborador :: TEXT, 11, '0')                                          AS CPF,
  c.nome                                                                             AS COLABORADOR,
  t.titulo                                                                           AS TITULO_TREINAMENTO,
  t.descricao                                                                        AS DESCRICAO_TREINAMENTO,
  to_char(tc.data_visualizacao AT TIME ZONE f_time_zone_datas, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_VISUALIZACAO
FROM treinamento_colaborador tc
  JOIN treinamento t ON tc.cod_treinamento = t.codigo
  JOIN colaborador c ON tc.cpf_colaborador = c.cpf
WHERE T.cod_unidade = f_cod_unidade
      AND tc.data_visualizacao :: DATE >= f_data_inicial
      AND tc.data_visualizacao :: DATE <= f_data_final
ORDER BY c.nome;
$$;
-- ########################################################################################################
END TRANSACTION;