package br.com.zalf.prolog.webservice.integracao.api.unidade;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
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
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/unidades/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiUnidadeResource {
    @NotNull
    private final ApiUnidadeService service = new ApiUnidadeService();

    @GET
    @LogRequest
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiUnidade> getUnidades(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasUnidadesAtivas") @Required final boolean apenasUnidadesAtivas) throws ProLogException {
        return service.getUnidades(tokenIntegracao, apenasUnidadesAtivas);
    }
}
