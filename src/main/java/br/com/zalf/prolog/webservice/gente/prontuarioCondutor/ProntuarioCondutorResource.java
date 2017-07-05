package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zart on 03/07/2017.
 */
@Path("/prontuarios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProntuarioCondutorResource {

    ProntuarioCondutorService service = new ProntuarioCondutorService();

    @GET
    @Path("/{cpf}")
    @Secured
    public ProntuarioCondutor getProntuario(@PathParam("cpf") Long cpf) {
        return service.getProntuario(cpf);
    }
}
