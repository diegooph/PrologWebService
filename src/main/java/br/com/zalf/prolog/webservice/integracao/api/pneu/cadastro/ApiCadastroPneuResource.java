package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.*;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/cadastro/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiCadastroPneuResource {
    @NotNull
    private final ApiCadastroPneuService service = new ApiCadastroPneuService();

    @PUT
    @LogIntegracaoRequest
    @Path("/carga-inicial-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiPneuCargaInicialResponse> inserirCargaInicialPneu(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ApiPneuCargaInicial> pneusCargaInicial) throws ProLogException {
        return service.inserirCargaInicialPneu(tokenIntegracao, pneusCargaInicial);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/cadastro-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao inserirPneuCadastro(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuCadastro pneuCadastro) throws ProLogException {
        return service.inserirPneuCadastro(tokenIntegracao, pneuCadastro);
    }

    @PUT
    @LogIntegracaoRequest
    @Path("/edicao-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizarPneuEdicao(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuEdicao pneuEdicao) throws ProLogException {
        return service.atualizarPneuEdicao(tokenIntegracao, pneuEdicao);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/transferencia-pneu")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao transferirPneu(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final ApiPneuTransferencia pneuTransferencia) throws Throwable {
        return service.transferirPneu(tokenIntegracao, pneuTransferencia);
    }
}
