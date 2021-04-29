begin transaction;
--######################################################################################################################
--######################################################################################################################
--######################################### Migra estrutura da tabela de respostas #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-2184
-- Dropa constraints e views necessárias.
drop view estratificacao_os;
alter table checklist_modelo_data drop constraint unico_modelo_por_unidade;
alter table checklist_alternativa_pergunta_data drop constraint fk_checklist_alternativa_pergunta_pergunta;
alter table checklist_perguntas_data drop constraint unica_pergunta_por_modelo;
alter table checklist_alternativa_pergunta_data drop constraint unica_alternativa_por_pergunta;

-- Recria constraint.
alter table checklist_alternativa_pergunta_data add constraint fk_checklist_alternativa_pergunta_pergunta
foreign key (cod_pergunta) references checklist_perguntas_data (codigo);

-- Cria tabela para salvar as versões dos modelos de checklist.
create table if not exists checklist_modelo_versao
(
    cod_versao_checklist_modelo    bigserial not null,
    cod_versao_user_friendly       bigint    not null,
    cod_checklist_modelo           bigint    not null,
    data_hora_criacao_versao       timestamp with time zone,
    cod_colaborador_criacao_versao bigint,
    constraint pk_checklist_modelo_versao
        primary key (cod_versao_checklist_modelo),
    constraint fk_checklist_modelo_versao_checklist_modelo
        foreign key (cod_checklist_modelo) references checklist_modelo_data (codigo) DEFERRABLE INITIALLY IMMEDIATE,
    constraint fk_checklist_modelo_versao_colaborador
        foreign key (cod_colaborador_criacao_versao) references colaborador_data (codigo),
    constraint unique_versao_user_friendly_modelo_checklist unique (cod_checklist_modelo, cod_versao_user_friendly),
    constraint unique_versao_modelo_checklist unique (cod_checklist_modelo, cod_versao_checklist_modelo),
    constraint check_data_not_null_acima_versao_1 check (cod_versao_user_friendly = 1 or
                                                         data_hora_criacao_versao is not null),
    constraint check_colaborador_not_null_acima_versao_1 check (cod_versao_user_friendly = 1 or
                                                                cod_colaborador_criacao_versao is not null)
);

comment on table checklist_modelo_versao is 'Salva as versões de um modelo de checklist. data_hora_criacao_versao e cod_colaborador_criacao_versao
    podem ser nulos pois na primeira versão não tínhamos quando foi criada e nem quem criou. Porém, existem checks que impedem que essas colunas
    sejam nulas em novas versões.';

--######################################################################################################################
-- CHECKLIST_MODELO_DATA
-- Cria coluna de versão atual na tabela de modelo.
alter table checklist_modelo_data add column cod_versao_atual bigint;
alter table checklist_modelo_data
    add constraint fk_checklist_modelo_checklist_modelo_versao
        foreign key (cod_versao_atual)
            references checklist_modelo_versao (cod_versao_checklist_modelo) DEFERRABLE INITIALLY IMMEDIATE;

drop view checklist_modelo;
create or replace view checklist_modelo as
  select
    cm.cod_unidade,
    cm.codigo,
    cm.cod_versao_atual,
    cm.nome,
    cm.status_ativo
  from checklist_modelo_data cm
  where cm.deletado = false;

insert into checklist_modelo_versao
(cod_checklist_modelo,
 cod_versao_user_friendly,
 data_hora_criacao_versao,
 cod_colaborador_criacao_versao)
select cm.codigo,
       1,
       null,
       null
from checklist_modelo_data cm;


-- Setamos o cod_versao_atual como o código gerado na CHECKLIST_MODELO_VERSAO para cada modelo.
with cte as (
    select cmv.cod_checklist_modelo,
           cmv.cod_versao_checklist_modelo
    from checklist_modelo_versao cmv
    where cmv.cod_versao_user_friendly = 1)

update checklist_modelo_data cmd
set cod_versao_atual = c.cod_versao_checklist_modelo
from cte c
where cmd.codigo = c.cod_checklist_modelo;
-- Agora pode ser NOT NULL.
alter table checklist_modelo_data alter column cod_versao_atual set not null;
--######################################################################################################################

--######################################################################################################################
-- CHECKLIST_DATA
-- Cria versão na tabela CHECKLIST_DATA.
alter table checklist_data add column cod_versao_checklist_modelo bigint;

with cte as (
    select cmv.cod_checklist_modelo,
           cmv.cod_versao_checklist_modelo
    from checklist_modelo_versao cmv
    where cmv.cod_versao_user_friendly = 1)
update checklist_data cd
set cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
from cte c
where cd.cod_checklist_modelo = c.cod_checklist_modelo;

-- Dropa a view checklist
drop view checklist;

-- Recria a view checklist com a coluna cod_versao_checklist_modelo
create view checklist(cod_unidade, cod_checklist_modelo, codigo, data_hora, data_hora_importado_prolog, cpf_colaborador,
                      placa_veiculo, tipo, tempo_realizacao, km_veiculo, data_hora_sincronizacao,
                      fonte_data_hora_realizacao, versao_app_momento_realizacao, versao_app_momento_sincronizacao,
                      device_id, device_imei, device_uptime_realizacao_millis, device_uptime_sincronizacao_millis,
                      foi_offline, total_perguntas_ok, total_perguntas_nok, total_alternativas_ok,
                      total_alternativas_nok, cod_versao_checklist_modelo) as
select c.cod_unidade,
       c.cod_checklist_modelo,
       c.codigo,
       c.data_hora,
       c.data_hora_importado_prolog,
       c.cpf_colaborador,
       c.placa_veiculo,
       c.tipo,
       c.tempo_realizacao,
       c.km_veiculo,
       c.data_hora_sincronizacao,
       c.fonte_data_hora_realizacao,
       c.versao_app_momento_realizacao,
       c.versao_app_momento_sincronizacao,
       c.device_id,
       c.device_imei,
       c.device_uptime_realizacao_millis,
       c.device_uptime_sincronizacao_millis,
       c.foi_offline,
       c.total_perguntas_ok,
       c.total_perguntas_nok,
       c.total_alternativas_ok,
       c.total_alternativas_nok,
       c.cod_versao_checklist_modelo
from checklist_data c
where (c.deletado = false);

alter table checklist_data alter column cod_versao_checklist_modelo set not null;
-- Essa constraint não precisa mais pois a fk com a versão já garante a existência do modelo.
alter table checklist_data drop constraint fk_checklist_checklist_modelo;
alter table checklist_data add constraint fk_checklist_data_checklist_modelo_versao
    foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
        references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);
--######################################################################################################################

--######################################################################################################################
-- CHECKLIST_PERGUNTAS_DATA
-- Cria versão na tabela CHECKLIST_PERGUNTAS_DATA.
alter table checklist_perguntas_data add column cod_versao_checklist_modelo bigint;
-- Para perguntas antigas (inativas) o código gerado será diferente da pergunta ativa atual. Iremos ignorar esses casos.
alter table checklist_perguntas_data add column codigo_fixo_pergunta bigserial not null;

with cte as (
    select cmv.cod_checklist_modelo,
           cmv.cod_versao_checklist_modelo
    from checklist_modelo_versao cmv
    where cmv.cod_versao_user_friendly = 1)
update checklist_perguntas_data cpd
set cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
from cte c
where cpd.cod_checklist_modelo = c.cod_checklist_modelo;

-- Constraint útil para servir como FK na COSI.
alter table checklist_perguntas_data add constraint unica_pergunta_versao unique (codigo_fixo_pergunta, codigo);

-- Remove FK única com cod_modelo e usa uma dupla compondo com versão do modelo.
alter table checklist_perguntas_data drop constraint fk_checklist_perguntas_checklist_modelo;
alter table checklist_perguntas_data
    add constraint fk_checklist_perguntas_checklist_modelo_versao
        foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
            references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);

drop view checklist_perguntas;
create or replace view checklist_perguntas as
  select
    cp.cod_checklist_modelo,
    cp.cod_versao_checklist_modelo,
    cp.cod_unidade,
    cp.ordem,
    cp.pergunta,
    cp.status_ativo,
    cp.single_choice,
    cp.cod_imagem,
    cp.codigo,
    cp.codigo_fixo_pergunta
  from checklist_perguntas_data cp
  where cp.deletado = false;
--######################################################################################################################

--######################################################################################################################
-- CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
-- Cria versão na tabela CHECKLIST_ALTERNATIVA_PERGUNTA_DATA.
alter table checklist_alternativa_pergunta_data add column cod_versao_checklist_modelo bigint;
-- Para alternativas antigas (inativas) o código gerado será diferente da alternativas ativa atual.
-- Iremos ignorar esses casos.
alter table checklist_alternativa_pergunta_data add column codigo_fixo_alternativa bigserial not null;

with cte as (
    select cmv.cod_checklist_modelo,
           cmv.cod_versao_checklist_modelo
    from checklist_modelo_versao cmv
    where cmv.cod_versao_user_friendly = 1)
update checklist_alternativa_pergunta_data capd
set cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
from cte c
where capd.cod_checklist_modelo = c.cod_checklist_modelo;

-- Constraint útil para servir como FK na COSI.
alter table checklist_alternativa_pergunta_data add constraint unica_alternativa_versao unique (codigo_fixo_alternativa, codigo);

alter table checklist_alternativa_pergunta_data drop constraint fk_checklist_alternativa_pergunta_checklist_modelo;
alter table checklist_alternativa_pergunta_data
    add constraint fk_checklist_alternativa_pergunta_checklist_modelo_versao
        foreign key (cod_checklist_modelo, cod_versao_checklist_modelo)
            references checklist_modelo_versao (cod_checklist_modelo, cod_versao_checklist_modelo);

drop view checklist_alternativa_pergunta;
create or replace view checklist_alternativa_pergunta as
  select
    cap.cod_checklist_modelo,
    cap.cod_versao_checklist_modelo,
    cap.cod_unidade,
    cap.alternativa,
    cap.ordem,
    cap.status_ativo,
    cap.cod_pergunta,
    cap.codigo,
    cap.codigo_fixo_alternativa,
    cap.alternativa_tipo_outros,
    cap.prioridade,
    cap.deve_abrir_ordem_servico
  from checklist_alternativa_pergunta_data cap
  where cap.deletado = false;
--######################################################################################################################

--######################################################################################################################
-- CHECKLIST_RESPOSTAS migração
-- Cria tabela que conterá as respostas NOK.
create table checklist_respostas_nok
(
    codigo                      bigserial not null,
    cod_unidade                 bigint not null,
    cod_checklist_modelo        bigint not null,
    cod_versao_checklist_modelo bigint not null,
    cod_checklist               bigint not null,
    cod_pergunta                bigint not null,
    cod_alternativa             bigint not null,
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
insert into checklist_respostas_nok
(cod_unidade,
 cod_checklist_modelo,
 cod_versao_checklist_modelo,
 cod_checklist,
 cod_pergunta,
 cod_alternativa,
 resposta_outros)
select cr.cod_unidade,
       cr.cod_checklist_modelo,
       (select cmv.cod_versao_checklist_modelo
        from checklist_modelo_versao cmv
        where cmv.cod_checklist_modelo = cr.cod_checklist_modelo
          and cmv.cod_versao_user_friendly = 1),
       cr.cod_checklist,
       cr.cod_pergunta,
       cr.cod_alternativa,
       f_if(cr.resposta <> 'NOK', cr.resposta, null)
from checklist_respostas cr
where cr.resposta <> 'OK';
--######################################################################################################################


--######################################################################################################################
create table if not exists checklist_ordem_servico_itens_apontamentos
(
    codigo                 bigserial not null,
    cod_item_ordem_servico bigint    not null,
    cod_resposta_nok       bigint    not null,
    nova_qtd_apontamentos  integer   not null,
    constraint pk_checklist_apontamentos primary key (codigo),
    constraint fk_checklist_apontamentos_item_ordem_servico foreign key (cod_item_ordem_servico)
        references checklist_ordem_servico_itens_data (codigo),
    constraint fk_checklist_apontamentos_resposta_nok foreign key (cod_resposta_nok)
        references checklist_respostas_nok (codigo),
    constraint unica_qtd_apontamentos_por_item_ordem_servico
        unique (cod_item_ordem_servico, nova_qtd_apontamentos)
);

comment on table checklist_ordem_servico_itens_apontamentos is 'Salva os apontamentos que houveram nos itens abertos de OS.
    Cada checklist é responsável por incrementar um apontamento de um item já em aberto, com essa tabela conseguiremos
    saber quais checklists foram responsáveis por cada apontamento do item de OS.';

-- Renomeia as colunas atuais.
alter table checklist_ordem_servico_itens_data rename column cod_pergunta to cod_pergunta_primeiro_apontamento;
alter table checklist_ordem_servico_itens_data rename column cod_alternativa to cod_alternativa_primeiro_apontamento;

-- Cria colunas para referenciarmos os códigos fixos das perguntas e alternativas.
-- Isso vai nos ajudar a criar a unique que impedirá dois itens iguais, em aberto, da mesma alternativa.
alter table checklist_ordem_servico_itens_data add column cod_fixo_pergunta bigint;
alter table checklist_ordem_servico_itens_data add column cod_fixo_alternativa bigint;

-- Remove constraints para o update funcionar.
alter table checklist_ordem_servico_itens_data drop constraint check_data_hora_inicio_resolucao_not_null;
alter table checklist_ordem_servico_itens_data drop constraint check_data_hora_fim_resolucao_not_null;

update checklist_ordem_servico_itens_data
set cod_fixo_pergunta = (select cpd.codigo_fixo_pergunta
                         from checklist_perguntas_data cpd
                         where cpd.codigo = cod_pergunta_primeiro_apontamento);
update checklist_ordem_servico_itens_data
set cod_fixo_alternativa = (select capd.codigo_fixo_alternativa
                            from checklist_alternativa_pergunta_data capd
                            where capd.codigo = cod_alternativa_primeiro_apontamento);

-- Recria as constraints após o update.
alter table checklist_ordem_servico_itens_data
  add constraint check_data_hora_inicio_resolucao_not_null check (
  deletado or ((data_hora_conserto is not null and data_hora_inicio_resolucao is not null)
               or (data_hora_conserto is null and data_hora_inicio_resolucao is null))) not valid;
alter table checklist_ordem_servico_itens_data
  add constraint check_data_hora_fim_resolucao_not_null check (
  deletado or ((data_hora_conserto is not null and data_hora_fim_resolucao is not null)
               or (data_hora_conserto is null and data_hora_fim_resolucao is null))) not valid;
comment on constraint check_data_hora_inicio_resolucao_not_null
on checklist_ordem_servico_itens_data
is 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_INICIO_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';
comment on constraint check_data_hora_fim_resolucao_not_null
on checklist_ordem_servico_itens_data
is 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_FIM_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';


alter table checklist_ordem_servico_itens_data drop constraint fk_checklist_ordem_servico_itens_perguntas;
alter table checklist_ordem_servico_itens_data drop constraint fk_checklist_ordem_servico_itens_alternativa_pergunta;

alter table checklist_ordem_servico_itens_data add constraint fk_checklist_ordem_servico_itens_perguntas
foreign key (cod_fixo_pergunta, cod_pergunta_primeiro_apontamento)
    references checklist_perguntas_data (codigo_fixo_pergunta, codigo);
alter table checklist_ordem_servico_itens_data add constraint fk_checklist_ordem_servico_itens_alternativa_pergunta
foreign key (cod_fixo_alternativa, cod_alternativa_primeiro_apontamento)
    references checklist_alternativa_pergunta_data (codigo_fixo_alternativa, codigo);
--######################################################################################################################


-- Recria VIEW.
CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
  SELECT
    cos.codigo                                                       AS cod_os,
    realizador.nome                                                  AS nome_realizador_checklist,
    c.placa_veiculo,
    c.km_veiculo                                                     AS km,
    timezone(tz_unidade(cos.cod_unidade), c.data_hora)               AS data_hora,
    c.tipo                                                           AS tipo_checklist,
    cp.codigo                                                        AS cod_pergunta,
    cp.ordem                                                         AS ordem_pergunta,
    cp.pergunta,
    cp.single_choice,
    NULL :: unknown                                                  AS url_imagem,
    cap.prioridade,
    CASE cap.prioridade
    WHEN 'CRITICA' :: text
      THEN 1
    WHEN 'ALTA' :: text
      THEN 2
    WHEN 'BAIXA' :: text
      THEN 3
    ELSE NULL :: integer
    END                                                              AS prioridade_ordem,
    cap.codigo                                                       AS cod_alternativa,
    cap.alternativa,
    prio.prazo,
    cr.resposta,
    v.cod_tipo,
    cos.cod_unidade,
    cos.status                                                       AS status_os,
    cos.cod_checklist,
    tz_unidade(cos.cod_unidade)                                      AS time_zone_unidade,
    cosi.status_resolucao                                            AS status_item,
    mecanico.nome                                                    AS nome_mecanico,
    cosi.cpf_mecanico,
    cosi.tempo_realizacao,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
    COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
    COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
    cosi.km                                                          AS km_fechamento,
    cosi.qt_apontamentos,
    cosi.feedback_conserto,
    cosi.codigo
  FROM (((((((((checklist c
    JOIN colaborador realizador ON ((realizador.cpf = c.cpf_colaborador)))
    JOIN veiculo v ON (((v.placa) :: text = (c.placa_veiculo) :: text)))
    JOIN checklist_ordem_servico cos ON (((c.codigo = cos.cod_checklist) AND (c.cod_unidade = cos.cod_unidade))))
    JOIN checklist_ordem_servico_itens cosi ON (((cos.codigo = cosi.cod_os) AND (cos.cod_unidade = cosi.cod_unidade))))
    JOIN checklist_perguntas cp ON ((((cp.cod_unidade = cos.cod_unidade) AND (cp.codigo = cosi.cod_pergunta)) AND
                                     (cp.cod_checklist_modelo = c.cod_checklist_modelo))))
    JOIN checklist_alternativa_pergunta cap ON ((
      (((cap.cod_unidade = cp.cod_unidade) AND (cap.cod_checklist_modelo = cp.cod_checklist_modelo)) AND
       (cap.cod_pergunta = cp.codigo)) AND (cap.codigo = cosi.cod_alternativa))))
    JOIN checklist_alternativa_prioridade prio ON (((prio.prioridade) :: text = (cap.prioridade) :: text)))
    JOIN checklist_respostas cr ON ((((((c.cod_unidade = cr.cod_unidade) AND
                                        (cr.cod_checklist_modelo = c.cod_checklist_modelo)) AND
                                       (cr.cod_checklist = c.codigo)) AND (cr.cod_pergunta = cp.codigo)) AND
                                     (cr.cod_alternativa = cap.codigo))))
    LEFT JOIN colaborador mecanico ON ((mecanico.cpf = cosi.cpf_mecanico)));

comment on view estratificacao_os
is 'View que compila as informações das OS e seus itens';


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

end transaction;