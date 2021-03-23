-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica os dados que são inseridos na tabela de 'pré-import', procurando os códigos correspondentes para efetuar a
-- importação dos veículos.
--
-- Pré-requisitos:
-- function REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS criada.
-- Histórico:
-- 2019-11-06 -> Function criada (thaisksf - PL-2318).
-- 2019-12-16 -> Corrige inserção de modelo e tipo (thaisksf - PL-2318)
-- 2019-12-16 -> Corrige inserção de modelo e tipo (thaisksf - PL-2318)
-- 2020-02-14 -> Altera function para ser SECURITY DEFINER e para utilizar a view VEICULO (luizfp).
-- 2020-03-26 -> Corrige insert de veículo, salvando também o código do diagrama (luizfp).
-- 2020-04-06 -> Corrige update de veículo, salvanto também o codigo do diagrama (thaisksf).
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
        NEW.STATUS_IMPORT_REALIZADO := OLD.STATUS_IMPORT_REALIZADO;
        NEW.USUARIO_UPDATE := OLD.USUARIO_UPDATE;
    ELSE
        NEW.PLACA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PLACA_EDITAVEL);
        NEW.MARCA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.TIPO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TIPO_EDITAVEL);
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
                    COD_DIAGRAMA = F_COD_DIAGRAMA_BANCO
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
                                         COD_DIAGRAMA)
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
                            F_COD_DIAGRAMA_BANCO);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                    NEW.ERROS_ENCONTRADOS = '-';
                END IF;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;