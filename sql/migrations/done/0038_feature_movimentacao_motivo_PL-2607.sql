create table movimentacao_motivo_movimento
(
    codigo                           bigserial                not null
        constraint pk_movimentacao_motivo
            primary key,
    cod_empresa                      bigint                   not null
        constraint fk_cod_empresa
            references empresa (codigo),
    motivo                           citext                   not null,
    ativo                            boolean                  not null,
    cod_auxiliar                     text,
    data_hora_ultima_alteracao       timestamp with time zone not null,
    cod_colaborador_ultima_alteracao bigint                   not null
        constraint fk_cod_colaborador
            references colaborador_data (codigo),
    constraint unico_motivo_por_empresa unique (codigo, cod_empresa)
);

create unique index movimentacao_motivo_motivo_uindex
    on movimentacao_motivo_movimento (motivo) where ativo = true;


CREATE TABLE movimentacao_motivo_movimento_historico
(
    CODIGO                             BIGSERIAL                NOT NULL
        constraint pk_movimentacao_motivo_historico
            primary key,
    COD_MOTIVO                         BIGINT                   NOT NULL
        constraint fk_movimentacao_motivo_codigo
            references movimentacao_motivo_movimento,
    DESCRICAO_MOTIVO                   citext                   not null,
    ATIVO_MOTIVO                       BOOLEAN                  not null,
    COD_AUXILIAR                       TEXT,
    data_hora_alteracao_anterior       timestamp with time zone not null,
    cod_colaborador_alteracao_anterior bigint                   not null
        constraint fk_cod_colaborador
            references colaborador_data (codigo)
);


CREATE TYPE MOVIMENTACAO_ORIGEM_DESTINO_TYPE AS ENUM (
    'EM_USO',
    'ESTOQUE',
    'DESCARTE',
    'ANALISE'
    );

create table movimentacao_motivo_movimento_transicao
(
    codigo                           bigserial                        not null
        constraint pk_movimentacao_motivo_movimento_transicao
            primary key,
    cod_empresa                      bigint                           not null,
    cod_unidade                      bigint                           not null,
    cod_motivo                       bigint                           not null,
    origem                           movimentacao_origem_destino_type not null,
    destino                          movimentacao_origem_destino_type not null,
    obrigatorio                      boolean default true             not null,
    data_hora_ultima_alteracao       timestamp with time zone,
    cod_colaborador_ultima_alteracao bigint
        constraint fk_cod_colaborador
            references colaborador_data (codigo),
    constraint fk_movimentacao_motivo_origem_destino_codigo_motivo foreign key (cod_motivo, cod_empresa)
        references movimentacao_motivo_movimento (codigo, cod_empresa),
    constraint fk_movimentacao_motivo_origem_destino_unidade foreign key (cod_unidade, cod_empresa)
        references unidade (codigo, cod_empresa),
    constraint unique_movimentacao_origem_destino_origem_destino unique (cod_motivo, cod_unidade, origem, destino),
    constraint check_relacao_origem_destino_validas
        CHECK (ORIGEM = 'ESTOQUE' AND DESTINO = 'EM_USO'
            OR ORIGEM = 'ESTOQUE' AND DESTINO = 'ANALISE'
            OR ORIGEM = 'EM_USO' AND DESTINO = 'ESTOQUE'
            OR ORIGEM = 'EM_USO' AND DESTINO = 'ANALISE'
            OR ORIGEM = 'EM_USO' AND DESTINO = 'EM_USO')

);

create table movimentacao_motivo_movimento_resposta
(
    codigo               bigserial not null
        constraint pk_movimentacao_motivo_movimento
            primary key,
    cod_movimentacao     bigint    not null
        constraint fk_movimentacao_motivo_movimento_codigo_movimento
            references movimentacao,
    cod_motivo_movimento bigint    not null
        constraint fk_movimentacao_motivo_movimento_motivo
            references movimentacao_motivo_movimento
);

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_ATUALIZA(F_COD_MOTIVO BIGINT,
                                                          F_DESCRICAO_MOTIVO CITEXT,
                                                          F_ATIVO_MOTIVO BOOLEAN,
                                                          F_COD_AUXILIAR_MOTIVO TEXT,
                                                          F_DATA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE,
                                                          F_COD_COLABORADOR_ALTERACAO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_DESCRICAO_ANTERIOR                          CITEXT;
    V_ATIVO_ANTERIOR                              BOOLEAN;
    V_COD_AUXILIAR_ANTERIOR                       TEXT;
    V_ULTIMA_ATUALIZACAO_ANTERIOR                 TIMESTAMP WITH TIME ZONE;
    V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR BIGINT;
BEGIN

    SELECT MOTIVO,
           ATIVO,
           COD_AUXILIAR,
           DATA_HORA_ULTIMA_ALTERACAO,
           COD_COLABORADOR_ULTIMA_ALTERACAO
    INTO V_DESCRICAO_ANTERIOR,
        V_ATIVO_ANTERIOR,
        V_COD_AUXILIAR_ANTERIOR,
        V_ULTIMA_ATUALIZACAO_ANTERIOR,
        V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR
    FROM MOVIMENTACAO_MOTIVO_MOVIMENTO
    WHERE CODIGO = F_COD_MOTIVO;

    IF TRIM(V_DESCRICAO_ANTERIOR) != (F_DESCRICAO_MOTIVO)
        OR V_ATIVO_ANTERIOR != F_ATIVO_MOTIVO
        OR V_COD_AUXILIAR_ANTERIOR IS NULL AND F_COD_AUXILIAR_MOTIVO IS NOT NULL
        OR V_COD_AUXILIAR_ANTERIOR IS NOT NULL AND F_COD_AUXILIAR_MOTIVO IS NULL
        OR TRIM(V_COD_AUXILIAR_ANTERIOR) != TRIM(F_COD_AUXILIAR_MOTIVO) THEN

        UPDATE MOVIMENTACAO_MOTIVO_MOVIMENTO
        SET MOTIVO                           = F_DESCRICAO_MOTIVO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_ULTIMA_ALTERACAO,
            COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ALTERACAO,
            ATIVO                            = F_ATIVO_MOTIVO,
            COD_AUXILIAR                     = F_COD_AUXILIAR_MOTIVO
        WHERE CODIGO = F_COD_MOTIVO;

        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR('Erro ao atualizar os dados do motivo, tente novamente.');
        END IF;

        INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_HISTORICO(COD_MOTIVO,
                                                            DESCRICAO_MOTIVO,
                                                            ATIVO_MOTIVO,
                                                            COD_AUXILIAR,
                                                            DATA_HORA_ALTERACAO_ANTERIOR,
                                                            COD_COLABORADOR_ALTERACAO_ANTERIOR)
        VALUES (F_COD_MOTIVO,
                V_DESCRICAO_ANTERIOR,
                V_ATIVO_ANTERIOR,
                V_COD_AUXILIAR_ANTERIOR,
                V_ULTIMA_ATUALIZACAO_ANTERIOR,
                V_COD_COLABORADOR_ULTIMA_ATUALIZACAO_ANTERIOR);
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_GET_BY_TRANSICAO(F_ORIGEM MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_DESTINO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO    BIGINT,
                DESCRICAO_MOTIVO TEXT,
                OBRIGATORIO      BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO       AS CODIGO_MOTIVO,
       MMM.MOTIVO       AS DESCRICAO_MOTIVO,
       MMMT.OBRIGATORIO AS OBRIGATORIO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT ON MMMT.COD_MOTIVO = MMM.CODIGO
WHERE MMMT.ORIGEM = F_ORIGEM
  AND MMMT.DESTINO = F_DESTINO
  AND MMMT.COD_UNIDADE = F_COD_UNIDADE
  AND MMM.ATIVO = TRUE
ORDER BY MMM.MOTIVO;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_GET_TRANSICAO_BY_UNIDADE(F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                CODIGO_UNIDADE BIGINT,
                ORIGEM         MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO        MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO    BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT DISTINCT MMMT.COD_UNIDADE AS CODIGO_UNIDADE,
                MMMT.ORIGEM      AS ORIGEM,
                MMMT.DESTINO     AS DESTINO,
                MMMT.OBRIGATORIO AS OBRIGATORIO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
                    ON MMM.CODIGO = MMMT.COD_MOTIVO
WHERE MMMT.COD_UNIDADE = F_COD_UNIDADE
  AND MMM.ATIVO = TRUE
ORDER BY MMMT.ORIGEM, MMMT.DESTINO;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_VISUALIZACAO(F_COD_MOTIVO_TRANSICAO BIGINT,
                                                                        F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO_TRANSICAO           BIGINT,
                NOME_EMPRESA                      TEXT,
                DESCRICAO_MOTIVO                  TEXT,
                ORIGEM                            MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO                           MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO                       BOOLEAN,
                DATA_HORA_ULTIMA_ALTERACAO        TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MMMT.CODIGO                                              AS CODIGO_MOTIVO_TRANSICAO,
       E.NOME                                                   AS NOME_EMPRESA,
       MMM.MOTIVO                                               AS DESCRICAO_MOTIVO,
       MMMT.ORIGEM                                              AS ORIGEM,
       MMMT.DESTINO                                             AS DESTINO,
       MMMT.OBRIGATORIO                                         AS OBRIGATORIO,
       MMMT.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO,
       CUA.NOME                                                 AS NOME_COLABORADOR_ULTIMA_ALTERACAO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT
         INNER JOIN EMPRESA E ON E.CODIGO = MMMT.COD_EMPRESA
         INNER JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMT.COD_MOTIVO
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMMT.CODIGO = F_COD_MOTIVO_TRANSICAO;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_INSERE(F_COD_EMPRESA_MOTIVO BIGINT,
                                                        F_DESCRICAO_MOTIVO TEXT,
                                                        F_ATIVO_MOTIVO BOOLEAN,
                                                        F_COD_AUXILIAR_MOTIVO TEXT,
                                                        F_DATA_HORA_INSERCAO_MOTIVO TIMESTAMP WITH TIME ZONE,
                                                        F_COD_COLABORADOR_AUTENTICADO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MOTIVO BIGINT;
BEGIN
    INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO (COD_EMPRESA,
                                               MOTIVO,
                                               ATIVO,
                                               COD_AUXILIAR,
                                               DATA_HORA_ULTIMA_ALTERACAO,
                                               COD_COLABORADOR_ULTIMA_ALTERACAO)
    VALUES (F_COD_EMPRESA_MOTIVO,
            F_DESCRICAO_MOTIVO,
            F_ATIVO_MOTIVO,
            F_COD_AUXILIAR_MOTIVO,
            F_DATA_HORA_INSERCAO_MOTIVO,
            F_COD_COLABORADOR_AUTENTICADO)
    RETURNING CODIGO INTO V_COD_MOTIVO;

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao inserir o motivo, tente novamente.');
    END IF;

    RETURN V_COD_MOTIVO;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                          F_APENAS_ATIVOS BOOLEAN,
                                                          F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO                     BIGINT,
                NOME_EMPRESA                      TEXT,
                DESCRICAO_MOTIVO                  TEXT,
                CODIGO_AUXILIAR                   TEXT,
                DATA_HORA_ULTIMA_ALTERACAO_MOTIVO TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT,
                ATIVO_MOTIVO                      BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
       E.NOME                                                  AS NOME_EMPRESA,
       MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
       MMM.COD_AUXILIAR                                      AS CODIGO_AUXILIAR,
       MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO_MOTIVO,
       CUA.NOME                                                AS NOME_COLABORADOR_ULTIMA_ALTERACAO,
       MMM.ATIVO                                               AS ATIVO_MOTIVO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN EMPRESA E ON E.CODIGO = MMM.COD_EMPRESA
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMM.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_APENAS_ATIVOS IS FALSE, TRUE, MMM.ATIVO = TRUE)
ORDER BY MMM.CODIGO;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_INSERE(F_COD_MOTIVO BIGINT,
                                                                  F_COD_EMPRESA BIGINT,
                                                                  F_COD_UNIDADE BIGINT,
                                                                  F_ORIGEM MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_DESTINO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                                                                  F_OBRIGATORIO BOOLEAN,
                                                                  F_DATA_HORA_INSERCAO TIMESTAMP WITH TIME ZONE,
                                                                  F_COD_COLABORADOR_INSERCAO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MOTIVO_ORIGEM_DESTINO BIGINT;
    V_OBRIGATORIO               BOOLEAN;
BEGIN
    SELECT MMMT.CODIGO, MMMT.OBRIGATORIO
    INTO V_COD_MOTIVO_ORIGEM_DESTINO, V_OBRIGATORIO
    FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT
    WHERE MMMT.DESTINO = F_DESTINO
      AND MMMT.ORIGEM = F_ORIGEM
      AND MMMT.COD_UNIDADE = F_COD_UNIDADE
      AND MMMT.COD_MOTIVO = F_COD_MOTIVO;

    IF V_COD_MOTIVO_ORIGEM_DESTINO IS NULL
    THEN
        INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO (COD_MOTIVO,
                                                             COD_EMPRESA,
                                                             COD_UNIDADE,
                                                             ORIGEM,
                                                             DESTINO,
                                                             OBRIGATORIO,
                                                             DATA_HORA_ULTIMA_ALTERACAO,
                                                             COD_COLABORADOR_ULTIMA_ALTERACAO)
        VALUES (F_COD_MOTIVO,
                F_COD_EMPRESA,
                F_COD_UNIDADE,
                F_ORIGEM,
                F_DESTINO,
                F_OBRIGATORIO,
                F_DATA_HORA_INSERCAO,
                F_COD_COLABORADOR_INSERCAO)
        RETURNING CODIGO INTO V_COD_MOTIVO_ORIGEM_DESTINO;

        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR('Erro ao salvar informações, tente novamente.');
        END IF;
    ELSE
        IF V_OBRIGATORIO != F_OBRIGATORIO
        THEN
            DELETE FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO WHERE CODIGO = V_COD_MOTIVO_ORIGEM_DESTINO;
            INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO (COD_MOTIVO,
                                                                 COD_EMPRESA,
                                                                 COD_UNIDADE,
                                                                 ORIGEM,
                                                                 DESTINO,
                                                                 OBRIGATORIO,
                                                                 DATA_HORA_ULTIMA_ALTERACAO,
                                                                 COD_COLABORADOR_ULTIMA_ALTERACAO)
            VALUES (F_COD_MOTIVO,
                    F_COD_EMPRESA,
                    F_COD_UNIDADE,
                    F_ORIGEM,
                    F_DESTINO,
                    F_OBRIGATORIO,
                    F_DATA_HORA_INSERCAO,
                    F_COD_COLABORADOR_INSERCAO)
            RETURNING CODIGO INTO V_COD_MOTIVO_ORIGEM_DESTINO;

            IF NOT FOUND
            THEN
                PERFORM THROW_GENERIC_ERROR('Erro ao salvar informações, tente novamente.');
            END IF;
        END IF;
    END IF;

    RETURN V_COD_MOTIVO_ORIGEM_DESTINO;
END
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_TRANSICAO_LISTAGEM(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                CODIGO_UNIDADE    BIGINT,
                NOME_UNIDADE      VARCHAR(40),
                CODIGO_MOTIVO     BIGINT,
                DESCRICAO_MOTIVO  CITEXT,
                ORIGEM_MOVIMENTO  MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                DESTINO_MOVIMENTO MOVIMENTACAO_ORIGEM_DESTINO_TYPE,
                OBRIGATORIO       BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN

    RETURN QUERY
        SELECT U.CODIGO AS CODIGO_UNIDADE,
               U.NOME           AS NOME_UNIDADE,
               MMMT.COD_MOTIVO  AS CODIGO_MOTIVO,
               MMM.MOTIVO       AS DESCRICAO_MOTIVO,
               MMMT.ORIGEM      AS ORIGEM_MOVIMENTO,
               MMMT.DESTINO     AS DESTINO_MOVIMENTO,
               MMMT.OBRIGATORIO AS OBRIGATORIO
        FROM UNIDADE U
                 left JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_TRANSICAO MMMT ON MMMT.cod_unidade = U.codigo
                 left JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMT.COD_MOTIVO
        WHERE U.CODIGO IN (SELECT DISTINCT FCGUA.CODIGO_UNIDADE
                                FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) FCGUA)
        ORDER BY MMMT.COD_UNIDADE, MMMT.ORIGEM, MMMT.DESTINO, MMM.codigo;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_VISUALIZACAO(F_COD_MOTIVO BIGINT,
                                                              F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO                     BIGINT,
                DESCRICAO_MOTIVO                  TEXT,
                ATIVO_MOTIVO                      BOOLEAN,
                CODIGO_AUXILIAR                   TEXT,
                DATA_HORA_ULTIMA_ALTERACAO_MOTIVO TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
       MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
       MMM.ATIVO                                               AS ATIVO_MOTIVO,
       MMM.COD_AUXILIAR                                        AS CODIGO_AUXILIAR,
       MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO_MOTIVO,
       CUA.NOME                                                AS NOME_COLABORADOR_ULTIMA_ALTERACAO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN EMPRESA E ON E.CODIGO = MMM.COD_EMPRESA
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMM.CODIGO = F_COD_MOTIVO;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_INSERE_MOTIVO_MOVIMENTO_RESPOSTA(F_COD_MOVIMENTO BIGINT,
                                                                              F_COD_MOTIVO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO MOVIMENTACAO_MOTIVO_MOVIMENTO_RESPOSTA (COD_MOVIMENTACAO,
                                                        COD_MOTIVO_MOVIMENTO)
    VALUES (F_COD_MOVIMENTO,
            F_COD_MOTIVO);

    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao salvar motivo de movimento, tente novamente.');
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_HISTORICO_LISTAGEM(F_COD_MOTIVO BIGINT,
                                                                    F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO              BIGINT,
                DESCRICAO_MOTIVO           CITEXT,
                ATIVO_MOTIVO               BOOLEAN,
                CODIGO_AUXILIAR            TEXT,
                DATA_HORA_ALTERACAO        TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
(SELECT MMMH.COD_MOTIVO                                            AS CODIGO_MOTIVO,
        MMMH.DESCRICAO_MOTIVO                                      AS DESCRICAO_MOTIVO,
        MMMH.ATIVO_MOTIVO                                          AS ATIVO_MOTIVO,
        MMMH.COD_AUXILIAR                                          AS CODIGO_AUXILIAR,
        MMMH.DATA_HORA_ALTERACAO_ANTERIOR AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ALTERACAO,
        CUA.NOME                                                   AS NOME_COLABORADOR_ALTERACAO
 FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_HISTORICO MMMH
          INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMMH.COD_COLABORADOR_ALTERACAO_ANTERIOR
 WHERE MMMH.COD_MOTIVO = F_COD_MOTIVO
 UNION
 SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
        MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
        MMM.ATIVO                                               AS ATIVO_MOTIVO,
        MMM.COD_AUXILIAR                                        AS CODIGO_AUXILIAR,
        MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ALTERACAO,
        CUA.NOME                                                AS NOME_COLABORADOR_ALTERACAO
 FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
          INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
 WHERE MMM.CODIGO = F_COD_MOTIVO)
    ORDER BY DATA_HORA_ALTERACAO DESC;
$$;

UPDATE FUNCAO_PROLOG_V11
SET descricao = 'Permite ao usuário editar o cadastro de motivos de movimentação.',
    funcao    = 'Edição dos motivos de movimentação de pneus.'
where codigo = 124;

UPDATE FUNCAO_PROLOG_V11
SET descricao = 'Permite ao usuário cadastrar novos motivos de movimentação de pneus.',
    funcao    = 'Cadastrar novos motivos para a movimentação de pneus.'
where codigo = 123;


--------- PL-2609 - RELATORIOS

DROP FUNCTION IF EXISTS FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);

CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                    F_DATA_INICIAL DATE,
                                                                    F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                TEXT,
                "DATA E HORA"            TEXT,
                "CPF DO RESPONSÁVEL"     TEXT,
                "NOME"                   TEXT,
                "PNEU"                   TEXT,
                "MARCA"                  TEXT,
                "MODELO"                 TEXT,
                "BANDA APLICADA"         TEXT,
                "MEDIDAS"                TEXT,
                "SULCO INTERNO"          TEXT,
                "SULCO CENTRAL INTERNO"  TEXT,
                "SULCO CENTRAL EXTERNO"  TEXT,
                "SULCO EXTERNO"          TEXT,
                "MENOR SULCO"            TEXT,
                "PRESSÃO ATUAL (PSI)"    TEXT,
                "VIDA ATUAL"             TEXT,
                "ORIGEM"                 TEXT,
                "PLACA DE ORIGEM"        TEXT,
                "POSIÇÃO DE ORIGEM"      TEXT,
                "DESTINO"                TEXT,
                "PLACA DE DESTINO"       TEXT,
                "POSIÇÃO DE DESTINO"     TEXT,
                "MOTIVO DA MOVIMENTAÇÃO" TEXT,
                "KM MOVIMENTAÇÃO"        TEXT,
                "RECAPADORA DESTINO"     TEXT,
                "CÓDIGO COLETA"          TEXT,
                "OBS. MOVIMENTAÇÃO"      TEXT,
                "OBS. GERAL"             TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME,
       TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
       LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
       C.NOME,
       P.CODIGO_CLIENTE                                                                                  AS PNEU,
       MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
       MP.NOME                                                                                           AS NOME_MODELO_PNEU,
       F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado',
            MARB.NOME || ' - ' || MODB.NOME)                                                             AS BANDA_APLICADA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                                    AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                            AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                            AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                                    AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    P.ALTURA_SULCO_CENTRAL_INTERNO,
                                    P.ALTURA_SULCO_INTERNO))                                             AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
       PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
       O.TIPO_ORIGEM                                                                                     AS ORIGEM,
       COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
       COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
       D.TIPO_DESTINO                                                                                    AS DESTINO,
       COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
       COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
       COALESCE(MMM.MOTIVO, '-')                                                                         AS MOTIVO_DA_MOVIMENTACAO,
       COALESCE(VORIGEM.KM, VDESTINO.KM) :: TEXT                                                         AS KM_COLETADO_MOVIMENTACAO,
       COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
       COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
       COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
       COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
FROM MOVIMENTACAO_PROCESSO MOVP
         JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
         JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
         JOIN PNEU P ON P.CODIGO = M.COD_PNEU
         JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
         JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL

    -- Terá recapadora apenas se foi movido para análise.
         LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
         LEFT JOIN VEICULO VORIGEM
                   ON O.PLACA = VORIGEM.PLACA
         LEFT JOIN VEICULO_TIPO VTORIGEM ON E.CODIGO = VTORIGEM.COD_EMPRESA AND VTORIGEM.CODIGO = VORIGEM.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDORIGEM ON VTORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_ORIGEM
                   ON NOMENCLATURA_ORIGEM.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_ORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
                       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

    -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
         LEFT JOIN VEICULO VDESTINO
                   ON D.PLACA = VDESTINO.PLACA
         LEFT JOIN VEICULO_TIPO VTDESTINO ON E.CODIGO = VTDESTINO.COD_EMPRESA AND VTDESTINO.CODIGO = VDESTINO.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDDESTINO ON VTDESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_DESTINO
                   ON NOMENCLATURA_DESTINO.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_DESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
                       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_RESPOSTA MMMR ON MMMR.COD_MOVIMENTACAO = M.CODIGO
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMR.COD_MOTIVO_MOVIMENTO
WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC;
$$;