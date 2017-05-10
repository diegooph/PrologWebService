package br.com.zalf.prolog.webservice.entrega.metas;

import java.time.Duration;

/**
 * Created by jean on 16/12/15.
 * Consolidado com as metas de todos os indicadores.
 */
public class Metas {

    public double metaDevHl;
    public double metaDevPdv;
    public double metaTracking;
    public int metaRaioTracking;
    public double metaTempoLargadaMapas;
    public double metaTempoRotaMapas;
    public double metaTempoInternoMapas;
    public double metaJornadaLiquidaMapas;
    public Duration metaTempoLargadaHoras;
    public Duration metaTempoRotaHoras;
    public Duration metaTempoInternoHoras;
    public Duration metaJornadaLiquidaHoras;
    public int metaCaixaViagem;
    public double metaDispersaoKm;
    public double metaDispersaoTempo;
    public double metaDevNf;

    public Metas() {
    }
}