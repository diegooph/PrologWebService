package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
public class FiltroAfericaoPlaca extends FiltroAfericao {

    private static final Long DEFAULT_VALUE_TIPO_VEICULO = -1L;
    private static final String DEFAULT_VALUE_PLACA_VEICULO = "";

    @NotNull
    Long codTipoVeiculo;
    @NotNull
    String placaVeiculo;

    @Builder
    private FiltroAfericaoPlaca(@NotNull final Long codUnidade,
                               @Nullable final Long codTipoVeiculo,
                               @Nullable final String placaVeiculo,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal,
                               final int limit,
                               final int offset) {
        super(codUnidade, dataInicial, dataFinal, limit, offset);
        this.codTipoVeiculo = codTipoVeiculo != null ? codTipoVeiculo : DEFAULT_VALUE_TIPO_VEICULO;
        this.placaVeiculo = placaVeiculo != null ? placaVeiculo : DEFAULT_VALUE_PLACA_VEICULO;
    }
}
