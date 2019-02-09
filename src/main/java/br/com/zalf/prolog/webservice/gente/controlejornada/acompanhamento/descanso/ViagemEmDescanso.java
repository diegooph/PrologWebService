package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    public static ViagemEmDescanso createDummy() {
        final List<ColaboradorEmDescanso> colaboradores = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            colaboradores.add(ColaboradorEmDescanso.createDummy(i % 2 == 0));
        }

        return new ViagemEmDescanso(colaboradores, colaboradores.size());
    }

    @NotNull
    public List<ColaboradorEmDescanso> getColaboradoresEmDescanso() {
        return colaboradoresEmDescanso;
    }

    public int getQtdColaboradoresEmDescanso() {
        return qtdColaboradoresEmDescanso;
    }
}