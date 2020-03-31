package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.DiagramaPosicaoMapeado;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.integracao.response.PosicaoPneuMepadoResponse;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
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
    private final ApiPneuService service = new ApiPneuService();

    @PUT
    @LogIntegracaoRequest
    @Path("/atualiza-status")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public SuccessResponseIntegracao atualizaStatusPneus(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws ProLogException {
        return service.atualizaStatusPneus(tokenIntegracao, pneusAtualizacaoStatus);
    }

    @POST
    @LogIntegracaoRequest
    @Path("/valida-posicoes-mapeadas")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<PosicaoPneuMepadoResponse> validaPosicoesVeiculo(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @Required final List<DiagramaPosicaoMapeado> diagramasPosicoes) throws ProLogException {
        return service.validaPosicoesVeiculo(tokenIntegracao, diagramasPosicoes);
    }
}
