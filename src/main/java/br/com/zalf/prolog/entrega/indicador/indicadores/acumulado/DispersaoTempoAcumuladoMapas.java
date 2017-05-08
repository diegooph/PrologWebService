package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class DispersaoTempoAcumuladoMapas extends IndicadorTempoAcumuladoMapas {

    public static final String DISPERSAO_TEMPO_ACUMULADO_MAPAS = "DISPERSAO_TEMPO_ACUMULADO_MAPAS";

    public DispersaoTempoAcumuladoMapas() {
        super();
    }

    public String getTipo(){
        return DISPERSAO_TEMPO_ACUMULADO_MAPAS;
    }

    public void calculaResultado(){

        if (getTotal() > 0){
            super.setResultado((double) getMapasOk() / getTotal());
            super.setBateuMeta(getResultado() <= getMeta());
        }else{
            super.setResultado(0);
            super.setBateuMeta(false);
        }
    }

    @Override
    public DispersaoTempoAcumuladoMapas setMapasOk(int mapasOk) {
        super.setMapasOk(mapasOk);
        return this;
    }

    @Override
    public DispersaoTempoAcumuladoMapas setMapasNok(int mapasNok) {
        super.setMapasNok(mapasNok);
        return this;
    }

    @Override
    public DispersaoTempoAcumuladoMapas setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DispersaoTempoAcumuladoMapas setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }
}
