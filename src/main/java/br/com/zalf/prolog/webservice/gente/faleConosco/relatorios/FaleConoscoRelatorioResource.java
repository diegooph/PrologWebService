package br.com.zalf.prolog.webservice.gente.faleConosco.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
@Path("/v2/fale-conosco/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoRelatorioResource {

    FaleConoscoRelatorioService service = new FaleConoscoRelatorioService();

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


}
