SELECT
  *
FROM CHECKLIST_ORDEM_SERVICO COS
WHERE COS.STATUS = 'A' AND ((SELECT COUNT(COSI.CODIGO)
                             FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                             WHERE COSI.STATUS_RESOLUCAO = 'P'
                                   AND COSI.COD_UNIDADE = COS.COD_UNIDADE
                                   AND COSI.COD_OS = COS.CODIGO) = 0);