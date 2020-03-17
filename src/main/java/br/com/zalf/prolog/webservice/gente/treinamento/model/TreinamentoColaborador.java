package br.com.zalf.prolog.webservice.gente.treinamento.model;



import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.time.LocalDateTime;

/**
 * Classe usada para armazenar a data de visualização de um treinamento por determinado colaborador.
 */

public class TreinamentoColaborador {
    private Long codTreinamento;
    private Colaborador colaborador;
    private LocalDateTime dataVisualizacao;

    public TreinamentoColaborador() {

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

    public LocalDateTime getDataVisualizacao() {
        return dataVisualizacao;
    }

    public void setDataVisualizacao(LocalDateTime dataVisualizacao) {
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
