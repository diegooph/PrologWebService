package br.com.zalf.prolog.webservice.gente.faleConosco.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
@Path("/faleconosco/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoRelatorioResource {

    FaleConoscoRelatorioService service = new FaleConoscoRelatorioService();

    @GET
    @Secured
    @Path("/resumo/{codUnidade}/csv")
    public StreamingOutput getResumoCsv(@PathParam("codUnidade") Long codUnidade, @QueryParam("dataInicial") Date dataInicial,
                                        @QueryParam("dataFinal") Date dataFinal) {
        return outputStream -> service.getResumoCsv(codUnidade, outputStream, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/resumo/{codUnidade}/report")
    public Report getResumoReport(@PathParam("codUnidade") Long codUnidade, @QueryParam("dataInicial") Date dataInicial,
                                  @QueryParam("dataFinal") Date dataFinal) {
        return service.getResumoReport(codUnidade, dataInicial, dataFinal);
    }


}
