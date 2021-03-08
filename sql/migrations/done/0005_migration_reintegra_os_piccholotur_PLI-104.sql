-- 4 Deletar lógicamente as O.S.s e os Itens.
-- Deleta COSI
UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
SET DELETADO            = TRUE,
    DATA_HORA_DELETADO  = NOW(),
    PG_USERNAME_DELECAO = SESSION_USER
WHERE COD_OS IN (12898, 12911, 12917, 12897, 12894)
  AND COD_UNIDADE = 107
  AND CODIGO IN (479334, 480870, 482117, 479180, 478947);

-- Deleta COS
UPDATE CHECKLIST_ORDEM_SERVICO_DATA
SET DELETADO            = TRUE,
    DATA_HORA_DELETADO  = NOW(),
    PG_USERNAME_DELECAO = SESSION_USER
WHERE CODIGO IN (12894, 12897, 12898, 12911, 12917)
  AND COD_UNIDADE = 107
  AND COD_CHECKLIST IN
      (1853968, 1854633, 1855131, 1860861, 1865466);

DELETE
FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
WHERE COD_UNIDADE = 107
  AND COD_CHECKLIST_OS_PROLOG IN (1853968, 1854633, 1855131, 1860861, 1865466);

-- 5 - Deletar O.S.s e ItensNok do schema piccolotur.
DELETE
FROM PICCOLOTUR.CHECKLIST_ITEM_NOK_ENVIADO_GLOBUS
WHERE COD_CHECKLIST IN (1853968, 1854633, 1855131, 1860861, 1865466)
  AND COD_UNIDADE = 107;

-- 6 - Remover flag de sincronizados da tabela de checklists_pendentes no schema piccolotur.
UPDATE PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
SET SINCRONIZADO = FALSE
WHERE COD_CHECKLIST_PARA_SINCRONIZAR IN
      (1853968, 1854633, 1855131, 1860861, 1865466);