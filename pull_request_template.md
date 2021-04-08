## :scroll: Descrição
<!--- Descreva suas mudanças em detalhes -->


## :bulb: Motivação e Contexto
<!--- Por que essa mudança é necessária? Que problema ela resolve? -->

## :green_heart: Como você testou?


## :pencil: Checklist
<!--- Coloque um `x` nos boxes que se aplicam -->
#### Gerais
- [ ] Atualizar apresentação do Sprint Review
- [ ] Atualizar o CHANGELOG
- [ ] Informar banco, servidor e branch utilizados para executar a tarefa
- [ ] REVISOR: review da tarefa verificando DoDs e/ou critérios de aceitação (1 por 1)
- [ ] Foi aplicado um ordenamento que faz sentido para os usuários (caso seja uma listagem)?

#### [BD] Específicas
- [ ] Migration funciona no setup do BD local?
- [ ] Atualizar no github o arquivo específico da function ou view que foi alterada
- [ ] Funciona na versão do Postgres de prod (12.2)?
- [ ] Verificado o query plan (explain analyze) para uso de indexes e evitar o uso de disco (disk e batches > 1)?

#### Testes
- [ ] Integrações testadas
- [ ] Além de funcionar, os dados mostrados estão corretos?
- [ ] Caso existam alterações, verificar as dependências
- [ ] Testar funcionalidade com apenas a permissão da função liberada
- [ ] Testar funcionalidade com o menor e maior nível de acesso a informação (0 e 3)
- [ ] Testar cenário de erro


## :crystal_ball: Próximos passos
- [ ] Mover migration para pasta 'done'
- [ ] Realizar o merge dos branchs
- [ ] Informar o suporte
