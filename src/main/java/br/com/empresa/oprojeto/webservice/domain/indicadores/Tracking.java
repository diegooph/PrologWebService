package br.com.empresa.oprojeto.webservice.domain.indicadores;


/**
 * Created by luiz on 12/2/15.
 */
public class Tracking extends Indicador {
    // Total de entregas que saiu
    private int total;
    private int insercoesOk;
    private int insercoesNok;

    public Tracking() {
    }

    public Tracking(int total, int insercoesOk, int insercoesNok) {
        this.total = total;
        this.insercoesOk = insercoesOk;
        this.insercoesNok = insercoesNok;
    }

    public Tracking(double meta, double resultado, int total, int insercoesOk, int insercoesNok) {
        super(meta, resultado);
        this.total = total;
        this.insercoesOk = insercoesOk;
        this.insercoesNok = insercoesNok;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getInsercoesOk() {
        return insercoesOk;
    }

    public void setInsercoesOk(int insercoesOk) {
        this.insercoesOk = insercoesOk;
    }

    public int getInsercoesNok() {
        return insercoesNok;
    }

    public void setInsercoesNok(int insercoesNok) {
        this.insercoesNok = insercoesNok;
    }

    @Override
    public double calculaResultado() {
        return 0;
    }
}
