-- Adiciona coluna IDENTIFICADOR_FROTA na tabela VEICULO_DATA
ALTER TABLE VEICULO_DATA
    ADD COLUMN IDENTIFICADOR_FROTA VARCHAR(15);

-- Altera a view VEICULO para conter o número de frota
CREATE OR REPLACE VIEW VEICULO(PLACA,
                               COD_UNIDADE,
                               COD_EMPRESA,
                               KM,
                               STATUS_ATIVO,
                               COD_TIPO,
                               COD_MODELO,
                               COD_EIXOS,
                               DATA_HORA_CADASTRO,
                               COD_UNIDADE_CADASTRO,
                               CODIGO,
                               COD_DIAGRAMA,
                               IDENTIFICADOR_FROTA) AS
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
       V.IDENTIFICADOR_FROTA
FROM VEICULO_DATA V
WHERE (V.DELETADO = FALSE);

-- Altera insert para salvar numero de frota
DROP FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
    F_PLACA TEXT,
    F_KM_ATUAL BIGINT,
    F_COD_MODELO BIGINT,
    F_COD_TIPO BIGINT);

-- Sobre:
-- Function para inserir um veículo no Prolog, utiliza dados vindos do sistema web
--
-- Histórico:
-- 2020-02-07 -> Function criada (wvinim - PL-1965)
-- 2020-04-29 -> Altera function para salvar número de frota (thaisksf - PL-2691)
CREATE OR REPLACE FUNCTION FUNC_VEICULO_INSERE_VEICULO(F_COD_UNIDADE BIGINT,
                                                       F_PLACA TEXT,
                                                       F_IDENTIFICADOR_FROTA TEXT,
                                                       F_KM_ATUAL BIGINT,
                                                       F_COD_MODELO BIGINT,
                                                       F_COD_TIPO BIGINT)
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
                        IDENTIFICADOR_FROTA)
    VALUES (V_COD_EMPRESA,
            F_COD_UNIDADE,
            F_PLACA,
            F_KM_ATUAL,
            V_STATUS_ATIVO,
            F_COD_TIPO,
            F_COD_MODELO,
            V_COD_DIAGRAMA,
            F_COD_UNIDADE,
            F_IDENTIFICADOR_FROTA) RETURNING CODIGO INTO V_COD_VEICULO_PROLOG;

    -- Verificamos se o insert funcionou.
    IF V_COD_VEICULO_PROLOG IS NULL OR V_COD_VEICULO_PROLOG <= 0
    THEN
        PERFORM THROW_GENERIC_ERROR(
                        'Não foi possível inserir o veículo, tente novamente');
    END IF;

    RETURN V_COD_VEICULO_PROLOG;
END;
$$;

-- Altera update de veículo para salvar número de frota
DROP FUNCTION FUNC_VEICULO_ATUALIZA_VEICULO(F_PLACA TEXT,
    F_NOVO_KM BIGINT,
    F_NOVO_COD_MODELO BIGINT,
    F_NOVO_COD_TIPO BIGINT);
-- Sobre:
-- Function para editar um veículo no Prolog, utiliza dados vindos do sistema web
--
-- Histórico:
-- 2020-02-25 -> Function criada (wvinim - PL-1965)
-- 2020-04-29 -> Altera function para salvar número de frota (thaisksf - PL-2691)
CREATE OR REPLACE FUNCTION FUNC_VEICULO_ATUALIZA_VEICULO(F_PLACA TEXT,
                                                         F_NOVO_IDENTIFICADOR_FROTA TEXT,
                                                         F_NOVO_KM BIGINT,
                                                         F_NOVO_COD_MODELO BIGINT,
                                                         F_NOVO_COD_TIPO BIGINT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA        BIGINT;
    V_COD_TIPO_ANTIGO    BIGINT;
    V_COD_DIAGRAMA       BIGINT;
    V_COD_VEICULO        BIGINT;
    V_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
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
    SET IDENTIFICADOR_FROTA = F_NOVO_IDENTIFICADOR_FROTA,
        KM                  = F_NOVO_KM,
        COD_MODELO          = F_NOVO_COD_MODELO,
        COD_TIPO            = F_NOVO_COD_TIPO,
        COD_DIAGRAMA        = V_COD_DIAGRAMA
    WHERE PLACA = F_PLACA RETURNING CODIGO INTO V_COD_VEICULO;

    GET DIAGNOSTICS V_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- VERIFICAMOS SE O UPDATE NA TABELA DE VEÍCULOS OCORREU COM ÊXITO.
    IF (V_QTD_ROWS_ALTERADAS IS NULL OR V_QTD_ROWS_ALTERADAS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível atualizar a placa "%"', F_PLACA;
    END IF;

    RETURN V_COD_VEICULO;
END;
$$;

-- Cria function de Listagem de Veículos - get por unidade
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_SOMENTE_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                CODIGO               BIGINT,
                PLACA                TEXT,
                COD_UNIDADE          BIGINT,
                KM                   BIGINT,
                STATUS_ATIVO         BOOLEAN,
                COD_TIPO             BIGINT,
                COD_MODELO           BIGINT,
                COD_DIAGRAMA         BIGINT,
                IDENTIFICADOR_FROTA  TEXT,
                COD_REGIONAL_ALOCADO BIGINT,
                MODELO               TEXT,
                NOME_DIAGRAMA        TEXT,
                DIANTEIRO            BIGINT,
                TRASEIRO             BIGINT,
                TIPO                 TEXT,
                MARCA                TEXT,
                COD_MARCA            BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT V.CODIGO                                                AS CODIGO,
       V.PLACA                                                 AS PLACA,
       V.COD_UNIDADE::BIGINT                                   AS COD_UNIDADE,
       V.KM                                                    AS KM,
       V.STATUS_ATIVO                                          AS STATUS_ATIVO,
       V.COD_TIPO                                              AS COD_TIPO,
       V.COD_MODELO                                            AS COD_MODELO,
       V.COD_DIAGRAMA                                          AS COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA                                   AS IDENTIFICADOR_FROTA,
       R.CODIGO                                                AS COD_REGIONAL_ALOCADO,
       MV.NOME                                                 AS MODELO,
       VD.NOME                                                 AS NOME_DIAGRAMA,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'D') AS DIANTEIRO,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'T') AS TRASEIRO,
       VT.NOME                                                 AS TIPO,
       MAV.NOME                                                AS MARCA,
       MAV.CODIGO                                              AS COD_MARCA
FROM VEICULO V
         JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = V.COD_DIAGRAMA
         JOIN VEICULO_DIAGRAMA_EIXOS VDE ON VDE.COD_DIAGRAMA = VD.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA
         JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
WHERE V.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_SOMENTE_ATIVOS IS NULL OR F_SOMENTE_ATIVOS IS FALSE
              THEN 1 = 1
          ELSE V.STATUS_ATIVO = TRUE
    END
GROUP BY V.PLACA, V.CODIGO, V.CODIGO, V.PLACA, V.COD_UNIDADE, V.KM, V.STATUS_ATIVO, V.COD_TIPO, V.COD_MODELO,
         V.COD_DIAGRAMA, V.IDENTIFICADOR_FROTA, R.CODIGO, MV.NOME, VD.NOME, VT.NOME, MAV.NOME, MAV.CODIGO
ORDER BY V.PLACA;
$$;

-- Dropa function do relatório de dados gerais de veículos por unidades.
DROP FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(BIGINT[]);

-- Adiciona número de frota no relatório de dados gerais de veículos
CREATE OR REPLACE FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                UNIDADE                  TEXT,
                PLACA                    TEXT,
                "IDENTIFICADOR FROTA"    TEXT,
                MARCA                    TEXT,
                MODELO                   TEXT,
                TIPO                     TEXT,
                "DIAGRAMA VINCULADO?"    TEXT,
                "KM ATUAL"               TEXT,
                STATUS                   TEXT,
                "DATA/HORA CADASTRO"     TEXT,
                "VEÍCULO COMPLETO"       TEXT,
                "QTD PNEUS VINCULADOS"   TEXT,
                "QTD POSIÇÕES DIAGRAMA"  TEXT,
                "QTD POSIÇÕES SEM PNEUS" TEXT,
                "QTD ESTEPES"            TEXT
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    ESTEPES            INTEGER := 900;
    POSICOES_SEM_PNEUS INTEGER = 0;
    SIM                TEXT    := 'SIM';
    NAO                TEXT    := 'NÃO';
BEGIN
    RETURN QUERY
        -- Calcula a quantidade de pneus e estepes que estão vinculados na placa.
        WITH QTD_PNEUS_VINCULADOS_PLACA AS (
            SELECT V.PLACA,
                   COUNT(VP.PLACA)
                   FILTER (WHERE VP.POSICAO < ESTEPES)  AS QTD_PNEUS_VINCULADOS,
                   COUNT(VP.PLACA)
                   FILTER (WHERE VP.POSICAO >= ESTEPES) AS QTD_ESTEPES_VINCULADOS,
                   VT.COD_DIAGRAMA
            FROM VEICULO V
                     JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
                     LEFT JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
            GROUP BY V.PLACA,
                     VT.COD_DIAGRAMA
        ),

             -- Calcula a quantidade de posições nos diagramas que existem no prolog.
             QTD_POSICOES_DIAGRAMA AS (
                 SELECT VDE.COD_DIAGRAMA,
                        SUM(VDE.QT_PNEUS) AS QTD_POSICOES_DIAGRAMA
                 FROM VEICULO_DIAGRAMA_EIXOS VDE
                 GROUP BY COD_DIAGRAMA
             )

        SELECT U.NOME :: TEXT                                                     AS UNIDADE,
               V.PLACA :: TEXT                                                    AS PLACA,
               V.IDENTIFICADOR_FROTA :: TEXT                                      AS IDENTIFICADOR_FROTA,
               MA.NOME :: TEXT                                                    AS MARCA,
               MO.NOME :: TEXT                                                    AS MODELO,
               VT.NOME :: TEXT                                                    AS TIPO,
               CASE
                   WHEN QPVP.COD_DIAGRAMA IS NULL
                       THEN 'NÃO'
                   ELSE 'SIM' END                                                 AS POSSUI_DIAGRAMA,
               V.KM :: TEXT                                                       AS KM_ATUAL,
               F_IF(V.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)           AS STATUS,
               COALESCE(TO_CHAR(V.DATA_HORA_CADASTRO, 'DD/MM/YYYY HH24:MI'), '-') AS DATA_HORA_CADASTRO,
               -- Caso a quantidade de posições sem pneus seja 0 é porque o veículo está com todos os pneus - veículo completo.
               CASE
                   WHEN (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) = POSICOES_SEM_PNEUS
                       THEN SIM
                   ELSE NAO END                                                   AS VEICULO_COMPLETO,
               QPVP.QTD_PNEUS_VINCULADOS :: TEXT                                  AS QTD_PNEUS_VINCULADOS,
               QSD.QTD_POSICOES_DIAGRAMA :: TEXT                                  AS QTD_POSICOES_DIAGRAMA,
               -- Calcula a quantidade de posições sem pneus.
               (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) :: TEXT    AS QTD_POSICOES_SEM_PNEUS,
               QPVP.QTD_ESTEPES_VINCULADOS :: TEXT                                AS QTD_ESTEPES_VINCULADOS
        FROM VEICULO V
                 JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
                 JOIN MODELO_VEICULO MO ON V.COD_MODELO = MO.CODIGO
                 JOIN MARCA_VEICULO MA ON MO.COD_MARCA = MA.CODIGO
                 JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
                 RIGHT JOIN QTD_PNEUS_VINCULADOS_PLACA QPVP ON QPVP.PLACA = V.PLACA
                 LEFT JOIN QTD_POSICOES_DIAGRAMA QSD ON QSD.COD_DIAGRAMA = QPVP.COD_DIAGRAMA
        ORDER BY U.NOME ASC,
                 STATUS ASC,
                 V.PLACA ASC,
                 MA.NOME ASC,
                 MO.NOME ASC,
                 VT.NOME ASC,
                 QTD_POSICOES_SEM_PNEUS DESC;
END;
$$;

-- Retorna um veículo completo de acordo com seu código
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_VEICULO(F_COD_VEICULO BIGINT)
    RETURNS TABLE
            (
                CODIGO               BIGINT,
                PLACA                TEXT,
                COD_UNIDADE          BIGINT,
                KM                   BIGINT,
                STATUS_ATIVO         BOOLEAN,
                COD_TIPO             BIGINT,
                COD_MODELO           BIGINT,
                COD_DIAGRAMA         BIGINT,
                IDENTIFICADOR_FROTA  TEXT,
                COD_REGIONAL_ALOCADO BIGINT,
                MODELO               TEXT,
                NOME_DIAGRAMA        TEXT,
                DIANTEIRO            BIGINT,
                TRASEIRO             BIGINT,
                TIPO                 TEXT,
                MARCA                TEXT,
                COD_MARCA            BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT V.CODIGO                                                AS CODIGO,
       V.PLACA                                                 AS PLACA,
       V.COD_UNIDADE::BIGINT                                   AS COD_UNIDADE,
       V.KM                                                    AS KM,
       V.STATUS_ATIVO                                          AS STATUS_ATIVO,
       V.COD_TIPO                                              AS COD_TIPO,
       V.COD_MODELO                                            AS COD_MODELO,
       V.COD_DIAGRAMA                                          AS COD_DIAGRAMA,
       V.IDENTIFICADOR_FROTA                                   AS IDENTIFICADOR_FROTA,
       R.CODIGO                                                AS COD_REGIONAL_ALOCADO,
       MV.NOME                                                 AS MODELO,
       VD.NOME                                                 AS NOME_DIAGRAMA,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'D') AS DIANTEIRO,
       COUNT(VDE.TIPO_EIXO) FILTER (WHERE VDE.TIPO_EIXO = 'T') AS TRASEIRO,
       VT.NOME                                                 AS TIPO,
       MAV.NOME                                                AS MARCA,
       MAV.CODIGO                                              AS COD_MARCA
FROM VEICULO V
         JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
         JOIN VEICULO_DIAGRAMA VD ON VD.CODIGO = V.COD_DIAGRAMA
         JOIN VEICULO_DIAGRAMA_EIXOS VDE ON VDE.COD_DIAGRAMA = VD.CODIGO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
         JOIN MARCA_VEICULO MAV ON MAV.CODIGO = MV.COD_MARCA
         JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
WHERE V.CODIGO = F_COD_VEICULO
GROUP BY V.PLACA, V.CODIGO, V.CODIGO, V.PLACA, V.COD_UNIDADE, V.KM, V.STATUS_ATIVO, V.COD_TIPO, V.COD_MODELO,
         V.COD_DIAGRAMA, V.IDENTIFICADOR_FROTA, R.CODIGO, MV.NOME, VD.NOME, VT.NOME, MAV.NOME, MAV.CODIGO
ORDER BY V.PLACA;
$$;

-- Busca pneus de um veículo de acordo com o cod_veiculo
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_PNEU_BY_COD_VEICULO(F_COD_VEICULO BIGINT)
    RETURNS TABLE
            (
                NOME_MARCA_PNEU              VARCHAR(255),
                COD_MARCA_PNEU               BIGINT,
                CODIGO                       BIGINT,
                CODIGO_CLIENTE               VARCHAR(255),
                COD_UNIDADE_ALOCADO          BIGINT,
                COD_REGIONAL_ALOCADO         BIGINT,
                PRESSAO_ATUAL                REAL,
                VIDA_ATUAL                   INTEGER,
                VIDA_TOTAL                   INTEGER,
                PNEU_NOVO_NUNCA_RODADO       BOOLEAN,
                NOME_MODELO_PNEU             VARCHAR(255),
                COD_MODELO_PNEU              BIGINT,
                QT_SULCOS_MODELO_PNEU        SMALLINT,
                ALTURA_SULCOS_MODELO_PNEU    REAL,
                ALTURA                       INTEGER,
                LARGURA                      INTEGER,
                ARO                          REAL,
                COD_DIMENSAO                 BIGINT,
                PRESSAO_RECOMENDADA          REAL,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_INTERNO         REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                DOT                          VARCHAR(20),
                VALOR                        REAL,
                COD_MODELO_BANDA             BIGINT,
                NOME_MODELO_BANDA            VARCHAR(255),
                QT_SULCOS_MODELO_BANDA       SMALLINT,
                ALTURA_SULCOS_MODELO_BANDA   REAL,
                COD_MARCA_BANDA              BIGINT,
                NOME_MARCA_BANDA             VARCHAR(255),
                VALOR_BANDA                  REAL,
                POSICAO_PNEU                 INTEGER,
                NOMENCLATURA                 VARCHAR(255),
                COD_VEICULO_APLICADO         BIGINT,
                PLACA_APLICADO               VARCHAR(7)
            )
    LANGUAGE SQL
AS
$$
SELECT MP.NOME                                  AS NOME_MARCA_PNEU,
       MP.CODIGO                                AS COD_MARCA_PNEU,
       P.CODIGO,
       P.CODIGO_CLIENTE,
       U.CODIGO                                 AS COD_UNIDADE_ALOCADO,
       R.CODIGO                                 AS COD_REGIONAL_ALOCADO,
       P.PRESSAO_ATUAL,
       P.VIDA_ATUAL,
       P.VIDA_TOTAL,
       P.PNEU_NOVO_NUNCA_RODADO,
       MOP.NOME                                 AS NOME_MODELO_PNEU,
       MOP.CODIGO                               AS COD_MODELO_PNEU,
       MOP.QT_SULCOS                            AS QT_SULCOS_MODELO_PNEU,
       MOP.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_PNEU,
       PD.ALTURA,
       PD.LARGURA,
       PD.ARO,
       PD.CODIGO                                AS COD_DIMENSAO,
       P.PRESSAO_RECOMENDADA,
       P.ALTURA_SULCO_CENTRAL_INTERNO,
       P.ALTURA_SULCO_CENTRAL_EXTERNO,
       P.ALTURA_SULCO_INTERNO,
       P.ALTURA_SULCO_EXTERNO,
       P.DOT,
       P.VALOR,
       MOB.CODIGO                               AS COD_MODELO_BANDA,
       MOB.NOME                                 AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                            AS QT_SULCOS_MODELO_BANDA,
       MOB.ALTURA_SULCOS                        AS ALTURA_SULCOS_MODELO_BANDA,
       MAB.CODIGO                               AS COD_MARCA_BANDA,
       MAB.NOME                                 AS NOME_MARCA_BANDA,
       PVV.VALOR                                AS VALOR_BANDA,
       PO.POSICAO_PROLOG                        AS POSICAO_PNEU,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-') AS NOMENCLATURA,
       VEI.CODIGO                               AS COD_VEICULO_APLICADO,
       VEI.PLACA                                AS PLACA_APLICADO
FROM PNEU P
         JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA
         JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO
         JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
         JOIN REGIONAL R ON U.COD_REGIONAL = R.CODIGO
         LEFT JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
         LEFT JOIN VEICULO VEI ON VEI.PLACA = VP.PLACA
         LEFT JOIN VEICULO_TIPO VT ON VT.CODIGO = VEI.COD_TIPO AND VT.COD_EMPRESA = P.COD_EMPRESA
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_ORDEM PO ON VP.POSICAO = PO.POSICAO_PROLOG
         LEFT JOIN MODELO_BANDA MOB ON MOB.CODIGO = P.COD_MODELO_BANDA AND MOB.COD_EMPRESA = U.COD_EMPRESA
         LEFT JOIN MARCA_BANDA MAB ON MAB.CODIGO = MOB.COD_MARCA AND MAB.COD_EMPRESA = MOB.COD_EMPRESA
         LEFT JOIN PNEU_VALOR_VIDA PVV ON PVV.COD_PNEU = P.CODIGO AND PVV.VIDA = P.VIDA_ATUAL
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON
        PPNE.COD_EMPRESA = P.COD_EMPRESA AND
        PPNE.COD_DIAGRAMA = VD.CODIGO AND
        PPNE.POSICAO_PROLOG = VP.POSICAO
WHERE VEI.CODIGO = F_COD_VEICULO
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;

-- MODIFICA IMPORTS PARA CONTER IDENTIFICADOR - FROTA.
-- Adiciona a coluna identificador_frota na tabela de import.
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_USUARIO TEXT,
                                                                              F_DATA DATE,
                                                                              OUT NOME_TABELA_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DIA                TEXT := (SELECT EXTRACT(DAY FROM F_DATA));
    MES                TEXT := (SELECT EXTRACT(MONTH FROM F_DATA));
    ANO                TEXT := (SELECT EXTRACT(YEAR FROM F_DATA)) ;
    NOME_TABELA_IMPORT TEXT := lower(remove_all_spaces(
                        'V_COD_EMP_' || F_COD_EMPRESA || '_COD_UNIDADE_' || F_COD_UNIDADE || '_' || ANO || '_' || MES ||
                        '_' || DIA || '_' || F_USUARIO));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                       BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT       BIGINT,
            COD_UNIDADE_EDITAVEL         BIGINT,
            PLACA_EDITAVEL               VARCHAR(255),
            KM_EDITAVEL                  BIGINT,
            MARCA_EDITAVEL               VARCHAR(255),
            MODELO_EDITAVEL              VARCHAR(255),
            TIPO_EDITAVEL                VARCHAR(255),
            QTD_EIXOS_EDITAVEL           VARCHAR(255),
            IDENTIFICADOR_FROTA_EDITAVEL VARCHAR(15),
            PLACA_FORMATADA_IMPORT       VARCHAR(255),
            MARCA_FORMATADA_IMPORT       VARCHAR(255),
            MODELO_FORMATADO_IMPORT      VARCHAR(255),
            TIPO_FORMATADO_IMPORT        VARCHAR(255),
            IDENTIFICADOR_FROTA_IMPORT   VARCHAR(15),
            STATUS_IMPORT_REALIZADO      BOOLEAN,
            ERROS_ENCONTRADOS            VARCHAR(255),
            USUARIO_UPDATE               VARCHAR(255),
            PRIMARY KEY (CODIGO),
            FOREIGN KEY (COD_DADOS_AUTOR_IMPORT) REFERENCES IMPLANTACAO.DADOS_AUTOR_IMPORT (CODIGO),
            FOREIGN KEY (COD_UNIDADE_EDITAVEL) REFERENCES UNIDADE (CODIGO)
        );', NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE VEÍCULOS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO();',
                   NOME_TABELA_IMPORT,
                   NOME_TABELA_IMPORT);

    --CRIA AUDIT PARA A TABELA
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_VEICULO ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_VEICULO
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                    NOME_TABELA_IMPORT,
                    NOME_TABELA_IMPORT);

    -- GARANTE UPDATE PARA O NATAN
    -- TODO REMOVER HARDCODED
      EXECUTE FORMAT(
      'grant select, update on implantacao.%I to prolog_user_natan;', NOME_TABELA_IMPORT
      );

    --RETORNA NOME DA TABELA
    SELECT NOME_TABELA_IMPORT INTO NOME_TABELA_CRIADA;
END ;
$$;

-- Adiciona a coluna identificador_frota no insert de import
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                               F_NOME_TABELA_IMPORT TEXT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_JSON_VEICULOS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO IMPLANTACAO.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_UNIDADE_EDITAVEL,
                                                PLACA_EDITAVEL,
                                                PLACA_FORMATADA_IMPORT,
                                                KM_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MARCA_FORMATADA_IMPORT,
                                                MODELO_EDITAVEL,
                                                MODELO_FORMATADO_IMPORT,
                                                TIPO_EDITAVEL,
                                                TIPO_FORMATADO_IMPORT,
                                                QTD_EIXOS_EDITAVEL,
                                                IDENTIFICADOR_FROTA_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT                                         AS PLACA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> ''placa'')) :: TEXT  AS PLACA_FORMATADA_IMPORT,
                          (SRC ->> ''km'') :: BIGINT                                          AS KM,
                          (SRC ->> ''marca'') :: TEXT                                         AS MARCA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''marca'')) :: TEXT  AS MARCA_FORMATADA_IMPORT,
                          (SRC ->> ''modelo'') :: TEXT                                        AS MODELO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''modelo'')) :: TEXT AS MODELO_FORMATADO_IMPORT,
                          (SRC ->> ''tipo'') :: TEXT                                          AS TIPO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''tipo'')) :: TEXT   AS TIPO_FORMATADO_IMPORT,
                          (SRC ->> ''qtdEixos'') :: TEXT                                      AS QTD_EIXOS,
                          (SRC ->> ''identificadorFrota'') :: TEXT                            AS IDENTIFICADOR_FROTA
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_UNIDADE,
                   F_JSON_VEICULOS);
END
$$;

-- Modifica trigger de verificação para conter a coluna identificacao_frota
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO()
    RETURNS TRIGGER
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA CONSTANT                 BIGINT   := (SELECT U.COD_EMPRESA
                                                        FROM UNIDADE U
                                                        WHERE U.CODIGO = NEW.COD_UNIDADE_EDITAVEL);
    F_VALOR_SIMILARIDADE CONSTANT          REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIAGRAMA CONSTANT REAL     := 0.5;
    F_SEM_SIMILARIDADE CONSTANT            REAL     := 0.0;
    F_QTD_ERROS                            SMALLINT := 0;
    F_MSGS_ERROS                           TEXT;
    F_QUEBRA_LINHA                         TEXT     := CHR(10);
    F_COD_MARCA_BANCO                      BIGINT;
    F_SIMILARIDADE_MARCA                   REAL;
    F_MARCA_MODELO                         TEXT;
    F_COD_MODELO_BANCO                     BIGINT;
    F_SIMILARIDADE_MODELO                  REAL;
    F_COD_DIAGRAMA_BANCO                   BIGINT;
    F_NOME_DIAGRAMA_BANCO                  TEXT;
    F_SIMILARIDADE_DIAGRAMA                REAL;
    F_DIAGRAMA_TIPO                        TEXT;
    F_EIXOS_DIAGRAMA                       TEXT;
    F_COD_TIPO_BANCO                       BIGINT;
    F_SIMILARIDADE_TIPO                    REAL;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        NEW.CODIGO := OLD.CODIGO;
        NEW.COD_DADOS_AUTOR_IMPORT := OLD.COD_DADOS_AUTOR_IMPORT;
        NEW.ERROS_ENCONTRADOS := OLD.ERROS_ENCONTRADOS;
        NEW.COD_UNIDADE_EDITAVEL := OLD.COD_UNIDADE_EDITAVEL;
        NEW.PLACA_EDITAVEL := OLD.PLACA_EDITAVEL;
        NEW.PLACA_FORMATADA_IMPORT := OLD.PLACA_FORMATADA_IMPORT;
        NEW.KM_EDITAVEL := OLD.KM_EDITAVEL;
        NEW.MARCA_EDITAVEL := OLD.MARCA_EDITAVEL;
        NEW.MARCA_FORMATADA_IMPORT := OLD.MARCA_FORMATADA_IMPORT;
        NEW.MODELO_EDITAVEL := OLD.MODELO_EDITAVEL;
        NEW.MODELO_FORMATADO_IMPORT := OLD.MODELO_FORMATADO_IMPORT;
        NEW.TIPO_EDITAVEL := OLD.TIPO_EDITAVEL;
        NEW.TIPO_FORMATADO_IMPORT := OLD.TIPO_FORMATADO_IMPORT;
        NEW.QTD_EIXOS_EDITAVEL := OLD.QTD_EIXOS_EDITAVEL;
        NEW.IDENTIFICADOR_FROTA_EDITAVEL := OLD.IDENTIFICADOR_FROTA_EDITAVEL;
        NEW.IDENTIFICADOR_FROTA_IMPORT := OLD.IDENTIFICADOR_FROTA_IMPORT;
        NEW.STATUS_IMPORT_REALIZADO := OLD.STATUS_IMPORT_REALIZADO;
        NEW.USUARIO_UPDATE := OLD.USUARIO_UPDATE;
    ELSE
        NEW.PLACA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PLACA_EDITAVEL);
        NEW.MARCA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.TIPO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TIPO_EDITAVEL);
        NEW.IDENTIFICADOR_FROTA_IMPORT := NEW.IDENTIFICADOR_FROTA_EDITAVEL;
        NEW.USUARIO_UPDATE := SESSION_USER;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        IF (NEW.PLACA_FORMATADA_IMPORT IS NOT NULL) THEN
            IF LENGTH(NEW.PLACA_FORMATADA_IMPORT) <> 7
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO POSSUI 7 CARACTERES', F_QUEBRA_LINHA);
            ELSE
                IF EXISTS(SELECT V.PLACA
                          FROM VEICULO V
                          WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                            AND V.COD_EMPRESA != F_COD_EMPRESA)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   F_QUEBRA_LINHA);
                ELSE
                    IF EXISTS(SELECT V.PLACA
                              FROM VEICULO V
                              WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                AND V.COD_EMPRESA = F_COD_EMPRESA
                                AND COD_UNIDADE != NEW.COD_UNIDADE_EDITAVEL)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAV.CODIGO                                                        AS COD_MARCA_BANCO,
                                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAV.NOME)) AS SIMILARIEDADE_MARCA
        INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
        FROM MARCA_VEICULO MAV
        GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAV.NOME, MAV.CODIGO
        ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

        F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
        THEN
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            SELECT DISTINCT ON (F_MARCA_MODELO) MOV.CODIGO AS COD_MODELO_VEICULO,
                                                CASE
                                                    WHEN F_COD_MARCA_BANCO = MOV.COD_MARCA
                                                        THEN
                                                        MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                   CONCAT(MOV.COD_MARCA, MOV.NOME)))
                                                    ELSE F_SEM_SIMILARIDADE
                                                    END    AS SIMILARIEDADE_MODELO
            INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
            FROM MODELO_VEICULO MOV
            WHERE MOV.COD_EMPRESA = F_COD_EMPRESA
            GROUP BY F_MARCA_MODELO, MOV.NOME, MOV.CODIGO
            ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_MODELO IS NULL)
            THEN
                INSERT INTO MODELO_VEICULO (NOME, COD_MARCA, COD_EMPRESA)
                VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, F_COD_EMPRESA) RETURNING CODIGO INTO F_COD_MODELO_BANCO;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        F_EIXOS_DIAGRAMA := CONCAT(NEW.QTD_EIXOS_EDITAVEL, NEW.TIPO_FORMATADO_IMPORT);
        -- Procura diagrama no banco:
        WITH INFO_DIAGRAMAS AS (
            SELECT COUNT(VDE.POSICAO) AS QTD_EIXOS, VDE.COD_DIAGRAMA AS CODIGO, VD.NOME AS NOME
            FROM VEICULO_DIAGRAMA_EIXOS VDE
                     JOIN
                 VEICULO_DIAGRAMA VD ON VDE.COD_DIAGRAMA = VD.CODIGO
            GROUP BY VDE.COD_DIAGRAMA, VD.NOME),

             DIAGRAMAS AS (
                 SELECT VDUP.COD_VEICULO_DIAGRAMA AS COD_DIAGRAMA,
                        VDUP.NOME                 AS NOME_DIAGRAMA,
                        VDUP.QTD_EIXOS            AS QTD_EIXOS
                 FROM IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG VDUP
                 UNION ALL
                 SELECT ID.CODIGO AS COD_DIAGRAMA, ID.NOME AS NOME_DIAGRAMA, ID.QTD_EIXOS
                 FROM INFO_DIAGRAMAS ID)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        SELECT DISTINCT ON (F_EIXOS_DIAGRAMA) D.NOME_DIAGRAMA AS NOME_DIAGRAMA,
                                              D.COD_DIAGRAMA  AS DIAGRAMA_BANCO,
                                              CASE
                                                  WHEN D.QTD_EIXOS ::TEXT = NEW.QTD_EIXOS_EDITAVEL
                                                      THEN
                                                      MAX(FUNC_GERA_SIMILARIDADE(F_EIXOS_DIAGRAMA,
                                                                                 CONCAT(D.QTD_EIXOS, D.NOME_DIAGRAMA)))
                                                  ELSE F_SEM_SIMILARIDADE
                                                  END         AS SIMILARIEDADE_DIAGRAMA
        INTO F_NOME_DIAGRAMA_BANCO, F_COD_DIAGRAMA_BANCO,
            F_SIMILARIDADE_DIAGRAMA
        FROM DIAGRAMAS D
        GROUP BY F_EIXOS_DIAGRAMA, D.NOME_DIAGRAMA, D.COD_DIAGRAMA, D.QTD_EIXOS
        ORDER BY F_EIXOS_DIAGRAMA, SIMILARIEDADE_DIAGRAMA DESC;

        F_DIAGRAMA_TIPO := CONCAT(F_NOME_DIAGRAMA_BANCO, NEW.TIPO_FORMATADO_IMPORT);
        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        CASE WHEN (F_SIMILARIDADE_DIAGRAMA >= F_VALOR_SIMILARIDADE_DIAGRAMA)
            THEN
                SELECT DISTINCT ON (F_DIAGRAMA_TIPO) VT.CODIGO AS COD_TIPO_VEICULO,
                                                     CASE
                                                         WHEN F_COD_DIAGRAMA_BANCO = VT.COD_DIAGRAMA
                                                             THEN MAX(FUNC_GERA_SIMILARIDADE(NEW.TIPO_FORMATADO_IMPORT, VT.NOME))
                                                         ELSE F_SEM_SIMILARIDADE
                                                         END   AS SIMILARIEDADE_TIPO_DIAGRAMA
                INTO F_COD_TIPO_BANCO, F_SIMILARIDADE_TIPO
                FROM VEICULO_TIPO VT
                WHERE VT.COD_EMPRESA = F_COD_EMPRESA
                GROUP BY F_DIAGRAMA_TIPO,
                         VT.CODIGO
                ORDER BY F_DIAGRAMA_TIPO, SIMILARIEDADE_TIPO_DIAGRAMA DESC;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                IF (F_SIMILARIDADE_TIPO < F_VALOR_SIMILARIDADE OR F_SIMILARIDADE_TIPO IS NULL)
                THEN
                    INSERT INTO VEICULO_TIPO (NOME, STATUS_ATIVO, COD_DIAGRAMA, COD_EMPRESA)
                    VALUES (NEW.TIPO_EDITAVEL, TRUE, F_COD_DIAGRAMA_BANCO, F_COD_EMPRESA) RETURNING CODIGO INTO F_COD_TIPO_BANCO;
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS =
                        concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', F_QUEBRA_LINHA);
            END CASE;
        -- VERIFICA QTD DE ERROS
        IF (F_QTD_ERROS > 0)
        THEN
            NEW.STATUS_IMPORT_REALIZADO = FALSE;
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            IF (F_QTD_ERROS = 0 AND EXISTS(SELECT V.PLACA
                                           FROM VEICULO V
                                           WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                             AND V.COD_EMPRESA = F_COD_EMPRESA
                                             AND COD_UNIDADE = NEW.COD_UNIDADE_EDITAVEL))
            THEN
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                UPDATE VEICULO_DATA
                SET COD_MODELO = F_COD_MODELO_BANCO,
                    COD_TIPO   = F_COD_TIPO_BANCO,
                    KM         = NEW.KM_EDITAVEL,
                    COD_DIAGRAMA = F_COD_DIAGRAMA_BANCO,
                    IDENTIFICADOR_FROTA = NEW.IDENTIFICADOR_FROTA_IMPORT
                WHERE PLACA = NEW.PLACA_FORMATADA_IMPORT
                  AND COD_EMPRESA = F_COD_EMPRESA
                  AND COD_UNIDADE = NEW.COD_UNIDADE_EDITAVEL;
                NEW.STATUS_IMPORT_REALIZADO = NULL;
                NEW.ERROS_ENCONTRADOS = 'A PLACA JÁ ESTAVA CADASTRADA - INFORMAÇÕES FORAM ATUALIZADAS.';
            ELSE
                IF (F_QTD_ERROS = 0 AND NOT EXISTS(SELECT V.PLACA
                                                   FROM VEICULO V
                                                   WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT))
                THEN
                    -- CADASTRA VEÍCULO.
                    INSERT INTO VEICULO (PLACA,
                                         COD_UNIDADE,
                                         KM,
                                         STATUS_ATIVO,
                                         COD_TIPO,
                                         COD_MODELO,
                                         COD_EIXOS,
                                         DATA_HORA_CADASTRO,
                                         COD_UNIDADE_CADASTRO,
                                         COD_EMPRESA,
                                         COD_DIAGRAMA,
                                         IDENTIFICADOR_FROTA)
                    VALUES (NEW.PLACA_FORMATADA_IMPORT,
                            NEW.COD_UNIDADE_EDITAVEL,
                            NEW.KM_EDITAVEL,
                            TRUE,
                            F_COD_TIPO_BANCO,
                            F_COD_MODELO_BANCO,
                            1,
                            NOW(),
                            NEW.COD_UNIDADE_EDITAVEL,
                            F_COD_EMPRESA,
                            F_COD_DIAGRAMA_BANCO,
                            NEW.IDENTIFICADOR_FROTA_IMPORT);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                    NEW.ERROS_ENCONTRADOS = '-';
                END IF;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;