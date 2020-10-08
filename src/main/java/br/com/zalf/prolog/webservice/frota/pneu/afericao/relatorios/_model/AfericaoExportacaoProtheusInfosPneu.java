package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class AfericaoExportacaoProtheusInfosPneu {
    @NotNull
    private final String cabecalhoLinhaDois;
    @NotNull
    private final String codClientePneu;
    @Nullable
    private final String nomenclaturaPosicao;
    @Nullable
    private final Double calibragemAferida;
    @Nullable
    private final Double calibragemRealizada;
    @Nullable
    private final Double alturaSulcoInterno;
    @Nullable
    private final Double alturaSulcoCentralInterno;
    @Nullable
    private final Double alturaSulcoExterno;

    @Override
    public String toString() {
        return "AfericaoExportacaoProtheusInfosPneu{" +
                "cabecalhoLinhaDois='" + cabecalhoLinhaDois + '\'' +
                ", codClientePneu='" + codClientePneu + '\'' +
                ", nomenclaturaPosicao='" + nomenclaturaPosicao + '\'' +
                ", calibragemAferida=" + calibragemAferida +
                ", calibragemRealizada=" + calibragemRealizada +
                ", alturaSulcoInterno=" + alturaSulcoInterno +
                ", alturaSulcoCentralInterno=" + alturaSulcoCentralInterno +
                ", alturaSulcoExterno=" + alturaSulcoExterno +
                '}';
    }
}
