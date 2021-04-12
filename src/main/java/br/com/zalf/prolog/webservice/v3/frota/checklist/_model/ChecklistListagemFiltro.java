package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-04-09
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Value(staticConstructor = "of")
public class ChecklistListagemFiltro {
    @NotNull
    List<Long> codUnidades;
    @Nullable
    Long codColaborador;
    @Nullable
    Long codTipoVeiculo;
    @Nullable
    Long codVeiculo;
    boolean incluirRespostas;
    @NotNull
    LocalDate dataInicial;
    @NotNull
    LocalDate dataFinal;
    @Max(value = 1000, message = "valor de pesquisa não pode ser maior que 1000 linhas.")
    @Min(value = 0, message = "não pode ser menor que zero.")
    int limit;
    @Min(value = 0, message = "não pode ser menor que zero.")
    long offset;
}
