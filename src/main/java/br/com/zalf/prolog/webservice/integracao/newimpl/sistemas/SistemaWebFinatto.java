package br.com.zalf.prolog.webservice.integracao.newimpl.sistemas;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.newimpl.AfericaoIntegrada;
import br.com.zalf.prolog.webservice.integracao.newimpl.RequestIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.webfinatto.SistemaWebFinattoOld;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequesterImpl;
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
public final class SistemaWebFinatto implements SistemaIntegrado, AfericaoIntegrada {
    @NotNull
    private final RequestIntegrado request;

    @NotNull
    @Override
    public SistemaKey getKey() {
        return SistemaKey.WEB_FINATTO;
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        return getImpl().insertAfericao(codUnidade, afericao, deveAbrirServico);
    }

    @NotNull
    private SistemaWebFinattoOld getImpl() {
        final String requestToken = request.getRequestToken();
        return new SistemaWebFinattoOld(new SistemaWebFinattoRequesterImpl(),
                                        getKey(),
                                        RecursoIntegrado.AFERICAO,
                                        IntegradorProLog.full(requestToken),
                                        requestToken);
    }
}
