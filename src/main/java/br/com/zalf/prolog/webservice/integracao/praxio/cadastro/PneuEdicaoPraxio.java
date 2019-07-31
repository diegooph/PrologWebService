package br.com.zalf.prolog.webservice.integracao.praxio.cadastro;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Created on 29/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuEdicaoPraxio {
    @NotNull
    private final Long codigoPraxio;
    @NotNull
    private final String novoCodigoCliente;
    @NotNull
    private final Long novoCodModeloPneu;
    @NotNull
    private final Long novoCodDimensaoPneu;
    @NotNull
    private final String novoDotPneu;
    @NotNull
    private final BigDecimal novoValorPneu;
    @Nullable
    private final Long novoCodModeloBanda;
    @Nullable
    private final BigDecimal novoValorBandaPneu;

    public PneuEdicaoPraxio(@NotNull final Long codigoPraxio,
                            @NotNull final String novoCodigoCliente,
                            @NotNull final Long novoCodModeloPneu,
                            @NotNull final Long novoCodDimensaoPneu,
                            @NotNull final String novoDotPneu,
                            @NotNull final BigDecimal novoValorPneu,
                            @Nullable final Long novoCodModeloBanda,
                            @Nullable final BigDecimal novoValorBandaPneu) {
        this.codigoPraxio = codigoPraxio;
        this.novoCodigoCliente = novoCodigoCliente;
        this.novoCodModeloPneu = novoCodModeloPneu;
        this.novoCodDimensaoPneu = novoCodDimensaoPneu;
        this.novoDotPneu = novoDotPneu;
        this.novoValorPneu = novoValorPneu;
        this.novoCodModeloBanda = novoCodModeloBanda;
        this.novoValorBandaPneu = novoValorBandaPneu;
    }

    @NotNull
    public static PneuEdicaoPraxio getPneuEdicaoPraxioDummy() {
        return new PneuEdicaoPraxio(
                18723L,
                "PN0023",
                131L,
                2L,
                "2343",
                new BigDecimal(1343.50),
                10L,
                new BigDecimal(542.50));
    }

    @NotNull
    public Long getCodigoPraxio() {
        return codigoPraxio;
    }

    @NotNull
    public String getNovoCodigoCliente() {
        return novoCodigoCliente;
    }

    @NotNull
    public Long getNovoCodModeloPneu() {
        return novoCodModeloPneu;
    }

    @NotNull
    public Long getNovoCodDimensaoPneu() {
        return novoCodDimensaoPneu;
    }

    @NotNull
    public String getNovoDotPneu() {
        return novoDotPneu;
    }

    @NotNull
    public BigDecimal getNovoValorPneu() {
        return novoValorPneu;
    }

    @Nullable
    public Long getNovoCodModeloBanda() {
        return novoCodModeloBanda;
    }

    @Nullable
    public BigDecimal getNovoValorBandaPneu() {
        return novoValorBandaPneu;
    }
}
