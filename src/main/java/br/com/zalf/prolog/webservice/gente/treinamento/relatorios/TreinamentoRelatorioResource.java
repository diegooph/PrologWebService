package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created on 14/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/treinamentos/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoRelatorioResource {

    @NotNull
    private final TreinamentoRelatorioService service = new TreinamentoRelatorioService();

    @GET
    @Path("visualizados/{codUnidade}/csv")
    @Secured()
    @Produces("application/csv")
    public StreamingOutput getRelatorioEstratificadoPorColaborador(@Required @PathParam("codUnidade") Long codUnidade,
                                                                   @QueryParam("dataInicial") String dataInicial,
                                                                   @QueryParam("dataFinal") String dataFinal) {
        return service.getRelatorioEstratificadoPorColaboradorCsv(codUnidade, dataInicial, dataFinal);
    }
}
