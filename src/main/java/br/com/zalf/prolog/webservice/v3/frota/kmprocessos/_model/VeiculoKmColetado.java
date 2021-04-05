package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value(staticConstructor = "of")
public class VeiculoKmColetado {
    @NotNull
    Long codVeiculo;
    long kmColetado;
}
