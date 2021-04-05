package br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.busca;

import lombok.Getter;
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
@Getter
public class DadosGeraisFiltro {
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

       private DadosGeraisFiltro(@NotNull final List<Long> codUnidades,
                                 @NotNull final LocalDate dataInicial,
                                 @NotNull final LocalDate dataFinal,
                                 final int limit,
                                 final int offset) {
              this.codUnidades = codUnidades;
              validateDates(dataInicial, dataFinal);
              this.dataInicial = dataInicial;
              this.dataFinal = dataFinal;
              this.limit = limit;
              this.offset = offset;
       }

       private void validateDates(final LocalDate dataInicial, final LocalDate dataFinal) {
              if (dataInicial.isAfter(dataFinal) || dataFinal.isBefore(dataInicial)) {
                     throw new IllegalArgumentException("range de datas inválido!");
              }
              if (dataInicial.isEqual(dataFinal)) {
                     throw new IllegalArgumentException("dataInicial e dataFinal são iguais!");
              }
       }
}
