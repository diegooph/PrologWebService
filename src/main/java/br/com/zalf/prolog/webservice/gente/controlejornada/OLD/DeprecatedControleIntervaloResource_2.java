package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.ResponseIntervalo;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.log.LogRequest;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/controle-intervalos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
public final class DeprecatedControleIntervaloResource_2 {
    @NotNull
    private final DeprecatedControleIntervaloService_2 service = new DeprecatedControleIntervaloService_2();

    /**
     * O motivo deste método não necessitar nem da permissão de marcacão de intervalo, é que se um colaborador que antes
     * tinha permissão passar a não ter mais, ele não poderia sincronizar possíveis intervalos que tenha no celular.
     * Por esse motivo, não pedimos permissão alguma. Para permitir que mesmo colaboradores que estejam inativos
     * também sincronizem seus intervalos setamos o considerOnlyActiveUsers para {@code false}.
     */
    @LogRequest
    @POST
    @UsedBy(platforms = Platform.ANDROID)
//    @Secured(authTypes = AuthType.BASIC, considerOnlyActiveUsers = false)
    public ResponseIntervalo insertIntervalo(
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) final long versaoDadosIntervalo,
            final IntervaloMarcacao intervaloMarcacao) {
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
            @HeaderParam(IntervaloOfflineSupport.HEADER_NAME_VERSAO_DADOS_INTERVALO) final long versaoDadosIntervalo,
            @PathParam("codUnidade") final Long codUnidade) {
        return service.getIntervaloOfflineSupport(versaoDadosIntervalo, codUnidade, new ColaboradorService());
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public IntervaloMarcacao getIntervaloAberto(@PathParam("codUnidade") final Long codUnidade,
                                                @PathParam("cpf") final Long cpf,
                                                @PathParam("codTipoIntervalo") final Long codTipoInvervalo) throws Throwable {
        return service.getUltimaMarcacaoInicioNaoFechada(codUnidade, cpf, codTipoInvervalo);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = {
            Pilares.Gente.Intervalo.MARCAR_INTERVALO,
            Pilares.Gente.Intervalo.ATIVAR_INATIVAR_TIPO_INTERVALO,
            Pilares.Gente.Intervalo.AJUSTE_MARCACOES,
            Pilares.Gente.Intervalo.VISUALIZAR_TODAS_MARCACOES})
    @Path("/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public List<Intervalo> getIntervalosColaborador(@PathParam("codUnidade") final Long codUnidade,
                                                    @PathParam("cpf") final Long cpf,
                                                    @PathParam("codTipoIntervalo") final String codTipo,
                                                    @QueryParam("limit") final long limit,
                                                    @QueryParam("offset") final long offset) {
        return service.getMarcacoesIntervaloColaborador(codUnidade, cpf, codTipo, limit, offset);
    }
}