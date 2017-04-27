package br.com.zalf.prolog.frota.pneu.movimentacao;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.List;

import static br.com.zalf.prolog.frota.pneu.movimentacao.OrigemDestinoConstants.*;

/**
 * Created by Zart on 03/03/17.
 */
public enum OrigemDestinoRegras {
    ONE(VEICULO, ESTOQUE, DESCARTE, ANALISE, VEICULO),
    TWO(ESTOQUE, VEICULO, DESCARTE, ANALISE),
    THREE(ANALISE, ESTOQUE, DESCARTE),
    FOUR(DESCARTE, null);

    @NotNull
    private final String origem;
    @Nullable
    private final List<String> destinos;

    OrigemDestinoRegras(@NotNull String origem, @Nullable String... destinos) {
        this.origem = origem;

        if (destinos != null) {
            this.destinos = Arrays.asList(destinos);
        } else {
            this.destinos = null;
        }
    }

    public String getOrigem() {
        return origem;
    }

    public List<String> getDestinos() {
        return destinos;
    }
}