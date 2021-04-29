BEGIN TRANSACTION ;

-- ########################################################################################################
-- ########################################################################################################
-- ######################## FUNÇÃO PARA BUSCAR A LISTAGEM DE MODELOS DE CHECKLIST #########################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION func_checklist_get_listagem_modelos_checklist(f_cod_unidade bigint, f_cargos text);
CREATE OR REPLACE FUNCTION func_checklist_get_listagem_modelos_checklist(f_cod_unidade bigint, f_cargos text)
  RETURNS TABLE("MODELO" text, "COD_MODELO" bigint, "COD_UNIDADE" bigint, "NOME_CARGO" text, "TIPO_VEICULO" text,
    "TOTAL_PERGUNTAS" bigint, "STATUS_ATIVO" boolean)
LANGUAGE SQL
AS $$
SELECT CM.NOME AS MODELO,
       CM.CODIGO AS COD_MODELO,
       CM.COD_UNIDADE AS COD_UNIDADE,
       F.NOME AS NOME_CARGO,
       VT.NOME AS TIPO_VEICULO,
       COUNT(CP.CODIGO) AS TOTAL_PERGUNTAS,
       CM.STATUS_ATIVO as STATUS_ATIVO
FROM CHECKLIST_MODELO CM
  JOIN CHECKLIST_PERGUNTAS CP ON CM.COD_UNIDADE = CP.COD_UNIDADE
                                 AND CM.CODIGO = CP.COD_CHECKLIST_MODELO
                                 AND CP.STATUS_ATIVO = TRUE
  LEFT JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.COD_UNIDADE = CMF.COD_UNIDADE
                                      AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO
  LEFT JOIN UNIDADE_FUNCAO UF ON CMF.COD_UNIDADE = UF.COD_UNIDADE
                            AND CMF.COD_FUNCAO = UF.COD_FUNCAO
  LEFT JOIN FUNCAO F ON UF.COD_FUNCAO = F.CODIGO
  LEFT JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CM.COD_UNIDADE = CMVT.COD_UNIDADE
                                             AND CM.CODIGO = CMVT.COD_MODELO
  LEFT JOIN  VEICULO_TIPO VT ON CMVT.COD_UNIDADE = VT.COD_UNIDADE
                          AND CMVT.COD_TIPO_VEICULO = VT.CODIGO
WHERE CM.COD_UNIDADE = f_cod_unidade
      AND CMF.COD_FUNCAO::TEXT LIKE f_cargos
GROUP BY CM.NOME, CM.CODIGO, CM.COD_UNIDADE, F.NOME, VT.NOME
ORDER BY CM.STATUS_ATIVO DESC, CM.CODIGO ASC;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ################### ALTERA TABELA DE ITENS DE SERVICO PARA TER UM CÓDIGO ÚNICO #########################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN CODIGO BIGSERIAL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD CONSTRAINT UNIQUE_CHECKLIST_ORDEM_SERVICO_ITENS UNIQUE (CODIGO);

-- ALTERA ESTRATIFICACAO OS PARA CONTER O CODIGO UNICO DO ITEM DA OS
create or replace view estratificacao_os as
  SELECT os.codigo AS cod_os,
    os.cod_unidade,
    os.status AS status_os,
    os.cod_checklist,
    cp.codigo AS cod_pergunta,
    cp.ordem AS ordem_pergunta,
    cp.pergunta,
    cp.single_choice,
    NULL::unknown AS url_imagem,
    cp.prioridade,
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
    cosi.codigo
   FROM ((((((((checklist c
     JOIN veiculo v ON (((v.placa)::text = (c.placa_veiculo)::text)))
     JOIN checklist_ordem_servico os ON (((c.codigo = os.cod_checklist) AND (c.cod_unidade = os.cod_unidade))))
     JOIN checklist_ordem_servico_itens cosi ON (((os.codigo = cosi.cod_os) AND (os.cod_unidade = cosi.cod_unidade))))
     JOIN checklist_perguntas cp ON (((cp.cod_unidade = os.cod_unidade) AND (cp.codigo = cosi.cod_pergunta) AND (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
     JOIN prioridade_pergunta_checklist ppc ON (((ppc.prioridade)::text = (cp.prioridade)::text)))
     JOIN checklist_alternativa_pergunta cap ON (((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo) AND (cap.cod_pergunta = cp.codigo) AND (cap.codigo = cosi.cod_alternativa))))
     JOIN checklist_respostas cr ON (((c.cod_unidade = cr.cod_unidade) AND (cr.cod_checklist_modelo = c.cod_checklist_modelo) AND (cr.cod_checklist = c.codigo) AND (cr.cod_pergunta = cp.codigo) AND (cr.cod_alternativa = cap.codigo))))
     LEFT JOIN colaborador co ON ((co.cpf = cosi.cpf_mecanico)));
comment on view estratificacao_os
is 'View que compila as informações das OS e seus itens';
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION ;