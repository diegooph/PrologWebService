-- Sobre:
--
-- Function utilizada para listar as alternativas de um modelo de checklist.
--
-- Essa function utiliza a F_PLACA_VEICULO para descobrir se alguma alternativa possui item pendente na placa em
-- questão. Além de lista as alternativas, a function retorna informações úteis à abertura de O.S, como:
-- TEM_ITEM_OS_PENDENTE, DEVE_ABRIR_ORDEM_SERVICO, QTD_APONTAMENTOS_ITEM, PRIORIDADE_ALTERNATIVA...
--
-- A function não deve filtrar por alternativas ou perguntas ativas. Deve sempre trazer todas. O ws se baseia nesse
-- comportamento.
--
-- Histórico:
-- 2018-12-14 -> Function criada (luizfp).
-- 2019-08-07 -> Adiciona novas colunas no retorno da function (didi - PL-2066).
--               • tem_item_os_pendente.
--               • qtd_apontamentos_item.
--               • prioridade_alternativa.
-- 2020-01-18 -> Function totalmente refatorada para considerar o código da versão do modelo de checklist.
--               Agora ela também retorna se a alternativa é do tipo_outros. E, caso tenha item em aberto, retorna o
--               texto tipo_outros que o usuário forneceu como resposta. Essas alterações fizeram parte da mudança de
--               estrutura do checklist.
-- 2020-01-28 -> Altera para não utilizar tabelas _DATA para buscar itens de OS, pois não queremos incrementar itens
--               deletados.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(F_COD_MODELO_CHECKLIST BIGINT,
                                                                      F_COD_VERSAO_MODELO_CHECKLIST BIGINT,
                                                                      F_PLACA_VEICULO TEXT)
    RETURNS TABLE
            (
                COD_ALTERNATIVA                    BIGINT,
                COD_CONTEXTO_PERGUNTA              BIGINT,
                COD_CONTEXTO_ALTERNATIVA           BIGINT,
                COD_ITEM_ORDEM_SERVICO             BIGINT,
                RESPOSTA_TIPO_OUTROS_ABERTURA_ITEM TEXT,
                TEM_ITEM_OS_PENDENTE               BOOLEAN,
                DEVE_ABRIR_ORDEM_SERVICO           BOOLEAN,
                ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
                QTD_APONTAMENTOS_ITEM              INTEGER,
                PRIORIDADE_ALTERNATIVA             TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    STATUS_ITEM_PENDENTE TEXT = 'P';
BEGIN
    RETURN QUERY
        -- Nessa CTE nós não usamos as tabelas com _DATA pois não queremos incrementar quantidade de itens de OS
        -- deletados.
        WITH ITENS_PENDENTES AS (
            SELECT COSI.CODIGO                               AS COD_ITEM_ORDEM_SERVICO,
                   COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO AS COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                   COSI.COD_CONTEXTO_ALTERNATIVA             AS COD_CONTEXTO_ALTERNATIVA,
                   COSI.QT_APONTAMENTOS                      AS QTD_APONTAMENTOS_ITEM,
                   COS.COD_CHECKLIST                         AS COD_CHECKLIST,
                   C.COD_CHECKLIST_MODELO                    AS COD_CHECKLIST_MODELO
            FROM CHECKLIST C
                     JOIN CHECKLIST_ORDEM_SERVICO COS
                          ON C.CODIGO = COS.COD_CHECKLIST
                     JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                          ON COS.CODIGO = COSI.COD_OS
                              AND COS.COD_UNIDADE = COSI.COD_UNIDADE
            WHERE C.PLACA_VEICULO = F_PLACA_VEICULO
              AND C.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
              AND COSI.STATUS_RESOLUCAO = STATUS_ITEM_PENDENTE
        )
        SELECT CAP.CODIGO                                          AS COD_ALTERNATIVA,
               CP.CODIGO_CONTEXTO                                  AS COD_CONTEXTO_PERGUNTA,
               CAP.CODIGO_CONTEXTO                                 AS COD_CONTEXTO_ALTERNATIVA,
               IP.COD_ITEM_ORDEM_SERVICO                           AS COD_ITEM_ORDEM_SERVICO,
               CRN.RESPOSTA_OUTROS                                 AS RESPOSTA_TIPO_OUTROS_ABERTURA_ITEM,
               F_IF(IP.COD_ITEM_ORDEM_SERVICO ISNULL, FALSE, TRUE) AS TEM_ITEM_OS_PENDENTE,
               CAP.DEVE_ABRIR_ORDEM_SERVICO                        AS DEVE_ABRIR_ORDEM_SERVICO,
               CAP.ALTERNATIVA_TIPO_OUTROS                         AS ALTERNATIVA_TIPO_OUTROS,
               IP.QTD_APONTAMENTOS_ITEM                            AS QTD_APONTAMENTOS_ITEM,
               CAP.PRIORIDADE::TEXT                                AS PRIORIDADE_ALTERNATIVA
        -- Nesse SELECT é utilizado a _DATA, pois um item pode estar pendente e sua alternativa deletada, nesse caso
        -- a alternativa ainda deve retornar para não quebrar o fluxo de processamento do checklist realizado.
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA_DATA CAP
                 JOIN CHECKLIST_PERGUNTAS_DATA CP
                      ON CAP.COD_PERGUNTA = CP.CODIGO
                 LEFT JOIN ITENS_PENDENTES IP
                           ON IP.COD_CONTEXTO_ALTERNATIVA = CAP.CODIGO_CONTEXTO
                 LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                           ON CRN.COD_ALTERNATIVA = IP.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
                               AND CRN.COD_CHECKLIST = IP.COD_CHECKLIST
        WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = F_COD_VERSAO_MODELO_CHECKLIST;
END ;
$$;