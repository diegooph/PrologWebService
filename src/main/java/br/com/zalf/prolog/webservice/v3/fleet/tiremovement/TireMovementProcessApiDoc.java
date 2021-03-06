package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovimentProcessDto;
import br.com.zalf.prolog.webservice.v3.validation.BranchesId;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Movimentação de Pneus")
public interface TireMovementProcessApiDoc {
    @ApiOperation(
            value = "Lista as movimentações.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = TireMovimentProcessDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<TireMovimentProcessDto> getAllTireMovements(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @BranchesId final List<Long> branchesId,
            @ApiParam(value = "Data Inicial - Utilizada para filtrar as movimentações realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-01-01",
                      required = true) @Required final String startDate,
            @ApiParam(value = "Data Final - Utilizada para filtrar as movimentações realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-02-01",
                      required = true) @Required final String endDate,
            @ApiParam(value = "Código de Colaborador - Utilizado para filtrar as movimentações realizadas por um " +
                    "colaborador. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long userId,
            @ApiParam(value = "Código de Veículo - Utilizado para filtrar as movimentações de apenas um veículo. Caso" +
                    " não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long vehicleId,
            @ApiParam(value = "Código de Pneu - Utilizado para filtrar as movimentações de apenas um pneu. Caso" +
                    " não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long tireId,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de movimentações retornadas pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de movimentações. A partir de qual movimentação deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
