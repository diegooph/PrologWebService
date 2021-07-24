package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value(staticConstructor = "of")
public class KmCollectedVehicle {
    @NotNull
    Long vehicleId;
    long kmCollected;
}
