package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
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
@Path("/v3/checklists/ordens-servicos")
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
    @Override
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR})
    public List<ChecklistOrdemServicoListagemDto> getOrdensServico(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("codTipoVeiculo") final Long codTipoVeiculo,
            @QueryParam("codVeiculo") final String codVeiculo,
            @QueryParam("statusOrdemServico") final StatusOrdemServico statusOrdemServico,
            @QueryParam("incluirItensOrdemServico") final boolean incluirItensOrdemServico,
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
