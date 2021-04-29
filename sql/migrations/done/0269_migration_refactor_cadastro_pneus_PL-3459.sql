-- #####################################################################################################################
-- Alteramos a tabela de serviço de cadastro para conter um código único.
alter table pneu_servico_cadastro_data
    add column codigo bigserial not null;

alter table pneu_servico_cadastro_data
    drop constraint pk_pneu_servico_cadastro;

alter table pneu_servico_cadastro_data
    add constraint unique_service_cadastro unique (cod_pneu, cod_servico_realizado);

alter table pneu_servico_cadastro_data
    add constraint pk_pneu_servico_cadastro primary key (codigo);

alter sequence pneu_servico_cadastro_data_codigo_seq
    rename to pneu_servico_cadastro_codigo_seq;

-- Alteramos a tabela de serviço incrementa vida para conter um código único.
alter table pneu_servico_realizado_incrementa_vida_data
    add column codigo bigserial not null;

alter table pneu_servico_realizado_incrementa_vida_data
    drop constraint if exists pk_pneu_servico_realizado_incrementa_vida;

alter table pneu_servico_realizado_incrementa_vida_data
    add constraint unique_servico_realizado_incrementa unique (cod_servico_realizado, fonte_servico_realizado);

alter table pneu_servico_realizado_incrementa_vida_data
    add constraint pk_pneu_servico_realizado_incrementa_vida
        primary key (codigo);

alter sequence pneu_servico_realizado_incrementa_vida_data_codigo_seq
    rename to pneu_servico_realizado_incrementa_vida_codigo_seq;

-- #####################################################################################################################
-- Adicionamos a nova coluna na tabela de pneus.
alter table pneu_data
    add column if not exists origem_cadastro text;

-- Atualizamos os pneus que foram cadastrados via API
update pneu_data
set origem_cadastro = 'API'
where codigo in (select pc.cod_pneu_cadastro_prolog from integracao.pneu_cadastrado pc);

-- Atualizamos os pneus cadastrados via INTERNO
with pneus_cadastrados_internamente as (
    select (vd.row_log ->> 'codigo')::bigint as cod_pneu
    from audit.pneu_data_audit vd
    where operacao = 'I'
      and pg_username != 'prolog_user'
)

update pneu_data
set origem_cadastro = 'INTERNO'
where codigo in (select * from pneus_cadastrados_internamente)
  and origem_cadastro is null;

-- O que não foi cadastrado por API e nem INTERNO, só pode ter sido via PROLOG_WEB
update pneu_data
set origem_cadastro = 'PROLOG_WEB'
where origem_cadastro is null;

alter table pneu_data
    alter column origem_cadastro set not null;

alter table pneu_data
    add constraint fk_origem_cadastro foreign key (origem_cadastro)
        references types.origem_acao_type (origem_acao);

create or replace view pneu as
select p.codigo_cliente,
       p.cod_modelo,
       p.cod_dimensao,
       p.pressao_recomendada,
       p.pressao_atual,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_externo,
       p.cod_unidade,
       p.status,
       p.vida_atual,
       p.vida_total,
       p.cod_modelo_banda,
       p.altura_sulco_central_externo,
       p.dot,
       p.valor,
       p.data_hora_cadastro,
       p.pneu_novo_nunca_rodado,
       p.codigo,
       p.cod_empresa,
       p.cod_unidade_cadastro,
       p.origem_cadastro
from pneu_data p
where p.deletado = false;

CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE BIGINT,
                                                    F_COD_UNIDADE_BASE BIGINT,
                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                    F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE PNEUS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MARCAS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MAB.CODIGO FROM MARCA_BANDA MAB WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MARCAS DE BANDAS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MODELOS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MOB.CODIGO FROM MODELO_BANDA MOB WHERE MOB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE BANDA PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM PNEUS PARA SEREM COPIADOS DA UNIDADE DE CÓDIGO: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE PNEUS.
    INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT MP.NOME,
           MP.COD_MARCA,
           F_COD_EMPRESA_USUARIO,
           MP.QT_SULCOS,
           MP.ALTURA_SULCOS
    FROM MODELO_PNEU MP
    WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE;

    -- COPIA AS MARCAS DE BANDAS.
    INSERT INTO MARCA_BANDA(NOME, COD_EMPRESA)
    SELECT MAB.NOME,
           F_COD_EMPRESA_USUARIO
    FROM MARCA_BANDA MAB
    WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT UNIQUE_MARCA_BANDA
        DO NOTHING;

    -- REALIZA O DE -> PARA DOS CÓDIGOS DE MARCAS DE BANDA E INSERE OS MODELOS
    WITH DADOS_MARCA_BANDA_DE_PARA AS (
        SELECT MABB.CODIGO       AS COD_MARCA_BANDA_BASE,
               MABB.NOME         AS NOME_MARCA_BANDA_BASE,
               MABN.CODIGO       AS COD_MARCA_BANDA_NOVO,
               MABN.CODIGO       AS NOME_MARCA_BANDA_NOVO,
               MOB.NOME          AS NOME_MODELO_BANDA_BASE,
               MOB.ALTURA_SULCOS AS ALTURA_SULCOS_BANDA_BASE,
               MOB.QT_SULCOS     AS QT_SULCOS_BANDA_BASE
        FROM MARCA_BANDA MABB
                 JOIN MARCA_BANDA MABN ON MABB.NOME = MABN.NOME
                 JOIN MODELO_BANDA MOB ON MABB.CODIGO = MOB.COD_MARCA AND MABB.COD_EMPRESA = MOB.COD_EMPRESA
        WHERE MABB.COD_EMPRESA = F_COD_EMPRESA_BASE
          AND MABN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- REALIZA A CLONAGEM DE MODELOS DE BANDA COM O CÓDIGO DAS MARCAS DE->PARA.
    INSERT
    INTO MODELO_BANDA(NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT DMBDP.NOME_MODELO_BANDA_BASE,
           DMBDP.COD_MARCA_BANDA_NOVO,
           F_COD_EMPRESA_USUARIO,
           DMBDP.QT_SULCOS_BANDA_BASE,
           DMBDP.ALTURA_SULCOS_BANDA_BASE
    FROM DADOS_MARCA_BANDA_DE_PARA DMBDP
    ON CONFLICT ON CONSTRAINT UNIQUE_NOME_MODELO_BANDA_POR_MARCA
        DO NOTHING;

    PERFORM SETVAL('PNEU_DATA_CODIGO_SEQ', (SELECT MAX(P.CODIGO + 1) FROM PNEU_DATA P));
    -- DADOS DE PARA
    WITH PNEUS_BASE AS (
        SELECT PD.CODIGO_CLIENTE               AS NUMERO_FOGO_BASE,
               PD.COD_MODELO                   AS COD_MODELO_PNEU_BASE,
               PD.COD_DIMENSAO                 AS COD_DIMENSAO_BASE,
               PD.PRESSAO_RECOMENDADA          AS PRESSAO_RECOMENDADA_BASE,
               PD.PRESSAO_ATUAL                AS PRESSAO_ATUAL_BASE,
               PD.ALTURA_SULCO_INTERNO         AS ALTURA_SULCO_INTERNO_BASE,
               PD.ALTURA_SULCO_CENTRAL_INTERNO AS ALTURA_SULCO_CENTRAL_INTERNO_BASE,
               PD.ALTURA_SULCO_EXTERNO         AS ALTURA_SULCO_EXTERNO_BASE,
               PD.STATUS                       AS STATUS_BASE,
               PD.VIDA_ATUAL                   AS VIDA_ATUAL_BASE,
               PD.VIDA_TOTAL                   AS VIDA_TOTAL_BASE,
               PD.COD_MODELO_BANDA             AS COD_MODELO_BANDA_BASE,
               PD.ALTURA_SULCO_CENTRAL_EXTERNO AS ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
               PD.DOT                          AS DOT_BASE,
               PD.VALOR                        AS VALOR_BASE,
               PD.ORIGEM_CADASTRO              AS ORIGEM_CADASTRO
        FROM PNEU PD
        WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
    ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PB.NUMERO_FOGO_BASE) PB.NUMERO_FOGO_BASE,
                                                      PB.COD_MODELO_PNEU_BASE,
                                                      MPB.CODIGO  AS COD_MODELO_PNEU_BASE,
                                                      MPN.CODIGO  AS COD_MODELO_PNEU_NOVO,
                                                      PB.COD_DIMENSAO_BASE,
                                                      PB.PRESSAO_RECOMENDADA_BASE,
                                                      PB.PRESSAO_ATUAL_BASE,
                                                      PB.ALTURA_SULCO_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_EXTERNO_BASE,
                                                      PB.STATUS_BASE,
                                                      PB.VIDA_ATUAL_BASE,
                                                      PB.VIDA_TOTAL_BASE,
                                                      PB.COD_MODELO_BANDA_BASE,
                                                      MABB.NOME   AS NOME_MARCA_BANDA_BASE,
                                                      MABN.NOME   AS NOME_MARCA_BANSA_NOVA,
                                                      MABB.CODIGO AS COD_MARCA_BANDA_BASE,
                                                      MABN.CODIGO AS COD_MARCA_BANDA_NOVA,
                                                      MOBB.CODIGO AS COD_MODELO_BANDA_BASE,
                                                      MOBN.CODIGO AS COD_MODELO_BANDA_NOVO,
                                                      PB.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
                                                      PB.DOT_BASE,
                                                      PB.VALOR_BASE,
                                                      PB.ORIGEM_CADASTRO
             FROM PNEUS_BASE PB
                      JOIN MODELO_PNEU MPB
                           ON MPB.CODIGO = PB.COD_MODELO_PNEU_BASE AND MPB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      JOIN MODELO_PNEU MPN
                           ON MPB.NOME = MPN.NOME AND MPB.COD_MARCA = MPN.COD_MARCA AND
                              MPN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MODELO_BANDA MOBB
                                ON PB.COD_MODELO_BANDA_BASE = MOBB.CODIGO AND MOBB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      LEFT JOIN MODELO_BANDA MOBN ON MOBB.NOME = MOBN.NOME AND MOBN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MARCA_BANDA MABB ON MABB.CODIGO = MOBB.COD_MARCA
                      LEFT JOIN MARCA_BANDA MABN ON MABN.CODIGO = MOBN.COD_MARCA AND MABB.NOME = MABN.NOME)

         -- REALIZA A CLONAGEM DE PNEUS
    INSERT
    INTO PNEU_DATA (CODIGO_CLIENTE,
                    COD_MODELO,
                    COD_DIMENSAO,
                    PRESSAO_RECOMENDADA,
                    PRESSAO_ATUAL,
                    ALTURA_SULCO_INTERNO,
                    ALTURA_SULCO_CENTRAL_INTERNO,
                    ALTURA_SULCO_EXTERNO,
                    COD_UNIDADE,
                    STATUS,
                    VIDA_ATUAL,
                    VIDA_TOTAL,
                    COD_MODELO_BANDA,
                    ALTURA_SULCO_CENTRAL_EXTERNO,
                    DOT,
                    VALOR,
                    COD_EMPRESA,
                    COD_UNIDADE_CADASTRO,
                    ORIGEM_CADASTRO)
    SELECT DDP.NUMERO_FOGO_BASE,
           DDP.COD_MODELO_PNEU_NOVO,
           DDP.COD_DIMENSAO_BASE,
           DDP.PRESSAO_RECOMENDADA_BASE,
           DDP.PRESSAO_ATUAL_BASE,
           DDP.ALTURA_SULCO_INTERNO_BASE,
           DDP.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
           DDP.ALTURA_SULCO_EXTERNO_BASE,
           F_COD_UNIDADE_USUARIO,
           DDP.STATUS_BASE,
           DDP.VIDA_ATUAL_BASE,
           DDP.VIDA_TOTAL_BASE,
           DDP.COD_MODELO_BANDA_NOVO,
           DDP.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
           DDP.DOT_BASE,
           DDP.VALOR_BASE,
           F_COD_EMPRESA_USUARIO,
           F_COD_UNIDADE_USUARIO,
           DDP.ORIGEM_CADASTRO
    FROM DADOS_DE_PARA DDP;
END;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(F_COD_PNEU_SISTEMA_INTEGRADO BIGINT,
                                                                   F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                                   F_COD_UNIDADE_PNEU BIGINT,
                                                                   F_COD_MODELO_PNEU BIGINT,
                                                                   F_COD_DIMENSAO_PNEU BIGINT,
                                                                   F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                                   F_VIDA_ATUAL_PNEU INTEGER,
                                                                   F_VIDA_TOTAL_PNEU INTEGER,
                                                                   F_DOT_PNEU CHARACTER VARYING,
                                                                   F_VALOR_PNEU NUMERIC,
                                                                   F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                                   F_COD_MODELO_BANDA_PNEU BIGINT,
                                                                   F_VALOR_BANDA_PNEU NUMERIC,
                                                                   F_DATA_HORA_PNEU_CADASTRO TIMESTAMP WITH TIME ZONE,
                                                                   F_TOKEN_INTEGRACAO CHARACTER VARYING,
                                                                   F_DEVE_SOBRESCREVER_PNEU BOOLEAN DEFAULT FALSE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PNEU_ORIGEM_CADASTRO CONSTANT TEXT    := 'API';
    PNEU_PRIMEIRA_VIDA   CONSTANT BIGINT  := 1;
    PNEU_STATUS_ESTOQUE  CONSTANT TEXT    := 'ESTOQUE';
    PNEU_POSSUI_BANDA    CONSTANT BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    COD_EMPRESA_PNEU     CONSTANT BIGINT  := (SELECT U.COD_EMPRESA
                                              FROM PUBLIC.UNIDADE U
                                              WHERE U.CODIGO = F_COD_UNIDADE_PNEU);
    PNEU_ESTA_NO_PROLOG  CONSTANT BOOLEAN := (SELECT EXISTS(SELECT P.CODIGO
                                                            FROM PUBLIC.PNEU P
                                                            WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
                                                              AND P.COD_EMPRESA = COD_EMPRESA_PNEU));
    COD_PNEU_PROLOG               BIGINT;
    F_QTD_ROWS_AFETADAS           BIGINT;
BEGIN
    PERFORM INTEGRACAO.FUNC_GARANTE_TOKEN_EMPRESA(COD_EMPRESA_PNEU, F_TOKEN_INTEGRACAO);

    -- Validamos se a Empresa existe.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_PNEU,
                                        FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

    -- Validamos se a Unidade repassada existe.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU,
                                        FORMAT('A Unidade %s repassada não existe no Sistema ProLog',
                                               F_COD_UNIDADE_PNEU));

    -- Validamos se a Unidade pertence a Empresa do token repassado.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(COD_EMPRESA_PNEU,
                                                F_COD_UNIDADE_PNEU,
                                                FORMAT('A Unidade %s não está configurada para esta empresa',
                                                       F_COD_UNIDADE_PNEU));

    -- Validamos se o modelo do pneu está mapeado.
    IF (SELECT NOT EXISTS(SELECT MP.CODIGO
                          FROM PUBLIC.MODELO_PNEU MP
                          WHERE MP.COD_EMPRESA = COD_EMPRESA_PNEU
                            AND MP.CODIGO = F_COD_MODELO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo do pneu %s não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_PNEU));
    END IF;

    -- Validamos se a dimensão do pneu está mapeada.
    IF (SELECT NOT EXISTS(SELECT DP.CODIGO
                          FROM PUBLIC.DIMENSAO_PNEU DP
                          WHERE DP.CODIGO = F_COD_DIMENSAO_PNEU))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A dimensão de código %s do pneu não está mapeada no Sistema ProLog',
                                                  F_COD_DIMENSAO_PNEU));
    END IF;

    -- Validamos se a pressão recomendada é válida.
    IF (F_PRESSAO_CORRETA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A pressão recomendada para o pneu não pode ser um número negativo');
    END IF;

    -- Validamos se a vida atual é correta.
    IF (F_VIDA_ATUAL_PNEU < PNEU_PRIMEIRA_VIDA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida atual do pneu deve ser no mínimo 1 (caso novo)');
    END IF;

    -- Validamos se a vida total é válida.
    IF (F_VIDA_TOTAL_PNEU < F_VIDA_ATUAL_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('A vida total do pneu não pode ser menor que a vida atual');
    END IF;

    -- Validamos se o valor do pneu é um valor válido.
    IF (F_VALOR_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do modelo de banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_COD_MODELO_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT(
                'O pneu %s não está na primeira vida, deve ser informado um modelo de banda',
                F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Validamos se o código do modelo da banda é válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND (SELECT NOT EXISTS(SELECT MB.CODIGO
                                                 FROM PUBLIC.MODELO_BANDA MB
                                                 WHERE MB.COD_EMPRESA = COD_EMPRESA_PNEU
                                                   AND MB.CODIGO = F_COD_MODELO_BANDA_PNEU)))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O modelo da banda %s do pneu não está mapeado no Sistema ProLog',
                                                  F_COD_MODELO_BANDA_PNEU));
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU IS NULL)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                'O pneu não está na primeira vida, deve ser informado o valor da banda aplicada');
    END IF;

    -- Validamos se o valor da banda é um valor válido. Apenas validamos se o pneu possuir banda.
    IF (PNEU_POSSUI_BANDA AND F_VALOR_BANDA_PNEU < 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR('O valor da banda do pneu não pode ser um número negativo');
    END IF;

    -- Validamos se o código do sistema integrado já está mapeado na tabela, apenas se não estiver devemos sobrescrever.
    -- Pode acontecer o caso onde o pneu está na base do ProLog e é rodado a sobrecarga. Neste cenário o pneu deve
    -- apenas ter as informações sobrescritas e a tabela de vínculo atualizada.
    IF (SELECT EXISTS(SELECT PC.COD_PNEU_CADASTRO_PROLOG
                      FROM INTEGRACAO.PNEU_CADASTRADO PC
                      WHERE PC.COD_PNEU_SISTEMA_INTEGRADO = F_COD_PNEU_SISTEMA_INTEGRADO
                        AND PC.COD_EMPRESA_CADASTRO = COD_EMPRESA_PNEU) AND NOT F_DEVE_SOBRESCREVER_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('O pneu de código interno %s já está cadastrado no Sistema ProLog',
                                                  F_COD_PNEU_SISTEMA_INTEGRADO));
    END IF;

    -- Já validamos se o pneu existe no ProLog através código do sistema integrado, então sobrescrevemos as
    -- informações dele ou, caso não deva sobrescrever, inserimos no base. Validamos também se o pneu já está na base
    -- do ProLog, caso ele não esteja, deveremos inserir e não sobrescrever.
    IF (PNEU_ESTA_NO_PROLOG AND F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Pegamos o código do pneu que iremos sobrescrever.
        SELECT P.CODIGO
        FROM PUBLIC.PNEU P
        WHERE P.CODIGO_CLIENTE = F_CODIGO_PNEU_CLIENTE
          AND P.COD_EMPRESA = COD_EMPRESA_PNEU
        INTO COD_PNEU_PROLOG;

        -- Sebrescrevemos os dados do pneu.
        PERFORM INTEGRACAO.FUNC_PNEU_SOBRESCREVE_PNEU_CADASTRADO(COD_PNEU_PROLOG,
                                                                 F_COD_UNIDADE_PNEU,
                                                                 F_COD_MODELO_PNEU,
                                                                 F_COD_DIMENSAO_PNEU,
                                                                 F_PRESSAO_CORRETA_PNEU,
                                                                 F_VIDA_ATUAL_PNEU,
                                                                 F_VIDA_TOTAL_PNEU,
                                                                 F_DOT_PNEU,
                                                                 F_VALOR_PNEU,
                                                                 F_PNEU_NOVO_NUNCA_RODADO,
                                                                 F_COD_MODELO_BANDA_PNEU,
                                                                 F_VALOR_BANDA_PNEU,
                                                                 F_DATA_HORA_PNEU_CADASTRO);
    ELSEIF (NOT PNEU_ESTA_NO_PROLOG)
    THEN
        -- Deveremos inserir os dados na base.
        INSERT INTO PUBLIC.PNEU(COD_EMPRESA,
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
                                DATA_HORA_CADASTRO,
                                ORIGEM_CADASTRO)
        VALUES (COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_COD_MODELO_PNEU,
                F_COD_DIMENSAO_PNEU,
                F_PRESSAO_CORRETA_PNEU,
                0, -- PRESSAO_ATUAL
                NULL, -- ALTURA_SULCO_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_INTERNO
                NULL, -- ALTURA_SULCO_CENTRAL_EXTERNO
                NULL, -- ALTURA_SULCO_EXTERNO
                PNEU_STATUS_ESTOQUE,
                F_VIDA_ATUAL_PNEU,
                F_VIDA_TOTAL_PNEU,
                F_DOT_PNEU,
                F_VALOR_PNEU,
                F_IF(PNEU_POSSUI_BANDA, F_COD_MODELO_BANDA_PNEU, NULL),
                   -- Forçamos FALSE caso o pneu já possua uma banda aplicada.
                F_IF(PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO),
                F_DATA_HORA_PNEU_CADASTRO,
                PNEU_ORIGEM_CADASTRO)
        RETURNING CODIGO INTO COD_PNEU_PROLOG;

        -- Precisamos criar um serviço de incremento de vida para o pneu cadastrado já possuíndo uma banda.
        IF (PNEU_POSSUI_BANDA)
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               COD_PNEU_PROLOG,
                                                               F_COD_MODELO_BANDA_PNEU,
                                                               F_VALOR_BANDA_PNEU,
                                                               F_VIDA_ATUAL_PNEU);
        END IF;
    ELSE
        -- Pneu está no ProLog e não deve sobrescrever.
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                FORMAT('O pneu %s já está cadastrado no Sistema ProLog', F_CODIGO_PNEU_CLIENTE));
    END IF;

    IF (F_DEVE_SOBRESCREVER_PNEU)
    THEN
        -- Se houve uma sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o pneu já encontra-se no ProLog,
        -- não temos nenhuma entrada para ele na tabela de mapeamento.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO)
        ON CONFLICT ON CONSTRAINT UNIQUE_PNEU_CADASTRADO_EMPRESA_INTEGRACAO
            DO UPDATE SET COD_PNEU_SISTEMA_INTEGRADO  = F_COD_PNEU_SISTEMA_INTEGRADO,
                          COD_UNIDADE_CADASTRO        = F_COD_UNIDADE_PNEU,
                          COD_CLIENTE_PNEU_CADASTRO   = F_CODIGO_PNEU_CLIENTE,
                          TOKEN_AUTENTICACAO_CADASTRO = F_TOKEN_INTEGRACAO,
                          DATA_HORA_ULTIMA_EDICAO     = F_DATA_HORA_PNEU_CADASTRO;
    ELSE
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento e
        -- deixar um erro estourar caso pneu já exista.
        INSERT INTO INTEGRACAO.PNEU_CADASTRADO(COD_PNEU_CADASTRO_PROLOG,
                                               COD_PNEU_SISTEMA_INTEGRADO,
                                               COD_EMPRESA_CADASTRO,
                                               COD_UNIDADE_CADASTRO,
                                               COD_CLIENTE_PNEU_CADASTRO,
                                               TOKEN_AUTENTICACAO_CADASTRO,
                                               DATA_HORA_CADASTRO_PROLOG)
        VALUES (COD_PNEU_PROLOG,
                F_COD_PNEU_SISTEMA_INTEGRADO,
                COD_EMPRESA_PNEU,
                F_COD_UNIDADE_PNEU,
                F_CODIGO_PNEU_CLIENTE,
                F_TOKEN_INTEGRACAO,
                F_DATA_HORA_PNEU_CADASTRO);
    END IF;

    GET DIAGNOSTICS F_QTD_ROWS_AFETADAS = ROW_COUNT;

    -- Verificamos se a inserção na tabela de mapeamento ocorreu com sucesso.
    IF (F_QTD_ROWS_AFETADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível inserir o pneu "%" na tabela de mapeamento', F_CODIGO_PNEU_CLIENTE;
    END IF;

    RETURN COD_PNEU_PROLOG;
END;
$$;

CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_PNEU_CONFERE_PLANILHA_IMPORTA_PNEU()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_ORIGEM_PNEU_CADASTRO        CONSTANT     TEXT     := 'INTERNO';
    F_VALOR_SIMILARIDADE          CONSTANT     REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIMENSAO CONSTANT     REAL     := 0.55;
    F_SEM_SIMILARIDADE            CONSTANT     REAL     := 0.0;
    F_QTD_ERROS                                SMALLINT := 0;
    F_MSGS_ERROS                               TEXT;
    F_QUEBRA_LINHA                             TEXT     := CHR(10);
    F_COD_MARCA_BANCO                          BIGINT;
    F_SIMILARIDADE_MARCA                       REAL;
    F_MARCA_MODELO                             TEXT;
    F_COD_MODELO_BANCO                         BIGINT;
    F_SIMILARIDADE_MODELO                      REAL;
    F_COD_MARCA_BANDA_BANCO                    BIGINT;
    F_SIMILARIDADE_MARCA_BANDA                 REAL;
    F_MARCA_MODELO_BANDA                       TEXT;
    F_COD_MODELO_BANDA_BANCO                   BIGINT;
    F_SIMILARIDADE_MODELO_BANDA                REAL;
    DATE_CONVERTER                             TEXT     := 'YYYYWW';
    PREFIXO_ANO                                TEXT     := SUBSTRING(CURRENT_TIMESTAMP::TEXT, 1, 2);
    DOT_EM_DATA                                DATE;
    F_COD_DIMENSAO                             BIGINT;
    F_SIMILARIDADE_DIMENSAO                    REAL;
    F_ALTURA_MIN_SULCOS                        REAL     := 1;
    F_ALTURA_MAX_SULCOS                        REAL     := 50;
    F_QTD_MIN_SULCOS                           SMALLINT := 1;
    F_QTD_MAX_SULCOS                           SMALLINT := 6;
    F_QTD_SULCOS_DEFAULT                       SMALLINT := 4;
    F_ERRO_SULCOS                              BOOLEAN  := FALSE;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO      REAL     := 0;
    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP REAL;
    F_PNEU_NOVO_NUNCA_RODADO_ARRAY             TEXT[]   := ('{"SIM", "OK", "TRUE"}');
    F_PNEU_NOVO_NUNCA_RODADO                   TEXT;
    F_COD_TIPO_SERVICO                         BIGINT;
    F_COD_SERVICO_REALIZADO                    BIGINT;
    F_COD_PNEU                                 BIGINT;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE = OLD.COD_UNIDADE;
            NEW.COD_EMPRESA = OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NUMERO_FOGO_FORMATADO_IMPORT = UPPER(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.NUMERO_FOGO_EDITAVEL));
        NEW.MARCA_FORMATADA_IMPORT = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.DOT_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.DOT_EDITAVEL);
        NEW.DIMENSAO_FORMATADA_IMPORT := REMOVE_ALL_SPACES(NEW.DIMENSAO_EDITAVEL);
        NEW.MARCA_BANDA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_BANDA_EDITAVEL);
        NEW.MODELO_BANDA_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_BANDA_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS =
                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES NÚMERO DE FOGO.
        -- Número de fogo nulo: Erro.
        -- Número de fogo cadastrado em outra unidade da mesma empresa: Erro.
        -- Número de fogo cadastrado na mesma unidade: Erro.
        IF (NEW.NUMERO_FOGO_FORMATADO_IMPORT IS NOT NULL)
        THEN
            IF EXISTS(SELECT P.CODIGO_CLIENTE
                      FROM PNEU P
                      WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                            NEW.NUMERO_FOGO_FORMATADO_IMPORT
                        AND P.COD_EMPRESA = NEW.COD_EMPRESA
                        AND P.COD_UNIDADE != NEW.COD_UNIDADE)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O PNEU JÁ ESTÁ CADASTRADO E PERTENCE A OUTRA UNIDADE',
                                      F_QUEBRA_LINHA);
                NEW.STATUS_IMPORT_REALIZADO = TRUE;
            ELSE
                IF EXISTS(SELECT P.CODIGO_CLIENTE
                          FROM PNEU P
                          WHERE REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(P.CODIGO_CLIENTE) ILIKE
                                NEW.NUMERO_FOGO_FORMATADO_IMPORT
                            AND P.COD_EMPRESA = NEW.COD_EMPRESA
                            AND P.COD_UNIDADE = NEW.COD_UNIDADE)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O PNEU JÁ ESTÁ CADASTRADO NA UNIDADE INFORMADA',
                                          F_QUEBRA_LINHA);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O NÚMERO DE FOGO NÃO PODE SER NULO', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES MARCA.
        -- Marca nula: Erro.
        IF (NEW.MARCA_FORMATADA_IMPORT IS NOT NULL)
        THEN
            -- Procura marca similar no banco.
            SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAP.CODIGO                                                        AS COD_MARCA_BANCO,
                                                            MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAP.NOME)) AS SIMILARIEDADE_MARCA
            INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
            FROM MARCA_PNEU MAP
            GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAP.NOME, MAP.CODIGO
            ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

            F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
            -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
            -- Se não for: Mostra erro de marca não encontrada (Não cadastra pois é nível Prolog).
            IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
            THEN
                -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
                IF (NEW.MODELO_FORMATADO_IMPORT IS NOT NULL)
                THEN
                    SELECT DISTINCT ON (F_MARCA_MODELO) MOP.CODIGO AS COD_MODELO_PNEU,
                                                        CASE
                                                            WHEN F_COD_MARCA_BANCO = MOP.COD_MARCA
                                                                THEN
                                                                MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                           CONCAT(MOP.COD_MARCA, MOP.NOME)))
                                                            ELSE F_SEM_SIMILARIDADE
                                                            END    AS SIMILARIEDADE_MODELO
                    INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
                    FROM MODELO_PNEU MOP
                    WHERE MOP.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO, MOP.NOME, MOP.CODIGO
                    ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;

                    -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.
                    IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO IS NULL)
                    THEN
                        BEGIN
                            -- VERIFICAÇÃO DE SULCOS.
                            -- Parse para smallint.
                            NEW.QTD_SULCOS_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O ' ||
                                                      'VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS NÃO PODE SER MAIOR QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS NÃO PODE SER MAIOR QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS, '- A ALTURA DOS SULCOS NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);

                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_FORMATADA_IMPORT)
                            RETURNING CODIGO INTO F_COD_MODELO_BANCO;
                        END IF;
                    END IF;
                ELSE
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE PNEU NÃO PODE SER NULO', F_QUEBRA_LINHA);
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DOT
        IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) > 4)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT DEVE POSSUIR NO MÁXIMO 4 DÍGITOS',
                                  F_QUEBRA_LINHA);

        ELSE
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) < 4)
            THEN
                NEW.DOT_FORMATADO_IMPORT = LPAD(NEW.DOT_FORMATADO_IMPORT, 4, '0');
            END IF;
            IF (CHAR_LENGTH(NEW.DOT_FORMATADO_IMPORT) = 4)
            THEN
                BEGIN
                    -- Transforma o DOT_FORMATADO em data
                    DOT_EM_DATA := TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 3, 4)),
                                                  (SUBSTRING(NEW.DOT_FORMATADO_IMPORT, 1, 2))),
                                           DATE_CONVERTER);
                    -- Verifica se a data do DOT que foi transformado é maior que a data atual, se for está errado.
                    IF (DOT_EM_DATA > CURRENT_DATE)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DOT NÃO PODE SER MAIOR QUE A DATA ATUAL',
                                       F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_datetime_format THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS,
                                       '- DOT COM CARACTERES INCORRETOS - DEVE POSSUIR APENAS NÚMEROS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        -- VERIFICAÇÕES DIMENSÃO
        IF (NEW.DIMENSAO_FORMATADA_IMPORT IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            SELECT DISTINCT ON (NEW.DIMENSAO_FORMATADA_IMPORT) DP.CODIGO                                                                    AS COD_DIMENSAO_BANCO,
                                                               MAX(func_gera_similaridade(NEW.DIMENSAO_FORMATADA_IMPORT,
                                                                                          CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO))) AS SIMILARIDADE_DIMENSAO
            INTO F_COD_DIMENSAO, F_SIMILARIDADE_DIMENSAO
            FROM DIMENSAO_PNEU DP
            GROUP BY NEW.DIMENSAO_FORMATADA_IMPORT, NEW.DIMENSAO_EDITAVEL,
                     CONCAT(DP.LARGURA, '/', DP.ALTURA, 'R', DP.ARO),
                     DP.CODIGO
            ORDER BY NEW.DIMENSAO_FORMATADA_IMPORT, SIMILARIDADE_DIMENSAO DESC;

            IF (F_SIMILARIDADE_DIMENSAO < F_VALOR_SIMILARIDADE_DIMENSAO)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A DIMENSÃO NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
            END IF;
        END IF;

        -- VERIFICAÇÕES PRESSÃO IDEAL
        IF (NEW.PRESSAO_RECOMENDADA_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PRESSÃO RECOMENDADA NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.PRESSAO_RECOMENDADA_EDITAVEL), ',', '.')::REAL;
                IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A PRESSÃO RECOMENDADA NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT > 150)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PRESSÃO RECOMENDADA NÃO PODE SER MAIOR QUE 150',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- PRESSÃO IDEAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VALOR PNEU
        IF (NEW.VALOR_PNEU_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NULO',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                NEW.VALOR_PNEU_FORMATADO_IMPORT :=
                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                        NEW.VALOR_PNEU_EDITAVEL), ',', '.')::REAL;
                IF (NEW.VALOR_PNEU_FORMATADO_IMPORT < 0)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DO PNEU NÃO PODE SER NEGATIVO',
                                          F_QUEBRA_LINHA);
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA TOTAL.
        IF (NEW.VIDA_TOTAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA TOTAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_TOTAL_FORMATADA_IMPORT.
                -- Acrescentado +1 à vida_total devido ao prolog considerar que a vida_atual do pneu novo é = 1, não 0.
                NEW.VIDA_TOTAL_FORMATADA_IMPORT := (NEW.VIDA_TOTAL_EDITAVEL :: INTEGER + 1);
                IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT < 1)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA TOTAL DO PNEU NÃO PODE SER NEGATIVA',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_TOTAL_FORMATADA_IMPORT > 10)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA TOTAL DO PNEU NÃO PODE SER MAIOR QUE 10',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA TOTAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        -- VERIFICAÇÕES VIDA ATUAL
        IF (NEW.VIDA_ATUAL_EDITAVEL IS NULL)
        THEN
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A VIDA ATUAL DO PNEU NÃO PODE SER NULA',
                                  F_QUEBRA_LINHA);
        ELSE
            BEGIN
                -- ACRESCENTA +1 NA VIDA_ATUAL_FORMATADA_IMPORT.
                -- É incrementado +1 à vida_atual devido ao prolog considerar que a vida do pneu novo é = 1 e não 0.
                NEW.VIDA_ATUAL_FORMATADA_IMPORT := (NEW.VIDA_ATUAL_EDITAVEL :: INTEGER + 1);

                --VIDA_ATUAL FOR MAIOR QUE A VIDA TOTAL: Erro.
                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > NEW.VIDA_TOTAL_FORMATADA_IMPORT)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- A VIDA ATUAL NÃO PODE SER MAIOR QUE A VIDA TOTAL',
                                          F_QUEBRA_LINHA);
                ELSE
                    IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT < 1)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A VIDA ATUAL DO PNEU NÃO PODE SER NEGATIVA',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;

                IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT = 1)
                THEN
                    IF (NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL IS NOT NULL)
                    THEN
                        FOREACH F_PNEU_NOVO_NUNCA_RODADO IN ARRAY F_PNEU_NOVO_NUNCA_RODADO_ARRAY
                            LOOP
                                F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP :=
                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.PNEU_NOVO_NUNCA_RODADO_EDITAVEL,
                                                                   F_PNEU_NOVO_NUNCA_RODADO));
                                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP >
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO)
                                THEN
                                    F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO := F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO_LOOP;
                                END IF;
                            END LOOP;
                    END IF;
                END IF;
                IF (F_SIMILARIDADE_PNEU_NOVO_NUNCA_RODADO >= F_VALOR_SIMILARIDADE)
                THEN
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := TRUE;
                ELSE
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT := FALSE;
                END IF;
            EXCEPTION
                WHEN invalid_text_representation THEN -- error that can be handled
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- VIDA ATUAL COM CARACTERES INCORRETOS',
                                   F_QUEBRA_LINHA);
            END;
        END IF;

        --VERIFICAÇÕES BANDA
        IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT IS NOT NULL AND NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
        THEN
            IF (NEW.MARCA_BANDA_FORMATADA_IMPORT IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- A MARCA DE BANDA NÃO PODE SER NULA PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                -- VERIFICAÇÕES MARCA DE BANDA: Procura marca de banda similar no banco.
                SELECT DISTINCT ON (NEW.MARCA_BANDA_FORMATADA_IMPORT) MAB.CODIGO                                                                  AS COD_MARCA_BANDA_BANCO,
                                                                      MAX(
                                                                              FUNC_GERA_SIMILARIDADE(NEW.MARCA_BANDA_FORMATADA_IMPORT, MAB.NOME)) AS SIMILARIEDADE_MARCA_BANDA
                INTO F_COD_MARCA_BANDA_BANCO, F_SIMILARIDADE_MARCA_BANDA
                FROM MARCA_BANDA MAB
                WHERE MAB.COD_EMPRESA = NEW.COD_EMPRESA
                GROUP BY NEW.MARCA_BANDA_FORMATADA_IMPORT, NEW.MARCA_BANDA_EDITAVEL, MAB.NOME, MAB.CODIGO
                ORDER BY NEW.MARCA_BANDA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA_BANDA DESC;

                F_MARCA_MODELO_BANDA := CONCAT(F_COD_MARCA_BANDA_BANCO, NEW.MODELO_BANDA_FORMATADO_IMPORT);
                -- Se a similaridade da marca de banda for menor que o exigido: Cadastra.
                IF (F_SIMILARIDADE_MARCA_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MARCA_BANDA IS NULL)
                THEN
                    INSERT INTO MARCA_BANDA (NOME, COD_EMPRESA)
                    VALUES (NEW.MARCA_BANDA_EDITAVEL, NEW.COD_EMPRESA)
                    RETURNING CODIGO INTO F_COD_MARCA_BANDA_BANCO;
                END IF;

                IF (NEW.MODELO_BANDA_FORMATADO_IMPORT IS NULL)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                          '- O MODELO DE BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                          F_QUEBRA_LINHA);
                ELSE
                    -- VERIFICAÇÕES MODELO DE BANDA: Procura modelo SIMILAR NO banco.
                    SELECT DISTINCT ON (F_MARCA_MODELO_BANDA) MOB.CODIGO AS COD_MODELO_BANDA,
                                                              CASE
                                                                  WHEN F_COD_MARCA_BANDA_BANCO = MOB.COD_MARCA
                                                                      THEN
                                                                      MAX(FUNC_GERA_SIMILARIDADE(
                                                                              F_MARCA_MODELO_BANDA,
                                                                              CONCAT(MOB.COD_MARCA, MOB.NOME)))
                                                                  ELSE F_SEM_SIMILARIDADE
                                                                  END    AS SIMILARIEDADE_MODELO_BANDA
                    INTO F_COD_MODELO_BANDA_BANCO, F_SIMILARIDADE_MODELO_BANDA
                    FROM MODELO_BANDA MOB
                    WHERE MOB.COD_EMPRESA = NEW.COD_EMPRESA
                    GROUP BY F_MARCA_MODELO_BANDA, MOB.NOME, MOB.CODIGO
                    ORDER BY F_MARCA_MODELO_BANDA, SIMILARIEDADE_MODELO_BANDA DESC;

                    -- Se a similaridade do modelo de banda for menor do que o exigido: cadastra novo modelo de banda.
                    IF (F_SIMILARIDADE_MODELO_BANDA < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO_BANDA IS NULL)
                    THEN
                        BEGIN
                            NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT :=
                                    REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                    NEW.QTD_SULCOS_BANDA_EDITAVEL), ',', '.')::SMALLINT;
                            IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT IS NULL)
                            THEN
                                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                      '- A QUANTIDADE DE SULCOS ESTAVA NULA, PORTANTO ASSUMIU O' ||
                                                      ' VALOR DEFAULT = 4',
                                                      F_QUEBRA_LINHA);
                                NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT := F_QTD_SULCOS_DEFAULT;
                            ELSE
                                IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT < F_QTD_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MENOR QUE 1',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT > F_QTD_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A QUANTIDADE DE SULCOS DE BANDA NÃO PODE SER MAIOR' ||
                                                              ' QUE 6',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            END IF;
                        EXCEPTION
                            WHEN invalid_text_representation THEN -- error that can be handled
                                F_ERRO_SULCOS := TRUE;
                                F_QTD_ERROS = F_QTD_ERROS + 1;
                                F_MSGS_ERROS =
                                        concat(F_MSGS_ERROS, F_QTD_ERROS,
                                               '- QUANTIDADE DE SULCOS DE BANDA COM VALOR INCORRETO',
                                               F_QUEBRA_LINHA);
                        END;
                        IF (NEW.ALTURA_SULCOS_BANDA_EDITAVEL IS NOT NULL)
                        THEN
                            BEGIN
                                NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT :=
                                        REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                                        NEW.ALTURA_SULCOS_BANDA_EDITAVEL), ',', '.')::REAL;
                                IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT < F_ALTURA_MIN_SULCOS)
                                THEN
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                          '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MENOR QUE 1mm',
                                                          F_QUEBRA_LINHA);
                                ELSE
                                    IF (NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT > F_ALTURA_MAX_SULCOS)
                                    THEN
                                        F_ERRO_SULCOS := TRUE;
                                        F_QTD_ERROS = F_QTD_ERROS + 1;
                                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                              '- A ALTURA DOS SULCOS DA BANDA NÃO PODE SER MAIOR ' ||
                                                              'QUE 50mm',
                                                              F_QUEBRA_LINHA);
                                    END IF;
                                END IF;
                            EXCEPTION
                                WHEN invalid_text_representation THEN -- error that can be handled
                                    F_ERRO_SULCOS := TRUE;
                                    F_QTD_ERROS = F_QTD_ERROS + 1;
                                    F_MSGS_ERROS =
                                            concat(F_MSGS_ERROS, F_QTD_ERROS,
                                                   '- ALTURA DOS SULCOS DE BANDA COM VALOR INCORRETO',
                                                   F_QUEBRA_LINHA);
                            END;
                        ELSE
                            F_ERRO_SULCOS := TRUE;
                            F_QTD_ERROS = F_QTD_ERROS + 1;
                            F_MSGS_ERROS =
                                    concat(F_MSGS_ERROS, F_QTD_ERROS,
                                           '- A ALTURA DOS SULCOS DE BANDA NÃO PODE SER NULA',
                                           F_QUEBRA_LINHA);
                        END IF;
                        IF (F_ERRO_SULCOS = FALSE)
                        THEN
                            INSERT INTO MODELO_BANDA (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
                            VALUES (NEW.MODELO_BANDA_EDITAVEL, F_COD_MARCA_BANDA_BANCO, NEW.COD_EMPRESA,
                                    NEW.QTD_SULCOS_BANDA_FORMATADA_IMPORT,
                                    NEW.ALTURA_SULCOS_BANDA_FORMATADA_IMPORT)
                            RETURNING CODIGO INTO F_COD_MODELO_BANDA_BANCO;
                        END IF;
                    END IF;
                END IF;
                --ELSE MARCA DE BANDA
            END IF;

            --VERIFICAÇÕES VALOR DE BANDA.
            IF (NEW.VALOR_BANDA_EDITAVEL IS NULL)
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                      '- O VALOR DA BANDA NÃO PODE SER NULO PARA PNEUS ACIMA DA PRIMEIRA VIDA',
                                      F_QUEBRA_LINHA);
            ELSE
                BEGIN
                    NEW.VALOR_BANDA_FORMATADO_IMPORT :=
                            REPLACE(REMOVE_NON_NUMERIC_CHARACTERS_EXCEPT_COMMA_PERIOD(
                                            NEW.VALOR_BANDA_EDITAVEL), ',', '.')::REAL;
                    IF (NEW.VALOR_BANDA_FORMATADO_IMPORT < 0)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- O VALOR DA BANDA NÃO PODE SER NEGATIVO',
                                              F_QUEBRA_LINHA);
                    END IF;
                EXCEPTION
                    WHEN invalid_text_representation THEN -- error that can be handled
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS =
                                concat(F_MSGS_ERROS, F_QTD_ERROS, '- VALOR DO PNEU COM CARACTERES INCORRETOS',
                                       F_QUEBRA_LINHA);
                END;
            END IF;
        END IF;

        IF (F_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            INSERT INTO PNEU (CODIGO_CLIENTE,
                              COD_MODELO,
                              COD_DIMENSAO,
                              PRESSAO_RECOMENDADA,
                              COD_UNIDADE,
                              STATUS,
                              VIDA_ATUAL,
                              VIDA_TOTAL,
                              PNEU_NOVO_NUNCA_RODADO,
                              COD_MODELO_BANDA,
                              DOT,
                              VALOR,
                              COD_EMPRESA,
                              COD_UNIDADE_CADASTRO,
                              ORIGEM_CADASTRO)
            VALUES (NEW.NUMERO_FOGO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANCO,
                    F_COD_DIMENSAO,
                    NEW.PRESSAO_RECOMENDADA_FORMATADA_IMPORT,
                    NEW.COD_UNIDADE,
                    'ESTOQUE',
                    NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                    NEW.VIDA_TOTAL_FORMATADA_IMPORT,
                    NEW.PNEU_NOVO_NUNCA_RODADO_FORMATADO_IMPORT,
                    F_COD_MODELO_BANDA_BANCO,
                    NEW.DOT_FORMATADO_IMPORT,
                    NEW.VALOR_PNEU_FORMATADO_IMPORT,
                    NEW.COD_EMPRESA,
                    NEW.COD_UNIDADE,
                    F_ORIGEM_PNEU_CADASTRO)
            RETURNING CODIGO INTO F_COD_PNEU;


            IF (NEW.VIDA_ATUAL_FORMATADA_IMPORT > 1)
            THEN
                SELECT PTS.CODIGO AS CODIGO
                FROM PNEU_TIPO_SERVICO AS PTS
                WHERE PTS.COD_EMPRESA IS NULL
                  AND PTS.STATUS_ATIVO = TRUE
                  AND PTS.INCREMENTA_VIDA = TRUE
                  AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE
                INTO F_COD_TIPO_SERVICO;

                INSERT INTO PNEU_SERVICO_REALIZADO (COD_TIPO_SERVICO,
                                                    COD_UNIDADE,
                                                    COD_PNEU,
                                                    CUSTO,
                                                    VIDA,
                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_TIPO_SERVICO,
                        NEW.COD_UNIDADE,
                        F_COD_PNEU,
                        NEW.VALOR_BANDA_FORMATADO_IMPORT,
                        (NEW.VIDA_ATUAL_FORMATADA_IMPORT - 1),
                        'FONTE_CADASTRO')
                RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

                INSERT INTO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA (COD_SERVICO_REALIZADO,
                                                                    COD_MODELO_BANDA,
                                                                    VIDA_NOVA_PNEU,
                                                                    FONTE_SERVICO_REALIZADO)
                VALUES (F_COD_SERVICO_REALIZADO,
                        F_COD_MODELO_BANDA_BANCO,
                        NEW.VIDA_ATUAL_FORMATADA_IMPORT,
                        'FONTE_CADASTRO');

                INSERT INTO PNEU_SERVICO_CADASTRO (COD_PNEU,
                                                   COD_SERVICO_REALIZADO)
                VALUES (F_COD_PNEU, F_COD_SERVICO_REALIZADO);
            END IF;
            NEW.STATUS_IMPORT_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;