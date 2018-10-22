package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import org.jetbrains.annotations.NotNull;

/**
 * Essa classe contém os parâmetros de verificação para exibição (ou não) de um alerta no momento de realização de uma
 * aferição no aplicativo.
 *
 * A parametrização é por unidade e é possível parametrizar um valor para exibição de alertas para coletas de sulcos
 * maiores ou menores do que o último valor salvo no sistema para o mesmo sulco.
 *
 * Created on 22/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoAlertaColetaSulco {

    /**
     * O código da {@link Unidade unidade} para a qual esses parâmetros de coleta de sulco se aplicam.
     */
    @NotNull
    private final Long codUnidadeReferente;

    /**
     * Indica a variação aceita, em milímetros, para coletas de sulco MENORES do que a última medida salva no sistema.
     *
     * Exemplo: se esse valor for 1 mm e o último sulco salvo no sistema for 10 mm, caso alguma coleta desse sulco seja
     * inferior a 9 mm, um alerta deve ser exibido. Caso valores entre [9, 10] sejam coletados, eles estão dentro da
     * varição aceita e nenhum alerta precisa ser exibido.
     */
    private final double variacaoAceitaSulcoMenorMilimetros;

    /**
     * Indica a variação aceita, em milímetros, para coletas de sulco MAIORES do que a última medida salva no sistema.
     *
     * Exemplo: se esse valor for 1 mm e o último sulco salvo no sistema for 8 mm, caso alguma coleta desse sulco seja
     * maior a 9 mm, um alerta deve ser exibido. Caso valores entre [8, 9] sejam coletados, eles estão dentro da
     * varição aceita e nenhum alerta precisa ser exibido.
     */
    private final double variacaoAceitaSulcoMaiorMilimetros;

    public ConfiguracaoAlertaColetaSulco(@NotNull final Long codUnidadeReferente,
                                         final double variacaoAceitaSulcoMenorMilimetros,
                                         final double variacaoAceitaSulcoMaiorMilimetros) {
        this.codUnidadeReferente = codUnidadeReferente;
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
    }

    @NotNull
    public Long getCodUnidadeReferente() {
        return codUnidadeReferente;
    }

    public double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }
}