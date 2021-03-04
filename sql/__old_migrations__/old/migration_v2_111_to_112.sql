BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
-- PL-2490
-- Devemos corrigir as movimentações
UPDATE MOVIMENTACAO_PROCESSO
SET DATA_HORA = DATA_HORA AT TIME ZONE TZ_UNIDADE(COD_UNIDADE) AT TIME ZONE 'UTC'
WHERE DATA_HORA::DATE > '2019-11-24';

-- Devemos corrigir os serviços fechados
UPDATE AFERICAO_MANUTENCAO
SET DATA_HORA_RESOLUCAO = DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(COD_UNIDADE) AT TIME ZONE 'UTC'
WHERE DATA_HORA_RESOLUCAO IS NOT NULL
  AND DATA_HORA_RESOLUCAO::DATE > '2019-11-24';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###############################    FUNC_VEICULO_TRANSFERE_VEICULO_ENTRE_EMPRESAS    ##################################
--######################################################################################################################
--######################################################################################################################
-- PL-2383
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_TRANSFERE_VEICULO_ENTRE_EMPRESAS(F_PLACA_VEICULO VARCHAR(7),
                                                                                 F_COD_EMPRESA_ORIGEM BIGINT,
                                                                                 F_COD_UNIDADE_ORIGEM BIGINT,
                                                                                 F_COD_EMPRESA_DESTINO BIGINT,
                                                                                 F_COD_UNIDADE_DESTINO BIGINT,
                                                                                 F_COD_MODELO_VEICULO_DESTINO BIGINT,
                                                                                 F_COD_TIPO_VEICULO_DESTINO BIGINT,
                                                                                 OUT VEICULO_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_NOME_EMPRESA_DESTINO                           VARCHAR(255) := (SELECT E.NOME
                                                                      FROM EMPRESA E
                                                                      WHERE E.CODIGO = F_COD_EMPRESA_DESTINO);
    F_NOME_UNIDADE_DESTINO                           VARCHAR(255) := (SELECT U.NOME
                                                                      FROM UNIDADE U
                                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO);
    F_LISTA_COD_AFERICAO_PLACA                       BIGINT[];
    F_COD_AFERICAO_FOREACH                           BIGINT;
    F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO          BIGINT[];
    F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES           BIGINT;
    F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES BIGINT;


BEGIN
    --VERIFICA SE EMPRESA ORIGEM POSSUI UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_ORIGEM);

    --VERIFICA SE EMPRESA DESTINO POSSUI UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

    --VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS.
    PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(F_COD_EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

    --VERIFICA SE VEICULO EXISTE
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE_ORIGEM, F_PLACA_VEICULO);

    --VERIFICA SE A PLACA POSSUI PNEUS.
    IF EXISTS(SELECT VP.COD_PNEU
              FROM VEICULO_PNEU VP
              WHERE VP.PLACA = F_PLACA_VEICULO
                AND VP.COD_UNIDADE = F_COD_UNIDADE_ORIGEM)
    THEN
        RAISE EXCEPTION 'ERRO! A PLACA: % POSSUI PNEUS VINCULADOS, FAVOR REMOVER OS PNEUS DO MESMO', F_PLACA_VEICULO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI TIPO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(
            SELECT VT.CODIGO
            FROM VEICULO_TIPO VT
            WHERE VT.COD_EMPRESA = F_COD_EMPRESA_DESTINO
              AND VT.CODIGO = F_COD_TIPO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO TIPO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_TIPO_VEICULO_DESTINO,
            F_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI MODELO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(SELECT MV.CODIGO
                  FROM MODELO_VEICULO MV
                  WHERE MV.COD_EMPRESA = F_COD_EMPRESA_DESTINO
                    AND MV.CODIGO = F_COD_MODELO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO MODELO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_MODELO_VEICULO_DESTINO,
            F_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO
              FROM AFERICAO A
              WHERE A.PLACA_VEICULO = F_PLACA_VEICULO)
    THEN
        --ENTÃO COLETAMOS TODOS OS CÓDIGOS DAS AFERIÇÕES QUE A PLACA POSSUI E ADICIONAMOS NO ARRAY.
        SELECT DISTINCT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO A
        WHERE A.PLACA_VEICULO = F_PLACA_VEICULO
        INTO F_LISTA_COD_AFERICAO_PLACA;

        --LAÇO FOR PARA PERCORRER TODOS OS VALORES EM F_LISTA_COD_AFERICAO_PLACA.
        FOREACH F_COD_AFERICAO_FOREACH IN ARRAY F_LISTA_COD_AFERICAO_PLACA
            LOOP
                --PARA CADA VALOR EM: F_LISTA_COD_AFERICAO_PLACA
                IF EXISTS(SELECT AM.COD_AFERICAO
                          FROM AFERICAO_MANUTENCAO AM
                          WHERE AM.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                            AND AM.DATA_HORA_RESOLUCAO IS NULL
                            AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                            AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE)
                THEN
                    --COLETA O(S) COD_PNEU CORRESPONDENTES AO COD_AFERICAO.
                    SELECT ARRAY_AGG(AM.COD_PNEU)
                    FROM AFERICAO_MANUTENCAO AM
                    WHERE AM.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND AM.DATA_HORA_RESOLUCAO IS NULL
                      AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                      AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                    INTO F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO;

                    --DELETA AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_MANUTENCAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);

                    --DELETA AFERICAO EM AFERICAO_VALORES_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_VALORES_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = F_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (F_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);
                END IF;
            END LOOP;

        --SE, E SOMENTE SE, A AFERIÇÃO POSSUIR TODOS OS VALORES EXCLUÍDOS, DEVE-SE EXCLUIR TODA A AFERIÇÃO.
        --SENÃO, A AFERIÇÃO CONTINUA EXISTINDO.
        FOREACH F_COD_AFERICAO_FOREACH IN ARRAY F_LISTA_COD_AFERICAO_PLACA
            LOOP
                F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                          FROM AFERICAO_VALORES_DATA AVD
                                                          WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH);

                F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                                    FROM AFERICAO_VALORES_DATA AVD
                                                                    WHERE AVD.COD_AFERICAO = F_COD_AFERICAO_FOREACH
                                                                      AND AVD.DELETADO IS TRUE);

                --SE A QUANTIDADE DE UM COD_AFERICAO EM AFERICAO_VALORES_DATA FOR IGUAL A QUANTIDADE DE UM COD_AFERICAO
                --DELETADO EM AFERICAO_VALORES_DATA, DEVEMOS EXCLUIR A AFERIÇÃO, POIS, TODOS SEUS VALORES FORAM
                --DELETADOS.
                IF (F_QTD_COD_AFERICAO_EM_AFERICAO_VALORES =
                    F_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND CODIGO = F_COD_AFERICAO_FOREACH;
                END IF;
            END LOOP;
    END IF;

    --REALIZA TRANSFERÊNCIA.
    UPDATE VEICULO_DATA
    SET COD_EMPRESA = F_COD_EMPRESA_DESTINO,
        COD_UNIDADE = F_COD_UNIDADE_DESTINO,
        COD_TIPO    = F_COD_TIPO_VEICULO_DESTINO,
        COD_MODELO  = F_COD_MODELO_VEICULO_DESTINO
    WHERE COD_EMPRESA = F_COD_EMPRESA_ORIGEM
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM
      AND PLACA = F_PLACA_VEICULO;

    --MENSAGEM DE SUCESSO.
    SELECT 'VEÍCULO TRANSFERIDO COM SUCESSO! O VEÍCULO COM PLACA: ' || F_PLACA_VEICULO ||
           ' FOI TRANSFERIDO PARA A EMPRESA ' || F_NOME_EMPRESA_DESTINO || ' JUNTO A UNIDADE ' ||
           F_NOME_UNIDADE_DESTINO || '.'
    INTO VEICULO_TRANSFERIDO;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--############################################ CRIAÇÃO DE NOVOS DIAGRAMAS ##############################################
--######################################################################################################################
-- PL-2479
-- Mapeia posições de 6º, 7º e 8º eixo
INSERT INTO PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO)
VALUES (611, NULL),
       (621, NULL),
       (612, NULL),
       (622, NULL),
       (711, NULL),
       (721, NULL),
       (712, NULL),
       (722, NULL),
       (811, NULL),
       (821, NULL),
       (812, NULL),
       (822, NULL);

-- Mapeia novas posições - 8 pneus por eixo
INSERT INTO PNEU_POSICAO (POSICAO_PNEU, DESCRICAO_POSICAO)
VALUES (113, NULL),
       (123, NULL),
       (114, NULL),
       (124, NULL),
       (213, NULL),
       (223, NULL),
       (214, NULL),
       (224, NULL),
       (313, NULL),
       (323, NULL),
       (314, NULL),
       (324, NULL),
       (413, NULL),
       (423, NULL),
       (414, NULL),
       (424, NULL),
       (513, NULL),
       (523, NULL),
       (514, NULL),
       (524, NULL),
       (613, NULL),
       (623, NULL),
       (614, NULL),
       (624, NULL),
       (713, NULL),
       (723, NULL),
       (714, NULL),
       (724, NULL),
       (813, NULL),
       (823, NULL),
       (814, NULL),
       (824, NULL);

-- Cria nova sequência na tabela pneu_ordem
DELETE
FROM PNEU_ORDEM
WHERE POSICAO_PROLOG IS NOT NULL;
INSERT INTO PNEU_ORDEM(POSICAO_PROLOG, ORDEM_EXIBICAO)
VALUES (111, 1),
       (112, 2),
       (113, 3),
       (114, 4),
       (211, 5),
       (212, 6),
       (213, 7),
       (214, 8),
       (311, 9),
       (312, 10),
       (313, 11),
       (314, 12),
       (411, 13),
       (412, 14),
       (413, 15),
       (414, 16),
       (511, 17),
       (512, 18),
       (513, 19),
       (514, 20),
       (611, 21),
       (612, 22),
       (613, 23),
       (614, 24),
       (711, 25),
       (712, 26),
       (713, 27),
       (714, 28),
       (811, 29),
       (812, 30),
       (813, 31),
       (814, 32),
       (821, 33),
       (822, 34),
       (823, 35),
       (824, 36),
       (721, 37),
       (722, 38),
       (723, 39),
       (724, 40),
       (621, 41),
       (622, 42),
       (623, 43),
       (624, 44),
       (521, 45),
       (522, 46),
       (523, 47),
       (524, 48),
       (421, 49),
       (422, 50),
       (423, 51),
       (424, 52),
       (321, 53),
       (322, 54),
       (323, 55),
       (324, 56),
       (221, 57),
       (222, 58),
       (223, 59),
       (224, 60),
       (121, 61),
       (122, 62),
       (123, 63),
       (124, 64),
       (900, 90),
       (901, 91),
       (902, 92),
       (903, 93),
       (904, 94),
       (905, 95),
       (906, 96),
       (907, 97),
       (908, 98);

-- PRANCHA 6 EIXOS
-- Criamos o diagrama
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM)
VALUES (20, 'PRANCHA 6 EIXOS', 'WWW.GOOGLE.COM/PRANCHA-6EIXOS');

--Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (20, 'T', 1, 4, FALSE),
       (20, 'T', 2, 4, FALSE),
       (20, 'T', 3, 4, FALSE),
       (20, 'T', 4, 4, FALSE),
       (20, 'T', 5, 4, FALSE),
       (20, 'T', 6, 4, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (20, 111),
       (20, 121),
       (20, 112),
       (20, 122),
       (20, 211),
       (20, 212),
       (20, 221),
       (20, 222),
       (20, 311),
       (20, 312),
       (20, 321),
       (20, 322),
       (20, 411),
       (20, 412),
       (20, 421),
       (20, 422),
       (20, 511),
       (20, 512),
       (20, 521),
       (20, 522),
       (20, 611),
       (20, 612),
       (20, 621),
       (20, 622),
       (20, 900),
       (20, 901),
       (20, 902),
       (20, 903),
       (20, 904),
       (20, 905),
       (20, 906),
       (20, 907),
       (20, 908);

-- PRANCHA 8 EIXOS
-- Criamos o diagrama
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM)
VALUES (21, 'PRANCHA 8 EIXOS', 'WWW.GOOGLE.COM/PRANCHA-8EIXOS');

--Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (21, 'T', 1, 4, FALSE),
       (21, 'T', 2, 4, FALSE),
       (21, 'T', 3, 4, FALSE),
       (21, 'T', 4, 4, FALSE),
       (21, 'T', 5, 4, FALSE),
       (21, 'T', 6, 4, FALSE),
       (21, 'T', 7, 4, FALSE),
       (21, 'T', 8, 4, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (21, 111),
       (21, 121),
       (21, 112),
       (21, 122),
       (21, 211),
       (21, 212),
       (21, 221),
       (21, 222),
       (21, 311),
       (21, 312),
       (21, 321),
       (21, 322),
       (21, 411),
       (21, 412),
       (21, 421),
       (21, 422),
       (21, 511),
       (21, 512),
       (21, 521),
       (21, 522),
       (21, 611),
       (21, 612),
       (21, 621),
       (21, 622),
       (21, 711),
       (21, 712),
       (21, 721),
       (21, 722),
       (21, 811),
       (21, 812),
       (21, 821),
       (21, 822),
       (21, 900),
       (21, 901),
       (21, 902),
       (21, 903),
       (21, 904),
       (21, 905),
       (21, 906),
       (21, 907),
       (21, 908);

-- PRANCHA 4 EIXOS - 8 PNEUS POR EIXO
-- Criamos o diagrama
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM)
VALUES (22, 'PRANCHA 4 EIXOS', 'WWW.GOOGLE.COM/PRANCHA-4EIXOS');

--Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL)
VALUES (22, 'T', 1, 8, FALSE),
       (22, 'T', 2, 8, FALSE),
       (22, 'T', 3, 8, FALSE),
       (22, 'T', 4, 8, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (22, 111),
       (22, 121),
       (22, 112),
       (22, 122),
       (22, 113),
       (22, 123),
       (22, 114),
       (22, 124),
       (22, 211),
       (22, 221),
       (22, 212),
       (22, 222),
       (22, 213),
       (22, 223),
       (22, 214),
       (22, 224),
       (22, 311),
       (22, 321),
       (22, 312),
       (22, 322),
       (22, 313),
       (22, 323),
       (22, 314),
       (22, 324),
       (22, 411),
       (22, 421),
       (22, 412),
       (22, 422),
       (22, 413),
       (22, 423),
       (22, 414),
       (22, 424),
       (22, 900),
       (22, 901),
       (22, 902),
       (22, 903),
       (22, 904),
       (22, 905),
       (22, 906),
       (22, 907),
       (22, 908);
--######################################################################################################################
--######################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ###################### CRIA FUNCTION PARA ALTERAR TIPO DE VEÍCULO MESMO COM PNEUS APLICADOS #########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2191
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_ALTERA_TIPO_VEICULO(F_PLACA_VEICULO TEXT,
                                                                    F_COD_VEICULO_TIPO_NOVO BIGINT,
                                                                    F_COD_UNIDADE BIGINT,
                                                                    OUT AVISO_TIPO_VEICULO_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_DIAGRAMA_NOVO BIGINT := (SELECT VT.COD_DIAGRAMA
                                   FROM VEICULO_TIPO VT
                                   WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO);
    F_COD_EMPRESA       BIGINT := (SELECT U.COD_EMPRESA
                                   FROM UNIDADE U
                                   WHERE U.CODIGO = F_COD_UNIDADE);

BEGIN
    -- Garante que unidade/empresa existem
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Garante que veiculo existe e pertence a unidade
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_VEICULO);

    -- Verifica se veículo não está deletado (a function FUNC_GARANTE_VEICULO_EXISTE, utiliza a veiculo_data, portanto
    -- também trás veículos deletados em sua consulta)
    IF EXISTS(SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_VEICULO AND VD.DELETADO = TRUE)
    THEN
        RAISE EXCEPTION
            'O veículo consta como deletado, placa: %, código da unidade: %.' , F_PLACA_VEICULO,
            (SELECT VD.COD_UNIDADE FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_VEICULO);
    END IF;

    -- Garante que tipo_veiculo_novo pertence a empresa
    IF NOT EXISTS(SELECT VT.CODIGO
                  FROM VEICULO_TIPO VT
                  WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO
                    AND VT.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION
            'O tipo de veículo de código: % Não pertence à empresa: %',
            F_COD_VEICULO_TIPO_NOVO,
            F_COD_EMPRESA;
    END IF;

    -- Verifica se o tipo_veiculo_novo é o atual
    IF EXISTS(SELECT V.CODIGO FROM VEICULO V WHERE V.PLACA = F_PLACA_VEICULO AND V.COD_TIPO = F_COD_VEICULO_TIPO_NOVO)
    THEN
        RAISE EXCEPTION
            'O tipo de veículo atual da placa % é igual ao informado. Código tipo de veículo: %',
            F_PLACA_VEICULO,
            F_COD_VEICULO_TIPO_NOVO;
    END IF;

    -- Verifica se placa tem pneus aplicados
    IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_VEICULO)
    THEN
        -- Se existirem pneus, verifica se os pneus que estão aplicados possuem as mesmas posições do novo tipo
        IF ((SELECT ARRAY_AGG(VP.POSICAO)
             FROM VEICULO_PNEU VP
             WHERE VP.PLACA = F_PLACA_VEICULO) <@
            (SELECT ARRAY_AGG(VDPP.POSICAO_PROLOG :: INTEGER)
             FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
             WHERE COD_DIAGRAMA = F_COD_DIAGRAMA_NOVO) = FALSE)
        THEN
            RAISE EXCEPTION
                'Existem pneus aplicados em posições que não fazem parte do tipo de veículo de código: %',
                F_COD_VEICULO_TIPO_NOVO;
        END IF;
    END IF;

    -- Realiza a mudança de tipo
    UPDATE VEICULO_DATA
    SET COD_TIPO = F_COD_VEICULO_TIPO_NOVO
    WHERE PLACA = F_PLACA_VEICULO
      AND COD_UNIDADE = F_COD_UNIDADE;

    --MENSAGEM DE SUCESSO.
    SELECT 'Tipo do veículo alterado! Placa: ' || F_PLACA_VEICULO ||
           ', Código da unidade: ' || F_COD_UNIDADE ||
           ', Tipo: ' || (SELECT VT.NOME FROM VEICULO_TIPO VT WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO) ||
           ', Código do tipo: ' || F_COD_VEICULO_TIPO_NOVO || '.'
    INTO AVISO_TIPO_VEICULO_ALTERADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ######################    TABELA TOKEN_AUTENTICACAO COM A COLUNA COD_COLABORADOR NOT NULL   #########################
-- #####################################################################################################################
-- #####################################################################################################################
-- PL-2116
-- ATUALIZA TABELA TOKEN_AUTENTICACAO ADICIONANDO OS VALORES DA COLUNA COD_COLABORADOR PARA SEUS RESPECTIVOS.
UPDATE TOKEN_AUTENTICACAO
SET COD_COLABORADOR = (SELECT C.CODIGO FROM COLABORADOR_DATA C WHERE C.CPF = CPF_COLABORADOR)
WHERE COD_COLABORADOR ISNULL;

-- MODIFICA A COLUNA COD_COLABORADOR PARA SER NOT NULL.
ALTER TABLE TOKEN_AUTENTICACAO
    ALTER COLUMN COD_COLABORADOR SET NOT NULL;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;