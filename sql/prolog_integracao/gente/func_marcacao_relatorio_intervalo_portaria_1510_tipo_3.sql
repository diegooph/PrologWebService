-- Sobre:
--
-- Function utilizada na integração para disponibilizar ao usuário as informações de marcações de intervalo no
-- padrão da portaria 1510.
--
-- Histórico:
-- 2019-11-06 -> Function criada (diogenesvanzella - PLI-45).
-- 2020-07-30 -> Retira marcações inativas da exibição (wvinim - PL-2832).
-- 2020-10-15 -> Otimiza query (luizfp - PL-3199).
create or replace function integracao.func_marcacao_relatorio_intervalo_portaria_1510_tipo_3(f_token_integracao text,
                                                                                             f_data_inicial date,
                                                                                             f_data_final date,
                                                                                             f_cod_unidade bigint,
                                                                                             f_cod_tipo_intervalo bigint,
                                                                                             f_cpf_colaborador bigint)
    returns table
            (
                nsr              text,
                tipo_registro    text,
                data_marcacao    text,
                horario_marcacao text,
                pis_colaborador  text
            )
    language sql
as
$$
with cte as (
    select lpad(row_number() over (partition by i.cod_unidade order by i.codigo)::text, 9, '0') as nsr,
           i.data_hora                                                                          as data_hora,
           i.cod_unidade                                                                        as cod_unidade,
           i.cod_tipo_intervalo                                                                 as cod_tipo_intervalo,
           i.cpf_colaborador                                                                    as cpf_colaborador,
           i.status_ativo                                                                       as status_ativo
    from intervalo i
    where i.cod_unidade in (select u.codigo
                            from integracao.token_integracao ti
                                     join unidade u on u.cod_empresa = ti.cod_empresa
                            where ti.token_integracao = f_token_integracao)
)
select i.nsr                                                                   as nsr,
       '3'::text                                                               as tipo_registro,
       to_char(i.data_hora at time zone tz_unidade(i.cod_unidade), 'DDMMYYYY') as data_marcacao,
       to_char(i.data_hora at time zone tz_unidade(i.cod_unidade), 'HH24MI')   as horario_marcacao,
       lpad(c.pis::text, 12, '0')                                              as pis_colaborador
from cte i
         join colaborador c on i.cpf_colaborador = c.cpf
     -- Aplicamos um filtro inicial e mais abrangente para que o index de unique da tabela seja utilizado.
     -- Com o '- 1 day' e '+ 1 day' todas as possíveis variações de timezone são abrangidas. É como se fosse um pré-filtro
     -- para depois filtrarmos novamente considerando timezone.
where i.data_hora >= (f_data_inicial::date - interval '1 day')
  and i.data_hora <= (f_data_final::date + interval '1 day')
  and (i.data_hora at time zone tz_unidade(i.cod_unidade))::date >= f_data_inicial
  and (i.data_hora at time zone tz_unidade(i.cod_unidade))::date <= f_data_final
  and c.pis is not null
  and case when f_cod_unidade is null then true else i.cod_unidade = f_cod_unidade end
  and case when f_cod_tipo_intervalo is null then true else i.cod_tipo_intervalo = f_cod_tipo_intervalo end
  and case when f_cpf_colaborador is null then true else i.cpf_colaborador = f_cpf_colaborador end
  and i.status_ativo;
$$;