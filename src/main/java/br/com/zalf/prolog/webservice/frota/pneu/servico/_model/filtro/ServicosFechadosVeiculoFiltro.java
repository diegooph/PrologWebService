package br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 2021-02-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class ServicosFechadosVeiculoFiltro {
    @NotNull
    Long codUnidade;
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @NotNull
    LocalDate dataInicial;
    @NotNull
    LocalDate dataFinal;
}
