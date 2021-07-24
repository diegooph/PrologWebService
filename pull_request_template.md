## :scroll: Descrição
<!--- Descreva suas mudanças em detalhes -->

## :green_heart: Como você testou?
<!--- Informe os testes realizados, de forma breve. Também adicione com qual BD os testes foram feitos. -->

## :pencil: Checklist
<!--- Coloque um `x` nos boxes que se aplicam -->
#### Gerais
- [ ] Atualizar o CHANGELOG
- [ ] Foi aplicado um ordenamento que faz sentido para os usuários (caso seja uma listagem)?
- [ ] REVISOR: review da tarefa verificando DoDs e/ou critérios de aceitação (1 por 1)

#### [BD] Específicas
- [ ] Migration funciona no setup do BD local?
- [ ] Arquivo específico da function ou view atualizado?
- [ ] Funciona na versão do Postgres de prod (12.2)?
- [ ] Verificado o query plan (explain analyze) para uso de indexes e evitar o uso de disco (disk e batches > 1)?

#### Testes
- [ ] Integrações testadas?
- [ ] Testou cenário de erro?
- [ ] Além de funcionar, os dados mostrados estão corretos?
- [ ] Testou apenas com a permissão da função liberada?
- [ ] Testou com o menor e maior nível de acesso a informação (0 e 3)?

## :crystal_ball: Próximos passos
- [ ] Mover migration para pasta 'done'
- [ ] Informar o suporte
