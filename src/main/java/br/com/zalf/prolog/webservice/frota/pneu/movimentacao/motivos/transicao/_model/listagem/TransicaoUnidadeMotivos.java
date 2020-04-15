package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class TransicaoUnidadeMotivos implements Comparable<TransicaoUnidadeMotivos> {

    private static Comparator<TransicaoUnidadeMotivos> COMPARATOR = Comparator
            .comparing(TransicaoUnidadeMotivos::getOrigemMovimento)
            .thenComparing(TransicaoUnidadeMotivos::getDestinoMovimento);
    @NotNull
    @EqualsAndHashCode.Include
    private final OrigemDestinoEnum origemMovimento;
    @NotNull
    @EqualsAndHashCode.Include
    private final OrigemDestinoEnum destinoMovimento;
    @NotNull
    private final List<MotivoMovimentoUnidade> motivosMovimento;
    @Nullable
    private final Boolean obrigatorioMotivoMovimento;

    @Override
    public int compareTo(@NotNull final TransicaoUnidadeMotivos outraTransicao) {
        return COMPARATOR.compare(this, outraTransicao);
    }
}
