Change Log
==========

<a name="v0.0.15"></a>
## Version [v0.0.15](https://github.com/luizfp/PrologWebService/compare/v0.0.14...v0.0.15) (release date) - [Unreleased]

#### Features

#### Refactor
* Refatorado o calculo do pré contracheque, agora o calculo do bônus é dinâmico de acordo com o indicador informado, 
a recarga pode ou não fazer parte do calculo do prêmio, além de algumas adaptações no BD:
    * Renomeada a tabela pre_contracheque_premissas -> pre_contracheque_informacoes
    * Removidas as colunas cod_import_he / cod_import_dsr / cod_import_vales
    * Adicionada coluna recarga_parte_premio
    * Adicionada contraint com Unidade
    * Renomeadas as contraints
    * Criada a tabela pre_contracheque_calculo_premio para armazenar os códigos dos itens que compõe o calculo do prêmio
    

#### Deprecated

#### Bug Fixes

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