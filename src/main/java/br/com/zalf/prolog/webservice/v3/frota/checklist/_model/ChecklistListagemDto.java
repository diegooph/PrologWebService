package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import io.swagger.annotations.ApiModel;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@ApiModel(description = "Objeto com as informações de um checklist.")
@Value(staticConstructor = "of")
public class ChecklistListagemDto {

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
    int limit;
    long offset;
}
