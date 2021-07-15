package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistListagemDto;
import br.com.zalf.prolog.webservice.v3.validation.IdBranches;
import io.swagger.annotations.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Api(value = "Checklists")
public interface ChecklistResourceApiDoc {
    @ApiOperation(
            value = "Lista checklists realizados.",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200, message = "Operação efetuada com sucesso.",
                    response = ChecklistListagemDto.class,
                    responseContainer = "List"),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    List<ChecklistListagemDto> getChecklistsListagem(
            @ApiParam(value = "Lista de códigos de unidade.",
                      example = "215",
                      required = true) @Required @IdBranches final List<Long> codUnidades,
            @ApiParam(value = "Data Inicial - Utilizada para filtrar os checklists realizados.",
                      format = "yyyy-MM-dd",
                      example = "2021-01-01",
                      required = true) @Required final String dataInicial,
            @ApiParam(value = "Data Final - Utilizada para filtrar os checklists realizados.",
                      format = "yyyy-MM-dd",
                      example = "2021-02-01",
                      required = true) @Required final String dataFinal,
            @ApiParam(value = "Código de Colaborador - Utilizado para filtrar checklists realizados por um " +
                    "colaborador. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codColaborador,
            @ApiParam(value = "Código de Tipo Veículo - Utilizado para filtrar checklists de apenas um tipo de " +
                    "veículo. Caso não deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codTipoVeiculo,
            @ApiParam(value = "Código de Veículo - Utilizado para filtrar checklists de apenas um veículo. Caso não " +
                    "deseje filtrar, basta não enviar esse parâmetro.")
            @Optional final Long codVeiculo,
            @ApiParam(value = "Flag utilizada para retornar as respostas preenchidas pelo colaborador. Por padrão é " +
                    "sempre retornado, para não retornar envie 'false'.",
                      required = true,
                      defaultValue = "true") final boolean incluirRespostas,
            @Max(value = 100, message = "O limite de busca é 100 registros.")
            @ApiParam(value = "Limite de checklists retornados pela busca. O valor máximo é 100.",
                      example = "100",
                      required = true) final int limit,
            @ApiParam(value = "Offset de checklists. A partir de qual checklist deve-se começar a busca.",
                      example = "0",
                      required = true) final int offset);
}