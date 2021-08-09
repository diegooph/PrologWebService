package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.authorization.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementProcessEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovimentProcessDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-04-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@RestController
@ConsoleDebugLog
@Path(TireMovementProcessResource.RESOURCE_PATH)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class TireMovementProcessResource implements TireMovementProcessApiDoc {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/movimentacoes";
    @NotNull
    private final TireMovementProcessService service;
    @NotNull
    private final TireMovementMapper mapper;

    @Autowired
    public TireMovementProcessResource(@NotNull final TireMovementProcessService service,
                                       @NotNull final TireMovementMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GET
    @Secured(authTypes = {AuthType.BEARER, AuthType.API},
             permissions = {
                     Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
                     Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
                     Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Override
    public List<TireMovimentProcessDto> getAllTireMovements(
            @QueryParam("codUnidades") @Required final List<Long> branchesId,
            @QueryParam("dataInicial") @Required final String startDate,
            @QueryParam("dataFinal") @Required final String endDate,
            @QueryParam("codColaborador") @Optional final Long userId,
            @QueryParam("codVeiculo") @Optional final Long vehicleId,
            @QueryParam("codPneu") @Optional final Long tireId,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final List<TireMovementProcessEntity> tireMovements =
                service.getAllTireMovements(branchesId, startDate, endDate, userId, vehicleId, tireId, limit, offset);
        return mapper.toDto(tireMovements);
    }
}
