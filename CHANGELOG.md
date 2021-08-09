
Change Log
==========

##
## UNRELEASED
##

### Refactors
* Melhora estrutura de autenticação do Prolog (PL-3900)


<a name="v3.7.0"></a>
## Version [v3.7.0](https://github.com/luizfp/PrologWebService/compare/v3.6.0...v3.7.0) (2021-08-04)

### Features
* Cria método de busca de empresa para uso no intralog
* Cria método de alteração de empresa para uso no intralog

### Refactors
* Salva metadados sobre alteração de empresa e retorna isso nas buscas
* Deleta resources/services/daos/testes depreciados do controle de jornada
* Deleta métodos antigos da estrutura de autenticação

### Bug Fixes
* Corrige nome do bucket para upload de logo de empresas

<a name="v3.6.0"></a>
## Version [v3.6.0](https://github.com/luizfp/PrologWebService/compare/v3.5.5...v3.6.0) (2021-08-03)

# Features
* Cria método de cadastro de empresa para uso no intralog
* Cria método para buscar empresas para uso no intralog
* Cria método para editar logo da empresa para uso no intralog

### Refactors
* Change code in V3 to EN except the DTOs (PL-3875)

### Bug Fixes
* Corrige erro no mapeamento de PSI em serviços fechados (PL-3866)
* Corrige o relatório 'Placas com pneus abaixo do limite' para não exibir placas inativas (PS-3610)

<a name="v3.5.5"></a>
## Version [v3.5.5](https://github.com/luizfp/PrologWebService/compare/v3.5.4...v3.5.5) (2021-07-19)

# Features
* Adiciona código e nome das funcionalidades do dash para agrupamento (PL-3876)

### Refactors
* Remove trava de empresa na propagação de km (PL-3699)
* Mapeia corretamente erros do Spring nos endpoints V3
* Remove CPF de relato (PL-3856)

### Bug Fixes
* Corrige erro de getConstraint ao receber valor nulo  
* Corrige erro ao editar modelo de checklist (PL-3528)
* Corrige erro ao movimentar pneus em placas com cadastro duplicado em outra empresa (PS-1606)

<a name="v3.5.4"></a>
## Version [v3.5.4](https://github.com/luizfp/PrologWebService/compare/v3.5.3...v3.5.4) (2021-07-11)

### Bug Fixes
* Corrige fechamento de serviço de pneu

<a name="v3.5.3"></a>
## Version [v3.5.3](https://github.com/luizfp/PrologWebService/compare/v3.5.2...v3.5.3) (2021-07-08)


### Bug Fixes
* Corrige processo de movimentação para buscar informações da placa correta
* Corrige processo de aferição para buscar configurações da placa correta
* Corrigir busca e avaliação de solicitação de folga
* Corrige mapeamento de erros de banco do V2
* Deixa de logar exception no sentry para mensagem de token não autorizado
* Trata mensagem de erro em métodos roteados (integrados) com AOP

<a name="v3.5.2"></a>
## Version [v3.5.2](https://github.com/luizfp/PrologWebService/compare/v3.5.1...v3.5.2) (2021-07-05)

### Refactors
* Centraliza mapeamentos das exceptions (PL-3700)
* Adiciona permissão de aferição placa na busca de campos personalizados

### Bug Fixes
* Corrigir uso de PageRequest na API v3 (PL-3704)
* Corrige mapeamento de erros de banco do V2

<a name="v3.5.1"></a>
## Version [v3.5.1](https://github.com/luizfp/PrologWebService/compare/v3.5.0...v3.5.1) (2021-06-29)

### Refactors
* Listagem de unidades retorna vazio quando não passa codGrupo (PL-3692)
* Retorna vazio na busca de campos personalizados na integração - Nepomuceno
* Corrige listagem de relatos de outros setores (PL-3709)

<a name="v3.5.0"></a>
## Version [v3.5.0](https://github.com/luizfp/PrologWebService/compare/v3.4.0...v3.5.0) (2021-06-21)

### Features
* Cria endpoint de listagem de veículos na API (PL-3670)
* Cria DTO para a listagem de veículos na API (PL-3672)
* Cria busca de campos personalizados para Aferição (PL-2909)
* Implementa integração de Inspeção de Removidos para Nepomuceno (PL-3673)
* Criar método para atualizar o KM atual do veículo na API v3 (PL-3678)
* Criar método para atualizar o status atual do pneu na API v3 (PL-3679)

### Refactors
* Permite apenas valores positivos na configuração de Aferição (PL-3497)
* Atualiza estrutura de solicitação de folga para não utilizar CPF (PL-3470)
* Refatora aferições para possibilitar recebimento de resposta de campos personalizados (PL-2910)

<a name="v3.4.0"></a>
## Version [v3.4.0](https://github.com/luizfp/PrologWebService/compare/v3.3.7...v3.4.0) (2021-06-07)

### Features
* Cria nova estrutura de roteamento (PL-3623)
* Implementa nova estrutura de roteamento para o insert de aferição (PL-3623)
* Adiciona endpoint de serviço de pneus na API (PL-3645)
* Cria endpoint para listagem de pneus na API (PL-3646)

### Refactors
* Melhora busca de indicadores consolidados e do extrato dos mapas (PL-3613)
* Altera assinatura e otimiza relatórios de previsão de troca (PL-3649)

### Bug Fixes
* Corrige criação do trator no objeto de dados de coleta de km (PL-3677)

<a name="v3.3.7"></a>
## Version [v3.3.7](https://github.com/luizfp/PrologWebService/compare/v3.3.6...v3.3.7) (2021-05-27)

### Refactors
* Corrige estrutura de testes (PL-3643)

### Bug Fixes
* Corrige agrupamento dos itens das Ordens de Serviço de checklist

<a name="v3.3.6"></a>
## Version [v3.3.6](https://github.com/luizfp/PrologWebService/compare/v3.3.5...v3.3.6) (2021-05-22)

### Refactors
* Otimiza busca dos indicadores individuais
* Adiciona @Max no Limit de busca de dados dos endpoints da Api.

### Bug Fixes
* Corrige upload de planilha de tracking
* Corrige falha ao movimentar pneus na integração com a finatto

<a name="v3.3.5"></a>
## Version [v3.3.5](https://github.com/luizfp/PrologWebService/compare/v3.3.4...v3.3.5) (2021-05-20)

### Refactors
* Melhora mapeamento de exceptions do SQL
* Otimiza geração da produtividade (PL-3648)

<a name="v3.3.4"></a>
## Version [v3.3.4](https://github.com/luizfp/PrologWebService/compare/v3.3.3...v3.3.4) (2021-05-17)

### Features
* Cria function para o suporte consultar atualizações num pneu (PL-3490)

### Refactors
* Atualiza insert/update de pneu para logar o colaborador que inseriu/alterou um pneu (PL-3490)
* Alterar relatórios e dashboards que utilizam a coluna placa da tabela veiculo_pneu (PL-3617)
* Remover usos da coluna placa da tabela veiculo_pneu nos fluxos de sistema (WS e BD) (PL-3618)
* Altera limite máximo permitido na listagem de checklists

### Bug Fixes
* Erro ao editar diagrama de veiculo (PL-3637)

<a name="v3.3.3"></a>
## Version [v3.3.3](https://github.com/luizfp/PrologWebService/compare/v3.3.2...v3.3.3) (2021-05-10)

### Refactors
* Adiciona validação para erros mapeados pelo `SpringDataJpa` (PL-3605)

### Bug Fixes
* Corrige problema de limpeza de arquivos/pastas temporárias

<a name="v3.3.2"></a>
## Version [v3.3.2](https://github.com/luizfp/PrologWebService/compare/v3.3.1...v3.3.2) (2021-05-09)

### Bug Fixes
* Corrige linha duplicada histórico de veículos (PL-3516)
* Corrige atualização km ao realizar movimentação (PL-3630)

### Refactors
* Remove dependências e configurações não utilizadas do swagger
* Padroniza documentação da API (PL-3621)
* Melhora scheduler de limpeza de arquivos (PL-3436)

<a name="v3.3.1"></a>
## Version [v3.3.1](https://github.com/luizfp/PrologWebService/compare/v3.3.0...v3.3.1) (2021-05-04)

### Bug Fixes
* Corrige edição de KM de processo de movimentação
* Corrige KM coletado enviado na listagem de movimentações da API v3
* Corrige problema ao gerar cronograma de aferição para a Finatto

<a name="v3.3.0"></a>
## Version [v3.3.0](https://github.com/luizfp/PrologWebService/compare/v3.2.0...v3.3.0) (2021-05-02)

### Features
* Cria anotações para garantir os códigos acessados pelas requisições (PL-3551)
* Cria functions de suporte para alterações em modelo de quiz (PL-3609)
* Logar no BD quem altera um colaborador (PL-3491)
* Cria v3 de listagem de checklist (PL-3461)
* Cria v3 de ordem de serviço de checklist (PL-3462)
* Cria v3 de listagem de movimentações (PL-3602)

### Refactors
* Altera paths de v3 para serem precedidos por `api`
* Altera path de unidades para estarem no `v3` e serem precedidos por `api`
* Altera calculo de vencidas da function de geraçao dos dashboards e do relatorio de cronograma (PL-3538)
* Realiza pequenas melhorias na PrologApplication (PL-3606)
* Adiciona valores de medidas na API de aferição (PL-3570)
* Altera relatório de exportação de Aferições para o Protheus (PL-3610)
* Roteia método de busca de KM atual para a integração da Nepomuceno (PL-3508)

### Bug Fixes
* Corrige function de transferência de veículo entre Empresas para o Suporte (PS-1504)
* Ajusta erros logados no sentry do package frota (PL-3593)
* Corrige regex de validação de commit message (PL-3588)

<a name="v3.2.0"></a>
## Version [v3.2.0](https://github.com/luizfp/PrologWebService/compare/v3.1.0...v3.2.0) (2021-04-18)

### Refactors
* Deleta integração com a Rodalog (PL-3476)
* Altera ServicoResource para utilizar código do veículo nos métodos e não placa (PL-3406)
* Altera métodos do ServicoResource que recebem objeto de filtro de GET para POST
* Adicionar código do veículo no retorno das buscas de serviço (PL-3524)
* Remove placa de todo o fluxo de aferições (PL-3403)
* Adiciona código de veículo no cronograma de aferição (PL-3496)
* Remove placa na realização de checklist (PL-3545)
* Remove placa da busca de relatórios de checklist (PL-3548)
* Remove placa das buscas do checklist (PL-3546)
* Modifica fechamento massivo de OS via Suporte (PL-3594)
* Remove placa das buscas, abertura e fechamento de OSs (PL-3547)
* Remove placa dos relatórios de OSs (PL-3549)
* Altera integrações para buscar a placa da tabela veículo (PL-3595)

### Bug Fixes
* Altera para não retornar unidades inativas (PL-3510)
* Corrige processamento de planilha de import de pneus
* Corrige processamento de planilha de import de vínculo de pneus a veículos
* Corrige valores retornados para exibição de sulco atual e pressão atual (PL-3578)

<a name="v3.1.0"></a>
## Version [v3.1.0](https://github.com/luizfp/PrologWebService/compare/v3.0.0...v3.1.0) (2021-04-08)

### Features
* Adiciona código do colaborador ao Sentry para ser logado junto em caso de erro
* Cria funcionalidade para alterar o km coletado nos processos (PL-3557)
* Migra todos os SQLs do repositório de BD para o de WS

### Refactors
* Configura swagger para rodar em prod (PL-3559)
* Altera versão do Sentry para 4.3.0
* Altera para o Sentry enviar informações sensitivas por default, como o token

### Bug Fixes
* Corrige sincroniza de O.S. na integração da Piccolotur (PL-3599)
* Corrige checkstyle para detecção de nome de pacote
* Corrige trigger de import de veículos 
* Corrige processamento de planilha de import de veículos
* Corrige processamento de planilha de import de colaboradores

<a name="v3.0.0"></a>
## Version [v3.0.0](https://github.com/luizfp/PrologWebService/compare/v2.2.0...v3.0.0) (2021-03-30)

### Features
* Migra projeto do Java 8 para o 11 (PL-3475)
* Finaliza cadastro de pneus na API v3 (PL-3459)
* Finaliza cadastro de veículos na API v3 (PL-3460)

### Refactors
* Deleta integração antiga da Avilan que era em XML (PL-3475)
* Altera integração com Globus/Piccolotur de envio de OS para utilizar implementação própria do SOAP (PL-3475)
* Adiciona mais uma opção no enum de evolução de km (PL-3562)

<a name="v2.2.0"></a>
## Version [v2.2.0](https://github.com/luizfp/PrologWebService/compare/v2.1.0...v2.2.0) (2021-03-21)

### Features
* Cria versão inicial de endpoint de cadastro de pneus na API v3 (PL-3459)
* Cria versão inicial de endpoint de cadastro de veículos na API v3 (PL-3460)

### Refactors
* Remove classes depreciadas e não utilizadas de O.S. de checklist
* Ordena busca de tipos de veículo por nome
* Alterar buscas de serviços fechados para considerar flag de fechado por aferição (PL-3561)
* Alterar processo de inserção de aferição para fechar serviços automaticamente (PL-3541)

### Bug Fixes
* Corrige listagem de relatos realizados (PL-3572)
* Corrige leitura dos headers de Logs de integração (PL-3519)
* Corrige busca do relatório de aferições integrado (PL-3568)
* Corrige permissões ao listar veículos
* Corrige filtro de unidades/clientes vindas da integração (PL-3560)

<a name="v2.1.0"></a>
## Version [v2.1.0](https://github.com/luizfp/PrologWebService/compare/v2.0.2...v2.1.0) (2021-03-07)

### Features
* Adiciona estrutura de testes e2e com testcontainers e Flyway (PL-3307)
* Cria integração com sistema WebFinatto (PL-2874)

### Refactors
* Ordena lista de histórico de acoplamentos por data/hora

### Bug Fixes
* Corrige path de resource de histórico de acoplamentos

<a name="v2.0.2"></a>
## Version [v2.0.2](https://github.com/luizfp/PrologWebService/compare/v2.0.1...v2.0.2) (2021-02-22)

### Refactors
* Altera root path do projeto removendo o v2 e colocando em cada Resource (PL-3500)
* Altera pesquisa de NPS para salvar origem da resposta e do bloqueio

<a name="v2.0.1"></a>
## Version [v2.0.1](https://github.com/luizfp/PrologWebService/compare/v2.0.0...v2.0.1) (2021-02-16)

### Bug Fixes
* Adiciona código veículo no fluxo da integração (PL-3521)

<a name="v2.0.0"></a>
## Version [v2.0.0](https://github.com/luizfp/PrologWebService/compare/v1.6.0...v2.0.0) (2021-02-14)

#### Features
* Cria funcionalidade de insert de processo de acoplamento (PL-3210)
* Cria funcionalidade de verificação de dados de coleta de km (PL-3291)
* Cria update único de KM de veiculo por código (PL-3213)
* Corrige bug em propagação de km ao realizar aferição (PL-3439)

#### Refactors
* Adiciona flag "motorizado" no tipo de veículo (PL-3387).
* Realiza modificação mo CRUD de veiculos, incluindo os campos ```motorizado``` e ```possuiHubodometro``` (PL-3223)
* Adiciona os veículos acoplados no objeto de `VeiculoVisualizacao` (PL-3212)
* Adiciona os veículos acoplados no objeto de `VeiculoListagem` (PL-3211)
* Adiciona propagação de km no fechamento de OS (PL-3335)
* Adiciona novas ações no insert do histórico de acoplamento (PL-3344)
* Adiciona novas validações no import massivo de veículos (PL-3288)
* Realiza ajustes no acoplamento para funcionar apenas com MUDOU_POSICAO (PL-3355)
* Refatora updates de km dos processos para usarem function centralizadora (PL-3290)
* Adiciona erro especifico ao tentar por hubodômetro em veículos motorizados (PL-3386)
* Adiciona validação para impedir que veículos acoplados sejam inativados (PL-3397)
* Adiciona código de posição e informação de se o veículo é motorizado para uso no front (PL-3498)
* Adiciona código veiculo nos objetos de O.S (PL-3472)

### Bug Fixes
* Corrige diferenças entre branches na edição de veículo (PL-3326)

<a name="v1.6.0"></a>
## Version [v1.6.0](https://github.com/luizfp/PrologWebService/compare/v1.5.4...v1.6.0) (2021-02-09)

### Features
* Cria método para verificar se uma marcação está finalizada (PL-3191)

#### Refactors
* Remove classe EnvironmentHelper (PL-3414)
* Altera API de e-mail para utilizar gerenciamento pelo Spring (PL-3414)
* Altera Firebase para utilizar gerenciamento pelo Spring (PL-3414)
* Considera bônus no pré-contracheque apenas se número de viagens for acima ou igual o parametrizado (PL-3368)

### Bug Fixes
* Corrige uso de autoCommit no salvamento de imagens do checklist

<a name="v1.5.4"></a>
## Version [v1.5.4](https://github.com/luizfp/PrologWebService/compare/v1.5.3...v1.5.4) (2021-01-24)

### Refactors
* Altera estrutura de unidades para utilizar projections do Spring
* Altera nome atributo de objeto de sucesso do retorno do descarte
* Adiciona informação de valor e CPK acumulados no relatório de KM rodado por pneu e vida (PL-3262)

### Bug Fixes
* Corrige salvamento de logs de integração
* Corrige uso de URL na integração com a Horizonte
* Corrige update de unidade
* Corrige testes do relatório de pneu por coluna

<a name="v1.5.3"></a>
## Version [v1.5.3](https://github.com/luizfp/PrologWebService/compare/v1.5.2...v1.5.3) (2021-01-23)

### Refactors
* Adapta integração com Visual Rodopar para funcionar na Imediato (PL-3430)
* Refatorações e reestruturações dos utils

### Bug Fixes
* Corrige uso de autoCommit em conexões com o Banco

<a name="v1.5.2"></a>
## Version [v1.5.2](https://github.com/luizfp/PrologWebService/compare/v1.5.1...v1.5.2) (2021-01-21)

### Refactors
* Melhora forma de resetar uma empresa de apresentação

### Bug Fixes
* Corrige marshaller de XML na integração com a Praxio (PL-3429)

<a name="v1.5.1"></a>
## Version [v1.5.1](https://github.com/luizfp/PrologWebService/compare/v1.5.0...v1.5.1) (2021-01-19)

### Refactors
* Altera forma de lidar com o sentry para utilizar integração com Spring Boot
* Altera forma de gerar o banner do Prolog no start da aplicação

### Bug Fixes
* Corrige criação de objetos de response do retorno de pneu de descarte

<a name="v1.5.0"></a>
## Version [v1.5.0](https://github.com/luizfp/PrologWebService/compare/v1.4.2...v1.5.0) (2021-01-18)

### Features
* Altera integração da Nepomuceno para trabalhar com código ao invés de placa (PL-3366)
* Adiciona Spring como gerenciador de conexões, não utilizando mais a classe `DatabaseManager`, configurando através de
  YAML (PL-3265)
* Adiciona `Hikari` como gerenciador de pool de conexões (PL-3266)
* Adiciona `Spring Data JPA` para realizar o mapeamento e CRUD de Unidade (PL-3309)
* Adiciona scheduler para deleção dos arquivos temporários do tomcat (PL-3318)
* Implementa bloqueios de placa para Nepomuceno (PL-3351)
* Adiciona deleção das checklists (PL-3217)
* Corrige busca de histórico de edições (PL-3204)

### Refactors
* Adiciona código de veiculo nas movimentações de origem e destino (PL-3321)
* Adiciona o token_integração em todas as requisições integradas (PL-3251)
* Refatora objeto de resposta do retorno de pneu de descarte (PL-3371)
* Adiciona ordenação do relatório de remuneração acumulada com nome do colaborador e cpf (PL-3137)
* Remove javadocs das DAOs (PL-3400)
* Remove dependência de pooling do tomcat
* Refactor altera movimentações para utilizarem código do veículo ao invés da placa (PL-3301)

### Bug Fixes
* Relatório qtd aferições by tipo e data não traz primeira linha (PL-3188)
* Realiza validação mapeamento de exception no internal mapper com sentry (PL-3280)

<a name="v1.4.2"></a>

## Version [v1.4.2](https://github.com/luizfp/PrologWebService/compare/v1.4.1...v1.4.2) (2020-11-22)

### Features
* Implementa documentação da classe de unidade (PL-2898)
* Configura FileWatcher no Prolog (PL-3267)

### Bug Fixes
* Corrige uso de data hora de queries que geram histórico de edição de veículo (PL-3264)
* Corrige envio de aferição de placas mercosul na integração da Nepomuceno (PL-3392)

### Refactors
* Revisa e excluí testes unitários sem sentido (PL-3220)

<a name="v1.4.1"></a>
## Version [v1.4.1](https://github.com/luizfp/PrologWebService/compare/v1.4.0...v1.4.1) (2020-11-08)

### Refactors
* Adapta integração da Avilan enviando novas informações (PL-3283)

### Bug Fixes
* Corrige parâmetro de data no relatório de indicadores (PS-1315)

<a name="v1.4.0"></a>
## Version [v1.4.0](https://github.com/luizfp/PrologWebService/compare/v1.3.3...v1.4.0) (2020-11-03)

### Features
* Melhora sistema de Logs (PL-2939)
* Cria componente na Dash - Farol Checklist (PL-2791)
* Cria deleção de checklists (PL-3217)

### Refactors
* Adiciona log nos métodos de insert de intervalo

### Bug Fixes
* Corrige log da integração que logava corpo de erro em sucesso e vice-versa (PL-3193)

<a name="v1.3.3"></a>
## Version [v1.3.3](https://github.com/luizfp/PrologWebService/compare/v1.3.2...v1.3.3) (2020-10-25)

#### Bug Fixes
* Corrige estrutura do json para exportação de aferições do Protheus (PL-3237)

<a name="v1.3.2"></a>
## Version [v1.3.2](https://github.com/luizfp/PrologWebService/compare/v1.3.1...v1.3.2) (2020-10-22)

### Refactor
* Altera o envio da aferição para o Protheus - Nepomuceno (PL-3233)

### Bug Fixes
* Corrige recebimento das respostas do Latromi na integração da Avilan (PL-3232)

<a name="v1.3.1"></a>
## Version [v1.3.1](https://github.com/luizfp/PrologWebService/compare/v1.3.0...v1.3.1) (2020-10-18)

### Features
* Implementa swagger (PL-3158)
* Permite a exportação de aferições no padrão Protheus para importação no sistema terceiro (PL-3182)

### Refactor
* Remove codMarcacaoPorUnidade do IntervaloMarcacao (PL-3199)
* Adapta sistema de autenticação para validar endpoints expostos para integração (PL-2937)
* Melhora estrutura de tratamento de erros, preparando para exposição da api interna (PL-2938)
* Melhora relatório de indicadores acumulados (PL-3199)

### Bug Fixes
* Ajusta tipos dos atributos nos objetos de retorno pra Avilan (PL-3205)

<a name="v1.3.0"></a>
## Version [v1.3.0](https://github.com/luizfp/PrologWebService/compare/v1.2.1...v1.3.0) (2020-10-13)

### Features
* Adiciona colunas no relatório de pneus descartados (PL-3145)
* Cria busca de evolução de km (PL-3171)
* Cria relatorio evolução de km (PL-3172)

### Refactor
* Altera inserção de checklist para aceitar observação (PL-3163)
* Insere observação na busca de um checklist (PL-3164)
* Altera sistema de autenticação interna permitindo Bearer e Basic

<a name="v1.2.1"></a>
## Version [v1.2.1](https://github.com/luizfp/PrologWebService/compare/v1.2.0...v1.2.1) (2020-10-04)

### Features
* Cria endpoint para buscar histórico de edições em csv (PL-3153)

### Refactor
* Salva código de veículo em tabelas (PL-3170)
* Altera insert/update de modelo de checklist para salvar JSON recebido (PS-1261)
* Adiciona identificador de frota em relatório integrado de aferição (PL-3130)
* Adiciona Spring Boot na estrutura do Prolog (PL-3159)

### Bug Fixes
* Corrige problema para realizar o upload de PDF de treinamento (PS-1257)
* Corrige relatório de km rodado por pneu para preencher as vidas nas colunas certas e trazer a primeira vida (PL-3156)

<a name="v1.2.0"></a>
## Version [v1.2.0](https://github.com/luizfp/PrologWebService/compare/v1.1.4...v1.2.0) (2020-09-23)

### Features
* Cria métodos para o vínculo automatizado entre placas e pneus (PL-2771)
* Cria relatório de último check realizado por placa (PL-3092)
* Cria testes automatizados de ordem de serviço da Avilan (PL-2906)
* Permitir listagem de histórico de edições de um veículo (PL-3098)

### Refactor
* Altera fluxo de update de um veículo (PL-3097)
* Adiciona permissão de relatório de checklist na busca de tipos de veículo
* Remove método @DELETE do resource de veículo

### Bugfix
* Corrige validação de DOT para deixar passar valores nulos ou vazios
* Corrige uso das Daos em integrações (PL-3136)
* Corrige bug em deleção de serviços e OSs ao transferir pneus (PL-2661)

<a name="v1.1.4"></a>
## Version [v1.1.4](https://github.com/luizfp/PrologWebService/compare/v1.1.3...v1.1.4) (2020-09-07)

### Features
* Cria métodos para o import automatizado de colaboradores (PL-2460)
* Cria nova estrutura de integração de Ordem de Serviço da Avilan (PL-2884)
* Cria estrutura de Logs de requisições e respostas (PL-2904)

### Refactor
* Insere código auxiliar na alternativa do modelo checklist (PLI-178)

<a name="v1.1.3"></a>
## Version [v1.1.3](https://github.com/luizfp/PrologWebService/compare/v1.1.2...v1.1.3) (2020-08-27)

### Features
* Cria método para buscar todos os diagramas com as nomenclaturas no VeiculoResource
* Insere coluna Jornada Bruta e Líquida no relatório de marcações por tipo jornada (PL-2850)
* Permite selecionar modelos de checklists bloqueados para integração (PL-2905)

### Refactors
* Altera validator de pneu para permitir valor >= 0

### Bugfix
* Atualizado versão da biblioteca do S3 para evitar erro no upload de arquivos: https://github.com/aws/aws-sdk-java/issues/2305
* Corrige erro na abertura de OS da Piccolotur (PL-3076)

<a name="v1.1.2"></a>
## Version [v1.1.2](https://github.com/luizfp/PrologWebService/compare/v1.1.1...v1.1.2) (2020-08-16)

### Features
* Cria classe VeiculoBackwardHelper para facilitar a migração dos usos de placa para código (PL-2621)

### Refactors
* Atualiza link da base de conhecimento na mensagem de erro do relatório de folha de ponto
* Remove obrigatoriedade de data de admissão no insert e update de colaborador
* Altera máximo de connections ao BD de 60 para 250 (PL-2826)

### Bugfix
* Erro no relatório Remuneração Acumulada Colaborador (PL-2852)

<a name="v1.1.1"></a>
## Version [v1.1.1](https://github.com/luizfp/PrologWebService/compare/v1.1.0...v1.1.1) (2020-08-01)

### Refactors
* Modifica inserção de movimentação para salvar a pressão atual do pneu (PL-2819)
* Modifica converter e functions de itens de OS para retornar url da midia da foto capturada no checklist e código do checklist (PL-2827)
* Criar arquivo YAML para conter familias e modelos bloqueados - Nepomuceno (PLI-192)

### Bug Fixes
* Corrige conversão de objetos antigos do checklist
* Corrige atualização de modelo de pneu (PL-2844)
* Corrigir stream de busca de código da filial da placa - Nepomuceno (PLI-191)

<a name="v1.1.0"></a>
## Version [v1.1.0](https://github.com/luizfp/PrologWebService/compare/v1.0.32...v1.1.0) (2020-07-12)

### Features
* Implementa tratamento de erros para o import dos mapas (PL-2410)
* Salva total de fotos que foram capturadas no processo de realização do checklist (PL-2708)
* Cria método de upload de fotos capturadas no checklist para o S3 (PL-2710)
* Adiciona possibilidade de parametrização de fotos no check no cadastro e edição de modelos (PL-1504)

## Refactors
* Bloqueia funcionalidades que unidade não contratou ou possui integração (PL-2671)
* Altera insert de checklist para lidar com possibilidade de que check já existia (PL-2820)
* Adiciona possibilidade de listar colaboradores, veículos e pneus por múltiplas unidades (PL-2695)
* Permite a configuração de mais de um código auxiliar para a mesma unidade (Integração Nepomuceno) (PLI-166)
* Utiliza configuração de cada unidade no Cronograma de Aferição (Integração Nepomuceno) (PLI-165)
* Cria estrutura de teste automatizados para a estrutura de aferição com o Protheus (PLI-150)
* Envia a data_hora aferição com timezone da unidade (Integração Nepomuceno) (PLI-173)
* Adiciona identificador frota nos objetos de serviços de pneus (PL-2761)
* Adiciona identificador frota nos objetos de aferição de pneus (PL-2760)
* Adiciona identificador frota no objeto PneuEmUso (PL-2760)
* Adiciona custo de seviços no relatório de dados gerais de movimentação (PL-2733)
* Implementa a estrutura de parametrização de fotos na busca de modelos de checklist (PL-2272)
* Cria uma nova listagem otimizada de checklists realizados, mantendo a compatibilidade (PL-2773)
* Adiciona a lista de mídias na visualização de checklists realizados (PL-2774)

<a name="v1.0.32"></a>
## Version [v1.0.32](https://github.com/luizfp/PrologWebService/compare/v1.0.31...v1.0.32) (2020-06-10)

## Refactors
* Atualiza uso da tabela 'afericao_configuracao_tipo_afericao_veiculo' na integração Nepomuceno (PLI-149)
* Configura modelos de veículos não utilizados da Nepomuceno (PLI-164)

<a name="v1.0.31"></a>
## Version [v1.0.31](https://github.com/luizfp/PrologWebService/compare/v1.0.30...v1.0.31) (2020-06-03)

### Features
* Cria relatório de CPK por marca, modelo e dimensão de pneu (PL-2699)
* Cria relatório de km rodado por vida de forma colunada (PL-2598)
* Altera path de relatório de km rodado por vida em linhas (PL-2598)
* Remove FKs de import de veículo e pneu (PL-2711)
* Permite parametrização de aferições e fechamento de serviço manuais, com equipamentos ou ambos (PL-2689)
* Permite salvar forma de coleta de dados da aferição (PL-2686)
* Permite salvar forma de coleta de dados no fechamento de serviços (PL-2714)
* Busca a forma de coleta dos dados para os serviços fechados (PL-2715)
* Adiciona número de frota ao veículo (PL-827)
* Adiciona número de frota ao import de veículo (PL-827)

## Refactors
* Refatora objetos que utilizam booleans podeAferirSulco, Pressao e SulcoPressao para não utilizar ou serem adaptados (PL-2689)
* Refatora relatórios e objetos de aferição para retornar também a forma de coleta dos dados (PL-2684)
* Altera vida máxima no cadastro/edição de pneu para 11

### Bug Fixes
* Corrige busca de modelos de quizzes para realização
* Corrige mensagem de erro ao tentar iniciar uma nova aferição de placa ou avulsa

<a name="v1.0.30"></a>
## Version [v1.0.30](https://github.com/luizfp/PrologWebService/compare/v1.0.29...v1.0.30) (2020-05-19)

### Features
* Cria estrutura de mapeamento de posições no Prolog (PLI-142)
* Cria flag nas integrações para ligar/desligar integrações (PLI-72)
* Cria método genérico de busca de Aferições Realizadas (AVACON) (PLI-144)
* Adiciona validação de unidades integradas no Sistema Globus Piccolotur (PLI-151)

### Refactors
* Melhora mensanges de retorno ao ativar/inativar um veículo
* Valida CPF nos processos de transferência integrados (PLI-147)
* Valida CPF nos processos de fechamento de OS integrados (PLI-153)

### Bugfix
* Corrige problema de parse de data na sincronia de checklist (PLI-146)
* Corrige uso do codUnidade no método de atualização de veiculo (PLI-129)

<a name="v1.0.29"></a>
## Version [v1.0.29](https://github.com/luizfp/PrologWebService/compare/v1.0.28...v1.0.29) (2020-04-28)

### Bugfix
* Fixa versão do jackson para evitar problemas entre bibliotecas
* Nova correção na abertura de Item de OS tipo Outros (PLI-138)

<a name="v1.0.28"></a>
## Version [v1.0.28](https://github.com/luizfp/PrologWebService/compare/v1.0.27...v1.0.28) (2020-04-27)

### Features
* Notifica quem solicitou o socorro se ele for invalidado (PL-2580)
* Notifica via e-mail quando um socorro em rota é aberto (PL-2522)

### Refactors
* Altera relatório de aderência de intervalos para melhorar otimização (PL-2720)

### Bugfix
* Modifica inserção para realizar deleção antes das inserções (PL-2681)
* Corrige totais considerando marcações fora de jornada. (PL-2565)
* Adiciona validação para placas em branco (PL-2654)
* Corrige relatório de aderição por placa pra fazer o cálculo do período da primeira aferição(PL-1900)
* Corrigir abertura de Item de OS tipo Outros (PLI-138)

<a name="v1.0.27"></a>
## Version [v1.0.27](https://github.com/luizfp/PrologWebService/compare/v1.0.26...v1.0.27) (2020-04-14)

### Features
* Inicaliza Sentry com informações da versão do WS
* Cria estrutura para empresa reset/clonagem de empresa de apresentação (PL-2034)

### Refactors
* Cria serializer/deserializer específico para o `OrigemDestinoEnum` (PL-2681) 
* Modifica listagem de transições por unidade para completar lista com transições não cadastradas (PL-2681)
* Adiciona código da unidade da placa no cronograma de aferição (PLI-119)

<a name="v1.0.26"></a>
## Version [v1.0.26](https://github.com/luizfp/PrologWebService/compare/v1.0.25...v1.0.26) (2020-04-12)

### Features
* Cria dashboard de socorros por status nos ultimos 30 dias (PL-2618)
* Cria estrutura para cadastro e configuração de motivos para o processo de movimentação (PL-2607)
* Permite alteração da pressão recomendada dos pneus (PL-2570)

### Refactors
* Permitir fechamento de O.Ss originadas no Prolog (PLI-99)
* Adiciona os registros de deslocamento para socorros em rota (PL-2631)
* Adiciona informações de tempo entre cada status do socorro em rota (PL-2585)
* Roteia método de sincronia de checklist offline (PLI-118)

### Bug Fixes
* Corrige parse sem replace de virgula no import de csv (PL-2573)

<a name="v1.0.25"></a>
## Version [v1.0.25](https://github.com/luizfp/PrologWebService/compare/v1.0.24...v1.0.25) (2020-04-02)

### Features
* Injeta colaborador autenticado no SecurityContext do request (PL-2638)
* Cria relatório de permissões detalhadas (PL-2627)
* Criar método de validação de posições de pneus (PLI-52)
* Criar estrutura para liberação de modelos de checklist para a integração de O.S (PLI-113)
* Criar método roteado para buscar os locais de movimento do Globus (PLI-107)

### Refactors
* Modifica function que insere checklist (PL-2569)

## Version [v1.0.24](https://github.com/luizfp/PrologWebService/compare/v1.0.23...v1.0.24) (2020-03-29)

### Features
* Cria estrutura de campos personalizados (PL-2616)
* Aplica fluxo de campos personalizados no processo de movimentação (PL-2616)
* Cria integração de pneus para o cliente Expresso Nepomuceno (PLI-93)

### Refactors
* Adiciona código auxiliar no cadastro de tipo de veículo (PL-2560)
* Altera fluxo de transferência de pneus dentro da integração (PLI-80)
* Altera fluxo de transferência de veículos dentro da integração (PLI-80)

<a name="v1.0.23"></a>
## Version [v1.0.23](https://github.com/luizfp/PrologWebService/compare/v1.0.22...v1.0.23) (2020-03-18)

### Features
* Adiciona a quantidade de permissões na busca de cargos (PL-2532)
* Criar configuração para saber se uma integração abre serviços de pneu (PLI-78)
* Cria edição, visualização e listagem de unidade (PL-2588)
* Altera forma de autenticação para usuários internos (PL-2550)

### Refactors
* Modifica update do pneu, permitindo atualizar vida total (PL-2145)
* Diferencia a plataforma de execução das ações de socorro em rota (PL-2527)
* Altera as interações com o banco de dados para implementar a amarração entre pneus e veículos (PL-1965)
* Impedir que itens sejam resolvidos com data anterior à realização do check (PL-2500)
* Remove arquivos de config antigos do eclipse
* Remove lista de pneus disponíveis do `ServicoHolder` (PL-2510)
* Bloqueia busca do `ServicoHolder` para apps <= 101 (PL-2510)
* Refatora métodos do socorro-rota que ainda estavam em hardcode (PL-2577)

<a name="v1.0.22"></a>
## Version [v1.0.22](https://github.com/luizfp/PrologWebService/compare/v1.0.21...v1.0.22) (2020-03-03)

### Bug Fixes
* Corrige problema do último release onde faltavam as implementações da versão: v1.0.19

<a name="v1.0.21"></a>
## Version [v1.0.21](https://github.com/luizfp/PrologWebService/compare/v1.0.20...v1.0.21) (2020-02-25)

### Refactors
* Adiciona codModeloChecklist no envio dos dados para o Globus (PLI-89)

<a name="v1.0.20"></a>
## Version [v1.0.20](https://github.com/luizfp/PrologWebService/compare/v1.0.19...v1.0.20) (2020-02-25)

### Refactors
* Reestrutura mensagens de erro na sincronização dos checklists integrados (PLI-70)

### Bug Fixes
* Hotfix - Corrige erro de cast da function de atualização de status do pneu (PLI-87)

<a name="v1.0.19"></a>
## Version [v1.0.19](https://github.com/luizfp/PrologWebService/compare/v1.0.18...v1.0.19) (2020-02-15)

### Features
* Notifica responsável pela abertura que o socorro foi atendido (PL-2541)
* Cria método de upload de fotos da abertura para o S3 (PL-2518)
* Cria relatório de dados gerais de socorros em rotas (PL-2523)

<a name="v1.0.18"></a>
## Version [v1.0.18](https://github.com/luizfp/PrologWebService/compare/v1.0.17...v1.0.18) (2020-02-13)

### Refactors
* Remove roteamento de tipos de veículos e cria busca específica para checklists (PL-2536)
* Cria bloqueio de integração por unidades (PLI-71)

<a name="v1.0.17"></a>
## Version [v1.0.17](https://github.com/luizfp/PrologWebService/compare/v1.0.16...v1.0.17) (2020-02-11)

### Bug Fixes
* Corrige problema ao sincronizar checklists realizados na integração com Avilan (PLI-74)
* Corrige envio de itens NOK para o Globus na integração de O.S (PLI-76)

<a name="v1.0.16"></a>
## Version [v1.0.16](https://github.com/luizfp/PrologWebService/compare/v1.0.15...v1.0.16) (2020-02-09)

### Features
* Cria objetos base do Socorro em Rota (PL-2421)
* Implementa solicitação de socorro em rota (PL-2423)
* Implementa a listagem de unidades dispoíveis na abertura de um socorro em rota (PL-2445)
* Implementa a listagem de veículos dispoíveis na abertura de um socorro em rota (PL-2446)
* Implementa a listagem de opções de problema disponíveis na abertura de um socorro em rota (PL-2447)
* Implementa a listagem de socorros em rota (PL-2424)
* Implementa a visualização de um socorro em rota (PL-2425)
* Implementa o atendimento de socorros em rota (PL-2426)
* Implementa a invalidação de socorros em rota (PL-2427)
* Implementa a finalização de socorros em rota (PL-2428)
* Implementa a listagem de opções de problemas (PL-2465)
* Implementa o cadastro de opções de problemas (PL-2466)
* Implementa a edição de opções de problemas (PL-2467)
* Implementa a visualziação de uma opção de problema específica (PL-2468)
* Implementa a ativação/inativação de uma opção de problema (PL-2478)
* Adiciona telefone e e-mail no cadastro, edição e visualização de colaboradores (PL-2471)
* Cria estrutura para enviar notificações em push do firebase (PL-2496)
* Envia notificações push com firebase para informar abertura de socorro (PL-2496)

### Refactors
* Modifica estrutura para a inserção de uma nova aferição, adicionando cod_diagrama (PL-1899)
* Modifica estrutura para a inserção de uma nova movimentação, adicionando cod_diagrama (PL-1899)
* Adiciona novas informações no mapa (PL-2409)

<a name="v1.0.15"></a>
## Version [v1.0.15](https://github.com/luizfp/PrologWebService/compare/v1.0.14...v1.0.15) (2020-01-30)

### Refactors
* Reestrutra integrações de ordem de serviço (PLI-66)

<a name="v1.0.14"></a>
## Version [v1.0.14](https://github.com/luizfp/PrologWebService/compare/v1.0.13...v1.0.14) (2020-01-26)

### Bug Fixes
* Corrige data/hora setada incorretamente nos processos de movimentação e fechamento de serviços de aferição (PL-2490)
* Corrige uso da data/hora na API de pneus (PLI-67)

<a name="v1.0.13"></a>
## Version [v1.0.13](https://github.com/luizfp/PrologWebService/compare/v1.0.12...v1.0.13) (2020-01-21)

### Bug Fixes
* Corrige problema com uso indevido de anotação @NotNull em objeto de pergunta do check

<a name="v1.0.12"></a>
## Version [v1.0.12](https://github.com/luizfp/PrologWebService/compare/v1.0.11...v1.0.12) (2020-01-18)

### Features
* Cria testes para validar criação e edição de modelos de checklist (PL-2305)
* Implementa o uso da tabela de histórico de checklists realizados e itens apontamentos (PL-2370) 

### Refactors
* Os método de busca de modelos de checklist para seleção foi recriado com novos objetos (PL-2228)
* Os métodos de início de um novo checklist foram recriados com novos objetos (PL-2228)
* Altera métodos de insert de checklist online e offline para novos objetos e estrutura (PL-2227)
* Altera integração com Avilan e com Piccolotur para utilizar novo objeto de checklist (PL-2227)
* Altera os fluxos de insert e edição de modelos de checklist para nova estrutura (PL-2231)
* Altera o fluxo de processamento de ordens de serviço para considerar a versão do modelo de checklist (PL-2346)
* Altera busca dos dados de checklist para realização offline incluindo novas informações 
(versão modelo e código context) PL-2349 
* Altera o fluxo de realização de checklist na integração ao Globus (PL-2369)
* Altera forma de processar abertura de OSs e considera texto das alternativas tipo_outros para abrir ou não 
um item (PL-2389)
* Altera método de busca das URLs das perguntas do checklist (PL-2386)
* Altera busca do farol do checklist (PL-2417)

<a name="v1.0.11"></a>
## Version [v1.0.11](https://github.com/luizfp/PrologWebService/compare/v1.0.10...v1.0.11) (2020-01-15)

### Refactors
* Adiciona configuração para bloquear processo de aferição (PL-1934)
* Adiciona nova permissão para busca de relatório de entrega

<a name="v1.0.10"></a>
## Version [v1.0.10](https://github.com/luizfp/PrologWebService/compare/v1.0.9...v1.0.10) (2020-01-07)

### Bug Fixes
* Adicionado TZ da unidade nas buscas de colaborador que tinham problemas (PL-2367)


<a name="v1.0.9"></a>
## Version [v1.0.9](https://github.com/luizfp/PrologWebService/compare/v1.0.8...v1.0.9) (2020-01-06)

### Features
* Cria import automatizado de pneus (PL-2320)

### Refactors
* Altera tratamento de erro da VeiculoService para utilizar tratador específico
* Retorna TZ da unidade no objeto colaborador

<a name="v1.0.8"></a>
## Version [v1.0.8](https://github.com/luizfp/PrologWebService/compare/v1.0.7...v1.0.8) (2019-12-02)

### Features
* Cria relatório de farol de aferições (PL-2379)
* Adiciona a configuração de restrições de pneus para o cronograma e serviços (PL-1989 / PL-2011)

### Refactors
* Remove validação de CPF no cadastro / edição de colaborador (PL-2400)
* Altera forma de autenticar o envio de movimentações com a Praxio (PLI-41) 
* Melhora código da estrutura de metas (PL-2232)
* Altera nome de coluna de serviço realizado de pneu (PL-2295) 

### Bug fixes
* Corrige problema para buscar valor de meta do apontamento do tracking (PL-2232)

<a name="v1.0.7"></a>
## Version [v1.0.7](https://github.com/luizfp/PrologWebService/compare/v1.0.6...v1.0.7) (2019-11-24)

### Bug Fixes
* Corrige validação de nome de colaborador ao cadastrar/editar

<a name="v1.0.6"></a>
## Version [v1.0.6](https://github.com/luizfp/PrologWebService/compare/v1.0.5...v1.0.6) (2019-11-24)

### Features
* Integra envio de movimentação em tempo real (PLI-41)

### Refactors
* Adiciona informações de fadiga no prontuário do condutor (PL-2269)
* Permite filtrar para incluir marcas não utilizadas na busca de marcas de pneus e bandas (PL-2390)
* Cria métodos para import de planilha de veículos (PL-2318)
* Adiciona filtro de todas unidades no cronograma de aferição (PLI-51)

<a name="v1.0.5"></a>
## Version [v1.0.5](https://github.com/luizfp/PrologWebService/compare/v1.0.4...v1.0.5) (2019-11-06)

### Refactors
* Altera método na API do ProLog para buscar relatório de controle de jornada da portaria 1510 (PLI-45)

<a name="v1.0.4"></a>
## Version [v1.0.4](https://github.com/luizfp/PrologWebService/compare/v1.0.3...v1.0.4) (2019-11-05)

### Features
* Faz log de requisições serem clicáveis e direcionarem aos métodos no Resource
* Cria método na API do ProLog para buscar relatório de controle de jornada da portaria 1510 (PLI-45)

### Refactors
* Adiciona queryparam no cadastro de pneus para deixar parametrizável validação de DOT
* Permite editar infos de marcas e modelos de pneu e de banda (PL-2263)
* Bloqueia métodos antigos de marca e modelo de pneu e banda (PL-2263)

### Bug Fixes
* Corrige fluxo que salva aferição valores para cenários onde não deve abrir serviços de pneus (PLI-37)

<a name="v1.0.3"></a>
## Version [v1.0.3](https://github.com/luizfp/PrologWebService/compare/v1.0.2...v1.0.3) (2019-10-20)

### Features
* Cria estrutura para salvar requisições e respostas das integrações. (PL-2306)
* Cria funcionalidade de testes do aferidor (PL-2343)
* Cria estrutura de pesquisa de NPS (PL-2350)

### Refactors
* Melhore log de erros no import do mapa
* Atualiza retrofit e okhttp para últimas versões
* Bloqueia abertura de Serviços de Pneus na integração com a Praxio (PLI-37)
* Mostra serviços fechados automaticamente por integração (PLI-31)
* Bloqueia movimentações diferentes de ESTOQUE -> DESCARTE para clientes Afere Fácil (PLI-38)
* Altera insert de veículo para utilizar novo objeto e salvar cod_empresa (PL-2276)

<a name="v1.0.2"></a>
## Version [v1.0.2](https://github.com/luizfp/PrologWebService/compare/v1.0.1...v1.0.2) (2019-09-29)

### Features
* Cria funcionalidade de inserção/edição de nomenclatura (PL-2259)

### Refactors
* Altera bloqueador de requisições do App para ignorar requisições do Afere Fácil da Praxio (PL-2207)
* Altera estrutura de checklist para não salvar nomes iguais (PL-2140)

<a name="v1.0.1"></a>
## Version [v1.0.1](https://github.com/luizfp/PrologWebService/compare/v1.0.0...v1.0.1) (2019-09-18)

### Features
* Bloqueia funcionalidades de cadastro/edição de pneus e veículos, movimentação e transferências no ProLog (PL-2296)
* Fecha serviços de pneus automaticamente ao atualizar o status do pneu na integração com Praxio (PL-2302)

### Refactors
* Seta prioridades das alternativas do check na integração com Avilan como BAIXA (PL-2304)
* Melhora mensagens de erro na integração com a Translecchi (PL-2284)
* Modifica método updateStatusAtivo do checklist, impossibilitando a ativação de modelos com mesmo nome. 

<a name="v1.0.0"></a>
## Version [v1.0.0](https://github.com/luizfp/PrologWebService/compare/v0.1.01...v1.0.0) (2019-09-15)

### Features
* Cria estrutura para verificar planilha de import de veículo. (PL-2189)
* Cria resource para monitorarmos se o WS está rodando
* Cria integração do ProLog com a Praxio (PL-2126)

### Refactors
* Modifica quantidade máxima de semanas para 53 no valor do DOT. (PL-2214)
* Permite que o valor do DOT seja inserido como null. (PL-2218)

<a name="v0.1.01"></a>
## Version [v0.1.01](https://github.com/luizfp/PrologWebService/compare/v0.1.0...v0.1.01) (2019-09-08)

### Features
* Cria funcionalidade de inserção/edição e visualização de nomenclaturas. (PL-2259)
* Adiciona relatório para exportação das marcações (PL-2223)
* Cria API do Controle de Jornada (PL-2271)

### Refactors
* Remove provisoriamente validador de placa do veículo no cadastro (foco em internacionalização)
* Melhora método de busca de um quiz pelo código (PL-2249)
* Adiciona o separa datas para o filtro de abertura e fechamento no relatório de estratificação de O.S. (PL-2175)
* Altera os métodos de validação das requisições para diferenciar retornos de login inválido e sem permissão (PL-2267)
* Adiciona código auxiliar ao cadastro / edição de tipos de marcação (PL-2223)

### Bug Fixes
* Corrige bug no Ajuste de Marcações do controle de jornada (PL-2280)

<a name="v0.1.0"></a>
## Version [v0.1.0](https://github.com/luizfp/PrologWebService/compare/v0.0.99...v0.1.0) (2019-08-19)

### Refactors
* Melhora tratamento de erros na busca de checklists

### Bug Fixes
* Corrige busca de checklists na integração com Avilan

<a name="v0.0.99"></a>
## Version [v0.0.99](https://github.com/luizfp/PrologWebService/compare/v0.0.98...v0.0.99) (2019-08-18)

### Features
* Adiciona a gestão de dispositivos móveis por empresa (PL-2150)

### Refactors
* Altera inserção de checklist para salvar quantidade de perguntas e alternativas OK e NOK (PL-2118)
* Melhora tratamento de erros na integração com Avilan (PL-2210)
* Remove pilar GERAL da visão ao logar pelo app (PL-2187)
* Adiciona os registros de IMEI nas telas de listagem e gestão de marcações (PL-2152) 

<a name="v0.0.98"></a>
## Version [v0.0.98](https://github.com/luizfp/PrologWebService/compare/v0.0.97...v0.0.98) (2019-07-16)

### Features
* Adiciona estrutura para inserção, listagem, edição e deleção lógica de cargos

### Refactors
* Altera todos os usos de @Notnull e @Nullable para biblioteca da jetbrains
* Altera inserção de modelos de checklist para permitir inserir um modelo já inativo (PL-2139)
* Altera update de modelos de checklist para permitir atualizar sem recriar perguntas e alternativas (PL-2139)
* Altera índices de importação do arquivo de prontuário do condutor

### Bug Fixes
* Corrige visão e edição de permissões para considerar apenas os pilares ativos por unidade (PL-2038)
* Corrige a listagem e edição dos modelos de checklist para exibir modelos sem cargos vinculados (PL-2037)

<a name="v0.0.97"></a>
## Version [v0.0.97](https://github.com/luizfp/PrologWebService/compare/v0.0.96...v0.0.97) (2019-06-28)

### Features
* Cria funcionalidade de listagem de marcações otimizada, com filtro de data. (PL-2000)
* Cria método de busca das regionais e unidades de filtro para início de checklist (PL-2085)

### Refactors
* Altera método de login para incluir se empresa está bloqueada para realizar checklist de diferentes unidades no `LoginHolder` (PL-2077)
* Altera método de busca de um checklist por código (PL-2075)

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
