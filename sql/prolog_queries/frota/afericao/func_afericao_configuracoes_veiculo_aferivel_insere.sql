-- Sobre:
-- Esta função cria uma nova configuração para um tipo de veículo aferível.
--
-- Atenção: a function é um upsert, ou seja, ela verifica se a configuração existe. Se sim, apenas atualiza. Se não,
-- cria uma nova linha na tabela.
--
-- Histórico:
-- 2020-04-28 -> Function criada (gustavocnp95 - PL-2689).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_CONFIGURACOES_VEICULO_AFERIVEL_INSERE(F_COD_CONFIGURACAO BIGINT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_COD_TIPO_VEICULO BIGINT,
                                                                               F_PODE_AFERIR_SULCO BOOLEAN,
                                                                               F_PODE_AFERIR_PRESSAO BOOLEAN,
                                                                               F_PODE_AFERIR_SULCO_PRESSAO BOOLEAN,
                                                                               F_PODE_AFERIR_ESTEPE BOOLEAN,
                                                                               F_FORMA_COLETA_DADOS_PRESSAO AFERICAO_FORMA_COLETA_DADOS_TYPE,
                                                                               F_FORMA_COLETA_DADOS_SULCO AFERICAO_FORMA_COLETA_DADOS_TYPE,
                                                                               F_FORMA_COLETA_DADOS_SULCO_PRESSAO AFERICAO_FORMA_COLETA_DADOS_TYPE,
                                                                               F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO AFERICAO_FORMA_COLETA_DADOS_TYPE)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CONFIGURACAO                      BIGINT;
    V_PODE_AFERIR_SULCO                     BOOLEAN;
    V_PODE_AFERIR_PRESSAO                   BOOLEAN;
    V_PODE_AFERIR_SULCO_PRESSAO             BOOLEAN;
    V_PODE_AFERIR_ESTEPE                    BOOLEAN;
    V_FORMA_COLETA_DADOS_PRESSAO            AFERICAO_FORMA_COLETA_DADOS_TYPE;
    V_FORMA_COLETA_DADOS_SULCO              AFERICAO_FORMA_COLETA_DADOS_TYPE;
    V_FORMA_COLETA_DADOS_SULCO_PRESSAO      AFERICAO_FORMA_COLETA_DADOS_TYPE;
    V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO AFERICAO_FORMA_COLETA_DADOS_TYPE;
BEGIN

    IF F_COD_CONFIGURACAO IS NULL THEN
        INSERT INTO AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO (COD_UNIDADE,
                                                                 COD_TIPO_VEICULO,
                                                                 PODE_AFERIR_SULCO,
                                                                 PODE_AFERIR_PRESSAO,
                                                                 PODE_AFERIR_SULCO_PRESSAO,
                                                                 PODE_AFERIR_ESTEPE,
                                                                 FORMA_COLETA_DADOS_PRESSAO,
                                                                 FORMA_COLETA_DADOS_SULCO,
                                                                 FORMA_COLETA_DADOS_SULCO_PRESSAO,
                                                                 FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)
        VALUES (F_COD_UNIDADE,
                F_COD_TIPO_VEICULO,
                F_PODE_AFERIR_SULCO,
                F_PODE_AFERIR_PRESSAO,
                F_PODE_AFERIR_SULCO_PRESSAO,
                F_PODE_AFERIR_ESTEPE,
                F_FORMA_COLETA_DADOS_PRESSAO,
                F_FORMA_COLETA_DADOS_SULCO,
                F_FORMA_COLETA_DADOS_SULCO_PRESSAO,
                F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)
        RETURNING CODIGO INTO V_COD_CONFIGURACAO;
    ELSE
        SELECT PODE_AFERIR_SULCO,
               PODE_AFERIR_PRESSAO,
               PODE_AFERIR_SULCO_PRESSAO,
               PODE_AFERIR_ESTEPE,
               FORMA_COLETA_DADOS_PRESSAO,
               FORMA_COLETA_DADOS_SULCO,
               FORMA_COLETA_DADOS_SULCO_PRESSAO,
               FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        INTO V_PODE_AFERIR_SULCO,
            V_PODE_AFERIR_PRESSAO,
            V_PODE_AFERIR_SULCO_PRESSAO,
            V_PODE_AFERIR_ESTEPE,
            V_FORMA_COLETA_DADOS_PRESSAO,
            V_FORMA_COLETA_DADOS_SULCO,
            V_FORMA_COLETA_DADOS_SULCO_PRESSAO,
            V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
        WHERE CODIGO = F_COD_CONFIGURACAO;

        IF V_PODE_AFERIR_SULCO != F_PODE_AFERIR_SULCO
            OR V_PODE_AFERIR_PRESSAO != F_PODE_AFERIR_PRESSAO
            OR V_PODE_AFERIR_SULCO_PRESSAO != F_PODE_AFERIR_SULCO_PRESSAO
            OR V_PODE_AFERIR_ESTEPE != F_PODE_AFERIR_ESTEPE
            OR V_FORMA_COLETA_DADOS_PRESSAO != F_FORMA_COLETA_DADOS_PRESSAO
            OR V_FORMA_COLETA_DADOS_SULCO != F_FORMA_COLETA_DADOS_SULCO
            OR V_FORMA_COLETA_DADOS_SULCO_PRESSAO != F_FORMA_COLETA_DADOS_SULCO_PRESSAO
            OR V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO != F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO THEN

            UPDATE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
            SET PODE_AFERIR_SULCO                     = F_PODE_AFERIR_SULCO,
                PODE_AFERIR_PRESSAO                   = F_PODE_AFERIR_PRESSAO,
                PODE_AFERIR_SULCO_PRESSAO             = F_PODE_AFERIR_SULCO_PRESSAO,
                PODE_AFERIR_ESTEPE                    = F_PODE_AFERIR_ESTEPE,
                FORMA_COLETA_DADOS_PRESSAO            = F_FORMA_COLETA_DADOS_PRESSAO,
                FORMA_COLETA_DADOS_SULCO              = F_FORMA_COLETA_DADOS_SULCO,
                FORMA_COLETA_DADOS_SULCO_PRESSAO      = F_FORMA_COLETA_DADOS_SULCO_PRESSAO,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO = F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
            WHERE CODIGO = F_COD_CONFIGURACAO
            RETURNING CODIGO INTO V_COD_CONFIGURACAO;
        END IF;
    END IF;

    RETURN V_COD_CONFIGURACAO;
END
$$;