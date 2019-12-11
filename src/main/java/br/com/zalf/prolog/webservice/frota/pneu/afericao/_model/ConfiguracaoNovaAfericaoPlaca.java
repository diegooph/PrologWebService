package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

/**
 * Created on 06/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoNovaAfericaoPlaca extends ConfiguracaoNovaAfericao {
    private final boolean podeAferirSulco;
    private final boolean podeAferirPressao;
    private final boolean podeAferirSulcoPressao;
    private final boolean podeAferirEstepe;

    public ConfiguracaoNovaAfericaoPlaca(
            final boolean podeAferirSulco,
            final boolean podeAferirPressao,
            final boolean podeAferirSulcoPressao,
            final boolean podeAferirEstepe,
            final double sulcoMinimoDescarte,
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
        super(  sulcoMinimoDescarte,
                sulcoMinimoRecape,
                toleranciaInspecao,
                toleranciaCalibragem,
                periodoDiasAfericaoSulco,
                periodoDiasAfericaoPressao,
                variacaoAceitaSulcoMenorMilimetros,
                variacaoAceitaSulcoMaiorMilimetros,
                usaDefaultProLog,
                bloqueiaValoresMenores,
                bloqueiaValoresMaiores);
        this.podeAferirSulco = podeAferirSulco;
        this.podeAferirPressao = podeAferirPressao;
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
        this.podeAferirEstepe = podeAferirEstepe;
    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    public boolean isPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public boolean isPodeAferirEstepe() {
        return podeAferirEstepe;
    }
}