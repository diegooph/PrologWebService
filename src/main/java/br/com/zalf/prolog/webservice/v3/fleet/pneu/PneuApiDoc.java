package br.com.zalf.prolog.webservice.v3.fleet.pneu;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.pneu._model.PneuListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.IdBranches;
import io.swagger.annotations.*;

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
    SuccessResponse insert(@ApiParam(hidden = true) final String tokenIntegracao,
                           @ApiParam(value = "Flag que controla se o Prolog deve aplicar validações no DOT do pneu. " +
                                   "Um DOT válido contém 4 caracteres numéricos, correspondentes à semana e ano de " +
                                   "fabricação do pneu.",
                                     required = true,
                                     defaultValue = "true") final boolean ignoreDotValidation,
                           @ApiParam(value = "Dados do pneu para inserir.",
                                     required = true) @Valid final PneuCadastroDto pneuCadastro) throws Throwable;

    @ApiOperation(value = "Lista os pneus.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = PneuListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<PneuListagemDto> getPneusByStatus(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @IdBranches final List<Long> codUnidades,
            @ApiParam(value = "Status do pneu. Podendo ser EM_USO, ESTOQUE, DESCARTE ou ANALISE. Utilizado para " +
                    "filtrar pneus de um status específico. Caso não deseje filtrar, basta não enviar esse " +
                    "parâmetro.",
                      example = "EM_USO")
            @Optional final StatusPneu statusPneu,
            @ApiParam(value = "Limite de pneus retornadas pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de pneus. A partir de qual pneu deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}