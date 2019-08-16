package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloPneu;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/pneus/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiPneuResource {
    @NotNull
    private ApiPneuService service = new ApiPneuService();

    @GET
    @LogIntegracaoRequest
    @Path("/marcas-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcaPneu> getMarcasPneu(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasMarcasPneuAtivas") @Required final boolean apenasMarcasPneuAtivas) throws ProLogException {
        return service.getMarcasPneu(tokenIntegracao, apenasMarcasPneuAtivas);
    }

    @GET
    @LogIntegracaoRequest
    @Path("/modelos-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiModeloPneu> getModelosPneu(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codMarcaPneu") @Required final Long codMarcaPneu,
            @QueryParam("apenasModelosPneuAtivos") @Required final boolean apenasModelosPneuAtivos) throws ProLogException {
        return service.getModelosPneu(tokenIntegracao, codMarcaPneu, apenasModelosPneuAtivos);
    }

    @GET
    @LogIntegracaoRequest
    @Path("/marcas-banda")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcaBanda> getMarcasBanda(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasMarcasBandaAtivas") @Required final boolean apenasMarcasBandaAtivas) throws ProLogException {
        return service.getMarcasBanda(tokenIntegracao, apenasMarcasBandaAtivas);
    }

    @GET
    @LogIntegracaoRequest
    @Path("/modelos-banda")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiModeloBanda> getModelosBanda(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codMarcaBanda") @Required final Long codMarcaBanda,
            @QueryParam("apenasModelosBandaAtivos") @Required final boolean apenasModelosBandaAtivos) throws ProLogException {
        return service.getModelosBanda(tokenIntegracao, codMarcaBanda, apenasModelosBandaAtivos);
    }
}
