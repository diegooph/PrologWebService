package br.com.zalf.prolog.webservice.gente.controleintervalo.controleintervalorelatorios;

import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 28/08/2017.
 */
@Path("/intervalos/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleIntervaloRelatorioResource {

    private ControleIntervalosRelatorioService service = new ControleIntervalosRelatorioService();

    @GET
    @Secured
    @Path("/realizados/{codUnidade}/{cpf}/csv")
    @Produces("application/csv")
    public StreamingOutput getIntervalosCsv(@PathParam("codUnidade") Long codUnidade,
                                            @QueryParam("dataInicial") Long dataInicial,
                                            @QueryParam("dataFinal") Long dataFinal,
                                            @PathParam("cpf") String cpf) {
        return outputStream -> service.getIntervalosCsv(outputStream, codUnidade, dataInicial, dataFinal, cpf);
    }

}
