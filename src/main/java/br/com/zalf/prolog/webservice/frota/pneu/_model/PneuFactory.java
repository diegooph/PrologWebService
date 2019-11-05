package br.com.zalf.prolog.webservice.frota.pneu._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 30/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PneuFactory {
    @NotNull
    Pneu createNew();
}