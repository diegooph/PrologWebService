-- PL-2569

-- TORNA DEFERRABLE O CÓDIGO DO VEÍCULO EM SOCORRO_ROTA_ABERTURA.
ALTER TABLE SOCORRO_ROTA_ABERTURA
    DROP CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_VEICULO_CODIGO;
ALTER TABLE SOCORRO_ROTA_ABERTURA
    ADD CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_VEICULO_CODIGO
        FOREIGN KEY (COD_VEICULO_PROBLEMA)
            REFERENCES VEICULO_DATA (CODIGO) DEFERRABLE INITIALLY DEFERRED;

-- TORNA DEFERRABLE O CÓDIGO DO VEÍCULO EM VEICULO_TRANSFERENCIA_INFORMACOES.
ALTER TABLE VEICULO_TRANSFERENCIA_INFORMACOES
    DROP CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFORMACOES_VEICULO;
ALTER TABLE VEICULO_TRANSFERENCIA_INFORMACOES
    ADD CONSTRAINT FK_VEICULO_TRANSFERENCIA_INFORMACOES_VEICULO
        FOREIGN KEY (COD_VEICULO)
            REFERENCES VEICULO_DATA (CODIGO) DEFERRABLE INITIALLY DEFERRED;

-- TORNA DEFERRABLE O CÓDIGO DO VEÍCULO EM INTEGRACAO.VEICULO_CADASTRADO.
ALTER TABLE INTEGRACAO.VEICULO_CADASTRADO
    DROP CONSTRAINT FK_VEICULO_CADASTRO_VEICULO;
ALTER TABLE INTEGRACAO.VEICULO_CADASTRADO
    ADD CONSTRAINT FK_VEICULO_CADASTRO_VEICULO FOREIGN KEY (COD_VEICULO_CADASTRO_PROLOG)
        REFERENCES PUBLIC.VEICULO_DATA (CODIGO) DEFERRABLE INITIALLY DEFERRED;

-- CRIA FUNCTION PARA ALTERAR A PLACA.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_ALTERA_PLACA(F_COD_UNIDADE BIGINT,
                                                             F_COD_VEICULO_ANTIGO BIGINT,
                                                             F_PLACA_ANTIGA TEXT,
                                                             F_PLACA_NOVA TEXT,
                                                             F_FORCAR_ATUALIZACAO_PLACA_INTEGRACAO BOOLEAN DEFAULT FALSE,
                                                             OUT F_AVISO_PLACA_ALTERADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_KM                 BIGINT;
    V_STATUS_ATIVO       BOOLEAN;
    V_COD_TIPO           BIGINT;
    V_COD_MODELO         BIGINT;
    V_COD_EIXOS          BIGINT;
    V_DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE;
    V_COD_EMPRESA        BIGINT;
    V_COD_VEICULO_NOVO   BIGINT;
    V_QTD_INSERTS        BIGINT;
    V_QTD_UPDATES        BIGINT;
    V_QTD_DELETES        BIGINT;
BEGIN
    -- GARANTE QUE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO ANTIGO EXISTE.
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_ANTIGA);

    -- VERIFICA SE PLACA NOVA ESTÁ DISPONÍVEL.
    IF EXISTS(SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_NOVA)
    THEN
        RAISE EXCEPTION
            'A placa % já existe no banco.', F_PLACA_NOVA;
    END IF;

    -- VERIFICA SE A PLACA É DE INTEGRAÇÃO.
    IF EXISTS(SELECT VC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO VC
              WHERE VC.PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA)
    THEN
        -- VERIFICA SE DEVE ALTERAR PLACA EM INTEGRAÇÃO.
        IF (F_FORCAR_ATUALIZACAO_PLACA_INTEGRACAO IS FALSE)
        THEN
            RAISE EXCEPTION
                'A placa % pertence à integração. Para atualizar a mesma, deve-se passar TRUE como parâmetro',
                F_PLACA_ANTIGA;
        END IF;
    END IF;

    -- BUSCA INFORMAÇÕES DO VEÍCULO ANTIGO.
    SELECT V.KM,
           V.STATUS_ATIVO,
           V.COD_TIPO,
           V.COD_MODELO,
           V.COD_EIXOS,
           V.DATA_HORA_CADASTRO,
           V.COD_EMPRESA
    FROM VEICULO V
    WHERE V.PLACA = F_PLACA_ANTIGA
      AND V.CODIGO = F_COD_VEICULO_ANTIGO
      AND V.COD_UNIDADE = F_COD_UNIDADE
    INTO V_KM,
        V_STATUS_ATIVO,
        V_COD_TIPO,
        V_COD_MODELO,
        V_COD_EIXOS,
        V_DATA_HORA_CADASTRO,
        V_COD_EMPRESA;

    IF ((V_KM IS NULL) OR (V_STATUS_ATIVO IS NULL) OR (V_COD_TIPO IS NULL) OR (V_COD_MODELO IS NULL)
        OR (V_COD_EIXOS IS NULL) OR (V_COD_EMPRESA IS NULL))
    THEN
        RAISE EXCEPTION
            'Não foi possível buscar informações do veículo com placa %, código %', F_PLACA_ANTIGA, F_COD_VEICULO_ANTIGO;
    END IF;

    -- DUPLICA INFORMAÇÕES DO VEÍCULO ANTIGO PARA A PLACA NOVA.
    INSERT INTO VEICULO_DATA (PLACA,
                              COD_UNIDADE,
                              KM,
                              STATUS_ATIVO,
                              COD_TIPO,
                              COD_MODELO,
                              COD_EIXOS,
                              DATA_HORA_CADASTRO,
                              COD_UNIDADE_CADASTRO,
                              COD_EMPRESA)
    VALUES (F_PLACA_NOVA,
            F_COD_UNIDADE,
            V_KM,
            V_STATUS_ATIVO,
            V_COD_TIPO,
            V_COD_MODELO,
            V_COD_EIXOS,
            V_DATA_HORA_CADASTRO,
            F_COD_UNIDADE,
            V_COD_EMPRESA) RETURNING CODIGO INTO V_COD_VEICULO_NOVO;
    GET DIAGNOSTICS V_QTD_INSERTS = ROW_COUNT;
    IF (V_QTD_INSERTS IS NULL OR V_QTD_INSERTS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível modificar a placa para %', F_PLACA_NOVA;
    END IF;

    -- MODIFICA PLACA EM INTEGRAÇÃO VEICULO_CADASTRADO
    IF EXISTS(SELECT VC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO VC
              WHERE VC.PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA)
    THEN
        UPDATE INTEGRACAO.VEICULO_CADASTRADO
        SET PLACA_VEICULO_CADASTRO = F_PLACA_NOVA
        WHERE PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % na tabela de integração veiculo_cadastrado', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA EM VEICULO_PNEU.
    IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE VEICULO_PNEU SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % no vínculo de veículo pneu', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NA AFERIÇÃO.
    IF EXISTS(SELECT AD.PLACA_VEICULO FROM AFERICAO_DATA AD WHERE AD.PLACA_VEICULO = F_PLACA_ANTIGA)
    THEN
        UPDATE AFERICAO_DATA SET PLACA_VEICULO = F_PLACA_NOVA WHERE PLACA_VEICULO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % nas aferições', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NO CHECKLIST.
    IF EXISTS(SELECT CD.PLACA_VEICULO FROM CHECKLIST_DATA CD WHERE CD.PLACA_VEICULO = F_PLACA_ANTIGA)
    THEN
        UPDATE CHECKLIST_DATA SET PLACA_VEICULO = F_PLACA_NOVA WHERE PLACA_VEICULO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % nos checklists', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NAS MOVIMENTAÇÕES.
    IF EXISTS(SELECT MO.PLACA FROM MOVIMENTACAO_ORIGEM MO WHERE MO.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE MOVIMENTACAO_ORIGEM SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % em movimentacao origem', F_PLACA_NOVA;
        END IF;
    END IF;

    IF EXISTS(SELECT MD.PLACA FROM MOVIMENTACAO_DESTINO MD WHERE MD.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE MOVIMENTACAO_DESTINO SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % em movimentacao destino', F_PLACA_NOVA;
        END IF;
    END IF;

    -- DELETA PLACA ANTIGA.
    DELETE
    FROM VEICULO_DATA VD
    WHERE VD.COD_UNIDADE = F_COD_UNIDADE
      AND VD.PLACA = F_PLACA_ANTIGA;
    GET DIAGNOSTICS V_QTD_DELETES = ROW_COUNT;
    IF (V_QTD_DELETES IS NULL OR V_QTD_DELETES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possivel modificar a placa % para %.', F_PLACA_ANTIGA, F_PLACA_NOVA;
    END IF;

    -- ALTERA CÓDIGO DO VEÍCULO NOVO PARA CÓDIGO ANTIGO
    UPDATE VEICULO SET CODIGO = F_COD_VEICULO_ANTIGO WHERE CODIGO = V_COD_VEICULO_NOVO;
    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
    IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possivel o código da placa % para %.', F_PLACA_NOVA, F_COD_VEICULO_ANTIGO;
    ELSE
        SELECT 'A placa foi alterada de ' || F_PLACA_ANTIGA ||
               ' para ' || F_PLACA_NOVA || '.'
        INTO F_AVISO_PLACA_ALTERADA;
    END IF;
END;
$$;

-- DROPA FUNCTION DE INSERIR CHECKLIST.
DROP FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(BIGINT,
    BIGINT,
    BIGINT,
    TIMESTAMP WITH TIME ZONE,
    BIGINT,
    BIGINT,
    TEXT,
    CHAR,
    BIGINT,
    BIGINT,
    TIMESTAMP WITH TIME ZONE,
    TEXT,
    INTEGER,
    INTEGER,
    TEXT,
    TEXT,
    BIGINT,
    BIGINT,
    BOOLEAN,
    INTEGER,
    INTEGER,
    INTEGER,
    INTEGER);

-- RECRIA A FUNCTION PARA QUE ELA PEGUE A PLACA ATUAL DO VEÍCULO COM BASE NO CÓDIGO.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(F_COD_UNIDADE_CHECKLIST BIGINT,
                                                                 F_COD_MODELO_CHECKLIST BIGINT,
                                                                 F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                 F_DATA_HORA_REALIZACAO TIMESTAMP WITH TIME ZONE,
                                                                 F_COD_COLABORADOR BIGINT,
                                                                 F_COD_VEICULO BIGINT,
                                                                 F_TIPO_CHECKLIST CHAR,
                                                                 F_KM_COLETADO BIGINT,
                                                                 F_TEMPO_REALIZACAO BIGINT,
                                                                 F_DATA_HORA_SINCRONIZACAO TIMESTAMP WITH TIME ZONE,
                                                                 F_FONTE_DATA_HORA_REALIZACAO TEXT,
                                                                 F_VERSAO_APP_MOMENTO_REALIZACAO INTEGER,
                                                                 F_VERSAO_APP_MOMENTO_SINCRONIZACAO INTEGER,
                                                                 F_DEVICE_ID TEXT,
                                                                 F_DEVICE_IMEI TEXT,
                                                                 F_DEVICE_UPTIME_REALIZACAO_MILLIS BIGINT,
                                                                 F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                                                                 F_FOI_OFFLINE BOOLEAN,
                                                                 F_TOTAL_PERGUNTAS_OK INTEGER,
                                                                 F_TOTAL_PERGUNTAS_NOK INTEGER,
                                                                 F_TOTAL_ALTERNATIVAS_OK INTEGER,
                                                                 F_TOTAL_ALTERNATIVAS_NOK INTEGER)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
    V_DEVE_ATUALIZAR_KM_VEICULO BOOLEAN := (CASE
                                                WHEN (F_KM_COLETADO > (SELECT V.KM
                                                                       FROM VEICULO V
                                                                       WHERE V.CODIGO = F_COD_VEICULO))
                                                    THEN
                                                    TRUE
                                                ELSE FALSE END);
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    V_PLACA_ATUAL_DO_VEICULO    TEXT    := (SELECT VD.PLACA
                                            FROM VEICULO_DATA VD
                                            WHERE VD.CODIGO = F_COD_VEICULO);
    V_COD_CHECKLIST_INSERIDO    BIGINT;
    V_QTD_LINHAS_ATUALIZADAS    BIGINT;
BEGIN

    INSERT INTO CHECKLIST(COD_UNIDADE,
                          COD_CHECKLIST_MODELO,
                          COD_VERSAO_CHECKLIST_MODELO,
                          DATA_HORA,
                          DATA_HORA_REALIZACAO_TZ_APLICADO,
                          CPF_COLABORADOR,
                          PLACA_VEICULO,
                          TIPO,
                          TEMPO_REALIZACAO,
                          KM_VEICULO,
                          DATA_HORA_SINCRONIZACAO,
                          FONTE_DATA_HORA_REALIZACAO,
                          VERSAO_APP_MOMENTO_REALIZACAO,
                          VERSAO_APP_MOMENTO_SINCRONIZACAO,
                          DEVICE_ID,
                          DEVICE_IMEI,
                          DEVICE_UPTIME_REALIZACAO_MILLIS,
                          DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                          FOI_OFFLINE,
                          TOTAL_PERGUNTAS_OK,
                          TOTAL_PERGUNTAS_NOK,
                          TOTAL_ALTERNATIVAS_OK,
                          TOTAL_ALTERNATIVAS_NOK)
    VALUES (F_COD_UNIDADE_CHECKLIST,
            F_COD_MODELO_CHECKLIST,
            F_COD_VERSAO_MODELO_CHECKLIST,
            F_DATA_HORA_REALIZACAO,
            (F_DATA_HORA_REALIZACAO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE_CHECKLIST)),
            (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
            V_PLACA_ATUAL_DO_VEICULO,
            F_TIPO_CHECKLIST,
            F_TEMPO_REALIZACAO,
            F_KM_COLETADO,
            F_DATA_HORA_SINCRONIZACAO,
            F_FONTE_DATA_HORA_REALIZACAO,
            F_VERSAO_APP_MOMENTO_REALIZACAO,
            F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
            F_DEVICE_ID,
            F_DEVICE_IMEI,
            F_DEVICE_UPTIME_REALIZACAO_MILLIS,
            F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
            F_FOI_OFFLINE,
            F_TOTAL_PERGUNTAS_OK,
            F_TOTAL_PERGUNTAS_NOK,
            F_TOTAL_ALTERNATIVAS_OK,
            F_TOTAL_ALTERNATIVAS_NOK) RETURNING CODIGO INTO V_COD_CHECKLIST_INSERIDO;

    -- Verificamos se o insert funcionou.
    IF V_COD_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível inserir o checklist';
    END IF;

    IF V_DEVE_ATUALIZAR_KM_VEICULO
    THEN
        UPDATE VEICULO SET KM = F_KM_COLETADO WHERE CODIGO = F_COD_VEICULO;
    END IF;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
    IF (V_DEVE_ATUALIZAR_KM_VEICULO AND V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o km do veículo';
    END IF;

    RETURN V_COD_CHECKLIST_INSERIDO;
END;
$$;