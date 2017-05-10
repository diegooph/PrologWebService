package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by jean on 01/09/16.
 */
public class TempoRotaAcumuladoMapas extends IndicadorTempoAcumuladoMapas {


    public static final String TEMPO_ROTA_ACUMULADO_MAPAS = "TEMPO_ROTA_ACUMULADO_MAPAS";

    public TempoRotaAcumuladoMapas() {
        super();
    }

    public String getTipo(){
        return TEMPO_ROTA_ACUMULADO_MAPAS;
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
    public TempoRotaAcumuladoMapas setMapasOk(int mapasOk) {
        super.setMapasOk(mapasOk);
        return this;
    }

    @Override
    public TempoRotaAcumuladoMapas setMapasNok(int mapasNok) {
        super.setMapasNok(mapasNok);
        return this;
    }

    @Override
    public TempoRotaAcumuladoMapas setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TempoRotaAcumuladoMapas setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }
}
