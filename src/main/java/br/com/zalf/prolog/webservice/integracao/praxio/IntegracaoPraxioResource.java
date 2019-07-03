package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
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
////-------------------------- INTEGRAÇÃO DE CADASTRO DE VEÍCULOS --------------------------------------------------////
////----------------------------------------------------------------------------------------------------------------////

    @POST
    @LogIntegracaoRequest
    @Path("/veiculo/cadastro/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirVeiculoPraxio(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        return service.inserirVeiculoCadastroPraxioDummy(tokenIntegracao, veiculoCadastroPraxio);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/veiculo/edição/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarVeiculoPraxio(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        return service.atualizarVeiculoPraxio(tokenIntegracao, veiculoEdicaoPraxio);
    }

    @DELETE
    @LogIntegracaoRequest
    @Path("/veiculo/ativar-desativar/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarVeiculoPraxio(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("placaVeiculo") @Required final String placaVeiculo,
            @QueryParam("veiculoAtivo") @Required final Boolean veiculoAtivo) throws ProLogException {
        if (placaVeiculo == null || placaVeiculo.isEmpty() || placaVeiculo.length() > 7) {
            throw new GenericException(
                    "O parâmetro 'placaVeiculo' não pode ser vazia nem conter mais que 7 caracteres");
        }
        if (veiculoAtivo == null) {
            throw new GenericException(
                    "O parâmetro 'veiculoAtivo' deve ser um valor VERDADEIRO ou FALSO");
        }
        final String msg = veiculoAtivo ? "ativado" : "desativado";
        return new SuccessResponseIntegracao("Veículo do Globus " + msg + " no ProLog com sucesso");
    }

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