-- Sobre:
--
-- Function utilizada para buscar os colaboradores de uma unidade que possuem acesso à uma função específica do Prolog.
--
-- Histórico:
-- 2020-07-08 -> Function criada (diogenesvanzella - PL-2671).
create or replace function func_colaborador_get_colaboradores_acesso_funcao_prolog(f_cod_unidade bigint,
                                                                                   f_cod_funcao_prolog bigint)
    returns table
            (
                cpf              bigint,
                nome_colaborador text,
                data_nascimento  date,
                nome_cargo       text,
                codigo_cargo     bigint
            )
    language sql
as
$$
select c.cpf             as cpf,
       c.nome::text      as nome_colaborador,
       c.data_nascimento as data_nascimento,
       f.nome            as nome_cargo,
       f.codigo          as codigo_cargo
from colaborador c
         join cargo_funcao_prolog_v11 cfp
              on c.cod_unidade = cfp.cod_unidade and c.cod_funcao = cfp.cod_funcao_colaborador
         join funcao f
              on f.codigo = c.cod_funcao and f.codigo = cfp.cod_funcao_colaborador and c.cod_empresa = f.cod_empresa
where c.cod_unidade = f_cod_unidade
  and cfp.cod_funcao_prolog = f_cod_funcao_prolog
  and c.status_ativo = true
order by codigo_cargo;
$$;