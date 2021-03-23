create or replace function migration_checklist.func_migration_7_mudancas_dao_modelos_check()
    returns void
    language plpgsql
as
$func$
begin
    --######################################################################################################################
--######################################################################################################################
--################################ DESCRIÇÃO ###########################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2231
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_MODELO_CHECKLIST_INFOS(F_COD_UNIDADE_MODELO BIGINT,
                                                                            F_NOME_MODELO TEXT,
                                                                            F_STATUS_ATIVO BOOLEAN,
                                                                            F_COD_CARGOS BIGINT[],
                                                                            F_COD_TIPOS_VEICULOS BIGINT[],
                                                                            F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                                            F_TOKEN_COLABORADOR TEXT)
        RETURNS TABLE
                (
                    COD_MODELO_CHECKLIST        BIGINT,
                    COD_VERSAO_MODELO_CHECKLIST BIGINT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        ERROR_MESSAGE          TEXT   := 'Erro ao salvar modelo de checklist, tente novamente';
        QTD_LINHAS_INSERIDAS   BIGINT;
        COD_MODELO_INSERIDO    BIGINT;
        NOVO_COD_VERSAO_MODELO BIGINT := NEXTVAL(
                PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));
    BEGIN
        -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
        SET CONSTRAINTS ALL DEFERRED;

        -- 1 -> Insere o modelo.
        INSERT INTO CHECKLIST_MODELO(COD_UNIDADE,
                                     COD_VERSAO_ATUAL,
                                     NOME,
                                     STATUS_ATIVO)
        VALUES (F_COD_UNIDADE_MODELO,
                NOVO_COD_VERSAO_MODELO,
                F_NOME_MODELO,
                F_STATUS_ATIVO) RETURNING CODIGO INTO COD_MODELO_INSERIDO;


        IF COD_MODELO_INSERIDO IS NULL OR COD_MODELO_INSERIDO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
        END IF;
        --

        -- 2 -> Insere a versão.
        INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                            COD_VERSAO_USER_FRIENDLY,
                                            COD_CHECKLIST_MODELO,
                                            DATA_HORA_CRIACAO_VERSAO,
                                            COD_COLABORADOR_CRIACAO_VERSAO)
        VALUES (NOVO_COD_VERSAO_MODELO,
                1,
                COD_MODELO_INSERIDO,
                F_DATA_HORA_ATUAL,
                (SELECT TA.COD_COLABORADOR FROM TOKEN_AUTENTICACAO TA WHERE TA.TOKEN = F_TOKEN_COLABORADOR));

        GET DIAGNOSTICS QTD_LINHAS_INSERIDAS = ROW_COUNT;

        IF QTD_LINHAS_INSERIDAS IS NULL OR QTD_LINHAS_INSERIDAS <> 1
        THEN
            PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
        END IF;
        --

        -- 3 -> Insere os tipos de veículos.
        INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO(COD_UNIDADE, COD_MODELO, COD_TIPO_VEICULO)
        VALUES (F_COD_UNIDADE_MODELO, COD_MODELO_INSERIDO, UNNEST(F_COD_TIPOS_VEICULOS));
        --

        -- 4 -> Insere os cargos.
        INSERT INTO CHECKLIST_MODELO_FUNCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_FUNCAO)
        VALUES (F_COD_UNIDADE_MODELO, COD_MODELO_INSERIDO, UNNEST(F_COD_CARGOS));
        --

        RETURN QUERY
            SELECT COD_MODELO_INSERIDO              AS COD_MODELO_CHECKLIST,
                   NOVO_COD_VERSAO_MODELO :: BIGINT AS COD_VERSAO_MODELO_CHECKLIST;
    END;
    $$;


    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_NOVA_VERSAO_MODELO(F_COD_UNIDADE_MODELO BIGINT,
                                                                        F_COD_MODELO BIGINT,
                                                                        F_NOME_MODELO TEXT,
                                                                        F_STATUS_ATIVO BOOLEAN,
                                                                        F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE,
                                                                        F_TOKEN_COLABORADOR TEXT)
        RETURNS TABLE
                (
                    COD_MODELO_CHECKLIST        BIGINT,
                    COD_VERSAO_MODELO_CHECKLIST BIGINT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        ERROR_MESSAGE          TEXT   := 'Erro ao atualizar modelo de checklist, tente novamente';
        QTD_LINHAS_ATUALIZADAS BIGINT;
        NOVO_COD_VERSAO_MODELO BIGINT := NEXTVAL(
                PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));
    BEGIN
        -- 1 -> Primeiro criamos uma nova versão.
        INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                            COD_VERSAO_USER_FRIENDLY,
                                            COD_CHECKLIST_MODELO,
                                            DATA_HORA_CRIACAO_VERSAO,
                                            COD_COLABORADOR_CRIACAO_VERSAO)
        VALUES (NOVO_COD_VERSAO_MODELO,
                (SELECT MAX(COD_VERSAO_USER_FRIENDLY) + 1
                 FROM CHECKLIST_MODELO_VERSAO CMV
                 WHERE CMV.COD_CHECKLIST_MODELO = F_COD_MODELO),
                F_COD_MODELO,
                F_DATA_HORA_ATUAL,
                (SELECT TA.COD_COLABORADOR
                 FROM TOKEN_AUTENTICACAO TA
                 WHERE TA.TOKEN = F_TOKEN_COLABORADOR));

        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF QTD_LINHAS_ATUALIZADAS IS NULL
            OR QTD_LINHAS_ATUALIZADAS <> 1
            OR NOVO_COD_VERSAO_MODELO IS NULL
            OR NOVO_COD_VERSAO_MODELO <= 0
        THEN
            PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
        END IF;

        -- 2 -> Agora atualizamos o modelo de checklist.
        UPDATE CHECKLIST_MODELO
        SET NOME             = F_NOME_MODELO,
            STATUS_ATIVO     = F_STATUS_ATIVO,
            COD_VERSAO_ATUAL = NOVO_COD_VERSAO_MODELO
        WHERE CODIGO = F_COD_MODELO
          AND COD_UNIDADE = F_COD_UNIDADE_MODELO;

        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        IF QTD_LINHAS_ATUALIZADAS IS NULL OR QTD_LINHAS_ATUALIZADAS <> 1
        THEN
            PERFORM THROW_GENERIC_ERROR(ERROR_MESSAGE);
        END IF;

        RETURN QUERY
            SELECT F_COD_MODELO           AS COD_MODELO_CHECKLIST,
                   NOVO_COD_VERSAO_MODELO AS COD_VERSAO_MODELO_CHECKLIST;
    END;
    $$;


    DROP FUNCTION FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST(F_NOME_MODELO TEXT,
        F_COD_UNIDADE BIGINT,
        F_COD_MODELO BIGINT,
        F_COD_CARGOS BIGINT[],
        F_COD_TIPOS_VEICULOS BIGINT[]);

    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST_INFOS(F_COD_UNIDADE BIGINT,
                                                                            F_COD_MODELO BIGINT,
                                                                            F_NOME_MODELO TEXT,
                                                                            F_COD_CARGOS BIGINT[],
                                                                            F_COD_TIPOS_VEICULOS BIGINT[])
        RETURNS VOID
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        QTD_TIPOS_VEICULO_PARA_DELETAR BIGINT := (SELECT COUNT(*)
                                                  FROM CHECKLIST_MODELO_VEICULO_TIPO
                                                  WHERE COD_UNIDADE = F_COD_UNIDADE
                                                    AND COD_MODELO = F_COD_MODELO);
        QTD_CARGOS_PARA_DELETAR        BIGINT := (SELECT COUNT(*)
                                                  FROM CHECKLIST_MODELO_FUNCAO
                                                  WHERE COD_UNIDADE = F_COD_UNIDADE
                                                    AND COD_CHECKLIST_MODELO = F_COD_MODELO);
        QTD_LINHAS_IMPACTADAS          BIGINT;
    BEGIN
        -- 1 -> Atualiza o modelo.
        UPDATE CHECKLIST_MODELO
        SET NOME = F_NOME_MODELO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND CODIGO = F_COD_MODELO;

        GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

        IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> 1
        THEN
            RAISE EXCEPTION 'Erro ao atualizar o nome do modelo de checklist';
        END IF;
        --

        -- 2 -> Atualiza os tipos de veículos.
        DELETE FROM CHECKLIST_MODELO_VEICULO_TIPO WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_MODELO = F_COD_MODELO;

        GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

        IF QTD_TIPOS_VEICULO_PARA_DELETAR = QTD_LINHAS_IMPACTADAS
        THEN
            INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO(COD_UNIDADE, COD_MODELO, COD_TIPO_VEICULO)
            VALUES (F_COD_UNIDADE, F_COD_MODELO, UNNEST(F_COD_TIPOS_VEICULOS));

            GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

            IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> ARRAY_LENGTH(F_COD_TIPOS_VEICULOS, 1)
            THEN
                RAISE EXCEPTION 'Erro ao inserir tipos de veículo liberados no modelo de checklist %', F_COD_MODELO;
            END IF;
        ELSE
            RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_VEICULO_TIPO';
        END IF;
        --

        -- 3 -> Atualiza os cargos.
        DELETE FROM CHECKLIST_MODELO_FUNCAO WHERE COD_UNIDADE = F_COD_UNIDADE AND COD_CHECKLIST_MODELO = F_COD_MODELO;

        GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

        IF QTD_CARGOS_PARA_DELETAR = QTD_LINHAS_IMPACTADAS
        THEN
            INSERT INTO CHECKLIST_MODELO_FUNCAO(COD_UNIDADE, COD_CHECKLIST_MODELO, COD_FUNCAO)
            VALUES (F_COD_UNIDADE, F_COD_MODELO, UNNEST(F_COD_CARGOS));

            GET DIAGNOSTICS QTD_LINHAS_IMPACTADAS = ROW_COUNT;

            IF QTD_LINHAS_IMPACTADAS IS NULL OR QTD_LINHAS_IMPACTADAS <> ARRAY_LENGTH(F_COD_CARGOS, 1)
            THEN
                RAISE EXCEPTION 'Erro ao inserir cargos liberados no modelo de checklist %', F_COD_MODELO;
            END IF;
        ELSE
            RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_FUNCAO';
        END IF;
        --
    END;
    $$;


    create extension fuzzystrmatch;

    -- Nova versão:
    --

    create or replace function func_checklist_analisa_mudancas_modelo(f_cod_modelo bigint,
                                                                      f_cod_versao_modelo bigint,
                                                                      f_nome_modelo text,
                                                                      f_cod_cargos bigint[],
                                                                      f_cod_tipos_veiculos bigint[],
                                                                      f_perguntas_alternativas_json jsonb)
        returns table
                (
                    codigo_item                   bigint,
                    item_novo                     boolean,
                    item_mudou_contexto           boolean,
                    item_tipo_pergunta            boolean,
                    algo_mudou_no_modelo          boolean,
                    algo_mudou_no_contexto        boolean,
                    deve_criar_nova_versao_modelo boolean
                )
        language plpgsql
    as
    $$
    declare
        algo_mudou_no_modelo          boolean := false;
        algo_mudou_no_contexto        boolean := false;
        deve_criar_nova_versao_modelo boolean := false;
    begin
        -- Verifica se o nome do modelo sofreu alteração, cria nova versão sem alterar contexto.
        if (select exists(
                           select soundex(f_nome_modelo)
                               except
                           select soundex(nome)
                           from checklist_modelo
                           where cod_versao_atual = f_cod_versao_modelo
                       ))
        then
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := true;
        end if;

        -- 1 -> Cria tabelas temporárias para nos ajudarem a trabalhar com os dados.
        create temp table if not exists perguntas
        (
            _id                     bigserial not null,
            codigo                  bigint,
            cod_imagem              bigint,
            descricao               text      not null,
            single_choice           boolean   not null,
            ordem_exibicao          integer   not null,
            pergunta_nova           boolean   not null,
            pergunta_mudou          boolean,
            pergunta_mudou_contexto boolean
        ) on commit delete rows;

        create temp table if not exists alternativas
        (
            _id                        bigserial not null,
            codigo                     bigint,
            descricao                  text      not null,
            prioridade                 text      not null,
            tipo_outros                boolean   not null,
            ordem_exibicao             integer   not null,
            deve_abrir_ordem_servico   boolean   not null,
            alternativa_nova           boolean   not null,
            alternativa_mudou          boolean,
            alternativa_mudou_contexto boolean
        ) on commit delete rows;
        --

        -- 2 -> Insere as perguntas.
        with cte as (
            select jsonb_array_elements(f_perguntas_alternativas_json) src
        )
        insert
        into perguntas (codigo, cod_imagem, descricao, single_choice, ordem_exibicao, pergunta_nova)
        select (src ->> 'codigo') :: bigint,
               (src ->> 'codImagem') :: bigint,
               (src ->> 'descricao'),
               (src ->> 'singleChoice') :: boolean,
               (src ->> 'ordemExibicao') :: integer,
               -- Se for uma pergunta sendo cadastrada, então ainda não tem código.
               (src ->> 'codigo') is null
        from cte;
        --


        -- 3 -> Insere as alternativas.
        with cte as (
            select jsonb_array_elements(jsonb_array_elements(f_perguntas_alternativas_json) -> 'alternativas') src
        )
        insert
        into alternativas (codigo, descricao, prioridade, tipo_outros, ordem_exibicao, deve_abrir_ordem_servico,
                           alternativa_nova)
        select (src ->> 'codigo') :: bigint,
               (src ->> 'descricao'),
               (src ->> 'prioridade'),
               (src ->> 'tipoOutros') :: boolean,
               (src ->> 'ordemExibicao') :: integer,
               (src ->> 'deveAbrirOrdemServico') :: boolean,
               -- Se for uma alternativa sendo cadastrada, então ainda não tem código.
               (src ->> 'codigo') is null
        from cte;
        --

        -- 4.1 -> Verifica se alguma pergunta foi deletada.
        if (select exists(select cp.codigo
                          from checklist_perguntas cp
                          where cp.cod_checklist_modelo = f_cod_modelo
                            and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
                              except
                          select codigo
                          from perguntas
                          where codigo is not null
                       ))
        then
            algo_mudou_no_contexto := true;
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := true;
        end if;

        -- 4.2 -> Verifica se alguma alternativa foi deletada.
        if (select exists(select cap.codigo
                          from checklist_alternativa_pergunta cap
                          where cap.cod_checklist_modelo = f_cod_modelo
                            and cap.cod_versao_checklist_modelo = f_cod_versao_modelo
                              except
                          select codigo
                          from alternativas
                          where codigo is not null
                       ))
        then
            algo_mudou_no_contexto := true;
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := true;
        end if;


        -- 4.3 -> Verifica se as perguntas mudaram com alteração de contexto.
        with perguntas_novas_ou_editadas as (
            select codigo,
                   cod_imagem,
                   -- Usamos o soundex para verificar se a descrição mudou e não o texto em si.
                   soundex(descricao)
            from perguntas
                except
            select cp.codigo,
                   cp.cod_imagem,
                   soundex(cp.pergunta)
            from checklist_perguntas cp
            where cp.cod_checklist_modelo = f_cod_modelo
              and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update perguntas p
        set pergunta_mudou_contexto = true
            -- CTE irá conter apenas as novas e/ou alteradas.
        from perguntas_novas_ou_editadas pne
             -- Só considera como alterada as perguntas que já existiam.
        where pne.codigo is not null
          and pne.codigo = p.codigo;

        if (select count(*) from perguntas where pergunta_mudou_contexto is true or pergunta_nova is true) > 0
        then
            algo_mudou_no_contexto := true;
            deve_criar_nova_versao_modelo := true;
        end if;
        --

        -- 4.4 -> Verifica se o tipo de escolha das perguntas mudou sem alteração de contexto.
        with perguntas_tipo_selecao_alterada as (
            select codigo,
                   single_choice
            from perguntas
                except
            select cp.codigo,
                   cp.single_choice
            from checklist_perguntas cp
            where cp.cod_checklist_modelo = f_cod_modelo
              and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update perguntas p
        set pergunta_mudou = true
            -- CTE irá conter apenas as perguntas que mudaram o tipo de seleção.
        from perguntas_tipo_selecao_alterada pne
             -- Só considera como alterada as perguntas que já existiam.
        where pne.codigo is not null
          and pne.codigo = p.codigo;

        if (select count(*) from perguntas where pergunta_mudou is true) > 0 and deve_criar_nova_versao_modelo is false
        then
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := true;
        end if;
        --

        -- 4.5 -> Verifica se a ordem das perguntas mudou sem alteração de contexto.
        with perguntas_ordem_alterada as (
            select codigo,
                   ordem_exibicao
            from perguntas
                except
            select cp.codigo,
                   cp.ordem
            from checklist_perguntas cp
            where cp.cod_checklist_modelo = f_cod_modelo
              and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update perguntas p
        set pergunta_mudou = true
            -- CTE irá conter apenas as perguntas que mudaram a ordem de exibição.
        from perguntas_ordem_alterada pne
             -- Só considera como alterada as perguntas que já existiam.
        where pne.codigo is not null
          and pne.codigo = p.codigo;

        if (select count(*) from perguntas where pergunta_mudou is true) > 0 and deve_criar_nova_versao_modelo is false
        then
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := true;
        end if;
        --

        -- 4.6 -> Verifica se as perguntas mudaram sem alteração de contexto.
        with perguntas_novas_ou_editadas as (
            select codigo,
                   descricao
            from perguntas
                except
            select cp.codigo,
                   cp.pergunta
            from checklist_perguntas cp
            where cp.cod_checklist_modelo = f_cod_modelo
              and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update perguntas p
        set pergunta_mudou = true
            -- CTE irá conter apenas as novas e/ou alteradas.
        from perguntas_novas_ou_editadas pne
             -- Só considera como alterada as perguntas que já existiam.
        where pne.codigo is not null
          and pne.codigo = p.codigo;

        if (select count(*) from perguntas where pergunta_mudou is true) > 0 AND deve_criar_nova_versao_modelo is false
        then
            algo_mudou_no_modelo := true;
        end if;
        --

        -- 5.1 -> Verifica se as alternativas mudaram com alteração de contexto.
        with alternativas_novas_ou_editadas as (
            select codigo,
                   -- Usamos o soundex para verificar se a descrição mudou e não o texto em si.
                   soundex(descricao),
                   prioridade,
                   tipo_outros,
                   deve_abrir_ordem_servico
            from alternativas
                except
            select cap.codigo,
                   soundex(cap.alternativa),
                   cap.prioridade,
                   cap.alternativa_tipo_outros,
                   cap.deve_abrir_ordem_servico
            from checklist_alternativa_pergunta cap
            where cap.cod_checklist_modelo = f_cod_modelo
              and cap.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update alternativas a
        set alternativa_mudou_contexto = true
            -- CTE irá conter apenas as novas e/ou alteradas.
        from alternativas_novas_ou_editadas ane
             -- Só considera como alterada as alternativas que já existiam.
        where ane.codigo is not null
          and a.codigo = ane.codigo;

        if (select count(*) from alternativas where alternativa_mudou_contexto is true or alternativa_nova is true) > 0
        then
            algo_mudou_no_contexto := true;
            deve_criar_nova_versao_modelo := true;
        end if;
        --

        -- 5.2 -> Verifica se as alternativas mudaram sem alteração de contexto.
        with alternativas_novas_ou_editadas as (
            select codigo,
                   descricao,
                   ordem_exibicao
            from alternativas
                except
            select cap.codigo,
                   cap.alternativa,
                   cap.ordem
            from checklist_alternativa_pergunta cap
            where cap.cod_checklist_modelo = f_cod_modelo
              and cap.cod_versao_checklist_modelo = f_cod_versao_modelo
        )

        update alternativas a
        set alternativa_mudou = true
            -- CTE irá conter apenas as novas e/ou alteradas.
        from alternativas_novas_ou_editadas ane
             -- Só considera como alterada as alternativas que já existiam.
        where ane.codigo is not null
          and a.codigo = ane.codigo;

        if (select count(*) from alternativas where alternativa_mudou is true) > 0 and
           deve_criar_nova_versao_modelo is false
        then
            algo_mudou_no_modelo := true;
            deve_criar_nova_versao_modelo := false;
        end if;
        --

        -- 6 -> Verifica se os cargos mudaram.
        if (select count(*)
            from (select codigo
                  from unnest(f_cod_cargos) codigo
                      except
                  select cmf.cod_funcao
                  from checklist_modelo_funcao cmf
                  where cmf.cod_checklist_modelo = f_cod_modelo) t) > 0
        then
            algo_mudou_no_modelo := true;
        end if;
        --

        -- 7 -> Verifica se os tipos de veículos mudaram.
        if (select count(*)
            from (select codigo
                  from unnest(f_cod_tipos_veiculos) codigo
                      except
                  select cmvt.cod_tipo_veiculo
                  from checklist_modelo_veiculo_tipo cmvt
                  where cmvt.cod_modelo = f_cod_modelo) t) > 0
        then
            algo_mudou_no_modelo := true;
        end if;
        --

        case
            -- A) Caso mais simples: nada mudou, não precisamos fazer nada.
            when algo_mudou_no_contexto is false and deve_criar_nova_versao_modelo is false and
                 algo_mudou_no_modelo is false
                then
                    return query
                        select null :: bigint  as codigo_item,
                               null :: boolean as item_novo,
                               null :: boolean as item_mudou_contexto,
                               null :: boolean as item_tipo_pergunta,
                               false           as algo_mudou_no_modelo,
                               false           as algo_mudou_no_contexto,
                               false           as deve_criar_nova_versao_modelo;

            -- B) Caso intermediário: algo mudou no modelo porém nada que justifique a criação de uma nova versão.
            when algo_mudou_no_modelo is true and deve_criar_nova_versao_modelo is false
                then
                    return query
                        select null :: bigint  as codigo_item,
                               null :: boolean as item_novo,
                               null :: boolean as item_mudou_contexto,
                               null :: boolean as item_tipo_pergunta,
                               true            as algo_mudou_no_modelo,
                               false           as algo_mudou_no_contexto,
                               false           as deve_criar_nova_versao_modelo;

            -- C) Caso intermediário: algo mudou no modelo com a criação de uma nova versão e mantendo o código de contexto.
            when algo_mudou_no_modelo is true and deve_criar_nova_versao_modelo is true and
                 algo_mudou_no_contexto is false
                then
                    return query
                        select p.codigo                      as codigo_item,
                               p.pergunta_nova               as item_novo,
                               p.pergunta_mudou_contexto     as item_mudou_contexto,
                               true                          as item_tipo_pergunta,
                               true                          as algo_mudou_no_modelo,
                               false                         as algo_mudou_no_contexto,
                               deve_criar_nova_versao_modelo as deve_criar_nova_versao_modelo
                        from perguntas p
                        union all
                        select a.codigo                      as codigo_item,
                               a.alternativa_nova            as item_novo,
                               a.alternativa_mudou_contexto  as item_mudou_contexto,
                               false                         as item_tipo_pergunta,
                               true                          as algo_mudou_no_modelo,
                               false                         as algo_mudou_no_contexto,
                               deve_criar_nova_versao_modelo as deve_criar_nova_versao_modelo
                        from alternativas a;

            -- D) Caso mais complexo: algo mudou e iremos precisar criar nova versão. Nesse cenário temos que retornar
            -- todas as informações.
            when algo_mudou_no_contexto is true and deve_criar_nova_versao_modelo is true
                then
                    return query
                        select p.codigo                      as codigo_item,
                               p.pergunta_nova               as item_novo,
                               p.pergunta_mudou_contexto     as item_mudou_contexto,
                               true                          as item_tipo_pergunta,
                               true                          as algo_mudou_no_modelo,
                               true                          as algo_mudou_no_contexto,
                               deve_criar_nova_versao_modelo as deve_criar_nova_versao_modelo
                        from perguntas p
                        union all
                        select a.codigo                      as codigo_item,
                               a.alternativa_nova            as item_novo,
                               a.alternativa_mudou_contexto  as item_mudou_contexto,
                               false                         as item_tipo_pergunta,
                               true                          as algo_mudou_no_modelo,
                               true                          as algo_mudou_no_contexto,
                               deve_criar_nova_versao_modelo as deve_criar_nova_versao_modelo
                        from alternativas a;
            else
                raise exception
                    'Erro! Estado ilegal dos dados. algo_mudou_no_modelo = false AND deve_criar_nova_versao_modelo = true';
            end case;

        drop table alternativas;
        drop table perguntas;
    END;
    $$;

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
                                     AND CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
      LEFT JOIN CHECKLIST_MODELO_FUNCAO CMF ON CM.COD_UNIDADE = CMF.COD_UNIDADE
                                               AND CM.CODIGO = CMF.COD_CHECKLIST_MODELO
      LEFT JOIN FUNCAO F ON CMF.COD_FUNCAO = F.CODIGO
      LEFT JOIN CHECKLIST_MODELO_VEICULO_TIPO CMVT ON CM.COD_UNIDADE = CMVT.COD_UNIDADE
                                                      AND CM.CODIGO = CMVT.COD_MODELO
      LEFT JOIN VEICULO_TIPO VT ON CMVT.COD_TIPO_VEICULO = VT.CODIGO
    WHERE CM.COD_UNIDADE = F_COD_UNIDADE
    GROUP BY CM.NOME, CM.CODIGO, CM.COD_UNIDADE, F.NOME, VT.CODIGO, CM.STATUS_ATIVO
    ORDER BY CM.STATUS_ATIVO DESC, CM.CODIGO ASC;
    $$;

    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_PERGUNTAS_MODELOS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                              F_COD_MODELO BIGINT,
                                                                              F_COD_VERSAO_MODELO BIGINT)
        RETURNS TABLE
                (
                    COD_PERGUNTA             BIGINT,
                    COD_CONTEXTO_PERGUNTA    BIGINT,
                    COD_IMAGEM               BIGINT,
                    URL_IMAGEM               TEXT,
                    PERGUNTA                 TEXT,
                    ORDEM_PERGUNTA           INTEGER,
                    SINGLE_CHOICE            BOOLEAN,
                    COD_ALTERNATIVA          BIGINT,
                    COD_CONTEXTO_ALTERNATIVA BIGINT,
                    ALTERNATIVA              TEXT,
                    PRIORIDADE               TEXT,
                    ORDEM_ALTERNATIVA        INTEGER,
                    DEVE_ABRIR_ORDEM_SERVICO BOOLEAN,
                    ALTERNATIVA_TIPO_OUTROS  BOOLEAN
                )
        LANGUAGE SQL
    AS
    $$
    SELECT CP.CODIGO                    AS COD_PERGUNTA,
           CP.CODIGO_CONTEXTO           AS COD_CONTEXTO_PERGUNTA,
           CGI.COD_IMAGEM               AS COD_IMAGEM,
           CGI.URL_IMAGEM               AS URL_IMAGEM,
           CP.PERGUNTA                  AS PERGUNTA,
           CP.ORDEM                     AS ORDEM_PERGUNTA,
           CP.SINGLE_CHOICE             AS SINGLE_CHOICE,
           CAP.CODIGO                   AS COD_ALTERNATIVA,
           CAP.CODIGO_CONTEXTO          AS COD_CONTEXTO_ALTERNATIVA,
           CAP.ALTERNATIVA              AS ALTERNATIVA,
           CAP.PRIORIDADE :: TEXT       AS PRIORIDADE,
           CAP.ORDEM                    AS ORDEM_ALTERNATIVA,
           CAP.DEVE_ABRIR_ORDEM_SERVICO AS DEVE_ABRIR_ORDEM_SERVICO,
           CAP.ALTERNATIVA_TIPO_OUTROS  AS ALTERNATIVA_TIPO_OUTROS
    FROM CHECKLIST_PERGUNTAS CP
             JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                  ON CP.CODIGO = CAP.COD_PERGUNTA
                      AND CAP.COD_UNIDADE = CP.COD_UNIDADE
                      AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
                      AND CAP.COD_VERSAO_CHECKLIST_MODELO = CP.COD_VERSAO_CHECKLIST_MODELO
             LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                       ON CGI.COD_IMAGEM = CP.COD_IMAGEM
    WHERE CP.COD_UNIDADE = F_COD_UNIDADE
      AND CP.COD_CHECKLIST_MODELO = F_COD_MODELO
      AND CP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO
    ORDER BY CP.ORDEM, CP.PERGUNTA, CAP.ORDEM;
    $$;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
end;
$func$;