package br.com.zalf.prolog.webservice.integracao.newimpl.sistemas;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.newimpl.AfericaoIntegrada;
import br.com.zalf.prolog.webservice.integracao.newimpl.RequestIntegrado;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.SistemaRodoparHorizonteOld;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data.RodoparHorizonteRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-04-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SistemaRodoparHorizonte implements SistemaIntegrado, AfericaoIntegrada {
    @NotNull
    private final RequestIntegrado request;

    @NotNull
    @Override
    public SistemaKey getKey() {
        return SistemaKey.RODOPAR_HORIZONTE;
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        return getImpl().insertAfericao(codUnidade, afericao, deveAbrirServico);
    }

    @NotNull
    private SistemaRodoparHorizonteOld getImpl() {
        final String requestToken = request.getRequestToken();
        return new SistemaRodoparHorizonteOld(new RodoparHorizonteRequesterImpl(),
                                              IntegradorProLog.full(requestToken),
                                              getKey(),
                                              RecursoIntegrado.AFERICAO,
                                              requestToken);
    }
}
