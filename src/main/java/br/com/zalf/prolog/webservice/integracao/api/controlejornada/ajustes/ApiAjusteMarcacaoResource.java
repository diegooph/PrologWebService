package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/marcacoes/ajustes/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiAjusteMarcacaoResource {
    @NotNull
    private final ApiAjusteMarcacaoService service = new ApiAjusteMarcacaoService();

    @GET
    @LogRequest
    @Path("ajustes-realizados")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoAjusteMarcacaoSincronizado") @Required final Long codUltimoAjusteMarcacaoSincronizado)
            throws ProLogException {
        return service.getAjustesMarcacaoRealizados(tokenIntegracao, codUltimoAjusteMarcacaoSincronizado);
    }
}
