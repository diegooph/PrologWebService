package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 01/09/16.
 */
public class JornadaAcumuladoMapas extends IndicadorTempoAcumuladoMapas {

    public static final String JORNADA_ACUMULADO_MAPAS = "JORNADA_ACUMULADO_MAPAS";

    public JornadaAcumuladoMapas() {
        super();
    }

    public String getTipo(){
        return JORNADA_ACUMULADO_MAPAS;
    }

    public void calculaResultado(){
        if(getTotal() > 0){
            super.setResultado((double)getMapasOk() / getTotal());
            super.setBateuMeta(getResultado() >= getMeta());
        }else{
            super.setResultado(0);
            super.setBateuMeta(false);
        }
    }

    @Override
    public JornadaAcumuladoMapas setMapasOk(int mapasOk) {
        super.setMapasOk(mapasOk);
        return this;
    }

    @Override
    public JornadaAcumuladoMapas setMapasNok(int mapasNok) {
        super.setMapasNok(mapasNok);
        return this;
    }

    @Override
    public JornadaAcumuladoMapas setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public JornadaAcumuladoMapas setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }
}

