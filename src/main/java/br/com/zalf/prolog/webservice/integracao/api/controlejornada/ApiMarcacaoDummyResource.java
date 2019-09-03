package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.model.ApiMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao.model.ApiTipoMarcacao;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * Created on 03/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/marcacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ApiMarcacaoDummyResource {

    @GET
    @LogIntegracaoRequest
    @Path("tipos-marcacoes-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiTipoMarcacao> getTipoMarcacoes(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasTiposMarcacoesAtivos") @Required final boolean apenasTiposMarcacoesAtivos)
            throws ProLogException {
        return Collections.singletonList(ApiTipoMarcacao.getDummy());
    }

    @GET
    @LogIntegracaoRequest
    @Path("marcacoes-realizadas-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacao> getMarcacoesRealizadas(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaMarcacaoSincronizada") @Required final Long codUltimaMarcacaoSincronizada)
            throws ProLogException {
        return Collections.singletonList(ApiMarcacao.getDummy());
    }

    @GET
    @LogIntegracaoRequest
    @Path("ajustes-realizados-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoAjusteMarcacaoSincronizado") @Required final Long codUltimoAjusteMarcacaoSincronizado)
            throws ProLogException {
        return Collections.singletonList(ApiAjusteMarcacao.getDummy());
    }
}
