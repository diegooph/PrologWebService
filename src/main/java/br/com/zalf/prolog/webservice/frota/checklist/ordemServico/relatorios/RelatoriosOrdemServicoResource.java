package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.commons.Report;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by luiz on 26/04/17.
 */
@Path("/checklists/ordens-servico/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatoriosOrdemServicoResource {
    private RelatoriosOrdemServicoService service = new RelatoriosOrdemServicoService();

    @GET
    @Path("/itens-nok/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getItensMaiorQuantidadeNokCsv(@PathParam("codUnidade") Long codUnidade,
                                                         @QueryParam("dataInicial") long dataInicial,
                                                         @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getItensMaiorQuantidadeNokCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/itens-nok/{codUnidade}/report")
    public Report getItensMaiorQuantidadeNokReport(@PathParam("codUnidade") Long codUnidade,
                                                   @QueryParam("dataInicial") long dataInicial,
                                                   @QueryParam("dataFinal") long dataFinal) {
        return service.getItensMaiorQuantidadeNokReport(codUnidade, dataInicial, dataFinal);
    }
}