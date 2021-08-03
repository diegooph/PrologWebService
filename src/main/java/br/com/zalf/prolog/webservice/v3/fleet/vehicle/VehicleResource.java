package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ConsoleDebugLog
@Path(VehicleResource.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Controller
public class VehicleResource implements VehicleApiDoc {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/veiculos";
    @NotNull
    private final VehicleService vehicleService;
    @NotNull
    private final VehicleMapper vehicleMapper;

    @Autowired
    public VehicleResource(@NotNull final VehicleService vehicleService, @NotNull final VehicleMapper vehicleMapper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
    }

    @POST
    @ApiExposed
    @Secured(permissions = Pilares.Frota.Veiculo.CADASTRAR)
    @Override
    public SuccessResponse insert(
            @HeaderParam(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO) @Optional final String integrationToken,
            @Valid final VehicleCreateDto vehicleCreateDto) throws Throwable {
        return vehicleService.insert(integrationToken, vehicleCreateDto);
    }

    @GET
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    @Override
    public List<VehicleDto> getAllVehicles(
            @QueryParam("codUnidades") @Required final List<Long> branchesId,
            @QueryParam("incluirInativos") @DefaultValue("true") final boolean includeInactive,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return vehicleMapper.toDto(vehicleService.getAllVehicles(branchesId, includeInactive, limit, offset));
    }
}
