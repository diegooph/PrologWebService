package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAtivacaoInativacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/controle-jornada/ajustes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ControleJornadaAjusteResource {

    @NotNull
    final ControleJornadaAjusteService service = new ControleJornadaAjusteService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/adicionar-marcacao-avulsa")
    public Response adicionarMarcacaoAjuste(@HeaderParam("Authorization") String userToken,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws ProLogException {
        return service.adicionarMarcacaoAjuste(userToken, marcacaoAjuste);
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/adicionar-marcacao-inicio-fim")
    public Response adicionarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws ProLogException {
        return service.adicionarMarcacaoAjusteInicioFim(userToken, marcacaoAjuste);
    }

    @DELETE
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/ativar-desativar-marcacao")
    public Response ativarInativarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final MarcacaoAjusteAtivacaoInativacao marcacaoAjuste) throws ProLogException {
        return service.ativarInativarMarcacaoAjuste(userToken, marcacaoAjuste);
    }

}
