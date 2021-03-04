-- Cria a coluna para armezenar o código do colaborador que realizou o cadastro.
alter table pneu_data
	add cod_colaborador_cadastro BIGINT;
comment on column pneu_data.cod_colaborador_cadastro is 'Código do colaborador que realizou o cadastro, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table pneu_data
	add constraint fk_colaborador_cadastro
		foreign key (cod_colaborador_cadastro) references colaborador_data (codigo);

-- Cria a coluna para armazenar a data e hora da última alteração.
alter table pneu_data
	add data_hora_ultima_alteracao TIMESTAMP WITH TIME ZONE;
comment on column pneu_data.data_hora_ultima_alteracao is 'Data e hora da última alteração, pode ser nulo.';

-- Cria a coluna para armazenar o código do colaborador que realizou a última alteração.
alter table pneu_data
	add cod_colaborador_ultima_alteracao BIGINT;
comment on column pneu_data.cod_colaborador_ultima_alteracao is 'Código do colaborador que realizou a última alteração, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table pneu_data
	add constraint fk_colaborador_ultima_alteracao
		foreign key (cod_colaborador_ultima_alteracao) references colaborador_data (codigo);

-- Refaz a view de pneu para adicionar os campos novos.
CREATE OR REPLACE VIEW PNEU AS
SELECT P.CODIGO_CLIENTE,
       P.COD_MODELO,
       P.COD_DIMENSAO,
       P.PRESSAO_RECOMENDADA,
       P.PRESSAO_ATUAL,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       P.COD_UNIDADE,
       P.STATUS,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       P.COD_MODELO_BANDA,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.DOT,
       P.VALOR,
       P.DATA_HORA_CADASTRO,
       P.PNEU_NOVO_NUNCA_RODADO,
       P.CODIGO,
       P.COD_EMPRESA,
       P.COD_UNIDADE_CADASTRO,
       P.COD_COLABORADOR_CADASTRO,
       P.COD_COLABORADOR_ULTIMA_ALTERACAO,
       P.DATA_HORA_ULTIMA_ALTERACAO
FROM PNEU_DATA P
WHERE P.DELETADO = FALSE;

-- Altera a function de inserção de pneu.
-- TODO adicionar validações, criar documentação e arquivo específico.
CREATE OR REPLACE FUNCTION FUNC_PNEU_INSERE_PNEU(F_CODIGO_PNEU_CLIENTE CHARACTER VARYING,
                                                 F_COD_MODELO_PNEU BIGINT,
                                                 F_COD_DIMENSAO_PNEU BIGINT,
                                                 F_PRESSAO_CORRETA_PNEU DOUBLE PRECISION,
                                                 F_COD_UNIDADE_PNEU BIGINT,
                                                 F_VIDA_ATUAL_PNEU INTEGER,
                                                 F_VIDA_TOTAL_PNEU INTEGER,
                                                 F_COD_MODELO_BANDA_PNEU BIGINT,
                                                 F_DOT_PNEU CHARACTER VARYING,
                                                 F_VALOR_PNEU NUMERIC,
                                                 F_PNEU_NOVO_NUNCA_RODADO BOOLEAN,
                                                 F_COD_COLABORADOR_CADASTRO BIGINT,
                                                 F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_PNEU_PRIMEIRA_VIDA  CONSTANT BIGINT  := 1;
    V_COD_PNEU_PROLOG              BIGINT;
    V_PNEU_STATUS_ESTOQUE CONSTANT TEXT    := 'ESTOQUE';
    V_PNEU_POSSUI_BANDA   CONSTANT BOOLEAN := F_IF(F_VIDA_ATUAL_PNEU > V_PNEU_PRIMEIRA_VIDA, TRUE, FALSE);
    V_COD_EMPRESA_PNEU    CONSTANT BIGINT  := (SELECT COD_EMPRESA
                                               FROM UNIDADE
                                               WHERE CODIGO = F_COD_UNIDADE_PNEU);
BEGIN
    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO PNEU(CODIGO_CLIENTE,
                     COD_MODELO,
                     COD_DIMENSAO,
                     PRESSAO_RECOMENDADA,
                     PRESSAO_ATUAL,
                     ALTURA_SULCO_INTERNO,
                     ALTURA_SULCO_CENTRAL_INTERNO,
                     ALTURA_SULCO_CENTRAL_EXTERNO,
                     ALTURA_SULCO_EXTERNO,
                     COD_UNIDADE,
                     STATUS,
                     VIDA_ATUAL,
                     VIDA_TOTAL,
                     COD_MODELO_BANDA,
                     DOT,
                     VALOR,
                     PNEU_NOVO_NUNCA_RODADO,
                     COD_EMPRESA,
                     COD_UNIDADE_CADASTRO,
                     COD_COLABORADOR_CADASTRO,
                     COD_COLABORADOR_ULTIMA_ALTERACAO,
                     DATA_HORA_ULTIMA_ALTERACAO)
    VALUES (F_CODIGO_PNEU_CLIENTE,
            F_COD_MODELO_PNEU,
            F_COD_DIMENSAO_PNEU,
            F_PRESSAO_CORRETA_PNEU,
            0,
            NULL,
            NULL,
            NULL,
            NULL,
            F_COD_UNIDADE_PNEU,
            V_PNEU_STATUS_ESTOQUE,
            F_VIDA_ATUAL_PNEU,
            F_VIDA_TOTAL_PNEU,
            F_IF(V_PNEU_POSSUI_BANDA, F_COD_MODELO_BANDA_PNEU, NULL),
            F_DOT_PNEU,
            F_VALOR_PNEU,
            F_IF(V_PNEU_POSSUI_BANDA, FALSE, F_PNEU_NOVO_NUNCA_RODADO),
            V_COD_EMPRESA_PNEU,
            F_COD_UNIDADE_PNEU,
            F_COD_COLABORADOR_CADASTRO,
            F_COD_COLABORADOR_CADASTRO,
            F_DATA_HORA_ULTIMA_ALTERACAO)
    RETURNING CODIGO INTO V_COD_PNEU_PROLOG;

    -- Verificamos se o insert funcionou.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                'Não foi possível inserir o pneu, tente novamente');
    END IF;

    RETURN V_COD_PNEU_PROLOG;
END;
$$;

-- Altera a function de update de pneu.
-- O código do colaborador que realizou a requisição é recebido via parâmetro.
-- Parâmetros adicionados: COD_COLABORADOR_ULTIMA_ALTERACAO, DATA_HORA_ULTIMA_ALTERACAO.
-- Os novos parâmetros serão inseridos na tabela PNEU_DATA.
-- Foi altera a lógica que verificação de alterações para poupar processamento em updates desnecessários.
DROP FUNCTION FUNC_PNEU_ATUALIZA(F_COD_CLIENTE TEXT,
                                              F_COD_MODELO BIGINT,
                                              F_COD_DIMENSAO BIGINT,
                                              F_COD_MODELO_BANDA BIGINT,
                                              F_DOT TEXT,
                                              F_VALOR NUMERIC,
                                              F_VIDA_TOTAL INT,
                                              F_PRESSAO_RECOMENDADA DOUBLE PRECISION,
                                              F_COD_ORIGINAL_PNEU BIGINT,
                                              F_COD_UNIDADE BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_ATUALIZA(F_COD_CLIENTE TEXT,
                                              F_COD_MODELO BIGINT,
                                              F_COD_DIMENSAO BIGINT,
                                              F_COD_MODELO_BANDA BIGINT,
                                              F_DOT TEXT,
                                              F_VALOR NUMERIC,
                                              F_VIDA_TOTAL INT,
                                              F_PRESSAO_RECOMENDADA DOUBLE PRECISION,
                                              F_COD_ORIGINAL_PNEU BIGINT,
                                              F_COD_UNIDADE BIGINT,
                                              F_COD_COLABORADOR_ULTIMA_ALTERACAO BIGINT,
                                              F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_MUDOU_PNEU BOOLEAN = (SELECT (TRIM(CODIGO_CLIENTE) IS DISTINCT FROM TRIM(F_COD_CLIENTE)) OR
                                   (COD_MODELO IS DISTINCT FROM F_COD_MODELO) OR
                                   (COD_DIMENSAO IS DISTINCT FROM F_COD_DIMENSAO) OR
                                   (COD_MODELO_BANDA IS DISTINCT FROM F_COD_MODELO_BANDA) OR
                                   (TRIM(DOT) IS DISTINCT FROM TRIM(F_DOT)) OR
                                   (VALOR IS DISTINCT FROM F_VALOR) OR
                                   (VIDA_TOTAL IS DISTINCT FROM F_VIDA_TOTAL) OR
                                   (PRESSAO_RECOMENDADA IS DISTINCT FROM F_PRESSAO_RECOMENDADA) OR
                                   (COD_UNIDADE IS DISTINCT FROM F_COD_UNIDADE)
                            FROM PNEU
                            WHERE CODIGO = F_COD_ORIGINAL_PNEU);
BEGIN
    IF V_MUDOU_PNEU THEN
        UPDATE PNEU
        SET CODIGO_CLIENTE                   = F_COD_CLIENTE,
            COD_MODELO                       = F_COD_MODELO,
            COD_DIMENSAO                     = F_COD_DIMENSAO,
            COD_MODELO_BANDA                 = F_COD_MODELO_BANDA,
            DOT                              = F_DOT,
            VALOR                            = F_VALOR,
            VIDA_TOTAL                       = F_VIDA_TOTAL,
            PRESSAO_RECOMENDADA              = F_PRESSAO_RECOMENDADA,
            COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ULTIMA_ALTERACAO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_HORA_ULTIMA_ALTERACAO
        WHERE CODIGO = F_COD_ORIGINAL_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE;

        -- Verificamos se o update funcionou.
        IF NOT FOUND
        THEN
            PERFORM THROW_GENERIC_ERROR(
                    'Não foi possível atualizar o pneu, tente novamente.');
        END IF;
    END IF;
END
$$;