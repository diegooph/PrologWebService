package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class VehicleMovement {
    @NotNull
    Long vehicleId;
    @NotNull
    String vehiclePlate;
    @Nullable
    String fleetId;
    @NotNull
    Long vehicleLayoutId;
    long vehicleKm;

    @NotNull
    public KmCollectedVehicle toKmCollectedVehicle() {
        return KmCollectedVehicle.of(getVehicleId(), getVehicleKm());
    }
}
