package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreation;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeListing;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeStatusChange;
import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@RestController
@ConsoleDebugLog
@Path(TireSizeResource.RESOURCE_PATH)
public class TireSizeResource {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/tires/sizes";
    @NotNull
    private final TireSizeService service;
    @NotNull
    private final TireSizeMapper mapper;

    @Autowired
    public TireSizeResource(@NotNull final TireSizeService service, @NotNull final TireSizeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
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

    @GET
    @ApiExposed
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    public List<TireSizeListing> getAll(
            @NotNull @CompanyId @QueryParam("companyId") @Required final Long companyId,
            @QueryParam("status") final Boolean statusActive) {
        return mapper.toDto(service.getAll(companyId, statusActive));
    }

    @PATCH
    @Path("update-status")
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Pneu.ALTERAR)
    public SuccessResponse updateStatus(@Valid final TireSizeStatusChange tireSizeStatusChange) {
        service.updateStatus(tireSizeStatusChange);
        return new SuccessResponse(
                null,
                String.format(
                        "Tire size %s successfully!",
                        (tireSizeStatusChange.getActive() ? "enabled" : "disabled")));
    }
}
