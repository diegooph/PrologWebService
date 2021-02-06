begin transaction;
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
    -- Primeiro criamos uma nova versão.
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

    -- Agora atualizamos o modelo de checklist.
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

CREATE FUNCTION FUNC_CHECKLIST_UPDATE_MODELO_CHECKLIST_INFOS(F_COD_UNIDADE BIGINT,
                                                             F_COD_MODELO BIGINT,
                                                             F_NOME_MODELO TEXT,
                                                             F_COD_CARGOS BIGINT[],
                                                             F_COD_TIPOS_VEICULOS BIGINT[]) RETURNS BOOLEAN
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
    ELSE
        RAISE EXCEPTION 'Não foi possível limpar as entradas da tabela CHECKLIST_MODELO_FUNCAO';
    END IF;
    --

    RETURN TRUE;
END;
$$;


create extension fuzzystrmatch;

create or replace function func_checklist_analisa_mudancas_modelo(f_cod_modelo bigint,
                                                                  f_cod_versao_modelo bigint,
                                                                  f_cod_cargos bigint[],
                                                                  f_cod_tipos_veiculos bigint[],
                                                                  f_perguntas_alternativas_json jsonb)
    returns table
            (
                codigo_item          bigint,
                item_novo            boolean,
                item_mudou_contexto  boolean,
                item_tipo_pergunta   boolean,
                algo_mudou_no_modelo boolean
            )
    language plpgsql
as
$$
declare
    algo_mudou_no_modelo boolean := false;
begin
    -- 1 -> Cria tabelas temporárias para nos ajudarem a trabalhar com os dados.
    create temp table if not exists perguntas
    (
        _id               bigserial not null,
        codigo            bigint,
        cod_imagem        bigint,
        descricao         text      not null,
        single_choice     boolean   not null,
        ordem_exibicao    integer   not null,
        pergunta_nova     boolean   not null,
        pergunta_alterada boolean
    );

    create temp table if not exists alternativas
    (
        _id                      bigserial not null,
        codigo                   bigint,
        descricao                text      not null,
        prioridade               text      not null,
        tipo_outros              boolean   not null,
        ordem_exibicao           integer   not null,
        deve_abrir_ordem_servico boolean   not null,
        alternativa_nova         boolean   not null,
        alternativa_alterada     boolean
    );
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
    from jsonb_array_elements(f_perguntas_alternativas_json) src;
    --

    -- 3 -> Insere as alternativas.
    with cte as (
        select jsonb_array_elements(jsonb_array_elements(f_perguntas_alternativas_json) -> 'alternativas') src
    )
    insert
    into alternativas (codigo, descricao, prioridade, tipo_outros, ordem_exibicao, deve_abrir_ordem_servico, alternativa_nova)
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

    -- 4 -> Verifica se as perguntas mudaram.
    with perguntas_novas_ou_editadas as (
        select codigo,
               cod_imagem,
               -- Usamos o soundex para verificar se a descrição mudou e não o texto em si.
               soundex(descricao),
               single_choice,
               ordem_exibicao
        from perguntas
            except
        select cp.codigo,
               cp.cod_imagem,
               soundex(cp.pergunta),
               cp.single_choice,
               cp.ordem
        from checklist_perguntas cp
        where cp.cod_checklist_modelo = f_cod_modelo
          and cp.cod_versao_checklist_modelo = f_cod_versao_modelo
    )

    update perguntas
    set pergunta_alterada = true
    from perguntas_novas_ou_editadas pne
    -- Só considera como alterada as perguntas que já existiam.
    where pne.codigo is not null;

    if (select count(*) from perguntas where pergunta_alterada is true or pergunta_nova is true) > 0
    then
        algo_mudou_no_modelo := true;
    end if;
    --

    -- 5 -> Verifica se as alternativas mudaram.
    with alternativas_novas_ou_editadas as (
        select codigo,
               -- Usamos o soundex para verificar se a descrição mudou e não o texto em si.
               soundex(descricao),
               prioridade,
               tipo_outros,
               ordem_exibicao,
               deve_abrir_ordem_servico
        from alternativas
            except
        select cap.codigo,
               soundex(cap.alternativa),
               cap.prioridade,
               cap.alternativa_tipo_outros,
               cap.ordem,
               cap.deve_abrir_ordem_servico
        from checklist_alternativa_pergunta cap
        where cap.cod_checklist_modelo = f_cod_modelo
          and cap.cod_versao_checklist_modelo = f_cod_versao_modelo
    )

    update alternativas
    set alternativa_alterada = true
    from alternativas_novas_ou_editadas ane
    -- Só considera como alterada as alternativas que já existiam.
    where ane.codigo is not null;

    if (select count(*) from alternativas where alternativa_alterada is true or alternativa_nova is true) > 0
    then
        algo_mudou_no_modelo := true;
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

    if algo_mudou_no_modelo
    then
        return query
            select null, null, null, null, true;
    else
        select p.codigo,
               p.pergunta_nova,
               p.pergunta_alterada,
               true,
               algo_mudou_no_modelo
        from perguntas p
        union all
        select a.codigo,
               a.alternativa_nova,
               a.alternativa_alterada,
               false,
               algo_mudou_no_modelo
        from alternativas a;
    end if;
END;
$$;


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
end transaction;