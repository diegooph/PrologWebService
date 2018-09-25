package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/controle-jornada/justificativa-ajuste")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class JustificativaAjusteResource {

    @NotNull
    final JustificativaAjusteService service = new JustificativaAjusteService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Secured()
    @Path("/adicionar")
    public AbstractResponse adicionarMarcacaoAjuste(
            @HeaderParam("Authorization") String userToken,
            @NotNull final JustificativaAjuste justificativaAjuste) throws ProLogException {
        return service.insertJustificativaAjuste(userToken, justificativaAjuste);
    }
}
