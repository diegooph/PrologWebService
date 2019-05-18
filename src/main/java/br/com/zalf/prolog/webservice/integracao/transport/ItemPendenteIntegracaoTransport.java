package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemPendenteIntegracaoTransport {
    private String placaVeiculo;
    private Long kmAberturaServico;
    private Long codOrdemServico;
    private Long codUnidadeOrdemServico;
    private StatusOrdemServico statusOrdemServico;
    private LocalDateTime dataHoraAberturaServico;
    private Long codItemOrdemServico;
    private Long codUnidadeItemOrdemServico;
    private LocalDateTime dataHoraPrimeiroApontamento;
    private StatusItemOrdemServico statusItemOrdemServico;
    private Integer prazoResolucaoItemHoras;
    private Integer qtdApontamentos;
    private Long codChecklistPrimeiroApontamento;
    private Long codPergunta;
    private String descricaoPergunta;
    private Long codAlternativaPergunta;
    private String descricaoAlternativa;
    private Boolean isTipoOutros;
    private String descricaoTipoOutros;
    private PrioridadeAlternativa prioridadeAlternativa;

    ItemPendenteIntegracaoTransport() {
        statusOrdemServico = StatusOrdemServico.ABERTA;
        statusItemOrdemServico = StatusItemOrdemServico.PENDENTE;
    }

    @NotNull
    static ItemPendenteIntegracaoTransport getDummy() {
        final ItemPendenteIntegracaoTransport item = new ItemPendenteIntegracaoTransport();
        item.setPlacaVeiculo("PRO0001");
        item.setKmAberturaServico(90051L);
        item.setCodOrdemServico(94L);
        item.setCodUnidadeOrdemServico(5L);
        item.setDataHoraAberturaServico(LocalDateTime.now());
        item.setCodItemOrdemServico(106851L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now());
        item.setPrazoResolucaoItemHoras(1);
        item.setQtdApontamentos(1);
        item.setCodChecklistPrimeiroApontamento(80931L);
        item.setCodPergunta(1130L);
        item.setDescricaoPergunta("Cintos de segurança e sensor");
        item.setCodAlternativaPergunta(294L);
        item.setDescricaoAlternativa("Sensor com problema");
        item.setTipoOutros(false);
        item.setDescricaoTipoOutros(null);
        item.setPrioridadeAlternativa(PrioridadeAlternativa.CRITICA);
        return item;
    }

    @NotNull
    static ItemPendenteIntegracaoTransport getDummyTipoOutros() {
        final ItemPendenteIntegracaoTransport item = new ItemPendenteIntegracaoTransport();
        item.setPlacaVeiculo("PRO0001");
        item.setKmAberturaServico(854966L);
        item.setCodOrdemServico(65L);
        item.setCodUnidadeOrdemServico(5L);
        item.setDataHoraAberturaServico(LocalDateTime.now());
        item.setCodItemOrdemServico(26304L);
        item.setCodUnidadeItemOrdemServico(5L);
        item.setDataHoraPrimeiroApontamento(LocalDateTime.now());
        item.setPrazoResolucaoItemHoras(720);
        item.setQtdApontamentos(1);
        item.setCodChecklistPrimeiroApontamento(80931L);
        item.setCodPergunta(1163L);
        item.setDescricaoPergunta("Tampa da buzina, painel, porta luvas");
        item.setCodAlternativaPergunta(381L);
        item.setDescricaoAlternativa("Outros");
        item.setTipoOutros(true);
        item.setDescricaoTipoOutros("Luz do painel estragada");
        item.setPrioridadeAlternativa(PrioridadeAlternativa.BAIXA);
        return item;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Long getKmAberturaServico() {
        return kmAberturaServico;
    }

    public void setKmAberturaServico(final Long kmAberturaServico) {
        this.kmAberturaServico = kmAberturaServico;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    public void setCodUnidadeOrdemServico(final Long codUnidadeOrdemServico) {
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
    }

    public StatusOrdemServico getStatusOrdemServico() {
        return statusOrdemServico;
    }

    public LocalDateTime getDataHoraAberturaServico() {
        return dataHoraAberturaServico;
    }

    public void setDataHoraAberturaServico(final LocalDateTime dataHoraAberturaServico) {
        this.dataHoraAberturaServico = dataHoraAberturaServico;
    }

    public Long getCodItemOrdemServico() {
        return codItemOrdemServico;
    }

    public void setCodItemOrdemServico(final Long codItemOrdemServico) {
        this.codItemOrdemServico = codItemOrdemServico;
    }

    public Long getCodUnidadeItemOrdemServico() {
        return codUnidadeItemOrdemServico;
    }

    public void setCodUnidadeItemOrdemServico(final Long codUnidadeItemOrdemServico) {
        this.codUnidadeItemOrdemServico = codUnidadeItemOrdemServico;
    }

    public LocalDateTime getDataHoraPrimeiroApontamento() {
        return dataHoraPrimeiroApontamento;
    }

    public void setDataHoraPrimeiroApontamento(final LocalDateTime dataHoraPrimeiroApontamento) {
        this.dataHoraPrimeiroApontamento = dataHoraPrimeiroApontamento;
    }

    public StatusItemOrdemServico getStatusItemOrdemServico() {
        return statusItemOrdemServico;
    }

    public Integer getPrazoResolucaoItemHoras() {
        return prazoResolucaoItemHoras;
    }

    public void setPrazoResolucaoItemHoras(final Integer prazoResolucaoItemHoras) {
        this.prazoResolucaoItemHoras = prazoResolucaoItemHoras;
    }

    public Integer getQtdApontamentos() {
        return qtdApontamentos;
    }

    public void setQtdApontamentos(final Integer qtdApontamentos) {
        this.qtdApontamentos = qtdApontamentos;
    }

    public Long getCodChecklistPrimeiroApontamento() {
        return codChecklistPrimeiroApontamento;
    }

    public void setCodChecklistPrimeiroApontamento(final Long codChecklistPrimeiroApontamento) {
        this.codChecklistPrimeiroApontamento = codChecklistPrimeiroApontamento;
    }

    public Long getCodPergunta() {
        return codPergunta;
    }

    public void setCodPergunta(final Long codPergunta) {
        this.codPergunta = codPergunta;
    }

    public String getDescricaoPergunta() {
        return descricaoPergunta;
    }

    public void setDescricaoPergunta(final String descricaoPergunta) {
        this.descricaoPergunta = descricaoPergunta;
    }

    public Long getCodAlternativaPergunta() {
        return codAlternativaPergunta;
    }

    public void setCodAlternativaPergunta(final Long codAlternativaPergunta) {
        this.codAlternativaPergunta = codAlternativaPergunta;
    }

    public String getDescricaoAlternativa() {
        return descricaoAlternativa;
    }

    public void setDescricaoAlternativa(final String descricaoAlternativa) {
        this.descricaoAlternativa = descricaoAlternativa;
    }

    public Boolean getTipoOutros() {
        return isTipoOutros;
    }

    public void setTipoOutros(final Boolean tipoOutros) {
        isTipoOutros = tipoOutros;
    }

    public String getDescricaoTipoOutros() {
        return descricaoTipoOutros;
    }

    public void setDescricaoTipoOutros(final String descricaoTipoOutros) {
        this.descricaoTipoOutros = descricaoTipoOutros;
    }

    public PrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }

    public void setPrioridadeAlternativa(final PrioridadeAlternativa prioridadeAlternativa) {
        this.prioridadeAlternativa = prioridadeAlternativa;
    }
}
