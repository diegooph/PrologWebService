package br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.fleet.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.IdBranches;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Ordens de Serviço")
public interface ChecklistOrdemServicoApiDoc {
    @ApiOperation(
            value = "Lista as ordens de serviço abertas e fechadas.",
            response = ChecklistOrdemServicoListagemDto.class,
            responseContainer = "List")
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
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true)
            @Required @IdBranches final List<Long> codUnidades,
            @ApiParam(value = "Código de Tipo Veículo - Utilizado para filtrar ordens de serviço de apenas um tipo de" +
                    " veículo. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codTipoVeiculo,
            @ApiParam(value = "Código de Veículo - Utilizado para filtrar ordens de serviço de apenas um veículo. " +
                    "Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final String codVeiculo,
            @ApiParam(value = "Status da ordem de serviço. Podendo ser ABERTA ou FECHADA. Utilizado para filtrar " +
                    "ordens de serviço de um status específico. Caso não deseje filtrar, basta não enviar esse " +
                    "parâmetro.",
                      example = "F")
            @Optional final StatusOrdemServico statusOrdemServico,
            @ApiParam(value = "Flag utilizada para retornar os itens das ordens de serviço. Por padrão é sempre " +
                    "retornado, para não retornar envie 'false'.",
                      required = true,
                      defaultValue = "true") final boolean incluirItensOrdemServico,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de ordens de serviço retornados pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de ordens de serviço. A partir de qual checklist deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
