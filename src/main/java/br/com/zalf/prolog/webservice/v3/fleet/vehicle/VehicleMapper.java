package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.attach._model.AttachProcessEntity;
import br.com.zalf.prolog.webservice.v3.fleet.attach._model.CurrentAttachEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.AxleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleMapper {

    @NotNull
    public List<VehicleDto> toDto(@NotNull final List<VehicleEntity> veiculoEntities) {
        return veiculoEntities.stream()
                .map(this::createVehicleDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public VehicleEntity toEntity(@NotNull final VehicleCreateDto dto,
                                  @NotNull final BranchEntity branchEntity,
                                  @NotNull final VehicleLayoutEntity vehicleLayoutEntity,
                                  @NotNull final VehicleTypeEntity vehicleTypeEntity,
                                  @NotNull final VehicleModelEntity vehicleModelEntity,
                                  @NotNull final OrigemAcaoEnum registerOrigin) {
        return VehicleEntity.builder()
                .withCompanyId(dto.getCompanyId())
                .withBranchEntity(branchEntity)
                .withBranchRegisterEntity(branchEntity)
                .withVehicleLayoutEntity(vehicleLayoutEntity)
                .withHasEngine(vehicleLayoutEntity.isHasEngine())
                .withVehicleTypeEntity(vehicleTypeEntity)
                .withVehicleModelEntity(vehicleModelEntity)
                .withPlate(dto.getVehiclePlate())
                .withFleetId(dto.getFleetId())
                .withVehicleKm(dto.getVehicleKm())
                .withHasHubodometer(dto.getHasHubodometer())
                .withCreatedAt(Now.getOffsetDateTimeUtc())
                .withIsActive(true)
                .withRegisterOrigin(registerOrigin)
                .build();
    }

    @NotNull
    private VehicleDto createVehicleDto(@NotNull final VehicleEntity vehicleEntity) {
        final VehicleModelEntity vehicleModelEntity = vehicleEntity.getVehicleModelEntity();
        final VehicleTypeEntity vehicleTypeEntity = vehicleEntity.getVehicleTypeEntity();
        final VehicleLayoutEntity vehicleLayoutEntity = vehicleEntity.getVehicleLayoutEntity();
        final BranchEntity branchEntity = vehicleEntity.getBranchEntity();
        final Optional<AttachProcessEntity> attachProcessEntity = vehicleEntity.getAttachProcessEntity();

        return new VehicleDto(
                vehicleEntity.getId(),
                vehicleEntity.getPlate(),
                vehicleEntity.getFleetId(),
                vehicleEntity.isHasEngine(),
                vehicleEntity.isHasHubodometer(),
                vehicleModelEntity.getVehicleMakeEntity().getId(),
                vehicleModelEntity.getVehicleMakeEntity().getName(),
                vehicleModelEntity.getId(),
                vehicleModelEntity.getName(),
                vehicleLayoutEntity.getId(),
                vehicleLayoutEntity.getAxleQuantity(AxleLayoutEntity.FRONT_AXLE),
                vehicleLayoutEntity.getAxleQuantity(AxleLayoutEntity.REAR_AXLE),
                vehicleTypeEntity.getId(),
                vehicleTypeEntity.getName(),
                branchEntity.getId(),
                branchEntity.getName(),
                branchEntity.getGroup().getId(),
                branchEntity.getGroup().getName(),
                vehicleEntity.getVehicleKm(),
                vehicleEntity.isActive(),
                vehicleEntity.getAppliedTiresQuantity(),
                vehicleEntity.isAttached(),
                vehicleEntity.getCurrentAttachPosition(),
                attachProcessEntity.map(attachProcess -> createAttachedVehicles(vehicleEntity.getId(),
                                                                                attachProcess.getId(),
                                                                                attachProcess.getCurrentAttachEntities()))
                        .orElse(null));
    }

    @NotNull
    private AttachedVehiclesDto createAttachedVehicles(@NotNull final Long vehicleId,
                                                       @NotNull final Long attachProcessId,
                                                       @NotNull final Set<CurrentAttachEntity> currentAttaches) {
        final List<AttachedVehicleDto> attachedVehicles = currentAttaches.stream()
                .filter(currentAttach -> !currentAttach.getCurrentAttachVehicleId().equals(vehicleId))
                .map(this::createAttachedVehicle)
                .collect(Collectors.toList());
        return new AttachedVehiclesDto(attachProcessId, attachedVehicles);
    }

    @NotNull
    private AttachedVehicleDto createAttachedVehicle(@NotNull final CurrentAttachEntity currentAttachEntity) {
        final VehicleEntity vehicleEntity = currentAttachEntity.getVehicleEntity();
        return new AttachedVehicleDto(vehicleEntity.getId(),
                                      vehicleEntity.getPlate(),
                                      vehicleEntity.getFleetId(),
                                      vehicleEntity.isHasEngine(),
                                      currentAttachEntity.getPositionId());
    }
}
