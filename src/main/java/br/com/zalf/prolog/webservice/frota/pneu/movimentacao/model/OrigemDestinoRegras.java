package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public enum OrigemDestinoRegras {
    ONE(OrigemDestinoConstants.VEICULO, OrigemDestinoConstants.ESTOQUE, OrigemDestinoConstants.DESCARTE, OrigemDestinoConstants.ANALISE, OrigemDestinoConstants.VEICULO),
    TWO(OrigemDestinoConstants.ESTOQUE, OrigemDestinoConstants.VEICULO, OrigemDestinoConstants.DESCARTE, OrigemDestinoConstants.ANALISE),
    THREE(OrigemDestinoConstants.ANALISE, OrigemDestinoConstants.ESTOQUE, OrigemDestinoConstants.DESCARTE),
    FOUR(OrigemDestinoConstants.DESCARTE, null);

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