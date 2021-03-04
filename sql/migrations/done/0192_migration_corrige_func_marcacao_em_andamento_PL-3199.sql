-- Corrige function.
-- Estava buscando qualquer marcação de início não finalizada ao invés da última marcação desde que fosse de início
-- e sem finalização.
create or replace function func_marcacao_busca_marcacao_em_andamento(f_cod_unidade bigint,
                                                                     f_cod_tipo_intervalo bigint,
                                                                     f_cpf_colaborador bigint)
    returns table
            (
                codigo                          bigint,
                cod_unidade                     bigint,
                cod_tipo_intervalo              bigint,
                cpf_colaborador                 bigint,
                data_nascimento_colaborador     date,
                data_hora                       timestamp without time zone,
                tipo_marcacao                   text,
                fonte_data_hora                 text,
                justificativa_tempo_recomendado text,
                justificativa_estouro           text,
                latitude_marcacao               text,
                longitude_marcacao              text,
                valido                          boolean,
                foi_editado                     boolean,
                cod_colaborador_insercao        bigint,
                status_ativo                    boolean,
                data_hora_sincronizacao         timestamp without time zone
            )
    language sql
as
$$
with intervalo_marcacao as (
    select i.codigo,
           i.cod_unidade,
           i.cod_tipo_intervalo,
           i.cpf_colaborador,
           c.data_nascimento                                                as data_nascimento_colaborador,
           i.data_hora at time zone tz_unidade(i.cod_unidade)               as data_hora,
           i.tipo_marcacao,
           i.fonte_data_hora,
           i.justificativa_tempo_recomendado,
           i.justificativa_estouro,
           i.latitude_marcacao,
           i.longitude_marcacao,
           i.valido,
           i.foi_ajustado,
           i.cod_colaborador_insercao,
           i.status_ativo,
           i.data_hora_sincronizacao at time zone tz_unidade(i.cod_unidade) as data_hora_sincronizacao
    from intervalo i
             join colaborador c
                  on i.cpf_colaborador = c.cpf
    where i.cod_unidade = f_cod_unidade
      and i.cod_tipo_intervalo = f_cod_tipo_intervalo
      and i.cpf_colaborador = f_cpf_colaborador
    order by i.codigo desc
    limit 1
)

-- Precisamos de dois selects pois primeiro buscamos a última marcação do tipo, colaborador e unidade informados.
-- Depois vericamos se essa última marcação é de início e ainda não tem fim.
select *
from intervalo_marcacao im
where not exists(select mvif.cod_marcacao_inicio
                 from marcacao_vinculo_inicio_fim mvif
                 where mvif.cod_marcacao_inicio = im.codigo)
  and im.tipo_marcacao = 'MARCACAO_INICIO'
  and im.valido = true
  and im.status_ativo = true;
$$;