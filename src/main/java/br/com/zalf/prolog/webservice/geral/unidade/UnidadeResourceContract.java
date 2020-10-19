package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

/**
 * Created on 2020-10-06
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Api(value = "Gestão de Unidades")
public interface UnidadeResourceContract {

    @ApiOperation(value = "Atualização informações da Unidade", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso"),
            @ApiResponse(code = 401, message = "Operação não autorizada"),
            @ApiResponse(code = 404, message = "Operação não encontrada"),
            @ApiResponse(code = 500, message = "Erro ao executar operação")
    })
    Response updateUnidade(final UnidadeEdicao unidade);

    @ApiOperation(value = "Busca informações de uma Unidade", response = UnidadeVisualizacaoListagem.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso", response = UnidadeVisualizacaoListagem.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    UnidadeVisualizacaoListagem getUnidadeByCodigo(final Long codUnidade);

    @ApiOperation(value = "Lista informações das Unidades da Empresa", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso",
                    response = UnidadeVisualizacaoListagem.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<UnidadeVisualizacaoListagem> getUnidadesListagem(final Long codEmpresa,
                                                          final List<Long> codigosRegionais);
}
