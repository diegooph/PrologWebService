-- Sobre:
-- Realiza a cópia de um modelo de checklist entre difererntes empresas. Os caros e tipos de veículos
-- que devem ser vinculados são recebidos por parâmetro, mas o fornecimento é opcional. Apenas imagens da galeria
-- pública do Prolog são mantidas vinculadas as perguntas.
--
-- Histórico:
-- 2019-06-12 -> Function criada (thaisksf - PL-2059).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-02-03 -> Adapta para a nova estrutura (wvinim - PL-2489).
-- 2020-03-06 -> Corrige a coluna sobre abrir ordem de serviço (wvinim).
-- 2020-07-07 -> Adição das informações de parametrização de mídias (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
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
    COD_VERSAO_MODELO_CHECKLIST_COPIADO    BIGINT := (SELECT COD_VERSAO_ATUAL
                                                      FROM CHECKLIST_MODELO
                                                      WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    PERGUNTA_MODELO_CHECKLIST_COPIADO      CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST,
                                                             F_COD_COLABORADOR_SOLICITANTE_COPIA);

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
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    RETURNING CODIGO INTO COD_MODELO_CHECKLIST_INSERIDO;

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
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               CP.ANEXO_MIDIA_RESPOSTA_OK
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
                                             ANEXO_MIDIA_RESPOSTA_OK,
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
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ANEXO_MIDIA_RESPOSTA_OK,
                    NOVO_COD_VERSAO_MODELO)
            RETURNING CODIGO INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        DEVE_ABRIR_ORDEM_SERVICO,
                                                        ANEXO_MIDIA,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        CAP.DEVE_ABRIR_ORDEM_SERVICO,
                        CAP.ANEXO_MIDIA,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END
$$;