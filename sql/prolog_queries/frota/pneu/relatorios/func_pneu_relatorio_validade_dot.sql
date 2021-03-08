CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_VALIDADE_DOT(F_COD_UNIDADES BIGINT[],
                                                            F_DATA_ATUAL TIMESTAMP WITHOUT TIME ZONE)
    RETURNS TABLE
            (
                "UNIDADE"         TEXT,
                "COD PNEU"        TEXT,
                "PLACA"           TEXT,
                "POSIÇÃO"         TEXT,
                "DOT CADASTRADO"  TEXT,
                "DOT VÁLIDO"      TEXT,
                "TEMPO DE USO"    TEXT,
                "TEMPO RESTANTE"  TEXT,
                "DATA VENCIMENTO" TEXT,
                "VENCIDO"         TEXT,
                "DATA GERAÇÃO"    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATE_FORMAT        TEXT := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
    DIA_MES_ANO_FORMAT TEXT := 'DD/MM/YYYY';
    DATA_HORA_FORMAT   TEXT := 'DD/MM/YYYY HH24:MI';
    DATE_CONVERTER     TEXT := 'YYYYWW';
    PREFIXO_ANO        TEXT := SUBSTRING(F_DATA_ATUAL::TEXT, 1, 2);
BEGIN
    RETURN QUERY
        WITH INFORMACOES_PNEU AS (
            SELECT P.CODIGO_CLIENTE                               AS COD_PNEU,
                   P.DOT                                          AS DOT_CADASTRADO,
                   -- Remove letras, characteres especiais e espaços do dot.
                   -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
                   TRIM(REGEXP_REPLACE(P.DOT, '[^0-9]', '', 'g')) AS DOT_LIMPO,
                   P.COD_UNIDADE                                  AS COD_UNIDADE,
                   U.NOME                                         AS UNIDADE,
                   VP.PLACA                                       AS PLACA_APLICADO,
                   PPNE.NOMENCLATURA                              AS POSICAO_PNEU
            FROM PNEU P
                     JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
                     JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA
                     LEFT JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO
                     LEFT JOIN VEICULO V ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
                     LEFT JOIN VEICULO_TIPO VT
                               ON V.COD_TIPO = VT.CODIGO
                     LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                     LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
                AND PPNE.COD_DIAGRAMA = VD.CODIGO
                AND PPNE.POSICAO_PROLOG = VP.POSICAO
            WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ),

             DATA_DOT AS (
                 SELECT IP.COD_PNEU,
                        -- Transforma o DOT_FORMATADO em data
                        CASE
                            WHEN (CHAR_LENGTH(IP.DOT_LIMPO) = 4)
                                THEN
                                TO_DATE(CONCAT(PREFIXO_ANO, (SUBSTRING(IP.DOT_LIMPO, 3, 4)),
                                               (SUBSTRING(IP.DOT_LIMPO, 1, 2))),
                                        DATE_CONVERTER)
                            ELSE NULL END AS DOT_EM_DATA
                 FROM INFORMACOES_PNEU IP
             ),

             VENCIMENTO_DOT AS (
                 SELECT DD.COD_PNEU,
                        -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
                        -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
                        -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
                        -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
                        CASE
                            WHEN DD.DOT_EM_DATA <= (F_DATA_ATUAL::DATE)
                                THEN DD.DOT_EM_DATA + INTERVAL '5 DAYS 5 YEARS'
                            ELSE NULL END AS DATA_VENCIMENTO
                 FROM DATA_DOT DD
             ),

             CALCULOS AS (
                 SELECT DD.COD_PNEU,
                        -- Verifica se o dot é válido
                        -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
                        -- podemos considerar inválidos os que possuem vencimento = null.
                        CASE WHEN VD.DATA_VENCIMENTO IS NULL THEN 'INVÁLIDO' ELSE 'VÁLIDO' END        AS DOT_VALIDO,
                        -- Cálculo tempo de uso
                        CASE
                            WHEN VD.DATA_VENCIMENTO IS NULL
                                THEN NULL
                            ELSE
                                TO_CHAR(AGE((F_DATA_ATUAL :: DATE), DD.DOT_EM_DATA), DATE_FORMAT) END AS TEMPO_DE_USO,
                        -- Cálculo dias restantes
                        TO_CHAR(AGE(VD.DATA_VENCIMENTO, F_DATA_ATUAL), DATE_FORMAT)                   AS TEMPO_RESTANTE,
                        -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
                        F_IF(((VD.DATA_VENCIMENTO::DATE) - (F_DATA_ATUAL::DATE)) < 0, TRUE, FALSE)    AS VENCIDO
                 FROM DATA_DOT DD
                          JOIN VENCIMENTO_DOT VD ON DD.COD_PNEU = VD.COD_PNEU
             )
        SELECT IP.UNIDADE::TEXT,
               IP.COD_PNEU::TEXT,
               COALESCE(IP.PLACA_APLICADO::TEXT, '-'),
               COALESCE(IP.POSICAO_PNEU::TEXT, '-'),
               COALESCE(IP.DOT_CADASTRADO::TEXT, '-'),
               CA.DOT_VALIDO,
               COALESCE(CA.TEMPO_DE_USO, '-'),
               COALESCE(CA.TEMPO_RESTANTE, '-'),
               COALESCE(TO_CHAR(VD.DATA_VENCIMENTO, DIA_MES_ANO_FORMAT)::TEXT, '-'),
               F_IF(CA.VENCIDO, 'SIM' :: TEXT, 'NÃO' :: TEXT),
               TO_CHAR(F_DATA_ATUAL, DATA_HORA_FORMAT)::TEXT
        FROM INFORMACOES_PNEU IP
                 JOIN VENCIMENTO_DOT VD ON IP.COD_PNEU = VD.COD_PNEU
                 JOIN CALCULOS CA ON CA.COD_PNEU = VD.COD_PNEU AND CA.COD_PNEU = IP.COD_PNEU
        ORDER BY VD.DATA_VENCIMENTO ASC, IP.PLACA_APLICADO;
END;
$$;