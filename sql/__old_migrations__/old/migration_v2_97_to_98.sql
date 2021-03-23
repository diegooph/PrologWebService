BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--########################## RETIRAR SÍMBOLOS DE CATEGORIZAÇÃO DOS NOMES DAS PERMISSÕES  ###############################
--######################################################################################################################
--######################################################################################################################
--PL-2074
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)' WHERE CODIGO = 142 AND COD_PILAR = 1;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)' WHERE CODIGO = 143 AND COD_PILAR = 1;
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Descarte (Estoque ou Veículo ou Análise -> Descarte)' WHERE CODIGO = 144 AND COD_PILAR = 1;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################### CRIAR CONSTRAINT PARA IMPEDIR VIDA ATUAL > VIDA TOTAL NA TABELA DE PNEU  #########################
--######################################################################################################################
--######################################################################################################################
--PL-2032
ALTER TABLE PNEU_DATA
  ADD CONSTRAINT CHECK_VIDA_ATUAL_MENOR_IGUAL_VIDA_TOTAL CHECK (VIDA_ATUAL <= VIDA_TOTAL);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############## ALTERA A FUNCTION DE LISTAGEM DE MODELOS DE CHECKLIST PARA IGNORAR VÍNCULOS COM CARGOS ################
--######################################################################################################################
--######################################################################################################################
--PL-2037
DROP FUNCTION FUNC_CHECKLIST_GET_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT, F_CARGOS TEXT);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    MODELO          TEXT,
    COD_MODELO      BIGINT,
    COD_UNIDADE     BIGINT,
    NOME_CARGO      TEXT,
    TIPO_VEICULO    TEXT,
    TOTAL_PERGUNTAS BIGINT,
    STATUS_ATIVO    BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  CM.NOME          AS MODELO,
  CM.CODIGO        AS COD_MODELO,
  CM.COD_UNIDADE   AS COD_UNIDADE,
  F.NOME           AS NOME_CARGO,
  VT.NOME          AS TIPO_VEICULO,
  COUNT(CP.CODIGO) AS TOTAL_PERGUNTAS,
  CM.STATUS_ATIVO  AS STATUS_ATIVO
FROM CHECKLIST_MODELO CM
  JOIN CHECKLIST_PERGUNTAS CP ON CM.COD_UNIDADE = CP.COD_UNIDADE
                                 AND CM.CODIGO = CP.COD_CHECKLIST_MODELO
                                 AND CP.STATUS_ATIVO = TRUE
  LEFT JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.COD_UNIDADE = CMF.COD_UNIDADE
                                           AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO
  LEFT JOIN FUNCAO F ON CMF.COD_FUNCAO = F.CODIGO
  LEFT JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CM.COD_UNIDADE = CMVT.COD_UNIDADE
                                                  AND CM.CODIGO = CMVT.COD_MODELO
  LEFT JOIN VEICULO_TIPO VT ON CMVT.COD_TIPO_VEICULO = VT.CODIGO
WHERE CM.COD_UNIDADE = F_COD_UNIDADE
GROUP BY CM.NOME, CM.CODIGO, CM.COD_UNIDADE, F.NOME, VT.NOME, CM.STATUS_ATIVO
ORDER BY CM.STATUS_ATIVO DESC, CM.CODIGO ASC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### MELHORIAS NA GESTÃO DOS TOKENS PARA CHECKLIST OFFLINE ####################################
--######################################################################################################################
--######################################################################################################################
--PL-2051

-- INSERE TOKENS PARA TODAS AS UNIDADES JÁ CADASTRADAS NO SISTEMA
INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE (COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
SELECT CODIGO AS COD_UNIDADE, F_RANDOM_STRING(64) AS TOKEN_SINCRONIZACAO_CHECKLIST
FROM UNIDADE
WHERE CODIGO NOT IN(SELECT COD_UNIDADE FROM CHECKLIST_OFFLINE_DADOS_UNIDADE);

-- CRIA FUNCTION PARA INSERIR UM TOKEN DE CHECKLIST OFFLINE ATRAVÉS DA TRIGGER AO INSERIR UMA UNIDADE
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE()
  RETURNS TRIGGER AS $TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
BEGIN
  INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE (COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
  VALUES (NEW.CODIGO, F_RANDOM_STRING(64));
  RETURN NEW;
END;
$TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
LANGUAGE plpgsql;

-- CRIA TRIGGER PARA INSERIR UM TOKEN DE CHECKLIST OFFLINE AO INSERIR UMA UNIDADE
CREATE CONSTRAINT TRIGGER TG_UNIDADE_INSERE_TOKEN_CHECKLIST_OFFLINE
  AFTER INSERT
  ON UNIDADE
  DEFERRABLE
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE();


-- CRIA FUNCTION GENÉRICA PARA BLOQUEAR ALTERAÇÕES EM TABELAS
CREATE OR REPLACE FUNCTION TG_FUNC_BLOQUEIO()
  RETURNS TRIGGER AS $$
BEGIN
  RAISE EXCEPTION 'A OPERAÇÃO DE % NA TABELA % ESTÁ BLOQUEADA!', TG_OP, TG_RELNAME;
END;
$$
LANGUAGE plpgsql;

-- CRIA TRIGGER PARA IMPEDIR A DELEÇÃO DE REGISTROS DE TOKEN PARA CHECKLIST OFFLINE
CREATE TRIGGER TG_BLOQUEIO_DELECAO_TOKEN_CHECKLIST_OFFLINE
  BEFORE DELETE
  ON CHECKLIST_OFFLINE_DADOS_UNIDADE
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_BLOQUEIO();

-- CRIA FUNCTION QUE BLOQUEIA O DECREMENTO DE VERSÃO DOS DADOS DE CHECKLIST OFFLINE
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_BLOQUEIA_DECREMENTO_VERSAO_DADOS_OFFLINE()
  RETURNS TRIGGER AS $TG_BLOQUEIA_DECREMENTO_VERSAO_CHECKLIST_OFFLINE$
BEGIN
  IF (NEW.VERSAO_DADOS < OLD.VERSAO_DADOS)
  THEN
    RAISE EXCEPTION 'NÃO É PERMITIDO DECREMENTAR A VERSÃO DOS DADOS DE CHECKLIST OFFLINE!'
    USING HINT = 'A VERSÃO ATUAL É ' || OLD.VERSAO_DADOS || ' E VOCÊ ESTÁ TENTANDO ALTERAR PARA ' || NEW.VERSAO_DADOS || '.';
  END IF;
  RETURN NEW;
END;
$TG_BLOQUEIA_DECREMENTO_VERSAO_CHECKLIST_OFFLINE$
LANGUAGE PLPGSQL;

-- CRIA TRIGGER PARA BLOQUEAR O DECREMENTO DE VERSÃO DOS DADOS DE CHECKLIST OFFLINE
CREATE TRIGGER TG_BLOQUEIA_DECREMENTO_VERSAO_CHECKLIST_OFFLINE
  BEFORE UPDATE ON CHECKLIST_OFFLINE_DADOS_UNIDADE
  FOR EACH ROW
  EXECUTE PROCEDURE TG_FUNC_CHECKLIST_BLOQUEIA_DECREMENTO_VERSAO_DADOS_OFFLINE();

-- CRIA FUNCTION QUE INCREMENTA A VERSÃO DOS DADOS DE CHECKLIST OFFLINE CASO A EMPRESA TENHA SIDO LIBERADA
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_UPDATE_VERSAO_DADOS_OFFLINE_EMPRESA_LIBERADA()
  RETURNS TRIGGER AS $TG_FUNC_UPDATE_VERSAO_DADOS_CHECKLIST_OFFLINE_EMPRESA_LIBERADA$
BEGIN
  UPDATE CHECKLIST_OFFLINE_DADOS_UNIDADE SET VERSAO_DADOS = VERSAO_DADOS + 1
      WHERE COD_UNIDADE IN (SELECT COD_UNIDADE FROM UNIDADE WHERE COD_EMPRESA = OLD.COD_EMPRESA);
  RETURN NEW;
END;
$TG_FUNC_UPDATE_VERSAO_DADOS_CHECKLIST_OFFLINE_EMPRESA_LIBERADA$
LANGUAGE PLPGSQL;

-- CRIA TRIGGER PARA INCREMENTAR A VERSÃO DOS DADOS DE CHECKLIST OFFLINE CASO A EMPRESA TENHA SIDO LIBERADA
CREATE TRIGGER TG_UPDATE_VERSAO_DADOS_CHECKLIST_OFFLINE_EMPRESA_LIBERADA
  AFTER DELETE ON CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA
  FOR EACH ROW
  EXECUTE PROCEDURE TG_FUNC_CHECKLIST_UPDATE_VERSAO_DADOS_OFFLINE_EMPRESA_LIBERADA();
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2146
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[]);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    UNIDADE                 TEXT,
    "CÓDIGO DO CHECKLIST"   TEXT,
    "NOME DO CHECKLIST"     TEXT,
    ATIVO                   TEXT,
    "CÓDIGO DA PERGUNTA"    TEXT,
    PERGUNTA                TEXT,
    "CÓDIGO DA ALTERNATIVA" TEXT,
    ALTERNATIVA             TEXT,
    "TIPO DE RESPOSTA"      TEXT,
    PRIORIDADE              TEXT
  )
LANGUAGE SQL
AS $$
SELECT U.NOME                                              AS NOME_UNIDADE,
       CM.CODIGO::TEXT                                     AS COD_MODELO_CHECKLIST,
       CM.NOME                                             AS NOME_MODELO,
       F_IF(CM.STATUS_ATIVO, 'SIM' :: TEXT, 'NÃO')         AS ATIVO,
       CP.CODIGO::TEXT                                     AS COD_PERGUNTA,
       CP.PERGUNTA                                         AS PERGUNTA,
       CAP.CODIGO::TEXT                                    AS COD_ALTERNATIVA,
       CAP.ALTERNATIVA                                     AS ALTERNATIVA,
       F_IF(CP.SINGLE_CHOICE, 'ÚNICA' :: TEXT, 'MÚLTIPLA') AS TIPO_DE_RESPOSTA,
       CAP.PRIORIDADE                                      AS PRIORIDADE
FROM CHECKLIST_PERGUNTAS CP
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CP.COD_UNIDADE = CAP.COD_UNIDADE
                  AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
                  AND CP.CODIGO = CAP.COD_PERGUNTA
         JOIN CHECKLIST_MODELO CM
              ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
         JOIN UNIDADE U
              ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CP.STATUS_ATIVO = TRUE
  AND CAP.STATUS_ATIVO = TRUE
ORDER BY U.NOME, CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
$$;

--######################################################################################################################
--######################################################################################################################
--################################ ALTERA A ESTRUTURA PARA CRIAR UM CRUD DE CARGOS #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-2110

-- DROPA A FUNCTION ANTIGA PARA RECRIAR COM OUTRO NOME MAIS INTUITIVO
DROP FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS(bigint);

-- RECRIA A FUNCTION QUE LISTA OS CARGOS POR UNIDADE
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS_UNIDADE(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO  BIGINT,
    NOME_CARGO TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT DISTINCT
    F.CODIGO       AS COD_CARGO,
    F.NOME :: TEXT AS NOME_CARGO
  FROM FUNCAO F
    JOIN UNIDADE U ON U.COD_EMPRESA = F.COD_EMPRESA
  WHERE U.CODIGO = F_COD_UNIDADE
  ORDER BY 2 ASC;
END;
$$;

-- CRIA A FUNÇÃO QUE LISTA OS CARGOS POR EMPRESA
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS_EMPRESA(
  F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    COD_CARGO                    BIGINT,
    NOME_CARGO                   TEXT,
    QTD_COLABORADORES_VINCULADOS BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT DISTINCT
    F.CODIGO                                  AS COD_CARGO,
    F.NOME :: TEXT                            AS NOME_CARGO,
    (SELECT COUNT(*)
     FROM COLABORADOR C
     WHERE C.COD_FUNCAO = F.CODIGO
           AND C.COD_EMPRESA = F_COD_EMPRESA) AS QTD_COLABORADORES_VINCULADOS
  FROM FUNCAO F
  WHERE F.COD_EMPRESA = F_COD_EMPRESA
  ORDER BY 2 ASC;
END;
$$;

-- DROPA AS DEPENDÊNCIAS DA TABELA FUNCAO
DROP VIEW view_produtividade_extrato;
DROP VIEW view_extrato_indicadores;
DROP FUNCTION FUNC_CARGOS_GET_PERMISSOES_DETALHADAS(BIGINT, BIGINT);

-- ALTERA O NOME DA TABELA FUNCAO PARA FUNCAO_DATA PARA DELEÇÃO LÓGICA
ALTER TABLE FUNCAO RENAME TO FUNCAO_DATA;

-- ADICIONA COLUNA DELETADO PARA DELEÇÃO LÓGICA
ALTER TABLE FUNCAO_DATA ADD COLUMN DELETADO BOOLEAN DEFAULT FALSE NOT NULL;

-- ADICIONA CAMPOS DE DATA E HORA DE DELEÇÃO
ALTER TABLE FUNCAO_DATA ADD DATA_HORA_DELETADO TIMESTAMP WITH TIME ZONE NULL;
ALTER TABLE FUNCAO_DATA ADD DATA_HORA_UPDATE TIMESTAMP WITH TIME ZONE NULL;

-- CRIA A CONSTRAINT QUE OBRIGA O PREENCHIMENTO DO CAMPO DATA_HORA_DELETADO CASO DELETADO = TRUE
ALTER TABLE FUNCAO_DATA
  ADD CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO CHECK(  (NOT DELETADO) OR (DATA_HORA_DELETADO IS NOT NULL) );

-- ALTERA O TIPO DA COLUNA DE NOME DO CARGO PARA CITEXT
CREATE EXTENSION IF NOT EXISTS CITEXT;
ALTER TABLE FUNCAO_DATA ALTER COLUMN NOME TYPE CITEXT;

-- REMOVE NOME DE CARGO DUPLICADO DO BANCO DE DADOS
DELETE FROM QUIZ_MODELO_FUNCAO WHERE COD_FUNCAO_COLABORADOR = 331;
DELETE FROM RESTRICAO_TREINAMENTO WHERE COD_FUNCAO = 331;
DELETE FROM FUNCAO_DATA WHERE CODIGO = 331 AND COD_EMPRESA = 2;

--ADICIONA UM ÍNDICE ÚNICO PARA OS NOMES DE CARGOS QUE NÃO FORAM DELETADOS
CREATE UNIQUE INDEX UNIQUE_CARGO_EMPRESA
 ON FUNCAO_DATA (NOME, COD_EMPRESA)
 WHERE (DELETADO IS FALSE);

-- ADICIONA CONSTRAINT DE LIMITE DE CARACTERES PARA NOME
ALTER TABLE FUNCAO_DATA ADD CONSTRAINT NOME_LIMITE_CARACTERES CHECK (char_length(nome) <= 40);

-- ADICIONA O COD_COLABORADOR NA TABELA TOKEN_AUTENTICACAO
ALTER TABLE TOKEN_AUTENTICACAO ADD COD_COLABORADOR BIGINT NULL;
ALTER TABLE TOKEN_AUTENTICACAO
ADD CONSTRAINT FK_TOKEN_AUTENTICACAO_COD_COLABORADOR
FOREIGN KEY (COD_COLABORADOR) REFERENCES COLABORADOR_DATA (CODIGO) ON DELETE CASCADE ON UPDATE CASCADE;

-- ADICIONA O CÓDIGO DO COLABORADOR QUE REALIZOU O UPDATE NA TABELA FUNCAO_DATA PARA O LOGGING DA TABELA
ALTER TABLE FUNCAO_DATA ADD COD_COLABORADOR_UPDATE BIGINT NULL;
ALTER TABLE FUNCAO_DATA
ADD CONSTRAINT FK_FUNCAO_DATA_COD_COLABORADOR_UPDATE
FOREIGN KEY (COD_COLABORADOR_UPDATE) REFERENCES COLABORADOR_DATA (CODIGO);

-- CRIA A VIEW FUNCAO
CREATE OR REPLACE VIEW FUNCAO AS
  SELECT
    F.CODIGO,
    F.NOME,
    F.COD_EMPRESA
  FROM FUNCAO_DATA F
  WHERE F.DELETADO = FALSE;

-- RECRIA AS DEPENDÊNCIAS DA TABELA FUNCAO

-- RECRIA A VIEW_PRODUTIVIDADE_EXTRATO
create view view_produtividade_extrato as
  SELECT vmc.cod_unidade,
         c.matricula_ambev,
         m.data,
         vmc.cpf,
         c.nome                                                                 AS nome_colaborador,
         c.data_nascimento,
         f.nome                                                                 AS funcao,
         f.codigo                                                               AS cod_funcao,
         e.nome                                                                 AS nome_equipe,
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
         to_seconds((m.tempoprevistoroad) :: text)                              AS tempoprevistoroad,
         m.hrsai,
         m.hrentr,
         to_seconds((((m.hrentr - m.hrsai)) :: time without time zone) :: text) AS tempo_rota,
         to_seconds((m.tempointerno) :: text)                                   AS tempointerno,
         m.hrmatinal,
         tracking.apontamentos_ok,
         tracking.total_apontamentos                                            AS total_tracking,
         to_seconds((
                        CASE
                          WHEN (((m.hrsai) :: time without time zone < m.hrmatinal) OR
                                (m.hrmatinal = '00:00:00' :: time without time zone)) THEN um.meta_tempo_largada_horas
                          ELSE ((m.hrsai - (m.hrmatinal) :: interval)) :: time without time zone
                            END) :: text)                                       AS tempo_largada,
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
         to_seconds((um.meta_tempo_rota_horas) :: text)                         AS meta_tempo_rota_horas,
         to_seconds((um.meta_tempo_interno_horas) :: text)                      AS meta_tempo_interno_horas,
         to_seconds((um.meta_tempo_largada_horas) :: text)                      AS meta_tempo_largada_horas,
         to_seconds((um.meta_jornada_liquida_horas) :: text)                    AS meta_jornada_liquida_horas,
         CASE
           WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                 (((m.entrega) :: text <> 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)))
                   THEN (m.vlbateujornmot + m.vlnaobateujornmot)
           WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 (((m.entrega) :: text <> 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) AND
                 (m.fator <> (0) :: double precision)) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
           WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 (((m.entrega) :: text <> 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) AND
                 (m.fator <> (0) :: double precision)) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
           ELSE (0) :: real
             END                                                                AS valor_rota,
         (
             CASE
               WHEN (((m.entrega) :: text <> 'AS' :: text) AND ((m.cargaatual) :: text = 'Recarga' :: text)) THEN CASE
                                                                                                                    WHEN (
                     (c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista))
                                                                                                                            THEN m.vlrecargamot
                                                                                                                    WHEN (
                     (c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                     (m.fator <> (0) :: double precision)) THEN (m.vlrecargaaju / m.fator)
                                                                                                                    WHEN (
                     (c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                     (m.fator <> (0) :: double precision)) THEN (m.vlrecargaaju / m.fator)
                                                                                                                    ELSE (0) :: real
                 END
               ELSE (0) :: real
                 END +
             CASE
               WHEN (((m.entrega) :: text = 'AS' :: text) AND ((m.cargaatual) :: text = 'Recarga' :: text)) THEN CASE
                                                                                                                   WHEN (
                     (c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista))
                                                                                                                           THEN uv.rm_motorista_valor_as_recarga
                                                                                                                   WHEN (
                     (c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                     (m.fator <> (0) :: double precision)) THEN uv.rm_ajudante_valor_as_recarga
                                                                                                                   WHEN (
                     (c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                     (m.fator <> (0) :: double precision)) THEN uv.rm_ajudante_valor_as_recarga
                                                                                                                   ELSE (0) :: real
                 END
               ELSE (0) :: real
                 END)                                                           AS valor_recarga,
         CASE
           WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                 ((m.entrega) :: text <> 'AS' :: text) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND
                 ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN (
             ((m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota) :: double precision) /
              (m.fator) :: double precision) - ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot))
           WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                 (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                   THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota) :: double precision) /
                          (m.fator) :: double precision) -
                         (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
           WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                 (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                   THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota) :: double precision) /
                          (m.fator) :: double precision) -
                         (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
           ELSE (0) :: double precision
             END                                                                AS valor_diferenca_eld,
         CASE
           WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                 ((m.entrega) :: text = 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN CASE
                                                                                                                WHEN (m.entregas = 1)
                                                                                                                        THEN uv.rm_motorista_valor_as_1_entrega
                                                                                                                WHEN (m.entregas = 2)
                                                                                                                        THEN uv.rm_motorista_valor_as_2_entregas
                                                                                                                WHEN (m.entregas = 3)
                                                                                                                        THEN uv.rm_motorista_valor_as_3_entregas
                                                                                                                WHEN (m.entregas > 3)
                                                                                                                        THEN uv.rm_motorista_valor_as_maior_3_entregas
                                                                                                                ELSE (0) :: real
             END
           WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 ((m.entrega) :: text = 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN CASE
                                                                                                                WHEN (m.entregas = 1)
                                                                                                                        THEN uv.rm_ajudante_valor_as_1_entrega
                                                                                                                WHEN (m.entregas = 2)
                                                                                                                        THEN uv.rm_ajudante_valor_as_2_entregas
                                                                                                                WHEN (m.entregas = 3)
                                                                                                                        THEN uv.rm_ajudante_valor_as_3_entregas
                                                                                                                WHEN (m.entregas > 3)
                                                                                                                        THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                                                                                                ELSE (0) :: real
             END
           WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                 ((m.entrega) :: text = 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN CASE
                                                                                                                WHEN (m.entregas = 1)
                                                                                                                        THEN uv.rm_ajudante_valor_as_1_entrega
                                                                                                                WHEN (m.entregas = 2)
                                                                                                                        THEN uv.rm_ajudante_valor_as_2_entregas
                                                                                                                WHEN (m.entregas = 3)
                                                                                                                        THEN uv.rm_ajudante_valor_as_3_entregas
                                                                                                                WHEN (m.entregas > 2)
                                                                                                                        THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                                                                                                ELSE (0) :: real
             END
           ELSE (0) :: real
             END                                                                AS valor_as,
         ((
              CASE
                WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR
                                                                 ((m.cargaatual) :: text = 'Recarga' :: text)))
                        THEN ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot)
                WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                      ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR
                       ((m.cargaatual) :: text = 'Recarga' :: text)))
                        THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
                WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                      ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR
                       ((m.cargaatual) :: text = 'Recarga' :: text)))
                        THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
                ELSE (0) :: real
                  END +
              CASE
                WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND
                      ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN (
                  (m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota) :: double precision) /
                  (m.fator) :: double precision)
                WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                      (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND
                      ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN (
                  (m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota) :: double precision) /
                  (m.fator) :: double precision)
                WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                      ((m.entrega) :: text <> 'AS' :: text) AND (m.fator <> (0) :: double precision) AND
                      (m.tempoprevistoroad > um.meta_tempo_rota_horas) AND
                      ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN (
                  (m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota) :: double precision) /
                  (m.fator) :: double precision)
                ELSE (0) :: double precision
                  END) +
          CASE
            WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista) AND
                  ((m.entrega) :: text = 'AS' :: text)) THEN CASE
                                                               WHEN ((m.entregas = 1) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_motorista_valor_as_1_entrega
                                                               WHEN ((m.entregas = 2) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_motorista_valor_as_2_entregas
                                                               WHEN ((m.entregas = 3) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_motorista_valor_as_3_entregas
                                                               WHEN ((m.entregas > 3) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_motorista_valor_as_maior_3_entregas
                                                               WHEN ((m.cargaatual) :: text = 'Recarga' :: text)
                                                                       THEN uv.rm_motorista_valor_as_recarga
                                                               ELSE (0) :: real
              END
            WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                  ((m.entrega) :: text = 'AS' :: text)) THEN CASE
                                                               WHEN ((m.entregas = 1) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_ajudante_valor_as_1_entrega
                                                               WHEN ((m.entregas = 2) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_ajudante_valor_as_2_entregas
                                                               WHEN ((m.entregas = 3) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_ajudante_valor_as_3_entregas
                                                               WHEN ((m.entregas > 3) AND ((m.cargaatual) :: text <> 'Recarga' :: text))
                                                                       THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                                               WHEN ((m.cargaatual) :: text = 'Recarga' :: text)
                                                                       THEN uv.rm_ajudante_valor_as_recarga
                                                               ELSE (0) :: real
              END
            WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante) AND
                  ((m.entrega) :: text = 'AS' :: text) AND ((m.cargaatual) :: text <> 'Recarga' :: text)) THEN CASE
                                                                                                                 WHEN (m.entregas = 1)
                                                                                                                         THEN uv.rm_ajudante_valor_as_1_entrega
                                                                                                                 WHEN (m.entregas = 2)
                                                                                                                         THEN uv.rm_ajudante_valor_as_2_entregas
                                                                                                                 WHEN (m.entregas = 3)
                                                                                                                         THEN uv.rm_ajudante_valor_as_3_entregas
                                                                                                                 WHEN (m.entregas > 2)
                                                                                                                         THEN uv.rm_ajudante_valor_as_maior_3_entregas
                                                                                                                 WHEN ((m.cargaatual) :: text = 'Recarga' :: text)
                                                                                                                         THEN uv.rm_ajudante_valor_as_recarga
                                                                                                                 ELSE (0) :: real
              END
            ELSE (0) :: real
              END)                                                              AS valor
  FROM (((((((((view_mapa_colaborador vmc
      JOIN colaborador_data c ON ((vmc.cpf = c.cpf)))
      JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = c.cod_empresa))))
      JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
      JOIN unidade_metas um ON ((um.cod_unidade = m.cod_unidade)))
      JOIN view_valor_cx_unidade ON ((view_valor_cx_unidade.cod_unidade = m.cod_unidade)))
      JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
      JOIN unidade_funcao_produtividade ufp ON (((ufp.cod_unidade = c.cod_unidade) AND
                                                 (ufp.cod_unidade = m.cod_unidade))))
      LEFT JOIN unidade_valores_rm uv ON ((uv.cod_unidade = m.cod_unidade)))
      LEFT JOIN (SELECT t.mapa                                                                               AS tracking_mapa,
                        t."código_transportadora"                                                            AS cod_transportadora,
                        sum(
                          CASE
                            WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                            ELSE 0
                              END)                                                                           AS apontamentos_ok,
                        count(t.disp_apont_cadastrado)                                                       AS total_apontamentos
                 FROM (tracking t
                     JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
                 GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND
                                                                           (tracking.cod_transportadora =
                                                                            m.cod_unidade))));

-- RECRIA A VIEW_EXTRATO_INDICADORES
create view view_extrato_indicadores as
  SELECT dados.cod_empresa,
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
           WHEN ((dados.resultado_devolucao_pdv) :: double precision <= dados.meta_dev_pdv) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_dev_pdv,
         CASE
           WHEN ((dados.resultado_devolucao_hectolitro) :: double precision <= dados.meta_dev_hl) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_dev_hl,
         CASE
           WHEN ((dados.resultado_devolucao_nf) :: double precision <= dados.meta_dev_nf) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_dev_nf,
         CASE
           WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_dispersao_tempo,
         CASE
           WHEN ((dados.resultado_dispersao_km) :: double precision <= dados.meta_dispersao_km) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_dispersao_km,
         CASE
           WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas) :: double precision)
                   THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_tempo_interno,
         CASE
           WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas) :: double precision)
                   THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_tempo_rota,
         CASE
           WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas) :: double precision)
                   THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_tempo_largada,
         CASE
           WHEN ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas) :: double precision) OR
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas) :: double precision)) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_jornada,
         CASE
           WHEN ((dados.resultado_tracking) :: double precision >= dados.meta_tracking) THEN 'SIM' :: text
           ELSE 'NÃO' :: text
             END AS bateu_tracking,
         CASE
           WHEN ((dados.resultado_devolucao_pdv) :: double precision <= dados.meta_dev_pdv) THEN 1
           ELSE 0
             END AS gol_dev_pdv,
         CASE
           WHEN ((dados.resultado_devolucao_hectolitro) :: double precision <= dados.meta_dev_hl) THEN 1
           ELSE 0
             END AS gol_dev_hl,
         CASE
           WHEN ((dados.resultado_devolucao_nf) :: double precision <= dados.meta_dev_nf) THEN 1
           ELSE 0
             END AS gol_dev_nf,
         CASE
           WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 1
           ELSE 0
             END AS gol_dispersao_tempo,
         CASE
           WHEN ((dados.resultado_dispersao_km) :: double precision <= dados.meta_dispersao_km) THEN 1
           ELSE 0
             END AS gol_dispersao_km,
         CASE
           WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_horas) :: double precision) THEN 1
           ELSE 0
             END AS gol_tempo_interno,
         CASE
           WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_horas) :: double precision) THEN 1
           ELSE 0
             END AS gol_tempo_rota,
         CASE
           WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_horas) :: double precision) THEN 1
           ELSE 0
             END AS gol_tempo_largada,
         CASE
           WHEN ((((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) +
                   dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_horas) :: double precision) OR
                 (dados.tempoprevistoroad > (dados.meta_tempo_rota_horas) :: double precision)) THEN 1
           ELSE 0
             END AS gol_jornada,
         CASE
           WHEN ((dados.resultado_tracking) :: double precision >= dados.meta_tracking) THEN 1
           ELSE 0
             END AS gol_tracking
  FROM (SELECT u.cod_empresa,
               u.cod_regional,
               u.codigo                                                                          AS cod_unidade,
               e.codigo                                                                          AS cod_equipe,
               c.cpf,
               c.nome,
               e.nome                                                                            AS equipe,
               f.nome                                                                            AS funcao,
               m.data,
               m.mapa,
               m.placa,
               m.cxcarreg,
               m.qthlcarregados,
               m.qthlentregues,
               trunc(((m.qthlcarregados - m.qthlentregues)) :: numeric, 2)                       AS qthldevolvidos,
               trunc((
                         CASE
                           WHEN (m.qthlcarregados > (0) :: double precision)
                                   THEN ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                           ELSE (0) :: real
                             END) :: numeric,
                     4)                                                                          AS resultado_devolucao_hectolitro,
               m.qtnfcarregadas,
               m.qtnfentregues,
               (m.qtnfcarregadas - m.qtnfentregues)                                              AS qtnfdevolvidas,
               trunc((
                         CASE
                           WHEN (m.qtnfcarregadas > 0) THEN (
                             ((m.qtnfcarregadas - m.qtnfentregues)) :: double precision / (m.qtnfcarregadas) :: real)
                           ELSE (0) :: double precision
                             END) :: numeric,
                     4)                                                                          AS resultado_devolucao_nf,
               m.entregascompletas,
               m.entregasnaorealizadas,
               m.entregasparciais,
               (m.entregascompletas + m.entregasnaorealizadas)                                   AS entregas_carregadas,
               trunc((
                         CASE
                           WHEN (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais) > 0) THEN (
                             ((m.entregasnaorealizadas) :: real + (m.entregasparciais) :: double precision) /
                             (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais)) :: double precision)
                           ELSE (0) :: double precision
                             END) :: numeric,
                     4)                                                                          AS resultado_devolucao_pdv,
               m.kmprevistoroad,
               m.kmsai,
               m.kmentr,
               (m.kmentr - m.kmsai)                                                              AS km_percorrido,
               CASE
                 WHEN (m.kmprevistoroad > (0) :: double precision) THEN trunc(
                                                                          (((((m.kmentr - m.kmsai)) :: double precision - m.kmprevistoroad) /
                                                                            m.kmprevistoroad)) :: numeric, 4)
                 ELSE NULL :: numeric
                   END                                                                           AS resultado_dispersao_km,
               to_char(m.hrsai, 'DD/MM/YYYY HH24:MI:SS' :: text)                                 AS hrsai,
               m.hrsai                                                                           AS hr_sai,
               to_char(m.hrentr, 'DD/MM/YYYY HH24:MI:SS' :: text)                                AS hrentr,
               m.hrentr                                                                          AS hr_entr,
               to_char((m.hrentr - m.hrsai), 'HH24:MI:SS' :: text)                               AS tempo_rota,
               date_part('epoch' :: text, m.tempoprevistoroad)                                   AS tempoprevistoroad,
               date_part('epoch' :: text, (m.hrentr - m.hrsai))                                  AS resultado_tempo_rota_segundos,
               CASE
                 WHEN (date_part('epoch' :: text, m.tempoprevistoroad) > (0) :: double precision) THEN (
                   (date_part('epoch' :: text, (m.hrentr - m.hrsai)) -
                    date_part('epoch' :: text, m.tempoprevistoroad)) / date_part('epoch' :: text, m.tempoprevistoroad))
                 ELSE (0) :: double precision
                   END                                                                           AS resultado_dispersao_tempo,
               date_part('epoch' :: text, m.tempointerno)                                        AS resultado_tempo_interno_segundos,
               m.tempointerno                                                                    AS tempo_interno,
               m.hrmatinal,
               date_part('epoch' :: text,
                         CASE
                           WHEN ((m.hrsai) :: time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                           ELSE ((m.hrsai - (m.hrmatinal) :: interval)) :: time without time zone
                             END)                                                                AS resultado_tempo_largada_segundos,
               CASE
                 WHEN ((m.hrsai) :: time without time zone < m.hrmatinal) THEN um.meta_tempo_largada_horas
                 ELSE ((m.hrsai - (m.hrmatinal) :: interval)) :: time without time zone
                   END                                                                           AS tempo_largada,
               COALESCE(tracking.total_apontamentos, (0) :: bigint)                              AS total_tracking,
               COALESCE(tracking.apontamentos_ok, (0) :: bigint)                                 AS apontamentos_ok,
               COALESCE((tracking.total_apontamentos - tracking.apontamentos_ok), (0) :: bigint) AS apontamentos_nok,
               CASE
                 WHEN (tracking.total_apontamentos > 0) THEN (tracking.apontamentos_ok / tracking.total_apontamentos)
                 ELSE (0) :: bigint
                   END                                                                           AS resultado_tracking,
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
               to_seconds((um.meta_tempo_rota_horas) :: text)                                    AS meta_tempo_rota_horas,
               to_seconds((um.meta_tempo_interno_horas) :: text)                                 AS meta_tempo_interno_horas,
               to_seconds((um.meta_tempo_largada_horas) :: text)                                 AS meta_tempo_largada_horas,
               to_seconds((um.meta_jornada_liquida_horas) :: text)                               AS meta_jornada_liquida_horas
        FROM (((((((((view_mapa_colaborador vmc
            JOIN colaborador_data c ON (((c.cpf = vmc.cpf) AND (c.cod_unidade = vmc.cod_unidade))))
            JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
            JOIN unidade u ON ((u.codigo = m.cod_unidade)))
            JOIN empresa em ON ((em.codigo = u.cod_empresa)))
            JOIN regional r ON ((r.codigo = u.cod_regional)))
            JOIN unidade_metas um ON ((um.cod_unidade = u.codigo)))
            JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
            JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = em.codigo))))
            LEFT JOIN (SELECT t.mapa                         AS tracking_mapa,
                              t."código_transportadora"      AS tracking_unidade,
                              count(t.disp_apont_cadastrado) AS total_apontamentos,
                              sum(
                                CASE
                                  WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                                  ELSE 0
                                    END)                     AS apontamentos_ok
                       FROM (tracking t
                           JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
                       GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND
                                                                                 (tracking.tracking_unidade =
                                                                                  m.cod_unidade))))
        ORDER BY m.data) dados;

-- RECRIA A FUNC_CARGOS_GET_PERMISSOES_DETALHADAS
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_PERMISSOES_DETALHADAS(
  F_COD_UNIDADE BIGINT,
  F_COD_CARGO   BIGINT)
  RETURNS TABLE(
    COD_CARGO           BIGINT,
    COD_UNIDADE_CARGO   BIGINT,
    NOME_CARGO          TEXT,
    COD_PILAR           BIGINT,
    NOME_PILAR          VARCHAR(255),
    COD_FUNCIONALIDADE  SMALLINT,
    NOME_FUNCIONALIDADE VARCHAR(255),
    COD_PERMISSAO       BIGINT,
    NOME_PERMISSAO      VARCHAR(255),
    IMPACTO_PERMISSAO   PROLOG_IMPACTO_PERMISSAO_TYPE,
    DESCRICAO_PERMISSAO TEXT,
    PERMISSAO_LIBERADA  BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH PERMISSOES_CARGO_UNIDADE AS (
      SELECT CFP.COD_FUNCAO_COLABORADOR AS COD_CARGO,
             CFP.COD_UNIDADE            AS COD_UNIDADE_CARGO,
             CFP.COD_FUNCAO_PROLOG      AS COD_FUNCAO_PROLOG,
             CFP.COD_PILAR_PROLOG       AS COD_PILAR_PROLOG
      FROM CARGO_FUNCAO_PROLOG_V11 CFP
      WHERE CFP.COD_UNIDADE = F_COD_UNIDADE
        AND CFP.COD_FUNCAO_COLABORADOR = F_COD_CARGO
  )

  SELECT F_COD_CARGO                       AS COD_CARGO,
         F_COD_UNIDADE                     AS COD_UNIDADE_CARGO,
         F.NOME :: TEXT                    AS NOME_CARGO,
         FP.COD_PILAR                      AS COD_PILAR,
         PP.PILAR                          AS NOME_PILAR,
         FP.COD_AGRUPAMENTO                AS COD_FUNCIONALIDADE,
         FPA.NOME                          AS NOME_FUNCIONALIDADE,
         FP.CODIGO                         AS COD_PERMISSAO,
         FP.FUNCAO                         AS NOME_PERMISSAO,
         FP.IMPACTO                        AS IMPACTO_PERMISSAO,
         FP.DESCRICAO                      AS DESCRICAO_PERMISSAO,
         PCU.COD_UNIDADE_CARGO IS NOT NULL AS PERMISSAO_LIBERADA
  FROM PILAR_PROLOG PP
         JOIN FUNCAO_PROLOG_V11 FP ON FP.COD_PILAR = PP.CODIGO
         JOIN UNIDADE_PILAR_PROLOG UPP ON UPP.COD_PILAR = PP.CODIGO
         JOIN FUNCAO_PROLOG_AGRUPAMENTO FPA ON FPA.CODIGO = FP.COD_AGRUPAMENTO
         JOIN FUNCAO F ON F.CODIGO = F_COD_CARGO
         LEFT JOIN PERMISSOES_CARGO_UNIDADE PCU ON PCU.COD_FUNCAO_PROLOG = FP.CODIGO
  WHERE UPP.COD_UNIDADE = F_COD_UNIDADE
    AND FP.COD_PILAR = ANY (PILARES_LIBERADOS_UNIDADE)
  ORDER BY PP.PILAR, FP.COD_AGRUPAMENTO, FP.IMPACTO DESC;
END;
$$;

-- RECRIA A FUNC_CARGOS_GET_CARGOS_EM_USO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_CARGOS_EM_USO(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO                    BIGINT,
    NOME_CARGO                   TEXT,
    QTD_COLABORADORES_VINCULADOS BIGINT,
    QTD_PERMISSOES_VINCULADAS    BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH CARGOS_EM_USO AS (
      SELECT DISTINCT COD_FUNCAO
      FROM COLABORADOR C
      WHERE C.COD_UNIDADE = F_COD_UNIDADE
  )

  SELECT
    F.CODIGO                                                                    AS COD_CARGO,
    F.NOME :: TEXT                                                              AS NOME_CARGO,
    (SELECT COUNT(*)
     FROM COLABORADOR C
     WHERE C.COD_FUNCAO = F.CODIGO
           AND C.COD_UNIDADE = F_COD_UNIDADE)                                   AS QTD_COLABORADORES_VINCULADOS,
    -- Se não tivesse esse FILTER, cargos que não possuem nenhuma permissão vinculada retornariam 1.
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    -- Consideramos apenas as permissões de pilares liberados para a unidade.
                    AND CFP.COD_PILAR_PROLOG = ANY (PILARES_LIBERADOS_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS
  FROM FUNCAO F
    LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
         AND CFP.COD_UNIDADE = F_COD_UNIDADE
  -- Não podemos simplesmente filtrar pelo código da unidade presente na tabela CARGO_FUNCAO_PROLOG_V11, pois desse
  -- modo iríamos remover do retorno cargos usados mas sem permissões vinculadas. Por isso utilizamos esse modo de
  -- filtragem com a CTE criada acima.
  WHERE F.CODIGO IN (SELECT *
                     FROM CARGOS_EM_USO)
  GROUP BY F.CODIGO, F.NOME
  ORDER BY F.NOME ASC;
END;
$$;

-- RECRIA A FUNC_CARGOS_GET_CARGOS_NAO_UTILIZADOS
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_CARGOS_NAO_UTILIZADOS(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO                 BIGINT,
    NOME_CARGO                TEXT,
    QTD_PERMISSOES_VINCULADAS BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH CARGOS_EM_USO AS (
      SELECT DISTINCT COD_FUNCAO
      FROM COLABORADOR C
      WHERE C.COD_UNIDADE = F_COD_UNIDADE
  )

  SELECT
    F.CODIGO                                                                    AS COD_CARGO,
    F.NOME :: TEXT                                                              AS NOME_CARGO,
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    -- Consideramos apenas as permissões de pilares liberados para a unidade.
                    AND CFP.COD_PILAR_PROLOG = ANY (PILARES_LIBERADOS_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS
  FROM FUNCAO F
    LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
         AND CFP.COD_UNIDADE = F_COD_UNIDADE
  -- Para buscar os cargos não utilizados, adotamos a lógica de buscar todos os da empresa e depois remover os que
  -- tem colaboradores vinculados, isso é feito nas duas condições abaixo do WHERE.
  WHERE F.COD_EMPRESA = (SELECT U.COD_EMPRESA
                         FROM UNIDADE U
                         WHERE U.CODIGO = F_COD_UNIDADE)
        AND F.CODIGO NOT IN (SELECT *
                             FROM CARGOS_EM_USO)
  GROUP BY F.CODIGO, F.NOME
  ORDER BY F.NOME ASC;
END;
$$;

-- CRIA FUNCTION PARA DELEÇÃO LÓGICA DE CARGOS - FUNC_CARGOS_DELETA_CARGO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_DELETA_CARGO(
      F_COD_EMPRESA        BIGINT,
      F_COD_CARGO          BIGINT,
      F_TOKEN              TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  F_COD_COLABORADOR_UPDATE BIGINT := (SELECT COD_COLABORADOR FROM TOKEN_AUTENTICACAO WHERE TOKEN = F_TOKEN);
  QTD_LINHAS_ATUALIZADAS   BIGINT;
BEGIN
  IF F_COD_COLABORADOR_UPDATE IS NULL OR F_COD_COLABORADOR_UPDATE <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível validar sua sessão, por favor, faça login novamente');
  END IF;

  IF ((SELECT COUNT(CODIGO) FROM FUNCAO WHERE COD_EMPRESA = F_COD_EMPRESA
                                          AND CODIGO = F_COD_CARGO) <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Erro ao deletar, possivelmente este cargo já foi deletado');
  END IF;

  IF ((SELECT COUNT(CODIGO) FROM COLABORADOR WHERE COD_EMPRESA = F_COD_EMPRESA
                                               AND COD_FUNCAO = F_COD_CARGO) > 0)
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não é possível deletar pois existem colaboradores vinculados a este cargo');
  END IF;

  -- Deleta cargo.
  UPDATE FUNCAO_DATA
  SET DELETADO               = TRUE,
      DATA_HORA_DELETADO     = NOW(),
      DATA_HORA_UPDATE       = NOW(),
      COD_COLABORADOR_UPDATE = F_COD_COLABORADOR_UPDATE
  WHERE COD_EMPRESA = F_COD_EMPRESA
    AND CODIGO = F_COD_CARGO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR('Erro ao deletar o cargo, tente novamente');
  END IF;
  --
  --
  RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;

CREATE OR REPLACE FUNCTION TRIM_AND_REMOVE_EXTRA_SPACES(F_TEXT TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Além do trim, se existir mais de um espaço entre duas strings, ele será reduzido a apenas um espaço.
  RETURN TRIM(REGEXP_REPLACE(F_TEXT, '\s+', ' ', 'g'));
END;
$$;

-- CRIA FUNCTION PARA EDITAR UM CARGO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_EDITA_CARGO(
  F_COD_EMPRESA BIGINT,
  F_COD_CARGO   BIGINT,
  F_NOME_CARGO  TEXT,
  F_TOKEN       TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  F_COD_COLABORADOR_UPDATE BIGINT := (SELECT COD_COLABORADOR
                                      FROM TOKEN_AUTENTICACAO
                                      WHERE TOKEN = F_TOKEN);
  QTD_LINHAS_ATUALIZADAS   BIGINT;
BEGIN
  IF F_COD_COLABORADOR_UPDATE IS NULL OR F_COD_COLABORADOR_UPDATE <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível validar sua sessão, por favor, faça login novamente');
  END IF;

  IF ((SELECT COUNT(CODIGO)
       FROM FUNCAO
       WHERE COD_EMPRESA = F_COD_EMPRESA
             AND CODIGO = F_COD_CARGO) <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Erro ao editar, possivelmente este cargo já foi deletado');
  END IF;

  -- Edita o cargo.
  UPDATE FUNCAO_DATA
  SET NOME                 = TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_CARGO),
    DATA_HORA_UPDATE       = NOW(),
    COD_COLABORADOR_UPDATE = F_COD_COLABORADOR_UPDATE
  WHERE COD_EMPRESA = F_COD_EMPRESA
        AND CODIGO = F_COD_CARGO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Erro ao editar o cargo, tente novamente');
  END IF;
  --
  --
  RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;

-- CRIA FUNCTION PARA INSERIR UM CARGO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_INSERE_CARGO(
  F_COD_EMPRESA BIGINT,
  F_NOME_CARGO  TEXT,
  F_TOKEN       TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_COLABORADOR_UPDATE BIGINT := (SELECT COD_COLABORADOR
                                      FROM TOKEN_AUTENTICACAO
                                      WHERE TOKEN = F_TOKEN);
  COD_CARGO_INSERIDO       BIGINT;
BEGIN
  IF F_COD_COLABORADOR_UPDATE IS NULL OR F_COD_COLABORADOR_UPDATE <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível validar sua sessão, por favor, faça login novamente');
  END IF;

  INSERT INTO FUNCAO_DATA (COD_EMPRESA, NOME, COD_COLABORADOR_UPDATE)
  VALUES (F_COD_EMPRESA, TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_CARGO), F_COD_COLABORADOR_UPDATE)
  RETURNING CODIGO
    INTO COD_CARGO_INSERIDO;

  -- Verificamos se o insert funcionou.
  IF COD_CARGO_INSERIDO <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível inserir o cargo, tente novamente');
  END IF;

  RETURN COD_CARGO_INSERIDO;
END;
$$;

-- CRIA FUNCTION PARA BUSCAR UM CARGO ESPECÍFICO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_CARGO(
  F_COD_EMPRESA BIGINT,
  F_COD_CARGO   BIGINT)
  RETURNS TABLE(
    COD_EMPRESA  BIGINT,
    COD_CARGO  BIGINT,
    NOME_CARGO TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT F.COD_EMPRESA AS COD_EMPRESA,
         F.CODIGO AS COD_CARGO,
         F.NOME :: TEXT AS NOME_CARGO
  FROM FUNCAO F
  WHERE F.COD_EMPRESA = F_COD_EMPRESA
    AND F.CODIGO = F_COD_CARGO;
END;
$$;


CREATE OR REPLACE FUNCTION PUBLIC.THROW_GENERIC_ERROR(F_MESSAGE TEXT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  RAISE EXCEPTION '%', F_MESSAGE
  USING ERRCODE = (SELECT SQL_ERROR_CODE
                   FROM PROLOG_SQL_ERROR_CODE
                   WHERE PROLOG_ERROR_CODE = 'GENERIC_ERROR');
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ CRIA ESTRUTURA GENÉRICA DE HISTÓRICO  DAS TABELAS ###################################
--######################################################################################################################
--######################################################################################################################
-- PL-2115

-- CRIA UM SCHEMA ESPECÍFICO PARA AS TABELAS DE AUDIT
CREATE SCHEMA AUDIT;

-- CRIA UM MÉTODO GENÉRICO DE CRIAÇÃO DE TABELAS DE HISTÓRICO
-- ESTE MÉTODO PODERÁ SER VINCULADO A QUALQUER TABELA ONDE SE QUEIRA SALVAR HISTÓRICO
-- A CRIAÇÃO DA TRIGGER DEFINE QUAIS EVENTOS SERÃO LOGADOS (INSERT, UPDATE, DELETE)
CREATE OR REPLACE FUNCTION AUDIT.FUNC_AUDIT()
  RETURNS TRIGGER
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  F_TABLE_NAME_AUDIT TEXT := TG_RELNAME || '_audit';
  F_TG_OP            TEXT := SUBSTRING(TG_OP, 1, 1);
  F_JSON             TEXT := CASE
                             WHEN F_TG_OP = 'I'
                               THEN ROW_TO_JSON(NEW)
                             ELSE ROW_TO_JSON(OLD)
                             END;
BEGIN
  EXECUTE FORMAT(
      'CREATE TABLE IF NOT EXISTS audit.%I (
        CODIGO                  SERIAL,
        DATA_HORA_UTC           TIMESTAMP WITH TIME ZONE DEFAULT (NOW() AT TIME ZONE ''UTC''),
        OPERACAO                VARCHAR(1),
        PG_USERNAME             TEXT,
        PG_APPLICATION_NAME     TEXT,
        LINHA_ANTERIOR  JSON
      );', F_TABLE_NAME_AUDIT);

  EXECUTE FORMAT(
      'INSERT INTO audit.%I (operacao, linha_anterior, pg_username, pg_application_name)
       VALUES (%L, %L, %L, %L);', F_TABLE_NAME_AUDIT, F_TG_OP, F_JSON, CURRENT_USER,
      (SELECT CURRENT_SETTING('application_name')));
  RETURN NULL;
END;
$$;

-- CRIA TRIGGER PARA GERAR HISTÓRICO DE INSERT, UPDATE E DELETE NA TABELA FUNCAO_DATA
CREATE TRIGGER TG_FUNC_AUDIT_FUNCAO_DATA
  AFTER INSERT OR UPDATE OR DELETE
  ON FUNCAO_DATA
  FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################### Correção do relatório de tempo de realização de checklist por motorista ######################
--######################################################################################################################
--######################################################################################################################
-- PL-2128
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"                              TEXT,
    "NOME"                                 TEXT,
    "FUNÇÃO"                               TEXT,
    "CHECKS SAÍDA"                         BIGINT,
    "CHECKS RETORNO"                       BIGINT,
    "TOTAL"                                BIGINT,
    "MÉDIA TEMPO DE REALIZAÇÃO (SEGUNDOS)" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                AS NOME_UNIDADE,
  CO.NOME                               AS NOME,
  F.NOME                                AS FUNCAO,
  SUM(CASE WHEN C.TIPO = 'S'
    THEN 1
      ELSE 0 END)                       AS CHECKS_SAIDA,
  SUM(CASE WHEN C.TIPO = 'R'
    THEN 1
      ELSE 0 END)                       AS CHECKS_RETORNO,
  COUNT(C.TIPO)                         AS TOTAL_CHECKS,
  ROUND(AVG(C.TEMPO_REALIZACAO) / 1000) AS MEDIA_SEGUNDOS_REALIZACAO
FROM CHECKLIST C
  JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR
  JOIN FUNCAO F ON F.CODIGO = CO.COD_FUNCAO AND F.COD_EMPRESA = CO.COD_EMPRESA
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CO.CPF, CO.NOME, F.CODIGO, F.NOME
ORDER BY U.NOME, CO.NOME
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;
