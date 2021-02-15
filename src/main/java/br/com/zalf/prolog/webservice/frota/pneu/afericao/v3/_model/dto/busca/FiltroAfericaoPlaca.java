package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Size;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Getter
public class FiltroAfericaoPlaca {

    private static final Long DEFAULT_VALUE_TIPO_VEICULO = -1L;
    private static final String DEFAULT_VALUE_PLACA_VEICULO = "";

    @NotNull
    Long codTipoVeiculo;
    @NotNull
    @Size(max = 7)
    String placaVeiculo;

    @NotNull
    DadosGeraisFiltro dadosGerais;

    @Builder
    private FiltroAfericaoPlaca(
            @Nullable final Long codTipoVeiculo,
            @Nullable final String placaVeiculo,
            @NotNull final DadosGeraisFiltro dadosGerais) {
        this.codTipoVeiculo = codTipoVeiculo != null ? codTipoVeiculo : DEFAULT_VALUE_TIPO_VEICULO;
        this.placaVeiculo = placaVeiculo != null ? placaVeiculo : DEFAULT_VALUE_PLACA_VEICULO;
        this.dadosGerais = dadosGerais;
    }
}
