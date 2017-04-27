package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 01/09/16.
 */
public class TempoInternoAcumuladoMapas extends IndicadorTempoAcumuladoMapas {

    public static final String TEMPO_INTERNO_ACUMULADO_MAPAS = "TEMPO_INTERNO_ACUMULADO_MAPAS";

    public TempoInternoAcumuladoMapas() {
        super();
    }

    public String getTipo(){
        return TEMPO_INTERNO_ACUMULADO_MAPAS;
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
    public TempoInternoAcumuladoMapas setMapasOk(int mapasOk) {
        super.setMapasOk(mapasOk);
        return this;
    }

    @Override
    public TempoInternoAcumuladoMapas setMapasNok(int mapasNok) {
        super.setMapasNok(mapasNok);
        return this;
    }

    @Override
    public TempoInternoAcumuladoMapas setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoInternoAcumuladoMapas setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }
}
