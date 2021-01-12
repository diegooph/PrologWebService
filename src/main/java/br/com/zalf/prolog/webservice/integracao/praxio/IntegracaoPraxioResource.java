package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/integracoes/praxio")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class IntegracaoPraxioResource {
    @NotNull
    private final IntegracaoPraxioService service = new IntegracaoPraxioService();

////----------------------------------------------------------------------------------------------------------------////
////-------------------------- VALIDAÇÃO DE TOKEN PARA A INTEGRÇÃO -------------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @GET
    @LogRequest
    @Path("/validate-token")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao validateTokenIntegracao(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao)
            throws ProLogException {
        return service.validateTokenIntegracao(tokenIntegracao);
    }

////----------------------------------------------------------------------------------------------------------------////
////-------------------------- INTEGRAÇÃO DE CADASTRO DE VEÍCULOS --------------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @POST
    @LogRequest
    @Path("/veiculo/cadastro")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        return service.inserirVeiculoPraxio(tokenIntegracao, veiculoCadastroPraxio);
    }

    @PUT
    @LogRequest
    @Path("/veiculo/edicao")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUnidadeVeiculoAntesEdicao") @Required final Long codUnidadeVeiculoAntesEdicao,
            @QueryParam("placaVeiculoAntesEdicao") @Required final String placaVeiculoAntesEdicao,
            @Required final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        return service.atualizarVeiculoPraxio(
                tokenIntegracao,
                codUnidadeVeiculoAntesEdicao,
                placaVeiculoAntesEdicao,
                veiculoEdicaoPraxio);
    }

    @POST
    @LogRequest
    @Path("/veiculo/transferencia")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao transferirVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws ProLogException {
        return service.transferirVeiculoPraxio(tokenIntegracao, veiculoTransferenciaPraxio);
    }

    @DELETE
    @LogRequest
    @Path("/veiculo/ativar-desativar")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao ativarDesativarVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("placaVeiculo") @Required final String placaVeiculo,
            @QueryParam("veiculoAtivo") @Required final Boolean veiculoAtivo) throws ProLogException {
        return service.ativarDesativarVeiculoPraxio(tokenIntegracao, placaVeiculo, veiculoAtivo);
    }

////----------------------------------------------------------------------------------------------------------------////
////----------------------------------- INTEGRAÇÃO DE PNEUS --------------------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @GET
    @LogRequest
    @Path("/afericoes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) throws ProLogException {
        return service.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
    }

////----------------------------------------------------------------------------------------------------------------////
////----------------------------------- INTEGRAÇÃO DE ORDENS DE SERVIÇO --------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @POST
    @LogRequest
    @Path("/ordens-servico/itens-pendentes")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirOrdensServicoGlobus(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        return service.inserirOrdensServicoGlobus(tokenIntegracao, ordensServicoAbertas);
    }

    @POST
    @LogRequest
    @Path("/ordens-servico/resolver-multiplos-itens")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItens(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
    }
}