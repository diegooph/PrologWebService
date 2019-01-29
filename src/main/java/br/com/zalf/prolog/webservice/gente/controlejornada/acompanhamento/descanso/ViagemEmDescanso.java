package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ViagemEmDescanso {
    @NotNull
    private final List<ColaboradorEmDescanso> colaboradoresEmDescanso;
    private final int qtdColaboradoresEmDescanso;

    public ViagemEmDescanso(@NotNull final List<ColaboradorEmDescanso> colaboradoresEmDescanso,
                            final int qtdColaboradoresEmDescanso) {
        this.colaboradoresEmDescanso = colaboradoresEmDescanso;
        this.qtdColaboradoresEmDescanso = qtdColaboradoresEmDescanso;
    }

    @NotNull
    public List<ColaboradorEmDescanso> getColaboradoresEmDescanso() {
        return colaboradoresEmDescanso;
    }

    public int getQtdColaboradoresEmDescanso() {
        return qtdColaboradoresEmDescanso;
    }
}