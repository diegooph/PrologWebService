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
    private String pressaoIdeal;
    private String qtdSulcos;
    private String alturaSulcos;
    private String valorPneu;
    private String valorBanda;
    private String vidaAtual;
    private String vidaTotal;
    private String marcaBanda;
    private String modeloBanda;
    private String qtdSulcosBanda;
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

    public void setPressaoIdeal(final String pressaoIdeal) {
        this.pressaoIdeal = pressaoIdeal;
    }

    public void setQtdSulcos(final String qtdSulcos) {
        this.qtdSulcos = qtdSulcos;
    }

    public void setAlturaSulcos(final String alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }

    public void setValorPneu(@NotNull final String valorPneu) {
        this.valorPneu = valorPneu;
    }

    public void setValorBanda(@NotNull final String valorBanda) {
        this.valorBanda = valorBanda;
    }

    public void setVidaAtual(final String vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public void setVidaTotal(final String vidaTotal) {
        this.vidaTotal = vidaTotal;
    }

    public void setMarcaBanda(@NotNull final String marcaBanda) {
        this.marcaBanda = marcaBanda;
    }

    public void setModeloBanda(@NotNull final String modeloBanda) {
        this.modeloBanda = modeloBanda;
    }

    public void setQtdSulcosBanda(final String qtdSulcosBanda) {
        this.qtdSulcosBanda = qtdSulcosBanda;
    }

    public void setPneuNovoNuncaRodado(@NotNull final String pneuNovoNuncaRodado) {
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }
}
