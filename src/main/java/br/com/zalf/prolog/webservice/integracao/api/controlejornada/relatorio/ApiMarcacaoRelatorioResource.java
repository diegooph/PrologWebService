package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import br.com.zalf.prolog.webservice.integracao.logger.LogIntegracaoRequest;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/api/marcacoes/relatorios/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcacaoRelatorioResource {
    @NotNull
    private ApiMarcacaoRelatorioService service = new ApiMarcacaoRelatorioService();

    @GET
    @LogIntegracaoRequest
    @Path("padrao-portaria-1510")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @HeaderParam(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("codUnidadeProLog") @Nullable final Long codUnidadeProLog,
            @QueryParam("codTipoMarcacao") @Nullable final Long codTipoMarcacao,
            @QueryParam("cpfColaborador") @Nullable final String cpfColaborador)
            throws ProLogException {
        return service.getRelatorioPortaria1510(
                tokenIntegracao,
                dataInicial,
                dataFinal,
                codUnidadeProLog,
                codTipoMarcacao,
                cpfColaborador);
    }
}
