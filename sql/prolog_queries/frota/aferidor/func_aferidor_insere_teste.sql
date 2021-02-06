-- Sobre:
-- Insere os valores de um procedimento de testes executados pelo aplicativo no aferidor.
--
-- Histórico:
-- 2019-10-08 -> Function criada (luizfp - PL-2343).
CREATE OR REPLACE FUNCTION AFERIDOR.FUNC_AFERIDOR_INSERE_TESTE(F_COD_COLABORADOR_EXECUCAO BIGINT,
                                                               F_DATA_HORA_EXECUCAO TIMESTAMP WITH TIME ZONE,
                                                               F_NOME_DISPOSITIVO TEXT,
                                                               F_COMANDOS_EXECUTADOS JSONB)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS
$$
DECLARE
  CODIGO_PROCEDIMENTO_TESTE BIGINT;
BEGIN
  INSERT INTO AFERIDOR.PROCEDIMENTO_TESTE (COD_COLABORADOR_EXECUCAO,
                                           DATA_HORA_EXECUCAO,
                                           NOME_DISPOSITIVO,
                                           VALORES_EXECUCAO)
  VALUES (F_COD_COLABORADOR_EXECUCAO,
          F_DATA_HORA_EXECUCAO,
          F_NOME_DISPOSITIVO,
          F_COMANDOS_EXECUTADOS) RETURNING CODIGO INTO CODIGO_PROCEDIMENTO_TESTE;

  IF NOT FOUND
  THEN
    RAISE EXCEPTION 'Erro ao salvar teste realizado!';
  END IF;

  RETURN CODIGO_PROCEDIMENTO_TESTE;
END;
$$;