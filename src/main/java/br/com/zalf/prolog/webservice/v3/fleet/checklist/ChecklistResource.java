package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistFilter;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@RestController
@ConsoleDebugLog
@Path(ChecklistResource.RESOURCE_PATH)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource implements ChecklistApiDoc {
    @NotNull
    public static final String RESOURCE_PATH = "/api/v3/checklists";
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistMapper checklistMapper;

    @Autowired
    public ChecklistResource(@NotNull final ChecklistService checklistService,
                             @NotNull final ChecklistMapper checklistMapper) {
        this.checklistService = checklistService;
        this.checklistMapper = checklistMapper;
    }

    @GET
    @Secured(authTypes = {AuthType.BEARER, AuthType.API},
             permissions = {
                     Pilares.Frota.Checklist.VISUALIZAR_TODOS,
                     Pilares.Frota.Checklist.REALIZAR})
    @Override
    public List<ChecklistDto> getAllChecklists(
            @QueryParam("codUnidades") @Required final List<Long> branchesId,
            @QueryParam("dataInicial") @Required final String startDate,
            @QueryParam("dataFinal") @Required final String endDate,
            @QueryParam("codColaborador") @Optional final Long userId,
            @QueryParam("codTipoVeiculo") @Optional final Long vehicleTypeId,
            @QueryParam("codVeiculo") @Optional final Long vehicleId,
            @QueryParam("incluirRespostas") @DefaultValue("true") final boolean includeAnswers,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final ChecklistFilter filter = ChecklistFilter.of(branchesId,
                                                          DateUtils.parseDate(startDate),
                                                          DateUtils.parseDate(endDate),
                                                          userId,
                                                          vehicleId,
                                                          vehicleTypeId,
                                                          includeAnswers,
                                                          limit,
                                                          offset);
        final List<ChecklistProjection> checklists = checklistService.getAllChecklists(filter);
        return checklistMapper.toDto(checklists, includeAnswers);
    }
}
