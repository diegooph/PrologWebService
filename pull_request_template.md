## :scroll: Description
<!--- Describe your changes in detail -->


## :bulb: Motivation and Context
<!--- Why is this change required? What problem does it solve? -->


## :green_heart: How did you test it?


## :pencil: Checklist
<!--- Put an `x` in the boxes that apply -->
#### Gerais
- [ ] Atualizar apresentação do Sprint Review
- [ ] Atualizar o CHANGELOG
- [ ] Informar banco, servidor e branch utilizados para executar a tarefa
- [ ] REVISOR: review da tarefa verificando DoDs e/ou critérios de aceitação (1 por 1)

#### [WS] Específicas
- [ ] Connection, ResulSet e Statement fechados
- [ ] Integrações testadas
- [ ] Foi aplicado um ordenamento que faz sentido para os usuários (caso seja uma listagem)?

#### [BD] Específicas
- [ ] Migration funciona no setup do BD local?
- [ ] Atualizar no github o arquivo específico da function ou view que foi alterada
- [ ] Funciona na versão do Postgres de prod (12.2)?
- [ ] Foi aplicado um ordenamento que faz sentido para os usuários (caso seja uma listagem)?
- [ ] Verificado o query plan (explain analyze) para uso de indexes e evitar o uso de disco (disk e batches > 1)?
- [ ] Mover migration para pasta 'done'

#### Testes
- [ ] Integrações testadas
- [ ] Além de funcionar, os dados mostrados estão corretos?
- [ ] Caso existam alterações, verificar as dependências
- [ ] Testar funcionalidade com apenas a permissão da função liberada
- [ ] Testar funcionalidade com o menor e maior nível de acesso a informação (0 e 3)
- [ ] Testar cenário de erro


## :crystal_ball: Next steps
- [ ] Realizar o merge dos branchs
- [ ] Informar o suporte
