package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class AfericaoExportacao {
    @NotNull
    private final String cabecalhoLinhaUm;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final LocalDate dataAfericao;
    @NotNull
    private final LocalTime horaAfericao;
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
        return "AfericaoExportacao{" +
                "cabecalhoLinhaUm='" + cabecalhoLinhaUm + '\'' +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", dataAfericao=" + dataAfericao +
                ", horaAfericao=" + horaAfericao +
                ", cabecalhoLinhaDois='" + cabecalhoLinhaDois + '\'' +
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
