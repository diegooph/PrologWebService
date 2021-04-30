package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MovimentacaoProcessoListagemDto {
    @NotNull
    private final Long codProcessoMovimentacao;
    @NotNull
    private final Long codUnidadeProcessoMovimentacao;
    @NotNull
    private final LocalDateTime dataHoraRealizacaoUtc;
    @NotNull
    private final LocalDateTime dataHoraRealizacaoTimeZoneAplicado;
    @NotNull
    private final Long codColaboradorResponsavel;
    @NotNull
    private final String cpfColaboradorResponsavel;
    @NotNull
    private final String nomeColaboradorResponsavel;
    @Nullable
    private final Long codVeiculo;
    @Nullable
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrotaVeiculo;
    @Nullable
    private final Long kmColetadoVeiculo;
    @Nullable
    private final Long codDiagramaVeiculo;
    @Nullable
    private final String observacaoProcessoMovimentacao;
    @NotNull
    private final List<MovimentacaoListagemDto> movimentacoesRealizadas;
}
