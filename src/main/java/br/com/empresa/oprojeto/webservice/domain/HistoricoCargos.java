package br.com.empresa.oprojeto.webservice.domain;


import java.util.Date;

public class HistoricoCargos {
    private Date dataInicio;
    private Date dataFim;
    private int cpfColaborador;
    private long codFuncao;

    public HistoricoCargos() {
    }

    public HistoricoCargos(Date dataInicio, Date dataFim, int cpfColaborador, long codFuncao) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.cpfColaborador = cpfColaborador;
        this.codFuncao = codFuncao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public int getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(int cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public long getCodFuncao() {
        return codFuncao;
    }

    public void setCodFuncao(long codFuncao) {
        this.codFuncao = codFuncao;
    }
}
