package br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
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
@Path("api/v3/checklists/ordens-servico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class ChecklistOrdemServicoResource implements ChecklistOrdemServicoApiDoc {
    @NotNull
    private final ChecklistOrdemServicoService service;
    @NotNull
    private final ChecklistOrdemServicoMapper mapper;

    @Autowired
    public ChecklistOrdemServicoResource(@NotNull final ChecklistOrdemServicoService service,
                                         @NotNull final ChecklistOrdemServicoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Checklist.VISUALIZAR})
    @ApiExposed
    @Override
    public List<ChecklistOrdemServicoListagemDto> getOrdensServico(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
            @QueryParam("codVeiculo") @Optional final String codVeiculo,
            @QueryParam("statusOrdemServico") @Optional final StatusOrdemServico statusOrdemServico,
            @QueryParam("incluirItensOrdemServico") @DefaultValue("true") final boolean incluirItensOrdemServico,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return mapper.toDto(
                service.getOrdensServico(codUnidades,
                                         codTipoVeiculo,
                                         codVeiculo,
                                         statusOrdemServico,
                                         incluirItensOrdemServico,
                                         limit,
                                         offset),
                incluirItensOrdemServico);
    }
}
