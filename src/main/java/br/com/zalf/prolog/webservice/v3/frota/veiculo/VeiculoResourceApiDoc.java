package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import io.swagger.annotations.*;

import javax.validation.Valid;

@Api(value = "Gestão de Veículos")
public interface VeiculoResourceApiDoc {
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
                      required = true) @Valid final VeiculoCadastroDto veiculoCadastroDto);
}
