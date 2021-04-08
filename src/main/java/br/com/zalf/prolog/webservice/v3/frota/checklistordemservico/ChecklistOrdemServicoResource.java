package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
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
public final class ChecklistOrdemServicoResource {

    @GET
    public String getOrdensServico(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
            @QueryParam("codVeiculo") @Optional final String codVeiculo,
            @QueryParam("statusOrdemServico") @Optional final StatusOrdemServico statusOrdemServico,
            @QueryParam("incluirItensOrdemServico") @Optional @DefaultValue(
                    value = "true") final boolean incluirItensOrdemServico,
            @QueryParam("limit") @Max(value = 1000,
                                      message = "O limite máximo de registros por página é 1000.") final int limit,
            @QueryParam("offset") final int offset) {
        return null;
    }
}
