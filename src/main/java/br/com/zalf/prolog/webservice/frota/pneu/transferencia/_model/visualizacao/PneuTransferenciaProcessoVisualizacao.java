package br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao;

import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
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
public final class PneuTransferenciaProcessoVisualizacao {
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
     * Lista de pneus que foram transferidos. Objeto contendo as informação dos pneus que foram transferidos.
     */
    @NotNull
    private final List<PneuTransferenciaInformacoes> pneusTransferidos;
    /**
     * Observação inserida pelo colaborador no momento da transferência. O colaborador pode não ter informado nenhum
     * texto, neste caso esse atributo estará vazio.
     */
    @Nullable
    private final String observacao;
    /**
     * Data e hora que a transferência foi realizada pelo colaborador.
     */
    @NotNull
    private final LocalDateTime dataHoraTransferencia;
    /**
     * Objeto que contém informações de qual placa o pneu estava aplicado no momento da transferência. Será
     * <code>null</code> se o pneu não estava aplicado.
     */
    @Nullable
    private final LinkTransferenciaVeiculo linkTransferenciaVeiculo;

    public PneuTransferenciaProcessoVisualizacao(@NotNull final Long codTransferenciaProcesso,
                                                 @NotNull final String nomeColaboradorRealizacaoTransferencia,
                                                 @NotNull final String nomeRegionalOrigem,
                                                 @NotNull final String nomeUnidadeOrigem,
                                                 @NotNull final String nomeRegionalDestino,
                                                 @NotNull final String nomeUnidadeDestino,
                                                 @NotNull final List<PneuTransferenciaInformacoes> pneusTransferidos,
                                                 @Nullable final String observacao,
                                                 @NotNull final LocalDateTime dataHoraTransferencia,
                                                 @Nullable final LinkTransferenciaVeiculo linkTransferenciaVeiculo) {
        this.codTransferenciaProcesso = codTransferenciaProcesso;
        this.nomeColaboradorRealizacaoTransferencia = nomeColaboradorRealizacaoTransferencia;
        this.nomeRegionalOrigem = nomeRegionalOrigem;
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
        this.nomeRegionalDestino = nomeRegionalDestino;
        this.nomeUnidadeDestino = nomeUnidadeDestino;
        this.pneusTransferidos = pneusTransferidos;
        this.observacao = observacao;
        this.dataHoraTransferencia = dataHoraTransferencia;
        this.linkTransferenciaVeiculo = linkTransferenciaVeiculo;
    }

    @NotNull
    public static PneuTransferenciaProcessoVisualizacao createDummy() {
        final List<PneuTransferenciaInformacoes> pneusTransferidos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Sulcos sulcos = new Sulcos();
            sulcos.setInterno(i + 13.5);
            sulcos.setCentralInterno(i + 13.4);
            sulcos.setCentralExterno(i + 13.3);
            sulcos.setExterno(i + 12.9);
            pneusTransferidos.add(new PneuTransferenciaInformacoes(
                    10L,
                    String.valueOf(i),
                    sulcos,
                    i + 100.5,
                    i));
        }
        return new PneuTransferenciaProcessoVisualizacao(
                101L,
                "John Doe",
                "Sudeste",
                "Zalf Sistemas 1",
                "Sudeste",
                "Zalf Sistemas 2",
                pneusTransferidos,
                "Operação de verão",
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
    public List<PneuTransferenciaInformacoes> getPneusTransferidos() {
        return pneusTransferidos;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }

    @NotNull
    public LocalDateTime getDataHoraTransferencia() {
        return dataHoraTransferencia;
    }

    @Nullable
    public LinkTransferenciaVeiculo getLinkTransferenciaVeiculo() {
        return linkTransferenciaVeiculo;
    }
}