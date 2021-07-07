create or replace function func_afericao_get_configuracoes_nova_afericao_placa(f_cod_veiculo bigint)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_calibragem                  real,
                tolerancia_inspecao                    real,
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
    language plpgsql
as
$$
declare
    f_cod_unidade      bigint;
    f_cod_tipo_veiculo bigint;
begin
    select into f_cod_unidade, f_cod_tipo_veiculo v.cod_unidade,
                                                  v.cod_tipo
    from veiculo v
    where v.codigo = f_cod_veiculo;

    return query
        select pru.sulco_minimo_descarte,
               pru.sulco_minimo_recapagem,
               pru.tolerancia_inspecao,
               pru.tolerancia_calibragem,
               pru.periodo_afericao_sulco,
               pru.periodo_afericao_pressao,
               config_pode_aferir.forma_coleta_dados_sulco,
               config_pode_aferir.forma_coleta_dados_pressao,
               config_pode_aferir.forma_coleta_dados_sulco_pressao,
               config_pode_aferir.pode_aferir_estepe,
               config_alerta_sulco.variacao_aceita_sulco_menor_milimetros,
               config_alerta_sulco.variacao_aceita_sulco_maior_milimetros,
               config_alerta_sulco.bloquear_valores_menores,
               config_alerta_sulco.bloquear_valores_maiores,
               config_alerta_sulco.usa_default_prolog as variacoes_sulco_default_prolog
        from func_afericao_get_config_tipo_afericao_veiculo(f_cod_unidade) as config_pode_aferir
                 join view_afericao_configuracao_alerta_sulco as config_alerta_sulco
                      on config_pode_aferir.cod_unidade_configuracao = config_alerta_sulco.cod_unidade
                 join pneu_restricao_unidade pru
                      on pru.cod_unidade = config_pode_aferir.cod_unidade_configuracao
        where config_pode_aferir.cod_unidade_configuracao = f_cod_unidade
          and config_pode_aferir.cod_tipo_veiculo = f_cod_tipo_veiculo;
end;
$$;