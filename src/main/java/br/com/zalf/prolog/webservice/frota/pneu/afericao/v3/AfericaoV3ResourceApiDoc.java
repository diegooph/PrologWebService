package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Aferições")
public interface AfericaoV3ResourceApiDoc {
    @ApiOperation(
            value = "Obtém uma lista de aferições realizadas.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = AfericaoPlacaDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<AfericaoPlacaDto> getAfericoesPlacas(
            @ApiParam(value = "Uma lista de códigos de unidade.",
                      required = true) @NotNull final List<Long> codUnidades,
            @ApiParam(value = "Uma placa de veículo específica para ser buscada. Se nenhuma for informada, todas " +
                    "placas serão trazidas.") @Nullable final String placaVeiculo,
            @ApiParam(value = "Um código de tipo veículo específico para ser buscado. Se nenhum for informado, todos " +
                    "os tipos serão trazidos.") @Nullable final Long codTipoVeiculo,
            @ApiParam(value = "Uma data inicial, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataInicial,
            @ApiParam(value = "Uma data final, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataFinal,
            @ApiParam(value = "Um limite de registros a serem retornados.") final int limit,
            @ApiParam(value = "Um deslocamento para realizar a paginação.") final int offset);

    List<AfericaoAvulsaDto> getAfericoesAvulsas(@NotNull final List<Long> codUnidades,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal,
                                                final int limit,
                                                final int offset);
}
