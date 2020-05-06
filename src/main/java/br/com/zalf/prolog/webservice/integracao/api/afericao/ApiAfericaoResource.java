package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
@DebugLog
@Path("/api/afericoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ApiAfericaoResource {

    @NotNull
    private final ApiAfericaoService service;

    @GET
    @LogIntegracaoRequest
    @Path("/afericoes-realizadas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<AfericaoRealizada> getAfericoesRealizadas(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) throws ProLogException {
        return service.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
    }

}
