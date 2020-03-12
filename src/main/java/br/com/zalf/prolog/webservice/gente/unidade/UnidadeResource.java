package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@DebugLog
@Path("/unidade")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UnidadeResource {

    @GET
    @Secured
    @Path("/getByCodUnidade/{codUnidade}")
    public UnidadeVisualizacao getByCpf(@PathParam("codUnidade") final Long codUnidade) throws ProLogException {
        final UnidadeDaoImpl unDao = new UnidadeDaoImpl();
        try {
            return unDao.getUnidadeByCodUnidade(codUnidade);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
