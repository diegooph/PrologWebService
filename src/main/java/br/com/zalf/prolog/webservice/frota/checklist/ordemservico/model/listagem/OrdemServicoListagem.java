package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class OrdemServicoListagem {
    private Long codOrdemServico;
    private Long codUnidadeOrdemServico;
    private String placaVeiculo;
    private LocalDateTime dataHoraAbertura;
    private int qtdItensPendentes;
    private int qtdItensResolvidos;

    @NotNull
    @Exclude
    private final String tipo;

    public OrdemServicoListagem(@NotNull final String tipo) {
        this.tipo = tipo;
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

    public int getQtdItensPendentes() {
        return qtdItensPendentes;
    }

    public void setQtdItensPendentes(final int qtdItensPendentes) {
        this.qtdItensPendentes = qtdItensPendentes;
    }

    public int getQtdItensResolvidos() {
        return qtdItensResolvidos;
    }

    public void setQtdItensResolvidos(final int qtdItensResolvidos) {
        this.qtdItensResolvidos = qtdItensResolvidos;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<OrdemServicoListagem> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(OrdemServicoListagem.class, "tipo")
                .registerSubtype(OrdemServicoAbertaListagem.class, OrdemServicoAbertaListagem.TIPO_SERIALIZACAO)
                .registerSubtype(OrdemServicoFechadaListagem.class, OrdemServicoFechadaListagem.TIPO_SERIALIZACAO);
    }
}