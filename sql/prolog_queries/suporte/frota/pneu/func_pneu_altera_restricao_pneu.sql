-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- É feita todas as validações em cima dos dados. Caso o dado seja null, a função vai assumir o mesmo valor
-- da coluna que o dado se refere antes do update.
--
-- Précondições:
-- Empresa existir
-- Unidade existir
-- Empresa possuir unidade
-- Empresa possuir parametrização na unidade
-- Todos os dados devem ser maiores que 0(zero)
-- Se um dado entrar como null, ele assume o mesmo valor que está na tabela antes do update
--
-- Histórico:
-- 2019-10-22 -> Function criada (natanrotta - PL-2337).
-- 2019-11-06 -> Altera nome function de SUPORTE.FUNC_PNEU_ALTERA_PARAMETRIZACAO_PNEU
-- para SUPORTE.FUNC_PNEU_ALTERA_RESTRICAO_PNEU (luizfp).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_RESTRICAO_PNEU(F_COD_EMPRESA BIGINT,
                                                                   F_COD_UNIDADE BIGINT,
                                                                   F_TOLERANCIA_CALIBRAGEM REAL,
                                                                   F_TOLERANCIA_INSPECAO REAL,
                                                                   F_SULCO_MINIMO_RECAPAGEM REAL,
                                                                   F_SULCO_MINIMO_DESCARTE REAL,
                                                                   F_PERIODO_AFERICAO_PRESSAO BIGINT,
                                                                   F_PERIODO_AFERICAO_SULCO BIGINT,
                                                                   OUT PARAMETRIZACAO_ATUALIZADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_PARAMETRIZACAO_EXISTENTE   BIGINT := (SELECT COD_EMPRESA
                                              FROM PNEU_RESTRICAO_UNIDADE
                                              WHERE COD_EMPRESA = F_COD_EMPRESA
                                                AND COD_UNIDADE = F_COD_UNIDADE);
    TOLERANCIA_CALIBRAGEM_ATUAL    REAL;
    SULCO_MINIMO_RECAPAGEM_ATUAL   REAL;
    SULCO_MINIMO_DESCARTE_ATUAL    REAL;
    TOLERANCIA_INSPECAO_ATUAL      REAL;
    PERIODO_AFERICAO_PRESSAO_ATUAL BIGINT;
    PERIODO_AFERICAO_SULCO_ATUAL   BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --SETAR VARIÁVEIS
    SELECT TOLERANCIA_CALIBRAGEM,
           SULCO_MINIMO_RECAPAGEM,
           SULCO_MINIMO_DESCARTE,
           TOLERANCIA_INSPECAO,
           PERIODO_AFERICAO_PRESSAO,
           PERIODO_AFERICAO_SULCO
    INTO
        TOLERANCIA_CALIBRAGEM_ATUAL,
        SULCO_MINIMO_RECAPAGEM_ATUAL,
        SULCO_MINIMO_DESCARTE_ATUAL,
        TOLERANCIA_INSPECAO_ATUAL,
        PERIODO_AFERICAO_PRESSAO_ATUAL,
        PERIODO_AFERICAO_SULCO_ATUAL
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --GARANTE QUE EMPRESA POSSUI UNIDADE.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    --VERIFICA SE A EMPRESA POSSUI PARAMETRIZACÃO NESSA UNIDADE.
    IF (COD_PARAMETRIZACAO_EXISTENTE IS NULL)
    THEN
        RAISE EXCEPTION 'ERRO! A EMPRESA: % NÃO POSSUI PARAMETRIZAÇÃO NA UNIDADE: %', F_COD_EMPRESA, F_COD_UNIDADE;
    END IF;

    --VERIFICA SE ALGUM DADO É MENOR QUE ZERO.
    IF (F_TOLERANCIA_CALIBRAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA DE CALIBRAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_RECAPAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO RECAPAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_DESCARTE < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO DESCARTE DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_TOLERANCIA_INSPECAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA INSPEÇÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_PRESSAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO PRESSÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_SULCO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO SULCO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    --ATUALIZA DADOS DA PARAMETRIZAÇÃO.
    UPDATE PNEU_RESTRICAO_UNIDADE
    SET TOLERANCIA_CALIBRAGEM    = F_IF(F_TOLERANCIA_CALIBRAGEM IS NULL, TOLERANCIA_CALIBRAGEM_ATUAL,
                                        F_TOLERANCIA_CALIBRAGEM),
        SULCO_MINIMO_RECAPAGEM   = F_IF(F_SULCO_MINIMO_RECAPAGEM IS NULL, SULCO_MINIMO_RECAPAGEM_ATUAL,
                                        F_SULCO_MINIMO_RECAPAGEM),
        SULCO_MINIMO_DESCARTE    = F_IF(F_SULCO_MINIMO_DESCARTE IS NULL, SULCO_MINIMO_DESCARTE_ATUAL,
                                        F_SULCO_MINIMO_DESCARTE),
        TOLERANCIA_INSPECAO      = F_IF(F_TOLERANCIA_INSPECAO IS NULL, TOLERANCIA_INSPECAO_ATUAL,
                                        F_TOLERANCIA_INSPECAO),
        PERIODO_AFERICAO_PRESSAO = F_IF(F_PERIODO_AFERICAO_PRESSAO IS NULL, PERIODO_AFERICAO_PRESSAO_ATUAL,
                                        F_PERIODO_AFERICAO_PRESSAO),
        PERIODO_AFERICAO_SULCO   = F_IF(F_PERIODO_AFERICAO_SULCO IS NULL, PERIODO_AFERICAO_SULCO_ATUAL,
                                        F_PERIODO_AFERICAO_SULCO)
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --MENSAGEM DE SUCESSO.
    SELECT 'DADOS ATUALIZADOS COM SUCESSO!!'
               || ' EMPRESA: '
               || COD_EMPRESA
               || ', UNIDADE: '
               || COD_UNIDADE
               || ', TOLERANCIA CALIBRAGEM: '
               || TOLERANCIA_CALIBRAGEM
               || ', SULCO MÍNIMO RECAPAGEM: '
               || SULCO_MINIMO_RECAPAGEM
               || ', SULCO MÍNIMO DESCARTE: '
               || SULCO_MINIMO_DESCARTE
               || ' TOLERANCIA INSPEÇÃO: '
               || TOLERANCIA_INSPECAO
               || ',PERÍODO AFERIÇÃO PRESSÃO: '
               || PERIODO_AFERICAO_PRESSAO
               || ', PERÍODO AFERIÇÃO SULCO: '
               || PERIODO_AFERICAO_SULCO
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE
    INTO PARAMETRIZACAO_ATUALIZADA;
END
$$;