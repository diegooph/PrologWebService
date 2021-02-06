-- Sobre:
-- Esta função lista todas as configurações de aferíção por tipo de veículo de uma unidade,
-- mesmo que essa configuração ainda não tenha sido criada.
--
-- Atenção: No caso de a configuração ainda não ter sido criada, a function retornada como padrão
-- true para os booleans e 'EQUIPAMENTO' para as formas de coleta dos dados, fazendo com que o default
-- seja poder realizar todas as aferições apenas com equipamento.
--
-- Histórico:
-- 2020-04-28 -> Function alterada para inserir no retorno novos campo de tipo de aferição (gustavocnp95 - PL-2689).
-- 2020-05-07 -> Function alterada para retirar campos nao mais existentes (gustavocnp95 - PL-2689)
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(
    F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_CONFIGURACAO                              BIGINT,
                COD_UNIDADE_CONFIGURACAO                      BIGINT,
                COD_TIPO_VEICULO                              BIGINT,
                NOME_TIPO_VEICULO                             TEXT,
                COD_EMPRESA_TIPO_VEICULO                      BIGINT,
                STATUS_ATIVO_TIPO_VEICULO                     BOOLEAN,
                PODE_AFERIR_ESTEPE                            BOOLEAN,
                FORMA_COLETA_DADOS_PRESSAO                    TEXT,
                FORMA_COLETA_DADOS_PRESSAO_LEGIVEL            TEXT,
                FORMA_COLETA_DADOS_SULCO                      TEXT,
                FORMA_COLETA_DADOS_SULCO_LEGIVEL              TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO              TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO_LEGIVEL      TEXT,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO         TEXT,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO_LEGIVEL TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                CONSTANT BIGINT := (SELECT U.COD_EMPRESA
                                                     FROM UNIDADE U
                                                     WHERE U.CODIGO = F_COD_UNIDADE);
    V_STATUS_LEGIVEL_EQUIPAMENTO CONSTANT TEXT   = (SELECT STATUS_LEGIVEL
                                                    FROM TYPES.AFERICAO_FORMA_COLETA_DADOS
                                                    WHERE FORMA_COLETA_DADOS = 'EQUIPAMENTO');
BEGIN
    RETURN QUERY
        SELECT CONFIG.CODIGO                                                AS COD_CONFIGURACAO,
               -- Usamos o código da unidade recebido por parâmetro pois se um tipo não tiver configurado para a unidade buscada
               -- o código da tabela será null.
               F_COD_UNIDADE                                                AS COD_UNIDADE_CONFIGURACAO,
               VT.CODIGO                                                    AS COD_TIPO_VEICULO,
               VT.NOME :: TEXT                                              AS NOME_TIPO_VEICULO,
               VT.COD_EMPRESA                                               AS COD_EMPRESA_TIPO_VEICULO,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               -- Essas verificações servem para o caso do tipo de veículo não ter configuracão criada,
               -- assim retornamos um default que libera tudo.
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE,
               -- Aqui é tratada tanto a primary key quando a forma legivel (br ou es).
               F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTPRESSAO.STATUS_LEGIVEL)                             AS FORMA_COLETA_DADOS_PRESSAO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTSULCO.STATUS_LEGIVEL)                               AS FORMA_COLETA_DADOS_SULCO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTSULCOPRESSAO.STATUS_LEGIVEL)                        AS FORMA_COLETA_DADOS_SULCO_PRESSAO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)           AS FORMA_COLETA_DADOS_FECHAMENTO_SERVICO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTFECHAMENTOSERVICO.STATUS_LEGIVEL)                   AS FORMA_COLETA_DADOS_FECHGAMENTO_SERVICO_LEGIVEL
        FROM VEICULO_TIPO VT
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = F_COD_UNIDADE
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTSULCO
                           ON FCDTSULCO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_SULCO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTPRESSAO
                           ON FCDTPRESSAO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_PRESSAO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTSULCOPRESSAO
                           ON FCDTSULCOPRESSAO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTFECHAMENTOSERVICO
                           ON FCDTFECHAMENTOSERVICO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        WHERE VT.COD_EMPRESA = V_COD_EMPRESA
          AND (CONFIG.COD_UNIDADE = F_COD_UNIDADE OR CONFIG.COD_UNIDADE IS NULL)
        ORDER BY VT.NOME ASC;
END ;
$$;