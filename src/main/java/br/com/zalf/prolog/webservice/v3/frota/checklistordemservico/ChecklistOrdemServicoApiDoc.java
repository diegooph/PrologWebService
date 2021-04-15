package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import javax.ws.rs.DefaultValue;
import java.util.List;

/**
 * Created on 2021-04-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Checklist")
public interface ChecklistOrdemServicoApiDoc {
    @ApiOperation(
            value = "Lista as ordens de serviço e possivelmente os itens das ordens de serviço por unidade.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = ChecklistOrdemServicoListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<ChecklistOrdemServicoListagemDto> getOrdensServico(
            @ApiParam(value = "Uma lista de código de unidades.", required = true) final List<Long> codUnidades,
            @ApiParam(value = "Um código de tipo de veículo.") final Long codTipoVeiculo,
            @ApiParam(value = "Um código de veículo.") final String codVeiculo,
            @ApiParam(value = "Um status de ordem de serviço.") final StatusOrdemServico statusOrdemServico,
            @ApiParam(value = "Indica se deve incluir os itens na ordem de serviço.") @DefaultValue(
                    value = "true") boolean incluirItensOrdemServico,
            @ApiParam(value = "Uma quantidade de ordens.") @Max(value = 1000,
                                                                message = "O limite máximo de registros por página é " +
                                                                        "1000.") final int limit,
            @ApiParam(value = "O index a partir do qual será contada a limitação.") final int offset);
}
