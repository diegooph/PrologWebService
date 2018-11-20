package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.StatusItemOrdemServico;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class ItemOrdemServicoVisualizacao {
    /**
     * Código sequencial do item.
     */
    private Long codigo;

    /**
     * Código da Ordem de Serviço da qual esse item pertence.
     */
    private Long codOrdemServico;

    /**
     * Código da unidade da qual este item pertence.
     */
    private Long codUnidadeItemOrdemServico;

    private PerguntaItemOrdemServico pergunta;

    private LocalDateTime dataHoraPrimeiroApontamento;

    /**
     * Status em que o item se encontra, podendo ser {@link StatusItemOrdemServico#RESOLVIDO}
     * ou {@link StatusItemOrdemServico#PENDENTE}
     */
    private StatusItemOrdemServico status;

    /**
     * Prazo para conserto do item.
     */
    @SerializedName("prazoConsertoItemEmSegundos")
    private Duration prazoConsertoItem;

    /**
     * Prazo restante para consertar o item, baseado na sua {@link PrioridadeAlternativa prioridade}.
     */
    @SerializedName("prazoRestanteConsertoItemEmSegundos")
    private Duration prazoRestanteConsertoItem;

    /**
     * Quantidade de apontamentos que um item tem.
     */
    private int qtdApontamentos;

    @Exclude
    @NotNull
    private final String tipo;

    public ItemOrdemServicoVisualizacao(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Long getCodUnidadeItemOrdemServico() {
        return codUnidadeItemOrdemServico;
    }

    public void setCodUnidadeItemOrdemServico(final Long codUnidadeItemOrdemServico) {
        this.codUnidadeItemOrdemServico = codUnidadeItemOrdemServico;
    }

    public PerguntaItemOrdemServico getPergunta() {
        return pergunta;
    }

    public void setPergunta(final PerguntaItemOrdemServico pergunta) {
        this.pergunta = pergunta;
    }

    public LocalDateTime getDataHoraPrimeiroApontamento() {
        return dataHoraPrimeiroApontamento;
    }

    public void setDataHoraPrimeiroApontamento(final LocalDateTime dataHoraPrimeiroApontamento) {
        this.dataHoraPrimeiroApontamento = dataHoraPrimeiroApontamento;
    }

    public StatusItemOrdemServico getStatus() {
        return status;
    }

    public void setStatus(final StatusItemOrdemServico status) {
        this.status = status;
    }

    public Duration getPrazoConsertoItem() {
        return prazoConsertoItem;
    }

    public void setPrazoConsertoItem(final Duration prazoConsertoItem) {
        this.prazoConsertoItem = prazoConsertoItem;
    }

    public Duration getPrazoRestanteConsertoItem() {
        return prazoRestanteConsertoItem;
    }

    public void setPrazoRestanteConsertoItem(final Duration prazoRestanteConsertoItem) {
        this.prazoRestanteConsertoItem = prazoRestanteConsertoItem;
    }

    public int getQtdApontamentos() {
        return qtdApontamentos;
    }

    public void setQtdApontamentos(final int qtdApontamentos) {
        this.qtdApontamentos = qtdApontamentos;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<ItemOrdemServicoVisualizacao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ItemOrdemServicoVisualizacao.class, "tipo")
                .registerSubtype(ItemOrdemServicoAberto.class, ItemOrdemServicoAberto.TIPO_SERIALIZACAO)
                .registerSubtype(ItemOrdemServicoFechado.class, ItemOrdemServicoFechado.TIPO_SERIALIZACAO);
    }
}