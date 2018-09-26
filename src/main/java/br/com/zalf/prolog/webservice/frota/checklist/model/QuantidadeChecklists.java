package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class QuantidadeChecklists {
    @NotNull
    private final LocalDate data;
    @NotNull
    private final String dataFormatada;
    private final int totalChecklistsSaida;
    private final int totalChecklistsRetorno;

    public QuantidadeChecklists(@NotNull final LocalDate data,
                                @NotNull final String dataFormatada,
                                final int totalChecklistsSaida,
                                final int totalChecklistsRetorno) {
        this.data = data;
        this.dataFormatada = dataFormatada;
        this.totalChecklistsSaida = totalChecklistsSaida;
        this.totalChecklistsRetorno = totalChecklistsRetorno;
    }

    @NotNull
    public LocalDate getData() {
        return data;
    }

    @NotNull
    public String getDataFormatada() {
        return dataFormatada;
    }

    public int getTotalChecklistsSaida() {
        return totalChecklistsSaida;
    }

    public int getTotalChecklistsRetorno() {
        return totalChecklistsRetorno;
    }

    public boolean teveChecklistsRealizados() {
        return totalChecklistsSaida > 0 || totalChecklistsRetorno > 0;
    }
}