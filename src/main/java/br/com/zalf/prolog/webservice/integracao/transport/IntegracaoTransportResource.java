package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/integracoes/fgt-sistemas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class IntegracaoTransportResource {
    @NotNull
    private final IntegracaoTransportService service = new IntegracaoTransportService();

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servicos/resolver-multiplos-itens")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItens(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
//        return service.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
        return null;
    }

    @GET
    @LogIntegracaoRequest
    @Path("/ordens-servicos/itens-pendentes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ItemPendenteIntegracaoTransport> getItensPendentes(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoItemPendenteSincronizado") @Required final Long codUltimoItemPendenteSincronizado)
            throws ProLogException {
//        return service.getItensPendentes(tokenIntegracao, codUltimoItemPendenteSincronizado);
        return null;
    }

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servicos/resolver-multiplos-itens/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItensDummies(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItensDummy(tokenIntegracao, itensResolvidos);
    }

    @GET
    @LogIntegracaoRequest
    @Path("/ordens-servicos/itens-pendentes/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ItemPendenteIntegracaoTransport> getItensPendentesDummies(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoItemPendenteSincronizado") @Required final Long codUltimoItemPendenteSincronizado)
            throws ProLogException {
        return service.getItensPendentesDummy(tokenIntegracao, codUltimoItemPendenteSincronizado);
    }
}