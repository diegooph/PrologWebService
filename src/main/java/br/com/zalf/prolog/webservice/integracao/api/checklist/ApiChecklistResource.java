package br.com.zalf.prolog.webservice.integracao.api.checklist;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/checklists")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiChecklistResource {
    @NotNull
    private final ApiChecklistService service = new ApiChecklistService();

    @GET
    @LogRequest
    @Path("/alternativas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasModelosAtivos") @Required final boolean apenasModelosAtivos,
            @QueryParam("apenasPerguntasAtivas") @Required final boolean apenasPerguntasAtivas,
            @QueryParam("apenasAlternativasAtivas") @Required final boolean apenasAlternativasAtivas) throws ProLogException {
        return service.getAlternativasModeloChecklist(
                tokenIntegracao,
                apenasModelosAtivos,
                apenasPerguntasAtivas,
                apenasAlternativasAtivas);
    }
}