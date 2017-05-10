package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

import java.time.Duration;

/**
 * Created by jean on 01/09/16.
 */
public class JornadaAcumuladoMedia extends IndicadorTempoAcumuladoMedia {

    public static final String JORNADA_ACUMULADO_MEDIA = "JORNADA_ACUMULADO_MEDIA";

    public JornadaAcumuladoMedia() {
        super();
    }

    public String getTipo(){
        return JORNADA_ACUMULADO_MEDIA;
    }

    @Override
    public JornadaAcumuladoMedia setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public JornadaAcumuladoMedia setResultado(Duration resultado) {
        super.setResultado(resultado);
        return this;
    }
}

