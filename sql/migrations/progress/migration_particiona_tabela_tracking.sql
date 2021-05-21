alter table tracking rename to tracking_old;
alter table tracking_old drop constraint pk_tracking;
alter table tracking_old drop constraint fk_tracking_unidade;
alter table tracking_old set schema backup;

create table tracking
(
    classe                      integer,
    data                        date       not null,
    mapa                        integer    not null,
    placa                       varchar(7) not null,
    cod_cliente                 integer    not null,
    seq_real                    integer,
    seq_plan                    integer,
    inicio_rota                 time,
    horario_matinal             time,
    saida_cdd                   time,
    chegada_ao_pdv              time,
    tempo_prev_retorno          time,
    tempo_retorno               time,
    dist_prev_retorno           real,
    dist_perc_retorno           real,
    inicio_entrega              time,
    fim_entrega                 time,
    fim_rota                    time,
    entrada_cdd                 time,
    caixas_carregadas           real,
    caixas_devolvidas           real,
    repasse                     real,
    tempo_de_entrega            time,
    tempo_descarga              time,
    tempo_espera                time,
    tempo_almoco                time,
    tempo_total_de_rota         time,
    disp_apont_cadastrado       real,
    lat_entrega                 varchar(255),
    lon_entrega                 varchar(255),
    unidade_negocio             integer,
    transportadora              varchar(255),
    lat_cliente_apontamento     varchar(255),
    lon_cliente_apontamento     varchar(255),
    lat_atual_cliente           varchar(255),
    lon_atual_cliente           varchar(255),
    distancia_prev              real,
    tempo_deslocamento          time,
    vel_media_km_h              real,
    distancia_perc_apontamento  real,
    aderencia_sequencia_entrega varchar(3),
    aderencia_janela_entrega    varchar(3),
    pdv_lacrado                 varchar(3),
    cod_unidade                 integer    not null
        constraint fk_tracking_unidade
            references unidade,
    data_hora_import            timestamp default now(),
    constraint pk_tracking
        primary key (mapa, data, placa, cod_cliente, cod_unidade)
) partition by list (cod_unidade);

comment on table tracking is 'Dados coletados da planilha ambev (aderência), usados para calcular o indicador tracking.';

create table tracking_1 partition of tracking for values in (1);
create table tracking_2 partition of tracking for values in (2);
create table tracking_3 partition of tracking for values in (3);
create table tracking_4 partition of tracking for values in (4);
create table tracking_5 partition of tracking for values in (5);
create table tracking_6 partition of tracking for values in (6);
create table tracking_7 partition of tracking for values in (7);
create table tracking_9 partition of tracking for values in (9);
create table tracking_11 partition of tracking for values in (11);
create table tracking_12 partition of tracking for values in (12);
create table tracking_16 partition of tracking for values in (16);
create table tracking_53 partition of tracking for values in (53);
create table tracking_56 partition of tracking for values in (56);
create table tracking_62 partition of tracking for values in (62);
create table tracking_68 partition of tracking for values in (68);
create table tracking_69 partition of tracking for values in (69);
create table tracking_70 partition of tracking for values in (70);
create table tracking_73 partition of tracking for values in (73);
create table tracking_76 partition of tracking for values in (76);
create table tracking_109 partition of tracking for values in (109);
create table tracking_116 partition of tracking for values in (116);
create table tracking_142 partition of tracking for values in (142);
create table tracking_205 partition of tracking for values in (205);
create table tracking_208 partition of tracking for values in (208);
create table tracking_211 partition of tracking for values in (211);
create table tracking_216 partition of tracking for values in (216);
create table tracking_217 partition of tracking for values in (217);
create table tracking_358 partition of tracking for values in (358);
create table tracking_375 partition of tracking for values in (375);
create table tracking_390 partition of tracking for values in (390);
create table tracking_460 partition of tracking for values in (460);

create index idx_tracking_1_cod_unidade on tracking_1 (cod_unidade);
create index idx_tracking_2_cod_unidade on tracking_2 (cod_unidade);
create index idx_tracking_3_cod_unidade on tracking_3 (cod_unidade);
create index idx_tracking_4_cod_unidade on tracking_4 (cod_unidade);
create index idx_tracking_5_cod_unidade on tracking_5 (cod_unidade);
create index idx_tracking_6_cod_unidade on tracking_6 (cod_unidade);
create index idx_tracking_7_cod_unidade on tracking_7 (cod_unidade);
create index idx_tracking_9_cod_unidade on tracking_9 (cod_unidade);
create index idx_tracking_11_cod_unidade on tracking_11 (cod_unidade);
create index idx_tracking_12_cod_unidade on tracking_12 (cod_unidade);
create index idx_tracking_16_cod_unidade on tracking_16 (cod_unidade);
create index idx_tracking_53_cod_unidade on tracking_53 (cod_unidade);
create index idx_tracking_56_cod_unidade on tracking_56 (cod_unidade);
create index idx_tracking_62_cod_unidade on tracking_62 (cod_unidade);
create index idx_tracking_68_cod_unidade on tracking_68 (cod_unidade);
create index idx_tracking_69_cod_unidade on tracking_69 (cod_unidade);
create index idx_tracking_70_cod_unidade on tracking_70 (cod_unidade);
create index idx_tracking_73_cod_unidade on tracking_73 (cod_unidade);
create index idx_tracking_76_cod_unidade on tracking_76 (cod_unidade);
create index idx_tracking_109_cod_unidade on tracking_109 (cod_unidade);
create index idx_tracking_116_cod_unidade on tracking_116 (cod_unidade);
create index idx_tracking_142_cod_unidade on tracking_142 (cod_unidade);
create index idx_tracking_205_cod_unidade on tracking_205 (cod_unidade);
create index idx_tracking_208_cod_unidade on tracking_208 (cod_unidade);
create index idx_tracking_211_cod_unidade on tracking_211 (cod_unidade);
create index idx_tracking_216_cod_unidade on tracking_216 (cod_unidade);
create index idx_tracking_217_cod_unidade on tracking_217 (cod_unidade);
create index idx_tracking_358_cod_unidade on tracking_358 (cod_unidade);
create index idx_tracking_375_cod_unidade on tracking_375 (cod_unidade);
create index idx_tracking_390_cod_unidade on tracking_390 (cod_unidade);
create index idx_tracking_460_cod_unidade on tracking_460 (cod_unidade);

insert into tracking
select *
from backup.tracking_old;