package br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@ConsoleDebugLog
@Path("/api/v3/inspections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RestController
public class InspectionResource implements InspectionApiDoc {
    @NotNull
    private final InspectionService service;
    @NotNull
    private final InspectionMapper inspectionMapper;

    @Autowired
    InspectionResource(@NotNull final InspectionService service,
                       @NotNull final InspectionMapper inspectionMapper) {
        this.service = service;
        this.inspectionMapper = inspectionMapper;
    }

    @GET
    @Path("/vehicles")
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Override
    public List<VehicleInspectionDto> getVehicleInspections(
            @QueryParam("branchesId") @NotNull final List<Long> branchesId,
            @QueryParam("initialDate") @NotNull final String initialDate,
            @QueryParam("finalDate") @NotNull final String finalDate,
            @QueryParam("vehicleTypeId") @Optional final Long vehicleTypeId,
            @QueryParam("vehicleId") @Optional final Long vehicleId,
            @QueryParam("includeMeasures") @DefaultValue("true") final boolean includeMeasures,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final VehicleInspectionFilter filter = VehicleInspectionFilter.of(branchesId,
                                                                          vehicleId,
                                                                          vehicleTypeId,
                                                                          DateUtils.parseDate(initialDate),
                                                                          DateUtils.parseDate(finalDate),
                                                                          limit,
                                                                          offset,
                                                                          includeMeasures);
        final List<VehicleInspectionProjection> vehicleInspections = service.getVehicleInspections(filter);
        return inspectionMapper.toVehicleInspectionDto(vehicleInspections);
    }

    @GET
    @Path("/tires")
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Override
    public List<TireInspectionDto> getTireInspections(
            @QueryParam("branchesId") @NotNull final List<Long> branchesId,
            @QueryParam("initialDate") @NotNull final String initialDate,
            @QueryParam("finalDate") @NotNull final String finalDate,
            @QueryParam("includeMeasures") @DefaultValue("true") final boolean includeMeasures,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final TireInspectionFilter filter = TireInspectionFilter.of(branchesId,
                                                                    DateUtils.parseDate(initialDate),
                                                                    DateUtils.parseDate(finalDate),
                                                                    limit,
                                                                    offset,
                                                                    includeMeasures);
        final List<TireInspectionProjection> tireInspections = service.getTireInspections(filter);
        return inspectionMapper.toTireInspectionDto(tireInspections);
    }
}
