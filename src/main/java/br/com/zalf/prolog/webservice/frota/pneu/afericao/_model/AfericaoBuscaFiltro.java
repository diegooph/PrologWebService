package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-01-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoBuscaFiltro {
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @NotNull
    Long codUnidade;
    @NotNull
    TipoMedicaoColetadaAfericao tipoAfericao;
}
