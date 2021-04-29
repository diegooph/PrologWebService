-- #####################################################################################################################
-- #####################################################################################################################
-- ############################ MIGRATION DE INTEGRAÇÃO DAS AFERIÇÕES COM O PROTHEUS ###################################
-- ################################################  PLI-93 ############################################################
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- ############################# CRIA TABELAS PARA REGISTRAR AS AFERIÇÕES INTEGRADAS ###################################
-- #####################################################################################################################
-- PL-2559

-- Cria a tabela pai contendo os dados principais da aferição
create table if not exists integracao.afericao_integrada
(
    codigo                   bigserial                not null
        constraint pk_afericao_integrada
            primary key,
    cod_empresa_prolog       bigint                   not null,
    cod_empresa_cliente      text,
    cod_unidade_prolog       bigint                   not null,
    cod_unidade_cliente      text                     not null,
    cpf_aferidor             text                     not null,
    placa_veiculo            text,
    cod_tipo_veiculo_prolog  bigint,
    cod_tipo_veiculo_cliente text,
    cod_diagrama_prolog      bigint,
    km_veiculo               text,
    tempo_realizacao         bigint                   not null,
    data_hora                timestamp with time zone not null,
    tipo_medicao_coletada    text                     not null,
    tipo_processo_coleta     text                     not null
);

comment on table integracao.afericao_integrada
    is 'Registro do envio da coleta de aferições para o cliente via integração.';

comment on column integracao.afericao_integrada.codigo is 'Código único para vincular à tabela de registro de valores.';
comment on column integracao.afericao_integrada.cod_empresa_prolog is 'Código da empresa no prolog.';
comment on column integracao.afericao_integrada.cod_empresa_cliente is 'Código da empresa no cliente.';
comment on column integracao.afericao_integrada.cod_unidade_prolog is 'Código da unidade no prolog.';
comment on column integracao.afericao_integrada.cod_unidade_cliente is 'Código da unidade no cliente.';
comment on column integracao.afericao_integrada.cpf_aferidor is 'CPF do colaborador que realizou a aferição.';
comment on column integracao.afericao_integrada.placa_veiculo is 'Placa do veículo aferido.';
comment on column integracao.afericao_integrada.cod_tipo_veiculo_prolog is 'Código do tipo do veículo no prolog.';
comment on column integracao.afericao_integrada.cod_tipo_veiculo_cliente is 'Código do tipo do veículo no cliente.';
comment on column integracao.afericao_integrada.cod_diagrama_prolog
    is 'Código do diagrama do veículo aferido no prolog.';
comment on column integracao.afericao_integrada.km_veiculo is 'Km do veículo no momento da aferição.';
comment on column integracao.afericao_integrada.tempo_realizacao is 'Tempo decorrido de realização em milissegundos.';
comment on column integracao.afericao_integrada.data_hora is 'Data e Hora da execução da aferição.';
comment on column integracao.afericao_integrada.tipo_medicao_coletada
    is 'Tipo da medição (SULCO, PRESSAO, SULCO_PRESSAO).';
comment on column integracao.afericao_integrada.tipo_processo_coleta
    is 'Tipo do processo de coleta (PLACA, PNEU_AVULSO).';

-- Cria a tabela filha contendo os valores da aferição, com referência à tabela pai
create table if not exists integracao.afericao_valores_integrada
(
    codigo                       bigserial not null
        constraint pk_afericao_valores_integrada
            primary key,
    cod_afericao_integrada       bigint    not null
        constraint fk_afericao_valores_integrada_afericao_integrada
            references integracao.afericao_integrada,
    cod_pneu                     text      not null,
    cod_pneu_cliente             text      not null,
    vida_momento_afericao        integer   not null,
    psi                          real,
    altura_sulco_interno         real,
    altura_sulco_central_interno real,
    altura_sulco_central_externo real,
    altura_sulco_externo         real,
    posicao_prolog               integer,
    nomenclatura_posicao         text
);
comment on table integracao.afericao_valores_integrada
    is 'Registro do envio dos valores de aferições para o cliente via integração.';

comment on column integracao.afericao_valores_integrada.codigo
    is 'Código único da tabela para facilitar possíveis processamentos.';
comment on column integracao.afericao_valores_integrada.cod_afericao_integrada
    is 'Código de referência à tabela afericao_integrada.';
comment on column integracao.afericao_valores_integrada.cod_pneu is 'Código único de identificação do pneu.';
comment on column integracao.afericao_valores_integrada.cod_pneu_cliente
    is 'Código de identificação do pneu pelo cliente, geralmente o número de fogo.';
comment on column integracao.afericao_valores_integrada.vida_momento_afericao is 'Vida do pneu no momento da aferição.';
comment on column integracao.afericao_valores_integrada.psi is 'Pressão aferida.';
comment on column integracao.afericao_valores_integrada.altura_sulco_interno is 'Altura do sulco interno aferido.';
comment on column integracao.afericao_valores_integrada.altura_sulco_central_interno
    is 'Altura do sulco central interno aferido.';
comment on column integracao.afericao_valores_integrada.altura_sulco_externo is 'Altura do sulco externo aferido.';
comment on column integracao.afericao_valores_integrada.altura_sulco_central_externo
    is 'Altura do sulco central externo aferido.';
comment on column integracao.afericao_valores_integrada.posicao_prolog
    is 'Código da posição no prolog no momento da aferição.';
comment on column integracao.afericao_valores_integrada.nomenclatura_posicao
    is 'Nomenclatura utilizada para a posição prolog';

-- Adicionar uma coluna de código auxiliar para a empresa
ALTER TABLE EMPRESA
    ADD COD_AUXILIAR TEXT;

-- Function para inserir uma aferição integrada
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_INTEGRADA(F_COD_UNIDADE_PROLOG BIGINT,
                                                            F_CPF_AFERIDOR TEXT,
                                                            F_PLACA_VEICULO TEXT,
                                                            F_COD_AUXILIAR_TIPO_VEICULO_PROLOG TEXT,
                                                            F_KM_VEICULO TEXT,
                                                            F_TEMPO_REALIZACAO BIGINT,
                                                            F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                            F_TIPO_MEDICAO_COLETADA TEXT,
                                                            F_TIPO_PROCESSO_COLETA TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA_PROLOG              BIGINT;
    V_COD_EMPRESA_CLIENTE             TEXT;
    V_COD_TIPO_VEICULO_PROLOG         BIGINT;
    V_COD_UNIDADE_CLIENTE             TEXT;
    V_COD_DIAGRAMA_VEICULO_PROLOG     BIGINT;
    V_COD_AFERICAO_INTEGRADA_INSERIDA BIGINT;
BEGIN
    -- Busca os dados de empresa e unidade para integração
    SELECT U.COD_AUXILIAR,
           E.CODIGO,
           E.COD_AUXILIAR
    FROM UNIDADE U
             JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
    WHERE U.CODIGO = F_COD_UNIDADE_PROLOG
    INTO V_COD_UNIDADE_CLIENTE, V_COD_EMPRESA_PROLOG, V_COD_EMPRESA_CLIENTE;

    -- Busca os dados de tipo de veículo e diagrama para enriquecer o registro de aferição integrada.
    SELECT VT.CODIGO,
           VT.COD_DIAGRAMA
    FROM VEICULO_TIPO VT
    WHERE VT.COD_AUXILIAR = F_COD_AUXILIAR_TIPO_VEICULO_PROLOG
      AND VT.COD_EMPRESA = V_COD_EMPRESA_PROLOG
    INTO V_COD_TIPO_VEICULO_PROLOG, V_COD_DIAGRAMA_VEICULO_PROLOG;

    -- Realiza a inserção do registro de aferição integrada.
    INSERT INTO INTEGRACAO.AFERICAO_INTEGRADA(COD_EMPRESA_PROLOG,
                                              COD_EMPRESA_CLIENTE,
                                              COD_UNIDADE_PROLOG,
                                              COD_UNIDADE_CLIENTE,
                                              CPF_AFERIDOR,
                                              PLACA_VEICULO,
                                              COD_TIPO_VEICULO_PROLOG,
                                              COD_TIPO_VEICULO_CLIENTE,
                                              COD_DIAGRAMA_PROLOG,
                                              KM_VEICULO,
                                              TEMPO_REALIZACAO,
                                              DATA_HORA,
                                              TIPO_MEDICAO_COLETADA,
                                              TIPO_PROCESSO_COLETA)
    VALUES (V_COD_EMPRESA_PROLOG,
            V_COD_EMPRESA_CLIENTE,
            F_COD_UNIDADE_PROLOG,
            V_COD_UNIDADE_CLIENTE,
            F_CPF_AFERIDOR,
            F_PLACA_VEICULO,
            V_COD_TIPO_VEICULO_PROLOG,
            F_COD_AUXILIAR_TIPO_VEICULO_PROLOG,
            V_COD_DIAGRAMA_VEICULO_PROLOG,
            F_KM_VEICULO,
            F_TEMPO_REALIZACAO,
            F_DATA_HORA,
            F_TIPO_MEDICAO_COLETADA,
            F_TIPO_PROCESSO_COLETA)
    RETURNING CODIGO INTO V_COD_AFERICAO_INTEGRADA_INSERIDA;

    IF (V_COD_AFERICAO_INTEGRADA_INSERIDA IS NULL OR V_COD_AFERICAO_INTEGRADA_INSERIDA <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível inserir a aferição nas tabelas de integração, tente novamente';
    END IF;

    RETURN V_COD_AFERICAO_INTEGRADA_INSERIDA;
END
$$;

-- Function para inserir os valores de uma aferição integrada
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_VALORES_INTEGRADA(F_COD_AFERICAO_INTEGRADA BIGINT,
                                                                    F_COD_PNEU TEXT,
                                                                    F_COD_PNEU_CLIENTE TEXT,
                                                                    F_VIDA_ATUAL INTEGER,
                                                                    F_PSI REAL,
                                                                    F_ALTURA_SULCO_INTERNO REAL,
                                                                    F_ALTURA_SULCO_CENTRAL_INTERNO REAL,
                                                                    F_ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                                                                    F_ALTURA_SULCO_EXTERNO REAL,
                                                                    F_POSICAO_PROLOG INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_AFERICAO_VALORES_INSERIDA BIGINT;
    V_NOMENCLATURA_POSICAO          TEXT := (SELECT NOMENCLATURA
                                             FROM PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                                             WHERE PPNE.COD_DIAGRAMA = (SELECT COD_DIAGRAMA_PROLOG
                                                                        FROM INTEGRACAO.AFERICAO_INTEGRADA
                                                                        WHERE CODIGO = F_COD_AFERICAO_INTEGRADA)
                                               AND PPNE.COD_EMPRESA = (SELECT COD_EMPRESA_PROLOG
                                                                       FROM INTEGRACAO.AFERICAO_INTEGRADA
                                                                       WHERE CODIGO = F_COD_AFERICAO_INTEGRADA)
                                               AND PPNE.POSICAO_PROLOG = F_POSICAO_PROLOG);
BEGIN
    -- Realiza a inserção do registro de aferição integrada.
    INSERT INTO INTEGRACAO.AFERICAO_VALORES_INTEGRADA(COD_AFERICAO_INTEGRADA,
                                                      COD_PNEU,
                                                      COD_PNEU_CLIENTE,
                                                      VIDA_MOMENTO_AFERICAO,
                                                      PSI,
                                                      ALTURA_SULCO_INTERNO,
                                                      ALTURA_SULCO_CENTRAL_INTERNO,
                                                      ALTURA_SULCO_CENTRAL_EXTERNO,
                                                      ALTURA_SULCO_EXTERNO,
                                                      POSICAO_PROLOG,
                                                      NOMENCLATURA_POSICAO)
    VALUES (F_COD_AFERICAO_INTEGRADA,
            F_COD_PNEU,
            F_COD_PNEU_CLIENTE,
            F_VIDA_ATUAL,
            F_PSI,
            F_ALTURA_SULCO_INTERNO,
            F_ALTURA_SULCO_CENTRAL_INTERNO,
            F_ALTURA_SULCO_CENTRAL_EXTERNO,
            F_ALTURA_SULCO_EXTERNO,
            F_POSICAO_PROLOG,
            V_NOMENCLATURA_POSICAO)
    RETURNING CODIGO INTO V_COD_AFERICAO_VALORES_INSERIDA;

    IF (V_COD_AFERICAO_VALORES_INSERIDA IS NULL OR V_COD_AFERICAO_VALORES_INSERIDA <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível inserir os valores da aferição na tabela de integração, tente novamente';
    END IF;
END
$$;

-- Function para listar as informações das aferições integradas, que serão utilizadas na tela de aferição avulsa.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_AFERICOES_INTEGRADA(F_COD_UNIDADE BIGINT,
                                                                                       F_COD_PNEUS_CLIENTE TEXT[])
    RETURNS TABLE
            (
                CODIGO_ULTIMA_AFERICAO        BIGINT,
                COD_PNEU                      TEXT,
                COD_PNEU_CLIENTE              TEXT,
                DATA_HORA_ULTIMA_AFERICAO     TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_AFERICAO     TEXT,
                TIPO_MEDICAO_COLETADA         TEXT,
                TIPO_PROCESSO_COLETA          TEXT,
                PLACA_APLICADO_QUANDO_AFERIDO TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA BIGINT := (SELECT COD_EMPRESA
                             FROM PUBLIC.UNIDADE U
                             WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    RETURN QUERY
        WITH ULTIMAS_AFERICOES_PNEU AS (
            SELECT DISTINCT MAX(AVI.CODIGO) OVER (PARTITION BY AVI.COD_PNEU_CLIENTE) AS CODIGO_ULTIMA_AFERICAO,
                            AVI.COD_PNEU                                             AS COD_PNEU,
                            AVI.COD_PNEU_CLIENTE                                     AS COD_PNEU_CLIENTE,
                            MAX(AVI.COD_AFERICAO_INTEGRADA)
                            OVER (PARTITION BY AVI.COD_PNEU_CLIENTE)                 AS COD_AFERICAO_INTEGRADA
            FROM INTEGRACAO.AFERICAO_VALORES_INTEGRADA AVI
            WHERE AVI.COD_PNEU_CLIENTE = ANY (F_COD_PNEUS_CLIENTE)
        )
        SELECT AI.CODIGO                                           AS CODIGO_ULTIMA_AFERICAO,
               UAP.COD_PNEU::TEXT                                  AS COD_PNEU,
               UAP.COD_PNEU_CLIENTE::TEXT                          AS COD_PNEU_CLIENTE,
               AI.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE) AS DATA_HORA_ULTIMA_AFERICAO,
               COALESCE(CD.NOME, 'Nome Indisponível')::TEXT        AS NOME_COLABORADOR_AFERICAO,
               AI.TIPO_MEDICAO_COLETADA                            AS TIPO_MEDICAO_COLETADA,
               AI.TIPO_PROCESSO_COLETA                             AS TIPO_PROCESSO_COLETA,
               AI.PLACA_VEICULO                                    AS PLACA_APLICADO_QUANDO_AFERIDO
        FROM ULTIMAS_AFERICOES_PNEU UAP
                 JOIN INTEGRACAO.AFERICAO_INTEGRADA AI ON UAP.COD_AFERICAO_INTEGRADA = AI.CODIGO
                 LEFT JOIN COLABORADOR_DATA CD ON CD.CPF = AI.CPF_AFERIDOR::BIGINT
        WHERE AI.COD_EMPRESA_PROLOG = V_COD_EMPRESA;
END;
$$;


create or replace function integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar              text,
                cod_unidade               bigint,
                cod_tipo_veiculo          bigint,
                pode_aferir_sulco         boolean,
                pode_aferir_pressao       boolean,
                pode_aferir_sulco_pressao boolean,
                pode_aferir_estepe        boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa bigint := (select u.cod_empresa
                             from public.unidade u
                             where u.codigo = any (f_cod_unidades));
begin
    return query
        with cod_auxiliares as (
            select vt.codigo                                   as cod_tipo_veiculo,
                   regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
            from veiculo_tipo vt
            where vt.cod_empresa = v_cod_empresa
        )

        select ca.cod_auxiliar                 as cod_auxiliar,
               actav.cod_unidade               as cod_unidade,
               actav.cod_tipo_veiculo          as cod_tipo_veiculo,
               actav.pode_aferir_sulco         as pode_aferir_sulco,
               actav.pode_aferir_pressao       as pode_aferir_pressao,
               actav.pode_aferir_sulco_pressao as pode_aferir_sulco_pressao,
               actav.pode_aferir_estepe        as pode_aferir_estepe
        from afericao_configuracao_tipo_afericao_veiculo actav
                 join cod_auxiliares ca on actav.cod_tipo_veiculo = ca.cod_tipo_veiculo
        where actav.cod_unidade = any (f_cod_unidades)
          and ca.cod_auxiliar is not null;
end;
$$;


create or replace function integracao.func_pneu_afericao_get_infos_unidade_afericao(f_cod_unidades bigint[])
    returns table
            (
                cod_auxiliar                  text,
                cod_unidade                   bigint,
                periodo_dias_afericao_sulco   integer,
                periodo_dias_afericao_pressao integer
            )
    language sql
as
$$
select u.cod_auxiliar               as cod_auxiliar,
       pru.cod_unidade              as cod_unidade,
       pru.periodo_afericao_sulco   as periodo_dias_afericao_sulco,
       pru.periodo_afericao_pressao as periodo_dias_afericao_pressao
from pneu_restricao_unidade pru
         join unidade u on pru.cod_unidade = u.codigo
where pru.cod_unidade = any (f_cod_unidades)
  and u.cod_auxiliar is not null;
$$;


create or replace function
    integracao.func_pneu_afericao_get_infos_placas_aferidas(f_cod_empresa bigint,
                                                            f_placas_afericao text[],
                                                            f_data_hora_atual timestamp with time zone)
    returns table
            (
                placa_afericao    text,
                intervalo_pressao integer,
                intervalo_sulco   integer
            )
    language sql
as
$$
with placas as (select unnest(f_placas_afericao) as placa)

select p.placa                                            as placa_afericao,
       coalesce(intervalo_pressao.intervalo, -1)::integer as intervalo_pressao,
       coalesce(intervalo_sulco.intervalo, -1)::integer   as intervalo_sulco
from placas p
         left join integracao.afericao_integrada ai
                   on p.placa = ai.placa_veiculo
                       and ai.cod_empresa_prolog = f_cod_empresa
         left join (select ai.placa_veiculo                                          as placa_intervalo,
                           extract(days from f_data_hora_atual -
                                             max(ai.data_hora at time zone
                                                 tz_unidade(ai.cod_unidade_prolog))) as intervalo
                    from integracao.afericao_integrada ai
                    where ai.tipo_medicao_coletada = 'PRESSAO'
                       or ai.tipo_medicao_coletada = 'SULCO_PRESSAO'
                    group by ai.placa_veiculo) as intervalo_pressao
                   on intervalo_pressao.placa_intervalo = p.placa
         left join (select ai.placa_veiculo                                          as placa_intervalo,
                           extract(days from f_data_hora_atual -
                                             max(ai.data_hora at time zone
                                                 tz_unidade(ai.cod_unidade_prolog))) as intervalo
                    from integracao.afericao_integrada ai
                    where ai.tipo_medicao_coletada = 'SULCO'
                       or ai.tipo_medicao_coletada = 'SULCO_PRESSAO'
                    group by ai.placa_veiculo) as intervalo_sulco
                   on intervalo_sulco.placa_intervalo = p.placa;
$$;

create or replace function
    integracao.func_pneu_afericao_get_config_nova_afericao_placa(f_cod_unidade bigint,
                                                                 f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_inspecao                    real,
                tolerancia_calibragem                  real,
                periodo_afericao_sulco                 integer,
                periodo_afericao_pressao               integer,
                pode_aferir_sulco                      boolean,
                pode_aferir_pressao                    boolean,
                pode_aferir_sulco_pressao              boolean,
                pode_aferir_estepe                     boolean,
                variacao_aceita_sulco_menor_milimetros double precision,
                variacao_aceita_sulco_maior_milimetros double precision,
                bloquear_valores_menores               boolean,
                bloquear_valores_maiores               boolean,
                variacoes_sulco_default_prolog         boolean
            )
    language sql
as
$$
with cod_auxiliares as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_empresa = (select u.cod_empresa from unidade u where u.codigo = f_cod_unidade)
)

select pru.sulco_minimo_descarte                                  as sulco_minimo_descarte,
       pru.sulco_minimo_recapagem                                 as sulco_minimo_recapagem,
       pru.tolerancia_inspecao                                    as tolerancia_inspecao,
       pru.tolerancia_calibragem                                  as tolerancia_calibragem,
       pru.periodo_afericao_sulco                                 as periodo_afericao_sulco,
       pru.periodo_afericao_pressao                               as periodo_afericao_pressao,
       config_pode_aferir.pode_aferir_sulco                       as pode_aferir_sulco,
       config_pode_aferir.pode_aferir_pressao                     as pode_aferir_pressao,
       config_pode_aferir.pode_aferir_sulco_pressao               as pode_aferir_sulco_pressao,
       config_pode_aferir.pode_aferir_estepe                      as pode_aferir_estepe,
       config_alerta_sulco.variacao_aceita_sulco_menor_milimetros as variacao_aceita_sulco_menor_milimetros,
       config_alerta_sulco.variacao_aceita_sulco_maior_milimetros as variacao_aceita_sulco_maior_milimetros,
       config_alerta_sulco.bloquear_valores_menores               as bloquear_valores_menores,
       config_alerta_sulco.bloquear_valores_maiores               as bloquear_valores_maiores,
       config_alerta_sulco.usa_default_prolog                     as variacoes_sulco_default_prolog
from func_afericao_get_config_tipo_afericao_veiculo(f_cod_unidade) as config_pode_aferir
         join view_afericao_configuracao_alerta_sulco as config_alerta_sulco
              on config_pode_aferir.cod_unidade_configuracao = config_alerta_sulco.cod_unidade
         join pneu_restricao_unidade pru
              on pru.cod_unidade = config_pode_aferir.cod_unidade_configuracao
         join cod_auxiliares ca on ca.cod_auxiliar = f_cod_auxiliar_tipo_veiculo
where config_pode_aferir.cod_unidade_configuracao = f_cod_unidade
  and config_pode_aferir.cod_tipo_veiculo = ca.cod_tipo_veiculo
  and pru.cod_unidade = f_cod_unidade;
$$;

create or replace function
    integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(f_cod_empresa bigint,
                                                                 f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                posicao_prolog       integer,
                nomenclatura_cliente text
            )
    language sql
as
$$
with cod_auxiliares as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa
)
select ppne.posicao_prolog as posicao_prolog,
       ppne.nomenclatura   as nomenclatura_cliente
from veiculo_tipo vt
         join pneu_posicao_nomenclatura_empresa ppne on vt.cod_diagrama = ppne.cod_diagrama
         join cod_auxiliares ca on ca.cod_tipo_veiculo = vt.codigo
where ca.cod_auxiliar = f_cod_auxiliar_tipo_veiculo
  and ppne.cod_empresa = f_cod_empresa;
$$;

create or replace function
    integracao.func_pneu_afericao_get_config_nova_afericao_avulsa(f_cod_unidade bigint)
    returns table
            (
                sulco_minimo_descarte                  real,
                sulco_minimo_recapagem                 real,
                tolerancia_inspecao                    real,
                tolerancia_calibragem                  real,
                periodo_afericao_sulco                 integer,
                periodo_afericao_pressao               integer,
                variacao_aceita_sulco_menor_milimetros double precision,
                variacao_aceita_sulco_maior_milimetros double precision,
                bloquear_valores_menores               boolean,
                bloquear_valores_maiores               boolean,
                variacoes_sulco_default_prolog         boolean
            )
    language sql
as
$$
select pru.sulco_minimo_descarte                                  as sulco_minimo_descarte,
       pru.sulco_minimo_recapagem                                 as sulco_minimo_recapagem,
       pru.tolerancia_inspecao                                    as tolerancia_inspecao,
       pru.tolerancia_calibragem                                  as tolerancia_calibragem,
       pru.periodo_afericao_sulco                                 as periodo_afericao_sulco,
       pru.periodo_afericao_pressao                               as periodo_afericao_pressao,
       config_alerta_sulco.variacao_aceita_sulco_menor_milimetros as variacao_aceita_sulco_menor_milimetros,
       config_alerta_sulco.variacao_aceita_sulco_maior_milimetros as variacao_aceita_sulco_maior_milimetros,
       config_alerta_sulco.bloquear_valores_menores               as bloquear_valores_menores,
       config_alerta_sulco.bloquear_valores_maiores               as bloquear_valores_maiores,
       config_alerta_sulco.usa_default_prolog                     as variacoes_sulco_default_prolog
from view_afericao_configuracao_alerta_sulco config_alerta_sulco
         join pneu_restricao_unidade pru
              on pru.cod_unidade = config_alerta_sulco.cod_unidade
where config_alerta_sulco.cod_unidade = f_cod_unidade;
$$;