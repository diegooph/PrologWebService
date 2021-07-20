package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuStatus;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceFilter;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@ConsoleDebugLog
@Path("/api/v3/tire-maintenances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TireMaintenanceResource implements TireMaintenanceApiDoc {
    @NotNull
    private final ServicoPneuService service;
    @NotNull
    private final TireMaintenanceMapper mapper;

    @GET
    @ApiExposed
    @Secured(permissions = Pilares.Frota.OrdemServico.Pneu.VISUALIZAR)
    @Override
    public List<TireMaintenanceDto> getAllTireMaintenance(
            @QueryParam("branchesId") @Required final List<Long> branchesId,
            @QueryParam("maintenanceStatus") @Optional final ServicoPneuStatus maintenanceStatus,
            @QueryParam("vehicleId") @Optional final Long vehicleId,
            @QueryParam("tireId") @Optional final Long tireId,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final TireMaintenanceFilter filter =
                TireMaintenanceFilter.of(branchesId, maintenanceStatus, vehicleId, tireId, limit, offset);
        final List<ServicoPneuEntity> tireMaintenances = this.service.getAllTireMaintenance(filter);
        return mapper.toDto(tireMaintenances);
    }
}
