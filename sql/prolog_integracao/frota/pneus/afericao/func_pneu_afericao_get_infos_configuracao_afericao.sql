create or replace function integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar_unidade             text,
                cod_auxiliar_tipo_veiculo        text,
                cod_unidade                      bigint,
                cod_tipo_veiculo                 bigint,
                nome_tipo_veiculo                text,
                cod_diagrama_veiculo             smallint,
                nome_diagrama_veiculo            text,
                qtd_eixo_dianteiro               bigint,
                qtd_eixo_traseiro                bigint,
                forma_coleta_dados_sulco         text,
                forma_coleta_dados_pressao       text,
                forma_coleta_dados_sulco_pressao text,
                pode_aferir_estepe               boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa bigint := (select u.cod_empresa
                             from public.unidade u
                             where u.codigo = any (f_cod_unidades)
                             limit 1);
begin
    return query
        with cod_auxiliares as (
            select vt.codigo                                   as cod_tipo_veiculo,
                   regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
            from veiculo_tipo vt
            where vt.cod_empresa = v_cod_empresa
        ),
             cod_auxiliares_and_unidade as (
                 select unnest(f_cod_unidades) as cod_unidade,
                        ca.cod_tipo_veiculo    as cod_tipo_veiculo,
                        ca.cod_auxiliar        as cod_auxiliar
                 from cod_auxiliares ca
             )
        select regexp_split_to_table(u.cod_auxiliar, ',')                 as cod_auxiliar_unidade,
               caau.cod_auxiliar                                          as cod_auxiliar_tipo_veiculo,
               caau.cod_unidade                                           as cod_unidade,
               caau.cod_tipo_veiculo                                      as cod_tipo_veiculo,
               vt.nome::text                                              as nome_tipo_veiculo,
               vd.codigo                                                  as cod_diagrama_veiculo,
               vd.nome::text                                              as nome_diagrama_veiculo,
               count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D')    as qtd_eixo_dianteiro,
               count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T')    as qtd_eixo_traseiro,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco)                       as forma_coleta_dados_sulco,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_pressao)                     as forma_coleta_dados_pressao,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco_pressao)               as forma_coleta_dados_sulco_pressao,
               f_if(actav.codigo is null, true, actav.pode_aferir_estepe) as pode_aferir_estepe
        from cod_auxiliares_and_unidade caau
                 join unidade u on u.codigo = caau.cod_unidade
                 join veiculo_tipo vt on caau.cod_tipo_veiculo = vt.codigo
                 join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                 join veiculo_diagrama_eixos vde on vd.codigo = vde.cod_diagrama
                 left join afericao_configuracao_tipo_afericao_veiculo actav
                           on actav.cod_tipo_veiculo = caau.cod_tipo_veiculo and actav.cod_unidade = caau.cod_unidade
        where caau.cod_unidade = any (f_cod_unidades)
          and caau.cod_auxiliar is not null
        group by u.cod_auxiliar, caau.cod_auxiliar, caau.cod_unidade, caau.cod_tipo_veiculo, vt.nome, vd.codigo,
                 vd.nome, actav.codigo
        order by caau.cod_auxiliar;
end;
$$;