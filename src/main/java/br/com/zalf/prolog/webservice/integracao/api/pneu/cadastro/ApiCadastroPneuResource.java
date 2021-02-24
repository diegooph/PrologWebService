package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.*;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/v2/api/cadastro/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiCadastroPneuResource {
    @NotNull
    private final ApiCadastroPneuService service = new ApiCadastroPneuService();

    @PUT
    @LogRequest
    @Path("/carga-inicial-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiPneuCargaInicialResponse> inserirCargaInicialPneu(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ApiPneuCargaInicial> pneusCargaInicial) throws ProLogException {
        return service.inserirCargaInicialPneu(tokenIntegracao, pneusCargaInicial);
    }

    @POST
    @LogRequest
    @Path("/cadastro-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirPneuCadastro(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuCadastro pneuCadastro) throws ProLogException {
        return service.inserirPneuCadastro(tokenIntegracao, pneuCadastro);
    }

    @PUT
    @LogRequest
    @Path("/edicao-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarPneuEdicao(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuEdicao pneuEdicao) throws ProLogException {
        return service.atualizarPneuEdicao(tokenIntegracao, pneuEdicao);
    }

    @POST
    @LogRequest
    @Path("/transferencia-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao transferirPneu(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuTransferencia pneuTransferencia) throws ProLogException {
        return service.transferirPneu(tokenIntegracao, pneuTransferencia);
    }
}
