package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Gestão de Pneus")
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
    List<MovimentacaoProcessoListagemDto> getMovimentacoes(
            @ApiParam(value = "Uma lista de código de unidades que serão usadas para filtras as movimentações.",
                      required = true) @NotNull final List<Long> codUnidades,
            @Optional final Long codColaborador,
            @Optional final Long codVeiculo,
            @Optional final Long codPneu,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            @Max(value = 1000, message = "O limite pode ser no máximo 1000.") final int limit,
            final int offset);
}
