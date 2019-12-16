package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.SocorroRotaAbertura;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Path("socorro-rota")
@DebugLog
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class SocorroRotaResource {
    @NotNull
    private SocorroRotaService service = new SocorroRotaService();

    /**
     * Resource para realizar a abertura de uma solicitação de socorro.
     */
    @POST
    @Secured(permissions = Pilares.Frota.SocorroRota.SOLICITAR_SOCORRO)
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/abertura")
    public ResponseWithCod aberturaSocorro(@Required final SocorroRotaAbertura socorroRotaAbertura) {
        return service.aberturaSocorro(socorroRotaAbertura);
    }
}