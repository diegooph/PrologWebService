Change Log
==========

<a name="v0.0.97"></a>
## Version [v0.0.97](https://github.com/luizfp/PrologWebService/compare/v0.0.96...v0.0.97) (release-date) [unreleased]

### Features
* Cria funcionalidade de listagem de marcações otimizada, com filtro de data. (PL-2000)

### Bug Fixes
* Corrige problema no fechamento de serviço de movimentação (PL-2114)

<a name="v0.0.96"></a>
## Version [v0.0.96](https://github.com/luizfp/PrologWebService/compare/v0.0.95...v0.0.96) (2019-06-21)

### Refactors
* Adiciona data/hora importado ProLog no objeto de checklist

### Bug Fixes
* Corrige inserção de checklist, seta autoCommit = false;

<a name="v0.0.95"></a>
## Version [v0.0.95](https://github.com/luizfp/PrologWebService/compare/v0.0.94...v0.0.95) (2019-06-20)

### Features
* Integração de pneus com o Rodopar (PL-2024)

### Bug Fixes
* Corrige componente da dash relatório status de placas aferição (PL-1672)

<a name="v0.0.94"></a>
## Version [v0.0.94](https://github.com/luizfp/PrologWebService/compare/v0.0.93...v0.0.94) (2019-06-10)

### Features
* Cria método para permitir a deleção de múltiplos itens do pré contracheque (PL-2002)
* Cria relatório com as resposta de um quiz (PL-1998)
* Implementa funcionalidade de transferência de veículos (PL-1532)

### Refactors
* Refatora relatórios do quiz para o padrão atual do ProLog (PL-2053)
* Adiciona placa ao exibir processo de transferência de pneu que ocorreu por conta da transferência de uma placa (PL-1941)
* Altera criação de O.S. para não setar mais o código - agora é gerado por trigger (PL-2068)
* Altera `ProLogException` para estender `RuntimeException` (PL-2015)
* Altera servidor para pegar e converter todos as exceptions para `ProLogError`, com exceção de 401 (PL-2015)

### Bug Fixes
* Adiciona permissão de realizar quiz na busca de um modelo específico (PL-2056)
* Corrige componente da dash checklists realizados em um determinado tempo (PL-1865)
* Corrige componente da dash de quantidade de dias com aferições vencidas (PL-1736)


<a name="v0.0.93"></a>
## Version [v0.0.93](https://github.com/luizfp/PrologWebService/compare/v0.0.92...v0.0.93) (2019-05-30)

### Features
* Cria integração de aferição de placas com o Protheus da Rodalog (PL-1721)
* Cria relatório de desgaste irregular dos pneus (PL-1996)

### Refactors
* Inserir verificações no fechamento de Itens de O.S da integração Transport (PL-2023)
* Altera integração com a Avilan para voltar a enviar ao App o KM atual do veículo ao invés de 0 (PL-2035)
* Cria nova estrutura para busca de permissões por usuário, com detalhes e concentrando em uma única consulta (PL-2004)

<a name="v0.0.92"></a>
## Version [v0.0.92](https://github.com/luizfp/PrologWebService/compare/v0.0.91...v0.0.92) (2019-05-16)

### Features
* Disponibilizar Exportação da Listagem de Veículos (PL-1964)
* Cria método para verificar se unidade tem tipo definido como jornada (PL-1734)

### Refactors
* Adiciona informações de placa e posição nos objetos de pneus quando aplicados (PL-1942)
* Permite filtrar por colaboradores ativos nos relatórios de folha ponto (PL-1905)
* Altera o filtro da listagem para buscar imagens apenas de modelos ativos (PL-1908)
* Altera integração com a Avilan para enviar ao App o KM atual do veículo
como 0 ao iniciar um check ou aferição. Além disso, ao finalizar esse check
ou aferição, a integração impede que seja enviado 0, o usuário será obrigado
a alterar o valor (PL-1966)
* Adiciona duração da última viagem no objeto de colaborador em descanso do acompanhamento de viagens (PL-1710)
* Atualiza versão da lib Apache PDFBox (PL-1707)
* Seta vida atual e total dos pneus ao buscar uma aferição por código (PL-1677)

### Bug Fixes
* Corrige busca de tipos de marcação da unidade ao gerar relatórios (PL-1667)

<a name="v0.0.91"></a>
## Version [v0.0.91](https://github.com/luizfp/PrologWebService/compare/v0.0.90...v0.0.91) (2019-05-06)

### Bug Fixes
* Remove resolução duplicada de itens de O.S.

<a name="v0.0.90"></a>
## Version [v0.0.90](https://github.com/luizfp/PrologWebService/compare/v0.0.89...v0.0.90) (2019-05-05)

### Features
* Cria integração de ordens de serviço do checklist com o sistema Transport da Translecchi

<a name="v0.0.89"></a>
## Version [v0.0.89](https://github.com/luizfp/PrologWebService/compare/v0.0.88...v0.0.89) (2019-04-27)

### Refactors
* Permite atualizar o tipo de um veículo se não houverem pneus aplicados (PL-1903)

* Verifica permissão de relatórios de quiz na busca dos modelos da unidade

<a name="v0.0.88"></a>
## Version [v0.0.88](https://github.com/luizfp/PrologWebService/compare/v0.0.87...v0.0.88) (2019-04-11)

### Features
* Cria novos métodos para relatório de listagem de colaboradores (PL-1892)
* Cria métodos para permitir a realização de checklists offline

<a name="v0.0.87"></a>
## Version [v0.0.87](https://github.com/luizfp/PrologWebService/compare/v0.0.86...v0.0.87) (2019-03-28)

### Features
* Cria novo package e estrutra de DAO para os métodos dos tipos de veículos (PL-1718)
* Cria testes para validar métodos dos tipos de veículos (PL-1719)
* Cria relatório para buscar o status atual dos pneus (PL-1855)

### Refactors
* Altera insert/update do tipo de veículo para passar a vincular o diagrama (PL-1830)

### Bug Fixes
* Corrige conversão do TipoMarcacao para geração do relatório de jornada

<a name="v0.0.86"></a>
## Version [v0.0.86](https://github.com/luizfp/PrologWebService/compare/v0.0.85...v0.0.86) (2019-03-24)

### Features
* Cria novos métodos de busca para a listagem de cargos (PL-1656)

<a name="v0.0.85"></a>
## Version [v0.0.85](https://github.com/luizfp/PrologWebService/compare/v0.0.84...v0.0.85) (2019-03-15)

### Features
* Cria relatório de vencimento de dot dos pneus
* Cria método de busca dos modelos de quizzes para listagem (PL-1743)

### Refactors
* Cria e verifica novas permissões das movimentações (PL-1812)
* Salva data/hora de início e fim da resolução de itens de O.S. (PL-1809)
* Adiciona data/hora de início e fim de resolução ao buscar itens de O.S. (PL-1821)

<a name="v0.0.84"></a>
## Version [v0.0.84](https://github.com/luizfp/PrologWebService/compare/v0.0.83...v0.0.84) (2019-02-09)

### Features
* Cria novos métodos de busca de marcas e modelos de veículos (PL-1441)
* Cria function para deleção de checklist e suas respectivas ordens de serviço (PL-1607)
* Cria novo diagrama para carreta 2 eixos single (PL-1569, PL-1570, PL-1571)
* Cria schema de backup (PL-1658)
* Cria teste para validar relatório de totais por tipo de marcação (PL-1617)
* Permite ao cliente selecionar quais tipos de marcação descontam do cálculo de jornada bruta e líquida (PL-1648)
* Cria métodos para nova tela de acompanhamento de viagens do controle de jornada (PL-1661)

### Refactors
* Remove validação de letras e números do cadastro de veículos (PL-1636)
* Adiciona data de geração de relatório no cronograma de aferição (PL-1521)
* Realiza backup de dados antes de deletar checklist (PL-1659)
* Adiciona novo atributo para identificar se um tipo é jornada (PL-1578)
* Adiciona total de horas noturnas por tipo no relatório Tempo Total Por Tipo de Marcação (PL-1530)
* Altera forma de cálculo do total de horas do relatório de folha de ponto (PL-1576)
* Altera busca de um tipo de marcação para utilizar function (PL-1681)

### Bug Fixes
* Corrige criação de componente da dashboard de aferições realizadas (PL-1643)
* Corrige componente na dashboard de relatos pendentes (PL-1666)
* Altera nome no header do relatório de totais por tipo de marcação para evitar problema com colunas de mesmo nome (PL-1159)
* Corrige criação de token de controle de jornada caso unidade não possua (PL-1575)
* Previne caso de erro causado por marcação de fim antes do início no relatório de folha ponto (PL-1682)

<a name="v0.0.83"></a>
## Version [v0.0.83](https://github.com/luizfp/PrologWebService/compare/v0.0.82...v0.0.83) (2019-01-11)

### Refactors
* Deixa sulcos do cadastro de pneus opcional e lida com possibilidade de sulcos nulos em algumas partes do sistema

<a name="v0.0.82"></a>
## Version [v0.0.82](https://github.com/luizfp/PrologWebService/compare/v0.0.81...v0.0.82) (2019-01-07)

### Features
* Cria funcionalidade para permitir a transferência de pneus entre unidades
* Cria atributo na alternativa do checklist para definir se ela deve ou não abrir O.S.
* Cria integração com a Praxio

### Refactors
* Altera forma de parse do arquivo de prontuário do condutor para ignorar colunas vazias

### Bug Fixes
* Corrige criação dos componentes resumidos da dashboard
* Força a parte de controle de jornada a retornar 401 caso token inválido

<a name="v0.0.81"></a>
## Version [v0.0.81](https://github.com/luizfp/PrologWebService/compare/v0.0.80...v0.0.81) (2018-12-11)

### Refactors
* Bloqueia resources antigos das ordens de serviço do checklist
* Altera forma de import do prontuário do condutor
* Lida com nova coluna no import do prontuário do condutor (powerOn)

<a name="v0.0.80"></a>
## Version [v0.0.80](https://github.com/luizfp/PrologWebService/compare/v0.0.79...v0.0.80) (2018-12-10)

### Features
* Cria componente para a Dashboard que mostra a quantidade de aferições realizadas por dia, separando por tipo, em um 
intervalo de 30 dias.
* Cria componente para a Dashboard que mostra a quantidade de relatos pendentes, separando por tipo.
* Cria parte de gestão de marcações para o controle de jornada

### Refactors
* Altera verificação de vlBateuJornMot, vlNaoBateuJornMot, vlRecargaMot, vlBateuJornAju, vlNaoBateuJornAju e
vlTotalMapa no import do mapa para lidar com possibilidade de vazio na célula importada
* Altera valor do fator de 0 para 1 no import do mapa, caso seja 0
* Altera forma de vínculo de marcações para utilizar vínculo por código e não mais automático por data/hora
* Altera objetos da parte de Ordem de Serviço de checklist

<a name="v0.0.79"></a>
## Version [v0.0.79](https://github.com/luizfp/PrologWebService/compare/v0.0.78...v0.0.79) (2018-11-23)

### Features
* Cria relatório do cronograma das aferições de placas
* Altera busca de uma nova aferição para incluir parâmetros definidos pelo cliente para exibição de alerta de sulco
* Cria componente para a Dashboard que mostra os checklists realizados em menos de 1 minuto e 30 segundos
* Adiciona filtros ao relatório de dados gerais
* Cria componente para a Dashboard que mostra a quantidade de dias que as aferições estão vencidas

### Refactors
* Adiciona infos extras no gráfico de scatter dos pneus com menor sulco e pressão da dashboard

<a name="v0.0.78"></a>
## Version [v0.0.78](https://github.com/luizfp/PrologWebService/compare/v0.0.77...v0.0.78) (2018-11-06)

### Features
* Cria nova function e novos métodos para gerar relatório de listagem de modelos de checklist
* Cria nova function e novos métodos para gerar relatório de dados gerais de checklist

### Refactors
* Altera nome dos métodos e da function referente ao relatório de marcações diárias
* Acrescenta colunas ao relatório de marcações diárias

### Bug Fixes
* Remove autenticação com @Secured do insert de marcações para corrigir problema no envio da data de nascimento do app

<a name="v0.0.77"></a>
## Version [v0.0.77](https://github.com/luizfp/PrologWebService/compare/v0.0.76...v0.0.77) (2018-10-19)

### Features
* Cria relatório para mostrar o KM rodado por pneu e por vida

### Refactors
* Altera insert do relato para salvar a versão do app que fez a requisição

### Bug Fixes
* Corrige busca das alternativas disponíveis para realização de um relato
* Corrige método de busca de um relato pelo seu código
* Corrige query de relatório da solicitação de folga
* Corrige leitura da hrSaida e hrEntrada no import do mapa

<a name="v0.0.76"></a>
## Version [v0.0.76](https://github.com/luizfp/PrologWebService/compare/v0.0.75...v0.0.76) (2018-10-02)

### Features
* Permite filtro por múltiplas unidades nos relatórios do checklist
* Cria gráfico em linhas como um componente da dashboard
* Cria método para fechamento de múltiplos itens de O.S. do checklist
* Cria nova function e novos métodos para gerar relatório geral de movimentação
* Cria componente da dashboard para buscar quantidade de checklists realizados por dia
* Cria componente da dashboard para buscar quantidade de itens de O.S. abertos por prioridade
* Cria componente da dashboard para buscar placas com maior quantidade de itens de O.S. abertos

### Refactors
* Atualiza versão da biblioteca univocity-parsers para 2.7.5
* Altera métodos de busca dos itens de O.S. do checklist

<a name="v0.0.75"></a>
## Version [v0.0.75](https://github.com/luizfp/PrologWebService/compare/v0.0.74...v0.0.75) (2018-09-04)

### Features
* Cria nova function e novos métodos para gerar relatório geral de aferição

### Bug Fixes
* Fixa delimitadores aceitos na escala diária (isso melhora e ajuda a biblioteca de parse a identificar o delimitador
utilizado no arquivo)
* Previne quebra da aplicação ao importar escala diária sem CPF de ajudante
* Corrige parse do código da rota na escala diária
* Corrige criação das alternativas 'tipo outros' do modelo de checklist

<a name="v0.0.74"></a>
## Version [v0.0.74](https://github.com/luizfp/PrologWebService/compare/v0.0.73...v0.0.74) (2018-08-30)

### Features
* Cria funcionalidade para permitir a aferição avulsa de pneus em estoque
* Cria relatório de aferições avulsas
* Cria listagem de aferições avulsas (por enquanto através de um Report)
* Cria DummyResource para retornar um JSON dummy de alguns objetos
* Cria retorno de JSON padrão do objeto Cargo e Visao no DummyResource
* Cria retorno de JSON padrão do objeto TipoIntervalo no DummyResource
* Permite filtrar por tipos de intervalo ativos/inativos ao realizar a buscas dos tipos de uma unidade
* Cria permissão para permitir alterar tipos de intervalo

### Refactors
* Bloqueia funcionalidade de aferição nos apps antigos (versionCode menor ou igual a 57) por conta das alterações geradas
pela implementação da aferição avulsa
* Altera método de inativação de tipo de intervalo para permitir ativar/inativar
* Adiciona verificação de permissões nos métodos de gestão de tipos de intervalo
* Permite apenas que tipos de intervalo ativos sejam editados
* Remove código da unidade dos métodos utilizados na PneuDao para atualizar algum atributo do pneu
* Cria novo método para marcar uma foto do cadastro do pneu como sincronizada sem utilizar o código da unidade
* Bloqueia PneuResource e MovimentacaoResoruce nos apps antigos (versionCode menor ou igual a 57) por conta de
alterações realizadas na parte de pneus (principalmente ligadas a sincronia de fotos do cadastro)
* Altera integração com a Avilan para incluir pressao recomendada nas buscas dos pneus


### Bug Fixes
* Corrige verificação de permissão para insert de itens da produtividade da Raízen
* Corrige alguns problemas de permissões não sendo verificadas
* Corrige dateFormat utilizado na produtividade da Raízen
* Altera DateFormat utilizado no upload da produtividade Raízen para previnir linhas de quebrarem por conta de uma /
* Previne crash causado no import de mapa caso matrícula não esteja presente
* Corrige parse do BigDecimal no import da produtividade da Raízen para diferentes tipos de arquivo importados (XLSX ou CSV)

<a name="v0.0.73"></a>
## Version [v0.0.73](https://github.com/luizfp/PrologWebService/compare/v0.0.72...v0.0.73) (2018-08-13)

### Features
* Cria novo método no Resource para busca dos itens de OS do checklist
* Cria teste para validar a migração dos dados realizada nas tabelas de checklist (troca das PKs)
* Cria teste para garantir que não haja INSERT/UPDATE em views no servidor

### Refactors
* Otimiza busca do farol do checklist fazendo apenas uma function para buscar tudo
* Otimiza busca dos itens de OS do checklist (tela das bolinhas de prioridade no App)
* Otimiza busca dos checklists fazendo apenas uma consulta para quando se está buscando os checklists resummidos

### Bug Fixes
* Corrige parse do BigDecimal no import da produtividade da Raízen

<a name="v0.0.72"></a>
## Version [v0.0.72](https://github.com/luizfp/PrologWebService/compare/v0.0.71...v0.0.72) (2018-08-05)

### Features
* Cria produtividade da Raízen

<a name="v0.0.71"></a>
## Version [v0.0.71](https://github.com/luizfp/PrologWebService/compare/v0.0.70...v0.0.71) (2018-07-31)

### Features
* Cria novo método para buscar todos os checklists completos
* Cria busca dos colaboradores por código de empresa

### Refactors
* Cria functions para a busca de colaboradores
* Altera path da busca de todos os colaboradores de uma unidade

### Bug Fixes
* Corrige cálculo das horas noturnas no relatório de folha de ponto do controle de jornada

<a name="v0.0.70"></a>
## Version [v0.0.70](https://github.com/luizfp/PrologWebService/compare/v0.0.69...v0.0.70) (2018-07-29)

### Features
* Cria relatório com o tempo total por tipo de marcação no controle de jornada
* Cálcula horas noturnas no relatório de folha de ponto

<a name="v0.0.69"></a>
## Version [v0.0.69](https://github.com/luizfp/PrologWebService/compare/v0.0.68...v0.0.69) (2018-07-19)

### Features
* Cria validator para o insert do Modelo de Quiz
* Cria relatório para mostrar o total de tempo gasto em cada tipo de intervalo por cada colaborador

### Refactors
* Adiciona permissão de movimentação na busca de uma recapadora específica

### Bug Fixes
* Corrige busca dos relatos para utilizar o código da equipe na filtragem ao invés do nome
* Adiciona permissão de movimentação na busca dos tipos dos serviços dos pneus
* Corrige update da banda de um pneu

<a name="v0.0.68"></a>
## Version [v0.0.68](https://github.com/luizfp/PrologWebService/compare/v0.0.67...v0.0.68) (2018-07-14)

### Features
* Cria error code específico para versão do app bloqueada
* Permite setar uma mensagem de erro quando bloquear uma versão do app

### Refactor
* Altera realtórios de pneus para possibilitar o filtro por mais de uma unidade
* Altera paths dos relatórios de pneus para seguir o padrão REST
* Altera busca das marcas/modelos de veículos para retornar mesmo as marcas das quais a empresa ainda não tenha modelos
* Seta `ResetAbandonedTimer` para resetar timer de abandono de uma connection
* Aumenta para 6 min timeout até uma connection ser considerada abandonada

### Bug Fixes
* Corrige query que busca a quantidade de serviços em aberto para mostrar na Dashboard
* Corrige query que busca a quantidade de serviços em aberto na listagem de serviços

<a name="v0.0.67"></a>
## Version [v0.0.67](https://github.com/luizfp/PrologWebService/compare/v0.0.66...v0.0.67) (2018-07-09)

### Bug Fixes
* Remove uso de anotação @NotNull da developerMessage na ProLogException
* Verifica se matrícula ambev ou da transportadora são nulas antes de setar no objeto Colaborador

<a name="v0.0.66"></a>
## Version [v0.0.66](https://github.com/luizfp/PrologWebService/compare/v0.0.65...v0.0.66) (2018-07-04)

### Refactors
* Remove utilização da tabela UNIDADE_FUNCAO do servidor

### Bug Fixes
* Lida com novo status no prontuário do condutor

<a name="v0.0.65"></a>
## Version [v0.0.65](https://github.com/luizfp/PrologWebService/compare/v0.0.64...v0.0.65) (2018-07-03)

### Features
* Cria back end para salvar mensagens de contato de possíveis clientes
* Ativa método de listagem de marcações de um colaborador

### Refactor
* Altera componente da Dashboard para mostrar apenas os veículos ativos e não apenas os veículos com pneus vinculados
* Previne retornar valor null para integração para tokens inválidos
* Permite aferições de estepe na integração com Avilan
* Adiciona a coluna cod_unidade_cadastro nas tabelas: colaborador, pneu e veiculo
* Remove o atributo setor da tabela colaborador
* Padroniza as mensagens de erro para cadastro de colaboradores, veículos e pneus
* Altera busca dos cargos por unidade para sempre buscar todos os cargos da empresa que essa unidade pertence
* Altera import do prontuário do contudor para se adequar ao novo colunamento da planilha base

### Bug Fixes
* Insere permissão de relatórios na busca dos tipos de intervalo no resource já depreciado
* Permite que usuários com permissões de colaboradores (visualizar, inserir e editar) possam buscar as equipes
* Remove o default do atributo cod_empresa da tabela colaborador

### Features
* Permite cadastro de tipos de serviços;

### Refactor
* Altera movimentação para inserir serviços realizados na análise;
* Altera cadastro para inserir um serviço de recapagem para os cadastros
de pneus que não estão na primeira vida;

<a name="v0.0.64"></a>
## Version [v0.0.64](https://github.com/luizfp/PrologWebService/compare/v0.0.63...v0.0.64) (2018-06-13)

### Refactor
* Remove JOIN não necessário na busca de uma aferição por código
* Deixa DOT opcional no cadastro do Pneu (`PneuValidator`)
* Agrupa métodos em comum utilizados nos validators
* Altera relatório de OS do checklist para function
* Modifica validação da banda do Pneu (`PneuValidator`)
* Melhora fluxo de lançamento de exceções e propagação nos services

<a name="v0.0.63"></a>
## Version [v0.0.63](https://github.com/luizfp/PrologWebService/compare/v0.0.62...v0.0.63) (2018-06-05)

### Features
* Permite ativar/inativar um modelo de checklist

### Refactors
* Altera fluxo de edição de um modelo de checklist
* Lida corretamente com exceções no insert do colaborador/veículo/pneu

### Bug Fixes
* Corrige validações feitas no cadastro/edição de colaborador
* Corrige busca de uma aferição por código removendo comparação de código de unidade com a tabela `PNEU`

<a name="v0.0.62"></a>
## Version [v0.0.62](https://github.com/luizfp/PrologWebService/compare/v0.0.61...v0.0.62) (2018-06-03)

### Features
* Cria validador para o insert do pneu
* Cria validador para o insert do colaborador
* Cria validador para o insert do veículo

### Refactors
* Altera insert da `Recapadora` para retornar o código do banco

### Bug Fixes
* Loga erros que possam acontecer no `RecapadoraService`
* Corrige busca dos modelos de checklist, buscando apenas veículos ativos que podem realizar o checklist
* Corrige busca das aferições

<a name="v0.0.61"></a>
## Version [v0.0.61](https://github.com/luizfp/PrologWebService/compare/v0.0.60...v0.0.61) (2018-05-22)

### Refactors
* Altera pasta de salvamento das imagens do checklist

### Bug Fixes
* Corrige join com tabela `PNEU_VALOR_VIDA` na busca dos pneus
* Sobe exception caso ocorra ao enviar uma imagem com `S3FileSender`
* Corrige busca da quantidade de pneus com pressão incorreta (dashboard)

<a name="v0.0.60"></a>
## Version [v0.0.60](https://github.com/luizfp/PrologWebService/compare/v0.0.59...v0.0.60) (2018-05-17)

### Refactors
* Adiciona código do colaborador na criação do objeto na `ColaboradorDaoImpl`

### Bug Fixes
* Seta credenciais da Amazon ao logar usuário caso ele tenha permissão de cadastro de pneu

<a name="v0.0.59"></a>
## Version [v0.0.59](https://github.com/luizfp/PrologWebService/compare/v0.0.58...v0.0.59) (2018-05-15)

### Bug Fixes
* Corrige busca do cronograma da aferição na integração com a Avilan

<a name="v0.0.58"></a>
## Version [v0.0.58](https://github.com/luizfp/PrologWebService/compare/v0.0.57...v0.0.58) (2018-05-15)

### Features
* Cria método para buscar um tipo de intervalo específico
* Cria estrutura para permitir configurar quais tipos de veículo podem realizar determinados tipos de aferição
* Cria classes para barrar requisições de versões específicas do App Android

### Refactors
* Altera nomes de alguns atributos do `ModeloChecklist`
* Seta código de unidade na busca dos modelos de checklist de uma unidade
* Refatora o sistema para lidar com o novo código único do pneu e com o código do cliente
* Bloqueia resources ligados a pneu para os apps com version code menor ou igual a 51
* Altera insert do pneu para retornar um `AbstractResponse`

### Bug Fixes
* Aprimora fluxo de inserção de um modelo de checklist
* Corrige busca dos modelos de checklist disponíveis
* Lida com caso de alternativas nulas na inserção do modelo de checklist
* Altera busca dos prontuários dos condutores para levar em conta apenas colaboradores ativos

<a name="v0.0.57"></a>
## Version [v0.0.57](https://github.com/luizfp/PrologWebService/compare/v0.0.56...v0.0.57) (2018-05-08)

### Features
* Permite importar arquivos XLSX, além de CSV, para a função de Escala Diária
* Cria relatório contendo o resumo geral dos pneus
* Cria método para buscar uma recapadora específica

### Refactors
* Altera path de relatório de produtividade do colaborador

### Bug Fixes
* Corrige criação de Veiculos na integração com a Avilan
* Corrige insert de um item da Escala Diária
* Corrige busca das recapadoras
* Corrige upload de uma imagem do modelo de checklist

<a name="v2.0.56"></a>
## Version [v2.0.56](https://github.com/luizfp/PrologWebService/compare/v2.0.55...v2.0.56) (2018-04-27)

### Bug Fixes
* Corrige problemas no relatório de folha de ponto dos colaboradores

<a name="v2.0.55"></a>
## Version [v2.0.55](https://github.com/luizfp/PrologWebService/compare/v2.0.54...v2.0.55) (2018-04-23)

### Features
* Cria relatório da folha de ponto dos colaboradores
* Cria funcionalidade de escala diária
* Adiciona nova lib para tratar o import dos CSVs

### Bug Fixes
* Corrige busca do relatório de serviços em aberto
* Corrige erro causado no import do mapa quando valor em horas era zero

<a name="v2.0.54"></a>
## Version [v2.0.54](https://github.com/luizfp/PrologWebService/compare/v2.0.53...v2.0.54) (2018-04-16)

### Features
* Cria estrutura para salvar/buscar fotos do pneu no momento do cadastro

### Refactors
* Corrige URLs das fotos do relato antes de salvar o relato
* Adiciona atributo de regional e unidade alocados no veículo
* Cria erro específico na integração com Avilan para impedir usuário de aferir veículos de outra unidade

<a name="v2.0.53"></a>
## Version [v2.0.53](https://github.com/luizfp/PrologWebService/compare/v2.0.52...v2.0.53) (2018-04-12)

### Features
* Cria classe utilitária para migrar veículos/pneus entre unidades

### Refactors
* Altera URL na integração com Avilan

### Bug Fixes
* Corrige crash no resource antigo dos intervalos ao realizar a conversão para o novo objeto

<a name="v2.0.52"></a>
## Version [v2.0.52](https://github.com/luizfp/PrologWebService/compare/v2.0.51...v2.0.52) (2018-04-03)

-- Sem descrição

<a name="v2.0.51"></a>
## Version [v2.0.51](https://github.com/luizfp/PrologWebService/compare/v2.0.50...v2.0.51) (2018-04-02)

### Features
* Cria relatório dos serviços em aberto dos pneu

### Bug Fixes
* Corrige insert do pneu (seta `false` caso `pneuNovoNuncaRodado` não seja informado)
* Corrige NPE ao verificar se um pneu nunca rodou

<a name="v2.0.50"></a>
## Version [v2.0.50](https://github.com/luizfp/PrologWebService/compare/v2.0.49...v2.0.50) (2018-04-01)

### Features
* Adiciona atributo para sabermos se um eixo é direcional no objeto `EixoVeiculo`
* Adiciona atributo para sabermos se devemos aferir os estepes dos veículos no objeto `NovaAfericao`
* Cria relatório para simular folha de ponto (intervalos)
* Cria resource específico para os relatórios dos serviços dos pneus
* Cria pool de conexões com o banco para melhorar a performance do servidor
* Adiciona atributo no objeto `Pneu` para sabermos se ele nunca foi rodado
* Cria conceito de galeria pública de imagens para o checklist
* Cria conceito de modelos de checklist padrões disponibilizados pelo ProLog
* Cria novo método para busca das URLs das imagens dos modelos de checklist que um colaborador tem acesso no path:
`checklists/modelos/url-imagens/{codUnidade}/{codFuncao}`

### Refactors
* Adiciona código da unidade ao objeto `Servico`
* Adiciona código da unidade ao objeto `Afericao`
* Altera nome tabela de `EMPRESA_RESTRICAO_PNEU` para `PNEU_RESTRICAO_UNIDADE`

### Bug Fixes
* Salva KM do veículo caso um serviço de pneus seja automaticamente fechado

#### Deprecated
* Método de busca das URLs das perguntas dos modelos de checklist que um colaborador tem acesso no path:
`checklists/urlImagens/{codUnidade}/{codFuncao}`

<a name="v2.0.49"></a>
## Version [v2.0.49](https://github.com/luizfp/PrologWebService/compare/v2.0.48...v2.0.49) (2018-03-19)

### Features
* Cria relatório dos intervalos seguindo padrão da portaria 1510
* Cria teste para busca dos intervalos agruapdos

### Refactors
* Remove verificação de permissões do método de busca dos filtros

### Bug Fixes
* Corrige query de busca da quantidade de aferições realizadas na semana atual
* Corrige busca do relatório de indicadores
* Corrige function de busca dos intervalosa agrupados
* Corrige insert do colaborador
* Corrige busca dos checklists

<a name="v2.0.48"></a>
## Version [v2.0.48](https://github.com/luizfp/PrologWebService/compare/v2.0.47-h1...v2.0.48) (2018-03-16)

--

<a name="v2.0.47-h1"></a>
## Version [v2.0.47-h1](https://github.com/luizfp/PrologWebService/compare/v2.0.47...v2.0.47-h1) (2018-03-05)

### Bug Fixes
* Corrige queries de busca dos relatos
* Corrige cálculo de dias desde a última aferição na integração com a Avilan

<a name="v2.0.47"></a>
## Version [v2.0.47](https://github.com/luizfp/PrologWebService/compare/v2.0.46...v2.0.47) (2018-03-05)

<a name="v2.0.46"></a>
## Version [v2.0.46](https://github.com/luizfp/PrologWebService/compare/v2.0.45...v2.0.46) (2018-02-23)

### Bug Fixes
* Corrige calculo do resultado de alguns indicadores (HL/PDV/NF)

<a name="v2.0.45"></a>
## Version [v2.0.45](https://github.com/luizfp/PrologWebService/compare/v2.0.44...v2.0.45) (2018-02-08)

### Features
* Implementa componentes da dashboard
* Cria método para buscar os motivos de descarte de uma empresa
* Cria método para cadastrar um motivo de descarte
* Cria método para ativar/desativar um motivo de descarte
* Cria teste para garantir que os códigos das permissões não se repetem dentro da classe `Pilares`
* Cria permissão específica para visualizar os relatórios dos veículos

### Refactor
* Remove métodos deprecated das seguintes classes: `EmpresaService`, `EmpresaDao`, `ProntuarioCondutorResource`,
`EmpresaDaoImpl`, `VeiculoResource`, `ProdutividadeResource`, `SolicitacaoFolgaDaoImpl`, `FaleConoscoDao`, 
`RelatorioEntregaResource`, `VeiculoDaoImpl`, `IndicadorResource`, `OrdemServicoResource`, `ContrachequeResource`,
`ImportResource`, `RelatorioPneuDaoImpl`
* Altera métodos utilizados na marcação de intervalo para permitirem autenticação BASIC

### Bug Fixes
* Corrige update do veículo

<a name="v2.0.44"></a>
## Version [v2.0.44](https://github.com/luizfp/PrologWebService/compare/v2.0.43-h1...v2.0.44) (2017-01-19)

### Refactor
* Altera seguintes classes para utilizarem Injection: `ControleIntervaloDaoImpl`, `ControleIntervaloRelatorioDaoImpl`,
`QuizDaoImpl`, `ProntuarioCondutorDaoImpl`, `FaleConoscoRelatorioDaoImpl`, `FaleConoscoDaoImpl`, `QuizModeloDaoImpl`,
`QuizRelatorioDaoImpl`, `SolicitacaoFolgaDaoImpl`, `SolicitacaoFolgaRelatorioDaoImpl`, `TreinamentoDaoImpl`, `MapaDaoImpl`,
`RelatoDaoImpl`, `RelatoRelatorioDaoImpl`, `LogDaoImpl`, `TrackingDaoImpl`
* Altera update do Pneu para setar apenas os atributos que nós permitimos serem atualizados
* Altera método de insert de modelo de pneu para retornar o código gerado para o modelo inserido 

### Bug Fixes
* Corrige insert do modelo de banda para não inserir mais nomes em lower case
* Corrige get do atributo aro da dimensão do pneu para utilizar getDouble ao invés de getInt

<a name="v2.0.43-h1"></a>
## Version [v2.0.43-h1](https://github.com/luizfp/PrologWebService/compare/v2.0.43...v2.0.43-h1) (2017-01-14)

### Bug Fixes
* Corrige busca do relatório de indicadores acumulados

<a name="v2.0.43"></a>
## Version [v2.0.43](https://github.com/luizfp/PrologWebService/compare/v2.0.42...v2.0.43) (2017-01-14)

### Features
* Cria relatório para mostrar os indicadores acumulados

<a name="v2.0.42"></a>
## Version [v2.0.42](https://github.com/luizfp/PrologWebService/compare/v2.0.41...v2.0.42) (2017-01-12)

### Features
* Permite filtrar por ativo/inativo busca dos veículos de uma unidade
* Cria método para alterar o status de um veículo (ativo/inativo)
* Adiciona `codUnidadeAlocado` e `codRegionalAlocado` ao objeto `Pneu`
* Cria objetos e métodos base para criação das dashboards
* Cria relatório para estratificar os indicadores de cada mapa por colaborador

### Refactor
* Altera seguintes classes para utilizarem Injection: `AutenticacaoDaoImpl`, `AppDaoImpl`, `MetasDaoImpl`,
`ProdutividadeRelatorioDaoImpl`, `ProdutividadeDaoImpl`, `RelatorioEntregaDaoImpl`, `ChecklistModeloDaoImpl`,
`RelatoriosOrdemServicoDaoImpl`, `OrdemServicoDaoImpl`, `ChecklistRelatorioDaoImpl`, `MovimentacaoDaoImpl`,
`RelatorioPneuDaoImpl`, `ServicoDaoImpl`, `VeiculoDaoImpl`, `CalendarioDaoImpl`, `ContrachequeDaoImpl`
* Altera `ModeloBanda` para conter a altura dos sulcos
* Altera método de início de uma aferição para enviar o tipo da aferição sendo iniciada
* Cria novo método com path diferente para a busca das marcas de banda

### Bug Fixes
* Corrige mapeamento de perguntas do checklist para URL de imagens na integração com Avilan
* Corrige ordenamento da busca dos serviços abertos/fechados dos pneus
* Corrige criação do objeto `CronogramaAfericao` na integração com a Avilan

<a name="v2.0.41"></a>
## Version [v2.0.41](https://github.com/luizfp/PrologWebService/compare/v2.0.40...v2.0.41) (2017-12-22)

### Features
* Cria tabela para poder vincular imagens aos modelos de checklists da Avilan, buscados do Latromi.
* Cria métodos para busca dos serviços fechados dos pneus

### Refactor
* Altera seguintes classes para utilizarem Injection: `IndicadorDaoImpl`
* Refatora criação de serviços dos pneus

### Bug Fixes
* Corrige busca das placas dos veículos com base no tipo na integração com a Avilan
* Corrige query de relatório do checklist

<a name="v2.0.40"></a>
## Version [v2.0.40](https://github.com/luizfp/PrologWebService/compare/v2.0.39...v2.0.40) (2017-11-29)

### Refactor
* Na integração com a Avilan, ao enviar uma aferição realizada, envia medidas apenas de pneus que não sejam estepes
* Altera para function o método que busca a previsão de troca
* Altera para function o método que busca a aderência ao checklist
* Altera para function o método que gera o tempo médio de realização dos checklists por colaborador
* Altera processo de aferição para lidar com os três diferentes tipos de aferição: Sulco, Pressão e Sulco e Pressão

### Bug Fixes
* Corrige NPE ao inserir colaborador sem matrícula transportadora
* Corrige permissões do método de busca da produtividade
* Corrige período da busca por aferições
* Corrige método que busca uma aferição pelo seu código

<a name="v2.0.39"></a>
## Version [v2.0.39](https://github.com/luizfp/PrologWebService/compare/v2.0.38...v2.0.39) (2017-11-20)

### Features
* Implementa relatório que estratifica as respostas NOK dos checklists
* Implementa 3 novos relatórios para a parte de intervalos
* Insere DOT no pneu
* Cria busca de treinamento por seu código
* Cria update do status do colaborador
* Adiciona data de fechamento ao treinamento
* Cria relatório sobre os relatos

### Refactor
* Torna o período de calculo da produtividade dinâmico para cada unidade
* Altera para function o método que busca o consolidado das produtividades
* Transfere métodos auxiliares da ProdutividadeDaoImpl para a ContrachequeDaoImpl
* Altera os logs dos services visando tornar funcional o Sentry
* Faz paginação na busca das aferições na integração com Avilan
* Exclui veículos da Avilan do cronograma desde que não sejam aferíveis
* Ordena busca dos checklists na integração com Avilan por data
* Envia limit e offset na busca das aferições na integração com a Avilan
* Retorna quantidade de itens OK e NOK no checklist
* Retorna colaboradores inativos na busca por colaborador
* Torna período da produtividade dinâmico por unidade
* Insere atributo valor na tabela pneu

### Bug Fixes
* Corrige formatação da meta de raio do tracking
* Adiciona permissão de relatórios indicadores no getFiltros
* Corrige problema de import do tracking com csv possuindo linhas vazias
* Previne exception ao enviar CPF com traços ou pontos no upload do contracheque
* Corrige queries de busca dos relatos
* Corrige insert do modelo de quiz
* Corrige update de um Pneu

<a name="v2.0.38"></a>
## Version [v2.0.38](https://github.com/luizfp/PrologWebService/compare/v2.0.37...v2.0.38) (2017-10-25)

### Features
* Implementa relatório para estratificar os mapas
* Implementa function para listar as respsotas NOK dos checklists

### Refactors
* Refatora busca dos treinamentos de uma unidade
* Realiza busca dos checklists de um colaborador informando CPF na requisição na integração com Avilan
* Permite busca dos checklists de um colaborador ser filtrada por data
* Renomeia parâmetro no objeto `Treinamento` de `funcoesLiberadas` para `cargosLiberados`

### Bug Fixes
* Corrige métodos de update e busca das metas, salvando em km
* Adiciona permissão de visualizar todos os prontuários na busca dos filtros
* Corrige busca de todos os relatos de uma unidade

<a name="v2.0.37"></a>
## Version [v2.0.37](https://github.com/luizfp/PrologWebService/compare/v2.0.36...v2.0.37) (2017-10-19)

### Refactors
* Altera insert de aferição na integração com Avilan para enviar CPF do colaborador que realizou a aferição e data/hora
em que foi realizada, não apenas data
* Altera busca das aferições na integração com Avilan para conter colaborador que realizou e hora realização

<a name="v2.0.36"></a>
## Version [v2.0.36](https://github.com/luizfp/PrologWebService/compare/v0.0.35-h1...v2.0.36) (2017-10-19)

### Features
* Integra busca das aferições com Avilan
* Cria tabela PUBLIC.PNEU_POSICAO para salvar a posição dos pneus que o ProLog usa com suas descrições

### Refactors
* Altera maneira de buscar o diagrama do veículo para os veículos vindos da Avilan. Agora o veículo não precisa
mais estar cadastrado no ProLog para o diagrama estar disponível
* Altera path para buscar os cargos
* Remove do retorno da busca de cargos a permissão de cada cargo
* Altera métodos de busca dos checklists e aferições já realizadas

### Bug Fixes
* Corrige update/insert da versão dos dados do intervalo
* Corrige problema para aferir os bitrucks da Avilan

<a name="v0.0.35-h1"></a>
## Version [v0.0.35-h1](https://github.com/luizfp/PrologWebService/compare/v0.0.35...v0.0.35-h1) (2017-10-13)

### Bug Fixes
* Integra busca dos checklists por colaborador com Avilan

<a name="v0.0.35"></a>
## Version [v0.0.35](https://github.com/luizfp/PrologWebService/compare/v0.0.34-h1...v0.0.35) (2017-10-13)

### Features
* Implementa upadate de um tipo de intervalo
* Implementa inativate de um tipo de intervalo
* Integra busca dos checklists com Avilan
* Salva localização do usuário, se houver, no momento da marcação de intervalo

### Refactor
* Altera path de alguns métodos de update/inativade tipo de intervalo

#### Deprecated
* Resource da Aferição com path `afericao`. Novo resource agora possui path `afericoes`

<a name="v0.0.34-h1"></a>
## Version [v0.0.34-h1](https://github.com/luizfp/PrologWebService/compare/v0.0.34...v0.0.34-h1) (2017-10-06)

### Bug Fixes
* Corrige path da busca do farol do checklist na integração com AvaCorp

<a name="v0.0.34"></a>
## Version [v0.0.34](https://github.com/luizfp/PrologWebService/compare/v0.0.33...v0.0.34) (2017-10-06)

### Features
* Integra função de farol do checklist com AvaCorp

### Refactor
* Melhora método de inser de movimentação
* Realiza merge do branch que corrige problema de permissões do ProLog
* Melhora funcionalidade de farol

### Bug Fixes
* Corrige erro ao deletar permissões de um cargo
* Corrige insert de movimentação da análise para o estoque

<a name="v0.0.33"></a>
## Version [v0.0.33](https://github.com/luizfp/PrologWebService/compare/v0.0.32...v0.0.33) (2017-09-27)

### Features
* Implementa busca por um modelo de quiz completo

### Refactor
* Retorna código do `FaleConosco` inserido no método de insert
* Realiza refatorações na busca do `IntervaloOfflineSupport` para retornar informações de cargo junto dos tipos de 
intervalo

### Bug Fixes
* Corrige erro nas queries que utilizam a func to_seconds

<a name="v0.0.32"></a>
## Version [v0.0.32](https://github.com/luizfp/PrologWebService/compare/v0.0.31...v0.0.32) (2017-09-18)

### Refactor
* Cria métodos necessários para dar suporte a realização de intervalo offline no aplicativo

### Features
* Cria métodos para tornar funcional a nova tela de farol do checklist
* Adiciona atributos no checklist para carregar a quantidade de itens OK/NOK
* Adiciona métodos para buscar os checklists completos ou resumidos

<a name="v0.0.31"></a>
## Version [v0.0.31](https://github.com/luizfp/PrologWebService/compare/v0.0.30...v0.0.31) (2017-09-14)

#### Refactor
* Cria novo resource para o checklist, pluralizado -> `checklists`

<a name="v0.0.30"></a>
## Version [v0.0.30](https://github.com/luizfp/PrologWebService/compare/v0.0.29...v0.0.30) (2017-09-06)

#### Bug Fixes
* Corrige calculo do tempo decorrido de um intervalo

<a name="v0.0.29"></a>
## Version [v0.0.29](https://github.com/luizfp/PrologWebService/compare/v0.0.28...v0.0.29) (2017-09-05)

#### Bug Fixes
* Corrige comparação de códigos ao finalizar um intervalo

<a name="v0.0.28"></a>
## Version [v0.0.28](https://github.com/luizfp/PrologWebService/compare/v0.0.27...v0.0.28) (2017-08-31)

### Features
* Implementa relatório com o extrato de intervalos realizados

#### Bug Fixes
* Corrige tempo de largada de alguns mapas com HRMATINAL igual a zero

<a name="v0.0.27"></a>
## Version [v0.0.27](https://github.com/luizfp/PrologWebService/compare/v0.0.26...v0.0.27) (2017-08-23)

### Features
* Implementa relatório com o resumo dos checklists realizados (csv/report)

#### Refactor

* Comenta método que retorna os intervalos de um colaborador

<a name="v0.0.26"></a>
## Version [v0.0.26](https://github.com/luizfp/PrologWebService/compare/v0.0.25...v0.0.26) (2017-08-21)

### Features
* Adiciona colunas referente a Jornada no relatório consolidado da produtividade
* Implementa controle de intervalos

#### Refactor

<a name="v0.0.25"></a>
## Version [v0.0.25](https://github.com/luizfp/PrologWebService/compare/v0.0.24...v0.0.25) (2017-08-16)

### Features
* Implementa relatório de estratificação dos serviços fechados (pneus)
* Implementa integração com AvaCorp(Avilan)

#### Refactor
* Altera o código do pneu de BIGINT para VARCHAR em todas as tabelas

<a name="v0.0.24"></a>
## Version [v0.0.24](https://github.com/luizfp/PrologWebService/compare/v0.0.23...v0.0.24) (2017-08-08)

### Features
* Implementa método para buscar apenas os motoristas e ajudantes de uma unidade

#### Refactor
* Altera query de busca do relatório de realização do Quiz por cargo, agora usa function

<a name="v0.0.23"></a>
## Version [v0.0.23](https://github.com/luizfp/PrologWebService/compare/v0.0.22...v0.0.23) (2017-08-03)

#### Refactor
* Corrige erro de parse em possível coluna vazia no campo "pontuação CNH" do prontuário
* Adiciona informações de Situação e CNH no resumo dos prontuários
* Altera busca do objeto `NovaAfericao` para retornar estepes do veículo em uma listagem separada
* Refatora algumas DAOs para não retornar mais boolean e em caso de erro lançar uma exceção

#### Bug Fixes
* Corrige fechamento de movimentação na `ServicoDaoImpl`
* Corrige import de contracheque com linhas em branco

<a name="v0.0.22"></a>
## Version [v0.0.22](https://github.com/luizfp/PrologWebService/compare/v0.0.21...v0.0.22) (2017-07-20)

#### Features

* Adiciona novas colunas no relatório de consolidado produtividade e troca para usar uma function

#### Refactor

* Altera forma de exibir a hora nos relatórios do quiz, de 12h para 24h
* Altera forma de exibir a hora nos relatórios de OS, de 12h para 24h

#### Bug Fixes
* Corrige o erro no filtro de datas dos relatórios do Quiz

<a name="v0.0.21"></a>
## Version [v0.0.21](https://github.com/luizfp/PrologWebService/compare/v0.0.20...v0.0.21) (2017-07-17)

#### Features
* Adicionadas colunas para estratificar a remuneração variável nos dois relatórios disponíveis.


#### Bug Fixes
* Corrigido erro que setava todos os excessos de velocidade como "excesso de veolocidade 1"

<a name="v0.0.20"></a>
## Version [v0.0.20](https://github.com/luizfp/PrologWebService/compare/v0.0.19...v0.0.20) (2017-07-12)


#### Refactor
* Corrige upload do prontuário

<a name="v0.0.19"></a>
## Version [v0.0.19](https://github.com/luizfp/PrologWebService/compare/v0.0.18...v0.0.19) (2017-07-07)

#### Features
* Cria tabelas para o prontuário do condutor
* Implementa dao para o prontuário

#### Refactor
* Remove coluna "cumpriu prazo" do relatório estratificação de OS

<a name="v0.0.15"></a>
## Version [v0.0.15](https://github.com/luizfp/PrologWebService/compare/v0.0.14...v0.0.15) (2017-06-19)

#### Features
* Cria um novo sistema de associar `diagramas` aos veículos
    * Criada tabela veiculo_diagrama para armazenar os tipos de diagramas que o Prolog suporta
    * Criada tabela veiculo_diagrama_eixos para detalhar cada tipo de diagrama
    * Adicionada a coluna cod_diagrama na tabela veiculo_tipo, bem como a devida constraint
    
* Implementa `delete ` de um `treinamento` (resta deletar do S3 os arquivos)
* Implementa tabela (pneu_ordem_nomenclatura_unidade) para armazenar e linkar as posições de pneus do Prolog com as devidas nomenclaturas utilizadas pelos clientes.
* Agora pneus podem ter o quarto sulco
    * Adição de coluna qt_sulcos na tabela modelo_pneu
    * Adição de coluna qt_sulcos na tabela modelo_banda
    * Renomeadas as colunas dos sulcos das tabelas pneu, aferição valores e movimentação
    * Adicionada coluna para o quarto sulco na tabela pneu, aferição valores e movimentação
* Altera classe Pneu e classe Banda, tornando distinto o modelo de cada um deles, além de adicionar a quantidade de sulcos
* Cria método para inserir um novo cargo (função)

#### Refactor
* Refatorado o calculo do pré contracheque, agora o calculo do bônus é dinâmico de acordo com o indicador informado, 
a recarga pode ou não fazer parte do calculo do prêmio, além de algumas adaptações no BD:
    * Renomeada a tabela pre_contracheque_premissas -> pre_contracheque_informacoes
    * Removidas as colunas cod_import_he / cod_import_dsr / cod_import_vales
    * Adicionada coluna recarga_parte_premio
    * Adicionada constraint com Unidade
    * Renomeadas as constraints
    * Criada a tabela pre_contracheque_calculo_premio para armazenar os códigos dos itens que compõe o calculo do prêmio
    

#### Bug Fixes
* Corrige erro ao importar tabela mapa com linhas vazias no final do arquivo
* Corrige falta de atualização do KM ao salvar uma movimentação 

<a name="v0.0.14"></a>
## Version [v0.0.14](https://github.com/luizfp/PrologWebService/compare/v0.0.14...v0.0.13-h2) (2017-05-17)

#### Features
* Novo método update de um `Treinamento` contendo `codigo` no path
* Novos métodos para buscar os treinamentos de um colaborador
* Objeto `Unidade` agora possui uma lista de objetos `Equipe` que são retornadas na busca pelos filtros
* Novos métodos da solicitação de folga agora precedidos pelo path `solicitacoes-folga`

#### Refactor
* Funções liberadas para ver um `Treinamento` são atualizadas no update do `Treinamento`
* Cálculo da `Produtividade` alterado. Agora está sendo pago jornada para todo mapa que tem o tempo previsto maior que 
9h20
* Altera path da busca de todos os fale conosco de `/fale-conosco/{codUnidade}/{equipe}/{cpf}` para 
`/fale-conosco/{codUnidade}/{nomeEquipe}/{cpf}`

#### Deprecated
* Método update do `Treinamento` sem `codigo` no path
* Métodos de buscar os treinamentos de um colaborador que possuiam os paths: `treinamentos/naoVistosColaborador/{cpf}`
e `treinamentos/vistosColaborador/{cpf}`
* Todos os métodos da solicitação de folga que possuiam o path `solicitacaoFolga`
* Todos os métodos do fale conosco que possuiam o path `faleConosco`

#### Bug Fixes
* Update do `Treinamento` retornava `true` caso o `Treinamento` não tivesse sido atualizado
* Update do `ModeloQuiz` retornava `true` caso o `ModeloQuiz` não tivesse sido atualizado

<a name="v0.0.12"></a>
## Version [v0.0.12](https://github.com/luizfp/PrologWebService/compare/v0.0.11...v0.0.12) (2017-05-08)

#### Features
* Novo método para buscar todos os Fale Conosco, agora é possível informar o cpf de quem queremos buscar ou buscar 
de todos os CPFs
* Novo relatório dos pneus, retorna os resumo da última aferição de cada pneu, além de a placa e posição do mesmo
* Novo relatório para estratificar os fale conosco
* Novo relatório para estratificar as folgas concedidas

#### Refactor
* Removida a coluna cod_unidade das buscas por um treinamento
* Alterada a forma de remuneração do AS
    * Add colunas para mapas com 3 entregas
    * Add colunas para mapas com >3 entregas
    * Remove colunas para mapas com >2 entregas 

#### Deprecated
* Método antigo de buscar todos os Fale Conosco que não recebe o CPF como parâmetro