-- Sobre:
--
-- Esta function foi criada para a integração de aferições. foi desenhada para ser genérica e funcionar com qualquer
-- empresa que queira utilizar a integração de aferição de pneus do prolog.
--
-- A function retorna a configuração de aferição para cada unidade requisitada. limitamos o retorno a apenas
-- configurações de tipos de veículos que possuam 'cod_auxiliar' mapeado para o tipo de veículo. fazemos isso pois a
-- utilização da configuração baseia-se no 'cod_auxiliar' não fazendo sentido retornar nada caso não tiver códigos
-- mapeados.
--
-- Histórico:
-- 2020-03-24 -> Function criada (diogenesvanzella - PL-2563).
-- 2020-04-07 -> Adiciona limit 1 para a busca do código da empresa (diogenesvanzella - PLI-119).
-- 2020-05-12 -> Substitui retorno de booleans "pode aferir" pelas novas colunas "forma coleta" (gustavocnp95 - PL-2689)
-- 2020-06-04 -> Retorna valores padrões caso não possuir configuração (diogenesvanzella - PLI-149).
-- 2020-06-15 -> Adiciona o cod_auxiliar da unidade no retorno (diogenesvanzella - PLI-165).
create or replace function integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar_unidade             text,
                cod_auxiliar_tipo_veiculo        text,
                cod_unidade                      bigint,
                cod_tipo_veiculo                 bigint,
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
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco)                       as forma_coleta_dados_sulco,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_pressao)                     as forma_coleta_dados_pressao,
               f_if(actav.codigo is null, 'EQUIPAMENTO',
                    actav.forma_coleta_dados_sulco_pressao)               as forma_coleta_dados_sulco_pressao,
               f_if(actav.codigo is null, true, actav.pode_aferir_estepe) as pode_aferir_estepe
        from cod_auxiliares_and_unidade caau
                 join unidade u on u.codigo = caau.cod_unidade
                 left join afericao_configuracao_tipo_afericao_veiculo actav
                           on actav.cod_tipo_veiculo = caau.cod_tipo_veiculo and actav.cod_unidade = caau.cod_unidade
        where caau.cod_unidade = any (f_cod_unidades)
          and caau.cod_auxiliar is not null
        order by caau.cod_auxiliar;
end;
$$;