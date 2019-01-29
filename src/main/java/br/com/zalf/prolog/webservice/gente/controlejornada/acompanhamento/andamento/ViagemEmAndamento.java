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
    @NotNull
    private final String formaCalculoJornadaBruta;
    @NotNull
    private final String formaCalculoJornadaLiquida;

    public ViagemEmAndamento(@NotNull final List<ColaboradorEmViagem> colaboradoresEmViagem,
                             final int qtdViagensEmAndamento,
                             @NotNull final String formaCalculoJornadaBruta,
                             @NotNull final String formaCalculoJornadaLiquida) {
        this.colaboradoresEmViagem = colaboradoresEmViagem;
        this.qtdViagensEmAndamento = qtdViagensEmAndamento;
        this.formaCalculoJornadaBruta = formaCalculoJornadaBruta;
        this.formaCalculoJornadaLiquida = formaCalculoJornadaLiquida;
    }

    @NotNull
    public static ViagemEmAndamento createDummy() {
        final List<ColaboradorEmViagem> colaboradores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            colaboradores.add(ColaboradorEmViagem.createDummy());
        }

        return new ViagemEmAndamento(
                colaboradores,
                colaboradores.size(),
                "Jornada Bruta = Tempo Total Jornada - Refeição - Descarga",
                "Jornada Líquida = Jornada Bruta - Descanso");
    }

    @NotNull
    public List<ColaboradorEmViagem> getColaboradoresEmViagem() {
        return colaboradoresEmViagem;
    }

    public int getQtdViagensEmAndamento() {
        return qtdViagensEmAndamento;
    }

    @NotNull
    public String getFormaCalculoJornadaBruta() {
        return formaCalculoJornadaBruta;
    }

    @NotNull
    public String getFormaCalculoJornadaLiquida() {
        return formaCalculoJornadaLiquida;
    }
}