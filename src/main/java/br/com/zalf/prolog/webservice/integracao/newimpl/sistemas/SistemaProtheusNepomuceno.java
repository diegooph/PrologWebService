package br.com.zalf.prolog.webservice.integracao.newimpl.sistemas;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.newimpl.AfericaoIntegrada;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.SistemaProtheusNepomucenoOld;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.v3.CurrentRequest;
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
public final class SistemaProtheusNepomuceno implements SistemaIntegrado, AfericaoIntegrada {
    @NotNull
    private final CurrentRequest request;

    @NotNull
    @Override
    public SistemaKey getKey() {
        return SistemaKey.PROTHEUS_NEPOMUCENO;
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        return getImpl().insertAfericao(codUnidade, afericao, deveAbrirServico);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @NotNull
    private SistemaProtheusNepomucenoOld getImpl() {
        final String requestToken = request.getRequestToken().get();
        return new SistemaProtheusNepomucenoOld(new ProtheusNepomucenoRequesterImpl(),
                                                getKey(),
                                                RecursoIntegrado.AFERICAO,
                                                IntegradorProLog.full(requestToken),
                                                requestToken);
    }
}
