create or replace function migration_checklist.func_migration_2_migra_estrutura_respostas_nok()
    returns void
    language plpgsql
as
$$
begin
    -- CHECKLIST_RESPOSTAS migração
    -- Cria tabela que conterá as respostas NOK.
    create table checklist_respostas_nok
    (
        codigo                      bigserial not null,
        cod_unidade                 bigint    not null,
        cod_checklist_modelo        bigint    not null,
        cod_versao_checklist_modelo bigint    not null,
        cod_checklist               bigint    not null,
        cod_pergunta                bigint    not null,
        cod_alternativa             bigint    not null,
        resposta_outros             text,
        constraint pk_checklist_respostas_nok primary key (codigo),
        constraint fk_checklist_respostas_unidade foreign key (cod_unidade) references unidade (codigo),
        constraint fk_checklist_respostas_checklist_modelo_versao
            foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
                references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo),
        constraint fk_checklist_respostas_checklist foreign key (cod_checklist) references checklist_data (codigo),
        constraint fk_checklist_respostas_checklist_perguntas foreign key (cod_pergunta) references checklist_perguntas_data (codigo),
        constraint fk_checklist_respostas_checklist_alternativa foreign key (cod_alternativa) references checklist_alternativa_pergunta_data (codigo),
        constraint unica_resposta_alternativa_por_check unique (cod_checklist, cod_alternativa)
    );

    comment on table checklist_respostas_nok is 'Tabela que salva apenas as respostas NOK de um checklist realizado.';
    comment on column checklist_respostas_nok.resposta_outros is 'Se a alternativa selecionada for do tipo outros e o
    usuário a tiver selecionado na realização do checklist, essa coluna irá conter a descrição que ele forneceu do problema.
    Caso contrário será NULL.';


    -- Insere na tabela criada apenas as respostas NOK dos checklists já realizados.
    with respostas_nok as (
        select cr.cod_unidade,
               cr.cod_checklist_modelo,
               cr.cod_checklist,
               cr.cod_pergunta,
               cr.cod_alternativa,
               f_if(cr.resposta <> 'NOK', cr.resposta, null) as resposta_outros
        from checklist_respostas cr
        where cr.resposta <> 'OK'
    ),
         checks_com_nok as (
             select rn.cod_unidade,
                    rn.cod_checklist_modelo,
                    cd.cod_versao_checklist_modelo,
                    rn.cod_checklist,
                    cpa.cod_pergunta_novo as cod_pergunta,
                    caa.cod_alternativa_novo as cod_alternativa,
                    rn.resposta_outros
             from respostas_nok rn
                      join checklist_data cd on rn.cod_checklist = cd.codigo
                      join migration_checklist.check_perguntas_aux cpa
                          on cpa.cod_pergunta_antigo = rn.cod_pergunta
                                 and cpa.cod_modelo_versao = cd.cod_versao_checklist_modelo
                      join migration_checklist.check_alternativas_aux caa
                          on caa.cod_alternativa_antigo = rn.cod_alternativa
                                 and caa.cod_modelo_versao = cd.cod_versao_checklist_modelo
         )

    insert
    into checklist_respostas_nok
    (cod_unidade,
     cod_checklist_modelo,
     cod_versao_checklist_modelo,
     cod_checklist,
     cod_pergunta,
     cod_alternativa,
     resposta_outros)
    select ccn.cod_unidade,
           ccn.cod_checklist_modelo,
           ccn.cod_versao_checklist_modelo,
           ccn.cod_checklist,
           ccn.cod_pergunta,
           ccn.cod_alternativa,
           ccn.resposta_outros
    from checks_com_nok ccn;
end;
$$;