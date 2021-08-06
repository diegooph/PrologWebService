package br.com.zalf.prolog.webservice.v3.fleet;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@RestController
@ConsoleDebugLog
@Path(TireSizeResource.RESOURCE_PATH)
public class TireSizeResource {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/tires/sizes";

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    public SuccessResponse insert(
            @Valid final TireSizeCreation tireSizeCreation) throws Throwable {
        return new SuccessResponse(1L, "Deu bom!");
    }
}
