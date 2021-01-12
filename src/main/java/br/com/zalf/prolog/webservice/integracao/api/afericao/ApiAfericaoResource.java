package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@ConsoleDebugLog
@Path("/api/afericoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ApiAfericaoResource {
    @NotNull
    private final ApiAfericaoService service = new ApiAfericaoService();

    @GET
    @LogRequest
    @Path("/afericoes-realizadas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoProcessoAfericao") final Long codUltimoProcessoAfericao,
            @QueryParam("dataHoraUltimaAtualizacaoUtc") final String dataHoraUltimaAtualizacaoUtc) throws ProLogException {
        return service.getAfericoesRealizadas(tokenIntegracao, codUltimoProcessoAfericao, dataHoraUltimaAtualizacaoUtc);
    }
}
