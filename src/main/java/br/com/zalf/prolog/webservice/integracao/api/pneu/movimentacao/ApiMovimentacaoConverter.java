package br.com.zalf.prolog.webservice.integracao.api.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/29/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMovimentacaoConverter {
    private ApiMovimentacaoConverter() {
        throw new IllegalStateException(ApiMovimentacaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static ApiProcessoMovimentacao convert(@NotNull final ProcessoMovimentacao processoMovimentacao) {
        return new ApiProcessoMovimentacao();
    }
}
