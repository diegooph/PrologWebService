-- PS-1367.
create table if not exists socorro_rota_deletado
(
    codigo                                bigserial                not null,
    data_hora_delecao                     timestamp with time zone not null,
    pg_username_delecao                   text                     not null,
    motivo_delecao                        text                     not null,
    socorro_rota                          jsonb                    not null,
    socorro_rota_abertura                 jsonb                    not null,
    socorro_rota_atendimento              jsonb,
    socorro_rota_atendimento_deslocamento jsonb,
    socorro_rota_finalizacao              jsonb,
    socorro_rota_invalidacao              jsonb,
    constraint pk_socorro_rota_deletado primary key (codigo)
);

create or replace function suporte.func_socorro_rota_deleta(f_cod_unidade_socorro_rota bigint,
                                                            f_cod_socorro_rota bigint,
                                                            f_motivo_delecao text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_motivo_delecao);

    if not exists(select sr.codigo
                  from socorro_rota sr
                  where sr.codigo = f_cod_socorro_rota
                    and sr.cod_unidade = f_cod_unidade_socorro_rota)
    then
        raise exception 'O socorro em rota informado não existe.';
    end if;

    if f_motivo_delecao is null or trim(f_motivo_delecao) = ''
    then
        raise exception 'Você precisa fornecer um motivo de deleção, como o número do ticket.';
    end if;

    -- Salva os dados do socorro sendo deletado.
    insert into socorro_rota_deletado (data_hora_delecao,
                                       pg_username_delecao,
                                       motivo_delecao,
                                       socorro_rota,
                                       socorro_rota_abertura,
                                       socorro_rota_atendimento,
                                       socorro_rota_atendimento_deslocamento,
                                       socorro_rota_finalizacao,
                                       socorro_rota_invalidacao)
    values (now(),
            session_user,
            f_motivo_delecao,
               -- socorro_rota.
            (select row_to_json(sr.*)
             from socorro_rota sr
             where sr.codigo = f_cod_socorro_rota
               and sr.cod_unidade = f_cod_unidade_socorro_rota),
               -- socorro_rota_abertura.
            (select row_to_json(sra.*)
             from socorro_rota_abertura sra
             where sra.cod_socorro_rota = f_cod_socorro_rota),
               -- socorro_rota_atendimento.
            (select row_to_json(srat.*)
             from socorro_rota_atendimento srat
             where srat.cod_socorro_rota = f_cod_socorro_rota),
               -- socorro_rota_atendimento_deslocamento.
            (select row_to_json(srad.*)
             from socorro_rota_atendimento_deslocamento srad
             where srad.cod_socorro_rota_atendimento in
                   (select srat.codigo
                    from socorro_rota_atendimento srat
                    where srat.cod_socorro_rota = f_cod_socorro_rota)),
               -- socorro_rota_finalizacao.
            (select row_to_json(srf.*)
             from socorro_rota_finalizacao srf
             where srf.cod_socorro_rota = f_cod_socorro_rota),
               -- socorro_rota_invalidacao.
            (select row_to_json(sri.*)
             from socorro_rota_invalidacao sri
             where sri.cod_socorro_rota = f_cod_socorro_rota));

    -- Dropa constraints que garantem a relação entre status e registros de ações de socorro.
    alter table socorro_rota
        drop constraint check_socorro_rota_status_abertura;
    alter table socorro_rota
        drop constraint check_socorro_rota_status_atendimento;
    alter table socorro_rota
        drop constraint check_socorro_rota_status_invalidacao;
    alter table socorro_rota
        drop constraint check_socorro_rota_status_finalizacao;

    set constraints socorro_rota_socorro_rota_abertura_fk deferred;
    set constraints socorro_rota_socorro_rota_atendimento_fk deferred;
    set constraints socorro_rota_socorro_rota_invalidacao_fk deferred;
    set constraints socorro_rota_socorro_rota_finalizacao_fk deferred;

    delete
    from socorro_rota_finalizacao srf
    where srf.cod_socorro_rota = f_cod_socorro_rota;

    delete
    from socorro_rota_invalidacao sri
    where sri.cod_socorro_rota = f_cod_socorro_rota;

    delete
    from socorro_rota_atendimento_deslocamento srad
    where srad.cod_socorro_rota_atendimento in
          (select srat.codigo from socorro_rota_atendimento srat where srat.cod_socorro_rota = f_cod_socorro_rota);

    delete
    from socorro_rota_atendimento srat
    where srat.cod_socorro_rota = f_cod_socorro_rota;

    delete
    from socorro_rota_abertura sra
    where sra.cod_socorro_rota = f_cod_socorro_rota;

    delete
    from socorro_rota sr
    where sr.codigo = f_cod_socorro_rota
      and sr.cod_unidade = f_cod_unidade_socorro_rota;

    -- Recria constraints de status e registros.
    alter table socorro_rota
        add constraint check_socorro_rota_status_abertura
            check (status_atual <> 'ABERTO' or
                   (status_atual = 'ABERTO' and cod_abertura is not null and cod_atendimento is null and
                    cod_invalidacao is null and cod_finalizacao is null));
    alter table socorro_rota
        add constraint check_socorro_rota_status_atendimento
            check (status_atual <> 'EM_ATENDIMENTO' or
                   (status_atual = 'EM_ATENDIMENTO' and cod_abertura is not null and cod_atendimento is not null and
                    cod_invalidacao is null and cod_finalizacao is null));
    alter table socorro_rota
        add constraint check_socorro_rota_status_invalidacao
            check (status_atual <> 'INVALIDO' or
                   (status_atual = 'INVALIDO' and cod_abertura is not null and cod_invalidacao is not null and
                    cod_finalizacao is null));
    alter table socorro_rota
        add constraint check_socorro_rota_status_finalizacao
            check (status_atual <> 'FINALIZADO' or
                   (status_atual = 'FINALIZADO' and cod_abertura is not null and cod_atendimento is not null and
                    cod_invalidacao is null and cod_finalizacao is not null));

    return format('Socorro em rota de código %s e unidade %s deletado com sucesso!',
                  f_cod_socorro_rota,
                  f_cod_unidade_socorro_rota);
end;
$$;