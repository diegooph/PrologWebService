-- Sobre:
--
-- A lógica aplicada nessa function é a seguinte:
-- Altera a placa do veículo mesmo se ele possuir vinculos em outras tabelas.
--
-- Como:
-- Duplica as informações do veículo para a nova placa informada, aponta as dependências para a placa e código novos.
-- Após, deleta a placa antiga do banco.
--
-- Précondições:
-- FUNC_GARANTE_UNIDADE_EXISTE criada.
-- FUNC_GARANTE_VEICULO_EXISTE criada.
--
-- Histórico:
-- 2020-03-23 -> Function criada (thaisksf - PL-2569).
-- 2020-03-24 -> Adiciona update para placas integradas (natanrotta - PL-2569).
-- 2020-03-31 -> Faz com que placa nova mantenha o código antigo (thaisksf - PL-2569).
-- 2020-04-28 -> Adiciona código de diagrama (thaisksf - PL-2724).
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
    V_COD_DIAGRAMA       BIGINT;
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
           V.COD_EMPRESA,
           V.COD_DIAGRAMA
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
        V_COD_EMPRESA,
        V_COD_DIAGRAMA;

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
                              COD_EMPRESA,
                              COD_DIAGRAMA)
    VALUES (F_PLACA_NOVA,
            F_COD_UNIDADE,
            V_KM,
            V_STATUS_ATIVO,
            V_COD_TIPO,
            V_COD_MODELO,
            V_COD_EIXOS,
            V_DATA_HORA_CADASTRO,
            F_COD_UNIDADE,
            V_COD_EMPRESA,
            V_COD_DIAGRAMA) RETURNING CODIGO INTO V_COD_VEICULO_NOVO;
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
