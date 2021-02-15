package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
@Getter
public class DadosGeraisFiltro {
       @NotNull
       Long codUnidade;

       @NotNull
       LocalDate dataInicial;

       @NotNull
       LocalDate dataFinal;

       int limit;
       int offset;
}
