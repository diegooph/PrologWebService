-- Sobre:
--
-- Function utilizada para integrar Nepomuceno-Afericao para determinada empresa.
-- A function irá integrar todos os módulos de aferição para a empresa informada.
--
-- Histórico:
-- 2020-04-17 -> Function criada (rotta_natan - PLI-91).
-- 2020-07-22 -> Altera function para bloquear unidades (diogenesvanzella - PLI-174).
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