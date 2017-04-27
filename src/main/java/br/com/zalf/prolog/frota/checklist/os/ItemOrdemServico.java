package br.com.zalf.prolog.frota.checklist.os;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.frota.checklist.PerguntaRespostaChecklist;

import java.util.Date;

/**
 * Created by jean on 25/07/16.
 * Itens que compõe uma OS
 */
public class ItemOrdemServico {

    public enum Status{
        RESOLVIDO("R"),
        PENDENTE("P");

        private final String s;

        Status(String s){this.s = s;}
        public String asString(){return s;}

        public static Status fromString(String text) throws IllegalArgumentException{
            if (text != null) {
                for (Status b : Status.values()) {
                    if (text.equalsIgnoreCase(b.s)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
        }
    }

    /**
     * Código da OS ao qual esse item pertence
     */
    private Long codOs;
    /**
     * Placa ao qual o item pertence
     */
    private String placa;
    /**
     * Pergunta e alternativa = item
     */
    private PerguntaRespostaChecklist pergunta;
    private Colaborador mecanico;
    /**
     * Data em que o item foi apontado pela primeira vez
     */
    private Date dataApontamento;
    /**
     * data e hora em que o item foi marcado como consertado
     */
    private Date dataHoraConserto;
    /**
     * Tempo que o item levou para ser consertado
     */
    private Tempo tempoRealizacaoConserto;
    /**
     * Km do veículo no momento em que o item foi fechado
     */
    private long kmVeiculoFechamento;
    private Status status;
    /**
     * Prazo em horas para conserto do item
     */
    private Tempo tempoLimiteResolucao;
    /**
     * Tempo restante para consertar o item, baseado na sua prioridade
     */
    private Tempo tempoRestante;
    /**
     * Observação do serviço realizado
     */
    private String feedbackResolucao;
    /**
     * Quantidade de apontamentos que um item tem
     */
    private int qtdApontamentos;

    public ItemOrdemServico() {
    }

    public int getQtdApontamentos() {
        return qtdApontamentos;
    }

    public void setQtdApontamentos(int qtdApontamentos) {
        this.qtdApontamentos = qtdApontamentos;
    }

    public Long getCodOs() {
        return codOs;
    }

    public void setCodOs(Long codOs) {
        this.codOs = codOs;
    }

    public PerguntaRespostaChecklist getPergunta() {
        return pergunta;
    }

    public void setPergunta(PerguntaRespostaChecklist pergunta) {
        this.pergunta = pergunta;
    }

    public Colaborador getMecanico() {
        return mecanico;
    }

    public void setMecanico(Colaborador mecanico) {
        this.mecanico = mecanico;
    }

    public long getKmVeiculoFechamento() {
        return kmVeiculoFechamento;
    }

    public void setKmVeiculoFechamento(long kmVeiculoFechamento) {
        this.kmVeiculoFechamento = kmVeiculoFechamento;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Tempo getTempoRestante() {
        return tempoRestante;
    }

    public void setTempoRestante(Tempo tempoRestante) {
        this.tempoRestante = tempoRestante;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Date getDataApontamento() {
        return dataApontamento;
    }

    public void setDataApontamento(Date dataApontamento) {
        this.dataApontamento = dataApontamento;
    }

    public Tempo getTempoLimiteResolucao() {
        return tempoLimiteResolucao;
    }

    public void setTempoLimiteResolucao(Tempo tempoLimiteResolucao) {
        this.tempoLimiteResolucao = tempoLimiteResolucao;
    }

    public Date getDataHoraConserto() {
        return dataHoraConserto;
    }

    public void setDataHoraConserto(Date dataHoraConserto) {
        this.dataHoraConserto = dataHoraConserto;
    }

    public Tempo getTempoRealizacaoConserto() {
        return tempoRealizacaoConserto;
    }

    public void setTempoRealizacaoConserto(Tempo tempoRealizacaoConserto) {
        this.tempoRealizacaoConserto = tempoRealizacaoConserto;
    }

    @Override
    public String toString() {
        return "ItemOrdemServico{" +
                "codOs=" + codOs +
                ", placa='" + placa + '\'' +
                ", pergunta=" + pergunta +
                ", mecanico=" + mecanico +
                ", dataApontamento=" + dataApontamento +
                ", dataHoraConserto=" + dataHoraConserto +
                ", tempoRealizacaoConsertoInMillis=" + tempoRealizacaoConserto +
                ", kmVeiculoFechamento=" + kmVeiculoFechamento +
                ", status=" + status +
                ", tempoLimiteResolucao=" + tempoLimiteResolucao +
                ", tempoRestante=" + tempoRestante +
                ", feedbackResolucao='" + feedbackResolucao + '\'' +
                ", qtdApontamentos=" + qtdApontamentos +
                '}';
    }
}
