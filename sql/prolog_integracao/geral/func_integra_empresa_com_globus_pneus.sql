-- Sobre:
--
-- Function utilizada para liberar a integração do módulo de Pneus para uma determinada empresa.
-- Caso a empresa já possua alguma integração rodando, será apenas liberada as novas funcionalidades, caso a empresa
-- não tenha nada rodando, será criado token e liberado o sistema para rotear as requisições do módulo de pneus.
-- O mesmo acontece caso a empresa já possua o módulo liberado, nesse cenário, iremos apenas incrementar o que ela já
-- possui com as novas configurações.
--
-- Histórico:
-- 2020-04-16 -> Function criada (rotta_natan - PLI-91).
-- 2020-07-22 -> Altera function para bloquear unidades (diogenesvanzella - PLI-174).
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