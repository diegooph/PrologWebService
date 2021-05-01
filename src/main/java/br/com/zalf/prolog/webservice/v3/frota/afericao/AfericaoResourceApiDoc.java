package br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoPlacaDto;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Aferições")
public interface AfericaoResourceApiDoc {
    @ApiOperation(
            value = "Obtém uma lista de aferições de placas realizadas.",
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
            @ApiParam(value = "Um código de veículo específico para ser buscado. Se nenhum for informado, todos " +
                    "os veículos da unidade serão retornados.") @Optional final Long codVeiculo,
            @ApiParam(value = "Um código de tipo veículo específico para ser buscado. Se nenhum for informado, todos " +
                    "os tipos serão trazidos.") @Optional final Long codTipoVeiculo,
            @ApiParam(value = "Uma data inicial, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataInicial,
            @ApiParam(value = "Uma data final, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataFinal,
            @ApiParam(value = "Um limite de registros a serem retornados.") final int limit,
            @ApiParam(value = "Um deslocamento para realizar a paginação.") final int offset,
            @ApiParam(value = "Uma variável para indicar se os valores de medidas devem ser " +
                    "retornados ou não") final boolean incluirMedidas);

    @ApiOperation(
            value = "Obtém uma lista de aferições avulsas (apenas pneus avulsos) realizadas.",
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
    List<AfericaoAvulsaDto> getAfericoesAvulsas(
            @ApiParam(value = "Uma lista de códigos de unidade.",
                      required = true) @NotNull final List<Long> codUnidades,
            @ApiParam(value = "Uma data inicial, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataInicial,
            @ApiParam(value = "Uma data final, a qual a aferição tenha sido realizadas.",
                      required = true) @NotNull final String dataFinal,
            @ApiParam(value = "Um limite de registros a serem retornados.") final int limit,
            @ApiParam(value = "Um deslocamento para realizar a paginação.") final int offset,
            @ApiParam(value = "Uma variável para indicar se os valores de medidas devem ser " +
                    "retornados ou não") final boolean incluirMedidas);
}
