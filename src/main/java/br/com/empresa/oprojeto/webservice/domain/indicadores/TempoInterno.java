package br.com.empresa.oprojeto.webservice.domain.indicadores;


import java.util.Date;

public class TempoInterno extends Indicador {
    private Date horaSaida;
    private Date horaEntrada;
    private Date tempoFechamento;

    public TempoInterno(Date horaSaida, Date horaEntrada, Date tempoFechamento) {
        this.horaSaida = horaSaida;
        this.horaEntrada = horaEntrada;
        this.tempoFechamento = tempoFechamento;
    }

    public TempoInterno(double meta, double resultado, Date horaSaida, Date horaEntrada, Date tempoFechamento) {
        super(meta, resultado);
        this.horaSaida = horaSaida;
        this.horaEntrada = horaEntrada;
        this.tempoFechamento = tempoFechamento;
    }

    public Date getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(Date horaSaida) {
        this.horaSaida = horaSaida;
    }

    public Date getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Date horaEntrada) {
        this.horaEntrada = horaEntrada;
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
