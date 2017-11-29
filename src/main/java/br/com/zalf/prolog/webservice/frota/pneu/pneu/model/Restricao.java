package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

/**
 * Created by jean on 10/06/16.
 */
public class Restricao {

    private double toleranciaCalibragem;
    private double toleranciaInspecao;
    private double sulcoMinimoRecape;
    private double sulcoMinimoDescarte;
    private int periodoDiasAfericaoPressao;
    private int periodoDiasAfericaoSulco;

    public Restricao() {
    }

    public Restricao(double toleranciaCalibragem,
                     double toleranciaInspecao,
                     double sulcoMinimoRecape,
                     double sulcoMinimoDescarte,
                     int periodoDiasAfericaoPressao,
                     int periodoDiasAfericaoSulco) {
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.toleranciaInspecao = toleranciaInspecao;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
    }

    public int getPeriodoDiasAfericaoPressao() {
        return periodoDiasAfericaoPressao;
    }

    public void setPeriodoDiasAfericaoPressao(int periodoDiasAfericaoPressao) {
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
    }

    public int getPeriodoDiasAfericaoSulco() {
        return periodoDiasAfericaoSulco;
    }

    public void setPeriodoDiasAfericaoSulco(int periodoDiasAfericaoSulco) {
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
    }

    public double getToleranciaInspecao() {
        return toleranciaInspecao;
    }

    public void setToleranciaInspecao(double toleranciaInspecao) {
        this.toleranciaInspecao = toleranciaInspecao;
    }

    public double getToleranciaCalibragem() {
        return toleranciaCalibragem;
    }

    public void setToleranciaCalibragem(double toleranciaCalibragem) {
        this.toleranciaCalibragem = toleranciaCalibragem;
    }

    public double getSulcoMinimoRecape() {
        return sulcoMinimoRecape;
    }

    public void setSulcoMinimoRecape(double sulcoMinimoRecape) {
        this.sulcoMinimoRecape = sulcoMinimoRecape;
    }

    public double getSulcoMinimoDescarte() {
        return sulcoMinimoDescarte;
    }

    public void setSulcoMinimoDescarte(double sulcoMinimoDescarte) {
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
    }

    @Override
    public String toString() {
        return "Restricao{" +
                "toleranciaCalibragem=" + toleranciaCalibragem +
                ", toleranciaInspecao=" + toleranciaInspecao +
                ", sulcoMinimoRecape=" + sulcoMinimoRecape +
                ", sulcoMinimoDescarte=" + sulcoMinimoDescarte +
                ", periodoDiasAfericaoPressao=" + periodoDiasAfericaoPressao +
                ", periodoDiasAfericaoSulco=" + periodoDiasAfericaoSulco +
                '}';
    }
}
