-- Sobre:
--
-- Function utilizada para buscar as aferições realizadas de uma empresa. A function utiliza um parâmetro como offset
-- de busca, assim, só serão retornadas as aferições cujo código for maior que o offset utilizado.
-- A function retorna os dados com base no token informado. Se mais de uma empresa utiliza o mesmo token, serão
-- retornadas as aferições de todas as empresas que possuem o mesmo token.
--
-- Histórico:
-- 2018-12-21 -> Function criada (diogenesvanzella - PL-1554).
-- 2020-05-19 -> Atualiza function para lidar com mais de uma empresa (diogenesvanzella - PLI-158).
create or replace function
    integracao.func_integracao_busca_afericoes_empresa(f_token_integracao text,
                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                       bigint,
                cod_unidade_afericao               bigint,
                cpf_colaborador                    text,
                placa_veiculo_aferido              varchar(255),
                cod_pneu_aferido                   bigint,
                numero_fogo                        varchar(255),
                altura_sulco_interno               numeric,
                altura_sulco_central_interno       numeric,
                altura_sulco_central_externo       numeric,
                altura_sulco_externo               numeric,
                pressao                            numeric,
                km_veiculo_momento_afericao        bigint,
                tempo_realizacao_afericao_em_milis bigint,
                vida_momento_afericao              integer,
                posicao_pneu_momento_afericao      integer,
                data_hora_afericao                 timestamp without time zone,
                tipo_medicao_coletada              varchar(13),
                tipo_processo_coleta               varchar(11)
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor :: text, 11, '0')              as cpf_colaborador,
       a.placa_veiculo                                    as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente                                   as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 1)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 1) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 1) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 1)         as altura_sulco_externo,
       trunc(av.psi::numeric, 1)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       a.tempo_realizacao                                 as tempo_realizacao_afericao_em_milis,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada                            as tipo_medicao_coletada,
       a.tipo_processo_coleta                             as tipo_processo_coleta
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