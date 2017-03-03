package br.com.zalf.prolog.webservice.errorhandling.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by luiz on 03/03/17.
 */
public class ResourceAlreadyDeletedException extends WebApplicationException {
    private static final String DEFAULT_MESSAGE = "O recurso já foi deletado ou nunca esteve disponível";

    public ResourceAlreadyDeletedException() {
        super(Response.status(Response.Status.GONE).entity(DEFAULT_MESSAGE).build());
    }
}