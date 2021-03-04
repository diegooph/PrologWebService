BEGIN TRANSACTION ;

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ##########  RENOMEIA PERMISSÃO DA RAÍZEN  ##############################################################
-- ########################################################################################################
-- ########################################################################################################
UPDATE FUNCAO_PROLOG_V11
SET FUNCAO = 'Raizen Produtividade - Inserir Registros (upload e cadastro)'
WHERE CODIGO = 417 AND COD_PILAR = 4;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ##########  ADICIONA O MODELO DO VEÍCULO AO RELATÓRIO DE DADOS DA ÚLTIMA AFERIÇÃO DO PNEU  #############
-- ########################################################################################################
-- ########################################################################################################
-- Adiciona o modelo do veículo ao relatório de dados da última aferição do pneu
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE TEXT []);
CREATE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADE TEXT [])
  RETURNS TABLE(
    "UNIDADE ALOCADO" TEXT,
    "PNEU" TEXT,
    "MARCA PNEU" TEXT,
    "MODELO PNEU" TEXT,
    "MEDIDAS" TEXT,
    "PLACA" TEXT,
    "MARCA VEÍCULO" TEXT,
    "MODELO VEÍCULO" TEXT,
    "TIPO VEÍCULO" TEXT,
    "POSIÇÃO" TEXT,
    "SULCO INTERNO" TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO" TEXT,
    "PRESSÃO (PSI)" TEXT,
    "VIDA" TEXT,
    "DOT" TEXT,
    "ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                                        AS "UNIDADE ALOCADO",
  P.CODIGO_CLIENTE                                                                              AS COD_PNEU,
  MAP.NOME                                                                                      AS NOME_MARCA,
  MP.NOME                                                                                       AS NOME_MODELO,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
   DP.ARO)                                                                                      AS MEDIDAS,
  COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU,
           '-')                                                                                 AS PLACA,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_MARCA, '-')                                             AS MARCA_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_MODELO, '-')                                            AS MODELO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                              AS TIPO_VEICULO,
  COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                              AS POSICAO_PNEU,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')         AS SULCO_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_INTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                  AS SULCO_CENTRAL_INTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_CENTRAL_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.',
          ',')                                                                                  AS SULCO_CENTRAL_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.ALTURA_SULCO_EXTERNO :: NUMERIC, 2) :: TEXT, '-'), '.', ',')         AS SULCO_EXTERNO,
  REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                              AS PRESSAO_ATUAL,
  P.VIDA_ATUAL :: TEXT                                                                          AS VIDA_ATUAL,
  COALESCE(P.DOT, '-')                                                                          AS DOT,
  COALESCE(TO_CHAR(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO AT TIME ZONE tz_unidade(DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA),
                   'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                                      AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO,
               MOV.NOME         AS VEICULO_MODELO,
               MAV.NOME         AS VEICULO_MARCA
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_UNIDADE = VT.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO
               JOIN MODELO_VEICULO MOV
                 ON V.COD_MODELO = MOV.CODIGO
               JOIN MARCA_VEICULO MAV
                 ON MOV.COD_MARCA = MAV.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN (SELECT
               AV.COD_PNEU,
               A.COD_UNIDADE    AS COD_UNIDADE_DATA,
               MAX(A.DATA_HORA) AS ULTIMA_AFERICAO
             FROM AFERICAO A
               JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO
             GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO
    ON DATA_ULTIMA_AFERICAO.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ##########  ALTERA NOME DO ATRIBUTO DE POSIÇÃO DO PNEU PARA 'POSIÇÃO PNEU ABETURA SERVIÇO'  ############
-- ########################################################################################################
-- ########################################################################################################
drop function func_relatorio_pneu_extrato_servicos_abertos(f_cod_unidade text[], f_data_inicial date, f_data_final date, f_data_atual date);
create function func_relatorio_pneu_extrato_servicos_abertos(f_cod_unidade text[], f_data_inicial date, f_data_final date, f_data_atual date)
  returns TABLE(
    "UNIDADE DO SERVIÇO" text,
    "CÓDIGO DO SERVIÇO" bigint,
    "TIPO DO SERVIÇO" text,
    "QTD APONTAMENTOS" integer,
    "DATA HORA ABERTURA" text,
    "QTD DIAS EM ABERTO" text,
    "NOME DO COLABORADOR" text,
    "PLACA" text,
    "AFERIÇÃO" bigint,
    "PNEU" text,
    "SULCO INTERNO" real,
    "SULCO CENTRAL INTERNO" real,
    "SULCO CENTRAL EXTERNO" real,
    "SULCO EXTERNO" real,
    "PRESSÃO (PSI)" real,
    "PRESSÃO RECOMENDADA (PSI)" real,
    "POSIÇÃO PNEU ABERTURA SERVIÇO" text,
    "ESTADO ATUAL" text,
    "MÁXIMO DE RECAPAGENS" text)
language sql
as $$
SELECT
  U.NOME AS "UNIDADE DO SERVIÇO",
  AM.CODIGO AS CODIGO_SERVICO,
  AM.TIPO_SERVICO,
  AM.QT_APONTAMENTOS,
  TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI')::TEXT AS DATA_HORA_ABERTURA,
  (SELECT (EXTRACT(EPOCH FROM AGE(f_data_atual, A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))) / 86400)::INTEGER)::TEXT AS DIAS_EM_ABERTO,
  C.NOME AS NOME_COLABORADOR,
  A.PLACA_VEICULO AS PLACA_VEICULO,
  A.CODIGO AS COD_AFERICAO,
  P.CODIGO_CLIENTE AS COD_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_EXTERNO AS SULCO_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_EXTERNO AS SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_CENTRAL_INTERNO AS SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA,
  AV.ALTURA_SULCO_INTERNO AS SULCO_INTERNO_PNEU_PROBLEMA,
  AV.PSI AS PRESSAO_PNEU_PROBLEMA,
  P.PRESSAO_RECOMENDADA,
  COALESCE(PONU.NOMENCLATURA, '-') AS POSICAO_PNEU_PROBLEMA,
  PVN.NOME AS VIDA_PNEU_PROBLEMA,
  PRN.NOME AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
  JOIN PNEU P
    ON AM.COD_PNEU = P.CODIGO
  JOIN AFERICAO A
    ON A.CODIGO = AM.COD_AFERICAO
  JOIN COLABORADOR C
    ON A.CPF_AFERIDOR = C.CPF
  JOIN AFERICAO_VALORES AV
    ON AV.COD_AFERICAO = AM.COD_AFERICAO
       AND AV.COD_PNEU = AM.COD_PNEU
  JOIN UNIDADE U
    ON U.CODIGO = AM.COD_UNIDADE
  JOIN PNEU_VIDA_NOMENCLATURA PVN
    ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
  JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
    ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
  JOIN VEICULO V
    ON A.PLACA_VEICULO = V.PLACA
       AND V.COD_UNIDADE = A.COD_UNIDADE
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON AM.COD_UNIDADE = PONU.COD_UNIDADE
       AND AV.POSICAO = PONU.POSICAO_PROLOG
       AND V.COD_TIPO = PONU.COD_TIPO_VEICULO
WHERE AM.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE >= f_data_inicial
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE <= f_data_final
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ##########  ALTERA NOME DO ATRIBUTO DE POSIÇÃO DO PNEU PARA 'POSIÇÃO PNEU ABETURA SERVIÇO'  ############
-- ########################################################################################################
-- ########################################################################################################
drop function func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade text[], f_data_inicial date, f_data_final date);
create function func_relatorio_pneu_extrato_servicos_fechados(f_cod_unidade text[], f_data_inicial date, f_data_final date)
  returns TABLE(
    "UNIDADE DO SERVIÇO" text,
    "DATA AFERIÇÃO" text,
    "DATA RESOLUÇÃO" text,
    "HORAS PARA RESOLVER" double precision,
    "MINUTOS PARA RESOLVER" double precision,
    "PLACA" text,
    "KM AFERIÇÃO" bigint,
    "KM CONSERTO" bigint,
    "KM PERCORRIDO" bigint,
    "COD PNEU" character varying,
    "PRESSÃO RECOMENDADA" real,
    "PRESSÃO AFERIÇÃO" text,
    "DISPERSÃO RECOMENDADA X AFERIÇÃO" text,
    "PRESSÃO INSERIDA" text,
    "DISPERSÃO RECOMENDADA X INSERIDA" text,
    "POSIÇÃO PNEU ABERTURA SERVIÇO" text,
    "SERVIÇO" text,
    "MECÂNICO" text,
    "PROBLEMA APONTADO (INSPEÇÃO)" text)
language sql
as $$
SELECT
  U.NOME                                                                                                      AS "UNIDADE DO SERVIÇO",
  TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MM:SS')                     AS DATA_HORA_AFERICAO,
  TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)), 'DD/MM/YYYY HH24:MM:SS')          AS DATA_HORA_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA ))) / 3600)                               AS HORAS_RESOLUCAO,
  TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA ))) / 60)                                 AS MINUTOS_RESOLUCAO,
  A.PLACA_VEICULO,
  A.KM_VEICULO                                                                                                AS KM_AFERICAO,
  AM.KM_MOMENTO_CONSERTO,
  AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                                                       AS KM_PERCORRIDO,
  P.CODIGO_CLIENTE,
  P.PRESSAO_RECOMENDADA,
  REPLACE(ROUND(AV.PSI::NUMERIC, 2)::TEXT, '.', ',')                                                          AS PSI_AFERICAO,
  REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ',')               AS DISPERSAO_PRESSAO_ANTES,
  REPLACE(ROUND(AM.PSI_APOS_CONSERTO::NUMERIC, 2)::TEXT, '.', ',')                                            AS PSI_POS_CONSERTO,
  REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ',') AS DISPERSAO_PRESSAO_DEPOIS,
  COALESCE(PON.NOMENCLATURA, '-')                                                                             AS POSICAO,
  AM.TIPO_SERVICO,
  INITCAP(C.NOME)                                                                                             AS NOME_MECANICO,
  COALESCE(AA.ALTERNATIVA, '-')                                                                               AS PROBLEMA_APONTADO
FROM AFERICAO_MANUTENCAO AM
  JOIN UNIDADE U
    ON AM.COD_UNIDADE = U.CODIGO
  JOIN AFERICAO_VALORES AV
    ON AM.COD_UNIDADE = AV.COD_UNIDADE
       AND AM.COD_AFERICAO = AV.COD_AFERICAO
       AND AM.COD_PNEU = AV.COD_PNEU
  JOIN AFERICAO A
    ON A.CODIGO = AV.COD_AFERICAO
  JOIN COLABORADOR C
    ON AM.CPF_MECANICO = C.CPF
  JOIN PNEU P
    ON P.CODIGO = AV.COD_PNEU
  JOIN VEICULO_PNEU VP
    ON VP.COD_PNEU = P.CODIGO
       AND VP.COD_UNIDADE = P.COD_UNIDADE
  LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
    ON AA.CODIGO = AM.COD_ALTERNATIVA
  JOIN VEICULO V
    ON V.PLACA = VP.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
    ON PON.COD_UNIDADE = P.COD_UNIDADE
       AND PON.COD_TIPO_VEICULO = V.COD_TIPO
       AND PON.POSICAO_PROLOG = AV.POSICAO
WHERE AV.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE >= f_data_inicial
      AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE))::DATE <= f_data_final
ORDER BY U.NOME, A.DATA_HORA DESC
$$;
-- ########################################################################################################
-- ########################################################################################################


create or replace function func_pneus_update_banda_pneu(f_cod_pneu bigint, f_cod_modelo_banda bigint, f_custo_banda real)
  returns boolean
language plpgsql
as $$
DECLARE
  cod_servico_realizado BIGINT;
BEGIN
  cod_servico_realizado = (
    SELECT CODIGO
    FROM PNEU_SERVICO_REALIZADO PSR
      JOIN PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA PSRIV
        ON PSR.CODIGO = PSRIV.cod_pneu_servico_realizado
           AND PSR.fonte_servico_realizado = PSRIV.fonte_servico_realizado
    WHERE
      PSR.COD_PNEU = f_cod_pneu
    ORDER BY CODIGO DESC
    LIMIT 1);
  UPDATE PNEU_SERVICO_REALIZADO
  SET CUSTO = f_custo_banda
  WHERE CODIGO = cod_servico_realizado;
  UPDATE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
  SET COD_MODELO_BANDA = f_cod_modelo_banda
  WHERE COD_PNEU_SERVICO_REALIZADO = cod_servico_realizado;

  -- FOUND será true se alguma linha foi modificada pela query executada
  IF FOUND THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ####################################  MIGRAÇÃO DAS PKS DO CHECKLIST  ###################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################


-- A coluna código está duplicada na tabela de alternativas de uma pergunta, como ela é usada apenas na view de
-- estratificação da OS, é mais vantajoso para nós remover essa coluna e criar com big serial novamente, do que tentar
-- corrigir todas as duplicatas.
DROP MATERIALIZED VIEW ESTRATIFICACAO_OS;

-- Remove tabela que não é mais utilizada na parte de checklist.
DROP TABLE CHECKLIST_MANUTENCAO;

-- ALTERA CHAVE PRIMÁRIA UTILIZADA NA TABELA DE CHECKLIST_MODELO

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Primeiro DROP das constraints que apontam para a PK atual.
ALTER TABLE CHECKLIST DROP CONSTRAINT FK_CHECKLIST_CHECKLIST_MODELO;
ALTER TABLE CHECKLIST_MODELO_FUNCAO DROP CONSTRAINT FK_CHECKLIST_MODELO_FUNCAO_MODELO;
ALTER TABLE CHECKLIST_MODELO_VEICULO_TIPO DROP CONSTRAINT FK_CHECKLIST_MODELO_VEICULO_TIPO_CHECKLIST_MODELO;
ALTER TABLE CHECKLIST_PERGUNTAS DROP CONSTRAINT FK_CHECKLIST_PERGUNTAS_CHECKLIST_MODELO;
-- Agora pode remover a PK.
ALTER TABLE CHECKLIST_MODELO DROP CONSTRAINT PK_CHECKLIST_MODELO;

-- Remove checklists realizados e modelo de check da unidade de Sapucaia do Sul (2) pois eles têm um modelo de mesmo
-- código do modelo de teste da Zalf e assim não teríamos como tornar o CODIGO PK.
DELETE FROM CHECKLIST_ORDEM_SERVICO WHERE COD_UNIDADE = 2;
DELETE FROM CHECKLIST_RESPOSTAS WHERE COD_UNIDADE = 2;
DELETE FROM CHECKLIST WHERE COD_CHECKLIST_MODELO = 1 AND COD_UNIDADE = 2;
DELETE FROM CHECKLIST_MODELO_VEICULO_TIPO WHERE COD_MODELO = 1 AND COD_UNIDADE = 2;
DELETE FROM CHECKLIST_MODELO_FUNCAO WHERE COD_CHECKLIST_MODELO = 1 AND COD_UNIDADE = 2;
DELETE FROM CHECKLIST_ALTERNATIVA_PERGUNTA WHERE COD_CHECKLIST_MODELO = 1 AND COD_UNIDADE = 2;
DELETE FROM CHECKLIST_PERGUNTAS WHERE COD_CHECKLIST_MODELO = 1 AND COD_UNIDADE = 2;
DELETE FROM CHECKLIST_MODELO WHERE CODIGO = 1 AND COD_UNIDADE = 2;

-- Recria PK.
ALTER TABLE CHECKLIST_MODELO ADD CONSTRAINT PK_CHECKLIST_MODELO PRIMARY KEY (CODIGO);
-- Cria um UNIQUE igual ao que era a PK anteriormente.
ALTER TABLE CHECKLIST_MODELO ADD CONSTRAINT UNICO_MODELO_POR_UNIDADE UNIQUE (CODIGO, COD_UNIDADE);

-- Recria FKs que apontam para CHECKLIST_MODELO.
ALTER TABLE CHECKLIST ADD CONSTRAINT FK_CHECKLIST_CHECKLIST_MODELO
FOREIGN KEY (COD_CHECKLIST_MODELO) REFERENCES CHECKLIST_MODELO(CODIGO);
ALTER TABLE CHECKLIST_MODELO_FUNCAO ADD CONSTRAINT FK_CHECKLIST_MODELO_FUNCAO_MODELO
FOREIGN KEY (COD_CHECKLIST_MODELO) REFERENCES CHECKLIST_MODELO(CODIGO);
ALTER TABLE CHECKLIST_MODELO_VEICULO_TIPO ADD CONSTRAINT FK_CHECKLIST_MODELO_VEICULO_TIPO_CHECKLIST_MODELO
FOREIGN KEY (COD_MODELO) REFERENCES CHECKLIST_MODELO(CODIGO);
ALTER TABLE CHECKLIST_PERGUNTAS ADD CONSTRAINT FK_CHECKLIST_PERGUNTAS_CHECKLIST_MODELO
FOREIGN KEY (COD_CHECKLIST_MODELO) REFERENCES CHECKLIST_MODELO(CODIGO);
-- A tabela de alternativas não tinha vínculo nenhum com modelo, iremos criar.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD CONSTRAINT FK_CHECKLIST_ALTERNATIVA_PERGUNTA_CHECKLIST_MODELO
FOREIGN KEY (COD_CHECKLIST_MODELO) REFERENCES CHECKLIST_MODELO(CODIGO);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ALTERA CHAVE PRIMÁRIA UTILIZADA NA TABELA DE CHECKLIST_PERGUNTAS
-- Tabelas que precisarão serem migradas, atualizando o código da alternativa, por consequência:
-- 1) CHECKLIST_RESPOSTAS
-- 2) CHECKLIST_ALTERNATIVA_PERGUNTA
-- 3) CHECKLIST_ORDEM_SERVICO_ITENS

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Adicionamos uma coluna provisória na tabela CHECKLIST_ORDEM_SERVICO_ITENS com o código do modelo de checklist.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN COD_CHECKLIST_MODELO BIGINT;
UPDATE CHECKLIST_ORDEM_SERVICO_ITENS COSI SET COD_CHECKLIST_MODELO =
(SELECT C.COD_CHECKLIST_MODELO
 FROM
   CHECKLIST_ORDEM_SERVICO COS
   JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO
 WHERE COS.CODIGO = COSI.COD_OS AND COS.COD_UNIDADE = COSI.COD_UNIDADE);
-- Para garantir que todos foram preenchidos.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ALTER COLUMN COD_CHECKLIST_MODELO SET NOT NULL;

-- Primeiro DROP das constraints que apontam para a PK atual.
ALTER TABLE CHECKLIST_RESPOSTAS DROP CONSTRAINT PK_CHECKLIST_RESPOSTAS;
ALTER TABLE CHECKLIST_RESPOSTAS DROP CONSTRAINT FK_CHECKLIST_RESPOSTAS_ALTERNATIVA_PERGUNTA;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA DROP CONSTRAINT PK_CHECKLIST_ALTERNATIVA_PERGUNTA;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA DROP CONSTRAINT FK_CHECKLIST_ALTERNATIVA_PERGUNTA_PERGUNTA;
-- A tabela CHECKLIST_ORDEM_SERVICO_ITENS não tem FK para a tabela de perguntas.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_ITENS;
-- Agora pode remover a PK.
ALTER TABLE CHECKLIST_PERGUNTAS DROP CONSTRAINT PK_CHECKLIST_PERGUNTAS;

-- Como a tabela possui muitos códigos duplicados, nós iremos ignorar a coluna atual e criar um novo autoincrement.
ALTER TABLE CHECKLIST_PERGUNTAS RENAME CODIGO TO CODIGO_ANTIGO;
ALTER TABLE CHECKLIST_PERGUNTAS ADD COLUMN CODIGO BIGSERIAL;
ALTER TABLE CHECKLIST_PERGUNTAS ALTER COLUMN CODIGO SET NOT NULL;

-- Atualiza os códigos para os gerados pela nova coluna.

-- 1) CHECKLIST_RESPOSTAS -> Atualiza o código das perguntas
ALTER TABLE CHECKLIST_RESPOSTAS RENAME COD_PERGUNTA TO COD_PERGUNTA_ANTIGO;
ALTER TABLE CHECKLIST_RESPOSTAS ADD COLUMN COD_PERGUNTA BIGINT;
-- Select do upadte testado, tudo OK!
UPDATE CHECKLIST_RESPOSTAS CR
SET COD_PERGUNTA = (SELECT CODIGO
                    FROM CHECKLIST_PERGUNTAS CP
                    WHERE CR.COD_UNIDADE = CP.COD_UNIDADE
                          AND CR.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
                          AND CR.COD_PERGUNTA_ANTIGO = CP.CODIGO_ANTIGO
                          AND NEXTVAL('QUERY_PROGRESS_1') != 0);
ALTER TABLE CHECKLIST_RESPOSTAS ALTER COLUMN COD_PERGUNTA SET NOT NULL;
ALTER TABLE CHECKLIST_RESPOSTAS DROP COLUMN COD_PERGUNTA_ANTIGO;

-- 2) CHECKLIST_ALTERNATIVA_PERGUNTA -> Atualiza o código das perguntas
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA RENAME COD_PERGUNTA TO COD_PERGUNTA_ANTIGO;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD COLUMN COD_PERGUNTA BIGINT;
-- Select do upadte testado, tudo OK!
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA CAP
SET COD_PERGUNTA = (SELECT CODIGO
                    FROM CHECKLIST_PERGUNTAS CP
                    WHERE CAP.COD_UNIDADE = CP.COD_UNIDADE
                          AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
                          AND CAP.COD_PERGUNTA_ANTIGO = CP.CODIGO_ANTIGO);
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ALTER COLUMN COD_PERGUNTA SET NOT NULL;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA DROP COLUMN COD_PERGUNTA_ANTIGO;

-- 3) CHECKLIST_ORDEM_SERVICO_ITENS -> Atualiza o código das perguntas
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS RENAME COD_PERGUNTA TO COD_PERGUNTA_ANTIGO;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN COD_PERGUNTA BIGINT;
-- Select do upadte testado, tudo OK!
UPDATE CHECKLIST_ORDEM_SERVICO_ITENS COSI
SET COD_PERGUNTA = (SELECT CODIGO
                    FROM CHECKLIST_PERGUNTAS CP
                    WHERE CP.COD_UNIDADE = COSI.COD_UNIDADE
                          AND CP.COD_CHECKLIST_MODELO = COSI.COD_CHECKLIST_MODELO
                          AND CP.CODIGO_ANTIGO = COSI.COD_PERGUNTA_ANTIGO);
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ALTER COLUMN COD_PERGUNTA SET NOT NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP COLUMN COD_PERGUNTA_ANTIGO;

-- Remove coluna antiga de código.
ALTER TABLE CHECKLIST_PERGUNTAS DROP COLUMN CODIGO_ANTIGO;

-- Recria as PKs que utilizam o código da pergunta em sua composição.
ALTER TABLE CHECKLIST_RESPOSTAS ADD CONSTRAINT PK_CHECKLIST_RESPOSTAS
PRIMARY KEY (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA);
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_ITENS
PRIMARY KEY (COD_OS, COD_PERGUNTA, COD_ALTERNATIVA, COD_UNIDADE);

-- Precisamos adicionar a PK nessa tabela antes de adicionar a FK na CHECKLIST_RESPOSTAS,
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD CONSTRAINT PK_CHECKLIST_ALTERNATIVA_PERGUNTA
PRIMARY KEY (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_PERGUNTA, CODIGO);

ALTER TABLE CHECKLIST_RESPOSTAS ADD CONSTRAINT FK_CHECKLIST_RESPOSTAS_ALTERNATIVA_PERGUNTA
FOREIGN KEY (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_PERGUNTA, COD_ALTERNATIVA)
REFERENCES CHECKLIST_ALTERNATIVA_PERGUNTA(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_PERGUNTA, CODIGO);

-- Cria nova PK da CHECKLIST_PERGUNTAS.
ALTER TABLE CHECKLIST_PERGUNTAS ADD CONSTRAINT PK_CHECKLIST_PERGUNTAS PRIMARY KEY (CODIGO);
-- Cria um UNIQUE igual ao que era a PK anteriormente.
ALTER TABLE CHECKLIST_PERGUNTAS ADD CONSTRAINT UNICA_PERGUNTA_POR_MODELO UNIQUE (COD_UNIDADE, COD_CHECKLIST_MODELO, CODIGO);

-- Recria FKs que apontam para CHECKLIST_PERGUNTAS.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD CONSTRAINT FK_CHECKLIST_ALTERNATIVA_PERGUNTA_PERGUNTA
FOREIGN KEY (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_PERGUNTA) REFERENCES CHECKLIST_PERGUNTAS (COD_UNIDADE, COD_CHECKLIST_MODELO, CODIGO);
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- ALTERA CHAVE PRIMÁRIA UTILIZADA NA TABELA DE CHECKLIST_ALTERNATIVA_PERGUNTA
-- Tabelas que precisarão serem migradas, atualizando o código da alternativa, por consequência:
-- 1) CHECKLIST_ORDEM_SERVICO_ITENS
-- 2) CHECKLIST_RESPOSTAS

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- Primeiro dropa as chaves que apontam para essa tabela.
ALTER TABLE CHECKLIST_RESPOSTAS DROP CONSTRAINT FK_CHECKLIST_RESPOSTAS_ALTERNATIVA_PERGUNTA;
ALTER TABLE CHECKLIST_RESPOSTAS DROP CONSTRAINT PK_CHECKLIST_RESPOSTAS;
-- Agora podemos remover a PK.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA DROP CONSTRAINT PK_CHECKLIST_ALTERNATIVA_PERGUNTA;

-- Iremos precisar do código antigo para realizar a migração de vínculos na tabela de respostas.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA RENAME CODIGO TO CODIGO_ANTIGO;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD COLUMN CODIGO BIGSERIAL;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ALTER COLUMN CODIGO SET NOT NULL;
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD CONSTRAINT PK_CHECKLIST_ALTERNATIVA_PERGUNTA PRIMARY KEY (CODIGO);

-- Vamos criar um unique se baseando no que era a PK anterior. Isso dá segurança para as queries antigas que realizam
-- um join se baseando nesses valores.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD CONSTRAINT UNICA_ALTERNATIVA_POR_PERGUNTA
UNIQUE (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_PERGUNTA, CODIGO);
-- ########################################################################################################

-- ########################################################################################################
-- 1) CHECKLIST_ORDEM_SERVICO_ITENS -> Atualiza o código das alternativas
-- Essa tabela não possuia nem FK para o código da alternativa, de qualquer modo, iremos migrar para os novos
-- códigos e já criar a FK. A migração será semelhante a realizada na tabela CHECKLIST_RESPOSTAS
-- Dropa a constraint de unique e de PK.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP CONSTRAINT UNIQUE_CHECKLIST_ORDEM_SERVICO_ITENS;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_ITENS;

-- Iremos utilizar o código antigo para realizar o migration.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS RENAME COD_ALTERNATIVA TO COD_ALTERNATIVA_ANTIGO;
-- Iremos inserir

-- Adiciona nova coluna de código na tabela de itens da OS para salvarmos os novos códigos das alternativas e na
-- sequência já popula a coluna com base no que era a chave antiga da tabela.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD COLUMN COD_ALTERNATIVA BIGINT;
UPDATE CHECKLIST_ORDEM_SERVICO_ITENS COSI
SET COD_ALTERNATIVA =
(SELECT CODIGO
 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
 WHERE CAP.CODIGO_ANTIGO = COSI.COD_ALTERNATIVA_ANTIGO
       AND CAP.COD_UNIDADE = COSI.COD_UNIDADE
       AND CAP.COD_CHECKLIST_MODELO = COSI.COD_CHECKLIST_MODELO
       AND CAP.COD_PERGUNTA = COSI.COD_PERGUNTA);
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ALTER COLUMN COD_ALTERNATIVA SET NOT NULL;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP COLUMN COD_ALTERNATIVA_ANTIGO;

-- Agora recriamos a PK e o unique, só que invertidos. O código que era um UNIQUE passará a ser PK e a PK que era
-- composta será um UNIQUE apenas para garantirmos que os JOINs de consultas antigas continuem funcionando e não
-- corram o risco de ter algum problema de inconsistência.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD CONSTRAINT PK_CHECKLIST_ORDEM_SERVICO_ITENS PRIMARY KEY (CODIGO);
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS ADD CONSTRAINT ITEM_OS_NAO_REPETIDO_POR_OS
UNIQUE (COD_OS, COD_PERGUNTA, COD_ALTERNATIVA, COD_UNIDADE);
-- ########################################################################################################

-- ########################################################################################################
-- 2) CHECKLIST_RESPOSTAS -> Atualiza o código das alternativas
-- Iremos utilizar o código antigo para realizar o migration.
ALTER TABLE CHECKLIST_RESPOSTAS RENAME COD_ALTERNATIVA TO COD_ALTERNATIVA_ANTIGO;

-- Adiciona nova coluna de código na tabela de respostas para salvarmos os novos códigos das alternativas e na sequência
-- já popula a coluna com base no que era a chave antiga da tabela.
ALTER TABLE CHECKLIST_RESPOSTAS ADD COLUMN COD_ALTERNATIVA BIGINT;
UPDATE CHECKLIST_RESPOSTAS CR
SET COD_ALTERNATIVA =
(SELECT CODIGO
 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
 WHERE CAP.CODIGO_ANTIGO = CR.COD_ALTERNATIVA_ANTIGO
       AND CAP.cod_unidade = CR.cod_unidade
       AND CAP.cod_checklist_modelo = CR.cod_checklist_modelo
       AND CAP.cod_pergunta = CR.cod_pergunta
       AND NEXTVAL('QUERY_PROGRESS_2') != 0);
ALTER TABLE CHECKLIST_RESPOSTAS ALTER COLUMN COD_ALTERNATIVA SET NOT NULL;
ALTER TABLE CHECKLIST_RESPOSTAS DROP COLUMN COD_ALTERNATIVA_ANTIGO;

-- Cria nova PK para a tabela de respostas apenas utilizando código da alternativa e do checklist.
ALTER TABLE CHECKLIST_RESPOSTAS ADD CONSTRAINT PK_CHECKLIST_RESPOSTAS PRIMARY KEY (COD_CHECKLIST, COD_ALTERNATIVA);

-- Recria as chaves que utilizam o código da alternativa em sua composição.
ALTER TABLE CHECKLIST_RESPOSTAS ADD CONSTRAINT FK_CHECKLIST_RESPOSTAS_ALTERNATIVA_PERGUNTA
FOREIGN KEY (COD_ALTERNATIVA) REFERENCES CHECKLIST_ALTERNATIVA_PERGUNTA(CODIGO);

-- Vamos criar um unique se baseando no que era a PK anterior. Isso dá segurança para as queries antigas que realizam
-- um join se baseando nesses valores.
ALTER TABLE CHECKLIST_RESPOSTAS ADD CONSTRAINT UNICA_RESPOSTA_ALTERNATIVA
UNIQUE (COD_UNIDADE, COD_CHECKLIST_MODELO, COD_CHECKLIST, COD_PERGUNTA, COD_ALTERNATIVA);
-- ########################################################################################################

-- O código antigo das alternativas não é mais necessário na tabela de alternativas de uma pergunta pois já realizamos
-- a migração das tabelas CHECKLIST_RESPOSTAS e CHECKLIST_ORDEM_SERVICO_ITENS.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA DROP COLUMN CODIGO_ANTIGO;
-- Remove coluna provisória.
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS DROP COLUMN COD_CHECKLIST_MODELO;
-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################

-- Agora podemos recriar a view.
CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS SELECT os.codigo AS cod_os,
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
    cosi.codigo,
    colab.nome AS nome_realizador_checklist,
    c.tipo AS tipo_checklist
   FROM (((((((((checklist c
     JOIN colaborador colab ON ((colab.cpf = c.cpf_colaborador)))
     JOIN veiculo v ON (((v.placa)::text = (c.placa_veiculo)::text)))
     JOIN checklist_ordem_servico os ON (((c.codigo = os.cod_checklist) AND (c.cod_unidade = os.cod_unidade))))
     JOIN checklist_ordem_servico_itens cosi ON (((os.codigo = cosi.cod_os) AND (os.cod_unidade = cosi.cod_unidade))))
     JOIN checklist_perguntas cp ON ((((cp.cod_unidade = os.cod_unidade) AND (cp.codigo = cosi.cod_pergunta)) AND (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
     JOIN prioridade_pergunta_checklist ppc ON (((ppc.prioridade)::text = (cp.prioridade)::text)))
     JOIN checklist_alternativa_pergunta cap ON (((((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND (cap.cod_pergunta = cp.codigo)) AND (cap.codigo = cosi.cod_alternativa))))
     JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND (cr.cod_alternativa = cap.codigo))))
     LEFT JOIN colaborador co ON ((co.cpf = cosi.cpf_mecanico)));
COMMENT ON VIEW ESTRATIFICACAO_OS
IS 'View que compila as informações das OS e seus itens';

-- ########################################################################################################
-- ########################################################################################################
-- ##########  ADICIONA COLUNA NO CHECKLIST PARA SABERMOS SE UMA ALTERNATIVA É DO TIPO OUTROS  ############
-- ########################################################################################################
-- ########################################################################################################
-- Modelos originais.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA ADD COLUMN ALTERNATIVA_TIPO_OUTROS BOOLEAN DEFAULT FALSE NOT NULL;
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA SET ALTERNATIVA_TIPO_OUTROS = TRUE WHERE ALTERNATIVA = 'Outros';

-- Modelos ProLog.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG ADD COLUMN ALTERNATIVA_TIPO_OUTROS BOOLEAN DEFAULT FALSE NOT NULL;
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG SET ALTERNATIVA_TIPO_OUTROS = TRUE WHERE ALTERNATIVA = 'Outros';
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ########### CRIA FUNCTION PARA BUSCAR O FAROL DO CHECKLIST #############################################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_FAROL_CHECKLIST(
  F_COD_UNIDADE BIGINT,
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE,
  F_ITENS_CRITICOS_RETROATIVOS BOOLEAN,
  F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    DATA                               DATE,
    PLACA                              TEXT,
    COD_CHECKLIST_SAIDA                BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_SAIDA   TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_SAIDA         BIGINT,
    NOME_COLABORADOR_CHECKLIST_SAIDA   TEXT,
    COD_CHECKLIST_RETORNO              BIGINT,
    DATA_HORA_ULTIMO_CHECKLIST_RETORNO TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO_RETORNO       BIGINT,
    NOME_COLABORADOR_CHECKLIST_RETORNO TEXT,
    CODIGO_PERGUNTA                    BIGINT,
    DESCRICAO_PERGUNTA                 TEXT,
    DESCRICAO_ALTERNATIVA              TEXT,
    ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
    DESCRICAO_ALTERNATIVA_TIPO_OUTROS  TEXT,
    CODIGO_ITEM_CRITICO                BIGINT,
    DATA_HORA_APONTAMENTO_ITEM_CRITICO TIMESTAMP WITHOUT TIME ZONE)
LANGUAGE SQL
AS
$$
DECLARE
  CHECKLIST_TIPO_SAIDA CHAR := 'S';
  CHECKLIST_TIPO_RETORNO CHAR := 'R';
  CHECKLIST_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  ORDEM_SERVICO_ABERTA CHAR := 'A';
  ORDEM_SERVICO_ITEN_PENDENDTE CHAR := 'P';
BEGIN
  WITH ULTIMOS_CHECKLISTS_VEICULOS AS (
      SELECT
        INNERTABLE.DATA,
        INNERTABLE.PLACA,
        INNERTABLE.COD_CHECKLIST_SAIDA,
        INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
        CS.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_SAIDA,
        COS.NOME                AS NOME_COLABORADOR_CHECKLIST_SAIDA,
        INNERTABLE.COD_CHECKLIST_RETORNO,
        INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
        CR.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_RETORNO,
        COR.NOME                AS NOME_COLABORADOR_CHECKLIST_RETORNO
      FROM
        (SELECT
           G.DAY::DATE                                       AS DATA,
           V.PLACA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.codigo END)                              AS COD_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.codigo END)                              AS COD_CHECKLIST_RETORNO,
           MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
             THEN C.DATA_HORA END) AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA_ULTIMO_CHECKLIST_RETORNO
         FROM VEICULO V
           CROSS JOIN GENERATE_SERIES(F_DATA_INICIAL, F_DATA_FINAL, '1 DAY') G(DAY)
           LEFT JOIN CHECKLIST C
             ON C.PLACA_VEICULO = V.PLACA AND G.DAY::DATE = (C.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE
         WHERE V.COD_UNIDADE = F_COD_UNIDADE AND V.STATUS_ATIVO = TRUE
         GROUP BY 1, 2
         ORDER BY 1, 2) AS INNERTABLE
        LEFT JOIN CHECKLIST CS ON CS.CODIGO = INNERTABLE.COD_CHECKLIST_SAIDA
        LEFT JOIN CHECKLIST CR ON CR.CODIGO = INNERTABLE.COD_CHECKLIST_RETORNO
        LEFT JOIN COLABORADOR COS ON COS.CPF = CS.CPF_COLABORADOR
        LEFT JOIN COLABORADOR COR ON COR.CPF = CR.CPF_COLABORADOR
      ORDER BY INNERTABLE.DATA, INNERTABLE.PLACA
  )

  SELECT
    Q.DATA,
    Q.PLACA,
    Q.COD_CHECKLIST_SAIDA,
    Q.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
    Q.COD_CHECKLIST_MODELO_SAIDA,
    Q.NOME_COLABORADOR_CHECKLIST_SAIDA,
    Q.COD_CHECKLIST_RETORNO,
    Q.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
    Q.COD_CHECKLIST_MODELO_RETORNO,
    Q.NOME_COLABORADOR_CHECKLIST_RETORNO,
    Q.CODIGO_PERGUNTA,
    Q.DESCRICAO_PERGUNTA,
    Q.DESCRICAO_ALTERNATIVA,
    CASE
    WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA
         OR Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
      THEN TRUE
    ELSE FALSE
    END AS ALTERNATIVA_TIPO_OUTROS,
    CASE
    WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA
         OR Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
      THEN CR.RESPOSTA
    ELSE NULL
    END AS DESCRICAO_ALTERNATIVA_TIPO_OUTROS,
    Q.CODIGO_ITEM_CRITICO,
    Q.DATA_HORA_APONTAMENTO_ITEM_CRITICO
  FROM
    (SELECT
       UCV.DATA,
       UCV.PLACA,
       UCV.COD_CHECKLIST_SAIDA,
       UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
       UCV.COD_CHECKLIST_MODELO_SAIDA,
       UCV.NOME_COLABORADOR_CHECKLIST_SAIDA,
       UCV.COD_CHECKLIST_RETORNO,
       UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
       UCV.COD_CHECKLIST_MODELO_RETORNO,
       UCV.NOME_COLABORADOR_CHECKLIST_RETORNO,
       CP.CODIGO       AS CODIGO_PERGUNTA,
       CP.PERGUNTA     AS DESCRICAO_PERGUNTA,
       CAP.ALTERNATIVA AS DESCRICAO_ALTERNATIVA,
       CAP.CODIGO      AS CODIGO_ALTERNATIVA,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                  AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA,
       CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
                  AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND CAP.ALTERNATIVA_TIPO_OUTROS)
         THEN TRUE
       ELSE FALSE
       END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO,
       CASE WHEN CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN COSI.CODIGO
       ELSE NULL
       END             AS CODIGO_ITEM_CRITICO,
       CASE
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA
       WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
         THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO
       ELSE NULL
       END             AS DATA_HORA_APONTAMENTO_ITEM_CRITICO
     FROM ULTIMOS_CHECKLISTS_VEICULOS UCV
       LEFT JOIN CHECKLIST_ORDEM_SERVICO COS
         ON COS.COD_CHECKLIST IN (UCV.COD_CHECKLIST_SAIDA, UCV.COD_CHECKLIST_RETORNO)
            AND COS.STATUS = ORDEM_SERVICO_ABERTA
            AND COS.CODIGO IS NOT NULL
       LEFT JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
         ON COS.CODIGO = COSI.COD_OS
            AND COS.COD_UNIDADE = COSI.COD_UNIDADE
            AND COSI.STATUS_RESOLUCAO = ORDEM_SERVICO_ITEN_PENDENDTE
       LEFT JOIN CHECKLIST_PERGUNTAS CP
         ON CP.CODIGO = COSI.COD_PERGUNTA
            AND CP.PRIORIDADE IS NOT NULL
            AND CP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
       LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
         ON CAP.COD_PERGUNTA = CP.CODIGO
            AND CAP.CODIGO = COSI.COD_ALTERNATIVA) AS Q
    LEFT JOIN CHECKLIST_RESPOSTAS CR
      ON (Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA
          AND Q.COD_CHECKLIST_SAIDA = CR.COD_CHECKLIST
          AND Q.CODIGO_ALTERNATIVA = CR.COD_ALTERNATIVA)
         OR
         (Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
          AND Q.COD_CHECKLIST_RETORNO = CR.COD_CHECKLIST
          AND Q.CODIGO_ALTERNATIVA = CR.COD_ALTERNATIVA)
  ORDER BY Q.DATA, Q.PLACA, Q.CODIGO_PERGUNTA;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ########### CRIA FUNCTION PARA BUSCAR QTD DE ITENS DE OS ABERTOS POR PLACA #############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_QTD_ITENS_OS(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_ITENS_OS_ABERTOS BOOLEAN,
  F_LIMIT            INTEGER,
  F_OFFSET           INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                    TEXT,
    KM_ATUAL_VEICULO                 BIGINT,
    TOTAL_ITENS_ABERTOS              BIGINT,
    ITENS_PRIORIDADE_CRITICA_ABERTOS BIGINT,
    ITENS_PRIORIDADE_ALTA_ABERTOS    BIGINT,
    ITENS_PRIORIDADE_BAIXA_ABERTOS   BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_ITEM_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  TIPO_ITEM_PRIORIDADE_ALTA    TEXT := 'ALTA';
  TIPO_ITEM_PRIORIDADE_BAIXA   TEXT := 'BAIXA';
  STATUS_ITEM_OS               CHAR := CASE WHEN F_ITENS_OS_ABERTOS THEN 'P' ELSE 'R' END;
  STATUS_OS                    CHAR := CASE WHEN F_ITENS_OS_ABERTOS THEN 'A' ELSE 'F' END;
BEGIN
  RETURN QUERY SELECT
                 V.PLACA :: TEXT      AS PLACA_VEICULO,
                 V.KM                 AS KM_ATUAL_VEICULO,
                 COUNT(CP.PRIORIDADE) AS TOTAL_ITENS_ABERTOS,
                 COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_CRITICA
                   THEN 1 END)        AS ITENS_PRIORIDADE_CRITICA_ABERTOS,
                 COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_ALTA
                   THEN 1 END)        AS ITENS_PRIORIDADE_ALTA_ABERTOS,
                 COUNT(CASE WHEN CP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_BAIXA
                   THEN 1 END)        AS ITENS_PRIORIDADE_BAIXA_ABERTOS
               FROM VEICULO V
                 JOIN CHECKLIST C
                   ON V.PLACA = C.PLACA_VEICULO
                 JOIN CHECKLIST_ORDEM_SERVICO COS
                   ON C.codigo = COS.cod_checklist
                 JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                   ON COS.CODIGO = COSI.COD_OS
                      AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                 JOIN CHECKLIST_PERGUNTAS CP
                   ON COSI.COD_PERGUNTA = CP.CODIGO
               WHERE V.COD_UNIDADE = F_COD_UNIDADE
                     AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_OS
                     AND COS.STATUS = STATUS_OS
                     AND CASE WHEN F_COD_TIPO_VEICULO IS NOT NULL THEN V.COD_TIPO = F_COD_TIPO_VEICULO ELSE TRUE END
                     AND CASE WHEN F_PLACA_VEICULO IS NOT NULL THEN V.PLACA = F_PLACA_VEICULO ELSE TRUE END
               GROUP BY V.PLACA
               ORDER BY
                 ITENS_PRIORIDADE_CRITICA_ABERTOS DESC,
                 ITENS_PRIORIDADE_ALTA_ABERTOS DESC,
                 ITENS_PRIORIDADE_BAIXA_ABERTOS DESC,
                 PLACA_VEICULO ASC
               LIMIT F_LIMIT
               OFFSET F_OFFSET;
END;
$$;

-- ########################################################################################################
-- ########################################################################################################
-- ########################################################################################################
-- ##########  CRIA FUNCTION PARA BUSCAS DOS CHECKLISTS REALIZADOS COM O TOTAL DE ITENS OK|NOK ############
-- ########################################################################################################
-- Function para buscar os checklist realizados com os totais de itens ok e noks
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(
  F_COD_UNIDADE BIGINT,
  F_COD_EQUIPE BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO VARCHAR(7),
  F_DATA_INICIAL DATE,
  F_DATA_FINAL DATE,
  F_TIMEZONE TEXT,
  F_LIMIT INTEGER,
  F_OFFSET BIGINT)
  RETURNS TABLE(
    CODIGO BIGINT,
    DATA_HORA TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO BIGINT,
    KM_VEICULO BIGINT,
    TEMPO_REALIZACAO BIGINT,
    CPF_COLABORADOR BIGINT,
    PLACA_VEICULO VARCHAR(7),
    TIPO CHAR,
    NOME VARCHAR(255),
    TOTAL_ITENS_OK BIGINT,
    TOTAL_ITENS_NOK BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE
  RESPOSTA_OK VARCHAR(2) := 'OK';
  F_HAS_EQUIPE INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 1 ELSE 0 END;
  F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 1 ELSE 0 END;
  F_HAS_PLACA_VEICULO INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY SELECT
                 C.CODIGO,
                 C.DATA_HORA AT TIME ZONE F_TIMEZONE AS DATA_HORA,
                 C.COD_CHECKLIST_MODELO,
                 C.KM_VEICULO,
                 C.TEMPO_REALIZACAO,
                 C.CPF_COLABORADOR,
                 C.PLACA_VEICULO,
                 C.TIPO,
                 CO.NOME,
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
               WHERE (C.DATA_HORA AT TIME ZONE F_TIMEZONE)::DATE >= F_DATA_INICIAL
                     AND (C.DATA_HORA AT TIME ZONE F_TIMEZONE)::DATE <= F_DATA_FINAL
                     AND C.COD_UNIDADE = F_COD_UNIDADE
                     AND (F_HAS_EQUIPE = 1 OR E.CODIGO = F_COD_EQUIPE)
                     AND (F_HAS_COD_TIPO_VEICULO = 1 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
                     AND (F_HAS_PLACA_VEICULO = 1 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
               ORDER BY DATA_HORA DESC
               LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- Function para buscar os checklists do colaborador com a contagem de itens ok e noks
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(
  F_CPF_COLABORADOR BIGINT,
  F_DATA_INICIAL    DATE,
  F_DATA_FINAL      DATE,
  F_TIMEZONE        TEXT,
  F_LIMIT           INTEGER,
  F_OFFSET          BIGINT)
  RETURNS TABLE(
    CODIGO               BIGINT,
    DATA_HORA            TIMESTAMP WITHOUT TIME ZONE,
    COD_CHECKLIST_MODELO BIGINT,
    KM_VEICULO           BIGINT,
    TEMPO_REALIZACAO     BIGINT,
    CPF_COLABORADOR      BIGINT,
    PLACA_VEICULO        VARCHAR(7),
    TIPO                 CHAR,
    NOME                 VARCHAR(255),
    TOTAL_ITENS_OK       BIGINT,
    TOTAL_ITENS_NOK      BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE
  RESPOSTA_OK        VARCHAR(2) := 'OK';
  F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
  F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
BEGIN
  RETURN QUERY
  SELECT
    C.CODIGO,
    C.DATA_HORA AT TIME ZONE F_TIMEZONE AS DATA_HORA,
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
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION ;