alter table relato
    add column cod_colaborador bigint;
update relato
set cod_colaborador = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_colaborador;
alter table relato
    alter column cod_colaborador set not null;

alter table relato
    add column cod_colaborador_classificacao bigint;
update relato
set cod_colaborador_classificacao = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_classificacao;

alter table relato
    add column cod_colaborador_fechamento bigint;
update relato
set cod_colaborador_fechamento = cd.codigo
from colaborador_data cd
where cd.cpf = relato.cpf_fechamento;

alter table relato drop column cpf_colaborador, drop column cpf_classificacao, drop column cpf_fechamento;
