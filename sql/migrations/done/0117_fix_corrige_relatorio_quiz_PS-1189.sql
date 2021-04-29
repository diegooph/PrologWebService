-- PS-1189

create or replace function func_quiz_relatorio_extrato_geral(f_cod_unidade bigint,
                                                             f_data_inicial date,
                                                             f_data_final date)
    returns table
            (
                "DATA DE REALIZAÇÃO" text,
                "QUIZ"               text,
                "NOME"               text,
                "CARGO"              text,
                "QTD CORRETAS"       integer,
                "QTD ERRADAS"        integer,
                "TOTAL PERGUNTAS"    integer,
                "NOTA 0 A 10"        numeric,
                "AVALIAÇÃO"          text
            )
    language plpgsql
as
$$
begin
    return query
        select format_with_tz(q.data_hora, tz_unidade(q.cod_unidade), 'DD/MM/YYYY HH24:MI')          as data_realizacao,
               qm.nome :: text                                                                       as quiz,
               initcap(c.nome)                                                                       as nome,
               f.nome :: text                                                                        as cargo,
               q.qt_corretas                                                                         as qtd_corretas,
               q.qt_erradas                                                                          as qtd_erradas,
               q.qt_corretas + q.qt_erradas                                                          as total_perguntas,
               trunc(((q.qt_corretas / (q.qt_corretas + q.qt_erradas) :: float) * 10) :: numeric, 2) as nota,
               case
                   when (q.qt_corretas / (q.qt_corretas + q.qt_erradas) :: float) >= qm.porcentagem_aprovacao
                       then
                       'APROVADO'
                   else 'REPROVADO' end                                                              as avaliacao
        from quiz q
                 join quiz_modelo qm on q.cod_modelo = qm.codigo and q.cod_unidade = qm.cod_unidade
                 join colaborador c on c.cpf = q.cpf_colaborador and c.cod_unidade = q.cod_unidade
                 join unidade u on u.codigo = c.cod_unidade and u.codigo = q.cod_unidade
                 join funcao f on f.codigo = c.cod_funcao and f.cod_empresa = u.cod_empresa
        where q.cod_unidade = f_cod_unidade
          and (q.data_hora at time zone tz_unidade(q.cod_unidade)) :: date between f_data_inicial and f_data_final
        order by q.data_hora desc;
end;
$$;