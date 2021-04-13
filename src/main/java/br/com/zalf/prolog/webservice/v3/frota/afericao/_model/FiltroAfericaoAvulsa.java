package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class FiltroAfericaoAvulsa {
    @NotNull
    List<Long> codUnidades;
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
}