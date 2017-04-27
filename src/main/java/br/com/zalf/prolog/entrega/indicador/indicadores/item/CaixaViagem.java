package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class CaixaViagem extends IndicadorItem {

    public static final String CAIXA_VIAGEM = "CAIXA_VIAGEM";

    private int cxsCarregadas;
    private int viagens;
    private double resultado;
    private double meta;

    public CaixaViagem() {
        super();
    }

    public String getTipo(){
        return CAIXA_VIAGEM;
    }

    public int getCxsCarregadas() {
        return cxsCarregadas;
    }

    public CaixaViagem setCxsCarregadas(int cxsCarregadas) {
        this.cxsCarregadas = cxsCarregadas;
        return this;
    }

    public int getViagens() {
        return viagens;
    }

    public CaixaViagem setViagens(int viagens) {
        this.viagens = viagens;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public CaixaViagem setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public CaixaViagem setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public void calculaResultado(){
        if(viagens > 0) {
            resultado = cxsCarregadas / viagens;
            setBateuMeta(resultado >= meta);
        }
    }

    @Override
    public CaixaViagem setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public CaixaViagem setMapa(int mapa){
        super.setMapa(mapa);
        return this;
    }

    @Override
    public CaixaViagem setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public String toString() {
        return "CaixaViagem{" +
                super.toString() +
                "cxsCarregadas=" + cxsCarregadas +
                ", viagens=" + viagens +
                ", resultado=" + resultado +
                ", meta=" + meta +
                '}';
    }
}

