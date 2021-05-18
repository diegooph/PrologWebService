--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Cria dependências necessárias do pneu e insere pneus.
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (131, 'R268', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (135, 'G658', 4, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (137, 'HSR', 3, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (139, 'FS511', 2, 3, 4, 15);
INSERT INTO public.modelo_pneu (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (148, 'XZU', 7, 3, 4, 15);
SELECT setval('modelo_pneu_codigo_seq', 148, true);

INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (6, 'Bandag', 3);
INSERT INTO public.marca_banda (codigo, nome, cod_empresa)
VALUES (25, 'Vipal', 3);
SELECT setval('marca_banda_codigo_seq', 25, true);

INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (11, 'BTLSA2', 6, 3, 5, 15);
INSERT INTO public.modelo_banda (codigo, nome, cod_marca, cod_empresa, qt_sulcos, altura_sulcos)
VALUES (57, 'vlw110', 25, 3, 3, 16);
SELECT setval('modelo_banda_codigo_seq', 57, true);


create table public.pneu_script
(
    codigo_cliente               varchar(255)                           not null,
    cod_modelo                   bigint                                 not null,
    cod_dimensao                 bigint                                 not null,
    pressao_recomendada          real                                   not null,
    pressao_atual                real,
    altura_sulco_interno         real,
    altura_sulco_central_interno real,
    altura_sulco_externo         real,
    cod_unidade                  bigint                                 not null,
    status                       varchar(255)                           not null,
    vida_atual                   integer,
    vida_total                   integer,
    cod_modelo_banda             bigint,
    altura_sulco_central_externo real,
    dot                          varchar(20),
    valor                        real                                   not null,
    data_hora_cadastro           timestamp with time zone default now(),
    pneu_novo_nunca_rodado       boolean                  default false not null,
    codigo                       bigserial                              not null,
    cod_empresa                  bigint                                 not null,
    cod_unidade_cadastro         integer                                not null,
    deletado                     boolean                  default false not null,
    data_hora_deletado           timestamp with time zone,
    pg_username_delecao          text
);

CREATE
    OR REPLACE FUNCTION TG_FUNC_PNEU_SCRIPT_INSERT()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PNEU_PRIMEIRA_VIDA BIGINT  := 1;
    PNEU_POSSUI_BANDA
                       BOOLEAN := F_IF(NEW.vida_atual > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_PNEU_PROLOG
                       BIGINT;
    F_QTD_ROWS_AFETADAS
                       BIGINT;
    F_COD_SERVICO_REALIZADO
                       BIGINT;
BEGIN
    INSERT INTO PUBLIC.PNEU(CODIGO,
                            COD_EMPRESA,
                            COD_UNIDADE_CADASTRO,
                            COD_UNIDADE,
                            CODIGO_CLIENTE,
                            COD_MODELO,
                            COD_DIMENSAO,
                            PRESSAO_RECOMENDADA,
                            PRESSAO_ATUAL,
                            ALTURA_SULCO_INTERNO,
                            ALTURA_SULCO_CENTRAL_INTERNO,
                            ALTURA_SULCO_CENTRAL_EXTERNO,
                            ALTURA_SULCO_EXTERNO,
                            STATUS,
                            VIDA_ATUAL,
                            VIDA_TOTAL,
                            DOT,
                            VALOR,
                            COD_MODELO_BANDA,
                            PNEU_NOVO_NUNCA_RODADO,
                            DATA_HORA_CADASTRO)
    VALUES (NEW.codigo,
            NEW.cod_empresa,
            NEW.cod_unidade,
            NEW.cod_unidade,
            NEW.codigo_cliente,
            NEW.cod_modelo,
            NEW.cod_dimensao,
            NEW.pressao_recomendada,
            NEW.pressao_atual, -- PRESSAO_ATUAL
            NEW.altura_sulco_interno, -- ALTURA_SULCO_INTERNO
            NEW.altura_sulco_central_interno, -- ALTURA_SULCO_CENTRAL_INTERNO
            NEW.altura_sulco_central_externo, -- ALTURA_SULCO_CENTRAL_EXTERNO
            NEW.altura_sulco_externo, -- ALTURA_SULCO_EXTERNO
            NEW.status,
            NEW.vida_atual,
            NEW.vida_total,
            NEW.dot,
            NEW.valor,
            NEW.cod_modelo_banda,
            NEW.pneu_novo_nunca_rodado,
            NEW.data_hora_cadastro)
    RETURNING CODIGO INTO COD_PNEU_PROLOG;

-- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
    IF
        (PNEU_POSSUI_BANDA)
    THEN
        --  Inserimos o serviço realizado, retornando o código.
        INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(COD_TIPO_SERVICO,
                                                  COD_UNIDADE,
                                                  COD_PNEU,
                                                  CUSTO,
                                                  VIDA,
                                                  FONTE_SERVICO_REALIZADO)
        VALUES ((SELECT PTS.CODIGO
                 FROM PNEU_TIPO_SERVICO AS PTS
                 WHERE PTS.COD_EMPRESA IS NULL
                   AND PTS.STATUS_ATIVO = TRUE
                   AND PTS.INCREMENTA_VIDA = TRUE
                   AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE),
                NEW.cod_unidade,
                COD_PNEU_PROLOG,
                200, -- Fixamos 200.
                NEW.vida_atual - 1,
                'FONTE_CADASTRO')
        RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

        -- Mapeamos o incremento de vida do serviço realizado acima.
        INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(COD_SERVICO_REALIZADO,
                                                                  COD_MODELO_BANDA,
                                                                  VIDA_NOVA_PNEU,
                                                                  FONTE_SERVICO_REALIZADO)
        VALUES (F_COD_SERVICO_REALIZADO,
                NEW.cod_modelo_banda,
                NEW.vida_atual,
                'FONTE_CADASTRO');

        INSERT INTO PUBLIC.PNEU_SERVICO_CADASTRO(COD_PNEU,
                                                 COD_SERVICO_REALIZADO)
        VALUES (COD_PNEU_PROLOG,
                F_COD_SERVICO_REALIZADO);

        GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

-- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
        IF
            (F_QTD_ROWS_AFETADAS <= 0)
        THEN
            PERFORM PUBLIC.THROW_GENERIC_ERROR(
                    FORMAT('Não foi possível incrementar a vida do pneu %s', COD_PNEU_PROLOG));
        END IF;
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

-- Verificamos se a inserção ocorreu com sucesso.
    IF
        (F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%"', COD_PNEU_PROLOG;
    END IF;

    RETURN NEW;
END
$$;

CREATE TRIGGER TG_PNEU_SCRIPT_INSERT
    BEFORE INSERT
    ON PNEU_SCRIPT
    FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_PNEU_SCRIPT_INSERT();

INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('15099', 139, 1, 100, 0, 10, 10, 10, 5, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('26354', 131, 2, 122, 0, 10, 10, 10, 5, 'EM_USO', 2, 4, 57, 10, '1012', 1120, '2018-03-26 18:24:46.745298',
        false, 2, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29167', 135, 2, 90, 0, 12, 12, 12, 5, 'EM_USO', 2, 4, 57, 12, '1210', 1120, '2018-03-26 18:31:27.652918',
        false, 6, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('58624', 148, 1, 120, 0, 16, 16, 16, 5, 'EM_USO', 1, 4, null, 16, '2217', 0.01, '2018-03-26 18:35:06.572154',
        false, 24, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55284', 148, 1, 120, 0, 16, 16, 16, 5, 'EM_USO', 1, 4, null, 16, '2216', 0.01, '2018-03-26 18:37:53.212183',
        false, 60, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('22351', 131, 1, 120, 0, 16, 16, 16, 5, 'EM_USO', 3, 4, 11, 16, '2215', 0.01, '2018-03-26 18:42:00.864434',
        false, 76, 3, 5, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29066', 137, 1, 120, 0, 11, 11, 11, 5, 'EM_USO', 2, 4, 57, 11, '4812', 0.01, '2018-03-26 18:49:39.090519',
        false, 87, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('95292', 137, 1, 90, 0, 2, 3, 2, 5, 'EM_USO', 2, 4, 11, 3, '2414', 0.01, '2018-03-26 18:55:39.776027', false,
        129, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('43703', 148, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 187, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('79927', 148, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 189, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('28842', 148, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 190, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13154', 148, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 191, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('513', 148, 4, 105, 100.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 192, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('514', 148, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 217, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('515', 148, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 218, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('516', 131, 4, 105, 101.5, 10.49, 10.49, 10.49, 5, 'EM_USO', 1, 4, null, 10.49, '', 1200, null, false, 219, 3,
        5, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('267', 148, 3, 115, 108.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 478, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('521', 137, 2, 105, 96.15, 6.91, 5.45, 7.56, 5, 'EM_USO', 1, 4, null, 6.32, '', 1200, null, false, 547, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('522', 137, 2, 105, 0, 17.8, 18.94, 18.04, 5, 'EM_USO', 1, 4, null, 17.54, '', 1200, null, false, 548, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('523', 137, 2, 105, 94.22, 8.37, 8.01, 7.91, 5, 'EM_USO', 1, 4, null, 7.82, '', 1200, null, false, 549, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('75', 148, 1, 115, 100.5, 15.5, 15.5, 15.5, 5, 'EM_USO', 1, 4, null, 15.5, '', 1200, null, false, 681, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('352', 148, 1, 115, 114.42, 12.13, 11.76, 13.23, 5, 'EM_USO', 1, 4, null, 11.65, '', 1200, null, false, 821,
        3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('397', 135, 1, 115, 0.1, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3817',
        1200, null, false, 849, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('20894', 135, 1, 115, 0.1, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '3717',
        1200, null, false, 850, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('78007', 137, 2, 105, 102.44, 6.26, 6.75, 7.59, 5, 'EM_USO', 1, 4, null, 7.39, '', 1200, null, false, 851, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('21069', 137, 2, 105, 99.65, 4.12, 3.21, 6, 5, 'EM_USO', 1, 4, null, 4.12, '', 1200, null, false, 852, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('85371', 137, 1, 115, 0.1, 15, 15, 15, 5, 'EM_USO', 1, 4, null, 15, '', 1200,
        null, false, 862, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13271', 137, 1, 115, 120.57, 9.64, 8.85, 8.44, 5, 'EM_USO', 1, 4, null, 7.38, '1209', 1200, null, false, 863,
        3, 5, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('14817', 131, 3, 115, 114.87, 2.79, 2.17, 3.37, 5, 'ESTOQUE', 1, 4, null, 2.17, '', 1200, null, false, 867, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('41530', 131, 3, 115, 126.77, 16, 16, 16, 5, 'ESTOQUE', 2, 4, 57, 16, '', 1200, null, false, 868, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56754', 131, 1, 115, 0.1, 15, 15, 15, 215, 'ESTOQUE', 1, 4, null, 15, '3817',
        1200, null, false, 899, 3, 5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('50346', 131, 2, 105, 101.57, 15, 15, 15, 5, 'ESTOQUE', 3, 4, 11, 15, '2546', 1200, null, false, 913, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('3000', 131, 5, 150, 0, 10, 10, 10, 5, 'ESTOQUE', 1, 4, null, 10, 'dot teste', 300, null, false, 914, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('289', 131, 1, 115, 4.52, 15, 15, 15, 5, 'ESTOQUE', 1, 4, null, 15, '3217', 1200, null, false, 915, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('10', 139, 18, 115, 139.89, 7.62, 8.82, 10.64, 5, 'ESTOQUE', 1, 4, null, 8.82, '', 1200, null, false, 916, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('700', 131, 3, 115, 0, 10, 10, 10, 5, 'ESTOQUE', 3, 4, 11, 10, 'dd mm yy', 1500, null, false, 917, 3, 5, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('25', 131, 1, 115, 113.19, 12.34, 12.44, 12, 5, 'ESTOQUE', 1, 4, null, 11.93, '', 1200, null, false, 918, 3,
        5, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('13', 137, 2, 105, 96.5, 5.06, 3.69, 6.84, 5, 'ESTOQUE', 1, 4, null, 4.57, '', 1200, null, false, 921, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('16', 137, 2, 105, 109.44, 5.7, 24.6, 9.8, 5, 'ESTOQUE', 1, 3, null, 14.8, '', 1200, null, false, 964, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('17', 131, 3, 115, 107.17, 5.22, 5.25, 7.3, 5, 'ESTOQUE', 1, 4, null, 5.25, '', 1200, null, false, 966, 3, 5,
        false, null, null);


INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('598', 135, 1, 115, 75.9, 7.52, 4.71, 6.49, 5, 'DESCARTE', 1, 4, null, 3.35, '', 1200, null, false, 938, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('445', 135, 1, 115, 100.5, 17.5, 17.5, 17.5, 5, 'DESCARTE', 1, 4, null, 17.5, '', 1200, null, false, 951, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('15', 137, 2, 105, 96.67, 15, 15, 15, 5, 'DESCARTE', 2, 4, 11, 15, '', 1200, null, false, 952, 3, 5, false,
        null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('18', 158, 3, 115, 6.57, 9.6, 9.02, 10.35, 5, 'ANALISE', 1, 4, null, 9.02, '', 1200, null, false, 967, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('20', 158, 3, 115, 109.44, 5.12, 4.02, 5.64, 5, 'ANALISE', 1, 4, null, 4.02, '', 1200, null, false, 969, 3, 5,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('24', 158, 3, 115, 112.07, 6.29, 7.62, 10.15, 5, 'ANALISE', 1, 4, null, 7.62, '', 1200, null, false, 972, 3, 5,
        false, null, null);

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('23', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('485', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('486', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('107', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('484', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('26', 139, 1, 100, 0, 10, 10, 10, 215, 'EM_USO', 1, 4, null, 10, '1021', 1000, '2018-03-21 20:11:39.316243',
        false, 1, 3, 215, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('29', 137, 2, 105, -1, -1, -1, -1, 215, 'EM_USO', 1, 4, null, -1, '', 1200, null, false, 987, 3, 215, false,
        null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('30', 137, 2, 105, 120.29, 11.97, 12.1, 12.46, 215, 'EM_USO', 1, 4, null, 12.07, '', 1200, null, false, 988, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('32', 148, 1, 115, 117.41, 9.23, 9.26, 8.41, 215, 'EM_USO', 1, 4, null, 6.9, '', 1200, null, false, 989, 3, 215,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('33', 148, 1, 115, 116, 7.21, 6.59, 7.07, 215, 'EM_USO', 1, 4, null, 6.39, '', 1200, null, false, 990, 3, 215,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('487', 148, 4, 105, 0, 17.59, 21.31, 21.99, 215, 'EM_USO', 1, 4, null, 22.45, '', 1200, null, false, 1041, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('488', 148, 4, 105, 0, 16, 9.77, 18.8, 215, 'EM_USO', 1, 4, null, 16.06, '', 1200, null, false, 1044, 3, 215,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('489', 148, 4, 105, 0, 16.26, 16.26, 18.37, 215, 'EM_USO', 1, 4, null, 16.22, '', 1200, null, false, 1045, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('490', 148, 4, 105, 0, 16.26, 16.52, 16.58, 215, 'EM_USO', 1, 4, null, 16.32, '', 1200, null, false, 1048, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('40563', 148, 1, 115, 4.34, 15.5, 15.5, 15.5, 215, 'EM_USO', 1, 4, null, 15.5, '3217', 1200, null, false, 1049,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('493', 137, 4, 105, 0, 16.06, 16.13, 16.16, 215, 'EM_USO', 1, 4, null, 16.09, '', 1200, null, false, 1051, 3,
        215, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('494', 137, 4, 105, 0, 2.28, 2.7, 8.92, 215, 'EM_USO', 1, 4, null, 4.49, '', 1200, null, false, 1056, 3, 215,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('34', 137, 2, 105, 105.94, 11.71, 12.39, 12.91, 215, 'EM_USO', 1, 4, null, 13.14, '', 1200, null, false, 1060,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('35', 131, 2, 105, 112.42, 12.39, 11.45, 12.81, 215, 'EM_USO', 1, 4, null, 12.17, '', 1200, null, false, 1063,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('36', 137, 2, 105, 104.37, 8.01, 7.82, 8.63, 215, 'EM_USO', 1, 4, null, 6.39, '', 1200, null, false, 1075, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('37', 137, 2, 105, 96.85, 11.35, 11.58, 11.84, 215, 'EM_USO', 1, 4, null, 11.32, '', 1200, null, false, 1077, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('38', 137, 2, 105, 114.52, 10.32, 11.16, 11.52, 215, 'EM_USO', 1, 4, null, 11.48, '', 1200, null, false, 1087,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('39', 137, 2, 105, 104.19, 11.13, 11.39, 11.39, 215, 'EM_USO', 1, 4, null, 11.55, '', 1200, null, false, 1095,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('40', 137, 2, 105, 118.54, 10.61, 10.96, 11.13, 215, 'EM_USO', 1, 4, null, 11.55, '', 1200, null, false, 1096,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('41', 137, 2, 105, 105.77, 7.26, 7.98, 6.32, 215, 'EM_USO', 1, 4, null, 8.17, '', 1200, null, false, 1102, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('42', 148, 2, 105, 101.04, 5.71, 6.65, 8.17, 215, 'EM_USO', 1, 4, null, 7.07, '', 1200, null, false, 1103, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('43', 148, 1, 115, 113.82, 11.29, 9.47, 10.9, 215, 'EM_USO', 1, 4, null, 9.47, '', 1200, null, false, 1115, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('44', 148, 3, 115, 118.02, 6.71, 6.03, 7.07, 215, 'EM_USO', 1, 4, null, 6.03, '', 1200, null, false, 1116, 3,
        215, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('49', 139, 2, 105, 59.75, 10.09, 8.47, 9.76, 215, 'ESTOQUE', 1, 4, null, 7.95, '', 1200, null, false, 1121, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('50', 135, 2, 105, 38.93, 9.31, 8.53, 9.41, 215, 'ESTOQUE', 1, 4, null, 8.53, '', 1200, null, false, 1122, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('51', 137, 2, 105, 112.59, 2.66, 2.23, 2.53, 215, 'ESTOQUE', 1, 4, null, 1.97, '', 1200, null, false, 1123, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('52', 137, 2, 105, 95.8, 3.79, 3.69, 3.11, 215, 'ESTOQUE', 1, 4, null, 3.37, '', 1200, null, false, 1124, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('53', 137, 2, 105, 95.45, 7.75, 8.76, 7.82, 215, 'ESTOQUE', 1, 4, null, 8.34, '', 1200, null, false, 1125, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('54', 137, 2, 105, 63.43, 3.21, 3.89, 4.86, 215, 'ESTOQUE', 1, 4, null, 4.86, '', 1200, null, false, 1126, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('55', 137, 2, 105, 95.8, 4.44, 4.05, 3.92, 215, 'ESTOQUE', 1, 4, null, 3.6, '', 1200, null, false, 1127, 3, 215,
        false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('56', 137, 2, 105, 103.14, 2.04, 1.81, 3.66, 215, 'ESTOQUE', 1, 4, null, 2.79, '', 1200, null, false, 1128, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('57', 131, 1, 115, 0.1, 15, 15, 15, 215, 'ESTOQUE', 1, 4, null, 15, '', 1200,
        null, false, 1134, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('62', 135, 2, 105, 104.37, 10.64, 9.41, 10.77, 215, 'ESTOQUE', 1, 4, null, 10.06, '', 1200, null, false, 1199,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('63', 137, 2, 105, 95.45, 5.61, 6.36, 5.84, 215, 'ESTOQUE', 1, 4, null, 5.61, '', 1200, null, false, 1233, 3,
        215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('64', 131, 2, 105, 109.27, 14.18, 14.27, 14.92, 215, 'ESTOQUE', 1, 4, null, 14.47, '', 1200, null, false, 1248,
        3, 215, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('70', 139, 1, 115, 0.1, 15, 15, 15, 215, 'ANALISE', 1, 4, null, 15, '3217',
        1200, null, false, 1270, 3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('65', 137, 2, 105, 115.39, 11.81, 12.2, 12.13, 215, 'ANALISE', 1, 4, null, 12.43, '', 1200, null, false, 1273,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('66', 137, 2, 105, 112.42, 8.92, 8.14, 9.28, 215, 'ANALISE', 1, 4, null, 8.17, '', 1200, null, false, 1274, 3,
        215, false, null, null);



INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('67', 137, 2, 105, 110.67, 12.3, 13.53, 13.33, 215, 'DESCARTE', 1, 4, null, 13.43, '', 1200, null, false, 1294,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('68', 137, 2, 105, 111.19, 12.26, 11.91, 13.66, 215, 'DESCARTE', 1, 4, null, 12.55, '', 1200, null, false, 1295,
        3, 215, false, null, null);
INSERT INTO public.pneu_script (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, pressao_atual,
                                altura_sulco_interno, altura_sulco_central_interno, altura_sulco_externo, cod_unidade,
                                status, vida_atual, vida_total, cod_modelo_banda, altura_sulco_central_externo, dot,
                                valor, data_hora_cadastro, pneu_novo_nunca_rodado, codigo, cod_empresa,
                                cod_unidade_cadastro, deletado, data_hora_deletado, pg_username_delecao)
VALUES ('69', 137, 2, 105, 106.99, 11.81, 11.81, 12.07, 215, 'DESCARTE', 1, 4, null, 12.3, '', 1200, null, false, 1296,
        3, 215, false, null, null);
SELECT setval('pneu_codigo_seq', 1296, true);

drop trigger TG_PNEU_SCRIPT_INSERT ON pneu_script;
drop function TG_FUNC_PNEU_SCRIPT_INSERT();
drop table pneu_script;