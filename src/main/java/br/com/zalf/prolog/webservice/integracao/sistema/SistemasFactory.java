package br.com.zalf.prolog.webservice.integracao.sistema;

import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public final class SistemasFactory {

    private SistemasFactory() {
        throw new IllegalStateException(SistemasFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    public static Sistema createSistema(@NotNull final String sistemaKey) {
        Preconditions.checkNotNull(sistemaKey, "sistemaKey não pode ser null!");

        switch (sistemaKey) {
            case "AVACORP_AVILAN":
                return new AvaCorpAvilan();
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey);
        }
    }
}