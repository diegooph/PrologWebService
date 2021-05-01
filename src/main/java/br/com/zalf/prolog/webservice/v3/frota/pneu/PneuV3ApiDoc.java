package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Gestão de Pneus")
public interface PneuV3ApiDoc {
    @ApiOperation(value = "Método utilizado para inserir um pneu.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso."),
            @ApiResponse(code = 401, message = "Operação não autorizada"),
            @ApiResponse(code = 404, message = "Operação não encontrada"),
            @ApiResponse(code = 500, message = "Erro ao executar operação")
    })
    @NotNull
    SuccessResponse insert(@ApiParam(hidden = true) final String tokenIntegracao,
                           final boolean ignoreDotValidation,
                           @Valid final PneuCadastroDto pneuCadastro);
}
