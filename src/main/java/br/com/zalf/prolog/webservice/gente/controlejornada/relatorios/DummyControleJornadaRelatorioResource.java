package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 09/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyControleJornadaRelatorioResource {

    @GET
    @Secured
    @Path("/intervalos/relatorios/folha-ponto-jornada")
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio() {
        return null;
    }
}
