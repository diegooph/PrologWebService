package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuStatus;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.validation.BranchesId;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Serviços de pneus", hidden = true)
public interface TireMaintenanceApiDoc {

    @ApiOperation(
            value = "Lista serviços de pneus abertos e fechados.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = TireMaintenanceDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<TireMaintenanceDto> getAllTireMaintenance(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @BranchesId final List<Long> branchesId,
            @ApiParam(value = "Status do serviço - Utilizado para filtrar serviços abertos ou fechados. Caso não " +
                    "deseje filtrar, basta não enviar esse parâmetro.",
                      example = "ABERTO",
                      allowableValues = "ABERTO, FECHADO") @Optional final ServicoPneuStatus maintenanceStatus,
            @ApiParam(value = "Código do veículo - Utilizado para filtrar serviços de pneus em um veículo específico." +
                    " Caso não deseje filtrar, basta não enviar esse parâmetro.") @Optional final Long vehicleId,
            @ApiParam(value = "Código do pneu - Utilizado para filtrar serviços de um pneu específico. Caso não " +
                    "deseje filtrar, basta não enviar esse parâmetro.") @Optional final Long tireId,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de serviços de pneus retornados pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de serviços de pneus. A partir de qual serviço deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
