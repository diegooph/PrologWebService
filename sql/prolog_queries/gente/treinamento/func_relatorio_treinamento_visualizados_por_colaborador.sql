-- Sobre:
--
-- Function utilizada para gerar o relatório de treinamentos visualizados por colaborador.
--
-- Histórico:
-- 2020-08-12 -> Function criada (luizfp - PS-1192).
-- 2020-08-12 -> Adiciona cargo do colaborador e filtra por ativos (luizfp - PS-1192).
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
select lpad(tc.cpf_colaborador :: text, 11, '0')                                          as cpf,
       c.nome                                                                             as colaborador,
       f.nome                                                                             as cargo_colaborador,
       t.titulo                                                                           as titulo_treinamento,
       t.descricao                                                                        as descricao_treinamento,
       to_char(tc.data_visualizacao at time zone f_time_zone_datas, 'DD/MM/YYYY HH24:MI') as data_hora_visualizacao
from treinamento_colaborador tc
         join treinamento t on tc.cod_treinamento = t.codigo
         join colaborador c on tc.cpf_colaborador = c.cpf
         join funcao f on c.cod_funcao = f.codigo
where t.cod_unidade = f_cod_unidade
  and (tc.data_visualizacao at time zone f_time_zone_datas):: date >= f_data_inicial
  and (tc.data_visualizacao at time zone f_time_zone_datas):: date <= f_data_final
  and c.status_ativo
order by c.nome;
$$;