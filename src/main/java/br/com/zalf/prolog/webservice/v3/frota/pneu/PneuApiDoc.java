package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.CodUnidades;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Gestão de Pneus")
public interface PneuApiDoc {
    @ApiOperation(value = "Insere um pneu.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso.", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    @NotNull
    SuccessResponse insert(@ApiParam(hidden = true) final String tokenIntegracao,
                           @ApiParam(value = "Flag que controla se o Prolog deve aplicar validações no DOT do pneu. " +
                                   "Um DOT válido contém 4 caracteres numéricos, correspondentes à semana e ano de " +
                                   "fabricação do pneu.",
                                     required = true,
                                     defaultValue = "true") final boolean ignoreDotValidation,
                           @ApiParam(value = "Dados do pneu para inserir.",
                                     required = true) @Valid final PneuCadastroDto pneuCadastro);

    List<PneuListagemDto> getPneusByStatus(@CodUnidades final List<Long> codUnidades,
                                           final StatusPneu statusPneu,
                                           final int limit,
                                           final int offset);
}