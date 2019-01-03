package br.com.zalf.prolog.webservice.integracao.transport;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ItensPendentesIntegracaoTransportConverter {

    public ItensPendentesIntegracaoTransportConverter() {
        throw new IllegalStateException(ItensPendentesIntegracaoTransportConverter.class.getSimpleName()
                + " cannot be instatiated!");
    }

    @NotNull
    static ItemPendenteIntegracaoTransport convert(@NotNull final ResultSet rSet) {
        final ItemPendenteIntegracaoTransport item = new ItemPendenteIntegracaoTransport();
        return item;
    }
}
