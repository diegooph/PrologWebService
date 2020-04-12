package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Path("socorro-rota")
@DebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 101,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class SocorroRotaResource {
    @NotNull
    private final SocorroRotaService service = new SocorroRotaService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(permissions = Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/abertura/image-upload")
    public SuccessResponseSocorroRotaUploadImagem uploadImagemSocorroRotaAbertura(
            @FormDataParam("upload") @Required final InputStream fileInputStream,
            @FormDataParam("upload") @Required final FormDataContentDisposition fileDetail) {
        return service.uploadImagemSocorroRotaAbertura(fileInputStream, fileDetail);
    }

    /**
     * Resource para abrir uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO)
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/abertura")
    public ResponseWithCod aberturaSocorro(@Required final SocorroRotaAbertura socorroRotaAbertura) {
        return service.aberturaSocorro(socorroRotaAbertura);
    }

    /**
     * Resource para buscar as unidades disponíveis para a abertura de socorro por colaborador
     */
    @GET
    @Path("/abertura/unidades-selecao")
    public List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @QueryParam("codColaborador") @Required final Long codColaborador) {

        return service.getUnidadesDisponiveisAberturaSocorroByCodColaborador(codColaborador);
    }

    /**
     * Resource para buscar os veículos disponíveis para a abertura de socorro por unidade
     */
    @GET
    @Path("/abertura/veiculos-selecao")
    public List<VeiculoAberturaSocorro> getVeiculosDisponiveisAberturaSocorroByUnidade(
            @QueryParam("codUnidade") @Required final Long codUnidade) {

        return service.getVeiculosDisponiveisAberturaSocorroByUnidade(codUnidade);
    }

    /**
     * Resource para buscar uma lista de socorros em rota por data inicial, final e unidades
     */
    @GET
    @Secured(permissions = {
            Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO,
            Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
            Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/listagem")
    public List<SocorroRotaListagem> getListagemSocorroRota(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @HeaderParam("Authorization") @Required final String userToken) {
        return service.getListagemSocorroRota(codUnidades, dataInicial, dataFinal, userToken);
    }

    /**
     * Resource para invalidar uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO, Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/invalidacao")
    public ResponseWithCod invalidacaoSocorro(@Required final SocorroRotaInvalidacao socorroRotaInvalidacao) {
        return service.invalidacaoSocorro(socorroRotaInvalidacao);
    }

    /**
     * Resource para atender uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/atendimento")
    public ResponseWithCod atendimentoSocorro(@Required final SocorroRotaAtendimento socorroRotaAtendimento) {
        return service.atendimentoSocorro(socorroRotaAtendimento);
    }

    /**
     * Resource para iniciar um deslocamento no atendimento de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/atendimento/deslocamento/inicio")
    public Response iniciaDeslocamento(@Required final SocorroRotaAtendimentoDeslocamento deslocamentoInicio){
        return service.iniciaDeslocamento(deslocamentoInicio);
    }

    /**
     * Resource para finalizar um deslocamento no atendimento de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/atendimento/deslocamento/fim")
    public Response finalizaDeslocamento(@Required final SocorroRotaAtendimentoDeslocamento deslocamentoFim){
        return service.finalizaDeslocamento(deslocamentoFim);
    }


    /**
     * Resource para finalizar uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = {Pilares.Frota.SocorroRota.TRATAR_SOCORRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/finalizacao")
    public ResponseWithCod finalizacaoSocorro(@Required final SocorroRotaFinalizacao socorroRotaFinalizacao) {
        return service.finalizacaoSocorro(socorroRotaFinalizacao);
    }

    /**
     * Resource para visualizar uma solicitação de socorro.
     */
    @GET
    @Secured(permissions = {
            Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO,
            Pilares.Frota.SocorroRota.TRATAR_SOCORRO,
            Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/visualizacao")
    public SocorroRotaVisualizacao getVisualizacaoSocorroRota(
            @QueryParam("codSocorroRota") @Required final Long codSocorroRota) {
        return service.getVisualizacaoSocorroRota(codSocorroRota);
    }
}