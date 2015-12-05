package br.com.empresa.oprojeto.webservice.domain.treinamento;



import java.util.Date;

public class TreinamentoColaborador {
    private int codTreinamento;
    private int cpfColaborador;
    private Date dataVisualizacao;

    public TreinamentoColaborador() {
    }

    public TreinamentoColaborador(int codTreinamento, int cpfColaborador, Date dataVisualizacao) {
        this.codTreinamento = codTreinamento;
        this.cpfColaborador = cpfColaborador;
        this.dataVisualizacao = dataVisualizacao;
    }

    public int getCodTreinamento() {
        return codTreinamento;
    }

    public void setCodTreinamento(int codTreinamento) {
        this.codTreinamento = codTreinamento;
    }

    public int getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(int cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public Date getDataVisualizacao() {
        return dataVisualizacao;
    }

    public void setDataVisualizacao(Date dataVisualizacao) {
        this.dataVisualizacao = dataVisualizacao;
    }
}
