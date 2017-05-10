package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.Pneu;

/**
 * Created by jean on 04/04/16.
 */
public class Servico {
    public static final String TIPO_CALIBRAGEM = "calibragem";
    public static final String TIPO_MOVIMENTACAO = "movimentacao";
    public static final String TIPO_INSPECAO = "inspecao";
    public static final String TIPO_AMBOS = "ambos";
    private Long codAfericao;
    private String tipo;
    private Pneu pneu;
    private Long cpfMecanico;
    private int qtApontamentos;
    private long kmVeiculo;

    /**
     * Armazena o tempo que o colaborador levou para realizar esse serviço, em milisegundos
     */
    private long tempoRealizacaoServicoInMillis;

    public Servico() {
    }

    public Long getCpfMecanico() {
        return cpfMecanico;
    }

    public void setCpfMecanico(Long cpfMecanico) {
        this.cpfMecanico = cpfMecanico;
    }

    public long getKmVeiculo() {
        return kmVeiculo;
    }

    public void setKmVeiculo(long kmVeiculo) {
        this.kmVeiculo = kmVeiculo;
    }

    public Long getCodAfericao() {
        return codAfericao;
    }

    public void setCodAfericao(Long codAfericao) {
        this.codAfericao = codAfericao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Pneu getPneu() {
        return pneu;
    }

    public void setPneu(Pneu pneu) {
        this.pneu = pneu;
    }

    public int getQtApontamentos() {
        return qtApontamentos;
    }

    public void setQtApontamentos(int qtApontamentos) {
        this.qtApontamentos = qtApontamentos;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "codAfericao=" + codAfericao +
                ", tipo='" + tipo + '\'' +
                ", pneu=" + pneu +
                ", cpfMecanico=" + cpfMecanico +
                ", qtApontamentos=" + qtApontamentos +
                ", kmVeiculo=" + kmVeiculo +
                '}';
    }

    public long getTempoRealizacaoServicoInMillis() {
        return tempoRealizacaoServicoInMillis;
    }

    public void setTempoRealizacaoServicoInMillis(long tempoRealizacaoServicoInMillis) {
        this.tempoRealizacaoServicoInMillis = tempoRealizacaoServicoInMillis;
    }
}
