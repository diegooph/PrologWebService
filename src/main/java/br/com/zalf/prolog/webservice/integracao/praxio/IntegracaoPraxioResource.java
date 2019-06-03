package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
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
@DebugLog
@Path("/integracoes/praxio")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class IntegracaoPraxioResource {
    @NotNull
    private final IntegracaoPraxioService service = new IntegracaoPraxioService();

////----------------------------------------------------------------------------------------------------------------////
////----------------------------------- INTEGRAÇÃO DE PNEUS --------------------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @GET
    @LogIntegracaoRequest
    @Path("/afericoes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) throws ProLogException {
        return service.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
    }

    @GET
    @LogIntegracaoRequest
    @Path("/afericoes/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadasDummies(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) {
        return service.getDummy();
    }

////----------------------------------------------------------------------------------------------------------------////
////----------------------------------- INTEGRAÇÃO DE ORDENS DE SERVIÇO --------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servico/itens-pendentes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirOrdensServicoGlobus(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        return service.inserirOrdensServicoGlobus(tokenIntegracao, ordensServicoAbertas);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servico/resolver-multiplos-itens")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItens(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servico/itens-pendentes/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirOrdensServicoGlobusDummy(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        return service.inserirOrdensServicoGlobusDummy(tokenIntegracao, ordensServicoAbertas);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/ordens-servicos/resolver-multiplos-itens/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItensDummy(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItensDummy(tokenIntegracao, itensResolvidos);
    }
}