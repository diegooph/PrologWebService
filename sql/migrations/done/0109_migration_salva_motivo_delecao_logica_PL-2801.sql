-- PL-2801
-- Adiciona motivo de deleção nas tabelas de aferição
ALTER TABLE AFERICAO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE AFERICAO_MANUTENCAO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE AFERICAO_VALORES_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;


-- 2020-07-07 -> Adiciona motivo de deleção (thaisksf - PL-2801)
DROP FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE BIGINT,
    F_PLACA TEXT,
    F_CODIGO_AFERICAO BIGINT,
    OUT AVISO_AFERICAO_DELETADA TEXT);

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE BIGINT,
                                                                 F_PLACA TEXT,
                                                                 F_CODIGO_AFERICAO BIGINT,
                                                                 F_MOTIVO_DELECAO TEXT,
                                                                 OUT AVISO_AFERICAO_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    IF ((SELECT COUNT(CODIGO)
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
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CODIGO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO VALORES.
    UPDATE AFERICAO_VALORES_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
    IF ((V_QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
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
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
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


-- 2020-07-07 -> Adiciona motivo de deleção e corrige uso de placa. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
    F_PLACA TEXT,
    F_CODIGO_PNEU BIGINT,
    F_CODIGO_AFERICAO BIGINT,
    OUT AVISO_AFERICAO_VALOR_DELETADA TEXT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
                                                                         F_CODIGO_PNEU BIGINT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS   BIGINT;
    -- Busca a quantidade de valores aferidos estão ativos nesta aferição
    V_QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                          FROM AFERICAO_VALORES
                                          WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                            AND COD_UNIDADE = F_COD_UNIDADE);

    -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
    V_PREFIXO_MENSAGEM_RETORNO TEXT;
    V_PLACA                    TEXT   := (SELECT A.PLACA_VEICULO
                                          FROM AFERICAO A
                                          WHERE A.CODIGO = F_CODIGO_AFERICAO);
BEGIN
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    IF NOT EXISTS(SELECT *
                  FROM AFERICAO_VALORES
                  WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                    AND COD_UNIDADE = F_COD_UNIDADE
                    AND COD_PNEU = F_CODIGO_PNEU)
    THEN
        RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Pneu %
            e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
    END IF;

    -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
    CASE V_QTD_VALORES_AFERICAO
        WHEN 1
            THEN -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
            PERFORM SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE, V_PLACA, F_CODIGO_AFERICAO,
                                                          F_MOTIVO_DELECAO);
            V_PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
        ELSE -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
        -- DELETA AFERIÇÃO.
            UPDATE AFERICAO_VALORES_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
            THEN
                RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %,
                    Pneu % e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
            END IF;

            -- DELETA AFERIÇÃO MANUTENÇÃO.
            -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
            UPDATE AFERICAO_MANUTENCAO_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            V_PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
        END CASE;

    SELECT V_PREFIXO_MENSAGEM_RETORNO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ', CÓDIGO DO PNEU: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_CODIGO_AFERICAO
    INTO AVISO_AFERICAO_VALOR_DELETADA;
END
$$;

-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
    F_COD_UNIDADE BIGINT,
    F_COD_PNEU BIGINT,
    F_NUMERO_FOGO TEXT,
    F_CODIGO_AFERICAO BIGINT,
    F_COD_SERVICO_AFERICAO BIGINT,
    F_TIPO_SERVICO_AFERICAO TEXT,
    OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT);

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
                                                                         F_COD_UNIDADE BIGINT,
                                                                         F_COD_PNEU BIGINT,
                                                                         F_NUMERO_FOGO TEXT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_COD_SERVICO_AFERICAO BIGINT,
                                                                         F_TIPO_SERVICO_AFERICAO TEXT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_TIPO_SERVICO_AFERICAO TEXT := LOWER(F_TIPO_SERVICO_AFERICAO);
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
                    AND AM.TIPO_SERVICO = V_TIPO_SERVICO_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não existe serviço de aferição com código: %, do tipo: "%", código de aferição: %,
     e codigo de pneus: %
                      para a unidade % - %', F_COD_SERVICO_AFERICAO, V_TIPO_SERVICO_AFERICAO,
            F_CODIGO_AFERICAO, F_COD_PNEU, F_COD_UNIDADE, (SELECT NOME
                                                           FROM UNIDADE
                                                           WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    -- Deleta aferição manutenção.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_SERVICO_AFERICAO
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_PNEU = F_COD_PNEU;
    SELECT 'SERVIÇO DE AFERIÇÃO DELETADO: '
               || F_COD_SERVICO_AFERICAO
               || ', DO TIPO: '
               || V_TIPO_SERVICO_AFERICAO
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


-- Adiciona motivo de deleção nas tabelas de checklist.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE CHECKLIST_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE CHECKLIST_MODELO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;
ALTER TABLE CHECKLIST_PERGUNTAS_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;


-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
    F_COD_CHECKLIST BIGINT,
    F_PLACA TEXT,
    F_CPF_COLABORADOR BIGINT,
    OUT AVISO_CHECKLIST_DELETADO TEXT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_CHECKLIST BIGINT,
                                                                        F_PLACA TEXT,
                                                                        F_CPF_COLABORADOR BIGINT,
                                                                        F_MOTIVO_DELECAO TEXT,
                                                                        OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_OS_DELETADA BIGINT;
BEGIN
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE CODIGO = F_COD_CHECKLIST
                            AND COD_UNIDADE = F_COD_UNIDADE
                            AND PLACA_VEICULO = F_PLACA
                            AND CPF_COLABORADOR = F_CPF_COLABORADOR))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique!';
    END IF;

    -- Deleta checklist de forma lógica.
    UPDATE CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CPF_COLABORADOR = F_CPF_COLABORADOR
      AND DELETADO = FALSE;

    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist de código: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_CHECKLIST = F_COD_CHECKLIST
                        AND COD_UNIDADE = F_COD_UNIDADE))
    THEN
        -- Deleta lógicamente a O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_CHECKLIST = F_COD_CHECKLIST
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE
        RETURNING CODIGO INTO V_COD_OS_DELETADA;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. do checklist: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = V_COD_OS_DELETADA
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. do checklist: % da Unidade: %',
                F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST;
    END IF;

    SELECT 'CHECKLIST DELETADO: '
               || F_COD_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;

-- 2020-07-07 -> Function criada. (thaisksf - PL-2801).
-- Não adicionei o Security Definer pois ela é para ser usada apenas como chamada interna.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(F_COD_UNIDADE BIGINT,
                                                                                          F_COD_CHECKLIST_MODELO BIGINT,
                                                                                          F_MOTIVO_DELECAO TEXT,
                                                                                          OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CHECKLISTS   BIGINT[];
    V_COD_OS_DELETADAS BIGINT[];
BEGIN
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE COD_UNIDADE = F_COD_UNIDADE
                            AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique.';
    END IF;

    SELECT ARRAY_AGG(CODIGO)
    FROM CHECKLIST
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
    INTO V_COD_CHECKLISTS;


    -- Deleta checklist de forma lógica.
    UPDATE
        CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
      AND DELETADO = FALSE;
    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_UNIDADE = F_COD_UNIDADE
                        AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)))
    THEN

        WITH OS_DELETADA(COD_OS) AS (
            UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET DELETADO = TRUE,
                    DATA_HORA_DELETADO = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO = F_MOTIVO_DELECAO
                WHERE COD_UNIDADE = F_COD_UNIDADE
                    AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)
                    AND DELETADO = FALSE RETURNING CODIGO
        )
        SELECT ARRAY_AGG(COD_OS)
        FROM OS_DELETADA
        INTO V_COD_OS_DELETADAS;

        -- Deleta lógicamente a O.S.
        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = ANY (V_COD_OS_DELETADAS)
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. da Unidade: %, Modelo: %.',
                F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS)))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS);
    END IF;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_CHECKLIST_MODELO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;

-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(F_COD_UNIDADE BIGINT,
    F_COD_MODELO_CHECKLIST BIGINT,
    F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO BOOLEAN,
    OUT AVISO_MODELO_CHECKLIST_DELETADO TEXT);

-- Existia um erro nessa function, caso a condição F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO fosse true, a function
-- chamada não existe:
--
-- PERFORM FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE := CHECKLIST.COD_UNIDADE,
--                                              F_COD_CHECKLIST := CHECKLIST.CODIGO);
--
--[2020-07-07 11:17:27] [42883] ERROR:
-- function func_checklist_deleta_checklist_e_os(f_cod_unidade => bigint, f_cod_checklist => bigint) does not exist.
--
-- Portanto, criei uma nova function para essa deleção interna:
-- SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO.
-- Não adicionei o Security Definer pois ela é para ser usada apenas como chamada interna.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO_CHECKLIST BIGINT,
                                                                          F_MOTIVO_DELECAO TEXT,
                                                                          F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
                                                                              BOOLEAN DEFAULT FALSE,
                                                                          OUT AVISO_MODELO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN

    IF F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
    THEN
        PERFORM SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(
                F_COD_UNIDADE := F_COD_UNIDADE,
                F_COD_CHECKLIST_MODELO := F_COD_MODELO_CHECKLIST,
                F_MOTIVO_DELECAO := F_MOTIVO_DELECAO);
    END IF;

    -- Deleta modelo de checklist.
    UPDATE CHECKLIST_MODELO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta pergundas do modelo de checklist.
    UPDATE CHECKLIST_PERGUNTAS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as perguntas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta as alternativas das pergundas do modelo de checklist.
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
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

-- Adiciona motivo de deleção na tabela de pneu
ALTER TABLE PNEU_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;

-- Altera function.
-- 2020-07-07 -> Adiciona motivo de deleção (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
    F_CODIGO_PNEU BIGINT,
    F_CODIGO_CLIENTE TEXT,
    OUT AVISO_PNEU_DELETADO TEXT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
                                                         F_CODIGO_PNEU BIGINT,
                                                         F_CODIGO_CLIENTE TEXT,
                                                         F_MOTIVO_DELECAO TEXT,
                                                         OUT AVISO_PNEU_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_STATUS_PNEU_ANALISE CONSTANT   TEXT := 'ANALISE';
    V_QTD_LINHAS_ATUALIZADAS         BIGINT;
    V_COD_AFERICAO                   BIGINT[];
    V_COD_AFERICAO_FOREACH           BIGINT;
    V_QTD_AFERICAO_VALORES           BIGINT;
    V_QTD_AFERICAO_VALORES_DELETADOS BIGINT;
BEGIN

    -- Verifica se o pneu existe.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está aplicado.
    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_CODIGO_PNEU
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em análise.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE
           AND P.STATUS = V_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se pneu é integrado
    IF EXISTS(SELECT IPC.COD_PNEU_CADASTRO_PROLOG
              FROM INTEGRACAO.PNEU_CADASTRADO IPC
              WHERE IPC.COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
                AND IPC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE)
    THEN
        -- Deleta Pneu (Não temos deleção lógica)
        DELETE
        FROM INTEGRACAO.PNEU_CADASTRADO
        WHERE COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE;
    END IF;

    -- Deleta pneu Prolog.
    UPDATE PNEU_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_CODIGO_PNEU
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em afericao_manutencao_data.
    IF (SELECT EXISTS(SELECT AM.COD_AFERICAO
                      FROM AFERICAO_MANUTENCAO_DATA AM
                      WHERE AM.COD_PNEU = F_CODIGO_PNEU
                        AND AM.COD_UNIDADE = F_COD_UNIDADE
                        AND AM.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código Cliente % e Unidade % '
                'em afericao_manutencao_data', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Verifica se o pneu está em afericao_valores_data.
    IF (SELECT EXISTS(SELECT AV.COD_AFERICAO
                      FROM AFERICAO_VALORES_DATA AV
                      WHERE AV.COD_PNEU = F_CODIGO_PNEU
                        AND AV.COD_UNIDADE = F_COD_UNIDADE
                        AND AV.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade % em afericao_valores_data',
                F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    --Busca todos os cod_afericao deletados a partir do pneu.
    SELECT ARRAY_AGG(AV.COD_AFERICAO)
    FROM AFERICAO_VALORES_DATA AV
    WHERE AV.COD_PNEU = F_CODIGO_PNEU
      AND AV.COD_UNIDADE = F_COD_UNIDADE
      AND AV.DELETADO IS TRUE
    INTO V_COD_AFERICAO;

    -- Verifica se algum valor foi deletado em afericao_valores_data.
    IF (V_COD_AFERICAO IS NOT NULL AND ARRAY_LENGTH(V_COD_AFERICAO, 1) > 0)
    THEN
        -- Iteração com cada cod_afericao deletado em afericao_valores_data.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_COD_AFERICAO
            LOOP
                -- Coleta a quantidade de aferições em afericao_valores_data.
                V_QTD_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                          FROM AFERICAO_VALORES_DATA AVD
                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                -- Coleta a quantidade de aferições deletadas em afericao_valores_data.
                V_QTD_AFERICAO_VALORES_DELETADOS = (SELECT COUNT(AVD.COD_AFERICAO)
                                                    FROM AFERICAO_VALORES_DATA AVD
                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                      AND AVD.DELETADO IS TRUE);

                -- Verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                IF (V_QTD_AFERICAO_VALORES = V_QTD_AFERICAO_VALORES_DELETADOS)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER,
                        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
                    WHERE CODIGO = V_COD_AFERICAO_FOREACH;

                    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

                    -- Garante que a deleção foi realizada.
                    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
                    THEN
                        RAISE EXCEPTION 'Erro ao deletar aferição com Código: %, Unidade: %',
                            V_COD_AFERICAO_FOREACH, F_COD_UNIDADE;
                    END IF;
                END IF;
            END LOOP;
    END IF;

    SELECT 'PNEU DELETADO: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DO CLIENTE: '
               || F_CODIGO_CLIENTE
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_PNEU_DELETADO;
END
$$;

-- Adiciona motivo de deleção na tabela de veículo
ALTER TABLE VEICULO_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;

-- Altera function.
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
    F_PLACA VARCHAR,
    OUT DEPENDENCIAS_DELETADAS TEXT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
                                                               F_PLACA VARCHAR(255),
                                                               F_MOTIVO_DELECAO TEXT,
                                                               OUT DEPENDENCIAS_DELETADAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_CODIGO_LOOP                   BIGINT;
    V_LISTA_COD_AFERICAO_PLACA      BIGINT[];
    V_LISTA_COD_CHECK_PLACA         BIGINT[];
    V_LISTA_COD_PROLOG_DELETADO_COS BIGINT[];
    V_NOME_EMPRESA                  VARCHAR(255) := (SELECT E.NOME
                                                     FROM EMPRESA E
                                                     WHERE E.CODIGO =
                                                           (SELECT U.COD_EMPRESA
                                                            FROM UNIDADE U
                                                            WHERE U.CODIGO = F_COD_UNIDADE));
    V_NOME_UNIDADE                  VARCHAR(255) := (SELECT U.NOME
                                                     FROM UNIDADE U
                                                     WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    -- VERIFICA SE UNIDADE EXISTE;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO EXISTE.
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);

    -- VERIFICA SE VEÍCULO POSSUI PNEU APLICADOS.
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA AND VP.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', F_PLACA;
    END IF;

    -- VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO FROM AFERICAO_DATA A WHERE A.PLACA_VEICULO = F_PLACA)
    THEN
        -- COLETAMOS TODOS OS COD_AFERICAO QUE A PLACA POSSUI.
        SELECT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO_DATA A
        WHERE A.PLACA_VEICULO = F_PLACA
        INTO V_LISTA_COD_AFERICAO_PLACA;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO.
        UPDATE AFERICAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_AFERICAO_PLACA);
    END IF;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    IF EXISTS(SELECT C.PLACA_VEICULO FROM CHECKLIST_DATA C WHERE C.PLACA_VEICULO = F_PLACA)
    THEN
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        SELECT ARRAY_AGG(C.CODIGO)
        FROM CHECKLIST_DATA C
        WHERE C.PLACA_VEICULO = F_PLACA
        INTO V_LISTA_COD_CHECK_PLACA;

        -- DELETA COD_CHECK EM COS.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA);

        -- BUSCO OS CODIGO PROLOG DELETADOS EM COS.
        SELECT ARRAY_AGG(CODIGO_PROLOG)
        FROM CHECKLIST_ORDEM_SERVICO_DATA
        WHERE COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA)
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO IS TRUE
        INTO V_LISTA_COD_PROLOG_DELETADO_COS;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        FOREACH V_CODIGO_LOOP IN ARRAY V_LISTA_COD_PROLOG_DELETADO_COS
            LOOP
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET DELETADO            = TRUE,
                    DATA_HORA_DELETADO  = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO      = F_MOTIVO_DELECAO
                WHERE DELETADO = FALSE
                  AND (COD_OS, COD_UNIDADE) = (SELECT COS.CODIGO, COS.COD_UNIDADE
                                               FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                               WHERE COS.CODIGO_PROLOG = V_CODIGO_LOOP);
            END LOOP;

        -- DELETA TODOS CHECKLIST DA PLACA.
        UPDATE CHECKLIST_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE PLACA_VEICULO = F_PLACA
          AND DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_CHECK_PLACA);
    END IF;

    -- Verifica se a placa é integrada.
    IF EXISTS(SELECT IVC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO IVC
              WHERE IVC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE
                AND IVC.PLACA_VEICULO_CADASTRO = F_PLACA)
    THEN
        -- Realiza a deleção da placa. (Não possuímos deleção lógica)
        DELETE
        FROM INTEGRACAO.VEICULO_CADASTRADO
        WHERE COD_UNIDADE_CADASTRO = F_COD_UNIDADE
          AND PLACA_VEICULO_CADASTRO = F_PLACA;
    END IF;

    -- REALIZA DELEÇÃO DA PLACA.
    UPDATE VEICULO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA = F_PLACA
      AND DELETADO = FALSE;

    -- MENSAGEM DE SUCESSO.
    SELECT 'Veículo deletado junto com suas dependências. Veículo: '
               || F_PLACA
               || ', Empresa: '
               || V_NOME_EMPRESA
               || ', Unidade: '
               || V_NOME_UNIDADE
    INTO DEPENDENCIAS_DELETADAS;
END;
$$;

-- Adiciona motivo de deleção na tabela de colaborador.
ALTER TABLE COLABORADOR_DATA
    ADD COLUMN MOTIVO_DELECAO TEXT;

-- Altera function.
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
DROP FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_CPF_COLABORADOR BIGINT,
    F_COD_COLABORADOR BIGINT,
    F_COD_UNIDADE BIGINT,
    OUT AVISO_COLABORADOR_DELETADO TEXT);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_CPF_COLABORADOR BIGINT,
                                                                       F_COD_COLABORADOR BIGINT,
                                                                       F_COD_UNIDADE BIGINT,
                                                                       F_MOTIVO_DELECAO TEXT,
                                                                       OUT AVISO_COLABORADOR_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS BIGINT;
    V_COD_EMPRESA            BIGINT := (SELECT U.COD_EMPRESA
                                        FROM UNIDADE U
                                        WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    -- Verifica integridade de colaborador com unidade.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(V_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Deleta colaborador.
    UPDATE COLABORADOR_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CPF = F_CPF_COLABORADOR;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %',
            F_COD_COLABORADOR, F_CPF_COLABORADOR, F_COD_UNIDADE;
    END IF;

    -- Desloga colaborador.
    DELETE FROM TOKEN_AUTENTICACAO WHERE COD_COLABORADOR = F_COD_COLABORADOR AND CPF_COLABORADOR = F_CPF_COLABORADOR;

    SELECT 'COLABORADOR COM CÓDIGO: '
               || F_COD_COLABORADOR
               || ', CPF: '
               || F_CPF_COLABORADOR
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ' DELETADO COM SUCESSO!'
    INTO AVISO_COLABORADOR_DELETADO;
END
$$;