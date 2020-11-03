package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/marcacoes/tipos-marcacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiTipoMarcacaoResource {
    @NotNull
    private final ApiTipoMarcacaoService service = new ApiTipoMarcacaoService();

    @GET
    @LogRequest
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiTipoMarcacao> getTipoMarcacoes(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasTiposMarcacoesAtivos") @Required final boolean apenasTiposMarcacoesAtivos)
            throws ProLogException {
        return service.getTiposMarcacoes(tokenIntegracao, apenasTiposMarcacoesAtivos);
    }
}
