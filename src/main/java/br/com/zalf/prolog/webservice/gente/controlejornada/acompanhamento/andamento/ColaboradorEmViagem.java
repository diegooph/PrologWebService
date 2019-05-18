package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorEmViagem {
    @NotNull
    private final Long cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @NotNull
    private final LocalDateTime dataHoraInicioJornada;
    @NotNull
    @SerializedName("jornadaBrutaEmSegundos")
    private final Duration jornadaBruta;
    @NotNull
    @SerializedName("jornadaLiquidaEmSegundos")
    private final Duration jornadaLiquida;
    @NotNull
    private final List<MarcacaoDentroJornada> marcacoesDentroJornada;
    private final int qtdMarcacoesDentroJornada;
    private final boolean inicioJornadaAjustada;

    public ColaboradorEmViagem(@NotNull final Long cpfColaborador,
                               @NotNull final String nomeColaborador,
                               @NotNull final LocalDateTime dataHoraInicioJornada,
                               @NotNull final Duration jornadaBruta,
                               @NotNull final Duration jornadaLiquida,
                               @NotNull final List<MarcacaoDentroJornada> marcacoesDentroJornada,
                               final int qtdMarcacoesDentroJornada,
                               final boolean inicioJornadaAjustada) {
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.dataHoraInicioJornada = dataHoraInicioJornada;
        this.jornadaBruta = jornadaBruta;
        this.jornadaLiquida = jornadaLiquida;
        this.marcacoesDentroJornada = marcacoesDentroJornada;
        this.qtdMarcacoesDentroJornada = qtdMarcacoesDentroJornada;
        this.inicioJornadaAjustada = inicioJornadaAjustada;
    }

    @NotNull
    public static ColaboradorEmViagem createDummy() {
        final List<MarcacaoDentroJornada> marcacoesDentroJornada = new ArrayList<>();
        marcacoesDentroJornada.add(MarcacaoDentroJornada.createDummy(false));
        marcacoesDentroJornada.add(MarcacaoDentroJornada.createDummy(true));

        return new ColaboradorEmViagem(
                3383283194L,
                "João Carlos de Souza",
                LocalDateTime.now().minus(5, ChronoUnit.HOURS),
                Duration.ofHours(5),
                Duration.ofHours(4),
                marcacoesDentroJornada,
                marcacoesDentroJornada.size(),
                true);
    }

    @NotNull
    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @NotNull
    public LocalDateTime getDataHoraInicioJornada() {
        return dataHoraInicioJornada;
    }

    @NotNull
    public Duration getJornadaBruta() {
        return jornadaBruta;
    }

    @NotNull
    public Duration getJornadaLiquida() {
        return jornadaLiquida;
    }

    @NotNull
    public List<MarcacaoDentroJornada> getMarcacoesDentroJornada() {
        return marcacoesDentroJornada;
    }

    public int getQtdMarcacoesDentroJornada() {
        return qtdMarcacoesDentroJornada;
    }

    public boolean isInicioJornadaAjustada() {
        return inicioJornadaAjustada;
    }
}