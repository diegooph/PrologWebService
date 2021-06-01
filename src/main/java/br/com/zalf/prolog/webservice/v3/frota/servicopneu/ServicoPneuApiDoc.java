package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;
import io.swagger.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Api(value = "Serviços de pneus")
public interface ServicoPneuApiDoc {

    @ApiOperation(
            value = "Lista serviços de pneus abertos e fechados.",
            response = ServicoPneuListagemDto.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = ServicoPneuListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<ServicoPneuListagemDto> getServicosByUnidadeAndStatus(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) final @NotNull List<Long> codUnidades,
            @ApiParam(value = "Status do servico.",
                      allowEmptyValue = true,
                      example = "ABERTO",
                      allowableValues = "ABERTO, FECHADO") final @Nullable ServicoPneuStatus status,
            @ApiParam(value = "Código do veiculo.",
                      allowEmptyValue = true) final @Nullable Long codVeiculo,
            @ApiParam(value = "Código do pneu.",
                      allowEmptyValue = true) final @Nullable Long codPneu,
            @ApiParam(value = "limite de ordens de serviço retornados pela busca. O valor máximo é 1000.",
                      example = "1000",
                      required = true) final int limit,
            @ApiParam(value = "Offset de ordens de serviço. A partir de qual checklist deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}
