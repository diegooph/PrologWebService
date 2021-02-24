package br.com.zalf.prolog.webservice.seguranca.relato.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 20/11/2017.
 */
@Path("/v2/relatos/relatorios")
@Secured(permissions = Pilares.Seguranca.Relato.RELATORIOS)
public class RelatoRelatorioResource {

    private RelatoRelatorioService service = new RelatoRelatorioService();

    @GET
    @Path("/extratos/{codUnidade}/{equipe}/csv")
    public StreamingOutput getRelatosEstratificadosCsv(@PathParam("codUnidade") Long codUnidade,
                                                       @QueryParam("dataInicial") Long dataInicial,
                                                       @QueryParam("dataFinal") Long dataFinal,
                                                       @PathParam("equipe") String equipe) {
       return outputStream -> service.getRelatosEstratificadosCsv(codUnidade, dataInicial, dataFinal, equipe, outputStream);
    }

    @GET
    @Path("/extratos/{codUnidade}/{equipe}/report")
    public Report getRelatosEstratificadosReport(@PathParam("codUnidade") Long codUnidade,
                                                 @QueryParam("dataInicial") Long dataInicial,
                                                 @QueryParam("dataFinal") Long dataFinal,
                                                 @PathParam("equipe") String equipe) {
        return service.getRelatosEstratificadosReport(codUnidade, dataInicial, dataFinal, equipe);
    }
}
