alter table treinamento_colaborador
    add column cod_colaborador bigint;

update treinamento_colaborador tr
set cod_colaborador = (select cd.codigo
                       from colaborador_data cd
                       where cd.cpf = tr.cpf_colaborador);

create or replace function func_relatorio_treinamento_visualizados_por_colaborador(f_data_inicial date,
                                                                                   f_data_final date,
                                                                                   f_time_zone_datas text,
                                                                                   f_cod_unidade bigint)
    returns TABLE
            (
                "CPF COLABORADOR"        text,
                "NOME COLABORADOR"       text,
                "CARGO COLABORADOR"      text,
                "TÍTULO TREINAMENTO"     text,
                "DESCRIÇÃO TREINAMENTO"  text,
                "DATA/HORA VISUALIZAÇÃO" text
            )
    language sql
as
$$
select lpad(c.cpf :: text, 11, '0')                                                       as cpf,
       c.nome                                                                             as colaborador,
       f.nome                                                                             as cargo_colaborador,
       t.titulo                                                                           as titulo_treinamento,
       t.descricao                                                                        as descricao_treinamento,
       to_char(tc.data_visualizacao at time zone f_time_zone_datas, 'DD/MM/YYYY HH24:MI') as data_hora_visualizacao
from treinamento_colaborador tc
         join treinamento t on tc.cod_treinamento = t.codigo
         join colaborador c on tc.cod_colaborador = c.codigo
         join funcao f on c.cod_funcao = f.codigo
where t.cod_unidade = f_cod_unidade
  and (tc.data_visualizacao at time zone f_time_zone_datas):: date >= f_data_inicial
  and (tc.data_visualizacao at time zone f_time_zone_datas):: date <= f_data_final
  and c.status_ativo
order by c.nome;
$$;

alter table treinamento_colaborador
    drop column cpf_colaborador;