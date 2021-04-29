create or replace function
    integracao.func_pneu_afericao_get_config_nova_afericao_placa(f_cod_unidade bigint,
                                                                 f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_inspecao                    real,
                tolerancia_calibragem                  real,
                periodo_afericao_sulco                 integer,
                periodo_afericao_pressao               integer,
                forma_coleta_dados_sulco               text,
                forma_coleta_dados_pressao             text,
                forma_coleta_dados_sulco_pressao       text,
                pode_aferir_estepe                     boolean,
                variacao_aceita_sulco_menor_milimetros double precision,
                variacao_aceita_sulco_maior_milimetros double precision,
                bloquear_valores_menores               boolean,
                bloquear_valores_maiores               boolean,
                variacoes_sulco_default_prolog         boolean
            )
    language sql
as
$$
with cod_auxiliares as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_empresa = (select u.cod_empresa from unidade u where u.codigo = f_cod_unidade)
)

select pru.sulco_minimo_descarte                                  as sulco_minimo_descarte,
       pru.sulco_minimo_recapagem                                 as sulco_minimo_recapagem,
       pru.tolerancia_inspecao                                    as tolerancia_inspecao,
       pru.tolerancia_calibragem                                  as tolerancia_calibragem,
       pru.periodo_afericao_sulco                                 as periodo_afericao_sulco,
       pru.periodo_afericao_pressao                               as periodo_afericao_pressao,
       config_pode_aferir.forma_coleta_dados_sulco                as pode_aferir_sulco,
       config_pode_aferir.forma_coleta_dados_pressao              as pode_aferir_pressao,
       config_pode_aferir.forma_coleta_dados_sulco_pressao        as pode_aferir_sulco_pressao,
       config_pode_aferir.pode_aferir_estepe                      as pode_aferir_estepe,
       config_alerta_sulco.variacao_aceita_sulco_menor_milimetros as variacao_aceita_sulco_menor_milimetros,
       config_alerta_sulco.variacao_aceita_sulco_maior_milimetros as variacao_aceita_sulco_maior_milimetros,
       config_alerta_sulco.bloquear_valores_menores               as bloquear_valores_menores,
       config_alerta_sulco.bloquear_valores_maiores               as bloquear_valores_maiores,
       config_alerta_sulco.usa_default_prolog                     as variacoes_sulco_default_prolog
from func_afericao_get_config_tipo_afericao_veiculo(f_cod_unidade) as config_pode_aferir
         join view_afericao_configuracao_alerta_sulco as config_alerta_sulco
              on config_pode_aferir.cod_unidade_configuracao = config_alerta_sulco.cod_unidade
         join pneu_restricao_unidade pru
              on pru.cod_unidade = config_pode_aferir.cod_unidade_configuracao
         join cod_auxiliares ca on ca.cod_auxiliar = f_cod_auxiliar_tipo_veiculo
where config_pode_aferir.cod_unidade_configuracao = f_cod_unidade
  and config_pode_aferir.cod_tipo_veiculo = ca.cod_tipo_veiculo
  and pru.cod_unidade = f_cod_unidade;
$$;