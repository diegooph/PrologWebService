update piccolotur.checklist_pendente_para_sincronizar
set bloqueado_sincronia = true
where sincronizado is false
  and precisa_ser_sincronizado is true
  and bloqueado_sincronia is false;