-- Sobre:
-- Function para inserir um veículo no Prolog, utiliza dados vindos do sistema web
--
-- Histórico:
-- 2020-02-07 -> Function criada (wvinim - PL-1965)
-- 2020-04-29 -> Altera function para salvar identificador de frota (thaisksf - PL-2691)
-- 2020-11-05 -> Adiciona campo possui_hubodometro para cadastro (steinert999 - PL_3223)
-- 2020-11-10 -> Adiciona campo motorizado para cadastro (steinert999 - PL-3223)
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
            F_POSSUI_HUBODOMETRO) RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF V_COD_VEICULO_PROLOG IS NULL OR V_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;