-- Sobre:
--
-- Realiza a busca de ordens de serviço para exibição em listagem.
--
-- Os parâmetros 'f_cod_unidade', 'f_limit' e 'f_offset' são obrigatórios. Os demais são filtros opcionais, basta enviar
-- 'null' que o filtro não será aplicado.
--
-- Histórico:
-- 2020-10-13 -> Query otimizada (luizfp - PL-3199).
create or replace function func_checklist_os_get_os_listagem(f_cod_unidade bigint,
                                                             f_cod_tipo_veiculo bigint,
                                                             f_placa_veiculo text,
                                                             f_status_os text,
                                                             f_limit integer,
                                                             f_offset integer)
    returns table
            (
                placa_veiculo        text,
                cod_os               bigint,
                cod_unidade_os       bigint,
                cod_checklist        bigint,
                data_hora_abertura   timestamp without time zone,
                data_hora_fechamento timestamp without time zone,
                status_os            text,
                qtd_itens_pendentes  integer,
                qtd_itens_resolvidos integer
            )
    language plpgsql
as
$$
declare
    v_status_item_pendente  constant text not null = 'P';
    v_status_item_resolvido constant text not null = 'R';
begin
    return query
        with os as (
            select cos.codigo                                                     as cod_os,
                   cos.cod_unidade                                                as cod_unidade_os,
                   count(cos.codigo)
                   filter (where cosi.status_resolucao = v_status_item_pendente)  as qtd_itens_pendentes,
                   count(cos.codigo)
                   filter (where cosi.status_resolucao = v_status_item_resolvido) as qtd_itens_resolvidos
            from checklist_ordem_servico cos
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
            where cos.cod_unidade = f_cod_unidade
            group by cos.cod_unidade, cos.codigo
        )

        select c.placa_veiculo :: text                                         as placa_veiculo,
               cos.codigo                                                      as cod_os,
               cos.cod_unidade                                                 as cod_unidade_os,
               cos.cod_checklist                                               as cod_checklist,
               -- A data/hora do check é a abertura da O.S.
               c.data_hora at time zone tz_unidade(c.cod_unidade)              as data_hora_abertura,
               cos.data_hora_fechamento at time zone tz_unidade(c.cod_unidade) as data_hora_fechamento,
               cos.status :: text                                              as status_os,
               os.qtd_itens_pendentes :: integer                               as qtd_itens_pendentes,
               os.qtd_itens_resolvidos :: integer                              as qtd_itens_resolvidos
        from checklist c
                 join checklist_ordem_servico cos
                      on cos.cod_checklist = c.codigo
                 join os
                      on os.cod_os = cos.codigo
                          and os.cod_unidade_os = cos.cod_unidade
                 join veiculo v
                      on v.placa = c.placa_veiculo
                 join veiculo_tipo vt
                      on v.cod_tipo = vt.codigo
        where c.cod_unidade = f_cod_unidade
          and case when f_cod_tipo_veiculo is null then true else f_cod_tipo_veiculo = vt.codigo end
          and case when f_placa_veiculo is null then true else f_placa_veiculo = c.placa_veiculo end
          and case when f_status_os is null then true else f_status_os = cos.status end
        order by cos.codigo desc
        limit f_limit offset f_offset;
end;
$$;