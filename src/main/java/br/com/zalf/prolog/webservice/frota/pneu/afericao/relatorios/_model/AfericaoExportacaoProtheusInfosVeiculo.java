package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class AfericaoExportacaoProtheusInfosVeiculo {
    @NotNull
    private final String cabecalhoLinhaUm;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final LocalDate dataAfericao;
    @NotNull
    private final LocalTime horaAfericao;

    @Override
    public String toString() {
        return "AfericaoExportacaoProtheusInfosVeiculo{" +
                "cabecalhoLinhaUm='" + cabecalhoLinhaUm + '\'' +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", dataAfericao=" + dataAfericao +
                ", horaAfericao=" + horaAfericao +
                '}';
    }
}
