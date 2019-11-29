package br.com.zalf.prolog.webservice.entrega.metas;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created by jean on 16/12/15.
 * Consolidado com as metas de todos os indicadores.
 */
public final class Metas {
    private final double metaDevHl;
    private final double metaDevPdv;
    private final double metaTracking;
    private final int metaRaioTracking;
    private final double metaTempoLargadaMapas;
    private final double metaTempoRotaMapas;
    private final double metaTempoInternoMapas;
    private final double metaJornadaLiquidaMapas;
    @NotNull
    private final Duration metaTempoLargadaHoras;
    @NotNull
    private final Duration metaTempoRotaHoras;
    @NotNull
    private final Duration metaTempoInternoHoras;
    @NotNull
    private final Duration metaJornadaLiquidaHoras;
    private final int metaCaixaViagem;
    private final double metaDispersaoKm;
    private final double metaDispersaoTempo;
    private final double metaDevNf;

    public Metas(final double metaDevHl,
                 final double metaDevPdv,
                 final double metaTracking,
                 final int metaRaioTracking,
                 final double metaTempoLargadaMapas,
                 final double metaTempoRotaMapas,
                 final double metaTempoInternoMapas,
                 final double metaJornadaLiquidaMapas,
                 @NotNull final Duration metaTempoLargadaHoras,
                 @NotNull final Duration metaTempoRotaHoras,
                 @NotNull final Duration metaTempoInternoHoras,
                 @NotNull final Duration metaJornadaLiquidaHoras,
                 final int metaCaixaViagem,
                 final double metaDispersaoKm,
                 final double metaDispersaoTempo,
                 final double metaDevNf) {
        this.metaDevHl = metaDevHl;
        this.metaDevPdv = metaDevPdv;
        this.metaTracking = metaTracking;
        this.metaRaioTracking = metaRaioTracking;
        this.metaTempoLargadaMapas = metaTempoLargadaMapas;
        this.metaTempoRotaMapas = metaTempoRotaMapas;
        this.metaTempoInternoMapas = metaTempoInternoMapas;
        this.metaJornadaLiquidaMapas = metaJornadaLiquidaMapas;
        this.metaTempoLargadaHoras = metaTempoLargadaHoras;
        this.metaTempoRotaHoras = metaTempoRotaHoras;
        this.metaTempoInternoHoras = metaTempoInternoHoras;
        this.metaJornadaLiquidaHoras = metaJornadaLiquidaHoras;
        this.metaCaixaViagem = metaCaixaViagem;
        this.metaDispersaoKm = metaDispersaoKm;
        this.metaDispersaoTempo = metaDispersaoTempo;
        this.metaDevNf = metaDevNf;
    }

    public double getMetaDevHl() {
        return metaDevHl;
    }

    public double getMetaDevPdv() {
        return metaDevPdv;
    }

    public double getMetaTracking() {
        return metaTracking;
    }

    public int getMetaRaioTracking() {
        return metaRaioTracking;
    }

    public double getMetaTempoLargadaMapas() {
        return metaTempoLargadaMapas;
    }

    public double getMetaTempoRotaMapas() {
        return metaTempoRotaMapas;
    }

    public double getMetaTempoInternoMapas() {
        return metaTempoInternoMapas;
    }

    public double getMetaJornadaLiquidaMapas() {
        return metaJornadaLiquidaMapas;
    }

    @NotNull
    public Duration getMetaTempoLargadaHoras() {
        return metaTempoLargadaHoras;
    }

    @NotNull
    public Duration getMetaTempoRotaHoras() {
        return metaTempoRotaHoras;
    }

    @NotNull
    public Duration getMetaTempoInternoHoras() {
        return metaTempoInternoHoras;
    }

    @NotNull
    public Duration getMetaJornadaLiquidaHoras() {
        return metaJornadaLiquidaHoras;
    }

    public int getMetaCaixaViagem() {
        return metaCaixaViagem;
    }

    public double getMetaDispersaoKm() {
        return metaDispersaoKm;
    }

    public double getMetaDispersaoTempo() {
        return metaDispersaoTempo;
    }

    public double getMetaDevNf() {
        return metaDevNf;
    }
}