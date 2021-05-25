-- Atenção! Caso a coluna "VALOR" retorne null, pode ser que alguma parametrização para a f_cod_unidade informada
-- esteja faltando na tabela 'unidade_valores_rm'.
create or replace function func_relatorio_produtividade_remuneracao_acumulada_colaborador(f_cod_unidade bigint,
                                                                                          f_cpf_colaborador bigint,
                                                                                          f_data_inicial date,
                                                                                          f_data_final date)
    returns table
            (
                "CPF_COLABORADOR"  bigint,
                "NOME_COLABORADOR" text,
                "DATA"             date,
                "CAIXAS_ENTREGUES" numeric,
                "FATOR"            real,
                "VALOR"            double precision
            )
    language sql
as
$$
select vpe.cpf,
       vpe.nome_colaborador,
       vpe.data,
       round(vpe.cxentreg::numeric, 2),
       vpe.fator,
       vpe.valor
from view_produtividade_extrato_com_total as vpe
where vpe.cod_unidade = f_cod_unidade
  and case
          when f_cpf_colaborador is null then true
          else vpe.cpf = f_cpf_colaborador
    end
  and vpe.data between f_data_inicial and f_data_final
order by vpe.cpf, vpe.data;
$$;

comment on function func_relatorio_produtividade_remuneracao_acumulada_colaborador(bigint, bigint, date, date)
    is 'Busca a produtividade do colaborador para um período.';