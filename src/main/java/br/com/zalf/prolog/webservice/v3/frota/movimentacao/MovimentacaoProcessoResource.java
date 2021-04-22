package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created on 2021-04-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@RestController
@ConsoleDebugLog
@Path("/v3/movimentacoes")
public final class MovimentacaoProcessoResource {
    @NotNull
    private final MovimentacaoProcessoService service;

    @Autowired
    public MovimentacaoProcessoResource(@NotNull final MovimentacaoProcessoService service) {
        this.service = service;
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    public String getMovimentacoes() {
        return null;
    }
}
