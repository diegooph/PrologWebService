package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/controle-intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ControleIntervaloResource {

    private final ControleIntervaloService service = new ControleIntervaloService();

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
            IntervaloMarcacao intervaloMarcacao) {

        return service.insertMarcacaoIntervalo(versaoDadosIntervalo, intervaloMarcacao);
    }

    /**
     * Essa busca só é feita no app caso exista algum usuário logado, então podemos deixar o authType apenas como BEARER.
     */
    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = AuthType.BEARER, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/{codUnidade}/offline-support")
    public IntervaloOfflineSupport getIntervaloOfflineSupport(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) long versaoDadosIntervalo,
            @PathParam("codUnidade") Long codUnidade) {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public IntervaloMarcacao getIntervaloAberto(@PathParam("codUnidade") Long codUnidade,
                                                @PathParam("cpf") Long cpf,
                                                @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Exception {
        return service.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoInvervalo);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.EDITAR_MARCACAO,
            Pilares.Gente.Intervalo.VALIDAR_INVALIDAR_MARCACAO,
            Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public List<Intervalo> getIntervalosColaborador(@PathParam("codUnidade") Long codUnidade,
                                                    @PathParam("cpf") Long cpf,
                                                    @PathParam("codTipoIntervalo") String codTipo,
                                                    @QueryParam("limit") long limit,
                                                    @QueryParam("offset") long offset) {
        return service.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/tipos/{codUnidade}/completos")
    public List<TipoIntervalo> getTiposIntervalosCompletos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, true);
    }

    @POST
    @Path("/tipos")
    public AbstractResponse insertTipoIntervalo(TipoIntervalo tipoIntervalo) {
        return service.insertTipoIntervalo(tipoIntervalo);
    }

    @PUT
    @Path("/tipos")
    public Response updateTipoInvervalo(TipoIntervalo tipoIntervalo) {
        if(service.updateTipoIntervalo(tipoIntervalo)) {
            return Response.ok("Tipo de intervalo editado com sucesso");
        } else {
            return Response.error("Erro ao editar o tipo de intervalo");
        }
    }

    @PUT
    @Path("/tipos/inativar/{codUnidade}/{codTipoIntervalo}")
    public Response inativarTipoIntervalo(@PathParam("codUnidade") Long codUnidade,
                                          @PathParam("codTipoIntervalo") Long codTipoIntervalo) {
        if(service.inativarTipoIntervalo(codUnidade, codTipoIntervalo)) {
            return Response.ok("Tipo de intervalo inativado com sucesso");
        } else {
            return Response.error("Erro ao inativar o tipo de intervalo");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.CRIAR_TIPO_INTERVALO})
    @Path("/tipos/{codUnidade}/resumidos")
    public List<TipoIntervalo> getTiposIntervalosResumidos(@PathParam("codUnidade") Long codUnidade) {
        return service.getTiposIntervalos(codUnidade, false);
    }
}