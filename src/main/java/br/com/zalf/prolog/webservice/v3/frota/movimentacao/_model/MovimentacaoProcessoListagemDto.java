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
    private final long codProcesso;
    private final long codUnidade;
    @NotNull
    private final LocalDateTime dataHoraRealizacaoUtc;
    @NotNull
    private final LocalDateTime dataHoraRealizacaoTimeZoneAplicado;
    private final long codColaboradorResponsavel;
    @NotNull
    private final String cpfColaboradorResponsavel;
    @NotNull
    private final String nomeColaboradorResponsavel;
    @Nullable
    private final String observacaoProcesso;
    @Nullable
    private final List<MovimentacaoListagemDto> movimentacoes;
}
