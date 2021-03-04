-- Essa migração deve ser executada quando o WS versão 57 for publicado.
BEGIN TRANSACTION;
-- ########################################################################################################
-- ########################################################################################################
-- #######################  CRIA RELATÓRIO DE PRODUTIVIDADE DO COLABORADOR  ###############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_relatorio_produtividade_remuneracao_acumulada_colaborador(f_cod_unidade bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    "CPF_COLABORADOR" BIGINT,
    "NOME_COLABORADOR" TEXT,
    "DATA" DATE,
    "CAIXAS_ENTREGUES" NUMERIC,
    "FATOR" REAL,
    "VALOR" DOUBLE PRECISION)
LANGUAGE SQL
AS $$
SELECT VPE.CPF,
  VPE.NOME_COLABORADOR,
  VPE.DATA,
  ROUND(VPE.CXENTREG::NUMERIC, 2),
  VPE.FATOR,
  VPE.VALOR
FROM VIEW_PRODUTIVIDADE_EXTRATO AS VPE
  WHERE VPE.COD_UNIDADE = f_cod_unidade
        AND CASE WHEN f_cpf_colaborador IS NULL THEN TRUE ELSE VPE.CPF = f_cpf_colaborador END
        AND VPE.DATA >= f_data_inicial
        AND VPE.DATA <= f_data_final
ORDER BY VPE.CPF, VPE.DATA ASC;
$$;
COMMENT ON FUNCTION func_relatorio_produtividade_remuneracao_acumulada_colaborador(f_cod_unidade bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date)
IS 'Busca a produtividade do colaborador para um período';
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- #######################  CORRIGE COLUNAS DA TABELA DE ESCALA_DIARIA  ###################################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE ESCALA_DIARIA ALTER COLUMN MAPA DROP NOT NULL;
ALTER TABLE ESCALA_DIARIA ALTER COLUMN DATA_HORA_CADASTRO SET NOT NULL;
ALTER TABLE ESCALA_DIARIA ALTER COLUMN DATA_HORA_ULTIMA_ALTERACAO SET NOT NULL;
ALTER TABLE ESCALA_DIARIA ALTER COLUMN CPF_CADASTRO SET NOT NULL;
ALTER TABLE ESCALA_DIARIA ALTER COLUMN CPF_ULTIMA_ALTERACAO SET NOT NULL;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############################### ATUALIZA NOME DAS VIDAS DOS PNEUS ######################################
-- ########################################################################################################
-- ########################################################################################################
UPDATE public.pneu_vida_nomenclatura SET nome = 'Primeira Vida' WHERE cod_vida = 1;
UPDATE public.pneu_vida_nomenclatura SET nome = '2ª Vida (1ª Recapagem)' WHERE cod_vida = 2;
UPDATE public.pneu_vida_nomenclatura SET nome = '3ª Vida (2ª Recapagem)' WHERE cod_vida = 3;
UPDATE public.pneu_vida_nomenclatura SET nome = '4ª Vida (3ª Recapagem)' WHERE cod_vida = 4;
UPDATE public.pneu_vida_nomenclatura SET nome = '5ª Vida (4ª Recapagem)' WHERE cod_vida = 5;
UPDATE public.pneu_vida_nomenclatura SET nome = '6ª Vida (5ª Recapagem)' WHERE cod_vida = 6;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############################## CORRIGE RELATÓRIO DAS ESCALAS DIÁRIAS / INTERVALOS ######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_relatorio_intervalo_escala_diaria(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text)
  RETURNS TABLE("UNIDADE" text, "PLACA VEÍCULO" text, "CÓDIGO ROTA (MAPA)" bigint, "DATA" text, "TIPO DE INTERVALO" text, "MOTORISTA" text, "INÍCIO INTERVALO MOTORISTA" text, "FIM INTERVALO MOTORISTA" text, "AJUDANTE 1" text, "INÍCIO INTERVALO AJUDANTE 1" text, "FIM INTERVALO AJUDANTE 1" text, "AJUDANTE 2" text, "INÍCIO INTERVALO AJUDANTE 2" text, "FIM INTERVALO AJUDANTE 2" text)
LANGUAGE SQL
AS $$
WITH TABLE_INTERVALOS AS (
    SELECT * FROM FUNC_INTERVALOS_AGRUPADOS(f_cod_unidade, NULL, f_cod_tipo_intervalo) F
    WHERE (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE >= f_data_inicial
      AND (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE <= f_data_final
)

SELECT        (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = f_cod_unidade),
              ED.PLACA,
              ED.MAPA,
              TO_CHAR(ED.DATA, 'DD/MM/YYYY'),
              (SELECT IT.NOME FROM INTERVALO_TIPO IT WHERE IT.CODIGO = f_cod_tipo_intervalo),

              -- MOTORISTA
              (CASE WHEN CM.CPF IS NULL THEN 'MOTORISTA NÃO CADASTRADO' ELSE CM.NOME END) AS NOME_MOTORISTA,
              CASE
                WHEN INT_MOT.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_MOT.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_MOT.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_MOT.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,

              -- AJUDANTE 1
              (CASE WHEN CA1.CPF IS NULL THEN 'AJUDANTE 1 NÃO CADASTRADO' ELSE CA1.NOME END) AS NOME_AJUDANTE_1,
              CASE
                WHEN INT_AJ1.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_AJ1.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_AJ1.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_AJ1.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,

              -- AJUDANTE 2
              (CASE WHEN CA2.CPF IS NULL THEN 'AJUDANTE 2 NÃO CADASTRADO' ELSE CA2.NOME END) AS NOME_AJUDANTE_2,
              CASE
                WHEN INT_AJ2.DATA_HORA_INICIO IS NOT NULL
                THEN TO_CHAR(INT_AJ2.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END,
              CASE
                WHEN INT_AJ2.DATA_HORA_FIM IS NOT NULL
                THEN TO_CHAR(INT_AJ2.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss')
                ELSE 'NÃO MARCADO'
              END
            FROM ESCALA_DIARIA AS ED
              LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA
              LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1
              LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2
              LEFT JOIN TABLE_INTERVALOS INT_MOT
                ON (COALESCE(INT_MOT.data_hora_inicio, INT_MOT.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_MOT.cpf_colaborador = ED.cpf_motorista

              LEFT JOIN TABLE_INTERVALOS INT_AJ1
                ON (COALESCE(INT_AJ1.data_hora_inicio, INT_AJ1.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_AJ1.cpf_colaborador = ED.cpf_ajudante_1

              LEFT JOIN TABLE_INTERVALOS INT_AJ2
                ON (COALESCE(INT_AJ2.data_hora_inicio, INT_AJ2.data_hora_fim) AT TIME ZONE f_time_zone_unidade)::DATE = ED.data
                   AND INT_AJ2.cpf_colaborador = ED.cpf_ajudante_2

            WHERE (ED.DATA >= f_data_inicial AND ED.DATA <= f_data_final) AND ED.COD_UNIDADE = F_COD_UNIDADE;
$$;
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION;