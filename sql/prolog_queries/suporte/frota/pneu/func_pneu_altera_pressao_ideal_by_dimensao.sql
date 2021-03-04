-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
--
-- Précondições:
--
-- Histórico:
-- PL-2063
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_DIMENSAO(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_COD_DIMENSAO BIGINT,
                                                                              F_NOVA_PRESSAO_RECOMENDADA BIGINT,
                                                                              F_QTD_PNEUS_IMPACTADOS BIGINT,
                                                                              OUT AVISO_PRESSAO_ALTERADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_REAL_PNEUS_IMPACTADOS  BIGINT;
    PRESSAO_MINIMA_RECOMENDADA BIGINT := 25;
    PRESSAO_MAXIMA_RECOMENDADA BIGINT := 150;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --Verifica se a pressao informada está dentro das recomendadas.
    IF (F_NOVA_PRESSAO_RECOMENDADA NOT BETWEEN PRESSAO_MINIMA_RECOMENDADA AND PRESSAO_MAXIMA_RECOMENDADA)
    THEN
        RAISE EXCEPTION 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', PRESSAO_MINIMA_RECOMENDADA,
            PRESSAO_MAXIMA_RECOMENDADA;
    END IF;

    -- Verifica se a empresa existe.
    IF NOT EXISTS(SELECT E.CODIGO
                  FROM EMPRESA E
                  WHERE E.CODIGO = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'Empresa de código % não existe!', F_COD_EMPRESA;
    END IF;

    -- Verifica se a unidade existe.
    IF NOT EXISTS(SELECT U.CODIGO
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE;
    END IF;

    -- Verifica se existe a dimensão informada.
    IF NOT EXISTS(SELECT DM.CODIGO
                  FROM DIMENSAO_PNEU DM
                  WHERE DM.CODIGO = F_COD_DIMENSAO)
    THEN
        RAISE EXCEPTION 'Dimensao de código % não existe!', F_COD_DIMENSAO;
    END IF;

    -- Verifica se a unidade é da empresa informada.
    IF NOT EXISTS(SELECT U.CODIGO
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE
                    AND U.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'A unidade % não pertence a empresa %!', F_COD_UNIDADE, F_COD_EMPRESA;
    END IF;

    -- Verifica se algum pneu possui dimensão informada.
    IF NOT EXISTS(SELECT P.COD_DIMENSAO
                  FROM PNEU P
                  WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
                    AND P.COD_UNIDADE = F_COD_UNIDADE
                    AND P.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'Não existem pneus com a dimensão % na unidade %', F_COD_DIMENSAO, F_COD_UNIDADE;
    END IF;

    -- Verifica quantidade de pneus impactados.
    SELECT COUNT(P.CODIGO)
    FROM PNEU P
    WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
      AND P.COD_UNIDADE = F_COD_UNIDADE
      AND P.COD_EMPRESA = F_COD_EMPRESA
    INTO QTD_REAL_PNEUS_IMPACTADOS;
    IF (QTD_REAL_PNEUS_IMPACTADOS <> F_QTD_PNEUS_IMPACTADOS)
    THEN
        RAISE EXCEPTION 'A quantidade de pneus informados como impactados pela mudança de pressão (%) não condiz com a
                       quantidade real de pneus que serão afetados!', F_QTD_PNEUS_IMPACTADOS;
    END IF;

    UPDATE PNEU
    SET PRESSAO_RECOMENDADA = F_NOVA_PRESSAO_RECOMENDADA
    WHERE COD_DIMENSAO = F_COD_DIMENSAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_EMPRESA = F_COD_EMPRESA;

    SELECT CONCAT('Pressão recomendada dos pneus com dimensão ',
                  F_COD_DIMENSAO,
                  ' da unidade ',
                  F_COD_UNIDADE,
                  ' alterada para ',
                  F_NOVA_PRESSAO_RECOMENDADA,
                  ' psi')
    INTO AVISO_PRESSAO_ALTERADA;
END;
$$;