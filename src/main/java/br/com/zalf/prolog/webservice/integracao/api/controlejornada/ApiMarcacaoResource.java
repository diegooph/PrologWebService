package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/marcacoes/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcacaoResource {
    @NotNull
    private final ApiMarcacaoService service = new ApiMarcacaoService();

    @GET
    @LogRequest
    @Path("marcacoes-realizadas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacao> getMarcacoesRealizadas(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaMarcacaoSincronizada") @Required final Long codUltimaMarcacaoSincronizada)
            throws ProLogException {
        return service.getMarcacoesRealizadas(tokenIntegracao, codUltimaMarcacaoSincronizada);
    }
}
