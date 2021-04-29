### Descrição do PR
...

### DoDs
#### Gerais
- [ ] Atualizar apresentação do Sprint Review
- [ ] Informar banco (local ou remoto?) e branch WS utilizados para executar a tarefa
- [ ] REVISOR: review da tarefa verificando DoDs e/ou critérios de aceitação (1 por 1)
- [ ] Realizar o merge dos branchs e deletar o antigo

#### [BD] Específicas
- [ ] Integrações testadas
- [ ] Migration criado e commitado
- [ ] Migration funciona no setup do BD local?
- [ ] Atualizar no github o arquivo específico da function ou view que foi alterada
- [ ] Funciona na versão do Postgres de prod (12.2)?
- [ ] Impacta functions de suporte?
- [ ] Foi aplicado um ordenamento que faz sentido para os usuários (caso seja uma listagem)?
- [ ] Verificado o query plan (explain analyze) para uso de indexes e evitar o uso de disco (disk e batches > 1)?
- [ ] Mover migration para pasta 'done'

#### Testes
- [ ] Além de funcionar, os dados mostrados estão corretos?
- [ ] Caso existam alterações, verificar as dependências
- [ ] Testar cenário de erro
