package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@RestController
@ConsoleDebugLog
@Path(TireSizeResource.RESOURCE_PATH)
public class TireSizeResource {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/tires/sizes";
    @NotNull
    private final TireSizeService service;

    @Autowired
    public TireSizeResource(@NotNull final TireSizeService service) {
        this.service = service;
    }

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    public SuccessResponse insert(@Context final SecurityContext securityContext,
                                  @Valid final TireSizeCreation tireSizeCreation) throws Throwable {

        return new SuccessResponse(
                service.insert(tireSizeCreation, (ColaboradorAutenticado) securityContext.getUserPrincipal()).getId(),
                "Registro inserido com sucesso!"
        );
    }
}
