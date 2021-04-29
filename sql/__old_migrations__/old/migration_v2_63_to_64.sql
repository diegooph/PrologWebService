BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ##########  CRIA FUNCTION ESTRATIFICANDO AS OSs DO CHECKLIST  ##########################################
-- ########################################################################################################
-- ########################################################################################################
create or replace function func_relatorio_estratificacao_os(f_cod_unidade bigint, f_placa_veiculo text, f_data_inicial date, f_data_final date, f_zone_id text, f_status_os text, f_status_item text)
  returns TABLE(os bigint, "ABERTURA OS" text, "DATA LIMITE CONSERTO" text, "STATUS OS" text, "PLACA" text, "PERGUNTA" text, "ALTERNATIVA" text, "PRIORIDADE" text, "PRAZO EM HORAS" integer, "DESCRIÇÃO" text, "STATUS ITEM" text, "DATA CONSERTO" text, "MECÂNICO" text, "DESCRIÇÃO CONSERTO" text, "TEMPO DE CONSERTO" bigint, "KM ABERTURA" bigint, "KM FECHAMENTO" bigint, "KM PERCORRIDO" text, "MOTORISTA" text, "TIPO DO CHECKLIST" text)
language sql
as $$
SELECT            cod_os                                                                    AS OS,
                  to_char(data_hora, 'DD/MM/YYYY HH24:MI')                                  AS "ABERTURA OS",
                  to_char(data_hora + (prazo || ' hour') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS "DATA LIMITE CONSERTO",
  (CASE WHEN status_os = 'A'
   THEN 'ABERTA'
   ELSE 'FECHADA' END  )                                                      AS "STATUS OS",
  placa_veiculo                                                             AS "PLACA",
  pergunta                                                                  AS "PERGUNTA",
  alternativa                                                               AS "ALTERNATIVA",
  prioridade                                                                AS "PRIORIDADE",
  prazo                                                                     AS "PRAZO EM HORAS",
  resposta                                                                  AS "DESCRIÇÃO",
  CASE WHEN status_ITEM = 'P'
THEN 'PENDENTE'
ELSE 'RESOLVIDO' END                                                      AS "STATUS ITEM",
  to_char(data_hora_conserto, 'DD/MM/YYYY HH24:MI')                           AS "DATA CONSERTO",
  nome_mecanico                                                            AS "MECÂNICO",
  feedback_conserto                                                         AS "DESCRIÇÃO CONSERTO",
  --                 PASSAR PRA MINUTOS
  tempo_realizacao / 60                                                     AS "TEMPO DE CONSERTO",
  km                                                                        AS "KM ABERTURA",
  km_fechamento                                                             AS "KM FECHAMENTO",
  coalesce((km_fechamento - km) :: TEXT, '-')                               AS "KM PERCORRIDO",
  nome_realizador_checklist AS "MOTORISTA",
  CASE WHEN tipo_checklist = 'S' THEN 'SAÍDA' ELSE 'RETORNO' END AS "TIPO DO CHECKLIST"
FROM estratificacao_os
WHERE cod_unidade = f_cod_unidade AND placa_veiculo LIKE f_placa_veiculo
AND (data_hora::DATE BETWEEN (f_data_inicial AT TIME ZONE f_zone_id)
AND (f_data_final AT TIME ZONE f_zone_id))
AND status_os LIKE f_status_os AND
status_item LIKE f_status_item
ORDER BY OS, "PRAZO EM HORAS";
$$;
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION ;