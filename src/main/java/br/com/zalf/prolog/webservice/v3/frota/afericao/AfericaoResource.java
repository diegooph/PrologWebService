package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Controller
@ConsoleDebugLog
@Path("/v3/afericoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class AfericaoResource {
    @NotNull
    private final AfericaoService service;

    @Autowired
    public AfericaoResource(@NotNull final AfericaoService service) {
        this.service = service;
    }

    @GET
    @NotNull
    @Path("/{codigo}")
    @Secured(permissions = {Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA})
    public AfericaoEntity getByCodigo(@PathParam("codigo") @NotNull final Long codigo) {
        return service.getByCodigo(codigo);
    }
}
