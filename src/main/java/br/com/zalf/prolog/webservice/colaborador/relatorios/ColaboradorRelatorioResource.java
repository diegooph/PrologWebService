package br.com.zalf.prolog.webservice.colaborador.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@Path("/colaboradores/relatorios")
@Secured(permissions = Pilares.Gente.Relatorios.LISTAGEM_COLABORADORES)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 55,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class ColaboradorRelatorioResource {

    @GET
    @Path("/listagem-colaboradores-by-unidade/csv")
    @Produces("application/csv")
    public StreamingOutput getCronogramaAfericoesPlacasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) {
        return outputStream -> new ColaboradorRelatorioService()
                .getListagemColaboradoresByUnidadeCsv(outputStream, codUnidades, userToken);
    }

    @GET
    @Path("/listagem-colaboradores-by-unidade/report")
    public Report getCronogramaAfericoesPlacasReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return new ColaboradorRelatorioService()
                .getListagemColaboradoresByUnidadeReport(codUnidades, userToken);
    }
}
