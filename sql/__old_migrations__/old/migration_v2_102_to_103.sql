BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--############################### TRANSACTION PARA MODIFICAR ESTRUTURA DE NOMENCLATURA #################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### CRIA TABELA PARA MAPEAMENTO DE DIAGRAMA x POSICOES PROLOG ################################
--######################################################################################################################
--######################################################################################################################
-- PL-2277
-- Já lançado em prod, aqui apenas para histórico.

-- CREATE TABLE VEICULO_DIAGRAMA_POSICAO_PROLOG
-- (
--     COD_DIAGRAMA   BIGINT NOT NULL,
--     POSICAO_PROLOG BIGINT NOT NULL,
--     FOREIGN KEY (COD_DIAGRAMA) REFERENCES VEICULO_DIAGRAMA (CODIGO),
--     FOREIGN KEY (POSICAO_PROLOG) REFERENCES PNEU_POSICAO (POSICAO_PNEU),
--     CONSTRAINT DIAGRAMA_POSICAO_PROLOG UNIQUE (COD_DIAGRAMA, POSICAO_PROLOG)
-- );
-- --######################################################################################################################
-- --######################################################################################################################
--
-- --######################################################################################################################
-- --############################## INSERE INFORMAÇÕES NA TABELA VEICULO_DIAGRAMA_POSICAO_PROLOG ##########################
-- --######################################################################################################################
-- -- PL-2277
-- INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG)
-- VALUES
-- -- TOCO
-- (1, 111),
-- (1, 121),
-- (1, 211),
-- (1, 212),
-- (1, 221),
-- (1, 222),
-- (1, 900),
-- (1, 901),
-- (1, 902),
-- (1, 903),
-- (1, 904),
-- (1, 905),
-- (1, 906),
-- (1, 907),
-- (1, 908),
-- -- TRUCK
-- (2, 111),
-- (2, 121),
-- (2, 211),
-- (2, 212),
-- (2, 221),
-- (2, 222),
-- (2, 311),
-- (2, 312),
-- (2, 321),
-- (2, 322),
-- (2, 900),
-- (2, 901),
-- (2, 902),
-- (2, 903),
-- (2, 904),
-- (2, 905),
-- (2, 906),
-- (2, 907),
-- (2, 908),
-- -- CARRETA 3 EIXOS
-- (3, 211),
-- (3, 212),
-- (3, 221),
-- (3, 222),
-- (3, 311),
-- (3, 312),
-- (3, 321),
-- (3, 322),
-- (3, 411),
-- (3, 412),
-- (3, 421),
-- (3, 422),
-- (3, 900),
-- (3, 901),
-- (3, 902),
-- (3, 903),
-- (3, 904),
-- (3, 905),
-- (3, 906),
-- (3, 907),
-- (3, 908),
-- --BI-TRUCK
-- (4, 111),
-- (4, 121),
-- (4, 211),
-- (4, 221),
-- (4, 311),
-- (4, 312),
-- (4, 321),
-- (4, 322),
-- (4, 411),
-- (4, 412),
-- (4, 421),
-- (4, 422),
-- (4, 900),
-- (4, 901),
-- (4, 902),
-- (4, 903),
-- (4, 904),
-- (4, 905),
-- (4, 906),
-- (4, 907),
-- (4, 908),
-- --CARRETA 2 EIXOS
-- (5, 211),
-- (5, 212),
-- (5, 222),
-- (5, 221),
-- (5, 311),
-- (5, 312),
-- (5, 322),
-- (5, 321),
-- (5, 900),
-- (5, 901),
-- (5, 902),
-- (5, 903),
-- (5, 904),
-- (5, 905),
-- (5, 906),
-- (5, 907),
-- (5, 908),
-- --EMPILHADEIRA SIMPLES
-- (6, 111),
-- (6, 121),
-- (6, 211),
-- (6, 221),
-- (6, 900),
-- (6, 901),
-- (6, 902),
-- (6, 903),
-- (6, 904),
-- (6, 905),
-- (6, 906),
-- (6, 907),
-- (6, 908),
-- --EMPILHADEIRA DUPLA
-- (7, 121),
-- (7, 122),
-- (7, 111),
-- (7, 112),
-- (7, 211),
-- (7, 221),
-- (7, 900),
-- (7, 901),
-- (7, 902),
-- (7, 903),
-- (7, 904),
-- (7, 905),
-- (7, 906),
-- (7, 907),
-- (7, 908),
-- --FROTA LEVE E UTILITÁRIOS
-- (8, 111),
-- (8, 121),
-- (8, 211),
-- (8, 221),
-- (8, 900),
-- (8, 901),
-- (8, 902),
-- (8, 903),
-- (8, 904),
-- (8, 905),
-- (8, 906),
-- (8, 907),
-- (8, 908),
-- --CARRETA 4 EIXOS
-- (9, 211),
-- (9, 212),
-- (9, 221),
-- (9, 222),
-- (9, 311),
-- (9, 312),
-- (9, 321),
-- (9, 322),
-- (9, 411),
-- (9, 412),
-- (9, 421),
-- (9, 422),
-- (9, 511),
-- (9, 512),
-- (9, 521),
-- (9, 522),
-- (9, 900),
-- (9, 901),
-- (9, 902),
-- (9, 903),
-- (9, 904),
-- (9, 905),
-- (9, 906),
-- (9, 907),
-- (9, 908),
-- --TRUCK ELÉTRICO
-- (10, 111),
-- (10, 121),
-- (10, 211),
-- (10, 212),
-- (10, 221),
-- (10, 222),
-- (10, 311),
-- (10, 321),
-- (10, 900),
-- (10, 901),
-- (10, 902),
-- (10, 903),
-- (10, 904),
-- (10, 905),
-- (10, 906),
-- (10, 907),
-- (10, 908),
-- --CARRETA SINGLE
-- (11, 211),
-- (11, 221),
-- (11, 311),
-- (11, 321),
-- (11, 900),
-- (11, 901),
-- (11, 902),
-- (11, 903),
-- (11, 904),
-- (11, 905),
-- (11, 906),
-- (11, 907),
-- (11, 908),
-- --MOTO
-- (12, 111),
-- (12, 211),
-- --TRICICLO INVERTIDO
-- (13, 111),
-- (13, 121),
-- (13, 211),
-- --ÔNIBUS BIARTICULADO
-- (14,111),
-- (14,121),
-- (14, 211),
-- (14, 212),
-- (14, 221),
-- (14, 222),
-- (14, 311),
-- (14, 312),
-- (14, 321),
-- (14, 322),
-- (14, 411),
-- (14, 412),
-- (14, 421),
-- (14, 422);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--########################################## CRIA TABELA DE NOMENCLATURA ###############################################
--######################################################################################################################
-- PL-2259
CREATE TABLE PNEU_POSICAO_NOMENCLATURA_EMPRESA
(
    COD_EMPRESA              BIGINT                                 NOT NULL,
    COD_DIAGRAMA             BIGINT                                 NOT NULL,
    POSICAO_PROLOG           INTEGER                                NOT NULL,
    NOMENCLATURA             VARCHAR(255)                           NOT NULL,
    COD_COLABORADOR_CADASTRO BIGINT,
    DATA_HORA_CADASTRO       TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT FK_PNEU_POSICAO_NOMENCLATURA_EMPRESA_EMPRESA
        FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO),
    CONSTRAINT FK_PNEU_POSICAO_NOMENCLATURA_EMPRESA_DIAGRAMA_POSICAO_PROLOG
        FOREIGN KEY (COD_DIAGRAMA, POSICAO_PROLOG)
            REFERENCES VEICULO_DIAGRAMA_POSICAO_PROLOG (COD_DIAGRAMA, POSICAO_PROLOG),
    CONSTRAINT FK_PNEU_POSICAO_NOMENCLATURA_EMPRESA_COLABORADOR
        FOREIGN KEY (COD_COLABORADOR_CADASTRO) REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT UNIQUE_DIAGRAMA_EMPRESA_POSICAO_PROLOG
        UNIQUE (COD_DIAGRAMA, COD_EMPRESA, POSICAO_PROLOG)
);
COMMENT ON TABLE PNEU_POSICAO_NOMENCLATURA_EMPRESA IS
    'Armazena a nomenclatura que cada posição de pneu deve ter para cada empresa/diagrama, EX: 111 / DD';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--####################### MODIFICA FUNCTION PARA REMOVER TAMBÉM CARACTERES QUE SIMULAM "ESPAÇOS" #######################
--######################################################################################################################
CREATE OR REPLACE FUNCTION REMOVE_EXTRA_SPACES(F_TEXT TEXT, F_REMOVE_CHAR_LIKE_SPACE BOOLEAN DEFAULT FALSE) RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF F_REMOVE_CHAR_LIKE_SPACE
    THEN
        RETURN REGEXP_REPLACE(TRIM(REGEXP_REPLACE((F_TEXT), '[\u0080-\u00ff]', '', 'g')), '\s+', ' ', 'g');
        ELSE
        RETURN TRIM(REGEXP_REPLACE(F_TEXT, '\s+', ' ', 'g'));
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--###################################### CRIA FUNCTION PARA FORMATAR NOMENCLATURA ######################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Formata a nomenclatura recebida para que não haja espaços excedentes ou characteres que simulam espaço.
--
-- Precondições:
-- 1) Func remove_extra_spaces criada.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION TG_FUNC_PNEU_NOMENCLATURA_FORMATA_NOMENCLATURA()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    NOVA_NOMENCLATURA TEXT := REMOVE_EXTRA_SPACES(NEW.NOMENCLATURA, TRUE);
BEGIN
    IF NOVA_NOMENCLATURA IS NULL OR NOVA_NOMENCLATURA = ''
    THEN
        PERFORM THROW_GENERIC_ERROR('A nomenclatura não pode estar vazia!');
    END IF;
    RETURN NEW;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--############################# CRIAR TRIGGER PARA FORMATAR NOMENCLATURA ANTES DE INSERIR ##############################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Trigger acionada a cada inserção/update.
--
-- Precondições:
-- 1) Tabela PNEU_POSICAO_NOMENCLATURA_EMPRESA criada
-- 2) Func tg_func_nomenclatura_formata_nomenclatura criada.
--
-- Histórico:
-- 2019-09-03 -> Trigger criada (thaisksf PL-2259).
CREATE TRIGGER TG_FORMATA_NOMENCLATURA
    BEFORE INSERT OR UPDATE
    ON PNEU_POSICAO_NOMENCLATURA_EMPRESA
    FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_PNEU_NOMENCLATURA_FORMATA_NOMENCLATURA();
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--####################### REALIZA MIGRAÇÃO DOS DADOS DA TABELA PNEU_ORDEM_NOMENCLATURA_UNIDADE #########################
--############################## PARA A NOVA TABELA PNEU_POSICAO_NOMENCLATURA_EMPRESA ####################################
--######################################################################################################################
-- PL-2259
INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_DIAGRAMA, COD_EMPRESA, POSICAO_PROLOG, NOMENCLATURA)
WITH DISTINCT_NOMENCLATURA_POR_EMPRESA AS (
    SELECT DISTINCT ON (VD.CODIGO, E.CODIGO, POSICAO_PROLOG, NOMENCLATURA) VD.CODIGO AS COD_DIAGRAMA,
                                                                           VD.NOME   AS DIAGRAMA,
                                                                           E.CODIGO  AS COD_EMPRESA,
                                                                           E.NOME    AS EMPRESA,
                                                                           PONU.POSICAO_PROLOG,
                                                                           PONU.NOMENCLATURA
    FROM PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
             JOIN VEICULO_TIPO VT ON PONU.COD_TIPO_VEICULO = VT.CODIGO
             JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
             JOIN UNIDADE U ON PONU.COD_UNIDADE = U.CODIGO
             JOIN EMPRESA E ON VT.COD_EMPRESA = E.CODIGO AND E.CODIGO = U.COD_EMPRESA)

SELECT DISTINCT ON (DNPE.COD_DIAGRAMA,DNPE.COD_EMPRESA,DNPE.POSICAO_PROLOG) DNPE.COD_DIAGRAMA,
                                                                            DNPE.COD_EMPRESA,
                                                                            DNPE.POSICAO_PROLOG,
                                                                            DNPE.NOMENCLATURA
FROM DISTINCT_NOMENCLATURA_POR_EMPRESA DNPE
         JOIN
     VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP ON DNPE.COD_DIAGRAMA = VDPP.COD_DIAGRAMA
         AND DNPE.POSICAO_PROLOG = VDPP.POSICAO_PROLOG;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--############################# ADICIONA CONSTRAINT PARA CODIGO DO COLABORADOR: NOT NULL ###############################
--######################################################################################################################
ALTER TABLE PNEU_POSICAO_NOMENCLATURA_EMPRESA
    ADD CONSTRAINT CHECK_COLABORADOR_NOT_NULL CHECK (COD_COLABORADOR_CADASTRO IS NOT NULL) NOT VALID;

COMMENT ON COLUMN PNEU_POSICAO_NOMENCLATURA_EMPRESA.COD_COLABORADOR_CADASTRO IS 'Se a coluna possuir valor null, a inserção
foi realizada através da migração da tabela antiga ';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--########################### REMOVE DEFAULT DA DATA_HORA_CADASTRO APÓS A MIGRAÇÃO DOS DADOS ###########################
--######################################################################################################################
ALTER TABLE PNEU_POSICAO_NOMENCLATURA_EMPRESA
    ALTER COLUMN DATA_HORA_CADASTRO DROP DEFAULT;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--####################################### RENOMEIA A ANTIGA TABELA DE NOMENCLATURA #####################################
--######################################################################################################################
ALTER TABLE PNEU_ORDEM_NOMENCLATURA_UNIDADE
    RENAME TO PNEU_ORDEM_NOMENCLATURA_ANTIGA;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################# CRIA FUNC PARA GARANTIR NOMENCLATURA COMPLETA ######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION ARRAY_SORT(ANYARRAY)
    RETURNS ANYARRAY
    LANGUAGE SQL
AS
$$
SELECT ARRAY(SELECT UNNEST($1) ORDER BY 1)
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Garante que a nomenclatura enviada pelo servidor possui todas as posições nomeadas, se possuir retorna true
--
-- Precondições:
-- 1) Tabela veiculo_diagrama_posicao_prolog criada
-- 2) Código do diagrama.
-- 3) Quantidade de objetos (com posições diferentes de estepes) que estão no servidor para realizar insert/update
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_PNEU_NOMENCLATURA_COMPLETA(F_COD_DIAGRAMA BIGINT,
                                                                   F_POSICOES_PROLOG INTEGER[],
                                                                   F_ERROR_MESSAGE TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    POSICAO_ESTEPE BIGINT := 900;
    ERROR_MESSAGE  TEXT   :=
        F_IF(F_ERROR_MESSAGE IS NULL, 'Erro! Nomenclatura incompleta.', F_ERROR_MESSAGE);
BEGIN
    IF (ARRAY_SORT(F_POSICOES_PROLOG) <> (SELECT ARRAY_AGG(VDP.POSICAO_PROLOG ORDER BY VDP.POSICAO_PROLOG) :: INTEGER[]
                                          FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDP
                                          WHERE VDP.COD_DIAGRAMA = F_COD_DIAGRAMA
                                            AND VDP.POSICAO_PROLOG < POSICAO_ESTEPE))
    THEN
        PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ CRIA FUNCTION PARA DELETAR A NOMENCLATURA DE ESTEPES ################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta a nomenclatura de estepes de um diagrama.
--
--
-- Histórico:
-- 2019-09-20 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_DELETA_ESTEPES(F_COD_EMPRESA BIGINT,
                                                                 F_COD_DIAGRAMA BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    ESTEPES BIGINT := 900;
BEGIN
    DELETE FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA WHERE COD_EMPRESA = F_COD_EMPRESA AND
                                                        COD_DIAGRAMA = F_COD_DIAGRAMA AND
                                                        POSICAO_PROLOG >= ESTEPES;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ CRIA FUNCTION PARA CADASTRAR/EDITAR NOMENCLATURA ####################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- insere/edita a nomenclatura de um diagrama de veículo.
--
-- Precondições:
-- 1) Informações necessárias para realizar o insert.
-- 2) Tabela diagrama_empresa_posicao_idioma criada.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                                            F_COD_DIAGRAMA BIGINT,
                                                                            F_POSICAO_PROLOG BIGINT,
                                                                            F_NOMENCLATURA VARCHAR(255),
                                                                            F_TOKEN_RESPONSAVEL_INSERCAO TEXT,
                                                                            F_DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_COLABORADOR_INSERCAO BIGINT := (SELECT CODIGO
                                        FROM COLABORADOR
                                        WHERE CPF = (SELECT CPF_COLABORADOR
                                                     FROM TOKEN_AUTENTICACAO
                                                     WHERE TOKEN = F_TOKEN_RESPONSAVEL_INSERCAO));
BEGIN
    INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_DIAGRAMA,
                                                   COD_EMPRESA,
                                                   POSICAO_PROLOG,
                                                   NOMENCLATURA,
                                                   COD_COLABORADOR_CADASTRO,
                                                   DATA_HORA_CADASTRO)
    VALUES (F_COD_DIAGRAMA,
            F_COD_EMPRESA,
            F_POSICAO_PROLOG,
            F_NOMENCLATURA,
            COD_COLABORADOR_INSERCAO,
            F_DATA_HORA_CADASTRO)
    ON CONFLICT ON CONSTRAINT UNIQUE_DIAGRAMA_EMPRESA_POSICAO_PROLOG
        DO UPDATE SET NOMENCLATURA             = F_NOMENCLATURA,
                      COD_COLABORADOR_CADASTRO = COD_COLABORADOR_INSERCAO,
                      DATA_HORA_CADASTRO       = F_DATA_HORA_CADASTRO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################# FUNCTION GET NOMENCLATURA ##############################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- retorna a nomenclatura das posições de um determinado diagrama.
--
-- Precondições:
-- 1) Informações necessárias para realizar a busca.
-- 2) Informações cadastradas no banco.
--
-- Histórico:
-- 2019-09-03 -> Function criada (thaisksf PL-2259).
CREATE OR REPLACE FUNCTION FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                                   F_COD_DIAGRAMA BIGINT)
    RETURNS TABLE
            (
                NOMENCLATURA   VARCHAR(255),
                POSICAO_PROLOG INTEGER
            )
    LANGUAGE SQL
AS
$$
SELECT PPNE.NOMENCLATURA, PPNE.POSICAO_PROLOG
FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
WHERE PPNE.COD_EMPRESA = F_COD_EMPRESA
  AND PPNE.COD_DIAGRAMA = F_COD_DIAGRAMA
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################## REFATORAÇÃO DAS FUNCTIONS #############################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--################################### FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS ####################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS (TEXT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADE TEXT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
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
                "FECHADO AUTOMATICAMENTE"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                              AS UNIDADE_SERVICO,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                    AS DATA_HORA_AFERICAO,
       TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                    AS DATA_HORA_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             3600)                                                         AS HORAS_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             60)                                                           AS MINUTOS_RESOLUCAO,
       A.PLACA_VEICULO,
       A.KM_VEICULO                                                        AS KM_AFERICAO,
       AM.KM_MOMENTO_CONSERTO,
       AM.KM_MOMENTO_CONSERTO -
       A.KM_VEICULO                                                        AS KM_PERCORRIDO,
       P.CODIGO_CLIENTE                                                    AS CODIGO_CLIENTE_PNEU,
       P.PRESSAO_RECOMENDADA                                               AS PRESSAO_RECOMENDADA_PNEU,
       COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                       AS PSI_AFERICAO,
       COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','),
                '-')                                                       AS DISPERSAO_PRESSAO_ANTES,
       COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                       AS PSI_POS_CONSERTO,
       COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
                        ','),
                '-')                                                       AS DISPERSAO_PRESSAO_DEPOIS,
       COALESCE(PPNE.NOMENCLATURA, '-')                                    AS POSICAO,
       AM.TIPO_SERVICO,
       COALESCE(INITCAP(C.NOME), '-')                                      AS NOME_MECANICO,
       COALESCE(AA.ALTERNATIVA, '-')                                       AS PROBLEMA_APONTADO,
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO, 'Sim' :: TEXT, 'Não') AS TIPO_FECHAMENTO
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

--######################################################################################################################
--####################################### FUNC_AFERICAO_RELATORIO_DADOS_GERAIS #########################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(BIGINT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                F_DATA_INICIAL DATE,
                                                                F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "CÓDIGO AFERIÇÃO"           TEXT,
                "UNIDADE"                   TEXT,
                "DATA E HORA"               TEXT,
                "CPF DO RESPONSÁVEL"        TEXT,
                "NOME COLABORADOR"          TEXT,
                "PNEU"                      TEXT,
                "STATUS ATUAL"              TEXT,
                "VALOR COMPRA"              TEXT,
                "MARCA DO PNEU"             TEXT,
                "MODELO DO PNEU"            TEXT,
                "QTD SULCOS MODELO"         TEXT,
                "VIDA ATUAL"                TEXT,
                "VALOR VIDA ATUAL"          TEXT,
                "BANDA APLICADA"            TEXT,
                "QTD SULCOS BANDA"          TEXT,
                "DIMENSÃO"                  TEXT,
                "DOT"                       TEXT,
                "DATA E HORA CADASTRO"      TEXT,
                "POSIÇÃO PNEU"              TEXT,
                "PLACA"                     TEXT,
                "VIDA MOMENTO AFERIÇÃO"     TEXT,
                "KM NO MOMENTO DA AFERIÇÃO" TEXT,
                "KM ATUAL"                  TEXT,
                "MARCA DO VEÍCULO"          TEXT,
                "MODELO DO VEÍCULO"         TEXT,
                "TIPO DE MEDIÇÃO COLETADA"  TEXT,
                "TIPO DA AFERIÇÃO"          TEXT,
                "TEMPO REALIZAÇÃO (MM:SS)"  TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO :: TEXT                                                                 AS COD_AFERICAO,
       U.NOME                                                                           AS UNIDADE,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI')                                                    AS DATA_HORA_AFERICAO,
       LPAD(C.CPF :: TEXT, 11, '0')                                                     AS CPF_COLABORADOR,
       C.NOME                                                                           AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                 AS CODIGO_CLIENTE_PNEU,
       P.STATUS                                                                         AS STATUS_ATUAL_PNEU,
       ROUND(P.VALOR :: NUMERIC, 2) :: TEXT                                             AS VALOR_COMPRA,
       MAP.NOME                                                                         AS MARCA_PNEU,
       MP.NOME                                                                          AS MODELO_PNEU,
       MP.QT_SULCOS :: TEXT                                                             AS QTD_SULCOS_MODELO,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                              AS VIDA_ATUAL,
       COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                            AS VALOR_VIDA_ATUAL,
       F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado') AS BANDA_APLICADA,
       COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                            AS QTD_SULCOS_BANDA,
       DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                  AS DIMENSAO,
       P.DOT                                                                            AS DOT,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    AS DATA_HORA_CADASTRO,
       COALESCE(PPNE.NOMENCLATURA, '-')                                                 AS POSICAO,
       COALESCE(A.PLACA_VEICULO, '-')                                                   AS PLACA,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                  AS VIDA_MOMENTO_AFERICAO,
       COALESCE(A.KM_VEICULO :: TEXT, '-')                                              AS KM_MOMENTO_AFERICAO,
       COALESCE(V.KM :: TEXT, '-')                                                      AS KM_ATUAL,
       COALESCE(M2.NOME, '-')                                                           AS MARCA_VEICULO,
       COALESCE(MV.NOME, '-')                                                           AS MODELO_VEICULO,
       A.TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA,
       TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')            AS TEMPO_REALIZACAO_MINUTOS,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                  AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                          AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                          AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                  AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                           AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')            AS PRESSAO
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
         JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
         JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
         JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Se foi aferição de pneu avulso, pode não possuir placa.
         LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO

         LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT ON E.CODIGO = VT.COD_EMPRESA AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN MODELO_VEICULO MV
                   ON MV.CODIGO = V.COD_MODELO
         LEFT JOIN MARCA_VEICULO M2
                   ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS #######################################
--######################################################################################################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(BIGINT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                    F_DATA_INICIAL DATE,
                                                                    F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"               TEXT,
                "DATA E HORA"           TEXT,
                "CPF DO RESPONSÁVEL"    TEXT,
                "NOME"                  TEXT,
                "PNEU"                  TEXT,
                "MARCA"                 TEXT,
                "MODELO"                TEXT,
                "BANDA APLICADA"        TEXT,
                "MEDIDAS"               TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO ATUAL (PSI)"   TEXT,
                "VIDA ATUAL"            TEXT,
                "ORIGEM"                TEXT,
                "PLACA DE ORIGEM"       TEXT,
                "POSIÇÃO DE ORIGEM"     TEXT,
                "DESTINO"               TEXT,
                "PLACA DE DESTINO"      TEXT,
                "POSIÇÃO DE DESTINO"    TEXT,
                "KM MOVIMENTAÇÃO"       TEXT,
                "RECAPADORA DESTINO"    TEXT,
                "CÓDIGO COLETA"         TEXT,
                "OBS. MOVIMENTAÇÃO"     TEXT,
                "OBS. GERAL"            TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME,
       TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
       LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
       C.NOME,
       P.CODIGO_CLIENTE                                                                                  AS PNEU,
       MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
       MP.NOME                                                                                           AS NOME_MODELO_PNEU,
       F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado',
            MARB.NOME || ' - ' || MODB.NOME)                                                             AS BANDA_APLICADA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                                    AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                            AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                            AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                                    AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                                             AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
       PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
       O.TIPO_ORIGEM                                                                                     AS ORIGEM,
       COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
       COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
       D.TIPO_DESTINO                                                                                    AS DESTINO,
       COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
       COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
       COALESCE(VORIGEM.KM, VDESTINO.KM) :: TEXT                                                         AS KM_COLETADO_MOVIMENTACAO,
       COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
       COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
       COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
       COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
FROM MOVIMENTACAO_PROCESSO MOVP
         JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
         JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
         JOIN PNEU P ON P.CODIGO = M.COD_PNEU
         JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
         JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL

    -- Terá recapadora apenas se foi movido para análise.
         LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
         LEFT JOIN VEICULO VORIGEM
                   ON O.PLACA = VORIGEM.PLACA
         LEFT JOIN VEICULO_TIPO VTORIGEM ON E.CODIGO = VTORIGEM.COD_EMPRESA AND VTORIGEM.CODIGO = VORIGEM.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDORIGEM ON VTORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_ORIGEM
                   ON NOMENCLATURA_ORIGEM.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_ORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
                       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

    -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
         LEFT JOIN VEICULO VDESTINO
                   ON D.PLACA = VDESTINO.PLACA
         LEFT JOIN VEICULO_TIPO VTDESTINO ON E.CODIGO = VTDESTINO.COD_EMPRESA AND VTDESTINO.CODIGO = VDESTINO.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDDESTINO ON VTDESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_DESTINO
                   ON NOMENCLATURA_DESTINO.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_DESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
                       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS #########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(BIGINT, TEXT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_LISTAGEM_PNEUS_BY_STATUS(F_COD_UNIDADE BIGINT,
                                                                  F_STATUS_PNEU TEXT)
    RETURNS TABLE
            (
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               TEXT,
                DOT                          TEXT,
                VALOR                        REAL,
                COD_UNIDADE_ALOCADO          BIGINT,
                COD_REGIONAL_ALOCADO         BIGINT,
                PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
                COD_MARCA_PNEU               BIGINT,
                NOME_MARCA_PNEU              TEXT,
                COD_MODELO_PNEU              BIGINT,
                NOME_MODELO_PNEU             TEXT,
                QT_SULCOS_MODELO_PNEU        SMALLINT,
                COD_MARCA_BANDA              BIGINT,
                NOME_MARCA_BANDA             TEXT,
                ALTURA_SULCOS_MODELO_PNEU    REAL,
                COD_MODELO_BANDA             BIGINT,
                NOME_MODELO_BANDA            TEXT,
                QT_SULCOS_MODELO_BANDA       SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA   REAL,
                VALOR_BANDA                  REAL,
                ALTURA                       INTEGER,
                LARGURA                      INTEGER,
                ARO                          REAL,
                COD_DIMENSAO                 BIGINT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO         REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                PRESSAO_RECOMENDADA          REAL,
                PRESSAO_ATUAL                REAL,
                STATUS                       TEXT,
                VIDA_ATUAL                   INTEGER,
                VIDA_TOTAL                   INTEGER,
                POSICAO_PNEU                 INTEGER,
                POSICAO_APLICADO_CLIENTE     TEXT,
                COD_VEICULO_APLICADO         BIGINT,
                PLACA_APLICADO               TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT P.CODIGO,
       P.CODIGO_CLIENTE,
       P.DOT,
       P.VALOR,
       U.CODIGO                         AS COD_UNIDADE_ALOCADO,
       R.CODIGO                         AS COD_REGIONAL_ALOCADO,
       P.PNEU_NOVO_NUNCA_RODADO,
       MP.CODIGO                        AS COD_MARCA_PNEU,
       MP.NOME                          AS NOME_MARCA_PNEU,
       MOP.CODIGO                       AS COD_MODELO_PNEU,
       MOP.NOME                         AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                    AS QT_SULCOS_MODELO_PNEU,
       MAB.CODIGO                       AS COD_MARCA_BANDA,
       MAB.NOME                         AS NOME_MARCA_BANDA,
       MOP.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_PNEU,
       MOB.CODIGO                       AS COD_MODELO_BANDA,
       MOB.NOME                         AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                    AS QT_SULCOS_MODELO_BANDA,
       MOB.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_BANDA,
       PVV.VALOR                        AS VALOR_BANDA,
       PD.ALTURA,
       PD.LARGURA,
       PD.ARO,
       PD.CODIGO                        AS COD_DIMENSAO,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       P.PRESSAO_RECOMENDADA,
       P.PRESSAO_ATUAL,
       P.STATUS,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       VP.POSICAO                       AS POSICAO_PNEU,
       COALESCE(PPNE.NOMENCLATURA, '-') AS POSICAO_APLICADO,
       VEI.CODIGO                       AS COD_VEICULO,
       VEI.PLACA                        AS PLACA
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE P.COD_UNIDADE = F_COD_UNIDADE
  AND P.STATUS LIKE F_STATUS_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### FUNC_PNEU_GET_PNEU_BY_CODIGO #############################################
--######################################################################################################################
--######################################################################################################################
-- PL-2258
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_CODIGO(F_COD_PNEU BIGINT)
    RETURNS TABLE
            (
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               TEXT,
                DOT                          TEXT,
                VALOR                        REAL,
                COD_UNIDADE_ALOCADO          BIGINT,
                COD_REGIONAL_ALOCADO         BIGINT,
                PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
                COD_MARCA_PNEU               BIGINT,
                NOME_MARCA_PNEU              TEXT,
                COD_MODELO_PNEU              BIGINT,
                NOME_MODELO_PNEU             TEXT,
                QT_SULCOS_MODELO_PNEU        SMALLINT,
                COD_MARCA_BANDA              BIGINT,
                NOME_MARCA_BANDA             TEXT,
                ALTURA_SULCOS_MODELO_PNEU    REAL,
                COD_MODELO_BANDA             BIGINT,
                NOME_MODELO_BANDA            TEXT,
                QT_SULCOS_MODELO_BANDA       SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA   REAL,
                VALOR_BANDA                  REAL,
                ALTURA                       INTEGER,
                LARGURA                      INTEGER,
                ARO                          REAL,
                COD_DIMENSAO                 BIGINT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO         REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                PRESSAO_RECOMENDADA          REAL,
                PRESSAO_ATUAL                REAL,
                STATUS                       TEXT,
                VIDA_ATUAL                   INTEGER,
                VIDA_TOTAL                   INTEGER,
                POSICAO_PNEU                 INTEGER,
                POSICAO_APLICADO_CLIENTE     TEXT,
                COD_VEICULO_APLICADO         BIGINT,
                PLACA_APLICADO               TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT P.CODIGO,
       P.CODIGO_CLIENTE,
       P.DOT,
       P.VALOR,
       U.CODIGO                         AS COD_UNIDADE_ALOCADO,
       R.CODIGO                         AS COD_REGIONAL_ALOCADO,
       P.PNEU_NOVO_NUNCA_RODADO,
       MP.CODIGO                        AS COD_MARCA_PNEU,
       MP.NOME                          AS NOME_MARCA_PNEU,
       MOP.CODIGO                       AS COD_MODELO_PNEU,
       MOP.NOME                         AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                    AS QT_SULCOS_MODELO_PNEU,
       MAB.CODIGO                       AS COD_MARCA_BANDA,
       MAB.NOME                         AS NOME_MARCA_BANDA,
       MOP.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_PNEU,
       MOB.CODIGO                       AS COD_MODELO_BANDA,
       MOB.NOME                         AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                    AS QT_SULCOS_MODELO_BANDA,
       MOB.ALTURA_SULCOS                AS ALTURA_SULCOS_MODELO_BANDA,
       PVV.VALOR                        AS VALOR_BANDA,
       PD.ALTURA,
       PD.LARGURA,
       PD.ARO,
       PD.CODIGO                        AS COD_DIMENSAO,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       P.PRESSAO_RECOMENDADA,
       P.PRESSAO_ATUAL,
       P.STATUS,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       VP.POSICAO                       AS POSICAO_PNEU,
       COALESCE(PPNE.NOMENCLATURA, '-') AS POSICAO_APLICADO_CLIENTE,
       VEI.CODIGO                       AS COD_VEICULO_APLICADO,
       VEI.PLACA                        AS PLACA_APLICADO
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
         LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE P.CODIGO = F_COD_PNEU
ORDER BY P.CODIGO_CLIENTE ASC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR ########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(BIGINT[], PNEU_STATUS_TYPE);
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(F_COD_UNIDADES BIGINT[],
                                                                  F_STATUS_PNEU PNEU_STATUS_TYPE DEFAULT NULL)
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"       TEXT,
                "PNEU"                  TEXT,
                "STATUS"                TEXT,
                "VALOR DE AQUISIÇÃO"    TEXT,
                "DATA/HORA CADASTRO"    TEXT,
                "MARCA"                 TEXT,
                "MODELO"                TEXT,
                "BANDA APLICADA"        TEXT,
                "VALOR DA BANDA"        TEXT,
                "MEDIDAS"               TEXT,
                "PLACA"                 TEXT,
                "TIPO"                  TEXT,
                "POSIÇÃO"               TEXT,
                "QUANTIDADE DE SULCOS"  TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO ATUAL (PSI)"   TEXT,
                "PRESSÃO IDEAL (PSI)"   TEXT,
                "VIDA ATUAL"            TEXT,
                "DOT"                   TEXT,
                "ÚLTIMA AFERIÇÃO"       TEXT,
                "DESCRIÇÃO DESGASTE"    TEXT,
                "NÍVEL DE DESGASTE"     TEXT,
                "APARÊNCIA PNEU"        TEXT,
                "CAUSAS PROVÁVEIS"      TEXT,
                "AÇÃO"                  TEXT,
                "PRECAUÇÃO"             TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_TIMESTAMP_FORMAT TEXT := 'DD/MM/YYYY HH24:MI';
BEGIN
    RETURN QUERY
        -- Essa CTE busca o código da última aferição de cada pneu.
        -- Com o código nós conseguimos buscar depois a data/hora da aferição e o código da unidade em que ocorreu,
        -- para aplicar o TZ correto.
        WITH ULTIMAS_AFERICOES AS (
            SELECT AV.COD_PNEU   AS COD_PNEU_AFERIDO,
                   MAX(A.CODIGO) AS COD_AFERICAO
            FROM AFERICAO A
                     JOIN AFERICAO_VALORES AV
                          ON AV.COD_AFERICAO = A.CODIGO
                     JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
            WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
            GROUP BY AV.COD_PNEU
        )

        SELECT U.NOME :: TEXT                                                               AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                                                     AS COD_PNEU,
               P.STATUS :: TEXT                                                             AS STATUS,
               COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                          AS VALOR_AQUISICAO,
               FORMAT_WITH_TZ(P.DATA_HORA_CADASTRO,
                              TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                              F_TIMESTAMP_FORMAT,
                              '-')                                                          AS DATA_HORA_CADASTRO,
               MAP.NOME :: TEXT                                                             AS NOME_MARCA_PNEU,
               MP.NOME :: TEXT                                                              AS NOME_MODELO_PNEU,
               F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME) AS BANDA_APLICADA,
               COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                        AS VALOR_BANDA,
               FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO)                     AS MEDIDAS,
               COALESCE(VP.PLACA, '-') :: TEXT                                              AS PLACA,
               COALESCE(VT.NOME, '-') :: TEXT                                               AS TIPO_VEICULO,
               COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                     AS POSICAO_PNEU,
               COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                               AS QTD_SULCOS,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                            P.ALTURA_SULCO_INTERNO))                        AS MENOR_SULCO,
               COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                AS PRESSAO_ATUAL,
               P.PRESSAO_RECOMENDADA :: TEXT                                                AS PRESSAO_RECOMENDADA,
               PVN.NOME :: TEXT                                                             AS VIDA_ATUAL,
               COALESCE(P.DOT, '-') :: TEXT                                                 AS DOT,
               -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
               -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
               CASE
                   WHEN UA.COD_AFERICAO IS NULL
                       THEN 'Nunca Aferido'
                   ELSE
                       FORMAT_WITH_TZ((SELECT A.DATA_HORA
                                       FROM AFERICAO A
                                       WHERE A.CODIGO = UA.COD_AFERICAO),
                                      TZ_UNIDADE((SELECT A.COD_UNIDADE
                                                  FROM AFERICAO A
                                                  WHERE A.CODIGO = UA.COD_AFERICAO)),
                                      F_TIMESTAMP_FORMAT)
                   END                                                                      AS ULTIMA_AFERICAO,
               PTDI.DESCRICAO                                                               AS DESCRICAO_DESGASTE,
               -- Por enquanto, deixamos hardcoded os ranges de cada nível de desgaste.
               CASE
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'BAIXO'
                       THEN 'BAIXO (0.1 mm até 0.9 mm)'
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'MODERADO'
                       THEN 'MODERADO (1.0 mm até 2.0 mm)'
                   WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'ACENTUADO'
                       THEN 'ACENTUADO (2.1 mm e acima)'
                   END                                                                      AS NIVEL_DESGASTE,
               PTDI.APARENCIA_PNEU                                                          AS APARENCIA_PNEU,
               PTDI.CAUSAS_PROVAVEIS                                                        AS CAUSAS_PROVAVEIS,
               PTDI.ACAO                                                                    AS ACAO,
               PTDI.PRECAUCAO                                                               AS PRECAUCAO
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
                 JOIN FUNC_PNEU_VERIFICA_DESGASTE_IRREGULAR(P.CODIGO,
                                                            P.ALTURA_SULCO_EXTERNO,
                                                            P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                                            P.ALTURA_SULCO_INTERNO) VERIF_DESGASTE
                      ON VERIF_DESGASTE.COD_PNEU = P.CODIGO
                 LEFT JOIN PNEU_TIPO_DESGASTE_IRREGULAR PTDI
                           ON PTDI.TIPO_DESGASTE_IRREGULAR = VERIF_DESGASTE.TIPO_DESGASTE_IRREGULAR
                 LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
                 LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
                 LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                    AND PPNE.COD_DIAGRAMA = VD.CODIGO
                    AND PPNE.POSICAO_PROLOG = VP.POSICAO
                 LEFT JOIN ULTIMAS_AFERICOES UA
                           ON UA.COD_PNEU_AFERIDO = P.CODIGO
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND F_IF(F_STATUS_PNEU IS NULL, TRUE, F_STATUS_PNEU = P.STATUS :: PNEU_STATUS_TYPE)
          AND VERIF_DESGASTE.TEM_DESGASTE_IRREGULAR
        ORDER BY VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR DESC, U.NOME, P.CODIGO_CLIENTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### FUNC_PNEU_RELATORIO_STATUS_ATUAL_PNEUS ########################################
--######################################################################################################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_PNEU_RELATORIO_STATUS_ATUAL_PNEUS(BIGINT[]);
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_STATUS_ATUAL_PNEUS(
    F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"    TEXT,
                "PNEU"               TEXT,
                "STATUS ATUAL"       TEXT,
                "PLACA APLICADO"     TEXT,
                "POSIÇÃO APLICADO"   TEXT,
                "RECAPADORA ALOCADO" TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_ANALISE TEXT := 'ANALISE';
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                           AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                 AS COD_PNEU,
               P.STATUS :: TEXT                         AS STATUS_ATUAL,
               COALESCE(VP.PLACA :: TEXT, '-')          AS PLACA_APLICADO,
               COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS POSICAO_APLICADO,
               COALESCE(
                       CASE
                           WHEN P.STATUS = F_STATUS_ANALISE
                               THEN (SELECT R.NOME AS NOME_RECAPADORA
                                     FROM MOVIMENTACAO M
                                              JOIN MOVIMENTACAO_DESTINO MD
                                                   ON M.CODIGO = MD.COD_MOVIMENTACAO
                                              JOIN RECAPADORA R ON MD.COD_RECAPADORA_DESTINO = R.CODIGO
                                     WHERE M.COD_PNEU = P.CODIGO
                                     ORDER BY M.CODIGO DESC
                                     LIMIT 1)
                           END,
                       '-')                             AS RECAPADORA_ALOCADO
        FROM PNEU P
                 JOIN UNIDADE U
                      ON P.COD_UNIDADE = U.CODIGO
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY U.CODIGO ASC, P.CODIGO_CLIENTE ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--###################################### FUNC_PNEU_RELATORIO_VALIDADE_DOT ##############################################
--######################################################################################################################
-- PL-2258
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(F_COD_UNIDADES BIGINT[],
                                                            F_DATA_ATUAL TIMESTAMP WITHOUT TIME ZONE)
    RETURNS TABLE
            (
                "UNIDADE"         TEXT,
                "COD PNEU"        TEXT,
                "PLACA"           TEXT,
                "POSIÇÃO"         TEXT,
                "DOT CADASTRADO"  TEXT,
                "DOT VÁLIDO"      TEXT,
                "TEMPO DE USO"    TEXT,
                "TEMPO RESTANTE"  TEXT,
                "DATA VENCIMENTO" TEXT,
                "VENCIDO"         TEXT,
                "DATA GERAÇÃO"    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATE_FORMAT        TEXT := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
    DIA_MES_ANO_FORMAT TEXT := 'DD/MM/YYYY';
    DATA_HORA_FORMAT   TEXT := 'DD/MM/YYYY HH24:MI';
    DATE_CONVERTER     TEXT := 'YYYYWW';
    PREFIXO_ANO        TEXT := SUBSTRING(F_DATA_ATUAL::TEXT, 1, 2);
BEGIN
    RETURN QUERY
        WITH INFORMACOES_PNEU AS (
            SELECT P.CODIGO_CLIENTE                               AS COD_PNEU,
                   P.DOT                                          AS DOT_CADASTRADO,
                   -- Remove letras, characteres especiais e espaços do dot.
                   -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
                   TRIM(REGEXP_REPLACE(P.DOT, '[^0-9]', '', 'g')) AS DOT_LIMPO,
                   P.COD_UNIDADE                                  AS COD_UNIDADE,
                   U.NOME                                         AS UNIDADE,
                   VP.PLACA                                       AS PLACA_APLICADO,
                   PPNE.NOMENCLATURA                              AS POSICAO_PNEU
            FROM PNEU P
                     JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
                     JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA
                     LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
                     LEFT JOIN VEICULO V ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
                     LEFT JOIN VEICULO_TIPO VT
                               ON V.COD_TIPO = VT.CODIGO
                     LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                     LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                AND PPNE.COD_DIAGRAMA = VD.CODIGO
                AND PPNE.POSICAO_PROLOG = VP.POSICAO
            WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ),

             DATA_DOT AS (
                 SELECT IP.COD_PNEU,
                        -- Transforma o DOT_FORMATADO em data
                        CASE
                            WHEN (CHAR_LENGTH(IP.DOT_LIMPO) = 4)
                                THEN
                                TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(IP.DOT_LIMPO, 3, 4)),
                                               (SUBSTRING(IP.DOT_LIMPO, 1, 2))),
                                        DATE_CONVERTER)
                            ELSE NULL END AS DOT_EM_DATA
                 FROM INFORMACOES_PNEU IP
             ),

             VENCIMENTO_DOT AS (
                 SELECT DD.COD_PNEU,
                        -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
                        -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
                        -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
                        -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
                        CASE
                            WHEN DD.DOT_EM_DATA <= (F_DATA_ATUAL::DATE)
                                THEN DD.DOT_EM_DATA + INTERVAL '5 DAYS 5 YEARS'
                            ELSE NULL END AS DATA_VENCIMENTO
                 FROM DATA_DOT DD
             ),

             CALCULOS AS (
                 SELECT DD.COD_PNEU,
                        -- Verifica se o dot é válido
                        -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
                        -- podemos considerar inválidos os que possuem vencimento = null.
                        CASE WHEN VD.DATA_VENCIMENTO IS NULL THEN 'INVÁLIDO' ELSE 'VÁLIDO' END        AS DOT_VALIDO,
                        -- Cálculo tempo de uso
                        CASE
                            WHEN VD.DATA_VENCIMENTO IS NULL
                                THEN NULL
                            ELSE
                                TO_CHAR(AGE((F_DATA_ATUAL :: DATE), DD.DOT_EM_DATA), DATE_FORMAT) END AS TEMPO_DE_USO,
                        -- Cálculo dias restantes
                        TO_CHAR(AGE(VD.DATA_VENCIMENTO, F_DATA_ATUAL), DATE_FORMAT)                   AS TEMPO_RESTANTE,
                        -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
                        F_IF(((VD.DATA_VENCIMENTO::DATE) - (F_DATA_ATUAL::DATE)) < 0, TRUE, FALSE)    AS VENCIDO
                 FROM DATA_DOT DD
                          JOIN VENCIMENTO_DOT VD ON DD.COD_PNEU = VD.COD_PNEU
             )
        SELECT IP.UNIDADE::TEXT,
               IP.COD_PNEU::TEXT,
               COALESCE(IP.PLACA_APLICADO::TEXT, '-'),
               COALESCE(IP.POSICAO_PNEU::TEXT, '-'),
               COALESCE(IP.DOT_CADASTRADO::TEXT, '-'),
               CA.DOT_VALIDO,
               COALESCE(CA.TEMPO_DE_USO, '-'),
               COALESCE(CA.TEMPO_RESTANTE, '-'),
               COALESCE(TO_CHAR(VD.DATA_VENCIMENTO, DIA_MES_ANO_FORMAT)::TEXT, '-'),
               F_IF(CA.VENCIDO, 'SIM' :: TEXT, 'NÃO' :: TEXT),
               TO_CHAR(F_DATA_ATUAL, DATA_HORA_FORMAT)::TEXT
        FROM INFORMACOES_PNEU IP
                 JOIN VENCIMENTO_DOT VD ON IP.COD_PNEU = VD.COD_PNEU
                 JOIN CALCULOS CA ON CA.COD_PNEU = VD.COD_PNEU AND CA.COD_PNEU = IP.COD_PNEU
        ORDER BY VD.DATA_VENCIMENTO ASC, IP.PLACA_APLICADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--###################################### FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU #####################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(TEXT[]);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT[])
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"               TEXT,
                "PNEU"                          TEXT,
                "STATUS ATUAL"                  TEXT,
                "MARCA PNEU"                    TEXT,
                "MODELO PNEU"                   TEXT,
                "MEDIDAS"                       TEXT,
                "PLACA APLICADO"                TEXT,
                "MARCA VEÍCULO"                 TEXT,
                "MODELO VEÍCULO"                TEXT,
                "TIPO VEÍCULO"                  TEXT,
                "POSIÇÃO APLICADO"              TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "VIDA ATUAL"                    TEXT,
                "DOT"                           TEXT,
                "ÚLTIMA AFERIÇÃO"               TEXT,
                "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Essa CTE busca o código da última aferição de cada pneu.
    -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
    RETURN QUERY
        WITH CODS_AFERICOES AS (
            SELECT AV.COD_PNEU   AS COD_PNEU_AFERIDO,
                   MAX(A.CODIGO) AS COD_AFERICAO
            FROM AFERICAO A
                     JOIN AFERICAO_VALORES AV
                          ON AV.COD_AFERICAO = A.CODIGO
                     JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
            WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
            GROUP BY AV.COD_PNEU
        ),

             ULTIMAS_AFERICOES AS (
                 SELECT CA.COD_PNEU_AFERIDO    AS COD_PNEU_AFERIDO,
                        A.DATA_HORA            AS DATA_HORA_AFERICAO,
                        A.COD_UNIDADE          AS COD_UNIDADE_AFERICAO,
                        A.TIPO_PROCESSO_COLETA AS TIPO_PROCESSO_COLETA
                 FROM CODS_AFERICOES CA
                          JOIN AFERICAO A ON A.CODIGO = CA.COD_AFERICAO)

        SELECT U.NOME :: TEXT                                                   AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                                         AS COD_PNEU,
               P.STATUS :: TEXT                                                 AS STATUS_ATUAL,
               MAP.NOME :: TEXT                                                 AS NOME_MARCA,
               MP.NOME :: TEXT                                                  AS NOME_MODELO,
               ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
                DP.ARO)                                                         AS MEDIDAS,
               COALESCE(VP.PLACA, '-') :: TEXT                                  AS PLACA,
               COALESCE(MARV.NOME, '-') :: TEXT                                 AS MARCA_VEICULO,
               COALESCE(MODV.NOME, '-') :: TEXT                                 AS MODELO_VEICULO,
               COALESCE(VT.NOME, '-') :: TEXT                                   AS TIPO_VEICULO,
               COALESCE(PPNE.NOMENCLATURA:: TEXT, '-')                          AS POSICAO_PNEU,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                   AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)           AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)           AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                   AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                            P.ALTURA_SULCO_INTERNO))            AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',') AS PRESSAO_ATUAL,
               P.VIDA_ATUAL :: TEXT                                             AS VIDA_ATUAL,
               COALESCE(P.DOT, '-') :: TEXT                                     AS DOT,
               COALESCE(TO_CHAR(UA.DATA_HORA_AFERICAO AT TIME ZONE
                                tz_unidade(UA.COD_UNIDADE_AFERICAO),
                                'DD/MM/YYYY HH24:MI'),
                        'Nunca Aferido')                                        AS ULTIMA_AFERICAO,
               CASE
                   WHEN UA.TIPO_PROCESSO_COLETA IS NULL
                       THEN 'Nunca Aferido'
                   WHEN UA.TIPO_PROCESSO_COLETA = 'PLACA'
                       THEN 'Aferido em uma placa'
                   ELSE 'Aferido Avulso (em estoque)' END                       AS TIPO_PROCESSO_ULTIMA_AFERICAO
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
                 LEFT JOIN MODELO_VEICULO MODV
                           ON MODV.CODIGO = V.COD_MODELO
                 LEFT JOIN MARCA_VEICULO MARV
                           ON MARV.CODIGO = MODV.COD_MARCA
                 LEFT JOIN ULTIMAS_AFERICOES UA
                           ON UA.COD_PNEU_AFERIDO = P.CODIGO
        WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
        ORDER BY U.NOME, P.CODIGO_CLIENTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--###################################### FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS #####################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(TEXT[], DATE, DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADE TEXT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
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
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                        AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                          AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                               AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                    AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT AS DIAS_EM_ABERTO,
       C.NOME                                                                                   AS NOME_COLABORADOR,
       A.PLACA_VEICULO                                                                          AS PLACA_VEICULO,
       P.CODIGO_CLIENTE                                                                         AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                 AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                         AS MEDIDAS,
       A.CODIGO :: TEXT                                                                         AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                          AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                  AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                  AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                          AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                   AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                  AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                             AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                 AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                 AS TOTAL_RECAPAGENS
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
  AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################### FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS #####################################
--######################################################################################################################
-- PL-2258
--CREATE INDEX IDX_PNEU_COD_UNIDADE ON PNEU_DATA (COD_UNIDADE);
DROP FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(TEXT[], TEXT);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(F_COD_UNIDADE TEXT[],
                                                                  F_STATUS_PNEU TEXT)
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"       TEXT,
                "PNEU"                  TEXT,
                "STATUS"                TEXT,
                "VALOR DE AQUISIÇÃO"    TEXT,
                "DATA/HORA CADASTRO"    TEXT,
                "MARCA"                 TEXT,
                "MODELO"                TEXT,
                "BANDA APLICADA"        TEXT,
                "VALOR DA BANDA"        TEXT,
                "MEDIDAS"               TEXT,
                "PLACA"                 TEXT,
                "TIPO"                  TEXT,
                "POSIÇÃO"               TEXT,
                "QUANTIDADE DE SULCOS"  TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO ATUAL (PSI)"   TEXT,
                "PRESSÃO IDEAL (PSI)"   TEXT,
                "VIDA ATUAL"            TEXT,
                "DOT"                   TEXT,
                "ÚLTIMA AFERIÇÃO"       TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                   AS UNIDADE_ALOCADO,
       P.CODIGO_CLIENTE                                                         AS COD_PNEU,
       P.STATUS                                                                 AS STATUS,
       COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                      AS VALOR_AQUISICAO,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                            AS DATA_HORA_CADASTRO,
       MAP.NOME                                                                 AS NOME_MARCA_PNEU,
       MP.NOME                                                                  AS NOME_MODELO_PNEU,
       CASE
           WHEN MARB.CODIGO IS NULL
               THEN 'Nunca Recapado'
           ELSE MARB.NOME || ' - ' || MODB.NOME
           END                                                                  AS BANDA_APLICADA,
       COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                    AS VALOR_BANDA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO) AS MEDIDAS,
       COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                   AS PLACA,
       COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                         AS TIPO_VEICULO,
       COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-') :: TEXT                 AS POSICAO_PNEU,
       COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                           AS QTD_SULCOS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                           AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                   AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                   AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                           AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                    AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                            AS PRESSAO_ATUAL,
       P.PRESSAO_RECOMENDADA :: TEXT                                            AS PRESSAO_RECOMENDADA,
       PVN.NOME :: TEXT                                                         AS VIDA_ATUAL,
       COALESCE(P.DOT, '-')                                                     AS DOT,
       COALESCE(
               TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_ULTIMA_AFERICAO),
                       'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                  AS ULTIMA_AFERICAO
FROM PNEU P
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN (SELECT PPNE.NOMENCLATURA AS POSICAO_PNEU,
                           VP.COD_PNEU       AS CODIGO_PNEU,
                           VP.PLACA          AS PLACA_VEICULO_PNEU,
                           VP.COD_UNIDADE    AS COD_UNIDADE_PNEU,
                           VT.NOME           AS VEICULO_TIPO
                    FROM VEICULO V
                             JOIN VEICULO_PNEU VP
                                  ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
                             JOIN VEICULO_TIPO VT
                                  ON V.COD_TIPO = VT.CODIGO
                             JOIN EMPRESA E ON VT.COD_EMPRESA = E.CODIGO
                        -- LEFT JOIN porque unidade pode não ter nomenclatura.
                             LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                             LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                                       ON PPNE.COD_EMPRESA = E.CODIGO
                                           AND PPNE.COD_DIAGRAMA = VD.CODIGO
                                           AND VP.POSICAO = PPNE.POSICAO_PROLOG
                    WHERE V.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
                    ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
                   ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
         LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F
                   ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND CASE
          WHEN F_STATUS_PNEU IS NULL
              THEN TRUE
          ELSE P.STATUS = F_STATUS_PNEU
    END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--############################################## FUNC_RELATORIO_PREVISAO_TROCA #########################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION FUNC_RELATORIO_PREVISAO_TROCA(DATE, DATE, TEXT[], CHARACTER VARYING);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PREVISAO_TROCA(F_DATA_INICIAL DATE,
                                                         F_DATA_FINAL DATE,
                                                         F_COD_UNIDADE TEXT[],
                                                         F_STATUS_PNEU CHARACTER VARYING)
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"         TEXT,
                "COD PNEU"                TEXT,
                "STATUS"                  TEXT,
                "VIDA ATUAL"              INTEGER,
                "MARCA"                   TEXT,
                "MODELO"                  TEXT,
                "MEDIDAS"                 TEXT,
                "PLACA APLICADO"          TEXT,
                "POSIÇÃO APLICADO"        TEXT,
                "QTD DE AFERIÇÕES"        BIGINT,
                "DATA 1ª AFERIÇÃO"        TEXT,
                "DATA ÚLTIMA AFERIÇÃO"    TEXT,
                "DIAS ATIVO"              INTEGER,
                "MÉDIA KM POR DIA"        NUMERIC,
                "MAIOR MEDIÇÃO VIDA"      NUMERIC,
                "MENOR SULCO ATUAL"       NUMERIC,
                "MILÍMETROS GASTOS"       NUMERIC,
                "KMS POR MILÍMETRO"       NUMERIC,
                "VALOR VIDA"              REAL,
                "VALOR ACUMULADO"         REAL,
                "VALOR POR KM VIDA ATUAL" NUMERIC,
                "VALOR POR KM ACUMULADO"  NUMERIC,
                "KMS A PERCORRER"         NUMERIC,
                "DIAS RESTANTES"          DOUBLE PRECISION,
                "PREVISÃO DE TROCA"       TEXT,
                "DESTINO"                 TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT VAP."UNIDADE ALOCADO",
       VAP."COD PNEU CLIENTE",
       VAP."STATUS PNEU",
       VAP."VIDA ATUAL",
       VAP."MARCA",
       VAP."MODELO",
       VAP."MEDIDAS",
       VP.PLACA                                 AS PLACA_APLICADO,
       COALESCE(PPNE.NOMENCLATURA, '-') :: TEXT AS POSICAO_APLICADO,
       VAP."QTD DE AFERIÇÕES",
       VAP."DTA 1a AFERIÇÃO",
       VAP."DTA ÚLTIMA AFERIÇÃO",
       VAP."DIAS ATIVO",
       VAP."MÉDIA KM POR DIA",
       VAP."MAIOR MEDIÇÃO VIDA",
       VAP."MENOR SULCO ATUAL",
       VAP."MILIMETROS GASTOS",
       VAP."KMS POR MILIMETRO",
       VAP.VALOR_VIDA_ATUAL,
       VAP.VALOR_ACUMULADO,
       VAP."VALOR POR KM",
       VAP."VALOR POR KM ACUMULADO",
       VAP."KMS A PERCORRER",
       VAP."DIAS RESTANTES",
       TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY'),
       VAP."DESTINO"
FROM VIEW_PNEU_ANALISE_VIDA_ATUAL AS VAP
         JOIN VEICULO_PNEU VP
              ON VAP."COD PNEU" = VP.COD_PNEU
         JOIN VEICULO V
              ON VP.PLACA = V.PLACA
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         JOIN EMPRESA E ON VT.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND VP.POSICAO = PPNE.POSICAO_PROLOG
WHERE VAP.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND VAP."PREVISÃO DE TROCA" <= F_DATA_FINAL
  AND VAP."STATUS PNEU" LIKE F_STATUS_PNEU
ORDER BY VAP."UNIDADE ALOCADO";
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--################################### FUNC_PNEUS_COPIA_NOMENCLATURAS_ENTRE_UNIDADES ####################################
--######################################################################################################################
-- PL-2258
--function não terá mais aplicabilidade, uma vez que as nomenclatura não serão mais por unidade.
DROP FUNCTION FUNC_PNEUS_COPIA_NOMENCLATURAS_ENTRE_UNIDADES(BIGINT, BIGINT[]);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--############################################ IS_PLACA_POSICAO_PNEU_VALIDA ############################################
--######################################################################################################################
-- PL-2258
DROP FUNCTION IS_PLACA_POSICAO_PNEU_VALIDA(BIGINT, INTEGER, BOOLEAN);
CREATE OR REPLACE FUNCTION IS_PLACA_POSICAO_PNEU_VALIDA(
  F_COD_VEICULO BIGINT,
  F_POSICAO_PNEU INTEGER,
  F_IS_PNEU_ESTEPE BOOLEAN)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS
$$
DECLARE
  IS_POSICAO_VALIDA BOOLEAN;
BEGIN
  IF (F_IS_PNEU_ESTEPE)
  THEN
    SELECT (F_POSICAO_PNEU >= 900 AND F_POSICAO_PNEU <= 908) INTO IS_POSICAO_VALIDA;
  ELSE
    SELECT EXISTS(SELECT
                    VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.POSICAO_PROLOG = F_POSICAO_PNEU
                        AND VDPP.COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                                                 FROM VEICULO_TIPO VT
                                                 WHERE VT.CODIGO = (SELECT V.COD_TIPO
                                                                    FROM VEICULO V
                                                                    WHERE V.CODIGO = F_COD_VEICULO)))
    INTO IS_POSICAO_VALIDA;
  END IF;
  RETURN IS_POSICAO_VALIDA;
END;
$$;

--######################################################################################################################
--############################################ FUNC_PNEU_GET_PNEU_BY_PLACA ############################################
--######################################################################################################################
-- PL-2259
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_PLACA(F_PLACA VARCHAR(7))
RETURNS TABLE (
                NOME_MARCA_PNEU VARCHAR(255),
                COD_MARCA_PNEU BIGINT,
                CODIGO BIGINT,
                CODIGO_CLIENTE VARCHAR(255),
                COD_UNIDADE_ALOCADO BIGINT,
                COD_REGIONAL_ALOCADO BIGINT,
                PRESSAO_ATUAL REAL,
                VIDA_ATUAL INTEGER,
                VIDA_TOTAL INTEGER,
                PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                NOME_MODELO_PNEU VARCHAR(255),
                COD_MODELO_PNEU BIGINT,
                QT_SULCOS_MODELO_PNEU SMALLINT,
                ALTURA_SULCOS_MODELO_PNEU REAL,
                ALTURA INTEGER,
                LARGURA INTEGER,
                ARO REAL,
                COD_DIMENSAO BIGINT,
                PRESSAO_RECOMENDADA REAL,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO REAL,
                ALTURA_SULCO_EXTERNO REAL,
                STATUS VARCHAR(255),
                DOT VARCHAR(20),
                VALOR REAL,
                COD_MODELO_BANDA BIGINT,
                NOME_MODELO_BANDA VARCHAR(255),
                QT_SULCOS_MODELO_BANDA SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA REAL,
                COD_MARCA_BANDA BIGINT,
                NOME_MARCA_BANDA VARCHAR(255),
                VALOR_BANDA REAL,
                POSICAO_PNEU INTEGER,
                POSICAO_APLICADO_CLIENTE VARCHAR(255),
                COD_VEICULO_APLICADO BIGINT,
                PLACA_APLICADO VARCHAR(7)
              )
LANGUAGE SQL
AS $$
SELECT
              MP.NOME                                    AS NOME_MARCA_PNEU,
              MP.CODIGO                                  AS COD_MARCA_PNEU,
              P.CODIGO,
              P.CODIGO_CLIENTE,
              U.CODIGO                                   AS COD_UNIDADE_ALOCADO,
              R.CODIGO                                   AS COD_REGIONAL_ALOCADO,
              P.PRESSAO_ATUAL,
              P.VIDA_ATUAL,
              P.VIDA_TOTAL,
              P.PNEU_NOVO_NUNCA_RODADO,
              MOP.NOME                                   AS NOME_MODELO_PNEU,
              MOP.CODIGO                                 AS COD_MODELO_PNEU,
              MOP.QT_SULCOS                              AS QT_SULCOS_MODELO_PNEU,
              MOP.ALTURA_SULCOS                          AS ALTURA_SULCOS_MODELO_PNEU,
              PD.ALTURA,
              PD.LARGURA,
              PD.ARO,
              PD.CODIGO                                  AS COD_DIMENSAO,
              P.PRESSAO_RECOMENDADA,
              P.ALTURA_SULCO_CENTRAL_INTERNO,
              P.ALTURA_SULCO_CENTRAL_EXTERNO,
              P.ALTURA_SULCO_INTERNO,
              P.ALTURA_SULCO_EXTERNO,
              P.STATUS,
              P.DOT,
              P.VALOR,
              MOB.CODIGO                                  AS COD_MODELO_BANDA,
              MOB.NOME                                    AS NOME_MODELO_BANDA,
              MOB.QT_SULCOS                               AS QT_SULCOS_MODELO_BANDA,
              MOB.ALTURA_SULCOS                           AS ALTURA_SULCOS_MODELO_BANDA,
              MAB.CODIGO                                  AS COD_MARCA_BANDA,
              MAB.NOME                                    AS NOME_MARCA_BANDA,
              PVV.VALOR                                   AS VALOR_BANDA,
              PO.POSICAO_PROLOG                           AS POSICAO_PNEU,
              COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')    AS POSICAO_APLICADO_CLIENTE,
              VEI.codigo                                  AS COD_VEICULO_APLICADO,
              VEI.placa                                   AS PLACA_APLICADO
            FROM PNEU P
            JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
            JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
            JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
            JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
            JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
            LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
            LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
            LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = P.COD_EMPRESA
            LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
            LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG
            LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
            LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
            LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
            LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON
                PPNE.COD_EMPRESA = P.COD_EMPRESA AND
                PPNE.COD_DIAGRAMA = VD.CODIGO AND
                PPNE.POSICAO_PROLOG = VP.POSICAO
    WHERE VP.PLACA = F_PLACA;
$$;
--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
-- alter table pneu_data drop constraint fk_pneu_modelo;
--
-- alter table modelo_pneu add constraint unique_modelo_pneu_empresa unique (cod_empresa, codigo);
--
-- alter table pneu_data add constraint fk_pneu_modelo
--     foreign key (cod_empresa, cod_modelo) references modelo_pneu (cod_empresa, codigo);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Apenas para histórico, foi rodado em prod em 2019-09-23.
-- alter table veiculo_data alter column km set not null;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### CRIA FUNC PARA CONFERIR DADOS DA PLANILHA DE IMPORT DE VEICULO ###########################
--######################################################################################################################
--######################################################################################################################
CREATE SCHEMA IMPLANTACAO;

CREATE TABLE IMPLANTACAO.TOKEN_IMPLANTACAO
(
    TOKEN     TEXT NOT NULL,
    DESCRICAO TEXT NOT NULL
);

INSERT INTO IMPLANTACAO.TOKEN_IMPLANTACAO (TOKEN, DESCRICAO)
VALUES ('VAN9tX7Be7egIAPliXXrKCDjDSMQ4K7aUUjP3rahyGPIRo4ANc2ipGS3ipTnAbOf795A1gs1xIJP8OZV',
        'Usado no import de planilha de veículo para conferência');
--Para a function funcionar:

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Retorna de 0 a 1 a similariedade entre dois TEXTs. Aonde 0 significa que os textos são diferentes e 1 que são
-- identicos.
--
-- Précondições:
-- 1) A function remove os caracteres especiais dos atributos antes de realizar a similaridade. Após, realiza o
-- procedimento e retorna o valor da similaridade encontrada.
-- 2) Function: REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(TEXT) criada.
--
-- Histórico:
-- 2019-08-13 -> Function criada (thaisksf - PL-2186).
CREATE OR REPLACE FUNCTION FUNC_GERA_SIMILARIDADE(F_TEXTO1 TEXT,
                                                  F_TEXTO2 TEXT)
    RETURNS REAL
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN SIMILARITY(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO1),
                      REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO2));
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Remove caracteres especiais, letras com acentos e espaços do TEXT passado para a function.
--
-- Histórico:
-- 2019-08-13 -> Function criada (thaisksf - PL-2186).
CREATE OR REPLACE FUNCTION REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN REGEXP_REPLACE(UNACCENT(F_TEXTO), '[^a-zA-Z0-9]+', '', 'g');
END;
$$;

--######################################################################################################################
--######################################################################################################################
--########################### CRIA FUNC PARA CONFERIR DADOS DA PLANILHA DE IMPORT DE VEICULO ###########################
--######################################################################################################################
--######################################################################################################################
CREATE EXTENSION IF NOT EXISTS PG_TRGM;
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Recebe as informações da planilha de import de veículos em formato Json. Após, as informações são colocadas em uma
-- tabela temporária, a fim de facilitar a manipulação.
--
-- Então, é realizado as seguintes análises:
--  - placa: aponta duplicidade;
--           remove caracteres e espaços excedentes;
--           verifica quantidade de caracteres acima de 7;
--           procura se já possui cadastro no banco:
--              -> se posuir, qual o status do veículo.
--
--  - marca: remove duplicidade;
--           procura por similaridade no banco:
--              -> se encontrar, retorna o código.
--
--  - modelo: remove duplicidade com base no cod_marca + modelo;
--            procura por similaridade no banco com base no cod_marca + modelo:
--              -> se encontrar, retorna o código.
--
--  - diagrama: remove duplicidade;
--              procura por similaridade no banco:
--                -> se encontrar, retorna o código.
--
--   - tipo: remove duplicidade com base no cod_diagrama + tipo;
--           procura por similaridade no banco com base no cod_diagrama+tipo:
--              -> se encontrar, retorna o código.
--
-- Précondições:
-- Planilha de import de veículo preenchida.
-- Function: FUNC_GARANTE_SIMILARIDADE(TEXT, TEXT) criada.
-- Function: REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(TEXT) criada.
--
-- Histórico:
-- 2019-08-13 -> Function criada (thaisksf - PL-2186).
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_CONFERE_PLANILHA_IMPORTACAO(F_COD_UNIDADE BIGINT,
                                                                                F_JSON_VEICULOS JSONB)
    RETURNS TABLE
            (
                "PLACA NA PLANILHA"           TEXT,
                "PLACA DUPLICADA NA PLANILHA" TEXT,
                "PLACA PLANILHA FORMATADA"    TEXT,
                "POSSUI 7 DIGITOS?"           TEXT,
                "PLACA_NA_UNIDADE/EMPRESA"    TEXT,
                "STATUS_ATIVO_BANCO"          BOOLEAN,
                "KM VEICULO"                  BIGINT,
                "MARCA PLANILHA"              TEXT,
                "MARCA BANCO"                 TEXT,
                "COD_MARCA"                   TEXT,
                "MODELO PLANILHA"             TEXT,
                "MODELO BANCO"                TEXT,
                "COD_MODELO"                  TEXT,
                "DIAGRAMA PLANILHA"           TEXT,
                "DIAGRAMA BANCO"              TEXT,
                "COD_DIAGRAMA"                TEXT,
                "TIPO PLANILHA"               TEXT,
                "TIPO BANCO"                  TEXT,
                "COD_TIPO"                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA CONSTANT            BIGINT := (SELECT U.COD_EMPRESA
                                                 FROM UNIDADE U
                                                 WHERE U.CODIGO = F_COD_UNIDADE);
    F_SIMILARIDADE CONSTANT           REAL   = 0.4;
    F_SIMILIARIDADE_DIAGRAMA CONSTANT REAL   = 0.5;
    F_SEM_SIMILARIDADE CONSTANT       REAL   = 0.0;
    NAO_ENCONTRADO CONSTANT           TEXT   := '-';
BEGIN
    CREATE TEMP TABLE IF NOT EXISTS TABLE_JSON
    (
        CODIGO             BIGSERIAL,
        PLACA              TEXT,
        PLACA_FORMATADA    TEXT,
        KM                 BIGINT,
        MARCA              TEXT,
        MARCA_FORMATADA    TEXT,
        MODELO             TEXT,
        MODELO_FORMATADO   TEXT,
        TIPO               TEXT,
        TIPO_FORMATADO     TEXT,
        DIAGRAMA           TEXT,
        DIAGRAMA_FORMATADO TEXT
    ) ON COMMIT DELETE ROWS;
    INSERT
    INTO TABLE_JSON (PLACA,
                     PLACA_FORMATADA,
                     KM,
                     MARCA,
                     MARCA_FORMATADA,
                     MODELO,
                     MODELO_FORMATADO,
                     TIPO,
                     TIPO_FORMATADO,
                     DIAGRAMA,
                     DIAGRAMA_FORMATADO)
    SELECT (SRC ->> 'placa') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> 'placa')) :: TEXT,
           (SRC ->> 'km') :: BIGINT,
           (SRC ->> 'marca') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('marca')) :: TEXT,
           (SRC ->> 'modelo') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('modelo')) :: TEXT,
           (SRC ->> 'tipo') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('tipo')) :: TEXT,
           (SRC ->> 'diagrama') :: TEXT,
           REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> ('diagrama')) :: TEXT
    FROM JSONB_ARRAY_ELEMENTS(F_JSON_VEICULOS) SRC;

    RETURN QUERY
        -- PROCURA PLACAS DUPLICADAS.
        WITH PROCURA_PLACAS_DUPLICADAS AS (
            SELECT TJ.PLACA_FORMATADA,
                   COUNT(TJ.PLACA_FORMATADA) AS PLACAS_DUPLICADAS
            FROM TABLE_JSON TJ
            GROUP BY TJ.PLACA_FORMATADA
        ),

             -- VERIFICAÇÕES PLACA.
             VERIFICACOES_PLACA AS (
                 SELECT TJ.CODIGO,
                        TJ.PLACA AS PLACA_PLANILHA,
                        CASE
                            WHEN (PPD.PLACAS_DUPLICADAS > 1)
                                THEN 'SIM'
                            ELSE NAO_ENCONTRADO
                            END  AS PLACAS_DUPLICADAS,
                        TJ.PLACA_FORMATADA,
                        V.PLACA  AS PLACA_VEICULO,
                        V.STATUS_ATIVO,
                        CASE
                            WHEN (V.PLACA IS NULL)
                                THEN NAO_ENCONTRADO
                            -- Vai retornar assim: ("Unidade Teste", "Zalf Sistemas").
                            ELSE (U.NOME, E.NOME) :: TEXT
                            END  AS PLACA_NA_UNIDADE_EMPRESA,
                        CASE
                            WHEN LENGTH(TJ.PLACA_FORMATADA) <> 7
                                THEN 'NÃO POSSUI'
                            ELSE 'SIM'
                            END  AS PLACA_7_DIGITOS,
                        TJ.KM
                 FROM TABLE_JSON TJ
                          JOIN PROCURA_PLACAS_DUPLICADAS PPD
                               ON PPD.PLACA_FORMATADA = TJ.PLACA_FORMATADA
                          LEFT JOIN VEICULO V ON TJ.PLACA_FORMATADA ILIKE V.PLACA
                          LEFT JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
             ),

             -- PROCURA SIMILARIDADE DE MARCA DE VEÍCULO NO BANCO.
             SIMILARIDADE_MARCAS_VEICULOS AS (
                 SELECT DISTINCT ON (TJ.MARCA_FORMATADA) TJ.MARCA                                                  AS MARCA_PLANILHA,
                                                         TJ.MARCA_FORMATADA                                        AS MARCA_PLANILHA_FORMATADA,
                                                         MAV.NOME                                                  AS MARCA_VEICULO_BANCO,
                                                         MAV.CODIGO                                                AS COD_MARCA_BANCO,
                                                         MAX(FUNC_GERA_SIMILARIDADE(TJ.MARCA_FORMATADA, MAV.NOME)) AS SIMILARIEDADE_MARCA
                 FROM TABLE_JSON TJ
                          FULL JOIN MARCA_VEICULO MAV ON TRUE
                 GROUP BY TJ.MARCA_FORMATADA, TJ.MARCA, MAV.NOME, MAV.CODIGO
                 ORDER BY TJ.MARCA_FORMATADA, SIMILARIEDADE_MARCA DESC
             ),

             -- REMOVE MARCAS_MODELOS IGUAIS.
             DISTINCT_MARCAS_MODELOS_VEICULOS AS (
                 SELECT DISTINCT ON (CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO_FORMATADO)) SMV.COD_MARCA_BANCO,
                                                                                       TJ.MARCA                                         AS MARCA_PLANILHA,
                                                                                       TJ.MODELO                                        AS MODELO_PLANILHA,
                                                                                       CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO)           AS MARCA_MODELO_PLANILHA,
                                                                                       CONCAT(SMV.COD_MARCA_BANCO, TJ.MODELO_FORMATADO) AS MARCA_MODELO_PLANILHA_FORMATADA
                 FROM TABLE_JSON TJ
                          JOIN SIMILARIDADE_MARCAS_VEICULOS SMV ON TJ.MARCA = SMV.MARCA_PLANILHA
             ),

             -- PROCURA SIMILARIEDADE DE MARCA_MODELO DE VEÍCULO NO BANCO.
             SIMILARIEDADE_MARCAS_MODELOS_VEICULOS AS (
                 SELECT DISTINCT ON (DMMV.MARCA_MODELO_PLANILHA_FORMATADA) DMMV.MARCA_PLANILHA                  AS MARCA_PLANILHA,
                                                                           DMMV.MODELO_PLANILHA                 AS MODELO_PLANILHA,
                                                                           DMMV.MARCA_MODELO_PLANILHA           AS MARCA_MODELO_PLANILHA,
                                                                           DMMV.MARCA_MODELO_PLANILHA_FORMATADA AS MARCA_MODELO_PLANILHA_FORMATADA,
                                                                           MAV.NOME                             AS MARCA_VEICULO_BANCO,
                                                                           MAV.CODIGO                           AS COD_MARCA_VEICULO,
                                                                           MOV.NOME                             AS MODELO_VEICULO_BANCO,
                                                                           MOV.CODIGO                           AS COD_MODELO_VEICULO,
                                                                           CASE
                                                                               WHEN DMMV.COD_MARCA_BANCO = MAV.CODIGO
                                                                                   THEN
                                                                                   MAX(FUNC_GERA_SIMILARIDADE(DMMV.MODELO_PLANILHA, MOV.NOME))
                                                                               ELSE F_SEM_SIMILARIDADE
                                                                               END                              AS SIMILARIEDADE_MARCA_MODELO
                 FROM DISTINCT_MARCAS_MODELOS_VEICULOS DMMV
                          FULL JOIN MARCA_VEICULO MAV ON TRUE
                          JOIN MODELO_VEICULO MOV ON MAV.CODIGO = MOV.COD_MARCA
                 WHERE MOV.COD_EMPRESA = F_COD_EMPRESA
                 GROUP BY DMMV.MARCA_MODELO_PLANILHA_FORMATADA, DMMV.MARCA_MODELO_PLANILHA, DMMV.MARCA_PLANILHA,
                          DMMV.COD_MARCA_BANCO,
                          DMMV.MODELO_PLANILHA, MAV.NOME,
                          MAV.CODIGO, MOV.NOME,
                          MOV.CODIGO
                 ORDER BY DMMV.MARCA_MODELO_PLANILHA_FORMATADA, SIMILARIEDADE_MARCA_MODELO DESC
             ),

             -- PROCURA DIAGRAMA NO BANCO.
             SIMILARIEDADE_DIAGRAMA AS (
                 SELECT DISTINCT ON (TJ.DIAGRAMA_FORMATADO ) TJ.DIAGRAMA                                                 AS DIAGRAMA_PLANILHA,
                                                             TJ.DIAGRAMA_FORMATADO                                       AS DIAGRAMA_PLANILHA_FORMATADO,
                                                             VD.NOME                                                     AS DIAGRAMA_BANCO,
                                                             VD.CODIGO                                                   AS COD_DIAGRAMA_BANCO,
                                                             MAX(FUNC_GERA_SIMILARIDADE(TJ.DIAGRAMA_FORMATADO, VD.NOME)) AS SIMILARIEDADE_DIAGRAMA
                 FROM TABLE_JSON TJ
                          FULL JOIN VEICULO_DIAGRAMA VD ON TRUE
                 GROUP BY TJ.DIAGRAMA_FORMATADO, TJ.DIAGRAMA, VD.NOME, VD.CODIGO
                 ORDER BY TJ.DIAGRAMA_FORMATADO, SIMILARIEDADE_DIAGRAMA DESC
             ),

             -- REMOVE TIPOS IGUAIS.
             DISTINCT_TIPOS_DIAGRAMA_VEICULOS AS (
                 SELECT DISTINCT ON (CONCAT(SD.COD_DIAGRAMA_BANCO, TJ.TIPO_FORMATADO)) SD.COD_DIAGRAMA_BANCO,
                                                                                       TJ.DIAGRAMA                                      AS DIAGRAMA_PLANILHA,
                                                                                       TJ.TIPO                                          AS TIPO_PLANILHA,
                                                                                       CONCAT(TJ.TIPO, TJ.DIAGRAMA)                     AS TIPO_DIAGRAMA_PLANILHA,
                                                                                       CONCAT(TJ.TIPO_FORMATADO, TJ.DIAGRAMA_FORMATADO) AS TIPO_DIAGRAMA_PLANILHA_FORMATADO
                 FROM TABLE_JSON TJ
                          JOIN SIMILARIEDADE_DIAGRAMA SD ON TJ.DIAGRAMA = SD.DIAGRAMA_PLANILHA
             ),

             -- PROCURA SIMILARIDADE DE TIPO_DIAGRAMA DE VEÍCULO NO BANCO.
             SIMILARIDADE_TIPOS_DIAGRAMA_VEICULOS AS (
                 SELECT DISTINCT ON (DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO) DTDV.DIAGRAMA_PLANILHA                AS DIAGRAMA_PLANILHA,
                                                                            DTDV.TIPO_PLANILHA                    AS TIPO_PLANILHA,
                                                                            DTDV.TIPO_DIAGRAMA_PLANILHA           AS TIPO_DIAGRAMA_PLANILHA,
                                                                            DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO AS TIPO_DIAGRAMA_PLANILHA_FORMATADO,
                                                                            VD.NOME                               AS DIAGRAMA_VEICULO_BANCO,
                                                                            VD.CODIGO                             AS COD_DIAGRAMA_VEICULO,
                                                                            VT.NOME                               AS TIPO_BANCO,
                                                                            VT.CODIGO                             AS COD_TIPO_VEICULO,
                                                                            CASE
                                                                                WHEN DTDV.COD_DIAGRAMA_BANCO = VD.CODIGO
                                                                                    THEN MAX(FUNC_GERA_SIMILARIDADE(DTDV.TIPO_PLANILHA, VT.NOME))
                                                                                ELSE F_SEM_SIMILARIDADE
                                                                                END                               AS SIMILARIEDADE_TIPO_DIAGRAMA
                 FROM DISTINCT_TIPOS_DIAGRAMA_VEICULOS DTDV
                          FULL JOIN VEICULO_TIPO VT ON TRUE
                          LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 WHERE VT.COD_EMPRESA = F_COD_EMPRESA
                 GROUP BY DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO,
                          DTDV.TIPO_DIAGRAMA_PLANILHA,
                          DTDV.DIAGRAMA_PLANILHA,
                          DTDV.COD_DIAGRAMA_BANCO,
                          DTDV.TIPO_PLANILHA,
                          VT.NOME,
                          VT.CODIGO,
                          VD.NOME,
                          VD.CODIGO
                 ORDER BY DTDV.TIPO_DIAGRAMA_PLANILHA_FORMATADO, SIMILARIEDADE_TIPO_DIAGRAMA DESC
             )

        SELECT VP.PLACA_PLANILHA,
               VP.PLACAS_DUPLICADAS,
               VP.PLACA_FORMATADA,
               VP.PLACA_7_DIGITOS,
               VP.PLACA_NA_UNIDADE_EMPRESA :: TEXT,
               F_IF(VP.PLACA_NA_UNIDADE_EMPRESA != NAO_ENCONTRADO, VP.STATUS_ATIVO, NULL),
               VP.KM,
               SMV.MARCA_PLANILHA,
               F_IF(SMV.SIMILARIEDADE_MARCA >= F_SIMILARIDADE, SMV.MARCA_VEICULO_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(SMV.SIMILARIEDADE_MARCA >= F_SIMILARIDADE, SMV.COD_MARCA_BANCO :: TEXT, NAO_ENCONTRADO),
               SMMV.MODELO_PLANILHA,
               F_IF(SMMV.SIMILARIEDADE_MARCA_MODELO >= F_SIMILARIDADE, SMMV.MODELO_VEICULO_BANCO :: TEXT,
                    NAO_ENCONTRADO),
               F_IF(SMMV.SIMILARIEDADE_MARCA_MODELO >= F_SIMILARIDADE, SMMV.COD_MODELO_VEICULO :: TEXT, NAO_ENCONTRADO),
               SD.DIAGRAMA_PLANILHA,
               F_IF(SD.SIMILARIEDADE_DIAGRAMA >= F_SIMILIARIDADE_DIAGRAMA, SD.DIAGRAMA_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(SD.SIMILARIEDADE_DIAGRAMA >= F_SIMILIARIDADE_DIAGRAMA, SD.COD_DIAGRAMA_BANCO :: TEXT,
                    NAO_ENCONTRADO) AS COD_DIAGRAMA,
               STDV.TIPO_PLANILHA,
               F_IF(STDV.SIMILARIEDADE_TIPO_DIAGRAMA >= F_SIMILARIDADE, STDV.TIPO_BANCO :: TEXT, NAO_ENCONTRADO),
               F_IF(STDV.SIMILARIEDADE_TIPO_DIAGRAMA >= F_SIMILARIDADE, STDV.COD_TIPO_VEICULO :: TEXT,
                    NAO_ENCONTRADO) AS COD_TIPO
        FROM VERIFICACOES_PLACA VP
                 JOIN TABLE_JSON TJ ON TJ.CODIGO = VP.CODIGO
                 JOIN SIMILARIDADE_MARCAS_VEICULOS SMV ON TJ.MARCA = SMV.MARCA_PLANILHA
                 JOIN SIMILARIEDADE_MARCAS_MODELOS_VEICULOS SMMV ON SMV.MARCA_PLANILHA = SMMV.MARCA_PLANILHA
                 JOIN SIMILARIEDADE_DIAGRAMA SD ON TJ.DIAGRAMA = SD.DIAGRAMA_PLANILHA
                 JOIN SIMILARIDADE_TIPOS_DIAGRAMA_VEICULOS STDV ON SD.DIAGRAMA_PLANILHA = STDV.DIAGRAMA_PLANILHA;
    DROP TABLE TABLE_JSON;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### FECHAMENTO MASSIVO DE OS #################################################
--######################################################################################################################
--######################################################################################################################
--CRIA TABELA PARA FECHAMENTO DE OS EM MASSA
CREATE TABLE SUPORTE.FECHAMENTO_OS
(
    CODIGO                     BIGSERIAL PRIMARY KEY,
    CPF_MECANICO               BIGINT,
    DATA_HORA_FIM_RESOLUCAO    TIMESTAMP WITH TIME ZONE,
    DATA_HORA_INICIO_RESOLUCAO TIMESTAMP WITH TIME ZONE,
    TEMPO_REALIZACAO           BIGINT,
    PLACA_VEICULO              VARCHAR(7),
    KM                         BIGINT,
    DATA_HORA_CONSERTO         TIMESTAMP WITH TIME ZONE,
    FEEDBACK_CONSERTO          TEXT,
    COD_UNIDADE                BIGINT,
    COD_OS                     BIGINT,
    COD_PERGUNTA               BIGINT,
    COD_ALTERNATIVA            BIGINT,
    STATUS_ITEM_FECHADO        BOOLEAN,
    MENSAGEM_STATUS_ITEM       TEXT,
    STATUS_OS_FECHADA          BOOLEAN,
    MENSAGEM_STATUS_OS         TEXT,
    USUARIO                    NAME,
    DATA_SOLICITACAO           DATE,
    HORA_SOLICITACAO           TIME WITHOUT TIME ZONE
);
--######################################################################################################################
--###################################  CRIA FUNCTION PARA FECHAMENTO DE OS EM MASSA ####################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- ao realizar o preenchumento da tabela SUPORTE.FECHAMENTO_OS, a trigger TG_VERIFICA_FECHAMENTO_OS é acionada e a
-- function é executada. A function realiza verificações e, se estiver tudo certo, fecha o item. Se todos os itens
-- estiverem fechados a Ordem de Serviço também é fechada.
-- Se a requisição for do tipo 'UPDATE' e o item já estiver sinalizado como fechado, o update não será realizado e será
-- salvo quem requisitou o novo fechamento, assim como uma mensagem sinalizando que o item já estava fechado.
--
-- A trigger func_audit também é acionada.
--
-- Précondições:
-- 1) Verifica se:
-- * todos os campos, exceto o KM, devem estar preenchidos. Caso o KM não esteja, será pego o km atual do veículo para o
-- fechamento do item. Não existe verificação entre KM inserido e atual devido a necessidade de fechamentos retroativos.
-- * a unidade está cadastrada;
-- * o colaborador (mecânico) existe e se ele é da empresa cujo check foi realizado;
-- * a placa está cadastrada
-- * a data/h do início da resolução é menor que a data/h atual
-- * a data/h do início da resolução é maior que a data/h da realização do check
-- * a data/h do fim da resolução é menor que a data/h atual
-- * a data/h do fim da resolução é maior que a data/h do início da resolução
-- * a data/h do fim da resolução é maior que a data/h da realização do check
-- * a data/h do conserto é menor que a data/h atual
-- * a data/h do conserto é maior que a data/h da realização do checklist
-- * a data/h do conserto é maior que a data/h do fim da resolução
-- * o cod_os existe:
-- ** se não existir: aponta o erro
-- ** se existir verifica se: a placa é do checklist que gerou a OS.
-- **                         a OS pertence à unidade informada
-- **                         a pergunta está contida na ordem de serviço itens
-- **                         a alternativa está contida na pergunta
--
-- 2) Realiza o fechamento dos itens, caso nenhuma das verificações do passo 1 tenha apontado erro.
-- * para o fechamento, verifica se:
-- * o item já estava fechado (status realizado): Se estiver, modifica o STATUS_ITEM_FECHADO para true e aponta que o
-- item já estava fechado na MENSAGEM_STATUS_ITEM;
-- **                                             Se não, o fechamento é realizado.
-- * existem itens pendentes na OS: Se existir e a OS estiver fechada há um erro, portanto a OS é reaberta e é exibida
-- uma mensagem específica para esse caso.
-- **                               Se não, o fechamento da OS é realizado.
--
-- 3) Vale lembrar que apenas 1 dos itens, de uma mesma OS, vai fechar a OS (O último item a ser resolvido). Portanto
-- pode haver itens fechados com OS aberta.
-- Histórico:
-- 2019-08-27 -> function criada (thaisksf - PL-2156).
CREATE OR REPLACE FUNCTION SUPORTE.TG_FUNC_CHECKLIST_OS_FECHAMENTO_MASSIVO_OS()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TEM_VALOR_NULL                  BOOLEAN                  := (NEW.CPF_MECANICO IS NULL OR
                                                                 NEW.DATA_HORA_FIM_RESOLUCAO IS NULL OR
                                                                 NEW.DATA_HORA_INICIO_RESOLUCAO IS NULL OR
                                                                 NEW.TEMPO_REALIZACAO IS NULL OR
                                                                 NEW.PLACA_VEICULO IS NULL OR
                                                                 NEW.DATA_HORA_CONSERTO IS NULL OR
                                                                 NEW.FEEDBACK_CONSERTO IS NULL OR
                                                                 NEW.COD_UNIDADE IS NULL OR
                                                                 NEW.COD_OS IS NULL OR
                                                                 NEW.COD_PERGUNTA IS NULL OR
                                                                 NEW.COD_ALTERNATIVA IS NULL);
    KM_ATUAL_VEICULO                BIGINT                   := (SELECT V.KM
                                                                 FROM VEICULO V
                                                                 WHERE V.PLACA = NEW.PLACA_VEICULO);
    KM_VEICULO                      BIGINT                   := (CASE
                                                                     WHEN NEW.KM IS NULL THEN KM_ATUAL_VEICULO
                                                                     ELSE NEW.KM END);
    STATUS_RESOLUCAO_COSI_REALIZADO TEXT                     := 'R';
    STATUS_RESOLUCAO_COSI_PENDENTE  TEXT                     := 'P';
    STATUS_COS_FECHADO              TEXT                     := 'F';
    STATUS_COS_ABERTO               TEXT                     := 'A';
    QTD_ERROS                       BIGINT                   := 0;
    SOMA_ERRO                       BIGINT                   := 1;
    MSGS_ERROS                      TEXT;
    CODIGO_EMPRESA                  BIGINT                   := (SELECT U.COD_EMPRESA
                                                                 FROM UNIDADE U
                                                                 WHERE U.CODIGO = NEW.COD_UNIDADE);
    CODIGO_CHECKLIST                BIGINT                   := (SELECT COS.COD_CHECKLIST
                                                                 FROM CHECKLIST_ORDEM_SERVICO COS
                                                                 WHERE COS.CODIGO = NEW.COD_OS
                                                                   AND COS.COD_UNIDADE = NEW.COD_UNIDADE);
    DATA_HORA_CHECKLIST             TIMESTAMP WITH TIME ZONE := (SELECT C.DATA_HORA
                                                                 FROM CHECKLIST C
                                                                 WHERE C.CODIGO = CODIGO_CHECKLIST);
    PLACA_CADASTRADA                BOOLEAN                  := TRUE;
    QUEBRA_LINHA                    TEXT                     := CHR(10);
    VERIFICAR_OS                    BOOLEAN                  := FALSE;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_ITEM_FECHADO IS TRUE)
    THEN
        NEW.MENSAGEM_STATUS_ITEM = CONCAT(OLD.MENSAGEM_STATUS_ITEM, QUEBRA_LINHA, 'O ITEM JÁ ESTAVA FECHADO');
        NEW.MENSAGEM_STATUS_OS = OLD.MENSAGEM_STATUS_OS;
        NEW.CPF_MECANICO = OLD.CPF_MECANICO;
        NEW.DATA_HORA_FIM_RESOLUCAO = OLD.DATA_HORA_FIM_RESOLUCAO;
        NEW.DATA_HORA_INICIO_RESOLUCAO = OLD.DATA_HORA_INICIO_RESOLUCAO;
        NEW.TEMPO_REALIZACAO = OLD.TEMPO_REALIZACAO;
        NEW.PLACA_VEICULO = OLD.PLACA_VEICULO;
        NEW.KM = OLD.KM;
        NEW.DATA_HORA_CONSERTO = OLD.DATA_HORA_CONSERTO;
        NEW.FEEDBACK_CONSERTO = OLD.FEEDBACK_CONSERTO;
        NEW.COD_UNIDADE = OLD.COD_UNIDADE;
        NEW.COD_OS = OLD.COD_OS;
        NEW.COD_PERGUNTA = OLD.COD_PERGUNTA;
        NEW.COD_ALTERNATIVA = OLD.COD_ALTERNATIVA;
        NEW.STATUS_ITEM_FECHADO = OLD.STATUS_ITEM_FECHADO;
        NEW.STATUS_OS_FECHADA = OLD.STATUS_OS_FECHADA;
        NEW.USUARIO := CONCAT(OLD.USUARIO, QUEBRA_LINHA, NEW.USUARIO);
        NEW.DATA_SOLICITACAO := OLD.DATA_SOLICITACAO;
        NEW.HORA_SOLICITACAO := OLD.HORA_SOLICITACAO;
    ELSE
        NEW.USUARIO := SESSION_USER;
        NEW.DATA_SOLICITACAO := CURRENT_DATE;
        NEW.HORA_SOLICITACAO := CURRENT_TIME;

        --VERIFICA INFORMAÇÕES
        IF (TEM_VALOR_NULL)
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - EXISTEM CAMPOS SEM PREENCHIMENTO');
        END IF;

        --VERIFICA SE UNIDADE ESTÁ CADASTRADA
        IF ((NEW.COD_UNIDADE IS NOT NULL) AND
            NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - UNIDADE NÃO CADASTRADA');
        END IF;

        --VERIFICA SE O COLABORADOR (MECÂNICO) EXISTE
        IF ((NEW.CPF_MECANICO IS NOT NULL) AND
            NOT EXISTS(SELECT C.CPF FROM COLABORADOR C WHERE C.CPF = NEW.CPF_MECANICO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - MECÂNICO NÃO CADASTRADO');
            --VERIFICA SE O COLABORADOR (MECÂNICO) É DA EMPRESA
        ELSEIF ((NEW.CPF_MECANICO IS NOT NULL) AND
                NOT EXISTS(
                        SELECT C.CPF
                        FROM COLABORADOR C
                        WHERE C.CPF = NEW.CPF_MECANICO
                          AND C.COD_EMPRESA = CODIGO_EMPRESA))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS =
                    CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                           ' - MECÂNICO NÃO PERTENCE À EMPRESA DA ORDEM DE SERVIÇO');
        END IF;

        --VERIFICA SE PLACA ESTÁ CADASTRADA
        IF ((NEW.PLACA_VEICULO IS NOT NULL) AND
            NOT EXISTS(SELECT V.PLACA FROM VEICULO V WHERE V.PLACA = NEW.PLACA_VEICULO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - PLACA NÃO ESTÁ CADASTRADA');
            PLACA_CADASTRADA = FALSE;
        END IF;

        --VERIFICAÇÕES REFERENTE ÀS DATAS E HORAS
        ---DATA/H INICIO DA RESOLUÇÃO:
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do início da resolução
        IF ((NEW.DATA_HORA_INICIO_RESOLUCAO IS NOT NULL) AND
            ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_INICIO_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE INICIO DA RESOLUÇÃO NÃO PODE SER MAIOR QUE A ATUAL ');
            ----Verifica e adiciona msg de erro se a  data do inicio da resolução for menor que a data/h do checklist realizado
        ELSEIF ((NEW.DATA_HORA_INICIO_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_INICIO_RESOLUCAO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE INICIO DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
        END IF;

        ---DATA/HR FIM DA RESOLUÇÃO
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do fim da resolução
        IF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL) AND
            ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_FIM_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MAIOR QUE A ATUAL');
            ----Verifica e adiciona msg de erro se a data/h do fim da resolução for menor que a data/h do inicio da resolucao
        ELSEIF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_FIM_RESOLUCAO < NEW.DATA_HORA_INICIO_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DE INICIO DA RESOLUCAO');
            ----Verifica e adiciona msg de erro se a data/h do fim da resolução for menor que a data/h do checklist realizado
        ELSEIF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_FIM_RESOLUCAO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
        END IF;

        ---DATA/H CONSERTO
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do conserto
        IF ((NEW.DATA_HORA_CONSERTO IS NOT NULL) AND
            ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_CONSERTO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DO CONSERTO NÃO PODE SER MAIOR QUE A ATUAL');
            ----Verifica e adiciona msg de erro se a data/h do conserto for menor que a data/h da realização do checklist
        ELSEIF ((NEW.DATA_HORA_CONSERTO IS NOT NULL)
            AND (NEW.DATA_HORA_CONSERTO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DO CONSERTO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
            ----Verifica e adiciona msg de erro se a data/h do conserto for menor que a data/h do fim da resolucao
        ELSEIF ((NEW.DATA_HORA_CONSERTO IS NOT NULL) AND (NEW.DATA_HORA_CONSERTO < NEW.DATA_HORA_FIM_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS =
                    CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                           ' - A DATA/HORA DE CONSERTO NÃO PODE SER MENOR QUE A DATA/HORA DO FIM DA RESOLUCAO');
        END IF;

        ---VERIFICA SE COD_OS EXISTE
        IF ((NEW.COD_OS IS NOT NULL AND NEW.COD_UNIDADE IS NOT NULL) AND
            NOT EXISTS(SELECT COS.CODIGO FROM CHECKLIST_ORDEM_SERVICO COS WHERE COS.CODIGO = NEW.COD_OS))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - CODIGO DA ORDEM DE SERVIÇO NÃO EXISTE');

            --VERIFICAÇÕES SE A OS EXISTIR
        ELSEIF ((NEW.COD_OS IS NOT NULL) AND
                EXISTS(SELECT COSI.COD_OS
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                       WHERE COSI.COD_OS = NEW.COD_OS))
        THEN
            ---VERIFICA SE A PLACA É DO CHECKLIST QUE GEROU A OS
            IF ((NEW.PLACA_VEICULO IS NOT NULL) AND (PLACA_CADASTRADA) AND NOT EXISTS(SELECT C.PLACA_VEICULO
                                                                                      FROM CHECKLIST C
                                                                                      WHERE C.CODIGO = CODIGO_CHECKLIST
                                                                                        AND C.PLACA_VEICULO = NEW.PLACA_VEICULO))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS =
                        CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                               ' - A PLACA NÃO PERTENCE AO CHECKLIST DA ORDEM DE SERVIÇO');
                ---VERIFICA SE OS PERTENCE À UNIDADE
            END IF;
            IF ((NEW.COD_UNIDADE IS NOT NULL) AND
                NOT EXISTS(SELECT COS.CODIGO
                           FROM CHECKLIST_ORDEM_SERVICO COS
                           WHERE COS.CODIGO = NEW.COD_OS
                             AND COS.COD_UNIDADE = NEW.COD_UNIDADE))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS =
                        CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                               ' - A ORDEM DE SERVIÇO ABERTA NÃO É DA UNIDADE INFORMADA');
            END IF;

            ---VERIFICA SE A PERGUNTA ESTÁ CONTIDA NA ORDEM DE SERVIÇO ITENS
            IF ((NEW.COD_PERGUNTA IS NOT NULL) AND
                NOT EXISTS(SELECT COSI.COD_PERGUNTA
                           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                           WHERE COSI.COD_OS = NEW.COD_OS
                             AND COSI.COD_PERGUNTA = NEW.COD_PERGUNTA))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                    ' - A PERGUNTA NÃO EXISTE PARA A ORDEM DE SERVIÇO INFORMADA');
                ---VERIFICA SE A ALTERNATIVA ESTÁ CONTIDA NA PERGUNTA
            ELSEIF ((NEW.COD_PERGUNTA IS NOT NULL AND NEW.COD_ALTERNATIVA IS NOT NULL) AND
                    NOT EXISTS(SELECT COSI.COD_ALTERNATIVA
                               FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                               WHERE COSI.COD_PERGUNTA = NEW.COD_PERGUNTA
                                 AND COSI.COD_ALTERNATIVA = NEW.COD_ALTERNATIVA))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                    ' - A ALTERNATIVA NÃO EXISTE PARA PERGUNTA INFORMADA');
            END IF;
        END IF;

        --REALIZA O FECHAMENTO DOS ITENS SE NÃO HOUVER ERROS
        IF (QTD_ERROS > 0)
        THEN
            NEW.STATUS_ITEM_FECHADO := FALSE;
            NEW.MENSAGEM_STATUS_ITEM :=
                    CONCAT('ITEM NÃO FECHADO', QUEBRA_LINHA, 'QUANTIDADE DE ERROS: ', QTD_ERROS, QUEBRA_LINHA,
                           MSGS_ERROS);
            NEW.STATUS_OS_FECHADA := FALSE;
            NEW.MENSAGEM_STATUS_OS := 'FECHAMENTO NÃO REALIZADO';
        ELSEIF (NOT TEM_VALOR_NULL OR QTD_ERROS = 0)
        THEN
            ---VERIFICA SE O ITEM JÁ ESTAVA FECHADO (STATUS REALIZADO)
            IF ((NEW.COD_OS IS NOT NULL AND NEW.COD_PERGUNTA IS NOT NULL AND NEW.COD_ALTERNATIVA IS NOT NULL) AND
                EXISTS(SELECT COSI.CODIGO
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                       WHERE COSI.COD_OS = NEW.COD_OS
                         AND COSI.COD_PERGUNTA = NEW.COD_PERGUNTA
                         AND COSI.COD_ALTERNATIVA = NEW.COD_ALTERNATIVA
                         AND COSI.STATUS_RESOLUCAO = STATUS_RESOLUCAO_COSI_REALIZADO))
            THEN
                -- O ITEM JÁ ESTAVA FECHADO
                NEW.STATUS_ITEM_FECHADO = TRUE;
                NEW.MENSAGEM_STATUS_ITEM = 'O ITEM JÁ ESTAVA FECHADO';
                VERIFICAR_OS := TRUE;
            ELSE
                -- REALIZADO O FECHAMENTO DO ITEM
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET CPF_MECANICO               = NEW.CPF_MECANICO,
                    DATA_HORA_FIM_RESOLUCAO    = NEW.DATA_HORA_FIM_RESOLUCAO,
                    DATA_HORA_INICIO_RESOLUCAO = NEW.DATA_HORA_INICIO_RESOLUCAO,
                    TEMPO_REALIZACAO           = NEW.TEMPO_REALIZACAO,
                    KM                         = KM_VEICULO,
                    DATA_HORA_CONSERTO         = NEW.DATA_HORA_CONSERTO,
                    FEEDBACK_CONSERTO          = NEW.FEEDBACK_CONSERTO,
                    STATUS_RESOLUCAO           = STATUS_RESOLUCAO_COSI_REALIZADO
                WHERE COD_UNIDADE = NEW.COD_UNIDADE
                  AND COD_OS = NEW.COD_OS
                  AND COD_PERGUNTA = NEW.COD_PERGUNTA
                  AND COD_ALTERNATIVA = NEW.COD_ALTERNATIVA;
                NEW.STATUS_ITEM_FECHADO = TRUE;
                NEW.MENSAGEM_STATUS_ITEM :=
                        CONCAT('ITEM FECHADO ATRAVÉS DO SUPORTE', QUEBRA_LINHA, 'KM DO VEÍCULO NA HORA DO FECHAMENTO: ',
                               KM_ATUAL_VEICULO);
                VERIFICAR_OS := TRUE;
            END IF;
        ELSE
            RAISE EXCEPTION 'ERRO AO FECHAR O ITEM DA ORDEM DE SERVIÇO';
        END IF;

        IF (VERIFICAR_OS)
        THEN
            -- VERIFICA SE EXISTEM ITENS PENDENTES NA OS
            IF EXISTS(SELECT COSI.CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                      WHERE COSI.COD_OS = NEW.COD_OS
                        AND COSI.COD_UNIDADE = NEW.COD_UNIDADE
                        AND COSI.STATUS_RESOLUCAO = STATUS_RESOLUCAO_COSI_PENDENTE)
            THEN
                NEW.STATUS_OS_FECHADA := FALSE;
                NEW.MENSAGEM_STATUS_OS := 'OS ABERTA';
                -- VERIFICA SE A OS ESTÁ FECHADA MESMO COM ITENS PENDENTES
                IF EXISTS(SELECT COS.STATUS
                          FROM CHECKLIST_ORDEM_SERVICO COS
                          WHERE COS.STATUS = STATUS_COS_FECHADO
                            AND COS.CODIGO = NEW.COD_OS
                            AND COS.COD_UNIDADE = NEW.COD_UNIDADE)
                THEN
                    -- SE ESTIVER, É UM ERRO. ENTÃO REABRE A OS E EXIBE MSG ESPECÍFICA.
                    UPDATE CHECKLIST_ORDEM_SERVICO
                    SET STATUS = STATUS_COS_ABERTO
                    WHERE CODIGO = NEW.COD_OS
                      AND COD_UNIDADE = NEW.COD_UNIDADE;
                    NEW.STATUS_OS_FECHADA := FALSE;
                    NEW.MENSAGEM_STATUS_OS :=
                            'ORDEM DE SERVIÇO ESTAVA FECHADA MAS POSSUÍA ITENS PENDENTES - OS FOI REABERTA';
                END IF;
            ELSE
                -- SE NÃO EXISTIR ITENS PENDENTES, A OS PODE SER FECHADA.
                UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET STATUS               = STATUS_COS_FECHADO,
                    DATA_HORA_FECHAMENTO = (SELECT COSI.DATA_HORA_FIM_RESOLUCAO
                                            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                            WHERE COSI.COD_UNIDADE = NEW.COD_UNIDADE
                                              AND COSI.COD_OS = NEW.COD_OS
                                              AND COSI.COD_PERGUNTA = NEW.COD_PERGUNTA
                                              AND COSI.COD_ALTERNATIVA = NEW.COD_ALTERNATIVA)
                WHERE COD_UNIDADE = NEW.COD_UNIDADE
                  AND CODIGO NOT IN
                      (SELECT COD_OS
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS
                       WHERE COD_UNIDADE = NEW.COD_UNIDADE
                         AND CPF_MECANICO IS NULL);
                NEW.STATUS_OS_FECHADA := TRUE;
                NEW.MENSAGEM_STATUS_OS := 'OS FECHADA';
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END
$$;

-- Realiza verificações e fecha (ou não) os itens da COSI e a OS da COS, com base no que foi preenchido na tabela
-- SUPORTE.FECHAMENTO_OS
CREATE TRIGGER TG_VERIFICA_FECHAMENTO_OS
    BEFORE INSERT OR UPDATE
    ON SUPORTE.FECHAMENTO_OS
    FOR EACH ROW
EXECUTE PROCEDURE SUPORTE.TG_FUNC_CHECKLIST_OS_FECHAMENTO_MASSIVO_OS();

--Mapeia histórico de alteração na tabela SUPORTE.FECHAMENTO_OS
CREATE TRIGGER TG_FUNC_AUDIT_FECHAMENTO_OS
    AFTER INSERT OR UPDATE
    ON SUPORTE.FECHAMENTO_OS
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################# INSERE DIAGRAMA CARRETA 1 EIXO #########################################
--######################################################################################################################
--######################################################################################################################
--PL-2309
INSERT INTO VEICULO_DIAGRAMA (CODIGO,
                              NOME,
                              URL_IMAGEM)
VALUES (15,
        'CARRETA 1 EIXO SINGLE',
        'WWW.GOOGLE.COM/CARRETA1');

INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA,
                                    TIPO_EIXO,
                                    POSICAO,
                                    QT_PNEUS,
                                    EIXO_DIRECIONAL)
VALUES (15,
        'T',
        1,
        2,
        FALSE);

INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (15, 211),
       (15, 221),
       (15, 900),
       (15, 901),
       (15, 902),
       (15, 903),
       (15, 904),
       (15, 905),
       (15, 906),
       (15, 907),
       (15, 908);

-- Insere estepes para o Biarticulado
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG)
VALUES (14, 900),
       (14, 901),
       (14, 902),
       (14, 903),
       (14, 904),
       (14, 905),
       (14, 906),
       (14, 907),
       (14, 908);
--######################################################################################################################
--######################################################################################################################



--################################        MIGRAÇÃO DAS FUNCS DE SUPORTE         ########################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--PL-2242
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_UNIDADE_CADASTRA_UNIDADE(BIGINT, BIGINT, TEXT, TEXT, INTEGER[])
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_COLABORADOR_BUSCA_VINCULAR_PERMISSAO     ####################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA(BIGINT, BIGINT)
    SET SCHEMA SUPORTE;

-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_COLABORADOR_BUSCA_VINCULAR_PERMISSAO(BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS     ####################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS(BIGINT, BIGINT, INTEGER, BIGINT, BIGINT, BIGINT, INTEGER,
    INTEGER, INTEGER, INTEGER)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_COLABORADOR_DELETA_COLABORADOR     ##########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_COLABORADOR_DELETA_COLABORADOR(BIGINT, BIGINT, BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS     #######################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(TEXT[], TEXT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_AFERICAO_ALTERA_KM_COLETADO_AFERICAO     ####################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_AFERICAO_ALTERA_KM_COLETADO_AFERICAO(BIGINT, TEXT, BIGINT, BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_MOVIMENTACAO_ALTERA_KM_MOVIMENTACAO     #####################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_MOVIMENTACAO_ALTERA_KM_MOVIMENTACAO(BIGINT, VARCHAR, INTEGER)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_NUMERO_FOGO     ################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_NUMERO_FOGO(BIGINT, BIGINT, TEXT, BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_AFERICAO_DELETA_AFERICAO_VALORES     ########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE
    ALTER FUNCTION FUNC_AFERICAO_DELETA_AFERICAO(BIGINT, TEXT, BIGINT)
    SET SCHEMA SUPORTE;

-- RECRIA FUNCTION NO SCHEMA SUPORTE
-- Dropamos e recriamos pq internamente ela usa outra function do suporte.
DROP FUNCTION PUBLIC.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(BIGINT, TEXT, BIGINT, BIGINT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
                                                                         F_PLACA TEXT,
                                                                         F_CODIGO_PNEU BIGINT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS   BIGINT;
    -- Busca a quantidade de valores aferidos estão ativos nesta aferição
    QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                        FROM AFERICAO_VALORES
                                        WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                          AND COD_UNIDADE = F_COD_UNIDADE);

    -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
    PREFIXO_MENSAGEM_RETORNO TEXT;
BEGIN
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    IF NOT EXISTS(SELECT *
                  FROM AFERICAO_VALORES
                  WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                    AND COD_UNIDADE = F_COD_UNIDADE
                    AND COD_PNEU = F_CODIGO_PNEU)
    THEN
        RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Placa %, Pneu %
            e Código %', F_COD_UNIDADE, F_PLACA,  F_CODIGO_PNEU, F_CODIGO_AFERICAO;
    END IF;

    -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
    CASE QTD_VALORES_AFERICAO
        WHEN 1
            THEN
                -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
                PERFORM SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO);
                PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
        ELSE
            -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
            -- DELETA AFERIÇÃO.
            UPDATE AFERICAO_VALORES_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            IF (QTD_LINHAS_ATUALIZADAS <= 0)
            THEN
                RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %, Placa %,
                    Pneu % e Código %', F_COD_UNIDADE, F_PLACA, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
            END IF;

            -- DELETA AFERIÇÃO MANUTENÇÃO.
            -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
            UPDATE AFERICAO_MANUTENCAO_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
        END CASE;

    SELECT PREFIXO_MENSAGEM_RETORNO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DO PNEU: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_CODIGO_AFERICAO
    INTO AVISO_AFERICAO_VALOR_DELETADA;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE     ###########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE(BIGINT, BIGINT, BIGINT, VARCHAR)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_PNEU_DELETA_PNEU     ########################################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_PNEU_DELETA_PNEU(BIGINT, BIGINT, TEXT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_VEICULO_DELETA_VEICULO     ##################################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_VEICULO_DELETA_VEICULO(BIGINT, BIGINT, TEXT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST     #######################################
--######################################################################################################################
 -- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(BIGINT, BIGINT, TEXT, BIGINT)
    SET SCHEMA SUPORTE;

-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(BIGINT, BIGINT, BOOLEAN)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_AFERICAO_DELETA_SERVICO_AFERICAO     ########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(BIGINT, BIGINT, BIGINT, TEXT, BIGINT, BIGINT, TEXT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST     ########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST( BIGINT, BIGINT, BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_DIMENSAO     ###################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_DIMENSAO( BIGINT, BIGINT, BIGINT, BIGINT, BIGINT)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST     ########################################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION func_checklist_copia_modelo_checklist(f_cod_modelo_checklist_copiado bigint, f_cod_unidade_destino_modelo_checklist bigint, f_copiar_cargos_liberados boolean, f_copiar_tipos_veiculos_liberados boolean, OUT cod_modelo_checklist_inserido bigint, OUT aviso_modelo_inserido text)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   CRIA FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST_ENTRE_EMPRESAS     #########################
--######################################################################################################################
-- RECRIA FUNCTION NO SCHEMA SUPORTE.
    ALTER FUNCTION func_checklist_copia_modelo_checklist_entre_empresas(f_cod_modelo_checklist_copiado bigint, f_cod_unidade_destino_modelo_checklist bigint, f_cod_cargos_checklist bigint[], f_cod_tipos_veiculos_checklist bigint[], OUT cod_modelo_checklist_inserido bigint, OUT aviso_modelo_inserido text)
    SET SCHEMA SUPORTE;

--######################################################################################################################
--######################################################################################################################
-- PL-2229
--######################################################################################################################
--############################     1 FUNC_AFERICAO_DELETA_AFERICAO    ##################################################
--######################################################################################################################
-- ATUALIZA TABELA AFERICAO_DATA
ALTER TABLE AFERICAO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE AFERICAO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE AFERICAO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE AFERICAO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA AFERICAO_VALORES_DATA
ALTER TABLE AFERICAO_VALORES_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE AFERICAO_VALORES_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE AFERICAO_VALORES_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE AFERICAO_VALORES_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA AFERICAO_MANUTENCAO_DATA
ALTER TABLE AFERICAO_MANUTENCAO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE AFERICAO_MANUTENCAO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE AFERICAO_MANUTENCAO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE AFERICAO_MANUTENCAO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_AFERICAO_DELETA_AFERICAO_VALORES
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE BIGINT,
                                                         F_PLACA TEXT,
                                                         F_CODIGO_AFERICAO BIGINT,
                                                         OUT AVISO_AFERICAO_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    IF ((SELECT COUNT(codigo)
         FROM AFERICAO_DATA
         WHERE CODIGO = F_CODIGO_AFERICAO
           AND COD_UNIDADE = F_COD_UNIDADE
           AND PLACA_VEICULO = F_PLACA) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade %, Placa % e Código %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO.
    UPDATE AFERICAO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CODIGO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO VALORES.
    UPDATE AFERICAO_VALORES_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
    IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                            FROM AFERICAO_VALORES_DATA AVD
                                            WHERE AVD.COD_UNIDADE = F_COD_UNIDADE
                                              AND AVD.COD_AFERICAO = F_CODIGO_AFERICAO) > 0))
    THEN
        RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: %, placa: % e código: %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO MANUTENÇÃO.
    -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO;

    SELECT 'AFERIÇÃO DELETADA: '
               || F_CODIGO_AFERICAO
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_AFERICAO_DELETADA;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   2 FUNC_AFERICAO_DELETA_AFERICAO_VALORES    ############################################
--######################################################################################################################
-- ATUALIZA TABELA
-- FUNC UTILIZA UMA TABELA JÁ MODIFICADA

-- CRIA FUNC_AFERICAO_DELETA_AFERICAO_VALORES
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
                                                                 F_PLACA TEXT,
                                                                 F_CODIGO_PNEU BIGINT,
                                                                 F_CODIGO_AFERICAO BIGINT,
                                                                 OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS   BIGINT;
    -- Busca a quantidade de valores aferidos estão ativos nesta aferição
    QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                        FROM AFERICAO_VALORES
                                        WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                          AND COD_UNIDADE = F_COD_UNIDADE);

    -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
    PREFIXO_MENSAGEM_RETORNO TEXT;
BEGIN
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    IF NOT EXISTS(SELECT *
                  FROM AFERICAO_VALORES
                  WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                    AND COD_UNIDADE = F_COD_UNIDADE
                    AND COD_PNEU = F_CODIGO_PNEU)
    THEN
        RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Placa %, Pneu %
            e Código %', F_COD_UNIDADE, F_PLACA,  F_CODIGO_PNEU, F_CODIGO_AFERICAO;
    END IF;

    -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
    CASE QTD_VALORES_AFERICAO
        WHEN 1
            THEN
                -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
                PERFORM SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO);
                PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
        ELSE
            -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
            -- DELETA AFERIÇÃO.
            UPDATE AFERICAO_VALORES_DATA
            SET DELETADO           = TRUE,
                DATA_HORA_DELETADO = NOW(),
                PG_USERNAME_DELECAO            = SESSION_USER
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            IF (QTD_LINHAS_ATUALIZADAS <= 0)
            THEN
                RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %, Placa %,
                    Pneu % e Código %', F_COD_UNIDADE, F_PLACA, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
            END IF;

            -- DELETA AFERIÇÃO MANUTENÇÃO.
            -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
            UPDATE AFERICAO_MANUTENCAO_DATA
            SET DELETADO           = TRUE,
                DATA_HORA_DELETADO = NOW(),
                PG_USERNAME_DELECAO            = SESSION_USER
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
        END CASE;

    SELECT PREFIXO_MENSAGEM_RETORNO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DO PNEU: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_CODIGO_AFERICAO
    INTO AVISO_AFERICAO_VALOR_DELETADA;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--###########################   3 FUNC_AFERICAO_DELETA_SERVICO_AFERICAO         ########################################
--######################################################################################################################
--PL-2229
-- ATUALIZA TABELA AFERICAO_MANUTENCAO_DATA
-- TABELA JÁ ATUALIZADA

-- ATUALIZA FUNC_AFERICAO_DELETA_SERVICO_AFERICAO
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
                                                                         F_COD_UNIDADE BIGINT,
                                                                         F_COD_PNEU BIGINT,
                                                                         F_NUMERO_FOGO TEXT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_COD_SERVICO_AFERICAO BIGINT,
                                                                         F_TIPO_SERVICO_AFERICAO TEXT,
                                                                         OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_TIPO_SERVICO_AFERICAO TEXT := LOWER(F_TIPO_SERVICO_AFERICAO);
BEGIN
    --Garante integridade entre unidade e empresa
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);
    --Verifica se o pneu existe
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO);
    --Verifica se existe afericao
    IF NOT EXISTS(SELECT A.CODIGO
                  FROM AFERICAO A
                  WHERE A.CODIGO = F_CODIGO_AFERICAO
                    AND A.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Aferição de código % não existe para a unidade % - %', F_CODIGO_AFERICAO, F_COD_UNIDADE,
            (SELECT NOME
             FROM UNIDADE
             WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    --Verifica se existe serviço de afericao
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_SERVICO_AFERICAO
                    AND AM.COD_AFERICAO = F_CODIGO_AFERICAO
                    AND AM.COD_PNEU = F_COD_PNEU
                    AND AM.COD_UNIDADE = F_COD_UNIDADE
                    AND AM.TIPO_SERVICO = F_TIPO_SERVICO_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não existe serviço de aferição com código: %, do tipo: "%", código de aferição: %,
     e codigo de pneus: %
                      para a unidade % - %', F_COD_SERVICO_AFERICAO, F_TIPO_SERVICO_AFERICAO,
            F_CODIGO_AFERICAO, F_COD_PNEU, F_COD_UNIDADE, (SELECT NOME
                                                           FROM UNIDADE
                                                           WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    -- Deleta aferição manutenção.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_SERVICO_AFERICAO
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_PNEU = F_COD_PNEU;
    SELECT 'SERVIÇO DE AFERIÇÃO DELETADO: '
               || F_COD_SERVICO_AFERICAO
               || ', DO TIPO: '
               || F_TIPO_SERVICO_AFERICAO
               || ', CODIGO DE AFERIÇÃO: '
               || F_CODIGO_AFERICAO
               || ', CÓDIGO PNEU: '
               || F_COD_PNEU
               || ', UNIDADE: '
               || F_COD_UNIDADE
               || ' - '
               || (SELECT U.NOME
                   FROM UNIDADE U
                   WHERE U.CODIGO = F_COD_UNIDADE)
    INTO AVISO_SERVICO_AFERICAO_DELETADO;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--#################################   4 FUNC_CARGOS_DELETA_CARGO      ##################################################
--######################################################################################################################
-- ATUALIZA TABELA FUNCAO_DATA
ALTER TABLE FUNCAO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE FUNCAO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE FUNCAO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE FUNCAO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_CARGOS_DELETA_CARGO
CREATE OR REPLACE FUNCTION FUNC_CARGOS_DELETA_CARGO(F_COD_EMPRESA BIGINT,
                                                    F_COD_CARGO BIGINT,
                                                    F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
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
                        'Erro ao deletar, possivelmente este cargo já foi deletado');
    END IF;

    IF ((SELECT COUNT(CODIGO)
         FROM COLABORADOR
         WHERE COD_EMPRESA = F_COD_EMPRESA
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
        PG_USERNAME_DELECAO    = SESSION_USER,
        COD_COLABORADOR_UPDATE = F_COD_COLABORADOR_UPDATE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND CODIGO = F_COD_CARGO;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao deletar o cargo, tente novamente');
    END IF;

    RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################  5 FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS   ###############################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_DATA
ALTER TABLE CHECKLIST_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_DATA
ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_ORDEM_SERVICO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_CHECKLIST BIGINT,
                                                                        F_PLACA TEXT,
                                                                        F_CPF_COLABORADOR BIGINT,
                                                                        OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    -- Deleta checklist.
    UPDATE CHECKLIST_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CPF_COLABORADOR = F_CPF_COLABORADOR;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist de código: % e Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta O.S.
    UPDATE CHECKLIST_ORDEM_SERVICO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_CHECKLIST = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                            FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
                                            WHERE COSD.COD_CHECKLIST = F_COD_CHECKLIST
                                              AND COSD.COD_UNIDADE = F_COD_UNIDADE) > 0))
    THEN
        RAISE EXCEPTION 'Erro ao deletar O.S. do checklist de código: % e Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta itens da O.S.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_OS =
          (SELECT COSD.CODIGO
           FROM CHECKLIST_ORDEM_SERVICO_DATA COSD
           WHERE COSD.COD_CHECKLIST = F_COD_CHECKLIST
             AND COSD.COD_UNIDADE = F_COD_UNIDADE);

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- Se tem O.S. então também deverá ter itens, por isso a verificação serve.
    IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                            FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                            WHERE COS.COD_CHECKLIST = F_COD_CHECKLIST
                                              AND COS.COD_UNIDADE = F_COD_UNIDADE) > 0))
    THEN
        RAISE EXCEPTION 'Erro ao deletar itens da O.S. do checklist de código: % e Unidade: %', F_COD_CHECKLIST,
            F_COD_UNIDADE;
    END IF;

    SELECT 'CHECKLIST DELETADO: '
               || F_COD_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   6 FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST    ###########################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_MODELO_DATA
ALTER TABLE CHECKLIST_MODELO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_MODELO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_MODELO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_MODELO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA CHECKLIST_PERGUNTAS_DATA
ALTER TABLE CHECKLIST_PERGUNTAS_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_PERGUNTAS_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_PERGUNTAS_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_PERGUNTAS_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- ATUALIZA TABELA CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO_CHECKLIST BIGINT,
                                                                          F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
                                                                              BOOLEAN DEFAULT FALSE,
                                                                          OUT AVISO_MODELO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    CHECKLIST              RECORD;
BEGIN

    IF F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
    THEN
        FOR CHECKLIST IN SELECT C.COD_UNIDADE,
                                C.CODIGO
                         FROM CHECKLIST C
                         WHERE C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
                           AND C.COD_UNIDADE = F_COD_UNIDADE
            LOOP
                PERFORM FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE := CHECKLIST.COD_UNIDADE,
                                                             F_COD_CHECKLIST := CHECKLIST.CODIGO);
            END LOOP;
    END IF;

    -- Deleta modelo de checklist.
    UPDATE CHECKLIST_MODELO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta pergundas do modelo de checklist.
    UPDATE CHECKLIST_PERGUNTAS_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as perguntas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta as alternativas das pergundas do modelo de checklist.
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as alternativas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- As únicas coisas que deletamos de fato são os vínculos de cargos e tipos de veículos, assim um modelo marcado
    -- como "deletado" não fica com vínculos que podem bloquear outras operações do BD.
    DELETE
    FROM CHECKLIST_MODELO_FUNCAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST;
    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_MODELO = F_COD_MODELO_CHECKLIST;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_MODELO_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || F_IF(F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO TAMBÉM FORAM DELETADOS.' :: TEXT,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO NÃO FORAM DELETADOS.' :: TEXT)
    INTO AVISO_MODELO_CHECKLIST_DELETADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   7 FUNC_CHECKLIST_DELETE_CHECKLIST_RESPOSTAS_CHECKLIST    ##############################
--######################################################################################################################



                                   --#########     NÃO ENCONTRADA         ###########--



--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   8 FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST   #############################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_MODELO_DATA
-- JÁ POSSUI TABELAS ATUALIZADAS

-- CRIA FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                 F_COD_OS BIGINT,
                                                                 F_COD_CHECKLIST BIGINT,
                                                                 OUT AVISO_CHECKLIST_OS_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    --VERIFICA SE EXISTE UNIDADE
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    --VERIFICA A EXISTÊNCIA DA ORDEM DE SERVIÇO DE ACORDO COM A CHAVE COMPOSTA RECEBIDA POR PARÂMETRO:
    IF NOT EXISTS(
            SELECT *
            FROM CHECKLIST_ORDEM_SERVICO_DATA
            WHERE CODIGO = F_COD_OS
              AND COD_UNIDADE = F_COD_UNIDADE
              AND COD_CHECKLIST = F_COD_CHECKLIST
        )
    THEN
        RAISE EXCEPTION 'ORDEM DE SERVIÇO COM CÓDIGO: %, UNIDADE: %, CÓDIGO DE CHECKLIST: % NÃO ENCONTRADO',
            F_COD_OS, F_COD_UNIDADE, F_COD_CHECKLIST;
    END IF;

    --DELETA ITEM ORDEM SERVIÇO:
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE COD_OS = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ITEM DA ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: % E CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    --DELETA OS:
    UPDATE CHECKLIST_ORDEM_SERVICO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST = F_COD_CHECKLIST;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: %, CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    SELECT 'DELEÇÃO DA OS: '
               || F_COD_OS
               || ', CÓDIGO CHECKLIST'
               || F_COD_CHECKLIST
               || ', CÓDIGO UNIDADE: '
               || F_COD_UNIDADE
               || ' REALIZADO COM SUCESSO.'
    INTO AVISO_CHECKLIST_OS_DELETADO;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--##############################   9 FUNC_COLABORADOR_DELETA_COLABORADOR     ###########################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
ALTER TABLE COLABORADOR_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE COLABORADOR_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE COLABORADOR_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE COLABORADOR_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_COLABORADOR_DELETA_COLABORADOR
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_COD_UNIDADE BIGINT,
                                                                       F_COD_COLABORADOR BIGINT,
                                                                       F_CPF BIGINT,
                                                                       OUT AVISO_COLABORADOR_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

    IF ((SELECT COUNT(codigo)
         FROM COLABORADOR_DATA
         WHERE CODIGO = F_COD_COLABORADOR
           AND COD_UNIDADE = F_COD_UNIDADE
           AND CPF = F_CPF) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum colaborador encontrado com estes parâmetros: Código %, CPF % e Unidade %',
            F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
    END IF;

    -- Deleta colaborador.
    UPDATE COLABORADOR_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CPF = F_CPF;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %',
            F_COD_COLABORADOR, F_CPF, F_COD_UNIDADE;
    END IF;

    SELECT 'COLABORADOR DELETADO: '
               || F_COD_COLABORADOR
               || ', CPF: '
               || F_CPF
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_COLABORADOR_DELETADO;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--##################################   10 FUNC_PNEU_DELETA_PNEU     ####################################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
ALTER TABLE PNEU_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE PNEU_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE PNEU_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE PNEU_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_PNEU_DELETA_PNEU
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
                                                 F_CODIGO BIGINT,
                                                 F_CODIGO_CLIENTE TEXT,
                                                 OUT AVISO_PNEU_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    F_STATUS_PNEU_ANALISE  TEXT := 'ANALISE';
BEGIN

    -- Verifica se o pneu existe.
    IF ((SELECT COUNT(CODIGO)
         FROM PNEU_DATA
         WHERE CODIGO = F_CODIGO
           AND COD_UNIDADE = F_COD_UNIDADE
           AND CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está aplicado.
    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_CODIGO
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em análise.
    IF ((SELECT COUNT(codigo)
         FROM PNEU_DATA
         WHERE CODIGO = F_CODIGO
           AND COD_UNIDADE = F_COD_UNIDADE
           AND CODIGO_CLIENTE = F_CODIGO_CLIENTE
           AND STATUS = F_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Deleta pneu.
    UPDATE PNEU_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_CODIGO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            F_CODIGO, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    SELECT 'PNEU DELETADO: '
               || F_CODIGO
               || ', CÓDIGO DO CLIENTE: '
               || F_CODIGO_CLIENTE
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_PNEU_DELETADO;
END
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--###############################   11 FUNC_VEICULO_DELETA_VEICULO    ##################################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
ALTER TABLE VEICULO_DATA
    ADD COLUMN PG_USERNAME_DELECAO TEXT;

ALTER TABLE VEICULO_DATA DROP CONSTRAINT DELETADO_CHECK_DATA_HORA_DELETADO;

UPDATE VEICULO_DATA
SET PG_USERNAME_DELECAO = 'nao_coletado'
WHERE DELETADO IS TRUE;

ALTER TABLE VEICULO_DATA
  ADD CONSTRAINT DELETADO_CHECK  CHECK(
      (NOT DELETADO)
          OR (DATA_HORA_DELETADO IS NOT NULL AND PG_USERNAME_DELECAO IS NOT NULL) );

-- CRIA FUNC_VEICULO_DELETA_VEICULO
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
                                                               F_COD_VEICULO BIGINT,
                                                               F_PLACA TEXT,
                                                               OUT AVISO_VEICULO_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

    IF ((SELECT COUNT(CODIGO)
         FROM VEICULO_DATA
         WHERE CODIGO = F_COD_VEICULO
           AND COD_UNIDADE = F_COD_UNIDADE
           AND PLACA = F_PLACA) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum veículo encontrado com estes parâmetros: Código %, Placa % e Unidade %',
            F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
    END IF;

    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.PLACA = F_PLACA
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O veículo de placa % não pode ser deletado pois possui pneus aplicados', F_PLACA;
    END IF;

    -- Deleta veículo.
    UPDATE VEICULO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW(),
        PG_USERNAME_DELECAO            = SESSION_USER
    WHERE CODIGO = F_COD_VEICULO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA = F_PLACA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o veículo de código: %, placa: % e Unidade: %',
            F_COD_VEICULO, F_PLACA, F_COD_UNIDADE;
    END IF;

    SELECT 'VEÍCULO DELETADO: '
               || F_COD_VEICULO
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_VEICULO_DELETADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   12 FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO     #############################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
-- TABELAS JÁ ESTÃO ATUALIZADAS

-- CRIA FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(
                                        F_COD_VEICULO BIGINT,
                                        F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
                                        F_DATA_HORA_REALIZACAO_TRANSFERENCIA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_INSERTS            BIGINT;
    QTD_UPDATES            BIGINT;
    F_STATUS_OS_ABERTA     TEXT := 'A';
    F_STATUS_OS_FECHADA    TEXT := 'F';
    F_STATUS_ITEM_PENDENTE TEXT := 'P';
    F_PLACA_VEICULO        TEXT := (SELECT V.PLACA
                                    FROM VEICULO V
                                    WHERE V.CODIGO = F_COD_VEICULO);
    F_OS                   CHECKLIST_ORDEM_SERVICO%ROWTYPE;
BEGIN
    FOR F_OS IN
        SELECT COS.CODIGO_PROLOG,
               COS.CODIGO,
               COS.COD_UNIDADE,
               COS.COD_CHECKLIST
            -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar OSs deletadas e não resolvidos.
        FROM CHECKLIST_ORDEM_SERVICO COS
                 JOIN CHECKLIST C ON C.CODIGO = COS.cod_checklist
        WHERE COS.STATUS = F_STATUS_OS_ABERTA
          AND C.PLACA_VEICULO = F_PLACA_VEICULO
        LOOP
            -- Copia os itens da OS.
            INSERT INTO CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA (
                COD_ITEM_OS_PROLOG)
            SELECT COSI.CODIGO
                   -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar itens já deletados e
                   -- não resolvidos.
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = F_OS.CODIGO
              AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
              AND COSI.STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE;

            GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

            -- Deleta os itens da OS.
            UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
            SET DELETADO            = TRUE,
                PG_USERNAME_DELECAO = SESSION_USER,
                DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
            WHERE COD_OS = F_OS.CODIGO
              AND COD_UNIDADE = F_OS.COD_UNIDADE
              AND STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE
              AND DELETADO = FALSE;

            GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

            IF QTD_INSERTS <> QTD_UPDATES
            THEN
                RAISE EXCEPTION
                    'Erro ao deletar os itens de O.S. de checklist na transferência de veículos. Rollback necessário!
                    __INSERTS: % UPDATES: %__', QTD_INSERTS, QTD_UPDATES;
            END IF;

            IF ((SELECT COUNT(COSI.CODIGO)
                 FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                 WHERE COSI.COD_OS = F_OS.CODIGO
                   AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
                   AND COSI.DELETADO = FALSE) > 0)
            THEN
                -- Se entrou aqui siginifca que a OS não tem mais itens em aberto, ela possuia alguns fechados e
                -- outros em aberto
                -- mas nós acabamos de deletar os que estavam em aberto.
                -- Por isso, precisamos fechar essa OS.
                UPDATE
                    CHECKLIST_ORDEM_SERVICO_DATA
                SET STATUS               = F_STATUS_OS_FECHADA,
                    PG_USERNAME_DELECAO  = SESSION_USER,
                    DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
                                            FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                                            WHERE COSI.COD_OS = F_OS.CODIGO
                                              AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
                                              AND COSI.DELETADO = FALSE)
                WHERE CODIGO_PROLOG = F_OS.CODIGO_PROLOG
                  AND CODIGO = F_OS.CODIGO
                  AND COD_UNIDADE = F_OS.COD_UNIDADE
                  AND COD_CHECKLIST = F_OS.COD_CHECKLIST
                  AND DELETADO = FALSE;
            ELSE
                -- Se entrou aqui siginifica que nós deletamos todos os itens da OS.
                -- Por isso, precisamos copiar a OS para a tabela de vínculo como deletada por transferência e
                -- depois deletá-la.

                -- Copia a OS.
                INSERT INTO CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA (COD_OS_PROLOG,
                                                                            COD_VEICULO_TRANSFERENCIA_INFORMACOES)
                SELECT F_OS.CODIGO_PROLOG,
                       F_COD_TRANSFERENCIA_VEICULO_INFORMACOES;

                GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

                -- Deleta a OS copiada.
                UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET DELETADO            = TRUE,
                    PG_USERNAME_DELECAO = SESSION_USER,
                    DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
                WHERE CODIGO_PROLOG = F_OS.CODIGO_PROLOG
                  AND CODIGO = F_OS.CODIGO
                  AND COD_UNIDADE = F_OS.COD_UNIDADE
                  AND COD_CHECKLIST = F_OS.COD_CHECKLIST
                  AND DELETADO = FALSE;

                GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

                IF QTD_INSERTS <> QTD_UPDATES
                THEN
                    RAISE EXCEPTION
                        'Erro ao deletar as OSs de checklist na transferência de veículos. Rollback necessário!
                        __INSERTS: % UPDATES: %__', QTD_INSERTS, QTD_UPDATES;
                END IF;
            END IF;
        END LOOP;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--############################   13 FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU    #################################
--######################################################################################################################
-- ATUALIZA TABELA CHECKLIST_ORDEM_SERVICO_ITENS_DATA
-- TABELA JÁ ATUALIZADA

-- CRIA FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU(
                                            F_COD_VEICULO BIGINT,
                                            F_COD_PNEU BIGINT,
                                            F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
                                            F_DATA_HORA_REALIZACAO_TRANSFERENCIA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_INSERTS     BIGINT;
    QTD_UPDATES     BIGINT;
    F_PLACA_VEICULO TEXT := (SELECT V.PLACA
                             FROM VEICULO V
                             WHERE V.CODIGO = F_COD_VEICULO);
BEGIN
    INSERT INTO AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA (COD_SERVICO,
                                                                    COD_VEICULO_TRANSFERENCIA_INFORMACOES)
    SELECT AM.CODIGO,
           F_COD_TRANSFERENCIA_VEICULO_INFORMACOES
           -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    FROM AFERICAO_MANUTENCAO AM
             JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
    WHERE A.PLACA_VEICULO = F_PLACA_VEICULO
      AND AM.COD_PNEU = F_COD_PNEU
      AND AM.DATA_HORA_RESOLUCAO IS NULL
      AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

    GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        PG_USERNAME_DELECAO = SESSION_USER,
        DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
    WHERE COD_PNEU = F_COD_PNEU
      AND DELETADO = FALSE
      AND DATA_HORA_RESOLUCAO IS NULL
      AND (FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL);

    GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

    -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
    -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
    -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo
    -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
    -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
    -- Porém, podemos ter uma inconsistência no BD.
    -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
    -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
    IF QTD_INSERTS <> QTD_UPDATES
    THEN
        RAISE EXCEPTION 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################ ALTERA COLUNA NOME DA TABELA CHECKLIST_MODELO_DATA ##################################
--######################################################################################################################
--######################################################################################################################
--PL-2140
​
-- DROP VIEW ESTRATIFICACAO_OS
DROP VIEW ESTRATIFICACAO_OS;
​
-- DROP VIEW CHECKLIST
DROP VIEW CHECKLIST;
​
-- DROP VIEW CHECKLIST_MODELO
DROP VIEW CHECKLIST_MODELO;
​
-- ATUALIZA MODELOS PARA NOMES DIFERENTES
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'TRUCK 1' WHERE CODIGO = 1;
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'TRUCK 2' WHERE CODIGO = 108;
​
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'AMBEV_DISTRIBUICAO 1' WHERE CODIGO = 296;
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'AMBEV_DISTRIBUICAO 2' WHERE CODIGO = 503;
​
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'BLITZ SEMANAL 1' WHERE CODIGO = 224;
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'BLITZ SEMANAL 2' WHERE CODIGO = 225;
​
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'CHECK LIST VUC 1' WHERE CODIGO = 162;
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'CHECK LIST VUC 2' WHERE CODIGO = 261;

UPDATE CHECKLIST_MODELO_DATA SET NOME = 'MERCEDES BENZ TRUCK 1' WHERE codigo = 525;
UPDATE CHECKLIST_MODELO_DATA SET NOME = 'MERCEDES BENZ TRUCK 2' WHERE codigo = 526;

UPDATE public.checklist_modelo_data SET nome = 'Check List Caminhão 1' WHERE codigo = 499;
UPDATE public.checklist_modelo_data SET nome = 'Check List Caminhão 2' WHERE codigo = 517;
​
-- REALIZA ALTERAÇÕES NA COLUNA NOME
ALTER TABLE CHECKLIST_MODELO_DATA ALTER COLUMN NOME TYPE CITEXT USING NOME::CITEXT;
​
CREATE UNIQUE INDEX CHECKLIST_MODELO_DATA_NOME_INDEX
  ON CHECKLIST_MODELO_DATA (COD_UNIDADE, NOME) WHERE STATUS_ATIVO AND NOT DELETADO;
​
-- RECRIA VIEW CHECKLIST
CREATE OR REPLACE VIEW CHECKLIST AS
SELECT C.COD_UNIDADE,
       C.COD_CHECKLIST_MODELO,
       C.CODIGO,
       C.DATA_HORA,
       C.DATA_HORA_IMPORTADO_PROLOG,
       C.CPF_COLABORADOR,
       C.PLACA_VEICULO,
       C.TIPO,
       C.TEMPO_REALIZACAO,
       C.KM_VEICULO,
       C.DATA_HORA_SINCRONIZACAO,
       C.FONTE_DATA_HORA_REALIZACAO,
       C.VERSAO_APP_MOMENTO_REALIZACAO,
       C.VERSAO_APP_MOMENTO_SINCRONIZACAO,
       C.DEVICE_ID,
       C.DEVICE_IMEI,
       C.DEVICE_UPTIME_REALIZACAO_MILLIS,
       C.DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
       C.FOI_OFFLINE,
       C.TOTAL_PERGUNTAS_OK,
       C.TOTAL_PERGUNTAS_NOK,
       C.TOTAL_ALTERNATIVAS_OK,
       C.TOTAL_ALTERNATIVAS_NOK
FROM CHECKLIST_DATA C
WHERE C.DELETADO = FALSE;
​
-- RECRIA A VIEW ESTRATIFICACAO_OS
CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
  SELECT
    COS.CODIGO                                                       AS COD_OS,
    REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
    C.PLACA_VEICULO,
    C.KM_VEICULO                                                     AS KM,
    TIMEZONE(TZ_UNIDADE(COS.COD_UNIDADE), C.DATA_HORA)               AS DATA_HORA,
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
    END                                                              AS PRIORIDADE_ORDEM,
    CAP.CODIGO                                                       AS COD_ALTERNATIVA,
    CAP.ALTERNATIVA,
    PRIO.PRAZO,
    CR.RESPOSTA,
    V.COD_TIPO,
    COS.COD_UNIDADE,
    COS.STATUS                                                       AS STATUS_OS,
    COS.COD_CHECKLIST,
    TZ_UNIDADE(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
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
​
COMMENT ON VIEW ESTRATIFICACAO_OS
IS 'View que compila as informações das os e seus itens';
​
-- RECRIA A VIEW CHECKLIST_MODELO
CREATE OR REPLACE VIEW CHECKLIST_MODELO AS
  SELECT
    CM.COD_UNIDADE,
    CM.CODIGO,
    CM.NOME,
    CM.STATUS_ATIVO
  FROM CHECKLIST_MODELO_DATA CM
  WHERE CM.DELETADO = FALSE;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################  CRIA FUNCTION FUNC_CHECKLIST_UPDATE_STATUS_MODELO  #####################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- Verifica se já existe um modelo ativo com mesmo nome do que está se tentando ativar e na mesma unidade. Se existir,
-- retorna um erro. Se não, deixa o update acontecer.
--
-- Histórico:
-- 2019-09-13 -> Function criada (Natan - PL-2140).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_UPDATE_STATUS_MODELO(F_COD_UNIDADE BIGINT,
                                                               F_COD_MODELO BIGINT,
                                                               F_STATUS_ATIVO BOOLEAN)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_NOME_MODELO_CHECKLIST TEXT := (SELECT NOME
                                     FROM CHECKLIST_MODELO_DATA
                                     WHERE COD_UNIDADE = F_COD_UNIDADE
                                       AND CODIGO = F_COD_MODELO);
BEGIN
    -- VERIFICA SE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Se estamos ativando o modelo e existe outro na mesma unidade, de mesmo nome e já ativo, lançamos um erro.
    IF F_STATUS_ATIVO AND (SELECT EXISTS(SELECT CM.CODIGO
                                         FROM CHECKLIST_MODELO CM
                                         WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                           AND LOWER(CM.NOME) = LOWER(F_NOME_MODELO_CHECKLIST)
                                           AND CM.STATUS_ATIVO = TRUE))
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro! Já existe um modelo de checklist ativo com esse nome.');
    END IF;

    UPDATE CHECKLIST_MODELO_DATA
    SET STATUS_ATIVO = F_STATUS_ATIVO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_COD_MODELO;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o status do modelo de checklist % para %', F_COD_MODELO, F_STATUS_ATIVO;
    END IF;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################  CONSTRAINT UNIQUE PARA EVITAR DIMENSÕES DUPLICADAS  ####################################
--######################################################################################################################
--######################################################################################################################
-- PL-2316 - Já rodada em produção
-- UPDATE PNEU
-- SET
--   COD_DIMENSAO = 8
-- WHERE COD_DIMENSAO = 14;
--
-- DELETE FROM DIMENSAO_PNEU WHERE CODIGO = 14;
--
-- ALTER TABLE DIMENSAO_PNEU ADD CONSTRAINT UNIQUE_DIMENSAO_PNEU UNIQUE(ALTURA, LARGURA, ARO);
--######################################################################################################################
--######################################################################################################################

END TRANSACTION;