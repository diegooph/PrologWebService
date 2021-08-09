package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@RestController
@ConsoleDebugLog
@Path(ChecklistWorkOrderResource.RESOURCE_PATH)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ChecklistWorkOrderResource implements ChecklistWorkOrderApiDoc {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/checklists/ordens-servico";
    @NotNull
    private final ChecklistWorkOrderService service;
    @NotNull
    private final ChecklistWorkOrderMapper mapper;

    @Autowired
    public ChecklistWorkOrderResource(@NotNull final ChecklistWorkOrderService service,
                                      @NotNull final ChecklistWorkOrderMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GET
    @Secured(authTypes = {AuthType.BEARER, AuthType.API},
             permissions = {Pilares.Frota.OrdemServico.Checklist.VISUALIZAR})
    @Override
    public List<ChecklistWorkOrderDto> getAllWorkOrders(
            @QueryParam("codUnidades") @Required final List<Long> branchesId,
            @QueryParam("codTipoVeiculo") @Optional final Long vehicleTypeId,
            @QueryParam("codVeiculo") @Optional final String vehicleId,
            @QueryParam("statusOrdemServico") @Optional final StatusOrdemServico workOrderStatus,
            @QueryParam("incluirItensOrdemServico") @DefaultValue("true") final boolean includeWorkOrderItems,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final List<ChecklistWorkOrderProjection> workOrders =
                service.getAllWorkOrders(branchesId,
                                         vehicleTypeId,
                                         vehicleId,
                                         workOrderStatus,
                                         includeWorkOrderItems,
                                         limit,
                                         offset);
        return mapper.toDto(workOrders, includeWorkOrderItems);
    }
}
