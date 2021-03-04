-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Deleta um serviço específico de uma aferição. Os Serviços existem na tabela AFERICAO_MANUTENCAO.
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER. (natanrotta - PL-2229).
-- 2019-09-18 -> Adicona no schema de suporte. (natanrotta - PL-2242).
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
                                                                         F_COD_UNIDADE BIGINT,
                                                                         F_COD_PNEU BIGINT,
                                                                         F_NUMERO_FOGO TEXT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_COD_SERVICO_AFERICAO BIGINT,
                                                                         F_TIPO_SERVICO_AFERICAO TEXT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_TIPO_SERVICO_AFERICAO TEXT := LOWER(F_TIPO_SERVICO_AFERICAO);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --Garante integridade entre unidade e empresa
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);
    --Verifica se o pneu existe
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO);
    --Verifica se existe afericao
    IF NOT EXISTS(SELECT A.CODIGO
                  FROM AFERICAO A
                  WHERE A.CODIGO = F_CODIGO_AFERICAO
                    AND A.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Aferição de código % não existe para a unidade % - %', F_CODIGO_AFERICAO, F_COD_UNIDADE,
            (SELECT NOME
             FROM UNIDADE
             WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    --Verifica se existe serviço de afericao
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_SERVICO_AFERICAO
                    AND AM.COD_AFERICAO = F_CODIGO_AFERICAO
                    AND AM.COD_PNEU = F_COD_PNEU
                    AND AM.COD_UNIDADE = F_COD_UNIDADE
                    AND AM.TIPO_SERVICO = F_TIPO_SERVICO_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não existe serviço de aferição com código: %, do tipo: "%", código de aferição: %,
     e codigo de pneus: %
                      para a unidade % - %', F_COD_SERVICO_AFERICAO, F_TIPO_SERVICO_AFERICAO,
            F_CODIGO_AFERICAO, F_COD_PNEU, F_COD_UNIDADE, (SELECT NOME
                                                           FROM UNIDADE
                                                           WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    -- Deleta aferição manutenção.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_SERVICO_AFERICAO
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_PNEU = F_COD_PNEU;
    SELECT 'SERVIÇO DE AFERIÇÃO DELETADO: '
               || F_COD_SERVICO_AFERICAO
               || ', DO TIPO: '
               || F_TIPO_SERVICO_AFERICAO
               || ', CODIGO DE AFERIÇÃO: '
               || F_CODIGO_AFERICAO
               || ', CÓDIGO PNEU: '
               || F_COD_PNEU
               || ', UNIDADE: '
               || F_COD_UNIDADE
               || ' - '
               || (SELECT U.NOME
                   FROM UNIDADE U
                   WHERE U.CODIGO = F_COD_UNIDADE)
    INTO AVISO_SERVICO_AFERICAO_DELETADO;
END
$$;