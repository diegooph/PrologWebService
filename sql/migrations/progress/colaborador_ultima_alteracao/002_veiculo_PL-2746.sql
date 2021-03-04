-- Cria a coluna para armezenar o código do colaborador que realizou o cadastro.
alter table veiculo_data
	add cod_colaborador_cadastro BIGINT;
comment on column veiculo_data.cod_colaborador_cadastro is 'Código do colaborador que realizou o cadastro, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table veiculo_data
	add constraint fk_colaborador_cadastro
		foreign key (cod_colaborador_cadastro) references colaborador_data (codigo);

-- Cria a coluna para armazenar a data e hora da última alteração.
alter table veiculo_data
	add data_hora_ultima_alteracao TIMESTAMP WITH TIME ZONE;
comment on column veiculo_data.data_hora_ultima_alteracao is 'Data e hora da última alteração, pode ser nulo.';

-- Cria a coluna para armazenar o código do colaborador que realizou a última alteração.
alter table veiculo_data
	add cod_colaborador_ultima_alteracao BIGINT;
comment on column veiculo_data.cod_colaborador_ultima_alteracao is 'Código do colaborador que realizou a última alteração, pode ser nulo.';

-- Cria a fk para a tabela de colaboradores.
alter table veiculo_data
	add constraint fk_colaborador_ultima_alteracao
		foreign key (cod_colaborador_ultima_alteracao) references colaborador_data (codigo);

-- Refaz a view de veículo para adicionar os campos novos.
CREATE OR REPLACE VIEW VEICULO AS
SELECT V.PLACA,
       V.COD_UNIDADE,
       V.COD_EMPRESA,
       V.KM,
       V.STATUS_ATIVO,
       V.COD_TIPO,
       V.COD_MODELO,
       V.COD_EIXOS,
       V.DATA_HORA_CADASTRO,
       V.COD_UNIDADE_CADASTRO,
       V.CODIGO,
       V.COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA,
       V.COD_COLABORADOR_CADASTRO,
       V.COD_COLABORADOR_ULTIMA_ALTERACAO,
       V.DATA_HORA_ULTIMA_ALTERACAO
FROM VEICULO_DATA V
WHERE V.DELETADO = FALSE;

-- Altera a function de inserção de veículo.
-- Parâmetros adicionados: COD_COLABORADOR_CADASTRO, DATA_HORA_ULTIMA_ALTERACAO.
-- Os novos parâmetros serão inseridos na tabela VEICULO_DATA.
DROP FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                          F_PLACA TEXT,
                                          F_IDENTIFICADOR_FROTA TEXT,
                                          F_KM_ATUAL BIGINT,
                                          F_COD_MODELO BIGINT,
                                          F_COD_TIPO BIGINT);
CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_IDENTIFICADOR_FROTA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT,
                                                       F_COD_COLABORADOR_CADASTRO BIGINT,
                                                       F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA           BIGINT;
    V_STATUS_ATIVO CONSTANT BOOLEAN := TRUE;
    V_COD_DIAGRAMA          BIGINT;
    V_COD_VEICULO_PROLOG    BIGINT;
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

    -- Aqui devemos apenas inserir o veículo no Prolog.
    INSERT INTO VEICULO(COD_EMPRESA,
                        COD_UNIDADE,
                        PLACA,
                        KM,
                        STATUS_ATIVO,
                        COD_TIPO,
                        COD_MODELO,
                        COD_DIAGRAMA,
                        COD_UNIDADE_CADASTRO,
                        IDENTIFICADOR_FROTA,
                        COD_COLABORADOR_CADASTRO,
                        DATA_HORA_ULTIMA_ALTERACAO,
                        COD_COLABORADOR_ULTIMA_ALTERACAO)
    VALUES (V_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            V_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            V_COD_DIAGRAMA,
            F_COD_UNIDADE,
            F_IDENTIFICADOR_FROTA,
            F_COD_COLABORADOR_CADASTRO,
            F_DATA_HORA_ULTIMA_ALTERACAO,
            F_COD_COLABORADOR_CADASTRO) RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;

-- Altera a function de update de veículo.
-- O código do colaborador que realizou a requisição é recebido via parâmetro.
-- Parâmetros adicionados: COD_COLABORADOR_ULTIMA_ALTERACAO, DATA_HORA_ULTIMA_ALTERACAO.
-- Os novos parâmetros serão inseridos na tabela VEICULO_DATA.
-- Foi adicionada uma lógica que verificação de alterações para poupar processamento em updates desnecessários.
DROP  FUNCTION FUNC_VEICULO_ATUALIZA_VEICULO(F_PLACA TEXT,
                                             F_NOVO_IDENTIFICADOR_FROTA TEXT,
                                             F_NOVO_KM BIGINT,
                                             F_NOVO_COD_MODELO BIGINT,
                                             F_NOVO_COD_TIPO BIGINT);
CREATE OR REPLACE FUNCTION FUNC_VEICULO_ATUALIZA_VEICULO(F_PLACA TEXT,
                                                         F_NOVO_IDENTIFICADOR_FROTA TEXT,
                                                         F_NOVO_KM BIGINT,
                                                         F_NOVO_COD_MODELO BIGINT,
                                                         F_NOVO_COD_TIPO BIGINT,
                                                         F_COD_COLABORADOR_ULTIMA_ALTERACAO BIGINT,
                                                         F_DATA_HORA_ULTIMA_ALTERACAO TIMESTAMP WITH TIME ZONE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA        BIGINT;
    V_COD_TIPO_ANTIGO    BIGINT;
    V_COD_DIAGRAMA       BIGINT;
    V_COD_VEICULO        BIGINT = (SELECT CODIGO FROM VEICULO WHERE PLACA = F_PLACA);
    V_MUDOU_VEICULO      BOOLEAN = (SELECT (TRIM(PLACA) IS DISTINCT FROM TRIM(F_PLACA)) OR
                                           (TRIM(IDENTIFICADOR_FROTA) IS DISTINCT FROM
                                            TRIM(F_NOVO_IDENTIFICADOR_FROTA)) OR
                                           (KM IS DISTINCT FROM F_NOVO_KM) OR
                                           (COD_MODELO IS DISTINCT FROM F_NOVO_COD_MODELO) OR
                                           (COD_TIPO IS DISTINCT FROM F_NOVO_COD_TIPO)
                                    FROM VEICULO
                                    WHERE CODIGO = V_COD_VEICULO);
BEGIN
    IF V_MUDOU_VEICULO THEN
        -- Validamos se o KM foi inputado corretamente.
        IF (F_NOVO_KM < 0)
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'A quilometragem do veículo não pode ser um número negativo.');
        END IF;

        V_COD_EMPRESA := (SELECT VD.COD_EMPRESA
                          FROM VEICULO_DATA VD
                          WHERE VD.PLACA = F_PLACA);

        -- Validamos se o modelo do veículo está mapeado.
        IF (SELECT NOT EXISTS(SELECT CODIGO
                              FROM MODELO_VEICULO MV
                              WHERE MV.COD_EMPRESA = V_COD_EMPRESA
                                AND MV.CODIGO = F_NOVO_COD_MODELO))
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Por favor, verifique o modelo do veículo e tente novamente.');
        END IF;

        -- Validamos se o tipo do veículo está mapeado.
        IF (SELECT NOT EXISTS(SELECT CODIGO
                              FROM VEICULO_TIPO VT
                              WHERE VT.CODIGO = F_NOVO_COD_TIPO
                                AND VT.COD_EMPRESA = V_COD_EMPRESA))
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'Por favor, verifique o tipo do veículo e tente novamente.');
        END IF;

        V_COD_TIPO_ANTIGO := (SELECT VD.COD_TIPO
                              FROM VEICULO_DATA VD
                              WHERE VD.PLACA = F_PLACA);

        -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
        IF ((V_COD_TIPO_ANTIGO <> F_NOVO_COD_TIPO)
            AND (SELECT COUNT(VP.*) FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA) > 0)
        THEN
            PERFORM THROW_GENERIC_ERROR(
                            'O tipo do veículo não pode ser alterado se a placa contém pneus aplicados.');
        END IF;

        -- Busca o código do diagrama de acordo com o tipo de veículo.
        V_COD_DIAGRAMA := (SELECT VT.COD_DIAGRAMA
                           FROM VEICULO_TIPO VT
                           WHERE VT.CODIGO = F_NOVO_COD_TIPO
                             AND VT.COD_EMPRESA = V_COD_EMPRESA);

        UPDATE VEICULO
        SET IDENTIFICADOR_FROTA              = F_NOVO_IDENTIFICADOR_FROTA,
            KM                               = F_NOVO_KM,
            COD_MODELO                       = F_NOVO_COD_MODELO,
            COD_TIPO                         = F_NOVO_COD_TIPO,
            COD_DIAGRAMA                     = V_COD_DIAGRAMA,
            COD_COLABORADOR_ULTIMA_ALTERACAO = F_COD_COLABORADOR_ULTIMA_ALTERACAO,
            DATA_HORA_ULTIMA_ALTERACAO       = F_DATA_HORA_ULTIMA_ALTERACAO
        WHERE CODIGO = V_COD_VEICULO;

        -- VERIFICAMOS SE O UPDATE NA TABELA DE VEÍCULOS OCORREU COM ÊXITO.
        IF NOT FOUND
        THEN
            RAISE EXCEPTION
                'Não foi possível atualizar a placa "%"', F_PLACA;
        END IF;
    END IF;

    RETURN V_COD_VEICULO;
END;
$$;