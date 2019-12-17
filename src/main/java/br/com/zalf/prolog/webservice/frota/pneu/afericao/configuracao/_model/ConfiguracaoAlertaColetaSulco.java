package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Essa classe contém os parâmetros de verificação para exibição (ou não) de um alerta no momento de realização de uma
 * aferição no aplicativo.
 * <p>
 * A parametrização é por unidade e é possível parametrizar um valor para exibição de alertas para coletas de sulcos
 * maiores ou menores do que o último valor salvo no sistema para o mesmo sulco.
 * <p>
 * Created on 22/10/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ConfiguracaoAlertaColetaSulco {

    /**
     * O código único dessa configuração.
     */
    @Nullable
    private final Long codigo;

    /**
     * O código da {@link Unidade unidade} para a qual esses parâmetros de coleta de sulco se aplicam.
     */
    @NotNull
    private final Long codUnidadeReferente;

    /**
     * O nome da {@link Unidade unidade} para a qual esses parâmetros de coleta de sulco se aplicam.
     */
    @NotNull
    private final String nomeUnidadeReferente;

    /**
     * Indica a variação aceita, em milímetros, para coletas de sulco MENORES do que a última medida salva no sistema.
     * <p>
     * Exemplo: se esse valor for 1 mm e o último sulco salvo no sistema for 10 mm, caso alguma coleta desse sulco seja
     * inferior a 9 mm, um alerta deve ser exibido. Caso valores entre [9, 10] sejam coletados, eles estão dentro da
     * varição aceita e nenhum alerta precisa ser exibido.
     */
    private final double variacaoAceitaSulcoMenorMilimetros;

    /**
     * Indica a variação aceita, em milímetros, para coletas de sulco MAIORES do que a última medida salva no sistema.
     * <p>
     * Exemplo: se esse valor for 1 mm e o último sulco salvo no sistema for 8 mm, caso alguma coleta desse sulco seja
     * maior a 9 mm, um alerta deve ser exibido. Caso valores entre [8, 9] sejam coletados, eles estão dentro da
     * varição aceita e nenhum alerta precisa ser exibido.
     */
    private final double variacaoAceitaSulcoMaiorMilimetros;

    /**
     * Parametrização que define se uma aferição que ultrapassou o limite de variação menor será bloqueada.
     */
    private final boolean bloqueiaValoresMenores;

    /**
     * Parametrização que define se uma aferição que ultrapassou o limite de variação maior será bloqueada.
     */
    private final boolean bloqueiaValoresMaiores;

    public ConfiguracaoAlertaColetaSulco(@Nullable final Long codigo,
                                         @NotNull final Long codUnidadeReferente,
                                         @NotNull final String nomeUnidadeReferente,
                                         final double variacaoAceitaSulcoMenorMilimetros,
                                         final double variacaoAceitaSulcoMaiorMilimetros,
                                         final boolean bloqueiaValoresMenores,
                                         final boolean bloqueiaValoresMaiores) {
        this.codigo = codigo;
        this.codUnidadeReferente = codUnidadeReferente;
        this.nomeUnidadeReferente = nomeUnidadeReferente;
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
        this.bloqueiaValoresMenores = bloqueiaValoresMenores;
        this.bloqueiaValoresMaiores = bloqueiaValoresMaiores;
    }

    @Nullable
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Long getCodUnidadeReferente() {
        return codUnidadeReferente;
    }

    @NotNull
    public String getNomeUnidadeReferente() {
        return nomeUnidadeReferente;
    }

    public double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }

    public boolean isBloqueiaValoresMenores() {
        return bloqueiaValoresMenores;
    }

    public boolean isBloqueiaValoresMaiores() {
        return bloqueiaValoresMaiores;
    }
}