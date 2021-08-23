create or replace function func_afericao_get_pneu_para_afericao_avulsa(f_cod_pneu bigint, f_tz_unidade text)
    returns table
            (
                codigo                                bigint,
                codigo_cliente                        text,
                dot                                   text,
                valor                                 real,
                cod_unidade_alocado                   bigint,
                cod_regional_alocado                  bigint,
                pneu_novo_nunca_rodado                boolean,
                cod_marca_pneu                        bigint,
                nome_marca_pneu                       text,
                cod_modelo_pneu                       bigint,
                nome_modelo_pneu                      text,
                qt_sulcos_modelo_pneu                 smallint,
                cod_marca_banda                       bigint,
                nome_marca_banda                      text,
                altura_sulcos_modelo_pneu             real,
                cod_modelo_banda                      bigint,
                nome_modelo_banda                     text,
                qt_sulcos_modelo_banda                smallint,
                altura_sulcos_modelo_banda            real,
                valor_banda                           real,
                altura                                numeric,
                largura                               numeric,
                aro                                   numeric,
                cod_dimensao                          bigint,
                altura_sulco_central_interno          real,
                altura_sulco_central_externo          real,
                altura_sulco_interno                  real,
                altura_sulco_externo                  real,
                pressao_recomendada                   real,
                pressao_atual                         real,
                status                                text,
                vida_atual                            integer,
                vida_total                            integer,
                posicao_pneu                          integer,
                posicao_aplicado_cliente              text,
                cod_veiculo_aplicado                  bigint,
                placa_aplicado                        text,
                identificador_frota                   text,
                ja_foi_aferido                        boolean,
                cod_ultima_afericao                   bigint,
                data_hora_ultima_afericao             timestamp without time zone,
                placa_veiculo_ultima_afericao         text,
                identificador_frota_ultima_afericao   text,
                tipo_medicao_coletada_ultima_afericao text,
                tipo_processo_coleta_ultima_afericao  text,
                nome_colaborador_ultima_afericao      text
            )
    language sql
as
$$
with afericoes as (
    select inner_table.codigo           as cod_afericao,
           inner_table.cod_pneu         as cod_pneu,
           inner_table.data_hora,
           inner_table.cod_veiculo,
           inner_table.tipo_medicao_coletada,
           inner_table.tipo_processo_coleta,
           inner_table.nome_colaborador as nome_colaborador,
           case
               when inner_table.nome_colaborador is not null
                   then true
               else false end           as ja_foi_aferido
    from (select a.codigo,
                 av.cod_pneu,
                 a.data_hora,
                 a.cod_veiculo,
                 a.tipo_medicao_coletada,
                 a.tipo_processo_coleta,
                 c.nome                      as nome_colaborador,
                 MAX(a.codigo)
                 over (
                     partition by cod_pneu ) as max_cod_afericao
          from pneu p
                   left join afericao_valores av on p.codigo = av.cod_pneu
                   left join afericao a on av.cod_afericao = a.codigo
                   left join colaborador c on a.cpf_aferidor = c.cpf
          where p.status = 'ESTOQUE'
            and p.codigo = f_cod_pneu) as inner_table
    where codigo = inner_table.max_cod_afericao
)

select func.*,
       a.ja_foi_aferido                      as ja_foi_aferido,
       a.cod_afericao                        as cod_ultima_afericao,
       a.data_hora at time zone f_tz_unidade as data_hora_ultima_afericao,
       v.placa :: text                       as placa_veiculo_ultima_afericao,
       v.identificador_frota                 as identificador_frota_ultima_afericao,
       a.tipo_medicao_coletada :: text       as tipo_medicao_coletada_ultima_afericao,
       a.tipo_processo_coleta :: text        as tipo_processo_coleta_ultima_afericao,
       a.nome_colaborador :: text            as nome_colaborador_ultima_afericao
from func_pneu_get_pneu_by_codigo(f_cod_pneu) as func
         left join afericoes a on func.codigo = a.cod_pneu
         left join veiculo v on v.codigo = a.cod_veiculo
where func.codigo = f_cod_pneu;
$$;