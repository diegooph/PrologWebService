package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemFiltro;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistProjection;
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
@Path("api/v3/checklists")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource implements ChecklistResourceApiDoc {
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistListagemMapper checklistListagemMapper;

    @Autowired
    public ChecklistResource(@NotNull final ChecklistService checklistService,
                             @NotNull final ChecklistListagemMapper checklistListagemMapper) {
        this.checklistService = checklistService;
        this.checklistListagemMapper = checklistListagemMapper;
    }

    @Override
    @GET
    @Path("/")
    public List<ChecklistListagemDto> getChecklistsListagem(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("codColaborador") @Optional final Long codColaborador,
            @QueryParam("codVeiculo") @Optional final Long codVeiculo,
            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
            @QueryParam("incluirRespostas") @DefaultValue("true") final boolean incluirRespostas,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        final ChecklistListagemFiltro checklistListagemFiltro =
                ChecklistListagemFiltro.of(codUnidades,
                                           DateUtils.parseDate(dataInicial),
                                           DateUtils.parseDate(dataFinal),
                                           codColaborador,
                                           codVeiculo,
                                           codTipoVeiculo,
                                           incluirRespostas,
                                           limit,
                                           offset);
        final List<ChecklistProjection> projections = checklistService.getChecklistsListagem(checklistListagemFiltro);
        return checklistListagemMapper.toDto(projections, incluirRespostas);
    }
}
