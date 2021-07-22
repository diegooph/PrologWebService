package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoInsercaoDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created on 2021-04-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@RestController
@ConsoleDebugLog
@Path("api/v3/movimentacoes")
public final class MovimentacaoProcessoResource implements MovimentacaoProcessoApiDoc {
    @NotNull
    private final MovimentacaoProcessoService service;
    @NotNull
    private final MovimentacaoProcessoMapper mapper;

    @Autowired
    public MovimentacaoProcessoResource(@NotNull final MovimentacaoProcessoService service,
                                        @NotNull final MovimentacaoProcessoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @POST
    public SuccessResponse insertProcessoMovimentacao(
            @NotNull final MovimentacaoProcessoInsercaoDto movimentacaoProcessoInsercaoDto) {
        return service.insertProcessoMovimentacao(mapper.toEntity(movimentacaoProcessoInsercaoDto));
    }

    @GET
    @ApiExposed
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Override
    public List<MovimentacaoProcessoListagemDto> getListagemMovimentacoes(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("codColaborador") @Optional final Long codColaborador,
            @QueryParam("codVeiculo") @Optional final Long codVeiculo,
            @QueryParam("codPneu") @Optional final Long codPneu,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) {
        return mapper.toDto(service.getListagemMovimentacoes(codUnidades,
                                                             dataInicial,
                                                             dataFinal,
                                                             codColaborador,
                                                             codVeiculo,
                                                             codPneu,
                                                             limit,
                                                             offset));
    }
}
