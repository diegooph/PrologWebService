create or replace function func_cargos_get_permissoes_detalhadas(f_cod_unidade bigint,
                                                                 f_cod_cargo bigint)
    returns table
            (
                cod_cargo                       bigint,
                cod_unidade_cargo               bigint,
                nome_cargo                      text,
                cod_pilar                       bigint,
                nome_pilar                      varchar(255),
                cod_funcionalidade              smallint,
                nome_funcionalidade             varchar(255),
                cod_permissao                   bigint,
                nome_permissao                  varchar(255),
                impacto_permissao               prolog_impacto_permissao_type,
                descricao_permissao             text,
                permissao_associada             boolean,
                permissao_bloqueada             boolean,
                cod_motivo_permissao_bloqueada  bigint,
                nome_motivo_permissao_bloqueada citext,
                observacao_permissao_bloqueada  text
            )
    language plpgsql
as
$$
declare
    pilares_liberados_unidade bigint[] := (select array_agg(upp.cod_pilar)
                                           from unidade_pilar_prolog upp
                                           where upp.cod_unidade = f_cod_unidade);
begin
    return query
        with permissoes_cargo_unidade as (
            select cfp.cod_funcao_colaborador as cod_cargo,
                   cfp.cod_unidade            as cod_unidade_cargo,
                   cfp.cod_funcao_prolog      as cod_funcao_prolog,
                   cfp.cod_pilar_prolog       as cod_pilar_prolog
            from cargo_funcao_prolog_v11 cfp
            where cfp.cod_unidade = f_cod_unidade
              and cfp.cod_funcao_colaborador = f_cod_cargo
        )

        select f_cod_cargo                       as cod_cargo,
               f_cod_unidade                     as cod_unidade_cargo,
               f.nome::text                      as nome_cargo,
               fp.cod_pilar                      as cod_pilar,
               pp.pilar                          as nome_pilar,
               fp.cod_agrupamento                as cod_funcionalidade,
               fpa.nome                          as nome_funcionalidade,
               fp.codigo                         as cod_permissao,
               fp.funcao                         as nome_permissao,
               fp.impacto                        as impacto_permissao,
               fp.descricao                      as descricao_permissao,
               pcu.cod_unidade_cargo is not null as permissao_associada,
               fpb.cod_funcao_prolog is not null as permissao_bloqueada,
               fpb.cod_motivo_bloqueio           as cod_motivo_permissao_bloqueada,
               fpmb.motivo                       as nome_motivo_permissao_bloqueada,
               f_if(fpb.observacao_bloqueio is null,
                    fpmb.descricao,
                    fpb.observacao_bloqueio)     as observacao_permissao_bloqueada
        from pilar_prolog pp
                 join funcao_prolog_v11 fp on fp.cod_pilar = pp.codigo
                 join unidade_pilar_prolog upp on upp.cod_pilar = pp.codigo
                 join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
                 join funcao f on f.codigo = f_cod_cargo
                 left join permissoes_cargo_unidade pcu on pcu.cod_funcao_prolog = fp.codigo
                 left join funcao_prolog_bloqueada fpb
                           on fp.codigo = fpb.cod_funcao_prolog
                               and fp.cod_pilar = fpb.cod_pilar_funcao
                               and fpb.cod_unidade = f_cod_unidade
                 left join funcao_prolog_motivo_bloqueio fpmb on fpb.cod_motivo_bloqueio = fpmb.codigo
        where upp.cod_unidade = f_cod_unidade
          and fp.cod_pilar = any (pilares_liberados_unidade)
        order by pp.pilar, fp.cod_agrupamento, fp.impacto desc;
end;
$$;