package br.com.zalf.prolog.webservice.gente.controleIntervalo.model;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;

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
    private boolean ativo;
    private List<EdicaoIntervalo> logEdicoes;

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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<EdicaoIntervalo> getLogEdicoes() {
        return logEdicoes;
    }

    public void setLogEdicoes(List<EdicaoIntervalo> logEdicoes) {
        this.logEdicoes = logEdicoes;
    }
}
