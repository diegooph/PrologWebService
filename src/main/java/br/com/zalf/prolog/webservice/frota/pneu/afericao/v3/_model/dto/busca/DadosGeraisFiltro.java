package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

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

       int limit;
       int offset;
}
