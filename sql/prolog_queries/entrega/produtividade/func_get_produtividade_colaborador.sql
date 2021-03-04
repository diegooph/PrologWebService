-- Sobre:
-- Function que traz a produtividade de um colaborador.
--
-- Pré-requisitos:
-- view VIEW_PRODUTIVIDADE_EXTRATO criada.
-- function FUNC_GET_DATA_INICIO_PRODUTIVIDADE criada.
-- function FUNC_GET_DATA_FIM_PRODUTIVIDADE criada.
--
-- Histórico:
-- 2020-09-09 -> Corrige tipagem (thaisksf - PL-3131).
CREATE OR REPLACE FUNCTION FUNC_GET_PRODUTIVIDADE_COLABORADOR(F_MES INTEGER, F_ANO INTEGER, F_CPF BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE                INTEGER,
                MATRICULA_AMBEV            INTEGER,
                DATA                       DATE,
                CPF                        BIGINT,
                NOME_COLABORADOR           CHARACTER VARYING,
                DATA_NASCIMENTO            DATE,
                FUNCAO                     CHARACTER VARYING,
                COD_FUNCAO                 BIGINT,
                NOME_EQUIPE                CHARACTER VARYING,
                FATOR                      REAL,
                CARGAATUAL                 CHARACTER VARYING,
                ENTREGA                    CHARACTER VARYING,
                MAPA                       INTEGER,
                PLACA                      CHARACTER VARYING,
                CXCARREG                   REAL,
                CXENTREG                   REAL,
                QTHLCARREGADOS             REAL,
                QTHLENTREGUES              REAL,
                QTNFCARREGADAS             INTEGER,
                QTNFENTREGUES              INTEGER,
                ENTREGASCOMPLETAS          INTEGER,
                ENTREGASNAOREALIZADAS      INTEGER,
                ENTREGASPARCIAIS           INTEGER,
                KMPREVISTOROAD             REAL,
                KMSAI                      INTEGER,
                KMENTR                     INTEGER,
                TEMPOPREVISTOROAD          INTEGER,
                HRSAI                      TIMESTAMP WITHOUT TIME ZONE,
                HRENTR                     TIMESTAMP WITHOUT TIME ZONE,
                TEMPO_ROTA                 BIGINT,
                TEMPOINTERNO               INTEGER,
                HRMATINAL                  TIME WITHOUT TIME ZONE,
                APONTAMENTOS_OK            BIGINT,
                TOTAL_TRACKING             BIGINT,
                TEMPO_LARGADA              INTEGER,
                META_TRACKING              REAL,
                META_TEMPO_ROTA_MAPAS      REAL,
                META_CAIXA_VIAGEM          REAL,
                META_DEV_HL                REAL,
                META_DEV_NF                REAL,
                META_DEV_PDV               REAL,
                META_DISPERSAO_KM          REAL,
                META_DISPERSAO_TEMPO       REAL,
                META_JORNADA_LIQUIDA_MAPAS REAL,
                META_RAIO_TRACKING         REAL,
                META_TEMPO_INTERNO_MAPAS   REAL,
                META_TEMPO_LARGADA_MAPAS   REAL,
                META_TEMPO_ROTA_HORAS      INTEGER,
                META_TEMPO_INTERNO_HORAS   INTEGER,
                META_TEMPO_LARGADA_HORAS   INTEGER,
                META_JORNADA_LIQUIDA_HORAS INTEGER,
                VALOR_ROTA                 REAL,
                VALOR_RECARGA              REAL,
                VALOR_DIFERENCA_ELD        DOUBLE PRECISION,
                VALOR_AS                   REAL,
                VALOR                      DOUBLE PRECISION
            )
    LANGUAGE SQL
AS
$$
SELECT *
FROM VIEW_PRODUTIVIDADE_EXTRATO
WHERE DATA BETWEEN FUNC_GET_DATA_INICIO_PRODUTIVIDADE(F_ANO, F_MES, F_CPF, NULL) AND
    FUNC_GET_DATA_FIM_PRODUTIVIDADE(F_ANO, F_MES, F_CPF, NULL)
  AND CPF = F_CPF
ORDER BY DATA ASC
$$;