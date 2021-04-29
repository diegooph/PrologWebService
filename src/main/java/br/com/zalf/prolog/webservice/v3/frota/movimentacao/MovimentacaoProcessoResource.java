package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.ws.rs.GET;
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
@Path("/v3/movimentacoes")
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

    @Override
    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    public List<MovimentacaoProcessoListagemDto> getMovimentacoes(
            @QueryParam("codUnidades") @NotNull final List<Long> codUnidades,
            @QueryParam("codColaborador") @Optional final Long codColaborador,
            @QueryParam("codVeiculo") @Optional final Long codVeiculo,
            @QueryParam("codPneu") @Optional final Long codPneu,
            @QueryParam("dataInicial") @NotNull final String dataInicial,
            @QueryParam("dataFinal") @NotNull final String dataFinal,
            @QueryParam("limit") @Max(value = 1000, message = "O limite pode ser no máximo 1000.") final int limit,
            @QueryParam("offset") final int offset) {
        return mapper.toDto(service.getAll(codUnidades,
                                           codColaborador,
                                           codVeiculo,
                                           codPneu,
                                           dataInicial,
                                           dataFinal,
                                           limit,
                                           offset));
    }
}
