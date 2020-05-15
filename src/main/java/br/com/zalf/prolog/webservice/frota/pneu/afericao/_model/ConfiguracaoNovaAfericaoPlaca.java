package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;

/**
 * Created on 06/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoNovaAfericaoPlaca extends ConfiguracaoNovaAfericao {

    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulco;
    private final FormaColetaDadosAfericaoEnum formaColetaDadosPressao;
    private final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;
    private final boolean podeAferirEstepe;

    public ConfiguracaoNovaAfericaoPlaca(
            final FormaColetaDadosAfericaoEnum formaColetaDadosSulco,
            final FormaColetaDadosAfericaoEnum formaColetaDadosPressao,
            final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao,
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
        super(sulcoMinimoDescarte,
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
        this.formaColetaDadosSulco = formaColetaDadosSulco;
        this.formaColetaDadosPressao = formaColetaDadosPressao;
        this.formaColetaDadosSulcoPressao = formaColetaDadosSulcoPressao;
        this.podeAferirEstepe = podeAferirEstepe;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulco() {
        return formaColetaDadosSulco;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosPressao() {
        return formaColetaDadosPressao;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulcoPressao() {
        return formaColetaDadosSulcoPressao;
    }

    public boolean isPodeAferirEstepe() {
        return podeAferirEstepe;
    }

}