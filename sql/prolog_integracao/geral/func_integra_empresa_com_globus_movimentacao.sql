-- Sobre:
--
-- Function utilizada para integrar Globus-Movimentacao para determinada empresa.
-- A function irá inserir todos os dados necessários para os campos personalizados na hora da movimentação.
--
-- Precondições
-- Token integrado deve existir
-- Integridade entre empresa e unidade
--
-- Histórico:
-- 2020-04-30 -> Function criada (natanrotta - PLI-130).
create or replace function
    integracao.func_integra_empresa_com_globus_movimentacao(f_token_integracao text,
                                                            f_cod_empresa bigint,
                                                            f_cpf_colaborador bigint,
                                                            out f_mensagem_de_sucesso text)
    returns text
    language plpgsql
as
$$
declare
    -- Código de agrupamento de MOVIMENTACAO.
    v_cod_funcao_agrupamento          bigint   := 14;
    -- Criamos o campo sem nenhuma opção, para que esse
    -- campo seja preenchido com opções vindas da Praxio.
    v_opcoes_selecao_campo            text[]   := '{}';
    -- Buscamos apenas as unidades que pertencem à empresa e que não estão com a integração bloqueada.
    v_cod_unidades_liberadas          bigint[] := (select array_agg(u.codigo)
                                                   from unidade u
                                                   where u.codigo not in
                                                         (select euib.cod_unidade_bloqueada
                                                          from integracao.empresa_unidades_integracao_bloqueada euib
                                                          where euib.cod_empresa = f_cod_empresa));
    v_cod_campo_personalizado_empresa bigint;
begin
    -- Verifica se token não existe.
    if not exists(select t.cod_empresa
                  from integracao.token_integracao t
                  where t.token_integracao = f_token_integracao
                    and t.cod_empresa = f_cod_empresa)
    then
        raise exception 'Erro! O Token informado não existe';
    end if;

    -- Garantimos que o colaborador é da empresa em questão.
    perform func_garante_integridade_empresa_colaborador(f_cod_empresa, f_cpf_colaborador);

    -- Criamos o campo personalizado para a empresa. O campo possuí informações default pois será
    -- utilizado de forma pré definida nas integrações.
    insert
    into campo_personalizado_empresa(cod_empresa,
                                     cod_tipo_campo,
                                     cod_funcao_prolog_agrupamento,
                                     nome,
                                     descricao,
                                     texto_auxilio_preenchimento,
                                     permite_selecao_multipla,
                                     opcoes_selecao,
                                     data_hora_ultima_atualizacao,
                                     cod_colaborador_ultima_atualizacao)
    values (f_cod_empresa,
            (select codigo from campo_personalizado_tipo where tipo = 'LISTA_SELECAO'),
            v_cod_funcao_agrupamento,
            'Unidade de Movimento',
            'Campo para selecionar a unidade de movimento',
            'Selecione...',
            false,
            v_opcoes_selecao_campo,
            now(),
            (select codigo from colaborador where cpf = f_cpf_colaborador))
    returning codigo into v_cod_campo_personalizado_empresa;

    -- Vinculamos o campo personalizado criado ao processo de movimentação.
    insert into movimentacao_campo_personalizado_unidade (cod_campo,
                                                          cod_unidade,
                                                          preenchimento_obrigatorio,
                                                          mensagem_caso_campo_nao_preenchido,
                                                          cod_funcao_prolog_agrupamento,
                                                          ordem_exibicao)
    values (v_cod_campo_personalizado_empresa,
            unnest(v_cod_unidades_liberadas),
            true,
            'Campo de seleção obrigatória',
            v_cod_funcao_agrupamento,
            1);

    -- Garantimos que o procedimento foi executado com sucesso.
    if not found then
        raise exception 'Erro ao inserir campo personalizado para a movimentação';
    end if;

    select 'Campos personalizados para a captura da unidade de movimento rodando para a empresa '
               || f_cod_empresa || '.'
    into f_mensagem_de_sucesso;
end;
$$;