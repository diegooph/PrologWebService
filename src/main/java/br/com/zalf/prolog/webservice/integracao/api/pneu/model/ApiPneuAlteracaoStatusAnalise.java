package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuAlteracaoStatusAnalise extends ApiPneuAlteracaoStatus {
    public ApiPneuAlteracaoStatusAnalise() {
        super(ApiStatusPneu.ANALISE);
    }

    public ApiPneuAlteracaoStatusAnalise(@NotNull final Long codigoPneuSistemaIntegrado,
                                         @NotNull final String codigoPneuCliente,
                                         @NotNull final Long codUnidadePneu,
                                         @NotNull final String cpfColaboradorAlteracaoStatus,
                                         @NotNull final OffsetDateTime dataHoraAlteracaoStatus,
                                         final boolean trocouDeBanda,
                                         @Nullable final Long codNovoModeloBanda,
                                         @Nullable final BigDecimal valorNovaBandaPneu) {
        super(ApiStatusPneu.ANALISE,
                codigoPneuSistemaIntegrado,
                codigoPneuCliente,
                codUnidadePneu,
                cpfColaboradorAlteracaoStatus,
                dataHoraAlteracaoStatus,
                trocouDeBanda,
                codNovoModeloBanda,
                valorNovaBandaPneu);
    }
}
