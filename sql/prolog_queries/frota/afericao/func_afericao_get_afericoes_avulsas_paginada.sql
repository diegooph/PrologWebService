create or replace function func_afericao_get_afericoes_avulsas_paginada(f_cod_unidades bigint[],
                                                                        f_data_inicial date,
                                                                        f_data_final date,
                                                                        f_limit bigint,
                                                                        f_offset bigint,
                                                                        f_incluir_medidas boolean default false)
    returns table
            (
                cod_afericao                 bigint,
                cod_unidade                  bigint,
                data_hora                    timestamp without time zone,
                tipo_medicao_coletada        text,
                tipo_processo_coleta         text,
                forma_coleta_dados           text,
                cpf                          text,
                nome                         text,
                tempo_realizacao             bigint,
                cod_pneu                     bigint,
                posicao                      integer,
                psi                          real,
                vida_momento_afericao        integer,
                altura_sulco_interno         real,
                altura_sulco_central_interno real,
                altura_sulco_central_externo real,
                altura_sulco_externo         real
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta,
       a.forma_coleta_dados::text                         as forma_coleta_dados,
       c.cpf::text                                        as cpf,
       c.nome::text                                       as nome,
       a.tempo_realizacao                                 as tempo_realizacao,
       av.cod_pneu                                        as cod_pneu,
       av.posicao                                         as posicao,
       av.psi                                             as psi,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.altura_sulco_interno                            as altura_sulco_interno,
       av.altura_sulco_central_interno                    as altura_sulco_central_interno,
       av.altura_sulco_central_externo                    as altura_sulco_central_externo,
       av.altura_sulco_externo                            as altura_sulco_externo
from afericao a
         join colaborador c on c.cpf = a.cpf_aferidor
         left join afericao_valores av on f_incluir_medidas and av.cod_afericao = a.codigo
where a.cod_unidade = any (f_cod_unidades)
  and a.tipo_processo_coleta = 'PNEU_AVULSO'
  and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date between f_data_inicial and f_data_final
order by a.data_hora desc
limit f_limit offset f_offset;
$$;