create function func_relatorio_acessos_produtividade_estratificado(f_cod_unidade bigint,
                                                                   f_data_inicial date,
                                                                   f_data_final date,
                                                                   f_cpf text)
    returns table
            (
                "NOME"               text,
                "CARGO"              text,
                "EQUIPE"             text,
                "DATA DO ACESSO"     text,
                "PERÍODO CONSULTADO" text
            )
    language sql
as
$$
select c.nome,
       f.nome,
       e.nome,
       to_char(ap.data_hora_consulta, 'DD/MM/YYYY HH24:MI'),
       ap.mes_ano_consultado
from acessos_produtividade ap
         join colaborador c on c.cpf = ap.cpf_colaborador
         join equipe e on e.codigo = c.cod_equipe and e.cod_unidade = c.cod_unidade
         join funcao f on f.codigo = c.cod_funcao and f.cod_empresa = c.cod_empresa
where ap.cpf_colaborador :: text like f_cpf
  and ap.data_hora_consulta :: date between (f_data_inicial at time zone
                                             (select timezone from func_get_time_zone_unidade(f_cod_unidade)))
    and (f_data_final at time zone (select timezone from func_get_time_zone_unidade(f_cod_unidade)))
  and ap.cod_unidade = f_cod_unidade
order by ap.data_hora_consulta
$$;