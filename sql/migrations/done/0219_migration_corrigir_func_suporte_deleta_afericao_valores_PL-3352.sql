drop function if exists suporte.func_afericao_deleta_afericao_valores(f_cod_unidade bigint,
                                                                      f_codigo_pneu bigint,
                                                                      f_codigo_afericao bigint,
                                                                      f_motivo_delecao text);
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
                                                                         F_CODIGO_PNEU BIGINT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS   BIGINT;
    -- Busca a quantidade de valores aferidos estão ativos nesta aferição
    V_QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                          FROM AFERICAO_VALORES
                                          WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                            AND COD_UNIDADE = F_COD_UNIDADE);

    -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
    V_PREFIXO_MENSAGEM_RETORNO TEXT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    IF NOT EXISTS(SELECT *
                  FROM AFERICAO_VALORES
                  WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                    AND COD_UNIDADE = F_COD_UNIDADE
                    AND COD_PNEU = F_CODIGO_PNEU)
    THEN
        RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Pneu %
            e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
    END IF;

    -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
    CASE V_QTD_VALORES_AFERICAO
        WHEN 1
            THEN
                -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
                PERFORM SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE,
                                                              F_CODIGO_AFERICAO,
                                                              F_MOTIVO_DELECAO);
                V_PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
        ELSE
            -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
            -- DELETA AFERIÇÃO.
            UPDATE AFERICAO_VALORES_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
            THEN
                RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %,
                    Pneu % e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
            END IF;

            -- DELETA AFERIÇÃO MANUTENÇÃO.
            -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
            UPDATE AFERICAO_MANUTENCAO_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            V_PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
        END CASE;

    SELECT V_PREFIXO_MENSAGEM_RETORNO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ', CÓDIGO DO PNEU: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_CODIGO_AFERICAO
    INTO AVISO_AFERICAO_VALOR_DELETADA;
END
$$;