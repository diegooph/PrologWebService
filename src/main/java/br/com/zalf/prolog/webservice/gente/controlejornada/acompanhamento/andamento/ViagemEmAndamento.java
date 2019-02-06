package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ViagemEmAndamento {
    @NotNull
    private final List<ColaboradorEmViagem> colaboradoresEmViagem;
    private final int qtdViagensEmAndamento;

    public ViagemEmAndamento(@NotNull final List<ColaboradorEmViagem> colaboradoresEmViagem,
                             final int qtdViagensEmAndamento) {
        this.colaboradoresEmViagem = colaboradoresEmViagem;
        this.qtdViagensEmAndamento = qtdViagensEmAndamento;
    }

    @NotNull
    public static ViagemEmAndamento createDummy() {
        final List<ColaboradorEmViagem> colaboradores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            colaboradores.add(ColaboradorEmViagem.createDummy());
        }

        return new ViagemEmAndamento(colaboradores, colaboradores.size());
    }

    @NotNull
    public List<ColaboradorEmViagem> getColaboradoresEmViagem() {
        return colaboradoresEmViagem;
    }

    public int getQtdViagensEmAndamento() {
        return qtdViagensEmAndamento;
    }
}