package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by Zart on 18/05/2017.
 */
@Path("/v2/produtividades/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Relatorios.PRODUTIVIDADE)
public class ProdutividadeRelatorioResource {
    private final ProdutividadeRelatorioService service = new ProdutividadeRelatorioService();

    @GET
    @Path("/consolidados/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getConsolidadoProdutividadeCsv(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getConsolidadoProdutividadeCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/consolidados/{codUnidade}/report")
    public Report getConsolidadoProdutividadeReport(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) {
        return service.getConsolidadoProdutividadeReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extratos/individuais/{codUnidade}/{cpf}/csv")
    @Produces("application/csv")
    public StreamingOutput getExtratoIndividualProdutividadeCsv(@PathParam("cpf") String cpf,
                                                                @PathParam("codUnidade") Long codUnidade,
                                                                @QueryParam("dataInicial") long dataInicial,
                                                                @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getExtratoIndividualProdutividadeCsv(outputStream, cpf, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extratos/individuais/{codUnidade}/{cpf}/report")
    public Report getExtratoIndividualProdutividadeReport(@PathParam("cpf") String cpf,
                                                          @PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return service.getExtratoIndividualProdutividadeReport(cpf, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extratos/acessos/{codUnidade}/{cpf}/csv")
    @Produces("application/csv")
    public StreamingOutput getAcessosProdutividadeCsv(@PathParam("cpf") String cpf,
                                                      @PathParam("codUnidade") Long codUnidade,
                                                      @QueryParam("dataInicial") long dataInicial,
                                                      @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getAcessosProdutividadeCsv(outputStream, cpf, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extratos/acessos/{codUnidade}/{cpf}/report")
    public Report getAcessosProdutividadeReport(@PathParam("cpf") String cpf,
                                                @PathParam("codUnidade") Long codUnidade,
                                                @QueryParam("dataInicial") long dataInicial,
                                                @QueryParam("dataFinal") long dataFinal) {
        return service.getAcessosProdutividadeReport(cpf, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/remuneracao-acumulada-colaborador/{codUnidade}/{cpf}")
    public List<ProdutividadeColaboradorRelatorio> getRelatorioProdutividadeColaborador(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("cpf") String cpfColaborador,
            @QueryParam("dataInicial") String dataInicial,
            @QueryParam("dataFinal") String dataFinal) {
        return service.getRelatorioProdutividadeColaborador(codUnidade, cpfColaborador, dataInicial, dataFinal);
    }
}
