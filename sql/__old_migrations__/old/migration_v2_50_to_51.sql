-- Essa migração deve ser executada quando o WS versão 51 for publicado.
BEGIN TRANSACTION;

-- ########################################################################################################
-- ########################################################################################################
-- ################# ADICIONA CONSTRAINT DE NOT NULL NA COLUNA PNEU_NOVO_NUNCA_RODADO #####################
-- ########################################################################################################
-- ########################################################################################################
UPDATE PNEU SET PNEU_NOVO_NUNCA_RODADO = FALSE WHERE PNEU_NOVO_NUNCA_RODADO IS NULL;
ALTER TABLE PNEU ALTER COLUMN PNEU_NOVO_NUNCA_RODADO SET NOT NULL;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ######################## REMOVE FUNCTION COM NOME INCORRETO DO BANCO ###################################
-- ########################################################################################################
-- ########################################################################################################
DROP FUNCTION FUNC_RELATORIO_PREVISA_TROCA(DATE, DATE, BIGINT);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ######################## RELATÓRIO DE SERVIÇOS ESTRATIFICADOS EM ABERTO ################################
-- ########################################################################################################
-- #########
-- ###############################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADE BIGINT,F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_DATA_ATUAL DATE, F_TIME_ZONE TEXT)
  RETURNS TABLE(
    "CÓDIGO DO SERVIÇO"                       BIGINT,
    "TIPO DO SERVIÇO"                         TEXT,
    "QTD APONTAMENTOS"                        INTEGER,
    "DATA HORA ABERTURA"                      TEXT,
    "QTD DIAS EM ABERTO"                      TEXT,
    "NOME DO COLABORADOR"                     TEXT,
    "PLACA"                                   TEXT,
    "AFERIÇÃO"                                BIGINT,
    "PNEU"                                    TEXT,
    "SULCO INTERNO"                           REAL,
    "SULCO CENTRAL INTERNO"                   REAL,
    "SULCO CENTRAL EXTERNO"                   REAL,
    "SULCO EXTERNO"                           REAL,
    "PRESSÃO (PSI)"                           REAL,
    "PRESSÃO RECOMENDADA (PSI)"               REAL,
    "POSIÇÃO DO PNEU"                         TEXT,
    "ESTADO ATUAL"                              TEXT,
    "MÁXIMO DE RECAPAGENS"                          TEXT)
LANGUAGE SQL
AS $$
SELECT
  AM.CODIGO AS CODIGO_SERVICO,
  AM.TIPO_SERVICO,
  AM.QT_APONTAMENTOS,
  to_char((A.DATA_HORA AT TIME ZONE F_TIME_ZONE), 'DD/MM/YYYY HH24:MI')::TEXT AS DATA_HORA_ABERTURA,
  (SELECT (EXTRACT(EPOCH FROM AGE(F_DATA_ATUAL,
                                  A.DATA_HORA AT TIME ZONE F_TIME_ZONE))
           / 86400)::INTEGER)::TEXT AS DIAS_EM_ABERTO,
  C.NOME AS NOME_COLABORADOR,
  A.PLACA_VEICULO AS PLACA_VEICULO,
  A.CODIGO AS COD_AFERICAO,
  AV.COD_PNEU AS COD_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA,
  CASE WHEN PONU.NOMENCLATURA IS NOT NULL THEN PONU.NOMENCLATURA ELSE AV.POSICAO::TEXT END AS POSICAO_PNEU_PROBLEMA,
  PVN.NOME AS VIDA_PNEU_PROBLEMA,
  PRN.NOME AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P ON AM.COD_UNIDADE = P.COD_UNIDADE AND AM.COD_PNEU = P.CODIGO
  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV ON AV.COD_AFERICAO = AM.COD_AFERICAO AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U ON U.CODIGO = AM.COD_UNIDADE
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
  JOIN VEICULO V ON A.PLACA_VEICULO = V.PLACA AND V.COD_UNIDADE = A.COD_UNIDADE
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON AM.COD_UNIDADE = PONU.COD_UNIDADE
       AND AV.POSICAO = PONU.POSICAO_PROLOG
       AND V.COD_TIPO = PONU.COD_TIPO_VEICULO
WHERE AM.COD_UNIDADE = F_COD_UNIDADE
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE >= F_DATA_INICIAL
      AND (A.DATA_HORA AT TIME ZONE F_TIME_ZONE)::DATE <= F_DATA_FINAL
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY A.DATA_HORA;
$$;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

END TRANSACTION;