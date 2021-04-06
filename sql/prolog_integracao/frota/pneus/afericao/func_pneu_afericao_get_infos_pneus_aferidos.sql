create or replace function integracao.func_pneu_afericao_get_infos_pneus_aferidos(f_cod_empresa bigint,
                                                                                  f_cod_pneus_cliente text[])
    returns table
            (
                cod_pneu_cliente              text,
                codigo_ultima_afericao        bigint,
                data_hora_ultima_afericao     timestamp without time zone,
                nome_colaborador_afericao     text,
                tipo_medicao_coletada         text,
                tipo_processo_coleta          text,
                placa_aplicado_quando_aferido text
            )
    language plpgsql
as
$$
begin
    return query
        with ultimas_afericoes_pneu as (
            select distinct max(avi.codigo) over (partition by avi.cod_pneu_cliente) as codigo_ultima_afericao,
                            avi.cod_pneu                                             as cod_pneu,
                            avi.cod_pneu_cliente                                     as cod_pneu_cliente,
                            max(avi.cod_afericao_integrada)
                            over (partition by avi.cod_pneu_cliente)                 as cod_afericao_integrada
            from integracao.afericao_valores_integrada avi
                     join integracao.afericao_integrada ai on avi.cod_afericao_integrada = ai.codigo
            where avi.cod_pneu_cliente = any (f_cod_pneus_cliente)
        )
        select uap.cod_pneu_cliente::text                                  as cod_pneu_cliente,
               ai.codigo                                                   as codigo_ultima_afericao,
               ai.data_hora at time zone tz_unidade(ai.cod_unidade_prolog) as data_hora_ultima_afericao,
               coalesce(cd.nome, 'Nome Indisponível')::text                as nome_colaborador_afericao,
               ai.tipo_medicao_coletada                                    as tipo_medicao_coletada,
               ai.tipo_processo_coleta                                     as tipo_processo_coleta,
               ai.placa_veiculo                                            as placa_aplicado_quando_aferido
        from ultimas_afericoes_pneu uap
                 left join integracao.afericao_integrada ai on uap.cod_afericao_integrada = ai.codigo
                 left join colaborador_data cd on cd.cpf = ai.cpf_aferidor::bigint
            and ai.cod_empresa_prolog = f_cod_empresa;
end;
$$;