package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class OrdemServicoVisualizacao {
    private Long codOrdemServico;
    private String placaVeiculo;
    private LocalDateTime dataHoraAbertura;
    private List<ItemOrdemServicoVisualizacao> itens;

    @Exclude
    @NotNull
    private final String tipo;

    public OrdemServicoVisualizacao(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<OrdemServicoVisualizacao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(OrdemServicoVisualizacao.class, "tipo")
                .registerSubtype(OrdemServicoAbertaVisualizacao.class, OrdemServicoAbertaVisualizacao.TIPO_SERIALIZACAO)
                .registerSubtype(OrdemServicoFechadaVisualizacao.class, OrdemServicoFechadaVisualizacao.TIPO_SERIALIZACAO);
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public LocalDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(final LocalDateTime dataHoraAbertura) {
        this.dataHoraAbertura = dataHoraAbertura;
    }

    public List<ItemOrdemServicoVisualizacao> getItens() {
        return itens;
    }

    public void setItens(final List<ItemOrdemServicoVisualizacao> itens) {
        this.itens = itens;
    }
}