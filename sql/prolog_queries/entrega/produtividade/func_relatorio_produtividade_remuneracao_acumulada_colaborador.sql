-- Atenção! Caso a coluna "VALOR" retorne null, pode ser que alguma parametrização para a f_cod_unidade informada
-- esteja faltando na tabela 'unidade_valores_rm'.
create or replace function func_relatorio_produtividade_remuneracao_acumulada_colaborador(f_cod_unidade bigint,
                                                                               f_cpf_colaborador bigint,
                                                                               f_data_inicial date,
                                                                               f_data_final date)
    returns TABLE
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
SELECT VPE.CPF,
       VPE.NOME_COLABORADOR,
       VPE.DATA,
       ROUND(VPE.CXENTREG::NUMERIC, 2),
       VPE.FATOR,
       VPE.VALOR
FROM VIEW_PRODUTIVIDADE_EXTRATO AS VPE
WHERE VPE.COD_UNIDADE = f_cod_unidade
  AND CASE
        WHEN f_cpf_colaborador IS NULL THEN TRUE
        ELSE VPE.CPF = f_cpf_colaborador
      END
  AND VPE.DATA BETWEEN f_data_inicial AND f_data_final
ORDER BY VPE.CPF, VPE.DATA;
$$;

comment on function func_relatorio_produtividade_remuneracao_acumulada_colaborador(bigint, bigint, date, date)
    is 'Busca a produtividade do colaborador para um período';