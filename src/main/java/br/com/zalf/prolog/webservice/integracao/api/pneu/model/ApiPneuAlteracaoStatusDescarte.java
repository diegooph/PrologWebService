package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuAlteracaoStatusDescarte extends ApiPneuAlteracaoStatus {
    public ApiPneuAlteracaoStatusDescarte(@NotNull final Long codigoPneuSistemaIntegrado,
                                          @NotNull final String codigoPneuCliente,
                                          @NotNull final Long codUnidadePneu,
                                          @NotNull final String cpfColaboradorAlteracaoStatus,
                                          @NotNull final LocalDateTime dataHoraAlteracaoStatus) {
        super(ApiStatusPneu.DESCARTE,
                codigoPneuSistemaIntegrado,
                codigoPneuCliente,
                codUnidadePneu,
                cpfColaboradorAlteracaoStatus,
                dataHoraAlteracaoStatus);
    }
}
