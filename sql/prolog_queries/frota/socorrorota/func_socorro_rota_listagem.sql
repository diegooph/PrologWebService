-- Sobre:
-- Esta function retorna uma lista dos socorros em rota com base nos filtros de múltiplas unidades, data inicial e final
--
-- Observação:
-- O filtro de data se baseia apenas na data de abertura.
--
-- Histórico:
-- 2019-12-20 -> Function criada (wvinim - PL-2424).
-- 2020-01-13 -> Adição de colunas para indicar se veículo ou colaborador foram deletados (wvinim - PL-2424).
-- 2020-01-14 -> Aplica o filtro por colaborador apenas se não tiver permissão para ver todos (wvinim - PL-2424).
-- 2020-01-30 -> Adiciona ordenamento pela data de abertura decrescente (wvinim - PL-2424).
-- 2020-02-11 -> Aplica a verificação que restringe a utilização apenas para empresas liberadas.
-- 2020-02-14 -> Adiciona as fotos de abertura (wvinim - PL-2517).
-- 2020-03-19 -> Adiciona as colunas refentes às datas de deslocamento (wvinim - PL-2631).
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_LISTAGEM(F_COD_UNIDADES BIGINT[],
                                                             F_DATA_INICIAL DATE,
                                                             F_DATA_FINAL DATE,
                                                             F_TOKEN TEXT)
    RETURNS TABLE
            (
                COD_SOCORRO_ROTA                          BIGINT,
                UNIDADE                                   TEXT,
                PLACA_VEICULO                             TEXT,
                VEICULO_DELETADO                          BOOLEAN,
                NOME_RESPONSAVEL_ABERTURA_SOCORRO         TEXT,
                COLABORADOR_DELETADO                      BOOLEAN,
                DESCRICAO_FORNECIDA_ABERTURA_SOCORRO      TEXT,
                DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO TEXT,
                DATA_HORA_ABERTURA_SOCORRO                TIMESTAMP WITHOUT TIME ZONE,
                ENDERECO_AUTOMATICO_ABERTURA_SOCORRO      TEXT,
                URL_FOTO_1_ABERTURA                       TEXT,
                URL_FOTO_2_ABERTURA                       TEXT,
                URL_FOTO_3_ABERTURA                       TEXT,
                STATUS_ATUAL_SOCORRO_ROTA                 SOCORRO_ROTA_STATUS_TYPE,
                DATA_HORA_DESLOCAMENTO_INICIO             TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_DESLOCAMENTO_FIM                TIMESTAMP WITHOUT TIME ZONE
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Permissões para ver todos os socorros em rota
    -- 146 - TRATAR_SOCORRO
    -- 147 - VISUALIZAR_SOCORROS_E_RELATORIOS
    F_PERMISSOES_VISUALIZAR_TODOS INTEGER[] := ARRAY [146,147];
    F_VER_TODOS                   BOOLEAN   := (SELECT POSSUI_PERMISSSAO
                                                FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN,
                                                                                                F_PERMISSOES_VISUALIZAR_TODOS,
                                                                                                FALSE,
                                                                                                TRUE));
    F_COD_COLABORADOR             BIGINT    := (SELECT COD_COLABORADOR
                                                FROM TOKEN_AUTENTICACAO
                                                WHERE TOKEN = F_TOKEN);
    -- Busca o código de empresa com base na primeira unidade do array recebido
    F_COD_EMPRESA                 BIGINT    := (SELECT COD_EMPRESA
                                                FROM UNIDADE
                                                WHERE CODIGO = (SELECT (F_COD_UNIDADES)[1]));
BEGIN
    -- Verifica se a funcionalidade está liberada para a empresa
    PERFORM FUNC_SOCORRO_ROTA_EMPRESA_LIBERADA(F_COD_EMPRESA);

    RETURN QUERY
        SELECT SR.CODIGO                                                               AS COD_SOCORRO_ROTA,
               U.NOME :: TEXT                                                          AS UNIDADE,
               VD.PLACA :: TEXT                                                        AS PLACA_VEICULO,
               VD.DELETADO                                                             AS VEICULO_DELETADO,
               CD.NOME :: TEXT                                                         AS NOME_RESPONSAVEL,
               CD.DELETADO                                                             AS COLABORADOR_DELETADO,
               SRA.DESCRICAO_PROBLEMA                                                  AS DESCRICAO_FORNECIDA,
               SROP.DESCRICAO :: TEXT                                                  AS DESCRICAO_OPCAO_PROBLEMA,
               SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)          AS DATA_HORA_ABERTURA,
               SRA.ENDERECO_AUTOMATICO                                                 AS ENDERECO_AUTOMATICO_ABERTURA,
               SRA.URL_FOTO_1_ABERTURA :: TEXT                                         AS URL_FOTO_1_ABERTURA,
               SRA.URL_FOTO_2_ABERTURA :: TEXT                                         AS URL_FOTO_2_ABERTURA,
               SRA.URL_FOTO_3_ABERTURA :: TEXT                                         AS URL_FOTO_3_ABERTURA,
               SR.STATUS_ATUAL :: SOCORRO_ROTA_STATUS_TYPE                             AS STATUS_ATUAL_SOCORRO,
               SRAD.DATA_HORA_DESLOCAMENTO_INICIO AT TIME ZONE
               TZ_UNIDADE(SR.COD_UNIDADE)                                              AS DATA_HORA_DESLOCAMENTO_INICIO,
               SRAD.DATA_HORA_DESLOCAMENTO_FIM AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE) AS DATA_HORA_DESLOCAMENTO_FIM
        FROM SOCORRO_ROTA SR
                 JOIN UNIDADE U ON U.CODIGO = SR.COD_UNIDADE
                 JOIN SOCORRO_ROTA_ABERTURA SRA ON SRA.COD_SOCORRO_ROTA = SR.CODIGO
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO SRAT
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAT.COD_SOCORRO_ROTA = SR.CODIGO
                 LEFT JOIN SOCORRO_ROTA_ATENDIMENTO_DESLOCAMENTO SRAD
                           ON SR.STATUS_ATUAL::TEXT = ANY (ARRAY ['EM_ATENDIMENTO', 'INVALIDO', 'FINALIZADO']) AND
                              SRAD.COD_SOCORRO_ROTA_ATENDIMENTO = SRAT.CODIGO
                 JOIN VEICULO_DATA VD ON SRA.COD_VEICULO_PROBLEMA = VD.CODIGO
                 JOIN COLABORADOR_DATA CD ON SRA.COD_COLABORADOR_ABERTURA = CD.CODIGO
                 JOIN SOCORRO_ROTA_OPCAO_PROBLEMA SROP ON SROP.CODIGO = SRA.COD_PROBLEMA_SOCORRO_ROTA
        WHERE SR.COD_UNIDADE = ANY (F_COD_UNIDADES)
          -- Aplica o filtro por colaborador apenas se não tiver permissão para ver todos
          AND F_IF(F_VER_TODOS, TRUE, SRA.COD_COLABORADOR_ABERTURA = F_COD_COLABORADOR)
          AND (SRA.DATA_HORA_ABERTURA AT TIME ZONE TZ_UNIDADE(SR.COD_UNIDADE)) :: DATE
            BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY SRA.DATA_HORA_ABERTURA DESC;
END;
$$;