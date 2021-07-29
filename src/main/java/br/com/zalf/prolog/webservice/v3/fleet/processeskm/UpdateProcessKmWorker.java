package br.com.zalf.prolog.webservice.v3.fleet.processeskm;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.VehicleService;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class UpdateProcessKmWorker {
    @NotNull
    private final VehicleService vehicleService;

    @NotNull
    public UpdateKmResponse updateProcessKm(@NotNull final ProcessKmUpdatable processKmUpdatable,
                                            @NotNull final UpdateProcessKm process) {
        final KmCollectedEntity entity = processKmUpdatable.getEntityKmCollected(process.getProcessId(),
                                                                                 process.getVehicleId());
        final KmCollectedVehicle kmCollectedVehicle = entity.getKmCollectedVehicle();
        applyValidations(process.getCompanyId(),
                         process.getVehicleId(),
                         kmCollectedVehicle.getVehicleId());
        if (kmCollectedVehicle.getKmCollected() != process.getNewKm()) {
            processKmUpdatable.updateProcessKmCollected(process.getProcessId(),
                                                        process.getVehicleId(),
                                                        process.getNewKm());
            return UpdateKmResponse.of(kmCollectedVehicle.getKmCollected(), true);
        }

        return UpdateKmResponse.of(kmCollectedVehicle.getKmCollected(), false);
    }

    private void applyValidations(@NotNull final Long receivedCompanyId,
                                  @NotNull final Long receivedVehicleId,
                                  @NotNull final Long databaseVehicleId) {
        if (!receivedVehicleId.equals(databaseVehicleId)) {
            fail();
        }
        final VehicleEntity vehicle = vehicleService.getById(databaseVehicleId);
        // Ensuring that the company of the vehicle is the same company received we also ensure that the updated process
        // belongs to the company in question.
        if (!receivedCompanyId.equals(vehicle.getCompanyId())) {
            fail();
        }
    }

    private void fail() {
        throw new GenericException(
                "It is only possible to update the collected km of processes that belongs to your company!");
    }
}
