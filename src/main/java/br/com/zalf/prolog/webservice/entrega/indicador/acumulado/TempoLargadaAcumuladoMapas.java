package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by jean on 01/09/16.
 */
public class TempoLargadaAcumuladoMapas extends IndicadorTempoAcumuladoMapas {

    public static final String TEMPO_LARGADA_ACUMULADO_MAPAS = "TEMPO_LARGADA_ACUMULADO_MAPAS";

    public TempoLargadaAcumuladoMapas() {
        super();
    }

    public String getTipo(){
        return TEMPO_LARGADA_ACUMULADO_MAPAS;
    }

    public void calculaResultado(){

        if (getTotal() > 0){
            super.setResultado((double)getMapasOk() / getTotal());
            super.setBateuMeta(getResultado() >= getMeta());
        }else{
            super.setResultado(0);
            super.setBateuMeta(false);
        }
    }

    @Override
    public TempoLargadaAcumuladoMapas setMapasOk(int mapasOk) {
        super.setMapasOk(mapasOk);
        return this;
    }

    @Override
    public TempoLargadaAcumuladoMapas setMapasNok(int mapasNok) {
        super.setMapasNok(mapasNok);
        return this;
    }

    @Override
    public TempoLargadaAcumuladoMapas setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoLargadaAcumuladoMapas setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }
}
