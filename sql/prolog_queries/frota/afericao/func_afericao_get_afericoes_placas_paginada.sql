-- Function usada no v3.
create or replace function func_afericao_get_afericoes_placas_paginada(f_cod_unidades bigint[],
                                                                       f_cod_tipo_veiculo bigint,
                                                                       f_cod_veiculo bigint,
                                                                       f_data_inicial date,
                                                                       f_data_final date,
                                                                       f_limit integer,
                                                                       f_offset integer,
                                                                       f_incluir_medidas boolean default false)
    returns table
            (
                km_veiculo                     bigint,
                cod_afericao                   bigint,
                cod_unidade                    bigint,
                data_hora_afericao_utc         timestamp with time zone,
                data_hora_afericao_tz_aplicado timestamp without time zone,
                cod_veiculo                    bigint,
                placa_veiculo                  text,
                identificador_frota            text,
                tipo_medicao_coletada          text,
                tipo_processo_coleta           text,
                forma_coleta_dados             text,
                cod_colaborador                bigint,
                cpf                            text,
                nome                           text,
                tempo_realizacao               bigint,
                cod_pneu                       bigint,
                codigo_cliente_pneu            text,
                posicao                        integer,
                psi                            real,
                vida_momento_afericao          integer,
                altura_sulco_interno           real,
                altura_sulco_central_interno   real,
                altura_sulco_central_externo   real,
                altura_sulco_externo           real
            )
    language sql
as
$$
select a.km_veiculo,
       a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade,
       a.data_hora                                        as data_hora_afericao_utc,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao_tz_aplicado,
       v.codigo                                           as cod_veiculo,
       v.placa                                            as placa_veiculo,
       v.identificador_frota                              as identificador_frota,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta,
       a.forma_coleta_dados::text                         as forma_coleta_dados,
       c.codigo                                           as cod_colaborador,
       c.cpf::text                                        as cpf,
       c.nome::text                                       as nome,
       a.tempo_realizacao                                 as tempo_realizacao,
       av.cod_pneu                                        as cod_pneu,
       p.codigo_cliente                                   as codigo_cliente_pneu,
       av.posicao                                         as posicao,
       av.psi                                             as psi,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.altura_sulco_interno                            as altura_sulco_interno,
       av.altura_sulco_central_interno                    as altura_sulco_central_interno,
       av.altura_sulco_central_externo                    as altura_sulco_central_externo,
       av.altura_sulco_externo                            as altura_sulco_externo
from afericao a
         join veiculo v on v.codigo = a.cod_veiculo
         join colaborador c on c.cpf = a.cpf_aferidor
         left join afericao_valores av on f_incluir_medidas and av.cod_afericao = a.codigo
         left join pneu p on f_incluir_medidas and p.codigo = av.cod_pneu
where a.cod_unidade = any (f_cod_unidades)
  and case
          when f_cod_tipo_veiculo is not null
              then v.cod_tipo = f_cod_tipo_veiculo
          else true end
  and case
          when f_cod_veiculo is not null
              then v.codigo = f_cod_veiculo
          else true end
  and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date between f_data_inicial and f_data_final
order by a.data_hora desc
limit f_limit offset f_offset;
$$;


-- Function usada no v2.
create function func_afericao_get_afericoes_placas_paginada(f_cod_unidades bigint[], f_cod_tipo_veiculo bigint,
                                                            f_placa_veiculo text, f_data_inicial date,
                                                            f_data_final date, f_limit bigint, f_offset bigint)
    returns TABLE
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
    -- Estes par??metros (-1 e '') foram necess??rios por conta da convers??o de null com o Spring data JPA,
    -- No qual converte null para bytea e mesmo com cast, n??o consegue converter para o tipo desejado.
          when f_cod_tipo_veiculo != -1 and f_cod_tipo_veiculo is not null
              then v.cod_tipo = f_cod_tipo_veiculo
          else true end
  and case
          when f_placa_veiculo != '' and f_placa_veiculo is not null
              then v.placa = f_placa_veiculo
          else true end
  and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date between f_data_inicial and f_data_final
order by a.data_hora desc
limit f_limit offset f_offset;
$$;