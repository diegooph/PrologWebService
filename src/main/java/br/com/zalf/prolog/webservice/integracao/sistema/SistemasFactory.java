package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.IntegradorHttpAvaCorpAvilan;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
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
            @NotNull final Integrador integradorDatabase) {

        Preconditions.checkNotNull(sistemaKey, "sistemaKey não pode ser null!");
        Preconditions.checkNotNull(integradorDatabase, "integradorDatabase não pode ser null!");

        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new AvaCorpAvilan(new IntegradorHttpAvaCorpAvilan(), integradorDatabase);
            default:
                throw new IllegalStateException("Nenhum sistema encontrado com a chave: " + sistemaKey.getKey());
        }
    }
}