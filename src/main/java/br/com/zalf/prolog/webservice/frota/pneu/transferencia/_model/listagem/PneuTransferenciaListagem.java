package br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem;

import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.LinkTransferenciaVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaListagem {
    /**
     * Código do processo de transferência de Pneu.
     */
    @NotNull
    private final Long codTransferenciaProcesso;
    /**
     * Nome do colaborador que realizou a transferência dos pneus.
     */
    @NotNull
    private final String nomeColaboradorRealizacaoTransferencia;
    /**
     * Nome da Regional onde os pneus estavam alocados.
     */
    @NotNull
    private final String nomeRegionalOrigem;
    /**
     * Nome da Unidade onde os pneus estavam alocados.
     */
    @NotNull
    private final String nomeUnidadeOrigem;
    /**
     * Nome da Regional para onde os pneus foram transferidos.
     */
    @NotNull
    private final String nomeRegionalDestino;
    /**
     * Nome da Unidade para onde os pneus foram transferidos.
     */
    @NotNull
    private final String nomeUnidadeDestino;
    /**
     * Lista de pneus que foram transferidos. Mostra apenas o código do cliente.
     */
    @NotNull
    private final List<String> codPneusCliente;
    /**
     * Observação inserida pelo colaborador no momento da transferência. O colaborador pode não ter informado nenhum
     * texto, neste caso esse atributo estará vazio.
     */
    @Nullable
    private final String observacaoTransferenciaProcesso;
    /**
     * Data e hora que a transferência foi realizada pelo colaborador.
     */
    @NotNull
    private final LocalDateTime dataHoraTransferenciaProcesso;
    /**
     * Objeto que contém informações de qual placa o pneu estava aplicado no momento da transferência. Será
     * <code>null</code> se o pneu não estava aplicado.
     */
    @Nullable
    private final LinkTransferenciaVeiculo linkTransferenciaVeiculo;

    public PneuTransferenciaListagem(@NotNull final Long codTransferenciaProcesso,
                                     @NotNull final String nomeColaboradorRealizacaoTransferencia,
                                     @NotNull final String nomeRegionalOrigem,
                                     @NotNull final String nomeUnidadeOrigem,
                                     @NotNull final String nomeRegionalDestino,
                                     @NotNull final String nomeUnidadeDestino,
                                     @NotNull final List<String> codPneusCliente,
                                     @Nullable final String observacaoTransferenciaProcesso,
                                     @NotNull final LocalDateTime dataHoraTransferenciaProcesso,
                                     @Nullable final LinkTransferenciaVeiculo linkTransferenciaVeiculo) {
        this.codTransferenciaProcesso = codTransferenciaProcesso;
        this.dataHoraTransferenciaProcesso = dataHoraTransferenciaProcesso;
        this.codPneusCliente = codPneusCliente;
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
        this.nomeUnidadeDestino = nomeUnidadeDestino;
        this.nomeRegionalOrigem = nomeRegionalOrigem;
        this.nomeRegionalDestino = nomeRegionalDestino;
        this.nomeColaboradorRealizacaoTransferencia = nomeColaboradorRealizacaoTransferencia;
        this.observacaoTransferenciaProcesso = observacaoTransferenciaProcesso;
        this.linkTransferenciaVeiculo = linkTransferenciaVeiculo;
    }

    @NotNull
    public static PneuTransferenciaListagem createDummy() {
        final List<String> codPneusCliente = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codPneusCliente.add(String.valueOf(i));
        }
        return new PneuTransferenciaListagem(
                101L,
                "Zalf Sistemas",
                "Sul",
                "Floripa",
                "Sul",
                "Sapucaia",
                codPneusCliente,
                "Operação de Verão",
                LocalDateTime.now(),
                new LinkTransferenciaVeiculo(1010L, "PRO0001"));
    }

    @NotNull
    public Long getCodTransferenciaProcesso() {
        return codTransferenciaProcesso;
    }

    @NotNull
    public String getNomeColaboradorRealizacaoTransferencia() {
        return nomeColaboradorRealizacaoTransferencia;
    }

    @NotNull
    public String getNomeRegionalOrigem() {
        return nomeRegionalOrigem;
    }

    @NotNull
    public String getNomeUnidadeOrigem() {
        return nomeUnidadeOrigem;
    }

    @NotNull
    public String getNomeRegionalDestino() {
        return nomeRegionalDestino;
    }

    @NotNull
    public String getNomeUnidadeDestino() {
        return nomeUnidadeDestino;
    }

    @NotNull
    public List<String> getCodPneusCliente() {
        return codPneusCliente;
    }

    @Nullable
    public String getObservacaoTransferenciaProcesso() {
        return observacaoTransferenciaProcesso;
    }

    @NotNull
    public LocalDateTime getDataHoraTransferenciaProcesso() {
        return dataHoraTransferenciaProcesso;
    }

    @Nullable
    public LinkTransferenciaVeiculo getLinkTransferenciaVeiculo() {
        return linkTransferenciaVeiculo;
    }
}