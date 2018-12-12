package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/integracoes/praxio")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class IntegracaoPraxioResource {
    @NotNull
    private static final String HEADER_TOKEN_INTEGRACAO = "ProLog-Token-Integracao";
    @NotNull
    private final IntegracaoPraxioService service = new IntegracaoPraxioService();

    @GET
    @Path("/afericoes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<AfericaoIntegracaoPraxio> getAfericoesRealizadas(
            @HeaderParam(HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) throws ProLogException {
        service.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
        return service.getDummy();
    }
}
