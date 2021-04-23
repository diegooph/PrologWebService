package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Api(value = "Gestão de Frota")
public interface ChecklistResourceApiDoc {
    @ApiOperation(value = "Método utilizado para buscar checklists realizados.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso."),
            @ApiResponse(code = 401, message = "Operação não autorizada"),
            @ApiResponse(code = 404, message = "Operação não encontrada"),
            @ApiResponse(code = 500, message = "Erro ao executar operação")
    })
    List<ChecklistListagemDto> getChecklistsListagem(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                     @QueryParam("dataInicial") @Required final String dataInicial,
                                                     @QueryParam("dataFinal") @Required final String dataFinal,
                                                     @QueryParam("codColaborador") @Optional final Long codColaborador,
                                                     @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
                                                     @QueryParam("codVeiculo") @Optional final Long codVeiculo,
                                                     @QueryParam("incluirRespostas") final boolean incluirRespostas,
                                                     @QueryParam("limit") final int limit,
                                                     @QueryParam("offset") final int offset);
}