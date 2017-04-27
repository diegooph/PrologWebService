package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

import java.time.Duration;

/**
 * Created by jean on 03/09/16.
 */
public class DispersaoTempoAcumuladoMedia extends IndicadorAcumulado {

    public static final String DISPERSAO_TEMPO_ACUMULADO_MEDIA = "DISPERSAO_TEMPO_ACUMULADO_MEDIA";

    private double meta;
    private Duration previsto;
    private Duration realizado;
    private Duration disperso;
    private double resultado;


    public DispersaoTempoAcumuladoMedia() {
        super();
    }

    public String getTipo(){
        return DISPERSAO_TEMPO_ACUMULADO_MEDIA;
    }

    public void calculaResultado(){
        disperso = realizado.minus(previsto);
        if(!disperso.isZero() && !previsto.isZero()){
            resultado = (float)realizado.minus(previsto).getSeconds() / previsto.getSeconds();
        }else {
            resultado = 0;
        }
        setBateuMeta(resultado <= meta);
    }

    public DispersaoTempoAcumuladoMedia setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public DispersaoTempoAcumuladoMedia setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public DispersaoTempoAcumuladoMedia setPrevisto(Duration previsto) {
        this.previsto = previsto;
        return this;
    }

    public DispersaoTempoAcumuladoMedia setRealizado(Duration realizado) {
        this.realizado = realizado;
        return this;
    }

    public Duration getDisperso() {
        return disperso;
    }

    public DispersaoTempoAcumuladoMedia setDisperso(Duration disperso) {
        this.disperso = disperso;
        return this;
    }
}
