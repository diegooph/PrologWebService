BEGIN TRANSACTION;
--######################################################################################################################
--######################################################################################################################
--######################################## FUNC PARA CRIAR NOVA DIMENSÃO ###############################################
--######################################################################################################################
--######################################################################################################################
-- PL-2312
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Caso a dimensão que desejamos inserir exista no banco, ela não será cadastrada.
-- Caso a dimensão seja realemtne nova, ela será adicionada na base de dados.
--
-- Précondições:
-- Dimensão não deve existir no banco.
-- Valores maiores que 0.
--
-- Histórico:
-- 2019-10-15 -> Function criada (Natan - PL-2312).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_CADASTRA_DIMENSAO_PNEU(F_ALTURA BIGINT,
                                                                    F_LARGURA BIGINT,
                                                                    F_ARO REAL,
                                                                    OUT AVISO_DIMENSAO_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_DIMENSAO_EXISTENTE BIGINT := (SELECT CODIGO
                                      FROM DIMENSAO_PNEU
                                      WHERE LARGURA = F_LARGURA
                                        AND ALTURA = F_ALTURA
                                        AND ARO = F_ARO);
    COD_DIMENSAO_CRIADA    BIGINT;
BEGIN
    --VERIFICA SE OS DADOS INFORMADOS SÃO MAIORES QUE 0.
    IF(F_ALTURA < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA ALTURA DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_ALTURA;
    END IF;

    IF(F_LARGURA < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA LARGURA DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_LARGURA;
    END IF;

    IF(F_ARO < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA ARO DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_ARO;
    END IF;

    --VERIFICA SE ESSA DIMENSÃO EXISTE NA BASE DE DADOS.
    IF (COD_DIMENSAO_EXISTENTE IS NOT NULL)
    THEN
        RAISE EXCEPTION 'ERRO! ESSA DIMENSÃO JÁ ESTÁ CADASTRADA, POSSUI O CÓDIGO = %.', COD_DIMENSAO_EXISTENTE;
    END IF;

    --ADICIONA NOVA DIMENSÃO E RETORNA SEU ID.
    INSERT INTO DIMENSAO_PNEU(ALTURA, LARGURA, ARO)
    VALUES (F_ALTURA, F_LARGURA, F_ARO) RETURNING CODIGO INTO COD_DIMENSAO_CRIADA;

    --MENSAGEM DE SUCESSO.
    SELECT 'DIMENSÃO CADASTRADA COM SUCESSO! DIMENSÃO: ' || F_LARGURA || '/' || F_ALTURA || 'R' || F_ARO ||
           ' COM CÓDIGO: '
               || COD_DIMENSAO_CRIADA || '.'
    INTO AVISO_DIMENSAO_CRIADA;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################## FUNC PARA ALTERAR PARAMETRIZAÇÃO ############################################
--######################################################################################################################
--######################################################################################################################
--PL-2337
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_PARAMETRIZACAO_PNEU(F_COD_EMPRESA BIGINT,
                                                                        F_COD_UNIDADE BIGINT,
                                                                        F_TOLERANCIA_CALIBRAGEM REAL,
                                                                        F_TOLERANCIA_INSPECAO REAL,
                                                                        F_SULCO_MINIMO_RECAPAGEM REAL,
                                                                        F_SULCO_MINIMO_DESCARTE REAL,
                                                                        F_PERIODO_AFERICAO_PRESSAO BIGINT,
                                                                        F_PERIODO_AFERICAO_SULCO BIGINT,
                                                                        OUT PARAMETRIZACAO_ATUALIZADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_PARAMETRIZACAO_EXISTENTE   BIGINT := (SELECT COD_EMPRESA
                                              FROM PNEU_RESTRICAO_UNIDADE
                                              WHERE COD_EMPRESA = F_COD_EMPRESA
                                                AND COD_UNIDADE = F_COD_UNIDADE);
    TOLERANCIA_CALIBRAGEM_ATUAL    REAL;
    SULCO_MINIMO_RECAPAGEM_ATUAL   REAL;
    SULCO_MINIMO_DESCARTE_ATUAL    REAL;
    TOLERANCIA_INSPECAO_ATUAL      REAL;
    PERIODO_AFERICAO_PRESSAO_ATUAL BIGINT;
    PERIODO_AFERICAO_SULCO_ATUAL   BIGINT;
BEGIN
    --SETAR VARIÁVEIS
    SELECT TOLERANCIA_CALIBRAGEM,
           SULCO_MINIMO_RECAPAGEM,
           SULCO_MINIMO_DESCARTE,
           TOLERANCIA_INSPECAO,
           PERIODO_AFERICAO_PRESSAO,
           PERIODO_AFERICAO_SULCO
    INTO
        TOLERANCIA_CALIBRAGEM_ATUAL,
        SULCO_MINIMO_RECAPAGEM_ATUAL,
        SULCO_MINIMO_DESCARTE_ATUAL,
        TOLERANCIA_INSPECAO_ATUAL,
        PERIODO_AFERICAO_PRESSAO_ATUAL,
        PERIODO_AFERICAO_SULCO_ATUAL
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --GARANTE QUE EMPRESA POSSUI UNIDADE.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    --VERIFICA SE A EMPRESA POSSUI PARAMETRIZACÃO NESSA UNIDADE.
    IF (COD_PARAMETRIZACAO_EXISTENTE IS NULL)
    THEN
        RAISE EXCEPTION 'ERRO! A EMPRESA: % NÃO POSSUI PARAMETRIZAÇÃO NA UNIDADE: %', F_COD_EMPRESA, F_COD_UNIDADE;
    END IF;

    --VERIFICA SE ALGUM DADO É MENOR QUE ZERO.
    IF (F_TOLERANCIA_CALIBRAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA DE CALIBRAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_RECAPAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO RECAPAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_DESCARTE < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO DESCARTE DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_TOLERANCIA_INSPECAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA INSPEÇÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_PRESSAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO PRESSÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_SULCO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO SULCO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    --ATUALIZA DADOS DA PARAMETRIZAÇÃO.
    UPDATE PNEU_RESTRICAO_UNIDADE
    SET TOLERANCIA_CALIBRAGEM    = F_IF(F_TOLERANCIA_CALIBRAGEM IS NULL, TOLERANCIA_CALIBRAGEM_ATUAL,
                                        F_TOLERANCIA_CALIBRAGEM),
        SULCO_MINIMO_RECAPAGEM   = F_IF(F_SULCO_MINIMO_RECAPAGEM IS NULL, SULCO_MINIMO_RECAPAGEM_ATUAL,
                                        F_SULCO_MINIMO_RECAPAGEM),
        SULCO_MINIMO_DESCARTE    = F_IF(F_SULCO_MINIMO_DESCARTE IS NULL, SULCO_MINIMO_DESCARTE_ATUAL,
                                        F_SULCO_MINIMO_DESCARTE),
        TOLERANCIA_INSPECAO      = F_IF(F_TOLERANCIA_INSPECAO IS NULL, TOLERANCIA_INSPECAO_ATUAL,
                                        F_TOLERANCIA_INSPECAO),
        PERIODO_AFERICAO_PRESSAO = F_IF(F_PERIODO_AFERICAO_PRESSAO IS NULL, PERIODO_AFERICAO_PRESSAO_ATUAL,
                                        F_PERIODO_AFERICAO_PRESSAO),
        PERIODO_AFERICAO_SULCO   = F_IF(F_PERIODO_AFERICAO_SULCO IS NULL, PERIODO_AFERICAO_SULCO_ATUAL,
                                        F_PERIODO_AFERICAO_SULCO)
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --MENSAGEM DE SUCESSO.
    SELECT 'DADOS ATUALIZADOS COM SUCESSO!!'
               || ' EMPRESA: '
               || COD_EMPRESA
               || ', UNIDADE: '
               || COD_UNIDADE
               || ', TOLERANCIA CALIBRAGEM: '
               || TOLERANCIA_CALIBRAGEM
               || ', SULCO MÍNIMO RECAPAGEM: '
               || SULCO_MINIMO_RECAPAGEM
               || ', SULCO MÍNIMO DESCARTE: '
               || SULCO_MINIMO_DESCARTE
               || ' TOLERANCIA INSPEÇÃO: '
               || TOLERANCIA_INSPECAO
               || ',PERÍODO AFERIÇÃO PRESSÃO: '
               || PERIODO_AFERICAO_PRESSAO
               || ', PERÍODO AFERIÇÃO SULCO: '
               || PERIODO_AFERICAO_SULCO
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE
    INTO PARAMETRIZACAO_ATUALIZADA;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2364
-- Histórico, já foi executado.
-- DROP FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(F_COD_UNIDADE BIGINT,
--     F_DATA_INICIAL DATE,
--     F_DATA_FINAL DATE,
--     F_CPF TEXT);
--
-- CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(F_COD_UNIDADE BIGINT,
--                                                                      F_DATA_INICIAL DATE,
--                                                                      F_DATA_FINAL DATE,
--                                                                      F_CPF TEXT)
--     RETURNS TABLE
--             (
--                 "NOME"                                         TEXT,
--                 "MATRÍCULA TRANSPORTADORA"                     TEXT,
--                 "MATRÍCULA AMBEV"                              TEXT,
--                 "CARGO"                                        TEXT,
--                 "SETOR"                                        TEXT,
--                 "EQUIPE"                                       TEXT,
--                 "INTERVALO"                                    TEXT,
--                 "INICIO INTERVALO"                             TEXT,
--                 "LATITUDE INÍCIO"                              TEXT,
--                 "LONGITUDE INÍCIO"                             TEXT,
--                 "FONTE DATA/HORA INÍCIO"                       TEXT,
--                 "DATA/HORA SINCRONIZAÇÃO INÍCIO"               TEXT,
--                 "FIM INTERVALO"                                TEXT,
--                 "LATITUDE FIM"                                 TEXT,
--                 "LONGITUDE FIM"                                TEXT,
--                 "FONTE DATA/HORA FIM"                          TEXT,
--                 "DATA/HORA SINCRONIZAÇÃO FIM"                  TEXT,
--                 "TEMPO DECORRIDO (MINUTOS)"                    TEXT,
--                 "TEMPO RECOMENDADO (MINUTOS)"                  BIGINT,
--                 "CUMPRIU TEMPO MÍNIMO"                         TEXT,
--                 "JUSTIFICATIVA NÃO CUMPRIMENTO TEMPO MÍNIMO"   TEXT,
--                 "JUSTIFICATIVA ESTOURO TEMPO MÁXIMO PERMITIDO" TEXT,
--                 "DISTANCIA ENTRE INÍCIO E FIM (METROS)"        TEXT,
--                 "DEVICE IMEI INÍCIO"                           TEXT,
--                 "DEVICE IMEI INÍCIO RECONHECIDO"               TEXT,
--                 "DEVICE IMEI FIM"                              TEXT,
--                 "DEVICE IMEI FIM RECONHECIDO"                  TEXT
--             )
--     LANGUAGE SQL
-- AS
-- $$
-- SELECT C.NOME                                                                  AS NOME_COLABORADOR,
--        COALESCE(C.MATRICULA_TRANS :: TEXT, '-')                                AS MATRICULA_TRANS,
--        COALESCE(C.MATRICULA_AMBEV :: TEXT, '-')                                AS MATRICULA_AMBEV,
--        F.NOME                                                                  AS CARGO,
--        S.NOME                                                                  AS SETOR,
--        E.NOME                                                                  AS EQUIPE,
--        IT.NOME                                                                 AS INTERVALO,
--        COALESCE(
--                TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
--                        'DD/MM/YYYY HH24:MI:SS'),
--                '')                                                             AS DATA_HORA_INICIO,
--        I.LATITUDE_MARCACAO_INICIO :: TEXT                                      AS LATITUDE_INICIO,
--        I.LONGITUDE_MARCACAO_INICIO :: TEXT                                     AS LONGITUDE_INICIO,
--        I.FONTE_DATA_HORA_INICIO,
--        COALESCE(TO_CHAR(I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
--                         'DD/MM/YYYY HH24:MI:SS'),
--                 ''),
--        COALESCE(TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
--                         'DD/MM/YYYY HH24:MI:SS'),
--                 '')                                                            AS DATA_HORA_FIM,
--        I.LATITUDE_MARCACAO_FIM :: TEXT                                         AS LATITUDE_FIM,
--        I.LONGITUDE_MARCACAO_FIM :: TEXT                                        AS LONGITUDE_FIM,
--        I.FONTE_DATA_HORA_FIM,
--        COALESCE(TO_CHAR(I.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
--                         'DD/MM/YYYY HH24:MI:SS'),
--                 ''),
--        COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60) :: TEXT,
--                 '')                                                            AS TEMPO_DECORRIDO_MINUTOS,
--        IT.TEMPO_RECOMENDADO_MINUTOS,
--        CASE
--            WHEN I.DATA_HORA_FIM IS NULL OR I.DATA_HORA_INICIO IS NULL
--                THEN ''
--            WHEN IT.TEMPO_RECOMENDADO_MINUTOS > (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
--                THEN 'NÃO'
--            ELSE 'SIM' END                                                      AS CUMPRIU_TEMPO_MINIMO,
--        I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
--        I.JUSTIFICATIVA_ESTOURO,
--        COALESCE(TRUNC((ST_DISTANCE(
--                ST_POINT(I.LONGITUDE_MARCACAO_INICIO :: FLOAT,
--                         I.LATITUDE_MARCACAO_INICIO :: FLOAT) :: GEOGRAPHY,
--                ST_POINT(I.LONGITUDE_MARCACAO_FIM :: FLOAT,
--                         I.LATITUDE_MARCACAO_FIM :: FLOAT) :: GEOGRAPHY))) :: TEXT,
--                 '-')                                                           AS DISTANCIA,
--        COALESCE(I.DEVICE_IMEI_INICIO :: TEXT, '-')                             AS DEVICE_IMEI_INICIO,
--        F_IF(
--                I.DEVICE_IMEI_INICIO IS NOT NULL,
--                F_IF(I.DEVICE_IMEI_INICIO_RECONHECIDO, 'SIM', 'NÃO' :: TEXT),
--                '-' :: TEXT)                                                    AS DEVICE_IMEI_INICIO_RECONHECIDO,
--        COALESCE(I.DEVICE_IMEI_FIM :: TEXT, '-')                                AS DEVICE_IMEI_FIM,
--        F_IF(
--                I.DEVICE_IMEI_FIM IS NOT NULL,
--                F_IF(I.DEVICE_IMEI_FIM_RECONHECIDO, 'SIM', 'NÃO' :: TEXT), '-') AS DEVICE_IMEI_FIM_RECONHECIDO
-- FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, CASE
--                                                   WHEN F_CPF = '%'
--                                                       THEN NULL
--                                                   ELSE F_CPF :: BIGINT END, NULL) I
--          JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
--          JOIN INTERVALO_TIPO IT ON IT.CODIGO = I.COD_TIPO_INTERVALO
--          JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
--          JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO
--          JOIN SETOR S ON S.CODIGO = C.COD_SETOR
--          JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE
-- WHERE ((I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
--            BETWEEN F_DATA_INICIAL
--            AND F_DATA_FINAL
--     OR (I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
--            BETWEEN F_DATA_INICIAL
--            AND F_DATA_FINAL)
-- ORDER BY I.DATA_HORA_INICIO, C.NOME
-- $$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################ ALTERAÇÕES NA PESQUISA NPS ##############################################
--######################################################################################################################
--######################################################################################################################
-- PL-2355

-- Deleta todas as pesquisas
DELETE FROM CS.NPS_PESQUISA;

-- Adiciona as colunas para armazenar as legendas das escala (baixa/alta)
ALTER TABLE CS.NPS_PESQUISA
	ADD LEGENDA_ESCALA_BAIXA VARCHAR(255) NOT NULL;
ALTER TABLE CS.NPS_PESQUISA
	ADD LEGENDA_ESCALA_ALTA VARCHAR(255) NOT NULL;

-- Dropa a function que retorna as pesquisas disponíveis
DROP FUNCTION cs.func_nps_busca_pesquisa_disponivel(bigint,date);

-- Recria a function que retorna as pesquisas disponíveis para trazer as legendas da escala (baixa/alta)
create or replace function cs.func_nps_busca_pesquisa_disponivel(f_cod_colaborador bigint,
                                                                 f_data_atual date)
    returns table
            (
                COD_PESQUISA_NPS           BIGINT,
                TITULO_PESQUISA            TEXT,
                BREVE_DESCRICAO_PESQUISA   TEXT,
                TITULO_PERGUNTA_ESCALA     TEXT,
                LEGENDA_ESCALA_BAIXA       TEXT,
                LEGENDA_ESCALA_ALTA        TEXT,
                TITULO_PERGUNTA_DESCRITIVA TEXT
            )
    language plpgsql
as
$$
declare
    f_cod_pesquisa_nps           bigint;
    f_titulo_pesquisa            text;
    f_breve_descricao_pesquisa   text;
    f_titulo_pergunta_escala     text;
    f_legenda_escala_baixa       text;
    f_legenda_escala_alta        text;
    f_titulo_pergunta_descritiva text;
begin
    -- Mesmo tendo o index para permitir apenas uma ativa por vez, esse SELECT já garante isso também.
    select np.codigo,
           np.titulo_pesquisa,
           np.breve_descricao_pesquisa,
           np.titulo_pergunta_escala,
           np.LEGENDA_ESCALA_BAIXA,
           np.LEGENDA_ESCALA_ALTA,
           np.titulo_pergunta_descritiva
    from cs.nps_pesquisa np
    -- Ativo e ainda em veiculação.
    where np.status_ativo
    and f_data_atual <@ periodo_veiculacao_pesquisa
    into
        f_cod_pesquisa_nps,
        f_titulo_pesquisa,
        f_breve_descricao_pesquisa,
        f_titulo_pergunta_escala,
        f_legenda_escala_baixa,
        f_legenda_escala_alta,
        f_titulo_pergunta_descritiva;

    if f_cod_pesquisa_nps is null
    then
        return query
        select null :: bigint, null :: text, null :: text, null :: text, null :: text, null :: text, null :: text;

        -- Break.
        return;
    end if;

    -- Se o colaborador ainda não respondeu e também não bloqueou a pesquisa, então temos uma disponível.
    if ((select not exists(select nbpc.cod_nps_pesquisa
                          from cs.nps_bloqueio_pesquisa_colaborador nbpc
                          where nbpc.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nbpc.cod_colaborador_bloqueio = f_cod_colaborador))
        and
        (select not exists(select nr.cod_nps_pesquisa
                          from cs.nps_respostas nr
                          where nr.cod_nps_pesquisa = f_cod_pesquisa_nps
                            and nr.cod_colaborador_respostas = f_cod_colaborador)))
    then
        return query
            select f_cod_pesquisa_nps,
                   f_titulo_pesquisa,
                   f_breve_descricao_pesquisa,
                   f_titulo_pergunta_escala,
                   f_legenda_escala_baixa,
                   f_legenda_escala_alta,
                   f_titulo_pergunta_descritiva;
    end if;
end;
$$;

-- Dropa a function que insere novas pesquisas
DROP FUNCTION CS.FUNC_NPS_INSERE_NOVA_PESQUISA(TEXT,TEXT,TEXT,TEXT,DATE,DATE);

-- Recria a function que insere novas pesquisas para conter as legendas da escala (baixa/alta)
create or replace function cs.func_nps_insere_nova_pesquisa(f_titulo_pesquisa text,
                                                            f_breve_descricao_pesquisa text,
                                                            f_titulo_pergunta_escala text,
                                                            f_legenda_escala_baixa text,
                                                            f_legenda_escala_alta text,
                                                            f_titulo_pergunta_descritiva text,
                                                            f_data_inicio_veiculacao_inclusivo date,
                                                            f_data_fim_veiculacao_exclusivo date,
                                                            out aviso_pesquisa_inserida text)
    returns text
    language plpgsql
    -- Para o time de CS poder usar.
    security definer
as
$$
begin
    -- Antes de inserir uma nova pesquisa, inativa todas as anteriores.
    update cs.nps_pesquisa set status_ativo = false;

    insert into cs.nps_pesquisa (titulo_pesquisa,
                                 breve_descricao_pesquisa,
                                 periodo_veiculacao_pesquisa,
                                 titulo_pergunta_escala,
                                 legenda_escala_baixa,
                                 legenda_escala_alta,
                                 titulo_pergunta_descritiva)
    values (f_titulo_pesquisa,
            f_breve_descricao_pesquisa,
            daterange(f_data_inicio_veiculacao_inclusivo, f_data_fim_veiculacao_exclusivo),
            f_titulo_pergunta_escala,
            f_legenda_escala_baixa,
            f_legenda_escala_alta,
            f_titulo_pergunta_descritiva);

    if not found
    then
        raise exception 'Erro ao inserir nova pesquisa de NPS';
    end if;

    select 'Pesquisa de NPS inserida com sucesso!'
    into aviso_pesquisa_inserida;
end;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2380
INSERT INTO VEICULO_DIAGRAMA(CODIGO, NOME, URL_IMAGEM) VALUES (19, 'BI-TRUCK EIXO SINGLE', 'WWW.GOOGLE.COM/BI-TRUCK');

-- Criamos os eixos
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (19, 'D', 1, 2, TRUE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (19, 'D', 2, 2, TRUE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (19, 'T', 3, 4, FALSE);
INSERT INTO VEICULO_DIAGRAMA_EIXOS(COD_DIAGRAMA, TIPO_EIXO, POSICAO, QT_PNEUS, EIXO_DIRECIONAL) VALUES (19, 'T', 4, 2, FALSE);

-- Criamos as posições
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 111);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 121);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 211);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 221);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 311);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 312);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 321);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 322);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 411);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 421);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 900);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 901);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 902);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 903);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 904);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 905);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 906);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 907);
INSERT INTO VEICULO_DIAGRAMA_POSICAO_PROLOG(COD_DIAGRAMA, POSICAO_PROLOG) VALUES (19, 908);
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################### FUNCTIONS DE MARCA DE BANDA ################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2263
CREATE OR REPLACE FUNCTION FUNC_PNEU_CADASTRA_MARCA_BANDA(F_COD_EMPRESA BIGINT,
                                                          F_MARCA_BANDA VARCHAR)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_MARCA_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT MB.NOME
              FROM MARCA_BANDA MB
              WHERE UNACCENT(TRIM(MB.NOME)) ILIKE UNACCENT(TRIM(F_MARCA_BANDA))
                AND MB.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe uma marca de nome \'%s\' cadastrada na empresa', F_MARCA_BANDA));
    END IF;

    INSERT INTO MARCA_BANDA (COD_EMPRESA, NOME)
    SELECT F_COD_EMPRESA,
           REMOVE_EXTRA_SPACES(F_MARCA_BANDA)
           RETURNING CODIGO INTO COD_MARCA_INSERIDO;

    RETURN COD_MARCA_INSERIDO;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_EDITA_MARCA_BANDA(F_COD_MARCA_BANDA BIGINT,
                                                       F_NOME_MARCA_BANDA VARCHAR)
    RETURNS BIGINT
    LANGUAGE SQL
AS
$$
UPDATE MARCA_BANDA
SET NOME = REMOVE_EXTRA_SPACES(F_NOME_MARCA_BANDA)
WHERE CODIGO = F_COD_MARCA_BANDA RETURNING CODIGO;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                CODIGO BIGINT,
                NOME   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MB.CODIGO       AS COD_MARCA_PNEU,
       MB.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_BANDA MB
WHERE MB.COD_EMPRESA = F_COD_EMPRESA
ORDER BY MB.NOME;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCA_BANDA_VISUALIZACAO(F_COD_MARCA BIGINT)
    RETURNS TABLE
            (
                CODIGO BIGINT,
                NOME   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MB.CODIGO       AS COD_MARCA_PNEU,
       MB.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_BANDA MB
WHERE MB.CODIGO = F_COD_MARCA;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################### FUNCTIONS DE MODELO DE BANDA ###############################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_CADASTRA_MODELO_BANDA(F_COD_EMPRESA BIGINT,
                                                           F_COD_MARCA_BANDA BIGINT,
                                                           F_NOME_MODELO_BANDA VARCHAR(255),
                                                           F_QTD_SULCOS INTEGER,
                                                           F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_MODELO_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT MB.CODIGO
              FROM MODELO_BANDA MB
              WHERE UNACCENT(TRIM(MB.NOME)) ILIKE UNACCENT(TRIM(F_NOME_MODELO_BANDA))
                AND MB.COD_MARCA = F_COD_MARCA_BANDA
                AND MB.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe um modelo de nome \'%s\' cadastrado na mesma marca', F_NOME_MODELO_BANDA));
    END IF;

    INSERT INTO MODELO_BANDA (COD_EMPRESA,
                              COD_MARCA,
                              NOME,
                              QT_SULCOS,
                              ALTURA_SULCOS)
    SELECT F_COD_EMPRESA,
           F_COD_MARCA_BANDA,
           REMOVE_EXTRA_SPACES(F_NOME_MODELO_BANDA),
           F_QTD_SULCOS,
           F_ALTURA_SULCOS
           RETURNING CODIGO INTO COD_MODELO_INSERIDO;

    RETURN COD_MODELO_INSERIDO;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_EDITA_MODELO_BANDA(F_COD_EMPRESA BIGINT,
                                                        F_COD_MARCA_BANDA BIGINT,
                                                        F_COD_MODELO_BANDA BIGINT,
                                                        F_NOME_MODELO_BANDA VARCHAR(255),
                                                        F_QTD_SULCOS INTEGER,
                                                        F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE SQL
AS
$$
UPDATE MODELO_BANDA
SET COD_MARCA     = F_COD_MARCA_BANDA,
    NOME          = REMOVE_EXTRA_SPACES(F_NOME_MODELO_BANDA),
    QT_SULCOS     = F_QTD_SULCOS,
    ALTURA_SULCOS = F_ALTURA_SULCOS
WHERE COD_EMPRESA = F_COD_EMPRESA
  AND CODIGO = F_COD_MODELO_BANDA RETURNING CODIGO;
$$;

--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT, F_COD_MARCA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_BANDA   BIGINT,
                NOME_MARCA_BANDA  TEXT,
                COD_MODELO_BANDA  BIGINT,
                NOME_MODELO_BANDA TEXT,
                QTD_SULCOS        SMALLINT,
                ALTURA_SULCOS     NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAB.CODIGO                             AS COD_MARCA_BANDA,
       MAB.NOME :: TEXT                       AS NOME_MARCA_BANDA,
       MOB.CODIGO                             AS COD_MODELO_BANDA,
       MOB.NOME :: TEXT                       AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOB.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_BANDA MAB
         JOIN MODELO_BANDA MOB
              ON MAB.CODIGO = MOB.COD_MARCA
WHERE F_IF(F_COD_EMPRESA IS NULL, TRUE, MAB.COD_EMPRESA = F_COD_EMPRESA)
  AND F_IF(F_COD_MARCA IS NULL, TRUE, MAB.CODIGO = F_COD_MARCA)
ORDER BY NOME_MARCA_BANDA, NOME_MODELO_BANDA
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELO_BANDA_VISUALIZACAO(F_COD_MODELO_BANDA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_BANDA            BIGINT,
                NOME_MARCA_BANDA           TEXT,
                COD_MODELO_BANDA           BIGINT,
                NOME_MODELO_BANDA          TEXT,
                QT_SULCOS_MODELO_BANDA     SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAB.CODIGO                            AS COD_MARCA_BANDA,
       MAB.NOME :: TEXT                      AS NOME_MARCA_BANDA,
       MOB.CODIGO                            AS COD_MODELO_BANDA,
       MOB.NOME :: TEXT                      AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                         AS QT_SULCOS_MODELO_BANDA,
       TRUNC(MOB.ALTURA_SULCOS ::NUMERIC, 2) AS ALTURA_SULCOS_MODELO_BANDA
FROM MODELO_BANDA MOB
         JOIN MARCA_BANDA MAB
              ON MOB.COD_MARCA = MAB.CODIGO
                  AND MOB.COD_EMPRESA = MAB.COD_EMPRESA
WHERE MOB.CODIGO = F_COD_MODELO_BANDA;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################### FUNCTION DE MARCA DE PNEU ##################################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_PNEU_LISTAGEM()
    RETURNS TABLE
            (
                COD_MARCA_PNEU  BIGINT,
                NOME_MARCA_PNEU TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MP.CODIGO       AS COD_MARCA_PNEU,
       MP.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_PNEU MP
ORDER BY MP.NOME;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################### FUNCTIONS DE MODELO DE PNEU ################################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_CADASTRA_MODELO_PNEU(F_COD_EMPRESA BIGINT,
                                                          F_COD_MARCA_PNEU BIGINT,
                                                          F_NOME_MODELO_PNEU VARCHAR(255),
                                                          F_QTD_SULCOS INTEGER,
                                                          F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_MODELO_INSERIDO BIGINT;
BEGIN
    IF EXISTS(SELECT MP.CODIGO
              FROM MODELO_PNEU MP
              WHERE UNACCENT(TRIM(MP.NOME)) ILIKE UNACCENT(TRIM(F_NOME_MODELO_PNEU))
                AND MP.COD_MARCA = F_COD_MARCA_PNEU
                AND MP.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        FORMAT(E'Já existe um modelo de nome \'%s\' cadastrado na mesma marca', F_NOME_MODELO_PNEU));
    END IF;

    INSERT INTO MODELO_PNEU (COD_EMPRESA,
                             COD_MARCA,
                             NOME,
                             QT_SULCOS,
                             ALTURA_SULCOS)
    SELECT F_COD_EMPRESA,
           F_COD_MARCA_PNEU,
           REMOVE_EXTRA_SPACES(F_NOME_MODELO_PNEU),
           F_QTD_SULCOS,
           F_ALTURA_SULCOS
           RETURNING CODIGO INTO COD_MODELO_INSERIDO;

    RETURN COD_MODELO_INSERIDO;
END;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_EDITA_MODELO_PNEU(F_COD_EMPRESA BIGINT,
                                                       F_COD_MARCA_PNEU BIGINT,
                                                       F_COD_MODELO_PNEU BIGINT,
                                                       F_NOME_MODELO_PNEU VARCHAR(255),
                                                       F_QTD_SULCOS INTEGER,
                                                       F_ALTURA_SULCOS DOUBLE PRECISION)
    RETURNS BIGINT
    LANGUAGE SQL
AS
$$
UPDATE MODELO_PNEU
SET COD_MARCA     = F_COD_MARCA_PNEU,
    NOME          = REMOVE_EXTRA_SPACES(F_NOME_MODELO_PNEU),
    QT_SULCOS     = F_QTD_SULCOS,
    ALTURA_SULCOS = F_ALTURA_SULCOS
WHERE COD_EMPRESA = F_COD_EMPRESA
  AND CODIGO = F_COD_MODELO_PNEU RETURNING CODIGO;
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT, F_COD_MARCA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_PNEU   BIGINT,
                NOME_MARCA_PNEU  TEXT,
                COD_MODELO_PNEU  BIGINT,
                NOME_MODELO_PNEU TEXT,
                QTD_SULCOS       SMALLINT,
                ALTURA_SULCOS    NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAP.CODIGO                             AS COD_MARCA_PNEU,
       MAP.NOME :: TEXT                       AS NOME_MARCA_PNEU,
       MOP.CODIGO                             AS COD_MODELO_PNEU,
       MOP.NOME :: TEXT                       AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOP.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_PNEU MAP
         JOIN MODELO_PNEU MOP
              ON MAP.CODIGO = MOP.COD_MARCA
WHERE F_IF(F_COD_EMPRESA IS NULL, TRUE, MOP.COD_EMPRESA = F_COD_EMPRESA)
  AND F_IF(F_COD_MARCA IS NULL, TRUE, MAP.CODIGO = F_COD_MARCA)
ORDER BY NOME_MARCA_PNEU, NOME_MODELO_PNEU
$$;

--######################################################################################################################

CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELO_PNEU_VISUALIZACAO(F_COD_MODELO BIGINT)
    RETURNS TABLE
            (
                COD_EMPRESA   BIGINT,
                COD_MARCA     BIGINT,
                COD_MODELO    BIGINT,
                NOME_MODELO   TEXT,
                QTD_SULCOS    SMALLINT,
                ALTURA_SULCOS NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MP.COD_EMPRESA,
       MP.COD_MARCA,
       MP.CODIGO,
       MP.NOME :: TEXT,
       MP.QT_SULCOS,
       TRUNC(MP.ALTURA_SULCOS :: NUMERIC, 2)
FROM MODELO_PNEU MP
WHERE MP.CODIGO = F_COD_MODELO;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################### ADICIONA CONSTRAINTS NAS TABELAS ###########################################
--######################################################################################################################
--######################################################################################################################
UPDATE MODELO_PNEU SET QT_SULCOS = F_IF(QT_SULCOS % 2 = 0, 4, 3)
WHERE QT_SULCOS > 6 OR QT_SULCOS < 1;

UPDATE MODELO_BANDA SET QT_SULCOS = F_IF(QT_SULCOS % 2 = 0, 4, 3)
WHERE QT_SULCOS > 6 OR QT_SULCOS < 1;

ALTER TABLE MODELO_PNEU ADD CONSTRAINT CHECK_QTD_SULCOS_MODELO_PNEU CHECK (QT_SULCOS >= 1 AND QT_SULCOS <= 6);
ALTER TABLE MODELO_BANDA ADD CONSTRAINT CHECK_QTD_SULCOS_MODELO_BANDA CHECK (QT_SULCOS >= 1 AND QT_SULCOS <= 6);
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

END TRANSACTION;