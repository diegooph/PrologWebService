package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Created on 31/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuCargaInicial {
    @NotNull
    private final Long codigoSistemaIntegrado;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final Long codUnidadePneu;
    @NotNull
    private final Long codModeloPneu;
    @NotNull
    private final Long codDimensaoPneu;
    @NotNull
    private final Double pressaoCorretaPneu;
    @NotNull
    private final Integer vidaAtualPneu;
    @NotNull
    private final Integer vidaTotalPneu;
    @NotNull
    private final String dotPneu;
    @NotNull
    private final BigDecimal valorPneu;
    @NotNull
    private final Boolean pneuNovoNuncaRodado;
    @Nullable
    private final Long codModeloBanda;
    @Nullable
    private final BigDecimal valorBandaPneu;
    @NotNull
    private final ApiStatusPneu statusPneu;
    @Nullable
    private final String placaVeiculoPneuAplicado;
    @Nullable
    private final Integer posicaoPneuAplicado;

    public ApiPneuCargaInicial(@NotNull final Long codigoSistemaIntegrado,
                               @NotNull final String codigoCliente,
                               @NotNull final Long codUnidadePneu,
                               @NotNull final Long codModeloPneu,
                               @NotNull final Long codDimensaoPneu,
                               @NotNull final Double pressaoCorretaPneu,
                               @NotNull final Integer vidaAtualPneu,
                               @NotNull final Integer vidaTotalPneu,
                               @NotNull final String dotPneu,
                               @NotNull final BigDecimal valorPneu,
                               @NotNull final Boolean pneuNovoNuncaRodado,
                               @Nullable final Long codModeloBanda,
                               @Nullable final BigDecimal valorBandaPneu,
                               @NotNull final ApiStatusPneu statusPneu,
                               @Nullable final String placaVeiculoPneuAplicado,
                               @Nullable final Integer posicaoPneuAplicado) {
        this.codigoSistemaIntegrado = codigoSistemaIntegrado;
        this.codigoCliente = codigoCliente;
        this.codUnidadePneu = codUnidadePneu;
        this.codModeloPneu = codModeloPneu;
        this.codDimensaoPneu = codDimensaoPneu;
        this.pressaoCorretaPneu = pressaoCorretaPneu;
        this.vidaAtualPneu = vidaAtualPneu;
        this.vidaTotalPneu = vidaTotalPneu;
        this.dotPneu = dotPneu;
        this.valorPneu = valorPneu;
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
        this.codModeloBanda = codModeloBanda;
        this.valorBandaPneu = valorBandaPneu;
        this.statusPneu = statusPneu;
        this.placaVeiculoPneuAplicado = placaVeiculoPneuAplicado;
        this.posicaoPneuAplicado = posicaoPneuAplicado;
    }

    @NotNull
    public static ApiPneuCargaInicial getPneuCargaInicialPraxioDummy() {
        return new ApiPneuCargaInicial(
                18723L,
                "PN0001",
                5L,
                131L,
                2L,
                120.0,
                2,
                3,
                "2343",
                new BigDecimal(1343.50),
                false,
                482L,
                new BigDecimal(352.00),
                ApiStatusPneu.EM_USO,
                "PRO0001",
                111);
    }

    @NotNull
    public Long getCodigoSistemaIntegrado() {
        return codigoSistemaIntegrado;
    }

    @NotNull
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @NotNull
    public Long getCodUnidadePneu() {
        return codUnidadePneu;
    }

    @NotNull
    public Long getCodModeloPneu() {
        return codModeloPneu;
    }

    @NotNull
    public Long getCodDimensaoPneu() {
        return codDimensaoPneu;
    }

    @NotNull
    public Double getPressaoCorretaPneu() {
        return pressaoCorretaPneu;
    }

    @NotNull
    public Integer getVidaAtualPneu() {
        return vidaAtualPneu;
    }

    @NotNull
    public Integer getVidaTotalPneu() {
        return vidaTotalPneu;
    }

    @NotNull
    public String getDotPneu() {
        return dotPneu;
    }

    @NotNull
    public BigDecimal getValorPneu() {
        return valorPneu;
    }

    @NotNull
    public Boolean getPneuNovoNuncaRodado() {
        return pneuNovoNuncaRodado;
    }

    @Nullable
    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    @Nullable
    public BigDecimal getValorBandaPneu() {
        return valorBandaPneu;
    }

    @NotNull
    public ApiStatusPneu getStatusPneu() {
        return statusPneu;
    }

    @Nullable
    public String getPlacaVeiculoPneuAplicado() {
        return placaVeiculoPneuAplicado;
    }

    @Nullable
    public Integer getPosicaoPneuAplicado() {
        return posicaoPneuAplicado;
    }
}
