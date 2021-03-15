package br.com.zalf.prolog.webservice.frota.pneu.v3;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastro;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Controller
@ConsoleDebugLog
@Path("/v3/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PneuV3Resource implements PneuV3ApiDoc {

    @POST
    @Override
    @NotNull
    public Response insert(@NotNull final PneuCadastro pneuCadastro) {
        throw new NotImplementedException("metodo não implementado até o momento.");
    }
}
