package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class ApiPneuAlteracaoStatus {
    @NotNull
    private final ApiStatusPneu statusPneu;
    @NotNull
    private final Long codigoPneuSistemaIntegrado;
    @NotNull
    private final String codigoPneuCliente;
    @NotNull
    private final Long codUnidadePneu;
    @NotNull
    private final String cpfColaboradorAlteracaoStatus;
    @NotNull
    private final LocalDateTime dataHoraAlteracaoStatusUtc;
    private final boolean trocouDeBanda;
    @Nullable
    private final Long codNovoModeloBanda;
    @Nullable
    private final BigDecimal valorNovaBandaPneu;

    public ApiPneuAlteracaoStatus(@NotNull final ApiStatusPneu statusPneu,
                                  @NotNull final Long codigoPneuSistemaIntegrado,
                                  @NotNull final String codigoPneuCliente,
                                  @NotNull final Long codUnidadePneu,
                                  @NotNull final String cpfColaboradorAlteracaoStatus,
                                  @NotNull final LocalDateTime dataHoraAlteracaoStatusUtc,
                                  final boolean trocouDeBanda,
                                  @Nullable final Long codNovoModeloBanda,
                                  @Nullable final BigDecimal valorNovaBandaPneu) {
        this.codigoPneuSistemaIntegrado = codigoPneuSistemaIntegrado;
        this.codigoPneuCliente = codigoPneuCliente;
        this.codUnidadePneu = codUnidadePneu;
        this.cpfColaboradorAlteracaoStatus = cpfColaboradorAlteracaoStatus;
        this.dataHoraAlteracaoStatusUtc = dataHoraAlteracaoStatusUtc;
        this.statusPneu = statusPneu;
        this.trocouDeBanda = trocouDeBanda;
        this.codNovoModeloBanda = codNovoModeloBanda;
        this.valorNovaBandaPneu = valorNovaBandaPneu;
    }

    @NotNull
    public ApiStatusPneu getStatusPneu() {
        return statusPneu;
    }

    @NotNull
    public Long getCodigoPneuSistemaIntegrado() {
        return codigoPneuSistemaIntegrado;
    }

    @NotNull
    public String getCodigoPneuCliente() {
        return codigoPneuCliente;
    }

    @NotNull
    public Long getCodUnidadePneu() {
        return codUnidadePneu;
    }

    @NotNull
    public String getCpfColaboradorAlteracaoStatus() {
        return cpfColaboradorAlteracaoStatus;
    }

    @NotNull
    public LocalDateTime getDataHoraAlteracaoStatusUtc() {
        return dataHoraAlteracaoStatusUtc;
    }

    public boolean isTrocouDeBanda() {
        return trocouDeBanda;
    }

    @Nullable
    public Long getCodNovoModeloBanda() {
        return codNovoModeloBanda;
    }

    @Nullable
    public BigDecimal getValorNovaBandaPneu() {
        return valorNovaBandaPneu;
    }
}
