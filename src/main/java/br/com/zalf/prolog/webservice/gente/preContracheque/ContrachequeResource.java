package br.com.zalf.prolog.webservice.gente.preContracheque;

import br.com.zalf.prolog.gente.pre_contracheque.Contracheque;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zalf on 23/11/16.
 */
@Path("/contracheque")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ContrachequeResource {

    private ContrachequeService service = new ContrachequeService();

    @GET
    @Secured
    @Android
    @Path("/{codUnidade}/{cpf}/{ano}/{mes}")
    public Contracheque getPreContracheque(@PathParam("cpf") Long cpf,
                                           @PathParam("codUnidade") Long codUnidade,
                                           @PathParam("ano") int ano,
                                           @PathParam("mes") int mes){
        return service.getPreContracheque(cpf, codUnidade, ano, mes);
    }
}
