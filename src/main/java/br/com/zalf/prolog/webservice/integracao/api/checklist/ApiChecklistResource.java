package br.com.zalf.prolog.webservice.integracao.api.checklist;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/checklists")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiChecklistResource {
    @NotNull
    private ApiChecklistService service = new ApiChecklistService();

    @GET
    @LogIntegracaoRequest
    @Path("/alternativas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
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
