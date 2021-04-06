
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT,
                                                                       F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                IDENTIFICADOR_FROTA   TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       V.PLACA                               AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

drop function func_afericao_get_afericoes_avulsas_paginada(f_cod_unidade bigint,
                                                           f_data_inicial date,
                                                           f_data_final date,
                                                           f_limit bigint,
                                                           f_offset bigint,
                                                           f_tz_unidade text);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT,
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_LIMIT BIGINT,
                                                                        F_OFFSET BIGINT,
                                                                        F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND A.TIPO_PROCESSO_COLETA = 'PNEU_AVULSO'
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
                                                                F_COD_AFERICAO BIGINT,
                                                                F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                COD_AFERICAO                 BIGINT,
                COD_UNIDADE                  BIGINT,
                DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO                TEXT,
                IDENTIFICADOR_FROTA          TEXT,
                KM_VEICULO                   BIGINT,
                TEMPO_REALIZACAO             BIGINT,
                TIPO_PROCESSO_COLETA         TEXT,
                TIPO_MEDICAO_COLETADA        TEXT,
                FORMA_COLETA_DADOS           TEXT,
                CPF                          TEXT,
                NOME                         TEXT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                ALTURA_SULCO_INTERNO         REAL,
                PRESSAO_PNEU                 INTEGER,
                POSICAO_PNEU                 INTEGER,
                VIDA_PNEU_MOMENTO_AFERICAO   INTEGER,
                VIDAS_TOTAL_PNEU             INTEGER,
                CODIGO_PNEU                  BIGINT,
                CODIGO_PNEU_CLIENTE          TEXT,
                PRESSAO_RECOMENDADA          REAL
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       V.PLACA                               AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                          AS KM_VEICULO,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       AV.ALTURA_SULCO_CENTRAL_INTERNO       AS ALTURA_SULCO_CENTRAL_INTERNO,
       AV.ALTURA_SULCO_CENTRAL_EXTERNO       AS ALTURA_SULCO_CENTRAL_EXTERNO,
       AV.ALTURA_SULCO_EXTERNO               AS ALTURA_SULCO_EXTERNO,
       AV.ALTURA_SULCO_INTERNO               AS ALTURA_SULCO_INTERNO,
       AV.PSI::INT                           AS PRESSAO_PNEU,
       AV.POSICAO                            AS POSICAO_PNEU,
       AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU_MOMENTO_AFERICAO,
       P.VIDA_TOTAL                          AS VIDAS_TOTAL_PNEU,
       P.CODIGO                              AS CODIGO_PNEU,
       P.CODIGO_CLIENTE::TEXT                AS CODIGO_PNEU_CLIENTE,
       P.PRESSAO_RECOMENDADA                 AS PRESSAO_RECOMENDADA
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV
              ON A.CODIGO = AV.COD_AFERICAO
         JOIN VEICULO V
              ON V.CODIGO = A.COD_VEICULO
         JOIN PNEU_ORDEM PO
              ON AV.POSICAO = PO.POSICAO_PROLOG
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         JOIN MODELO_PNEU MO
              ON MO.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP
              ON MP.CODIGO = MO.COD_MARCA
         JOIN COLABORADOR C
              ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO
  AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;

drop function func_afericao_get_cronograma_afericoes_placas(f_cod_unidades bigint[],
                                                            f_data_hora_atual timestamp with time zone);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                            TEXT,
                COD_VEICULO                      BIGINT,
                IDENTIFICADOR_FROTA              TEXT,
                COD_UNIDADE_PLACA                BIGINT,
                NOME_MODELO                      TEXT,
                INTERVALO_PRESSAO                INTEGER,
                INTERVALO_SULCO                  INTEGER,
                PERIODO_AFERICAO_SULCO           INTEGER,
                PERIODO_AFERICAO_PRESSAO         INTEGER,
                PNEUS_APLICADOS                  INTEGER,
                STATUS_ATIVO_TIPO_VEICULO        BOOLEAN,
                FORMA_COLETA_DADOS_SULCO         TEXT,
                FORMA_COLETA_DADOS_PRESSAO       TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                PODE_AFERIR_ESTEPE               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                              AS PLACA,
               COALESCE(V.CODIGO, -1)::BIGINT                               AS COD_VEICULO,
               V.IDENTIFICADOR_FROTA ::TEXT                                 AS IDENTIFICADOR_FROTA,
               V.COD_UNIDADE :: BIGINT                                      AS COD_UNIDADE_PLACA,
               MV.NOME :: TEXT                                              AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER         AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER           AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                   AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                 AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                   AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE
        FROM VEICULO V
                 JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.
                                                        COD_UNIDADE = V.COD_UNIDADE
                 JOIN VEICULO_TIPO VT ON VT.
                                             CODIGO = V.COD_TIPO
                 JOIN MODELO_VEICULO MV ON MV.
                                               CODIGO = V.COD_MODELO
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.
                                  COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN (SELECT A.COD_VEICULO AS COD_VEICULO_INTERVALO, EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.COD_VEICULO) AS INTERVALO_PRESSAO
                 ON INTERVALO_PRESSAO.COD_VEICULO_INTERVALO = V.CODIGO
                 LEFT JOIN (SELECT A.COD_VEICULO AS COD_VEICULO_INTERVALO, EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.COD_VEICULO) AS INTERVALO_SULCO
                 ON INTERVALO_SULCO.COD_VEICULO_INTERVALO = V.CODIGO
                 LEFT JOIN (SELECT VP.COD_VEICULO           AS COD_VEICULOS,
                                   COUNT(VP.COD_PNEU) AS TOTAL
                            FROM VEICULO_PNEU VP
                            WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES)
                            GROUP BY VP.COD_VEICULO) AS NUMERO_PNEUS ON
            COD_VEICULOS = V.CODIGO
        WHERE V.STATUS_ATIVO = TRUE
          AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY MV.NOME, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC, PNEUS_APLICADOS DESC;
END;
$$;

--------------------------------------------------------- INTEGRAÇÃO ---------------------------------------------------

create or replace function
    integracao.func_integracao_busca_afericoes_empresa(f_token_integracao text,
                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                       bigint,
                cod_unidade_afericao               bigint,
                cpf_colaborador                    text,
                placa_veiculo_aferido              varchar(255),
                cod_pneu_aferido                   bigint,
                numero_fogo                        varchar(255),
                altura_sulco_interno               numeric,
                altura_sulco_central_interno       numeric,
                altura_sulco_central_externo       numeric,
                altura_sulco_externo               numeric,
                pressao                            numeric,
                km_veiculo_momento_afericao        bigint,
                tempo_realizacao_afericao_em_milis bigint,
                vida_momento_afericao              integer,
                posicao_pneu_momento_afericao      integer,
                data_hora_afericao                 timestamp without time zone,
                tipo_medicao_coletada              varchar(13),
                tipo_processo_coleta               varchar(11)
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor :: text, 11, '0')              as cpf_colaborador,
       v.placa                                            as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente                                   as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 1)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 1) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 1) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 1)         as altura_sulco_externo,
       trunc(av.psi::numeric, 1)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       a.tempo_realizacao                                 as tempo_realizacao_afericao_em_milis,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada                            as tipo_medicao_coletada,
       a.tipo_processo_coleta                             as tipo_processo_coleta
from afericao a
         left join veiculo v on v.codigo = a.cod_veiculo
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.codigo > f_cod_ultima_afericao_sincronizada
order by a.codigo;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_codigo(f_token_integracao text,
                                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                identificador_frota                   text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       v.identificador_frota                              as identificador_frota,
       v.placa                                            as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
         join veiculo v on v.codigo = a.cod_veiculo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.codigo > f_cod_ultima_afericao_sincronizada
order by a.codigo;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_data_hora(f_token_integracao text,
                                                                          f_data_hora timestamp without time zone)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                identificador_frota                   text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       v.placa                                            as placa_veiculo_aferido,
       v.identificador_frota                              as identificador_frota,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
         join veiculo v on a.cod_veiculo = v.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.data_hora > f_data_hora
order by a.data_hora;
$$;