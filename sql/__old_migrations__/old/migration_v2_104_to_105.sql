BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
-- PL-2276
-- 1 - Dropa trigger para evitar logs demais.
DROP TRIGGER TG_FUNC_AUDIT_VEICULO ON VEICULO_DATA;

-- 2 - Adiciona coluna com FK.
ALTER TABLE VEICULO_DATA
    ADD COLUMN COD_EMPRESA BIGINT;
ALTER TABLE VEICULO_DATA
    ADD CONSTRAINT FK_VEICULO_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO);

-- 3 - Popula dados atuais.
UPDATE VEICULO_DATA
SET COD_EMPRESA = (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = COD_UNIDADE);

-- 4 - Seta constraint de NOT NULL;
ALTER TABLE VEICULO_DATA
    ALTER COLUMN COD_EMPRESA SET NOT NULL;

-- 5 - recria trigger.
CREATE TRIGGER TG_FUNC_AUDIT_VEICULO
    AFTER INSERT OR UPDATE OR DELETE
    ON VEICULO_DATA
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

-- 6 - Altera function de inserção de veículo por integração.
-- Sobre:
-- Function para inserir um veículo integrado no ProLog, salvando tanto na tabela VEICULO_DATA quanto na
-- INTEGRACAO.VEICULO_CADASTRADO.
--
-- Histórico:
-- 2019-10-07 -> Adiciona cod_empresa ao insert de veículo (luizfp - PL-2276)
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                                         F_PLACA_VEICULO_CADASTRADO TEXT,
                                                                         F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                                         F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                                         F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                         F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO  BIGINT  := (SELECT TI.COD_EMPRESA
                                     FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                     WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
    STATUS_VEICULO       BOOLEAN := TRUE;
    COD_VEICULO_PROLOG   BIGINT;
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN

    -- Validamos se a Unidade pertence a mesma empresa do token
    IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO)
        <>
        (SELECT TI.COD_EMPRESA
         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT(
                                '[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s", verificar vinculos',
                                F_TOKEN_INTEGRACAO,
                                F_COD_UNIDADE_VEICULO_ALOCADO));
    END IF;

    -- Validamos se a placa já existe no ProLog
    IF (SELECT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                               F_PLACA_VEICULO_CADASTRADO));
    END IF;

    -- Validamos se o KM foi inputado corretamente
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                               COD_UNIDADE,
                               PLACA,
                               KM,
                               STATUS_ATIVO,
                               COD_TIPO,
                               COD_MODELO,
                               COD_UNIDADE_CADASTRO)
    VALUES (COD_EMPRESA_VEICULO,
            F_COD_UNIDADE_VEICULO_ALOCADO,
            F_PLACA_VEICULO_CADASTRADO,
            F_KM_ATUAL_VEICULO_CADASTRADO,
            STATUS_VEICULO,
            F_COD_TIPO_VEICULO_CADASTRADO,
            F_COD_MODELO_VEICULO_CADASTRADO,
            F_COD_UNIDADE_VEICULO_ALOCADO) RETURNING CODIGO INTO COD_VEICULO_PROLOG;

    INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                              COD_UNIDADE_CADASTRO,
                                              COD_VEICULO_CADASTRO_PROLOG,
                                              PLACA_VEICULO_CADASTRO,
                                              DATA_HORA_CADASTRO_PROLOG)
    VALUES (COD_EMPRESA_VEICULO,
            F_COD_UNIDADE_VEICULO_ALOCADO,
            COD_VEICULO_PROLOG,
            F_PLACA_VEICULO_CADASTRADO,
            F_DATA_HORA_VEICULO_CADASTRO);

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;

-- 7 - Melhora FKs.
-- Corrige placas que estão com tipos de outra empresa.
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 19
WHERE PLACA = 'EEE6790';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 19
WHERE PLACA = 'FFZ4542';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 19
WHERE PLACA = 'DWC0665';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 20
WHERE PLACA = 'EEM8819';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 20
WHERE PLACA = 'DPX9251';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 20
WHERE PLACA = 'ENM8911';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 412
WHERE PLACA = 'FOL5499';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 412
WHERE PLACA = 'BZR5788';
UPDATE PUBLIC.VEICULO_DATA
SET COD_TIPO = 412
WHERE PLACA = 'FDE2289';

ALTER TABLE VEICULO_TIPO
    ADD CONSTRAINT UNIQUE_VEICULO_TIPO_EMPRESA UNIQUE (CODIGO, COD_EMPRESA);
ALTER TABLE VEICULO_DATA
    DROP CONSTRAINT FK_VEICULO_TIPO;
ALTER TABLE VEICULO_DATA
    ADD CONSTRAINT FK_VEICULO_TIPO
        FOREIGN KEY (COD_TIPO, COD_EMPRESA) REFERENCES VEICULO_TIPO (CODIGO, COD_EMPRESA);

ALTER TABLE MODELO_VEICULO
    ADD CONSTRAINT UNIQUE_MODELO_VEICULO_EMPRESA UNIQUE (CODIGO, COD_EMPRESA);
ALTER TABLE VEICULO_DATA
    DROP CONSTRAINT FK_VEICULO_MODELO_VEICULO;
ALTER TABLE VEICULO_DATA
    ADD CONSTRAINT FK_VEICULO_MODELO_VEICULO
        FOREIGN KEY (COD_MODELO, COD_EMPRESA) REFERENCES MODELO_VEICULO (CODIGO, COD_EMPRESA);

-- 8 - Recria a view contendo a nova coluna.
DROP VIEW ESTRATIFICACAO_OS;
DROP VIEW VEICULO;

CREATE OR REPLACE VIEW VEICULO AS
SELECT V.PLACA,
       V.COD_UNIDADE,
       V.COD_EMPRESA,
       V.KM,
       V.STATUS_ATIVO,
       V.COD_TIPO,
       V.COD_MODELO,
       V.COD_EIXOS,
       V.DATA_HORA_CADASTRO,
       V.COD_UNIDADE_CADASTRO,
       V.CODIGO
FROM VEICULO_DATA V
WHERE V.DELETADO = FALSE;

CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
SELECT COS.CODIGO                                                       AS COD_OS,
       REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
       C.PLACA_VEICULO,
       C.KM_VEICULO                                                     AS KM,
       timezone(tz_unidade(COS.COD_UNIDADE), C.DATA_HORA)               AS DATA_HORA,
       C.TIPO                                                           AS TIPO_CHECKLIST,
       CP.CODIGO                                                        AS COD_PERGUNTA,
       CP.ORDEM                                                         AS ORDEM_PERGUNTA,
       CP.PERGUNTA,
       CP.SINGLE_CHOICE,
       NULL :: UNKNOWN                                                  AS URL_IMAGEM,
       CAP.PRIORIDADE,
       CASE CAP.PRIORIDADE
           WHEN 'CRITICA' :: TEXT
               THEN 1
           WHEN 'ALTA' :: TEXT
               THEN 2
           WHEN 'BAIXA' :: TEXT
               THEN 3
           ELSE NULL :: INTEGER
           END                                                          AS PRIORIDADE_ORDEM,
       CAP.CODIGO                                                       AS COD_ALTERNATIVA,
       CAP.ALTERNATIVA,
       PRIO.PRAZO,
       CR.RESPOSTA,
       V.COD_TIPO,
       COS.COD_UNIDADE,
       COS.STATUS                                                       AS STATUS_OS,
       COS.COD_CHECKLIST,
       tz_unidade(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
       COSI.STATUS_RESOLUCAO                                            AS STATUS_ITEM,
       MECANICO.NOME                                                    AS NOME_MECANICO,
       COSI.CPF_MECANICO,
       COSI.TEMPO_REALIZACAO,
       COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
       COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
       COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
       COSI.KM                                                          AS KM_FECHAMENTO,
       COSI.QT_APONTAMENTOS,
       COSI.FEEDBACK_CONSERTO,
       COSI.CODIGO
FROM (((((((((CHECKLIST C
    JOIN COLABORADOR REALIZADOR ON ((REALIZADOR.CPF = C.CPF_COLABORADOR)))
    JOIN VEICULO V ON (((V.PLACA) :: TEXT = (C.PLACA_VEICULO) :: TEXT)))
    JOIN CHECKLIST_ORDEM_SERVICO COS ON (((C.CODIGO = COS.COD_CHECKLIST) AND (C.COD_UNIDADE = COS.COD_UNIDADE))))
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI ON (((COS.CODIGO = COSI.COD_OS) AND (COS.COD_UNIDADE = COSI.COD_UNIDADE))))
    JOIN CHECKLIST_PERGUNTAS CP ON ((((CP.COD_UNIDADE = COS.COD_UNIDADE) AND (CP.CODIGO = COSI.COD_PERGUNTA)) AND
                                     (CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO))))
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON ((
            (((CAP.COD_UNIDADE = CP.COD_UNIDADE) AND (CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO)) AND
             (CAP.COD_PERGUNTA = CP.CODIGO)) AND (CAP.CODIGO = COSI.COD_ALTERNATIVA))))
    JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO ON (((PRIO.PRIORIDADE) :: TEXT = (CAP.PRIORIDADE) :: TEXT)))
    JOIN CHECKLIST_RESPOSTAS CR ON ((((((C.COD_UNIDADE = CR.COD_UNIDADE) AND
                                        (CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO)) AND
                                       (CR.COD_CHECKLIST = C.CODIGO)) AND (CR.COD_PERGUNTA = CP.CODIGO)) AND
                                     (CR.COD_ALTERNATIVA = CAP.CODIGO))))
         LEFT JOIN COLABORADOR MECANICO ON ((MECANICO.CPF = COSI.CPF_MECANICO)));

COMMENT ON VIEW ESTRATIFICACAO_OS
    IS 'View que compila as informações das OS e seus itens';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################### ALTERA NOME DE DIAGRAMA: CARRETA SINGLE ####################################
--############################################## PARA CARRETA 2 EIXOS SINGLE ###########################################
--######################################################################################################################
UPDATE VEICULO_DIAGRAMA
SET NOME = 'CARRETA 2 EIXOS SINGLES'
WHERE CODIGO = 11;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--#########################################  CRIA NOVO DIAGRAMA PARA PRAXIO  ###########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2360
-- Criamos o diagrama
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM)
VALUES (17, 'CARRETA 1 EIXO', 'WWW.GOOGLE.COM/CARRETA');

-- Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (17, 'T', 1, 4, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 211);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 212);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 221);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 222);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 900);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 901);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 902);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 903);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 904);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 905);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 906);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 907);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (17, 908);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2362
INSERT INTO PUBLIC.VEICULO_DIAGRAMA (CODIGO, NOME, URL_IMAGEM)
VALUES (18, 'CARRETA 3 EIXOS SINGLE', 'WWW.GOOGLE.COM/CARRETA-SINGLE');

INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (18, 'T', 1, 2, FALSE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (18, 'T', 2, 2, FALSE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (18, 'T', 3, 2, FALSE);

INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 211);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 221);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 311);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 321);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 411);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 421);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 900);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 901);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 902);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 903);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 904);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 905);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 906);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 907);
INSERT INTO PUBLIC.VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (18, 908);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
CREATE SCHEMA AFERIDOR;

CREATE TABLE IF NOT EXISTS AFERIDOR.COMANDOS_BLUETOOTH
(
  CODIGO           BIGSERIAL NOT NULL,
  NOME             TEXT      NOT NULL,
  DESCRICAO        TEXT      NOT NULL,
  COMANDO_ENVIO    TEXT,
  VALOR_RETORNO    TEXT,
  EXEMPLO_EXECUCAO TEXT      NOT NULL,
  CONSTRAINT PK_COMANDO PRIMARY KEY (CODIGO)
);
COMMENT ON TABLE AFERIDOR.COMANDOS_BLUETOOTH IS 'Salva todos os comandos bluetooth compreensíveis pelo Aferidor Zalf.';

CREATE TABLE IF NOT EXISTS AFERIDOR.COMANDOS_TESTE
(
  ONE_ROW  BOOLEAN NOT NULL DEFAULT TRUE,
  COMANDOS TEXT[]  NOT NULL,
  CONSTRAINT PK_COMANDOS_TESTE PRIMARY KEY (ONE_ROW),
  CONSTRAINT CHECK_ONE_ROW CHECK (ONE_ROW)
);
COMMENT ON TABLE AFERIDOR.COMANDOS_TESTE
IS 'Salva os comandos que devem ser enviados ao aferidor para concluirmos um procedimento de testes.
Essa tabela está restrita a ter no máximo uma linha.';

INSERT INTO AFERIDOR.COMANDOS_TESTE (COMANDOS)
VALUES (ARRAY ['D', 'B', 'S']);

CREATE TABLE IF NOT EXISTS AFERIDOR.PROCEDIMENTO_TESTE
(
  CODIGO                   BIGSERIAL                NOT NULL,
  COD_COLABORADOR_EXECUCAO BIGINT                   NOT NULL,
  DATA_HORA_EXECUCAO       TIMESTAMP WITH TIME ZONE NOT NULL,
  NOME_DISPOSITIVO         TEXT                     NOT NULL,
  VALORES_EXECUCAO         JSONB                    NOT NULL,
  CONSTRAINT PK_PROCEDIMENTO_TESTE PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PROCEDIMENTO_TESTE_COLABORADOR FOREIGN KEY (COD_COLABORADOR_EXECUCAO)
  REFERENCES COLABORADOR_DATA (CODIGO)
);
COMMENT ON TABLE AFERIDOR.PROCEDIMENTO_TESTE IS 'Salva os procedimentos de testes já realizados no aferidor.';

CREATE OR REPLACE FUNCTION AFERIDOR.FUNC_AFERIDOR_GET_PROCEDIMENTO_TESTE()
  RETURNS TEXT[]
LANGUAGE SQL
AS
$$
SELECT CT.COMANDOS FROM AFERIDOR.COMANDOS_TESTE CT;
$$;

CREATE OR REPLACE FUNCTION AFERIDOR.FUNC_AFERIDOR_INSERE_TESTE(F_COD_COLABORADOR_EXECUCAO BIGINT,
                                                               F_DATA_HORA_EXECUCAO TIMESTAMP WITH TIME ZONE,
                                                               F_NOME_DISPOSITIVO TEXT,
                                                               F_COMANDOS_EXECUTADOS JSONB)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS
$$
DECLARE
  CODIGO_PROCEDIMENTO_TESTE BIGINT;
BEGIN
  INSERT INTO AFERIDOR.PROCEDIMENTO_TESTE (COD_COLABORADOR_EXECUCAO,
                                           DATA_HORA_EXECUCAO,
                                           NOME_DISPOSITIVO,
                                           VALORES_EXECUCAO)
  VALUES (F_COD_COLABORADOR_EXECUCAO,
          F_DATA_HORA_EXECUCAO,
          F_NOME_DISPOSITIVO,
          F_COMANDOS_EXECUTADOS) RETURNING CODIGO INTO CODIGO_PROCEDIMENTO_TESTE;

  IF NOT FOUND
  THEN
    RAISE EXCEPTION 'Erro ao salvar teste realizado!';
  END IF;

  RETURN CODIGO_PROCEDIMENTO_TESTE;
END;
$$;

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Dado de pressão',
        'Transmite após a estabilização da leitura',
        null,
        'P + valor da pressão em PSI',
        'P115.75 = 115,75PSI');


INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Dado de profundidade',
        'Transmite após a estabilização da leitura',
        null,
        'T + valor de profundidade em milímetros',
        'T19.53 = 19,53mm');


INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Carga da bateria',
        'Retorna a carga da bateria (em porcentagem) quando recebe B pelo Bluetooth.',
        'B',
        'B + porcentagem da bateria',
        E'Envio: B\n' ||
        'Retorno: B95');


INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Versão do Firmware',
        'Retorna a versão do firmware quando recebe S pelo Bluetooth',
        'S',
        'S + versão do firmware',
        E'Envio: S\n' ||
        'Retorno: S1.0.0');


INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Identificação do modelo de aferidor',
        'Retornar YES ao receber ALARM? pelo Bluetooth',
        'ALARM?',
        'YES',
        E'Envio: ALARM?\n' ||
        'Retorno: YES');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Medição correta',
        'Aviso visual com LED (azul) e sonoro curto com buzzer',
        'OK',
        null,
        'Envio: OK');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Medição errada',
        'Aviso visual com LED (laranja) e sonoro longo com buzzer',
        'FAIL',
        null,
        'Envio: FAIL');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Temperatura máxima e mínima da registrada na placa',
        'Retorna os valores máximos temperatura registrados na placa. A atualização dos registros é realizada a cada 60s',
        'get_temp',
        'Temperature log: Min = XXoC, Max = YYoC',
        E'Envio: get_temp\n' ||
        'Retorno: Temperature log: Min = 23oC, Max = 34oC');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Apagar os registros de temperatura',
        'Apaga os registros de temperatura. Necessário realizar o comando após o carregamento do firmware. A atualização dos registros a cada 60s',
        'del_temp',
        'Temperature records deleted!',
        E'Envio: get_temp\n' ||
        'Retorno: Temperature records deleted!');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Nome do dispositivo',
        'Retorna o nome do aferidor, sendo o mesmo que aparece no pareamento do Bluetooth',
        'D',
        'Aferidor Zalf XXXXX',
        E'Envio: D\n' ||
        'Retorno: Aferidor Zalf 00001');

INSERT INTO AFERIDOR.COMANDOS_BLUETOOTH (NOME,
                                         DESCRICAO,
                                         COMANDO_ENVIO,
                                         VALOR_RETORNO,
                                         EXEMPLO_EXECUCAO)
VALUES ('Calibração de profundidade',
        'Envia o ponto de calibração que está sendo aplicado ao aferidor. Ver: 4. Calibração',
        'V + profundidade atual do sensor em milímetros.',
        null,
        'Envio: V9.8');
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################

-- PL-2350

create schema cs;

create table cs.nps_pesquisa
(
    codigo                      bigserial not null,
    titulo_pesquisa             text      not null,
    breve_descricao_pesquisa    varchar(255),
    periodo_veiculacao_pesquisa daterange not null,
    titulo_pergunta_escala      text      not null,
    titulo_pergunta_descritiva  text,
    status_ativo                boolean   not null default true,
    -- Para impedir que pesquisas diferentes tenham períodos de veiculação que se sobreponham.
    exclude using gist (periodo_veiculacao_pesquisa with &&) where (status_ativo),
    constraint pk_nps_pesquisa primary key (codigo)
);

-- Apenas uma ativa por vez.
create unique index idx_nps_pesquisa_status_ativo on cs.nps_pesquisa (status_ativo)
where status_ativo;

comment on table cs.nps_pesquisa is 'Salva as pesquisas de NPS que queremos disponibilizar aos usuários do ProLog.
Só é permitido uma única pesquisa ativa por vez. Também não é permitido pesquisas diferentes com datas de veiculação que se sobreponham.';


create table cs.nps_respostas
(
    codigo                        bigserial                not null,
    cod_nps_pesquisa              bigint                   not null,
    cod_colaborador_respostas     bigint                   not null,
    data_hora_realizacao_pesquisa timestamp with time zone not null,
    resposta_pergunta_escala      smallint                 not null,
    resposta_pergunta_descritiva  text,
    constraint pk_nps_respostas primary key (codigo),
    constraint fk_nps_respostas_nps_pesquisa foreign key (cod_nps_pesquisa)
        references cs.nps_pesquisa (codigo),
    constraint fk_nps_respostas_colaborador foreign key (cod_colaborador_respostas)
        references colaborador_data (codigo),
    constraint unique_resposta_nps_colaborador unique (cod_nps_pesquisa, cod_colaborador_respostas),
    constraint check_resposta_escala_valor_valido
        check (resposta_pergunta_escala >= 0 and resposta_pergunta_escala <= 10)
);
comment on table cs.nps_respostas is 'Salva as respostas dos usuários a uma pesquisa de NPS realizada no ProLog.';


create table cs.nps_bloqueio_pesquisa_colaborador
(
    cod_nps_pesquisa            bigint                   not null,
    cod_colaborador_bloqueio    bigint                   not null,
    data_hora_bloqueio_pesquisa timestamp with time zone not null,
    constraint fk_nps_respostas_nps_pesquisa foreign key (cod_nps_pesquisa)
        references cs.nps_pesquisa (codigo),
    constraint fk_nps_respostas_colaborador foreign key (cod_colaborador_bloqueio)
        references colaborador_data (codigo),
    constraint unique_bloqueio_nps_colaborador unique (cod_nps_pesquisa, cod_colaborador_bloqueio)
);
comment on table cs.nps_bloqueio_pesquisa_colaborador is 'Salva quais pesquisas o colaborador bloqueou para não ser exibida mais para ele.
Dessa forma, mesmo que uma pesquisa esteja ativa e ainda em veiculação, se um colaborador bloqueá-la, ela não será mais exibida para ele,
mesmo que ele ainda não a tenha respondido.';


create or replace function cs.func_nps_insere_nova_pesquisa(f_titulo_pesquisa text,
                                                            f_breve_descricao_pesquisa text,
                                                            f_titulo_pergunta_escala text,
                                                            f_titulo_pergunta_descritiva text,
                                                            f_data_inicio_veiculacao_inclusivo date,
                                                            f_data_fim_veiculacao_exclusivo date,
                                                            out aviso_pesquisa_inserida text)
    returns text
    language plpgsql
    -- Para o time de CS poder usar.
    security definer
as
$$
begin
    -- Antes de inserir uma nova pesquisa, inativa todas as anteriores.
    update cs.nps_pesquisa set status_ativo = false;

    insert into cs.nps_pesquisa (titulo_pesquisa,
                                 breve_descricao_pesquisa,
                                 periodo_veiculacao_pesquisa,
                                 titulo_pergunta_escala,
                                 titulo_pergunta_descritiva)
    values (f_titulo_pesquisa,
            f_breve_descricao_pesquisa,
            daterange(f_data_inicio_veiculacao_inclusivo, f_data_fim_veiculacao_exclusivo),
            f_titulo_pergunta_escala,
            f_titulo_pergunta_descritiva);

    if not found
    then
        raise exception 'Erro ao inserir nova pesquisa de NPS';
    end if;

    select 'Pesquisa de NPS inserida com sucesso!'
    into aviso_pesquisa_inserida;
end;
$$;


create or replace function cs.func_nps_busca_pesquisa_disponivel(f_cod_colaborador bigint,
                                                                 f_data_atual date)
    returns table
            (
                COD_PESQUISA_NPS           BIGINT,
                TITULO_PESQUISA            TEXT,
                BREVE_DESCRICAO_PESQUISA   TEXT,
                TITULO_PERGUNTA_ESCALA     TEXT,
                TITULO_PERGUNTA_DESCRITIVA TEXT
            )
    language plpgsql
as
$$
declare
    f_cod_pesquisa_nps           bigint;
    f_titulo_pesquisa            text;
    f_breve_descricao_pesquisa   text;
    f_titulo_pergunta_escala     text;
    f_titulo_pergunta_descritiva text;
begin
    -- Mesmo tendo o index para permitir apenas uma ativa por vez, esse SELECT já garante isso também.
    select np.codigo,
           np.titulo_pesquisa,
           np.breve_descricao_pesquisa,
           np.titulo_pergunta_escala,
           np.titulo_pergunta_descritiva
    from cs.nps_pesquisa np
    -- Ativo e ainda em veiculação.
    where np.status_ativo
    and f_data_atual <@ periodo_veiculacao_pesquisa
    into
        f_cod_pesquisa_nps,
        f_titulo_pesquisa,
        f_breve_descricao_pesquisa,
        f_titulo_pergunta_escala,
        f_titulo_pergunta_descritiva;

    if f_cod_pesquisa_nps is null
    then
        return query
        select null :: bigint, null :: text, null :: text, null :: text, null :: text;

        -- Break.
        return;
    end if;

    -- Se o colaborador ainda não respondeu e também não bloqueou a pesquisa, então temos uma disponível.
    if ((select not exists(select nbpc.cod_nps_pesquisa
                          from cs.nps_bloqueio_pesquisa_colaborador nbpc
                          where nbpc.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nbpc.cod_colaborador_bloqueio = f_cod_colaborador))
        and
        (select not exists(select nr.cod_nps_pesquisa
                          from cs.nps_respostas nr
                          where nr.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nr.cod_colaborador_respostas = f_cod_colaborador)))
    then
        return query
            select f_cod_pesquisa_nps,
                   f_titulo_pesquisa,
                   f_breve_descricao_pesquisa,
                   f_titulo_pergunta_escala,
                   f_titulo_pergunta_descritiva;
    end if;
end;
$$;


create or replace function cs.func_nps_insere_respostas_pesquisa(f_cod_pesquisa_nps bigint,
                                                                 f_cod_colaborador_realizacao bigint,
                                                                 f_data_hora_realizacao_pesquisa timestamp with time zone,
                                                                 f_resposta_pergunta_escala smallint,
                                                                 f_resposta_pergunta_descritiva text)
    returns bigint
    language plpgsql
as
$$
declare
    cod_respostas_pesquisa_nps bigint;
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_respostas (cod_nps_pesquisa,
                                  cod_colaborador_respostas,
                                  data_hora_realizacao_pesquisa,
                                  resposta_pergunta_escala,
                                  resposta_pergunta_descritiva)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao,
            f_data_hora_realizacao_pesquisa,
            f_resposta_pergunta_escala,
            f_resposta_pergunta_descritiva) returning codigo into cod_respostas_pesquisa_nps;

    if not FOUND
    then
        raise exception 'Erro ao inserir respostas da pesquisa de NPS % para colaborador %',
            f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao;
    end if;

    return cod_respostas_pesquisa_nps;
end;
$$;

create or replace function cs.func_nps_bloqueia_pesquisa(f_cod_pesquisa_nps bigint,
                                                         f_cod_colaborador_bloqueio bigint,
                                                         f_data_hora_bloqueio_pesquisa timestamp with time zone)
    returns void
    language plpgsql
as
$$
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_bloqueio_pesquisa_colaborador (cod_nps_pesquisa,
                                                      cod_colaborador_bloqueio,
                                                      data_hora_bloqueio_pesquisa)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio,
            f_data_hora_bloqueio_pesquisa);

    if not found
    then
        raise exception 'Erro ao bloquear pesquisa de NPS % para colaborador %',
            f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio;
    end if;
end;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;