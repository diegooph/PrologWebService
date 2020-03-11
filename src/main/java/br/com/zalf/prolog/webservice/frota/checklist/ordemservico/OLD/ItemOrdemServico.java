package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Itens que compõem uma Ordem de Serviço.
 *
 * Created by jean on 25/07/16.
 */
@Deprecated
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
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
        }
    }

    /**
     * Código da OS ao qual esse item pertence
     */
    private Long codOs;

    /**
     * Código da unidade da qual este item pertence.
     */
    private Long codUnidadeItemOs;

    /**
     * Placa ao qual o item pertence
     */
    private String placa;

    /**
     * Pergunta e alternativa = item
     */
    private PerguntaRespostaChecklist pergunta;

    /**
     * Usuário responsável pelo conserto do item
     */
    private Colaborador mecanico;

    /**
     * Data em que o item foi apontado pela primeira vez
     */
    private LocalDateTime dataApontamento;

    /**
     * data e hora em que o item foi marcado como consertado
     */
    private LocalDateTime dataHoraConserto;

    /**
     * Km do veículo no momento em que o item foi fechado
     */
    private long kmVeiculoFechamento;

    /**
     * Status em que o item se encontra, podendo ser {@link Status}#RESOLVIDO
     * ou {@link Status}#PENDENTE
     */
    private Status status;

    /**
     * Tempo que o item levou para ser consertado
     */
    @SerializedName("tempoRealizacaoConsertoEmSegundos")
    private Duration tempoRealizacaoConserto;

    /**
     * Prazo em horas para conserto do item
     */
    @SerializedName("tempoLimiteResolucaoEmSegundos")
    private Duration tempoLimiteResolucao;

    /**
     * Tempo restante para consertar o item, baseado na sua prioridade
     */
    @SerializedName("tempoRestanteEmSegundos")
    private Duration tempoRestante;

    /**
     * Observação do serviço realizado
     */
    private String feedbackResolucao;

    /**
     * Quantidade de apontamentos que um item tem
     */
    private int qtdApontamentos;

    /**
     * Código sequencial do item.
     */
    private Long codigo;

    public ItemOrdemServico() {

    }

    public Long getCodUnidadeItemOs() {
        return codUnidadeItemOs;
    }

    public void setCodUnidadeItemOs(final Long codUnidadeItemOs) {
        this.codUnidadeItemOs = codUnidadeItemOs;
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

    public Duration getTempoRestante() {
        return tempoRestante;
    }

    public void setTempoRestante(Duration tempoRestante) {
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

    public LocalDateTime getDataApontamento() {
        return dataApontamento;
    }

    public void setDataApontamento(LocalDateTime dataApontamento) {
        this.dataApontamento = dataApontamento;
    }

    public Duration getTempoLimiteResolucao() {
        return tempoLimiteResolucao;
    }

    public void setTempoLimiteResolucao(Duration tempoLimiteResolucao) {
        this.tempoLimiteResolucao = tempoLimiteResolucao;
    }

    public LocalDateTime getDataHoraConserto() {
        return dataHoraConserto;
    }

    public void setDataHoraConserto(LocalDateTime dataHoraConserto) {
        this.dataHoraConserto = dataHoraConserto;
    }

    public Duration getTempoRealizacaoConserto() {
        return tempoRealizacaoConserto;
    }

    public void setTempoRealizacaoConserto(Duration tempoRealizacaoConserto) {
        this.tempoRealizacaoConserto = tempoRealizacaoConserto;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
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
                ", kmVeiculoFechamento=" + kmVeiculoFechamento +
                ", status=" + status +
                ", tempoRealizacaoConserto=" + tempoRealizacaoConserto +
                ", tempoLimiteResolucao=" + tempoLimiteResolucao +
                ", tempoRestante=" + tempoRestante +
                ", feedbackResolucao='" + feedbackResolucao + '\'' +
                ", qtdApontamentos=" + qtdApontamentos +
                ", codigo=" + codigo +
                '}';
    }
}
