package br.com.zalf.prolog.webservice.integracao.transport;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface IntegracaoTransportDao {

    void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                @NotNull final LocalDateTime dataHoraAtualUtc,
                                @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws Throwable;

    @NotNull
    List<ItemPendenteIntegracaoTransport> getItensPendentes(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoItemPendenteSincronizado) throws Throwable;
}