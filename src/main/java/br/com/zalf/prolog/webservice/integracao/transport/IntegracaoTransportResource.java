package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/integracoes/fgt-sistemas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class IntegracaoTransportResource {
    @NotNull
    private final IntegracaoTransportService service = new IntegracaoTransportService();

    @POST
    @LogRequest
    @Path("/ordens-servicos/resolver-multiplos-itens")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItens(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
    }

    @GET
    @LogRequest
    @Path("/ordens-servicos/itens-pendentes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ItemPendenteIntegracaoTransport> getItensPendentes(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoItemPendenteSincronizado") @Required final Long codUltimoItemPendenteSincronizado)
            throws ProLogException {
        return service.getItensPendentes(tokenIntegracao, codUltimoItemPendenteSincronizado);
    }

    @POST
    @LogRequest
    @Path("/ordens-servicos/resolver-multiplos-itens/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItensDummies(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItensDummy(tokenIntegracao, itensResolvidos);
    }

    @GET
    @LogRequest
    @Path("/ordens-servicos/itens-pendentes/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ItemPendenteIntegracaoTransport> getItensPendentesDummies(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoItemPendenteSincronizado") @Required final Long codUltimoItemPendenteSincronizado)
            throws ProLogException {
        return service.getItensPendentesDummy(tokenIntegracao, codUltimoItemPendenteSincronizado);
    }
}