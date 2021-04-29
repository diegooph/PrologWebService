BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--############ ADICIONA CONSTRAINTS PARA IMPEDIR QUE PNEUS VINCULADOS TENHAM STATUS DIFERENTE DE 'EM_USO' ##############
--######################################################################################################################
--######################################################################################################################
-- PL-1738
ALTER TABLE VEICULO_PNEU
    ADD COLUMN STATUS_PNEU VARCHAR(255) DEFAULT 'EM_USO' NOT NULL;

ALTER TABLE VEICULO_PNEU
    ADD
        CONSTRAINT CHECK_STATUS_VALIDO
            CHECK ((STATUS_PNEU ::TEXT) = ('EM_USO'));

ALTER TABLE PNEU_DATA
    ADD CONSTRAINT UNIQUE_PNEU_STATUS UNIQUE (CODIGO, STATUS, COD_UNIDADE);

UPDATE PNEU_DATA
SET STATUS = 'EM_USO'
WHERE STATUS != 'EM_USO'
  AND CODIGO IN (SELECT VP.COD_PNEU FROM VEICULO_PNEU VP);

ALTER TABLE VEICULO_PNEU
    ADD CONSTRAINT FK_VEICULO_PNEU_STATUS_PNEU FOREIGN KEY
        (STATUS_PNEU, COD_UNIDADE, COD_PNEU) REFERENCES PNEU_DATA (STATUS, COD_UNIDADE, CODIGO) DEFERRABLE INITIALLY
            DEFERRED;

ALTER TABLE VEICULO_PNEU
    DROP CONSTRAINT FK_VEICULO_PNEU_PNEU;
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2489 - Correção nas functions de cópia de modelos de checklist

-- Funções auxiliares
CREATE OR REPLACE FUNCTION FUNC_GARANTE_COD_COLABORADOR_EXISTE(
  F_COD_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE O COLABORADOR EXISTE
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CODIGO = F_COD_COLABORADOR)
  THEN RAISE EXCEPTION 'O colaborador com CÓDIGO: % não está cadastrado.', F_COD_COLABORADOR;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(
  F_COD_EMPRESA BIGINT,
  F_COD_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Verifica se colaborador existe.
  PERFORM FUNC_GARANTE_COD_COLABORADOR_EXISTE(F_COD_COLABORADOR);

  -- Verifica se empresa existe.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  -- Verifica se o colaborador pertence à empresa.
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CODIGO = F_COD_COLABORADOR AND C.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'O colaborador com o código: %, nome: %, não pertence a empresa: % - %!',
  F_COD_COLABORADOR,
  (SELECT C.NOME FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
  F_COD_EMPRESA,
  (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA);
  END IF;
END;
$$;

-- Realiza drop das functions antigas.
drop function suporte.func_checklist_copia_modelo_checklist(bigint, bigint, boolean, boolean, out bigint, out text);
drop function suporte.func_checklist_copia_modelo_checklist_entre_empresas(bigint, bigint, bigint[], bigint[], out bigint, out text);

-- Function de cópia de modelos de checklist entre unidades
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                         F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                         F_COPIAR_CARGOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         F_COPIAR_TIPOS_VEICULOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                         OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO  BIGINT;
    COD_PERGUNTA_CRIADO                   BIGINT;
    F_COD_EMPRESA                         BIGINT := (SELECT COD_EMPRESA
                                                     FROM UNIDADE
                                                     WHERE CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    PERGUNTA_MODELO_CHECKLIST_COPIADO     CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
    MODELO_VEICULO_TIPO_CHECKLIST_COPIADO CHECKLIST_MODELO_VEICULO_TIPO%ROWTYPE;
    NOME_MODELO_CHECKLIST_COPIADO         TEXT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO   BIGINT := (SELECT COD_VERSAO_ATUAL
                                                     FROM CHECKLIST_MODELO
                                                     WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    STATUS_MODELO_CHECKLIST_COPIADO       BOOLEAN;
    NOVO_COD_VERSAO_MODELO                BIGINT;
BEGIN
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA, F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CODIGO
                  FROM CHECKLIST_MODELO
                  WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DA MESMA EMPRESA.
    SELECT COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA !=
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Só é possível copiar modelos de checklists entre unidades da mesma empresa para garantirmos
            o vínculo correto de imagens da galeria.';
    END IF;

    -- Busca o nome e status do modelo copiado.
    SELECT CONCAT(CC.NOME, ' (cópia)'), CC.STATUS_ATIVO
             FROM CHECKLIST_MODELO CC
             WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO NOME_MODELO_CHECKLIST_COPIADO, STATUS_MODELO_CHECKLIST_COPIADO;

    -- Busca o novo código de versão do modelo de checklist
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
                PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
            NOVO_COD_VERSAO_MODELO,
            NOME_MODELO_CHECKLIST_COPIADO,
            STATUS_MODELO_CHECKLIST_COPIADO) RETURNING CODIGO
               INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                            COD_VERSAO_USER_FRIENDLY,
                                            COD_CHECKLIST_MODELO,
                                            DATA_HORA_CRIACAO_VERSAO,
                                            COD_COLABORADOR_CRIACAO_VERSAO)
        VALUES (NOVO_COD_VERSAO_MODELO,
                1,
                COD_MODELO_CHECKLIST_INSERIDO,
                NOW(),
                F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COPIAR_CARGOS_LIBERADOS
    THEN
        -- INSERE OS CARGOS LIBERADOS.
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
            (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    CMF.COD_FUNCAO
             FROM CHECKLIST_MODELO_FUNCAO CMF
             WHERE CMF.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO);
    END IF;

    IF F_COPIAR_TIPOS_VEICULOS_LIBERADOS
    THEN
        -- COPIA OS TIPOS DE VEÍCULO VINCULADOS.
        FOR MODELO_VEICULO_TIPO_CHECKLIST_COPIADO IN
            SELECT CMVT.COD_UNIDADE,
                   CMVT.COD_MODELO,
                   CMVT.COD_TIPO_VEICULO
            FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
            WHERE CMVT.COD_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
            LOOP
                -- INSERE OS TIPOS DE VEÍCULOS VINCULADOS.
                INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (cod_unidade, cod_modelo, cod_tipo_veiculo)
                VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        COD_MODELO_CHECKLIST_INSERIDO,
                        MODELO_VEICULO_TIPO_CHECKLIST_COPIADO.COD_TIPO_VEICULO);
            END LOOP;
    END IF;

    -- INSERE AS PERGUNTAS E ALTERNATIVAS.
    FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                    NOVO_COD_VERSAO_MODELO) RETURNING CODIGO
                       INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END;
$$;

-- Function para copiar modelos de checklist entre empresas.
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST_ENTRE_EMPRESAS(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                                        F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                                        F_COD_CARGOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        F_COD_TIPOS_VEICULOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                                        OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST BIGINT := (SELECT U.COD_EMPRESA
                                                      FROM UNIDADE U
                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO   BIGINT;
    COD_PERGUNTA_CRIADO                    BIGINT;
    NOVO_COD_VERSAO_MODELO                 BIGINT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO   BIGINT := (SELECT COD_VERSAO_ATUAL
                                                     FROM CHECKLIST_MODELO
                                                     WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    PERGUNTA_MODELO_CHECKLIST_COPIADO      CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
BEGIN
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST, F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CM.CODIGO
                  FROM CHECKLIST_MODELO CM
                  WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DE EMPRESAS DIFERENTES.
    SELECT CM.COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST =
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Essa function deve ser utilizada para copiar modelos de checklists entre empresas diferentes.
                        Utilize a function: FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST, para copiar checklists entre unidades
             da mesma empresa.';
    END IF;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS CARGOS EXISTEM.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_CARGO)
                                                                        FROM UNNEST(F_COD_CARGOS_CHECKLIST)
                                                                                 AS COD_CARGO
                                                                                 LEFT JOIN FUNCAO F
                                                                                           ON F.CODIGO = COD_CARGO
                                                                        WHERE F.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS CARGOS PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não pertence(m) a empresa para a qual você está tentando copiar o
                modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_CARGO)
                 FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                          LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                 WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO EXISTEM.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                                                                                  FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST)
                                                                                           AS COD_TIPO_VEICULO
                                                                                           LEFT JOIN VEICULO_TIPO VT
                                                                                                     ON VT.CODIGO = COD_TIPO_VEICULO
                                                                                  WHERE VT.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não pertence(m) a empresa para a qual você está tentando
                copiar o modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                 FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                          LEFT JOIN VEICULO_TIPO VT
                                    ON VT.CODIGO = COD_TIPO_VEICULO
                 WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    -- Busca o novo código de versão do modelo de checklist.
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    SELECT F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
           NOVO_COD_VERSAO_MODELO,
           CONCAT(CC.NOME, ' (cópia)'),
           CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO RETURNING CODIGO
        INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO.
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                            COD_VERSAO_USER_FRIENDLY,
                                            COD_CHECKLIST_MODELO,
                                            DATA_HORA_CRIACAO_VERSAO,
                                            COD_COLABORADOR_CRIACAO_VERSAO)
        VALUES (NOVO_COD_VERSAO_MODELO,
                1,
                COD_MODELO_CHECKLIST_INSERIDO,
                NOW(),
                F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE CARGOS QUE PODEM REALIZAR O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_FUNCAO
        FROM UNNEST(F_COD_CARGOS_CHECKLIST) CODIGO_FUNCAO;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE TIPOS DE VEÍCULOS LIBERADOS PARA O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (COD_MODELO, COD_UNIDADE, COD_TIPO_VEICULO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_TIPO_VEICULO
        FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) CODIGO_TIPO_VEICULO;
    END IF;

    -- INSERE AS PERGUNTAS E ALTERNATIVAS.
    FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                    -- Só copiamos o código da imagem se a imagem vinculada for da galeria pública do Prolog.
                    F_IF((SELECT EXISTS(SELECT CGI.COD_IMAGEM
                                        FROM CHECKLIST_GALERIA_IMAGENS CGI
                                        WHERE CGI.COD_IMAGEM = PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM
                                          AND CGI.COD_EMPRESA IS NULL)),
                         PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                         NULL),
                    NOVO_COD_VERSAO_MODELO) RETURNING CODIGO
                       INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--ṔL-1899 - Salvar diagrama do veículo nas funcionalidades que geram histórico para visualização
--######################################################################################################################
--#####################################           CRIA NOVA COLUNA           ###########################################
--######################################################################################################################
--ALTERA TABELA DE AFERICAO_DATA, ADICIONANDO NOVA COLUNA COD_DIAGRAMA.
ALTER TABLE AFERICAO_DATA
    ADD COLUMN COD_DIAGRAMA BIGINT REFERENCES VEICULO_DIAGRAMA (CODIGO);

--ALTERA TABELA DE MOVIMENTACAO_ORIGEM, ADICIONANDO NOVA COLUNA COD_DIAGRAMA.
ALTER TABLE MOVIMENTACAO_ORIGEM
    ADD COLUMN COD_DIAGRAMA BIGINT REFERENCES VEICULO_DIAGRAMA (CODIGO);

--ALTERA TABELA DE MOVIMENTACAO_DESTINO, ADICIONANDO NOVA COLUNA COD_DIAGRAMA.
ALTER TABLE MOVIMENTACAO_DESTINO
    ADD COLUMN COD_DIAGRAMA BIGINT REFERENCES VEICULO_DIAGRAMA (CODIGO);

--######################################################################################################################
--#####################################           CRIA CONSTRAINT            ###########################################
--######################################################################################################################
--TABELA AFERICAO_DATA.
ALTER TABLE AFERICAO_DATA
    ADD CONSTRAINT CHECK_DIAGRAMA_NOT_NULL CHECK (
            (TIPO_PROCESSO_COLETA = 'PLACA' AND COD_DIAGRAMA IS NOT NULL) OR
            (TIPO_PROCESSO_COLETA = 'PNEU_AVULSO' AND COD_DIAGRAMA IS NULL)
        ) NOT VALID;


--TABELA MOVIMENTACAO_ORIGEM.
ALTER TABLE MOVIMENTACAO_ORIGEM
    ADD CONSTRAINT CHECK_DIAGRAMA_NOT_NULL CHECK (
            (TIPO_ORIGEM = 'EM_USO' AND COD_DIAGRAMA IS NOT NULL) OR
            (TIPO_ORIGEM = 'ESTOQUE' AND COD_DIAGRAMA IS NULL) OR
            (TIPO_ORIGEM = 'ANALISE' AND COD_DIAGRAMA IS NULL) OR
            (TIPO_ORIGEM = 'DESCARTE' AND COD_DIAGRAMA IS NULL)
        ) NOT VALID;

--TABELA MOVIMENTACAO_DESTINO.
ALTER TABLE MOVIMENTACAO_DESTINO
    ADD CONSTRAINT CHECK_DIAGRAMA_NOT_NULL CHECK (
            (TIPO_DESTINO = 'EM_USO' AND COD_DIAGRAMA IS NOT NULL) OR
            (TIPO_DESTINO = 'ESTOQUE' AND COD_DIAGRAMA IS NULL) OR
            (TIPO_DESTINO = 'ANALISE' AND COD_DIAGRAMA IS NULL) OR
            (TIPO_DESTINO = 'DESCARTE' AND COD_DIAGRAMA IS NULL)
        ) NOT VALID;
--######################################################################################################################
--#####################################      ATUALIZA AQUELES QUE ESTÃO NULL      ######################################
--######################################################################################################################

-- Dropa a trigger para evitar a geração de dados desnecessários
DROP TRIGGER TG_FUNC_AUDIT_AFERICAO ON AFERICAO_DATA;

--ATUALIZA A COLUNA COD_DIAGRAMA, ADICIONANDO O CÓDIGO DO DIAGRAMA DAS RESPECTIVAS PLACAS EM AFERICAO.
UPDATE AFERICAO_DATA
SET COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                    FROM VEICULO_TIPO VT
                    WHERE VT.CODIGO IN
                          (SELECT V.COD_TIPO FROM VEICULO_DATA V WHERE V.PLACA = AFERICAO_DATA.PLACA_VEICULO))
WHERE COD_DIAGRAMA ISNULL;

-- Recria a trigger de aferição
CREATE TRIGGER TG_FUNC_AUDIT_AFERICAO
  AFTER INSERT OR UPDATE OR DELETE
  ON AFERICAO_DATA
  FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

UPDATE MOVIMENTACAO_DESTINO
SET COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                    FROM VEICULO_TIPO VT
                    WHERE VT.CODIGO IN (SELECT V.COD_TIPO
                                        FROM VEICULO_DATA V
                                        WHERE V.PLACA = MOVIMENTACAO_DESTINO.PLACA))
WHERE COD_DIAGRAMA ISNULL;

UPDATE MOVIMENTACAO_ORIGEM
SET COD_DIAGRAMA = (SELECT VT.COD_DIAGRAMA
                    FROM VEICULO_TIPO VT
                    WHERE VT.CODIGO IN (SELECT V.COD_TIPO
                                        FROM VEICULO_DATA V
                                        WHERE V.PLACA = MOVIMENTACAO_ORIGEM.PLACA))
WHERE COD_DIAGRAMA ISNULL;
--######################################################################################################################
--#####################################            CRIA FUNCTIONS            ###########################################
--######################################################################################################################
--CRIA FUNCTION PARA A INSERÇÃO DE NOVA AFERIÇÃO.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_INSERT_AFERICAO(F_COD_UNIDADE BIGINT,
                                                         F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                         F_CPF_AFERIDOR BIGINT,
                                                         F_TEMPO_REALIZACAO BIGINT,
                                                         F_TIPO_MEDICAO_COLETADA VARCHAR(255),
                                                         F_TIPO_PROCESSO_COLETA VARCHAR(255),
                                                         F_PLACA_VEICULO VARCHAR(255),
                                                         F_KM_VEICULO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_TIPO_VEICULO      BIGINT := (SELECT V.COD_TIPO
                                       FROM VEICULO_DATA V
                                       WHERE V.PLACA = F_PLACA_VEICULO);
    F_COD_DIAGRAMA_VEICULO  BIGINT := (SELECT VT.COD_DIAGRAMA
                                       FROM VEICULO_TIPO VT
                                       WHERE VT.CODIGO = F_COD_TIPO_VEICULO);
    F_COD_AFERICAO_INSERIDA BIGINT;
BEGIN
    --REALIZA INSERÇÃO DA AFERIÇÃO
    INSERT INTO AFERICAO_DATA(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO, TEMPO_REALIZACAO,
                              TIPO_MEDICAO_COLETADA, COD_UNIDADE, TIPO_PROCESSO_COLETA, DELETADO, DATA_HORA_DELETADO,
                              PG_USERNAME_DELECAO, COD_DIAGRAMA)
    VALUES (F_DATA_HORA, F_PLACA_VEICULO, F_CPF_AFERIDOR, F_KM_VEICULO, F_TEMPO_REALIZACAO, F_TIPO_MEDICAO_COLETADA,
            F_COD_UNIDADE, F_TIPO_PROCESSO_COLETA, FALSE, NULL, NULL,
            F_COD_DIAGRAMA_VEICULO) RETURNING CODIGO INTO F_COD_AFERICAO_INSERIDA;

    RETURN F_COD_AFERICAO_INSERIDA;
END
$$;
--######################################################################################################################
--CRIA FUNCTION PARA A INSERÇÃO DE NOVA MOVIMENTAÇÃO ORIGEM.
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_ORIGEM(F_COD_PNEU BIGINT,
                                                                                F_COD_UNIDADE BIGINT,
                                                                                F_TIPO_ORIGEM VARCHAR(255),
                                                                                F_COD_MOVIMENTACAO BIGINT,
                                                                                F_PLACA_VEICULO VARCHAR(7),
                                                                                F_KM_ATUAL BIGINT,
                                                                                F_POSICAO_PROLOG INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_TIPO_VEICULO           BIGINT       := (SELECT V.COD_TIPO
                                                  FROM VEICULO_DATA V
                                                  WHERE V.PLACA = F_PLACA_VEICULO);
    F_COD_DIAGRAMA_VEICULO       BIGINT       := (SELECT VT.COD_DIAGRAMA
                                                  FROM VEICULO_TIPO VT
                                                  WHERE VT.CODIGO = F_COD_TIPO_VEICULO);
    F_TIPO_ORIGEM_ATUAL          VARCHAR(255) := (SELECT P.STATUS
                                                  FROM PNEU P
                                                  WHERE P.CODIGO = F_COD_PNEU
                                                    AND P.COD_UNIDADE = F_COD_UNIDADE
                                                    AND F_TIPO_ORIGEM IN (SELECT P.STATUS
                                                                          FROM PNEU P
                                                                          WHERE P.CODIGO = F_COD_PNEU
                                                                            AND P.COD_UNIDADE = F_COD_UNIDADE));
    F_COD_MOVIMENTACAO_REALIZADA BIGINT;
BEGIN
    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO ORIGEM
    INSERT INTO MOVIMENTACAO_ORIGEM(COD_MOVIMENTACAO,
                                    TIPO_ORIGEM,
                                    PLACA,
                                    KM_VEICULO,
                                    POSICAO_PNEU_ORIGEM,
                                    COD_DIAGRAMA)
    VALUES (F_COD_MOVIMENTACAO,
            F_TIPO_ORIGEM_ATUAL,
            F_PLACA_VEICULO,
            F_KM_ATUAL,
            F_POSICAO_PROLOG,
            F_COD_DIAGRAMA_VEICULO) RETURNING COD_MOVIMENTACAO INTO F_COD_MOVIMENTACAO_REALIZADA;

    IF (F_COD_MOVIMENTACAO_REALIZADA <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao inserir a origem veiculo da movimentação');
    END IF;
END
$$;
--######################################################################################################################
--CRIA FUNCTION PARA A INSERÇÃO DE NOVA MOVIMENTAÇÃO DESTINO.
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_DESTINO(F_COD_MOVIMENTACAO BIGINT,
                                                                                 F_TIPO_DESTINO VARCHAR(255),
                                                                                 F_PLACA_VEICULO VARCHAR(255),
                                                                                 F_KM_ATUAL BIGINT,
                                                                                 F_POSICAO_PROLOG BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_TIPO_VEICULO           BIGINT := (SELECT V.COD_TIPO
                                            FROM VEICULO_DATA V
                                            WHERE V.PLACA = F_PLACA_VEICULO);
    F_COD_DIAGRAMA_VEICULO       BIGINT := (SELECT VT.COD_DIAGRAMA
                                            FROM VEICULO_TIPO VT
                                            WHERE VT.CODIGO = F_COD_TIPO_VEICULO);
    F_COD_MOVIMENTACAO_REALIZADA BIGINT;
BEGIN
    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO DESTINO.
    INSERT INTO MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO,
                                     TIPO_DESTINO,
                                     PLACA,
                                     KM_VEICULO,
                                     POSICAO_PNEU_DESTINO,
                                     COD_MOTIVO_DESCARTE,
                                     URL_IMAGEM_DESCARTE_1,
                                     URL_IMAGEM_DESCARTE_2,
                                     URL_IMAGEM_DESCARTE_3,
                                     COD_RECAPADORA_DESTINO,
                                     COD_COLETA,
                                     COD_DIAGRAMA)
    VALUES (F_COD_MOVIMENTACAO,
            F_TIPO_DESTINO,
            F_PLACA_VEICULO,
            F_KM_ATUAL,
            F_POSICAO_PROLOG,
            NULL,
            NULL,
            NULL,
            NULL,
            NULL,
            NULL,
            F_COD_DIAGRAMA_VEICULO) RETURNING COD_MOVIMENTACAO INTO F_COD_MOVIMENTACAO_REALIZADA;

    IF (F_COD_MOVIMENTACAO_REALIZADA <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao inserir o destino veiculo da movimentação');
    END IF;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### ADICIONA NOVAS COLUNAS NA TABELA DE MAPA #######################################
--######################################################################################################################
--######################################################################################################################
-- PL-2409
ALTER TABLE MAPA
    ADD COLUMN DATA_ENTREGA DATE,
    ADD COLUMN QT_ENTREGAS_CARREG_RV INTEGER,
    ADD COLUMN QT_ENTREGAS_ENTREG_RV INTEGER,
    ADD COLUMN INDICE_DEV_ENTREGAS REAL,
    ADD COLUMN CPF_MOTORISTA BIGINT,
    ADD COLUMN CPF_AJUDANTE_1 BIGINT,
    ADD COLUMN CPF_AJUDANTE_2 BIGINT,
    ADD COLUMN INICIO_ROTA TIMESTAMP,
    ADD COLUMN TERMINO_ROTA TIMESTAMP,
    ADD COLUMN MOTORISTA_JT_12X36 VARCHAR(255),
    ADD COLUMN RETIRA VARCHAR(255);
--######################################################################################################################
--######################################################################################################################





-- SOCORRO EM ROTA





--######################################################################################################################
--######################################################################################################################
-- PL-2496.
create schema messaging;

CREATE TYPE messaging.aplicacao_referencia_token_type AS ENUM (
    'PROLOG_ANDROID_DEBUG',
    'PROLOG_ANDROID_PROD',
    'PROLOG_WEB',
    'AFERE_FACIL_ANDROID_DEBUG',
    'AFERE_FACIL_ANDROID_PROD');

alter table token_autenticacao
    add constraint unique_colaborador_token unique (cod_colaborador, token);

create table messaging.push_colaborador_token
(
    codigo                     bigserial                                 not null,
    cod_colaborador            bigint                                    not null,
    token_colaborador_logado   text                                      not null,
    aplicacao_referencia_token messaging.aplicacao_referencia_token_type not null,
    token_push_firebase        text                                      not null,
    data_hora_cadastro         timestamp with time zone                  not null,
    constraint pk_push_colaborador_token primary key (codigo),
    constraint fk_push_colaborador_token_colaborador foreign key (cod_colaborador, token_colaborador_logado)
        references public.token_autenticacao (cod_colaborador, token) on delete cascade,
    constraint unique_token unique (token_push_firebase)
);

comment on table messaging.push_colaborador_token
    is 'Salva o token do Firebase que está atualmente associado ao colaborador.';

comment on column messaging.push_colaborador_token.token_push_firebase
    is 'Segundo a documentação, o token é único e não pode ser reutilizado.
    (https://stackoverflow.com/questions/52070864/is-a-fcm-token-reused).
    Essa tabela tem FK com PUBLIC.TOKEN_AUTENTICACAO. A FK é com "on delete cascade" para que quando um colaborador
    deslogue ele não receba mais notificações de push.';

CREATE TRIGGER TG_FUNC_AUDIT_MESSAGING_PUSH_COLABORADOR_TOKEN
    AFTER INSERT OR UPDATE OR DELETE
    ON MESSAGING.PUSH_COLABORADOR_TOKEN
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

CREATE TYPE messaging.push_message_type AS ENUM ('MULTICAST');
CREATE TYPE messaging.push_plataform_destination AS ENUM ('ANDROID');

create table messaging.push_log
(
    codigo                    bigserial                            not null,
    data_hora_log             timestamp with time zone             not null,
    push_message_scope        text                                 not null,
    push_message_sent         jsonb                                not null,
    message_type              messaging.push_message_type          not null,
    plataform_destination     messaging.push_plataform_destination not null,
    request_response_firebase jsonb                                not null,
    fatal_send_exception      text,
    constraint pk_push_log primary key (codigo)
);

comment on table messaging.push_log
    is 'Salva os logs de requisições de notificações feitas para o FCM e as respostas que obtivemos da API.';

comment on column messaging.push_log.push_message_sent
    is 'Mensagem que foi enviada.';

comment on column messaging.push_log.plataform_destination
    is 'Indica para qual plataforma a mensagem foi enviada.';

comment on column messaging.push_log.request_response_firebase
    is 'Contém o JSON de cada request e response feito à API do FCM. Um request é a requisição de envio para um
    destinatário específico. Os requests sempre irão existir, nem sempre existirá uma resposta ou, as vezes, existirá
    uma exception mostrando o erro capturado no envio para um destinatário específico.';

comment on column messaging.push_log.fatal_send_exception
    is 'Pode conter o stacktrace de uma exception fatal capturada na tentativa de realizar o envio das mensagens.
    Se existir algum conteúdo nessa coluna, significa que nenhuma mensagem pôde ser entregue, pois a falha foi geral.';

CREATE OR REPLACE FUNCTION
    MESSAGING.FUNC_PUSH_SALVA_TOKEN_COLABORADOR(F_COD_COLABORADOR BIGINT,
                                                F_TOKEN_COLABORADOR_LOGADO TEXT,
                                                F_APLICACAO_REFERENCIA_TOKEN MESSAGING.APLICACAO_REFERENCIA_TOKEN_TYPE,
                                                F_TOKEN_PUSH_FIREBASE TEXT,
                                                F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE) RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MESSAGING.PUSH_COLABORADOR_TOKEN (COD_COLABORADOR,
                                                  TOKEN_COLABORADOR_LOGADO,
                                                  APLICACAO_REFERENCIA_TOKEN,
                                                  TOKEN_PUSH_FIREBASE,
                                                  DATA_HORA_CADASTRO)
    VALUES (F_COD_COLABORADOR,
            F_TOKEN_COLABORADOR_LOGADO,
            F_APLICACAO_REFERENCIA_TOKEN,
            F_TOKEN_PUSH_FIREBASE,
            F_DATA_HORA_ATUAL)
    ON CONFLICT ON CONSTRAINT UNIQUE_TOKEN
        DO UPDATE SET COD_COLABORADOR          = F_COD_COLABORADOR,
                      TOKEN_COLABORADOR_LOGADO = F_TOKEN_COLABORADOR_LOGADO,
                      DATA_HORA_CADASTRO       = F_DATA_HORA_ATUAL;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao salvar token de push para o colaborador.
            cod_colaborador: %s - aplicacao_referencia_token: %s - token_push: %s',
            F_COD_COLABORADOR,
            F_APLICACAO_REFERENCIA_TOKEN,
            F_TOKEN_PUSH_FIREBASE;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION MESSAGING.FUNC_PUSH_SALVA_LOG(F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                         F_PUSH_MESSAGE_SCOPE TEXT,
                                                         F_PUSH_MESSAGE_SENT JSONB,
                                                         F_MESSAGE_TYPE MESSAGING.PUSH_MESSAGE_TYPE,
                                                         F_PLATAFORM_DESTINATION MESSAGING.PUSH_PLATAFORM_DESTINATION,
                                                         F_REQUEST_RESPONSE_FIREBASE JSONB,
                                                         F_FATAL_SEND_EXCEPTION TEXT) RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MESSAGING.PUSH_LOG (DATA_HORA_LOG,
                                    PUSH_MESSAGE_SCOPE,
                                    PUSH_MESSAGE_SENT,
                                    MESSAGE_TYPE,
                                    PLATAFORM_DESTINATION,
                                    REQUEST_RESPONSE_FIREBASE,
                                    FATAL_SEND_EXCEPTION)
    VALUES (F_DATA_HORA_ATUAL,
            F_PUSH_MESSAGE_SCOPE,
            F_PUSH_MESSAGE_SENT,
            F_MESSAGE_TYPE,
            F_PLATAFORM_DESTINATION,
            F_REQUEST_RESPONSE_FIREBASE,
            F_FATAL_SEND_EXCEPTION);

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao salvar ao salvar log da mensagem: %s', F_PUSH_MESSAGE_SENT;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA_GET_COLABORADORES_NOTIFICACAO(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_COLABORADOR     BIGINT,
                TOKEN_PUSH_FIREBASE TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PERMISSAO_TRATAR_SOCORRO CONSTANT BIGINT := 146;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                AS COD_COLABORADOR,
               PCT.TOKEN_PUSH_FIREBASE AS TOKEN_PUSH_FIREBASE
        FROM COLABORADOR C
                 JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE
            AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                 JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND
                                  F.CODIGO = CFP.COD_FUNCAO_COLABORADOR AND C.COD_EMPRESA = F.COD_EMPRESA
                 JOIN MESSAGING.PUSH_COLABORADOR_TOKEN PCT ON C.CODIGO = PCT.COD_COLABORADOR
        WHERE C.COD_UNIDADE = F_COD_UNIDADE
          AND CFP.COD_FUNCAO_PROLOG = PERMISSAO_TRATAR_SOCORRO
          -- Filtra apenas por aplicativos do Prolog.
          AND PCT.APLICACAO_REFERENCIA_TOKEN IN ('PROLOG_ANDROID_DEBUG', 'PROLOG_ANDROID_PROD')
          AND C.STATUS_ATIVO = TRUE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2422.
-- Remove tabela não mais utilizada.
DROP TABLE FUNCAO_PROLOG;
ALTER TABLE FUNCAO_PROLOG_V11
    DROP COLUMN FUNCAO_OLD;

-- Pra corrigir um bug na tabela, sequence ta mal posicionada. Próximo valor vai ser 28.
SELECT setval('funcao_prolog_agrupamento_codigo_seq', 27, TRUE);

INSERT INTO PUBLIC.FUNCAO_PROLOG_AGRUPAMENTO (NOME, COD_PILAR)
VALUES ('Socorro em Rota', 1);

INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, COD_PILAR, IMPACTO, COD_AGRUPAMENTO, FUNCAO, DESCRICAO)
VALUES (145, 1, 'BAIXO', 28, 'Solicitar socorro', 'Permite ao usuário solicitar um socorro em rota.');
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, COD_PILAR, IMPACTO, COD_AGRUPAMENTO, FUNCAO, DESCRICAO)
VALUES (146, 1, 'CRITICO', 28, 'Tratar socorros',
        'Permite ao usuário tratar todo o fluxo de um socorro solictado, sendo: realizar o atendimento, invalidar um socorro e finalizar o atendimento');
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, COD_PILAR, IMPACTO, COD_AGRUPAMENTO, FUNCAO, DESCRICAO)
VALUES (147, 1, 'MEDIO', 28, 'Visualizar socorros e relatórios',
        'Permite ao usuário visualizar os socorros existens e relatórios sobre socorros em rota.');
INSERT INTO PUBLIC.FUNCAO_PROLOG_V11 (CODIGO, COD_PILAR, IMPACTO, COD_AGRUPAMENTO, FUNCAO, DESCRICAO)
VALUES (148, 1, 'ALTO', 28, 'Gerenciar Opções de Problema',
        'Permite ao usuário listar, criar, editar, ativar e inativar as opções de problema.');
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2420
CREATE TYPE SOCORRO_ROTA_STATUS_TYPE AS ENUM (
    'ABERTO',
    'EM_ATENDIMENTO',
    'INVALIDO',
    'FINALIZADO');

CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_OPCAO_PROBLEMA
(
    CODIGO                             BIGSERIAL                NOT NULL,
    COD_EMPRESA                        BIGINT                   NOT NULL,
    DESCRICAO                          CITEXT                   NOT NULL,
    OBRIGA_DESCRICAO                   BOOLEAN                  NOT NULL,
    COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT                   NOT NULL,
    DATA_HORA_ULTIMA_ATUALIZACAO       TIMESTAMP WITH TIME ZONE NOT NULL,
    STATUS_ATIVO                       BOOLEAN DEFAULT TRUE     NOT NULL,
    CONSTRAINT PK_SOCORRO_ROTA_OPCAO_PROBLEMA PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_OPCAO_PROBLEMA_EMPRESA_CODIGO FOREIGN KEY (COD_EMPRESA)
        REFERENCES EMPRESA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_OPCAO_PROBLEMA_COLABORADOR_CODIGO FOREIGN KEY (COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        REFERENCES COLABORADOR_DATA (CODIGO)
);

-- Cria constraint unique para descrição e empresa quando o status for true
CREATE UNIQUE INDEX UNIQUE_DESCRICAO_EMPRESA
    ON SOCORRO_ROTA_OPCAO_PROBLEMA (COD_EMPRESA, DESCRICAO)
    WHERE (STATUS_ATIVO IS TRUE);

COMMENT ON COLUMN SOCORRO_ROTA_OPCAO_PROBLEMA.OBRIGA_DESCRICAO IS 'Campo para forçar o usuário a digitar uma
    descrição na abertura do socorro em rota, caso a opção de problema selecionada tenha esse campo marcado.';


CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO
(
    CODIGO                    BIGSERIAL                NOT NULL,
    COD_PROBLEMA_SOCORRO_ROTA BIGINT                   NOT NULL,
    COD_COLABORADOR_ALTERACAO BIGINT                   NOT NULL,
    DATA_HORA_ALTERACAO       TIMESTAMP WITH TIME ZONE NOT NULL,
    COD_EMPRESA               BIGINT                   NOT NULL,
    DESCRICAO                 CITEXT                   NOT NULL,
    OBRIGA_DESCRICAO          BOOLEAN                  NOT NULL,
    STATUS_ATIVO              BOOLEAN                  NOT NULL,
    CONSTRAINT PK_SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_OPCAO_PROBLEMA FOREIGN KEY (COD_PROBLEMA_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA_OPCAO_PROBLEMA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO_COLABORADOR FOREIGN KEY (COD_COLABORADOR_ALTERACAO)
        REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO_EMPRESA_CODIGO FOREIGN KEY (COD_EMPRESA)
        REFERENCES EMPRESA (CODIGO)
);

COMMENT ON COLUMN SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO.COD_COLABORADOR_ALTERACAO IS 'Código do colaborador que
    realizou a última alteração, vindo da tabela socorro_rota_opcao_problema.';

COMMENT ON COLUMN SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO.DATA_HORA_ALTERACAO IS 'Data e hora da última alteração,
    vinda da tabela socorro_rota_opcao_problema.';

COMMENT ON COLUMN SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO.DESCRICAO IS 'Descrição anterior, vinda da tabela
    socorro_rota_opcao_problema.';

COMMENT ON COLUMN SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO.OBRIGA_DESCRICAO IS 'Booleano de obrigação de descrição
    anterior, vindo da tabela socorro_rota_opcao_problema.';

CREATE TABLE IF NOT EXISTS SOCORRO_ROTA
(
    CODIGO       BIGSERIAL                NOT NULL,
    COD_UNIDADE  BIGINT                   NOT NULL,
    STATUS_ATUAL SOCORRO_ROTA_STATUS_TYPE NOT NULL,
    CONSTRAINT PK_SOCORRO_ROTA PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_UNIDADE_CODIGO FOREIGN KEY (COD_UNIDADE)
        REFERENCES UNIDADE (CODIGO)
);

COMMENT ON TABLE SOCORRO_ROTA IS 'Salva um socorro em rota específico e o seu estado atual.
Os estados possíveis são os seguintes:
ABERTO
ABERTO -> EM_ATENDIMENTO
ABERTO -> INVALIDO (motorista também pode se não tiver sido atendido ou o usuário com permissão)
EM_ATENDIMENTO -> FINALIZADO
EM_ATENDIMENTO -> INVALIDO';

-- Cria tabela para o status de abertura
CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_ABERTURA
(
    CODIGO                               BIGSERIAL                NOT NULL,
    COD_SOCORRO_ROTA                     BIGINT                   NOT NULL,
    COD_COLABORADOR_ABERTURA             BIGINT                   NOT NULL,
    COD_VEICULO_PROBLEMA                 BIGINT                   NOT NULL,
    KM_VEICULO_ABERTURA                  BIGINT                   NOT NULL,
    COD_PROBLEMA_SOCORRO_ROTA            BIGINT                   NOT NULL,
    DESCRICAO_PROBLEMA                   TEXT,
    DATA_HORA_ABERTURA                   TIMESTAMP WITH TIME ZONE NOT NULL,
    URL_FOTO_1_ABERTURA                  TEXT,
    URL_FOTO_2_ABERTURA                  TEXT,
    URL_FOTO_3_ABERTURA                  TEXT,
    LATITUDE_ABERTURA                    TEXT                     NOT NULL,
    LONGITUDE_ABERTURA                   TEXT                     NOT NULL,
    PRECISAO_LOCALIZACAO_ABERTURA_METROS NUMERIC(5, 2)            NOT NULL,
    ENDERECO_AUTOMATICO                  TEXT,
    PONTO_REFERENCIA                     TEXT,
    VERSAO_APP_MOMENTO_ABERTURA          BIGINT                   NOT NULL,
    DEVICE_ID_ABERTURA                   TEXT,
    DEVICE_IMEI_ABERTURA                 TEXT,
    DEVICE_UPTIME_MILLIS_ABERTURA        BIGINT,
    ANDROID_API_VERSION_ABERTURA         INTEGER,
    MARCA_DEVICE_ABERTURA                TEXT,
    MODELO_DEVICE_ABERTURA               TEXT,
    CONSTRAINT PK_SOCORRO_ROTA_ABERTURA PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA FOREIGN KEY (COD_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_COLABORADOR_CODIGO FOREIGN KEY (COD_COLABORADOR_ABERTURA)
        REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_VEICULO_CODIGO FOREIGN KEY (COD_VEICULO_PROBLEMA)
        REFERENCES VEICULO_DATA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ABERTURA_SOCORRO_ROTA_OPCAO_PROBLEMA FOREIGN KEY (COD_PROBLEMA_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA_OPCAO_PROBLEMA (CODIGO),
    CONSTRAINT UNICA_ABERTURA_SOCORRO UNIQUE (COD_SOCORRO_ROTA)
);


-- Cria trigger function para obrigar que a solicitação de socorro tenha uma descrição de acordo com o tipo de problema
CREATE OR REPLACE FUNCTION TG_FUNC_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
DECLARE
    OBRIGA_DESCRICAO BOOLEAN := (SELECT OBRIGA_DESCRICAO
                                 FROM SOCORRO_ROTA_OPCAO_PROBLEMA
                                 WHERE CODIGO = NEW.COD_PROBLEMA_SOCORRO_ROTA);
BEGIN
    IF OBRIGA_DESCRICAO AND (NEW.DESCRICAO_PROBLEMA IS NULL OR LENGTH(NEW.DESCRICAO_PROBLEMA) = 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Essa opção de problema exige uma descrição');
    END IF;
    RETURN NEW;
END;
$$;

-- Cria o evento da trigger e chama a function específica
CREATE TRIGGER TG_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA
    BEFORE INSERT
    ON SOCORRO_ROTA_ABERTURA
    FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_SOCORRO_VERIFICA_DESCRICAO_PROBLEMA();

-- Cria tabela para o status de atendimento
CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_ATENDIMENTO
(
    CODIGO                                  BIGSERIAL                NOT NULL,
    COD_SOCORRO_ROTA                        BIGINT                   NOT NULL,
    COD_COLABORADOR_ATENDIMENTO             BIGINT                   NOT NULL,
    OBSERVACAO_ATENDIMENTO                  TEXT,
    DATA_HORA_ATENDIMENTO                   TIMESTAMP WITH TIME ZONE NOT NULL,
    LATITUDE_ATENDIMENTO                    TEXT                     NOT NULL,
    LONGITUDE_ATENDIMENTO                   TEXT                     NOT NULL,
    PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS NUMERIC(5, 2)            NOT NULL,
    ENDERECO_AUTOMATICO                     TEXT,
    VERSAO_APP_MOMENTO_ATENDIMENTO          BIGINT                   NOT NULL,
    DEVICE_ID_ATENDIMENTO                   TEXT,
    DEVICE_IMEI_ATENDIMENTO                 TEXT,
    DEVICE_UPTIME_MILLIS_ATENDIMENTO        BIGINT,
    ANDROID_API_VERSION_ATENDIMENTO         INTEGER,
    MARCA_DEVICE_ATENDIMENTO                TEXT,
    MODELO_DEVICE_ATENDIMENTO               TEXT,
    CONSTRAINT PK_SOCORRO_ROTA_ATENDIMENTO PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ATENDIMENTO_SOCORRO_ROTA FOREIGN KEY (COD_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_ATENDIMENTO_COLABORADOR_CODIGO FOREIGN KEY (COD_COLABORADOR_ATENDIMENTO)
        REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT UNICO_ATENDIMENTO_SOCORRO UNIQUE (COD_SOCORRO_ROTA)
);

-- Cria tabela para o status de invalidação
CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_INVALIDACAO
(
    CODIGO                                  BIGSERIAL                NOT NULL,
    COD_SOCORRO_ROTA                        BIGINT                   NOT NULL,
    COD_COLABORADOR_INVALIDACAO             BIGINT                   NOT NULL,
    MOTIVO_INVALIDACAO                      TEXT                     NOT NULL,
    DATA_HORA_INVALIDACAO                   TIMESTAMP WITH TIME ZONE NOT NULL,
    URL_FOTO_1_INVALIDACAO                  TEXT,
    URL_FOTO_2_INVALIDACAO                  TEXT,
    URL_FOTO_3_INVALIDACAO                  TEXT,
    LATITUDE_INVALIDACAO                    TEXT                     NOT NULL,
    LONGITUDE_INVALIDACAO                   TEXT                     NOT NULL,
    PRECISAO_LOCALIZACAO_INVALIDACAO_METROS NUMERIC(5, 2)            NOT NULL,
    ENDERECO_AUTOMATICO                     TEXT,
    VERSAO_APP_MOMENTO_INVALIDACAO          BIGINT                   NOT NULL,
    DEVICE_ID_INVALIDACAO                   TEXT,
    DEVICE_IMEI_INVALIDACAO                 TEXT,
    DEVICE_UPTIME_MILLIS_INVALIDACAO        BIGINT,
    ANDROID_API_VERSION_INVALIDACAO         INTEGER,
    MARCA_DEVICE_INVALIDACAO                TEXT,
    MODELO_DEVICE_INVALIDACAO               TEXT,
    CONSTRAINT PK_SOCORRO_ROTA_INVALIDACAO PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_INVALIDACAO_SOCORRO_ROTA FOREIGN KEY (COD_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_INVALIDACAO_COLABORADOR_CODIGO FOREIGN KEY (COD_COLABORADOR_INVALIDACAO)
        REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT UNICA_INVALIDACAO_SOCORRO UNIQUE (COD_SOCORRO_ROTA)
);

-- Cria tabela para o status de finalização
CREATE TABLE IF NOT EXISTS SOCORRO_ROTA_FINALIZACAO
(
    CODIGO                                  BIGSERIAL                NOT NULL,
    COD_SOCORRO_ROTA                        BIGINT                   NOT NULL,
    COD_COLABORADOR_FINALIZACAO             BIGINT                   NOT NULL,
    OBSERVACAO_FINALIZACAO                  TEXT                     NOT NULL,
    DATA_HORA_FINALIZACAO                   TIMESTAMP WITH TIME ZONE NOT NULL,
    URL_FOTO_1_FINALIZACAO                  TEXT,
    URL_FOTO_2_FINALIZACAO                  TEXT,
    URL_FOTO_3_FINALIZACAO                  TEXT,
    LATITUDE_FINALIZACAO                    TEXT                     NOT NULL,
    LONGITUDE_FINALIZACAO                   TEXT                     NOT NULL,
    PRECISAO_LOCALIZACAO_FINALIZACAO_METROS NUMERIC(5, 2)            NOT NULL,
    ENDERECO_AUTOMATICO                     TEXT,
    VERSAO_APP_MOMENTO_FINALIZACAO          BIGINT                   NOT NULL,
    DEVICE_ID_FINALIZACAO                   TEXT,
    DEVICE_IMEI_FINALIZACAO                 TEXT,
    DEVICE_UPTIME_MILLIS_FINALIZACAO        BIGINT,
    ANDROID_API_VERSION_FINALIZACAO         INTEGER,
    MARCA_DEVICE_FINALIZACAO                TEXT,
    MODELO_DEVICE_FINALIZACAO               TEXT,
    CONSTRAINT PK_SOCORRO_ROTA_FINALIZACAO PRIMARY KEY (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_FINALIZACAO_SOCORRO_ROTA FOREIGN KEY (COD_SOCORRO_ROTA)
        REFERENCES SOCORRO_ROTA (CODIGO),
    CONSTRAINT FK_SOCORRO_ROTA_FINALIZACAO_COLABORADOR_CODIGO FOREIGN KEY (COD_COLABORADOR_FINALIZACAO)
        REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT UNICA_FINALIZACAO_SOCORRO UNIQUE (COD_SOCORRO_ROTA)
);

-- Cria a function de abertura para solitação de socorro
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ABERTURA(F_COD_UNIDADE BIGINT,
                                                      F_COD_COLABORADOR_ABERTURA BIGINT,
                                                      F_COD_VEICULO_PROBLEMA BIGINT,
                                                      F_KM_VEICULO_ABERTURA BIGINT,
                                                      F_COD_PROBLEMA_SOCORRO_ROTA BIGINT,
                                                      F_DESCRICAO_PROBLEMA TEXT,
                                                      F_DATA_HORA_ABERTURA TIMESTAMP WITH TIME ZONE,
                                                      F_URL_FOTO_1_ABERTURA TEXT,
                                                      F_URL_FOTO_2_ABERTURA TEXT,
                                                      F_URL_FOTO_3_ABERTURA TEXT,
                                                      F_LATITUDE_ABERTURA TEXT,
                                                      F_LONGITUDE_ABERTURA TEXT,
                                                      F_PRECISAO_LOCALIZACAO_ABERTURA_METROS NUMERIC,
                                                      F_ENDERECO_AUTOMATICO TEXT,
                                                      F_PONTO_REFERENCIA TEXT,
                                                      F_VERSAO_APP_MOMENTO_ABERTURA BIGINT,
                                                      F_DEVICE_ID_ABERTURA TEXT,
                                                      F_DEVICE_IMEI_ABERTURA TEXT,
                                                      F_DEVICE_UPTIME_MILLIS_ABERTURA BIGINT,
                                                      F_ANDROID_API_VERSION_ABERTURA INTEGER,
                                                      F_MARCA_DEVICE_ABERTURA TEXT,
                                                      F_MODELO_DEVICE_ABERTURA TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_SOCORRO_INSERIDO          BIGINT;
    F_COD_SOCORRO_ABERTURA_INSERIDO BIGINT;
BEGIN
    INSERT INTO SOCORRO_ROTA (COD_UNIDADE, STATUS_ATUAL)
    VALUES (F_COD_UNIDADE, 'ABERTO')
    RETURNING CODIGO INTO F_COD_SOCORRO_INSERIDO;

    IF F_COD_SOCORRO_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    INSERT INTO SOCORRO_ROTA_ABERTURA (COD_SOCORRO_ROTA,
                                       COD_COLABORADOR_ABERTURA,
                                       COD_VEICULO_PROBLEMA,
                                       KM_VEICULO_ABERTURA,
                                       COD_PROBLEMA_SOCORRO_ROTA,
                                       DESCRICAO_PROBLEMA,
                                       DATA_HORA_ABERTURA,
                                       URL_FOTO_1_ABERTURA,
                                       URL_FOTO_2_ABERTURA,
                                       URL_FOTO_3_ABERTURA,
                                       LATITUDE_ABERTURA,
                                       LONGITUDE_ABERTURA,
                                       PRECISAO_LOCALIZACAO_ABERTURA_METROS,
                                       ENDERECO_AUTOMATICO,
                                       PONTO_REFERENCIA,
                                       VERSAO_APP_MOMENTO_ABERTURA,
                                       DEVICE_ID_ABERTURA,
                                       DEVICE_IMEI_ABERTURA,
                                       DEVICE_UPTIME_MILLIS_ABERTURA,
                                       ANDROID_API_VERSION_ABERTURA,
                                       MARCA_DEVICE_ABERTURA,
                                       MODELO_DEVICE_ABERTURA)
    VALUES (F_COD_SOCORRO_INSERIDO,
            F_COD_COLABORADOR_ABERTURA,
            F_COD_VEICULO_PROBLEMA,
            F_KM_VEICULO_ABERTURA,
            F_COD_PROBLEMA_SOCORRO_ROTA,
            F_DESCRICAO_PROBLEMA,
            F_DATA_HORA_ABERTURA,
            F_URL_FOTO_1_ABERTURA,
            F_URL_FOTO_2_ABERTURA,
            F_URL_FOTO_3_ABERTURA,
            F_LATITUDE_ABERTURA,
            F_LONGITUDE_ABERTURA,
            F_PRECISAO_LOCALIZACAO_ABERTURA_METROS,
            F_ENDERECO_AUTOMATICO,
            F_PONTO_REFERENCIA,
            F_VERSAO_APP_MOMENTO_ABERTURA,
            F_DEVICE_ID_ABERTURA,
            F_DEVICE_IMEI_ABERTURA,
            F_DEVICE_UPTIME_MILLIS_ABERTURA,
            F_ANDROID_API_VERSION_ABERTURA,
            F_MARCA_DEVICE_ABERTURA,
            F_MODELO_DEVICE_ABERTURA)
    RETURNING CODIGO INTO F_COD_SOCORRO_ABERTURA_INSERIDO;

    IF F_COD_SOCORRO_ABERTURA_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    END IF;

    RETURN F_COD_SOCORRO_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2424
-- Cria function de listagem de socorros em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_LISTAGEM(F_COD_UNIDADES BIGINT[],
                                                      F_DATA_INICIAL DATE,
                                                      F_DATA_FINAL DATE,
                                                      F_TOKEN TEXT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                          BIGINT,
                UNIDADE                                   TEXT,
                PLACA_VEICULO                             TEXT,
                VEICULO_DELETADO                          BOOLEAN,
                NOME_RESPONSAVEL_ABERTURA_SOCORRO         TEXT,
                COLABORADOR_DELETADO                      BOOLEAN,
                DESCRICAO_FORNECIDA_ABERTURA_SOCORRO      TEXT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO TEXT,
                DATA_HORA_ABERTURA_SOCORRO                TIMESTAMP WITHOUT TIME ZONE,
                ENDERECO_AUTOMATICO_ABERTURA_SOCORRO      TEXT,
                STATUS_ATUAL_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Permissões para ver todos os socorros em rota
    -- 146 - TRATAR_SOCORRO
    -- 147 - VISUALIZAR_SOCORROS_E_RELATORIOS
    F_PERMISSOES_VISUALIZAR_TODOS INTEGER[] := ARRAY [146,147];
    F_VER_TODOS                   BOOLEAN   := (SELECT POSSUI_PERMISSSAO
                                                FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN,
                                                                                                F_PERMISSOES_VISUALIZAR_TODOS,
                                                                                                FALSE,
                                                                                                TRUE));
    F_COD_COLABORADOR             BIGINT    := (SELECT COD_COLABORADOR
                                                FROM TOKEN_AUTENTICACAO
                                                WHERE TOKEN = F_TOKEN);
BEGIN
    RETURN QUERY
        SELECT SR.CODIGO                                                      AS COD_SOCORRO_ROTA,
               U.NOME :: TEXT                                                 AS UNIDADE,
               VD.PLACA :: TEXT                                               AS PLACA_VEICULO,
               VD.DELETADO                                                    AS VEICULO_DELETADO,
               CD.NOME :: TEXT                                                AS NOME_RESPONSAVEL,
               CD.DELETADO                                                    AS COLABORADOR_DELETADO,
               SRA.DESCRICAO_PROBLEMA                                         AS DESCRICAO_FORNECIDA,
               SROP.DESCRICAO :: TEXT                                         AS DESCRICAO_OPCAO_PROBLEMA,
               SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_ABERTURA,
               SRA.ENDERECO_AUTOMATICO                                        AS ENDERECO_AUTOMATICO_ABERTURA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                    AS STATUS_ATUAL_SOCORRO
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON U.CODIGO = SR.COD_UNIDADE
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
                 JOIN VEICULO_DATA VD ON SRA.COD_VEICULO_PROBLEMA = VD.CODIGO
                 JOIN COLABORADOR_DATA CD ON SRA.COD_COLABORADOR_ABERTURA = CD.CODIGO
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRA.COD_PROBLEMA_SOCORRO_ROTA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          -- Aplica o filtro por colaborador apenas se não tiver permissão para ver todos
          AND F_IF(F_VER_TODOS, TRUE, SRA.COD_COLABORADOR_ABERTURA = F_COD_COLABORADOR)
          AND (SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) :: DATE
            BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY SRA.DATA_HORA_ABERTURA DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2427
-- Cria function de invalidação de socorros em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_INVALIDACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_INVALIDACAO BIGINT,
                                                         F_MOTIVO_INVALIDACAO TEXT,
                                                         F_DATA_HORA_INVALIDACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_INVALIDACAO TEXT,
                                                         F_URL_FOTO_2_INVALIDACAO TEXT,
                                                         F_URL_FOTO_3_INVALIDACAO TEXT,
                                                         F_LATITUDE_INVALIDACAO TEXT,
                                                         F_LONGITUDE_INVALIDACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_VERSAO_APP_MOMENTO_INVALIDACAO BIGINT,
                                                         F_DEVICE_ID_INVALIDACAO TEXT,
                                                         F_DEVICE_IMEI_INVALIDACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_INVALIDACAO BIGINT,
                                                         F_ANDROID_API_VERSION_INVALIDACAO INTEGER,
                                                         F_MARCA_DEVICE_INVALIDACAO TEXT,
                                                         F_MODELO_DEVICE_INVALIDACAO TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_PERMISSAO_TRATAR_SOCORRO                BOOLEAN;
    F_PERMISSAO_ABERTURA_SOCORRO              BOOLEAN;
    F_STATUS_SOCORRO                          SOCORRO_ROTA_STATUS_TYPE;
    F_COD_SOCORRO_INVALIDACAO_INSERIDO        BIGINT;
    F_QTD_LINHAS_ATUALIZADAS                  BIGINT;
    F_COD_COLABORADOR_ABERTURA       CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ABERTURA
                                                          FROM SOCORRO_ROTA_ABERTURA
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO    CONSTANT BIGINT  := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                          FROM SOCORRO_ROTA_ATENDIMENTO
                                                          WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_PERMISSAO_TRATAR_SOCORRO   CONSTANT INTEGER := 146;
    F_COD_PERMISSAO_ABERTURA_SOCORRO CONSTANT INTEGER := 145;
BEGIN
    -- Verifica se o socorro em rota existe.
    IF F_COD_COLABORADOR_ABERTURA IS NULL
        THEN
        PERFORM THROW_GENERIC_ERROR(
                    'Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está invalidando.
    IF F_COD_COLABORADOR_ATENDIMENTO IS NOT NULL AND F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_INVALIDACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível invalidar um socorro que foi atendido por outro colaborador.');
    END IF;

    F_PERMISSAO_TRATAR_SOCORRO := (SELECT *
                                   FROM
                                       FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                               F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_TRATAR_SOCORRO));
    F_PERMISSAO_ABERTURA_SOCORRO := (SELECT *
                                     FROM
                                         FUNC_COLABORADOR_VERIFICA_POSSUI_FUNCAO_PROLOG(
                                                 F_COD_COLABORADOR_INVALIDACAO, F_COD_PERMISSAO_ABERTURA_SOCORRO));
    F_STATUS_SOCORRO := (SELECT STATUS_ATUAL
                         FROM SOCORRO_ROTA
                         WHERE CODIGO = F_COD_SOCORRO_ROTA);

    -- Caso tenha a permissão de tratar socorros, impede a invalidação caso os status sejam INVALIDO e FINALZADO.
    IF F_PERMISSAO_TRATAR_SOCORRO
    THEN
        IF F_STATUS_SOCORRO = 'INVALIDO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi invalidado.');
        END IF;
        IF F_STATUS_SOCORRO = 'FINALIZADO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele já foi finalizado.');
        END IF;
    -- Caso tenha a permissão de abertura, impede a invalidação de socorros de outros colaboradores e também
    -- de socorros que não estão mais em aberto.
    ELSEIF F_PERMISSAO_ABERTURA_SOCORRO
    THEN
        IF F_COD_COLABORADOR_ABERTURA <> F_COD_COLABORADOR_INVALIDACAO
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Você não tem permissão para invalidar pedidos de socorro de outros colaboradores.');
        END IF;
        IF F_STATUS_SOCORRO <> 'ABERTO'
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível invalidar o socorro, pois ele não está mais em aberto.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                    'Você não ter permissão para invalidar o socorro.');
    END IF;

    INSERT INTO SOCORRO_ROTA_INVALIDACAO (COD_SOCORRO_ROTA,
                                       COD_COLABORADOR_INVALIDACAO,
                                       MOTIVO_INVALIDACAO,
                                       DATA_HORA_INVALIDACAO,
                                       URL_FOTO_1_INVALIDACAO,
                                       URL_FOTO_2_INVALIDACAO,
                                       URL_FOTO_3_INVALIDACAO,
                                       LATITUDE_INVALIDACAO,
                                       LONGITUDE_INVALIDACAO,
                                       PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
                                       ENDERECO_AUTOMATICO,
                                       VERSAO_APP_MOMENTO_INVALIDACAO,
                                       DEVICE_ID_INVALIDACAO,
                                       DEVICE_IMEI_INVALIDACAO,
                                       DEVICE_UPTIME_MILLIS_INVALIDACAO,
                                       ANDROID_API_VERSION_INVALIDACAO,
                                       MARCA_DEVICE_INVALIDACAO,
                                       MODELO_DEVICE_INVALIDACAO)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_INVALIDACAO,
            F_MOTIVO_INVALIDACAO,
            F_DATA_HORA_INVALIDACAO,
            F_URL_FOTO_1_INVALIDACAO,
            F_URL_FOTO_2_INVALIDACAO,
            F_URL_FOTO_3_INVALIDACAO,
            F_LATITUDE_INVALIDACAO,
            F_LONGITUDE_INVALIDACAO,
            F_PRECISAO_LOCALIZACAO_INVALIDACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_VERSAO_APP_MOMENTO_INVALIDACAO,
            F_DEVICE_ID_INVALIDACAO,
            F_DEVICE_IMEI_INVALIDACAO,
            F_DEVICE_UPTIME_MILLIS_INVALIDACAO,
            F_ANDROID_API_VERSION_INVALIDACAO,
            F_MARCA_DEVICE_INVALIDACAO,
            F_MODELO_DEVICE_INVALIDACAO) RETURNING CODIGO INTO F_COD_SOCORRO_INVALIDACAO_INSERIDO;

    IF F_COD_SOCORRO_INVALIDACAO_INSERIDO IS NOT NULL AND F_COD_SOCORRO_INVALIDACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA SET STATUS_ATUAL = 'INVALIDO' WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS IS NULL OR F_QTD_LINHAS_ATUALIZADAS <= 0
            THEN
            PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_INVALIDACAO_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2426
-- Cria function de atendimento de socorros em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_ATENDIMENTO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_ATENDIMENTO BIGINT,
                                                         F_OBSERVACAO_ATENDIMENTO TEXT,
                                                         F_DATA_HORA_ATENDIMENTO TIMESTAMP WITH TIME ZONE,
                                                         F_LATITUDE_ATENDIMENTO TEXT,
                                                         F_LONGITUDE_ATENDIMENTO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_VERSAO_APP_MOMENTO_ATENDIMENTO BIGINT,
                                                         F_DEVICE_ID_ATENDIMENTO TEXT,
                                                         F_DEVICE_IMEI_ATENDIMENTO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_ATENDIMENTO BIGINT,
                                                         F_ANDROID_API_VERSION_ATENDIMENTO INTEGER,
                                                         F_MARCA_DEVICE_ATENDIMENTO TEXT,
                                                         F_MODELO_DEVICE_ATENDIMENTO TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_SOCORRO_ATENDIMENTO_INSERIDO BIGINT;
    F_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe
    IF F_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível localizar o socorro em rota.');
    END IF;

    IF F_STATUS_SOCORRO = 'EM_ATENDIMENTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi atendido.');
    END IF;
    IF F_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi invalidado.');
    END IF;
    IF F_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível atender o socorro, pois ele já foi finalizado.');
    END IF;

    INSERT INTO SOCORRO_ROTA_ATENDIMENTO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_ATENDIMENTO,
                                          OBSERVACAO_ATENDIMENTO,
                                          DATA_HORA_ATENDIMENTO,
                                          LATITUDE_ATENDIMENTO,
                                          LONGITUDE_ATENDIMENTO,
                                          PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          VERSAO_APP_MOMENTO_ATENDIMENTO,
                                          DEVICE_ID_ATENDIMENTO,
                                          DEVICE_IMEI_ATENDIMENTO,
                                          DEVICE_UPTIME_MILLIS_ATENDIMENTO,
                                          ANDROID_API_VERSION_ATENDIMENTO,
                                          MARCA_DEVICE_ATENDIMENTO,
                                          MODELO_DEVICE_ATENDIMENTO)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_ATENDIMENTO,
            F_OBSERVACAO_ATENDIMENTO,
            F_DATA_HORA_ATENDIMENTO,
            F_LATITUDE_ATENDIMENTO,
            F_LONGITUDE_ATENDIMENTO,
            F_PRECISAO_LOCALIZACAO_ATENDIMENTO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_VERSAO_APP_MOMENTO_ATENDIMENTO,
            F_DEVICE_ID_ATENDIMENTO,
            F_DEVICE_IMEI_ATENDIMENTO,
            F_DEVICE_UPTIME_MILLIS_ATENDIMENTO,
            F_ANDROID_API_VERSION_ATENDIMENTO,
            F_MARCA_DEVICE_ATENDIMENTO,
            F_MODELO_DEVICE_ATENDIMENTO)
    RETURNING CODIGO INTO F_COD_SOCORRO_ATENDIMENTO_INSERIDO;

    IF F_COD_SOCORRO_ATENDIMENTO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA SET STATUS_ATUAL = 'EM_ATENDIMENTO' WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar o atendimento desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a atendimento desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_ATENDIMENTO_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2428
-- Cria function de finalização de socorros em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_FINALIZACAO(F_COD_SOCORRO_ROTA BIGINT,
                                                         F_COD_COLABORADOR_FINALIZACAO BIGINT,
                                                         F_MOTIVO_FINALIZACAO TEXT,
                                                         F_DATA_HORA_FINALIZACAO TIMESTAMP WITH TIME ZONE,
                                                         F_URL_FOTO_1_FINALIZACAO TEXT,
                                                         F_URL_FOTO_2_FINALIZACAO TEXT,
                                                         F_URL_FOTO_3_FINALIZACAO TEXT,
                                                         F_LATITUDE_FINALIZACAO TEXT,
                                                         F_LONGITUDE_FINALIZACAO TEXT,
                                                         F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS NUMERIC,
                                                         F_ENDERECO_AUTOMATICO TEXT,
                                                         F_VERSAO_APP_MOMENTO_FINALIZACAO BIGINT,
                                                         F_DEVICE_ID_FINALIZACAO TEXT,
                                                         F_DEVICE_IMEI_FINALIZACAO TEXT,
                                                         F_DEVICE_UPTIME_MILLIS_FINALIZACAO BIGINT,
                                                         F_ANDROID_API_VERSION_FINALIZACAO INTEGER,
                                                         F_MARCA_DEVICE_FINALIZACAO TEXT,
                                                         F_MODELO_DEVICE_FINALIZACAO TEXT) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_STATUS_SOCORRO                   SOCORRO_ROTA_STATUS_TYPE := (SELECT SR.STATUS_ATUAL
                                                                    FROM SOCORRO_ROTA SR
                                                                    WHERE SR.CODIGO = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ABERTURA         BIGINT                   := (SELECT SRA.COD_COLABORADOR_ABERTURA
                                                                    FROM SOCORRO_ROTA_ABERTURA SRA
                                                                    WHERE SRA.COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_COLABORADOR_ATENDIMENTO      BIGINT                   := (SELECT COD_COLABORADOR_ATENDIMENTO
                                                                    FROM SOCORRO_ROTA_ATENDIMENTO
                                                                    WHERE COD_SOCORRO_ROTA = F_COD_SOCORRO_ROTA);
    F_COD_SOCORRO_FINALIZACAO_INSERIDO BIGINT;
    F_QTD_LINHAS_ATUALIZADAS           BIGINT;
BEGIN
    -- Verifica se o socorro em rota existe
    IF F_COD_COLABORADOR_ABERTURA IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível localizar o socorro em rota.');
    END IF;

    -- Verifica se o socorro em rota foi atendido por quem está finalizando
    IF F_COD_COLABORADOR_ATENDIMENTO != F_COD_COLABORADOR_FINALIZACAO
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não é possível finalizar um socorro que foi atendido por outro colaborador.');
    END IF;

    IF F_STATUS_SOCORRO = 'ABERTO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele ainda não foi atendido.');
    END IF;
    IF F_STATUS_SOCORRO = 'INVALIDO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi invalidado.');
    END IF;
    IF F_STATUS_SOCORRO = 'FINALIZADO'
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível finalizar o socorro, pois ele já foi finalizado.');
    END IF;

    INSERT INTO SOCORRO_ROTA_FINALIZACAO (COD_SOCORRO_ROTA,
                                          COD_COLABORADOR_FINALIZACAO,
                                          OBSERVACAO_FINALIZACAO,
                                          DATA_HORA_FINALIZACAO,
                                          URL_FOTO_1_FINALIZACAO,
                                          URL_FOTO_2_FINALIZACAO,
                                          URL_FOTO_3_FINALIZACAO,
                                          LATITUDE_FINALIZACAO,
                                          LONGITUDE_FINALIZACAO,
                                          PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
                                          ENDERECO_AUTOMATICO,
                                          VERSAO_APP_MOMENTO_FINALIZACAO,
                                          DEVICE_ID_FINALIZACAO,
                                          DEVICE_IMEI_FINALIZACAO,
                                          DEVICE_UPTIME_MILLIS_FINALIZACAO,
                                          ANDROID_API_VERSION_FINALIZACAO,
                                          MARCA_DEVICE_FINALIZACAO,
                                          MODELO_DEVICE_FINALIZACAO)
    VALUES (F_COD_SOCORRO_ROTA,
            F_COD_COLABORADOR_FINALIZACAO,
            F_MOTIVO_FINALIZACAO,
            F_DATA_HORA_FINALIZACAO,
            F_URL_FOTO_1_FINALIZACAO,
            F_URL_FOTO_2_FINALIZACAO,
            F_URL_FOTO_3_FINALIZACAO,
            F_LATITUDE_FINALIZACAO,
            F_LONGITUDE_FINALIZACAO,
            F_PRECISAO_LOCALIZACAO_FINALIZACAO_METROS,
            F_ENDERECO_AUTOMATICO,
            F_VERSAO_APP_MOMENTO_FINALIZACAO,
            F_DEVICE_ID_FINALIZACAO,
            F_DEVICE_IMEI_FINALIZACAO,
            F_DEVICE_UPTIME_MILLIS_FINALIZACAO,
            F_ANDROID_API_VERSION_FINALIZACAO,
            F_MARCA_DEVICE_FINALIZACAO,
            F_MODELO_DEVICE_FINALIZACAO)
    RETURNING CODIGO INTO F_COD_SOCORRO_FINALIZACAO_INSERIDO;

    IF F_COD_SOCORRO_FINALIZACAO_INSERIDO > 0
    THEN
        UPDATE SOCORRO_ROTA SET STATUS_ATUAL = 'FINALIZADO' WHERE CODIGO = F_COD_SOCORRO_ROTA;
        GET DIAGNOSTICS F_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF F_QTD_LINHAS_ATUALIZADAS <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
        END IF;
    ELSE
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível realizar a invalidação desse socorro em rota, tente novamente.');
    END IF;

    RETURN F_COD_SOCORRO_FINALIZACAO_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2425
-- Cria function de visualização de socorro em rota
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_VISUALIZACAO(F_COD_SOCORRO_ROTA BIGINT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                    BIGINT,
                STATUS_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE,
                PLACA_VEICULO_ABERTURA              TEXT,
                COD_COLABORADOR_ABERTURA            BIGINT,
                NOME_RESPONSAVEL_ABERTURA           TEXT,
                KM_VEICULO_COLETADO_ABERTURA        BIGINT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA   TEXT,
                DESCRICAO_FORNECIDA_ABERTURA        TEXT,
                PONTO_REFERENCIA_FORNECIDO_ABERTURA TEXT,
                DATA_HORA_ABERTURA                  TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ABERTURA                   TEXT,
                LONGITUDE_ABERTURA                  TEXT,
                ENDERECO_AUTOMATICO_ABERTURA        TEXT,
                MARCA_APARELHO_ABERTURA             TEXT,
                MODELO_APARELHO_ABERTURA            TEXT,
                IMEI_APARELHO_ABERTURA              TEXT,
                URL_FOTO_1_ABERTURA                 TEXT,
                URL_FOTO_2_ABERTURA                 TEXT,
                URL_FOTO_3_ABERTURA                 TEXT,
                COD_COLABORADOR_ATENDIMENTO         BIGINT,
                NOME_RESPONSAVEL_ATENDIMENTO        TEXT,
                OBSERVACAO_ATENDIMENTO              TEXT,
                DATA_HORA_ATENDIMENTO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_ATENDIMENTO                TEXT,
                LONGITUDE_ATENDIMENTO               TEXT,
                ENDERECO_AUTOMATICO_ATENDIMENTO     TEXT,
                MARCA_APARELHO_ATENDIMENTO          TEXT,
                MODELO_APARELHO_ATENDIMENTO         TEXT,
                IMEI_APARELHO_ATENDIMENTO           TEXT,
                COD_COLABORADOR_INVALIDACAO         BIGINT,
                NOME_RESPONSAVEL_INVALIDACAO        TEXT,
                MOTIVO_INVALIDACAO                  TEXT,
                DATA_HORA_INVALIDACAO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_INVALIDACAO                TEXT,
                LONGITUDE_INVALIDACAO               TEXT,
                ENDERECO_AUTOMATICO_INVALIDACAO     TEXT,
                MARCA_APARELHO_INVALIDACAO          TEXT,
                MODELO_APARELHO_INVALIDACAO         TEXT,
                IMEI_APARELHO_INVALIDACAO           TEXT,
                URL_FOTO_1_INVALIDACAO              TEXT,
                URL_FOTO_2_INVALIDACAO              TEXT,
                URL_FOTO_3_INVALIDACAO              TEXT,
                COD_COLABORADOR_FINALIZACAO         BIGINT,
                NOME_RESPONSAVEL_FINALIZACAO        TEXT,
                OBSERVACAO_FINALIZACAO              TEXT,
                DATA_HORA_FINALIZACAO               TIMESTAMP WITHOUT TIME ZONE,
                LATITUDE_FINALIZACAO                TEXT,
                LONGITUDE_FINALIZACAO               TEXT,
                ENDERECO_AUTOMATICO_FINALIZACAO     TEXT,
                MARCA_APARELHO_FINALIZACAO          TEXT,
                MODELO_APARELHO_FINALIZACAO         TEXT,
                IMEI_APARELHO_FINALIZACAO           TEXT,
                URL_FOTO_1_FINALIZACAO              TEXT,
                URL_FOTO_2_FINALIZACAO              TEXT,
                URL_FOTO_3_FINALIZACAO              TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT SR.CODIGO                                                          AS COD_SOCORRO_ROTA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                        AS STATUS_SOCORRO_ROTA,
               V.PLACA :: TEXT                                                    AS PLACA_VEICULO_ABERTURA,
               SRAB.COD_COLABORADOR_ABERTURA                                      AS COD_COLABORADOR_ABERTURA,
               CDAB.NOME :: TEXT                                                  AS NOME_RESPONSAVEL_ABERTURA,
               SRAB.KM_VEICULO_ABERTURA                                           AS KM_VEICULO_COLETADO_ABERTURA,
               SROP.DESCRICAO :: TEXT                                             AS DESCRICAO_OPCAO_PROBLEMA_ABERTURA,
               SRAB.DESCRICAO_PROBLEMA :: TEXT                                    AS DESCRICAO_FORNECIDA_ABERTURA,
               SRAB.PONTO_REFERENCIA :: TEXT                                      AS PONTO_REFERENCIA_FORNECIDO_ABERTURA,
               SRAB.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)    AS DATA_HORA_ABERTURA,
               SRAB.LATITUDE_ABERTURA :: TEXT                                     AS LATITUDE_ABERTURA,
               SRAB.LONGITUDE_ABERTURA :: TEXT                                    AS LONGITUDE_ABERTURA,
               SRAB.ENDERECO_AUTOMATICO :: TEXT                                   AS ENDERECO_AUTOMATICO_ABERTURA,
               SRAB.MARCA_DEVICE_ABERTURA :: TEXT                                 AS MARCA_APARELHO_ABERTURA,
               SRAB.MODELO_DEVICE_ABERTURA :: TEXT                                AS MODELO_APARELHO_ABERTURA,
               SRAB.DEVICE_IMEI_ABERTURA :: TEXT                                  AS IMEI_APARELHO_ABERTURA,
               SRAB.URL_FOTO_1_ABERTURA :: TEXT                                   AS URL_FOTO_1_ABERTURA,
               SRAB.URL_FOTO_2_ABERTURA :: TEXT                                   AS URL_FOTO_2_ABERTURA,
               SRAB.URL_FOTO_3_ABERTURA :: TEXT                                   AS URL_FOTO_3_ABERTURA,
               SRAT.COD_COLABORADOR_ATENDIMENTO                                   AS COD_COLABORADOR_ATENDIMENTO,
               CDAT.NOME :: TEXT                                                  AS NOME_RESPONSAVEL_ATENDIMENTO,
               SRAT.OBSERVACAO_ATENDIMENTO :: TEXT                                AS OBSERVACAO_ATENDIMENTO,
               SRAT.DATA_HORA_ATENDIMENTO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_ATENDIMENTO,
               SRAT.LATITUDE_ATENDIMENTO :: TEXT                                  AS LATITUDE_ATENDIMENTO,
               SRAT.LONGITUDE_ATENDIMENTO :: TEXT                                 AS LONGITUDE_ATENDIMENTO,
               SRAT.ENDERECO_AUTOMATICO :: TEXT                                   AS ENDERECO_AUTOMATICO_ATENDIMENTO,
               SRAT.MARCA_DEVICE_ATENDIMENTO :: TEXT                              AS MARCA_APARELHO_ATENDIMENTO,
               SRAT.MODELO_DEVICE_ATENDIMENTO :: TEXT                             AS MODELO_APARELHO_ATENDIMENTO,
               SRAT.DEVICE_IMEI_ATENDIMENTO :: TEXT                               AS IMEI_APARELHO_ATENDIMENTO,
               SRI.COD_COLABORADOR_INVALIDACAO                                    AS COD_COLABORADOR_INVALIDACAO,
               CDI.NOME :: TEXT                                                   AS NOME_RESPONSAVEL_INVALIDACAO,
               SRI.MOTIVO_INVALIDACAO :: TEXT                                     AS MOTIVO_INVALIDACAO,
               SRI.DATA_HORA_INVALIDACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)  AS DATA_HORA_INVALIDACAO,
               SRI.LATITUDE_INVALIDACAO :: TEXT                                   AS LATITUDE_INVALIDACAO,
               SRI.LONGITUDE_INVALIDACAO :: TEXT                                  AS LONGITUDE_INVALIDACAO,
               SRI.ENDERECO_AUTOMATICO :: TEXT                                    AS ENDERECO_AUTOMATICO_INVALIDACAO,
               SRI.MARCA_DEVICE_INVALIDACAO :: TEXT                               AS MARCA_APARELHO_INVALIDACAO,
               SRI.MODELO_DEVICE_INVALIDACAO :: TEXT                              AS MODELO_APARELHO_INVALIDACAO,
               SRI.DEVICE_IMEI_INVALIDACAO :: TEXT                                AS IMEI_APARELHO_INVALIDACAO,
               SRI.URL_FOTO_1_INVALIDACAO :: TEXT                                 AS URL_FOTO_1_INVALIDACAO,
               SRI.URL_FOTO_2_INVALIDACAO :: TEXT                                 AS URL_FOTO_2_INVALIDACAO,
               SRI.URL_FOTO_3_INVALIDACAO :: TEXT                                 AS URL_FOTO_3_INVALIDACAO,
               SRF.COD_COLABORADOR_FINALIZACAO                                    AS COD_COLABORADOR_FINALIZACAO,
               CDF.NOME :: TEXT                                                   AS NOME_RESPONSAVEL_FINALIZACAO,
               SRF.OBSERVACAO_FINALIZACAO :: TEXT                                 AS OBSERVACAO_FINALIZACAO,
               SRF.DATA_HORA_FINALIZACAO AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)  AS DATA_HORA_FINALIZACAO,
               SRF.LATITUDE_FINALIZACAO :: TEXT                                   AS LATITUDE_FINALIZACAO,
               SRF.LONGITUDE_FINALIZACAO :: TEXT                                  AS LONGITUDE_FINALIZACAO,
               SRF.ENDERECO_AUTOMATICO :: TEXT                                    AS ENDERECO_AUTOMATICO_FINALIZACAO,
               SRF.MARCA_DEVICE_FINALIZACAO :: TEXT                               AS MARCA_APARELHO_FINALIZACAO,
               SRF.MODELO_DEVICE_FINALIZACAO :: TEXT                              AS MODELO_APARELHO_FINALIZACAO,
               SRF.DEVICE_IMEI_FINALIZACAO :: TEXT                                AS IMEI_APARELHO_FINALIZACAO,
               SRF.URL_FOTO_1_FINALIZACAO :: TEXT                                 AS URL_FOTO_1_FINALIZACAO,
               SRF.URL_FOTO_2_FINALIZACAO :: TEXT                                 AS URL_FOTO_2_FINALIZACAO,
               SRF.URL_FOTO_3_FINALIZACAO :: TEXT                                 AS URL_FOTO_3_FINALIZACAO
        FROM SOCORRO_ROTA SR
                 JOIN SOCORRO_ROTA_ABERTURA SRAB ON SR.CODIGO = SRAB.COD_SOCORRO_ROTA
                 JOIN VEICULO_DATA V ON V.CODIGO = SRAB.COD_VEICULO_PROBLEMA
                 JOIN COLABORADOR_DATA CDAB ON CDAB.CODIGO = SRAB.COD_COLABORADOR_ABERTURA
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRAB.COD_PROBLEMA_SOCORRO_ROTA
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SR.CODIGO = SRAT.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              CDAT.CODIGO = SRAT.COD_COLABORADOR_ATENDIMENTO
                 LEFT JOIN SOCORRO_ROTA_INVALIDACAO SRI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND SR.CODIGO = SRI.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDI
                           ON SR.STATUS_ATUAL = 'INVALIDO' AND CDI.CODIGO = SRI.COD_COLABORADOR_INVALIDACAO
                 LEFT JOIN SOCORRO_ROTA_FINALIZACAO SRF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND SR.CODIGO = SRF.COD_SOCORRO_ROTA
                 LEFT JOIN COLABORADOR_DATA CDF
                           ON SR.STATUS_ATUAL = 'FINALIZADO' AND CDF.CODIGO = SRF.COD_COLABORADOR_FINALIZACAO
        WHERE SR.CODIGO = F_COD_SOCORRO_ROTA;
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível encontrar esse socorro');
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2465
-- Cria function de listagem das opções de problemas com base no código de empresa
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_OPCOES_PROBLEMAS_LISTAGEM(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                COD_OPCAO_PROBLEMA BIGINT,
                DESCRICAO          CITEXT,
                OBRIGA_DESCRICAO   BOOLEAN,
                STATUS_ATIVO       BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT SROP.CODIGO,
               SROP.DESCRICAO,
               SROP.OBRIGA_DESCRICAO,
               SROP.STATUS_ATIVO
        FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
        WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
        ORDER BY SROP.STATUS_ATIVO DESC, SROP.DESCRICAO ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2466
-- Cria function para inserir opção de problema
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_INSERT_OPCOES_PROBLEMAS(F_COD_EMPRESA BIGINT,
                                                                     F_DESCRICAO TEXT,
                                                                     F_OBRIGA_DESCRICAO BOOLEAN,
                                                                     F_COD_COLABORADOR BIGINT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE) RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_OPCAO_PROBLEMA_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT SROP.CODIGO
              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
              WHERE UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_DESCRICAO))
                AND SROP.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma descrição \'%s\' cadastrada nesta empresa', F_DESCRICAO));
    END IF;

    INSERT INTO SOCORRO_ROTA_OPCAO_PROBLEMA (COD_EMPRESA, DESCRICAO, OBRIGA_DESCRICAO,
                                             COD_COLABORADOR_ULTIMA_ATUALIZACAO, DATA_HORA_ULTIMA_ATUALIZACAO)
    VALUES (F_COD_EMPRESA, F_DESCRICAO, F_OBRIGA_DESCRICAO, F_COD_COLABORADOR,
            F_DATA_HORA)
    RETURNING CODIGO INTO F_COD_OPCAO_PROBLEMA_INSERIDO;

    IF F_COD_OPCAO_PROBLEMA_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir a opção de problema, tente novamente');
    END IF;

    RETURN F_COD_OPCAO_PROBLEMA_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2467
-- Cria function para editar uma opção de problema
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_UPDATE_OPCOES_PROBLEMAS(F_COD_OPCAO_PROBLEMA BIGINT,
                                                                     F_COD_EMPRESA BIGINT,
                                                                     F_DESCRICAO TEXT,
                                                                     F_OBRIGA_DESCRICAO BOOLEAN,
                                                                     F_COD_COLABORADOR BIGINT,
                                                                     F_DATA_HORA TIMESTAMP WITH TIME ZONE) RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Todos os campos a seguir devem ser buscados no registro anterior, antes da alteração
    -- Estes campos serão adicionados na tabela de histórico
    F_OLD_DESCRICAO                          TEXT;
    F_OLD_OBRIGA_DESCRICAO                   BOOLEAN;
    F_OLD_STATUS_ATIVO                       BOOLEAN;
    F_OLD_DATA_HORA_ALTERACAO                TIMESTAMP WITH TIME ZONE;
    F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    F_COD_HISTORICO_ALTERACAO                BIGINT;
BEGIN
    IF EXISTS(SELECT SROP.CODIGO
              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
              WHERE UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_DESCRICAO))
                AND SROP.CODIGO != F_COD_OPCAO_PROBLEMA
                AND SROP.STATUS_ATIVO
                AND SROP.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma descrição \'%s\' cadastrada na empresa', F_DESCRICAO));
    END IF;

    SELECT DESCRICAO, OBRIGA_DESCRICAO, DATA_HORA_ULTIMA_ATUALIZACAO, COD_COLABORADOR_ULTIMA_ATUALIZACAO, STATUS_ATIVO
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA
    WHERE CODIGO = F_COD_OPCAO_PROBLEMA
    INTO F_OLD_DESCRICAO, F_OLD_OBRIGA_DESCRICAO, F_OLD_DATA_HORA_ALTERACAO, F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
        F_OLD_STATUS_ATIVO;

    IF F_OLD_DESCRICAO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar a opção de problema.');
    END IF;

    UPDATE SOCORRO_ROTA_OPCAO_PROBLEMA
    SET DESCRICAO                          = F_DESCRICAO,
        OBRIGA_DESCRICAO                   = F_OBRIGA_DESCRICAO,
        COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
        DATA_HORA_ULTIMA_ATUALIZACAO       = F_DATA_HORA
    WHERE CODIGO = F_COD_OPCAO_PROBLEMA;

    -- FOUND será true se alguma linha foi modificada pela query executada.
    IF NOT FOUND THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível editar a opção de problema.');
    END IF;

    INSERT INTO SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO (COD_PROBLEMA_SOCORRO_ROTA,
                                                       COD_COLABORADOR_ALTERACAO,
                                                       DATA_HORA_ALTERACAO,
                                                       COD_EMPRESA,
                                                       DESCRICAO,
                                                       OBRIGA_DESCRICAO,
                                                       STATUS_ATIVO)
    VALUES (F_COD_OPCAO_PROBLEMA,
            F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
            F_OLD_DATA_HORA_ALTERACAO,
            F_COD_EMPRESA,
            F_OLD_DESCRICAO,
            F_OLD_OBRIGA_DESCRICAO,
            F_OLD_STATUS_ATIVO)
    RETURNING CODIGO INTO F_COD_HISTORICO_ALTERACAO;

    IF NOT FOUND OR F_COD_HISTORICO_ALTERACAO IS NULL OR F_COD_HISTORICO_ALTERACAO <= 0 THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível criar um histórico para a edição de problema, ' ||
                                    'contate nosso suporte.');
    END IF;

    RETURN TRUE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################# FUNCTION PARA RETORNAR UMA OPÇÃO DE PROBLEMA ESPECÍFICA ################################
--######################################################################################################################
--######################################################################################################################
-- PL-2468
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_OPCAO_PROBLEMA_ITEM(F_COD_OPCAO_PROBLEMA BIGINT)
    RETURNS TABLE
            (
                COD_OPCAO_PROBLEMA                  BIGINT,
                DESCRICAO                           TEXT,
                OBRIGA_DESCRICAO                    BOOLEAN,
                STATUS_ATIVO                        BOOLEAN,
                NOME_COLABORADOR_ULTIMA_ATUALIZACAO TEXT,
                DATA_HORA_ULTIMA_ATUALIZACAO        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT SROP.CODIGO,
               SROP.DESCRICAO :: TEXT,
               SROP.OBRIGA_DESCRICAO,
               SROP.STATUS_ATIVO,
               (SELECT CD.NOME
                FROM COLABORADOR_DATA CD
                WHERE CD.CODIGO = SROP.COD_COLABORADOR_ULTIMA_ATUALIZACAO)::TEXT,
               TO_CHAR((SROP.DATA_HORA_ULTIMA_ATUALIZACAO), 'DD/MM/YYYY HH24:MI')
        FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
        WHERE SROP.CODIGO = F_COD_OPCAO_PROBLEMA;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível encontrar a opção de problema');
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################### CRIA FUNCTION PARA ATIVAR/DESATIVAR UMA OPÇÃO DE PROBLEMA ############################
--######################################################################################################################
--######################################################################################################################
-- PL-2478
CREATE OR REPLACE FUNCTION FUNC_SOCORRO_ROTA_UPDATE_STATUS_OPCAO_PROBLEMA(F_COD_EMPRESA BIGINT,
                                                                          F_COD_COLABORADOR BIGINT,
                                                                          F_COD_OPCAO_PROBLEMA BIGINT,
                                                                          F_STATUS_ATIVO BOOLEAN)
    RETURNS VOID
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_CPF_COLABORADOR                        BIGINT := (SELECT CD.CPF
                                                        FROM COLABORADOR_DATA CD
                                                        WHERE CD.CODIGO = F_COD_COLABORADOR);
    -- Todos os campos a seguir devem ser buscados no registro anterior, antes da alteração
    -- Estes campos serão adicionados na tabela de histórico.
    F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    F_OLD_DATA_HORA_ALTERACAO                TIMESTAMP WITH TIME ZONE;
    F_OLD_DESCRICAO                          TEXT;
    F_OLD_OBRIGA_DESCRICAO                   BOOLEAN;
    F_OLD_STATUS_ATIVO                       BOOLEAN;
    F_COD_HISTORICO_ALTERACAO                BIGINT;

BEGIN
    -- VERIFICA SE EMPRESA EXISTE.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(F_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Busca registro para histórico.
    SELECT SROP.COD_COLABORADOR_ULTIMA_ATUALIZACAO,
           SROP.DATA_HORA_ULTIMA_ATUALIZACAO,
           SROP.DESCRICAO,
           SROP.OBRIGA_DESCRICAO,
           SROP.STATUS_ATIVO
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
    WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
      AND SROP.CODIGO = F_COD_OPCAO_PROBLEMA
    INTO F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
        F_OLD_DATA_HORA_ALTERACAO,
        F_OLD_DESCRICAO,
        F_OLD_OBRIGA_DESCRICAO,
        F_OLD_STATUS_ATIVO;

    IF F_OLD_DESCRICAO IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar a opção de problema.');
    END IF;

    -- Se estamos ativando uma opcao de problema e existe outra na mesma empresa, de mesma descrição e já ativa, é
    -- lançado erro.
    IF F_STATUS_ATIVO AND (SELECT EXISTS(SELECT SROP.CODIGO
                                         FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
                                         WHERE SROP.COD_EMPRESA = F_COD_EMPRESA
                                           AND UNACCENT(TRIM(SROP.DESCRICAO)) ILIKE UNACCENT(TRIM(F_OLD_DESCRICAO))
                                           AND SROP.STATUS_ATIVO = TRUE))
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro! Já existe uma opção de problema ativa com esse nome.');
    END IF;

    UPDATE SOCORRO_ROTA_OPCAO_PROBLEMA
    SET STATUS_ATIVO                       = F_STATUS_ATIVO,
        COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
        DATA_HORA_ULTIMA_ATUALIZACAO       = NOW()
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND CODIGO = F_COD_OPCAO_PROBLEMA;

    IF NOT FOUND
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o status da opção de problema % para %', F_OLD_DESCRICAO, F_STATUS_ATIVO;
    END IF;

    INSERT INTO SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO (COD_PROBLEMA_SOCORRO_ROTA,
                                                       COD_COLABORADOR_ALTERACAO,
                                                       DATA_HORA_ALTERACAO,
                                                       COD_EMPRESA,
                                                       DESCRICAO,
                                                       OBRIGA_DESCRICAO,
                                                       STATUS_ATIVO)
    VALUES (F_COD_OPCAO_PROBLEMA,
            F_OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
            F_OLD_DATA_HORA_ALTERACAO,
            F_COD_EMPRESA,
            F_OLD_DESCRICAO,
            F_OLD_OBRIGA_DESCRICAO,
            F_STATUS_ATIVO)
    RETURNING CODIGO INTO F_COD_HISTORICO_ALTERACAO;

    IF NOT FOUND OR F_COD_HISTORICO_ALTERACAO IS NULL OR F_COD_HISTORICO_ALTERACAO <= 0 THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível criar um histórico para a mudança de status de opção de' ||
                                    ' problema, contate nosso suporte.');
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################## ALTERAÇÕES EM COLABORADOR PARA SALVAR TELEFONE E E-MAIL ###############################
--######################################################################################################################
--######################################################################################################################
-- PL-2471
-- Cria tabela de países a nível prolog.

-- Cria a tabela de países
CREATE TABLE PROLOG_PAISES
(
    CODIGO           SERIAL      NOT NULL
        CONSTRAINT PK_PROLOG_PAISES
            PRIMARY KEY,
    NOME_UPPER       VARCHAR(80) NOT NULL,
    NOME             VARCHAR(80) NOT NULL,
    SIGLA_ISO2       CHAR(2)     NOT NULL
        CONSTRAINT PROLOG_PAISES_SIGLA_ISO2_KEY
            UNIQUE,
    SIGLA_ISO3       CHAR(3) DEFAULT NULL::BPCHAR,
    NUMERO_UN        SMALLINT,
    PREFIXO_TELEFONE INTEGER     NOT NULL,
    CONSTRAINT UNIQUE_SIGLA_ISO2_PREFIXO_TELEFONE UNIQUE (SIGLA_ISO2, PREFIXO_TELEFONE)
);

-- Preenche a tabela com a lista de países do universo e tudo mais
INSERT INTO prolog_paises (codigo, sigla_iso2, nome_upper, nome, sigla_iso3, numero_un, prefixo_telefone) VALUES
(1, 'AF', 'AFGHANISTAN', 'Afghanistan', 'AFG', 4, 93),
(2, 'AL', 'ALBANIA', 'Albania', 'ALB', 8, 355),
(3, 'DZ', 'ALGERIA', 'Algeria', 'DZA', 12, 213),
(4, 'AS', 'AMERICAN SAMOA', 'American Samoa', 'ASM', 16, 1),
(5, 'AD', 'ANDORRA', 'Andorra', 'AND', 20, 376),
(6, 'AO', 'ANGOLA', 'Angola', 'AGO', 24, 244),
(7, 'AI', 'ANGUILLA', 'Anguilla', 'AIA', 660, 1),
(8, 'AQ', 'ANTARCTICA', 'Antarctica', 'ATA', 10, 0),
(9, 'AG', 'ANTIGUA AND BARBUDA', 'Antigua and Barbuda', 'ATG', 28, 1),
(10, 'AR', 'ARGENTINA', 'Argentina', 'ARG', 32, 54),
(11, 'AM', 'ARMENIA', 'Armenia', 'ARM', 51, 374),
(12, 'AW', 'ARUBA', 'Aruba', 'ABW', 533, 297),
(13, 'AU', 'AUSTRALIA', 'Australia', 'AUS', 36, 61),
(14, 'AT', 'AUSTRIA', 'Austria', 'AUT', 40, 43),
(15, 'AZ', 'AZERBAIJAN', 'Azerbaijan', 'AZE', 31, 994),
(16, 'BS', 'BAHAMAS', 'Bahamas', 'BHS', 44, 1),
(17, 'BH', 'BAHRAIN', 'Bahrain', 'BHR', 48, 973),
(18, 'BD', 'BANGLADESH', 'Bangladesh', 'BGD', 50, 880),
(19, 'BB', 'BARBADOS', 'Barbados', 'BRB', 52, 1),
(20, 'BY', 'BELARUS', 'Belarus', 'BLR', 112, 375),
(21, 'BE', 'BELGIUM', 'Belgium', 'BEL', 56, 32),
(22, 'BZ', 'BELIZE', 'Belize', 'BLZ', 84, 501),
(23, 'BJ', 'BENIN', 'Benin', 'BEN', 204, 229),
(24, 'BM', 'BERMUDA', 'Bermuda', 'BMU', 60, 1),
(25, 'BT', 'BHUTAN', 'Bhutan', 'BTN', 64, 975),
(26, 'BO', 'BOLIVIA', 'Bolivia', 'BOL', 68, 591),
(27, 'BA', 'BOSNIA AND HERZEGOVINA', 'Bosnia and Herzegovina', 'BIH', 70, 387),
(28, 'BW', 'BOTSWANA', 'Botswana', 'BWA', 72, 267),
(29, 'BV', 'BOUVET ISLAND', 'Bouvet Island', 'BVT', 74, 0),
(30, 'BR', 'BRAZIL', 'Brazil', 'BRA', 76, 55),
(31, 'IO', 'BRITISH INDIAN OCEAN TERRITORY', 'British Indian Ocean Territory', 'IOT', 86, 246),
(32, 'BN', 'BRUNEI DARUSSALAM', 'Brunei Darussalam', 'BRN', 96, 673),
(33, 'BG', 'BULGARIA', 'Bulgaria', 'BGR', 100, 359),
(34, 'BF', 'BURKINA FASO', 'Burkina Faso', 'BFA', 854, 226),
(35, 'BI', 'BURUNDI', 'Burundi', 'BDI', 108, 257),
(36, 'KH', 'CAMBODIA', 'Cambodia', 'KHM', 116, 855),
(37, 'CM', 'CAMEROON', 'Cameroon', 'CMR', 120, 237),
(38, 'CA', 'CANADA', 'Canada', 'CAN', 124, 1),
(39, 'CV', 'CAPE VERDE', 'Cape Verde', 'CPV', 132, 238),
(40, 'KY', 'CAYMAN ISLANDS', 'Cayman Islands', 'CYM', 136, 1),
(41, 'CF', 'CENTRAL AFRICAN REPUBLIC', 'Central African Republic', 'CAF', 140, 236),
(42, 'TD', 'CHAD', 'Chad', 'TCD', 148, 235),
(43, 'CL', 'CHILE', 'Chile', 'CHL', 152, 56),
(44, 'CN', 'CHINA', 'China', 'CHN', 156, 86),
(45, 'CX', 'CHRISTMAS ISLAND', 'Christmas Island', 'CXR', 162, 61),
(46, 'CC', 'COCOS (KEELING) ISLANDS', 'Cocos (Keeling) Islands', NULL, NULL, 672),
(47, 'CO', 'COLOMBIA', 'Colombia', 'COL', 170, 57),
(48, 'KM', 'COMOROS', 'Comoros', 'COM', 174, 269),
(49, 'CG', 'CONGO', 'Congo', 'COG', 178, 242),
(50, 'CD', 'CONGO, THE DEMOCRATIC REPUBLIC OF THE', 'Congo, the Democratic Republic of the', 'COD', 180, 242),
(51, 'CK', 'COOK ISLANDS', 'Cook Islands', 'COK', 184, 682),
(52, 'CR', 'COSTA RICA', 'Costa Rica', 'CRI', 188, 506),
(53, 'CI', 'COTE D''IVOIRE', 'Cote D''Ivoire', 'CIV', 384, 225),
(54, 'HR', 'CROATIA', 'Croatia', 'HRV', 191, 385),
(55, 'CU', 'CUBA', 'Cuba', 'CUB', 192, 53),
(56, 'CY', 'CYPRUS', 'Cyprus', 'CYP', 196, 357),
(57, 'CZ', 'CZECHIA', 'Czech Republic', 'CZE', 203, 420),
(58, 'DK', 'DENMARK', 'Denmark', 'DNK', 208, 45),
(59, 'DJ', 'DJIBOUTI', 'Djibouti', 'DJI', 262, 253),
(60, 'DM', 'DOMINICA', 'Dominica', 'DMA', 212, 1),
(61, 'DO', 'DOMINICAN REPUBLIC', 'Dominican Republic', 'DOM', 214, 1),
(62, 'EC', 'ECUADOR', 'Ecuador', 'ECU', 218, 593),
(63, 'EG', 'EGYPT', 'Egypt', 'EGY', 818, 20),
(64, 'SV', 'EL SALVADOR', 'El Salvador', 'SLV', 222, 503),
(65, 'GQ', 'EQUATORIAL GUINEA', 'Equatorial Guinea', 'GNQ', 226, 240),
(66, 'ER', 'ERITREA', 'Eritrea', 'ERI', 232, 291),
(67, 'EE', 'ESTONIA', 'Estonia', 'EST', 233, 372),
(68, 'ET', 'ETHIOPIA', 'Ethiopia', 'ETH', 231, 251),
(69, 'FK', 'FALKLAND ISLANDS (MALVINAS)', 'Falkland Islands (Malvinas)', 'FLK', 238, 500),
(70, 'FO', 'FAROE ISLANDS', 'Faroe Islands', 'FRO', 234, 298),
(71, 'FJ', 'FIJI', 'Fiji', 'FJI', 242, 679),
(72, 'FI', 'FINLAND', 'Finland', 'FIN', 246, 358),
(73, 'FR', 'FRANCE', 'France', 'FRA', 250, 33),
(74, 'GF', 'FRENCH GUIANA', 'French Guiana', 'GUF', 254, 594),
(75, 'PF', 'FRENCH POLYNESIA', 'French Polynesia', 'PYF', 258, 689),
(76, 'TF', 'FRENCH SOUTHERN TERRITORIES', 'French Southern Territories', 'ATF', 260, 0),
(77, 'GA', 'GABON', 'Gabon', 'GAB', 266, 241),
(78, 'GM', 'GAMBIA', 'Gambia', 'GMB', 270, 220),
(79, 'GE', 'GEORGIA', 'Georgia', 'GEO', 268, 995),
(80, 'DE', 'GERMANY', 'Germany', 'DEU', 276, 49),
(81, 'GH', 'GHANA', 'Ghana', 'GHA', 288, 233),
(82, 'GI', 'GIBRALTAR', 'Gibraltar', 'GIB', 292, 350),
(83, 'GR', 'GREECE', 'Greece', 'GRC', 300, 30),
(84, 'GL', 'GREENLAND', 'Greenland', 'GRL', 304, 299),
(85, 'GD', 'GRENADA', 'Grenada', 'GRD', 308, 1),
(86, 'GP', 'GUADELOUPE', 'Guadeloupe', 'GLP', 312, 590),
(87, 'GU', 'GUAM', 'Guam', 'GUM', 316, 1),
(88, 'GT', 'GUATEMALA', 'Guatemala', 'GTM', 320, 502),
(89, 'GN', 'GUINEA', 'Guinea', 'GIN', 324, 224),
(90, 'GW', 'GUINEA-BISSAU', 'Guinea-Bissau', 'GNB', 624, 245),
(91, 'GY', 'GUYANA', 'Guyana', 'GUY', 328, 592),
(92, 'HT', 'HAITI', 'Haiti', 'HTI', 332, 509),
(93, 'HM', 'HEARD ISLAND AND MCDONALD ISLANDS', 'Heard Island and Mcdonald Islands', 'HMD', 334, 0),
(94, 'VA', 'HOLY SEE (VATICAN CITY STATE)', 'Holy See (Vatican City State)', 'VAT', 336, 39),
(95, 'HN', 'HONDURAS', 'Honduras', 'HND', 340, 504),
(96, 'HK', 'HONG KONG', 'Hong Kong', 'HKG', 344, 852),
(97, 'HU', 'HUNGARY', 'Hungary', 'HUN', 348, 36),
(98, 'IS', 'ICELAND', 'Iceland', 'ISL', 352, 354),
(99, 'IN', 'INDIA', 'India', 'IND', 356, 91),
(100, 'ID', 'INDONESIA', 'Indonesia', 'IDN', 360, 62),
(101, 'IR', 'IRAN, ISLAMIC REPUBLIC OF', 'Iran, Islamic Republic of', 'IRN', 364, 98),
(102, 'IQ', 'IRAQ', 'Iraq', 'IRQ', 368, 964),
(103, 'IE', 'IRELAND', 'Ireland', 'IRL', 372, 353),
(104, 'IL', 'ISRAEL', 'Israel', 'ISR', 376, 972),
(105, 'IT', 'ITALY', 'Italy', 'ITA', 380, 39),
(106, 'JM', 'JAMAICA', 'Jamaica', 'JAM', 388, 1),
(107, 'JP', 'JAPAN', 'Japan', 'JPN', 392, 81),
(108, 'JO', 'JORDAN', 'Jordan', 'JOR', 400, 962),
(109, 'KZ', 'KAZAKHSTAN', 'Kazakhstan', 'KAZ', 398, 7),
(110, 'KE', 'KENYA', 'Kenya', 'KEN', 404, 254),
(111, 'KI', 'KIRIBATI', 'Kiribati', 'KIR', 296, 686),
(112, 'KP', 'KOREA, DEMOCRATIC PEOPLE''S REPUBLIC OF', 'Korea, Democratic People''s Republic of', 'PRK', 408, 850),
(113, 'KR', 'KOREA, REPUBLIC OF', 'Korea, Republic of', 'KOR', 410, 82),
(114, 'KW', 'KUWAIT', 'Kuwait', 'KWT', 414, 965),
(115, 'KG', 'KYRGYZSTAN', 'Kyrgyzstan', 'KGZ', 417, 996),
(116, 'LA', 'LAO PEOPLE''S DEMOCRATIC REPUBLIC', 'Lao People''s Democratic Republic', 'LAO', 418, 856),
(117, 'LV', 'LATVIA', 'Latvia', 'LVA', 428, 371),
(118, 'LB', 'LEBANON', 'Lebanon', 'LBN', 422, 961),
(119, 'LS', 'LESOTHO', 'Lesotho', 'LSO', 426, 266),
(120, 'LR', 'LIBERIA', 'Liberia', 'LBR', 430, 231),
(121, 'LY', 'LIBYAN ARAB JAMAHIRIYA', 'Libyan Arab Jamahiriya', 'LBY', 434, 218),
(122, 'LI', 'LIECHTENSTEIN', 'Liechtenstein', 'LIE', 438, 423),
(123, 'LT', 'LITHUANIA', 'Lithuania', 'LTU', 440, 370),
(124, 'LU', 'LUXEMBOURG', 'Luxembourg', 'LUX', 442, 352),
(125, 'MO', 'MACAO', 'Macao', 'MAC', 446, 853),
(126, 'MK', 'MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF', 'Macedonia, the Former Yugoslav Republic of', 'MKD', 807, 389),
(127, 'MG', 'MADAGASCAR', 'Madagascar', 'MDG', 450, 261),
(128, 'MW', 'MALAWI', 'Malawi', 'MWI', 454, 265),
(129, 'MY', 'MALAYSIA', 'Malaysia', 'MYS', 458, 60),
(130, 'MV', 'MALDIVES', 'Maldives', 'MDV', 462, 960),
(131, 'ML', 'MALI', 'Mali', 'MLI', 466, 223),
(132, 'MT', 'MALTA', 'Malta', 'MLT', 470, 356),
(133, 'MH', 'MARSHALL ISLANDS', 'Marshall Islands', 'MHL', 584, 692),
(134, 'MQ', 'MARTINIQUE', 'Martinique', 'MTQ', 474, 596),
(135, 'MR', 'MAURITANIA', 'Mauritania', 'MRT', 478, 222),
(136, 'MU', 'MAURITIUS', 'Mauritius', 'MUS', 480, 230),
(137, 'YT', 'MAYOTTE', 'Mayotte', 'MYT', 175, 269),
(138, 'MX', 'MEXICO', 'Mexico', 'MEX', 484, 52),
(139, 'FM', 'MICRONESIA, FEDERATED STATES OF', 'Micronesia, Federated States of', 'FSM', 583, 691),
(140, 'MD', 'MOLDOVA, REPUBLIC OF', 'Moldova, Republic of', 'MDA', 498, 373),
(141, 'MC', 'MONACO', 'Monaco', 'MCO', 492, 377),
(142, 'MN', 'MONGOLIA', 'Mongolia', 'MNG', 496, 976),
(143, 'MS', 'MONTSERRAT', 'Montserrat', 'MSR', 500, 1),
(144, 'MA', 'MOROCCO', 'Morocco', 'MAR', 504, 212),
(145, 'MZ', 'MOZAMBIQUE', 'Mozambique', 'MOZ', 508, 258),
(146, 'MM', 'MYANMAR', 'Myanmar', 'MMR', 104, 95),
(147, 'NA', 'NAMIBIA', 'Namibia', 'NAM', 516, 264),
(148, 'NR', 'NAURU', 'Nauru', 'NRU', 520, 674),
(149, 'NP', 'NEPAL', 'Nepal', 'NPL', 524, 977),
(150, 'NL', 'NETHERLANDS', 'Netherlands', 'NLD', 528, 31),
(151, 'AN', 'NETHERLANDS ANTILLES', 'Netherlands Antilles', 'ANT', 530, 599),
(152, 'NC', 'NEW CALEDONIA', 'New Caledonia', 'NCL', 540, 687),
(153, 'NZ', 'NEW ZEALAND', 'New Zealand', 'NZL', 554, 64),
(154, 'NI', 'NICARAGUA', 'Nicaragua', 'NIC', 558, 505),
(155, 'NE', 'NIGER', 'Niger', 'NER', 562, 227),
(156, 'NG', 'NIGERIA', 'Nigeria', 'NGA', 566, 234),
(157, 'NU', 'NIUE', 'Niue', 'NIU', 570, 683),
(158, 'NF', 'NORFOLK ISLAND', 'Norfolk Island', 'NFK', 574, 672),
(159, 'MP', 'NORTHERN MARIANA ISLANDS', 'Northern Mariana Islands', 'MNP', 580, 1),
(160, 'NO', 'NORWAY', 'Norway', 'NOR', 578, 47),
(161, 'OM', 'OMAN', 'Oman', 'OMN', 512, 968),
(162, 'PK', 'PAKISTAN', 'Pakistan', 'PAK', 586, 92),
(163, 'PW', 'PALAU', 'Palau', 'PLW', 585, 680),
(164, 'PS', 'PALESTINIAN TERRITORY, OCCUPIED', 'Palestinian Territory, Occupied', NULL, NULL, 970),
(165, 'PA', 'PANAMA', 'Panama', 'PAN', 591, 507),
(166, 'PG', 'PAPUA NEW GUINEA', 'Papua New Guinea', 'PNG', 598, 675),
(167, 'PY', 'PARAGUAY', 'Paraguay', 'PRY', 600, 595),
(168, 'PE', 'PERU', 'Peru', 'PER', 604, 51),
(169, 'PH', 'PHILIPPINES', 'Philippines', 'PHL', 608, 63),
(170, 'PN', 'PITCAIRN', 'Pitcairn', 'PCN', 612, 0),
(171, 'PL', 'POLAND', 'Poland', 'POL', 616, 48),
(172, 'PT', 'PORTUGAL', 'Portugal', 'PRT', 620, 351),
(173, 'PR', 'PUERTO RICO', 'Puerto Rico', 'PRI', 630, 1),
(174, 'QA', 'QATAR', 'Qatar', 'QAT', 634, 974),
(175, 'RE', 'REUNION', 'Reunion', 'REU', 638, 262),
(176, 'RO', 'ROMANIA', 'Romania', 'ROU', 642, 40),
(177, 'RU', 'RUSSIAN FEDERATION', 'Russian Federation', 'RUS', 643, 7),
(178, 'RW', 'RWANDA', 'Rwanda', 'RWA', 646, 250),
(179, 'SH', 'SAINT HELENA', 'Saint Helena', 'SHN', 654, 290),
(180, 'KN', 'SAINT KITTS AND NEVIS', 'Saint Kitts and Nevis', 'KNA', 659, 1),
(181, 'LC', 'SAINT LUCIA', 'Saint Lucia', 'LCA', 662, 1),
(182, 'PM', 'SAINT PIERRE AND MIQUELON', 'Saint Pierre and Miquelon', 'SPM', 666, 508),
(183, 'VC', 'SAINT VINCENT AND THE GRENADINES', 'Saint Vincent and the Grenadines', 'VCT', 670, 1),
(184, 'WS', 'SAMOA', 'Samoa', 'WSM', 882, 684),
(185, 'SM', 'SAN MARINO', 'San Marino', 'SMR', 674, 378),
(186, 'ST', 'SAO TOME AND PRINCIPE', 'Sao Tome and Principe', 'STP', 678, 239),
(187, 'SA', 'SAUDI ARABIA', 'Saudi Arabia', 'SAU', 682, 966),
(188, 'SN', 'SENEGAL', 'Senegal', 'SEN', 686, 221),
(189, 'RS', 'SERBIA', 'Serbia', 'SRB', 688, 381),
(190, 'SC', 'SEYCHELLES', 'Seychelles', 'SYC', 690, 248),
(191, 'SL', 'SIERRA LEONE', 'Sierra Leone', 'SLE', 694, 232),
(192, 'SG', 'SINGAPORE', 'Singapore', 'SGP', 702, 65),
(193, 'SK', 'SLOVAKIA', 'Slovakia', 'SVK', 703, 421),
(194, 'SI', 'SLOVENIA', 'Slovenia', 'SVN', 705, 386),
(195, 'SB', 'SOLOMON ISLANDS', 'Solomon Islands', 'SLB', 90, 677),
(196, 'SO', 'SOMALIA', 'Somalia', 'SOM', 706, 252),
(197, 'ZA', 'SOUTH AFRICA', 'South Africa', 'ZAF', 710, 27),
(198, 'GS', 'SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS', 'South Georgia and the South Sandwich Islands', 'SGS', 239, 0),
(199, 'ES', 'SPAIN', 'Spain', 'ESP', 724, 34),
(200, 'LK', 'SRI LANKA', 'Sri Lanka', 'LKA', 144, 94),
(201, 'SD', 'SUDAN', 'Sudan', 'SDN', 736, 249),
(202, 'SR', 'SURINAME', 'Suriname', 'SUR', 740, 597),
(203, 'SJ', 'SVALBARD AND JAN MAYEN', 'Svalbard and Jan Mayen', 'SJM', 744, 47),
(204, 'SZ', 'SWAZILAND', 'Swaziland', 'SWZ', 748, 268),
(205, 'SE', 'SWEDEN', 'Sweden', 'SWE', 752, 46),
(206, 'CH', 'SWITZERLAND', 'Switzerland', 'CHE', 756, 41),
(207, 'SY', 'SYRIAN ARAB REPUBLIC', 'Syrian Arab Republic', 'SYR', 760, 963),
(208, 'TW', 'TAIWAN, PROVINCE OF CHINA', 'Taiwan, Province of China', 'TWN', 158, 886),
(209, 'TJ', 'TAJIKISTAN', 'Tajikistan', 'TJK', 762, 992),
(210, 'TZ', 'TANZANIA, UNITED REPUBLIC OF', 'Tanzania, United Republic of', 'TZA', 834, 255),
(211, 'TH', 'THAILAND', 'Thailand', 'THA', 764, 66),
(212, 'TL', 'TIMOR-LESTE', 'Timor-Leste', 'TLS', 626, 670),
(213, 'TG', 'TOGO', 'Togo', 'TGO', 768, 228),
(214, 'TK', 'TOKELAU', 'Tokelau', 'TKL', 772, 690),
(215, 'TO', 'TONGA', 'Tonga', 'TON', 776, 676),
(216, 'TT', 'TRINIDAD AND TOBAGO', 'Trinidad and Tobago', 'TTO', 780, 1),
(217, 'TN', 'TUNISIA', 'Tunisia', 'TUN', 788, 216),
(218, 'TR', 'TURKEY', 'Turkey', 'TUR', 792, 90),
(219, 'TM', 'TURKMENISTAN', 'Turkmenistan', 'TKM', 795, 993),
(220, 'TC', 'TURKS AND CAICOS ISLANDS', 'Turks and Caicos Islands', 'TCA', 796, 1),
(221, 'TV', 'TUVALU', 'Tuvalu', 'TUV', 798, 688),
(222, 'UG', 'UGANDA', 'Uganda', 'UGA', 800, 256),
(223, 'UA', 'UKRAINE', 'Ukraine', 'UKR', 804, 380),
(224, 'AE', 'UNITED ARAB EMIRATES', 'United Arab Emirates', 'ARE', 784, 971),
(225, 'GB', 'UNITED KINGDOM', 'United Kingdom', 'GBR', 826, 44),
(226, 'US', 'UNITED STATES', 'United States', 'USA', 840, 1),
(227, 'UM', 'UNITED STATES MINOR OUTLYING ISLANDS', 'United States Minor Outlying Islands', 'UMI', 581, 1),
(228, 'UY', 'URUGUAY', 'Uruguay', 'URY', 858, 598),
(229, 'UZ', 'UZBEKISTAN', 'Uzbekistan', 'UZB', 860, 998),
(230, 'VU', 'VANUATU', 'Vanuatu', 'VUT', 548, 678),
(231, 'VE', 'VENEZUELA', 'Venezuela', 'VEN', 862, 58),
(232, 'VN', 'VIET NAM', 'Viet Nam', 'VNM', 704, 84),
(233, 'VG', 'VIRGIN ISLANDS, BRITISH', 'Virgin Islands, British', 'VGB', 92, 1),
(234, 'VI', 'VIRGIN ISLANDS, U.S.', 'Virgin Islands, U.s.', 'VIR', 850, 1),
(235, 'WF', 'WALLIS AND FUTUNA', 'Wallis and Futuna', 'WLF', 876, 681),
(236, 'EH', 'WESTERN SAHARA', 'Western Sahara', 'ESH', 732, 212),
(237, 'YE', 'YEMEN', 'Yemen', 'YEM', 887, 967),
(238, 'ZM', 'ZAMBIA', 'Zambia', 'ZMB', 894, 260),
(239, 'ZW', 'ZIMBABWE', 'Zimbabwe', 'ZWE', 716, 263),
(240, 'ME', 'MONTENEGRO', 'Montenegro', 'MNE', 499, 382),
(241, 'XK', 'KOSOVO', 'Kosovo', 'XKX', 0, 383),
(242, 'AX', 'ALAND ISLANDS', 'Aland Islands', 'ALA', '248', '358'),
(243, 'BQ', 'BONAIRE, SINT EUSTATIUS AND SABA', 'Bonaire, Sint Eustatius and Saba', 'BES', '535', '599'),
(244, 'CW', 'CURACAO', 'Curacao', 'CUW', '531', '599'),
(245, 'GG', 'GUERNSEY', 'Guernsey', 'GGY', '831', '44'),
(246, 'IM', 'ISLE OF MAN', 'Isle of Man', 'IMN', '833', '44'),
(247, 'JE', 'JERSEY', 'Jersey', 'JEY', '832', '44'),
(248, 'BL', 'SAINT BARTHELEMY', 'Saint Barthelemy', 'BLM', '652', '590'),
(249, 'MF', 'SAINT MARTIN', 'Saint Martin', 'MAF', '663', '590'),
(250, 'SX', 'SINT MAARTEN', 'Sint Maarten', 'SXM', '534', '1'),
(251, 'SS', 'SOUTH SUDAN', 'South Sudan', 'SSD', '728', '211');

SELECT setval('prolog_paises_codigo_seq', 251, true);

-- Cria tabela de telefones de colaboradores.
CREATE TABLE COLABORADOR_TELEFONE
(
    CODIGO                             BIGSERIAL                              NOT NULL
        CONSTRAINT PK_COLABORADOR_TELEFONE
            PRIMARY KEY,
    COD_COLABORADOR                    BIGINT                                 NOT NULL
        CONSTRAINT FK_COLABORADOR_TELEFONE_COLABORADOR_DATA_CODIGO
            REFERENCES COLABORADOR_DATA (CODIGO),
    SIGLA_ISO2                         CHAR(2)                                NOT NULL,
    PREFIXO_PAIS                       INTEGER                                NOT NULL,
    NUMERO_TELEFONE                    TEXT                                   NOT NULL,
    DATA_HORA_ULTIMA_ATUALIZACAO       TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT                                 NOT NULL
        CONSTRAINT FK_COLABORADOR_TELEFONE_ATUALIZACAO_COLABORADOR_DATA_CODIGO
            REFERENCES COLABORADOR_DATA (CODIGO),
    CONSTRAINT FK_COLABORADOR_TELEFONE_PROLOG_PAISES
        FOREIGN KEY (SIGLA_ISO2, PREFIXO_PAIS) REFERENCES PROLOG_PAISES (SIGLA_ISO2, PREFIXO_TELEFONE)
);

-- Cria um DOMAIN para email com validação em regex
CREATE DOMAIN email AS citext
    CHECK ( value ~
            '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );

-- Cria tabela de emails de colaboradores.
create table colaborador_email
(
    codigo                             bigserial not null
        constraint pk_colaborador_email
            primary key,
    cod_colaborador                    bigint
        constraint fk_colaborador_email_colaborador_data_codigo
            references colaborador_data (codigo),
    email                              email     not null,
    data_hora_ultima_atualizacao       timestamp with time zone default now(),
    cod_colaborador_ultima_atualizacao bigint
        constraint fk_colaborador_email_atualizacao_colaborador_data_codigo
            references colaborador_data (codigo)
);

alter table colaborador_data
    alter column status_ativo set default true;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria function de insert de colaborador
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_INSERT_COLABORADOR(F_CPF BIGINT,
                                                               F_MATRICULA_AMBEV INTEGER,
                                                               F_MATRICULA_TRANS INTEGER,
                                                               F_DATA_NASCIMENTO DATE,
                                                               F_DATA_ADMISSAO DATE,
                                                               F_NOME VARCHAR,
                                                               F_COD_SETOR BIGINT,
                                                               F_COD_FUNCAO INTEGER,
                                                               F_COD_UNIDADE INTEGER,
                                                               F_COD_PERMISSAO BIGINT,
                                                               F_COD_EMPRESA BIGINT,
                                                               F_COD_EQUIPE BIGINT,
                                                               F_PIS VARCHAR,
                                                               F_SIGLA_ISO2 CHARACTER VARYING,
                                                               F_PREFIXO_PAIS INTEGER,
                                                               F_TELEFONE TEXT,
                                                               F_EMAIL EMAIL,
                                                               F_COD_UNIDADE_CADASTRO INTEGER,
                                                               F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_COLABORADOR_UPDATE CONSTANT BIGINT := (SELECT COD_COLABORADOR
                                                 FROM TOKEN_AUTENTICACAO
                                                 WHERE TOKEN = F_TOKEN);
    F_COD_COLABORADOR_INSERIDO        BIGINT;
    F_COD_TELEFONE_INSERIDO           BIGINT;
    F_COD_EMAIL_INSERIDO              BIGINT;
BEGIN
    INSERT INTO COLABORADOR (CPF,
                             MATRICULA_AMBEV,
                             MATRICULA_TRANS,
                             DATA_NASCIMENTO,
                             DATA_ADMISSAO,
                             NOME,
                             COD_SETOR,
                             COD_FUNCAO,
                             COD_UNIDADE,
                             COD_PERMISSAO,
                             COD_EMPRESA,
                             COD_EQUIPE,
                             PIS,
                             COD_UNIDADE_CADASTRO)
    VALUES (F_CPF,
            F_MATRICULA_AMBEV,
            F_MATRICULA_TRANS,
            F_DATA_NASCIMENTO,
            F_DATA_ADMISSAO,
            F_NOME,
            F_COD_SETOR,
            F_COD_FUNCAO,
            F_COD_UNIDADE,
            F_COD_PERMISSAO,
            F_COD_EMPRESA,
            F_COD_EQUIPE,
            F_PIS,
            F_COD_UNIDADE_CADASTRO)
    RETURNING CODIGO
        INTO F_COD_COLABORADOR_INSERIDO;

    -- Verificamos se o insert de colaborador funcionou.
    IF F_COD_COLABORADOR_INSERIDO IS NULL OR F_COD_COLABORADOR_INSERIDO <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o colaborador, tente novamente');
    END IF;

    IF F_PREFIXO_PAIS IS NOT NULL AND F_TELEFONE IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_TELEFONE (SIGLA_ISO2,
                                          PREFIXO_PAIS,
                                          COD_COLABORADOR,
                                          NUMERO_TELEFONE,
                                          COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_SIGLA_ISO2,
                F_PREFIXO_PAIS,
                F_COD_COLABORADOR_INSERIDO,
                F_TELEFONE,
                F_COD_COLABORADOR_UPDATE)
        RETURNING CODIGO
            INTO F_COD_TELEFONE_INSERIDO;

        -- Verificamos se o insert do telefone do colaborador funcionou.
        IF F_COD_TELEFONE_INSERIDO IS NULL OR F_COD_TELEFONE_INSERIDO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível inserir o colaborador devido a problemas no telefone, tente novamente');
        END IF;
    END IF;

    IF F_EMAIL IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_EMAIL (COD_COLABORADOR,
                                       EMAIL,
                                       COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_COD_COLABORADOR_INSERIDO,
                F_EMAIL,
                F_COD_COLABORADOR_UPDATE)
        RETURNING CODIGO
            INTO F_COD_EMAIL_INSERIDO;

        -- Verificamos se o insert do email funcionou.
        IF F_COD_EMAIL_INSERIDO IS NULL OR F_COD_EMAIL_INSERIDO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível inserir o colaborador devido a problemas no e-mail, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR_INSERIDO;
END;
$$;

-- Cria function de uptade de colaborador
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_UPDATE_COLABORADOR(F_COD_COLABORADOR BIGINT,
                                                               F_CPF BIGINT,
                                                               F_MATRICULA_AMBEV INTEGER,
                                                               F_MATRICULA_TRANS INTEGER,
                                                               F_DATA_NASCIMENTO DATE,
                                                               F_DATA_ADMISSAO DATE,
                                                               F_NOME VARCHAR,
                                                               F_COD_SETOR BIGINT,
                                                               F_COD_FUNCAO INTEGER,
                                                               F_COD_UNIDADE INTEGER,
                                                               F_COD_PERMISSAO BIGINT,
                                                               F_COD_EMPRESA BIGINT,
                                                               F_COD_EQUIPE BIGINT,
                                                               F_PIS VARCHAR,
                                                               F_SIGLA_ISO2 CHARACTER VARYING,
                                                               F_PREFIXO_PAIS INTEGER,
                                                               F_TELEFONE TEXT,
                                                               F_EMAIL EMAIL,
                                                               F_TOKEN TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_COLABORADOR_UPDATE CONSTANT BIGINT := (SELECT COD_COLABORADOR
                                                 FROM TOKEN_AUTENTICACAO
                                                 WHERE TOKEN = F_TOKEN);
BEGIN
    UPDATE COLABORADOR
    SET CPF             = F_CPF,
        MATRICULA_AMBEV = F_MATRICULA_AMBEV,
        MATRICULA_TRANS = F_MATRICULA_TRANS,
        DATA_NASCIMENTO = F_DATA_NASCIMENTO,
        DATA_ADMISSAO   = F_DATA_ADMISSAO,
        NOME            = F_NOME,
        COD_SETOR       = F_COD_SETOR,
        COD_FUNCAO      = F_COD_FUNCAO,
        COD_UNIDADE     = F_COD_UNIDADE,
        COD_PERMISSAO   = F_COD_PERMISSAO,
        COD_EMPRESA     = F_COD_EMPRESA,
        COD_EQUIPE      = F_COD_EQUIPE,
        PIS             = F_PIS
    WHERE CODIGO = F_COD_COLABORADOR;

    -- Validamos se houve alguma atualização dos valores.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados do colaborador, tente novamente');
    END IF;

    -- Será permitido somente 1 email e telefone por colaborador no lançamento inicial.
    -- Deletamos email e telefone vinculados ao colaborador
    DELETE FROM COLABORADOR_EMAIL WHERE COD_COLABORADOR = F_COD_COLABORADOR;
    DELETE FROM COLABORADOR_TELEFONE WHERE COD_COLABORADOR = F_COD_COLABORADOR;

    IF F_PREFIXO_PAIS IS NOT NULL AND F_TELEFONE IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_TELEFONE (SIGLA_ISO2,
                                          PREFIXO_PAIS,
                                          COD_COLABORADOR,
                                          NUMERO_TELEFONE,
                                          COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_SIGLA_ISO2,
                F_PREFIXO_PAIS,
                F_COD_COLABORADOR,
                F_TELEFONE,
                F_COD_COLABORADOR_UPDATE);

        -- Verificamos se o insert do telefone do colaborador funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível atualizar o colaborador devido a problemas no telefone, tente novamente');
        END IF;
    END IF;

    IF F_EMAIL IS NOT NULL
    THEN
        INSERT INTO COLABORADOR_EMAIL (COD_COLABORADOR,
                                       EMAIL,
                                       COD_COLABORADOR_ULTIMA_ATUALIZACAO)
        VALUES (F_COD_COLABORADOR,
                F_EMAIL,
                F_COD_COLABORADOR_UPDATE);

        -- Verificamos se o insert do email funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Não foi possível atualizar o colaborador devido a problemas no e-mail, tente novamente');
        END IF;
    END IF;

    RETURN F_COD_COLABORADOR;
END;
$$;

--Cria as functions de audit
CREATE TRIGGER TG_FUNC_AUDIT_COLABORADOR_TELEFONE
    AFTER INSERT OR UPDATE OR DELETE
    ON COLABORADOR_TELEFONE
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

CREATE TRIGGER TG_FUNC_AUDIT_COLABORADOR_EMAIL
    AFTER INSERT OR UPDATE OR DELETE
    ON COLABORADOR_EMAIL
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();

-- Deleta e recria functions de listagem de colaboradores
-- Por empresa
DROP FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(BIGINT, BOOLEAN);
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO             BIGINT,
                CPF                BIGINT,
                PIS                CHARACTER VARYING,
                MATRICULA_AMBEV    INTEGER,
                MATRICULA_TRANS    INTEGER,
                DATA_NASCIMENTO    DATE,
                DATA_ADMISSAO      DATE,
                DATA_DEMISSAO      DATE,
                STATUS_ATIVO       BOOLEAN,
                NOME_COLABORADOR   TEXT,
                NOME_EMPRESA       TEXT,
                COD_EMPRESA        BIGINT,
                LOGO_THUMBNAIL_URL TEXT,
                NOME_REGIONAL      TEXT,
                COD_REGIONAL       BIGINT,
                NOME_UNIDADE       TEXT,
                COD_UNIDADE        BIGINT,
                NOME_EQUIPE        TEXT,
                COD_EQUIPE         BIGINT,
                NOME_SETOR         TEXT,
                COD_SETOR          BIGINT,
                COD_FUNCAO         BIGINT,
                NOME_FUNCAO        TEXT,
                PERMISSAO          BIGINT,
                TZ_UNIDADE         TEXT,
                SIGLA_ISO2         TEXT,
                PREFIXO_PAIS       INTEGER,
                NUMERO_TELEFONE    TEXT,
                EMAIL              TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       C.CPF,
       C.PIS,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       EM.NOME         AS NOME_EMPRESA,
       EM.CODIGO       AS COD_EMPRESA,
       EM.LOGO_THUMBNAIL_URL,
       R.REGIAO        AS NOME_REGIONAL,
       R.CODIGO        AS COD_REGIONAL,
       U.NOME          AS NOME_UNIDADE,
       U.CODIGO        AS COD_UNIDADE,
       EQ.NOME         AS NOME_EQUIPE,
       EQ.CODIGO       AS COD_EQUIPE,
       S.NOME          AS NOME_SETOR,
       S.CODIGO        AS COD_SETOR,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       C.COD_PERMISSAO AS PERMISSAO,
       U.TIMEZONE      AS TZ_UNIDADE,
       CT.SIGLA_ISO2 :: TEXT,
       CT.PREFIXO_PAIS,
       CT.NUMERO_TELEFONE,
       CE.EMAIL
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
         LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR
         LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR
WHERE C.COD_EMPRESA = F_COD_EMPRESA
  AND CASE
          WHEN F_STATUS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_STATUS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;

-- Por unidade
DROP FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(BIGINT, BOOLEAN);
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_STATUS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO             BIGINT,
                CPF                BIGINT,
                PIS                CHARACTER VARYING,
                MATRICULA_AMBEV    INTEGER,
                MATRICULA_TRANS    INTEGER,
                DATA_NASCIMENTO    DATE,
                DATA_ADMISSAO      DATE,
                DATA_DEMISSAO      DATE,
                STATUS_ATIVO       BOOLEAN,
                NOME_COLABORADOR   TEXT,
                NOME_EMPRESA       TEXT,
                COD_EMPRESA        BIGINT,
                LOGO_THUMBNAIL_URL TEXT,
                NOME_REGIONAL      TEXT,
                COD_REGIONAL       BIGINT,
                NOME_UNIDADE       TEXT,
                COD_UNIDADE        BIGINT,
                NOME_EQUIPE        TEXT,
                COD_EQUIPE         BIGINT,
                NOME_SETOR         TEXT,
                COD_SETOR          BIGINT,
                COD_FUNCAO         BIGINT,
                NOME_FUNCAO        TEXT,
                PERMISSAO          BIGINT,
                TZ_UNIDADE         TEXT,
                SIGLA_ISO2         TEXT,
                PREFIXO_PAIS       INTEGER,
                NUMERO_TELEFONE    TEXT,
                EMAIL              TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.CODIGO,
       C.CPF,
       C.PIS,
       C.MATRICULA_AMBEV,
       C.MATRICULA_TRANS,
       C.DATA_NASCIMENTO,
       C.DATA_ADMISSAO,
       C.DATA_DEMISSAO,
       C.STATUS_ATIVO,
       INITCAP(C.NOME) AS NOME_COLABORADOR,
       EM.NOME         AS NOME_EMPRESA,
       EM.CODIGO       AS COD_EMPRESA,
       EM.LOGO_THUMBNAIL_URL,
       R.REGIAO        AS NOME_REGIONAL,
       R.CODIGO        AS COD_REGIONAL,
       U.NOME          AS NOME_UNIDADE,
       U.CODIGO        AS COD_UNIDADE,
       EQ.NOME         AS NOME_EQUIPE,
       EQ.CODIGO       AS COD_EQUIPE,
       S.NOME          AS NOME_SETOR,
       S.CODIGO        AS COD_SETOR,
       F.CODIGO        AS COD_FUNCAO,
       F.NOME          AS NOME_FUNCAO,
       C.COD_PERMISSAO AS PERMISSAO,
       U.TIMEZONE      AS TZ_UNIDADE,
       CT.SIGLA_ISO2 :: TEXT,
       CT.PREFIXO_PAIS,
       CT.NUMERO_TELEFONE,
       CE.EMAIL
FROM COLABORADOR C
         JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
         JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
         JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
         JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
         LEFT JOIN COLABORADOR_TELEFONE CT ON C.CODIGO = CT.COD_COLABORADOR
         LEFT JOIN COLABORADOR_EMAIL CE ON C.CODIGO = CE.COD_COLABORADOR
WHERE C.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_STATUS_ATIVOS IS NULL
              THEN 1 = 1
          ELSE C.STATUS_ATIVO = F_STATUS_ATIVOS
    END
ORDER BY C.NOME ASC
$$;

--######################################################################################################################
--######################################################################################################################

END TRANSACTION;