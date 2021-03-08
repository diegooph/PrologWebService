CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_VISUALIZACAO(F_COD_MOTIVO BIGINT,
                                                              F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO                     BIGINT,
                DESCRICAO_MOTIVO                  TEXT,
                ATIVO_MOTIVO                      BOOLEAN,
                CODIGO_AUXILIAR                   TEXT,
                DATA_HORA_ULTIMA_ALTERACAO_MOTIVO TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ULTIMA_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
       MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
       MMM.ATIVO                                               AS ATIVO_MOTIVO,
       MMM.COD_AUXILIAR                                        AS CODIGO_AUXILIAR,
       MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ULTIMA_ALTERACAO_MOTIVO,
       CUA.NOME                                                AS NOME_COLABORADOR_ULTIMA_ALTERACAO
FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
         INNER JOIN EMPRESA E ON E.CODIGO = MMM.COD_EMPRESA
         INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
WHERE MMM.CODIGO = F_COD_MOTIVO;
$$;