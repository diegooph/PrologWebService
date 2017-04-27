package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

import java.time.Duration;

/**
 * Created by jean on 01/09/16.
 */
public class TempoLargadaAcumuladoMedia extends IndicadorTempoAcumuladoMedia {

    public static final String TEMPO_LARGADA_ACUMULADO_MEDIA = "TEMPO_LARGADA_ACUMULADO_MEDIA";

    public TempoLargadaAcumuladoMedia() {
        super();
    }

    public String getTipo(){
        return TEMPO_LARGADA_ACUMULADO_MEDIA;
    }

    @Override
    public TempoLargadaAcumuladoMedia setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoLargadaAcumuladoMedia setResultado(Duration resultado) {
        super.setResultado(resultado);
        return this;
    }

}
