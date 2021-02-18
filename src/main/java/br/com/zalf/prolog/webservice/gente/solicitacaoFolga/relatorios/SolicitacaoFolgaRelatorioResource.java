package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by luiz on 12/05/17.
 */
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/v2/solicitacoes-folga/relatorios")
public class SolicitacaoFolgaRelatorioResource {

    SolicitacaoFolgaRelatorioService service = new SolicitacaoFolgaRelatorioService();

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

}