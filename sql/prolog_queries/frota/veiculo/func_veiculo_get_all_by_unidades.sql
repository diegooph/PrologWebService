create or replace function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
                                                            f_apenas_ativos boolean,
                                                            f_cod_tipo_veiculo bigint)
    returns table
            (
                codigo              bigint,
                placa               text,
                cod_regional        bigint,
                nome_regional       text,
                cod_unidade         bigint,
                nome_unidade        text,
                km                  bigint,
                status_ativo        boolean,
                cod_tipo            bigint,
                cod_modelo          bigint,
                cod_diagrama        bigint,
                identificador_frota text,
                modelo              text,
                possui_hubodometro  boolean,
                motorizado          boolean,
                acoplado            boolean,
                nome_diagrama       text,
                dianteiro           bigint,
                traseiro            bigint,
                tipo                text,
                marca               text,
                cod_marca           bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       r.codigo                                                as cod_regional,
       r.regiao                                                as nome_regional,
       u.codigo                                                as cod_unidade,
       u.nome                                                  as nome_unidade,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       mv.nome                                                 as modelo,
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
       v.acoplado                                              as acoplado,
       vd.nome                                                 as nome_diagrama,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
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
where v.cod_unidade = any (f_cod_unidades)
  and case
          when f_apenas_ativos is null or f_apenas_ativos = false
              then true
          else v.status_ativo = true
    end
  and case
          when f_cod_tipo_veiculo is null
              then true
          else v.cod_tipo = f_cod_tipo_veiculo
    end
group by v.placa, v.codigo, v.codigo, v.placa, u.codigo, v.km, v.status_ativo, v.cod_tipo, v.cod_modelo,
         v.motorizado, v.acoplado, v.possui_hubodometro, v.cod_diagrama, v.identificador_frota, r.codigo, mv.nome,
         vd.nome, vt.nome, mav.nome, mav.codigo
order by v.placa;
$$;