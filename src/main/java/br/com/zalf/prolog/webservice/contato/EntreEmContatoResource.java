package br.com.zalf.prolog.webservice.contato;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/comercial/mensagens")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class EntreEmContatoResource {
    @NotNull
    private final EntreEmContatoService service = new EntreEmContatoService();

    @POST
    public AbstractResponse insert(@NotNull final MensagemContato contato) throws ProLogException {
        return service.insertNovoContato(contato);
    }
}