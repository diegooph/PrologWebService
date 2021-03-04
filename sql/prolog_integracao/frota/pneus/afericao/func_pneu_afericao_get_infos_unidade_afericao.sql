-- Sobre:
--
-- Esta function foi criada para a integração de aferições. Foi desenhada para ser genérica e funcionar com qualquer
-- empresa que queira utilizar a integração de aferição de pneus do Prolog.
--
-- A function retorna a configuração de cada unidade utilizada para montar o cronograma de aferição. Limitamos o
-- retorno a apenas as unidades que tenham 'cod_auxiliar' mapeados.
--
-- Histórico:
-- 2020-03-24 -> Function criada (diogenesvanzella - PL-2563).
-- 2020-06-16 -> Retorna os vários cod_auxiliares em coluna (diogenesvanzella - PLI-166).
create or replace function integracao.func_pneu_afericao_get_infos_unidade_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar                  text,
                cod_unidade                   bigint,
                periodo_dias_afericao_sulco   integer,
                periodo_dias_afericao_pressao integer
            )
    language sql
as
$$
select regexp_split_to_table(u.cod_auxiliar, ',') as cod_auxiliar,
       pru.cod_unidade                            as cod_unidade,
       pru.periodo_afericao_sulco                 as periodo_dias_afericao_sulco,
       pru.periodo_afericao_pressao               as periodo_dias_afericao_pressao
from pneu_restricao_unidade pru
         join unidade u on pru.cod_unidade = u.codigo
where pru.cod_unidade = any (f_cod_unidades)
  and u.cod_auxiliar is not null;
$$;