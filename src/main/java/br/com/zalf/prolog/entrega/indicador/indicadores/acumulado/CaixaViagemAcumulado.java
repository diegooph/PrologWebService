package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class CaixaViagemAcumulado extends IndicadorAcumulado{

    public static final String CAIXA_VIAGEM_ACUMULADO = "CAIXA_VIAGEM_ACUMULADO";

    private int cxsCarregadasTotal;
    private int viagensTotal;
    private double resultado;
    private double meta;

    public CaixaViagemAcumulado() {
        super();
    }

    public String getTipo(){
        return CAIXA_VIAGEM_ACUMULADO;
    }

    public int getCxsCarregadasTotal() {
        return cxsCarregadasTotal;
    }

    public CaixaViagemAcumulado setCxsCarregadasTotal(int cxsCarregadasTotal) {
        this.cxsCarregadasTotal = cxsCarregadasTotal;
        return this;
    }

    public int getViagensTotal() {
        return viagensTotal;
    }

    public CaixaViagemAcumulado setViagensTotal(int viagensTotal) {
        this.viagensTotal = viagensTotal;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public CaixaViagemAcumulado setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public CaixaViagemAcumulado setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public CaixaViagemAcumulado setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    public void calculaResultado(){
        resultado = viagensTotal > 0 ? cxsCarregadasTotal / viagensTotal : 0;
        super.setBateuMeta(resultado >= meta);
    }

    @Override
    public String toString() {
        return "CaixaViagemAcumulado{" +
                "cxsCarregadasTotal=" + cxsCarregadasTotal +
                ", viagensTotal=" + viagensTotal +
                ", resultado=" + resultado +
                ", meta=" + meta + " " +
                super.toString() +
                '}';
    }
}
