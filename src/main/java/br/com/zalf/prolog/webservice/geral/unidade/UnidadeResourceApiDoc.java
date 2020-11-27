package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoDto;
import io.swagger.annotations.*;

import java.util.List;

/**
 * Created on 2020-10-06
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Api(value = "Gestão de Unidades")
public interface UnidadeResourceApiDoc {
    @ApiOperation(value = "Atualiza as informações de uma unidade.", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso."),
            @ApiResponse(code = 401, message = "Operação não autorizada"),
            @ApiResponse(code = 404, message = "Operação não encontrada"),
            @ApiResponse(code = 500, message = "Erro ao executar operação")
    })
    SuccessResponse updateUnidade(
            @ApiParam(
                    value = "Um json contendo informações de uma unidade para atualizá-la.",
                    required = true) final UnidadeEdicao unidade);

    @ApiOperation(value = "Método depreciado para atualização de unidades.", hidden = true)
    Response updateUnidadeOld(final UnidadeEdicao unidade);

    @ApiOperation(
            value = "Busca as informações de uma unidade através de um código específico.",
            response = UnidadeVisualizacaoDto.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = UnidadeVisualizacaoDto.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    UnidadeVisualizacaoDto getUnidadeByCodigo(
            @ApiParam(value = "O código da unidade que será buscada.", required = true) final Long codUnidade);

    @ApiOperation(
            value = "Lista informações das unidades de uma empresa possibilitando filtrar por regional.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = UnidadeVisualizacaoDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<UnidadeVisualizacaoDto> getUnidadesListagem(
            @ApiParam(value = "Um código de empresa existente.", required = true) final Long codEmpresa,
            @ApiParam(value = "Um ou mais códigos de regional, existentes." +
                    "<br><b>Importante:</b> Se nenhum código for informado, será realizada a busca por todas as " +
                    "regionais da empresa.") final List<Long> codigosRegionais);
}
