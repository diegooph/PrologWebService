package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ItemResolvidoGlobus {
    private Long codUnidadeItemOs;
    private Long codOsGlobus;
    private Long codItemResolvidoGlobus;
    private String cpfColaboradoResolucao;
    private String placaVeiculoItemOs;
    private Long kmColetadoResolucao;
    private Long duracaoResolucaoItemOsMillis;
    private String feedbackResolucaoItemOs;
    private LocalDateTime dataHoraResolucaoItemOsUtc;
    private LocalDateTime dataHoraInicioResolucaoItemOsUtc;
    private LocalDateTime dataHoraFimResolucaoItemOsUtc;

    public ItemResolvidoGlobus() {
    }

    @NotNull
    public static ItemResolvidoGlobus getDummy() {
        final ItemResolvidoGlobus itemResolvido = new ItemResolvidoGlobus();
        itemResolvido.setCodUnidadeItemOs(5L);
        itemResolvido.setCodOsGlobus(1L);
        itemResolvido.setCodItemResolvidoGlobus(100L);
        itemResolvido.setCpfColaboradorResolucao("03383283194");
        itemResolvido.setPlacaVeiculoItemOs("PRO0001");
        itemResolvido.setKmColetadoResolucao(54939L);
        itemResolvido.setDuracaoResolucaoItemOsMillis(Duration.ofHours(2L).getSeconds());
        itemResolvido.setFeedbackResolucaoItemOs("Item consertado através da integração com o Globus");
        itemResolvido.setDataHoraResolucaoItemOsUtc(Now.localDateTimeUtc());
        itemResolvido.setDataHoraInicioResolucaoItemOsUtc(Now.localDateTimeUtc().minus(Duration.ofHours(1)));
        itemResolvido.setDataHoraFimResolucaoItemOsUtc(Now.localDateTimeUtc().minus(Duration.ofMinutes(15)));
        return itemResolvido;
    }

    public Long getCodUnidadeItemOs() {
        return codUnidadeItemOs;
    }

    public void setCodUnidadeItemOs(final Long codUnidadeItemOs) {
        this.codUnidadeItemOs = codUnidadeItemOs;
    }

    public Long getCodOsGlobus() {
        return codOsGlobus;
    }

    public void setCodOsGlobus(final Long codOsGlobus) {
        this.codOsGlobus = codOsGlobus;
    }

    public Long getCodItemResolvidoGlobus() {
        return codItemResolvidoGlobus;
    }

    public void setCodItemResolvidoGlobus(final Long codItemResolvidoGlobus) {
        this.codItemResolvidoGlobus = codItemResolvidoGlobus;
    }

    public String getCpfColaboradorResolucao() {
        return cpfColaboradoResolucao;
    }

    public void setCpfColaboradorResolucao(final String cpfColaboradorResolucao) {
        this.cpfColaboradoResolucao = cpfColaboradorResolucao;
    }

    public String getPlacaVeiculoItemOs() {
        return placaVeiculoItemOs;
    }

    public void setPlacaVeiculoItemOs(final String placaVeiculoItemOs) {
        this.placaVeiculoItemOs = placaVeiculoItemOs;
    }

    public Long getKmColetadoResolucao() {
        return kmColetadoResolucao;
    }

    public void setKmColetadoResolucao(final Long kmColetadoResolucao) {
        this.kmColetadoResolucao = kmColetadoResolucao;
    }

    public Long getDuracaoResolucaoItemOsMillis() {
        return duracaoResolucaoItemOsMillis;
    }

    public void setDuracaoResolucaoItemOsMillis(final Long duracaoResolucaoItemOsMillis) {
        this.duracaoResolucaoItemOsMillis = duracaoResolucaoItemOsMillis;
    }

    public String getFeedbackResolucaoItemOs() {
        return feedbackResolucaoItemOs;
    }

    public void setFeedbackResolucaoItemOs(final String feedbackResolucaoItemOs) {
        this.feedbackResolucaoItemOs = feedbackResolucaoItemOs;
    }

    public LocalDateTime getDataHoraResolucaoItemOsUtc() {
        return dataHoraResolucaoItemOsUtc;
    }

    public void setDataHoraResolucaoItemOsUtc(final LocalDateTime dataHoraResolucaoItemOsUtc) {
        this.dataHoraResolucaoItemOsUtc = dataHoraResolucaoItemOsUtc;
    }

    public LocalDateTime getDataHoraInicioResolucaoItemOsUtc() {
        return dataHoraInicioResolucaoItemOsUtc;
    }

    public void setDataHoraInicioResolucaoItemOsUtc(final LocalDateTime dataHoraInicioResolucaoItemOsUtc) {
        this.dataHoraInicioResolucaoItemOsUtc = dataHoraInicioResolucaoItemOsUtc;
    }

    public LocalDateTime getDataHoraFimResolucaoItemOsUtc() {
        return dataHoraFimResolucaoItemOsUtc;
    }

    public void setDataHoraFimResolucaoItemOsUtc(final LocalDateTime dataHoraFimResolucaoItemOsUtc) {
        this.dataHoraFimResolucaoItemOsUtc = dataHoraFimResolucaoItemOsUtc;
    }
}
