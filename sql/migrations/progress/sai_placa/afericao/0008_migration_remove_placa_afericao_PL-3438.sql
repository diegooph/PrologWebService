drop view if exists view_analise_pneus;
drop view if exists view_pneu_analise_vida_atual;
drop view if exists view_pneu_analise_vidas;
drop view if exists view_pneu_km_rodado_total;
drop view if exists view_pneu_km_rodado_vida;
drop view if exists afericao;

create or replace view afericao as
select ad.codigo,
       ad.data_hora,
       ad.cod_veiculo,
       ad.cpf_aferidor,
       ad.km_veiculo,
       ad.tempo_realizacao,
       ad.tipo_medicao_coletada,
       ad.cod_unidade,
       ad.tipo_processo_coleta,
       ad.forma_coleta_dados,
       ad.cod_diagrama
from afericao_data ad
where ad.deletado = false;

-- Corrige Inconsistência: Veículos deletados com aferições não deletadas.
-- Deleta serviço de aferição
update afericao_manutencao_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and cod_afericao in (select a.codigo
                       from afericao a
                       where a.cod_veiculo not in (select v.codigo from veiculo v));

-- Deleta valores de aferição.
update afericao_valores_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and cod_afericao in (select a.codigo
                       from afericao a
                       where a.cod_veiculo not in (select v.codigo from veiculo v));

-- Deleta aferição
update afericao_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and codigo in (select a.codigo
                 from afericao a
                 where a.cod_veiculo not in (select v.codigo from veiculo v));

drop function func_afericao_get_pneus_disponiveis_afericao_avulsa(f_cod_unidade bigint);
create or replace function func_afericao_get_pneus_disponiveis_afericao_avulsa(f_cod_unidade bigint)
    returns table
            (
                codigo                                bigint,
                codigo_cliente                        text,
                dot                                   text,
                valor                                 real,
                cod_unidade_alocado                   bigint,
                nome_unidade_alocado                  text,
                cod_regional_alocado                  bigint,
                nome_regional_alocado                 text,
                pneu_novo_nunca_rodado                boolean,
                cod_marca_pneu                        bigint,
                nome_marca_pneu                       text,
                cod_modelo_pneu                       bigint,
                nome_modelo_pneu                      text,
                qt_sulcos_modelo_pneu                 smallint,
                cod_marca_banda                       bigint,
                nome_marca_banda                      text,
                altura_sulcos_modelo_pneu             real,
                cod_modelo_banda                      bigint,
                nome_modelo_banda                     text,
                qt_sulcos_modelo_banda                smallint,
                altura_sulcos_modelo_banda            real,
                valor_banda                           real,
                altura                                integer,
                largura                               integer,
                aro                                   real,
                cod_dimensao                          bigint,
                altura_sulco_central_interno          real,
                altura_sulco_central_externo          real,
                altura_sulco_interno                  real,
                altura_sulco_externo                  real,
                pressao_recomendada                   real,
                pressao_atual                         real,
                status                                text,
                vida_atual                            integer,
                vida_total                            integer,
                posicao_pneu                          integer,
                posicao_aplicado_cliente              text,
                cod_veiculo_aplicado                  bigint,
                placa_aplicado                        text,
                identificador_frota                   text,
                ja_foi_aferido                        boolean,
                cod_ultima_afericao                   bigint,
                data_hora_ultima_afericao             timestamp without time zone,
                placa_veiculo_ultima_afericao         text,
                identificador_frota_ultima_afericao   text,
                tipo_medicao_coletada_ultima_afericao text,
                tipo_processo_coleta_ultima_afericao  text,
                nome_colaborador_ultima_afericao      text
            )
    language sql
as
$$
with afericoes as (
    select inner_table.codigo           as cod_afericao,
           inner_table.cod_pneu         as cod_pneu,
           inner_table.data_hora,
           inner_table.cod_veiculo,
           inner_table.tipo_medicao_coletada,
           inner_table.tipo_processo_coleta,
           inner_table.nome_colaborador as nome_colaborador,
           case
               when inner_table.nome_colaborador is not null
                   then true
               else false end           as ja_foi_aferido
    from (select a.codigo,
                 av.cod_pneu,
                 a.data_hora,
                 a.cod_veiculo,
                 a.tipo_medicao_coletada,
                 a.tipo_processo_coleta,
                 c.nome                      as nome_colaborador,
                 MAX(a.codigo)
                 over (
                     partition by cod_pneu ) as max_cod_afericao
          from pneu p
                   left join afericao_valores av on p.codigo = av.cod_pneu
                   left join afericao a on av.cod_afericao = a.codigo
                   left join colaborador c on a.cpf_aferidor = c.cpf
          where p.cod_unidade = f_cod_unidade
            and p.status = 'ESTOQUE') as inner_table
    where codigo = inner_table.max_cod_afericao
)
select f.*,
       a.ja_foi_aferido                as ja_foi_aferido,
       a.cod_afericao                  as cod_ultima_afericao,
       a.data_hora at time zone tz_unidade(f_cod_unidade)
                                       as data_hora_ultima_afericao,
       v.placa :: text                 as placa_veiculo_ultima_afericao,
       v.identificador_frota :: text   as identificador_frota_ultima_afericao,
       a.tipo_medicao_coletada :: text as tipo_medicao_coletada_ultima_afericao,
       a.tipo_processo_coleta :: text  as tipo_processo_coleta_ultima_afericao,
       a.nome_colaborador :: text      as nome_colaborador_ultima_afericao
from func_pneu_get_listagem_pneus_by_status(array [f_cod_unidade], 'ESTOQUE') as f
         left join afericoes a on f.codigo = a.cod_pneu
         left join veiculo v on a.cod_veiculo = v.codigo;
$$;

drop function func_afericao_get_pneu_para_afericao_avulsa(f_cod_pneu bigint, f_tz_unidade text);
create or replace function func_afericao_get_pneu_para_afericao_avulsa(f_cod_pneu bigint, f_tz_unidade text)
    returns table
            (
                codigo                                bigint,
                codigo_cliente                        text,
                dot                                   text,
                valor                                 real,
                cod_unidade_alocado                   bigint,
                cod_regional_alocado                  bigint,
                pneu_novo_nunca_rodado                boolean,
                cod_marca_pneu                        bigint,
                nome_marca_pneu                       text,
                cod_modelo_pneu                       bigint,
                nome_modelo_pneu                      text,
                qt_sulcos_modelo_pneu                 smallint,
                cod_marca_banda                       bigint,
                nome_marca_banda                      text,
                altura_sulcos_modelo_pneu             real,
                cod_modelo_banda                      bigint,
                nome_modelo_banda                     text,
                qt_sulcos_modelo_banda                smallint,
                altura_sulcos_modelo_banda            real,
                valor_banda                           real,
                altura                                integer,
                largura                               integer,
                aro                                   real,
                cod_dimensao                          bigint,
                altura_sulco_central_interno          real,
                altura_sulco_central_externo          real,
                altura_sulco_interno                  real,
                altura_sulco_externo                  real,
                pressao_recomendada                   real,
                pressao_atual                         real,
                status                                text,
                vida_atual                            integer,
                vida_total                            integer,
                posicao_pneu                          integer,
                posicao_aplicado_cliente              text,
                cod_veiculo_aplicado                  bigint,
                placa_aplicado                        text,
                identificador_frota                   text,
                ja_foi_aferido                        boolean,
                cod_ultima_afericao                   bigint,
                data_hora_ultima_afericao             timestamp without time zone,
                placa_veiculo_ultima_afericao         text,
                identificador_frota_ultima_afericao   text,
                tipo_medicao_coletada_ultima_afericao text,
                tipo_processo_coleta_ultima_afericao  text,
                nome_colaborador_ultima_afericao      text
            )
    language sql
as
$$
with afericoes as (
    select inner_table.codigo           as cod_afericao,
           inner_table.cod_pneu         as cod_pneu,
           inner_table.data_hora,
           inner_table.cod_veiculo,
           inner_table.tipo_medicao_coletada,
           inner_table.tipo_processo_coleta,
           inner_table.nome_colaborador as nome_colaborador,
           case
               when inner_table.nome_colaborador is not null
                   then true
               else false end           as ja_foi_aferido
    from (select a.codigo,
                 av.cod_pneu,
                 a.data_hora,
                 a.cod_veiculo,
                 a.tipo_medicao_coletada,
                 a.tipo_processo_coleta,
                 c.nome                      as nome_colaborador,
                 MAX(a.codigo)
                 over (
                     partition by cod_pneu ) as max_cod_afericao
          from pneu p
                   left join afericao_valores av on p.codigo = av.cod_pneu
                   left join afericao a on av.cod_afericao = a.codigo
                   left join colaborador c on a.cpf_aferidor = c.cpf
          where p.status = 'ESTOQUE'
            and p.codigo = f_cod_pneu) as inner_table
    where codigo = inner_table.max_cod_afericao
)

select func.*,
       a.ja_foi_aferido                      as ja_foi_aferido,
       a.cod_afericao                        as cod_ultima_afericao,
       a.data_hora at time zone f_tz_unidade as data_hora_ultima_afericao,
       v.placa :: text                       as placa_veiculo_ultima_afericao,
       v.identificador_frota                 as identificador_frota_ultima_afericao,
       a.tipo_medicao_coletada :: text       as tipo_medicao_coletada_ultima_afericao,
       a.tipo_processo_coleta :: text        as tipo_processo_coleta_ultima_afericao,
       a.nome_colaborador :: text            as nome_colaborador_ultima_afericao
from func_pneu_get_pneu_by_codigo(f_cod_pneu) as func
         left join afericoes a on func.codigo = a.cod_pneu
         left join veiculo v on v.codigo = a.cod_veiculo
where func.codigo = f_cod_pneu;
$$;

drop function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint, f_vida_pneu integer);
create or replace function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint,
                                                                       f_vida_pneu integer)
    returns numeric
    language sql
as
$$
with movimentacoes_vida_pneu as (
    select mp.data_hora    as data_hora_movimentacao,
           mo.tipo_origem  as tipo_origem,
           md.tipo_destino as tipo_destino,
           v_destino.placa as placa_destino,
           md.km_veiculo   as km_veiculo_destino,
           v_origem.placa  as placa_origem,
           mo.km_veiculo   as km_veiculo_origem
    from movimentacao_processo mp
             join movimentacao m on mp.codigo = m.cod_movimentacao_processo
             join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
             left join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             join movimentacao_destino md on m.codigo = md.cod_movimentacao
             left join veiculo v_destino on v_destino.codigo = md.cod_veiculo
    where (mo.tipo_origem = 'EM_USO' or md.tipo_destino = 'EM_USO')
      and m.cod_pneu = f_cod_pneu
      and m.vida = f_vida_pneu
),

     afericoes_vida_pneu as (
         select a.data_hora  as data_hora_afericao,
                v.placa      as placa_afericao,
                a.km_veiculo as km_veiculo_afericao
         from afericao a
                  join afericao_valores av on av.cod_afericao = a.codigo
                  join veiculo v on a.cod_veiculo = v.codigo
         where a.tipo_processo_coleta = 'PLACA'
           and av.cod_pneu = f_cod_pneu
           and av.vida_momento_afericao = f_vida_pneu
     ),

     kms_primeira_aplicacao_ate_primeira_afericao as (
         select sum((select avp.km_veiculo_afericao
                     from afericoes_vida_pneu avp
                     where avp.placa_afericao = pvp.placa_destino
                       and avp.data_hora_afericao > pvp.data_hora_movimentacao
                     order by avp.data_hora_afericao
                     limit 1) - pvp.km_veiculo_destino) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu de qualquer origem e foi aplicado no veículo.
         where pvp.tipo_origem <> 'EM_USO'
           and pvp.tipo_destino = 'EM_USO'
     ),

     kms_ultima_afericao_ate_remocao as (
         select sum(pvp.km_veiculo_origem - (select avp.km_veiculo_afericao
                                             from afericoes_vida_pneu avp
                                             where avp.placa_afericao = pvp.placa_origem
                                               and avp.data_hora_afericao < pvp.data_hora_movimentacao
                                             order by avp.data_hora_afericao desc
                                             limit 1)) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu do veículo e foi movido para qualquer outro destino que não veículo.
         where pvp.tipo_origem = 'EM_USO'
           and pvp.tipo_destino <> 'EM_USO'
     )

select coalesce((select aplicacao.km_percorrido
                 from kms_primeira_aplicacao_ate_primeira_afericao aplicacao), 0)
           +
       coalesce((select remocao.km_percorrido
                 from kms_ultima_afericao_ate_remocao remocao), 0) as km_total_aplicacao_remocao;
$$;

drop function func_veiculo_transferencia_deleta_servicos_pneu(f_cod_veiculo bigint,
    f_cod_pneu bigint,
    f_cod_transferencia_veiculo_informacoes bigint,
    f_data_hora_realizacao_transferencia timestamp with time zone);
create or replace function func_veiculo_transferencia_deleta_servicos_pneu(f_cod_veiculo bigint,
                                                                           f_cod_pneu bigint,
                                                                           f_cod_transferencia_veiculo_informacoes bigint,
                                                                           f_data_hora_realizacao_transferencia timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    v_qtd_inserts bigint;
    v_qtd_updates bigint;
begin
    insert into afericao_manutencao_servico_deletado_transferencia (cod_servico,
                                                                    cod_veiculo_transferencia_informacoes)
    select am.codigo,
           f_cod_transferencia_veiculo_informacoes
           -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    from afericao_manutencao am
             join afericao a on a.codigo = am.cod_afericao
    where a.cod_veiculo = f_cod_veiculo
      and am.cod_pneu = f_cod_pneu
      and am.data_hora_resolucao is null
      and (am.fechado_automaticamente_movimentacao = false or am.fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_inserts = row_count;

    update afericao_manutencao_data
    set deletado            = true,
        pg_username_delecao = SESSION_USER,
        data_hora_deletado  = f_data_hora_realizacao_transferencia
    where cod_pneu = f_cod_pneu
      and deletado = false
      and data_hora_resolucao is null
      and (fechado_automaticamente_movimentacao = false or fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_updates = row_count;

    -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
    -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
    -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo
    -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
    -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
    -- Porém, podemos ter uma inconsistência no BD.
    -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
    -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
    if v_qtd_inserts <> v_qtd_updates
    then
        raise exception 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
    end if;
end;
$$;

drop function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
    f_placa varchar,
    f_motivo_delecao text);
create or replace function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
                                                               f_placa varchar(255),
                                                               f_motivo_delecao text,
                                                               out dependencias_deletadas text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_codigo_loop                   bigint;
    v_lista_cod_afericao_placa      bigint[];
    v_lista_cod_check_placa         bigint[];
    v_lista_cod_prolog_deletado_cos bigint[];
    v_nome_empresa                  varchar(255) := (select e.nome
                                                     from empresa e
                                                     where e.codigo =
                                                           (select u.cod_empresa
                                                            from unidade u
                                                            where u.codigo = f_cod_unidade));
    v_nome_unidade                  varchar(255) := (select u.nome
                                                     from unidade u
                                                     where u.codigo = f_cod_unidade);
    v_cod_veiculo                   bigint       := (select codigo
                                                     from veiculo v
                                                     where v.placa = f_placa
                                                       and v.cod_unidade = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veículo possui pneus aplicados.
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se placa possui aferição. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select a.codigo from afericao_data a where a.cod_veiculo = v_cod_veiculo)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Deletamos aferição em afericao_manutencao_data, caso não esteja deletada.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição em afericao_valores_data, caso não esteja deletada.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição, caso não esteja deletada.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- Verifica se placa possui checklist. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select c.placa_veiculo from checklist_data c where c.deletado = false and c.placa_veiculo = f_placa)
    then
        -- Busca todos os códigos de checklists da placa.
        select array_agg(c.codigo)
        from checklist_data c
        where c.deletado = false
          and c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- Deleta todos os checklists da placa. Usamos deleção lógica em conjunto com uma tabela de deleção específica.
        insert into checklist_delecao (cod_checklist,
                                       cod_colaborador,
                                       data_hora,
                                       acao_executada,
                                       origem_delecao,
                                       observacao,
                                       pg_username_delecao)
        select unnest(v_lista_cod_check_placa),
               null,
               now(),
               'DELETADO',
               'SUPORTE',
               f_motivo_delecao,
               session_user;

        update checklist_data set deletado = true where codigo = any (v_lista_cod_check_placa);

        -- Usamos, obrigatoriamente, a view checklist_ordem_servico para
        -- evitar de tentar deletar OSs que estão deletadas.
        if exists(select cos.codigo
                  from checklist_ordem_servico cos
                  where cos.cod_checklist = any (v_lista_cod_check_placa))
        then
            -- Deleta ordens de serviços dos checklists.
            update checklist_ordem_servico_data
            set deletado            = true,
                data_hora_deletado  = now(),
                pg_username_delecao = session_user,
                motivo_delecao      = f_motivo_delecao
            where deletado = false
              and cod_checklist = any (v_lista_cod_check_placa);

            -- Busca os codigo Prolog deletados nas Ordens de Serviços.
            select array_agg(codigo_prolog)
            from checklist_ordem_servico_data
            where cod_checklist = any (v_lista_cod_check_placa)
              and deletado is true
            into v_lista_cod_prolog_deletado_cos;

            -- Para cada código prolog deletado em cos, deletamos o referente na cosi.
            foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
                loop
                    -- Deleta em cosi aqueles que foram deletados na cos.
                    update checklist_ordem_servico_itens_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user,
                        motivo_delecao      = f_motivo_delecao
                    where deletado = false
                      and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                                   from checklist_ordem_servico_data cos
                                                   where cos.codigo_prolog = v_codigo_loop);
                end loop;
        end if;
    end if;

    -- Verifica se a placa é integrada.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa (não possuímos deleção lógica).
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- Realiza deleção da placa.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = now(),
        pg_username_delecao = session_user,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- Mensagem de sucesso.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;

drop function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar,
    f_cod_empresa_origem bigint,
    f_cod_unidade_origem bigint,
    f_cod_empresa_destino bigint,
    f_cod_unidade_destino bigint,
    f_cod_modelo_veiculo_destino bigint,
    f_cod_tipo_veiculo_destino bigint);
create or replace function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar(7),
                                                                                 f_cod_empresa_origem bigint,
                                                                                 f_cod_unidade_origem bigint,
                                                                                 f_cod_empresa_destino bigint,
                                                                                 f_cod_unidade_destino bigint,
                                                                                 f_cod_modelo_veiculo_destino bigint,
                                                                                 f_cod_tipo_veiculo_destino bigint,
                                                                                 out veiculo_transferido text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_nome_empresa_destino                           varchar(255) := (select e.nome
                                                                      from empresa e
                                                                      where e.codigo = f_cod_empresa_destino);
    v_nome_unidade_destino                           varchar(255) := (select u.nome
                                                                      from unidade u
                                                                      where u.codigo = f_cod_unidade_destino);
    v_lista_cod_oss_check                            bigint[];
    v_lista_cod_afericao_placa                       bigint[];
    v_cod_afericao_foreach                           bigint;
    v_lista_cod_pneu_em_afericao_manutencao          bigint[];
    v_qtd_cod_afericao_em_afericao_valores           bigint;
    v_qtd_cod_afericao_deletados_em_afericao_valores bigint;
    v_cod_veiculo                                    bigint       := (select v.codigo
                                                                      from veiculo v
                                                                      where v.placa = f_placa_veiculo
                                                                        and v.cod_unidade = f_cod_unidade_origem);
begin
    perform suporte.func_historico_salva_execucao();

    -- Verifica se empresa origem possui unidade origem.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_origem, f_cod_unidade_origem);

    -- Verifica se empresa destino possui unidade destino.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_destino, f_cod_unidade_destino);

    perform func_garante_empresas_distintas(f_cod_empresa_origem, f_cod_empresa_destino);
    perform func_garante_veiculo_existe(f_cod_unidade_origem, f_placa_veiculo);

    -- Verifica se a placa possui pneus.
    if exists(select vp.cod_pneu
              from veiculo_pneu vp
              where vp.placa = f_placa_veiculo
                and vp.cod_unidade = f_cod_unidade_origem)
    then
        raise exception 'Erro! A placa: % possui pneus vinculados, favor remover os pneus do mesmo', f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui tipo do veículo informado.
    if not exists(
            select vt.codigo
            from veiculo_tipo vt
            where vt.cod_empresa = f_cod_empresa_destino
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception 'Erro! O código tipo: % não existe na empresa destino: %', f_cod_tipo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se o tipo de veículo informado tem o mesmo diagrama do veículo.
    if not exists(
            select v.codigo
            from veiculo v
                     join veiculo_tipo vt on v.cod_diagrama = vt.cod_diagrama
            where v.placa = f_placa_veiculo
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception 'Erro! O diagrama do tipo: % é diferente do veículo: %', f_cod_tipo_veiculo_destino,
            f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui modelo do veículo informado.
    if not exists(select mv.codigo
                  from modelo_veiculo mv
                  where mv.cod_empresa = f_cod_empresa_destino
                    and mv.codigo = f_cod_modelo_veiculo_destino)
    then
        raise exception 'Erro! O código modelo: % não existe na empresa destino: %', f_cod_modelo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se placa possui aferição.
    if exists(select a.codigo
              from afericao a
              where a.cod_veiculo = v_cod_veiculo)
    then
        -- Então coletamos todos os códigos das aferições que a placa possui e adicionamos no array.
        select distinct array_agg(a.codigo)
        from afericao a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Laço for para percorrer todos os valores em f_lista_cod_afericao_placa.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                -- Para cada valor em: f_lista_cod_afericao_placa.
                if exists(select am.cod_afericao
                          from afericao_manutencao am
                          where am.cod_afericao = v_cod_afericao_foreach
                            and am.data_hora_resolucao is null
                            and am.fechado_automaticamente_integracao is false
                            and am.fechado_automaticamente_movimentacao is false)
                then
                    -- Coleta o(s) cod_pneu correspondentes ao cod_afericao.
                    select array_agg(am.cod_pneu)
                    from afericao_manutencao am
                    where am.cod_afericao = v_cod_afericao_foreach
                      and am.data_hora_resolucao is null
                      and am.fechado_automaticamente_integracao is false
                      and am.fechado_automaticamente_movimentacao is false
                    into v_lista_cod_pneu_em_afericao_manutencao;

                    -- Deleta aferição em afericao_manutencao_data através do cod_afericao e cod_pneu.
                    update afericao_manutencao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);

                    -- Deleta afericao em afericao_valores_data através do cod_afericao e cod_pneu.
                    update afericao_valores_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);
                end if;
            end loop;

        -- Se, e somente se, a aferição possuir todos os valores excluídos, deve-se excluir toda a aferição.
        -- Senão, a aferição continua existindo.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                v_qtd_cod_afericao_em_afericao_valores = (select count(avd.cod_afericao)
                                                          from afericao_valores_data avd
                                                          where avd.cod_afericao = v_cod_afericao_foreach);

                v_qtd_cod_afericao_deletados_em_afericao_valores = (select count(avd.cod_afericao)
                                                                    from afericao_valores_data avd
                                                                    where avd.cod_afericao = v_cod_afericao_foreach
                                                                      and avd.deletado is true);

                -- Se a quantidade de um cod_afericao em afericao_valores_data for igual a quantidade de um cod_afericao
                -- deletado em afericao_valores_data, devemos excluir a aferição, pois, todos seus valores foram
                -- deletados.
                if (v_qtd_cod_afericao_em_afericao_valores =
                    v_qtd_cod_afericao_deletados_em_afericao_valores)
                then
                    update afericao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and codigo = v_cod_afericao_foreach;
                end if;
            end loop;
    end if;

    -- Se possuir itens de OS aberto, deletamos esses itens.
    select array_agg(cos.codigo_prolog)
    from checklist c
             join checklist_ordem_servico cos
                  on c.codigo = cos.cod_checklist
    where c.placa_veiculo = f_placa_veiculo
      and cos.status = 'A'
    into v_lista_cod_oss_check;

    if (f_size_array(v_lista_cod_oss_check) > 0)
    then
        -- Deletamos primeiro as OSs.
        update checklist_ordem_servico_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transferência de veículo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
        where codigo_prolog = any (v_lista_cod_oss_check);

        -- Agora deletamos os itens.
        update checklist_ordem_servico_itens_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transferência de veículo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
            -- Precisamos usar a _DATA nesse where pois já deletamos as OSs.
        where (cod_os, cod_unidade) in (select cosd.codigo, cosd.cod_unidade
                                        from checklist_ordem_servico_data cosd
                                        where cosd.codigo_prolog = any (v_lista_cod_oss_check));
    end if;

    -- Se o veículo for integrado, atualiza os dados de empresa e unidade na tabela de integração.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_empresa_cadastro = f_cod_empresa_origem
                and ivc.cod_unidade_cadastro = f_cod_unidade_origem
                and ivc.placa_veiculo_cadastro = f_placa_veiculo)
    then
        update integracao.veiculo_cadastrado
        set cod_unidade_cadastro = f_cod_unidade_destino,
            cod_empresa_cadastro = f_cod_empresa_destino
        where cod_empresa_cadastro = f_cod_empresa_origem
          and cod_unidade_cadastro = f_cod_unidade_origem
          and placa_veiculo_cadastro = f_placa_veiculo;
    end if;

    -- Realiza transferência.
    update veiculo
    set cod_empresa = f_cod_empresa_destino,
        cod_unidade = f_cod_unidade_destino,
        cod_tipo    = f_cod_tipo_veiculo_destino,
        cod_modelo  = f_cod_modelo_veiculo_destino
    where cod_empresa = f_cod_empresa_origem
      and cod_unidade = f_cod_unidade_origem
      and placa = f_placa_veiculo;

    -- Mensagem de sucesso.
    select 'Veículo transferido com sucesso! O veículo com placa: ' || f_placa_veiculo ||
           ' foi transferido para a empresa ' || v_nome_empresa_destino || ' junto a unidade ' ||
           v_nome_unidade_destino || '.'
    into veiculo_transferido;
end
$$;

drop function func_veiculo_busca_evolucao_km_consolidado(f_cod_empresa bigint,
    f_cod_veiculo bigint,
    f_data_inicial date,
    f_data_final date);
create or replace function func_veiculo_busca_evolucao_km_consolidado(f_cod_empresa bigint,
                                                                      f_cod_veiculo bigint,
                                                                      f_data_inicial date,
                                                                      f_data_final date)
    returns table
            (
                processo                       text,
                cod_processo                   bigint,
                data_hora                      timestamp without time zone,
                placa                          varchar(7),
                km_coletado                    bigint,
                variacao_km_entre_coletas      bigint,
                km_atual                       bigint,
                diferenca_km_atual_km_coletado bigint
            )
    language plpgsql
as
$$
declare
    v_cod_unidades constant bigint[] not null := (select array_agg(u.codigo)
                                                  from unidade u
                                                  where u.cod_empresa = f_cod_empresa);
    v_check_data            boolean not null  := f_if(f_data_inicial is null or f_data_final is null,
                                                      false,
                                                      true);
begin
    return query
        with dados as (
            (select distinct on (mp.codigo) 'MOVIMENTACAO'                              as processo,
                                            m.codigo                                    as codigo,
                                            mp.data_hora at time zone tz_unidade(mp.cod_unidade)
                                                                                        as data_hora,
                                            coalesce(v_origem.codigo, v_destino.codigo) as cod_veiculo,
                                            coalesce(mo.km_veiculo, md.km_veiculo)      as km_coletado
             from movimentacao_processo mp
                      join movimentacao m on mp.codigo = m.cod_movimentacao_processo
                 and mp.cod_unidade = m.cod_unidade
                      join movimentacao_destino md on m.codigo = md.cod_movimentacao
                      join veiculo v_destino on v_destino.codigo = md.cod_veiculo
                      join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
                      join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             where coalesce(mo.cod_veiculo, md.cod_veiculo) = f_cod_veiculo
             group by m.codigo, mp.cod_unidade, mp.codigo, v_origem.codigo, v_destino.codigo, mo.km_veiculo,
                      md.km_veiculo)
            union
            (select 'CHECKLIST'                                        as processo,
                    c.codigo                                           as codigo,
                    c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
                    c.cod_veiculo                                      as cod_veiculo,
                    c.km_veiculo                                       as km_coletado
             from checklist c
             where c.cod_veiculo = f_cod_veiculo
               and c.cod_unidade = any (v_cod_unidades)
             union
             (select 'AFERICAO'                                         as processo,
                     a.codigo                                           as codigo,
                     a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.cod_veiculo                                      as cod_veiculo,
                     a.km_veiculo                                       as km_coletado
              from afericao a
              where a.cod_veiculo = f_cod_veiculo
                and a.cod_unidade = any (v_cod_unidades)
             )
             union
             (select 'FECHAMENTO_SERVICO_PNEU'                                     as processo,
                     am.codigo                                                     as codigo,
                     am.data_hora_resolucao at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.cod_veiculo                                                 as cod_veiculo,
                     am.km_momento_conserto                                        as km_coletado
              from afericao a
                       join afericao_manutencao am on a.codigo = am.cod_afericao
              where a.cod_veiculo = f_cod_veiculo
                and am.cod_unidade = any (v_cod_unidades)
                and am.data_hora_resolucao is not null
             )
             union
             (select 'FECHAMENTO_ITEM_CHECKLIST'                                    as processo,
                     cosi.codigo                                                    as codigo,
                     cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade) as data_hora,
                     c.cod_veiculo                                                  as cod_veiculo,
                     cosi.km                                                        as km_coletado
              from checklist c
                       join checklist_ordem_servico cos on cos.cod_checklist = c.codigo
                       join checklist_ordem_servico_itens cosi
                            on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
              where c.cod_veiculo = f_cod_veiculo
                and cosi.status_resolucao = 'R'
                and cosi.cod_unidade = any (v_cod_unidades)
              order by cosi.data_hora_fim_resolucao)
             union
             (select 'TRANSFERENCIA_DE_VEICULOS'             as processo,
                     vtp.codigo                              as codigo,
                     vtp.data_hora_transferencia_processo at time zone
                     tz_unidade(vtp.cod_unidade_colaborador) as data_hora,
                     vti.cod_veiculo                         as cod_veiculo,
                     vti.km_veiculo_momento_transferencia    as km_coletado
              from veiculo_transferencia_processo vtp
                       join veiculo_transferencia_informacoes vti on vtp.codigo = vti.cod_processo_transferencia
              where vti.cod_veiculo = f_cod_veiculo
                and vtp.cod_unidade_destino = any (v_cod_unidades)
                and vtp.cod_unidade_origem = any (v_cod_unidades)
             )
             union
             (select 'SOCORRO_EM_ROTA'                                              as processo,
                     sra.cod_socorro_rota                                           as codigo,
                     sra.data_hora_abertura at time zone tz_unidade(sr.cod_unidade) as data_hora,
                     sra.cod_veiculo_problema                                       as cod_veiculo,
                     sra.km_veiculo_abertura                                        as km_coletado
              from socorro_rota_abertura sra
                       join socorro_rota sr on sra.cod_socorro_rota = sr.codigo
              where sra.cod_veiculo_problema = f_cod_veiculo
                and sr.cod_unidade = any (v_cod_unidades)
             )
             union
             (select distinct on (func.km_veiculo) 'EDICAO_DE_VEICULOS'       as processo,
                                                   func.codigo_historico      as codigo,
                                                   func.data_hora_edicao      as data_hora,
                                                   func.codigo_veiculo_edicao as cod_veiculo,
                                                   func.km_veiculo            as km_coletado
              from func_veiculo_listagem_historico_edicoes(f_cod_empresa, f_cod_veiculo) as func
              where func.codigo_historico is not null
             )
            )
        )
        select d.processo,
               d.codigo,
               d.data_hora,
               v.placa,
               d.km_coletado,
               d.km_coletado - lag(d.km_coletado) over (order by d.data_hora) as variacao_entre_coletas,
               v.km                                                           as km_atual,
               (v.km - d.km_coletado)                                         as diferenca_atual_coletado
        from dados d
                 join veiculo v on v.codigo = d.cod_veiculo
        where f_if(v_check_data, d.data_hora :: date between f_data_inicial and f_data_final, true)
        order by row_number() over () desc;
end;
$$;

-- Recria views

create or replace view view_analise_pneus as
select u.nome                                                                         as "UNIDADE ALOCADO",
       pd.codigo                                                                      as "COD PNEU",
       pd.codigo_cliente                                                              as "COD PNEU CLIENTE",
       pd.status                                                                      as "STATUS PNEU",
       pd.cod_unidade,
       map.nome                                                                       as "MARCA",
       mp.nome                                                                        as "MODELO",
       (((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro             as "MEDIDAS",
       dados.qt_afericoes                                                             as "QTD DE AFERIÇÕES",
       to_char(dados.primeira_afericao::timestamp with time zone, 'DD/MM/YYYY'::text) as "DTA 1a AFERIÇÃO",
       to_char(dados.ultima_afericao::timestamp with time zone, 'DD/MM/YYYY'::text)   as "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias                                                               as "DIAS ATIVO",
       round(
               case
                   when dados.total_dias > 0 then dados.total_km / dados.total_dias::numeric
                   else null::numeric
                   end)                                                               as "MÉDIA KM POR DIA",
       pd.altura_sulco_interno,
       pd.altura_sulco_central_interno,
       pd.altura_sulco_central_externo,
       pd.altura_sulco_externo,
       round(dados.maior_sulco::numeric, 2)                                           as "MAIOR MEDIÇÃO VIDA",
       round(dados.menor_sulco::numeric, 2)                                           as "MENOR SULCO ATUAL",
       round(dados.sulco_gasto::numeric, 2)                                           as "MILIMETROS GASTOS",
       round(dados.km_por_mm::numeric, 2)                                             as "KMS POR MILIMETRO",
       round((dados.km_por_mm * dados.sulco_restante)::numeric)                       as "KMS A PERCORRER",
       trunc(
               case
                   when dados.total_km > 0::numeric and dados.total_dias > 0 and
                        (dados.total_km / dados.total_dias::numeric) > 0::numeric then
                           dados.km_por_mm * dados.sulco_restante /
                           (dados.total_km / dados.total_dias::numeric)::double precision
                   else 0::double precision
                   end)                                                               as "DIAS RESTANTES",
       case
           when dados.total_km > 0::numeric and dados.total_dias > 0 and
                (dados.total_km / dados.total_dias::numeric) > 0::numeric then (dados.km_por_mm * dados.sulco_restante /
                                                                                (dados.total_km / dados.total_dias::numeric)::double precision)::integer +
                                                                               'NOW'::text::date
           else null::date
           end                                                                        as "PREVISÃO DE TROCA"
from pneu_data pd
         join (select av.cod_pneu,
                      av.cod_unidade,
                      count(av.altura_sulco_central_interno)                                   as qt_afericoes,
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as primeira_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as ultima_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as total_dias,
                      max(total_km.total_km)                                                   as total_km,
                      max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))  as maior_sulco,
                      min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))     as menor_sulco,
                      max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                              LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                    av.altura_sulco_central_externo, av.altura_sulco_externo)) as sulco_gasto,
                      case
                          when
                                  case
                                      when p_1.vida_atual = p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_descarte
                                      when p_1.vida_atual < p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_recapagem
                                      else null::real
                                      end < 0::double precision then 0::real
                          else
                              case
                                  when p_1.vida_atual = p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_descarte
                                  when p_1.vida_atual < p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_recapagem
                                  else null::real
                                  end
                          end                                                                  as sulco_restante,
                      case
                          when (max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                                min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date) > 0 then
                                      max(total_km.total_km)::double precision / max(
                                          GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                                              LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                    av.altura_sulco_central_externo, av.altura_sulco_externo))
                          else 0::double precision
                          end                                                                  as km_por_mm
               from afericao_valores_data av
                        join afericao_data a on a.codigo = av.cod_afericao
                        join pneu_data p_1 on p_1.codigo::text = av.cod_pneu::text and p_1.status::text = 'EM_USO'::text
                        join pneu_restricao_unidade pru on pru.cod_unidade = av.cod_unidade
                        join (select total_km_rodado.cod_pneu,
                                     total_km_rodado.cod_unidade,
                                     sum(total_km_rodado.km_rodado) as total_km
                              from (select av_1.cod_pneu,
                                           av_1.cod_unidade,
                                           max(a_1.km_veiculo) - min(a_1.km_veiculo) as km_rodado
                                    from afericao_valores_data av_1
                                             join afericao_data a_1 on a_1.codigo = av_1.cod_afericao
                                    group by av_1.cod_pneu, av_1.cod_unidade, a_1.cod_veiculo) total_km_rodado
                              group by total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                             on total_km.cod_pneu = av.cod_pneu and total_km.cod_unidade = av.cod_unidade
               group by av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                        pru.sulco_minimo_recapagem) dados on dados.cod_pneu = pd.codigo
         join dimensao_pneu dp on dp.codigo = pd.cod_dimensao
         join unidade u on u.codigo = pd.cod_unidade
         join modelo_pneu mp on mp.codigo = pd.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca;

create or replace view view_pneu_km_rodado_vida as
select p.codigo                                                                      as cod_pneu,
       coalesce(q.vida_pneu, p.vida_atual)                                           as vida_pneu,
       (coalesce(sum(q.km_rodado), 0)
           +
        (select func_pneu_calcula_km_aplicacao_remocao_pneu(p.codigo, q.vida_pneu))) as km_rodado_vida
from (select av.cod_pneu,
             av.vida_momento_afericao                as vida_pneu,
             (max(a.km_veiculo) - min(a.km_veiculo)) as km_rodado
      from (afericao_valores av
               join afericao a on a.codigo = av.cod_afericao
          )
      where ((a.tipo_processo_coleta)::text = 'PLACA'::text)
      group by av.cod_pneu, a.cod_veiculo, av.vida_momento_afericao
      order by av.cod_pneu) q
         right join pneu_data p on p.codigo = q.cod_pneu
group by p.codigo, q.vida_pneu
order by p.codigo, q.vida_pneu;

create view view_pneu_analise_vidas as
with dados_afericao as (
    select a.codigo                                                                                         as cod_afericao,
           a.cod_unidade                                                                                    as cod_unidade_afericao,
           a.data_hora                                                                                      as data_hora_afericao,
           a.tipo_processo_coleta                                                                           as tipo_processo_coleta_afericao,
           av.cod_pneu,
           av.vida_momento_afericao,
           av.altura_sulco_central_interno,
           av.altura_sulco_central_externo,
           av.altura_sulco_externo,
           av.altura_sulco_interno,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora)                   as row_number_asc,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora desc)              as row_number_desc
    from (afericao a
             join afericao_valores av on ((a.codigo = av.cod_afericao)))
),
     primeira_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where (da.row_number_asc = 1)
     ),
     ultima_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where (da.row_number_desc = 1)
     ),
     analises_afericoes as (
         select da.cod_pneu,
                da.vida_momento_afericao               as vida_analisada_pneu,
                count(da.cod_pneu)                     as quantidade_afericoes_pneu_vida,
                max(GREATEST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                             da.altura_sulco_interno)) as maior_sulco_aferido_vida,
                min(LEAST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                          da.altura_sulco_interno))    as menor_sulco_aferido_vida
         from dados_afericao da
         group by da.cod_pneu, da.vida_momento_afericao
     )
select p.codigo                                                                            as cod_pneu,
       p.status,
       p.valor                                                                             as valor_pneu,
       COALESCE(pvv.valor, (0)::real)                                                      as valor_banda,
       pa.data_hora_afericao                                                               as data_hora_primeira_afericao,
       pa.cod_afericao                                                                     as cod_primeira_afericao,
       pa.cod_unidade_afericao                                                             as cod_unidade_primeira_afericao,
       ua.data_hora_afericao                                                               as data_hora_ultima_afericao,
       ua.cod_afericao                                                                     as cod_ultima_afericao,
       ua.cod_unidade_afericao                                                             as cod_unidade_ultima_afericao,
       aa.vida_analisada_pneu,
       aa.quantidade_afericoes_pneu_vida,
       aa.maior_sulco_aferido_vida,
       aa.menor_sulco_aferido_vida,
       (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida)                         as sulco_gasto,
       (date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)))::integer as total_dias_ativo,
       km_rodado_pneu.km_rodado_vida                                                       as total_km_rodado_vida,
       func_pneu_calcula_sulco_restante(p.vida_atual, p.vida_total, p.altura_sulco_externo,
                                        p.altura_sulco_central_externo, p.altura_sulco_central_interno,
                                        p.altura_sulco_interno, pru.sulco_minimo_recapagem,
                                        pru.sulco_minimo_descarte)                         as sulco_restante,
       case
           when ((date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)) > (0)::double precision) and
                 ((aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > (0)::double precision)) then (
                   (km_rodado_pneu.km_rodado_vida)::double precision /
                   (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida))
           else (0)::double precision
           end                                                                             as km_por_mm_vida,
       case
           when (km_rodado_pneu.km_rodado_vida = (0)::numeric) then (0)::double precision
           else
               case
                   when (km_rodado_pneu.vida_pneu = 1)
                       then (p.valor / (km_rodado_pneu.km_rodado_vida)::double precision)
                   else (COALESCE(pvv.valor, (0)::real) / (km_rodado_pneu.km_rodado_vida)::double precision)
                   end
           end                                                                             as valor_por_km_vida
from ((((((analises_afericoes aa
    join primeira_afericao pa on (((pa.cod_pneu = aa.cod_pneu) and (pa.vida_momento_afericao = aa.vida_analisada_pneu))))
    join ultima_afericao ua on (((ua.cod_pneu = aa.cod_pneu) and (ua.vida_momento_afericao = aa.vida_analisada_pneu))))
    join pneu p on ((aa.cod_pneu = p.codigo)))
    join pneu_restricao_unidade pru on ((p.cod_unidade = pru.cod_unidade)))
    left join pneu_valor_vida pvv on ((p.codigo = pvv.cod_pneu)))
         join view_pneu_km_rodado_vida km_rodado_pneu
              on (((km_rodado_pneu.cod_pneu = aa.cod_pneu) and (km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu))))
order by aa.cod_pneu, aa.vida_analisada_pneu;

create view view_pneu_km_rodado_total as
with km_rodado_total as (
    select view_pneu_km_rodado_vida.cod_pneu,
           sum(view_pneu_km_rodado_vida.km_rodado_vida) as total_km_rodado_todas_vidas
    from view_pneu_km_rodado_vida
    group by view_pneu_km_rodado_vida.cod_pneu
    order by view_pneu_km_rodado_vida.cod_pneu
)
select km_vida.cod_pneu,
       km_vida.vida_pneu,
       km_vida.km_rodado_vida,
       km_total.total_km_rodado_todas_vidas
from (view_pneu_km_rodado_vida km_vida
         join km_rodado_total km_total on ((km_vida.cod_pneu = km_total.cod_pneu)))
order by km_vida.cod_pneu, km_vida.vida_pneu;

create view view_pneu_analise_vida_atual as
select u.nome                                                               as "UNIDADE ALOCADO",
       p.codigo                                                             as "COD PNEU",
       p.codigo_cliente                                                     as "COD PNEU CLIENTE",
       (p.valor + sum(pvv.valor))                                           as valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                                   as km_acumulado,
       p.vida_atual                                                         as "VIDA ATUAL",
       p.status                                                             as "STATUS PNEU",
       p.cod_unidade,
       p.valor                                                              as valor_pneu,
       case
           when (dados.vida_analisada_pneu = 1) then dados.valor_pneu
           else dados.valor_banda
           end                                                              as valor_vida_atual,
       map.nome                                                             as "MARCA",
       mp.nome                                                              as "MODELO",
       ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) as "MEDIDAS",
       dados.quantidade_afericoes_pneu_vida                                 as "QTD DE AFERIÇÕES",
       to_char(dados.data_hora_primeira_afericao, 'DD/MM/YYYY'::text)       as "DTA 1a AFERIÇÃO",
       to_char(dados.data_hora_ultima_afericao, 'DD/MM/YYYY'::text)         as "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias_ativo                                               as "DIAS ATIVO",
       round(
               case
                   when (dados.total_dias_ativo > 0)
                       then (dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric)
                   else null::numeric
                   end)                                                     as "MÉDIA KM POR DIA",
       round((dados.maior_sulco_aferido_vida)::numeric, 2)                  as "MAIOR MEDIÇÃO VIDA",
       round((dados.menor_sulco_aferido_vida)::numeric, 2)                  as "MENOR SULCO ATUAL",
       round((dados.sulco_gasto)::numeric, 2)                               as "MILIMETROS GASTOS",
       round((dados.km_por_mm_vida)::numeric, 2)                            as "KMS POR MILIMETRO",
       round((dados.valor_por_km_vida)::numeric, 2)                         as "VALOR POR KM",
       round((
                 case
                     when (sum(v.total_km_rodado_todas_vidas) > (0)::numeric) then ((p.valor + sum(pvv.valor)) /
                                                                                    (sum(v.total_km_rodado_todas_vidas))::double precision)
                     else (0)::double precision
                     end)::numeric, 2)                                      as "VALOR POR KM ACUMULADO",
       round(((dados.km_por_mm_vida * dados.sulco_restante))::numeric)      as "KMS A PERCORRER",
       trunc(
               case
                   when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                         ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                           (dados.km_por_mm_vida * dados.sulco_restante) /
                           ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision)
                   else (0)::double precision
                   end)                                                     as "DIAS RESTANTES",
       case
           when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                   (((dados.km_por_mm_vida * dados.sulco_restante) /
                     ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                   ('NOW'::text)::date)
           else null::date
           end                                                              as "PREVISÃO DE TROCA",
       case
           when (p.vida_atual = p.vida_total) then 'DESCARTE'::text
           else 'ANÁLISE'::text
           end                                                              as "DESTINO"
from (((((((pneu p
    join (select view_pneu_analise_vidas.cod_pneu,
                 view_pneu_analise_vidas.vida_analisada_pneu,
                 view_pneu_analise_vidas.status,
                 view_pneu_analise_vidas.valor_pneu,
                 view_pneu_analise_vidas.valor_banda,
                 view_pneu_analise_vidas.quantidade_afericoes_pneu_vida,
                 view_pneu_analise_vidas.data_hora_primeira_afericao,
                 view_pneu_analise_vidas.data_hora_ultima_afericao,
                 view_pneu_analise_vidas.total_dias_ativo,
                 view_pneu_analise_vidas.total_km_rodado_vida,
                 view_pneu_analise_vidas.maior_sulco_aferido_vida,
                 view_pneu_analise_vidas.menor_sulco_aferido_vida,
                 view_pneu_analise_vidas.sulco_gasto,
                 view_pneu_analise_vidas.sulco_restante,
                 view_pneu_analise_vidas.km_por_mm_vida,
                 view_pneu_analise_vidas.valor_por_km_vida
          from view_pneu_analise_vidas) dados on (((dados.cod_pneu = p.codigo) and
                                                   (dados.vida_analisada_pneu = p.vida_atual))))
    join dimensao_pneu dp on ((dp.codigo = p.cod_dimensao)))
    join unidade u on ((u.codigo = p.cod_unidade)))
    join modelo_pneu mp on (((mp.codigo = p.cod_modelo) and (mp.cod_empresa = u.cod_empresa))))
    join marca_pneu map on ((map.codigo = mp.cod_marca)))
    join view_pneu_km_rodado_total v on (((p.codigo = v.cod_pneu) and (p.vida_atual = v.vida_pneu))))
         left join pneu_valor_vida pvv on ((pvv.cod_pneu = p.codigo)))
group by u.nome, p.codigo, p.valor, p.vida_atual, p.status, p.vida_total, p.codigo_cliente, p.cod_unidade,
         dados.valor_banda, dados.valor_pneu, map.nome,
         mp.nome, dp.largura, dp.altura, dp.aro, dados.quantidade_afericoes_pneu_vida,
         dados.data_hora_primeira_afericao, dados.data_hora_ultima_afericao, dados.total_dias_ativo,
         dados.total_km_rodado_vida, dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto,
         dados.km_por_mm_vida, dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu
order by case
             when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                   ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                     (((dados.km_por_mm_vida * dados.sulco_restante) /
                       ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                     ('NOW'::text)::date)
             else null::date
             end;