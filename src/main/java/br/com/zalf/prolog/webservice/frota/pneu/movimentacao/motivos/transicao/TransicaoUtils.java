package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2020-04-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class TransicaoUtils {
    private static final List<TransicaoUnidadeMotivos> TRANSICOES_POSSIVEIS;

    static {
        TRANSICOES_POSSIVEIS = new ArrayList<>();
        TRANSICOES_POSSIVEIS.add(new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.ESTOQUE,
                OrigemDestinoEnum.VEICULO,
                Collections.emptyList(),
                false));
        TRANSICOES_POSSIVEIS.add(new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.ESTOQUE,
                OrigemDestinoEnum.ANALISE,
                Collections.emptyList(),
                false));
        TRANSICOES_POSSIVEIS.add(new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.VEICULO,
                OrigemDestinoEnum.ESTOQUE,
                Collections.emptyList(),
                false));
        TRANSICOES_POSSIVEIS.add(new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.VEICULO,
                OrigemDestinoEnum.ANALISE,
                Collections.emptyList(),
                false));
        TRANSICOES_POSSIVEIS.add(new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.VEICULO,
                OrigemDestinoEnum.VEICULO,
                Collections.emptyList(),
                false));
    }

    private TransicaoUtils() {
        throw new IllegalStateException(TransicaoUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<TransicaoUnidadeMotivos> getListDeTransicoesPossiveis() {
        return TRANSICOES_POSSIVEIS;
    }
}
