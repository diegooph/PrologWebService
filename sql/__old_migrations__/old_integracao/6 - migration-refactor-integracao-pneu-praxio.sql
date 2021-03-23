BEGIN TRANSACTION;
--######################################################################################################################
-- PL-2222
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(
  F_COD_SISTEMA_INTEGRADO_PNEUS BIGINT[])
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  DELETE FROM PUBLIC.VEICULO_PNEU
  WHERE COD_PNEU IN (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                     FROM INTEGRACAO.PNEU_CADASTRADO PC
                     WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = ANY(F_COD_SISTEMA_INTEGRADO_PNEUS));
END;
$$;
--######################################################################################################################

--######################################################################################################################
-- PL-2296
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_SERVICO_IS_TIPO_SERVICO(F_COD_SERVICO BIGINT, F_TIPO_SERVICO_PNEU TEXT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN
  (SELECT AM.TIPO_SERVICO
   FROM AFERICAO_MANUTENCAO AM
   WHERE AM.CODIGO = F_COD_SERVICO) = F_TIPO_SERVICO_PNEU;
END;
$$;
--######################################################################################################################

--######################################################################################################################
-- Bloqueia funcionalidades para empresas parceiras Praxio.
INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (48, 'API_PROLOG', 'VEICULO_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (48, 'API_PROLOG', 'PNEU_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (51, 'API_PROLOG', 'VEICULO_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (51, 'API_PROLOG', 'PNEU_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (52, 'API_PROLOG', 'PNEU_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (52, 'API_PROLOG', 'VEICULO_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (53, 'API_PROLOG', 'PNEU_TRANSFERENCIA');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (53, 'API_PROLOG', 'VEICULO_TRANSFERENCIA');

-- PLI-37 - Roteia método de inserir aferição
INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (48, 'API_PROLOG', 'AFERICAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (51, 'API_PROLOG', 'AFERICAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (52, 'API_PROLOG', 'AFERICAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (53, 'API_PROLOG', 'AFERICAO');

-- PLI-38 - Bloquear movimentações que não são DESCARTE
INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (48, 'API_PROLOG', 'MOVIMENTACAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (51, 'API_PROLOG', 'MOVIMENTACAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (52, 'API_PROLOG', 'MOVIMENTACAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (53, 'API_PROLOG', 'MOVIMENTACAO');

INSERT INTO PUBLIC.INTEGRACAO(
  COD_EMPRESA,
  CHAVE_SISTEMA,
  RECURSO_INTEGRADO)
VALUES (57, 'API_PROLOG', 'MOVIMENTACAO');
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################   Fechamento automatico de serviços de pneus   ##########################################
--######################################################################################################################
--######################################################################################################################
-- PL-31
ALTER TABLE AFERICAO_MANUTENCAO_DATA ADD COLUMN FECHADO_AUTOMATICAMENTE_INTEGRACAO BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE AFERICAO_MANUTENCAO_DATA DROP CONSTRAINT CHECK_ESTADOS_SERVICOS;

UPDATE AFERICAO_MANUTENCAO_DATA
SET FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE
WHERE FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL;

ALTER TABLE AFERICAO_MANUTENCAO_DATA ALTER COLUMN FECHADO_AUTOMATICAMENTE_MOVIMENTACAO SET NOT NULL;

ALTER TABLE AFERICAO_MANUTENCAO_DATA ALTER COLUMN FECHADO_AUTOMATICAMENTE_MOVIMENTACAO SET DEFAULT FALSE;

ALTER TABLE AFERICAO_MANUTENCAO_DATA ADD CONSTRAINT CHECK_ESTADOS_SERVICOS CHECK(
  CASE
  WHEN (TIPO_SERVICO::TEXT = 'movimentacao'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está PENDENTE.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO por mecânico
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO AUTOMATICAMENTE
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE)) -- Verificamos se está RESOLVIDO POR INTEGRAÇÃO
  WHEN (TIPO_SERVICO::TEXT = 'calibragem'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está PENDENTE.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO por mecânico
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO AUTOMATICAMENTE
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE)) -- Verificamos se está RESOLVIDO POR INTEGRAÇÃO
  WHEN (TIPO_SERVICO::TEXT = 'inspecao'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está PENDENTE.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_ALTERNATIVA,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO por mecânico
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE) -- Verificamos se está RESOLVIDO AUTOMATICAMENTE
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE)) -- Verificamos se está RESOLVIDO POR INTEGRAÇÃO
  END
);

COMMENT ON COLUMN AFERICAO_MANUTENCAO_DATA.DATA_HORA_RESOLUCAO IS 'A data e hora são setados sempre, independente do
processo que foi utilizado (integração, movimentação ou por mecânico)';
--######################################################################################################################

--######################################################################################################################
CREATE OR REPLACE VIEW AFERICAO_MANUTENCAO AS
  SELECT
    AM.COD_AFERICAO,
    AM.COD_PNEU,
    AM.COD_UNIDADE,
    AM.TIPO_SERVICO,
    AM.DATA_HORA_RESOLUCAO,
    AM.CPF_MECANICO,
    AM.QT_APONTAMENTOS,
    AM.PSI_APOS_CONSERTO,
    AM.KM_MOMENTO_CONSERTO,
    AM.COD_ALTERNATIVA,
    AM.COD_PNEU_INSERIDO,
    AM.CODIGO,
    AM.COD_PROCESSO_MOVIMENTACAO,
    AM.TEMPO_REALIZACAO_MILLIS,
    AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO,
    AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO
  FROM AFERICAO_MANUTENCAO_DATA AM
  WHERE (AM.DELETADO = FALSE);
--######################################################################################################################

--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(
  F_COD_UNIDADE TEXT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE)
  RETURNS TABLE (
    "UNIDADE DO SERVIÇO"               TEXT,
    "DATA AFERIÇÃO"                    TEXT,
    "DATA RESOLUÇÃO"                   TEXT,
    "HORAS PARA RESOLVER"              DOUBLE PRECISION,
    "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
    "PLACA"                            TEXT,
    "KM AFERIÇÃO"                      BIGINT,
    "KM CONSERTO"                      BIGINT,
    "KM PERCORRIDO"                    BIGINT,
    "COD PNEU"                         CHARACTER VARYING,
    "PRESSÃO RECOMENDADA"              REAL,
    "PRESSÃO AFERIÇÃO"                 TEXT,
    "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
    "PRESSÃO INSERIDA"                 TEXT,
    "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
    "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
    "SERVIÇO"                          TEXT,
    "MECÂNICO"                         TEXT,
    "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
    "FECHADO AUTOMATICAMENTE"          TEXT)
LANGUAGE SQL
AS
$$
SELECT
  U.NOME                                                                                                                     AS UNIDADE_SERVICO,
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI:SS')                                    AS DATA_HORA_AFERICAO,
  TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI:SS')                         AS DATA_HORA_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 3600)                                               AS HORAS_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 60)                                                 AS MINUTOS_RESOLUCAO,
  A.PLACA_VEICULO                                                                                                            AS PLACA_VEICULO,
  A.KM_VEICULO                                                                                                               AS KM_AFERICAO,
  AM.KM_MOMENTO_CONSERTO                                                                                                     AS KM_MOMENTO_CONSERTO,
  AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                                                                      AS KM_PERCORRIDO,
  P.CODIGO_CLIENTE                                                                                                           AS CODIGO_CLIENTE_PNEU,
  P.PRESSAO_RECOMENDADA                                                                                                      AS PRESSAO_RECOMENDADA_PNEU,
  COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','), '-')                                                      AS PSI_AFERICAO,
  COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','), '-')               AS DISPERSAO_PRESSAO_ANTES,
  COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','), '-')                                        AS PSI_POS_CONSERTO,
  COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','), '-') AS DISPERSAO_PRESSAO_DEPOIS,
  COALESCE(PPNE.NOMENCLATURA, '-')                                                                                           AS POSICAO,
  AM.TIPO_SERVICO                                                                                                            AS TIPO_SERVICO,
  COALESCE(INITCAP(C.NOME), '-')                                                                                             AS NOME_MECANICO,
  COALESCE(AA.ALTERNATIVA, '-')                                                                                              AS PROBLEMA_APONTADO,
  F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, 'Sim' :: TEXT, 'Não')               AS TIPO_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
  JOIN UNIDADE U
    ON AM.COD_UNIDADE = U.CODIGO
  JOIN AFERICAO_VALORES AV
    ON AM.COD_UNIDADE = AV.COD_UNIDADE
       AND AM.COD_AFERICAO = AV.COD_AFERICAO
       AND AM.COD_PNEU = AV.COD_PNEU
  JOIN AFERICAO A
    ON A.CODIGO = AV.COD_AFERICAO
  LEFT JOIN COLABORADOR C
    ON AM.CPF_MECANICO = C.CPF
  JOIN PNEU P
    ON P.CODIGO = AV.COD_PNEU
  LEFT JOIN VEICULO_PNEU VP
    ON VP.COD_PNEU = P.CODIGO
       AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
    ON AA.CODIGO = AM.COD_ALTERNATIVA
  LEFT JOIN VEICULO V
    ON V.PLACA = VP.PLACA
  LEFT JOIN EMPRESA E
    ON U.COD_EMPRESA = E.CODIGO
  LEFT JOIN VEICULO_TIPO VT
    ON E.CODIGO = VT.COD_EMPRESA
       AND VT.CODIGO = V.COD_TIPO
  LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
    ON PPNE.COD_EMPRESA = P.COD_EMPRESA
       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
       AND PPNE.POSICAO_PROLOG = AV.POSICAO
WHERE AV.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
      AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;
--######################################################################################################################

--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(
  F_COD_UNIDADE TEXT[],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE,
  F_DATA_ATUAL DATE)
  RETURNS TABLE(
    "UNIDADE DO SERVIÇO"            TEXT,
    "CÓDIGO DO SERVIÇO"             TEXT,
    "TIPO DO SERVIÇO"               TEXT,
    "QTD APONTAMENTOS"              TEXT,
    "DATA HORA ABERTURA"            TEXT,
    "QTD DIAS EM ABERTO"            TEXT,
    "NOME DO COLABORADOR"           TEXT,
    "PLACA"                         TEXT,
    "PNEU"                          TEXT,
    "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
    "MEDIDAS"                       TEXT,
    "COD AFERIÇÃO"                  TEXT,
    "SULCO INTERNO"                 TEXT,
    "SULCO CENTRAL INTERNO"         TEXT,
    "SULCO CENTRAL EXTERNO"         TEXT,
    "SULCO EXTERNO"                 TEXT,
    "MENOR SULCO"                   TEXT,
    "PRESSÃO (PSI)"                 TEXT,
    "PRESSÃO RECOMENDADA (PSI)"     TEXT,
    "ESTADO ATUAL"                  TEXT,
    "MÁXIMO DE RECAPAGENS"          TEXT)
LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                       AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                            AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                              AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                                   AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT     AS DIAS_EM_ABERTO,
       C.NOME                                                                                       AS NOME_COLABORADOR,
       A.PLACA_VEICULO                                                                              AS PLACA_VEICULO,
       P.CODIGO_CLIENTE                                                                             AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                     AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                             AS MEDIDAS,
       A.CODIGO :: TEXT                                                                             AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                              AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                      AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                      AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                              AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                       AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                      AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.', ',')                       AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                     AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                     AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P
    ON AM.COD_PNEU = P.CODIGO
  JOIN DIMENSAO_PNEU DP
    ON DP.CODIGO = P.COD_DIMENSAO
  JOIN AFERICAO A
    ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C
    ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV
    ON AV.COD_AFERICAO = AM.COD_AFERICAO
       AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U
    ON U.CODIGO = AM.COD_UNIDADE
  JOIN EMPRESA E
    ON U.COD_EMPRESA = E.CODIGO
  JOIN PNEU_VIDA_NOMENCLATURA PVN
    ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
    ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
  JOIN VEICULO V
    ON A.PLACA_VEICULO = V.PLACA
       AND V.COD_UNIDADE = A.COD_UNIDADE
  LEFT JOIN VEICULO_TIPO VT
    ON V.COD_TIPO = VT.CODIGO
  LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
  LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                                                      AND PPNE.COD_DIAGRAMA = VD.CODIGO
                                                      AND AV.POSICAO = PPNE.POSICAO_PROLOG
WHERE AM.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
      AND (AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;
--######################################################################################################################

--######################################################################################################################
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(
  F_COD_PNEU_PROLOG     BIGINT,
  F_DATA_HORA_RESOLUCAO TIMESTAMP WITHOUT TIME ZONE)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  UPDATE PUBLIC.AFERICAO_MANUTENCAO
  SET
    KM_MOMENTO_CONSERTO = 0, -- Zero pois no fechamento automatico não há o input de KM
    DATA_HORA_RESOLUCAO = F_DATA_HORA_RESOLUCAO,
    FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE,
    FECHADO_AUTOMATICAMENTE_INTEGRACAO = TRUE
  WHERE COD_PNEU = F_COD_PNEU_PROLOG AND DATA_HORA_RESOLUCAO IS NULL;
END;
$$;
--######################################################################################################################

--######################################################################################################################
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(
  F_COD_PNEU_SISTEMA_INTEGRADO       BIGINT,
  F_CODIGO_PNEU_CLIENTE              CHARACTER VARYING,
  F_COD_UNIDADE_PNEU                 BIGINT,
  F_CPF_COLABORADOR_ALTERACAO_STATUS CHARACTER VARYING,
  F_DATA_HORA_ALTERACAO_STATUS       TIMESTAMP WITHOUT TIME ZONE,
  F_STATUS_PNEU                      CHARACTER VARYING,
  F_TROCOU_DE_BANDA                  BOOLEAN,
  F_COD_NOVO_MODELO_BANDA_PNEU       BIGINT,
  F_VALOR_NOVA_BANDA_PNEU            NUMERIC,
  F_PLACA_VEICULO_PNEU_APLICADO      CHARACTER VARYING,
  F_POSICAO_VEICULO_PNEU_APLICADO    INTEGER,
  F_TOKEN_INTEGRACAO                 CHARACTER VARYING)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_PNEU BIGINT := (SELECT TI.COD_EMPRESA
                              FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                              WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_VEICULO_PROLOG BIGINT := (SELECT V.CODIGO
                                FROM PUBLIC.VEICULO V
                                WHERE V.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                                      AND V.COD_UNIDADE IN (SELECT U.CODIGO
                                                            FROM PUBLIC.UNIDADE U
                                                            WHERE U.COD_EMPRESA = COD_EMPRESA_PNEU));
  IS_POSICAO_ESTEPE BOOLEAN := F_IF(F_POSICAO_VEICULO_PNEU_APLICADO >= 900
                                    AND F_POSICAO_VEICULO_PNEU_APLICADO <= 908, TRUE, FALSE);
  COD_PNEU_PROLOG BIGINT := (SELECT PC.COD_PNEU_CADASTRO_PROLOG
                             FROM INTEGRACAO.PNEU_CADASTRADO PC
                             WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                                   AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU);
  VIDA_ATUAL_PNEU INTEGER := (SELECT P.VIDA_ATUAL FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG);
  PROXIMA_VIDA_PNEU INTEGER := VIDA_ATUAL_PNEU + 1;
  STATUS_APLICADO_VEICULO TEXT := 'EM_USO';
  COD_SERVICO_INCREMENTA_VIDA BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  -- Validamos se a Unidade repassada existe.
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                      FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                             F_COD_UNIDADE_PNEU));

  -- Validamos se a Unidade pertence a Empresa do token repassado.
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                              F_COD_UNIDADE_PNEU,
                                              FORMAT('A Unidade %s não está configurada para esta empresa',
                                                     F_COD_UNIDADE_PNEU));

  -- Validamos se o código do pneu no sistema integrado está mapeado na tabela interna do ProLog.
  IF (COD_PNEU_PROLOG IS NULL)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s não está mapeado no Sistema ProLog',
                                              F_COD_PNEU_SISTEMA_INTEGRADO));
  END IF;

  -- Deletamos o vinculo do pneu com a placa. Caso o pneu não estava vinculado, nada irá acontecer.
  DELETE FROM VEICULO_PNEU VP WHERE VP.COD_PNEU = COD_PNEU_PROLOG;

  -- Atualiza o pneu para o status em que ele deve estar.
  UPDATE PUBLIC.PNEU
  SET
    STATUS = F_STATUS_PNEU,
    COD_UNIDADE = F_COD_UNIDADE_PNEU
  WHERE CODIGO = COD_PNEU_PROLOG;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Validamos se o status do pneu foi atualizado com sucesso
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível atualizar as informações do pneu %s para o status %s',
                                              F_CODIGO_PNEU_CLIENTE,
                                              F_STATUS_PNEU));
  END IF;

  -- Precisamos vincular o pneu ao veículo apenas se o status for aplicado.
  IF (F_STATUS_PNEU = STATUS_APLICADO_VEICULO)
  THEN
    -- Transferimos o pneu para a unidade do veículo, caso ele já não esteja.
    IF ((SELECT P.COD_UNIDADE FROM PUBLIC.PNEU P WHERE P.CODIGO = COD_PNEU_PROLOG) <> F_COD_UNIDADE_PNEU)
    THEN
      UPDATE PUBLIC.PNEU
      SET
        COD_UNIDADE = (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG)
      WHERE CODIGO = COD_PNEU_PROLOG;

      SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = COD_VEICULO_PROLOG INTO F_COD_UNIDADE_PNEU;
    END IF;

    PERFORM INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(COD_VEICULO_PROLOG,
                                                            F_PLACA_VEICULO_PNEU_APLICADO,
                                                            COD_PNEU_PROLOG,
                                                            F_CODIGO_PNEU_CLIENTE,
                                                            F_COD_UNIDADE_PNEU,
                                                            F_POSICAO_VEICULO_PNEU_APLICADO,
                                                            IS_POSICAO_ESTEPE);
  END IF;

  IF (F_TROCOU_DE_BANDA)
  THEN
    -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
    IF (F_COD_NOVO_MODELO_BANDA_PNEU IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O código do modelo da banda deve ser informado');
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF ((SELECT NOT EXISTS(SELECT MB.CODIGO
                           FROM PUBLIC.MODELO_BANDA MB
                           WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                 AND MB.CODIGO = F_COD_NOVO_MODELO_BANDA_PNEU)))
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda do pneu %s não está mapeado no Sistema ProLog',
                                                F_COD_NOVO_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (F_VALOR_NOVA_BANDA_PNEU IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu deve ser informado');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (F_VALOR_NOVA_BANDA_PNEU < 0)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
    END IF;

    -- Busca serviço que incrementa a vida do pneu dentro da empresa em questão.
    SELECT * FROM PUBLIC.FUNC_PNEU_GET_SERVICO_INCREMENTA_VIDA_PNEU_EMPRESA(COD_EMPRESA_PNEU)
    INTO COD_SERVICO_INCREMENTA_VIDA;

    IF (COD_SERVICO_INCREMENTA_VIDA IS NULL)
    THEN
      PERFORM PUBLIC.THROW_GENERIC_ERROR('Erro ao vincular banda ao pneu');
    END IF;

    -- Incrementa a vida do pneu simulando um processo de movimentação.
    PERFORM INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(F_COD_UNIDADE_PNEU,
                                                                      COD_PNEU_PROLOG,
                                                                      F_COD_NOVO_MODELO_BANDA_PNEU,
                                                                      F_VALOR_NOVA_BANDA_PNEU,
                                                                      PROXIMA_VIDA_PNEU,
                                                                      COD_SERVICO_INCREMENTA_VIDA);

    -- Após incrementar a vida e criar o serviço, atualizamos o pneu para ficar com a banda e a vida correta.
    PERFORM PUBLIC.FUNC_PNEUS_INCREMENTA_VIDA_PNEU(COD_PNEU_PROLOG, F_COD_NOVO_MODELO_BANDA_PNEU);
  END IF;

  -- Qualquer alteração de status do pneu deve verificar se o pneu tem serviços aberto e fechá-los.
  PERFORM INTEGRACAO.FUNC_PNEU_FECHA_SERVICO_PNEU_AUTOMATICAMENTE(COD_PNEU_PROLOG,
                                                                  F_DATA_HORA_ALTERACAO_STATUS);

  RETURN COD_PNEU_PROLOG;
END;
$$;
--######################################################################################################################

END TRANSACTION;
