package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.ResponseIntervalo;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 08/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@DebugLog
@Path("/controle-jornada-marcacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        targetVersionCode = 60,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class ControleJornadaResource {
    @NotNull
    private final ControleJornadaService service = new ControleJornadaService();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = AuthType.BASIC, considerOnlyActiveUsers = false)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @HeaderParam(ProLogCustomHeaders.APP_VERSION_ANDROID_APP) Integer versaoApp,
            IntervaloMarcacao intervaloMarcacao) {
        return service.insertMarcacaoIntervalo(versaoDadosIntervalo, intervaloMarcacao, versaoApp);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/em-andamento/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public IntervaloMarcacao getIntervaloAberto(@PathParam("codUnidade") Long codUnidade,
                                                @PathParam("cpf") Long cpf,
                                                @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Throwable {
        return service.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoInvervalo);
    }
}
