-- Sobre:
-- Busca todos os colaboradores ATIVOS de uma empresa que possuam uma permissão específica do ProLog.
-- A lista de permissões (e seus códigos para uso nessa function) pode ser encontrada na tabela FUNCAO_PROLOG_V11.
--
-- Histórico:
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-07-02 -> Altera function para não considerar permissões bloqueadas (luiz_fp - PL-2671)
-- 2020-07-08 -> Remove verificação das permissões bloqueadas (diogenesvanzella - PL-2671)
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
create or replace function suporte.func_colaborador_busca_por_permissao_empresa(f_cod_empresa bigint,
                                                                                f_cod_permissao bigint)
    returns table
            (
                funcionalidade  text,
                permissao       text,
                cod_empresa     bigint,
                empresa         text,
                cod_unidade     bigint,
                unidade         text,
                cod_colaborador bigint,
                colaborador     text,
                cpf             bigint,
                data_nascimento date,
                cargo           text
            )
    language plpgsql
as
$$
begin
    perform suporte.func_historico_salva_execucao();
    return query
        select fpa.nome::text    as funcionalidade,
               fp.funcao::text   as permissao,
               e.codigo          as cod_empresa,
               e.nome::text      as empresa,
               u.codigo          as cod_unidade,
               u.nome::text      as unidade,
               c.codigo          as cod_colaborador,
               c.nome::text      as colaborador,
               c.cpf             as cpf,
               c.data_nascimento as data_nascimento,
               f.nome::text      as cargo
        from colaborador c
                 left join cargo_funcao_prolog_v11 cfp
                           on cfp.cod_funcao_colaborador = c.cod_funcao and cfp.cod_unidade = c.cod_unidade
                 left join unidade u on u.codigo = c.cod_unidade
                 left join empresa e on e.codigo = c.cod_empresa
                 left join funcao f on f.codigo = c.cod_funcao
                 left join funcao_prolog_v11 fp on fp.codigo = cfp.cod_funcao_prolog
                 left join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
        where c.cod_empresa = f_cod_empresa
          and c.status_ativo = true
          and cfp.cod_funcao_prolog = f_cod_permissao
        order by unidade, colaborador;
end;
$$;