package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ItemOrdemServicoResolvido extends ItemOrdemServicoVisualizacao {
    static final String TIPO_SERIALIZACAO = "ITEM_RESOLVIDO";

    private Long codColaboradorResolucao;
    private String nomeColaboradorResolucao;
    private LocalDateTime dataHoraResolucao;
    private String feedbackResolucao;
    @SerializedName("duracaoResolucaoEmSegundos")
    private Duration duracaoResolucao;

    public ItemOrdemServicoResolvido() {
        super(TIPO_SERIALIZACAO);
    }

    @NotNull
    public static ItemOrdemServicoResolvido createDummy() {
        final ItemOrdemServicoResolvido item = new ItemOrdemServicoResolvido();
        item.setCodigo(1L);
        item.setCodOrdemServico(2L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setPergunta(PerguntaItemOrdemServico.createDummy());
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
        item.setStatus(StatusItemOrdemServico.RESOLVIDO);
        item.setPrazoResolucaoItem(Duration.ofMinutes(42));
        item.setPrazoRestanteResolucaoItem(Duration.ofMinutes(20));
        item.setQtdApontamentos(10);

        // Resolvido.
        item.setCodColaboradorResolucao(10L);
        item.setNomeColaboradorResolucao("ProLoggerson");
        item.setDataHoraResolucao(LocalDateTime.now());
        item.setFeedbackResolucao("Feedback Fechamento");
        item.setDuracaoResolucao(Duration.ofMinutes(10));
        return item;
    }

    public Long getCodColaboradorResolucao() {
        return codColaboradorResolucao;
    }

    public void setCodColaboradorResolucao(final Long codColaboradorResolucao) {
        this.codColaboradorResolucao = codColaboradorResolucao;
    }

    public String getNomeColaboradorResolucao() {
        return nomeColaboradorResolucao;
    }

    public void setNomeColaboradorResolucao(final String nomeColaboradorResolucao) {
        this.nomeColaboradorResolucao = nomeColaboradorResolucao;
    }

    public LocalDateTime getDataHoraResolucao() {
        return dataHoraResolucao;
    }

    public void setDataHoraResolucao(final LocalDateTime dataHoraResolucao) {
        this.dataHoraResolucao = dataHoraResolucao;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public Duration getDuracaoResolucao() {
        return duracaoResolucao;
    }

    public void setDuracaoResolucao(final Duration tempoResolucao) {
        this.duracaoResolucao = tempoResolucao;
    }
}