package br.com.empresa.oprojeto.webservice.domain.indicadores;


import java.util.Date;

public class Jornada extends Indicador {
    private Date tempoMatinal;
    private Date tempoRota;
    private Date tempoFechamento;

    public Jornada(Date tempoMatinal, Date tempoRota, Date tempoFechamento) {
        this.tempoMatinal = tempoMatinal;
        this.tempoRota = tempoRota;
        this.tempoFechamento = tempoFechamento;
    }

    public Jornada(double meta, double resultado, Date tempoMatinal, Date tempoRota, Date tempoFechamento) {
        super(meta, resultado);
        this.tempoMatinal = tempoMatinal;
        this.tempoRota = tempoRota;
        this.tempoFechamento = tempoFechamento;
    }

    public Date getTempoMatinal() {
        return tempoMatinal;
    }

    public void setTempoMatinal(Date tempoMatinal) {
        this.tempoMatinal = tempoMatinal;
    }

    public Date getTempoRota() {
        return tempoRota;
    }

    public void setTempoRota(Date tempoRota) {
        this.tempoRota = tempoRota;
    }

    public Date getTempoFechamento() {
        return tempoFechamento;
    }

    public void setTempoFechamento(Date tempoFechamento) {
        this.tempoFechamento = tempoFechamento;
    }

    @Override
    public double calculaResultado() {
        return 0;
    }
}
