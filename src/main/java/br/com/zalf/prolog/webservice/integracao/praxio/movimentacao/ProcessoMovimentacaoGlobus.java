package br.com.zalf.prolog.webservice.integracao.praxio.movimentacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProcessoMovimentacaoGlobus {
    @NotNull
    private final List<MovimentacaoGlobus> trocas;

    public ProcessoMovimentacaoGlobus(@NotNull final List<MovimentacaoGlobus> trocas) {
        this.trocas = trocas;
    }

    @NotNull
    public List<MovimentacaoGlobus> getTrocas() {
        return trocas;
    }
}
