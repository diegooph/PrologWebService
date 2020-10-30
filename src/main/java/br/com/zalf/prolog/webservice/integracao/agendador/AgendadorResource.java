package br.com.zalf.prolog.webservice.integracao.agendador;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 31/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/integracoes/agendador")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AgendadorResource {
    @NotNull
    private final AgendadorService service = new AgendadorService();

    @GET
    @Path("/sincroniza-checklists")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public Boolean sincronizaChecklists(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_AGENDADOR) @Required final String tokenAgendador) {
        service.sincronizaChecklists();
        return true;
    }

    @GET
    @Path("/sincroniza-ordens-servicos")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public Boolean sincronizaOrdensServicos(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_AGENDADOR) @Required final String tokenAgendador) {
        service.sincronizaOrdensServicos();
        return true;
    }
}
