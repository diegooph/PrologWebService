CREATE OR REPLACE FUNCTION FUNC_AFERICAO_UPSERT_CONFIGURACAO_CRONOGRAMA_SERVICO(F_CODIGO_EMPRESA BIGINT,
                                                                                F_CODIGO_UNIDADE BIGINT,
                                                                                F_TOLERANCIA_CALIBRAGEM NUMERIC,
                                                                                F_TOLERANCIA_INSPECAO NUMERIC,
                                                                                F_SULCO_MINIMO_RECAPAGEM NUMERIC,
                                                                                F_SULCO_MINIMO_DESCARTE NUMERIC,
                                                                                F_PERIODO_AFERICAO_PRESSAO INTEGER,
                                                                                F_PERIODO_AFERICAO_SULCO INTEGER,
                                                                                F_COD_COLABORADOR BIGINT,
                                                                                F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    CODIGO_CONFIG BIGINT;
    OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    OLD_DATA_HORA_ULTIMA_ATUALIZACAO TIMESTAMP WITH TIME ZONE;
    OLD_TOLERANCIA_CALIBRAGEM NUMERIC;
    OLD_TOLERANCIA_INSPECAO NUMERIC;
    OLD_SULCO_MINIMO_RECAPAGEM NUMERIC;
    OLD_SULCO_MINIMO_DESCARTE NUMERIC;
    OLD_PERIODO_AFERICAO_PRESSAO INTEGER;
    OLD_PERIODO_AFERICAO_SULCO INTEGER;
BEGIN
    -- BUSCA E ARMAZENA OS DADOS ANTIGOS
    SELECT CODIGO,
          COD_COLABORADOR_ULTIMA_ATUALIZACAO,
          DATA_HORA_ULTIMA_ATUALIZACAO,
          TOLERANCIA_CALIBRAGEM,
          TOLERANCIA_INSPECAO,
          SULCO_MINIMO_RECAPAGEM,
          SULCO_MINIMO_DESCARTE,
          PERIODO_AFERICAO_PRESSAO,
          PERIODO_AFERICAO_SULCO
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_CODIGO_EMPRESA
     AND COD_UNIDADE = F_CODIGO_UNIDADE
    INTO CODIGO_CONFIG, OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO, OLD_DATA_HORA_ULTIMA_ATUALIZACAO,
        OLD_TOLERANCIA_CALIBRAGEM,OLD_TOLERANCIA_INSPECAO, OLD_SULCO_MINIMO_RECAPAGEM, OLD_SULCO_MINIMO_DESCARTE,
        OLD_PERIODO_AFERICAO_PRESSAO, OLD_PERIODO_AFERICAO_SULCO;

    -- CASO A CONFIG EXISTA, VERIFICA SE HOUVE MUDANÇAS, ATUALIZA E SALVA O HISTÓRICO.
    IF CODIGO_CONFIG > 0 THEN
        -- VERIFICA SE HOUVE MUDANÇAS
        IF  OLD_TOLERANCIA_CALIBRAGEM != F_TOLERANCIA_CALIBRAGEM OR
            OLD_TOLERANCIA_INSPECAO != F_TOLERANCIA_INSPECAO OR
            OLD_SULCO_MINIMO_RECAPAGEM != F_SULCO_MINIMO_RECAPAGEM OR
            OLD_SULCO_MINIMO_DESCARTE != F_SULCO_MINIMO_DESCARTE OR
            OLD_PERIODO_AFERICAO_PRESSAO != F_PERIODO_AFERICAO_PRESSAO OR
            OLD_PERIODO_AFERICAO_SULCO != F_PERIODO_AFERICAO_SULCO THEN
            -- ATUALIZA.
            UPDATE PNEU_RESTRICAO_UNIDADE
            SET TOLERANCIA_CALIBRAGEM              = F_TOLERANCIA_CALIBRAGEM,
                COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
                DATA_HORA_ULTIMA_ATUALIZACAO       = F_DATA_HORA_ATUAL_UTC,
                TOLERANCIA_INSPECAO                = F_TOLERANCIA_INSPECAO,
                SULCO_MINIMO_RECAPAGEM             = F_SULCO_MINIMO_RECAPAGEM,
                SULCO_MINIMO_DESCARTE              = F_SULCO_MINIMO_DESCARTE,
                PERIODO_AFERICAO_PRESSAO           = F_PERIODO_AFERICAO_PRESSAO,
                PERIODO_AFERICAO_SULCO             = F_PERIODO_AFERICAO_SULCO
            WHERE COD_EMPRESA = F_CODIGO_EMPRESA
              AND COD_UNIDADE = F_CODIGO_UNIDADE;

            -- SALVA O HISTÓRICO.
            INSERT INTO PNEU_RESTRICAO_UNIDADE_HISTORICO (
                                        COD_RESTRICAO_UNIDADE_PNEU,
                                        COD_EMPRESA,
                                        COD_UNIDADE,
                                        COD_COLABORADOR,
                                        DATA_HORA_ALTERACAO,
                                        TOLERANCIA_CALIBRAGEM,
                                        TOLERANCIA_INSPECAO,
                                        SULCO_MINIMO_RECAPAGEM,
                                        SULCO_MINIMO_DESCARTE,
                                        PERIODO_AFERICAO_PRESSAO,
                                        PERIODO_AFERICAO_SULCO)
            VALUES (CODIGO_CONFIG,
                    F_CODIGO_EMPRESA,
                    F_CODIGO_UNIDADE,
                    OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
                    OLD_DATA_HORA_ULTIMA_ATUALIZACAO,
                    OLD_TOLERANCIA_CALIBRAGEM,
                    OLD_TOLERANCIA_INSPECAO,
                    OLD_SULCO_MINIMO_RECAPAGEM,
                    OLD_SULCO_MINIMO_DESCARTE,
                    OLD_PERIODO_AFERICAO_PRESSAO,
                    OLD_PERIODO_AFERICAO_SULCO);
        END IF;
    -- SE NÃO EXISTIR.
    ELSE
        -- INSERE A CONFIG.
        INSERT INTO PNEU_RESTRICAO_UNIDADE (COD_EMPRESA,
                                            COD_UNIDADE,
                                            COD_COLABORADOR_ULTIMA_ATUALIZACAO,
                                            DATA_HORA_ULTIMA_ATUALIZACAO,
                                            TOLERANCIA_CALIBRAGEM,
                                            TOLERANCIA_INSPECAO,
                                            SULCO_MINIMO_RECAPAGEM,
                                            SULCO_MINIMO_DESCARTE,
                                            PERIODO_AFERICAO_PRESSAO,
                                            PERIODO_AFERICAO_SULCO)
        VALUES (F_CODIGO_EMPRESA,
                F_CODIGO_UNIDADE,
                F_COD_COLABORADOR,
                F_DATA_HORA_ATUAL_UTC,
                F_TOLERANCIA_CALIBRAGEM,
                F_TOLERANCIA_INSPECAO,
                F_SULCO_MINIMO_RECAPAGEM,
                F_SULCO_MINIMO_DESCARTE,
                F_PERIODO_AFERICAO_PRESSAO,
                F_PERIODO_AFERICAO_SULCO);
    END IF;
END;
$$;