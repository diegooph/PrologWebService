package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/raizen/produtividades/relatorios")
@ConsoleDebugLog
@Secured(permissions = Pilares.Entrega.RaizenProdutividade.VISUALIZAR_RELATORIOS)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RaizenProdutividadeRelatorioResource {
    @NotNull
    private final RaizenProdutividadeRelatorioService service = new RaizenProdutividadeRelatorioService();

    @GET
    @Produces("application/csv")
    @Path("/dados-gerais-produtividades/{codUnidade}/csv")
    public StreamingOutput getDadosGeraisProdutividadeCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                          @QueryParam("dataInicial") @Required final String dataInicial,
                                                          @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> service.getDadosGeraisProdutividadeCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/dados-gerais-produtividades/{codUnidade}/report")
    public Report getDadosGeraisProdutividadeReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                                    @QueryParam("dataInicial") @Required final String dataInicial,
                                                    @QueryParam("dataFinal") @Required final String dataFinal)
            throws ProLogException {
        return service.getDadosGeraisProdutividadeReport(codUnidade, dataInicial, dataFinal);
    }
}