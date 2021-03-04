package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * Created on 03/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("/v2/api/marcacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcacaoDummyResource {

    @GET
    @LogRequest
    @Path("tipos-marcacoes-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiTipoMarcacao> getTipoMarcacoes(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("apenasTiposMarcacoesAtivos") @Required final boolean apenasTiposMarcacoesAtivos)
            throws ProLogException {
        return Collections.singletonList(ApiTipoMarcacao.getDummy());
    }

    @GET
    @LogRequest
    @Path("marcacoes-realizadas-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacao> getMarcacoesRealizadas(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimaMarcacaoSincronizada") @Required final Long codUltimaMarcacaoSincronizada)
            throws ProLogException {
        return Collections.singletonList(ApiMarcacao.getDummy());
    }

    @GET
    @LogRequest
    @Path("ajustes-realizados-dummy")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("codUltimoAjusteMarcacaoSincronizado") @Required final Long codUltimoAjusteMarcacaoSincronizado)
            throws ProLogException {
        return Collections.singletonList(ApiAjusteMarcacao.getDummy());
    }
}
