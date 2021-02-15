package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@AllArgsConstructor
@Getter
public abstract class FiltroAfericao {
       @NotNull
       Long codUnidade;

       @NotNull
       LocalDate dataInicial;

       @NotNull
       LocalDate dataFinal;

       int limit;
       int offset;
}
