Change Log
==========

<a name="v0.0.30"></a>
## Version [v0.0.30](https://github.com/luizfp/PrologWebService/compare/v0.0.29...v0.0.30) (release-date) [unreleased]

### Features

#### Refactor

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

#### Refactor

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