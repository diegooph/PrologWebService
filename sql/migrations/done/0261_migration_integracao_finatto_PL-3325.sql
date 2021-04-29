create or replace function integracao.func_geral_unidade_get_infos_de_para(f_cod_unidades bigint[])
    returns table
            (
                cod_empresa_prolog   bigint,
                nome_empresa_prolog  text,
                cod_unidade_prolog   bigint,
                nome_unidade_prolog  text,
                cod_regional_prolog  bigint,
                nome_regional_prolog text,
                cod_auxiliar_unidade text
            )
    language plpgsql
as
$$
begin
    return query
        select u.cod_empresa  as cod_empresa_prolog,
               e.nome::text   as nome_empresa_prolog,
               u.codigo       as cod_unidade_prolog,
               u.nome::text   as nome_unidade_prolog,
               r.codigo       as cod_regional_prolog,
               r.regiao::text as nome_regional_prolog,
               u.cod_auxiliar as cod_auxiliar_unidade
        from unidade u
                 join regional r on u.cod_regional = r.codigo
                 join empresa e on u.cod_empresa = e.codigo
        where u.codigo = any (f_cod_unidades);
end
$$;

drop function if exists integracao.func_pneu_afericao_get_infos_configuracao_afericao(bigint[]);
create or replace function integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar_unidade             text,
                cod_auxiliar_tipo_veiculo        text,
                cod_unidade                      bigint,
                cod_tipo_veiculo                 bigint,
                nome_tipo_veiculo                text,
                cod_diagrama_veiculo             smallint,
                nome_diagrama_veiculo            text,
                qtd_eixo_dianteiro               bigint,
                qtd_eixo_traseiro                bigint,
                forma_coleta_dados_sulco         text,
                forma_coleta_dados_pressao       text,
                forma_coleta_dados_sulco_pressao text,
                pode_aferir_estepe               boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa bigint := (select u.cod_empresa
                             from public.unidade u
                             where u.codigo = any (f_cod_unidades)
                             limit 1);
begin
    return query
        with cod_auxiliares as (
            select vt.codigo                                   as cod_tipo_veiculo,
                   regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
            from veiculo_tipo vt
            where vt.cod_empresa = v_cod_empresa
        ),
             cod_auxiliares_and_unidade as (
                 select unnest(f_cod_unidades) as cod_unidade,
                        ca.cod_tipo_veiculo    as cod_tipo_veiculo,
                        ca.cod_auxiliar        as cod_auxiliar
                 from cod_auxiliares ca
             )
        select regexp_split_to_table(u.cod_auxiliar, ',')                 as cod_auxiliar_unidade,
               caau.cod_auxiliar                                          as cod_auxiliar_tipo_veiculo,
               caau.cod_unidade                                           as cod_unidade,
               caau.cod_tipo_veiculo                                      as cod_tipo_veiculo,
               vt.nome::text                                              as nome_tipo_veiculo,
               vd.codigo                                                  as cod_diagrama_veiculo,
               vd.nome::text                                              as nome_diagrama_veiculo,
               count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D')    as qtd_eixo_dianteiro,
               count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T')    as qtd_eixo_traseiro,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco)                       as forma_coleta_dados_sulco,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_pressao)                     as forma_coleta_dados_pressao,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco_pressao)               as forma_coleta_dados_sulco_pressao,
               f_if(actav.codigo is null, true, actav.pode_aferir_estepe) as pode_aferir_estepe
        from cod_auxiliares_and_unidade caau
                 join unidade u on u.codigo = caau.cod_unidade
                 join veiculo_tipo vt on caau.cod_tipo_veiculo = vt.codigo
                 join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                 join veiculo_diagrama_eixos vde on vd.codigo = vde.cod_diagrama
                 left join afericao_configuracao_tipo_afericao_veiculo actav
                           on actav.cod_tipo_veiculo = caau.cod_tipo_veiculo and actav.cod_unidade = caau.cod_unidade
        where caau.cod_unidade = any (f_cod_unidades)
          and caau.cod_auxiliar is not null
        group by u.cod_auxiliar, caau.cod_auxiliar, caau.cod_unidade, caau.cod_tipo_veiculo, vt.nome, vd.codigo,
                 vd.nome, actav.codigo
        order by caau.cod_auxiliar;
end;
$$;

drop function if exists integracao.func_pneu_afericao_get_infos_afericoes_integrada(f_cod_unidade bigint,
    f_cod_pneus_cliente text[]);
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