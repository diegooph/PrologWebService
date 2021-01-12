package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturAutenticacaoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturMovimentacaoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 04/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/integracoes/praxio")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class IntegracaoPraxioResourceDummy {
    @NotNull
    private final IntegracaoPraxioService service = new IntegracaoPraxioService();

    @GET
    @LogRequest
    @Path("/validate-token/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao validateTokenIntegracao(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao)
            throws ProLogException {
        if (tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
            return new SuccessResponseIntegracao("Token validado com sucesso!");
        } else {
            throw new GenericException("Token inválido!");
        }
    }

    @POST
    @LogRequest
    @Path("/veiculo/cadastro/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        return service.inserirVeiculoCadastroPraxioDummy(tokenIntegracao, veiculoCadastroPraxio);
    }

    @PUT
    @LogRequest
    @Path("/veiculo/edicao/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUnidadeVeiculoAntesEdicao") @Required final Long codUnidadeVeiculoAntesEdicao,
            @QueryParam("placaVeiculoAntesEdicao") @Required final String placaVeiculoAntesEdicao,
            @Required final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        return service.atualizarVeiculoPraxioDummy(
                tokenIntegracao,
                codUnidadeVeiculoAntesEdicao,
                placaVeiculoAntesEdicao,
                veiculoEdicaoPraxio);
    }

    @POST
    @LogRequest
    @Path("/veiculo/transferencia/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao transferirVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws ProLogException {
        return service.transferirVeiculoPraxioDummy(tokenIntegracao, veiculoTransferenciaPraxio);
    }

    @DELETE
    @LogRequest
    @Path("/veiculo/ativar-desativar/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao ativarDesativarVeiculoPraxio(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
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

    @GET
    @LogRequest
    @Path("/afericoes/dummies")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadasDummies(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaAfericao") @Required final Long codUltimaAfericao) {
        return service.getAfericoesRealizadasDummy();
    }

    @POST
    @LogRequest
    @Path("/ordens-servico/itens-pendentes/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirOrdensServicoGlobusDummy(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        return service.inserirOrdensServicoGlobusDummy(tokenIntegracao, ordensServicoAbertas);
    }

    @POST
    @LogRequest
    @Path("/ordens-servicos/resolver-multiplos-itens/dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao resolverMultiplosItensDummy(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        return service.resolverMultiplosItensDummy(tokenIntegracao, itensResolvidos);
    }

    @GET
    @LogRequest
    @Path("/autenticacao-globus-sucesso")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public GlobusPiccoloturAutenticacaoResponse autenticaUsuarioGlobusDummy(
            @QueryParam("token") final String token,
            @QueryParam("shortCode") final Long shortCode) throws ProLogException {
        return new GlobusPiccoloturAutenticacaoResponse(
                true,
                "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck");
    }

    @POST
    @LogRequest
    @Path("/insert-movimentacao-sucesso")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public GlobusPiccoloturMovimentacaoResponse insertProcessoMovimentacao(
            @HeaderParam("authorization") @NotNull final String tokenIntegracao,
            final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws ProLogException {
        return new GlobusPiccoloturMovimentacaoResponse(true, new ArrayList<>(), null);
    }

    @GET
    @LogRequest
    @Path("/busca-locais-movimento-sucesso")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public GlobusPiccoloturLocalMovimentoResponse getLocaisMovimentoGlobus(
            @HeaderParam("authorization") @NotNull final String tokenIntegracao,
            @QueryParam("cpf") final String cpf) throws ProLogException {
        final List<GlobusPiccoloturLocalMovimento> locais = new ArrayList<>();
        locais.add(new GlobusPiccoloturLocalMovimento(5L, 1L, "LOCAL 5"));
        locais.add(new GlobusPiccoloturLocalMovimento(215L, 2L, "LOCAL 215"));
        locais.add(new GlobusPiccoloturLocalMovimento(103L, 3L, "LOCAL 103"));
        locais.add(new GlobusPiccoloturLocalMovimento(179L, 4L, "LOCAL 179"));
        return new GlobusPiccoloturLocalMovimentoResponse(true, "JOHN DOE", locais);
    }
}
