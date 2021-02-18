package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.justificativa;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.debug.ResourceDebugOnly;

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
@Path("/v2/dummies")
@ConsoleDebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyJustificativaAjusteResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/justificativas-ajustes-list")
    public List<JustificativaAjuste> getJustificativasAjuste() {
        final List<JustificativaAjuste> justificativas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            justificativas.add(JustificativaAjuste.createDummy());
        }
        return justificativas;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/justificativa-ajuste")
    public JustificativaAjuste getJustificativaAjuste() {
        return JustificativaAjuste.createDummy();
    }
}