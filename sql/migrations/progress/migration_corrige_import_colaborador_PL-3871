CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_COLABORADOR_CONFERE_PLANILHA_IMPORTA_COLABORADOR()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_VALOR_SIMILARIDADE CONSTANT REAL     := 0.45;
    V_QTD_ERROS                   SMALLINT := 0;
    V_MSGS_ERROS                  TEXT;
    V_QUEBRA_LINHA                TEXT     := CHR(10);
    V_COD_EQUIPE_BANCO            BIGINT;
    V_SIMILARIDADE_EQUIPE         REAL;
    V_COD_SETOR_BANCO             BIGINT;
    V_SIMILARIDADE_SETOR          REAL;
    V_COD_FUNCAO_BANCO            BIGINT;
    V_SIMILARIDADE_FUNCAO         REAL;
    V_EXISTE_FUNCAO_DELETADA      BOOLEAN;
    V_VALOR_SIMILARIDADE_PAISES   REAL     := 0.4;
    V_NOME_PAIS                   VARCHAR(255);
    V_SIGLA_ISO2                  CHAR(2);
    V_EXISTE_TELEFONE             BOOLEAN  := FALSE;
    V_SIMILARIDADE_PAISES         REAL;
    V_PREFIXO_PAIS                INTEGER;
    V_EXISTE_EMAIL                BOOLEAN  := FALSE;
    V_COD_COLABORADOR_CADASTRADO  BIGINT;

BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        RETURN OLD;
    ELSE
        IF (TG_OP = 'UPDATE')
        THEN
            NEW.COD_UNIDADE := OLD.COD_UNIDADE;
            NEW.COD_EMPRESA := OLD.COD_EMPRESA;
        END IF;
        NEW.USUARIO_UPDATE := SESSION_USER;
        NEW.NOME_FORMATADO_IMPORT := UPPER(NEW.NOME_EDITAVEL);
        NEW.EQUIPE_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.EQUIPE_EDITAVEL);
        NEW.SETOR_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.SETOR_EDITAVEL);
        NEW.FUNCAO_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.FUNCAO_EDITAVEL);

        -- VERIFICA SE EMPRESA EXISTE.
        IF NOT EXISTS(SELECT E.CODIGO FROM EMPRESA E WHERE E.CODIGO = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE EMPRESA COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE EXISTE.
        IF NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- NÃO EXISTE UNIDADE COM CÓDIGO INFORMADO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICA SE UNIDADE PERTENCE A EMPRESA.
        IF NOT EXISTS(
                SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE AND U.COD_EMPRESA = NEW.COD_EMPRESA)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A UNIDADE NÃO PERTENCE A EMPRESA', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES CPF.
        -- Nulo: erro.
        -- Com mais de 11 dígitos: erro.
        -- Com menos de 7 dígitos: erro.
        -- Com letras: erro.
        -- Cadastrado em outra unidade da mesma empresa: erro.
        -- Cadastrado na mesma unidade: erro.
        -- Cadastrado em outra empresa: erro.
        IF (NEW.CPF_EDITAVEL IS NOT NULL)
        THEN
            -- CPF com mais de 11 dígitos.
            IF (LENGTH(NEW.CPF_EDITAVEL) > 11)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                      '- O CPF DEVE CONTER ATÉ 11 DÍGITOS',
                                      V_QUEBRA_LINHA);
            ELSE
                -- CPF com menos de 7 dígitos.
                IF (LENGTH(NEW.CPF_EDITAVEL) < 7)
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                          '- O CPF DEVE CONTER MAIS QUE 7 DÍGITOS',
                                          V_QUEBRA_LINHA);
                ELSE
                    --CPF com letras.
                    IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.CPF_EDITAVEL)) IS FALSE)
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                              '- O CPF DEVE CONTER APENAS NÚMEROS',
                                              V_QUEBRA_LINHA);
                    ELSE
                        NEW.CPF_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.CPF_EDITAVEL);
                        -- CPF cadastrado na mesma empresa mas em outra unidade.
                        IF EXISTS(SELECT CD.CPF
                                  FROM COLABORADOR_DATA CD
                                  WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                    AND CD.COD_EMPRESA = NEW.COD_EMPRESA
                                    AND CD.COD_UNIDADE != NEW.COD_UNIDADE)
                        THEN
                            V_QTD_ERROS = V_QTD_ERROS + 1;
                            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                  '- O CPF JÁ ESTÁ CADASTRADO E PERTENCE A OUTRA UNIDADE',
                                                  V_QUEBRA_LINHA);
                            NEW.STATUS_IMPORT_REALIZADO = TRUE;
                        ELSE
                            -- CPF cadastrado na mesma unidade.
                            IF EXISTS(SELECT CD.CPF
                                      FROM COLABORADOR_DATA CD
                                      WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                        AND CD.COD_EMPRESA = NEW.COD_EMPRESA
                                        AND CD.COD_UNIDADE = NEW.COD_UNIDADE)
                            THEN
                                V_QTD_ERROS = V_QTD_ERROS + 1;
                                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                      '- O CPF JÁ ESTÁ CADASTRADO NA UNIDADE INFORMADA',
                                                      V_QUEBRA_LINHA);
                                NEW.STATUS_IMPORT_REALIZADO = TRUE;
                            ELSE
                                -- CPF em outra empresa.
                                IF EXISTS(SELECT CD.CPF
                                          FROM COLABORADOR_DATA CD
                                          WHERE CD.CPF = NEW.CPF_FORMATADO_IMPORT
                                            AND CD.COD_EMPRESA != NEW.COD_EMPRESA)
                                THEN
                                    V_QTD_ERROS = V_QTD_ERROS + 1;
                                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS,
                                                          '- O CPF JÁ ESTÁ CADASTRADO EM OUTRA EMPRESA',
                                                          V_QUEBRA_LINHA);
                                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                                END IF;
                            END IF;
                        END IF;
                    END IF;
                END IF;
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O CPF NÃO PODE SER NULO', V_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES PIS.
        -- PIS com menos de 11 dígitos: erro.
        IF ((NEW.PIS_EDITAVEL IS NOT NULL) AND (LENGTH(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL)) <> 11))
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PIS DEVE CONTER 11 DÍGITOS', V_QUEBRA_LINHA);
        ELSE
            IF ((NEW.PIS_EDITAVEL IS NOT NULL) AND
                (LENGTH(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL)) = 11))
            THEN
                NEW.PIS_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PIS_EDITAVEL);
            END IF;
        END IF;

        -- Data de nascimento.
        -- Nula: erro.
        -- Maior que data atual: erro.
        IF (NEW.DATA_NASCIMENTO_EDITAVEL IS NOT NULL)
        THEN
            -- Utilizamos o LTRIM para remover zeros à esquerda. Exemplo: se n retirarmos o '0' uma data errada como:
            -- 0111990, seria cadastrado como 01-11-1990 pois pegaria o primeiro dia do mês 11.
            NEW.DATA_NASCIMENTO_EDITAVEL = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.DATA_NASCIMENTO_EDITAVEL);
            IF (LENGTH(LTRIM(NEW.DATA_NASCIMENTO_EDITAVEL, '0')) > 6)
            THEN
                NEW.DATA_NASCIMENTO_FORMATADA_IMPORT := TO_DATE(LPAD(NEW.DATA_NASCIMENTO_EDITAVEL, 8, '0'), 'DDMMYYYY');
                IF (NEW.DATA_NASCIMENTO_FORMATADA_IMPORT > NOW())
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE NASCIMENTO NÃO PODE SER MAIOR QUE A
                                                                     DATA ATUAL', V_QUEBRA_LINHA);
                END IF;
            ELSE
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- DATA DE NASCIMENTO INVÁLIDA', V_QUEBRA_LINHA);
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE NASCIMENTO NÃO PODE SER NULA', V_QUEBRA_LINHA);
        END IF;

        -- Data de admissão.
        -- Nula: erro.
        -- Maior que data atual: erro.
        IF (NEW.DATA_ADMISSAO_EDITAVEL IS NOT NULL)
        THEN
            -- Utilizamos o LTRIM para remover zeros à esquerda. Exemplo: se n retirarmos o '0' uma data errada como:
            -- 0111990, seria cadastrado como 01-11-1990 pois pegaria o primeiro dia do mês 11.
            NEW.DATA_ADMISSAO_EDITAVEL = REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.DATA_ADMISSAO_EDITAVEL);
            IF (LENGTH(LTRIM(NEW.DATA_ADMISSAO_EDITAVEL, '0')) > 6)
            THEN
                NEW.DATA_ADMISSAO_FORMATADA_IMPORT := TO_DATE(LPAD(NEW.DATA_ADMISSAO_EDITAVEL, 8, '0'), 'DDMMYYYY');
                IF (NEW.DATA_ADMISSAO_FORMATADA_IMPORT > NOW())
                THEN
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE ADMISSÃO NÃO PODE SER MAIOR QUE A DATA
                                                                     ATUAL', V_QUEBRA_LINHA);
                END IF;
            ELSE
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- DATA DE ADMISSÃO INVÁLIDA', V_QUEBRA_LINHA);
            END IF;
        ELSE
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS =
                    CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A DATA DE ADMISSÃO NÃO PODE SER NULA', V_QUEBRA_LINHA);
        END IF;

        -- Matrícula Promax.
        -- Com letras: erro.
        IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PROMAX_EDITAVEL)) IS FALSE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A MATRÍCULA PROMAX DEVE CONTER APENAS NÚMEROS',
                                  V_QUEBRA_LINHA);
        ELSE
            NEW.MATRICULA_PROMAX_FORMATADA_IMPORT :=
                    REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PROMAX_EDITAVEL);
        END IF;

        -- Matricula ponto
        -- Com letras: erro.
        IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PONTO_EDITAVEL)) IS FALSE)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A MATRÍCULA PONTO DEVE CONTER APENAS NÚMEROS',
                                  V_QUEBRA_LINHA);
        ELSE
            NEW.MATRICULA_PONTO_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MATRICULA_PONTO_EDITAVEL);
        END IF;

        -- Equipe.
        -- Nula: erro.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        IF (NEW.EQUIPE_FORMATADA_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A EQUIPE NÃO PODE SER NULA.', V_QUEBRA_LINHA);
        ELSE
            -- Procura equipe similar no banco.
            SELECT DISTINCT ON
                (NEW.EQUIPE_FORMATADA_IMPORT) E.CODIGO                                                         AS COD_EQUIPE,
                                              MAX(FUNC_GERA_SIMILARIDADE(NEW.EQUIPE_FORMATADA_IMPORT, E.NOME)) AS SIMILARIEDADE_EQUIPE
            INTO V_COD_EQUIPE_BANCO, V_SIMILARIDADE_EQUIPE
            FROM EQUIPE E
            WHERE E.COD_UNIDADE = NEW.COD_UNIDADE
            GROUP BY NEW.EQUIPE_FORMATADA_IMPORT, E.NOME, E.CODIGO
            ORDER BY NEW.EQUIPE_FORMATADA_IMPORT, SIMILARIEDADE_EQUIPE DESC;

            -- Se a similaridade da equipe for menor que o exigido ou nula: Cadastra.
            IF (V_SIMILARIDADE_EQUIPE < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_EQUIPE IS NULL)
            THEN
                INSERT INTO EQUIPE (NOME, COD_UNIDADE)
                VALUES (NEW.EQUIPE_EDITAVEL, NEW.COD_UNIDADE) RETURNING CODIGO INTO V_COD_EQUIPE_BANCO;
            END IF;
        END IF;

        -- Setor.
        -- Nulo: erro.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        IF (NEW.SETOR_FORMATADO_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O SETOR NÃO PODE SER NULO.', V_QUEBRA_LINHA);
        ELSE
            -- Procura setor similar no banco.
            SELECT DISTINCT ON
                (NEW.SETOR_FORMATADO_IMPORT) S.CODIGO                                                        AS COD_SETOR,
                                             MAX(FUNC_GERA_SIMILARIDADE(NEW.SETOR_FORMATADO_IMPORT, S.NOME)) AS SIMILARIEDADE_SETOR
            INTO V_COD_SETOR_BANCO, V_SIMILARIDADE_SETOR
            FROM SETOR S
            WHERE S.COD_UNIDADE = NEW.COD_UNIDADE
            GROUP BY NEW.SETOR_FORMATADO_IMPORT, S.NOME, S.CODIGO
            ORDER BY NEW.SETOR_FORMATADO_IMPORT, SIMILARIEDADE_SETOR DESC;

            -- Se a similaridade do setor for menor que o exigido ou nula: Cadastra.
            IF (V_SIMILARIDADE_SETOR < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_SETOR IS NULL)
            THEN
                INSERT INTO SETOR (NOME, COD_UNIDADE)
                VALUES (NEW.SETOR_EDITAVEL, NEW.COD_UNIDADE) RETURNING CODIGO INTO V_COD_SETOR_BANCO;
            END IF;

        END IF;

        -- Função.
        -- Nula: erro.
        -- Deletada: update para remover a deleção.
        -- Não similar: cadastra.
        -- Não existente: cadastra.
        -- Caso exista
        IF (NEW.FUNCAO_FORMATADA_IMPORT IS NULL)
        THEN
            V_QTD_ERROS = V_QTD_ERROS + 1;
            V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- A FUNÇÃO NÃO PODE SER NULA.', V_QUEBRA_LINHA);
        ELSE
            -- Procura função similar no banco que não esteja deletada.
            SELECT DISTINCT ON
                (NEW.FUNCAO_FORMATADA_IMPORT) F.CODIGO                                           AS COD_FUNCAO,
                                              MAX(FUNC_GERA_SIMILARIDADE(NEW.FUNCAO_FORMATADA_IMPORT, F.NOME)) AS SIMILARIEDADE_FUNCAO
            INTO V_COD_FUNCAO_BANCO, V_SIMILARIDADE_FUNCAO
            FROM FUNCAO F
            WHERE F.COD_EMPRESA = NEW.COD_EMPRESA
            GROUP BY NEW.FUNCAO_FORMATADA_IMPORT, F.NOME, F.CODIGO
            ORDER BY NEW.FUNCAO_FORMATADA_IMPORT, SIMILARIEDADE_FUNCAO DESC;

            -- Se a similaridade da funcao for menor que o exigido ou nula: Verifica se há alguma similar deletada.
            IF (V_SIMILARIDADE_FUNCAO < V_VALOR_SIMILARIDADE OR V_SIMILARIDADE_FUNCAO IS NULL)
            THEN
                SELECT DISTINCT ON
                    (NEW.FUNCAO_FORMATADA_IMPORT) FD.CODIGO                    AS COD_FUNCAO,
                                                  MAX(FUNC_GERA_SIMILARIDADE(
                                                          NEW.FUNCAO_FORMATADA_IMPORT, FD.NOME)) AS SIMILARIEDADE_FUNCAO,
                                                  FD.DELETADO
                INTO V_COD_FUNCAO_BANCO, V_SIMILARIDADE_FUNCAO, V_EXISTE_FUNCAO_DELETADA
                FROM FUNCAO_DATA FD
                WHERE FD.COD_EMPRESA = NEW.COD_EMPRESA AND FD.DELETADO = TRUE
                GROUP BY NEW.FUNCAO_FORMATADA_IMPORT, FD.NOME, FD.CODIGO
                ORDER BY NEW.FUNCAO_FORMATADA_IMPORT, SIMILARIEDADE_FUNCAO DESC;
                -- Se a similaridade da funcao for maior que o exigido e ela estiver deletada: Realiza update.
                IF (V_SIMILARIDADE_FUNCAO > V_VALOR_SIMILARIDADE AND V_EXISTE_FUNCAO_DELETADA)
                THEN
                    UPDATE FUNCAO_DATA
                    SET DELETADO            = FALSE,
                        DATA_HORA_DELETADO  = NULL,
                        PG_USERNAME_DELECAO = NULL
                    WHERE CODIGO = V_COD_FUNCAO_BANCO
                      AND COD_EMPRESA = NEW.COD_EMPRESA;
                    -- Se não, cadastra.
                ELSE
                    INSERT INTO FUNCAO_DATA (NOME, COD_EMPRESA)
                    VALUES (NEW.FUNCAO_EDITAVEL, NEW.COD_EMPRESA) RETURNING CODIGO INTO V_COD_FUNCAO_BANCO;
                END IF;
            END IF;
        END IF;

        -- Telefone.
        -- Com letras ou caracteres especiais: erro.
        -- Sem pais: erro.
        IF ((NEW.TELEFONE_EDITAVEL IS NOT NULL) AND (LENGTH(NEW.TELEFONE_EDITAVEL) > 0))
        THEN
            IF (CHECK_NON_NUMERIC_CHARACTERS(REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TELEFONE_EDITAVEL)) IS FALSE)
            THEN
                V_QTD_ERROS = V_QTD_ERROS + 1;
                V_MSGS_ERROS =
                        CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O TELEFONE DEVE CONTER APENAS NÚMEROS', V_QUEBRA_LINHA);
            ELSE
                -- Verifica pais
                IF (NEW.PAIS_EDITAVEL IS NOT NULL)
                THEN
                    NEW.PAIS_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PAIS_EDITAVEL);
                    -- Verifica similaridade do pais para pegar sigla.
                    SELECT DISTINCT ON
                        (NEW.PAIS_FORMATADO_IMPORT) PP.NOME                                                         AS NOME_PAIS,
                                                    PP.SIGLA_ISO2                                                   AS SIGLA_ISO2,
                                                    PP.PREFIXO_TELEFONE                                             AS PREFIXO_PAIS,
                                                    MAX(FUNC_GERA_SIMILARIDADE(NEW.PAIS_FORMATADO_IMPORT, PP.NOME)) AS SIMILARIEDADE_PAISES
                    INTO V_NOME_PAIS, V_SIGLA_ISO2, V_PREFIXO_PAIS, V_SIMILARIDADE_PAISES
                    FROM PROLOG_PAISES PP
                    GROUP BY NEW.PAIS_FORMATADO_IMPORT, PP.NOME, PP.SIGLA_ISO2, PREFIXO_TELEFONE
                    ORDER BY NEW.PAIS_FORMATADO_IMPORT, SIMILARIEDADE_PAISES DESC;

                    IF (V_SIMILARIDADE_PAISES < V_VALOR_SIMILARIDADE_PAISES OR V_SIMILARIDADE_PAISES IS NULL)
                    THEN
                        V_QTD_ERROS = V_QTD_ERROS + 1;
                        V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PAIS NÃO FOI ENCONTRADO', V_QUEBRA_LINHA);
                    ELSE
                        NEW.TELEFONE_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TELEFONE_EDITAVEL);
                        V_EXISTE_TELEFONE := TRUE;
                    END IF;
                ELSE
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS = CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- O PAIS NÃO PODE SER NULO', V_QUEBRA_LINHA);
                END IF;
            END IF;
        END IF;

        -- E-mail.
        -- Se não for do tipo EMAIL: erro.
        IF ((NEW.EMAIL_EDITAVEL IS NOT NULL) AND (LENGTH(NEW.TELEFONE_EDITAVEL) > 0))
        THEN
            BEGIN
                NEW.EMAIL_FORMATADO_IMPORT := REMOVE_ALL_SPACES(NEW.EMAIL_EDITAVEL);
            EXCEPTION
                WHEN INVALID_TEXT_REPRESENTATION THEN -- error that can be handled
                    V_QTD_ERROS = V_QTD_ERROS + 1;
                    V_MSGS_ERROS =
                            CONCAT(V_MSGS_ERROS, V_QTD_ERROS, '- E-MAIL INCORRETO', V_QUEBRA_LINHA);
            END;
            V_EXISTE_EMAIL = TRUE;
        END IF;


        IF (V_QTD_ERROS > 0)
        THEN
            NEW.ERROS_ENCONTRADOS = V_MSGS_ERROS;
        ELSE
            INSERT INTO COLABORADOR_DATA (CPF,
                                          MATRICULA_AMBEV,
                                          MATRICULA_TRANS,
                                          DATA_NASCIMENTO,
                                          DATA_ADMISSAO,
                                          NOME,
                                          COD_EQUIPE,
                                          COD_FUNCAO,
                                          COD_UNIDADE,
                                          COD_PERMISSAO,
                                          COD_EMPRESA,
                                          COD_SETOR,
                                          PIS,
                                          COD_UNIDADE_CADASTRO,
                                          DELETADO)

            VALUES (NEW.CPF_FORMATADO_IMPORT,
                    NEW.MATRICULA_PROMAX_FORMATADA_IMPORT,
                    NEW.MATRICULA_PONTO_FORMATADA_IMPORT,
                    NEW.DATA_NASCIMENTO_FORMATADA_IMPORT,
                    NEW.DATA_ADMISSAO_FORMATADA_IMPORT,
                    NEW.NOME_FORMATADO_IMPORT,
                    V_COD_EQUIPE_BANCO,
                    V_COD_FUNCAO_BANCO,
                    NEW.COD_UNIDADE,
                    0,
                    NEW.COD_EMPRESA,
                    V_COD_SETOR_BANCO,
                    NEW.PIS_FORMATADO_IMPORT,
                    NEW.COD_UNIDADE,
                    FALSE) RETURNING CODIGO INTO V_COD_COLABORADOR_CADASTRADO;

            IF (V_EXISTE_TELEFONE IS TRUE)
            THEN
                INSERT INTO COLABORADOR_TELEFONE(COD_COLABORADOR,
                                                 SIGLA_ISO2,
                                                 PREFIXO_PAIS,
                                                 NUMERO_TELEFONE,
                                                 COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                VALUES (V_COD_COLABORADOR_CADASTRADO,
                        V_SIGLA_ISO2,
                        V_PREFIXO_PAIS,
                        NEW.TELEFONE_FORMATADO_IMPORT,
                        V_COD_COLABORADOR_CADASTRADO);
            END IF;

            IF (V_EXISTE_EMAIL IS TRUE)
            THEN
                INSERT INTO COLABORADOR_EMAIL(COD_COLABORADOR,
                                              EMAIL,
                                              COD_COLABORADOR_ULTIMA_ATUALIZACAO)
                VALUES (V_COD_COLABORADOR_CADASTRADO,
                        NEW.EMAIL_FORMATADO_IMPORT,
                        V_COD_COLABORADOR_CADASTRADO);
            END IF;
            NEW.STATUS_IMPORT_REALIZADO = TRUE;
            NEW.ERROS_ENCONTRADOS = '-';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;