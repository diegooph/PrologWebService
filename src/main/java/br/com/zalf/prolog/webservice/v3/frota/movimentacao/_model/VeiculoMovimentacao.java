package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class VeiculoMovimentacao {
    @NotNull
    Long codVeiculo;
    long kmColetado;
}
