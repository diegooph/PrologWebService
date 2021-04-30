package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Movimentações")
public interface MovimentacaoProcessoApiDoc {
    @ApiOperation(
            value = "Lista os processos de movimentação e suas informações.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = MovimentacaoProcessoListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<MovimentacaoProcessoListagemDto> getListagemMovimentacoes(
            @ApiParam(value = "Uma lista de código de unidades que serão usadas para filtrar as movimentações.",
                      required = true) @Required final List<Long> codUnidades,
            @ApiParam(value = "Uma data inicial para o filtro de faixa de quando as movimentações foram realizadas, " +
                    "no formato yyyy-MM-dd.", required = true) @Required final String dataInicial,
            @ApiParam(value = "Uma data final para o filtro de faixa de quando as movimentações foram realizadas, no " +
                    "formato yyyy-MM-dd.", required = true) @Required final String dataFinal,
            @ApiParam(value = "Um código de colaborador especifico que tenha realizado a movimentação.")
            @Optional final Long codColaborador,
            @ApiParam(value = "Um código de veículo no qual a movimentação ocorreu.") @Optional final Long codVeiculo,
            @ApiParam(value = "Um código de pneu no qual a movimentação ocorreu.") @Optional final Long codPneu,
            @ApiParam(value = "Um limite de registros a serem retornados, no máximo 1000.",
                      required = true) @Max(value = 1000,
                                            message = "O limite pode ser no máximo 1000.") final int limit,
            @ApiParam(value = "Um index de página, começando em 0.",
                      required = true) final int offset);
}
