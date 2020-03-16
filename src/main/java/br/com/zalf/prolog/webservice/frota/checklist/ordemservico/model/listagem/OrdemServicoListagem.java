package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Esta é a superclasse utilizada para instanciar a Listagem de Ordens de Serviço Abertas ou a
 * Listagem de Ordens de Serviço Fechadas.
 * <p>
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class OrdemServicoListagem {

    /**
     * O status atual dessa O.S.
     */
    @NotNull
    private final StatusOrdemServico status;
    @NotNull
    @Exclude
    private final String tipo;
    /**
     * Código da Ordem de Serviço.
     */
    private Long codOrdemServico;
    /**
     * Código da {@link Unidade} a qual a Ordem de Serviço pertence.
     */
    private Long codUnidadeOrdemServico;
    /**
     * Placa do {@link Veiculo} a qual a Ordem de Serviço pertence.
     */
    private String placaVeiculo;
    /**
     * Data e Hora em que a Ordem de Serviço foi aberta.
     */
    private LocalDateTime dataHoraAbertura;
    /**
     * Quantidade de Itens para serem resolvidos.
     */
    private int qtdItensPendentes;
    /**
     * Quantidade de Itens já resolvidos.
     */
    private int qtdItensResolvidos;

    public OrdemServicoListagem(@NotNull final String tipo,
                                @NotNull final StatusOrdemServico status) {
        this.tipo = tipo;
        this.status = status;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<OrdemServicoListagem> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(OrdemServicoListagem.class, "tipo")
                .registerSubtype(OrdemServicoAbertaListagem.class, OrdemServicoAbertaListagem.TIPO_SERIALIZACAO)
                .registerSubtype(OrdemServicoFechadaListagem.class, OrdemServicoFechadaListagem.TIPO_SERIALIZACAO);
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

    public StatusOrdemServico getStatus() {
        return status;
    }
}