package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.pneu._model;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuPlanilha {
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

    public void setNumeroFogo(@Nullable final String numeroFogo) {
        this.numeroFogo = numeroFogo;
    }

    public void setMarca(@Nullable final String marca) {
        this.marca = marca;
    }

    public void setModelo(@Nullable final String modelo) {
        this.modelo = modelo;
    }

    public void setDot(@Nullable final String dot) {
        this.dot = dot;
    }

    public void setDimensao(@Nullable final String dimensao) {
        this.dimensao = dimensao;
    }

    public void setPressaoIdeal(@Nullable final String pressaoIdeal) {
        this.pressaoIdeal = pressaoIdeal;
    }

    public void setQtdSulcos(@Nullable final String qtdSulcos) {
        this.qtdSulcos = qtdSulcos;
    }

    public void setAlturaSulcos(@Nullable final String alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }

    public void setValorPneu(@Nullable final String valorPneu) {
        this.valorPneu = valorPneu;
    }

    public void setValorBanda(@Nullable final String valorBanda) {
        this.valorBanda = valorBanda;
    }

    public void setVidaAtual(@Nullable final String vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public void setVidaTotal(@Nullable final String vidaTotal) {
        this.vidaTotal = vidaTotal;
    }

    public void setMarcaBanda(@Nullable final String marcaBanda) {
        this.marcaBanda = marcaBanda;
    }

    public void setModeloBanda(@Nullable final String modeloBanda) {
        this.modeloBanda = modeloBanda;
    }

    public void setQtdSulcosBanda(@Nullable final String qtdSulcosBanda) {
        this.qtdSulcosBanda = qtdSulcosBanda;
    }

    public void setPneuNovoNuncaRodado(@Nullable final String pneuNovoNuncaRodado) {
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }
}
