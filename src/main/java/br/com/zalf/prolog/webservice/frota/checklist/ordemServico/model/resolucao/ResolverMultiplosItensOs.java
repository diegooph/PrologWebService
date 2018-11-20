package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.resolucao;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResolverMultiplosItensOs {

    /**
     * Quem está resolvendo o item.
     */
    private Long cpfColaboradorResolucao;

    /**
     * A placa do veículo da qual os itens resolvidos pertencem.
     */
    private String placaVeiculo;

    /**
     * Duração da resolução dos itens.
     */
    @SerializedName("duracaoResolucaoItensEmSegundos")
    private Duration duracaoResolucaoItens;

    /**
     * Km do veículo no momento em que os itens foram resolvidos.
     */
    private long kmVeiculoResolucao;

    /**
     * Observação sobre a resolução.
     */
    private String feedbackResolucao;

    /**
     * O código da unidade da qual os itens de O.S. pertencem.
     */
    private Long codUnidadeItensOs;

    /**
     * Códigos itens que serão resolvidos.
     */
    private List<Long> codigosItens;

    public ResolverMultiplosItensOs() {

    }

    @NotNull
    public static ResolverMultiplosItensOs createDummy() {
        final ResolverMultiplosItensOs conserto = new ResolverMultiplosItensOs();
        conserto.setCpfColaboradorResolucao(12345678987L);
        conserto.setPlacaVeiculo("AAA1234");
        conserto.setDuracaoResolucaoItens(Duration.ofMinutes(10));
        conserto.setKmVeiculoResolucao(234000);
        conserto.setFeedbackResolucao("Tudo resolvido!");
        conserto.setCodUnidadeItensOs(5L);
        final List<Long> codItens = new ArrayList<>();
        codItens.add(1L);
        codItens.add(2L);
        codItens.add(3L);
        conserto.setCodigosItens(codItens);
        return conserto;
    }

    public Long getCodUnidadeItensOs() {
        return codUnidadeItensOs;
    }

    public void setCodUnidadeItensOs(final Long codUnidadeItensOs) {
        this.codUnidadeItensOs = codUnidadeItensOs;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public Long getCpfColaboradorResolucao() {
        return cpfColaboradorResolucao;
    }

    public void setCpfColaboradorResolucao(final Long cpfColaboradorResolucao) {
        this.cpfColaboradorResolucao = cpfColaboradorResolucao;
    }

    public Duration getDuracaoResolucaoItens() {
        return duracaoResolucaoItens;
    }

    public void setDuracaoResolucaoItens(final Duration duracaoResolucaoItens) {
        this.duracaoResolucaoItens = duracaoResolucaoItens;
    }

    public long getKmVeiculoResolucao() {
        return kmVeiculoResolucao;
    }

    public void setKmVeiculoResolucao(final long kmVeiculoResolucao) {
        this.kmVeiculoResolucao = kmVeiculoResolucao;
    }

    public String getFeedbackResolucao() {
        return feedbackResolucao;
    }

    public void setFeedbackResolucao(final String feedbackResolucao) {
        this.feedbackResolucao = feedbackResolucao;
    }

    public List<Long> getCodigosItens() {
        return codigosItens;
    }

    public void setCodigosItens(final List<Long> codigosItens) {
        this.codigosItens = codigosItens;
    }
}