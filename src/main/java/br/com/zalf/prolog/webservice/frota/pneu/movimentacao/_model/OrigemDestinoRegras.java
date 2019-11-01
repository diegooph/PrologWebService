package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
public enum OrigemDestinoRegras {
    ONE(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.DESCARTE, OrigemDestinoEnum.ANALISE, OrigemDestinoEnum.VEICULO),
    TWO(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.DESCARTE, OrigemDestinoEnum.ANALISE),
    THREE(OrigemDestinoEnum.ANALISE, OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.DESCARTE),
    // Cast utilizado para remover alerta de parâmetro varargs.
    FOUR(OrigemDestinoEnum.DESCARTE, (OrigemDestinoEnum) null);

    @NotNull
    private final OrigemDestinoEnum origem;
    @Nullable
    private final List<OrigemDestinoEnum> destinos;

    OrigemDestinoRegras(@NotNull OrigemDestinoEnum origem, @Nullable OrigemDestinoEnum... destinos) {
        this.origem = origem;

        if (destinos != null) {
            this.destinos = Arrays.asList(destinos);
        } else {
            this.destinos = null;
        }
    }

    public OrigemDestinoEnum getOrigem() {
        return origem;
    }

    public List<OrigemDestinoEnum> getDestinos() {
        return destinos;
    }
}