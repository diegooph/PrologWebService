package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuFotoEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface PneuFotoV3Service {

    @NotNull
    List<PneuFotoEntity> addPhotosToPneu(@NotNull final PneuEntity pneu, @NotNull final List<String> urls);
}
