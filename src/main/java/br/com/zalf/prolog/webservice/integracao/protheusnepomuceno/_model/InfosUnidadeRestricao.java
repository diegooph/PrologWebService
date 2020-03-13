package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/13/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class InfosUnidadeRestricao {
    /**
     * Código da unidade a qual os períodos de aferição se referem.
     */
    @NotNull
    private final Long codUnidade;
    /**
     * Número inteiro que representa a quantos dias uma operação de aferição de Sulco deve ser realizada.
     */
    private final int periodoDiasAfericaoSulco;
    /**
     * Número inteiro que representa a quantos dias uma operação de aferição de Pressão deve ser realizada.
     */
    private final int periodoDiasAfericaoPressao;

    public InfosUnidadeRestricao(@NotNull final Long codUnidade,
                                 final int periodoDiasAfericaoSulco,
                                 final int periodoDiasAfericaoPressao) {
        this.codUnidade = codUnidade;
        this.periodoDiasAfericaoSulco = periodoDiasAfericaoSulco;
        this.periodoDiasAfericaoPressao = periodoDiasAfericaoPressao;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    public int getPeriodoDiasAfericaoSulco() {
        return periodoDiasAfericaoSulco;
    }

    public int getPeriodoDiasAfericaoPressao() {
        return periodoDiasAfericaoPressao;
    }
}
