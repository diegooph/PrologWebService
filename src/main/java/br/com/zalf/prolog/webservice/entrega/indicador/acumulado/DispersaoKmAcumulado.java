package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class DispersaoKmAcumulado extends IndicadorAcumulado {

    public static final String DISPERSAO_KM_ACUMULADO = "DISPERSAO_KM_ACUMULADO";

    private int kmPlanejadoTotal;
    private int kmPercorridoTotal;
    private double resultado;
    private double meta;

    public DispersaoKmAcumulado() {
        super();
    }

    public void calculaResultado(){
        resultado = kmPlanejadoTotal > 0 ? (double) (kmPercorridoTotal-kmPlanejadoTotal) / kmPlanejadoTotal : 0;
        super.setBateuMeta(resultado <= meta);
    }

    public String getTipo(){
        return DISPERSAO_KM_ACUMULADO;
    }

    public int getKmPlanejadoTotal() {
        return kmPlanejadoTotal;
    }

    public DispersaoKmAcumulado setKmPlanejadoTotal(int kmPlanejadoTotal) {
        this.kmPlanejadoTotal = kmPlanejadoTotal;
        return this;
    }

    public int getKmPercorridoTotal() {
        return kmPercorridoTotal;
    }

    public DispersaoKmAcumulado setKmPercorridoTotal(int kmPercorridoTotal) {
        this.kmPercorridoTotal = kmPercorridoTotal;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public DispersaoKmAcumulado setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public DispersaoKmAcumulado setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public String toString() {
        return "DispersaoKmAcumulado{" +
                "kmPlanejadoTotal=" + kmPlanejadoTotal +
                ", kmPercorridoTotal=" + kmPercorridoTotal +
                ", resultado=" + resultado +
                ", meta=" + meta +
                '}';
    }
}
