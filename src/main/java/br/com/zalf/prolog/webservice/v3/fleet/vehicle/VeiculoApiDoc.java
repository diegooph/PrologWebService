package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.BranchesId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Gestão de Veículos")
public interface VeiculoApiDoc {
    @ApiOperation(value = "Insere um veículo.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso.", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    SuccessResponse insert(
            @ApiParam(hidden = true) final String tokenIntegracao,
            @ApiParam(value = "Dados do veículo para inserir.",
                      required = true) @Valid final VeiculoCadastroDto veiculoCadastroDto) throws Throwable;

    @ApiOperation(value = "Lista os veículos.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = VeiculoListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<VeiculoListagemDto> getListagemVeiculos(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @BranchesId final List<Long> codUnidades,
            @ApiParam(value = "Flag que controla se veículos inativos serão retornados. ",
                      defaultValue = "true",
                      required = true) @Required final boolean incluirInativos,
            @ApiParam(value = "Limite de veículos retornadas pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de veículos. A partir de qual veículo deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
