package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import io.swagger.annotations.*;

import javax.validation.Valid;

@Api(value = "Gestão de Frota")
public interface VeiculoV3ResourceApiDoc {
    @ApiOperation(value = "Método utilizado para inserir um veículo.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso."),
            @ApiResponse(code = 401, message = "Operação não autorizada"),
            @ApiResponse(code = 404, message = "Operação não encontrada"),
            @ApiResponse(code = 500, message = "Erro ao executar operação")
    })
    SuccessResponse insert(
            @ApiParam(hidden = true) final String tokenIntegracao,
            @ApiParam(value = "Dados do veículo para inserir.",
                      required = true)
            @Valid final VeiculoCadastroDto veiculoCadastroDto);
}
