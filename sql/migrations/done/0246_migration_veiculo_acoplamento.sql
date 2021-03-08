-- 01_migration_adiciona_categoria_em_diagrama_PL-3215.sql.
-- Adicionado flag para verificar se o Diagrama possui ou não motor (thaisksf - PL-3215).
alter table veiculo_diagrama
    add column motorizado boolean;

update veiculo_diagrama vd
set motorizado = true
where codigo in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19, 24);

update veiculo_diagrama vd
set motorizado = false
where codigo not in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19, 24);

alter table veiculo_diagrama alter column motorizado set not null;

-- 02_migration_cria_estrutura_engate_desengate_PL-3208.sql.
-- Refatora constraints em veiculo_data e veiculo_pneu para utilizar código
alter table veiculo_pneu
    drop constraint fk_veiculo_pneu_veiculo;
alter table veiculo_data
    drop constraint unique_veiculo_unidade_diagrama;

-- Cria constraint unique para fk.
alter table veiculo_data
    add constraint
        unico_veiculo_unidade_diagrama unique (codigo, cod_unidade, cod_diagrama);

alter table veiculo_pneu
    add constraint fk_veiculo_unidade_diagrama
        foreign key (cod_veiculo, cod_unidade, cod_diagrama) references veiculo_data (codigo, cod_unidade, cod_diagrama)
            deferrable;

-- Cria constraint unique para fk.
alter table veiculo_diagrama
    add constraint
        unico_veiculo_diagrama_motorizado unique (codigo, motorizado);

-- Cria type para posicao
create table types.veiculo_acoplamento_posicao
(
    codigo                smallint,
    posicao_legivel_pt_br text                 not null,
    posicao_legivel_es    text                 not null,
    ativo                 boolean default true not null,
    constraint pk_veiculo_acoplamento_posicao primary key (codigo)
);

-- Insere posições.
insert into types.veiculo_acoplamento_posicao (codigo, posicao_legivel_pt_br, posicao_legivel_es, ativo)
values (1, 'Motorizado', 'Motorizado', true),
       (2, 'Engate 1', 'Enganche 1', true),
       (3, 'Engate 2', 'Enganche 2', true),
       (4, 'Engate 3', 'Enganche 3', true),
       (5, 'Engate 4', 'Enganche 4', true),
       (6, 'Engate 5', 'Enganche 5', true);

create type types.veiculo_acoplamento_acao_type as enum ('MUDOU_POSICAO', 'ACOPLADO', 'DESACOPLADO', 'PERMANECEU');

-- Cria type para ação.
create table types.veiculo_acoplamento_acao
(
    acao               types.veiculo_acoplamento_acao_type,
    constraint pk_veiculo_acoplamento_acao primary key (acao),
    acao_legivel_pt_br text    not null,
    acao_legivel_es    text    not null,
    ativo              boolean not null
);

-- Insere ações.
insert into types.veiculo_acoplamento_acao (acao, acao_legivel_pt_br, acao_legivel_es, ativo)
values ('ACOPLADO', 'ACOPLADO', 'ACOPLADO', true),
       ('DESACOPLADO', 'DESACOPLADO', 'DESACOPLADO', true),
       ('PERMANECEU', 'PERMANECEU', 'PERMANECIÓ', true),
       ('MUDOU_POSICAO', 'MUDOU POSIÇÃO', 'POSICIÓN CAMBIADA', true);

-- Cria tabela para processo de acoplamento
create table veiculo_acoplamento_processo
(
    codigo          bigserial,
    cod_unidade     bigint                   not null,
    cod_colaborador bigint                   not null,
    data_hora       timestamp with time zone not null,
    observacao      text,
    constraint pk_veiculo_acoplamento_processo primary key (codigo),
    constraint fk_veiculo_acoplamento_processo_unidade foreign key (cod_unidade)
        references unidade (codigo),
    constraint fk_veiculo_acoplamento_processo_colaborador foreign key (cod_colaborador)
        references colaborador_data (codigo),
    constraint unico_processo_por_unidade unique (codigo, cod_unidade)
);

-- Cria tabela de acoplamento.
create table veiculo_acoplamento_atual
(
    cod_processo bigint   not null,
    cod_unidade  bigint   not null,
    cod_posicao  smallint not null,
    cod_diagrama bigint   not null,
    motorizado   boolean  not null,
    cod_veiculo  bigint   not null,
    constraint fk_processo_unidade foreign key (cod_processo, cod_unidade)
        references veiculo_acoplamento_processo (codigo, cod_unidade),
    constraint fk_veiculo_unidade_diagrama foreign key (cod_veiculo, cod_unidade, cod_diagrama)
        references veiculo_data (codigo, cod_unidade, cod_diagrama),
    constraint fk_posicao foreign key (cod_posicao)
        references types.veiculo_acoplamento_posicao (codigo),
    constraint fk_diagrama_motorizado foreign key (cod_diagrama, motorizado)
        references veiculo_diagrama (codigo, motorizado),
    constraint unica_posicao_processo
        unique (cod_posicao, cod_processo),
    constraint unico_veiculo
        unique (cod_veiculo),
    constraint check_posicao_motorizado
        check ((motorizado is false and cod_posicao <> 1) or
               (motorizado is true and cod_posicao = 1))
);

-- Cria tabela de histórico.
create table veiculo_acoplamento_historico
(
    codigo       bigserial                           not null
        constraint pk_veiculo_acoplamento_historico
            primary key,
    cod_processo bigint                              not null,
    cod_posicao  smallint                            not null,
    cod_diagrama bigint                              not null,
    motorizado   boolean                             not null,
    cod_veiculo  bigint                              not null,
    km_coletado  bigint                              not null,
    acao         types.veiculo_acoplamento_acao_type not null,
    constraint fk_processo_unidade foreign key (cod_processo)
        references veiculo_acoplamento_processo (codigo),
    constraint fk_posicao foreign key (cod_posicao)
        references types.veiculo_acoplamento_posicao (codigo),
    constraint fk_veiculo foreign key (cod_veiculo)
        references veiculo_data (codigo),
    constraint fk_acao foreign key (acao)
        references types.veiculo_acoplamento_acao (acao),
    constraint fk_diagrama_motorizado foreign key (cod_diagrama, motorizado)
        references veiculo_diagrama (codigo, motorizado),
    constraint unico_veiculo_processo_historico
        unique (cod_veiculo, cod_processo),
    constraint check_posicao_motorizado
        check ((motorizado is false and cod_posicao <> 1) or
               (motorizado is true and cod_posicao = 1)),
    constraint unico_processo_posicao_acao
        exclude using gist (cod_processo with =, cod_posicao with =, acao with =),
    constraint unico_codigo_historico_acoplamento_veiculo
        unique (codigo, cod_processo, cod_veiculo)
);
comment on column veiculo_acoplamento_historico.cod_posicao
    is 'Quando a ação for "MUDOU_POSICAO", a posição nesta coluna é a nova posição do veículo no acoplamento.';

-- cria index para garantir que as posições nas ações PERMANECEU e DESACOPLADO sejam únicas.
create unique index unico_processo_posicao_historico_desacoplado on veiculo_acoplamento_historico
    (cod_processo, cod_posicao) where (acao = 'PERMANECEU' or acao = 'DESACOPLADO');
-- cria index para garantir que as posições nas ações PERMANECEU e ACOPLADO sejam únicas.
create unique index unico_processo_posicao_historico_acoplado on veiculo_acoplamento_historico
    (cod_processo, cod_posicao) where (acao = 'PERMANECEU' or acao = 'ACOPLADO');

-- 03_migration_crud_veiculo_possui_hubodometro_PL-3223.sql.
alter table veiculo_data
    add column motorizado boolean;

update veiculo_data
set motorizado = vd.motorizado
from veiculo_diagrama as vd
where cod_diagrama = vd.codigo;

alter table veiculo_diagrama
    add constraint unique_codigo_motorizado_veiculo_diagrama
        unique (codigo, motorizado);

alter table veiculo_data
    add constraint fk_diagrama_motorizado
        foreign key (cod_diagrama, motorizado) references veiculo_diagrama (codigo, motorizado);

alter table veiculo_data
    alter column motorizado set not null;

alter table veiculo_data
    add column possui_hubodometro boolean default false not null;
alter table veiculo_data
    add constraint check_hubodometro
        check ((possui_hubodometro = true and motorizado = false) or (possui_hubodometro = false));

alter table veiculo_data
    alter column possui_hubodometro drop default;

create or replace view veiculo
            (codigo,
             placa,
             identificador_frota,
             cod_unidade,
             cod_empresa,
             km,
             status_ativo,
             cod_diagrama,
             cod_tipo,
             cod_modelo,
             cod_eixos,
             data_hora_cadastro,
             cod_unidade_cadastro,
             foi_editado,
             possui_hubodometro,
             motorizado)
as
SELECT v.codigo,
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
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado
FROM veiculo_data v
WHERE v.deletado = false;

------------------------------------------------------- LISTAGEM ------------------------------------------------------
drop function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
    f_apenas_ativos boolean,
    f_cod_tipo_veiculo bigint);

CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_ALL_BY_UNIDADES(F_COD_UNIDADES BIGINT[],
                                                            F_APENAS_ATIVOS BOOLEAN,
                                                            F_COD_TIPO_VEICULO BIGINT)
    RETURNS TABLE
            (
                CODIGO              BIGINT,
                PLACA               TEXT,
                COD_REGIONAL        BIGINT,
                NOME_REGIONAL       TEXT,
                COD_UNIDADE         BIGINT,
                NOME_UNIDADE        TEXT,
                KM                  BIGINT,
                STATUS_ATIVO        BOOLEAN,
                COD_TIPO            BIGINT,
                COD_MODELO          BIGINT,
                COD_DIAGRAMA        BIGINT,
                IDENTIFICADOR_FROTA TEXT,
                MODELO              TEXT,
                POSSUI_HUBODOMETRO  BOOLEAN,
                MOTORIZADO          BOOLEAN,
                NOME_DIAGRAMA       TEXT,
                DIANTEIRO           BIGINT,
                TRASEIRO            BIGINT,
                TIPO                TEXT,
                MARCA               TEXT,
                COD_MARCA           BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT V.CODIGO                                                AS CODIGO,
       V.PLACA                                                 AS PLACA,
       R.CODIGO                                                AS COD_REGIONAL,
       R.REGIAO                                                AS NOME_REGIONAL,
       U.CODIGO                                                AS COD_UNIDADE,
       U.NOME                                                  AS NOME_UNIDADE,
       V.KM                                                    AS KM,
       V.STATUS_ATIVO                                          AS STATUS_ATIVO,
       V.COD_TIPO                                              AS COD_TIPO,
       V.COD_MODELO                                            AS COD_MODELO,
       V.COD_DIAGRAMA                                          AS COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA                                   AS IDENTIFICADOR_FROTA,
       MV.NOME                                                 AS MODELO,
       V.POSSUI_HUBODOMETRO                                    AS POSSUI_HUBODOMETRO,
       V.MOTORIZADO                                            AS MOTORIZADO,
       VD.NOME                                                 AS NOME_DIAGRAMA,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'D') AS DIANTEIRO,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'T') AS TRASEIRO,
       VT.NOME                                                 AS TIPO,
       MAV.NOME                                                AS MARCA,
       MAV.CODIGO                                              AS COD_MARCA
FROM VEICULO V
         JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = V.COD_DIAGRAMA
         JOIN VEICULO_DIAGRAMA_EIXOS VDE ON VDE.COD_DIAGRAMA = VD.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA
         JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND CASE
          WHEN F_APENAS_ATIVOS IS NULL OR F_APENAS_ATIVOS = FALSE
              THEN TRUE
          ELSE V.STATUS_ATIVO = TRUE
    END
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NULL
              THEN TRUE
          ELSE V.COD_TIPO = F_COD_TIPO_VEICULO
    END
GROUP BY V.PLACA, V.CODIGO, V.CODIGO, V.PLACA, U.CODIGO, V.KM, V.STATUS_ATIVO, V.COD_TIPO, V.COD_MODELO,
         V.MOTORIZADO, V.POSSUI_HUBODOMETRO, V.COD_DIAGRAMA, V.IDENTIFICADOR_FROTA, R.CODIGO, MV.NOME, VD.NOME,
         VT.NOME, MAV.NOME, MAV.CODIGO
ORDER BY V.PLACA;
$$;

---------------------------------------------------BUSCA ESPECIFICO ---------------------------------------------------
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
                possui_hubodometro   boolean,
                motorizado           boolean,
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
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
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
         v.possui_hubodometro,
         v.motorizado,
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

------------------------------------------------------- INSERÇÃO ------------------------------------------------------
drop function func_veiculo_insere_veiculo(f_cod_unidade bigint,
    f_placa text,
    f_identificador_frota text,
    f_km_atual bigint,
    f_cod_modelo bigint,
    f_cod_tipo bigint);

CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_IDENTIFICADOR_FROTA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT,
                                                       F_POSSUI_HUBODOMETRO BOOLEAN)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA           BIGINT;
    V_STATUS_ATIVO CONSTANT BOOLEAN := TRUE;
    V_COD_DIAGRAMA          BIGINT;
    V_COD_VEICULO_PROLOG    BIGINT;
    V_MOTORIZADO            BOOLEAN;
BEGIN
    -- Busca o código da empresa de acordo com a unidade
    V_COD_EMPRESA := (SELECT U.COD_EMPRESA
                      FROM UNIDADE U
                      WHERE U.CODIGO = F_COD_UNIDADE);

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL < 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'A quilometragem do veículo não pode ser um número negativo.');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = V_COD_EMPRESA
                            AND MV.CODIGO = F_COD_MODELO))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o modelo do veículo e tente novamente.');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO
                            AND VT.COD_EMPRESA = V_COD_EMPRESA))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o tipo do veículo e tente novamente.');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo.
    V_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_COD_TIPO
                         AND VT.COD_EMPRESA = V_COD_EMPRESA);

    V_MOTORIZADO := (SELECT VD.MOTORIZADO
                     FROM VEICULO_DIAGRAMA VD
                     WHERE VD.CODIGO = V_COD_DIAGRAMA);

    if (V_MOTORIZADO AND F_POSSUI_HUBODOMETRO)
    then
        perform throw_generic_error('Não é possivel cadastrar um veiculo motorizado com hubodometro, favor verificar.');
    end if;

    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO VEICULO(COD_EMPRESA,
                        COD_UNIDADE,
                        PLACA,
                        KM,
                        STATUS_ATIVO,
                        COD_TIPO,
                        COD_MODELO,
                        COD_DIAGRAMA,
                        MOTORIZADO,
                        COD_UNIDADE_CADASTRO,
                        IDENTIFICADOR_FROTA,
                        POSSUI_HUBODOMETRO)
    VALUES (V_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            V_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            V_COD_DIAGRAMA,
            V_MOTORIZADO,
            F_COD_UNIDADE,
            F_IDENTIFICADOR_FROTA,
            F_POSSUI_HUBODOMETRO)
    RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF V_COD_VEICULO_PROLOG IS NULL OR V_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;
---------------------------------------------------- ATUALIZAÇÃO ------------------------------------------------------
alter table veiculo_edicao_historico
    add column if not exists possui_hubodometro boolean not null default false;

alter table veiculo_edicao_historico
    alter column possui_hubodometro drop default;

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
    f_antigo_possui_hubodometro boolean,
    f_total_edicoes smallint,
    f_cod_colaborador_edicao bigint,
    f_origem_edicao text,
    f_data_hora_edicao timestamp with time zone,
    f_informacoes_extras_edicao text);

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
                                                                   f_antigo_possui_hubodometro boolean,
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
                                          cod_modelo_veiculo,
                                          possui_hubodometro)
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
            f_antigo_cod_modelo,
            f_antigo_possui_hubodometro)
    returning codigo into v_cod_edicao_historico;

    if v_cod_edicao_historico is null or v_cod_edicao_historico <= 0
    then
        perform throw_generic_error('Erro ao gerar o histórico de edição, tente novamente.');
    end if;

    return v_cod_edicao_historico;
end;
$$;


drop function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
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
                                                         f_novo_possui_hubodometro boolean,
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
                antigo_status              boolean,
                antigo_possui_hubodometro  boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant bigint not null  := (select v.cod_empresa
                                                      from veiculo v
                                                      where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant bigint not null  := (select vt.cod_diagrama
                                                      from veiculo_tipo vt
                                                      where vt.codigo = f_novo_cod_tipo
                                                        and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant bigint not null  := (select mv.cod_marca
                                                      from modelo_veiculo mv
                                                      where mv.codigo = f_novo_cod_modelo);
    v_novo_motorizado   constant boolean not null := (select vd.motorizado
                                                      from veiculo_diagrama vd
                                                      where vd.codigo = v_novo_cod_diagrama);
    v_cod_edicao_historico       bigint;
    v_total_edicoes              smallint;
    v_cod_unidade                bigint;
    v_antiga_placa               text;
    v_antigo_identificador_frota text;
    v_antigo_km                  bigint;
    v_antigo_cod_diagrama        bigint;
    v_antigo_cod_tipo            bigint;
    v_antigo_cod_marca           bigint;
    v_antigo_cod_modelo          bigint;
    v_antigo_status              boolean;
    v_antigo_possui_hubodometro  boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
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
                                                  f_novo_status,
                                                  f_novo_possui_hubodometro)) - hstore((v_antigo_identificador_frota,
                                                                                        v_antigo_km,
                                                                                        v_antigo_cod_diagrama,
                                                                                        v_antigo_cod_tipo,
                                                                                        v_antigo_cod_marca,
                                                                                        v_antigo_cod_modelo,
                                                                                        v_antigo_status,
                                                                                        v_antigo_possui_hubodometro))));

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
                                                                          v_antigo_possui_hubodometro,
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
            motorizado          = v_novo_motorizado,
            possui_hubodometro  = f_novo_possui_hubodometro,
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
               v_antigo_status,
               v_antigo_possui_hubodometro;
end;
$$;
----------------------------------------------------- INTEGRAÇÕES -----------------------------------------------------
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
    v_novo_possui_hubodometro boolean;
begin
    -- Não usamos 'strict' propositalmente pois não queremos quebrar no select. Deixamos as próprias validações da
    -- function verificarem e quebrarem.
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.km,
           vd.cod_tipo,
           vd.cod_modelo,
           vd.possui_hubodometro
    into
        v_cod_empresa_veiculo,
        v_cod_unidade_veiculo,
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_novo_km_veiculo,
        v_novo_cod_tipo_veiculo,
        v_novo_cod_modelo_veiculo,
        v_novo_possui_hubodometro
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
                                          v_novo_possui_hubodometro,
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
    v_novo_possui_hubodometro   boolean;
begin
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.status_ativo,
           vd.possui_hubodometro
    into strict
        v_cod_empresa_veiculo,
        v_cod_unidade_atual_veiculo,
        v_cod_veiculo_prolog,
        v_novo_identificador_frota,
        v_novo_status_veiculo,
        v_novo_possui_hubodometro
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
                                          v_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          now(),
                                          null);
end;
$$;

drop function integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
    f_placa_original_veiculo text,
    f_novo_cod_unidade_alocado bigint,
    f_nova_placa_veiculo text,
    f_novo_km_veiculo bigint,
    f_novo_cod_modelo_veiculo bigint,
    f_novo_cod_tipo_veiculo bigint,
    f_data_hora_edicao_veiculo timestamp with time zone,
    f_token_integracao text);

create or replace function
    integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
                                                    f_placa_original_veiculo text,
                                                    f_novo_cod_unidade_alocado bigint,
                                                    f_nova_placa_veiculo text,
                                                    f_novo_km_veiculo bigint,
                                                    f_novo_cod_modelo_veiculo bigint,
                                                    f_novo_cod_tipo_veiculo bigint,
                                                    f_data_hora_edicao_veiculo timestamp with time zone,
                                                    f_token_integracao text,
                                                    f_novo_possui_hubodometro boolean default false)
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
                                          f_novo_possui_hubodometro,
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

drop function integracao.func_veiculo_insere_veiculo_prolog(f_cod_unidade_veiculo_alocado bigint,
    f_placa_veiculo_cadastrado text,
    f_km_atual_veiculo_cadastrado bigint,
    f_cod_modelo_veiculo_cadastrado bigint,
    f_cod_tipo_veiculo_cadastrado bigint,
    f_data_hora_veiculo_cadastro timestamp with time zone,
    f_token_integracao text);
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(F_COD_UNIDADE_VEICULO_ALOCADO BIGINT,
                                                  F_PLACA_VEICULO_CADASTRADO TEXT,
                                                  F_KM_ATUAL_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
                                                  F_COD_TIPO_VEICULO_CADASTRADO BIGINT,
                                                  F_DATA_HORA_VEICULO_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                  F_TOKEN_INTEGRACAO TEXT,
                                                  F_POSSUI_HUBODOMETRO BOOLEAN DEFAULT FALSE)
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
    V_INSERT_COD_DIAGRAMA              BIGINT;
    V_INSERT_MOTORIZADO                BOOLEAN;
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
        SELECT VT.COD_DIAGRAMA, VD.MOTORIZADO
        FROM PUBLIC.VEICULO_TIPO VT
                 JOIN VEICULO_DIAGRAMA VD
                      ON VD.CODIGO = VT.COD_DIAGRAMA
        WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO
          AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO
        INTO V_INSERT_COD_DIAGRAMA, V_INSERT_MOTORIZADO;
        -- Aqui devemos apenas inserir o veículo no ProLog.
        INSERT INTO PUBLIC.VEICULO(COD_EMPRESA,
                                   COD_UNIDADE,
                                   PLACA,
                                   KM,
                                   STATUS_ATIVO,
                                   COD_TIPO,
                                   COD_DIAGRAMA,
                                   MOTORIZADO,
                                   COD_MODELO,
                                   COD_UNIDADE_CADASTRO,
                                   POSSUI_HUBODOMETRO)
        VALUES (COD_EMPRESA_VEICULO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_PLACA_VEICULO_CADASTRADO,
                F_KM_ATUAL_VEICULO_CADASTRADO,
                STATUS_ATIVO_VEICULO,
                F_COD_TIPO_VEICULO_CADASTRADO,
                V_INSERT_COD_DIAGRAMA,
                V_INSERT_MOTORIZADO,
                F_COD_MODELO_VEICULO_CADASTRADO,
                F_COD_UNIDADE_VEICULO_ALOCADO,
                F_POSSUI_HUBODOMETRO)
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


-- 04_migration_veiculos_acoplados_listagem_PL-3211.sql.
-- PL-3211.

-- Tabela não tem index para cod_unidade e em produção pode ser útil pois é utilizado na query abaixo.
create index idx_veiculo_acoplamento_atual_cod_unidade on veiculo_acoplamento_atual (cod_unidade);

-- A query performou melhor utilizando plpgsql ao invés de sql.
create or replace function func_veiculo_get_veiculos_acoplados_unidades(f_cod_unidades bigint[],
                                                                        f_apenas_veiculos_ativos boolean,
                                                                        f_cod_tipo_veiculo bigint)
    returns table
            (
                cod_processo_acoplamento bigint,
                cod_veiculo              bigint,
                placa                    text,
                identificador_frota      text,
                motorizado               boolean,
                posicao_acoplado         smallint
            )
    language plpgsql
as
$$
begin
    return query
        select vaa.cod_processo              as cod_processo_acoplamento,
               v.codigo                      as cod_veiculo,
               v.placa :: text               as placa,
               v.identificador_frota :: text as identificador_frota,
               v.motorizado                  as motorizado,
               vaa.cod_posicao               as posicao_acoplado
        from veiculo_acoplamento_atual vaa
                 join veiculo v on vaa.cod_veiculo = v.codigo
        where vaa.cod_unidade = any (f_cod_unidades)
          and case
                  when f_apenas_veiculos_ativos is null or f_apenas_veiculos_ativos = false
                      then true
                  else v.status_ativo = true
            end
          and case
                  when f_cod_tipo_veiculo is null
                      then true
                  else v.cod_tipo = f_cod_tipo_veiculo
            end
        order by vaa.cod_processo, vaa.cod_posicao;
end;
$$;

-- 05_migration_veiculos_acoplados_visualizacao_PL-3212.sql.
-- PL-3212.

-- Altera ordem da constraint de (cod_posicao, cod_processo) para (cod_processo, cod_posicao) para que em um filtro
-- por cod_processo na tabela seja mais performático e provável ser utilizado o index de unique.
alter table veiculo_acoplamento_atual
    drop constraint unica_posicao_processo,
    add constraint unica_posicao_processo unique (cod_processo, cod_posicao);

-- A query performou melhor utilizando plpgsql ao invés de sql.
create or replace function func_veiculo_get_veiculos_acoplados(f_cod_veiculo bigint)
    returns table
            (
                cod_processo_acoplamento bigint,
                cod_veiculo              bigint,
                placa                    text,
                identificador_frota      text,
                motorizado               boolean,
                posicao_acoplado         smallint
            )
    language plpgsql
as
$$
begin
    return query
        select vaa.cod_processo              as cod_processo_acoplamento,
               v.codigo                      as cod_veiculo,
               v.placa :: text               as placa,
               v.identificador_frota :: text as identificador_frota,
               v.motorizado                  as motorizado,
               vaa.cod_posicao               as posicao_acoplado
        from veiculo_acoplamento_atual vaa
                 join veiculo v on vaa.cod_veiculo = v.codigo
        where vaa.cod_processo = (select q.cod_processo
                                  from veiculo_acoplamento_atual q
                                  where q.cod_veiculo = f_cod_veiculo)
        order by vaa.cod_posicao;
end;
$$;

-- 06_migration_refactor_unica_func_salva_km_PL-3213.sql.
-- Necessário modificar a view de veículo para conter flag motorizado.
create or replace view veiculo
            (codigo,
             placa,
             identificador_frota,
             cod_unidade,
             cod_empresa,
             km,
             status_ativo,
             cod_diagrama,
             cod_tipo,
             cod_modelo,
             cod_eixos,
             data_hora_cadastro,
             cod_unidade_cadastro,
             foi_editado,
             possui_hubodometro,
             motorizado)
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
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado
from veiculo_data v
where v.deletado = false;

-- Cria Enum de processos.
create type types.veiculo_processo_type as enum (
    'ACOPLAMENTO',
    'AFERICAO',
    'FECHAMENTO_SERVICO_PNEU',
    'CHECKLIST',
    'FECHAMENTO_ITEM_CHECKLIST',
    'EDICAO_DE_VEICULOS',
    'MOVIMENTACAO',
    'SOCORRO_EM_ROTA',
    'TRANSFERENCIA_DE_VEICULOS');

-- Cria Type.
create table types.veiculo_processo
(
    processo               types.veiculo_processo_type not null
        constraint pk_veiculo_processo
            primary key,
    processo_legivel_pt_br text                        not null,
    processo_legivel_es    text                        not null,
    ativo                  boolean                     not null
);

-- Insere types.
insert into types.veiculo_processo (processo, processo_legivel_pt_br, processo_legivel_es, ativo)
values ('ACOPLAMENTO', 'ACOPLAMENTO', 'ACOPLAMIENTO', true),
       ('AFERICAO', 'AFERIÇÃO', 'MEDIDA', true),
       ('FECHAMENTO_SERVICO_PNEU', 'FECHAMENTO SERVIÇO PNEU', 'CERRAR SERVICIOS NEUMATICOS', true),
       ('CHECKLIST', 'CHECKLIST', 'CHECKLIST', true),
       ('FECHAMENTO_ITEM_CHECKLIST', 'FECHAMENTO ITEM CHECKLIST', 'CERRAR ELEMENTOS CHECKLIST', true),
       ('EDICAO_DE_VEICULOS', 'EDIÇÃO DE VEÍCULOS', 'EDICIÓN DE VEHÍCULOS', true),
       ('MOVIMENTACAO', 'MOVIMENTAÇÃO', 'MOVIMIENTO', true),
       ('SOCORRO_EM_ROTA', 'SOCORRO EM ROTA', 'AYUDA EN RUTA ', true),
       ('TRANSFERENCIA_DE_VEICULOS', 'TRANSFERÊNCIA DE VEÍCULOS', 'TRANSFERENCIA DE VEHICULO ', true);

-- Function para realizar update do km.
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_tipo_processo types.veiculo_processo_type,
                                                        f_deve_propagar_km boolean)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                 bigint;
    v_diferenca_km             bigint;
    v_km_motorizado            bigint;
    v_possui_hubodometro       boolean;
    v_motorizado               boolean;
    v_cod_processo_acoplamento bigint;
    v_cod_veiculos_acoplados   bigint[];
begin
    if not f_deve_propagar_km
    then
        update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
        return f_km_coletado;
    end if;

    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.cod_unidade = f_cod_unidade
      and v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento;
    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    v_cod_veiculos_acoplados = (select array_agg(vaa.cod_veiculo)

                                                from veiculo_acoplamento_atual vaa
                                                         join veiculo v
                                                              on vaa.cod_unidade = v.cod_unidade
                                                                  and vaa.cod_veiculo = v.codigo
                                                where vaa.cod_unidade = f_cod_unidade
                                                  and vaa.cod_processo = v_cod_processo_acoplamento
                                                  and v.possui_hubodometro is false);
                end if;
                case when ((v_possui_hubodometro is false and v_motorizado is false and
                            v_cod_processo_acoplamento is null))
                    then
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        return f_km_coletado;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;

-- 07_migration_insert_processo_acoplamento_PL-3210.sql.
-- PL-3210.
create or replace function func_veiculo_remove_acoplamento_atual(f_cod_processo_acoplamento bigint)
    returns void
    language plpgsql
as
$$
begin
    delete
    from veiculo_acoplamento_atual
    where cod_processo = f_cod_processo_acoplamento;

    if not found
    then
        perform throw_server_side_error(format('Erro ao deletar estado atual de acoplamento para o process:
                                               %s.', f_cod_processo_acoplamento));
    end if;
end;
$$;

create or replace function func_veiculo_insert_processo_acoplamento(f_cod_unidade bigint,
                                                                    f_cod_colaborador_realizacao bigint,
                                                                    f_data_hora_atual timestamp with time zone,
                                                                    f_observacao text)
    returns bigint
    language sql
as
$$
insert into veiculo_acoplamento_processo(cod_unidade,
                                         cod_colaborador,
                                         data_hora,
                                         observacao)
values (f_cod_unidade,
        f_cod_colaborador_realizacao,
        f_data_hora_atual,
        f_observacao)
returning codigo as codigo;
$$;

create or replace function func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
                                                                     f_cod_veiculo bigint,
                                                                     f_cod_diagrama_veiculo bigint,
                                                                     f_posicao_acao_realizada smallint,
                                                                     f_veiculo_motorizado boolean,
                                                                     f_km_coletado bigint,
                                                                     f_acao_realizada text)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_historico(cod_processo,
                                              cod_veiculo,
                                              cod_diagrama,
                                              cod_posicao,
                                              motorizado,
                                              km_coletado,
                                              acao)
    values (f_cod_processo_acoplamento,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acao_realizada,
            f_veiculo_motorizado,
            f_km_coletado,
            f_acao_realizada::types.veiculo_acoplamento_acao_type);

    if not found
    then
        perform throw_server_side_error('Erro ao inserir histórico de acoplamento para o veículo: %s.', f_cod_veiculo);
    end if;
end;
$$;

create or replace function func_veiculo_insert_estado_atual_acoplamentos(f_cod_processo_acoplamento bigint,
                                                                         f_cod_unidade bigint,
                                                                         f_cod_veiculo bigint,
                                                                         f_cod_diagrama_veiculo bigint,
                                                                         f_posicao_acoplamento smallint,
                                                                         f_veiculo_motorizado boolean)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_atual(cod_processo,
                                          cod_unidade,
                                          cod_veiculo,
                                          cod_diagrama,
                                          cod_posicao,
                                          motorizado)
    values (f_cod_processo_acoplamento,
            f_cod_unidade,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acoplamento,
            f_veiculo_motorizado);

    if not found
    then
        perform throw_server_side_error('Erro ao inserir estado atual de acoplamento para o veículo: %s.',
                                        f_cod_veiculo);
    end if;
end;
$$;

-- 08_migration_feat_busca_historico_acoplamentos_PL-3209.sql.
-- Sobre:
-- Esta function retorna os processos de acoplamento que foram realizados de acordo com os filtros aplicados.
--
-- Histórico:
-- 2020-11-04 -> Function criada (thaisksf - PL-3209).
create or replace function func_veiculo_busca_veiculo_acoplamento_historico(f_cod_unidades bigint[],
                                                                            f_cod_veiculos bigint[],
                                                                            f_data_inicial date,
                                                                            f_data_final date)
    returns table
            (
                cod_processo        bigint,
                nome_unidade        text,
                nome_colaborador    text,
                placa               text,
                identificador_frota text,
                km                  bigint,
                cod_posicao         smallint,
                nome_posicao        text,
                acao                text,
                data_hora           timestamp without time zone,
                observacao          text
            )
    language plpgsql
as
$$
begin
    return query
        select vap.codigo                                             as cod_processo,
               u.nome ::text                                          as unidade,
               c.nome ::text                                          as colaborador,
               v.placa ::text                                         as placa,
               v.identificador_frota ::text                           as identificador_frota,
               vah.km_veiculo                                        as km,
               vah.cod_posicao                                        as cod_posicao,
               vapo.posicao_legivel_pt_br ::text                      as posicao_legivel_pt_br,
               vah.acao ::text                                        as acao,
               vap.data_hora at time zone tz_unidade(vap.cod_unidade) as data_hora,
               vap.observacao ::text                                  as observacao
        from veiculo_acoplamento_processo vap
                 join veiculo_acoplamento_historico vah on vap.cod_unidade = any (f_cod_unidades)
            and vap.codigo = vah.cod_processo
                 join types.veiculo_acoplamento_posicao vapo on vapo.codigo = vah.cod_posicao
                 join veiculo v on v.codigo = vah.cod_veiculo
                 join unidade u on u.codigo = vap.cod_unidade
                 join colaborador c on vap.cod_colaborador = c.codigo
        where vap.cod_unidade = any (f_cod_unidades)
          and case
                  when f_cod_veiculos is not null
                      then
                          vah.cod_processo in (select h.cod_processo
                                               from veiculo_acoplamento_historico h
                                               where h.cod_veiculo = any (f_cod_veiculos))
                  else
                      true
            end
          and case
                  when (f_data_inicial is not null and f_data_final is not null)
                      then
                      (vap.data_hora at time zone tz_unidade(vap.cod_unidade)) :: date
                          between f_data_inicial and f_data_final
                  else
                      true
            end
        order by unidade, cod_processo, cod_posicao;
end;
$$;

-- 09_migration_cria_func_busca_estado_coleta_km_por_cod_veiculo_PL-3291.
create or replace function func_veiculo_busca_dados_coleta_km_por_cod_veiculo(f_cod_veiculo bigint)
returns table (cod_veiculo                 bigint,
                placa                      text,
                motorizado                 boolean,
                km_atual                   bigint,
                identificador_frota        text,
                possui_hubodometro         boolean,
                acoplado                   boolean,
                deve_coletar_km            boolean,
                cod_veiculo_trator         bigint,
                placa_trator               text,
                km_atual_trator            bigint,
                identificador_frota_trator text)
language plpgsql
as
$$
    declare
        v_posicao_trator constant bigint not null  := 1;

        v_placa                               text;
        v_motorizado                          boolean;
        v_km_atual                            bigint;
        v_identificador_frota                 text;
        v_possui_hubodometro                  boolean;
        v_deve_coletar_km                     boolean;

        v_cod_processo  constant              bigint := (select vaa.cod_processo
                                                              from veiculo_acoplamento_atual vaa
                                                              where vaa.cod_veiculo = f_cod_veiculo);
        v_acoplado      constant              boolean := v_cod_processo is not null;
        v_cod_veiculo_trator_processo         bigint;
        v_placa_veiculo_trator_processo       text;
        v_km_atual_trator_processo            bigint;
        v_identificador_frota_trator_processo text;

    begin
        select v.placa,
               v.motorizado,
               v.km,
               v.identificador_frota,
               v.possui_hubodometro
        from veiculo v
        where v.codigo = f_cod_veiculo
        into
        v_placa,
        v_motorizado,
        v_km_atual,
        v_identificador_frota,
        v_possui_hubodometro;

        if(not v_motorizado and not v_possui_hubodometro)
        then
             select vaa.cod_veiculo
               from veiculo_acoplamento_historico vaa
               where vaa.cod_processo = v_cod_processo
               and vaa.cod_posicao = v_posicao_trator
        into v_cod_veiculo_trator_processo;
              select v.codigo,
                     v.placa,
                     v.km,
                     v.identificador_frota
            from veiculo v
            where v_cod_veiculo_trator_processo is not null
                and v.codigo = v_cod_veiculo_trator_processo
            into v_cod_veiculo_trator_processo,
                v_placa_veiculo_trator_processo,
                 v_km_atual_trator_processo,
                 v_identificador_frota_trator_processo;
        end if;

        v_deve_coletar_km := f_if(not v_motorizado and
                                  not v_possui_hubodometro and
                                  (not v_acoplado or
                                   v_cod_veiculo_trator_processo is null), false, true);
        return query
            select f_cod_veiculo,
                   v_placa,
                   v_motorizado,
                   v_km_atual,
                   v_identificador_frota,
                   v_possui_hubodometro,
                   v_acoplado,
                   v_deve_coletar_km,
                   v_cod_veiculo_trator_processo,
                   v_placa_veiculo_trator_processo,
                   v_km_atual_trator_processo,
                   v_identificador_frota_trator_processo;
    end;
$$;

-- 10_migration_cria_tabela_historico_km_propagado_PL-3314.sql.
create table veiculo_processo_km_historico
(
    codigo                             bigserial                not null
        constraint pk_veiculo_km_propagacao
            primary key,
    cod_unidade                        bigint                   not null
        constraint fk_unidade
            references unidade (codigo),
    cod_historico_processo_acoplamento bigint                   null
        constraint fk_veiculo_acoplamento_historico
            references veiculo_acoplamento_historico (codigo),
    cod_processo_acoplamento           bigint                   null,
    cod_processo_veiculo               bigint                   not null,
    tipo_processo_veiculo              text                     not null,
    cod_veiculo                        bigint                   not null
        constraint fk_veiculo
            references veiculo_data (codigo),
    veiculo_fonte_processo             boolean                  not null,
    motorizado                         boolean                  not null,
    km_antigo                          bigint                   not null,
    km_final                           bigint                   not null,
    km_coletado_processo               bigint                   not null,
    data_hora_processo                 timestamp with time zone not null,
    constraint unique_cod_processo_tipo_processo_cod_veiculo
        unique (cod_processo_veiculo, tipo_processo_veiculo, cod_veiculo),
    constraint fk_historico_veiculo_acoplamento_historico_veiculo
        foreign key (cod_historico_processo_acoplamento, cod_processo_acoplamento, cod_veiculo)
            references veiculo_acoplamento_historico (codigo, cod_processo, cod_veiculo),
    constraint check_motorizado_cod_acoplamento check (
            (motorizado = true and cod_processo_acoplamento is not null)
            or (motorizado = false))
);

create or replace view veiculo_km_propagacao as
select codigo,
       cod_veiculo,
       cod_processo_acoplamento,
       cod_historico_processo_acoplamento,
       cod_processo_veiculo,
       tipo_processo_veiculo,
       cod_unidade,
       veiculo_fonte_processo,
       motorizado,
       km_antigo,
       km_final,
       km_coletado_processo,
       data_hora_processo
from veiculo_processo_km_historico
where km_antigo <> km_final
  and veiculo_fonte_processo = false;

-- 11_migration_adiciona_flag_veiculo_acoplado_PL-3320.sql.
-- Modificações estruturais.
alter table veiculo_data
    add column acoplado boolean default false not null;

alter table veiculo_acoplamento_atual
    add column acoplado boolean default true not null;

alter table veiculo_acoplamento_atual
    add constraint check_acoplado_true check (acoplado is true);

alter table veiculo_data
    add constraint unico_veiculo_acoplado unique (codigo, acoplado);

update veiculo_data vd
set acoplado = true
where vd.codigo in (select vaa.cod_veiculo from veiculo_acoplamento_atual vaa);

alter table veiculo_acoplamento_atual
    add constraint fk_veiculo_acoplado foreign key (cod_veiculo, acoplado)
        references veiculo_data (codigo, acoplado) deferrable initially deferred;

-- Adiciona flag na view de veículo
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
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado,
       v.acoplado
from veiculo_data v
where v.deletado = false;

-- Faz com que a func de insert de acoplamento altere a flag em veículo_data para true antes de acoplar o veiculo.
create or replace function func_veiculo_insert_estado_atual_acoplamentos(f_cod_processo_acoplamento bigint,
                                                                         f_cod_unidade bigint,
                                                                         f_cod_veiculo bigint,
                                                                         f_cod_diagrama_veiculo bigint,
                                                                         f_posicao_acoplamento smallint,
                                                                         f_veiculo_motorizado boolean)
    returns void
    language plpgsql
as
$$
begin
    update veiculo_data vd
    set acoplado = true
    where vd.cod_unidade = f_cod_unidade
      and vd.codigo = f_cod_veiculo;

    insert into veiculo_acoplamento_atual(cod_processo,
                                          cod_unidade,
                                          cod_veiculo,
                                          cod_diagrama,
                                          cod_posicao,
                                          motorizado)
    values (f_cod_processo_acoplamento,
            f_cod_unidade,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acoplamento,
            f_veiculo_motorizado);

    if not found
    then
        perform throw_server_side_error(format(
                'Erro ao inserir estado atual de acoplamento para o veículo: %s.', f_cod_veiculo));
    end if;
end;
$$;

alter table veiculo_acoplamento_historico
    rename column km_coletado to km_veiculo;

drop function if exists func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
    f_cod_veiculo bigint,
    f_cod_diagrama_veiculo bigint,
    f_posicao_acao_realizada smallint,
    f_veiculo_motorizado boolean,
    f_km_coletado bigint,
    f_acao_realizada text);
-- Faz com que a func de deleção de acoplamento altere a flag em veículo_data para false.
create or replace function func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
                                                                     f_cod_veiculo bigint,
                                                                     f_cod_diagrama_veiculo bigint,
                                                                     f_posicao_acao_realizada smallint,
                                                                     f_veiculo_motorizado boolean,
                                                                     f_acao_realizada text)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_historico(cod_processo,
                                              cod_veiculo,
                                              cod_diagrama,
                                              cod_posicao,
                                              motorizado,
                                              km_veiculo,
                                              acao)
    values (f_cod_processo_acoplamento,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acao_realizada,
            f_veiculo_motorizado,
            (select v.km from veiculo v where v.codigo = f_cod_veiculo),
            f_acao_realizada::types.veiculo_acoplamento_acao_type);

    if (f_acao_realizada::types.veiculo_acoplamento_acao_type = ('DESACOPLADO'::types.veiculo_acoplamento_acao_type))
    then
        update veiculo_data vd
        set acoplado = false
        where vd.codigo = f_cod_veiculo;
    end if;

    if not found
    then
        perform throw_server_side_error(format('Erro ao inserir histórico de acoplamento para o veículo: %s.',
                                               f_cod_veiculo));
    end if;
end;
$$;

-- 12_migration_func_salva_historico_PL-3315.sql.
drop function if exists func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
    f_cod_historico_processo_acoplamento bigint,
    f_cod_processo_acoplamento bigint,
    f_cod_veiculo_propagado bigint,
    f_motorizado boolean,
    f_veiculo_fonte_processo boolean,
    f_km_antigo bigint,
    f_km_final bigint,
    f_km_coletado bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_cod_processo bigint,
    f_data_hora timestamp with time zone);

create or replace function func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
                                                                      f_cod_historico_processo_acoplamento bigint,
                                                                      f_cod_processo_acoplamento bigint,
                                                                      f_cod_veiculo_propagado bigint,
                                                                      f_motorizado boolean,
                                                                      f_veiculo_fonte_processo boolean,
                                                                      f_km_antigo bigint,
                                                                      f_km_final bigint,
                                                                      f_km_coletado bigint,
                                                                      f_tipo_processo types.veiculo_processo_type,
                                                                      f_cod_processo bigint,
                                                                      f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_historico_propagacao bigint;
begin
    insert into veiculo_processo_km_historico (cod_unidade,
                                               cod_historico_processo_acoplamento,
                                               cod_processo_acoplamento,
                                               cod_processo_veiculo,
                                               tipo_processo_veiculo,
                                               cod_veiculo,
                                               motorizado,
                                               veiculo_fonte_processo,
                                               km_antigo,
                                               km_final,
                                               km_coletado_processo,
                                               data_hora_processo)
    values (f_cod_unidade,
            f_cod_historico_processo_acoplamento,
            f_cod_processo_acoplamento,
            f_cod_processo,
            f_tipo_processo,
            f_cod_veiculo_propagado,
            f_motorizado,
            f_veiculo_fonte_processo,
            f_km_antigo,
            f_km_final,
            f_km_coletado,
            f_data_hora)
    returning codigo into v_cod_historico_propagacao;
    return v_cod_historico_propagacao;
end;
$$;


drop function func_veiculo_update_km_atual(f_cod_unidade bigint,
    f_cod_veiculo bigint,
    f_km_coletado bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_deve_propagar_km boolean);

create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo types.veiculo_processo_type,
                                                        f_deve_propagar_km boolean,
                                                        f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                           bigint;
    v_diferenca_km                       bigint;
    v_km_motorizado                      bigint;
    v_possui_hubodometro                 boolean;
    v_motorizado                         boolean;
    v_cod_processo_acoplamento           bigint;
    v_cod_historico_processo_acoplamento bigint[];
    v_cod_veiculos_acoplados             bigint[];
    v_km_veiculos_acoplados              bigint[];
    v_veiculos_motorizados               boolean[];
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.cod_unidade = f_cod_unidade
      and v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento;

    if not f_deve_propagar_km
    then
        if v_km_atual < f_km_coletado
        then
            update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
            return f_km_coletado;
        end if;
    end if;

    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    select array_agg(vaa.cod_veiculo), array_agg(v.motorizado), array_agg(v.km), array_agg(vah.codigo)
                    from veiculo_acoplamento_atual vaa
                             join veiculo v
                                  on vaa.cod_unidade = v.cod_unidade
                                      and vaa.cod_veiculo = v.codigo
                             inner join veiculo_acoplamento_historico vah on vaa.cod_processo = vah.cod_processo
                        and vaa.cod_veiculo = vah.cod_veiculo
                    where vaa.cod_unidade = f_cod_unidade
                      and vaa.cod_processo = v_cod_processo_acoplamento
                      and v.possui_hubodometro is false
                    into v_cod_veiculos_acoplados,
                        v_veiculos_motorizados,
                        v_km_veiculos_acoplados,
                        v_cod_historico_processo_acoplamento;
                end if;
                case when (v_possui_hubodometro is false and v_motorizado is false and
                           v_cod_processo_acoplamento is null)
                    then
                        perform func_veiculo_salva_historico_km_propagacao(
                                f_cod_unidade,
                                null,
                                v_cod_processo_acoplamento,
                                f_cod_veiculo,
                                v_motorizado,
                                true,
                                v_km_atual,
                                v_km_atual,
                                f_km_coletado,
                                f_tipo_processo,
                                f_cod_processo,
                                f_data_hora);
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        perform func_veiculo_salva_historico_km_propagacao(
                                                                f_cod_unidade,
                                                                unnest(v_cod_historico_processo_acoplamento),
                                                                v_cod_processo_acoplamento,
                                                                unnest(v_cod_veiculos_acoplados),
                                                                unnest(v_veiculos_motorizados),
                                                                (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                                unnest(v_km_veiculos_acoplados),
                                                                unnest(v_km_veiculos_acoplados),
                                                                f_km_coletado,
                                                                f_tipo_processo,
                                                                f_cod_processo,
                                                                f_data_hora);
                                                        return v_km_atual;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados) + v_diferenca_km,
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados),
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;

-- 13_migration_impede_delecao_logica_veiculos_acoplados_PL-3289.sql.
-- Impede deleção de veículos acoplados.
alter table veiculo_data
    add constraint check_veiculo_deletado_acoplado
        check (acoplado is false or (acoplado is true and deletado is false));

-- Impede deleção de veículos acoplados.
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
begin
    perform suporte.func_historico_salva_execucao();
    -- VERIFICA SE UNIDADE EXISTE;
    perform func_garante_unidade_existe(f_cod_unidade);

    -- VERIFICA SE VEÍCULO EXISTE.
    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- VERIFICA SE VEÍCULO POSSUI PNEU APLICADOS.
    if EXISTS(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- VERIFICA SE POSSUI ACOPLAMENTO.
    if EXISTS(select vd.codigo
              from veiculo_data vd
              where vd.placa = f_placa
                and vd.cod_unidade = f_cod_unidade
                and vd.acoplado is true)
    then
        raise exception 'Erro! A Placa: % possui acoplamentos. Favor removê-los', f_placa;
    end if;

    -- VERIFICA SE PLACA POSSUI AFERIÇÃO.
    if EXISTS(select a.codigo from afericao_data a where a.placa_veiculo = f_placa)
    then
        -- COLETAMOS TODOS OS COD_AFERICAO QUE A PLACA POSSUI.
        select ARRAY_AGG(a.codigo)
        from afericao_data a
        where a.placa_veiculo = f_placa
        into v_lista_cod_afericao_placa;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- DELETAMOS AFERIÇÃO.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    if EXISTS(select c.placa_veiculo from checklist_data c where c.placa_veiculo = f_placa)
    then
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        select ARRAY_AGG(c.codigo)
        from checklist_data c
        where c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- DELETA COD_CHECK EM COS.
        update checklist_ordem_servico_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_checklist = any (v_lista_cod_check_placa);

        -- BUSCO OS CODIGO PROLOG DELETADOS EM COS.
        select ARRAY_AGG(codigo_prolog)
        from checklist_ordem_servico_data
        where cod_checklist = any (v_lista_cod_check_placa)
          and cod_unidade = f_cod_unidade
          and deletado is true
        into v_lista_cod_prolog_deletado_cos;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
            loop
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                update checklist_ordem_servico_itens_data
                set deletado            = true,
                    data_hora_deletado  = NOW(),
                    pg_username_delecao = SESSION_USER,
                    motivo_delecao      = f_motivo_delecao
                where deletado = false
                  and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                               from checklist_ordem_servico_data cos
                                               where cos.codigo_prolog = v_codigo_loop);
            end loop;

        -- DELETA TODOS CHECKLIST DA PLACA.
        update checklist_data
        set deletado            = true,
            data_hora_deletado  = NOW(),
            pg_username_delecao = SESSION_USER,
            motivo_delecao      = f_motivo_delecao
        where placa_veiculo = f_placa
          and deletado = false
          and codigo = any (v_lista_cod_check_placa);
    end if;

    -- Verifica se a placa é integrada.
    if EXISTS(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa. (Não possuímos deleção lógica)
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- REALIZA DELEÇÃO DA PLACA.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = NOW(),
        pg_username_delecao = SESSION_USER,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- MENSAGEM DE SUCESSO.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;

-- 14_migration_altera_estrutura_fechamento_os_PL-3335.sql
create sequence if not exists codigo_resolucao_item_os minvalue 0 increment 1 no cycle;
alter table checklist_ordem_servico_itens_data
    add column if not exists cod_processo bigint;
alter table checklist_ordem_servico_itens_data
    add constraint cod_processo unique (codigo, cod_unidade, cod_processo);

create or replace view checklist_ordem_servico_itens as
select cosi.cod_unidade,
       cosi.codigo,
       cosi.cod_os,
       cosi.cpf_mecanico,
       cosi.cod_pergunta_primeiro_apontamento,
       cosi.cod_contexto_pergunta,
       cosi.cod_contexto_alternativa,
       cosi.cod_alternativa_primeiro_apontamento,
       cosi.status_resolucao,
       cosi.qt_apontamentos,
       cosi.km,
       cosi.data_hora_conserto,
       cosi.data_hora_inicio_resolucao,
       cosi.data_hora_fim_resolucao,
       cosi.tempo_realizacao,
       cosi.feedback_conserto,
       cosi.cod_processo
from checklist_ordem_servico_itens_data cosi
where cosi.deletado = false;

drop function if exists func_checklist_os_resolver_itens(f_cod_unidade bigint,
                                                            f_cod_itens bigint[],
                                                            f_cpf bigint,
                                                            f_tempo_realizacao bigint,
                                                            f_km bigint,
                                                            f_status_resolucao text,
                                                            f_data_hora_conserto timestamp with time zone,
                                                            f_data_hora_inicio_resolucao timestamp with time zone,
                                                            f_data_hora_fim_resolucao timestamp with time zone,
                                                            f_feedback_conserto text);
create or replace function func_checklist_os_resolver_itens(f_cod_unidade bigint,
                                                            f_cod_veiculo bigint,
                                                            f_cod_itens bigint[],
                                                            f_cpf bigint,
                                                            f_tempo_realizacao bigint,
                                                            f_km bigint,
                                                            f_status_resolucao text,
                                                            f_data_hora_conserto timestamp with time zone,
                                                            f_data_hora_inicio_resolucao timestamp with time zone,
                                                            f_data_hora_fim_resolucao timestamp with time zone,
                                                            f_feedback_conserto text) returns bigint
    language plpgsql
as
$$
declare
    v_cod_item                  bigint;
    v_data_realizacao_checklist timestamp with time zone;
    v_alternativa_item          text;
    v_error_message             text                                 := E'Erro! A data de resolução %s não pode ser anterior a data de abertura %s do item "%s".';
    v_qtd_linhas_atualizadas    bigint;
    v_total_linhas_atualizadas  bigint                               := 0;
    v_cod_processo  constant    bigint not null                      := (select nextval('CODIGO_RESOLUCAO_ITEM_OS'));
    v_tipo_processo constant    types.veiculo_processo_type not null := 'FECHAMENTO_ITEM_CHECKLIST';
    v_km_real                   bigint;
begin
    v_km_real := (select *
                  from func_veiculo_update_km_atual(f_cod_unidade,
                                                    f_cod_veiculo,
                                                    f_km,
                                                    v_cod_processo,
                                                    v_tipo_processo,
                                                    true,
                                                    CURRENT_TIMESTAMP));

    foreach v_cod_item in array f_cod_itens
        loop
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
            select c.data_hora, capd.alternativa
            from checklist_ordem_servico_itens cosi
                     join checklist_ordem_servico cos
                          on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
                     join checklist c on cos.cod_checklist = c.codigo
                     join checklist_alternativa_pergunta_data capd
                          on capd.codigo = cosi.cod_alternativa_primeiro_apontamento
            where cosi.codigo = v_cod_item
            into v_data_realizacao_checklist, v_alternativa_item;

            -- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
            if v_data_realizacao_checklist is not null and v_data_realizacao_checklist >= f_data_hora_inicio_resolucao
            then
                perform throw_generic_error(
                        FORMAT(v_error_message, v_data_realizacao_checklist, f_data_hora_inicio_resolucao,
                               v_alternativa_item));
            end if;

            -- Atualiza os itens
            update checklist_ordem_servico_itens
            set cpf_mecanico               = f_cpf,
                tempo_realizacao           = f_tempo_realizacao,
                km                         = v_km_real,
                status_resolucao           = f_status_resolucao,
                data_hora_conserto         = f_data_hora_conserto,
                data_hora_inicio_resolucao = f_data_hora_inicio_resolucao,
                data_hora_fim_resolucao    = f_data_hora_fim_resolucao,
                feedback_conserto          = f_feedback_conserto,
                cod_processo               = v_cod_processo
            where cod_unidade = f_cod_unidade
              and codigo = v_cod_item
              and data_hora_conserto is null;

            get diagnostics v_qtd_linhas_atualizadas = row_count;

            -- Verificamos se o update funcionou.
            if v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0
            then
                perform throw_generic_error('Erro ao marcar os itens como resolvidos.');
            end if;
            v_total_linhas_atualizadas := v_total_linhas_atualizadas + v_qtd_linhas_atualizadas;
        end loop;
    return v_total_linhas_atualizadas;
end;
$$;


-- 15_migration_modifica_import_massivo_veiculos_PL-3288.sql.
-- adiciona flag motorizado em tabela implantação.veiculo_diagrama_usuario_prolog
alter table implantacao.veiculo_diagrama_usuario_prolog
    add column motorizado boolean;

update implantacao.veiculo_diagrama_usuario_prolog
set motorizado = true
where cod_veiculo_diagrama in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19);

update implantacao.veiculo_diagrama_usuario_prolog
set motorizado = false
where cod_veiculo_diagrama not in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19);

alter table implantacao.veiculo_diagrama_usuario_prolog
    alter column motorizado set not null;

-- Adiciona informações se o veículo é motorizado e se possui hubodometro
create or replace function implantacao.func_veiculo_import_cria_tabela_import(f_cod_empresa bigint,
                                                                              f_cod_unidade bigint,
                                                                              f_usuario text,
                                                                              f_data date,
                                                                              out nome_tabela_criada text)
    returns text
    language plpgsql
as
$$
declare
    v_dia                text := (select EXTRACT(day from f_data));
    v_mes                text := (select EXTRACT(month from f_data));
    v_ano                text := (select EXTRACT(year from f_data)) ;
    v_nome_tabela_import text := LOWER(remove_all_spaces(
                                        'V_COD_EMP_' || f_cod_empresa || '_COD_UNIDADE_' || f_cod_unidade || '_' ||
                                        v_ano || '_' || v_mes ||
                                        '_' || v_dia || '_' || f_usuario));
begin
    execute FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                       BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT       BIGINT,
            COD_EMPRESA                  BIGINT,
            COD_UNIDADE                  BIGINT,
            PLACA_EDITAVEL               VARCHAR(255),
            KM_EDITAVEL                  BIGINT,
            MARCA_EDITAVEL               VARCHAR(255),
            MODELO_EDITAVEL              VARCHAR(255),
            TIPO_EDITAVEL                VARCHAR(255),
            QTD_EIXOS_EDITAVEL           VARCHAR(255),
            IDENTIFICADOR_FROTA_EDITAVEL VARCHAR(15),
            POSSUI_HUBODOMETRO_EDITAVEL  VARCHAR(15),
            PLACA_FORMATADA_IMPORT       VARCHAR(255),
            MARCA_FORMATADA_IMPORT       VARCHAR(255),
            MODELO_FORMATADO_IMPORT      VARCHAR(255),
            TIPO_FORMATADO_IMPORT        VARCHAR(255),
            IDENTIFICADOR_FROTA_IMPORT   VARCHAR(15),
            STATUS_IMPORT_REALIZADO      BOOLEAN,
            ERROS_ENCONTRADOS            VARCHAR(255),
            USUARIO_UPDATE               VARCHAR(255),
            PRIMARY KEY (CODIGO),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO)
        );', v_nome_tabela_import);

    -- Trigger para verificar planilha e realizar o import de veículos.
    execute FORMAT('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO();',
                   v_nome_tabela_import,
                   v_nome_tabela_import);

    -- Cria audit para a tabela.
    execute format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_VEICULO ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_VEICULO
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                   v_nome_tabela_import,
                   v_nome_tabela_import);

    -- Garante select, update para o usuário que está realizando o import.
    execute FORMAT(
            'grant select, update on implantacao.%I to %I;',
            v_nome_tabela_import,
            (select interno.func_busca_nome_usuario_banco(f_usuario)));

    -- Retorna nome da tabela.
    select v_nome_tabela_import into nome_tabela_criada;
end ;
$$;

-- 2020-11-20 -> Adiona colunas e verificações para nova funcionalidade de acoplamento (thaisksf - PL-3288).
create or replace function implantacao.tg_func_veiculo_confere_planilha_importa_veiculo()
    returns trigger
    security definer
    language plpgsql
as
$$
declare
    v_valor_similaridade          constant real     := 0.4;
    v_valor_similaridade_diagrama constant real     := 0.5;
    v_sem_similaridade            constant real     := 0.0;
    v_qtd_erros                            smallint := 0;
    v_msgs_erros                           text;
    v_quebra_linha                         text     := CHR(10);
    v_cod_marca_banco                      bigint;
    v_similaridade_marca                   real;
    v_marca_modelo                         text;
    v_cod_modelo_banco                     bigint;
    v_similaridade_modelo                  real;
    v_cod_diagrama_banco                   bigint;
    v_nome_diagrama_banco                  text;
    v_similaridade_diagrama                real;
    v_diagrama_tipo                        text;
    v_eixos_diagrama                       text;
    v_diagrama_motorizado                  boolean;
    v_cod_tipo_banco                       bigint;
    v_similaridade_tipo                    real;
    v_possui_hubodometro_flag              boolean;
    v_similaridades_possui_hubodometro     real;
    v_possui_hubodometro                   text;
    v_possui_hubodometro_possibilidades    text[]   := ('{"SIM", "OK", "TRUE", "TEM"}');
begin
    if (tg_op = 'UPDATE' and old.status_import_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.placa_formatada_import := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.marca_formatada_import := remove_espacos_e_caracteres_especiais(new.marca_editavel);
        new.modelo_formatado_import := remove_espacos_e_caracteres_especiais(new.modelo_editavel);
        new.tipo_formatado_import := remove_espacos_e_caracteres_especiais(new.tipo_editavel);
        new.identificador_frota_import := new.identificador_frota_editavel;
        new.usuario_update := SESSION_USER;

        -- VERIFICA SE EMPRESA EXISTE
        if not EXISTS(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE EXISTE
        if not EXISTS(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        if not EXISTS(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        if (new.placa_formatada_import is not null) then
            if LENGTH(new.placa_formatada_import) <> 7
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO POSSUI 7 CARACTERES', v_quebra_linha);
            else
                if EXISTS(select v.placa
                          from veiculo v
                          where v.placa = new.placa_formatada_import
                            and v.cod_empresa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros =
                            concat(v_msgs_erros, v_qtd_erros, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   v_quebra_linha);
                else
                    if EXISTS(select v.placa
                              from veiculo v
                              where v.placa = new.placa_formatada_import
                                and v.cod_empresa = new.cod_empresa
                                and cod_unidade != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              v_quebra_linha);
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO PODE SER NULA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        select distinct on (new.marca_formatada_import) mav.codigo                                                        as cod_marca_banco,
                                                        MAX(func_gera_similaridade(new.marca_formatada_import, mav.nome)) as similariedade_marca
        into v_cod_marca_banco, v_similaridade_marca
        from marca_veiculo mav
        group by new.marca_formatada_import, new.marca_editavel, mav.nome, mav.codigo
        order by new.marca_formatada_import, similariedade_marca desc;

        v_marca_modelo := CONCAT(v_cod_marca_banco, new.modelo_formatado_import);
        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        if (v_similaridade_marca >= v_valor_similaridade)
        then
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            select distinct on (v_marca_modelo) mov.codigo as cod_modelo_veiculo,
                                                case
                                                    when v_cod_marca_banco = mov.cod_marca
                                                        then
                                                        MAX(func_gera_similaridade(v_marca_modelo,
                                                                                   CONCAT(mov.cod_marca, mov.nome)))
                                                    else v_sem_similaridade
                                                    end    as similariedade_modelo
            into v_cod_modelo_banco, v_similaridade_modelo
            from modelo_veiculo mov
            where mov.cod_empresa = new.cod_empresa
            group by v_marca_modelo, mov.nome, mov.codigo
            order by v_marca_modelo, similariedade_modelo desc;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            if (v_similaridade_modelo < v_valor_similaridade or v_similaridade_modelo is null)
            then
                insert into modelo_veiculo (nome, cod_marca, cod_empresa)
                values (new.modelo_editavel, v_cod_marca_banco, new.cod_empresa)
                returning codigo into v_cod_modelo_banco;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A MARCA NÃO FOI ENCONTRADA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        v_eixos_diagrama := CONCAT(new.qtd_eixos_editavel, new.tipo_formatado_import);
        -- Procura diagrama no banco:
        with info_diagramas as (
            select COUNT(vde.posicao) as qtd_eixos,
                   vde.cod_diagrama   as codigo,
                   vd.nome            as nome,
                   vd.motorizado      as diagrama_motorizado
            from veiculo_diagrama_eixos vde
                     join
                 veiculo_diagrama vd on vde.cod_diagrama = vd.codigo
            group by vde.cod_diagrama, vd.nome, vd.motorizado),

             diagramas as (
                 select vdup.cod_veiculo_diagrama as cod_diagrama,
                        vdup.nome                 as nome_diagrama,
                        vdup.qtd_eixos            as qtd_eixos,
                        vdup.motorizado           as diagrama_motorizado
                 from implantacao.veiculo_diagrama_usuario_prolog vdup
                 union all
                 select id.codigo as cod_diagrama, id.nome as nome_diagrama, id.qtd_eixos, id.diagrama_motorizado
                 from info_diagramas id)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        select distinct on (v_eixos_diagrama) d.nome_diagrama as nome_diagrama,
                                              d.cod_diagrama  as diagrama_banco,
                                              case
                                                  when d.qtd_eixos ::text = new.qtd_eixos_editavel
                                                      then
                                                      MAX(func_gera_similaridade(v_eixos_diagrama,
                                                                                 CONCAT(d.qtd_eixos, d.nome_diagrama)))
                                                  else v_sem_similaridade
                                                  end         as similariedade_diagrama,
                                              d.diagrama_motorizado
        into v_nome_diagrama_banco, v_cod_diagrama_banco,
            v_similaridade_diagrama, v_diagrama_motorizado
        from diagramas d
        group by v_eixos_diagrama, d.nome_diagrama, d.cod_diagrama, d.qtd_eixos, d.diagrama_motorizado
        order by v_eixos_diagrama, similariedade_diagrama desc;

        -- Se o diagrama não for motorizado, perguntar se possui hubodômetro
        -- Verifica se possui hubodometro;
        case when (v_diagrama_motorizado is false and new.possui_hubodometro_editavel is not null)
            then
                foreach v_possui_hubodometro in array v_possui_hubodometro_possibilidades
                    loop
                        v_similaridades_possui_hubodometro :=
                                MAX(func_gera_similaridade(new.possui_hubodometro_editavel,
                                                           v_possui_hubodometro));
                        if (v_similaridades_possui_hubodometro >
                            v_valor_similaridade)
                        then
                            v_possui_hubodometro_flag = true;
                        end if;
                    end loop;
            else v_possui_hubodometro_flag = false;
            end case;

        v_diagrama_tipo := CONCAT(v_nome_diagrama_banco, new.tipo_formatado_import);
        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        case when (v_similaridade_diagrama >= v_valor_similaridade_diagrama)
            then
                select distinct on (v_diagrama_tipo) vt.codigo as cod_tipo_veiculo,
                                                     case
                                                         when v_cod_diagrama_banco = vt.cod_diagrama
                                                             then MAX(func_gera_similaridade(new.tipo_formatado_import, vt.nome))
                                                         else v_sem_similaridade
                                                         end   as similariedade_tipo_diagrama
                into v_cod_tipo_banco, v_similaridade_tipo
                from veiculo_tipo vt
                where vt.cod_empresa = new.cod_empresa
                group by v_diagrama_tipo,
                         vt.codigo
                order by v_diagrama_tipo, similariedade_tipo_diagrama desc;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                if (v_similaridade_tipo < v_valor_similaridade or v_similaridade_tipo is null)
                then
                    insert into veiculo_tipo (nome, status_ativo, cod_diagrama, cod_empresa)
                    values (new.tipo_editavel, true, v_cod_diagrama_banco, new.cod_empresa)
                    returning codigo into v_cod_tipo_banco;
                end if;
            else
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros =
                        concat(v_msgs_erros, v_qtd_erros, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', v_quebra_linha);
            end case;
        -- VERIFICA QTD DE ERROS
        if (v_qtd_erros > 0)
        then
            new.status_import_realizado = false;
            new.erros_encontrados = v_msgs_erros;
        else
            if (v_qtd_erros = 0 and EXISTS(select v.placa
                                           from veiculo v
                                           where v.placa = new.placa_formatada_import
                                             and v.cod_empresa = new.cod_empresa
                                             and cod_unidade = new.cod_unidade))
            then
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                update veiculo_data
                set cod_modelo          = v_cod_modelo_banco,
                    cod_tipo            = v_cod_tipo_banco,
                    km                  = new.km_editavel,
                    cod_diagrama        = v_cod_diagrama_banco,
                    identificador_frota = new.identificador_frota_import
                where placa = new.placa_formatada_import
                  and cod_empresa = new.cod_empresa
                  and cod_unidade = new.cod_unidade;
                new.status_import_realizado = null;
                new.erros_encontrados = 'A PLACA JÁ ESTAVA CADASTRADA - INFORMAÇÕES FORAM ATUALIZADAS.';
            else
                if (v_qtd_erros = 0 and not EXISTS(select v.placa
                                                   from veiculo v
                                                   where v.placa = new.placa_formatada_import))
                then
                    -- CADASTRA VEÍCULO.
                    insert into veiculo (placa,
                                         cod_unidade,
                                         km,
                                         status_ativo,
                                         cod_tipo,
                                         cod_modelo,
                                         cod_eixos,
                                         data_hora_cadastro,
                                         cod_unidade_cadastro,
                                         cod_empresa,
                                         cod_diagrama,
                                         identificador_frota,
                                         possui_hubodometro,
                                         motorizado)
                    values (new.placa_formatada_import,
                            new.cod_unidade,
                            new.km_editavel,
                            true,
                            v_cod_tipo_banco,
                            v_cod_modelo_banco,
                            1,
                            NOW(),
                            new.cod_unidade,
                            new.cod_empresa,
                            v_cod_diagrama_banco,
                            new.identificador_frota_import,
                            v_possui_hubodometro_flag,
                            v_diagrama_motorizado);
                    new.status_import_realizado = true;
                    new.erros_encontrados = '-';
                end if;
            end if;
        end if;
    end if;
    return new;
end;
$$;

-- Adiciona informações se o veículo é motorizado e se possui hubodometro
create or replace function implantacao.func_veiculo_insere_planilha_importacao(f_cod_dados_autor_import bigint,
                                                                               f_nome_tabela_import text,
                                                                               f_cod_empresa bigint,
                                                                               f_cod_unidade bigint,
                                                                               f_json_veiculos jsonb)
    returns void
    language plpgsql
as
$$
begin
    execute FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                PLACA_EDITAVEL,
                                                PLACA_FORMATADA_IMPORT,
                                                KM_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MARCA_FORMATADA_IMPORT,
                                                MODELO_EDITAVEL,
                                                MODELO_FORMATADO_IMPORT,
                                                TIPO_EDITAVEL,
                                                TIPO_FORMATADO_IMPORT,
                                                QTD_EIXOS_EDITAVEL,
                                                IDENTIFICADOR_FROTA_EDITAVEL,
                                                POSSUI_HUBODOMETRO_EDITAVEL  )
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT                                         AS PLACA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> ''placa'')) :: TEXT  AS PLACA_FORMATADA_IMPORT,
                          (SRC ->> ''km'') :: BIGINT                                          AS KM,
                          (SRC ->> ''marca'') :: TEXT                                         AS MARCA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''marca'')) :: TEXT  AS MARCA_FORMATADA_IMPORT,
                          (SRC ->> ''modelo'') :: TEXT                                        AS MODELO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''modelo'')) :: TEXT AS MODELO_FORMATADO_IMPORT,
                          (SRC ->> ''tipo'') :: TEXT                                          AS TIPO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''tipo'')) :: TEXT   AS TIPO_FORMATADO_IMPORT,
                          (SRC ->> ''qtdEixos'') :: TEXT                                      AS QTD_EIXOS,
                          (SRC ->> ''identificadorFrota'') :: TEXT                            AS IDENTIFICADOR_FROTA,
                          (SRC ->> ''possuiHubodometro'') :: TEXT                             AS POSSUI_HUBODOMETRO
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   f_nome_tabela_import,
                   f_cod_dados_autor_import,
                   f_cod_empresa,
                   f_cod_unidade,
                   f_json_veiculos);
end
$$;


-- 16_migration_refatora_processos_altera_km-PL-3290.sql.
---------------- CHECKLIST
create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
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
    -- Iremos atualizar o km do veículo somente para o caso em que o km atual do veículo for menor que o km coletado.
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
    v_cod_novo_checklist        bigint;
    v_cod_checklist_inserido    bigint;
    v_qtd_linhas_atualizadas    bigint;
    v_checklist_ja_existia      boolean := false;
    v_km_final                  bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));

    if v_deve_atualizar_km_veiculo
    then
        v_km_final :=
                (select *
                 from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                                   f_cod_veiculo,
                                                   f_km_coletado,
                                                   v_cod_novo_checklist,
                                                   'CHECKLIST',
                                                   true,
                                                   f_data_hora_realizacao));
    end if;

    insert into checklist_data(codigo,
                               cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
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
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
            f_observacao,
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

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    -- Se devemos atualizar o km mas nenhuma linha foi alterada, então temos um erro.
    if (v_deve_atualizar_km_veiculo and (v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0))
    then
        raise exception 'Não foi possível atualizar o km do veículo.';
    end if;

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;


------------------ SOCORRO EM ROTA
create or replace function func_socorro_rota_abertura(f_cod_unidade bigint,
                                                      f_cod_colaborador_abertura bigint,
                                                      f_cod_veiculo_problema bigint,
                                                      f_km_veiculo_abertura bigint,
                                                      f_cod_problema_socorro_rota bigint,
                                                      f_descricao_problema text,
                                                      f_data_hora_abertura timestamp with time zone,
                                                      f_url_foto_1_abertura text,
                                                      f_url_foto_2_abertura text,
                                                      f_url_foto_3_abertura text,
                                                      f_latitude_abertura text,
                                                      f_longitude_abertura text,
                                                      f_precisao_localizacao_abertura_metros numeric,
                                                      f_endereco_automatico text,
                                                      f_ponto_referencia text,
                                                      f_device_id_abertura text,
                                                      f_device_imei_abertura text,
                                                      f_device_uptime_millis_abertura bigint,
                                                      f_android_api_version_abertura integer,
                                                      f_marca_device_abertura text,
                                                      f_modelo_device_abertura text,
                                                      f_plataforma_origem prolog_plataforma_socorro_rota_type,
                                                      f_versao_plataforma_origem text) returns bigint
    language plpgsql
as
$$
declare
    f_cod_socorro_inserido bigint;
    f_cod_empresa          bigint := (select cod_empresa
                                      from unidade
                                      where codigo = f_cod_unidade);
    f_cod_abertura         bigint;
    v_km_final             bigint;
begin
    -- Verifica se a funcionalidade está liberada para a empresa.
    perform func_socorro_rota_empresa_liberada(f_cod_empresa);

    -- Assim conseguimos inserir mantendo a referência circular entre a pai e as filhas.
    set constraints all deferred;

    -- Pega o código de abertura da sequence para poder atualizar a tabela pai.
    f_cod_abertura := (select nextval(pg_get_serial_sequence('socorro_rota_abertura', 'codigo')));


    v_km_final := (select *
                   from func_veiculo_update_km_atual(f_cod_unidade,
                                                     f_cod_veiculo_problema,
                                                     f_km_veiculo_abertura,
                                                     f_cod_abertura,
                                                     'SOCORRO_EM_ROTA',
                                                     true,
                                                     f_data_hora_abertura));

    -- Insere na tabela pai.
    insert into socorro_rota (cod_unidade, status_atual, cod_abertura)
    values (f_cod_unidade, 'ABERTO', f_cod_abertura)
    returning codigo into f_cod_socorro_inserido;

    -- Exibe erro se não puder inserir.
    if f_cod_socorro_inserido is null or f_cod_socorro_inserido <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Insere na tabela de abertura
    insert into socorro_rota_abertura (codigo,
                                       cod_socorro_rota,
                                       cod_colaborador_abertura,
                                       cod_veiculo_problema,
                                       km_veiculo_abertura,
                                       cod_problema_socorro_rota,
                                       descricao_problema,
                                       data_hora_abertura,
                                       url_foto_1_abertura,
                                       url_foto_2_abertura,
                                       url_foto_3_abertura,
                                       latitude_abertura,
                                       longitude_abertura,
                                       precisao_localizacao_abertura_metros,
                                       endereco_automatico,
                                       ponto_referencia,
                                       device_id_abertura,
                                       device_imei_abertura,
                                       device_uptime_millis_abertura,
                                       android_api_version_abertura,
                                       marca_device_abertura,
                                       modelo_device_abertura,
                                       cod_empresa,
                                       plataforma_origem,
                                       versao_plataforma_origem)
    values (f_cod_abertura,
            f_cod_socorro_inserido,
            f_cod_colaborador_abertura,
            f_cod_veiculo_problema,
            v_km_final,
            f_cod_problema_socorro_rota,
            f_descricao_problema,
            f_data_hora_abertura,
            f_url_foto_1_abertura,
            f_url_foto_2_abertura,
            f_url_foto_3_abertura,
            f_latitude_abertura,
            f_longitude_abertura,
            f_precisao_localizacao_abertura_metros,
            f_endereco_automatico,
            f_ponto_referencia,
            f_device_id_abertura,
            f_device_imei_abertura,
            f_device_uptime_millis_abertura,
            f_android_api_version_abertura,
            f_marca_device_abertura,
            f_modelo_device_abertura,
            f_cod_empresa,
            f_plataforma_origem,
            f_versao_plataforma_origem);

    -- Exibe erro se não puder inserir.
    if f_cod_abertura is null or f_cod_abertura <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Retorna o código do socorro.
    return f_cod_socorro_inserido;
end;
$$;


---------------- AFERICAO
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
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
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
    values (v_cod_afericao,
            f_data_hora,
            f_placa_veiculo,
            f_cpf_aferidor,
            v_km_final,
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

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
$$;


drop function if exists func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
    f_cod_unidade bigint,
    f_tipo_origem varchar(255),
    f_cod_movimentacao bigint,
    f_placa_veiculo varchar(7),
    f_km_atual bigint,
    f_posicao_prolog integer);
create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem varchar(255),
                                                                                f_cod_movimentacao bigint,
                                                                                f_placa_veiculo varchar(7),
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
    v_km_atual                   bigint;
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
           v.cod_diagrama,
           v.km
    from veiculo_data v
    where v.placa = f_placa_veiculo
    into strict
        v_cod_veiculo,
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

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
            v_km_atual,
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


-- 17_migration_cria_nova_logica_propagacao_empresas_selecionadas_PL-3359.sql.
create table veiculo_acoplamento_empresa_liberada
(
    cod_empresa bigint primary key,
    constraint fk_cod_empresa foreign key (cod_empresa) references empresa (codigo)
);

-- Altera flag de propagação para temporariamente verificar se a empres está liberada.
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo types.veiculo_processo_type,
                                                        f_deve_propagar_km boolean,
                                                        f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                           bigint;
    v_diferenca_km                       bigint;
    v_km_motorizado                      bigint;
    v_possui_hubodometro                 boolean;
    v_motorizado                         boolean;
    v_cod_processo_acoplamento           bigint;
    v_cod_historico_processo_acoplamento bigint[];
    v_cod_veiculos_acoplados             bigint[];
    v_km_veiculos_acoplados              bigint[];
    v_veiculos_motorizados               boolean[];
    v_cod_empresa                        bigint;
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo, v.cod_empresa
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.cod_unidade = f_cod_unidade
      and v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento, v_cod_empresa;

    case when exists(select vael.cod_empresa
                     from veiculo_acoplamento_empresa_liberada vael
                     where vael.cod_empresa = v_cod_empresa)
        then
            f_deve_propagar_km = true;
        else
            f_deve_propagar_km = false;
        end case;

    if not f_deve_propagar_km
    then
        if v_km_atual < f_km_coletado
        then
            update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
            return f_km_coletado;
        end if;
    end if;

    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    select array_agg(vaa.cod_veiculo), array_agg(v.motorizado), array_agg(v.km), array_agg(vah.codigo)
                    from veiculo_acoplamento_atual vaa
                             join veiculo v
                                  on vaa.cod_unidade = v.cod_unidade
                                      and vaa.cod_veiculo = v.codigo
                             inner join veiculo_acoplamento_historico vah on vaa.cod_processo = vah.cod_processo
                        and vaa.cod_veiculo = vah.cod_veiculo
                    where vaa.cod_unidade = f_cod_unidade
                      and vaa.cod_processo = v_cod_processo_acoplamento
                      and v.possui_hubodometro is false
                    into v_cod_veiculos_acoplados,
                        v_veiculos_motorizados,
                        v_km_veiculos_acoplados,
                        v_cod_historico_processo_acoplamento;
                end if;
                case when (v_possui_hubodometro is false and v_motorizado is false and
                           v_cod_processo_acoplamento is null)
                    then
                        perform func_veiculo_salva_historico_km_propagacao(
                                f_cod_unidade,
                                null,
                                v_cod_processo_acoplamento,
                                f_cod_veiculo,
                                v_motorizado,
                                true,
                                v_km_atual,
                                v_km_atual,
                                f_km_coletado,
                                f_tipo_processo,
                                f_cod_processo,
                                f_data_hora);
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        perform func_veiculo_salva_historico_km_propagacao(
                                                                f_cod_unidade,
                                                                unnest(v_cod_historico_processo_acoplamento),
                                                                v_cod_processo_acoplamento,
                                                                unnest(v_cod_veiculos_acoplados),
                                                                unnest(v_veiculos_motorizados),
                                                                (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                                unnest(v_km_veiculos_acoplados),
                                                                unnest(v_km_veiculos_acoplados),
                                                                f_km_coletado,
                                                                f_tipo_processo,
                                                                f_cod_processo,
                                                                f_data_hora);
                                                        return v_km_atual;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados) + v_diferenca_km,
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;


-- 18_migration_cria_validacoes_insert_acoplamento_PL-3298.sql.
-- PL-3298.
create or replace function func_veiculo_get_estado_acoplamento(f_cod_veiculos bigint[])
    returns table
            (
                cod_veiculo                        bigint,
                cod_processo_acoplamento_vinculado bigint,
                posicao_acoplado                   smallint,
                motorizado                         boolean,
                possui_hubodometro                 boolean
            )
    language plpgsql
as
$$
begin
    return query
        select v.codigo             as cod_veiculo,
               vaa.cod_processo     as cod_processo,
               vaa.cod_posicao      as cod_posicao,
               v.motorizado         as motorizado,
               v.possui_hubodometro as possui_hubodometro
        from veiculo v
                 left join veiculo_acoplamento_atual vaa
                           on v.codigo = vaa.cod_veiculo
        where v.codigo = any (f_cod_veiculos);
end;
$$;


-- Corrige throw
create or replace function func_veiculo_remove_acoplamento_atual(f_cod_processo_acoplamento bigint)
    returns void
    language plpgsql
as
$$
begin
    delete
    from veiculo_acoplamento_atual
    where cod_processo = f_cod_processo_acoplamento;

    if not found
    then
        perform throw_server_side_error(format('Erro ao deletar estado atual de acoplamento para o processo: %s.
                                               ', f_cod_processo_acoplamento));
    end if;
end;
$$;


-- 19_migration_modifica_tipo_coluna_PL-3381.sql.
drop view if exists veiculo_km_propagacao;

alter table if exists veiculo_processo_km_historico
    alter column tipo_processo_veiculo type types.veiculo_processo_type
        using tipo_processo_veiculo::types.veiculo_processo_type;

create or replace view veiculo_km_propagacao as
select codigo,
       cod_veiculo,
       cod_processo_acoplamento,
       cod_historico_processo_acoplamento,
       cod_processo_veiculo,
       tipo_processo_veiculo,
       cod_unidade,
       veiculo_fonte_processo,
       motorizado,
       km_antigo,
       km_final,
       km_coletado_processo,
       data_hora_processo
from veiculo_processo_km_historico
where km_antigo <> km_final
  and veiculo_fonte_processo = false;


-- 20_migration_possui_hubodometro_func_listagem_historico_PL-3385.sql.
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
                marca_veiculo             text,
                possui_hubodometro        boolean
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
               mav.nome::text                   as marca_veiculo,
               veh.possui_hubodometro           as possui_hubodometro
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


-- 21_migration_corrige_estrutura_historico_edicao_veiculo_PL-3326.sql.
-- func_veiculo_atualiza_veiculo
drop function func_veiculo_atualiza_veiculo(bigint, text, text, bigint, bigint, bigint, boolean, boolean, bigint, text,
    timestamp with time zone, text);
create or replace function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
                                                         f_nova_placa text,
                                                         f_novo_identificador_frota text,
                                                         f_novo_km bigint,
                                                         f_novo_cod_tipo bigint,
                                                         f_novo_cod_modelo bigint,
                                                         f_novo_status boolean,
                                                         f_novo_possui_hubodometro boolean,
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
                antigo_status               boolean,
                antigo_possui_hubodometro   boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant  bigint not null  := (select v.cod_empresa
                                                       from veiculo v
                                                       where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant  bigint not null  := (select vt.cod_diagrama
                                                       from veiculo_tipo vt
                                                       where vt.codigo = f_novo_cod_tipo
                                                         and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant  bigint not null  := (select mv.cod_marca
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
    v_novo_motorizado   constant  boolean not null := (select vd.motorizado
                                                       from veiculo_diagrama vd
                                                       where vd.codigo = v_novo_cod_diagrama);
    v_antigo_possui_hubodometro   boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
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
                                                  f_novo_status,
                                                  f_novo_possui_hubodometro)) - hstore((v_antigo_identificador_frota,
                                                                                        v_antigo_km,
                                                                                        v_antigo_cod_diagrama,
                                                                                        v_antigo_cod_tipo,
                                                                                        v_antigo_cod_marca,
                                                                                        v_antigo_cod_modelo,
                                                                                        v_antigo_status,
                                                                                        v_antigo_possui_hubodometro))));

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
                                                     f_novo_possui_hubodometro,
                                                     v_total_edicoes);

        update veiculo
        set identificador_frota = f_novo_identificador_frota,
            km                  = f_novo_km,
            cod_modelo          = f_novo_cod_modelo,
            cod_tipo            = f_novo_cod_tipo,
            cod_diagrama        = v_novo_cod_diagrama,
            status_ativo        = f_novo_status,
            motorizado          = v_novo_motorizado,
            possui_hubodometro  = f_novo_possui_hubodometro,
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
               v_antigo_status,
               v_antigo_possui_hubodometro;
end;
$$;

-- func_veiculo_gera_historico_atualizacao
drop function if exists func_veiculo_gera_historico_atualizacao(f_cod_empresa bigint, f_cod_veiculo bigint, f_cod_colaborador_edicao bigint, f_origem_edicao text, f_data_hora_edicao_tz_aplicado timestamp with time zone, f_informacoes_extras_edicao text, f_nova_placa text, f_novo_identificador_frota text, f_novo_km bigint, f_novo_cod_diagrama bigint, f_novo_cod_tipo bigint, f_novo_cod_modelo bigint, f_novo_status boolean, f_antigo_possui_hubodometro boolean, f_total_edicoes smallint);
drop function if exists func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint, f_cod_unidade_veiculo bigint, f_cod_veiculo bigint, f_antiga_placa text, f_antigo_identificador_frota text, f_antigo_km bigint, f_antigo_cod_diagrama bigint, f_antigo_cod_tipo bigint, f_antigo_cod_modelo bigint, f_antigo_status boolean, f_antigo_possui_hubodometro boolean, f_total_edicoes smallint, f_cod_colaborador_edicao bigint, f_origem_edicao text, f_data_hora_edicao timestamp with time zone, f_informacoes_extras_edicao text);
drop function if exists func_veiculo_gera_historico_atualizacao(f_cod_empresa_veiculo bigint, f_cod_unidade_veiculo bigint, f_cod_veiculo bigint, f_antiga_placa text, f_antigo_identificador_frota text, f_antigo_km bigint, f_antigo_cod_diagrama bigint, f_antigo_cod_tipo bigint, f_antigo_cod_modelo bigint, f_antigo_status boolean, f_total_edicoes smallint, f_cod_colaborador_edicao bigint, f_origem_edicao text, f_data_hora_edicao timestamp with time zone, f_informacoes_extras_edicao text);

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
                                                                   f_novo_possui_hubodometro boolean,
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
    v_antigo_possui_hubodometro          boolean;
    v_data_hora_edicao_tz_unidade        timestamp with time zone;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    v_data_hora_edicao_tz_unidade := f_data_hora_edicao_tz_aplicado at time zone
                                     tz_unidade(v_cod_unidade);

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
                                          estado_antigo,
                                          possui_hubodometro)
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
            true,
            v_antigo_possui_hubodometro);

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
                                          estado_antigo,
                                          possui_hubodometro)
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
            false,
            f_novo_possui_hubodometro);

    return query
        select v_cod_edicao_historico_estado_antigo, v_cod_edicao_historico_estado_novo;
end;
$$;


-- 22_migration_impede_inativacao_veiculos_acoplados_PL-3397.sql.
-- Cria constraint para impedir que quando o veículo possuir acoplamento, ele seja inativado.
alter table veiculo_data
    add constraint check_status_ativo_acoplamento
        check (status_ativo <> false or (status_ativo = false and acoplado = false));

-- Adiciona informação de acoplamento
drop function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
    f_apenas_ativos boolean,
    f_cod_tipo_veiculo bigint);
create or replace function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
                                                            f_apenas_ativos boolean,
                                                            f_cod_tipo_veiculo bigint)
    returns table
            (
                codigo              bigint,
                placa               text,
                cod_regional        bigint,
                nome_regional       text,
                cod_unidade         bigint,
                nome_unidade        text,
                km                  bigint,
                status_ativo        boolean,
                cod_tipo            bigint,
                cod_modelo          bigint,
                cod_diagrama        bigint,
                identificador_frota text,
                modelo              text,
                possui_hubodometro  boolean,
                motorizado          boolean,
                acoplado            boolean,
                nome_diagrama       text,
                dianteiro           bigint,
                traseiro            bigint,
                tipo                text,
                marca               text,
                cod_marca           bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       r.codigo                                                as cod_regional,
       r.regiao                                                as nome_regional,
       u.codigo                                                as cod_unidade,
       u.nome                                                  as nome_unidade,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       mv.nome                                                 as modelo,
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
       v.acoplado                                              as acoplado,
       vd.nome                                                 as nome_diagrama,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
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
where v.cod_unidade = any (f_cod_unidades)
  and case
          when f_apenas_ativos is null or f_apenas_ativos = false
              then true
          else v.status_ativo = true
    end
  and case
          when f_cod_tipo_veiculo is null
              then true
          else v.cod_tipo = f_cod_tipo_veiculo
    end
group by v.placa, v.codigo, v.codigo, v.placa, u.codigo, v.km, v.status_ativo, v.cod_tipo, v.cod_modelo,
         v.motorizado, v.acoplado, v.possui_hubodometro, v.cod_diagrama, v.identificador_frota, r.codigo, mv.nome,
         vd.nome, vt.nome, mav.nome, mav.codigo
order by v.placa;
$$;


-- 23_migration_dialog_km_PL-3369.sql.
drop function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_placa_veiculo text,
    f_prioridade_alternativa text,
    f_status_itens text,
    f_data_hora_atual_utc timestamp with time zone,
    f_limit integer,
    f_offset integer);
create or replace function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
                                                                 f_cod_os bigint,
                                                                 f_placa_veiculo text,
                                                                 f_prioridade_alternativa text,
                                                                 f_status_itens text,
                                                                 f_data_hora_atual_utc timestamp with time zone,
                                                                 f_limit integer,
                                                                 f_offset integer)
    returns table
            (
                cod_veiculo                           bigint,
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_item_os                   bigint,
                cod_item_os                           bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        with dados as (
            select c.cod_veiculo                                                          as cod_veiculo,
                   c.placa_veiculo::text                                                  as placa_veiculo,
                   v.km                                                                   as km_atual_veiculo,
                   cos.codigo                                                             as cod_os,
                   cos.cod_unidade                                                        as cod_unidade_item_os,
                   cosi.codigo                                                            as cod_item_os,
                   c.data_hora at time zone tz_unidade(c.cod_unidade)                     as data_hora_primeiro_apontamento_item,
                   cosi.status_resolucao                                                  as status_item_os,
                   prio.prazo                                                             as prazo_resolucao_item_horas,
                   to_minutes_trunc(
                               (c.data_hora + (prio.prazo || ' HOURS')::interval)
                               -
                               f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
                   cosi.qt_apontamentos                                                   as qtd_apontamentos,
                   co.codigo                                                              as cod_colaborador_resolucao,
                   co.nome::text                                                          as nome_colaborador_resolucao,
                   cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
                   cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
                   cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
                   cosi.feedback_conserto                                                 as feedback_resolucao,
                   millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
                   cosi.km                                                                as km_veiculo_coletado_resolucao,
                   cp.codigo                                                              as cod_pergunta,
                   cp.pergunta                                                            as descricao_pergunta,
                   cap.codigo                                                             as cod_alternativa,
                   cap.alternativa                                                        as descricao_alternativa,
                   cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
                   case
                       when cap.alternativa_tipo_outros
                           then
                           (select crn.resposta_outros
                            from checklist_respostas_nok crn
                            where crn.cod_checklist = c.codigo
                              and crn.cod_alternativa = cap.codigo)::text
                       end                                                                as descricao_tipo_outros,
                   cap.prioridade::text                                                   as prioridade_alternativa,
                   an.url_midia::text                                                     as url_midia,
                   an.cod_checklist::bigint                                               as cod_checklist
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
                     join checklist_perguntas cp
                          on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist_alternativa_prioridade prio
                          on cap.prioridade = prio.prioridade
                     join veiculo v
                          on c.placa_veiculo = v.placa
                     left join colaborador co
                               on co.cpf = cosi.cpf_mecanico
                     left join checklist_ordem_servico_itens_midia im
                               on im.cod_item_os = cosi.codigo
                     left join checklist_respostas_midias_alternativas_nok an
                               on im.cod_midia_nok = an.codigo
            where f_if(f_cod_unidade is null, true, cos.cod_unidade = f_cod_unidade)
              and f_if(f_cod_os is null, true, cos.codigo = f_cod_os)
              and f_if(f_placa_veiculo is null, true, c.placa_veiculo = f_placa_veiculo)
              and f_if(f_prioridade_alternativa is null, true, cap.prioridade = f_prioridade_alternativa)
              and f_if(f_status_itens is null, true, cosi.status_resolucao = f_status_itens)
            limit f_limit
            offset
            f_offset
        ),
             dados_veiculo as (
                 select v.placa::text as placa_veiculo,
                        v.km          as km_atual_veiculo
                 from veiculo v
                 where v.placa = f_placa_veiculo
             )

             -- Nós usamos esse dados_veiculo com f_if pois pode acontecer de não existir dados para os filtros aplicados e
             -- desse modo acabaríamos não retornando placa e km também, mas essas são informações necessárias pois o objeto
             -- construído a partir dessa function usa elas.
        select d.cod_veiculo                                                             as cod_veiculo,
               f_if(d.placa_veiculo is null, dv.placa_veiculo, d.placa_veiculo)          as placa_veiculo,
               f_if(d.km_atual_veiculo is null, dv.km_atual_veiculo, d.km_atual_veiculo) as km_atual_veiculo,
               d.cod_os                                                                  as cod_os,
               d.cod_unidade_item_os                                                     as cod_unidade_item_os,
               d.cod_item_os                                                             as cod_item_os,
               d.data_hora_primeiro_apontamento_item                                     as data_hora_primeiro_apontamento_item,
               d.status_item_os                                                          as status_item_os,
               d.prazo_resolucao_item_horas                                              as prazo_resolucao_item_horas,
               d.prazo_restante_resolucao_item_minutos                                   as prazo_restante_resolucao_item_minutos,
               d.qtd_apontamentos                                                        as qtd_apontamentos,
               d.cod_colaborador_resolucao                                               as cod_colaborador_resolucao,
               d.nome_colaborador_resolucao                                              as nome_colaborador_resolucao,
               d.data_hora_resolucao                                                     as data_hora_resolucao,
               d.data_hora_inicio_resolucao                                              as data_hora_inicio_resolucao,
               d.data_hora_fim_resolucao                                                 as data_hora_fim_resolucao,
               d.feedback_resolucao                                                      as feedback_resolucao,
               d.duracao_resolucao_minutos                                               as duracao_resolucao_minutos,
               d.km_veiculo_coletado_resolucao                                           as km_veiculo_coletado_resolucao,
               d.cod_pergunta                                                            as cod_pergunta,
               d.descricao_pergunta                                                      as descricao_pergunta,
               d.cod_alternativa                                                         as cod_alternativa,
               d.descricao_alternativa                                                   as descricao_alternativa,
               d.alternativa_tipo_outros                                                 as alternativa_tipo_outros,
               d.descricao_tipo_outros                                                   as descricao_tipo_outros,
               d.prioridade_alternativa                                                  as prioridade_alternativa,
               d.url_midia                                                               as url_midia,
               d.cod_checklist                                                           as cod_checklist
        from dados d
                 right join dados_veiculo dv
                            on d.placa_veiculo = dv.placa_veiculo
        order by cod_os, cod_item_os, cod_checklist;
end;
$$;

drop function func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
    f_cod_os bigint,
    f_data_hora_atual_utc timestamp with time zone);
create or replace function func_checklist_os_get_ordem_servico_resolucao(f_cod_unidade bigint,
                                                                         f_cod_os bigint,
                                                                         f_data_hora_atual_utc timestamp with time zone)
    returns table
            (
                cod_veiculo                           bigint,
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_os                        bigint,
                status_os                             text,
                data_hora_abertura_os                 timestamp without time zone,
                data_hora_fechamento_os               timestamp without time zone,
                cod_item_os                           bigint,
                cod_unidade_item_os                   bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        select c.cod_veiculo                                                          as cod_veiculo,
               c.placa_veiculo::text                                                  as placa_veiculo,
               v.km                                                                   as km_atual_veiculo,
               cos.codigo                                                             as cod_os,
               cos.cod_unidade                                                        as cod_unidade_os,
               cos.status::text                                                       as status_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_abertura_os,
               cos.data_hora_fechamento at time zone tz_unidade(f_cod_unidade)        as data_hora_fechamento_os,
               cosi.codigo                                                            as cod_item_os,
               cos.cod_unidade                                                        as cod_unidade_item_os,
               c.data_hora_realizacao_tz_aplicado                                     as data_hora_primeiro_apontamento_item,
               cosi.status_resolucao                                                  as status_item_os,
               prio.prazo                                                             as prazo_resolucao_item_horas,
               to_minutes_trunc(
                           (c.data_hora + (prio.prazo || ' HOURS')::interval)
                           -
                           f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
               cosi.qt_apontamentos                                                   as qtd_apontamentos,
               co.codigo                                                              as cod_colaborador_resolucao,
               co.nome::text                                                          as nome_colaborador_resolucao,
               cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
               cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
               cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
               cosi.feedback_conserto                                                 as feedback_resolucao,
               millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
               cosi.km                                                                as km_veiculo_coletado_resolucao,
               cp.codigo                                                              as cod_pergunta,
               cp.pergunta                                                            as descricao_pergunta,
               cap.codigo                                                             as cod_alternativa,
               cap.alternativa                                                        as descricao_alternativa,
               cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
               case
                   when cap.alternativa_tipo_outros
                       then
                       (select crn.resposta_outros
                        from checklist_respostas_nok crn
                        where crn.cod_checklist = c.codigo
                          and crn.cod_alternativa = cap.codigo)::text
                   end                                                                as descricao_tipo_outros,
               cap.prioridade::text                                                   as prioridade_alternativa,
               an.url_midia::text                                                     as url_midia,
               an.cod_checklist::bigint                                               as cod_checklist
        from checklist c
                 join checklist_ordem_servico cos
                      on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os
                          and cos.cod_unidade = cosi.cod_unidade
            -- O join com perguntas e alternativas é feito com a tabela _DATA pois OSs de perguntas e alternativas
            -- deletadas ainda devem ser exibidas.
                 join checklist_perguntas_data cp
                      on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                 join checklist_alternativa_pergunta_data cap
                      on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                 join checklist_alternativa_prioridade prio
                      on cap.prioridade = prio.prioridade
                 join veiculo v
                      on c.placa_veiculo = v.placa
                 left join colaborador co
                           on co.cpf = cosi.cpf_mecanico
                 left join checklist_ordem_servico_itens_midia im
                           on im.cod_item_os = cosi.codigo
                 left join checklist_respostas_midias_alternativas_nok an
                           on im.cod_midia_nok = an.codigo
        where cos.codigo = f_cod_os
          and cos.cod_unidade = f_cod_unidade
        order by cosi.codigo;
end;
$$;



-- Corrige a atualização de km no checklist. Não deveria ter a verificação de km menor ou maior.
create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
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
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo text    := (select vd.placa
                                         from veiculo_data vd
                                         where vd.codigo = f_cod_veiculo);
    v_cod_novo_checklist     bigint;
    v_cod_checklist_inserido bigint;
    v_checklist_ja_existia   boolean := false;
    v_km_final               bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));


    v_km_final :=
            (select *
             from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                               f_cod_veiculo,
                                               f_km_coletado,
                                               v_cod_novo_checklist,
                                               'CHECKLIST',
                                               true,
                                               f_data_hora_realizacao));

    insert into checklist_data(codigo,
                               cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
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
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
            f_observacao,
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

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;


-- 24_migration_motorizado_PL-3498.sql.
drop function func_veiculo_busca_veiculo_acoplamento_historico(f_cod_unidades bigint[],
                                                                            f_cod_veiculos bigint[],
                                                                            f_data_inicial date,
                                                                            f_data_final date);
create or replace function func_veiculo_busca_veiculo_acoplamento_historico(f_cod_unidades bigint[],
                                                                            f_cod_veiculos bigint[],
                                                                            f_data_inicial date,
                                                                            f_data_final date)
    returns table
            (
                cod_processo        bigint,
                nome_unidade        text,
                nome_colaborador    text,
                placa               text,
                identificador_frota text,
                motorizado          boolean,
                km                  bigint,
                cod_posicao         smallint,
                nome_posicao        text,
                acao                text,
                data_hora           timestamp without time zone,
                observacao          text
            )
    language plpgsql
as
$$
begin
    return query
        select vap.codigo                                             as cod_processo,
               u.nome ::text                                          as unidade,
               c.nome ::text                                          as colaborador,
               v.placa ::text                                         as placa,
               v.identificador_frota ::text                           as identificador_frota,
               v.motorizado                                           as motorizado,
               vah.km_veiculo                                         as km,
               vah.cod_posicao                                        as cod_posicao,
               vapo.posicao_legivel_pt_br ::text                      as posicao_legivel_pt_br,
               vah.acao ::text                                        as acao,
               vap.data_hora at time zone tz_unidade(vap.cod_unidade) as data_hora,
               vap.observacao ::text                                  as observacao
        from veiculo_acoplamento_processo vap
                 join veiculo_acoplamento_historico vah on vap.cod_unidade = any (f_cod_unidades)
            and vap.codigo = vah.cod_processo
                 join types.veiculo_acoplamento_posicao vapo on vapo.codigo = vah.cod_posicao
                 join veiculo v on v.codigo = vah.cod_veiculo
                 join unidade u on u.codigo = vap.cod_unidade
                 join colaborador c on vap.cod_colaborador = c.codigo
        where vap.cod_unidade = any (f_cod_unidades)
          and case
                  when f_cod_veiculos is not null
                      then
                          vah.cod_processo in (select h.cod_processo
                                               from veiculo_acoplamento_historico h
                                               where h.cod_veiculo = any (f_cod_veiculos))
                  else
                      true
            end
          and case
                  when (f_data_inicial is not null and f_data_final is not null)
                      then
                      (vap.data_hora at time zone tz_unidade(vap.cod_unidade)) :: date
                          between f_data_inicial and f_data_final
                  else
                      true
            end
        order by unidade, cod_processo, cod_posicao;
end;
$$;


-- 25_migration_correcoes_apos_merge_dev.sql.
CREATE OR REPLACE VIEW CHECKLIST_ORDEM_SERVICO_ITENS AS
SELECT COSI.COD_UNIDADE,
       COSI.CODIGO,
       COSI.COD_OS,
       COSI.CPF_MECANICO,
       COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
       COSI.COD_CONTEXTO_PERGUNTA,
       COSI.COD_CONTEXTO_ALTERNATIVA,
       COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
       COSI.STATUS_RESOLUCAO,
       COSI.QT_APONTAMENTOS,
       COSI.KM,
       COSI.DATA_HORA_CONSERTO,
       COSI.DATA_HORA_INICIO_RESOLUCAO,
       COSI.DATA_HORA_FIM_RESOLUCAO,
       COSI.TEMPO_REALIZACAO,
       COSI.FEEDBACK_CONSERTO,
       COSI.COD_PROCESSO
FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
WHERE COSI.DELETADO = FALSE;

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
       v.foi_editado,
       v.possui_hubodometro,
       v.motorizado,
       v.acoplado
from veiculo_data v
where v.deletado = false;

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
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
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
    values (v_cod_afericao,
            f_data_hora,
            f_placa_veiculo,
            f_cpf_aferidor,
            v_km_final,
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

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
$$;

create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
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
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo text    := (select vd.placa
                                         from veiculo_data vd
                                         where vd.codigo = f_cod_veiculo);
    v_cod_novo_checklist     bigint;
    v_cod_checklist_inserido bigint;
    v_checklist_ja_existia   boolean := false;
    v_km_final               bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));


    v_km_final :=
            (select *
             from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                               f_cod_veiculo,
                                               f_km_coletado,
                                               v_cod_novo_checklist,
                                               'CHECKLIST',
                                               true,
                                               f_data_hora_realizacao));

    insert into checklist_data(codigo,
                               cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               placa_veiculo,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
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
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            v_placa_atual_do_veiculo,
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
            f_observacao,
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

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;

-- Sobre:
--
-- Function utilizada para buscar os Itens de uma Ordem de Serviço para serem resolvidos.
create or replace function func_checklist_os_get_itens_resolucao(f_cod_unidade bigint,
                                                                 f_cod_os bigint,
                                                                 f_placa_veiculo text,
                                                                 f_prioridade_alternativa text,
                                                                 f_status_itens text,
                                                                 f_data_hora_atual_utc timestamp with time zone,
                                                                 f_limit integer,
                                                                 f_offset integer)
    returns table
            (
                cod_veiculo                           bigint,
                placa_veiculo                         text,
                km_atual_veiculo                      bigint,
                cod_os                                bigint,
                cod_unidade_item_os                   bigint,
                cod_item_os                           bigint,
                data_hora_primeiro_apontamento_item   timestamp without time zone,
                status_item_os                        text,
                prazo_resolucao_item_horas            integer,
                prazo_restante_resolucao_item_minutos bigint,
                qtd_apontamentos                      integer,
                cod_colaborador_resolucao             bigint,
                nome_colaborador_resolucao            text,
                data_hora_resolucao                   timestamp without time zone,
                data_hora_inicio_resolucao            timestamp without time zone,
                data_hora_fim_resolucao               timestamp without time zone,
                feedback_resolucao                    text,
                duracao_resolucao_minutos             bigint,
                km_veiculo_coletado_resolucao         bigint,
                cod_pergunta                          bigint,
                descricao_pergunta                    text,
                cod_alternativa                       bigint,
                descricao_alternativa                 text,
                alternativa_tipo_outros               boolean,
                descricao_tipo_outros                 text,
                prioridade_alternativa                text,
                url_midia                             text,
                cod_checklist                         bigint
            )
    language plpgsql
as
$$
begin
    return query
        with dados as (
            select c.cod_veiculo                                                          as cod_veiculo,
                   c.placa_veiculo::text                                                  as placa_veiculo,
                   v.km                                                                   as km_atual_veiculo,
                   cos.codigo                                                             as cod_os,
                   cos.cod_unidade                                                        as cod_unidade_item_os,
                   cosi.codigo                                                            as cod_item_os,
                   c.data_hora at time zone tz_unidade(c.cod_unidade)                     as data_hora_primeiro_apontamento_item,
                   cosi.status_resolucao                                                  as status_item_os,
                   prio.prazo                                                             as prazo_resolucao_item_horas,
                   to_minutes_trunc(
                               (c.data_hora + (prio.prazo || ' HOURS')::interval)
                               -
                               f_data_hora_atual_utc)                                     as prazo_restante_resolucao_item_minutos,
                   cosi.qt_apontamentos                                                   as qtd_apontamentos,
                   co.codigo                                                              as cod_colaborador_resolucao,
                   co.nome::text                                                          as nome_colaborador_resolucao,
                   cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade)         as data_hora_resolucao,
                   cosi.data_hora_inicio_resolucao at time zone tz_unidade(c.cod_unidade) as data_hora_inicio_resolucao,
                   cosi.data_hora_fim_resolucao at time zone tz_unidade(c.cod_unidade)    as data_hora_fim_resolucao,
                   cosi.feedback_conserto                                                 as feedback_resolucao,
                   millis_to_minutes(cosi.tempo_realizacao)                               as duracao_resolucao_minutos,
                   cosi.km                                                                as km_veiculo_coletado_resolucao,
                   cp.codigo                                                              as cod_pergunta,
                   cp.pergunta                                                            as descricao_pergunta,
                   cap.codigo                                                             as cod_alternativa,
                   cap.alternativa                                                        as descricao_alternativa,
                   cap.alternativa_tipo_outros                                            as alternativa_tipo_outros,
                   case
                       when cap.alternativa_tipo_outros
                           then
                           (select crn.resposta_outros
                            from checklist_respostas_nok crn
                            where crn.cod_checklist = c.codigo
                              and crn.cod_alternativa = cap.codigo)::text
                       end                                                                as descricao_tipo_outros,
                   cap.prioridade::text                                                   as prioridade_alternativa,
                   an.url_midia::text                                                     as url_midia,
                   an.cod_checklist::bigint                                               as cod_checklist
            from checklist c
                     join checklist_ordem_servico cos
                          on c.codigo = cos.cod_checklist
                     join checklist_ordem_servico_itens cosi
                          on cos.codigo = cosi.cod_os
                              and cos.cod_unidade = cosi.cod_unidade
                     join checklist_perguntas cp
                          on cosi.cod_pergunta_primeiro_apontamento = cp.codigo
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist_alternativa_prioridade prio
                          on cap.prioridade = prio.prioridade
                     join veiculo v
                          on c.placa_veiculo = v.placa
                     left join colaborador co
                               on co.cpf = cosi.cpf_mecanico
                     left join checklist_ordem_servico_itens_midia im
                               on im.cod_item_os = cosi.codigo
                     left join checklist_respostas_midias_alternativas_nok an
                               on im.cod_midia_nok = an.codigo
            where f_if(f_cod_unidade is null, true, cos.cod_unidade = f_cod_unidade)
              and f_if(f_cod_os is null, true, cos.codigo = f_cod_os)
              and f_if(f_placa_veiculo is null, true, c.placa_veiculo = f_placa_veiculo)
              and f_if(f_prioridade_alternativa is null, true, cap.prioridade = f_prioridade_alternativa)
              and f_if(f_status_itens is null, true, cosi.status_resolucao = f_status_itens)
            limit f_limit offset f_offset
        ),
             dados_veiculo as (
                 select v.placa::text as placa_veiculo,
                        v.km          as km_atual_veiculo
                 from veiculo v
                 where v.placa = f_placa_veiculo
             )

             -- Nós usamos esse dados_veiculo com f_if pois pode acontecer de não existir dados para os filtros aplicados e
             -- desse modo acabaríamos não retornando placa e km também, mas essas são informações necessárias pois o objeto
             -- construído a partir dessa function usa elas.
        select d.cod_veiculo                                                             as cod_veiculo,
               f_if(d.placa_veiculo is null, dv.placa_veiculo, d.placa_veiculo)          as placa_veiculo,
               f_if(d.km_atual_veiculo is null, dv.km_atual_veiculo, d.km_atual_veiculo) as km_atual_veiculo,
               d.cod_os                                                                  as cod_os,
               d.cod_unidade_item_os                                                     as cod_unidade_item_os,
               d.cod_item_os                                                             as cod_item_os,
               d.data_hora_primeiro_apontamento_item                                     as data_hora_primeiro_apontamento_item,
               d.status_item_os                                                          as status_item_os,
               d.prazo_resolucao_item_horas                                              as prazo_resolucao_item_horas,
               d.prazo_restante_resolucao_item_minutos                                   as prazo_restante_resolucao_item_minutos,
               d.qtd_apontamentos                                                        as qtd_apontamentos,
               d.cod_colaborador_resolucao                                               as cod_colaborador_resolucao,
               d.nome_colaborador_resolucao                                              as nome_colaborador_resolucao,
               d.data_hora_resolucao                                                     as data_hora_resolucao,
               d.data_hora_inicio_resolucao                                              as data_hora_inicio_resolucao,
               d.data_hora_fim_resolucao                                                 as data_hora_fim_resolucao,
               d.feedback_resolucao                                                      as feedback_resolucao,
               d.duracao_resolucao_minutos                                               as duracao_resolucao_minutos,
               d.km_veiculo_coletado_resolucao                                           as km_veiculo_coletado_resolucao,
               d.cod_pergunta                                                            as cod_pergunta,
               d.descricao_pergunta                                                      as descricao_pergunta,
               d.cod_alternativa                                                         as cod_alternativa,
               d.descricao_alternativa                                                   as descricao_alternativa,
               d.alternativa_tipo_outros                                                 as alternativa_tipo_outros,
               d.descricao_tipo_outros                                                   as descricao_tipo_outros,
               d.prioridade_alternativa                                                  as prioridade_alternativa,
               d.url_midia                                                               as url_midia,
               d.cod_checklist                                                           as cod_checklist
        from dados d
                 right join dados_veiculo dv
                            on d.placa_veiculo = dv.placa_veiculo
        order by cod_os, cod_item_os, cod_checklist;
end;
$$;

create or replace function func_checklist_os_resolver_itens(f_cod_unidade bigint,
                                                            f_cod_veiculo bigint,
                                                            f_cod_itens bigint[],
                                                            f_cpf bigint,
                                                            f_tempo_realizacao bigint,
                                                            f_km bigint,
                                                            f_status_resolucao text,
                                                            f_data_hora_conserto timestamp with time zone,
                                                            f_data_hora_inicio_resolucao timestamp with time zone,
                                                            f_data_hora_fim_resolucao timestamp with time zone,
                                                            f_feedback_conserto text) returns bigint
    language plpgsql
as
$$
declare
    v_cod_item                  bigint;
    v_data_realizacao_checklist timestamp with time zone;
    v_alternativa_item          text;
    v_error_message             text                                 := E'Erro! A data de resolução %s não pode ser anterior a data de abertura %s do item "%s".';
    v_qtd_linhas_atualizadas    bigint;
    v_total_linhas_atualizadas  bigint                               := 0;
    v_cod_processo  constant    bigint not null                      := (select nextval('CODIGO_RESOLUCAO_ITEM_OS'));
    v_tipo_processo constant    types.veiculo_processo_type not null := 'FECHAMENTO_ITEM_CHECKLIST';
    v_km_real                   bigint;
begin
    v_km_real := (select *
                  from func_veiculo_update_km_atual(f_cod_unidade,
                                                    f_cod_veiculo,
                                                    f_km,
                                                    v_cod_processo,
                                                    v_tipo_processo,
                                                    true,
                                                    CURRENT_TIMESTAMP));

    foreach v_cod_item in array f_cod_itens
        loop
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
            select c.data_hora, capd.alternativa
            from checklist_ordem_servico_itens cosi
                     join checklist_ordem_servico cos
                          on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
                     join checklist c on cos.cod_checklist = c.codigo
                     join checklist_alternativa_pergunta_data capd
                          on capd.codigo = cosi.cod_alternativa_primeiro_apontamento
            where cosi.codigo = v_cod_item
            into v_data_realizacao_checklist, v_alternativa_item;

            -- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
            if v_data_realizacao_checklist is not null and v_data_realizacao_checklist >= f_data_hora_inicio_resolucao
            then
                perform throw_generic_error(
                        FORMAT(v_error_message, v_data_realizacao_checklist, f_data_hora_inicio_resolucao,
                               v_alternativa_item));
            end if;

            -- Atualiza os itens
            update checklist_ordem_servico_itens
            set cpf_mecanico               = f_cpf,
                tempo_realizacao           = f_tempo_realizacao,
                km                         = v_km_real,
                status_resolucao           = f_status_resolucao,
                data_hora_conserto         = f_data_hora_conserto,
                data_hora_inicio_resolucao = f_data_hora_inicio_resolucao,
                data_hora_fim_resolucao    = f_data_hora_fim_resolucao,
                feedback_conserto          = f_feedback_conserto,
                cod_processo               = v_cod_processo
            where cod_unidade = f_cod_unidade
              and codigo = v_cod_item
              and data_hora_conserto is null;

            get diagnostics v_qtd_linhas_atualizadas = row_count;

            -- Verificamos se o update funcionou.
            if v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0
            then
                perform throw_generic_error('Erro ao marcar os itens como resolvidos.');
            end if;
            v_total_linhas_atualizadas := v_total_linhas_atualizadas + v_qtd_linhas_atualizadas;
        end loop;
    return v_total_linhas_atualizadas;
end;
$$;

create or replace function implantacao.tg_func_veiculo_confere_planilha_importa_veiculo()
    returns trigger
    security definer
    language plpgsql
as
$$
declare
    v_valor_similaridade          constant real     := 0.4;
    v_valor_similaridade_diagrama constant real     := 0.5;
    v_sem_similaridade            constant real     := 0.0;
    v_qtd_erros                            smallint := 0;
    v_msgs_erros                           text;
    v_quebra_linha                         text     := CHR(10);
    v_cod_marca_banco                      bigint;
    v_similaridade_marca                   real;
    v_marca_modelo                         text;
    v_cod_modelo_banco                     bigint;
    v_similaridade_modelo                  real;
    v_cod_diagrama_banco                   bigint;
    v_nome_diagrama_banco                  text;
    v_similaridade_diagrama                real;
    v_diagrama_tipo                        text;
    v_eixos_diagrama                       text;
    v_diagrama_motorizado                  boolean;
    v_cod_tipo_banco                       bigint;
    v_similaridade_tipo                    real;
    v_possui_hubodometro_flag              boolean;
    v_similaridades_possui_hubodometro     real;
    v_possui_hubodometro                   text;
    v_possui_hubodometro_possibilidades    text[]   := ('{"SIM", "OK", "TRUE", "TEM"}');
begin
    if (tg_op = 'UPDATE' and old.status_import_realizado is true)
    then
        return old;
    else
        if (tg_op = 'UPDATE')
        then
            new.cod_unidade = old.cod_unidade;
            new.cod_empresa = old.cod_empresa;
        end if;
        new.placa_formatada_import := remove_espacos_e_caracteres_especiais(new.placa_editavel);
        new.marca_formatada_import := remove_espacos_e_caracteres_especiais(new.marca_editavel);
        new.modelo_formatado_import := remove_espacos_e_caracteres_especiais(new.modelo_editavel);
        new.tipo_formatado_import := remove_espacos_e_caracteres_especiais(new.tipo_editavel);
        new.identificador_frota_import := new.identificador_frota_editavel;
        new.usuario_update := SESSION_USER;

        -- VERIFICA SE EMPRESA EXISTE
        if not EXISTS(select e.codigo from empresa e where e.codigo = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE EXISTE
        if not EXISTS(select u.codigo from unidade u where u.codigo = new.cod_unidade)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', v_quebra_linha);
        end if;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        if not EXISTS(
                select u.codigo from unidade u where u.codigo = new.cod_unidade and u.cod_empresa = new.cod_empresa)
        then
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros =
                    concat(v_msgs_erros, v_qtd_erros, '- A UNIDADE NÃO PERTENCE A EMPRESA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        if (new.placa_formatada_import is not null) then
            if LENGTH(new.placa_formatada_import) <> 7
            then
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO POSSUI 7 CARACTERES', v_quebra_linha);
            else
                if EXISTS(select v.placa
                          from veiculo v
                          where v.placa = new.placa_formatada_import
                            and v.cod_empresa != new.cod_empresa)
                then
                    v_qtd_erros = v_qtd_erros + 1;
                    v_msgs_erros =
                            concat(v_msgs_erros, v_qtd_erros, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   v_quebra_linha);
                else
                    if EXISTS(select v.placa
                              from veiculo v
                              where v.placa = new.placa_formatada_import
                                and v.cod_empresa = new.cod_empresa
                                and cod_unidade != new.cod_unidade)
                    then
                        v_qtd_erros = v_qtd_erros + 1;
                        v_msgs_erros = concat(v_msgs_erros, v_qtd_erros,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              v_quebra_linha);
                    end if;
                end if;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A PLACA NÃO PODE SER NULA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        select distinct on (new.marca_formatada_import) mav.codigo                                                        as cod_marca_banco,
                                                        MAX(func_gera_similaridade(new.marca_formatada_import, mav.nome)) as similariedade_marca
        into v_cod_marca_banco, v_similaridade_marca
        from marca_veiculo mav
        group by new.marca_formatada_import, new.marca_editavel, mav.nome, mav.codigo
        order by new.marca_formatada_import, similariedade_marca desc;

        v_marca_modelo := CONCAT(v_cod_marca_banco, new.modelo_formatado_import);
        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        if (v_similaridade_marca >= v_valor_similaridade)
        then
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            select distinct on (v_marca_modelo) mov.codigo as cod_modelo_veiculo,
                                                case
                                                    when v_cod_marca_banco = mov.cod_marca
                                                        then
                                                        MAX(func_gera_similaridade(v_marca_modelo,
                                                                                   CONCAT(mov.cod_marca, mov.nome)))
                                                    else v_sem_similaridade
                                                    end    as similariedade_modelo
            into v_cod_modelo_banco, v_similaridade_modelo
            from modelo_veiculo mov
            where mov.cod_empresa = new.cod_empresa
            group by v_marca_modelo, mov.nome, mov.codigo
            order by v_marca_modelo, similariedade_modelo desc;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            if (v_similaridade_modelo < v_valor_similaridade or v_similaridade_modelo is null)
            then
                insert into modelo_veiculo (nome, cod_marca, cod_empresa)
                values (new.modelo_editavel, v_cod_marca_banco, new.cod_empresa)
                returning codigo into v_cod_modelo_banco;
            end if;
        else
            v_qtd_erros = v_qtd_erros + 1;
            v_msgs_erros = concat(v_msgs_erros, v_qtd_erros, '- A MARCA NÃO FOI ENCONTRADA', v_quebra_linha);
        end if;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        v_eixos_diagrama := CONCAT(new.qtd_eixos_editavel, new.tipo_formatado_import);
        -- Procura diagrama no banco:
        with info_diagramas as (
            select COUNT(vde.posicao) as qtd_eixos,
                   vde.cod_diagrama   as codigo,
                   vd.nome            as nome,
                   vd.motorizado      as diagrama_motorizado
            from veiculo_diagrama_eixos vde
                     join
                 veiculo_diagrama vd on vde.cod_diagrama = vd.codigo
            group by vde.cod_diagrama, vd.nome, vd.motorizado),

             diagramas as (
                 select vdup.cod_veiculo_diagrama as cod_diagrama,
                        vdup.nome                 as nome_diagrama,
                        vdup.qtd_eixos            as qtd_eixos,
                        vdup.motorizado           as diagrama_motorizado
                 from implantacao.veiculo_diagrama_usuario_prolog vdup
                 union all
                 select id.codigo as cod_diagrama, id.nome as nome_diagrama, id.qtd_eixos, id.diagrama_motorizado
                 from info_diagramas id)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        select distinct on (v_eixos_diagrama) d.nome_diagrama as nome_diagrama,
                                              d.cod_diagrama  as diagrama_banco,
                                              case
                                                  when d.qtd_eixos ::text = new.qtd_eixos_editavel
                                                      then
                                                      MAX(func_gera_similaridade(v_eixos_diagrama,
                                                                                 CONCAT(d.qtd_eixos, d.nome_diagrama)))
                                                  else v_sem_similaridade
                                                  end         as similariedade_diagrama,
                                              d.diagrama_motorizado
        into v_nome_diagrama_banco, v_cod_diagrama_banco,
            v_similaridade_diagrama, v_diagrama_motorizado
        from diagramas d
        group by v_eixos_diagrama, d.nome_diagrama, d.cod_diagrama, d.qtd_eixos, d.diagrama_motorizado
        order by v_eixos_diagrama, similariedade_diagrama desc;

        -- Se o diagrama não for motorizado, perguntar se possui hubodômetro
        -- Verifica se possui hubodometro;
        case when (v_diagrama_motorizado is false and new.possui_hubodometro_editavel is not null)
            then
                foreach v_possui_hubodometro in array v_possui_hubodometro_possibilidades
                    loop
                        v_similaridades_possui_hubodometro :=
                                MAX(func_gera_similaridade(new.possui_hubodometro_editavel,
                                                           v_possui_hubodometro));
                        if (v_similaridades_possui_hubodometro >
                            v_valor_similaridade)
                        then
                            v_possui_hubodometro_flag = true;
                        end if;
                    end loop;
            else v_possui_hubodometro_flag = false;
            end case;

        v_diagrama_tipo := CONCAT(v_nome_diagrama_banco, new.tipo_formatado_import);
        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        case when (v_similaridade_diagrama >= v_valor_similaridade_diagrama)
            then
                select distinct on (v_diagrama_tipo) vt.codigo as cod_tipo_veiculo,
                                                     case
                                                         when v_cod_diagrama_banco = vt.cod_diagrama
                                                             then MAX(func_gera_similaridade(new.tipo_formatado_import, vt.nome))
                                                         else v_sem_similaridade
                                                         end   as similariedade_tipo_diagrama
                into v_cod_tipo_banco, v_similaridade_tipo
                from veiculo_tipo vt
                where vt.cod_empresa = new.cod_empresa
                group by v_diagrama_tipo,
                         vt.codigo
                order by v_diagrama_tipo, similariedade_tipo_diagrama desc;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                if (v_similaridade_tipo < v_valor_similaridade or v_similaridade_tipo is null)
                then
                    insert into veiculo_tipo (nome, status_ativo, cod_diagrama, cod_empresa)
                    values (new.tipo_editavel, true, v_cod_diagrama_banco, new.cod_empresa)
                    returning codigo into v_cod_tipo_banco;
                end if;
            else
                v_qtd_erros = v_qtd_erros + 1;
                v_msgs_erros =
                        concat(v_msgs_erros, v_qtd_erros, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', v_quebra_linha);
            end case;
        -- VERIFICA QTD DE ERROS
        if (v_qtd_erros > 0)
        then
            new.status_import_realizado = false;
            new.erros_encontrados = v_msgs_erros;
        else
            if (v_qtd_erros = 0 and EXISTS(select v.placa
                                           from veiculo v
                                           where v.placa = new.placa_formatada_import
                                             and v.cod_empresa = new.cod_empresa
                                             and cod_unidade = new.cod_unidade))
            then
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                update veiculo_data
                set cod_modelo          = v_cod_modelo_banco,
                    cod_tipo            = v_cod_tipo_banco,
                    km                  = new.km_editavel,
                    cod_diagrama        = v_cod_diagrama_banco,
                    identificador_frota = new.identificador_frota_import
                where placa = new.placa_formatada_import
                  and cod_empresa = new.cod_empresa
                  and cod_unidade = new.cod_unidade;
                new.status_import_realizado = null;
                new.erros_encontrados = 'A PLACA JÁ ESTAVA CADASTRADA - INFORMAÇÕES FORAM ATUALIZADAS.';
            else
                if (v_qtd_erros = 0 and not EXISTS(select v.placa
                                                   from veiculo v
                                                   where v.placa = new.placa_formatada_import))
                then
                    -- CADASTRA VEÍCULO.
                    insert into veiculo (placa,
                                         cod_unidade,
                                         km,
                                         status_ativo,
                                         cod_tipo,
                                         cod_modelo,
                                         cod_eixos,
                                         data_hora_cadastro,
                                         cod_unidade_cadastro,
                                         cod_empresa,
                                         cod_diagrama,
                                         identificador_frota,
                                         possui_hubodometro,
                                         motorizado)
                    values (new.placa_formatada_import,
                            new.cod_unidade,
                            new.km_editavel,
                            true,
                            v_cod_tipo_banco,
                            v_cod_modelo_banco,
                            1,
                            NOW(),
                            new.cod_unidade,
                            new.cod_empresa,
                            v_cod_diagrama_banco,
                            new.identificador_frota_import,
                            v_possui_hubodometro_flag,
                            v_diagrama_motorizado);
                    new.status_import_realizado = true;
                    new.erros_encontrados = '-';
                end if;
            end if;
        end if;
    end if;
    return new;
end;
$$;

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
                marca_veiculo             text,
                possui_hubodometro        boolean
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
               mav.nome::text                   as marca_veiculo,
               veh.possui_hubodometro           as possui_hubodometro
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
        order by veh.data_hora_utc, veh.estado_antigo;
end;
$$;

create or replace function func_veiculo_get_all_by_unidades(f_cod_unidades bigint[],
                                                            f_apenas_ativos boolean,
                                                            f_cod_tipo_veiculo bigint)
    returns table
            (
                codigo              bigint,
                placa               text,
                cod_regional        bigint,
                nome_regional       text,
                cod_unidade         bigint,
                nome_unidade        text,
                km                  bigint,
                status_ativo        boolean,
                cod_tipo            bigint,
                cod_modelo          bigint,
                cod_diagrama        bigint,
                identificador_frota text,
                modelo              text,
                possui_hubodometro  boolean,
                motorizado          boolean,
                acoplado            boolean,
                nome_diagrama       text,
                dianteiro           bigint,
                traseiro            bigint,
                tipo                text,
                marca               text,
                cod_marca           bigint
            )
    language sql
as
$$
select v.codigo                                                as codigo,
       v.placa                                                 as placa,
       r.codigo                                                as cod_regional,
       r.regiao                                                as nome_regional,
       u.codigo                                                as cod_unidade,
       u.nome                                                  as nome_unidade,
       v.km                                                    as km,
       v.status_ativo                                          as status_ativo,
       v.cod_tipo                                              as cod_tipo,
       v.cod_modelo                                            as cod_modelo,
       v.cod_diagrama                                          as cod_diagrama,
       v.identificador_frota                                   as identificador_frota,
       mv.nome                                                 as modelo,
       v.possui_hubodometro                                    as possui_hubodometro,
       v.motorizado                                            as motorizado,
       v.acoplado                                              as acoplado,
       vd.nome                                                 as nome_diagrama,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'D') as dianteiro,
       COUNT(vde.tipo_eixo) filter (where vde.tipo_eixo = 'T') as traseiro,
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
where v.cod_unidade = any (f_cod_unidades)
  and case
          when f_apenas_ativos is null or f_apenas_ativos = false
              then true
          else v.status_ativo = true
    end
  and case
          when f_cod_tipo_veiculo is null
              then true
          else v.cod_tipo = f_cod_tipo_veiculo
    end
group by v.placa, v.codigo, v.codigo, v.placa, u.codigo, v.km, v.status_ativo, v.cod_tipo, v.cod_modelo,
         v.motorizado, v.acoplado, v.possui_hubodometro, v.cod_diagrama, v.identificador_frota, r.codigo, mv.nome,
         vd.nome, vt.nome, mav.nome, mav.codigo
order by v.placa;
$$;

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
begin
    perform suporte.func_historico_salva_execucao();

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veiculo possui pneus aplicados.
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se possui acoplamento.
    if EXISTS(select vd.codigo
              from veiculo_data vd
              where vd.placa = f_placa
                and vd.cod_unidade = f_cod_unidade
                and vd.acoplado is true)
    then
        raise exception 'Erro! A Placa: % possui acoplamentos. Favor removê-los', f_placa;
    end if;

    -- Verifica se a placa possui aferição.
    if exists(select a.codigo from afericao_data a where a.placa_veiculo = f_placa)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.placa_veiculo = f_placa
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

CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_IDENTIFICADOR_FROTA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT,
                                                       F_POSSUI_HUBODOMETRO BOOLEAN)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA           BIGINT;
    V_STATUS_ATIVO CONSTANT BOOLEAN := TRUE;
    V_COD_DIAGRAMA          BIGINT;
    V_COD_VEICULO_PROLOG    BIGINT;
    V_MOTORIZADO            BOOLEAN;
BEGIN
    -- Busca o código da empresa de acordo com a unidade
    V_COD_EMPRESA := (SELECT U.COD_EMPRESA
                      FROM UNIDADE U
                      WHERE U.CODIGO = F_COD_UNIDADE);

    -- Validamos se o KM foi inputado corretamente.
    IF (F_KM_ATUAL < 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'A quilometragem do veículo não pode ser um número negativo.');
    END IF;

    -- Validamos se o modelo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM MODELO_VEICULO MV
                          WHERE MV.COD_EMPRESA = V_COD_EMPRESA
                            AND MV.CODIGO = F_COD_MODELO))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o modelo do veículo e tente novamente.');
    END IF;

    -- Validamos se o tipo do veículo está mapeado.
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM VEICULO_TIPO VT
                          WHERE VT.CODIGO = F_COD_TIPO
                            AND VT.COD_EMPRESA = V_COD_EMPRESA))
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Por favor, verifique o tipo do veículo e tente novamente.');
    END IF;

    -- Busca o código do diagrama de acordo com o tipo de veículo.
    V_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                       FROM VEICULO_TIPO VT
                       WHERE VT.CODIGO = F_COD_TIPO
                         AND VT.COD_EMPRESA = V_COD_EMPRESA);

    V_MOTORIZADO := (SELECT VD.MOTORIZADO
                     FROM VEICULO_DIAGRAMA VD
                     WHERE VD.CODIGO = V_COD_DIAGRAMA);

    if (V_MOTORIZADO AND F_POSSUI_HUBODOMETRO)
    then
        perform throw_generic_error('Não é possivel cadastrar um veiculo motorizado com hubodometro, favor verificar.');
    end if;

    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO VEICULO(COD_EMPRESA,
                        COD_UNIDADE,
                        PLACA,
                        KM,
                        STATUS_ATIVO,
                        COD_TIPO,
                        COD_MODELO,
                        COD_DIAGRAMA,
                        MOTORIZADO,
                        COD_UNIDADE_CADASTRO,
                        IDENTIFICADOR_FROTA,
                        POSSUI_HUBODOMETRO)
    VALUES (V_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            V_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            V_COD_DIAGRAMA,
            V_MOTORIZADO,
            F_COD_UNIDADE,
            F_IDENTIFICADOR_FROTA,
            F_POSSUI_HUBODOMETRO)
    RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF V_COD_VEICULO_PROLOG IS NULL OR V_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;

CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                               F_NOME_TABELA_IMPORT TEXT,
                                                                               F_COD_EMPRESA BIGINT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_JSON_VEICULOS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
begin
    execute FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_EMPRESA,
                                                COD_UNIDADE,
                                                PLACA_EDITAVEL,
                                                PLACA_FORMATADA_IMPORT,
                                                KM_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MARCA_FORMATADA_IMPORT,
                                                MODELO_EDITAVEL,
                                                MODELO_FORMATADO_IMPORT,
                                                TIPO_EDITAVEL,
                                                TIPO_FORMATADO_IMPORT,
                                                QTD_EIXOS_EDITAVEL,
                                                IDENTIFICADOR_FROTA_EDITAVEL,
                                                POSSUI_HUBODOMETRO_EDITAVEL  )
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_EMPRESA,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT                                         AS PLACA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> ''placa'')) :: TEXT  AS PLACA_FORMATADA_IMPORT,
                          (SRC ->> ''km'') :: BIGINT                                          AS KM,
                          (SRC ->> ''marca'') :: TEXT                                         AS MARCA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''marca'')) :: TEXT  AS MARCA_FORMATADA_IMPORT,
                          (SRC ->> ''modelo'') :: TEXT                                        AS MODELO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''modelo'')) :: TEXT AS MODELO_FORMATADO_IMPORT,
                          (SRC ->> ''tipo'') :: TEXT                                          AS TIPO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''tipo'')) :: TEXT   AS TIPO_FORMATADO_IMPORT,
                          (SRC ->> ''qtdEixos'') :: TEXT                                      AS QTD_EIXOS,
                          (SRC ->> ''identificadorFrota'') :: TEXT                            AS IDENTIFICADOR_FROTA,
                          (SRC ->> ''possuiHubodometro'') :: TEXT                             AS POSSUI_HUBODOMETRO
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   f_nome_tabela_import,
                   f_cod_dados_autor_import,
                   f_cod_empresa,
                   f_cod_unidade,
                   f_json_veiculos);
end
$$;

-- Sobre:
--
-- Function para editar um veículo no Prolog. Pode ser chamada tanto pelo fluxo do Prolog quanto pela API ou mesmo
-- suporte. A origem é informada pelo atributo 'f_origem_edicao`.
--
-- A placa é recebida mas ainda não utilizada pois alteração de placa ainda não é permitida.
--
-- Histórico:
-- 2020-02-25 -> Function criada (wvinim - PL-1965).
-- 2020-04-29 -> Altera function para salvar identificador de frota (thaisksf - PL-2691).
-- 2020-09-04 -> Altera function para gerar histórico de edição (luiz_fp - PL-3096).
-- 2020-09-24 -> Corrige function para considerar código da marca no total de edições (luiz_fp).
-- 2020-11-03 -> Atualiza para usar novo formato de geração de histórico de edições (gustavocnp95 - PL-3204)
-- 2020-11-06 -> Adiciona parâmetro f_novo_possui_hubodometro (steinert999 - PL-3223).
-- 2020-11-10 -> Adiciona parâmetro v_novo_motorizado (steinert999 - PL-3223).
-- 2020-12-21 -> Mescla as duas versões da function (thaisksf - PL-3326).
create or replace function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
                                                         f_nova_placa text,
                                                         f_novo_identificador_frota text,
                                                         f_novo_km bigint,
                                                         f_novo_cod_tipo bigint,
                                                         f_novo_cod_modelo bigint,
                                                         f_novo_status boolean,
                                                         f_novo_possui_hubodometro boolean,
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
                antigo_status               boolean,
                antigo_possui_hubodometro   boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant  bigint not null  := (select v.cod_empresa
                                                       from veiculo v
                                                       where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant  bigint not null  := (select vt.cod_diagrama
                                                       from veiculo_tipo vt
                                                       where vt.codigo = f_novo_cod_tipo
                                                         and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant  bigint not null  := (select mv.cod_marca
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
    v_novo_motorizado   constant  boolean not null := (select vd.motorizado
                                                       from veiculo_diagrama vd
                                                       where vd.codigo = v_novo_cod_diagrama);
    v_antigo_possui_hubodometro   boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
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
                                                  f_novo_status,
                                                  f_novo_possui_hubodometro)) - hstore((v_antigo_identificador_frota,
                                                                                        v_antigo_km,
                                                                                        v_antigo_cod_diagrama,
                                                                                        v_antigo_cod_tipo,
                                                                                        v_antigo_cod_marca,
                                                                                        v_antigo_cod_modelo,
                                                                                        v_antigo_status,
                                                                                        v_antigo_possui_hubodometro))));

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
                                                     f_novo_possui_hubodometro,
                                                     v_total_edicoes);

        update veiculo
        set identificador_frota = f_novo_identificador_frota,
            km                  = f_novo_km,
            cod_modelo          = f_novo_cod_modelo,
            cod_tipo            = f_novo_cod_tipo,
            cod_diagrama        = v_novo_cod_diagrama,
            status_ativo        = f_novo_status,
            motorizado          = v_novo_motorizado,
            possui_hubodometro  = f_novo_possui_hubodometro,
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
               v_antigo_status,
               v_antigo_possui_hubodometro;
end;
$$;

create or replace function func_movimentacao_insert_movimentacao_veiculo_destino(f_cod_movimentacao bigint,
                                                                                 f_tipo_destino text,
                                                                                 f_cod_veiculo bigint,
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
    v_km_atual                   bigint;
begin
    select v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo v
    where v.codigo = f_cod_veiculo
    into strict
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    -- Realiza inserção da movimentação destino.
    insert into movimentacao_destino(cod_movimentacao,
                                     tipo_destino,
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
            v_km_atual,
            f_posicao_prolog,
            null,
            null,
            null,
            null,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir o destino veiculo da movimentação');
    end if;
end
$$;

create or replace function func_movimentacao_insert_movimentacao_veiculo_origem(f_cod_pneu bigint,
                                                                                f_cod_unidade bigint,
                                                                                f_tipo_origem text,
                                                                                f_cod_movimentacao bigint,
                                                                                f_cod_veiculo bigint,
                                                                                f_posicao_prolog integer)
    returns void
    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo           bigint;
    v_cod_diagrama_veiculo       bigint;
    v_km_atual                   bigint;
    v_cod_movimentacao_realizada bigint;
    v_tipo_origem_atual constant text := (select p.status
                                          from pneu p
                                          where p.codigo = f_cod_pneu
                                            and p.cod_unidade = f_cod_unidade
                                            and f_tipo_origem in (select p.status
                                                                  from pneu p
                                                                  where p.codigo = f_cod_pneu
                                                                    and p.cod_unidade = f_cod_unidade));
begin
    select v.cod_tipo,
           v.cod_diagrama,
           v.km
    from veiculo v
    where v.codigo = f_cod_veiculo
    into strict
        v_cod_tipo_veiculo,
        v_cod_diagrama_veiculo,
        v_km_atual;

    -- Realiza inserção da movimentação origem.
    insert into movimentacao_origem(cod_movimentacao,
                                    tipo_origem,
                                    km_veiculo,
                                    posicao_pneu_origem,
                                    cod_diagrama,
                                    cod_veiculo)
    values (f_cod_movimentacao,
            v_tipo_origem_atual,
            v_km_atual,
            f_posicao_prolog,
            v_cod_diagrama_veiculo,
            f_cod_veiculo)
    returning cod_movimentacao into v_cod_movimentacao_realizada;

    if (v_cod_movimentacao_realizada <= 0)
    then
        perform throw_server_side_error('Erro ao inserir a origem veiculo da movimentação');
    end if;
end
$$;

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
                                                                   f_novo_possui_hubodometro boolean,
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
    v_antigo_possui_hubodometro          boolean;
    v_data_hora_edicao_tz_unidade        timestamp with time zone;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status,
        v_antigo_possui_hubodometro
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    v_data_hora_edicao_tz_unidade := f_data_hora_edicao_tz_aplicado at time zone
                                     tz_unidade(v_cod_unidade);

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
                                          estado_antigo,
                                          possui_hubodometro)
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
            true,
            v_antigo_possui_hubodometro);

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
                                          estado_antigo,
                                          possui_hubodometro)
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
            false,
            f_novo_possui_hubodometro);

    return query
        select v_cod_edicao_historico_estado_antigo, v_cod_edicao_historico_estado_novo;
end;
$$;