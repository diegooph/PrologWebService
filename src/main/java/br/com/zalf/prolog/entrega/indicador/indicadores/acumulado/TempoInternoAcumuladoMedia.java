package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

import java.time.Duration;

/**
 * Created by jean on 01/09/16.
 */
public class TempoInternoAcumuladoMedia extends IndicadorTempoAcumuladoMedia {

    public static final String TEMPO_INTERNO_ACUMULADO_MEDIA = "TEMPO_INTERNO_ACUMULADO_MEDIA";

    public TempoInternoAcumuladoMedia() {
        super();
    }

    public String getTipo(){
        return TEMPO_INTERNO_ACUMULADO_MEDIA;
    }

    @Override
    public TempoInternoAcumuladoMedia setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public IndicadorTempoAcumuladoMedia setResultado(Duration resultado) {
        super.setResultado(resultado);
        return this;
    }
}
