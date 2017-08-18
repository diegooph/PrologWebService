package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public final class SistemasFactory {

    private SistemasFactory() {
        throw new IllegalStateException(SistemasFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    public static Sistema createSistema(
            @NotNull final SistemaKey sistemaKey,
            @NotNull final IntegradorProLog integradorProLog,
            @NotNull final String userToken) {

        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new AvaCorpAvilan(new AvaCorpAvilanRequesterImpl(), integradorProLog, userToken);
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}