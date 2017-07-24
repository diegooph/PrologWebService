package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import com.google.common.base.Preconditions;
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
            @NotNull final IntegradorProLog integradorProLog) {

        Preconditions.checkNotNull(sistemaKey, "sistemaKey não pode ser null!");
        Preconditions.checkNotNull(integradorProLog, "integradorProLog não pode ser null!");

        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new AvaCorpAvilan(new AvaCorpAvilanRequesterImpl(), integradorProLog);
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}