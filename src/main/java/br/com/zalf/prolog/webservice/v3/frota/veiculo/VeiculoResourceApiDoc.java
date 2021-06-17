package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoListagemDto;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

@Api(value = "Gestão de Veículos")
public interface VeiculoResourceApiDoc {
    @ApiOperation(value = "Insere um veículo.", response = SuccessResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operação efetuada com sucesso.", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Operação não autorizada", response = Response.class),
            @ApiResponse(code = 404, message = "Operação não encontrada", response = Response.class),
            @ApiResponse(code = 500, message = "Erro ao executar operação", response = Response.class)
    })
    SuccessResponse insert(
            @ApiParam(hidden = true) final String tokenIntegracao,
            @ApiParam(value = "Dados do veículo para inserir.",
                      required = true) @Valid final VeiculoCadastroDto veiculoCadastroDto);

    @Secured(permissions = {
            Pilares.Frota.Veiculo.VISUALIZAR,
            Pilares.Frota.Veiculo.ALTERAR,
            Pilares.Frota.Veiculo.CADASTRAR,
            Pilares.Frota.Checklist.VISUALIZAR_TODOS})
    List<VeiculoListagemDto> getListagemVeiculos(
            @QueryParam("codUnidades") @Required List<Long> codUnidades,
            @QueryParam("incluirInativos") @DefaultValue("true") boolean incluirInativos,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset);
}
