package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public class Intervalo {
    private Long codigo;
    private TipoIntervalo tipo;
    private Date dataHoraInicio;
    private Date dataHoraFim;
    private Colaborador colaborador;
    private List<EdicaoIntervalo> edicoes;
    private String justificativaEstouro;
    private boolean valido;
    private Duration tempoDecorrido;

    public Intervalo() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public TipoIntervalo getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntervalo tipo) {
        this.tipo = tipo;
    }

    public Date getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Date dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Date getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Date dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public List<EdicaoIntervalo> getEdicoes() {
        return edicoes;
    }

    public void setEdicoes(List<EdicaoIntervalo> edicoes) {
        this.edicoes = edicoes;
    }

    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    public void setJustificativaEstouro(String justificativaEstouro) {
        this.justificativaEstouro = justificativaEstouro;
    }

    public Duration getTempoDecorrido() {
        return tempoDecorrido;
    }

    public void setTempoDecorrido(Duration tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }
}