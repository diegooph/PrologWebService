BEGIN TRANSACTION;
-- ALTERA AS CONSTRAINTS DE CHECKLIST E SOCORRO EM ROTA PARA DEFERRABLE INITIALLY DEFERRED
ALTER TABLE CHECKLIST_MODELO_DATA
    ALTER CONSTRAINT FK_CHECKLIST_MODELO_CHECKLIST_MODELO_VERSAO
        DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE SOCORRO_ROTA
    ALTER CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_ABERTURA_FK
        DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE SOCORRO_ROTA
    ALTER CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_ATENDIMENTO_FK
        DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE SOCORRO_ROTA
    ALTER CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_INVALIDACAO_FK
        DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE SOCORRO_ROTA
    ALTER CONSTRAINT SOCORRO_ROTA_SOCORRO_ROTA_FINALIZACAO_FK
        DEFERRABLE INITIALLY DEFERRED;
-- CONSTRAINTS PARA DROPAR EM MIGRATION_CHECKLIST
------------------------------------------------------------------------------------------------------------------------
--- TABELA MIGRATION_CHECKLIST.CHECK_ALTERNATIVAS_AUX
ALTER TABLE MIGRATION_CHECKLIST.CHECK_ALTERNATIVAS_AUX
    DROP CONSTRAINT CHECK_ALTERNATIVAS_AUX_COD_MODELO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_ALTERNATIVAS_AUX
    DROP CONSTRAINT CHECK_ALTERNATIVAS_AUX_COD_MODELO_VERSAO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_ALTERNATIVAS_AUX
    DROP CONSTRAINT CHECK_ALTERNATIVAS_AUX_COD_ALTERNATIVA_ANTIGO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_ALTERNATIVAS_AUX
    DROP CONSTRAINT CHECK_ALTERNATIVAS_AUX_COD_ALTERNATIVA_NOVO_FKEY;
------------------------------------------------------------------------------------------------------------------------
--- TABELA MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX
ALTER TABLE MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX
    DROP CONSTRAINT CHECK_PERGUNTAS_AUX_COD_MODELO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX
    DROP CONSTRAINT CHECK_PERGUNTAS_AUX_COD_MODELO_VERSAO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX
    DROP CONSTRAINT CHECK_PERGUNTAS_AUX_COD_PERGUNTA_ANTIGO_FKEY;

ALTER TABLE MIGRATION_CHECKLIST.CHECK_PERGUNTAS_AUX
    DROP CONSTRAINT CHECK_PERGUNTAS_AUX_COD_PERGUNTA_NOVO_FKEY;
------------------------------------------------------------------------------------------------------------------------
--- TABELA MIGRATION_CHECKLIST.CHECKLIST_RESPOSTAS
ALTER TABLE MIGRATION_CHECKLIST.CHECKLIST_RESPOSTAS
    DROP CONSTRAINT FK_CHECKLIST_RESPOSTAS_CHECKLIST;

ALTER TABLE MIGRATION_CHECKLIST.CHECKLIST_RESPOSTAS
    DROP CONSTRAINT FK_CHECKLIST_RESPOSTAS_ALTERNATIVA_PERGUNTA;

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################### CRIA TABELA DE VÍNCULO - USUÁRIO EMPRESA #####################################
--######################################################################################################################
--######################################################################################################################
CREATE TABLE USUARIO_EMPRESA
(
    COD_EMPRESA     BIGINT
        CONSTRAINT USUARIO_EMPRESA_COD_EMPRESA_FKEY
            REFERENCES PUBLIC.EMPRESA,
    USERNAME        TEXT NOT NULL,
    COD_PROLOG_USER BIGINT
        CONSTRAINT USUARIO_EMPRESA_PROLOG_USER_CODIGO_FK
            REFERENCES INTERNO.PROLOG_USER
);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################################ FUNCTIOS DE DELEÇÃO #################################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta as aferições de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                      F_COD_AFERICOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA AMSDT
    WHERE AMSDT.COD_SERVICO IN (SELECT AMD.CODIGO
                                FROM AFERICAO_MANUTENCAO_DATA AMD
                                WHERE AMD.COD_AFERICAO = ANY
                                      (F_COD_AFERICOES));

    DELETE
    FROM AFERICAO_MANUTENCAO_DATA AMD
    WHERE AMD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO ACTAV
    WHERE ACTAV.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_VALORES_DATA AVD
    WHERE AVD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_DATA AD
    WHERE AD.CODIGO = ANY (F_COD_AFERICOES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE_HISTORICO PRUH
    WHERE PRUH.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE PRU
    WHERE PRU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO_BACKUP ACTAVB
    WHERE ACTAVB.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_ALERTA_SULCO ACAS
    WHERE ACAS.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta os checklists de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                       F_COD_CHECKLISTS BIGINT[],
                                                                       F_COD_CHECKLISTS_MODELO BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_ITENS_OS     BIGINT[] := (SELECT ARRAY_AGG(COSID.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
                                    WHERE COSID.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_COD_OS           BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
    V_CODIGO_OS_PROLOG BIGINT[] := (SELECT ARRAY_AGG(COSD.CODIGO_PROLOG)
                                    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                    WHERE COSD.COD_UNIDADE = ANY (F_COD_UNIDADES));
BEGIN
    -- Tornando a constraint deferível
    SET CONSTRAINTS FK_CHECKLIST_MODELO_CHECKLIST_MODELO_VERSAO DEFERRED;

    -- Deleção de checklists realizados.

    DELETE
    FROM CHECKLIST_RESPOSTAS_NOK CRN
    WHERE CRN.COD_CHECKLIST = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA COSIDT
    WHERE COSIDT.COD_ITEM_OS_PROLOG = ANY (V_COD_ITENS_OS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS COSIA
    WHERE COSIA.COD_CHECKLIST_REALIZADO = ANY (F_COD_CHECKLISTS);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
    WHERE COSID.CODIGO = ANY (V_COD_ITENS_OS)
      AND COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA COSDT
    WHERE COSDT.COD_OS_PROLOG = ANY (V_CODIGO_OS_PROLOG);

    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
    WHERE COSD.CODIGO = ANY (V_COD_OS)
      AND COSD.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_DATA CD
    WHERE CD.CODIGO = ANY (F_COD_CHECKLISTS);

    -- Deleção de modelos.

    -- DROPA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    DROP RULE ALTERNATIVA_CHECK_DELETE_PROTECT ON CHECKLIST_ALTERNATIVA_PERGUNTA_DATA;
    -- DELETA ALTERNATIVA.
    DELETE
    FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAPD
    WHERE CAPD.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CAPD.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);
    -- RECRIA REGRA QUE IMPEDE QUE ALTERNATIVA SEJA DELETADA.
    CREATE RULE ALTERNATIVA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_ALTERNATIVA_PERGUNTA_DATA DO INSTEAD NOTHING;

    -- DROPA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    DROP RULE PERGUNTA_CHECK_DELETE_PROTECT ON CHECKLIST_PERGUNTAS_DATA;
    -- DELETA PERGUNTA.
    DELETE
    FROM CHECKLIST_PERGUNTAS_DATA CP
    WHERE CP.COD_UNIDADE = ANY (F_COD_UNIDADES);
    -- RECRIA REGRA QUE IMPEDE QUE PERGUNTA SEJA DELETADA.
    CREATE RULE PERGUNTA_CHECK_DELETE_PROTECT AS
        ON DELETE TO PUBLIC.CHECKLIST_PERGUNTAS_DATA DO INSTEAD NOTHING;

    -- DELETA MODELO DE VERSÃO DE CHECK
    DELETE
    FROM CHECKLIST_MODELO_VERSAO CMV
    WHERE CMV.COD_CHECKLIST_MODELO = ANY (F_COD_CHECKLISTS_MODELO);

    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
    WHERE CMVT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO_BACKUP CMVTB
    WHERE CMVTB.COD_MODELO = ANY (F_COD_CHECKLISTS_MODELO);

    DELETE
    FROM CHECKLIST_MODELO_FUNCAO CMF
    WHERE CMF.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM CHECKLIST_MODELO_DATA CMD
    WHERE CMD.CODIGO = ANY (F_COD_CHECKLISTS_MODELO);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta os colaboradores de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_COLABORADORES(F_COD_EMPRESA BIGINT, F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_COLABORADORES BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                     FROM COLABORADOR_DATA CD
                                     WHERE CD.COD_EMPRESA = F_COD_EMPRESA);
BEGIN
    -- DELETA TOKENS
    DELETE FROM TOKEN_AUTENTICACAO TA WHERE TA.COD_COLABORADOR = ANY (V_COD_COLABORADORES);

    -- DELETA INFORMAÇÕES DE TELEFONE
    DELETE FROM COLABORADOR_TELEFONE CT WHERE CT.COD_COLABORADOR = ANY (V_COD_COLABORADORES);

    -- DELETA INFORMACOES DE E-MAIL
    DELETE FROM COLABORADOR_EMAIL CE WHERE CE.COD_COLABORADOR = ANY (V_COD_COLABORADORES);

    -- REMOVE CODIGO DE COLABORADOR DA TABELA FUNCAO_DATA
    UPDATE FUNCAO_DATA SET COD_COLABORADOR_UPDATE = NULL WHERE COD_COLABORADOR_UPDATE = ANY (V_COD_COLABORADORES);

    -- DELETA TODOS OS COLABORADORES DA UNIDADE DESTINO
    DELETE FROM COLABORADOR_DATA WHERE COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- REALIZA UPDATE DE COLABORADORES QUE FORAM TRANSFERIDOS ENTRE EMPRESA PARA REMOVER O COD_UNIDADE_CADASTRO
    UPDATE COLABORADOR_DATA
    SET COD_UNIDADE_CADASTRO = COD_UNIDADE
    WHERE COD_UNIDADE_CADASTRO = ANY (F_COD_UNIDADES);

    -- DELETA AS EQUIPES DA UNIDADE DESTINO
    DELETE FROM EQUIPE E WHERE E.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA OS SETORES DA UNIDADE DESTINO
    DELETE FROM SETOR S WHERE S.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA AS FUNÇÕES DA EMPRESA DESTINO
    DELETE FROM CARGO_FUNCAO_PROLOG_V11 CFPV WHERE CFPV.COD_UNIDADE = ANY (F_COD_UNIDADES);
    DELETE FROM FUNCAO_DATA FD WHERE FD.COD_EMPRESA = F_COD_EMPRESA;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta fale conosco de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_FALE_CONOSCO(F_COD_FALE_CONOSCO BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM FALE_CONOSCO FC
    WHERE FC.COD_UNIDADE = ANY (F_COD_FALE_CONOSCO);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta intervalo de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_INTERVALO_DEPENDENCIAS(F_COD_UNIDADES BIGINT[], F_COD_MARCACOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MARCACOES_VINCULO_INICIO_FIM BIGINT[] := (SELECT ARRAY_AGG(MVIF.CODIGO)
                                                    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                                                    WHERE (MVIF.COD_MARCACAO_INICIO = ANY (F_COD_MARCACOES))
                                                       OR (MVIF.COD_MARCACAO_FIM = ANY (F_COD_MARCACOES)));
BEGIN
    DELETE
    FROM MARCACAO_INCONSISTENCIA MI
    WHERE MI.COD_MARCACAO_VINCULO_INICIO_FIM = ANY (V_COD_MARCACOES_VINCULO_INICIO_FIM);

    DELETE
    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
    WHERE MVIF.CODIGO = ANY (V_COD_MARCACOES_VINCULO_INICIO_FIM);

    DELETE
    FROM MARCACAO_INICIO MI
    WHERE MI.COD_MARCACAO_INICIO = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_FIM MF
    WHERE MF.COD_MARCACAO_FIM = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_HISTORICO MH
    WHERE MH.COD_MARCACAO = ANY (F_COD_MARCACOES);

    DELETE
    FROM MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA MTDCJBL
    WHERE MTDCJBL.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM MARCACAO_TIPO_JORNADA MTJ
    WHERE MTJ.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO I
    WHERE I.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_UNIDADE IU
    WHERE IU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_TIPO_CARGO ITC
    WHERE ITC.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM INTERVALO_TIPO IT
    WHERE IT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM COMERCIAL.UNIDADE_CARGO_CONTROLE_JORNADA UCCJ
    WHERE UCCJ.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM MARCACAO_AJUSTE MJ
    WHERE MJ.COD_UNIDADE_AJUSTE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta movimentacoes de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_MOVIMENTACOES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                          F_COD_MOVIMENTACOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA MPSRRD
    WHERE MPSRRD.COD_MOVIMENTACAO = ANY (F_COD_MOVIMENTACOES);

    DELETE
    FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA MPSRD
    WHERE MPSRD.COD_MOVIMENTACAO = ANY (F_COD_MOVIMENTACOES);

    DELETE
    FROM MOVIMENTACAO_ORIGEM MO
    WHERE MO.
              COD_MOVIMENTACAO = ANY (F_COD_MOVIMENTACOES);

    DELETE
    FROM MOVIMENTACAO_DESTINO MD
    WHERE MD.COD_MOVIMENTACAO = ANY (F_COD_MOVIMENTACOES);

    DELETE
    FROM MOVIMENTACAO M
    WHERE M.CODIGO = ANY (F_COD_MOVIMENTACOES);

    DELETE
    FROM MOVIMENTACAO_PROCESSO MP
    WHERE MP.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta nps de acordo com codigo de colaboradores.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_NPS(F_COD_COLABORADORES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETA COLABORADORES BLOQUEADOS DO NPS
    DELETE
    FROM CS.NPS_BLOQUEIO_PESQUISA_COLABORADOR NBPC
    WHERE NBPC.COD_COLABORADOR_BLOQUEIO = ANY (F_COD_COLABORADORES);

    -- DELETA RESPOSTAS DE NPS
    DELETE FROM CS.NPS_RESPOSTAS NR WHERE NR.COD_COLABORADOR_RESPOSTAS = ANY (F_COD_COLABORADORES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta pneus de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_PNEUS(F_COD_EMPRESA BIGINT, F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_PNEUS BIGINT[] := (SELECT ARRAY_AGG(PD.CODIGO)
                             FROM PNEU_DATA PD
                             WHERE PD.COD_EMPRESA = F_COD_EMPRESA);
BEGIN
    -- DELETA VINCULO VEÍCULO_PNEU
    DELETE
    FROM VEICULO_PNEU VP
    WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA FOTO DE PNEU
    DELETE
    FROM PNEU_FOTO_CADASTRO PFC
    WHERE PFC.COD_PNEU = ANY (V_COD_PNEUS);

    -- DELETA TODOS OS PNEUS DA EMPRESA DESTINO
    DELETE
    FROM PNEU_DATA PD
    WHERE PD.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA OS MODELOS DE PNEUS DA EMPRESA DESTINO
    DELETE
    FROM MODELO_PNEU MP
    WHERE MP.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA OS MODELOS DE BANDA DA EMPRESA DESTINO
    DELETE
    FROM MODELO_BANDA MOB
    WHERE MOB.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA AS MARCAS DE BANDA DA EMPRESA DESTINO
    DELETE
    FROM MARCA_BANDA MAB
    WHERE MAB.COD_EMPRESA = F_COD_EMPRESA;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta produtividades de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_PRODUTIVIDADES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM ACESSOS_PRODUTIVIDADE AP
    WHERE AP.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta quiz de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_QUIZ_DEPENDENCIAS(F_COD_UNIDADES BIGINT[], F_COD_QUIZ BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM QUIZ_RESPOSTAS QR
    WHERE QR.COD_QUIZ = ANY (F_COD_QUIZ);

    DELETE
    FROM QUIZ_ALTERNATIVA_PERGUNTA QAP
    WHERE QAP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_PERGUNTAS QP
    WHERE QP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_MODELO_TREINAMENTO QMT
    WHERE QMT.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_MODELO_FUNCAO QMF
    WHERE QMF.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ_MODELO QM
    WHERE QM.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM QUIZ Q
    WHERE Q.CODIGO = ANY (F_COD_QUIZ);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta relatos de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_RELATOS_DEPENDENCIAS(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE FROM RELATO R WHERE R.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE FROM RELATO_ALTERNATIVA RA WHERE RA.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta serviços de pneus de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_SERVICOS_PNEU_DEPENDENCIAS(F_COD_EMPRESA BIGINT, F_COD_SERVICOS_REALIZADOS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA PSRIVD
    WHERE PSRIVD.COD_SERVICO_REALIZADO = ANY (F_COD_SERVICOS_REALIZADOS);

    DELETE
    FROM PNEU_SERVICO_CADASTRO_DATA PSCD
    WHERE PSCD.COD_SERVICO_REALIZADO = ANY (F_COD_SERVICOS_REALIZADOS);

    DELETE
    FROM PNEU_SERVICO_REALIZADO_DATA PSRD
    WHERE PSRD.CODIGO = ANY (F_COD_SERVICOS_REALIZADOS);

    DELETE
    FROM PNEU_TIPO_SERVICO PTS
    WHERE PTS.COD_EMPRESA = F_COD_EMPRESA;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta socorros de uma empresa e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_SOCORROS_DEPENDENCIAS(F_COD_EMPRESA BIGINT, F_COD_SOCORROS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PROBLEMAS_SOCORROS_ROTAS BIGINT[] := (SELECT array_agg(SROP.CODIGO)
                                              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
                                              WHERE SROP.COD_EMPRESA = (F_COD_EMPRESA));
BEGIN
    -- Dropa constraints que garantem a relação entre status e registros de ações de socorro.
    ALTER TABLE SOCORRO_ROTA
        DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ABERTURA;
    ALTER TABLE SOCORRO_ROTA
        DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ATENDIMENTO;
    ALTER TABLE SOCORRO_ROTA
        DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_INVALIDACAO;
    ALTER TABLE SOCORRO_ROTA
        DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_FINALIZACAO;

    -- Tornando as constraints de FK deferíveis.
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_ABERTURA_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_ATENDIMENTO_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_INVALIDACAO_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_FINALIZACAO_FK DEFERRED;

    -- Deleta os registros.
    DELETE
    FROM SOCORRO_ROTA_FINALIZACAO SRF
    WHERE SRF.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_INVALIDACAO SRI
    WHERE SRI.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_ATENDIMENTO SRAT
    WHERE SRAT.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_ABERTURA SRA
    WHERE SRA.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA SR
    WHERE SR.CODIGO = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO SROPH
    WHERE SROPH.COD_PROBLEMA_SOCORRO_ROTA = ANY (COD_PROBLEMAS_SOCORROS_ROTAS);

    DELETE
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
    WHERE SROP.CODIGO = ANY (COD_PROBLEMAS_SOCORROS_ROTAS);

    -- Recria constraints de status e registros.

    -- Garante que no status aberto só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ABERTURA
            CHECK (STATUS_ATUAL <> 'ABERTO' OR
                   (STATUS_ATUAL = 'ABERTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

    -- Garante que no status em atendimento só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ATENDIMENTO
            CHECK (STATUS_ATUAL <> 'EM_ATENDIMENTO' OR
                   (STATUS_ATUAL = 'EM_ATENDIMENTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

    -- Garante que no status de invalidação só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_INVALIDACAO
            CHECK (STATUS_ATUAL <> 'INVALIDO' OR
                   (STATUS_ATUAL = 'INVALIDO' AND COD_ABERTURA IS NOT NULL AND COD_INVALIDACAO IS NOT NULL AND
                    COD_FINALIZACAO IS NULL));

    -- Garante que no status finalizado só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_FINALIZACAO
            CHECK (STATUS_ATUAL <> 'FINALIZADO' OR
                   (STATUS_ATUAL = 'FINALIZADO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NOT NULL));
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta testes de aferidor de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TESTES_AFERIDOR(F_COD_TESTES_AFERIDOR BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM AFERIDOR.PROCEDIMENTO_TESTE PT
    WHERE PT.CODIGO = ANY (F_COD_TESTES_AFERIDOR);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta unidades de uma empresa.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TOKENS_CHECKLISTS_OFFLINES(F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETE A TRIGGER QUE ACIONA A FUNCTION DE AVISO DE BLOQUEIO.
    DROP TRIGGER TG_BLOQUEIO_DELECAO_TOKEN_CHECKLIST_OFFLINE ON CHECKLIST_OFFLINE_DADOS_UNIDADE;

    -- DELETA TOKENS CHECKLISTS OFFILINES
    DELETE
    FROM CHECKLIST_OFFLINE_DADOS_UNIDADE CODU
    WHERE CODU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- RECRIA A TRIGGER QUE ACIONA A FUNCTION DE AVISO DE BLOQUEIO NA DELEÇÃO.
    CREATE TRIGGER TG_BLOQUEIO_DELECAO_TOKEN_CHECKLIST_OFFLINE
        BEFORE DELETE
        ON CHECKLIST_OFFLINE_DADOS_UNIDADE
        FOR EACH ROW
    EXECUTE PROCEDURE TG_FUNC_BLOQUEIO();
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta transferencias de pneus de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TRANSFERENCIAS_PNEUS_DEPENDENCIAS(V_COD_PNEU_TRANSFERENCIAS_PROCESSOS BIGINT[],
                                                                                 F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM PNEU_TRANSFERENCIA_INFORMACOES PTI
    WHERE PTI.COD_PROCESSO_TRANSFERENCIA = ANY (V_COD_PNEU_TRANSFERENCIAS_PROCESSOS);

    DELETE
    FROM PNEU_TRANSFERENCIA_PROCESSO PTP
    WHERE (PTP.COD_UNIDADE_ORIGEM = ANY (F_COD_UNIDADES))
       OR (PTP.COD_UNIDADE_DESTINO = ANY (F_COD_UNIDADES));
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta transferencias de veiculos de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TRANSFERENCIAS_VEICULOS_DEPENDENCIAS(F_CODIGOS_TRANSFERENCIAS_PROCESSOS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_TRANSFERENCIAS_INFORMACOES BIGINT[] := (SELECT ARRAY_AGG(VTI.CODIGO)
                                                  FROM VEICULO_TRANSFERENCIA_INFORMACOES VTI
                                                  WHERE VTI.COD_PROCESSO_TRANSFERENCIA = ANY
                                                        (F_CODIGOS_TRANSFERENCIAS_PROCESSOS));
BEGIN
    DELETE
    FROM CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA COSDT
    WHERE COSDT.COD_VEICULO_TRANSFERENCIA_INFORMACOES = ANY (V_COD_TRANSFERENCIAS_INFORMACOES);
    -- DUVIDA: Deletar também os registros da tabela checklist_ordem_servico_item_deletado_transferencia?

    DELETE
    FROM VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU VTVIP
    WHERE VTVIP.COD_VEICULO_TRANSFERENCIA_INFORMACOES = ANY (V_COD_TRANSFERENCIAS_INFORMACOES);

    DELETE
    FROM VEICULO_TRANSFERENCIA_INFORMACOES VTI
    WHERE VTI.CODIGO = ANY (V_COD_TRANSFERENCIAS_INFORMACOES);

    DELETE
    FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
    WHERE VTP.CODIGO = ANY (F_CODIGOS_TRANSFERENCIAS_PROCESSOS);
END;
$$;

--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta treinamentos de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_TREINAMENTOS_DEPENDENCIAS(V_COD_TREINAMENTOS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM TREINAMENTO_COLABORADOR TC
    WHERE TC.COD_TREINAMENTO = ANY (V_COD_TREINAMENTOS);

    DELETE
    FROM RESTRICAO_TREINAMENTO RT
    WHERE RT.COD_TREINAMENTO = ANY (V_COD_TREINAMENTOS);

    DELETE
    FROM TREINAMENTO_URL_PAGINAS TUP
    WHERE TUP.COD_TREINAMENTO = ANY (V_COD_TREINAMENTOS);

    DELETE
    FROM TREINAMENTO T
    WHERE T.CODIGO = ANY (V_COD_TREINAMENTOS);
END;
$$;



--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta unidades de uma empresa.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_UNIDADES(F_COD_EMPRESA BIGINT, F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETA VINCULO UNIDADE PILAR
    DELETE FROM UNIDADE_PILAR_PROLOG UPP WHERE UPP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA UNIDADES
    DELETE FROM UNIDADE U WHERE U.COD_EMPRESA = F_COD_EMPRESA;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta veiculos de uma unidade e suas dependências.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_VEICULOS(F_COD_EMPRESA BIGINT, F_COD_UNIDADES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- DELETA VINCULO VEÍCULO_PNEU
    DELETE FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA TODOS OS VEÍCULOS DA EMPRESA DESTINO
    DELETE
    FROM VEICULO_DATA VD
    WHERE VD.COD_EMPRESA = F_COD_EMPRESA;

    -- REALIZA UPDATE DE VEICULOS QUE FORAM TRANSFERIDOS ENTRE EMPRESA PARA REMOVER O COD_UNIDADE_CADASTRO
    UPDATE VEICULO_DATA
    SET COD_UNIDADE_CADASTRO = COD_UNIDADE
    WHERE COD_UNIDADE_CADASTRO = ANY (F_COD_UNIDADES);

    -- DELETA VEICULOS DA EMPRESA DESTINO QUE ESTÃO EM BACKUP
    DELETE
    FROM VEICULO_BACKUP VB
    WHERE VB.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- REALIZA UPDATE DE VEICULOS QUE FORAM TRANSFERIDOS ENTRE EMPRESA PARA REMOVER O COD_UNIDADE_CADASTRO
    UPDATE VEICULO_BACKUP
    SET COD_UNIDADE_CADASTRO = COD_UNIDADE
    WHERE COD_UNIDADE_CADASTRO = ANY (F_COD_UNIDADES);

    -- DELETA OS MODELOS DE VEÍCULO DA EMPRESA DESTINO
    DELETE
    FROM MODELO_VEICULO MV
    WHERE MV.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA NOMENCLATURAS
    DELETE
    FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
    WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA NOMENCLATURAS ANTIGAS
    DELETE
    FROM PNEU_ORDEM_NOMENCLATURA_ANTIGA PONA
    WHERE PONA.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA OS TIPOS DE VEÍCULOS DA EMPRESA DESTINO
    DELETE
    FROM VEICULO_TIPO VT
    WHERE VT.COD_EMPRESA = F_COD_EMPRESA;

    -- DELETA AS NOMENCLATURAS BACKUP
    DELETE
    FROM PNEU_ORDEM_NOMENCLATURA_UNIDADE_BACKUP PONUB
    WHERE PONUB.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA OS TIPOS DE VEÍCULOS DA EMPRESA DESTINO
    DELETE
    FROM VEICULO_TIPO_BACKUP VTB
    WHERE VTB.COD_UNIDADE = ANY (F_COD_UNIDADES);

    -- DELETA VEICULO DA TABELA DE ALTERAÇÃO DE KM
    DELETE
    FROM PROLOG_ANALISES.VEICULO_ALTERACAO_KM VAK
    WHERE VAK.COD_UNIDADE_ALOCADO = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### FUNCTIONS DE RESET E CLONAGEM ############################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona unidades de uma empresa para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_UNIDADES(F_COD_EMPRESA_BASE BIGINT,
                                                       F_COD_EMPRESA_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO UNIDADE (NOME,
                         TOTAL_COLABORADORES,
                         COD_REGIONAL,
                         COD_EMPRESA,
                         TIMEZONE,
                         DATA_HORA_CADASTRO,
                         STATUS_ATIVO)
    SELECT U.NOME,
           U.TOTAL_COLABORADORES,
           U.COD_REGIONAL,
           F_COD_EMPRESA_USUARIO,
           U.TIMEZONE,
           now(),
           TRUE
    FROM UNIDADE U
    WHERE U.COD_EMPRESA = F_COD_EMPRESA_BASE;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona colaboradores de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_COLABORADORES(F_COD_EMPRESA_BASE BIGINT,
                                                            F_COD_UNIDADE_BASE BIGINT,
                                                            F_COD_EMPRESA_USUARIO BIGINT,
                                                            F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_CPF_PREFIXO_PADRAO          TEXT   := '0338328';
    V_CPF_SUFIXO_PADRAO           BIGINT := 0;
    V_CPF_VERIFICACAO             BIGINT;
    V_CPFS_VALIDOS_CADASTRO       BIGINT[];
    V_TENTATIVA_BUSCAR_CPF_VALIDO BIGINT := 0;
BEGIN
    -- VERIFICA SE EXISTEM EQUIPES DE VEÍCULOS PARA COPIAR
    IF NOT EXISTS(SELECT E.CODIGO FROM EQUIPE E WHERE E.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem equipes para serem copiadas da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- VERIFICA SE EXISTEM SETORES PARA COPIAR
    IF NOT EXISTS(SELECT SE.CODIGO FROM SETOR SE WHERE SE.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem setores para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- VERIFICA SE EXISTEM CARGOS PARA COPIAR
    IF NOT EXISTS(SELECT F.CODIGO FROM FUNCAO F WHERE F.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem cargos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM COLABORADORES PARA COPIAR
    IF NOT EXISTS(SELECT CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem colaboradores para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA AS EQUIPES
    INSERT INTO EQUIPE (NOME,
                        COD_UNIDADE)
    SELECT E.NOME,
           F_COD_UNIDADE_USUARIO
    FROM EQUIPE E
    WHERE E.COD_UNIDADE = F_COD_UNIDADE_BASE;

    -- COPIA OS SETORES
    INSERT INTO SETOR(NOME,
                      COD_UNIDADE)
    SELECT SE.NOME,
           F_COD_UNIDADE_USUARIO
    FROM SETOR SE
    WHERE SE.COD_UNIDADE = F_COD_UNIDADE_BASE;

    -- COPIA AS FUNÇÕES
    INSERT INTO FUNCAO_DATA (NOME,
                             COD_EMPRESA)
    SELECT F.NOME,
           F_COD_EMPRESA_USUARIO
    FROM FUNCAO F
    WHERE F.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT DO NOTHING;

    --SELECIONA CPFS VÁLIDOS PARA CADASTRO.
    WHILE (((ARRAY_LENGTH(V_CPFS_VALIDOS_CADASTRO, 1)) < (SELECT COUNT(CD.CPF)
                                                          FROM COLABORADOR_DATA CD
                                                          WHERE CD.COD_UNIDADE = F_COD_UNIDADE_BASE)) OR
           ((ARRAY_LENGTH(V_CPFS_VALIDOS_CADASTRO, 1)) IS NULL))
        LOOP
            --EXISTEM 10000 CPFS DISPONÍVEIS PARA CADASTRO (03383280000 ATÉ 03383289999),
            --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            IF (V_TENTATIVA_BUSCAR_CPF_VALIDO = 10000)
            THEN
                RAISE EXCEPTION
                    'Não existem cpfs disponíveis para serem cadastrados';
            END IF;
            V_CPF_VERIFICACAO := (CONCAT(V_CPF_PREFIXO_PADRAO, LPAD(V_CPF_SUFIXO_PADRAO::TEXT, 4, '0')))::BIGINT;
            IF NOT EXISTS(SELECT CD.CPF FROM COLABORADOR_DATA CD WHERE CD.CPF = V_CPF_VERIFICACAO)
            THEN
                -- CPFS VÁLIDOS PARA CADASTRO
                V_CPFS_VALIDOS_CADASTRO := ARRAY_APPEND(V_CPFS_VALIDOS_CADASTRO, V_CPF_VERIFICACAO);
            END IF;
            V_CPF_SUFIXO_PADRAO := V_CPF_SUFIXO_PADRAO + 1;
            V_TENTATIVA_BUSCAR_CPF_VALIDO := V_TENTATIVA_BUSCAR_CPF_VALIDO + 1;
        END LOOP;

    WITH CPFS_VALIDOS_CADASTRO AS (
        SELECT ROW_NUMBER() OVER () AS CODIGO,
               CDN                  AS CPF_NOVO_CADASTRO
        FROM UNNEST(V_CPFS_VALIDOS_CADASTRO) CDN),
         COLABORADORES_BASE AS (
             SELECT ROW_NUMBER() OVER () AS CODIGO,
                    CO.CPF               AS CPF_BASE,
                    CO.NOME              AS NOME_BASE,
                    CO.DATA_NASCIMENTO   AS DATA_NASCIMENTO_BASE,
                    CO.DATA_ADMISSAO     AS DATA_ADMISSAO_BASE,
                    CO.COD_EQUIPE        AS COD_EQUIPE_BASE,
                    CO.COD_SETOR         AS COD_SETOR_BASE,
                    CO.COD_FUNCAO        AS COD_FUNCAO_BASE,

                    CO.COD_PERMISSAO     AS COD_PERMISSAO_BASE
             FROM COLABORADOR CO
             WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
         ),
         DADOS_DE_PARA AS (
             SELECT CVC.CPF_NOVO_CADASTRO AS CPF_CADASTRO,
                    CB.CPF_BASE           AS CPF_BASE,
                    CB.NOME_BASE          AS NOME_BASE,
                    CB.DATA_ADMISSAO_BASE AS DATA_NASCIMENTO_BASE,
                    CB.DATA_ADMISSAO_BASE AS DATA_ADMISSAO_BASE,
                    CB.COD_PERMISSAO_BASE AS COD_PERMISSAO_BASE,
                    EB.CODIGO             AS COD_EQUIPE_BASE,
                    EN.CODIGO             AS COD_EQUIPE_NOVA,
                    SB.CODIGO             AS COD_SETOR_BASE,
                    SN.CODIGO             AS COD_SETOR_NOVO,
                    FB.CODIGO             AS COD_FUNCAO_BASE,
                    FN.CODIGO             AS COD_FUNCAO_NOVO
             FROM COLABORADORES_BASE CB
                      JOIN EQUIPE EB ON EB.CODIGO = CB.COD_EQUIPE_BASE
                      JOIN EQUIPE EN ON EB.NOME = EN.NOME
                      JOIN SETOR SB ON CB.COD_SETOR_BASE = SB.CODIGO
                      JOIN SETOR SN ON SB.NOME = SN.NOME
                      JOIN FUNCAO FB ON CB.COD_FUNCAO_BASE = FB.CODIGO
                      JOIN FUNCAO FN ON FB.NOME = FN.NOME
                      JOIN CPFS_VALIDOS_CADASTRO CVC ON CVC.CODIGO = CB.CODIGO
             WHERE EB.COD_UNIDADE = F_COD_UNIDADE_BASE
               AND EN.COD_UNIDADE = F_COD_UNIDADE_USUARIO
               AND SB.COD_UNIDADE = F_COD_UNIDADE_BASE
               AND SN.COD_UNIDADE = F_COD_UNIDADE_USUARIO
               AND FB.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND FN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- INSERE OS COLABORADORES DE->PARA.
    INSERT
    INTO COLABORADOR_DATA(CPF,
                          DATA_NASCIMENTO,
                          DATA_ADMISSAO,
                          STATUS_ATIVO,
                          NOME,
                          COD_EQUIPE,
                          COD_FUNCAO,
                          COD_UNIDADE,
                          COD_PERMISSAO,
                          COD_EMPRESA,
                          COD_SETOR,
                          COD_UNIDADE_CADASTRO,
                          DELETADO)
    SELECT DDP.CPF_CADASTRO,
           DDP.DATA_NASCIMENTO_BASE,
           DDP.DATA_ADMISSAO_BASE,
           TRUE,
           DDP.NOME_BASE,
           DDP.COD_EQUIPE_NOVA,
           DDP.COD_FUNCAO_NOVO,
           F_COD_UNIDADE_USUARIO,
           DDP.COD_PERMISSAO_BASE,
           F_COD_EMPRESA_USUARIO,
           DDP.COD_SETOR_NOVO,
           F_COD_UNIDADE_USUARIO,
           FALSE
    FROM DADOS_DE_PARA DDP;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona veículos de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_VEICULOS(F_COD_EMPRESA_BASE BIGINT,
                                                       F_COD_UNIDADE_BASE BIGINT,
                                                       F_COD_EMPRESA_USUARIO BIGINT,
                                                       F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_PLACA_PREFIXO_PADRAO          TEXT   := 'ZXY';
    V_PLACA_SUFIXO_PADRAO           BIGINT := 0;
    V_PLACA_VERIFICACAO             TEXT;
    V_PLACAS_VALIDAS_CADASTRO       TEXT[];
    V_TENTATIVA_BUSCAR_PLACA_VALIDA BIGINT := 0;

BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT MV.CODIGO FROM MODELO_VEICULO MV WHERE MV.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem modelos de veículos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM TIPOS DE VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT VT.CODIGO FROM VEICULO_TIPO VT WHERE VT.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem tipos de veículos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem veículos para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE VEÍCULOS.
    INSERT INTO MODELO_VEICULO (NOME,
                                COD_MARCA,
                                COD_EMPRESA)
    SELECT MV.NOME,
           MV.COD_MARCA,
           F_COD_EMPRESA_USUARIO
    FROM MODELO_VEICULO MV
    WHERE MV.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT NOMES_UNICOS_POR_EMPRESA_E_MARCA DO NOTHING;

    -- COPIA OS TIPOS DE VEÍCULOS.
    INSERT INTO VEICULO_TIPO(NOME,
                             STATUS_ATIVO,
                             COD_DIAGRAMA,
                             COD_EMPRESA)
    SELECT VT.NOME,
           VT.STATUS_ATIVO,
           VT.COD_DIAGRAMA,
           F_COD_EMPRESA_USUARIO
    FROM VEICULO_TIPO VT
    WHERE VT.COD_EMPRESA = F_COD_EMPRESA_BASE;

    --SELECIONA PLACAS VÁLIDAS PARA CADASTRO.
    WHILE
    ((ARRAY_LENGTH(V_PLACAS_VALIDAS_CADASTRO, 1) < (SELECT COUNT(VD.PLACA)
                                                    FROM VEICULO_DATA VD
                                                    WHERE VD.COD_UNIDADE = F_COD_UNIDADE_BASE)) OR
     (ARRAY_LENGTH(V_PLACAS_VALIDAS_CADASTRO, 1) IS NULL))
        LOOP
            --EXISTEM 10000 PLACAS DISPONÍVEIS PARA CADASTRO (DE ZXY0000 ATÉ ZXY9999),
            --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            IF (V_TENTATIVA_BUSCAR_PLACA_VALIDA = 10000)
            THEN
                RAISE EXCEPTION
                    'Não existem placas válidas para serem cadastradas';
            END IF;
            V_PLACA_VERIFICACAO := CONCAT(V_PLACA_PREFIXO_PADRAO, LPAD(V_PLACA_SUFIXO_PADRAO::TEXT, 4, '0'));
            IF NOT EXISTS(SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA ILIKE V_PLACA_VERIFICACAO)
            THEN
                -- PLACAS VÁLIDAS PARA CADASTRO.
                V_PLACAS_VALIDAS_CADASTRO := ARRAY_APPEND(V_PLACAS_VALIDAS_CADASTRO, V_PLACA_VERIFICACAO);
            END IF;
            V_PLACA_SUFIXO_PADRAO := V_PLACA_SUFIXO_PADRAO + 1;
            V_TENTATIVA_BUSCAR_PLACA_VALIDA := V_TENTATIVA_BUSCAR_PLACA_VALIDA + 1;
        END LOOP;

    WITH PLACAS_VALIDAS_CADASTRO AS (
        SELECT ROW_NUMBER() OVER () AS CODIGO,
               VDN                  AS PLACA_CADASTRO
        FROM UNNEST(V_PLACAS_VALIDAS_CADASTRO) VDN),
         VEICULOS_BASE AS (
             SELECT ROW_NUMBER() OVER () AS CODIGO,
                    VD.PLACA             AS PLACA_BASE,
                    VD.KM                AS KM_BASE,
                    VD.COD_MODELO        AS MODELO_BASE,
                    VD.COD_TIPO          AS TIPO_BASE,
                    VD.COD_DIAGRAMA      AS COD_DIAGRAMA_BASE
             FROM VEICULO_DATA VD
             WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
         ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PVC.PLACA_CADASTRO, VB.PLACA_BASE) PVC.PLACA_CADASTRO   AS PLACA_CADASTRO,
                                                                    VB.PLACA_BASE        AS PLACA_BASE,
                                                                    VB.KM_BASE           AS KM_BASE,
                                                                    MVA.CODIGO           AS MODELO_BASE,
                                                                    MVN.CODIGO           AS MODELO_NOVO,
                                                                    VTA.CODIGO           AS TIPO_BASE,
                                                                    VTN.CODIGO           AS TIPO_NOVO,
                                                                    VB.COD_DIAGRAMA_BASE AS COD_DIAGRAMA_BASE
             FROM VEICULOS_BASE VB
                      JOIN MODELO_VEICULO MVA ON MVA.CODIGO = VB.MODELO_BASE
                      JOIN MODELO_VEICULO MVN ON MVA.NOME = MVN.NOME AND MVA.COD_MARCA = MVN.COD_MARCA
                      JOIN VEICULO_TIPO VTA ON VB.TIPO_BASE = VTA.CODIGO
                      JOIN VEICULO_TIPO VTN ON VTA.NOME = VTN.NOME AND VTA.COD_DIAGRAMA = VTN.COD_DIAGRAMA
                      JOIN PLACAS_VALIDAS_CADASTRO PVC ON PVC.CODIGO = VB.CODIGO
             WHERE MVA.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND MVN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
               AND VTA.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND VTN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- INSERE AS PLACAS DE->PARA.
    INSERT
    INTO VEICULO_DATA(PLACA,
                      COD_UNIDADE,
                      KM,
                      STATUS_ATIVO,
                      COD_TIPO,
                      COD_MODELO,
                      COD_EIXOS,
                      COD_UNIDADE_CADASTRO,
                      DELETADO,
                      COD_EMPRESA,
                      COD_DIAGRAMA)
    SELECT DDP.PLACA_CADASTRO,
           F_COD_UNIDADE_USUARIO,
           DDP.KM_BASE,
           TRUE,
           DDP.TIPO_NOVO,
           DDP.MODELO_NOVO,
           1,
           F_COD_UNIDADE_USUARIO,
           FALSE,
           F_COD_EMPRESA_USUARIO,
           DDP.COD_DIAGRAMA_BASE
    FROM DADOS_DE_PARA DDP;
END ;
$$;
--######################################################################################################################
--######################################################################################################################
-- SOBRE:
-- A LÓGICA APLICADA NESSA FUNCTION É A SEGUINTE:
-- CLONA PNEUS DE UMA UNIDADE PARA OUTRA.
--
-- HISTÓRICO:
-- 2020-04-06 -> FUNCTION CRIADA (THAISKSF - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE BIGINT,
                                                    F_COD_UNIDADE_BASE BIGINT,
                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                    F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE PNEUS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MARCAS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MAB.CODIGO FROM MARCA_BANDA MAB WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MARCAS DE BANDAS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MODELOS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MOB.CODIGO FROM MODELO_BANDA MOB WHERE MOB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE BANDA PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM PNEUS PARA SEREM COPIADOS DA UNIDADE DE CÓDIGO: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE PNEUS.
    INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT MP.NOME,
           MP.COD_MARCA,
           F_COD_EMPRESA_USUARIO,
           MP.QT_SULCOS,
           MP.ALTURA_SULCOS
    FROM MODELO_PNEU MP
    WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE;

    -- COPIA AS MARCAS DE BANDAS.
    INSERT INTO MARCA_BANDA(NOME, COD_EMPRESA)
    SELECT MAB.NOME,
           F_COD_EMPRESA_USUARIO
    FROM MARCA_BANDA MAB
    WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT UNIQUE_MARCA_BANDA
        DO NOTHING;

    -- REALIZA O DE -> PARA DOS CÓDIGOS DE MARCAS DE BANDA E INSERE OS MODELOS
    WITH DADOS_MARCA_BANDA_DE_PARA AS (
        SELECT MABB.CODIGO       AS COD_MARCA_BANDA_BASE,
               MABB.NOME         AS NOME_MARCA_BANDA_BASE,
               MABN.CODIGO       AS COD_MARCA_BANDA_NOVO,
               MABN.CODIGO       AS NOME_MARCA_BANDA_NOVO,
               MOB.NOME          AS NOME_MODELO_BANDA_BASE,
               MOB.ALTURA_SULCOS AS ALTURA_SULCOS_BANDA_BASE,
               MOB.QT_SULCOS     AS QT_SULCOS_BANDA_BASE
        FROM MARCA_BANDA MABB
                 JOIN MARCA_BANDA MABN ON MABB.NOME = MABN.NOME
                 JOIN MODELO_BANDA MOB ON MABB.CODIGO = MOB.COD_MARCA AND MABB.COD_EMPRESA = MOB.COD_EMPRESA
        WHERE MABB.COD_EMPRESA = F_COD_EMPRESA_BASE
          AND MABN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- REALIZA A CLONAGEM DE MODELOS DE BANDA COM O CÓDIGO DAS MARCAS DE->PARA.
    INSERT
    INTO MODELO_BANDA(NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT DMBDP.NOME_MODELO_BANDA_BASE,
           DMBDP.COD_MARCA_BANDA_NOVO,
           F_COD_EMPRESA_USUARIO,
           DMBDP.QT_SULCOS_BANDA_BASE,
           DMBDP.ALTURA_SULCOS_BANDA_BASE
    FROM DADOS_MARCA_BANDA_DE_PARA DMBDP
    ON CONFLICT ON CONSTRAINT UNIQUE_NOME_MODELO_BANDA_POR_MARCA
        DO NOTHING;

    -- DADOS DE PARA
    WITH PNEUS_BASE AS (
        SELECT PD.CODIGO_CLIENTE               AS NUMERO_FOGO_BASE,
               PD.COD_MODELO                   AS COD_MODELO_PNEU_BASE,
               PD.COD_DIMENSAO                 AS COD_DIMENSAO_BASE,
               PD.PRESSAO_RECOMENDADA          AS PRESSAO_RECOMENDADA_BASE,
               PD.PRESSAO_ATUAL                AS PRESSAO_ATUAL_BASE,
               PD.ALTURA_SULCO_INTERNO         AS ALTURA_SULCO_INTERNO_BASE,
               PD.ALTURA_SULCO_CENTRAL_INTERNO AS ALTURA_SULCO_CENTRAL_INTERNO_BASE,
               PD.ALTURA_SULCO_EXTERNO         AS ALTURA_SULCO_EXTERNO_BASE,
               PD.STATUS                       AS STATUS_BASE,
               PD.VIDA_ATUAL                   AS VIDA_ATUAL_BASE,
               PD.VIDA_TOTAL                   AS VIDA_TOTAL_BASE,
               PD.COD_MODELO_BANDA             AS COD_MODELO_BANDA_BASE,
               PD.ALTURA_SULCO_CENTRAL_EXTERNO AS ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
               PD.DOT                          AS DOT_BASE,
               PD.VALOR                        AS VALOR_BASE
        FROM PNEU PD
        WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
    ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PB.NUMERO_FOGO_BASE) PB.NUMERO_FOGO_BASE,
                                                      PB.COD_MODELO_PNEU_BASE,
                                                      MPB.CODIGO  AS COD_MODELO_PNEU_BASE,
                                                      MPN.CODIGO  AS COD_MODELO_PNEU_NOVO,
                                                      PB.COD_DIMENSAO_BASE,
                                                      PB.PRESSAO_RECOMENDADA_BASE,
                                                      PB.PRESSAO_ATUAL_BASE,
                                                      PB.ALTURA_SULCO_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_EXTERNO_BASE,
                                                      PB.STATUS_BASE,
                                                      PB.VIDA_ATUAL_BASE,
                                                      PB.VIDA_TOTAL_BASE,
                                                      PB.COD_MODELO_BANDA_BASE,
                                                      MABB.NOME   AS NOME_MARCA_BANDA_BASE,
                                                      MABN.NOME   AS NOME_MARCA_BANSA_NOVA,
                                                      MABB.CODIGO AS COD_MARCA_BANDA_BASE,
                                                      MABN.CODIGO AS COD_MARCA_BANDA_NOVA,
                                                      MOBB.CODIGO AS COD_MODELO_BANDA_BASE,
                                                      MOBN.CODIGO AS COD_MODELO_BANDA_NOVO,
                                                      PB.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
                                                      PB.DOT_BASE,
                                                      PB.VALOR_BASE
             FROM PNEUS_BASE PB
                      JOIN MODELO_PNEU MPB
                           ON MPB.CODIGO = PB.COD_MODELO_PNEU_BASE AND MPB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      JOIN MODELO_PNEU MPN
                           ON MPB.NOME = MPN.NOME AND MPB.COD_MARCA = MPN.COD_MARCA AND
                              MPN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MODELO_BANDA MOBB
                                ON PB.COD_MODELO_BANDA_BASE = MOBB.CODIGO AND MOBB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      LEFT JOIN MODELO_BANDA MOBN ON MOBB.NOME = MOBN.NOME AND MOBN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MARCA_BANDA MABB ON MABB.CODIGO = MOBB.COD_MARCA
                      LEFT JOIN MARCA_BANDA MABN ON MABN.CODIGO = MOBN.COD_MARCA AND MABB.NOME = MABN.NOME)

         -- REALIZA A CLONAGEM DE PNEUS
    INSERT
    INTO PNEU_DATA (CODIGO_CLIENTE,
                    COD_MODELO,
                    COD_DIMENSAO,
                    PRESSAO_RECOMENDADA,
                    PRESSAO_ATUAL,
                    ALTURA_SULCO_INTERNO,
                    ALTURA_SULCO_CENTRAL_INTERNO,
                    ALTURA_SULCO_EXTERNO,
                    COD_UNIDADE,
                    STATUS,
                    VIDA_ATUAL,
                    VIDA_TOTAL,
                    COD_MODELO_BANDA,
                    ALTURA_SULCO_CENTRAL_EXTERNO,
                    DOT,
                    VALOR,
                    COD_EMPRESA,
                    COD_UNIDADE_CADASTRO)
    SELECT DDP.NUMERO_FOGO_BASE,
           DDP.COD_MODELO_PNEU_NOVO,
           DDP.COD_DIMENSAO_BASE,
           DDP.PRESSAO_RECOMENDADA_BASE,
           DDP.PRESSAO_ATUAL_BASE,
           DDP.ALTURA_SULCO_INTERNO_BASE,
           DDP.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
           DDP.ALTURA_SULCO_EXTERNO_BASE,
           F_COD_UNIDADE_USUARIO,
           DDP.STATUS_BASE,
           DDP.VIDA_ATUAL_BASE,
           DDP.VIDA_TOTAL_BASE,
           DDP.COD_MODELO_BANDA_NOVO,
           DDP.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
           DDP.DOT_BASE,
           DDP.VALOR_BASE,
           F_COD_EMPRESA_USUARIO,
           F_COD_UNIDADE_USUARIO
    FROM DADOS_DE_PARA DDP;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona vinculo de veículos_pneus de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE FUNCTION INTERNO.FUNC_CLONA_VINCULO_VEICULOS_PNEUS(F_COD_UNIDADE_BASE BIGINT, F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
DECLARE
    V_PLACAS_COM_VINCULO TEXT := (SELECT ARRAY_AGG(VP.PLACA)
                                  FROM VEICULO_PNEU VP
                                  WHERE VP.COD_UNIDADE = F_COD_UNIDADE_BASE);
BEGIN
    -- COPIA VÍNCULOS, CASO EXISTAM.
    IF (V_PLACAS_COM_VINCULO IS NOT NULL)
    THEN
        WITH VEICULOS_BASE AS (
            SELECT ROW_NUMBER() OVER () AS CODIGO,
                   V.PLACA,
                   VDPP.POSICAO_PROLOG
            FROM VEICULO_DATA V
                     JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO AND V.COD_EMPRESA = VT.COD_EMPRESA
                     JOIN VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                          ON VT.COD_DIAGRAMA = VDPP.COD_DIAGRAMA
            WHERE V.COD_UNIDADE = F_COD_UNIDADE_BASE
        ),
             VEICULOS_NOVOS AS (
                 SELECT ROW_NUMBER() OVER () AS CODIGO, V.PLACA, V.COD_DIAGRAMA, VDPP.POSICAO_PROLOG
                 FROM VEICULO_DATA V
                          JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO AND V.COD_EMPRESA = VT.COD_EMPRESA
                          JOIN VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP ON VT.COD_DIAGRAMA = VDPP.COD_DIAGRAMA
                 WHERE V.COD_UNIDADE = F_COD_UNIDADE_USUARIO),
             DADOS_DE_PARA AS (
                 SELECT VN.PLACA          AS PLACA_NOVA,
                        VN.POSICAO_PROLOG AS POSICAO_PROLOG_NOVO,
                        VN.COD_DIAGRAMA   AS COD_DIAGRAMA_NOVO,
                        PDN.CODIGO        AS COD_PNEU_NOVO
                 FROM VEICULOS_BASE VB
                          JOIN VEICULOS_NOVOS VN ON VB.CODIGO = VN.CODIGO AND VB.POSICAO_PROLOG = VN.POSICAO_PROLOG
                          JOIN VEICULO_PNEU VP ON VB.PLACA = VP.PLACA AND VB.POSICAO_PROLOG = VP.POSICAO
                          JOIN PNEU_DATA PDB
                               ON VP.STATUS_PNEU = PDB.STATUS AND VP.COD_UNIDADE = PDB.COD_UNIDADE AND
                                  VP.COD_PNEU = PDB.CODIGO
                          JOIN PNEU_DATA PDN
                               ON PDB.CODIGO_CLIENTE = PDN.CODIGO_CLIENTE AND
                                  PDN.COD_UNIDADE = F_COD_UNIDADE_USUARIO AND
                                  PDN.STATUS = 'EM_USO')
        INSERT
        INTO VEICULO_PNEU (PLACA, COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA)
        SELECT DDP.PLACA_NOVA,
               DDP.COD_PNEU_NOVO,
               F_COD_UNIDADE_USUARIO,
               DDP.POSICAO_PROLOG_NOVO,
               DDP.COD_DIAGRAMA_NOVO
        FROM DADOS_DE_PARA DDP;
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona nomenclaturas de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_NOMENCLATURAS(F_COD_EMPRESA_BASE BIGINT,
                                                            F_COD_EMPRESA_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- COPIA NOMENCLATURAS, CASO EXISTAM.
    IF EXISTS(SELECT PPNE.NOMENCLATURA
              FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
              WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_EMPRESA,
                                                       COD_DIAGRAMA,
                                                       POSICAO_PROLOG,
                                                       NOMENCLATURA,
                                                       DATA_HORA_CADASTRO)
        SELECT F_COD_EMPRESA_USUARIO,
               PPNE.COD_DIAGRAMA,
               PPNE.POSICAO_PROLOG,
               PPNE.NOMENCLATURA,
               now()
        FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
        WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA_BASE;
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Reseta uma empresa de apresentação de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_RESETA_EMPRESA_APRESENTACAO(F_COD_EMPRESA_BASE BIGINT,
                                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                                    OUT MENSAGEM_SUCESSO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_UNIDADES_BASE                     BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_BASE);
    V_COD_UNIDADE_BASE                      BIGINT;
    V_COD_UNIDADES_USUARIO                  BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_USUARIO);
    V_COD_UNIDADE_USUARIO_NOVA              BIGINT;
    V_COD_COLABORADORES_USUARIO             BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM COLABORADOR_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_AFERICOES                         BIGINT[] := (SELECT ARRAY_AGG(AD.CODIGO)
                                                         FROM AFERICAO_DATA AD
                                                         WHERE AD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS                        BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM CHECKLIST_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS_MODELO                 BIGINT[] := (SELECT DISTINCT ARRAY_AGG(CMD.CODIGO)
                                                         FROM CHECKLIST_MODELO_DATA CMD
                                                         WHERE CMD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_TOKENS_CHECKLISTS_OFF                 TEXT     := (SELECT ARRAY_AGG(CODU.TOKEN_SINCRONIZACAO_CHECKLIST)
                                                         FROM CHECKLIST_OFFLINE_DADOS_UNIDADE CODU
                                                         WHERE CODU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MOVIMENTACOES                     BIGINT[] := (SELECT ARRAY_AGG(MO.CODIGO)
                                                         FROM MOVIMENTACAO MO
                                                         WHERE MO.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SOCORROS                          BIGINT[] := (SELECT ARRAY_AGG(SR.CODIGO)
                                                         FROM SOCORRO_ROTA SR
                                                         WHERE SR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS BIGINT[] := (SELECT ARRAY_AGG(VTP.CODIGO)
                                                         FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
                                                         WHERE (VTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (VTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_PNEU_TRANSFERENCIAS_PROCESSOS     BIGINT[] := (SELECT ARRAY_AGG(PTP.CODIGO)
                                                         FROM PNEU_TRANSFERENCIA_PROCESSO PTP
                                                         WHERE (PTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (PTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_COLABORADORES_NPS                 BIGINT[] := (SELECT ARRAY_AGG(COLABORADORES.COD_COLABORADOR_NPS)
                                                         FROM (SELECT NBPC.COD_COLABORADOR_BLOQUEIO AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_BLOQUEIO_PESQUISA_COLABORADOR NBPC
                                                               WHERE NBPC.COD_COLABORADOR_BLOQUEIO = ANY (V_COD_COLABORADORES_USUARIO)
                                                               UNION
                                                               SELECT NR.COD_COLABORADOR_RESPOSTAS AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_RESPOSTAS NR
                                                               WHERE NR.COD_COLABORADOR_RESPOSTAS = ANY (V_COD_COLABORADORES_USUARIO)) COLABORADORES);
    V_COD_TREINAMENTOS                      BIGINT[] := (SELECT ARRAY_AGG(T.CODIGO)
                                                         FROM TREINAMENTO T
                                                         WHERE T.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SERVICOS_REALIZADOS               BIGINT[] := (SELECT ARRAY_AGG(PSR.CODIGO)
                                                         FROM PNEU_SERVICO_REALIZADO_DATA PSR
                                                         WHERE PSR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_INTERVALO                         BIGINT[] := (SELECT ARRAY_AGG(IU.COD_UNIDADE)
                                                         FROM INTERVALO_UNIDADE IU
                                                         WHERE IU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MARCACOES                         BIGINT[] := (SELECT ARRAY_AGG(I.CODIGO)
                                                         FROM INTERVALO I
                                                         WHERE I.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_RELATOS                           BIGINT[] := (SELECT ARRAY_AGG(R.CODIGO)
                                                         FROM RELATO R
                                                         WHERE R.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_QUIZ                              BIGINT[] := (SELECT ARRAY_AGG(Q.CODIGO)
                                                         FROM QUIZ Q
                                                         WHERE Q.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_FALE_CONOSCO                      BIGINT[] := (SELECT ARRAY_AGG(FC.CODIGO)
                                                         FROM FALE_CONOSCO FC
                                                         WHERE FC.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_TESTES_AFERIDOR                   BIGINT[] := (SELECT ARRAY_AGG(PT.CODIGO)
                                                         FROM AFERIDOR.PROCEDIMENTO_TESTE PT
                                                         WHERE PT.COD_COLABORADOR_EXECUCAO = ANY (V_COD_COLABORADORES_USUARIO));
BEGIN
    -- VERIFICA SE EMPRESAS EXISTEM.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_BASE);
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_USUARIO);

    -- BUSCA E DELETA VÍNCULOS QUE POSSAM EXISTIR DE COLABORADOR, VEÍCULOS E PNEUS.
    --- AFERIÇAO.
    IF (V_COD_AFERICOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_AFERICOES);
    END IF;

    --- CHECKLIST.
    IF ((V_COD_CHECKLISTS IS NOT NULL) OR (V_COD_CHECKLISTS_MODELO IS NOT NULL))
    THEN
        PERFORM INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO,
                                                            V_COD_CHECKLISTS,
                                                            V_COD_CHECKLISTS_MODELO);
    END IF;

    --- DELETA TOKEN CKECKLIST OFFLINE
    -- (MESMO SEM TER CHECKLIST - PODE HAVER O TOKEN - POIS ELE É CRIADO ASSIM QUE UMA UNIDADE É CADASTRADA)
    IF (V_TOKENS_CHECKLISTS_OFF IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TOKENS_CHECKLISTS_OFFLINES(V_COD_UNIDADES_USUARIO);
    END IF;

    -- MOVIMENTAÇÃO.
    IF (V_COD_MOVIMENTACOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_MOVIMENTACOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MOVIMENTACOES);
    END IF;

    --- SOCORRO EM ROTA.
    IF (V_COD_SOCORROS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SOCORROS_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SOCORROS);
    END IF;

    --- TRANSFERENCIA DE VEÍCULOS.
    IF (V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_VEICULOS_DEPENDENCIAS(V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS);
    END IF;

    -- TRANSFERENCIA DE PNEU
    IF (V_COD_PNEU_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_PNEUS_DEPENDENCIAS(V_COD_PNEU_TRANSFERENCIAS_PROCESSOS,
                                                                      V_COD_UNIDADES_USUARIO);
    END IF;

    -- INTERVALO
    IF (V_COD_INTERVALO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_INTERVALO_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MARCACOES);
    END IF;

    -- NPS
    IF (V_COD_COLABORADORES_NPS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_NPS(V_COD_COLABORADORES_NPS);
    END IF;

    -- PRODUTIVIDADE
    IF EXISTS(SELECT AP.COD_UNIDADE FROM ACESSOS_PRODUTIVIDADE AP WHERE AP.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO))
    THEN
        PERFORM INTERNO.FUNC_DELETA_PRODUTIVIDADES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- RELATO
    IF (V_COD_RELATOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_RELATOS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- QUIZ
    IF (V_COD_QUIZ IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_QUIZ_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_QUIZ);
    END IF;

    -- TREINAMENTO
    IF (V_COD_TREINAMENTOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TREINAMENTOS_DEPENDENCIAS(V_COD_TREINAMENTOS);
    END IF;

    -- SERVICO PNEU
    IF (V_COD_SERVICOS_REALIZADOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SERVICOS_PNEU_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SERVICOS_REALIZADOS);
    END IF;

    -- FALE CONOSCO
    IF (V_COD_FALE_CONOSCO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_FALE_CONOSCO(V_COD_FALE_CONOSCO);
    END IF;

    -- TESTES AFERIDOR
    IF (V_COD_TESTES_AFERIDOR IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TESTES_AFERIDOR(V_COD_TESTES_AFERIDOR);
    END IF;

    -- DELETA VEÍCULOS
    PERFORM INTERNO.FUNC_DELETA_VEICULOS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA PNEUS
    PERFORM INTERNO.FUNC_DELETA_PNEUS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA COLABORADORES
    PERFORM INTERNO.FUNC_DELETA_COLABORADORES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA UNIDADES
    PERFORM INTERNO.FUNC_DELETA_UNIDADES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- CLONAGENS
    --- CLONA UNIDADES
    PERFORM INTERNO.FUNC_CLONA_UNIDADES(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    --- CLONA NOMENCLATURAS
    PERFORM INTERNO.FUNC_CLONA_NOMENCLATURAS(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    FOREACH V_COD_UNIDADE_BASE IN ARRAY V_COD_UNIDADES_BASE
        LOOP
            V_COD_UNIDADE_USUARIO_NOVA := (SELECT UNOVA.CODIGO
                                           FROM UNIDADE UBASE
                                                    JOIN UNIDADE UNOVA ON UBASE.NOME = UNOVA.NOME
                                           WHERE UBASE.CODIGO = V_COD_UNIDADE_BASE
                                             AND UNOVA.COD_EMPRESA = F_COD_EMPRESA_USUARIO);

            --- CLONA VEÍCULOS
            IF EXISTS(SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VEICULOS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                    V_COD_UNIDADE_USUARIO_NOVA);

            END IF;

            --- CLONA PNEUS
            IF EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                 V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA VINCULOS
            IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VINCULO_VEICULOS_PNEUS(V_COD_UNIDADE_BASE, V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA COLABORADORES
            IF EXISTS(SELECT CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_COLABORADORES(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE,
                                                         F_COD_EMPRESA_USUARIO,
                                                         V_COD_UNIDADE_USUARIO_NOVA);
            END IF;
        END LOOP;

    SELECT 'A EMPRESA FOI RESETADA E OS DADOS FORAM CLONADOS COM SUCESSO.' INTO MENSAGEM_SUCESSO;
END;
$$;

END TRANSACTION;