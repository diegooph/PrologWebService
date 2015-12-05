package br.com.empresa.oprojeto.webservice.domain.indicadores;


public class DevolucaoNf extends Indicador {
    private int nfCarregadas;
    private int nfEntregues;
    private int nfDevolvidas;

    public DevolucaoNf() {
    }

    public DevolucaoNf(int nfCarregadas, int nfEntregues, int nfDevolvidas) {
        this.nfCarregadas = nfCarregadas;
        this.nfEntregues = nfEntregues;
        this.nfDevolvidas = nfDevolvidas;
    }

    public DevolucaoNf(double meta, double resultado, int nfCarregadas, int nfEntregues, int nfDevolvidas) {
        super(meta, resultado);
        this.nfCarregadas = nfCarregadas;
        this.nfEntregues = nfEntregues;
        this.nfDevolvidas = nfDevolvidas;
    }

    public int getNfCarregadas() {
        return nfCarregadas;
    }

    public void setNfCarregadas(int nfCarregadas) {
        this.nfCarregadas = nfCarregadas;
    }

    public int getNfEntregues() {
        return nfEntregues;
    }

    public void setNfEntregues(int nfEntregues) {
        this.nfEntregues = nfEntregues;
    }

    public int getNfDevolvidas() {
        return nfDevolvidas;
    }

    public void setNfDevolvidas(int nfDevolvidas) {
        this.nfDevolvidas = nfDevolvidas;
    }

    @Override
    public double calculaResultado() {
        return 0;
    }
}
