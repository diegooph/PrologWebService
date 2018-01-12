Change Log
==========

<a name="v2.0.42"></a>
## Version [v2.0.42](https://github.com/luizfp/PrologAndroid/compare/v2.0.41...v2.0.42) (release-date) - [unreleased]

### Features
* Permite filtrar por ativo/inativo busca dos veículos de uma unidade
* Cria método para alterar o status de um veículo (ativo/inativo)
* Adiciona `codUnidadeAlocado` e `codRegionalAlocado` ao objeto `Pneu`
* Cria objetos e métodos base para criação das dashboards

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
## Version [v2.0.41](https://github.com/luizfp/PrologAndroid/compare/v2.0.40...v2.0.41) (2017-12-22)

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
## Version [v2.0.40](https://github.com/luizfp/PrologAndroid/compare/v2.0.39...v2.0.40) (2017-11-29)

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
## Version [v2.0.39](https://github.com/luizfp/PrologAndroid/compare/v2.0.38...v2.0.39) (2017-11-20)

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
## Version [v2.0.38](https://github.com/luizfp/PrologAndroid/compare/v2.0.37...v2.0.38) (2017-10-25)

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
## Version [v2.0.37](https://github.com/luizfp/PrologAndroid/compare/v2.0.36...v2.0.37) (2017-10-19)

### Refactors
* Altera insert de aferição na integração com Avilan para enviar CPF do colaborador que realizou a aferição e data/hora
em que foi realizada, não apenas data
* Altera busca das aferições na integração com Avilan para conter colaborador que realizou e hora realização

<a name="v2.0.36"></a>
## Version [v2.0.36](https://github.com/luizfp/PrologAndroid/compare/v0.0.35-h1...v2.0.36) (2017-10-19)

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
## Version [v0.0.35-h1](https://github.com/luizfp/PrologAndroid/compare/v0.0.35...v0.0.35-h1) (2017-10-13)

### Bug Fixes
* Integra busca dos checklists por colaborador com Avilan

<a name="v0.0.35"></a>
## Version [v0.0.35](https://github.com/luizfp/PrologAndroid/compare/v0.0.34-h1...v0.0.35) (2017-10-13)

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
## Version [v0.0.34-h1](https://github.com/luizfp/PrologAndroid/compare/v0.0.34...v0.0.34-h1) (2017-10-06)

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