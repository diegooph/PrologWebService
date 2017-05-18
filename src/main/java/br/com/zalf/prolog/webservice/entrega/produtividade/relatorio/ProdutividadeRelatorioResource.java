package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 18/05/2017.
 */
@Path("/produtividades/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProdutividadeRelatorioResource {

    private ProdutividadeRelatorioService service = new ProdutividadeRelatorioService();

    @GET
    @Secured
    @Path("/consolidados/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getConsolidadoProdutividadeCsv(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getConsolidadoProdutividadeCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/consolidados/{codUnidade}/report")
    public Report getConsolidadoProdutividadeReport(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) {
        return service.getConsolidadoProdutividadeReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/extratos/individuais/{cpf}/csv")
    public StreamingOutput getExtratoIndividualProdutividadeCsv(@PathParam("cpf") Long cpf,
                                                                @QueryParam("dataInicial") long dataInicial,
                                                                @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getExtratoIndividualProdutividadeCsv(outputStream, cpf, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/extratos/individuais/{cpf}/report")
    public Report getExtratoIndividualProdutividadeReport(@PathParam("cpf") Long cpf,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return service.getExtratoIndividualProdutividadeReport(cpf, dataInicial, dataFinal);
    }




}
