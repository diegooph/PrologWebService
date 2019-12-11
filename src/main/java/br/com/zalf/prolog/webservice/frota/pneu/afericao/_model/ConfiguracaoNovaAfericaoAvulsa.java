package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

/**
 * Created on 06/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoNovaAfericaoAvulsa extends ConfiguracaoNovaAfericao {

    public ConfiguracaoNovaAfericaoAvulsa(final double sulcoMinimoDescarte,
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

    }
}