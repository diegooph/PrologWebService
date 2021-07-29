package br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.TireInspectionDto;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.VehicleInspectionDto;
import br.com.zalf.prolog.webservice.v3.validation.BranchesId;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Aferições")
public interface InspectionApiDoc {
    @ApiOperation(
            value = "Lista as aferições de veículos.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = VehicleInspectionDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<VehicleInspectionDto> getVehicleInspections(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @NotNull @BranchesId final List<Long> branchesId,
            @ApiParam(value = "Data Inicial - Utilizada para filtrar as aferições realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-01-01",
                      required = true) @NotNull final String initialDate,
            @ApiParam(value = "Data Final - Utilizada para filtrar as aferições realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-02-01",
                      required = true) @NotNull final String finalDate,
            @ApiParam(value = "Código de Tipo Veículo - Utilizado para filtrar aferições de apenas um tipo de " +
                    "veículo. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long vehicleTypeId,
            @ApiParam(value = "Código de Veículo - Utilizado para filtar aferições de um veículo específico. " +
                    "Caso não deseje filtrar, basta não enviar esse parâmetro.") @Optional final Long vehicleId,
            @ApiParam(value = "Flag utilizada para retornar as medidas (altura de sulco e pressão) coletadas no " +
                    "processo de aferição. Por padrão é sempre retornado as medidas, para não retornar as medidas " +
                    "envie 'false'.",
                      required = true,
                      defaultValue = "true") final boolean includeMeasures,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de aferições retornados pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de aferições. A partir de qual aferição deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);

    @ApiOperation(
            value = "Lista as aferições avulsas.",
            notes = "Aferição Avulsa é uma processo realizado em pneus que não estão aplicados à veículos.",
            response = TireInspectionDto.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = VehicleInspectionDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<TireInspectionDto> getTireInspections(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @NotNull @BranchesId final List<Long> branchesId,
            @ApiParam(value = "Data Inicial - Utilizada para filtrar as aferições realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-01-01",
                      required = true) @NotNull final String initialDate,
            @ApiParam(value = "Data Final - Utilizada para filtrar as aferições realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-02-01",
                      required = true) @NotNull final String finalDate,
            @ApiParam(value = "Flag utilizada para retornar as medidas (altura de sulco e pressão) coletadas no " +
                    "processo de aferição. Por padrão é sempre retornado as medidas, para não retornar as medidas " +
                    "envie 'false'.",
                      required = true,
                      defaultValue = "true") final boolean includeMeasures,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de aferições retornados pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de aferições. A partir de qual aferição deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
