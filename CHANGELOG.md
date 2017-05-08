Change Log
==========

<a name="v0.0.11"></a>
## Version [v0.0.11](https://github.com/luizfp/PrologWebService/compare/v0.0.10-hotfix1...v0.0.11) (2017-03-23)

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

#### Métodos novos em Resources
* FaleConoscoRelatorioResource:

      @Path("/fale-conosco/relatorios") 
       

      @GET
      @Secured
      @Path("/resumos/{codUnidade}/csv")
      public StreamingOutput getResumoCsv(@PathParam("codUnidade") Long codUnidade, @QueryParam("dataInicial") long dataInicial,
                                          @QueryParam("dataFinal") long dataFinal) {
          return outputStream -> service.getResumoCsv(codUnidade, outputStream, new Date(dataInicial), new Date(dataFinal));
      }
  
      @GET
      @Secured
      @Path("/resumos/{codUnidade}/report")
      public Report getResumoReport(@PathParam("codUnidade") Long codUnidade, @QueryParam("dataInicial") long dataInicial,
                                    @QueryParam("dataFinal") long dataFinal) {
          return service.getResumoReport(codUnidade, new Date(dataInicial),  new Date(dataFinal));
      }
      
* SolicitacaoFolgaResource

        @Path("/solicitacao-folgas/relatorios")
        

        @GET
        @Path("/resumos/{codUnidade}/csv")
        @Secured
        public StreamingOutput getResumoFolgasConcedidasCsv(@PathParam("codUnidade") Long codUnidade,
                                                            @QueryParam("dataInicial") long dataInicial,
                                                            @QueryParam("dataFinal") long dataFinal) {
            return outputStream -> service.getResumoFolgasConcedidasCsv(codUnidade, outputStream, dataInicial, dataFinal);
        }
    
        @GET
        @Secured
        @Path("/resumos/{codUnidade}/report")
        public Report getResumoFolgasConcedidasReport(@PathParam("codUnidade") Long codUnidade,
                                                      @QueryParam("dataInicial") long dataInicial,
                                                      @QueryParam("dataFinal") long dataFinal) {
            return service.getResumoFolgasConcedidasReport(codUnidade, dataInicial, dataFinal);
        }

      
  
