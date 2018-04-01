package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 22/05/17.
 */
public class EixoVeiculo {
    @NotNull
    private final TipoEixoVeiculo tipoEixo;
    private final int quantidadePneus;
    private final int posicao;
    private final boolean direcional;

    public EixoVeiculo(@NotNull final TipoEixoVeiculo tipoEixo,
                       final int quantidadePneus,
                       final int posicao,
                       final boolean direcional) {
        this.tipoEixo = tipoEixo;
        this.quantidadePneus = quantidadePneus;
        this.posicao = posicao;
        this.direcional = direcional;
    }

    @NotNull
    public TipoEixoVeiculo getTipoEixo() {
        return tipoEixo;
    }

    public int getQuantidadePneus() {
        return quantidadePneus;
    }

    public int getPosicao() {
        return posicao;
    }

    public boolean isDirecional() {
        return direcional;
    }
}