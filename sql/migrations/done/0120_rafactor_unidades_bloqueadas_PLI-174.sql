-- Adicionamos colunas novas para permitir bloquear unidades para recursos específicos.
alter table integracao.empresa_unidades_integracao_bloqueada
    add column if not exists chave_sistema text not null;

alter table integracao.empresa_unidades_integracao_bloqueada
    add column if not exists recuro_integrado text not null;

-- Criamos uma FK para referenciar melhor as tabelas e permitir o uso correto das informações.
alter table integracao.empresa_unidades_integracao_bloqueada
    drop constraint if exists fk_empresa_sistema_unidade_bloqueada;

alter table integracao.empresa_unidades_integracao_bloqueada
    add constraint fk_empresa_sistema_unidade_bloqueada foreign key (cod_empresa, chave_sistema, recuro_integrado)
        references integracao.empresa_integracao_sistema (cod_empresa, chave_sistema, recurso_integrado);

-- Alteramos o unique da tabela para considerar a chave do sistema e o recurso integrado
alter table integracao.empresa_unidades_integracao_bloqueada
    drop constraint if exists unique_unidade_integracao_bloqueada;

alter table integracao.empresa_unidades_integracao_bloqueada
    drop constraint if exists unique_unidade_integracao_sistema_bloqueada;

alter table integracao.empresa_unidades_integracao_bloqueada
    add constraint unique_unidade_integracao_sistema_bloqueada
        unique (cod_empresa, cod_unidade_bloqueada, chave_sistema, recuro_integrado);

-- Reestrutura function para buscar as unidades bloqueadas.
drop function if exists integracao.func_geral_busca_unidades_bloqueadas_integracao(f_user_token text);
create or replace function integracao.func_geral_busca_unidades_bloqueadas_integracao(f_user_token text,
                                                                                      f_sistema_key text,
                                                                                      f_recurso_integrado text)
    returns table
            (
                cod_unidade_bloqueada bigint
            )
    language sql
as
$$
select euib.cod_unidade_bloqueada
from integracao.empresa_unidades_integracao_bloqueada euib
where euib.cod_empresa = (select cod_empresa
                          from token_autenticacao ta
                                   join colaborador c on c.codigo = ta.cod_colaborador
                          where ta.token = f_user_token)
  and euib.chave_sistema = f_sistema_key
  and euib.recuro_integrado = f_recurso_integrado
$$;

create or replace function integracao.func_geral_busca_unidades_bloqueadas_by_token_integracao(f_token_integracao text,
                                                                                               f_sistema_key text,
                                                                                               f_recurso_integrado text)
    returns table
            (
                cod_unidade_bloqueada bigint
            )
    language sql
as
$$
select euib.cod_unidade_bloqueada
from integracao.empresa_unidades_integracao_bloqueada euib
where euib.cod_empresa = (select cod_empresa
                          from integracao.token_integracao ti
                          where ti.token_integracao = f_token_integracao)
  and euib.chave_sistema = f_sistema_key
  and euib.recuro_integrado = f_recurso_integrado
$$;

-- Reestruturamos functions de liberação de integração das empresas
create or replace function
    integracao.func_integra_empresa_com_globus_checklist(f_cod_empresa bigint,
                                                         f_token_integracao text,
                                                         f_empresa_ja_possui_integracao boolean,
                                                         f_cod_modelos_checklist_integrados bigint[],
                                                         f_cod_unidades_nao_integradas bigint[] default null,
                                                         out f_mensagem_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    v_chave_sistema        text   := 'GLOBUS_PICCOLOTUR';
    v_modulos_checklist    text[] := ('{"CHECKLIST",
                                           "CHECKLIST_OFFLINE",
                                           "CHECKLIST_MODELO",
                                           "CHECKLIST_ORDEM_SERVICO"}');
    v_cod_modelo_checklist bigint;
begin
    -- A flag 'f_incrementa_integracao_empresa' indica se essa empresa já possui um token e iremos apenas liberar
    -- uma nova funcionalidade, ou se ela não possui nada e estamos liberando a primeira funcionalidade dela.
    if (f_empresa_ja_possui_integracao)
    then
        -- Se estamos liberando uma funcionalidade para uma empresa que já possui integração, deve existir o token dela.
        if (not exists(select *
                       from integracao.token_integracao t
                       where t.token_integracao = f_token_integracao
                         and t.cod_empresa = f_cod_empresa))
        then
            raise exception 'Erro! Token e Empresa informados não existem.
                Para liberar uma funcionalidade para uma empresa que já possui
                integração é necessário informar o Código e o Token dela.';
        end if;
    else
        -- Se é a primeira funcionalidade que iremos liberar para a empresa, então nem Token nem Empresa
        -- devem estar mapeados.
        if (exists(select *
                   from integracao.token_integracao t
                   where t.token_integracao = f_token_integracao
                      or t.cod_empresa = f_cod_empresa))
        then
            raise exception 'Erro! Empresa ou Token já estão mapeados na integração';
        else
            -- Aqui podemos inserir o token com segurança.
            insert into integracao.token_integracao (cod_empresa, token_integracao)
            values (f_cod_empresa, f_token_integracao);
        end if;
    end if;

    -- Adiciona a chave do sistema para a empresa, liberando o roteamento de requisições no Servidor.
    -- Caso a empresa já possua esse módulo liberado, não fazemos nada. Caso possua liberação parcial (apenas uma
    -- chave de sistema), faremos a liberação total, que incluem todas as chaves de sistema deste módulo.
    insert into integracao.empresa_integracao_sistema(cod_empresa, chave_sistema, recurso_integrado)
    values (f_cod_empresa, v_chave_sistema, unnest(v_modulos_checklist))
    on conflict do nothing;

    -- Configuramos as unidades da empresa que não possuirão integração nesse momento.
    if (f_size_array(f_cod_unidades_nao_integradas) > 0)
    then
        -- Não precisamos verificar se cada unidade pertence à empresa, pois a FK da tabela garante isso.
        insert into integracao.empresa_unidades_integracao_bloqueada(cod_empresa,
                                                                     cod_unidade_bloqueada,
                                                                     chave_sistema,
                                                                     recuro_integrado)
        select f_cod_empresa,
               unidades.cod_unidade,
               v_chave_sistema,
               unnest(v_modulos_checklist)
        from (select unnest(f_cod_unidades_nao_integradas) as cod_unidade) as unidades
        on conflict on constraint unique_unidade_integracao_sistema_bloqueada do nothing;
    end if;

    -- Liberamos os modelos de checklist que serão utilizados na integração.
    -- Modelos que não estiver mapeados aqui utilizarão o fluxo do Prolog e não serão integrados.
    if (f_size_array(f_cod_modelos_checklist_integrados) > 0)
    then
        foreach v_cod_modelo_checklist in array f_cod_modelos_checklist_integrados
            loop
            -- Validamos se o modelo do checklist sendo mapeado, pertence à empresa sendo integrada. Caso o modelo
            -- já estiver liberado, não fazemos nada, apenas liberamos os novos.
                if ((select u.cod_empresa
                     from unidade u
                     where u.codigo = (select cm.cod_unidade
                                       from checklist_modelo_data cm
                                       where cm.codigo = v_cod_modelo_checklist)) = f_cod_empresa)
                then
                    insert into piccolotur.modelo_checklist_integrado(cod_unidade, cod_modelo_checklist)
                    values ((select cm.cod_unidade
                             from checklist_modelo_data cm
                             where cm.codigo = v_cod_modelo_checklist), v_cod_modelo_checklist)
                    on conflict do nothing;
                else
                    raise exception
                        'Erro! O modelo de checklist % não pertence a uma unidade da empresa %',
                        v_cod_modelo_checklist, f_cod_empresa;
                end if;
            end loop;
    end if;

    -- Mensagem de sucesso.
    select 'Integração Globus-Checklist rodando para a empresa ' || f_cod_empresa || '.'
    into f_mensagem_sucesso;
end ;
$$;


create or replace function
    integracao.func_integra_empresa_com_globus_pneus(f_cod_empresa bigint,
                                                     f_token_integracao text,
                                                     f_empresa_ja_possui_integracao boolean,
                                                     f_deve_abrir_servico_pneu boolean,
                                                     f_deve_sobrescrever_pneu boolean,
                                                     f_deve_sobrescrever_veiculo boolean,
                                                     f_cod_unidades_nao_integradas bigint[] default null,
                                                     out f_mensagem_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    v_chave_sistema_api_pneu      text   := 'API_PROLOG';
    v_chave_sistema_globus        text   := 'GLOBUS_PICCOLOTUR';
    v_modulos_pneu_sistema_globus text[] := ('{"MOVIMENTACAO"}');
    v_modulos_pneu_api_pneu       text[] := ('{"AFERICAO",
                                             "AFERICAO_SERVICO",
                                             "PNEUS",
                                             "PNEU_TRANSFERENCIA",
                                             "VEICULOS",
                                             "VEICULO_TRANSFERENCIA"}');
begin
    -- A flag 'f_incrementa_integracao_empresa' indica se essa empresa já possui um token e iremos apenas liberar
    -- uma nova funcionalidade, ou se ela não possui nada e estamos liberando a primeira funcionalidade dela.
    if (f_empresa_ja_possui_integracao)
    then
        -- Se estamos liberando uma funcionalidade para uma empresa que já possui integração, deve existir o token dela.
        if (not exists(select *
                       from integracao.token_integracao t
                       where t.token_integracao = f_token_integracao
                         and t.cod_empresa = f_cod_empresa))
        then
            raise exception 'Erro! Token e Empresa informados não existem.
                Para liberar uma funcionalidade para uma empresa que já possui
                integração é necessário informar o Código e o Token dela.';
        end if;
    else
        -- Se é a primeira funcionalidade que iremos liberar para a empresa, então nem Token nem Empresa
        -- devem estar mapeados.
        if (exists(select *
                   from integracao.token_integracao t
                   where t.token_integracao = f_token_integracao
                      or t.cod_empresa = f_cod_empresa))
        then
            raise exception 'Erro! Empresa ou Token já estão mapeados na integração';
        else
            -- Aqui podemos inserir o token com segurança.
            insert into integracao.token_integracao (cod_empresa, token_integracao)
            values (f_cod_empresa, f_token_integracao);
        end if;
    end if;

    -- Liberamos os recursos do sistema API_PROLOG. Eles farão com que o roteamento seja ativado no Servidor.
    insert into integracao.empresa_integracao_sistema(cod_empresa, chave_sistema, recurso_integrado)
    values (f_cod_empresa, v_chave_sistema_api_pneu, unnest(v_modulos_pneu_api_pneu))
    on conflict do nothing;

    -- Liberamos os recursos do sistema GLOBUS_PICCOLOTUR. Eles farão com que o roteamento seja ativado no Servidor.
    insert into integracao.empresa_integracao_sistema(cod_empresa, chave_sistema, recurso_integrado)
    values (f_cod_empresa, v_chave_sistema_globus, unnest(v_modulos_pneu_sistema_globus))
    on conflict do nothing;

    -- Configuramos as unidades que estarão bloqueadas nessa integração.
    if (f_size_array(f_cod_unidades_nao_integradas) > 0)
    then
        -- As constraints da tabela garantem que apenas unidades da empresa correta serão bloqueadas.
        insert into integracao.empresa_unidades_integracao_bloqueada(cod_empresa,
                                                                     cod_unidade_bloqueada,
                                                                     chave_sistema,
                                                                     recuro_integrado)
        select f_cod_empresa,
               unidades.cod_unidade,
               v_chave_sistema_api_pneu,
               unnest(v_modulos_pneu_api_pneu)
        from (select unnest(f_cod_unidades_nao_integradas) as cod_unidade) as unidades
        on conflict on constraint unique_unidade_integracao_sistema_bloqueada do nothing;

        insert into integracao.empresa_unidades_integracao_bloqueada(cod_empresa,
                                                                     cod_unidade_bloqueada,
                                                                     chave_sistema,
                                                                     recuro_integrado)
        select f_cod_empresa,
               unidades.cod_unidade,
               v_chave_sistema_globus,
               unnest(v_modulos_pneu_sistema_globus)
        from (select unnest(f_cod_unidades_nao_integradas) as cod_unidade) as unidades
        on conflict on constraint unique_unidade_integracao_sistema_bloqueada do nothing;
    end if;

    -- Configuramos a carga inicial na integração. Caso já tem alguma configuração iremos atualizar os valores.
    insert into integracao.empresa_config_carga_inicial(cod_empresa, sobrescreve_pneus, sobrescreve_veiculos)
    values (f_cod_empresa, f_deve_sobrescrever_pneu, f_deve_sobrescrever_veiculo)
    on conflict on constraint pk_empresa_config_carga_inicial
        do update set sobrescreve_pneus    = f_deve_sobrescrever_pneu,
                      sobrescreve_veiculos = f_deve_sobrescrever_veiculo;

    -- Configuramos a abertura de serviço de pneu na integração. Caso já tem alguma configuração iremos atualizar
    -- os valores.
    insert into integracao.empresa_config_abertura_servico_pneu(cod_empresa, deve_abrir_servico_pneu)
    values (f_cod_empresa, f_deve_abrir_servico_pneu)
    on conflict on constraint unique_empresa_config_abertura_servico_pneu
        do update set deve_abrir_servico_pneu = f_deve_abrir_servico_pneu;

    select 'Integração Globus-Pneus rodando para a empresa ' || f_cod_empresa || '. '
        'Agora de forma manual, deve-se adicionar as URL na tabela "EMPRESA_INTEGRACAO_METODOS"'
    into f_mensagem_sucesso;
end;
$$;


create or replace function
    integracao.func_integra_empresa_com_nepomuceno_afericao(f_cod_empresa bigint,
                                                            f_token_integracao text,
                                                            f_cod_unidades_nao_integradas bigint[] default null,
                                                            out f_mensagem_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    v_chave_sistema_nepomuceno text   := 'PROTHEUS_NEPOMUCENO';
    v_modulos_nepomuceno       text[] := ('{"AFERICAO", "TIPO_VEICULO"}');
begin
    -- verifica se token não existe e adiciona ele.
    if exists(select *
              from integracao.token_integracao t
              where t.token_integracao = f_token_integracao
                and t.cod_empresa = f_cod_empresa)
    then
        raise exception 'Erro! O Token informado já existe';
    else
        insert into integracao.token_integracao(cod_empresa, token_integracao)
        values (f_cod_empresa, f_token_integracao);
    end if;

    -- adiciona empresa integração sistema.
    insert into integracao.empresa_integracao_sistema(cod_empresa, chave_sistema, recurso_integrado)
    values (f_cod_empresa, v_chave_sistema_nepomuceno, unnest(v_modulos_nepomuceno))
    on conflict do nothing;

    -- Config empresa_integracao_sistema.
    if (f_size_array(f_cod_unidades_nao_integradas) > 0)
    then
        insert into integracao.empresa_unidades_integracao_bloqueada(cod_empresa,
                                                                     cod_unidade_bloqueada,
                                                                     chave_sistema,
                                                                     recuro_integrado)
        select f_cod_empresa,
               unidades.cod_unidade,
               v_chave_sistema_nepomuceno,
               unnest(v_modulos_nepomuceno)
        from (select unnest(f_cod_unidades_nao_integradas) as cod_unidade) as unidades
        on conflict on constraint unique_unidade_integracao_sistema_bloqueada do nothing;
    end if;

    select 'Integração Nepomuceno-Afericao rodando para a empresa ' || f_cod_empresa || '. '
        'Agora de forma manual, deve-se adicionar as URL na tabela "EMPRESA_INTEGRACAO_METODOS"'
    into f_mensagem_sucesso;
end;
$$;