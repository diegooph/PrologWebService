package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.ChecklistOfflineSupport;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.ResponseChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.offline.model.ResponseChecklistWithCod;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 16/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/checklist-offline/")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistOfflineResource {
    @NotNull
    private final ChecklistOfflineService service = new ChecklistOfflineService();

    @POST
    @Secured(permissions = {})
    public ResponseChecklistWithCod insert(
            @HeaderParam(ChecklistOfflineSupport.HEADER_TOKEN_CHECKLIST) @Required final String tokenSincronizacao,
            @HeaderParam(ChecklistOfflineSupport.HEADER_VERSAO_DADOS_CHECKLIST) @Required final long versaoDadosChecklsitApp,
            @HeaderParam(ProLogCustomHeaders.APP_VERSION_ANDROID_APP) @Required final long versaoAppMomentoSincronizacao,
            @Required final ChecklistInsercao checklist) throws ProLogException {
        return service.insertChecklistOffline(
                tokenSincronizacao,
                versaoDadosChecklsitApp,
                versaoAppMomentoSincronizacao,
                checklist);
    }

    @GET
    @Path("offline-support/{codUnidade}")
    @Secured(permissions = {})
    public ChecklistOfflineSupport getChecklistOfflineSupport(
            @HeaderParam(ChecklistOfflineSupport.HEADER_VERSAO_DADOS_CHECKLIST) @Required final Long versaoDados,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("forcarAtualizacao") @Required final boolean forcarAtualizacao) throws ProLogException {
        return service.getChecklistOfflineSupport(versaoDados, codUnidade, forcarAtualizacao);
    }

    @GET
    @Path("offline-support/estado-dados/{codUnidade}")
    @Secured(permissions = {})
    public ResponseChecklist getEstadoDadosChecklistOffline(
            @HeaderParam(ChecklistOfflineSupport.HEADER_VERSAO_DADOS_CHECKLIST) @Required final Long versaoDados,
            @PathParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return service.getEstadoDadosChecklistOffline(versaoDados, codUnidade);
    }
}
