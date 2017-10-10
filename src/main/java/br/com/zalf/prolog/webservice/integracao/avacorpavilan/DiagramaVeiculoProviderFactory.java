package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 10/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class DiagramaVeiculoProviderFactory {

    private DiagramaVeiculoProviderFactory() {
        throw new IllegalStateException(DiagramaVeiculoProviderFactory.class.getSimpleName() +
                "cannot be instanciated!");
    }

    static DiagramaVeiculoProvider getDiagramaVeiculoProvider(@Nonnull final SistemaKey sistemaKey) {
        Preconditions.checkNotNull(sistemaKey, "sistemaKey não pode ser Null");

        switch (sistemaKey) {
            case AVACORP_AVILAN:
                return new DiagramaVeiculoProviderAvila();
            default:
                throw new IllegalStateException("Nenhum Provider encontrado com para a chave: " + sistemaKey.getKey());
        }
    }
}
