package br.com.zalf.prolog.webservice.gente.controleintervalo.justificativa_ajuste;

import br.com.zalf.prolog.webservice.DummyResource;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 06/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyJustificativaAjusteResource extends DummyResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/justificativas-ajustes-list")
    @Secured
    public List<JustificativaAjuste> getJustificativasAjuste() {
        ensureDebugEnvironment();
        final List<JustificativaAjuste> justificativas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            justificativas.add(JustificativaAjuste.createDummy());
        }
        return justificativas;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/justificativa-ajuste")
    @Secured
    public JustificativaAjuste getJustificativaAjuste() {
        ensureDebugEnvironment();
        return JustificativaAjuste.createDummy();
    }
}
