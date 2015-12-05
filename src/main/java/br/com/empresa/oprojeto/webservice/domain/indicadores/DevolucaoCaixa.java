package br.com.empresa.oprojeto.webservice.domain.indicadores;


public class DevolucaoCaixa extends Indicador {
    private int cxCarregadas;
    private int cxEntregues;
    private int cxDevolvidas;

    public DevolucaoCaixa(int cxCarregadas, int cxEntregues, int cxDevolvidas) {
        this.cxCarregadas = cxCarregadas;
        this.cxEntregues = cxEntregues;
        this.cxDevolvidas = cxDevolvidas;
    }

    public DevolucaoCaixa(double meta, double resultado, int cxCarregadas, int cxEntregues, int cxDevolvidas) {
        super(meta, resultado);
        this.cxCarregadas = cxCarregadas;
        this.cxEntregues = cxEntregues;
        this.cxDevolvidas = cxDevolvidas;
    }

    public int getCxCarregadas() {
        return cxCarregadas;
    }

    public void setCxCarregadas(int cxCarregadas) {
        this.cxCarregadas = cxCarregadas;
    }

    public int getCxEntregues() {
        return cxEntregues;
    }

    public void setCxEntregues(int cxEntregues) {
        this.cxEntregues = cxEntregues;
    }

    public int getCxDevolvidas() {
        return cxDevolvidas;
    }

    public void setCxDevolvidas(int cxDevolvidas) {
        this.cxDevolvidas = cxDevolvidas;
    }

    @Override
    public double calculaResultado() {
        return 0;
    }
}
