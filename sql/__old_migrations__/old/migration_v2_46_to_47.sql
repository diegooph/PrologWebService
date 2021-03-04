-- Essa migração deve ser executada quando o WS versão 47 for publicado.
BEGIN TRANSACTION;

  -- ########################################################################################################
  -- Cria coluna TIMEZONE na tabela unidade
  ALTER TABLE unidade ADD timezone TEXT NULL;
  UPDATE unidade SET timezone = 'America/Sao_Paulo' WHERE codigo != 16;
  UPDATE unidade SET timezone = 'America/Cuiaba' WHERE codigo = 16;
  ALTER TABLE unidade ALTER COLUMN timezone SET NOT NULL;
  -- ########################################################################################################

  -- ########################################################################################################
  -- Cria function para buscar o timezone da unidade
  CREATE OR REPLACE FUNCTION func_get_time_zone_unidade(F_COD_UNIDADE bigint)
  RETURNS TABLE(TIMEZONE TEXT)
  LANGUAGE SQL
  AS $$
  SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE;
  $$;
  -- ########################################################################################################

  -- ########################################################################################################
  -- DROPS DAS VIEWS COM DEPENDÊNCIA
  DROP VIEW view_pneu_analise_vida_atual;
  DROP VIEW view_intervalo_mapa_colaborador;
  DROP VIEW view_extrato_indicadores;
  DROP VIEW view_analise_pneus;
  DROP VIEW view_produtividade_extrato;
  DROP VIEW estratificacao_os;
  DROP VIEW resumo_dados;
  DROP VIEW view_valor_cx_unidade;
  DROP VIEW view_mapa_colaborador;
  DROP VIEW view_pneu_analise_vidas;
  DROP VIEW view_extrato_mapas_versus_intervalos;
  DROP VIEW view_pneu_km_percorrido;
  -- ########################################################################################################

  -- ########################################################################################################
  -- ALTERAÇÕES DAS COLUNAS QUE UTILIZAM TIMESTAMP
  ALTER TABLE acessos_produtividade ALTER COLUMN data_hora_consulta TYPE timestamp with time zone USING data_hora_consulta AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE afericao ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE afericao_manutencao ALTER COLUMN data_hora_resolucao TYPE timestamp with time zone USING data_hora_resolucao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE calendario ALTER COLUMN data TYPE timestamp with time zone USING data AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE checklist ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE checklist_manutencao ALTER COLUMN data_apontamento TYPE timestamp with time zone USING data_apontamento AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE checklist_manutencao ALTER COLUMN data_resolucao TYPE timestamp with time zone USING data_resolucao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE checklist_ordem_servico ALTER COLUMN data_hora_fechamento TYPE timestamp with time zone USING data_hora_fechamento AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE checklist_ordem_servico_itens ALTER COLUMN data_hora_conserto TYPE timestamp with time zone USING data_hora_conserto AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE dashboard_componente ALTER COLUMN data_hora_criacao TYPE timestamp with time zone USING data_hora_criacao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE dashboard_componente ALTER COLUMN data_hora_ultima_alteracao TYPE timestamp with time zone USING data_hora_ultima_alteracao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE dashboard_componente_tipo ALTER COLUMN data_hora_criacao TYPE timestamp with time zone USING data_hora_criacao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE dashboard_componente_tipo ALTER COLUMN data_hora_ultima_alteracao TYPE timestamp with time zone USING data_hora_ultima_alteracao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE fale_conosco ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE fale_conosco ALTER COLUMN data_hora_feedback TYPE timestamp with time zone USING data_hora_feedback AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE gsd ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE intervalo ALTER COLUMN data_hora_fim TYPE timestamp with time zone USING data_hora_fim AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE intervalo ALTER COLUMN data_hora_inicio TYPE timestamp with time zone USING data_hora_inicio AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE movimentacao_motivo_descarte_empresa ALTER COLUMN data_hora_insercao TYPE timestamp with time zone USING data_hora_insercao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE movimentacao_motivo_descarte_empresa ALTER COLUMN data_hora_ultima_alteracao TYPE timestamp with time zone USING data_hora_ultima_alteracao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE movimentacao_processo ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE prontuario_condutor_consolidado ALTER COLUMN data_atualizacao TYPE timestamp with time zone USING data_atualizacao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE quiz ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE quiz_modelo ALTER COLUMN data_hora_fechamento TYPE timestamp with time zone USING data_hora_fechamento AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE quiz_modelo ALTER COLUMN data_hora_abertura TYPE timestamp with time zone USING data_hora_abertura AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE relato ALTER COLUMN data_hora_classificacao TYPE timestamp with time zone USING data_hora_classificacao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE relato ALTER COLUMN data_hora_database TYPE timestamp with time zone USING data_hora_database AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE relato ALTER COLUMN data_hora_fechamento TYPE timestamp with time zone USING data_hora_fechamento AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE relato ALTER COLUMN data_hora_local TYPE timestamp with time zone USING data_hora_local AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE token_autenticacao ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE treinamento ALTER COLUMN data_hora_cadastro TYPE timestamp with time zone USING data_hora_cadastro AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE treinamento_colaborador ALTER COLUMN data_visualizacao TYPE timestamp with time zone USING data_visualizacao AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE veiculo_pneu_inconsistencia ALTER COLUMN data_hora TYPE timestamp with time zone USING data_hora AT TIME ZONE 'America/Sao_Paulo';
  ALTER TABLE veiculo_pneu_inconsistencia ALTER COLUMN data_hora_fechamento TYPE timestamp with time zone USING data_hora_fechamento AT TIME ZONE 'America/Sao_Paulo';
  -- ########################################################################################################

  -- ########################################################################################################
  -- ORDEM DE CRIAÇÃO DAS VIEWS
  -- view_pneu_km_percorrido; 1 - OK (Correção timezone não se aplica)
  -- view_extrato_mapas_versus_intervalos; 2 - OK (Correção timezone não aplicado)
  -- view_pneu_analise_vidas; 3 - OK (Correção timezone aplicada)
  -- view_mapa_colaborador; 4 - OK (Correção timezone não aplicado)
  -- view_valor_cx_unidade; 5 - OK (Correção timezone não se aplica)
  -- resumo_dados; 6 - OK (Correção timezone não aplicado)
  -- estratificacao_os; 7 - OK (Correção timezone aplicada)
  -- view_produtividade_extrato; 8 - OK (Correção timezone não aplicado)
  -- view_analise_pneus; 9 - OK (Correção timezone aplicada)
  -- view_extrato_indicadores; 10 - OK (Correção timezone não aplicado)
  -- view_intervalo_mapa_colaborador; 11 - OK (Correção timezone não aplicado)
  -- view_pneu_analise_vida_atual; 12 - OK (Correção timezone não se aplica)

  -- VIEW PARA CALCULAR O KM PERCORRIDO POR UM PNEU EM CADA VIDA
  CREATE VIEW view_pneu_km_percorrido AS SELECT total_km_rodado.cod_pneu,
    total_km_rodado.vida_momento_afericao AS vida,
    total_km_rodado.cod_unidade,
    sum(total_km_rodado.km_rodado) AS total_km
   FROM ( SELECT av_1.cod_pneu,
            av_1.vida_momento_afericao,
            av_1.cod_unidade,
            a_1.placa_veiculo,
            (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
           FROM (afericao_valores av_1
             JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
          GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo, av_1.vida_momento_afericao) total_km_rodado
  GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade, total_km_rodado.vida_momento_afericao;

  -- View que faz a junção dos mapas comos intervalos de cada colaborador que saiu naquele mapa
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
     LEFT JOIN intervalo int_mot ON (((int_mot.cpf_colaborador = mot.cpf) AND ((int_mot.data_hora_inicio)::date = m.data))))
     LEFT JOIN intervalo int_aj1 ON (((int_aj1.cpf_colaborador = aj1.cpf) AND ((int_aj1.data_hora_inicio)::date = m.data))))
     LEFT JOIN intervalo int_aj2 ON (((int_aj2.cpf_colaborador = aj2.cpf) AND ((int_aj2.data_hora_inicio)::date = m.data))))
  ORDER BY m.mapa DESC;

  -- VIEW QUE GERA O EXTRATO POR VIDA DE UM PNEU
  CREATE VIEW view_pneu_analise_vidas AS SELECT av.cod_pneu,
    av.vida_momento_afericao AS vida,
    p_1.status,
    p_1.valor AS valor_pneu,
    COALESCE(pvv.valor, (0)::real) AS valor_banda,
    av.cod_unidade,
    count(av.altura_sulco_central_interno) AS qt_afericoes,
    (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date AS primeira_afericao,
    (max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date AS ultima_afericao,
    ((max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date - (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date) AS total_dias,
    max(total_km.total_km) AS total_km_percorrido_vida,
    max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS maior_sulco,
    min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) AS menor_sulco,
    (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) AS sulco_gasto,
        CASE
            WHEN (
            CASE
                WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                ELSE NULL::real
            END < (0)::double precision) THEN (0)::real
            ELSE
            CASE
                WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                ELSE NULL::real
            END
        END AS sulco_restante,
        CASE
            WHEN ((((max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date - (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date) > 0) AND ((max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))) > (0)::double precision)) THEN ((max(total_km.total_km))::double precision / (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo, av.altura_sulco_interno))))
            ELSE (0)::double precision
        END AS km_por_mm,
        CASE
            WHEN (max(total_km.total_km) <= (0)::numeric) THEN (0)::double precision
            ELSE
            CASE
                WHEN (av.vida_momento_afericao = 1) THEN (p_1.valor / (max(total_km.total_km))::double precision)
                ELSE (COALESCE(pvv.valor, (0)::real) / (max(total_km.total_km))::double precision)
            END
        END AS valor_por_km_vida_atual
   FROM (((((afericao_valores av
     JOIN afericao a ON ((a.codigo = av.cod_afericao)))
     JOIN pneu p_1 ON ((((p_1.codigo)::text = (av.cod_pneu)::text) AND (p_1.cod_unidade = av.cod_unidade))))
     JOIN empresa_restricao_pneu erp ON ((erp.cod_unidade = av.cod_unidade)))
     LEFT JOIN pneu_valor_vida pvv ON ((((pvv.cod_unidade = p_1.cod_unidade) AND ((pvv.cod_pneu)::text = (p_1.codigo)::text)) AND (pvv.vida = av.vida_momento_afericao))))
     JOIN ( SELECT view_pneu_km_percorrido.cod_pneu,
            view_pneu_km_percorrido.vida,
            view_pneu_km_percorrido.cod_unidade,
            view_pneu_km_percorrido.total_km
           FROM view_pneu_km_percorrido) total_km ON (((((total_km.cod_pneu)::text = (av.cod_pneu)::text) AND (total_km.cod_unidade = av.cod_unidade)) AND (total_km.vida = av.vida_momento_afericao))))
  GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, erp.sulco_minimo_descarte, erp.sulco_minimo_recapagem, av.vida_momento_afericao, pvv.valor, p_1.valor, p_1.status
  ORDER BY av.cod_pneu, av.vida_momento_afericao;

  -- VIEW QUE LISTA EM DUAS COLUNAS TODOS OS MAPAS E QUAL COLABORADOR PARTICIPOU
  CREATE VIEW view_mapa_colaborador AS SELECT m.mapa,
      c.cpf,
      c.cod_unidade
     FROM ((mapa m
       JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
       JOIN colaborador c ON ((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND (c.cod_unidade = m.cod_unidade))))
  UNION
   SELECT m.mapa,
      c.cpf,
      c.cod_unidade
     FROM ((mapa m
       JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
       JOIN colaborador c ON ((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (c.cod_unidade = m.cod_unidade))))
  UNION
   SELECT m.mapa,
      c.cpf,
      c.cod_unidade
     FROM ((mapa m
       JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
       JOIN colaborador c ON ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (c.cod_unidade = m.cod_unidade))));
  COMMENT ON VIEW view_mapa_colaborador IS 'View utilizada para linkar os mapas relizados por cada colaborador';

  -- VIEW QUE CALCULA O VALOR DA CX PARA DIFERENTES MAPAS (C/ OU S/ JORNADA BATIDA)
  CREATE VIEW view_valor_cx_unidade AS SELECT DISTINCT m.cod_unidade,
      max(round(((m.vlbateujornmot / m.cxentreg))::numeric, 2)) AS valor_cx_motorista_rota,
      max(round(((m.vlbateujornaju / m.cxentreg))::numeric, 2)) AS valor_cx_ajudante_rota
     FROM mapa m
    WHERE ((m.vltotalmapa > (0)::double precision) AND (m.vlbateujornmot > (0)::double precision))
    GROUP BY m.cod_unidade;

  -- VIEW QUE GERA O RELATÓRIO COM OS DIAS COM DADOS IMPORTADOS (TRACKING / MAPA)
  CREATE VIEW RESUMO_DADOS AS SELECT DISTINCT M.DATA AS MAPA,
      T.DATA AS TRACKING,
      M.COD_UNIDADE,
      T."código_transportadora"
     FROM (MAPA M
       LEFT JOIN TRACKING T ON ((M.DATA = T.DATA)))
    ORDER BY M.DATA DESC, T.DATA;
  COMMENT ON VIEW resumo_dados IS 'View utilizada para juntar as datas que tem dados enviados ao sistema, planilhas ambev (2art e tracking)';

  -- VIEW QUE ESTRATIFICA TODOS OS ITENS DE TODAS AS O.S.
  CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS SELECT OS.CODIGO AS COD_OS,
      OS.COD_UNIDADE,
      OS.STATUS AS STATUS_OS,
      OS.COD_CHECKLIST,
      CP.CODIGO AS COD_PERGUNTA,
      CP.ORDEM AS ORDEM_PERGUNTA,
      CP.PERGUNTA,
      CP.SINGLE_CHOICE,
      NULL::UNKNOWN AS URL_IMAGEM,
      CP.PRIORIDADE,
      C.PLACA_VEICULO,
      C.KM_VEICULO AS KM,
      V.COD_TIPO,
      CAP.CODIGO AS COD_ALTERNATIVA,
      CAP.ALTERNATIVA,
      CR.RESPOSTA,
      COSI.STATUS_RESOLUCAO AS STATUS_ITEM,
      CO.NOME AS NOME_MECANICO,
      COSI.CPF_MECANICO,
      C.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(OS.COD_UNIDADE)) AS DATA_HORA,
      PPC.PRAZO,
      COSI.TEMPO_REALIZACAO,
      COSI.DATA_HORA_CONSERTO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(OS.COD_UNIDADE)) AS DATA_HORA_CONSERTO,
      COSI.KM AS KM_FECHAMENTO,
      COSI.QT_APONTAMENTOS,
      COSI.FEEDBACK_CONSERTO,
      (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(OS.COD_UNIDADE)) AS TIME_ZONE_UNIDADE
     FROM ((((((((CHECKLIST C
       JOIN VEICULO V ON (((V.PLACA)::TEXT = (C.PLACA_VEICULO)::TEXT)))
       JOIN CHECKLIST_ORDEM_SERVICO OS ON (((C.CODIGO = OS.COD_CHECKLIST) AND (C.COD_UNIDADE = OS.COD_UNIDADE))))
       JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI ON (((OS.CODIGO = COSI.COD_OS) AND (OS.COD_UNIDADE = COSI.COD_UNIDADE))))
       JOIN CHECKLIST_PERGUNTAS CP ON ((((CP.COD_UNIDADE = OS.COD_UNIDADE) AND (CP.CODIGO = COSI.COD_PERGUNTA)) AND (CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO))))
       JOIN PRIORIDADE_PERGUNTA_CHECKLIST PPC ON (((PPC.PRIORIDADE)::TEXT = (CP.PRIORIDADE)::TEXT)))
       JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON (((((CAP.COD_UNIDADE = CP.COD_UNIDADE) AND (CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO)) AND (CAP.COD_PERGUNTA = CP.CODIGO)) AND (CAP.CODIGO = COSI.COD_ALTERNATIVA))))
       JOIN CHECKLIST_RESPOSTAS CR ON ((((((C.COD_UNIDADE = CR.COD_UNIDADE) AND (CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO)) AND (CR.COD_CHECKLIST = C.CODIGO)) AND (CR.COD_PERGUNTA = CP.CODIGO)) AND (CR.COD_ALTERNATIVA = CAP.CODIGO))))
       LEFT JOIN COLABORADOR CO ON ((CO.CPF = COSI.CPF_MECANICO)));
  COMMENT ON VIEW estratificacao_os IS 'View que compila as informações das OS e seus itens';

  -- VIEW QUE ESTRATIFICA TODOS OS MAPAS
  CREATE VIEW view_produtividade_extrato AS SELECT vmc.cod_unidade,
      c.matricula_ambev,
      m.data,
      vmc.cpf,
      c.nome AS nome_colaborador,
      c.data_nascimento,
      f.nome AS funcao,
      f.codigo AS cod_funcao,
      e.nome AS nome_equipe,
      m.fator,
      m.cargaatual,
      m.entrega,
      m.mapa,
      m.placa,
      m.cxcarreg,
      m.cxentreg,
      m.qthlcarregados,
      m.qthlentregues,
      m.qtnfcarregadas,
      m.qtnfentregues,
      m.entregascompletas,
      m.entregasnaorealizadas,
      m.entregasparciais,
      m.kmprevistoroad,
      m.kmsai,
      m.kmentr,
      to_seconds((m.tempoprevistoroad)::text) AS tempoprevistoroad,
      m.hrsai,
      m.hrentr,
      to_seconds((((m.hrentr - m.hrsai))::time without time zone)::text) AS tempo_rota,
      to_seconds((m.tempointerno)::text) AS tempointerno,
      m.hrmatinal,
      tracking.apontamentos_ok,
      tracking.total_apontamentos AS total_tracking,
      to_seconds((
          CASE
              WHEN (((m.hrsai)::time without time zone < m.hrmatinal) OR (m.hrmatinal = '00:00:00'::time without time zone)) THEN um.meta_tempo_largada_horas
              ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
          END)::text) AS tempo_largada,
      um.meta_tracking,
      um.meta_tempo_rota_mapas,
      um.meta_caixa_viagem,
      um.meta_dev_hl,
      um.meta_dev_nf,
      um.meta_dev_pdv,
      um.meta_dispersao_km,
      um.meta_dispersao_tempo,
      um.meta_jornada_liquida_mapas,
      um.meta_raio_tracking,
      um.meta_tempo_interno_mapas,
      um.meta_tempo_largada_mapas,
      to_seconds((um.meta_tempo_rota_horas)::text) AS meta_tempo_rota_horas,
      to_seconds((um.meta_tempo_interno_horas)::text) AS meta_tempo_interno_horas,
      to_seconds((um.meta_tempo_largada_horas)::text) AS meta_tempo_largada_horas,
      to_seconds((um.meta_jornada_liquida_horas)::text) AS meta_jornada_liquida_horas,
          CASE
              WHEN (((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN (m.vlbateujornmot + m.vlnaobateujornmot)
              WHEN (((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
              WHEN (((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
              ELSE (0)::real
          END AS valor_rota,
      (
          CASE
              WHEN (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text = 'Recarga'::text)) THEN
              CASE
                  WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) THEN m.vlrecargamot
                  WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN (m.vlrecargaaju / m.fator)
                  WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN (m.vlrecargaaju / m.fator)
                  ELSE (0)::real
              END
              ELSE (0)::real
          END +
          CASE
              WHEN (((m.entrega)::text = 'AS'::text) AND ((m.cargaatual)::text = 'Recarga'::text)) THEN
              CASE
                  WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) THEN uv.rm_motorista_valor_as_recarga
                  WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN uv.rm_ajudante_valor_as_recarga
                  WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN uv.rm_ajudante_valor_as_recarga
                  ELSE (0)::real
              END
              ELSE (0)::real
          END) AS valor_recarga,
          CASE
              WHEN (((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota)::double precision) / (m.fator)::double precision) - ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot))
              WHEN (((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision) - (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
              WHEN (((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision) - (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
              ELSE (0)::double precision
          END AS valor_diferenca_eld,
          CASE
              WHEN ((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
              CASE
                  WHEN (m.entregas = 1) THEN uv.rm_motorista_valor_as_1_entrega
                  WHEN (m.entregas = 2) THEN uv.rm_motorista_valor_as_2_entregas
                  WHEN (m.entregas = 3) THEN uv.rm_motorista_valor_as_3_entregas
                  WHEN (m.entregas > 3) THEN uv.rm_motorista_valor_as_maior_3_entregas
                  ELSE (0)::real
              END
              WHEN ((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
              CASE
                  WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                  WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                  WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                  WHEN (m.entregas > 3) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                  ELSE (0)::real
              END
              WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
              CASE
                  WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                  WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                  WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                  WHEN (m.entregas > 2) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                  ELSE (0)::real
              END
              ELSE (0)::real
          END AS valor_as,
      ((
          CASE
              WHEN ((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot)
              WHEN ((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
              WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
              ELSE (0)::real
          END +
          CASE
              WHEN (((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota)::double precision) / (m.fator)::double precision)
              WHEN (((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision)
              WHEN (((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision)
              ELSE (0)::double precision
          END) +
          CASE
              WHEN (((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text = 'AS'::text)) THEN
              CASE
                  WHEN ((m.entregas = 1) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_1_entrega
                  WHEN ((m.entregas = 2) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_2_entregas
                  WHEN ((m.entregas = 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_3_entregas
                  WHEN ((m.entregas > 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_maior_3_entregas
                  WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_motorista_valor_as_recarga
                  ELSE (0)::real
              END
              WHEN (((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) THEN
              CASE
                  WHEN ((m.entregas = 1) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_1_entrega
                  WHEN ((m.entregas = 2) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_2_entregas
                  WHEN ((m.entregas = 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_3_entregas
                  WHEN ((m.entregas > 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                  WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_ajudante_valor_as_recarga
                  ELSE (0)::real
              END
              WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
              CASE
                  WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                  WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                  WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                  WHEN (m.entregas > 2) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                  WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_ajudante_valor_as_recarga
                  ELSE (0)::real
              END
              ELSE (0)::real
          END) AS valor
     FROM (((((((((view_mapa_colaborador vmc
       JOIN colaborador c ON ((vmc.cpf = c.cpf)))
       JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = c.cod_empresa))))
       JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
       JOIN unidade_metas um ON ((um.cod_unidade = m.cod_unidade)))
       JOIN view_valor_cx_unidade ON ((view_valor_cx_unidade.cod_unidade = m.cod_unidade)))
       JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
       JOIN unidade_funcao_produtividade ufp ON (((ufp.cod_unidade = c.cod_unidade) AND (ufp.cod_unidade = m.cod_unidade))))
       LEFT JOIN unidade_valores_rm uv ON ((uv.cod_unidade = m.cod_unidade)))
       LEFT JOIN ( SELECT t.mapa AS tracking_mapa,
              t."código_transportadora" AS cod_transportadora,
              sum(
                  CASE
                      WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                      ELSE 0
                  END) AS apontamentos_ok,
              count(t.disp_apont_cadastrado) AS total_apontamentos
             FROM (tracking t
               JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
            GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND (tracking.cod_transportadora = m.cod_unidade))))
    WHERE (m.fator > (0)::double precision);
  COMMENT ON VIEW view_produtividade_extrato IS 'View que calcula a produtividade individual';

  -- VIEW UTILIZADA PARA FAZER O CALCULO DE PREVISÃO DE TROCA DOS PNEUS
  -- DEPRECATED A PARTIR DA v2_39
  CREATE VIEW view_analise_pneus AS SELECT p.codigo AS "COD PNEU",
      p.status AS "STATUS PNEU",
      p.cod_unidade,
      map.nome AS "MARCA",
      mp.nome AS "MODELO",
      ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
      dados.qt_afericoes AS "QTD DE AFERIÇÕES",
      to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
      to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
      dados.total_dias AS "DIAS ATIVO",
      round(
          CASE
              WHEN (dados.total_dias > 0) THEN (dados.total_km / (dados.total_dias)::numeric)
              ELSE NULL::numeric
          END) AS "MÉDIA KM POR DIA",
      p.altura_sulco_interno,
      p.altura_sulco_central_interno,
      p.altura_sulco_central_externo,
      p.altura_sulco_externo,
      round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
      round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
      round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
      round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
      round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
      trunc(
          CASE
              WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision)
              ELSE (0)::double precision
          END) AS "DIAS RESTANTES",
          CASE
              WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
              ELSE NULL::date
          END AS "PREVISÃO DE TROCA"
     FROM (((((pneu p
       JOIN ( SELECT av.cod_pneu,
              av.cod_unidade,
              count(av.altura_sulco_central_interno) AS qt_afericoes,
              (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date AS primeira_afericao,
              (max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date AS ultima_afericao,
              ((max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date - (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date) AS total_dias,
              max(total_km.total_km) AS total_km,
              max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) AS maior_sulco,
              min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) AS menor_sulco,
              (max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno))) AS sulco_gasto,
                  CASE
                      WHEN (
                      CASE
                          WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                          WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                          ELSE NULL::real
                      END < (0)::double precision) THEN (0)::real
                      ELSE
                      CASE
                          WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_descarte)
                          WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)) - erp.sulco_minimo_recapagem)
                          ELSE NULL::real
                      END
                  END AS sulco_restante,
                  CASE
                      WHEN (((max(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date - (min(a.data_hora AT TIME ZONE (SELECT timezone FROM func_get_time_zone_unidade(av.cod_unidade))))::date) > 0) THEN (((max(total_km.total_km))::double precision / max(GREATEST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno))) - min(LEAST(av.altura_sulco_central_interno, av.altura_sulco_externo, av.altura_sulco_interno)))
                      ELSE (0)::double precision
                  END AS km_por_mm
             FROM ((((afericao_valores av
               JOIN afericao a ON ((a.codigo = av.cod_afericao)))
               JOIN pneu p_1 ON (((((p_1.codigo)::text = (av.cod_pneu)::text) AND (p_1.cod_unidade = av.cod_unidade)) AND ((p_1.status)::text = 'EM_USO'::text))))
               JOIN empresa_restricao_pneu erp ON ((erp.cod_unidade = av.cod_unidade)))
               JOIN ( SELECT total_km_rodado.cod_pneu,
                      total_km_rodado.cod_unidade,
                      sum(total_km_rodado.km_rodado) AS total_km
                     FROM ( SELECT av_1.cod_pneu,
                              av_1.cod_unidade,
                              a_1.placa_veiculo,
                              (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
                             FROM (afericao_valores av_1
                               JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
                            GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo) total_km_rodado
                    GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km ON ((((total_km.cod_pneu)::text = (av.cod_pneu)::text) AND (total_km.cod_unidade = av.cod_unidade))))
            GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, erp.sulco_minimo_descarte, erp.sulco_minimo_recapagem) dados ON ((((dados.cod_pneu)::text = (p.codigo)::text) AND (dados.cod_unidade = p.cod_unidade))))
       JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
       JOIN unidade u ON ((u.codigo = p.cod_unidade)))
       JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
       JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)));
  COMMENT ON VIEW view_analise_pneus IS 'View utilizada para gerar dados de uso sobre os pneus, esses dados são usados para gerar relatórios';

  -- VIEW UTILIZADA PARA LISTAR OS MAPAS E INDICADORES POR COLABORADOR
  CREATE VIEW view_extrato_indicadores AS SELECT dados.cod_empresa,
      dados.cod_regional,
      dados.cod_unidade,
      dados.cod_equipe,
      dados.cpf,
      dados.nome,
      dados.equipe,
      dados.funcao,
      dados.data,
      dados.mapa,
      dados.placa,
      dados.cxcarreg,
      dados.qthlcarregados,
      dados.qthlentregues,
      dados.qthldevolvidos,
      dados.resultado_devolucao_hectolitro,
      dados.qtnfcarregadas,
      dados.qtnfentregues,
      dados.qtnfdevolvidas,
      dados.resultado_devolucao_nf,
      dados.entregascompletas,
      dados.entregasnaorealizadas,
      dados.entregasparciais,
      dados.entregas_carregadas,
      dados.resultado_devolucao_pdv,
      dados.kmprevistoroad,
      dados.kmsai,
      dados.kmentr,
      dados.km_percorrido,
      dados.resultado_dispersao_km,
      dados.hrsai,
      dados.hr_sai,
      dados.hrentr,
      dados.hr_entr,
      dados.tempo_rota,
      dados.tempoprevistoroad,
      dados.resultado_tempo_rota_segundos,
      dados.resultado_dispersao_tempo,
      dados.resultado_tempo_interno_segundos,
      dados.tempo_interno,
      dados.hrmatinal,
      dados.resultado_tempo_largada_segundos,
      dados.tempo_largada,
      dados.total_tracking,
      dados.apontamentos_ok,
      dados.apontamentos_nok,
      dados.resultado_tracking,
      dados.meta_tracking,
      dados.meta_tempo_rota_mapas,
      dados.meta_caixa_viagem,
      dados.meta_dev_hl,
      dados.meta_dev_pdv,
      dados.meta_dev_nf,
      dados.meta_dispersao_km,
      dados.meta_dispersao_tempo,
      dados.meta_jornada_liquida_mapas,
      dados.meta_raio_tracking,
      dados.meta_tempo_interno_mapas,
      dados.meta_tempo_largada_mapas,
      dados.meta_tempo_rota_horas,
      dados.meta_tempo_interno_horas,
      dados.meta_tempo_largada_horas,
      dados.meta_jornada_liquida_horas,
          CASE
              WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_dev_pdv,
          CASE
              WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_dev_hl,
          CASE
              WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_dev_nf,
          CASE
              WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_dispersao_tempo,
          CASE
              WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_dispersao_km,
          CASE
              WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_tempo_interno,
          CASE
              WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_tempo_rota,
          CASE
              WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_tempo_largada,
          CASE
              WHEN (((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) + dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_jornada,
          CASE
              WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 'SIM'::text
              ELSE 'NÃO'::text
          END AS bateu_tracking,
          CASE
              WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 1
              ELSE 0
          END AS gol_dev_pdv,
          CASE
              WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 1
              ELSE 0
          END AS gol_dev_hl,
          CASE
              WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 1
              ELSE 0
          END AS gol_dev_nf,
          CASE
              WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 1
              ELSE 0
          END AS gol_dispersao_tempo,
          CASE
              WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 1
              ELSE 0
          END AS gol_dispersao_km,
          CASE
              WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas)::double precision) THEN 1
              ELSE 0
          END AS gol_tempo_interno,
          CASE
              WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas)::double precision) THEN 1
              ELSE 0
          END AS gol_tempo_rota,
          CASE
              WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas)::double precision) THEN 1
              ELSE 0
          END AS gol_tempo_largada,
          CASE
              WHEN (((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) + dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas)::double precision) THEN 1
              ELSE 0
          END AS gol_jornada,
          CASE
              WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 1
              ELSE 0
          END AS gol_tracking
     FROM ( SELECT u.cod_empresa,
              u.cod_regional,
              u.codigo AS cod_unidade,
              e.codigo AS cod_equipe,
              c.cpf,
              c.nome,
              e.nome AS equipe,
              f.nome AS funcao,
              m.data,
              m.mapa,
              m.placa,
              m.cxcarreg,
              m.qthlcarregados,
              m.qthlentregues,
              trunc(((m.qthlcarregados - m.qthlentregues))::numeric, 2) AS qthldevolvidos,
              trunc((
                  CASE
                      WHEN (m.qthlcarregados > (0)::double precision) THEN ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                      ELSE (0)::real
                  END)::numeric, 4) AS resultado_devolucao_hectolitro,
              m.qtnfcarregadas,
              m.qtnfentregues,
              (m.qtnfcarregadas - m.qtnfentregues) AS qtnfdevolvidas,
              trunc((
                  CASE
                      WHEN (m.qtnfcarregadas > 0) THEN (((m.qtnfcarregadas - m.qtnfentregues))::double precision / (m.qtnfcarregadas)::real)
                      ELSE (0)::double precision
                  END)::numeric, 4) AS resultado_devolucao_nf,
              m.entregascompletas,
              m.entregasnaorealizadas,
              m.entregasparciais,
              (m.entregascompletas + m.entregasnaorealizadas) AS entregas_carregadas,
              trunc((
                  CASE
                      WHEN (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais) > 0) THEN (((m.entregasnaorealizadas)::real + (m.entregasparciais)::double precision) / (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais))::double precision)
                      ELSE (0)::double precision
                  END)::numeric, 4) AS resultado_devolucao_pdv,
              m.kmprevistoroad,
              m.kmsai,
              m.kmentr,
              (m.kmentr - m.kmsai) AS km_percorrido,
                  CASE
                      WHEN (m.kmprevistoroad > (0)::double precision) THEN trunc((((((m.kmentr - m.kmsai))::double precision - m.kmprevistoroad) / m.kmprevistoroad))::numeric, 4)
                      ELSE NULL::numeric
                  END AS resultado_dispersao_km,
              to_char(m.hrsai, 'DD/MM/YYYY HH24:MI:SS'::text) AS hrsai,
              m.hrsai AS hr_sai,
              to_char(m.hrentr, 'DD/MM/YYYY HH24:MI:SS'::text) AS hrentr,
              m.hrentr AS hr_entr,
              to_char((m.hrentr - m.hrsai), 'HH24:MI:SS'::text) AS tempo_rota,
              date_part('epoch'::text, m.tempoprevistoroad) AS tempoprevistoroad,
              date_part('epoch'::text, (m.hrentr - m.hrsai)) AS resultado_tempo_rota_segundos,
                  CASE
                      WHEN (date_part('epoch'::text, m.tempoprevistoroad) > (0)::double precision) THEN ((date_part('epoch'::text, (m.hrentr - m.hrsai)) - date_part('epoch'::text, m.tempoprevistoroad)) / date_part('epoch'::text, m.tempoprevistoroad))
                      ELSE (0)::double precision
                  END AS resultado_dispersao_tempo,
              date_part('epoch'::text, m.tempointerno) AS resultado_tempo_interno_segundos,
              m.tempointerno AS tempo_interno,
              m.hrmatinal,
              date_part('epoch'::text,
                  CASE
                      WHEN ((m.hrsai)::time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                      ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                  END) AS resultado_tempo_largada_segundos,
                  CASE
                      WHEN ((m.hrsai)::time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                      ELSE ((m.hrsai - (m.hrmatinal)::interval))::time without time zone
                  END AS tempo_largada,
              COALESCE(tracking.total_apontamentos, (0)::bigint) AS total_tracking,
              COALESCE(tracking.apontamentos_ok, (0)::bigint) AS apontamentos_ok,
              COALESCE((tracking.total_apontamentos - tracking.apontamentos_ok), (0)::bigint) AS apontamentos_nok,
                  CASE
                      WHEN (tracking.total_apontamentos > 0) THEN (tracking.apontamentos_ok / tracking.total_apontamentos)
                      ELSE (0)::bigint
                  END AS resultado_tracking,
              um.meta_tracking,
              um.meta_tempo_rota_mapas,
              um.meta_caixa_viagem,
              um.meta_dev_hl,
              um.meta_dev_pdv,
              um.meta_dev_nf,
              um.meta_dispersao_km,
              um.meta_dispersao_tempo,
              um.meta_jornada_liquida_mapas,
              um.meta_raio_tracking,
              um.meta_tempo_interno_mapas,
              um.meta_tempo_largada_mapas,
              to_seconds((um.meta_tempo_rota_horas)::text) AS meta_tempo_rota_horas,
              to_seconds((um.meta_tempo_interno_horas)::text) AS meta_tempo_interno_horas,
              to_seconds((um.meta_tempo_largada_horas)::text) AS meta_tempo_largada_horas,
              to_seconds((um.meta_jornada_liquida_horas)::text) AS meta_jornada_liquida_horas
             FROM (((((((((view_mapa_colaborador vmc
               JOIN colaborador c ON (((c.cpf = vmc.cpf) AND (c.cod_unidade = vmc.cod_unidade))))
               JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
               JOIN unidade u ON ((u.codigo = m.cod_unidade)))
               JOIN empresa em ON ((em.codigo = u.cod_empresa)))
               JOIN regional r ON ((r.codigo = u.cod_regional)))
               JOIN unidade_metas um ON ((um.cod_unidade = u.codigo)))
               JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
               JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = em.codigo))))
               LEFT JOIN ( SELECT t.mapa AS tracking_mapa,
                      t."código_transportadora" AS tracking_unidade,
                      count(t.disp_apont_cadastrado) AS total_apontamentos,
                      sum(
                          CASE
                              WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                              ELSE 0
                          END) AS apontamentos_ok
                     FROM (tracking t
                       JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
                    GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND (tracking.tracking_unidade = m.cod_unidade))))
            ORDER BY m.data) dados;

  -- View que lista cada mapa realizado por um colaborador e o intervalo realizado para aquele mapa
  CREATE VIEW view_intervalo_mapa_colaborador AS SELECT view_extrato_mapas_versus_intervalos.data,
      view_extrato_mapas_versus_intervalos.mapa,
      view_extrato_mapas_versus_intervalos.cod_unidade,
      view_extrato_mapas_versus_intervalos.cpf_motorista AS cpf,
      view_extrato_mapas_versus_intervalos.inicio_intervalo_mot AS inicio_intervalo,
      view_extrato_mapas_versus_intervalos.fim_intervalo_mot AS fim_intervalo,
      view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_mot AS tempo_decorrido_minutos,
      view_extrato_mapas_versus_intervalos.mot_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
     FROM view_extrato_mapas_versus_intervalos
  UNION
   SELECT view_extrato_mapas_versus_intervalos.data,
      view_extrato_mapas_versus_intervalos.mapa,
      view_extrato_mapas_versus_intervalos.cod_unidade,
      view_extrato_mapas_versus_intervalos.cpf_aj1 AS cpf,
      view_extrato_mapas_versus_intervalos.inicio_intervalo_aj1 AS inicio_intervalo,
      view_extrato_mapas_versus_intervalos.fim_intervalo_aj1 AS fim_intervalo,
      view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj1 AS tempo_decorrido_minutos,
      view_extrato_mapas_versus_intervalos.aj1_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
     FROM view_extrato_mapas_versus_intervalos
  UNION
   SELECT view_extrato_mapas_versus_intervalos.data,
      view_extrato_mapas_versus_intervalos.mapa,
      view_extrato_mapas_versus_intervalos.cod_unidade,
      view_extrato_mapas_versus_intervalos.cpf_aj2 AS cpf,
      view_extrato_mapas_versus_intervalos.inicio_intervalo_aj2 AS inicio_intervalo,
      view_extrato_mapas_versus_intervalos.fim_intervalo_aj2 AS fim_intervalo,
      view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj2 AS tempo_decorrido_minutos,
      view_extrato_mapas_versus_intervalos.aj2_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
     FROM view_extrato_mapas_versus_intervalos;

  -- View que detalha a vida atual de um pneu
  CREATE VIEW view_pneu_analise_vida_atual AS SELECT p.codigo AS "COD PNEU",
      (p.valor + sum(acumulado.valor_banda)) AS valor_acumulado,
      sum(acumulado.total_km_percorrido_vida) AS km_acumulado,
      p.vida_atual AS "VIDA ATUAL",
      p.status AS "STATUS PNEU",
      p.cod_unidade,
      p.valor AS valor_pneu,
          CASE
              WHEN (dados.vida = 1) THEN dados.valor_pneu
              ELSE dados.valor_banda
          END AS valor_vida_atual,
      map.nome AS "MARCA",
      mp.nome AS "MODELO",
      ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
      dados.qt_afericoes AS "QTD DE AFERIÇÕES",
      to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
      to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
      dados.total_dias AS "DIAS ATIVO",
      round(
          CASE
              WHEN (dados.total_dias > 0) THEN (dados.total_km_percorrido_vida / (dados.total_dias)::numeric)
              ELSE NULL::numeric
          END) AS "MÉDIA KM POR DIA",
      round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
      round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
      round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
      round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
      round((dados.valor_por_km_vida_atual)::numeric, 2) AS "VALOR POR KM",
      round((
          CASE
              WHEN (sum(acumulado.total_km_percorrido_vida) > (0)::numeric) THEN ((p.valor + sum(acumulado.valor_banda)) / (sum(acumulado.total_km_percorrido_vida))::double precision)
              ELSE (0)::double precision
          END)::numeric, 2) AS "VALOR POR KM ACUMULADO",
      round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
      trunc(
          CASE
              WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision)
              ELSE (0)::double precision
          END) AS "DIAS RESTANTES",
          CASE
              WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
              ELSE NULL::date
          END AS "PREVISÃO DE TROCA",
          CASE
              WHEN (p.vida_atual = p.vida_total) THEN 'DESCARTE'::text
              ELSE 'ANÁLISE'::text
          END AS "DESTINO"
     FROM ((((((pneu p
       JOIN ( SELECT view_pneu_analise_vidas.cod_pneu,
              view_pneu_analise_vidas.vida,
              view_pneu_analise_vidas.status,
              view_pneu_analise_vidas.valor_pneu,
              view_pneu_analise_vidas.valor_banda,
              view_pneu_analise_vidas.cod_unidade,
              view_pneu_analise_vidas.qt_afericoes,
              view_pneu_analise_vidas.primeira_afericao,
              view_pneu_analise_vidas.ultima_afericao,
              view_pneu_analise_vidas.total_dias,
              view_pneu_analise_vidas.total_km_percorrido_vida,
              view_pneu_analise_vidas.maior_sulco,
              view_pneu_analise_vidas.menor_sulco,
              view_pneu_analise_vidas.sulco_gasto,
              view_pneu_analise_vidas.sulco_restante,
              view_pneu_analise_vidas.km_por_mm,
              view_pneu_analise_vidas.valor_por_km_vida_atual
             FROM view_pneu_analise_vidas) dados ON (((((dados.cod_pneu)::text = (p.codigo)::text) AND (dados.cod_unidade = p.cod_unidade)) AND (dados.vida = p.vida_atual))))
       JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
       JOIN unidade u ON ((u.codigo = p.cod_unidade)))
       JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
       JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)))
       JOIN view_pneu_analise_vidas acumulado ON ((((acumulado.cod_pneu)::text = (p.codigo)::text) AND (acumulado.cod_unidade = p.cod_unidade))))
    GROUP BY p.codigo, p.cod_unidade, dados.valor_banda, dados.valor_pneu, map.nome, mp.nome, dp.largura, dp.altura, dp.aro, dados.qt_afericoes, dados.primeira_afericao, dados.ultima_afericao, dados.total_dias, dados.total_km_percorrido_vida, dados.maior_sulco, dados.menor_sulco, dados.sulco_gasto, dados.km_por_mm, dados.valor_por_km_vida_atual, dados.sulco_restante, dados.vida
    ORDER BY
          CASE
              WHEN (((dados.total_km_percorrido_vida > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km_percorrido_vida / (dados.total_dias)::numeric))::double precision))::integer + ('now'::text)::date)
              ELSE NULL::date
          END;
  -- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##########################    SESSION FUNCIONTION                   ####################################
-- ########################################################################################################
-- ########################################################################################################

DROP FUNCTION func_relatorio_checklist_resumo_realizados(bigint,date,date,text);
CREATE OR REPLACE FUNCTION func_relatorio_checklist_resumo_realizados(f_cod_unidade bigint, f_data_inicial date, f_data_final date, f_placa_veiculo text)
  RETURNS TABLE("DATA" TEXT, "HORA" TEXT, "COLABORADOR" text, "PLACA" text, "KM" bigint,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" bigint, "TIPO" text, "TOTAL DE PERGUNTAS" bigint, "TOTAL NOK" bigint,
                "PRIORIDADE BAIXA" bigint, "PRIORIDADE ALTA" bigint, "PRIORIDADE CRÍTICA" bigint)
LANGUAGE SQL
AS $$
SELECT
  to_char((C.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade)))::DATE, 'DD/MM/YYYY'),
  to_char((C.DATA_HORA AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(f_cod_unidade)))::TIME, 'HH24:MI'),
  CO.NOME,
  C.PLACA_VEICULO,
  C.km_veiculo,
  C.tempo_realizacao/1000,
  case when C.tipo = 'S' then 'Saída' else 'Retorno' end,
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
WHERE c.cod_unidade = f_cod_unidade and c.placa_veiculo like f_placa_veiculo and c.data_hora::date BETWEEN f_data_inicial and f_data_final
-- WHERE c.cod_unidade = 7 and c.placa_veiculo like '%' and c.data_hora::date BETWEEN '2017-08-22' and '2017-08-22'
GROUP BY c.data_hora, 2, 3, 4, 5, 6, 7, 8
ORDER BY c.data_hora DESC
$$;

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
  WHERE I.COD_UNIDADE = f_cod_unidade and ((i.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))::date BETWEEN f_data_inicial and f_data_final
  or (i.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))::date BETWEEN f_data_inicial and f_data_final) and i.CPF_COLABORADOR::TEXT LIKE f_cpf
ORDER BY I.DATA_HORA_INICIO, C.NOME
$$;

END TRANSACTION;