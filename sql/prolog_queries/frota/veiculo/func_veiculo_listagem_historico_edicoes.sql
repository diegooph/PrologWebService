create or replace function func_veiculo_listagem_historico_edicoes(f_cod_empresa bigint,
                                                                   f_cod_veiculo bigint)
    returns table
            (
                codigo_historico          bigint,
                codigo_empresa_veiculo    bigint,
                codigo_veiculo_edicao     bigint,
                codigo_colaborador_edicao bigint,
                nome_colaborador_edicao   text,
                data_hora_edicao          timestamp without time zone,
                origem_edicao             text,
                origem_edicao_legivel     text,
                total_edicoes             smallint,
                informacoes_extras        text,
                placa                     text,
                identificador_frota       text,
                km_veiculo                bigint,
                status                    boolean,
                diagrama_veiculo          text,
                tipo_veiculo              text,
                modelo_veiculo            text,
                marca_veiculo             text,
                possui_hubodometro        boolean
            )
    language plpgsql
as
$$
begin
    return query
        select veh.codigo                       as codigo_historico,
               veh.cod_empresa_veiculo          as codigo_empresa_veiculo,
               veh.cod_veiculo_edicao           as codigo_veiculo_edicao,
               veh.cod_colaborador_edicao       as codigo_colaborador_edicao,
               c.nome::text                     as nome_colaborador_edicao,
               veh.data_hora_edicao_tz_aplicado as data_hora_edicao,
               veh.origem_edicao                as origem_edicao,
               oa.origem_acao                   as origem_edicao_legivel,
               veh.total_edicoes_processo       as total_edicoes,
               veh.informacoes_extras           as informacoes_extras,
               veh.placa                        as placa,
               veh.identificador_frota          as identificador_frota,
               veh.km                           as km_veiculo,
               veh.status                       as status,
               vd.nome::text                    as diagrama_veiculo,
               vt.nome::text                    as tipo_veiculo,
               mv.nome::text                    as modelo_veiculo,
               mav.nome::text                   as marca_veiculo,
               veh.possui_hubodometro           as possui_hubodometro
        from veiculo_edicao_historico veh
                 inner join types.origem_acao oa on oa.origem_acao = veh.origem_edicao
                 inner join veiculo_diagrama vd on vd.codigo = veh.cod_diagrama_veiculo
                 inner join veiculo_tipo vt on vt.codigo = veh.cod_tipo_veiculo
                 inner join modelo_veiculo mv on mv.codigo = veh.cod_modelo_veiculo
                 inner join marca_veiculo mav on mav.codigo = mv.cod_marca
                 left join colaborador c on c.codigo = veh.cod_colaborador_edicao
            and c.cod_empresa = veh.cod_empresa_veiculo
        where veh.cod_veiculo_edicao = f_cod_veiculo
          and veh.cod_empresa_veiculo = f_cod_empresa
        order by veh.data_hora_utc, veh.estado_antigo;
end;
$$;