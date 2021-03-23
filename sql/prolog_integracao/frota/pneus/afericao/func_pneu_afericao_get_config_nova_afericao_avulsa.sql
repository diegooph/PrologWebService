-- Sobre:
--
-- Esta function foi criada para a integração de aferições. Foi desenhada para ser genérica e funcionar com qualquer
-- empresa que queira utilizar a integração de aferição de pneus do Prolog.
--
-- Os dados retornados por essa function são as configurações que serão utilizadas para a realização de uma aferição
-- Avulsa.
--
-- Histórico:
-- 2020-03-24 -> Function criada (diogenesvanzella - PL-2563).
create or replace function
    integracao.func_pneu_afericao_get_config_nova_afericao_avulsa(f_cod_unidade bigint)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_inspecao                    real,
                tolerancia_calibragem                  real,
                periodo_afericao_sulco                 integer,
                periodo_afericao_pressao               integer,
                variacao_aceita_sulco_menor_milimetros double precision,
                variacao_aceita_sulco_maior_milimetros double precision,
                bloquear_valores_menores               boolean,
                bloquear_valores_maiores               boolean,
                variacoes_sulco_default_prolog         boolean
            )
    language sql
as
$$
select pru.sulco_minimo_descarte                                  as sulco_minimo_descarte,
       pru.sulco_minimo_recapagem                                 as sulco_minimo_recapagem,
       pru.tolerancia_inspecao                                    as tolerancia_inspecao,
       pru.tolerancia_calibragem                                  as tolerancia_calibragem,
       pru.periodo_afericao_sulco                                 as periodo_afericao_sulco,
       pru.periodo_afericao_pressao                               as periodo_afericao_pressao,
       config_alerta_sulco.variacao_aceita_sulco_menor_milimetros as variacao_aceita_sulco_menor_milimetros,
       config_alerta_sulco.variacao_aceita_sulco_maior_milimetros as variacao_aceita_sulco_maior_milimetros,
       config_alerta_sulco.bloquear_valores_menores               as bloquear_valores_menores,
       config_alerta_sulco.bloquear_valores_maiores               as bloquear_valores_maiores,
       config_alerta_sulco.usa_default_prolog                     as variacoes_sulco_default_prolog
from view_afericao_configuracao_alerta_sulco config_alerta_sulco
         join pneu_restricao_unidade pru
              on pru.cod_unidade = config_alerta_sulco.cod_unidade
where config_alerta_sulco.cod_unidade = f_cod_unidade;
$$;