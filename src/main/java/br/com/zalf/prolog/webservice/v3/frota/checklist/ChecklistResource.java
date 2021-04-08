package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistGetDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */

@Controller
@ConsoleDebugLog
@Path("/v3/checklist")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource implements ChecklistResourceApiDoc {
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistGetMapper checklistGetMapper;

    @Autowired
    public ChecklistResource(final @NotNull ChecklistService checklistService,
                             final @NotNull ChecklistGetMapper checklistGetMapper) {
        this.checklistService = checklistService;
        this.checklistGetMapper = checklistGetMapper;
    }

    @Override
    @ApiExposed
    @GET
    @Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
    @Path("/")
    public List<ChecklistGetDto> getChecklists(@NotNull @QueryParam("codUnidades") final List<Long> codUnidades,
                                               @QueryParam("codColaborador") @Optional final Long codColaborador,
                                               @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
                                               @QueryParam("codVeiculo") @Optional final Long codVeiculo,
                                               @QueryParam("incluirRespostas") @DefaultValue("true") final boolean incluirRespostas,
                                               @QueryParam("dataInicial") final @NotNull String dataInicial,
                                               @QueryParam("dataFinal") final @NotNull String dataFinal,
                                               @QueryParam("limit") final int limit /*max 1000*/,
                                               @QueryParam("offset") final long offset) {
        return checklistGetMapper.toDto(checklistService.getChecklists(codUnidades,
                                                                       codColaborador,
                                                                       codTipoVeiculo,
                                                                       codVeiculo,
                                                                       incluirRespostas,
                                                                       dataInicial,
                                                                       dataFinal,
                                                                       limit,
                                                                       offset));
    }
}
