CREATE OR REPLACE FUNCTION FUNC_MOTIVO_MOVIMENTO_HISTORICO_LISTAGEM(F_COD_MOTIVO BIGINT,
                                                                    F_TIME_ZONE TEXT)
    RETURNS TABLE
            (
                CODIGO_MOTIVO              BIGINT,
                DESCRICAO_MOTIVO           CITEXT,
                ATIVO_MOTIVO               BOOLEAN,
                CODIGO_AUXILIAR            TEXT,
                DATA_HORA_ALTERACAO        TIMESTAMP WITHOUT TIME ZONE,
                NOME_COLABORADOR_ALTERACAO TEXT
            )
    LANGUAGE SQL
AS
$$
(SELECT MMMH.COD_MOTIVO                                            AS CODIGO_MOTIVO,
        MMMH.DESCRICAO_MOTIVO                                      AS DESCRICAO_MOTIVO,
        MMMH.ATIVO_MOTIVO                                          AS ATIVO_MOTIVO,
        MMMH.COD_AUXILIAR                                          AS CODIGO_AUXILIAR,
        MMMH.DATA_HORA_ALTERACAO_ANTERIOR AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ALTERACAO,
        CUA.NOME                                                   AS NOME_COLABORADOR_ALTERACAO
 FROM MOVIMENTACAO_MOTIVO_MOVIMENTO_HISTORICO MMMH
          INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMMH.COD_COLABORADOR_ALTERACAO_ANTERIOR
 WHERE MMMH.COD_MOTIVO = F_COD_MOTIVO
 UNION
 SELECT MMM.CODIGO                                              AS CODIGO_MOTIVO,
        MMM.MOTIVO                                              AS DESCRICAO_MOTIVO,
        MMM.ATIVO                                               AS ATIVO_MOTIVO,
        MMM.COD_AUXILIAR                                        AS CODIGO_AUXILIAR,
        MMM.DATA_HORA_ULTIMA_ALTERACAO AT TIME ZONE F_TIME_ZONE AS DATA_HORA_ALTERACAO,
        CUA.NOME                                                AS NOME_COLABORADOR_ALTERACAO
 FROM MOVIMENTACAO_MOTIVO_MOVIMENTO MMM
          INNER JOIN COLABORADOR CUA ON CUA.CODIGO = MMM.COD_COLABORADOR_ULTIMA_ALTERACAO
 WHERE MMM.CODIGO = F_COD_MOTIVO)
    ORDER BY DATA_HORA_ALTERACAO DESC;
$$;