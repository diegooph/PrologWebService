package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ConsertoMultiplosItensOs {

    /**
     * Quem está realizando o conserto.
     */
    private Long cpfColaboradorConserto;

    /**
     * A placa do veículo da qual os itens fechados pertencem.
     */
    private String placaVeiculo;

    /**
     * Duração do consertado.
     */
    @SerializedName("duracaoRealizacaoConsertoEmSegundos")
    private Duration duracaoRealizacaoConserto;

    /**
     * Km do veículo no momento em que o item foi consertado.
     */
    private long kmVeiculoConserto;

    /**
     * Observação sobre o conserto.
     */
    private String feedbackResolucao;

    /**
     * O código da unidade da qual os itens de O.S. pertencem.
     */
    private Long codUnidadeItensOs;

    /**
     * Códigos dos {@link ItemOrdemServico itens} que serão fechados.
     */
    private List<Long> codigosItens;

    public ConsertoMultiplosItensOs() {

    }

    @NotNull
    public static ConsertoMultiplosItensOs createDummy() {
        final ConsertoMultiplosItensOs conserto = new ConsertoMultiplosItensOs();
        conserto.setCpfColaboradorConserto(12345678987L);
        conserto.setPlacaVeiculo("AAA1234");
        conserto.setDuracaoRealizacaoConserto(Duration.ofMinutes(10));
        conserto.setKmVeiculoConserto(234000);
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

    public Long getCpfColaboradorConserto() {
        return cpfColaboradorConserto;
    }

    public void setCpfColaboradorConserto(final Long cpfColaboradorConserto) {
        this.cpfColaboradorConserto = cpfColaboradorConserto;
    }

    public Duration getDuracaoRealizacaoConserto() {
        return duracaoRealizacaoConserto;
    }

    public void setDuracaoRealizacaoConserto(final Duration duracaoRealizacaoConserto) {
        this.duracaoRealizacaoConserto = duracaoRealizacaoConserto;
    }

    public long getKmVeiculoConserto() {
        return kmVeiculoConserto;
    }

    public void setKmVeiculoConserto(final long kmVeiculoConserto) {
        this.kmVeiculoConserto = kmVeiculoConserto;
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