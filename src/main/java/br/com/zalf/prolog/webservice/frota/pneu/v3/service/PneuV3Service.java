package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuV3Service {

    @NotNull
    PneuEntity create(@NotNull final PneuEntity pneu);
}
