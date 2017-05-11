package br.com.zalf.prolog.webservice.gente.treinamento.model;



import br.com.zalf.prolog.webservice.colaborador.Colaborador;

import java.util.Date;

/**
 * Classe usada para armazenar a data de visualização de um treinamento por determinado colaborador.
 */

public class TreinamentoColaborador {
    private Long codTreinamento;
    private Colaborador colaborador;
    private Date dataVisualizacao;

    public TreinamentoColaborador() {
    }

    public TreinamentoColaborador(Long codTreinamento, Colaborador colaborador, Date dataVisualizacao) {
        this.codTreinamento = codTreinamento;
        this.colaborador = colaborador;
        this.dataVisualizacao = dataVisualizacao;
    }

    public Long getCodTreinamento() {
        return codTreinamento;
    }

    public void setCodTreinamento(Long codTreinamento) {
        this.codTreinamento = codTreinamento;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Date getDataVisualizacao() {
        return dataVisualizacao;
    }

    public void setDataVisualizacao(Date dataVisualizacao) {
        this.dataVisualizacao = dataVisualizacao;
    }

    @Override
    public String toString() {
        return "TreinamentoColaborador{" +
                "codTreinamento=" + codTreinamento +
                ", colaborador=" + colaborador +
                ", dataVisualizacao=" + dataVisualizacao +
                '}';
    }
}
