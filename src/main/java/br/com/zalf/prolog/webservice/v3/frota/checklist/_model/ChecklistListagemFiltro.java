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
    @NotNull
    LocalDate dataInicial;
    @NotNull
    LocalDate dataFinal;
    @Nullable
    Long codColaborador;
    @Nullable
    Long codVeiculo;
    @Nullable
    Long codTipoVeiculo;
    boolean incluirRespostas;
    @Max(value = 100, message = "Valor de pesquisa não pode ser maior que 100 linhas.")
    @Min(value = 0, message = "Não pode ser menor que zero.")
    int limit;
    @Min(value = 0, message = "Não pode ser menor que zero.")
    int offset;
}
