alter table unidade
    add column pais             text,
    add column estado_provincia text,
    add column cidade           text,
    add column cep              text,
    add column endereco         text;

alter table unidade
    add constraint fk_pais foreign key (pais) references prolog_paises (sigla_iso2);