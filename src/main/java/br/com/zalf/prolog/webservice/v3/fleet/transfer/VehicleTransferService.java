package br.com.zalf.prolog.webservice.v3.fleet.transfer;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.transfer._model.VehicleTransferInfosEntity;
import br.com.zalf.prolog.webservice.v3.fleet.transfer._model.VehicleTransferProcessEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleTransferService implements KmProcessoAtualizavel {
    @NotNull
    private final VehicleTransferDao vehicleTransferDao;
    @NotNull
    private final VehicleTransferInfosDao vehicleTransferInfosDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId, @NotNull final Long vehicleId) {
        final VehicleTransferProcessEntity processEntity = getById(entityId);
        final Optional<VehicleTransferInfosEntity> transferInfos = processEntity.getVehicleTransferInfos(vehicleId);
        return transferInfos.orElseThrow(() -> new IllegalStateException(
                String.format("O veículo %d não foi transferido no processo %d.", vehicleId, entityId)));
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateVehicleKmAtTransfer(processId, vehicleId, newKm);
    }

    @NotNull
    public VehicleTransferProcessEntity getById(@NotNull final Long transferProcessId) {
        return vehicleTransferDao.getOne(transferProcessId);
    }

    public void updateVehicleKmAtTransfer(@NotNull final Long transferProcessId,
                                          @NotNull final Long vehicleId,
                                          final long newKm) {
        final VehicleTransferProcessEntity processEntity = getById(transferProcessId);
        final Optional<VehicleTransferInfosEntity> transferInfos = processEntity.getVehicleTransferInfos(vehicleId);
        if (transferInfos.isPresent()) {
            final VehicleTransferInfosEntity infosEntity = transferInfos.get();
            final VehicleTransferInfosEntity updateEntity = infosEntity.toBuilder().withVehicleKm(newKm).build();
            vehicleTransferInfosDao.save(updateEntity);
        } else {
            throw new IllegalStateException(
                    String.format("O veículo %d não está presente no processo de transferência %d.",
                                  vehicleId,
                                  transferProcessId));
        }
    }
}
