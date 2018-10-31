package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

/**
 * Classe para encapsular os atributos de configuração de aferição.
 *
 * Created on 31/10/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoNovaAfericao {

    private double sulcoMinimoDescarte;
    private double sulcoMinimoRecape;
    private double toleranciaInspecao;
    private double toleranciaCalibragem;
    private int periodoDiasAfericaoSulco;
    private int periodoDiasAfericaoPressao;
    private boolean podeAferirSulco;
    private boolean podeAferirPressao;
    private boolean podeAferirSulcoPressao;
    private boolean podeAferirEstepe;
    private double variacaoAceitaSulcoMenorMilimetros;
    private double variacaoAceitaSulcoMaiorMilimetros;
    private boolean usaDefaultProLog;

    public ConfiguracaoNovaAfericao() {
    }

    public double getSulcoMinimoDescarte() {
        return sulcoMinimoDescarte;
    }

    public void setSulcoMinimoDescarte(final double sulcoMinimoDescarte) {
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
    }

    public double getSulcoMinimoRecape() {
        return sulcoMinimoRecape;
    }

    public void setSulcoMinimoRecape(final double sulcoMinimoRecape) {
        this.sulcoMinimoRecape = sulcoMinimoRecape;
    }

    public double getToleranciaInspecao() {
        return toleranciaInspecao;
    }

    public void setToleranciaInspecao(final double toleranciaInspecao) {
        this.toleranciaInspecao = toleranciaInspecao;
    }

    public double getToleranciaCalibragem() {
        return toleranciaCalibragem;
    }

    public void setToleranciaCalibragem(final double toleranciaCalibragem) {
        this.toleranciaCalibragem = toleranciaCalibragem;
    }

    public int getPeriodoDiasAfericaoSulco() {
        return periodoDiasAfericaoSulco;
    }

    public void setPeriodoDiasAfericaoSulco(final int periodoDiasAfericaoSulco) {
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
    }

    public int getPeriodoDiasAfericaoPressao() {
        return periodoDiasAfericaoPressao;
    }

    public void setPeriodoDiasAfericaoPressao(final int periodoDiasAfericaoPressao) {
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public void setPodeAferirSulco(final boolean podeAferirSulco) {
        this.podeAferirSulco = podeAferirSulco;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    public void setPodeAferirPressao(final boolean podeAferirPressao) {
        this.podeAferirPressao = podeAferirPressao;
    }

    public boolean isPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public void setPodeAferirSulcoPressao(final boolean podeAferirSulcoPressao) {
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
    }

    public boolean isPodeAferirEstepe() {
        return podeAferirEstepe;
    }

    public void setPodeAferirEstepe(final boolean podeAferirEstepe) {
        this.podeAferirEstepe = podeAferirEstepe;
    }

    public double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public void setVariacaoAceitaSulcoMenorMilimetros(final double variacaoAceitaSulcoMenorMilimetros) {
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
    }

    public double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }

    public void setVariacaoAceitaSulcoMaiorMilimetros(final double variacaoAceitaSulcoMaiorMilimetros) {
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
    }

    public boolean isUsaDefaultProLog() {
        return usaDefaultProLog;
    }

    public void setUsaDefaultProLog(final boolean usaDefaultProLog) {
        this.usaDefaultProLog = usaDefaultProLog;
    }
}
