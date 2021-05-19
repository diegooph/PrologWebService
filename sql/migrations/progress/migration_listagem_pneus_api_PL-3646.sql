begin transaction;
alter table pneu_posicao_nomenclatura_empresa
    add constraint pk_diagrama_empresa_posicao_prolog
        primary key (cod_empresa, cod_diagrama, posicao_prolog);
end transaction;