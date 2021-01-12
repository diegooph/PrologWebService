package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/api/pneus/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcaModeloPneuResource {
    @NotNull
    private final ApiMarcaModeloPneuService service = new ApiMarcaModeloPneuService();

    @GET
    @LogRequest
    @Path("/marcas-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcaPneu> getMarcasPneu(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasMarcasPneuAtivas") @Required final boolean apenasMarcasPneuAtivas) throws ProLogException {
        return service.getMarcasPneu(tokenIntegracao, apenasMarcasPneuAtivas);
    }

    @GET
    @LogRequest
    @Path("/modelos-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiModeloPneu> getModelosPneu(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codMarcaPneu") @Required final Long codMarcaPneu,
            @QueryParam("apenasModelosPneuAtivos") @Required final boolean apenasModelosPneuAtivos) throws ProLogException {
        return service.getModelosPneu(tokenIntegracao, codMarcaPneu, apenasModelosPneuAtivos);
    }

    @GET
    @LogRequest
    @Path("/marcas-banda")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcaBanda> getMarcasBanda(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasMarcasBandaAtivas") @Required final boolean apenasMarcasBandaAtivas) throws ProLogException {
        return service.getMarcasBanda(tokenIntegracao, apenasMarcasBandaAtivas);
    }

    @GET
    @LogRequest
    @Path("/modelos-banda")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiModeloBanda> getModelosBanda(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codMarcaBanda") @Required final Long codMarcaBanda,
            @QueryParam("apenasModelosBandaAtivos") @Required final boolean apenasModelosBandaAtivos) throws ProLogException {
        return service.getModelosBanda(tokenIntegracao, codMarcaBanda, apenasModelosBandaAtivos);
    }
}
