package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.model.ApiMarcacao;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/marcacoes/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcacaoResource {
    @NotNull
    private ApiMarcacaoService service = new ApiMarcacaoService();

    @GET
    @LogIntegracaoRequest
    @Path("marcacoes-realizadas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacao> getMarcacoesRealizadas(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaMarcacaoSincronizada") @Required final Long codUltimaMarcacaoSincronizada)
            throws ProLogException {
        return service.getMarcacoesRealizadas(tokenIntegracao, codUltimaMarcacaoSincronizada);
    }
}