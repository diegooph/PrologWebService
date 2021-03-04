create or replace function func_veiculo_get_veiculo(f_cod_veiculo bigint)
    returns table
            (
                codigo               bigint,
                placa                text,
                cod_unidade          bigint,
                cod_empresa          bigint,
                km                   bigint,
                status_ativo         boolean,
                cod_tipo             bigint,
                cod_modelo           bigint,
                cod_diagrama         bigint,
                identificador_frota  text,
                cod_regional_alocado bigint,
                modelo               text,
                possui_hubodometro   boolean,
                motorizado           boolean,
                nome_diagrama        text,
                dianteiro            bigint,
                traseiro             bigint,
                tipo                 text,
                marca                text,
                cod_marca            bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       v.cod_unidade::bigint                                   as cod_unidade,
       v.cod_empresa::bigint                                   as cod_empresa,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       r.codigo                                                as cod_regional_alocado,
       mv.nome                                                 as modelo,
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
       vd.nome                                                 as nome_diagrama,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
       vt.nome                                                 as tipo,
       mav.nome                                                as marca,
       mav.codigo                                              as cod_marca
from veiculo v
         join modelo_veiculo mv on mv.codigo = v.cod_modelo
         join veiculo_diagrama vd on vd.codigo = v.cod_diagrama
         join veiculo_diagrama_eixos vde on vde.cod_diagrama = vd.codigo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
         join marca_veiculo mav on mav.codigo = mv.cod_marca
         join unidade u on u.codigo = v.cod_unidade
         join regional r on u.cod_regional = r.codigo
where v.codigo = f_cod_veiculo
group by v.placa,
         v.codigo,
         v.codigo,
         v.placa,
         v.cod_unidade,
         v.cod_empresa,
         v.km,
         v.status_ativo,
         v.cod_tipo,
         v.cod_modelo,
         v.possui_hubodometro,
         v.motorizado,
         v.cod_diagrama,
         v.identificador_frota,
         r.codigo,
         mv.nome,
         vd.nome,
         vt.nome,
         mav.nome,
         mav.codigo
order by v.placa;
$$;