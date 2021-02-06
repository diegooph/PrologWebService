-- Sobre:
--
-- Esta função realiza o upsert das configurações de aferição por unidade.
--
-- Histórico:
-- 2019-12-10 -> Adição de colunas de bloqueio  (wvinim - PL-1934).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(F_CODIGO BIGINT,
                                                                    F_COD_UNIDADE BIGINT,
                                                                    F_VARIACAO_SULCO_MENOR DOUBLE PRECISION,
                                                                    F_VARIACAO_SULCO_MAIOR DOUBLE PRECISION,
                                                                    F_BLOQUEAR_VALORES_MENORES BOOLEAN,
                                                                    F_BLOQUEAR_VALORES_MAIORES BOOLEAN)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF F_CODIGO IS NULL
    THEN
        INSERT INTO AFERICAO_CONFIGURACAO_ALERTA_SULCO (COD_UNIDADE,
                                                        VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
                                                        VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
                                                        BLOQUEAR_VALORES_MENORES,
                                                        BLOQUEAR_VALORES_MAIORES)
        VALUES (F_COD_UNIDADE,
                F_VARIACAO_SULCO_MENOR,
                F_VARIACAO_SULCO_MAIOR,
                F_BLOQUEAR_VALORES_MENORES,
                F_BLOQUEAR_VALORES_MAIORES);
    ELSE
        UPDATE AFERICAO_CONFIGURACAO_ALERTA_SULCO
        SET VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS = F_VARIACAO_SULCO_MENOR,
            VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS = F_VARIACAO_SULCO_MAIOR,
            BLOQUEAR_VALORES_MENORES               = F_BLOQUEAR_VALORES_MENORES,
            BLOQUEAR_VALORES_MAIORES               = F_BLOQUEAR_VALORES_MAIORES
        WHERE CODIGO = F_CODIGO;
    END IF;

    -- Validamos se houve alguma inserção ou atualização dos valores.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('Erro ao atualizar configurações da unidade %s', F_COD_UNIDADE));
    END IF;

    RETURN FOUND;
END;
$$;