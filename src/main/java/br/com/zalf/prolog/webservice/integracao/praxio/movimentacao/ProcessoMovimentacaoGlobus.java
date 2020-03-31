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
    private final Long codUnidadeMovimentacaoRealizada;
    @NotNull
    private final List<MovimentacaoGlobus> trocas;

    public ProcessoMovimentacaoGlobus(@NotNull final Long codUnidadeMovimentacaoRealizada,
                                      @NotNull final List<MovimentacaoGlobus> trocas) {
        this.codUnidadeMovimentacaoRealizada = codUnidadeMovimentacaoRealizada;
        this.trocas = trocas;
    }

    @NotNull
    public Long getCodUnidadeMovimentacaoRealizada() {
        return codUnidadeMovimentacaoRealizada;
    }

    @NotNull
    public List<MovimentacaoGlobus> getTrocas() {
        return trocas;
    }
}
