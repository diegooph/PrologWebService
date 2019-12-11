package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

/**
 * Classe para encapsular os atributos de configuração de aferição.
 * <p>
 * Created on 31/10/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class ConfiguracaoNovaAfericao {
    private final double sulcoMinimoDescarte;
    private final double sulcoMinimoRecape;
    private final double toleranciaInspecao;
    private final double toleranciaCalibragem;
    private final int periodoDiasAfericaoSulco;
    private final int periodoDiasAfericaoPressao;
    private final double variacaoAceitaSulcoMenorMilimetros;
    private final double variacaoAceitaSulcoMaiorMilimetros;
    private final boolean usaDefaultProLog;
    private final boolean bloqueiaValoresMenores;
    private final boolean bloqueiaValoresMaiores;

    public ConfiguracaoNovaAfericao(final double sulcoMinimoDescarte,
                                    final double sulcoMinimoRecape,
                                    final double toleranciaInspecao,
                                    final double toleranciaCalibragem,
                                    final int periodoDiasAfericaoSulco,
                                    final int periodoDiasAfericaoPressao,
                                    final double variacaoAceitaSulcoMenorMilimetros,
                                    final double variacaoAceitaSulcoMaiorMilimetros,
                                    final boolean usaDefaultProLog,
                                    final boolean bloqueiaValoresMenores,
                                    final boolean bloqueiaValoresMaiores) {
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.toleranciaInspecao = toleranciaInspecao;
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
        this.usaDefaultProLog = usaDefaultProLog;
        this.bloqueiaValoresMenores = bloqueiaValoresMenores;
        this.bloqueiaValoresMaiores = bloqueiaValoresMaiores;
    }

    public double getSulcoMinimoDescarte() {
        return sulcoMinimoDescarte;
    }

    public double getSulcoMinimoRecape() {
        return sulcoMinimoRecape;
    }

    public double getToleranciaInspecao() {
        return toleranciaInspecao;
    }

    public double getToleranciaCalibragem() {
        return toleranciaCalibragem;
    }

    public int getPeriodoDiasAfericaoSulco() {
        return periodoDiasAfericaoSulco;
    }


    public int getPeriodoDiasAfericaoPressao() {
        return periodoDiasAfericaoPressao;
    }

    public double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }

    public boolean isUsaDefaultProLog() {
        return usaDefaultProLog;
    }

    public boolean isBloqueiaValoresMenores() {
        return bloqueiaValoresMenores;
    }

    public boolean isBloqueiaValoresMaiores() {
        return bloqueiaValoresMaiores;
    }
}