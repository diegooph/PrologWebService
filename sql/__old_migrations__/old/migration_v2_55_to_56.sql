-- Essa migração deve ser executada quando o WS versão 56 for publicado.
BEGIN TRANSACTION;
-- ########################################################################################################
-- ########################################################################################################
-- #######################      INSERE PERMISSÕES DE RECAPADORAS NO BD      ###############################
-- ########################################################################################################
-- ########################################################################################################
INSERT INTO FUNCAO_PROLOG_V11 VALUES (130, 'Recapadora - Cadastrar', 1);
INSERT INTO FUNCAO_PROLOG_V11 VALUES (131, 'Recapadora - Visualizar', 1);
INSERT INTO FUNCAO_PROLOG_V11 VALUES (132, 'Recapadora - Editar', 1);

-- ########################################################################################################
-- ########################################################################################################
-- #######################   ADICIONA CONSTRAINT NA TABELA RECAPADORA   ###################################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE RECAPADORA ADD CONSTRAINT NOME_RECAPADORA_UNIQUE UNIQUE (NOME, COD_EMPRESA);
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- #######################  RECRIA FUNCTION PARA CORRIGIR PROBLEMA COM DATAS NULAS ########################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION func_relatorio_intervalo_folha_de_ponto(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text);
CREATE OR REPLACE FUNCTION func_relatorio_intervalo_folha_de_ponto(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text)
  RETURNS TABLE(
    cpf_colaborador bigint,
    nome_colaborador text,
    cod_tipo_intervalo bigint,
    cod_tipo_intervalo_por_unidade bigint,
    data_hora_inicio timestamp without time zone,
    data_hora_fim timestamp without time zone,
    diferenca_marcacoes_segundos double PRECISION,
    TROCOU_DIA BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  CPF_COLABORADOR,
  C.NOME AS NOME_COLABORADOR,
  COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO,
  COD_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
  DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE,
  DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE,
  EXTRACT(EPOCH FROM (DATA_HORA_FIM - DATA_HORA_INICIO)) AS DIFERENCA_MARCACOES_SEGUNDOS,
  DATA_HORA_INICIO IS NOT NULL AND DATA_HORA_FIM IS NOT NULL AND (DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE)::DATE != (DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE)::DATE AS TROCOU_DIA
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  ((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL AND (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
  OR
  ((F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
ORDER BY F.CPF_COLABORADOR, COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) ASC;
$$;

END TRANSACTION;