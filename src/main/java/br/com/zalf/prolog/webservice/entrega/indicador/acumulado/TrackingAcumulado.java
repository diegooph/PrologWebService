package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class TrackingAcumulado extends IndicadorQtdAcumulado{



    public static final String TRACKING_ACUMULADO = "TRACKING_ACUMULADO";

    public String getTipo(){
        return TRACKING_ACUMULADO;
    }

    @Override
    public TrackingAcumulado setTotalOk(int totalOk) {
        super.setTotalOk(totalOk);
        return this;
    }

    @Override
    public TrackingAcumulado setTotalNok(int totalNok) {
        super.setTotalNok(totalNok);
        return this;
    }

    @Override
    public TrackingAcumulado setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public TrackingAcumulado setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }

    public void calculaResultado(){
        /*
        ok = hl entregues
        nok = hl devolvidos
        resultado = nok / total
         */
        this.setResultado(getTotal() > 0 ? (double)getTotalOk() / getTotal() : 0);
        super.setBateuMeta(this.getResultado() >= this.getMeta());
    }

}
