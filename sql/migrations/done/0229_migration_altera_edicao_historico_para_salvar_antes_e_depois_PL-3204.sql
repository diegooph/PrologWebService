-- Lógica de migração dos dados
do
$$
    declare
        v_cod_veiculo_atual                                bigint;
        v_cursor_edicoes cursor for select *
                                    from temp_historico_edicao
                                    order by codigo;
        v_cod_insercao_estado_atual                        bigint;
        v_cod_veiculo_existentes_historico_edicao constant bigint[] := array(select distinct cod_veiculo_edicao
                                                                             from veiculo_edicao_historico);
        v_record_edicao_atual                              record;
        v_record_veiculo_audit                             record;
        v_intervalo_range                         constant interval := interval '2 second';
    begin
        -- Cria as novas colunas para o novo formato do historico de edicao de veiculo
        alter table veiculo_edicao_historico
            add column if not exists codigo_edicao_vinculada bigint,
            add column if not exists estado_antigo           boolean,
            add column if not exists data_hora_utc           timestamp with time zone;
        update veiculo_edicao_historico
        set estado_antigo = true;
        -- Cria tabela temporaria proveniente da tabela de audit para todos os códigos de veiculo que existem na tabela
        -- de histórico de edição de veiculo
        create temp table if not exists temp_veiculos_audit
        as
        select vd.data_hora_utc as data_hora_log,
               vd.operacao      as operacao,
               (jsonb_populate_record(NULL::veiculo_data, vd.row_log)).*
        from audit.veiculo_data_audit vd
        where vd.row_log ->> 'codigo' = any (v_cod_veiculo_existentes_historico_edicao::text[])
          and vd.operacao = 'U'
        order by vd.data_hora_utc;
        -- Realiza o processamento para criar a data utc para tudo que for prolog ou suporte, pq sabemos
        -- que a data está correta (se a data sem timezone agora é 12h, e estamos na Palhoça - America/Sao_Paulo, -3h -,
        -- a data salva na coluna data_hora_edicao_tz_aplicado vai ser 9h. Então reaplicamos o tz que é pra subir pra 12
        -- e salvarmos no UTC.
        update veiculo_edicao_historico
        set data_hora_utc = data_hora_edicao_tz_aplicado at time zone tz_unidade((select cod_unidade
                                                                                  from veiculo_data
                                                                                  where codigo = cod_veiculo_edicao))
        where origem_edicao = 'SUPORTE'
           or origem_edicao = 'PROLOG';
        -- Aqui realizamos o processamento para os dados apenas da API. Aqui, se o registro não for encontrado na tabela
        -- de audit, quer dizer que é o registro que está com a hora incorreta (salvou a data em utc onde deveria ser
        -- com tz aplicado, então apenas replicamos o dado. Agora, se for encontrado na tabela de audit, então quer
        -- dizer que salvou certo, salvou com o timezone aplicado (se estamos em palhoça. salvou -3h). Então reaplicamos
        -- o tz para subir a hora.
        -- Aproveitamos para realizar o ajuste da data hora tz aplicado. Agora que temos o utc correto, basta aplicar
        -- o tz para diminuir a hora e salvar novamente na coluna.
        update veiculo_edicao_historico
        set data_hora_utc = case
                                when (select codigo
                                      from temp_veiculos_audit
                                      where codigo = cod_veiculo_edicao
                                        and data_hora_log between (data_hora_edicao_tz_aplicado at time zone
                                                                   tz_unidade((
                                                                       select cod_unidade
                                                                       from veiculo_data
                                                                       where codigo = cod_veiculo_edicao)) -
                                                                   v_intervalo_range)
                                          and (data_hora_edicao_tz_aplicado at time zone
                                               tz_unidade((
                                                   select cod_unidade
                                                   from veiculo_data
                                                   where codigo = cod_veiculo_edicao)) +
                                               v_intervalo_range)
                                      fetch first 1 row only) is not null
                                    then data_hora_edicao_tz_aplicado at time zone
                                         tz_unidade((
                                             select cod_unidade
                                             from veiculo_data
                                             where codigo = cod_veiculo_edicao))
                                else data_hora_edicao_tz_aplicado
            end
        where origem_edicao = 'API';
        update veiculo_edicao_historico
        set data_hora_edicao_tz_aplicado = data_hora_utc at time zone tz_unidade((select cod_unidade
                                                                                  from veiculo_data
                                                                                  where codigo = cod_veiculo_edicao))
        where origem_edicao = 'API';
        -- Após fazer o processamento acima, a gente vai arrumar a coluna data_hora_edicao_tz_aplicado para salvar
        -- realmente a data hora com tz aplicado (se estamos na palhoça, -3h).
        foreach v_cod_veiculo_atual in array v_cod_veiculo_existentes_historico_edicao
            loop
            -- cria a uma tabela temporaria com os historicos de um veiculo a cada código de veiculo
            -- Usamos o select igual o da function atual, mas trazemos os códigos das coisas ao invés do nome.
            -- Usei a tabela temporaria apenas pra deixar mais procedural e facil de compreender o que está sendo
            -- trabalhado.
                create temp table temp_historico_edicao
                as
                select *
                from (select codigo,
                             cod_empresa_veiculo,
                             cod_veiculo_edicao,
                             cod_colaborador_edicao,
                             data_hora_edicao_tz_aplicado,
                             data_hora_utc,
                             origem_edicao,
                             total_edicoes_processo,
                             informacoes_extras,
                             placa,
                             identificador_frota,
                             km,
                             status,
                             cod_diagrama_veiculo,
                             cod_tipo_veiculo,
                             cod_modelo_veiculo,
                             codigo_edicao_vinculada,
                             estado_antigo
                      from veiculo_edicao_historico) historico
                where historico.cod_veiculo_edicao = v_cod_veiculo_atual
                order by historico.cod_empresa_veiculo, cod_veiculo_edicao,
                         data_hora_utc;
                open v_cursor_edicoes;
                loop
                    fetch v_cursor_edicoes into v_record_edicao_atual;
                    exit when not FOUND;
                    -- Obtem a linha correspondente a alteração que estou trabalhando, no audit, pois lá contem os dados
                    -- novos
                    select *
                    into v_record_veiculo_audit
                    from temp_veiculos_audit
                    where codigo = v_record_edicao_atual.cod_veiculo_edicao
                      and data_hora_log between v_record_edicao_atual.data_hora_utc - v_intervalo_range
                        and v_record_edicao_atual.data_hora_utc + v_intervalo_range
                    fetch first 1 row only;
                    raise info '%', v_record_veiculo_audit.codigo;
                    -- Esse if existe por conta de registros de veiculos que tiveram a unidade modificada, pois por isso
                    -- ao calcular a data UTC a partir do timezone da unidade, resulta em uma data diferente da data
                    -- registrada na audit.
                    if v_record_veiculo_audit.placa is not null
                    then
                        insert into veiculo_edicao_historico (cod_empresa_veiculo,
                                                              cod_veiculo_edicao,
                                                              cod_colaborador_edicao,
                                                              data_hora_edicao_tz_aplicado,
                                                              data_hora_utc,
                                                              origem_edicao,
                                                              total_edicoes_processo,
                                                              informacoes_extras,
                                                              placa,
                                                              identificador_frota,
                                                              km,
                                                              status,
                                                              cod_diagrama_veiculo,
                                                              cod_tipo_veiculo,
                                                              cod_modelo_veiculo,
                                                              codigo_edicao_vinculada,
                                                              estado_antigo)
                        values (v_record_veiculo_audit.cod_empresa,
                                v_record_veiculo_audit.codigo,
                                v_record_edicao_atual.cod_colaborador_edicao,
                                v_record_edicao_atual.data_hora_edicao_tz_aplicado,
                                v_record_edicao_atual.data_hora_utc,
                                v_record_edicao_atual.origem_edicao,
                                v_record_edicao_atual.total_edicoes_processo,
                                v_record_edicao_atual.informacoes_extras,
                                v_record_veiculo_audit.placa,
                                v_record_veiculo_audit.identificador_frota,
                                v_record_veiculo_audit.km,
                                v_record_veiculo_audit.status_ativo,
                                v_record_veiculo_audit.cod_diagrama,
                                v_record_veiculo_audit.cod_tipo,
                                v_record_veiculo_audit.cod_modelo,
                                v_record_edicao_atual.codigo,
                                false)
                        returning codigo into v_cod_insercao_estado_atual;
                        update veiculo_edicao_historico
                        set codigo_edicao_vinculada = v_cod_insercao_estado_atual
                        where codigo = v_record_edicao_atual.codigo;
                    else
                        delete from veiculo_edicao_historico where codigo = v_record_edicao_atual.codigo;
                    end if;
                end loop;
                close v_cursor_edicoes;
                drop table temp_historico_edicao;
            end loop;
        drop table temp_veiculos_audit;
        alter table veiculo_edicao_historico
            alter column data_hora_utc set not null,
            alter column estado_antigo set not null,
            alter column codigo_edicao_vinculada set not null,
            add constraint  fk_veiculo_edicao_historico
            foreign key (codigo) references veiculo_edicao_historico;
    end;
$$;
------------------------------------------------------------------------------------------------------------------------

drop function if exists func_veiculo_listagem_historico_edicoes(f_cod_empresa bigint,
    f_cod_veiculo bigint);
create or replace function func_veiculo_listagem_historico_edicoes(f_cod_empresa bigint,
                                                                   f_cod_veiculo bigint)
    returns table
            (
                codigo_historico          bigint,
                codigo_empresa_veiculo    bigint,
                codigo_veiculo_edicao     bigint,
                codigo_colaborador_edicao bigint,
                nome_colaborador_edicao   text,
                data_hora_edicao          timestamp without time zone,
                origem_edicao             text,
                origem_edicao_legivel     text,
                total_edicoes             smallint,
                informacoes_extras        text,
                placa                     text,
                identificador_frota       text,
                km_veiculo                bigint,
                status                    boolean,
                diagrama_veiculo          text,
                tipo_veiculo              text,
                modelo_veiculo            text,
                marca_veiculo             text
            )
    language plpgsql
as
$$
begin
    return query
        select veh.codigo                       as codigo_historico,
               veh.cod_empresa_veiculo          as codigo_empresa_veiculo,
               veh.cod_veiculo_edicao           as codigo_veiculo_edicao,
               veh.cod_colaborador_edicao       as codigo_colaborador_edicao,
               c.nome::text                     as nome_colaborador_edicao,
               veh.data_hora_edicao_tz_aplicado as data_hora_edicao,
               veh.origem_edicao                as origem_edicao,
               oa.origem_acao                   as origem_edicao_legivel,
               veh.total_edicoes_processo       as total_edicoes,
               veh.informacoes_extras           as informacoes_extras,
               veh.placa                        as placa,
               veh.identificador_frota          as identificador_frota,
               veh.km                           as km_veiculo,
               veh.status                       as status,
               vd.nome::text                    as diagrama_veiculo,
               vt.nome::text                    as tipo_veiculo,
               mv.nome::text                    as modelo_veiculo,
               mav.nome::text                   as marca_veiculo
        from veiculo_edicao_historico veh
                 inner join types.origem_acao oa on oa.origem_acao = veh.origem_edicao
                 inner join veiculo_diagrama vd on vd.codigo = veh.cod_diagrama_veiculo
                 inner join veiculo_tipo vt on vt.codigo = veh.cod_tipo_veiculo
                 inner join modelo_veiculo mv on mv.codigo = veh.cod_modelo_veiculo
                 inner join marca_veiculo mav on mav.codigo = mv.cod_marca
                 left join colaborador c on c.codigo = veh.cod_colaborador_edicao
            and c.cod_empresa = veh.cod_empresa_veiculo
        where veh.cod_veiculo_edicao = f_cod_veiculo
          and veh.cod_empresa_veiculo = f_cod_empresa
          -- A lógica no java depende que o valor nulo venha primeiro, que no caso é o estado atual do veículo.
        order by veh.data_hora_utc, veh.estado_antigo;
end;
$$;


drop function if exists func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint,
    f_cod_unidade_veiculo bigint,
    f_cod_veiculo bigint,
    f_antiga_placa text,
    f_antigo_identificador_frota text,
    f_antigo_km bigint,
    f_antigo_cod_diagrama bigint,
    f_antigo_cod_tipo bigint,
    f_antigo_cod_modelo bigint,
    f_antigo_status boolean,
    f_total_edicoes smallint,
    f_cod_colaborador_edicao bigint,
    f_origem_edicao text,
    f_data_hora_edicao timestamp with time zone,
    f_informacoes_extras_edicao text);
create or replace function func_veiculo_gera_historico_atualizacao(f_cod_empresa bigint,
                                                                   f_cod_veiculo bigint,
                                                                   f_cod_colaborador_edicao bigint,
                                                                   f_origem_edicao text,
                                                                   f_data_hora_edicao_tz_aplicado timestamp
                                                                       with time zone,
                                                                   f_informacoes_extras_edicao text,
                                                                   f_nova_placa text,
                                                                   f_novo_identificador_frota text,
                                                                   f_novo_km bigint,
                                                                   f_novo_cod_diagrama bigint,
                                                                   f_novo_cod_tipo bigint,
                                                                   f_novo_cod_modelo bigint,
                                                                   f_novo_status boolean,
                                                                   f_total_edicoes smallint)
    returns table
            (
                codigo_historico_estado_antigo bigint,
                codigo_historico_estado_novo   bigint
            )
    language plpgsql
as
$$
declare
    v_cod_edicao_historico_estado_antigo bigint;
    v_cod_edicao_historico_estado_novo   bigint;
    v_cod_unidade                        bigint;
    v_antiga_placa                       text;
    v_antigo_identificador_frota         text;
    v_antigo_km                          bigint;
    v_antigo_cod_diagrama                bigint;
    v_antigo_cod_tipo                    bigint;
    v_antigo_cod_marca                   bigint;
    v_antigo_cod_modelo                  bigint;
    v_antigo_status                      boolean;
    v_data_hora_edicao_tz_unidade timestamp with time zone;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

   v_data_hora_edicao_tz_unidade := f_data_hora_edicao_tz_aplicado at time zone
   tz_unidade( v_cod_unidade);

    set constraints all deferred;
    v_cod_edicao_historico_estado_antigo
        := (select nextval(pg_get_serial_sequence('veiculo_edicao_historico', 'codigo')));
    v_cod_edicao_historico_estado_novo
        := (select nextval(pg_get_serial_sequence('veiculo_edicao_historico', 'codigo')));

    insert into veiculo_edicao_historico (codigo,
                                          cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          data_hora_utc,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo,
                                          codigo_edicao_vinculada,
                                          estado_antigo)
    values (v_cod_edicao_historico_estado_antigo,
            f_cod_empresa,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            v_data_hora_edicao_tz_unidade,
            now(),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            v_antiga_placa,
            v_antigo_identificador_frota,
            v_antigo_km,
            v_antigo_status,
            v_antigo_cod_diagrama,
            v_antigo_cod_tipo,
            v_antigo_cod_modelo,
            v_cod_edicao_historico_estado_novo,
            true);

    insert into veiculo_edicao_historico (codigo,
                                          cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          data_hora_utc,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo,
                                          codigo_edicao_vinculada,
                                          estado_antigo)
    values (v_cod_edicao_historico_estado_novo,
            f_cod_empresa,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            v_data_hora_edicao_tz_unidade,
            now(),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            f_nova_placa,
            f_novo_identificador_frota,
            f_novo_km,
            f_novo_status,
            f_novo_cod_diagrama,
            f_novo_cod_tipo,
            f_novo_cod_modelo,
            v_cod_edicao_historico_estado_antigo,
            false);

    return query
        select v_cod_edicao_historico_estado_antigo, v_cod_edicao_historico_estado_novo;
end;
$$;


drop function if exists func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
    f_nova_placa text,
    f_novo_identificador_frota text,
    f_novo_km bigint,
    f_novo_cod_tipo bigint,
    f_novo_cod_modelo bigint,
    f_novo_status boolean,
    f_cod_colaborador_edicao bigint,
    f_origem_edicao text,
    f_data_hora_edicao timestamp with time zone,
    f_informacoes_extras_edicao text);
create or replace function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
                                                         f_nova_placa text,
                                                         f_novo_identificador_frota text,
                                                         f_novo_km bigint,
                                                         f_novo_cod_tipo bigint,
                                                         f_novo_cod_modelo bigint,
                                                         f_novo_status boolean,
                                                         f_cod_colaborador_edicao bigint,
                                                         f_origem_edicao text,
                                                         f_data_hora_edicao timestamp with time zone,
                                                         f_informacoes_extras_edicao text)
    returns table
            (
                cod_edicao_historico_antigo bigint,
                cod_edicao_historico_novo   bigint,
                total_edicoes               smallint,
                antiga_placa                text,
                antigo_identificador_frota  text,
                antigo_km                   bigint,
                antigo_cod_diagrama         bigint,
                antigo_cod_tipo             bigint,
                antigo_cod_modelo           bigint,
                antigo_status               boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant  bigint not null := (select v.cod_empresa
                                                      from veiculo v
                                                      where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant  bigint not null := (select vt.cod_diagrama
                                                      from veiculo_tipo vt
                                                      where vt.codigo = f_novo_cod_tipo
                                                        and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant  bigint not null := (select mv.cod_marca
                                                      from modelo_veiculo mv
                                                      where mv.codigo = f_novo_cod_modelo);
    v_cod_edicao_historico_antigo bigint;
    v_cod_edicao_historico_novo   bigint;
    v_total_edicoes               smallint;
    v_cod_unidade                 bigint;
    v_antiga_placa                text;
    v_antigo_identificador_frota  text;
    v_antigo_km                   bigint;
    v_antigo_cod_diagrama         bigint;
    v_antigo_cod_tipo             bigint;
    v_antigo_cod_marca            bigint;
    v_antigo_cod_modelo           bigint;
    v_antigo_status               boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    -- Validamos se o km foi inputado corretamente.
    if (f_novo_km < 0)
    then
        perform throw_generic_error(
                'A quilometragem do veículo não pode ser um número negativo.');
    end if;

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    if ((v_antigo_cod_tipo <> f_novo_cod_tipo)
        and (select count(vp.*)
             from veiculo_pneu vp
             where vp.placa = (select v.placa from veiculo v where v.codigo = f_cod_veiculo)) > 0)
    then
        perform throw_generic_error(
                'O tipo do veículo não pode ser alterado se a placa contém pneus aplicados.');
    end if;

    -- Agora que passou nas verificações, calcula quantas alterações foram feitas:
    -- hstore é uma estrutura que salva os dados como chave => valor. Fazendo hstore(novo) - hstore(antigo) irá
    -- sobrar apenas as entradas (chave => valor) que mudaram. Depois, aplicamos um akeys(hstore), que retorna um
    -- array das chaves (apenas as que mudaram) (poderia ser um avalues(hstore) também). Por fim, fazemos um
    -- f_size_array para saber o tamanho desse array: isso nos dá o número de edições realizadas.
    -- IMPORTANTE: como a placa não é atualiza no update abaixo, também ignoramos ela na contagem de total de edições.
    v_total_edicoes := f_size_array(akeys(hstore((f_novo_identificador_frota,
                                                  f_novo_km,
                                                  v_novo_cod_diagrama,
                                                  f_novo_cod_tipo,
                                                  v_novo_cod_marca,
                                                  f_novo_cod_modelo,
                                                  f_novo_status)) - hstore((v_antigo_identificador_frota,
                                                                            v_antigo_km,
                                                                            v_antigo_cod_diagrama,
                                                                            v_antigo_cod_tipo,
                                                                            v_antigo_cod_marca,
                                                                            v_antigo_cod_modelo,
                                                                            v_antigo_status))));

    -- O update no veículo só será feito se algo de fato mudou. E algo só mudou se o total de edições for maior que 0.
    if (v_total_edicoes is not null and v_total_edicoes > 0)
    then
        select codigo_historico_estado_antigo, codigo_historico_estado_novo
        into strict v_cod_edicao_historico_antigo, v_cod_edicao_historico_novo
        from func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                     f_cod_veiculo,
                                                     f_cod_colaborador_edicao,
                                                     f_origem_edicao,
                                                     f_data_hora_edicao,
                                                     f_informacoes_extras_edicao,
                                                     f_nova_placa,
                                                     f_novo_identificador_frota,
                                                     f_novo_km,
                                                     v_novo_cod_diagrama,
                                                     f_novo_cod_tipo,
                                                     f_novo_cod_modelo,
                                                     f_novo_status,
                                                     v_total_edicoes);

        update veiculo
        set identificador_frota = f_novo_identificador_frota,
            km                  = f_novo_km,
            cod_modelo          = f_novo_cod_modelo,
            cod_tipo            = f_novo_cod_tipo,
            cod_diagrama        = v_novo_cod_diagrama,
            status_ativo        = f_novo_status,
            foi_editado         = true
        where codigo = f_cod_veiculo
          and cod_empresa = v_cod_empresa;

        -- Verificamos se o update na tabela de veículos ocorreu com êxito.
        if (not found)
        then
            perform throw_generic_error('Não foi possível atualizar o veículo, tente novamente.');
        end if;
    end if;

    return query
        select v_cod_edicao_historico_antigo,
               v_cod_edicao_historico_novo,
               v_total_edicoes,
               v_antiga_placa,
               v_antigo_identificador_frota,
               v_antigo_km,
               v_antigo_cod_diagrama,
               v_antigo_cod_tipo,
               v_antigo_cod_modelo,
               v_antigo_status;
end;
$$;


drop function if exists suporte.func_veiculo_altera_placa(f_cod_unidade_veiculo bigint,
    f_cod_veiculo bigint,
    f_placa_antiga text,
    f_placa_nova text,
    f_informacoes_extras_suporte text,
    f_forcar_atualizacao_placa_integracao boolean,
    out f_aviso_placa_alterada text);
create or replace function suporte.func_veiculo_altera_placa(f_cod_unidade_veiculo bigint,
                                                             f_cod_veiculo bigint,
                                                             f_placa_antiga text,
                                                             f_placa_nova text,
                                                             f_informacoes_extras_suporte text,
                                                             f_forcar_atualizacao_placa_integracao boolean default false,
                                                             out f_aviso_placa_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_empresa         bigint;
    v_identificador_frota text;
    v_km                  bigint;
    v_cod_diagrama        bigint;
    v_cod_tipo            bigint;
    v_cod_modelo          bigint;
    v_status              boolean;
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade_veiculo);
    perform func_garante_veiculo_existe(f_cod_unidade_veiculo, f_placa_antiga);

    -- Verifica se placa nova está disponível.
    if exists(select vd.placa from veiculo_data vd where vd.placa = f_placa_nova)
    then
        raise exception
            'A placa % já existe no banco.', f_placa_nova;
    end if;

    -- Verifica se a placa é de integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        -- Verifica se deve alterar placa em integração.
        if (f_forcar_atualizacao_placa_integracao is false)
        then
            raise exception
                'A placa % pertence à integração. para atualizar a mesma, deve-se passar true como parâmetro.',
                f_placa_antiga;
        end if;
    end if;

    if exists(select vp.placa from veiculo_pneu vp where vp.placa = f_placa_antiga)
    then
        -- Assim conseguimos alterar a placa na VEICULO_PNEU sem ela ainda existir na tabela VEICULO_DATA.
        set constraints all deferred;

        update veiculo_pneu
        set placa = f_placa_nova
        where placa = f_placa_antiga
          and cod_unidade = f_cod_unidade_veiculo;

        if (not found)
        then
            raise exception
                'Não foi possível modificar a placa para % no vínculo de veículo pneu.', f_placa_nova;
        end if;
    end if;

    -- Agora alteramos a placa.
    select v.cod_empresa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_empresa,
        v_identificador_frota,
        v_km,
        v_cod_diagrama,
        v_cod_tipo,
        v_cod_modelo,
        v_status
    from veiculo v
    where v.codigo = f_cod_veiculo
      and v.cod_unidade = f_cod_unidade_veiculo;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    f_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_nova,
                                                    v_identificador_frota,
                                                    v_km,
                                                    v_cod_diagrama, -- Apenas a placa mudou.
                                                    v_cod_tipo,
                                                    v_cod_modelo,
                                                    v_status,
                                                    1::smallint);

    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set placa       = f_placa_nova,
        foi_editado = true
    where codigo = f_cod_veiculo
      and placa = f_placa_antiga
      and cod_unidade = f_cod_unidade_veiculo;

    -- Modifica placa na integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        update integracao.veiculo_cadastrado
        set placa_veiculo_cadastro = f_placa_nova
        where placa_veiculo_cadastro = f_placa_antiga;

        if (not found)
        then
            raise exception
                'Não foi possível modificar a placa para % na tabela de integração VEICULO_CADASTRADO.', f_placa_nova;
        end if;
    end if;


    select 'A placa foi alterada de '
               || f_placa_antiga ||
           ' para '
               || f_placa_nova || '.'
    into f_aviso_placa_alterada;
end ;
$$;


drop function if exists suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo text,
    f_cod_veiculo_tipo_novo bigint,
    f_cod_unidade bigint,
    f_informacoes_extras_suporte text,
    out aviso_tipo_veiculo_alterado text);
create or replace function suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo text,
                                                                    f_cod_veiculo_tipo_novo bigint,
                                                                    f_cod_unidade bigint,
                                                                    f_informacoes_extras_suporte text,
                                                                    out aviso_tipo_veiculo_alterado text)
    returns text
    security definer
    language plpgsql
as
$$
declare
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    v_cod_diagrama_novo constant bigint := (select vt.cod_diagrama
                                            from veiculo_tipo vt
                                            where vt.codigo = f_cod_veiculo_tipo_novo);
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    V_cod_empresa       constant bigint := (select u.cod_empresa
                                            from unidade u
                                            where u.codigo = f_cod_unidade);
    v_cod_veiculo                bigint;
    v_identificador_frota_antigo text;
    v_km_antigo                  bigint;
    v_cod_diagrama_antigo        bigint;
    v_cod_tipo_antigo            bigint;
    v_cod_modelo                 bigint;
    v_status_antigo              boolean;
begin
    perform suporte.func_historico_salva_execucao();

    -- Garante que unidade/empresa existem.
    perform func_garante_unidade_existe(f_cod_unidade);

    -- Garante que veiculo existe e pertence a unidade sem considerar os deletados.
    perform func_garante_veiculo_existe(f_cod_unidade, f_placa_veiculo, false);

    -- Garante que tipo_veiculo_novo pertence a empresa.
    if not exists(select vt.codigo
                  from veiculo_tipo vt
                  where vt.codigo = f_cod_veiculo_tipo_novo
                    and vt.cod_empresa = V_cod_empresa)
    then
        raise exception
            'O tipo de veículo de código: % não pertence à empresa: %',
            f_cod_veiculo_tipo_novo,
            V_cod_empresa;
    end if;

    -- Verifica se placa tem pneus aplicados.
    if exists(select vp.placa from veiculo_pneu vp where vp.placa = f_placa_veiculo)
    then
        -- Se existirem pneus, verifica se os pneus que aplicados possuem as mesmas posições do novo tipo.
        if ((select array_agg(vp.posicao)
             from veiculo_pneu vp
             where vp.placa = f_placa_veiculo) <@
            (select array_agg(vdpp.posicao_prolog :: integer)
             from veiculo_diagrama_posicao_prolog vdpp
             where cod_diagrama = v_cod_diagrama_novo) = false)
        then
            raise exception
                'Existem pneus aplicados em posições que não fazem parte do tipo de veículo de código: %',
                f_cod_veiculo_tipo_novo;
        end if;
    end if;

    -- Busca os dados necessários para mandarmos para a function de update.
    select v.codigo,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_veiculo,
        v_identificador_frota_antigo,
        v_km_antigo,
        v_cod_diagrama_antigo,
        v_cod_tipo_antigo,
        v_cod_modelo,
        v_status_antigo
    from veiculo v
    where v.placa = f_placa_veiculo
      and v.cod_unidade = f_cod_unidade;

    -- Verifica se o tipo_veiculo_novo é o atual.
    if v_cod_tipo_antigo = f_cod_veiculo_tipo_novo
    then
        raise exception
            'O tipo de veículo atual da placa % é igual ao informado. Código tipo de veículo: %',
            f_placa_veiculo,
            f_cod_veiculo_tipo_novo;
    end if;

    if exists(select vp.placa from veiculo_pneu vp where vp.placa = f_placa_veiculo)
        and v_cod_diagrama_antigo <> v_cod_diagrama_novo
    then
        -- Assim conseguimos alterar o cod_diagrama na VEICULO_PNEU sem ele ainda estar alterado na tabela VEICULO_DATA.
        set constraints all deferred;

        update veiculo_pneu
        set cod_diagrama = v_cod_diagrama_novo
        where placa = f_placa_veiculo
          and cod_unidade = f_cod_unidade
          and cod_diagrama = v_cod_diagrama_antigo;

        if (not found)
        then
            raise exception
                'Não foi possível modificar o cod_diagrama para a placa % no vínculo de veículo pneu.', f_placa_veiculo;
        end if;
    end if;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    v_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_veiculo,
                                                    v_identificador_frota_antigo,
                                                    v_km_antigo,
                                                    v_cod_diagrama_novo,
                                                    f_cod_veiculo_tipo_novo,
                                                    v_cod_modelo,
                                                    v_status_antigo,
                                                    (f_if(v_cod_tipo_antigo <> f_cod_veiculo_tipo_novo, 1, 0)
                                                        +
                                                     f_if(v_cod_diagrama_antigo <> v_cod_diagrama_novo, 1, 0))::smallint);

    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set cod_tipo     = f_cod_veiculo_tipo_novo,
        cod_diagrama = v_cod_diagrama_novo,
        foi_editado  = true
    where codigo = v_cod_veiculo
      and placa = f_placa_veiculo
      and cod_unidade = f_cod_unidade;

    -- Mensagem de sucesso.
    select 'Tipo do veículo alterado! ' ||
           'Placa: ' || f_placa_veiculo ||
           ', Código da unidade: ' || f_cod_unidade ||
           ', Tipo: ' || (select vt.nome from veiculo_tipo vt where vt.codigo = f_cod_veiculo_tipo_novo) ||
           ', Código do tipo: ' || f_cod_veiculo_tipo_novo || '.'
    into aviso_tipo_veiculo_alterado;
end;
$$;