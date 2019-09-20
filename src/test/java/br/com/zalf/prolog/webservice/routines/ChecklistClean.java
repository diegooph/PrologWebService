package br.com.zalf.prolog.webservice.routines;

import java.util.Date;

/**
 * Created on 27/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistClean {

    public static final char TIPO_SAIDA = 'S';
    public static final char TIPO_RETORNO = 'R';

    private Long codUnidade;
    private Long codModelo;
    private Long codigo;
    private Date data;
    private Long cpfColaborador;
    private String placaVeiculo;
    private char tipo;
    private long tempoRealizacaoCheckInMillis;
    private long kmAtualVeiculo;

    public ChecklistClean() {
    }

    public String generateKey() {
        final String unidade = String.valueOf(getCodUnidade());
        final String modelo = String.valueOf(getCodModelo());
        final String cpf = String.valueOf(getCpfColaborador());
        final String tipo = String.valueOf(getTipo());
        return "U->" + unidade + " M->" + modelo + " CPF->" + cpf + " T->" + tipo;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public Long getCodModelo() {
        return codModelo;
    }

    public void setCodModelo(Long codModelo) {
        this.codModelo = codModelo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(Long cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public long getTempoRealizacaoCheckInMillis() {
        return tempoRealizacaoCheckInMillis;
    }

    public void setTempoRealizacaoCheckInMillis(long tempoRealizacaoCheckInMillis) {
        this.tempoRealizacaoCheckInMillis = tempoRealizacaoCheckInMillis;
    }

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }

    public void setKmAtualVeiculo(long kmAtualVeiculo) {
        this.kmAtualVeiculo = kmAtualVeiculo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof ChecklistClean))
            return false;

        if (obj == this)
            return true;

        final ChecklistClean checklistClean = (ChecklistClean) obj;

        return !(codUnidade == null || checklistClean.codUnidade == null) && codUnidade.equals(checklistClean.codUnidade)
                && !(codModelo == null || checklistClean.codModelo == null) && codModelo.equals(checklistClean.codModelo)
                && !(cpfColaborador == null || checklistClean.cpfColaborador == null) && cpfColaborador.equals(checklistClean.cpfColaborador)
                && !(placaVeiculo == null || checklistClean.placaVeiculo == null) && placaVeiculo.equals(checklistClean.placaVeiculo)
                && tipo == checklistClean.tipo;
    }

    @Override
    public String toString() {
        return "Checklist{" +
                "codUnidade=" + codUnidade +
                ", codModelo=" + codModelo +
                ", codigo=" + codigo +
                ", CpfColaborador=" + cpfColaborador +
                ", data=" + data +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", tipo=" + tipo +
                ", kmAtualVeiculo=" + kmAtualVeiculo +
                ", tempoRealizacaoCheckInMillis=" + tempoRealizacaoCheckInMillis +
                '}';
    }
}
