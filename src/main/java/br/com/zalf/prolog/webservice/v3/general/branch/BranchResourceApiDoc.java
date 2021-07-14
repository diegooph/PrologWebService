package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeVisualizacaoListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.BranchId;
import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created on 2020-10-06
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Api(value = "Gestão de Unidades")
public interface BranchResourceApiDoc {
    @ApiOperation(value = "Atualiza informações de uma unidade.",
                  notes = "Se tratando de sobrescrita, caso alguma propriedade não for fornecida, assumiremos null. " +
                          "\nPara sobrescrever apenas uma propriedade envie as demais contendo o valor original. " +
                          "\nPara remover uma propriedade, envie null e as demais contendo o valor original.",
                  response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso.", response = Response.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    SuccessResponse updateUnidade(
            @ApiParam(value = "Dados da unidade para atualizar.",
                      required = true) @Valid final UnidadeEdicaoDto unidade);

    @ApiOperation(
            value = "Lista uma unidade específica.",
            response = UnidadeVisualizacaoListagemDto.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = UnidadeVisualizacaoListagemDto.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    UnidadeVisualizacaoListagemDto getUnidadeByCodigo(
            @ApiParam(value = "Código da unidade.",
                      required = true,
                      example = "215") @BranchId final Long codUnidade);

    @ApiOperation(
            value = "Lista as unidades de uma empresa.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = UnidadeVisualizacaoListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<UnidadeVisualizacaoListagemDto> getUnidadesListagem(
            @ApiParam(value = "Código de empresa.", required = true, example = "10") @CompanyId final Long codEmpresa,
            @ApiParam(value = "Lista de códigos de grupos - Utilizado para filtrar unidades de grupos específicos. " +
                    "Caso não deseje filtrar, basta não enviar esse parâmetro.",
                      example = "1") final List<Long> codGrupos);
}
