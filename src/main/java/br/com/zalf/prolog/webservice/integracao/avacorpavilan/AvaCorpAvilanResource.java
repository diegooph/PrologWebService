package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created on 2020-09-03
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/integracoes/avilan")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AvaCorpAvilanResource {
    @NotNull
    private final AvaCorpAvilanService service = new AvaCorpAvilanService();

    @GET
    @Path("/ordens-servicos-pendentes/servicos-pentendes-csv") /* O último path será o nome do arquivo baixado. */
    @Produces("application/csv")
    public StreamingOutput getOrdensServicosPendentesSincroniaCsv(
            @QueryParam("dataInicial") @Optional final String dataInicial,
            @QueryParam("dataFinal") @Optional final String dataFinal) throws ProLogException {
        return outputStream ->
                service.getOrdensServicosPendentesSincroniaCsv(outputStream, dataInicial, dataFinal);
    }
}
