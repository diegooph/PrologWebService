create or replace function interno.func_clona_colaboradores(f_cod_empresa_base bigint,
                                                            f_cod_unidade_base bigint,
                                                            f_cod_empresa_usuario bigint,
                                                            f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cpf_prefixo_padrao          text   := '0338328';
    v_cpf_sufixo_padrao           bigint := 0;
    v_cpf_verificacao             bigint;
    v_cpfs_validos_cadastro       bigint[];
    v_tentativa_buscar_cpf_valido bigint := 0;
begin
    -- VERIFICA SE EXISTEM EQUIPES DE VEÍCULOS PARA COPIAR
    if not EXISTS(select e.codigo from equipe e where e.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem equipes para serem copiadas da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM SETORES PARA COPIAR
    if not EXISTS(select se.codigo from setor se where se.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem setores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- VERIFICA SE EXISTEM CARGOS PARA COPIAR
    if not EXISTS(select f.codigo from funcao f where f.cod_empresa = f_cod_empresa_base)
    then
        raise exception
            'Não existem cargos para serem copiados da empresa de código: %.' , f_cod_empresa_base;
    end if;

    -- VERIFICA SE EXISTEM COLABORADORES PARA COPIAR
    if not EXISTS(select cd.codigo from colaborador_data cd where cd.cod_unidade = f_cod_unidade_base)
    then
        raise exception
            'Não existem colaboradores para serem copiados da unidade de código: %.' , f_cod_unidade_base;
    end if;

    -- COPIA AS EQUIPES
    insert into equipe (nome,
                        cod_unidade)
    select e.nome,
           f_cod_unidade_usuario
    from equipe e
    where e.cod_unidade = f_cod_unidade_base;

    -- COPIA OS SETORES
    insert into setor(nome,
                      cod_unidade)
    select se.nome,
           f_cod_unidade_usuario
    from setor se
    where se.cod_unidade = f_cod_unidade_base;

    -- COPIA AS FUNÇÕES
    insert into funcao_data (nome,
                             cod_empresa)
    select f.nome,
           f_cod_empresa_usuario
    from funcao f
    where f.cod_empresa = f_cod_empresa_base
    on conflict do nothing;

    --SELECIONA CPFS VÁLIDOS PARA CADASTRO.
    while (((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) < (select COUNT(cd.cpf)
                                                          from colaborador_data cd
                                                          where cd.cod_unidade = f_cod_unidade_base)) or
           ((ARRAY_LENGTH(v_cpfs_validos_cadastro, 1)) is null))
        loop
        --EXISTEM 10000 CPFS DISPONÍVEIS PARA CADASTRO (03383280000 ATÉ 03383289999),
        --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            if (v_tentativa_buscar_cpf_valido = 10000)
            then
                raise exception
                    'Não existem cpfs disponíveis para serem cadastrados';
            end if;
            v_cpf_verificacao := (CONCAT(v_cpf_prefixo_padrao, LPAD(v_cpf_sufixo_padrao::text, 4, '0')))::bigint;
            if not EXISTS(select cd.cpf from colaborador_data cd where cd.cpf = v_cpf_verificacao)
            then
                -- CPFS VÁLIDOS PARA CADASTRO
                v_cpfs_validos_cadastro := ARRAY_APPEND(v_cpfs_validos_cadastro, v_cpf_verificacao);
            end if;
            v_cpf_sufixo_padrao := v_cpf_sufixo_padrao + 1;
            v_tentativa_buscar_cpf_valido := v_tentativa_buscar_cpf_valido + 1;
        end loop;

    perform setval('colaborador_data_codigo_seq', (select max(cd.codigo + 1) from colaborador_data cd));

    with cpfs_validos_cadastro as (
        select ROW_NUMBER() over () as codigo,
               cdn                  as cpf_novo_cadastro
        from UNNEST(v_cpfs_validos_cadastro) cdn),
         colaboradores_base as (
             select ROW_NUMBER() over () as codigo,
                    co.cpf               as cpf_base,
                    co.nome              as nome_base,
                    co.data_nascimento   as data_nascimento_base,
                    co.data_admissao     as data_admissao_base,
                    co.cod_equipe        as cod_equipe_base,
                    co.cod_setor         as cod_setor_base,
                    co.cod_funcao        as cod_funcao_base,
                    co.cod_permissao     as cod_permissao_base
             from colaborador co
             where cod_unidade = f_cod_unidade_base
         ),
         dados_de_para as (
             select cvc.cpf_novo_cadastro   as cpf_cadastro,
                    cb.cpf_base             as cpf_base,
                    cb.nome_base            as nome_base,
                    cb.data_nascimento_base as data_nascimento_base,
                    cb.data_admissao_base   as data_admissao_base,
                    cb.cod_permissao_base   as cod_permissao_base,
                    eb.codigo               as cod_equipe_base,
                    en.codigo               as cod_equipe_nova,
                    sb.codigo               as cod_setor_base,
                    sn.codigo               as cod_setor_novo,
                    fb.codigo               as cod_funcao_base,
                    fn.codigo               as cod_funcao_novo
             from colaboradores_base cb
                      join equipe eb on eb.codigo = cb.cod_equipe_base
                      join equipe en on eb.nome = en.nome
                      join setor sb on cb.cod_setor_base = sb.codigo
                      join setor sn on sb.nome = sn.nome
                      join funcao fb on cb.cod_funcao_base = fb.codigo
                      join funcao fn on fb.nome = fn.nome
                      join cpfs_validos_cadastro cvc on cvc.codigo = cb.codigo
             where eb.cod_unidade = f_cod_unidade_base
               and en.cod_unidade = f_cod_unidade_usuario
               and sb.cod_unidade = f_cod_unidade_base
               and sn.cod_unidade = f_cod_unidade_usuario
               and fb.cod_empresa = f_cod_empresa_base
               and fn.cod_empresa = f_cod_empresa_usuario)
         -- INSERE OS COLABORADORES DE->PARA.
    insert
    into colaborador_data(cpf,
                          data_nascimento,
                          data_admissao,
                          status_ativo,
                          nome,
                          cod_equipe,
                          cod_funcao,
                          cod_unidade,
                          cod_permissao,
                          cod_empresa,
                          cod_setor,
                          cod_unidade_cadastro,
                          deletado)
    select ddp.cpf_cadastro,
           ddp.data_nascimento_base,
           ddp.data_admissao_base,
           true,
           ddp.nome_base,
           ddp.cod_equipe_nova,
           ddp.cod_funcao_novo,
           f_cod_unidade_usuario,
           ddp.cod_permissao_base,
           f_cod_empresa_usuario,
           ddp.cod_setor_novo,
           f_cod_unidade_usuario,
           false
    from dados_de_para ddp;
end;
$$;