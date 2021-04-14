package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class FiltroAfericaoPlaca {
    @NotNull
    private static final Long DEFAULT_VALUE_TIPO_VEICULO = -1L;
    @NotNull
    private static final String DEFAULT_VALUE_PLACA_VEICULO = "";

    @NotNull
    List<Long> codUnidades;
    @Nullable
    Long codVeiculo;
    @Nullable
    Long codTipoVeiculo;
    @NotNull
    LocalDate dataInicial;
    @NotNull
    LocalDate dataFinal;
    @Max(value = 1000, message = "valor de pesquisa não pode ser maior que 1000 linhas.")
    @Min(value = 0, message = "não pode ser menor que zero.")
    int limit;
    @Min(value = 0, message = "não pode ser menor que zero.")
    int offset;
    boolean incluirMedidas;

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo == null ? DEFAULT_VALUE_PLACA_VEICULO : placaVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo == null ? DEFAULT_VALUE_TIPO_VEICULO : codTipoVeiculo;
    }
}
