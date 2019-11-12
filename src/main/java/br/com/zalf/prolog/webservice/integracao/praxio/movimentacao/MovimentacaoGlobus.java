package br.com.zalf.prolog.webservice.integracao.praxio.movimentacao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 11/7/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MovimentacaoGlobus {
    @NotNull
    public static final String PNEU_RETIRADO = "retirada";
    @NotNull
    public static final String PNEU_INSERIDO = "colocacao";
    @NotNull
    private Long sequencia;
    @NotNull
    private final String placa;
    @NotNull
    private final LocalDateTime dataHora;
    @NotNull
    private final String numeroFogoPneu;
    @NotNull
    private final String tipoOperacao;
    @Nullable
    private final String observacao;
    @Nullable
    private final Integer posicao;

    public MovimentacaoGlobus(@NotNull Long sequencia,
                              @NotNull final String placa,
                              @NotNull final LocalDateTime dataHora,
                              @NotNull final String numeroFogoPneu,
                              @NotNull final String tipoOperacao,
                              @Nullable final String observacao,
                              @Nullable final Integer posicao) {
        this.sequencia = sequencia;
        this.placa = placa;
        this.dataHora = dataHora;
        this.numeroFogoPneu = numeroFogoPneu;
        this.tipoOperacao = tipoOperacao;
        this.observacao = observacao;
        this.posicao = posicao;
    }

    @NotNull
    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(@NotNull final Long sequencia) {
        this.sequencia = sequencia;
    }

    @NotNull
    public String getPlaca() {
        return placa;
    }

    @NotNull
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @NotNull
    public String getNumeroFogoPneu() {
        return numeroFogoPneu;
    }

    @NotNull
    public String getTipoOperacao() {
        return tipoOperacao;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }

    @Nullable
    public Integer getPosicao() {
        return posicao;
    }
}
