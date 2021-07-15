package br.com.zalf.prolog.webservice.v3.fleet.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoProcessoListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.CodUnidades;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Api(value = "Movimentação de Pneus")
public interface MovimentacaoProcessoApiDoc {
    @ApiOperation(
            value = "Lista as movimentações.",
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
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @CodUnidades final List<Long> codUnidades,
            @ApiParam(value = "Data Inicial - Utilizada para filtrar as movimentações realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-01-01",
                      required = true) @Required final String dataInicial,
            @ApiParam(value = "Data Final - Utilizada para filtrar as movimentações realizadas.",
                      format = "yyyy-MM-dd",
                      example = "2021-02-01",
                      required = true) @Required final String dataFinal,
            @ApiParam(value = "Código de Colaborador - Utilizado para filtrar as movimentações realizadas por um " +
                    "colaborador. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codColaborador,
            @ApiParam(value = "Código de Veículo - Utilizado para filtrar as movimentações de apenas um veículo. Caso" +
                    " não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codVeiculo,
            @ApiParam(value = "Código de Pneu - Utilizado para filtrar as movimentações de apenas um pneu. Caso" +
                    " não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codPneu,
            @Max(value = 1000, message = "O limite de busca é 1000 registros.")
            @ApiParam(value = "Limite de movimentações retornadas pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de movimentações. A partir de qual movimentação deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
