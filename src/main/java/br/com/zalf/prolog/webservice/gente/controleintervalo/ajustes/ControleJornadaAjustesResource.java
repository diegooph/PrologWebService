package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
public class ControleJornadaAjustesResource {

    @NotNull
    final ControleJornadaAjustesService service = new ControleJornadaAjustesService();

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Secured(authTypes = {AuthType.BEARER, AuthType.BASIC}, permissions = Pilares.Gente.Intervalo.MARCAR_INTERVALO)
    @Path("/abertos/{codUnidade}/{cpf}/{codTipoIntervalo}")
    public IntervaloMarcacao getIntervaloAberto(@PathParam("codUnidade") Long codUnidade,
                                                @PathParam("cpf") Long cpf,
                                                @PathParam("codTipoIntervalo") Long codTipoInvervalo) throws Exception {
        return null;
    }
}
