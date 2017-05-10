package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

import java.time.Duration;

/**
 * Created by jean on 01/09/16.
 */
public class TempoRotaAcumuladoMedia extends IndicadorTempoAcumuladoMedia {


    public static final String TEMPO_ROTA_ACUMULADO_MEDIA = "TEMPO_ROTA_ACUMULADO_MEDIA";

    public TempoRotaAcumuladoMedia() {
        super();
    }

    public String getTipo(){
        return TEMPO_ROTA_ACUMULADO_MEDIA;
    }

    @Override
    public TempoRotaAcumuladoMedia setMeta(Duration meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoRotaAcumuladoMedia setResultado(Duration resultado) {
        super.setResultado(resultado);
        return this;
    }

}
