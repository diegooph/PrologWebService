package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento;

import org.jetbrains.annotations.NotNull;

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