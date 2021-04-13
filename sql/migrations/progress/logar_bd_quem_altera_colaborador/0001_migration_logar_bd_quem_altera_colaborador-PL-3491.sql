alter table colaborador_data
    add column cod_colaborador_cadastro bigint;

alter table colaborador_data
    add column cod_colaborador_ultima_atualizacao bigint;

create or replace view colaborador as
select
    c.cpf,
    c.matricula_ambev,
    c.matricula_trans,
    c.data_nascimento,
    c.data_admissao,
    c.data_demissao,
    c.status_ativo,
    c.nome,
    c.cod_equipe,
    c.cod_funcao,
    c.cod_unidade,
    c.cod_permissao,
    c.cod_empresa,
    c.cod_setor,
    c.pis,
    c.data_hora_cadastro,
    c.codigo,
    c.cod_unidade_cadastro,
    c.cod_colaborador_cadastro,
    c.cod_colaborador_ultima_atualizacao
from colaborador_data c
where c.deletado = false;