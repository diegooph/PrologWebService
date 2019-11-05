package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

/**
 * Created by jean on 28/06/16.
 */
public class Faixa {

    private double inicio;
    private double fim;
    private double porcentagem;
    private int totalPneus;
    private boolean isNaoAferidos;

    public Faixa() {
    }

    public boolean isNaoAferidos() {
        return isNaoAferidos;
    }

    public void setNaoAferidos(boolean naoAferidos) {
        isNaoAferidos = naoAferidos;
    }

    public double getInicio() {
        return inicio;
    }

    public void setInicio(double inicio) {
        this.inicio = inicio;
    }

    public double getFim() {
        return fim;
    }

    public void setFim(double fim) {
        this.fim = fim;
    }

    public double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(double porcentagem) {
        this.porcentagem = porcentagem;
    }

    public int getTotalPneus() {
        return totalPneus;
    }

    public void setTotalPneus(int totalPneus) {
        this.totalPneus = totalPneus;
    }

    @Override
    public String toString() {
        return "Faixa{" +
                "inicio=" + inicio +
                ", fim=" + fim +
                ", porcentagem=" + porcentagem +
                ", totalPneus=" + totalPneus +
                ", isNaoAferidos=" + isNaoAferidos +
                '}';
    }
}
