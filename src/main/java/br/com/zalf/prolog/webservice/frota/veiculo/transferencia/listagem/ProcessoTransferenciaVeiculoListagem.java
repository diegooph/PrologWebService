package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.listagem;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProcessoTransferenciaVeiculoListagem {
    /**
     * Código do processo de movimentação que foi realizado.
     */
    @NotNull
    private final Long codProcessoTransferencia;
    /**
     * Nome do colaborador que foi responsável pelo processo de transferência.
     */
    @NotNull
    private final String nomeColaboradorRealizacao;
    /**
     * Data e Hora que o processo de transferência foi executada.
     */
    @NotNull
    private final LocalDateTime dataHoraRealizacao;
    /**
     * Nome da Unidade à qual a(s) placa(s) pertenciam antes da transferência.
     */
    @NotNull
    private final String nomeUnidadeOrigem;
    /**
     * Nome da Unidade à qual a(s) placa(s) foram transferidas.
     */
    @NotNull
    private final String nomeUnidadeDestino;
    /**
     * Nome da Regional à qual a(s) placa(s) pertenciam antes da transferência.
     */
    @NotNull
    private final String nomeRegionalOrigem;
    /**
     * Nome da Regional à qual a(s) placa(s) foram transferidas.
     */
    @NotNull
    private final String nomeRegionalDestino;
    /**
     * Observações inseridas pelo colaborador sobre o processo de transferência.
     */
    @NotNull
    private final String observacaoRealizacao;
    /**
     * Lista de placas que foram transferidas nesse processo.
     */
    @NotNull
    private final List<String> placasTransferidas;
    /**
     * Atributo numérico que representa a quantidade de plaas transferidas neste processo.
     */
    private final int qtdPlacasTransferidos;

    public ProcessoTransferenciaVeiculoListagem(@NotNull final Long codProcessoTransferencia,
                                                @NotNull final String nomeColaboradorRealizacao,
                                                @NotNull final LocalDateTime dataHoraRealizacao,
                                                @NotNull final String nomeUnidadeOrigem,
                                                @NotNull final String nomeUnidadeDestino,
                                                @NotNull final String nomeRegionalOrigem,
                                                @NotNull final String nomeRegionalDestino,
                                                @NotNull final String observacaoRealizacao,
                                                @NotNull final List<String> placasTransferidas,
                                                final int qtdPlacasTransferidos) {
        this.codProcessoTransferencia = codProcessoTransferencia;
        this.nomeColaboradorRealizacao = nomeColaboradorRealizacao;
        this.dataHoraRealizacao = dataHoraRealizacao;
        this.nomeUnidadeOrigem = nomeUnidadeOrigem;
        this.nomeUnidadeDestino = nomeUnidadeDestino;
        this.nomeRegionalOrigem = nomeRegionalOrigem;
        this.nomeRegionalDestino = nomeRegionalDestino;
        this.observacaoRealizacao = observacaoRealizacao;
        this.placasTransferidas = placasTransferidas;
        this.qtdPlacasTransferidos = qtdPlacasTransferidos;
    }

    @NotNull
    public Long getCodProcessoTransferencia() {
        return codProcessoTransferencia;
    }

    @NotNull
    public String getNomeColaboradorRealizacao() {
        return nomeColaboradorRealizacao;
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    @NotNull
    public String getNomeUnidadeOrigem() {
        return nomeUnidadeOrigem;
    }

    @NotNull
    public String getNomeUnidadeDestino() {
        return nomeUnidadeDestino;
    }

    @NotNull
    public String getNomeRegionalOrigem() {
        return nomeRegionalOrigem;
    }

    @NotNull
    public String getNomeRegionalDestino() {
        return nomeRegionalDestino;
    }

    @NotNull
    public String getObservacaoRealizacao() {
        return observacaoRealizacao;
    }

    @NotNull
    public List<String> getPlacasTransferidas() {
        return placasTransferidas;
    }

    public int getQtdPlacasTransferidos() {
        return qtdPlacasTransferidos;
    }
}