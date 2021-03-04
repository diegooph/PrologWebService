create or replace function f_millis_to_seconds(bigint) returns bigint
    language plpgsql
as
$$
begin
    -- Valor recebido em milissegundos é retornado em segundos.
    return (select $1 / 1000);
end;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_codigo(f_token_integracao text,
                                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       a.placa_veiculo::text                              as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.codigo > f_cod_ultima_afericao_sincronizada
order by a.codigo;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_data_hora(f_token_integracao text,
                                                                          f_data_hora timestamp without time zone)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       a.placa_veiculo::text                              as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.data_hora > f_data_hora
order by a.data_hora;
$$;