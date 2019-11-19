package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.pneu._model;

/**
 * Created on 19/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuPlanilha {
    private Long codUnidade;
    private String numeroFogo;
    private String marca;
    private String modelo;
    private Long dot;
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

    public void setCodUnidade(Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public String getNumeroFogo() {
        return numeroFogo;
    }

    public void setNumeroFogo(String numeroFogo) {
        this.numeroFogo = numeroFogo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setDot(Long dot) {
        this.dot = dot;
    }

    public void setDimensao(String dimensao) {
        this.dimensao = dimensao;
    }

    public void setPressaoIdeal(String pressaoIdeal) {
        this.pressaoIdeal = pressaoIdeal;
    }

    public void setQtdSulcos(String qtdSulcos) {
        this.qtdSulcos = qtdSulcos;
    }

    public void setAlturaSulcos(String alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }

    public void setValorPneu(String valorPneu) {
        this.valorPneu = valorPneu;
    }

    public void setValorBanda(String valorBanda) {
        this.valorBanda = valorBanda;
    }

    public void setVidaAtual(String vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public void setVidaTotal(String vidaTotal) {
        this.vidaTotal = vidaTotal;
    }

    public void setMarcaBanda(String marcaBanda) {
        this.marcaBanda = marcaBanda;
    }

    public void setModeloBanda(String modeloBanda) {
        this.modeloBanda = modeloBanda;
    }

    public void setQtdSulcosBanda(String qtdSulcosBanda) {
        this.qtdSulcosBanda = qtdSulcosBanda;
    }

    public void setPneuNovoNuncaRodado(String pneuNovoNuncaRodado) {
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }
}
