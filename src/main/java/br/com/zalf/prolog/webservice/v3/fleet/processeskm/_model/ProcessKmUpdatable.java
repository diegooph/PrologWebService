package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ProcessKmUpdatable {
    @NotNull
    KmCollectedEntity getEntityKmCollected(@NotNull final Long entityId,
                                           @NotNull final Long vehicleId);

    void updateProcessKmCollected(@NotNull final Long processId,
                                  @NotNull final Long vehicleId,
                                  final long newKm);
}
