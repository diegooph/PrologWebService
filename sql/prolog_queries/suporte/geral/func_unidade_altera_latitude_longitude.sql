-- Sobre:
-- Function para alterar a latitude e longitude de uma unidade.
--
-- Histórico:
-- 2020-03-04 -> Function criada (luizfp).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_UNIDADE_ALTERA_LATITUDE_LONGITUDE(F_COD_UNIDADE BIGINT,
                                                                          F_LATITUDE_UNIDADE TEXT,
                                                                          F_LONGITUDE_UNIDADE TEXT,
                                                                          OUT AVISO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    UPDATE UNIDADE
    SET LATITUDE_UNIDADE  = F_LATITUDE_UNIDADE,
        LONGITUDE_UNIDADE = F_LONGITUDE_UNIDADE
    WHERE CODIGO = F_COD_UNIDADE;

    SELECT 'LATITUDE E LONGITUDE DA UNIDADE '
               || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE)
               || ', CÓDIGO UNIDADE: '
               || F_COD_UNIDADE
               || ' ALTERADAS.'
    INTO AVISO;
END ;
$$;