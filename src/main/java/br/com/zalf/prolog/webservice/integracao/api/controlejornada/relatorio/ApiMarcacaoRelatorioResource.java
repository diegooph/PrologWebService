package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.log.LogRequest;
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
@ConsoleDebugLog
@Path("/api/marcacoes/relatorios/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ApiMarcacaoRelatorioResource {
    @NotNull
    private final ApiMarcacaoRelatorioService service = new ApiMarcacaoRelatorioService();

    @GET
    @LogRequest
    @Path("padrao-portaria-1510-tipo-3")
    @UsedBy(platforms = Platform.INTEGRACOES)
    public List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Required final String tokenIntegracao,
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
