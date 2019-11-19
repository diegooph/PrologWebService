package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuPlanilha {
    private Long codUnidade;
    private String numeroFogo;
    private String marca;
    private String modelo;
    private String dot;
    private String dimensao;
    private double pressaoIdeal;
    private int qtdSulcos;
    private double alturaSulcos;
    private BigDecimal valorPneu;
    private BigDecimal valorBanda;
    private int vidaAtual;
    private int vidaTotal;
    private String marcaBanda;
    private String modeloBanda;
    private int qtdSulcosBanda;
    private String pneuNovoNuncaRodado;

    public void setCodUnidade(@NotNull final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public void setNumeroFogo(@NotNull final String numeroFogo) {
        this.numeroFogo = numeroFogo;
    }

    public void setMarca(@NotNull final String marca) {
        this.marca = marca;
    }

    public void setModelo(@NotNull final String modelo) {
        this.modelo = modelo;
    }

    public void setDot(@NotNull final String dot) {
        this.dot = dot;
    }

    public void setDimensao(@NotNull final String dimensao) {
        this.dimensao = dimensao;
    }

    public void setPressaoIdeal(final double pressaoIdeal) {
        this.pressaoIdeal = pressaoIdeal;
    }

    public void setQtdSulcos(final int qtdSulcos) {
        this.qtdSulcos = qtdSulcos;
    }

    public void setAlturaSulcos(final double alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }

    public void setValorPneu(@NotNull final BigDecimal valorPneu) {
        this.valorPneu = valorPneu;
    }

    public void setValorBanda(@NotNull final BigDecimal valorBanda) {
        this.valorBanda = valorBanda;
    }

    public void setVidaAtual(final int vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public void setVidaTotal(final int vidaTotal) {
        this.vidaTotal = vidaTotal;
    }

    public void setMarcaBanda(@NotNull final String marcaBanda) {
        this.marcaBanda = marcaBanda;
    }

    public void setModeloBanda(@NotNull final String modeloBanda) {
        this.modeloBanda = modeloBanda;
    }

    public void setQtdSulcosBanda(final int qtdSulcosBanda) {
        this.qtdSulcosBanda = qtdSulcosBanda;
    }

    public void setPneuNovoNuncaRodado(@NotNull final String pneuNovoNuncaRodado) {
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }
}
