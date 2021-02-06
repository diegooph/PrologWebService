-- PL-3096
create table types.origem_acao_type
(
    origem_acao               text    not null
        constraint pk_origem_acao_type
            primary key,
    origem_acao_legivel_pt_br text    not null,
    origem_acao_legivel_es    text    not null,
    ativo                     boolean not null default true
);

insert into types.origem_acao_type (origem_acao,
                                    origem_acao_legivel_pt_br,
                                    origem_acao_legivel_es)
values ('API', 'API', 'API'),
       ('PROLOG', 'Prolog', 'Prolog'),
       ('INTERNO', 'Sistema Interno', 'Sistema Interno'),
       ('SUPORTE', 'Suporte', 'Soporte');

create table veiculo_edicao_historico
(
    codigo                       bigserial                   not null,
    cod_empresa_veiculo          bigint                      not null,
    cod_veiculo_edicao           bigint                      not null,
    cod_colaborador_edicao       bigint,
    data_hora_edicao_tz_aplicado timestamp without time zone not null,
    origem_edicao                text                        not null,
    total_edicoes_processo       smallint                    not null,
    informacoes_extras           text,
    placa                        text                        not null,
    identificador_frota          text,
    km                           bigint                      not null,
    status                       boolean                     not null,
    cod_diagrama_veiculo         bigint                      not null,
    cod_tipo_veiculo             bigint                      not null,
    cod_modelo_veiculo           bigint                      not null,
    constraint pk_veiculo_edicao_historico primary key (codigo),
    constraint fk_empresa foreign key (cod_empresa_veiculo) references empresa (codigo),
    constraint fk_veiculo foreign key (cod_veiculo_edicao) references veiculo_data (codigo),
    constraint fk_colaborador foreign key (cod_colaborador_edicao) references colaborador_data (codigo),
    constraint fk_origem_edicao foreign key (origem_edicao) references types.origem_acao_type (origem_acao),
    constraint fk_cod_tipo_veiculo foreign key (cod_tipo_veiculo) references veiculo_tipo (codigo),
    constraint fk_cod_modelo_veiculo foreign key (cod_modelo_veiculo) references modelo_veiculo (codigo),
    constraint fk_cod_diagrama_veiculo foreign key (cod_diagrama_veiculo) references veiculo_diagrama (codigo),
    constraint check_pelo_menos_uma_edicao check ( total_edicoes_processo > 0 ),
    constraint check_colaborador_existente_se_origem_edicao_prolog
        check ( origem_edicao <> 'PROLOG' or cod_colaborador_edicao is not null )
);

comment on column veiculo_edicao_historico.informacoes_extras is 'Qualquer informação a mais que se queira adicionar.
Em edições via suporte, pode conter o código do ticket.';

create index idx_veiculo_edicao_historico_cod_empresa_veiculo
    on veiculo_edicao_historico (cod_empresa_veiculo);
create index idx_veiculo_edicao_historico_cod_veiculo_edicao
    on veiculo_edicao_historico (cod_veiculo_edicao);
create index idx_veiculo_edicao_historico_cod_colaborador_edicao
    on veiculo_edicao_historico (cod_colaborador_edicao);

-- Altera tabela de veículo para sabermos de forma rápida se ele já foi editado.
alter table veiculo_data
    add column foi_editado boolean default false not null;

drop view estratificacao_os;
drop view veiculo;
create or replace view veiculo
as
select v.codigo,
       v.placa,
       v.identificador_frota,
       v.cod_unidade,
       v.cod_empresa,
       v.km,
       v.status_ativo,
       v.cod_diagrama,
       v.cod_tipo,
       v.cod_modelo,
       v.cod_eixos,
       v.data_hora_cadastro,
       v.cod_unidade_cadastro,
       v.foi_editado
from veiculo_data v
where v.deletado = false;

-- Não precisa atualizar o arquivo específico porque não mudou.
create or replace view estratificacao_os as
select cos.codigo                                                       as cod_os,
       realizador.nome                                                  as nome_realizador_checklist,
       c.placa_veiculo,
       c.km_veiculo                                                     as km,
       c.data_hora_realizacao_tz_aplicado                               as data_hora,
       c.tipo                                                           as tipo_checklist,
       cp.codigo                                                        as cod_pergunta,
       cp.codigo_contexto                                               as cod_contexto_pergunta,
       cp.ordem                                                         as ordem_pergunta,
       cp.pergunta,
       cp.single_choice,
       null :: unknown                                                  as url_imagem,
       cap.prioridade,
       case cap.prioridade
           when 'CRITICA' :: text
               then 1
           when 'ALTA' :: text
               then 2
           when 'BAIXA' :: text
               then 3
           else null :: integer
           end                                                          as prioridade_ordem,
       cap.codigo                                                       as cod_alternativa,
       cap.codigo_contexto                                              as cod_contexto_alternativa,
       cap.alternativa,
       prio.prazo,
       crn.resposta_outros,
       v.cod_tipo,
       cos.cod_unidade,
       cos.status                                                       as status_os,
       cos.cod_checklist,
       tz_unidade(cos.cod_unidade)                                      as time_zone_unidade,
       cosi.status_resolucao                                            as status_item,
       mecanico.nome                                                    as nome_mecanico,
       cosi.cpf_mecanico,
       cosi.tempo_realizacao,
       cosi.data_hora_conserto at time zone tz_unidade(cos.cod_unidade) as data_hora_conserto,
       cosi.data_hora_inicio_resolucao                                  as data_hora_inicio_resolucao_utc,
       cosi.data_hora_fim_resolucao                                     as data_hora_fim_resolucao_utc,
       cosi.km                                                          as km_fechamento,
       cosi.qt_apontamentos,
       cosi.feedback_conserto,
       cosi.codigo
from checklist_data c
         join colaborador realizador
              on realizador.cpf = c.cpf_colaborador
         join veiculo v
              on v.placa :: text = c.placa_veiculo :: text
         join checklist_ordem_servico cos
              on c.codigo = cos.cod_checklist
         join checklist_ordem_servico_itens cosi
              on cos.codigo = cosi.cod_os
                  and cos.cod_unidade = cosi.cod_unidade
         join checklist_perguntas cp
              on cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                  and cosi.cod_contexto_pergunta = cp.codigo_contexto
         join checklist_alternativa_pergunta cap
              on cap.cod_pergunta = cp.codigo
                  and cosi.cod_contexto_alternativa = cap.codigo_contexto
         join checklist_alternativa_prioridade prio
              on prio.prioridade :: text = cap.prioridade :: text
         join checklist_respostas_nok crn
              on crn.cod_checklist = c.codigo
                  and crn.cod_alternativa = cap.codigo
         left join colaborador mecanico on mecanico.cpf = cosi.cpf_mecanico;
--

drop function func_veiculo_atualiza_veiculo(f_placa text,
    f_novo_identificador_frota text,
    f_novo_km bigint,
    f_novo_cod_modelo bigint,
    f_novo_cod_tipo bigint);

-- select *
-- from func_veiculo_atualiza_veiculo(f_cod_veiculo => 41171,
--                                    f_nova_placa => 'DIA0020',
--                                    f_novo_identificador_frota => 'TESTE',
--                                    f_novo_km => 1004,
--                                    f_novo_cod_tipo => 975,
--                                    f_novo_cod_modelo => 1207,
--                                    f_novo_status => true,
--                                    f_cod_colaborador_edicao => 2272,
--                                    f_origem_edicao => 'SUPORTE',
--                                    f_data_hora_edicao => now(),
--                                    f_informacoes_extras_edicao => '#1234');
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
                cod_edicao_historico       bigint,
                total_edicoes              smallint,
                antiga_placa               text,
                antigo_identificador_frota text,
                antigo_km                  bigint,
                antigo_cod_diagrama        bigint,
                antigo_cod_tipo            bigint,
                antigo_cod_modelo          bigint,
                antigo_status              boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant bigint not null := (select v.cod_empresa
                                                     from veiculo v
                                                     where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant bigint not null := (select vt.cod_diagrama
                                                     from veiculo_tipo vt
                                                     where vt.codigo = f_novo_cod_tipo
                                                       and vt.cod_empresa = v_cod_empresa);
    v_cod_edicao_historico       bigint;
    v_total_edicoes              smallint;
    v_cod_unidade                bigint;
    v_antiga_placa               text;
    v_antigo_identificador_frota text;
    v_antigo_km                  bigint;
    v_antigo_cod_diagrama        bigint;
    v_antigo_cod_tipo            bigint;
    v_antigo_cod_modelo          bigint;
    v_antigo_status              boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_modelo,
        v_antigo_status
    from veiculo v
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
                                                  f_novo_cod_modelo,
                                                  f_novo_status)) - hstore((v_antigo_identificador_frota,
                                                                            v_antigo_km,
                                                                            v_antigo_cod_diagrama,
                                                                            v_antigo_cod_tipo,
                                                                            v_antigo_cod_modelo,
                                                                            v_antigo_status))));

    -- O update no veículo só será feito se algo de fato mudou. E algo só mudou se o total de edições for maior que 0.
    if (v_total_edicoes is not null and v_total_edicoes > 0)
    then
        v_cod_edicao_historico := func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                                          v_cod_unidade,
                                                                          f_cod_veiculo,
                                                                          v_antiga_placa,
                                                                          v_antigo_identificador_frota,
                                                                          v_antigo_km,
                                                                          v_antigo_cod_diagrama,
                                                                          v_antigo_cod_tipo,
                                                                          v_antigo_cod_modelo,
                                                                          v_antigo_status,
                                                                          v_total_edicoes,
                                                                          f_cod_colaborador_edicao,
                                                                          f_origem_edicao,
                                                                          f_data_hora_edicao,
                                                                          f_informacoes_extras_edicao);

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
        select v_cod_edicao_historico,
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

create extension if not exists hstore;

create or replace function func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint,
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
                                                                   f_informacoes_extras_edicao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_edicao_historico bigint;
begin
    insert into veiculo_edicao_historico (cod_empresa_veiculo,
                                          cod_veiculo_edicao,
                                          cod_colaborador_edicao,
                                          data_hora_edicao_tz_aplicado,
                                          origem_edicao,
                                          total_edicoes_processo,
                                          informacoes_extras,
                                          placa,
                                          identificador_frota,
                                          km,
                                          status,
                                          cod_diagrama_veiculo,
                                          cod_tipo_veiculo,
                                          cod_modelo_veiculo)
    values (f_cod_empresa_veiculo,
            f_cod_veiculo,
            f_cod_colaborador_edicao,
            f_data_hora_edicao at time zone tz_unidade(f_cod_unidade_veiculo),
            f_origem_edicao,
            f_total_edicoes,
            f_informacoes_extras_edicao,
            f_antiga_placa,
            f_antigo_identificador_frota,
            f_antigo_km,
            f_antigo_status,
            f_antigo_cod_diagrama,
            f_antigo_cod_tipo,
            f_antigo_cod_modelo)
    returning codigo into v_cod_edicao_historico;

    if v_cod_edicao_historico is null or v_cod_edicao_historico <= 0
    then
        perform throw_generic_error('Erro ao gerar o histórico de edição, tente novamente.');
    end if;

    return v_cod_edicao_historico;
end;
$$;

-- Adiciona cod_empresa ao retorno.
drop function func_veiculo_get_veiculo(f_cod_veiculo bigint);

create or replace function func_veiculo_get_veiculo(f_cod_veiculo bigint)
    returns table
            (
                codigo               bigint,
                placa                text,
                cod_unidade          bigint,
                cod_empresa          bigint,
                km                   bigint,
                status_ativo         boolean,
                cod_tipo             bigint,
                cod_modelo           bigint,
                cod_diagrama         bigint,
                identificador_frota  text,
                cod_regional_alocado bigint,
                modelo               text,
                nome_diagrama        text,
                dianteiro            bigint,
                traseiro             bigint,
                tipo                 text,
                marca                text,
                cod_marca            bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       v.cod_unidade::bigint                                   as cod_unidade,
       v.cod_empresa::bigint                                   as cod_empresa,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       r.codigo                                                as cod_regional_alocado,
       mv.nome                                                 as modelo,
       vd.nome                                                 as nome_diagrama,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       count(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
       vt.nome                                                 as tipo,
       mav.nome                                                as marca,
       mav.codigo                                              as cod_marca
from veiculo v
         join modelo_veiculo mv on mv.codigo = v.cod_modelo
         join veiculo_diagrama vd on vd.codigo = v.cod_diagrama
         join veiculo_diagrama_eixos vde on vde.cod_diagrama = vd.codigo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
         join marca_veiculo mav on mav.codigo = mv.cod_marca
         join unidade u on u.codigo = v.cod_unidade
         join regional r on u.cod_regional = r.codigo
where v.codigo = f_cod_veiculo
group by v.placa,
         v.codigo,
         v.codigo,
         v.placa,
         v.cod_unidade,
         v.cod_empresa,
         v.km,
         v.status_ativo,
         v.cod_tipo,
         v.cod_modelo,
         v.cod_diagrama,
         v.identificador_frota,
         r.codigo,
         mv.nome,
         vd.nome,
         vt.nome,
         mav.nome,
         mav.codigo
order by v.placa;
$$;
--

-- Altera function de update de veículo da integração.
-- Teste:
-- select *
-- from integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado := 215,
--                                                      f_placa_original_veiculo := 'DIA0020',
--                                                      f_novo_cod_unidade_alocado := 215,
--                                                      f_nova_placa_veiculo := 'DIA0020',
--                                                      f_novo_km_veiculo := 1001,
--                                                      f_novo_cod_modelo_veiculo := 121,
--                                                      f_novo_cod_tipo_veiculo := 975,
--                                                      f_data_hora_edicao_veiculo := now(),
--                                                      f_token_integracao := 'TOKEN_INTEGRACAO');
create or replace function
    integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
                                                    f_placa_original_veiculo text,
                                                    f_novo_cod_unidade_alocado bigint,
                                                    f_nova_placa_veiculo text,
                                                    f_novo_km_veiculo bigint,
                                                    f_novo_cod_modelo_veiculo bigint,
                                                    f_novo_cod_tipo_veiculo bigint,
                                                    f_data_hora_edicao_veiculo timestamp with time zone,
                                                    f_token_integracao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo constant bigint not null := (select u.cod_empresa
                                                       from public.unidade u
                                                       where u.codigo = f_cod_unidade_original_alocado);
    v_cod_veiculo_prolog           bigint;
    v_identificador_frota          text;
    v_status_ativo                 boolean;
begin
    -- Validamos se o usuário trocou a unidade alocada do veículo.
    if (f_cod_unidade_original_alocado <> f_novo_cod_unidade_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se o usuário trocou a placa do veículo.
    if (f_placa_original_veiculo <> f_nova_placa_veiculo)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] O ProLog não permite a edição da placa do veículo');
    end if;

    -- Validamos se a Unidade do veículo trocou.
    if ((select v.cod_unidade
         from public.veiculo_data v
         where v.placa = f_placa_original_veiculo) <> f_cod_unidade_original_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se a Unidade pertence a mesma empresa do token.
    if ((select u.cod_empresa from public.unidade u where u.codigo = f_novo_cod_unidade_alocado)
        not in (select ti.cod_empresa
                from integracao.token_integracao ti
                where ti.token_integracao = f_token_integracao))
    then
        perform public.throw_generic_error(
                format(
                        '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
                        f_token_integracao,
                        f_novo_cod_unidade_alocado));
    end if;

    -- Validamos se a placa já existe no ProLog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_nova_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_nova_placa_veiculo));
    end if;

    -- Validamos se o modelo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.modelo_veiculo mv
                          where mv.cod_empresa = v_cod_empresa_veiculo
                            and mv.codigo = f_novo_cod_modelo_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    -- Validamos se o tipo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.veiculo_tipo vt
                          where vt.codigo = f_novo_cod_tipo_veiculo
                            and vt.cod_empresa = v_cod_empresa_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    select vd.codigo,
           vd.identificador_frota,
           vd.status_ativo
    into strict
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_status_ativo
    from veiculo_data vd
    where vd.placa = f_placa_original_veiculo
      and vd.cod_unidade = f_cod_unidade_original_alocado;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_nova_placa_veiculo,
                                          v_identificador_frota,
                                          f_novo_km_veiculo,
                                          f_novo_cod_tipo_veiculo,
                                          f_novo_cod_modelo_veiculo,
                                          v_status_ativo,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_original_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;
--

-- Altera function de update de status de veículo da integração.
-- select *
-- from integracao.func_veiculo_ativa_desativa_veiculo_prolog(f_placa_veiculo => 'DIA0020',
--                                                            f_ativar_desativar_veiculo => true,
--                                                            f_data_hora_edicao_veiculo => now(),
--                                                            f_token_integracao => 'TOKEN_INTEGRACAO');
create or replace function
    integracao.func_veiculo_ativa_desativa_veiculo_prolog(f_placa_veiculo text,
                                                          f_ativar_desativar_veiculo boolean,
                                                          f_data_hora_edicao_veiculo timestamp with time zone,
                                                          f_token_integracao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo     bigint;
    v_cod_unidade_veiculo     bigint;
    v_cod_veiculo_prolog      bigint;
    v_identificador_frota     text;
    v_novo_km_veiculo         bigint;
    v_novo_cod_tipo_veiculo   bigint;
    v_novo_cod_modelo_veiculo bigint;
begin
    -- Não usamos 'strict' propositalmente pois não queremos quebrar no select. Deixamos as próprias validações da
    -- function verificarem e quebrarem.
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.km,
           vd.cod_tipo,
           vd.cod_modelo
    into
        v_cod_empresa_veiculo,
        v_cod_unidade_veiculo,
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_novo_km_veiculo,
        v_novo_cod_tipo_veiculo,
        v_novo_cod_modelo_veiculo
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Validamos se a unidade pertence a mesma empresa do token.
    perform integracao.func_garante_token_empresa(
            v_cod_empresa_veiculo,
            f_token_integracao,
            format('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                   verificar vínculos', f_token_integracao, v_cod_unidade_veiculo));

    -- Validamos se a placa já existe no Prolog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_placa_veiculo));
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_identificador_frota,
                                          v_novo_km_veiculo,
                                          v_novo_cod_tipo_veiculo,
                                          v_novo_cod_modelo_veiculo,
                                          f_ativar_desativar_veiculo,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', f_placa_veiculo));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;
--

-- 2020-09-11 -> Altera function para utilizar function de update de veículo padrão (luiz_fp - PL-3097).
drop function integracao.func_veiculo_sobrescreve_veiculo_cadastrado(text, bigint, bigint, bigint, bigint, bigint);

-- select *
-- from integracao.func_veiculo_sobrescreve_veiculo_cadastrado(f_placa_veiculo => 'DIA0020',
--                                                             f_cod_unidade_veiculo => 215,
--                                                             f_km_atual_veiculo => 1212,
--                                                             f_cod_tipo_veiculo => 975,
--                                                             f_cod_modelo_veiculo => 121);
create or replace function
    integracao.func_veiculo_sobrescreve_veiculo_cadastrado(f_placa_veiculo text,
                                                           f_cod_unidade_veiculo bigint,
                                                           f_km_atual_veiculo bigint,
                                                           f_cod_tipo_veiculo bigint,
                                                           f_cod_modelo_veiculo bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo       bigint;
    v_cod_unidade_atual_veiculo bigint;
    v_cod_veiculo_prolog        bigint;
    v_novo_identificador_frota  text;
    v_novo_status_veiculo       boolean;
begin
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.status_ativo
    into strict
        v_cod_empresa_veiculo,
        v_cod_unidade_atual_veiculo,
        v_cod_veiculo_prolog,
        v_novo_identificador_frota,
        v_novo_status_veiculo
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Devemos tratar os serviços abertos para o veículo (setar fechado_integracao), apenas se a unidade mudar.
    if (v_cod_unidade_atual_veiculo <> f_cod_unidade_veiculo)
    then
        perform integracao.func_veiculo_deleta_servicos_abertos_placa(f_placa_veiculo,
                                                                      f_cod_unidade_veiculo);

        -- A function que atualiza veículo não atualiza o código da unidade, pois essa coluna não deve mesmo mudar em
        -- um update convencional, apenas através de uma transferência entre unidades, que é um outro processo.
        -- Como a integração precisa desse comportamento, precisamos fazer um novo update dessa coluna caso os códigos
        -- de unidade tenha sido alterados.
        update veiculo
        set cod_unidade = f_cod_unidade_veiculo
        where codigo = v_cod_veiculo_prolog
          and cod_unidade = v_cod_unidade_atual_veiculo;
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_novo_identificador_frota,
                                          f_km_atual_veiculo,
                                          f_cod_tipo_veiculo,
                                          f_cod_modelo_veiculo,
                                          v_novo_status_veiculo,
                                          null,
                                          'API',
                                          now(),
                                          null);
end;
$$;
--

-- Alterado para não utilizar o código do diagrama no update.
-- Não foi alterado o case (para lower) desta function pois a alteração no escopo foi pequena no arquivo específico e
-- não quis que ele constasse inteiro como alterado.
-- select *
-- from integracao.func_veiculo_insere_veiculo_prolog(f_cod_unidade_veiculo_alocado => 215,
--                                                    f_placa_veiculo_cadastrado => 'DIA0020',
--                                                    f_km_atual_veiculo_cadastrado => 1000,
--                                                    f_cod_modelo_veiculo_cadastrado => 1207,
--                                                    f_cod_tipo_veiculo_cadastrado => 975,
--                                                    f_data_hora_veiculo_cadastro => now(),
--                                                    f_token_integracao => 'TOKEN_INTEGRACAO');
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                  F_PLACA_VEICULO_CADASTRADO TEXT,
                                                  F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                  F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                  F_TOKEN_INTEGRACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_EMPRESA_VEICULO       CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                                   FROM PUBLIC.UNIDADE U
                                                   WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO);
    DEVE_SOBRESCREVER_VEICULO CONSTANT BOOLEAN := (SELECT *
                                                   FROM INTEGRACAO.FUNC_EMPRESA_GET_CONFIG_SOBRESCREVE_VEICULOS(
                                                           COD_EMPRESA_VEICULO));
    VEICULO_ESTA_NO_PROLOG    CONSTANT BOOLEAN := (SELECT EXISTS(SELECT V.CODIGO
                                                                 FROM PUBLIC.VEICULO_DATA V
                                                                 WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO));
    STATUS_ATIVO_VEICULO      CONSTANT BOOLEAN := TRUE;
    COD_VEICULO_PROLOG                 BIGINT;
    F_QTD_ROWS_ALTERADAS               BIGINT;
BEGIN
    -- Validamos se a Unidade pertence a mesma empresa do token.
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(
            COD_EMPRESA_VEICULO,
            F_TOKEN_INTEGRACAO,
            FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                     confira se está usando o token correto', F_TOKEN_INTEGRACAO, F_COD_UNIDADE_VEICULO_ALOCADO));

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO
                            AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM PUBLIC.VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                            AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    END IF;

    -- Validamos se a placa já existe no ProLog.
    IF (VEICULO_ESTA_NO_PROLOG AND NOT DEVE_SOBRESCREVER_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                       F_PLACA_VEICULO_CADASTRADO));
    END IF;

    IF (VEICULO_ESTA_NO_PROLOG AND DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Buscamos o código do veículo que será sobrescrito.
        SELECT V.CODIGO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO_CADASTRADO
          AND V.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO COD_VEICULO_PROLOG;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        PERFORM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO_BY_PLACA(F_PLACA_VEICULO_CADASTRADO);

        -- Sebrescrevemos os dados do veículo.
        PERFORM INTEGRACAO.FUNC_VEICULO_SOBRESCREVE_VEICULO_CADASTRADO(
                F_PLACA_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                F_COD_MODELO_VEICULO_CADASTRADO);

    ELSE
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                (SELECT VT.COD_DIAGRAMA
                 FROM PUBLIC.VEICULO_TIPO VT
                 WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
                   AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO),
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO)
        RETURNING CODIGO INTO COD_VEICULO_PROLOG;
    END IF;

    IF (DEVE_SOBRESCREVER_VEICULO)
    THEN
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_VEICULO_CADASTRO_PROLOG = COD_VEICULO_PROLOG,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_VEICULO_ALOCADO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_VEICULO_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(COD_EMPRESA_CADASTRO,
                                                  COD_UNIDADE_CADASTRO,
                                                  COD_VEICULO_CADASTRO_PROLOG,
                                                  PLACA_VEICULO_CADASTRO,
                                                  DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                COD_VEICULO_PROLOG,
                F_PLACA_VEICULO_CADASTRADO,
                F_DATA_HORA_VEICULO_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
    END IF;

    RETURN COD_VEICULO_PROLOG;
END;
$$;
--

-- 2020-09-11 -> Corrige verificação do código da empresa (luiz_fp - PL-3097).
create or replace function integracao.func_garante_token_empresa(f_cod_empresa bigint,
                                                                 f_token_integracao text,
                                                                 f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    error_message text :=
        f_if(f_error_message is null,
             format('Token não autorizado para a empresa %s', f_cod_empresa),
             f_error_message);
begin
    if (f_cod_empresa is null or f_cod_empresa not in (select ti.cod_empresa
                                                       from integracao.token_integracao ti
                                                       where ti.token_integracao = f_token_integracao))
    then
        perform throw_generic_error(error_message);
    end if;
end;
$$;

-- PL-3133.

-- Altera as FKs das tabelas que usam placa para serem update cascade.
-- A tabela VEICULO_PNEU não é alterada pois a FK de placa é composta também p

alter table afericao_data
    drop constraint fk_afericao_veiculo,
    add constraint fk_afericao_veiculo
        foreign key (placa_veiculo) references veiculo_data (placa) on update cascade;

alter table checklist_data
    drop constraint fk_checklist_placa,
    add constraint fk_checklist_placa
        foreign key (placa_veiculo) references veiculo_data (placa) on update cascade;

alter table movimentacao_origem
    drop constraint fk_movimentacao_origem_veiculo,
    add constraint fk_movimentacao_origem_veiculo
        foreign key (placa) references veiculo_data (placa) on update cascade;

alter table movimentacao_destino
    drop constraint fk_movimentacao_destino_veiculo,
    add constraint fk_movimentacao_destino_veiculo
        foreign key (placa) references veiculo_data (placa) on update cascade;
--

-- Essa function parou de cadastrar um novo veículo e alterar as referências à placa na mão e passou a contar com o
-- cascade.
-- select *
-- from suporte.func_veiculo_altera_placa(f_cod_unidade_veiculo => 215,
--                                        f_cod_veiculo => 41171,
--                                        f_placa_antiga => 'DIA0020',
--                                        f_placa_nova => 'DIA0022',
--                                        f_informacoes_extras_suporte => '#1234');
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

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    f_cod_unidade_veiculo,
                                                    f_cod_veiculo,
                                                    f_placa_antiga,
                                                    v_identificador_frota,
                                                    v_km,
                                                    v_cod_diagrama,
                                                    v_cod_tipo,
                                                    v_cod_modelo,
                                                    v_status,
                                                    1::smallint, -- Apenas a placa mudou.
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte);

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


-- Altera para ter parâmetro opcional de considerar deletados.
drop function func_garante_veiculo_existe(f_cod_unidade_veiculo bigint, f_placa_veiculo text, f_error_message text);

create or replace function func_garante_veiculo_existe(f_cod_unidade_veiculo bigint,
                                                       f_placa_veiculo text,
                                                       f_considerar_deletados boolean default true,
                                                       f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message text :=
        f_if(f_error_message is null,
             format('Não foi possível encontrar o veículo com estes parâmetros: Unidade %s, Placa %s',
                    f_cod_unidade_veiculo, f_placa_veiculo),
             f_error_message);
begin
    if not exists(select vd.codigo
                  from veiculo_data vd
                  where vd.placa = f_placa_veiculo
                    and vd.cod_unidade = f_cod_unidade_veiculo
                    and f_if(f_considerar_deletados, true, vd.deletado = false))
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;

drop function suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo text,
    f_cod_veiculo_tipo_novo bigint,
    f_cod_unidade bigint);

-- select *
-- from suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo => 'PRO0020',
--                                               f_cod_veiculo_tipo_novo => 64,
--                                               f_cod_unidade => 5,
--                                               f_informacoes_extras_suporte => '#1234');
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

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    f_cod_unidade,
                                                    v_cod_veiculo,
                                                    f_placa_veiculo,
                                                    v_identificador_frota_antigo,
                                                    v_km_antigo,
                                                    v_cod_diagrama_antigo,
                                                    v_cod_tipo_antigo,
                                                    v_cod_modelo,
                                                    v_status_antigo,
                                                    (f_if(v_cod_tipo_antigo <> f_cod_veiculo_tipo_novo, 1, 0)
                                                        +
                                                     f_if(v_cod_diagrama_antigo <> v_cod_diagrama_novo, 1, 0))::smallint,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte);

    -- Mensagem de sucesso.
    select 'Tipo do veículo alterado! ' ||
           'Placa: ' || f_placa_veiculo ||
           ', Código da unidade: ' || f_cod_unidade ||
           ', Tipo: ' || (select vt.nome from veiculo_tipo vt where vt.codigo = f_cod_veiculo_tipo_novo) ||
           ', Código do tipo: ' || f_cod_veiculo_tipo_novo || '.'
    into aviso_tipo_veiculo_alterado;
end;
$$;

-- PL-3098.
create view types.origem_acao
    (origem_acao, origem_acao_legivel) as
select types.origem_acao_type.origem_acao,
       f_if((select current_setting('lc_messages'::text) = 'es_es.UTF-8'::text),
            types.origem_acao_type.origem_acao_legivel_es,
            types.origem_acao_type.origem_acao_legivel_pt_br) as origem_acao_legivel
from types.origem_acao_type
where types.origem_acao_type.ativo = true;

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
                modelo_veiculo            text
            )
    language plpgsql
as
$$
begin
    return query
        select *
        from (select veh.codigo                       as codigo_historico,
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
                     mv.nome::text                    as modelo_veiculo
              from veiculo_edicao_historico veh
                       inner join types.origem_acao oa on oa.origem_acao = veh.origem_edicao
                       inner join veiculo_diagrama vd on vd.codigo = veh.cod_diagrama_veiculo
                       inner join veiculo_tipo vt on vt.codigo = veh.cod_tipo_veiculo
                       inner join modelo_veiculo mv on mv.codigo = veh.cod_modelo_veiculo
                       left join colaborador c on c.codigo = veh.cod_colaborador_edicao
              where veh.cod_veiculo_edicao = f_cod_veiculo
                and veh.cod_empresa_veiculo = f_cod_empresa
              union all
              select null                  as codigo_historico,
                     v.cod_empresa         as codigo_empresa_veiculo,
                     v.codigo              as codigo_veiculo_edicao,
                     null                  as codigo_colaborador_edicao,
                     null                  as nome_colaborador_edicao,
                     null                  as data_hora_edicao,
                     null                  as origem_edicao,
                     null                  as origem_edicao_legivel,
                     null                  as total_edicoes,
                     null                  as informacoes_extras,
                     v.placa               as placa,
                     v.identificador_frota as identificador_frota,
                     v.km                  as km_veiculo,
                     v.status_ativo        as status,
                     vd.nome::text         as diagrama_veiculo,
                     vt.nome::text         as tipo_veiculo,
                     mv.nome::text         as modelo_veiculo
              from veiculo v
                       inner join veiculo_diagrama vd on vd.codigo = v.cod_diagrama
                       inner join veiculo_tipo vt on vt.codigo = v.cod_tipo
                       inner join modelo_veiculo mv on mv.codigo = v.cod_modelo
              where v.codigo = f_cod_veiculo
                and v.cod_empresa = f_cod_empresa) as historico_completo
        -- A lógica no java depende que o valor nulo venha primeiro, que no caso é o estado atual do veículo.
        order by historico_completo.codigo_historico desc nulls first;
end;
$$;

-- Update para tornar todos os identificador frota em branco igual a nulo. Update feito na veiculo_data pra abranger
-- os veiculos inativos tbm.
update veiculo_data
set identificador_frota = null
where trim(identificador_frota) = '';

--------------------------------------------------------------------------------------------------------

drop function if exists func_veiculo_listagem_historico_edicoes(f_cod_empresa bigint, f_cod_veiculo bigint);
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
        select *
        from (select veh.codigo                       as codigo_historico,
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
              where veh.cod_veiculo_edicao = f_cod_veiculo
                and veh.cod_empresa_veiculo = f_cod_empresa
              union all
              select null                  as codigo_historico,
                     v.cod_empresa         as codigo_empresa_veiculo,
                     v.codigo              as codigo_veiculo_edicao,
                     null                  as codigo_colaborador_edicao,
                     null                  as nome_colaborador_edicao,
                     null                  as data_hora_edicao,
                     null                  as origem_edicao,
                     null                  as origem_edicao_legivel,
                     null                  as total_edicoes,
                     null                  as informacoes_extras,
                     v.placa               as placa,
                     v.identificador_frota as identificador_frota,
                     v.km                  as km_veiculo,
                     v.status_ativo        as status,
                     vd.nome::text         as diagrama_veiculo,
                     vt.nome::text         as tipo_veiculo,
                     mv.nome::text         as modelo_veiculo,
                     mav.nome::text        as marca_veiculo
              from veiculo v
                       inner join veiculo_diagrama vd on vd.codigo = v.cod_diagrama
                       inner join veiculo_tipo vt on vt.codigo = v.cod_tipo
                       inner join modelo_veiculo mv on mv.codigo = v.cod_modelo
                       inner join marca_veiculo mav on mav.codigo = mv.cod_marca
              where v.codigo = f_cod_veiculo
                and v.cod_empresa = f_cod_empresa) as historico_completo
             -- A lógica no java depende que o valor nulo venha primeiro, que no caso é o estado atual do veículo.
        order by historico_completo.codigo_historico desc nulls first;
end;
$$;