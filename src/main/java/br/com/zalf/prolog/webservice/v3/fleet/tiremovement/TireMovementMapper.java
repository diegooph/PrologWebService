package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Component
public final class TireMovementMapper {
    @NotNull
    public List<TireMovimentProcessDto> toDto(@NotNull final List<TireMovementProcessEntity> tireMovementProcess) {
        return tireMovementProcess.stream()
                .map(this::createTireMovementProcessDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private TireMovimentProcessDto createTireMovementProcessDto(
            @NotNull final TireMovementProcessEntity movementProcessEntity) {
        final Optional<VehicleMovement> vehicleMovement = movementProcessEntity.getVehicleMovement();
        return new TireMovimentProcessDto(movementProcessEntity.getId(),
                                          movementProcessEntity.getBranchId(),
                                          movementProcessEntity.getMovementProcessAt(),
                                          movementProcessEntity.getMovementProcessAtWithTimeZone(),
                                          movementProcessEntity.getMovementProcessBy().getId(),
                                          movementProcessEntity.getMovementProcessBy().getCpfFormatado(),
                                          movementProcessEntity.getMovementProcessBy().getName(),
                                          vehicleMovement.map(VehicleMovement::getVehicleId).orElse(null),
                                          vehicleMovement.map(VehicleMovement::getVehiclePlate).orElse(null),
                                          vehicleMovement.map(VehicleMovement::getFleetId).orElse(null),
                                          vehicleMovement.map(VehicleMovement::getVehicleKm).orElse(null),
                                          vehicleMovement.map(VehicleMovement::getVehicleLayoutId).orElse(null),
                                          movementProcessEntity.getNotes(),
                                          movementProcessEntity.getTireMovementEntities().stream()
                                                  .map(this::createTireMovementDto)
                                                  .collect(Collectors.toList()));
    }

    @NotNull
    private TireMovementDto createTireMovementDto(@NotNull final TireMovementEntity tireMovementEntity) {
        final TireMovementSourceEntity sourceMovement = tireMovementEntity.getTireMovementSourceEntity();
        final TireMovementDestinationEntity destinationMovement = tireMovementEntity.getTireMovementDestinationEntity();
        final TireEntity tireMoved = tireMovementEntity.getTireEntity();
        final RetreaderEntity destinationRetreader = destinationMovement.getRetreaderEntity();
        final Optional<Set<TireServiceEntity>> tireServiceEntities =
                Optional.ofNullable(tireMovementEntity.getTireServiceEntities());
        return new TireMovementDto(tireMovementEntity.getId(),
                                   tireMoved.getId(),
                                   tireMoved.getClientNumber(),
                                   tireMoved.getTireSizeEntity().getId(),
                                   tireMovementEntity.getTireLifeCycle(),
                                   tireMovementEntity.getInternalGroove(),
                                   tireMovementEntity.getMiddleInternalGroove(),
                                   tireMovementEntity.getMiddleExternalGroove(),
                                   tireMovementEntity.getExternalGroove(),
                                   tireMovementEntity.getCurrentPressure(),
                                   sourceMovement.getMovementSourceType().asString(),
                                   sourceMovement.getTirePosition(),
                                   destinationMovement.getMovementDestinationType().asString(),
                                   destinationMovement.getTirePosition(),
                                   tireMovementEntity.getNotes(),
                                   destinationMovement.getScrapReasonId(),
                                   destinationMovement.getUrlScrapImage1(),
                                   destinationMovement.getUrlScrapImage2(),
                                   destinationMovement.getUrlScrapImage3(),
                                   destinationRetreader != null ? destinationRetreader.getCompanyId() : null,
                                   destinationRetreader != null ? destinationRetreader.getName() : null,
                                   destinationMovement.getAdditionalInformation(),
                                   tireServiceEntities.map(this::createTireServicesDto).orElse(null));
    }

    @Nullable
    private List<TireServiceDto> createTireServicesDto(@NotNull final Set<TireServiceEntity> tireServiceEntities) {
        return tireServiceEntities.size() == 0
                ? null
                : tireServiceEntities.stream().map(this::createTireServiceDto).collect(Collectors.toList());
    }

    @NotNull
    private TireServiceDto createTireServiceDto(@NotNull final TireServiceEntity tireServiceEntity) {
        return new TireServiceDto(tireServiceEntity.getId(),
                                  tireServiceEntity.getTireServiceTypeEntity().getName(),
                                  tireServiceEntity.getTireServiceTypeEntity().isIncreaseLifeCycle(),
                                  tireServiceEntity.getServiceCost(),
                                  tireServiceEntity.getTireLifeCycle());
    }
}
