create or replace function func_afericao_get_afericoes_placas_paginada(f_cod_unidades bigint[],
                                                                       f_cod_tipo_veiculo bigint,
                                                                       f_placa_veiculo text,
                                                                       f_data_inicial date,
                                                                       f_data_final date,
                                                                       f_limit bigint,
                                                                       f_offset bigint)
    returns table
            (
                km_veiculo            bigint,
                cod_afericao          bigint,
                cod_unidade           bigint,
                data_hora             timestamp without time zone,
                placa_veiculo         text,
                identificador_frota   text,
                tipo_medicao_coletada text,
                tipo_processo_coleta  text,
                forma_coleta_dados    text,
                cpf                   text,
                nome                  text,
                tempo_realizacao      bigint
            )
    language sql
as
$$
select a.km_veiculo,
       a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
       v.placa                                            as placa_veiculo,
       v.identificador_frota                              as identificador_frota,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta,
       a.forma_coleta_dados::text                         as forma_coleta_dados,
       c.cpf::text                                        as cpf,
       c.nome::text                                       as nome,
       a.tempo_realizacao                                 as tempo_realizacao
from afericao a
         join veiculo v on v.codigo = a.cod_veiculo
         join colaborador c on c.cpf = a.cpf_aferidor
where a.cod_unidade = any (f_cod_unidades)
  and case
    -- Estes parâmetros (-1 e '') foram necessários por conta da conversão de null com o Spring data JPA,
    -- No qual converte null para bytea e mesmo com cast, não consegue converter para o tipo desejado.
          when f_cod_tipo_veiculo != -1 and f_cod_tipo_veiculo is not null
              then v.cod_tipo = f_cod_tipo_veiculo
          else true end
  and case
          when f_placa_veiculo != '' and f_placa_veiculo is not null
              then v.placa = f_placa_veiculo
          else true end
  and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date between f_data_inicial and f_data_final
limit f_limit offset f_offset;
$$;