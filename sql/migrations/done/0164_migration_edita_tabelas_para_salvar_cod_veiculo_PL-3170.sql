-- Critério de aceitação: alterar todas as tabelas onde a placa é utilizada e adicionar o código do veículo.

-- Cria unique para placa e codigo do veículo.
alter table veiculo_data
    add constraint unique_placa_codigo unique (placa, codigo);

-- AFERICAO_DATA.
-- Adiciona coluna cod_veiculo.
alter table afericao_data
    add column cod_veiculo bigint;

-- Preenche coluna com os códigos dos veiculos.
update afericao_data ad
set cod_veiculo = (select vd.codigo
                   from veiculo_data vd
                   where vd.placa = ad.placa_veiculo)
where cod_veiculo is null;

-- Cria FK.
alter table afericao_data
    add constraint fk_afericao_cod_veiculo_placa
        foreign key (cod_veiculo, placa_veiculo)
            references veiculo_data (codigo, placa);


-- Existem aferições que não possuem placas (PNEU_AVULSO), portanto necessária constraint que verifique isso.
alter table afericao_data
    add
        constraint check_cod_veiculo_not_null
            check ((((tipo_processo_coleta)::text = 'PLACA'::text) and (cod_veiculo is not null)) or
                   (((tipo_processo_coleta)::text = 'PNEU_AVULSO'::text) and (cod_veiculo is null)));

-- CHECKLIST_DATA.
-- Adiciona coluna cod_veiculo.
alter table checklist_data
    add column cod_veiculo bigint;

-- Preenche coluna com os códigos dos veiculos.
update checklist_data cd
set cod_veiculo = (select vd.codigo
                   from veiculo_data vd
                   where vd.placa = cd.placa_veiculo)
where cod_veiculo is null;

-- Muda coluna para not null.
alter table checklist_data
    alter column cod_veiculo set not null;

-- Cria FK.
alter table checklist_data
    add constraint fk_checklist_cod_veiculo_placa
        foreign key (cod_veiculo, placa_veiculo)
            references veiculo_data (codigo, placa);

-- MOVIMENTACAO_DESTINO
-- Adiciona coluna cod_veiculo.
alter table movimentacao_destino
    add column cod_veiculo bigint;

-- Preenche coluna com os códigos dos veiculos.
update movimentacao_destino md
set cod_veiculo = (select vd.codigo
                   from veiculo_data vd
                   where vd.placa = md.placa)
where cod_veiculo is null;

-- cria FK.
alter table movimentacao_destino
    add constraint fk_movimentacao_destino_cod_veiculo_placa
        foreign key (cod_veiculo, placa)
            references veiculo_data (codigo, placa);

-- Movimentações que possuem placa devem possuir codigo de veículo.
alter table movimentacao_destino
    add
        constraint check_cod_veiculo_not_null
            check ((((tipo_destino)::text = 'EM_USO'::text) and (cod_veiculo is not null)) or
                   (((tipo_destino)::text = 'ESTOQUE'::text) and (cod_veiculo is null)) or
                   (((tipo_destino)::text = 'ANALISE'::text) and (cod_veiculo is null)) or
                   (((tipo_destino)::text = 'RECAPAGEM'::text) and (cod_veiculo is null)) or
                   (((tipo_destino)::text = 'DESCARTE'::text) and (cod_veiculo is null)));

-- MOVIMENTACAO_ORIGEM
-- Adiciona coluna cod_veiculo.
alter table movimentacao_origem
    add column cod_veiculo bigint;

-- Preenche coluna com os códigos dos veiculos.
update movimentacao_origem mo
set cod_veiculo = (select vd.codigo
                   from veiculo_data vd
                   where vd.placa = mo.placa)
where cod_veiculo is null;

-- Cria FK.
alter table movimentacao_origem
    add constraint fk_movimentacao_origem_cod_veiculo_placa
        foreign key (cod_veiculo, placa)
            references veiculo_data (codigo, placa);


-- Movimentações que possuem placa devem possuir codigo de veículo.
alter table movimentacao_origem
    add
        constraint check_cod_veiculo_not_null
            check ((((tipo_origem)::text = 'EM_USO'::text) and (cod_veiculo is not null)) or
                   (((tipo_origem)::text = 'ESTOQUE'::text) and (cod_veiculo is null)) or
                   (((tipo_origem)::text = 'ANALISE'::text) and (cod_veiculo is null)) or
                   (((tipo_origem)::text = 'RECAPAGEM'::text) and (cod_veiculo is null)) or
                   (((tipo_origem)::text = 'DESCARTE'::text) and (cod_veiculo is null)));

-- VEICULO_PNEU
-- Adiciona coluna cod_veiculo.
alter table veiculo_pneu
    add column cod_veiculo bigint;

-- Preenche coluna com os códigos dos veiculos.
update veiculo_pneu vp
set cod_veiculo = (select vd.codigo
                   from veiculo_data vd
                   where vd.placa = vp.placa)
where cod_veiculo is null;

-- Muda coluna para not null.
alter table veiculo_pneu
    alter column cod_veiculo set not null;

-- Cria FK.
alter table veiculo_pneu
    add constraint fk_veiculo_pneu_cod_veiculo_placa
        foreign key (cod_veiculo, placa)
            references veiculo_data (codigo, placa);

-- Critério de aceitação: alterar inserts nas tabelas para inseriram o código do veículo, além da placa.
-- Altera insert em afericao_data:
drop function func_afericao_insert_afericao(f_cod_unidade bigint,
    f_data_hora timestamp with time zone,
    f_cpf_aferidor bigint,
    f_tempo_realizacao bigint,
    f_tipo_medicao_coletada varchar,
    f_tipo_processo_coleta varchar,
    f_forma_coleta_dados text,
    f_placa_veiculo varchar,
    f_km_veiculo bigint);
create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_placa_veiculo varchar(255),
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.placa = f_placa_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao_inserida bigint;
begin
    -- realiza inserção da aferição.
    insert into afericao_data(data_hora,
                              placa_veiculo,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (f_data_hora,
            f_placa_veiculo,
            f_cpf_aferidor,
            f_km_veiculo,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    return v_cod_afericao_inserida;
end
$$;

-- Altera insert em checklist_data:
create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_tempo_realizacao bigint,
                                                                 f_data_hora_sincronizacao timestamp with time zone,
                                                                 f_fonte_data_hora_realizacao text,
                                                                 f_versao_app_momento_realizacao integer,
                                                                 f_versao_app_momento_sincronizacao integer,
                                                                 f_device_id text,
                                                                 f_device_imei text,
                                                                 f_device_uptime_realizacao_millis bigint,
                                                                 f_device_uptime_sincronizacao_millis bigint,
                                                                 f_foi_offline boolean,
                                                                 f_total_perguntas_ok integer,
                                                                 f_total_perguntas_nok integer,
                                                                 f_total_alternativas_ok integer,
                                                                 f_total_alternativas_nok integer,
                                                                 f_total_midias_perguntas_ok integer,
                                                                 f_total_midias_alternativas_nok integer)
    returns table
            (
                cod_checklist_inserido bigint,
                checklist_ja_existia   boolean
            )
    language plpgsql
as
$$
declare
    -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
    v_deve_atualizar_km_veiculo boolean := (case
                                                when (f_km_coletado > (select v.km
                                                                       from veiculo v
                                                                       where v.codigo = f_cod_veiculo))
                                                    then
                                                    true
                                                else false end);
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo    text    := (select vd.placa
                                            from veiculo_data vd
                                            where vd.codigo = f_cod_veiculo);
    v_cod_checklist_inserido    bigint;
    v_qtd_linhas_atualizadas    bigint;
    v_checklist_ja_existia      boolean := false;
begin

    insert into checklist_data(cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               data_hora_sincronizacao,
                               fonte_data_hora_realizacao,
                               versao_app_momento_realizacao,
                               versao_app_momento_sincronizacao,
                               device_id,
                               device_imei,
                               device_uptime_realizacao_millis,
                               device_uptime_sincronizacao_millis,
                               foi_offline,
                               total_perguntas_ok,
                               total_perguntas_nok,
                               total_alternativas_ok,
                               total_alternativas_nok,
                               total_midias_perguntas_ok,
                               total_midias_alternativas_nok,
                               cod_veiculo)
    values (f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            f_km_coletado,
            f_data_hora_sincronizacao,
            f_fonte_data_hora_realizacao,
            f_versao_app_momento_realizacao,
            f_versao_app_momento_sincronizacao,
            f_device_id,
            f_device_imei,
            f_device_uptime_realizacao_millis,
            f_device_uptime_sincronizacao_millis,
            f_foi_offline,
            f_total_perguntas_ok,
            f_total_perguntas_nok,
            f_total_alternativas_ok,
            f_total_alternativas_nok,
            nullif(f_total_midias_perguntas_ok, 0),
            nullif(f_total_midias_alternativas_nok, 0),
            f_cod_veiculo)
    on conflict on constraint unique_checklist
        do update set data_hora_sincronizacao = f_data_hora_sincronizacao
                      -- https://stackoverflow.com/a/40880200/4744158
    returning codigo, not (checklist_data.xmax = 0) into v_cod_checklist_inserido, v_checklist_ja_existia;

    -- Verificamos se o insert funcionou.
    if v_cod_checklist_inserido <= 0
    then
        raise exception 'Não foi possível inserir o checklist.';
    end if;

    if v_deve_atualizar_km_veiculo
    then
        update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
    end if;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
    if (v_deve_atualizar_km_veiculo and (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0))
    then
        raise exception 'Não foi possível atualizar o km do veículo.';
    end if;

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;

-- Altera insert em movimentacao_destino:
create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino varchar(255),
                                                                                 f_placa_veiculo varchar(255),
                                                                                 f_km_atual bigint,
                                                                                 f_posicao_prolog bigint)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_cod_movimentacao_realizada bigint;
    v_cod_veiculo                bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo;


    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO DESTINO.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
                                     placa,
                                     km_veiculo,
                                     posicao_pneu_destino,
                                     cod_motivo_descarte,
                                     url_imagem_descarte_1,
                                     url_imagem_descarte_2,
                                     url_imagem_descarte_3,
                                     cod_recapadora_destino,
                                     cod_coleta,
                                     cod_diagrama,
                                     cod_veiculo)
    values (f_cod_movimentacao,
            f_tipo_destino,
            f_placa_veiculo,
            f_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;

-- Altera insert em movimentacao_origem:
create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem varchar(255),
                                                                                f_cod_movimentacao bigint,
                                                                                f_placa_veiculo varchar(7),
                                                                                f_km_atual bigint,
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_veiculo                bigint;
    v_cod_diagrama_veiculo       bigint;
    v_tipo_origem_atual          varchar(255) := (select p.status
                                                  from pneu p
                                                  where p.codigo = f_cod_pneu
                                                    and p.cod_unidade = f_cod_unidade
                                                    and f_tipo_origem in (select p.status
                                                                          from pneu p
                                                                          where p.codigo = f_cod_pneu
                                                                            and p.cod_unidade = f_cod_unidade));
    f_cod_movimentacao_realizada bigint;
begin
    select v.codigo,
           v.cod_tipo,
           v.cod_diagrama
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict
        v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo;

    --REALIZA INSERÇÃO DA MOVIMENTAÇÃO ORIGEM
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    placa,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            f_placa_veiculo,
            f_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            v_cod_veiculo)
    returning cod_movimentacao into f_cod_movimentacao_realizada;

    if (f_cod_movimentacao_realizada <= 0)
    then
        perform throw_generic_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;

-- Altera insert em veiculo_pneu:
drop function func_veiculo_insere_veiculo_pneu(f_cod_unidade bigint,
    f_placa text,
    f_cod_pneu bigint,
    f_posicao bigint);
create or replace function func_veiculo_insere_veiculo_pneu(f_cod_unidade bigint,
                                                            f_placa text,
                                                            f_cod_veiculo bigint,
                                                            f_cod_pneu bigint,
                                                            f_posicao bigint)
    returns boolean
    language plpgsql
as
$$
declare
    v_cod_empresa  bigint;
    v_cod_tipo     bigint;
    v_cod_diagrama bigint;
begin
    -- Busca o código da empresa de acordo com a unidade
    v_cod_empresa := (select u.cod_empresa
                      from unidade u
                      where u.codigo = f_cod_unidade);

    -- Busca o código do tipo de veículo pela placa
    v_cod_tipo := (select vd.cod_tipo
                   from veiculo_data vd
                   where vd.placa = f_placa);

    -- Busca o código do diagrama de acordo com o tipo de veículo
    v_cod_diagrama := (select vt.cod_diagrama
                       from public.veiculo_tipo vt
                       where vt.codigo = v_cod_tipo
                         and vt.cod_empresa = v_cod_empresa);

    if v_cod_diagrama is null or v_cod_diagrama <= 0
    then
        perform throw_generic_error('Não foi possível realizar o vínculo entre veículo e pneu.');
    end if;

    -- Aqui devemos apenas inserir o veículo no prolog.
    insert into veiculo_pneu(cod_unidade,
                             placa,
                             cod_pneu,
                             posicao,
                             cod_diagrama,
                             cod_veiculo)
    values (f_cod_unidade,
            f_placa,
            f_cod_pneu,
            f_posicao,
            v_cod_diagrama,
            f_cod_veiculo);

    -- Validamos se houve alguma inserção ou atualização dos valores.
    if not found
    then
        perform throw_generic_error('Não foi possível realizar o vínculo entre veículo e pneu.');
    end if;

    return found;
end;
$$;

-- DoD que verifica dependências:
-- 2020-09-24 -> Adiciona cod_veiculo (thaisksf - PL-3170).
create or replace function implantacao.tg_func_vinculo_veiculo_pneu_confere_planilha_vinculo()
    returns trigger
    language plpgsql
    security definer
as
$$
declare
    v_qtd_erros            smallint := 0;
    v_msgs_erros           text;
    v_quebra_linha         text     := chr(10);
    v_cod_pneu             bigint;
    v_status_pneu          varchar(255);
    v_cod_unidade_pneu     bigint;
    v_cod_veiculo          bigint;
    v_placa                varchar(7);
    v_cod_tipo_veiculo     bigint;
    v_cod_diagrama_veiculo bigint;
    v_cod_unidade_placa    bigint;
    v_cod_empresa_placa    bigint;
    v_posicao_prolog       integer;
begin
    if (tg_op = 'UPDATE' and old.status_vinculo_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.usuario_update := session_user;
        new.placa_formatada_vinculo := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.numero_fogo_pneu_formatado_vinculo := remove_all_spaces(new.numero_fogo_pneu_editavel);
        new.nomenclatura_posicao_formatada_vinculo := remove_all_spaces(new.nomenclatura_posicao_editavel);

        -- Verifica se empresa existe.
        if not exists(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade existe.
        if not exists(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- Verifica se unidade pertence a empresa.
        if not exists(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- Verificações placas.
        -- Placa nula: Erro.
        -- Placa cadastrada em outra empresa: Erro.
        -- Placa cadastrada em outra unidade da mesma empresa: Erro.
        -- Posicao já ocupada por outro pneu: Erro.
        if ((new.placa_formatada_vinculo is not null) and
            (length(new.placa_formatada_vinculo) <> 0))
        then
            select v.codigo,
                   v.placa,
                   v.cod_tipo,
                   v.cod_diagrama,
                   v.cod_unidade,
                   v.cod_empresa
            into v_cod_veiculo,
                v_placa,
                v_cod_tipo_veiculo,
                v_cod_diagrama_veiculo,
                v_cod_unidade_placa,
                v_cod_empresa_placa
            from veiculo v
            where remove_all_spaces(v.placa) ilike
                  new.placa_formatada_vinculo;
            if (v_placa is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- A PLACA NÃO FOI ENCONTRADA',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_empresa_placa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA EMPRESA',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_cod_unidade_placa != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS A PLACA PERTENCE A OUTRA
                                              UNIDADE',
                                              v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    else
                        -- Verificar se a posição existe nesse veículo e se está disponível.
                        if ((new.nomenclatura_posicao_formatada_vinculo is not null) and
                            (length(new.nomenclatura_posicao_formatada_vinculo) <> 0))
                        then
                            select ppne.posicao_prolog
                            into v_posicao_prolog
                            from pneu_posicao_nomenclatura_empresa ppne
                            where ppne.cod_diagrama = v_cod_diagrama_veiculo
                              and remove_all_spaces(ppne.nomenclatura)
                                ilike new.nomenclatura_posicao_formatada_vinculo
                              and ppne.cod_empresa = new.cod_empresa;
                            if (v_posicao_prolog is not null)
                            then
                                if exists(select vp.placa
                                          from veiculo_pneu vp
                                          where remove_all_spaces(vp.placa) = new.placa_formatada_vinculo
                                            and vp.posicao = v_posicao_prolog
                                            and vp.cod_unidade = new.cod_unidade)
                                then
                                    v_qtd_erros = v_qtd_erros + 1;
                                    v_msgs_erros =
                                            concat(v_msgs_erros, v_qtd_erros,
                                                   '- JÁ EXISTE PNEU VINCULADO À POSIÇÃO (NOMENCLATURA) INFORMADA',
                                                   v_quebra_linha);
                                end if;
                            else
                                v_qtd_erros = v_qtd_erros + 1;
                                v_msgs_erros =
                                        concat(v_msgs_erros, v_qtd_erros,
                                               '- NOMENCLATURA NÃO ENCONTRADA',
                                               v_quebra_linha);
                                new.status_vinculo_realizado = false;
                            end if;
                        else
                            v_qtd_erros = v_qtd_erros + 1;
                            v_msgs_erros =
                                    concat(v_msgs_erros, v_qtd_erros,
                                           '- NOMENCLATURA NÃO PODE SER NULA',
                                           v_quebra_linha);
                        end if;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A PLACA DE FOGO NÃO PODE SER NULA',
                           v_quebra_linha);
        end if;


        -- Verificações número de fogo.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Código do pneu não encontrado: Erro.
        -- Status do pneu diferente de 'ESTOQUE': Erro.
        if ((new.numero_fogo_pneu_formatado_vinculo is not null) and
            (length(new.numero_fogo_pneu_formatado_vinculo) <> 0))
        then
            select p.codigo,
                   p.status,
                   p.cod_unidade
            into v_cod_pneu, v_status_pneu, v_cod_unidade_pneu
            from pneu p
            where remove_all_spaces(p.codigo_cliente) ilike
                  new.numero_fogo_pneu_formatado_vinculo
              and p.cod_empresa = new.cod_empresa;
            if (v_cod_pneu is null)
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                      '- O PNEU NÃO FOI ENCONTRADO',
                                      v_quebra_linha);
                new.status_vinculo_realizado = false;
            else
                if (v_cod_unidade_pneu != new.cod_unidade)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                          '- NÃO É POSSÍVEL REALIZAR O VÍNCULO POIS O PNEU PERTENCE A OUTRA UNIDADE',
                                          v_quebra_linha);
                    new.status_vinculo_realizado = false;
                else
                    if (v_status_pneu != 'ESTOQUE')
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros =
                                concat(v_msgs_erros, v_qtd_erros,
                                       '- PARA REALIZAR O VÍNCULO O PNEU DEVE ESTAR EM ESTOQUE, O STATUS ATUAL DO PNEU
                                       É: ',
                                       v_status_pneu, v_quebra_linha);
                        new.status_vinculo_realizado = false;
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- O NÚMERO DE FOGO NÃO PODE SER NULO',
                           v_quebra_linha);
        end if;

        if (v_qtd_erros > 0)
        then
            new.erros_encontrados = v_msgs_erros;
        else
            update pneu_data set status = 'EM_USO' where codigo = v_cod_pneu;
            insert into veiculo_pneu (placa,
                                      cod_pneu,
                                      cod_unidade,
                                      posicao,
                                      cod_diagrama,
                                      cod_veiculo)
            values (v_placa,
                    v_cod_pneu,
                    new.cod_unidade,
                    v_posicao_prolog,
                    v_cod_diagrama_veiculo,
                    v_cod_veiculo);

            new.status_vinculo_realizado = true;
            new.erros_encontrados = '-';
        end if;
    end if;
    return new;
end;
$$;

-- 2020-09-24 -> Adiciona cod_veiculo (thaisksf - PL-3170).
create or replace function
    interno.func_clona_vinculo_veiculos_pneus(f_cod_unidade_base bigint, f_cod_unidade_usuario bigint)
    returns void
    language plpgsql
as
$$
declare
    v_placas_com_vinculo text := (select array_agg(vp.placa)
                                  from veiculo_pneu vp
                                  where vp.cod_unidade = f_cod_unidade_base);
begin
    -- Copia vínculos, caso existam.
    if (v_placas_com_vinculo is not null)
    then
        with veiculos_base as (
            select row_number() over () as codigo_comparacao,
                   v.placa,
                   vdpp.posicao_prolog
            from veiculo_data v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                     join veiculo_diagrama_posicao_prolog vdpp
                          on vt.cod_diagrama = vdpp.cod_diagrama
            where v.cod_unidade = f_cod_unidade_base
        ),
             veiculos_novos as (
                 select row_number() over () as codigo_comparacao,
                        v.placa,
                        v.cod_diagrama,
                        v.codigo,
                        vdpp.posicao_prolog
                 from veiculo_data v
                          join veiculo_tipo vt on v.cod_tipo = vt.codigo and v.cod_empresa = vt.cod_empresa
                          join veiculo_diagrama_posicao_prolog vdpp on vt.cod_diagrama = vdpp.cod_diagrama
                 where v.cod_unidade = f_cod_unidade_usuario),
             dados_de_para as (
                 select vn.codigo         as cod_veiculo_novo,
                        vn.placa          as placa_nova,
                        vn.posicao_prolog as posicao_prolog_novo,
                        vn.cod_diagrama   as cod_diagrama_novo,
                        pdn.codigo        as cod_pneu_novo
                 from veiculos_base vb
                          join veiculos_novos vn
                               on vb.codigo_comparacao = vn.codigo_comparacao and vb.posicao_prolog = vn.posicao_prolog
                          join veiculo_pneu vp on vb.placa = vp.placa and vb.posicao_prolog = vp.posicao
                          join pneu_data pdb
                               on vp.status_pneu = pdb.status and vp.cod_unidade = pdb.cod_unidade and
                                  vp.cod_pneu = pdb.codigo
                          join pneu_data pdn
                               on pdb.codigo_cliente = pdn.codigo_cliente and
                                  pdn.cod_unidade = f_cod_unidade_usuario and
                                  pdn.status = 'EM_USO')
        insert
        into veiculo_pneu (placa,
                           cod_pneu,
                           cod_unidade,
                           posicao,
                           cod_diagrama,
                           cod_veiculo)
        select ddp.placa_nova,
               ddp.cod_pneu_novo,
               f_cod_unidade_usuario,
               ddp.posicao_prolog_novo,
               ddp.cod_diagrama_novo,
               ddp.cod_veiculo_novo
        from dados_de_para ddp;
    end if;
end;
$$;