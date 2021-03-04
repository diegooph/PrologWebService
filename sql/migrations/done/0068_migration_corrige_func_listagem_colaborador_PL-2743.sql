-- Corrige Function
-- PL-2743
DROP FUNCTION func_colaborador_relatorio_listagem_colaboradores_by_unidade(bigint[]);
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_RELATORIO_LISTAGEM_COLABORADORES_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                UNIDADE                     TEXT,
                CPF                         TEXT,
                COLABORADOR                 TEXT,
                "DATA NASCIMENTO"           TEXT,
                PIS                         TEXT,
                CARGO                       TEXT,
                SETOR                       TEXT,
                EQUIPE                      TEXT,
                "STATUS"                    TEXT,
                "DATA ADMISSÃO"             TEXT,
                "DATA DEMISSÃO"             TEXT,
                "QTD PERMISSÕES ASSOCIADAS" BIGINT,
                "MATRÍCULA AMBEV"           TEXT,
                "MATRÍCULA TRANSPORTADORA"  TEXT,
                "NÍVEL ACESSO INFORMAÇÃO"   TEXT,
                "DATA/HORA CADASTRO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$

BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                                                            AS NOME_UNIDADE,
               LPAD(CO.CPF :: TEXT, 11, '0')                                             AS CPF_COLABORADOR,
               CO.NOME :: TEXT                                                           AS NOME_COLABORADOR,
               COALESCE(TO_CHAR(CO.DATA_NASCIMENTO, 'DD/MM/YYYY'), '-')                  AS DATA_NASCIMENTO_COLABORADOR,
               COALESCE(LPAD(CO.PIS :: TEXT, 12, '0'), '-')                              AS PIS_COLABORADOR,
               F.NOME :: TEXT                                                            AS NOME_CARGO,
               SE.NOME :: TEXT                                                           AS NOME_SETOR,
               E.NOME :: TEXT                                                            AS NOME_EQUIPE,
               F_IF(CO.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)                 AS STATUS_COLABORADOR,
               COALESCE(TO_CHAR(CO.DATA_ADMISSAO, 'DD/MM/YYYY'), '-')                    AS DATA_ADMISSAO_COLABORADOR,
               COALESCE(TO_CHAR(CO.DATA_DEMISSAO, 'DD/MM/YYYY'), '-')                    AS DATA_DEMISSAO_COLABORADOR,
               COUNT(*)
               FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                   -- CONSIDERAMOS APENAS AS PERMISSÕES DE PILARES LIBERADOS PARA A UNIDADE DO COLABORADOR.
                   AND CFP.COD_PILAR_PROLOG IN (SELECT UPP.COD_PILAR
                                                FROM UNIDADE_PILAR_PROLOG UPP
                                                WHERE UPP.COD_UNIDADE = CO.COD_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS,
               COALESCE(CO.MATRICULA_AMBEV :: TEXT,
                        '-')                                                             AS MATRICULA_AMBEV_COLABORADOR,
               COALESCE(CO.MATRICULA_TRANS :: TEXT,
                        '-')                                                             AS MATRICULA_TRANSPORTADORA_COLABORADOR,
               PE.DESCRICAO :: TEXT                                                      AS DESCRICAO_PERMISSAO,
               COALESCE(TO_CHAR(CO.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(CO.COD_UNIDADE),
                                'DD/MM/YYYY HH24:MI'),
                        '-')                                                             AS DATA_HORA_CADASTRO_COLABORADOR
        FROM COLABORADOR CO
                 JOIN UNIDADE U
                      ON CO.COD_UNIDADE = U.CODIGO
                 JOIN FUNCAO F
                      ON CO.COD_FUNCAO = F.CODIGO
                 JOIN SETOR SE
                      ON CO.COD_UNIDADE = SE.COD_UNIDADE AND CO.COD_SETOR = SE.CODIGO
                 JOIN EQUIPE E
                      ON CO.COD_EQUIPE = E.CODIGO
                 JOIN PERMISSAO PE
                      ON CO.COD_PERMISSAO = PE.CODIGO
                 LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
                           ON CO.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR
                               AND CO.COD_UNIDADE = CFP.COD_UNIDADE
        WHERE CO.COD_UNIDADE = ANY (F_COD_UNIDADES)
        GROUP BY U.NOME,
                 CO.CPF,
                 CO.NOME,
                 F.NOME,
                 SE.NOME,
                 E.NOME,
                 CO.STATUS_ATIVO,
                 CO.COD_UNIDADE,
                 CO.DATA_NASCIMENTO,
                 CO.DATA_ADMISSAO,
                 CO.DATA_DEMISSAO,
                 CO.MATRICULA_AMBEV,
                 CO.MATRICULA_TRANS,
                 CO.PIS,
                 PE.DESCRICAO,
                 CO.DATA_HORA_CADASTRO
        ORDER BY U.NOME,
                 CO.NOME,
                 F.NOME,
                 CO.STATUS_ATIVO;
END;
$$;