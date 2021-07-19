package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoAtualEntity;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoProcessoEntity;
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
        final Optional<AcoplamentoProcessoEntity> acoplamentoProcessoEntity =
                vehicleEntity.getAcoplamentoProcessoEntity();

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
                vehicleEntity.getPosicaoAcopladoAtual(),
                acoplamentoProcessoEntity.map(acoplamentoProcesso -> createAttachedVehicles(vehicleEntity.getId(),
                                                                                            acoplamentoProcesso.getCodigo(),
                                                                                            acoplamentoProcesso.getAcoplamentoAtualEntities()))
                        .orElse(null));
    }

    @NotNull
    private VeiculosAcopladosListagemDto createAttachedVehicles(
            @NotNull final Long codVeiculo,
            @NotNull final Long codProcessoAcoplamento,
            @NotNull final Set<AcoplamentoAtualEntity> acoplamentosAtuais) {
        final List<VeiculoAcopladoListagemDto> attachedVehicles = acoplamentosAtuais.stream()
                .filter(acoplamento -> !acoplamento.getCodVeiculoAcoplamentoAtual().equals(codVeiculo))
                .map(this::createAttachedVehicle)
                .collect(Collectors.toList());
        return new VeiculosAcopladosListagemDto(codProcessoAcoplamento, attachedVehicles);
    }

    @NotNull
    private VeiculoAcopladoListagemDto createAttachedVehicle(
            @NotNull final AcoplamentoAtualEntity acoplamentoAtualEntity) {
        final VehicleEntity vehicleEntity = acoplamentoAtualEntity.getVehicleEntity();
        return new VeiculoAcopladoListagemDto(vehicleEntity.getId(),
                                              vehicleEntity.getPlate(),
                                              vehicleEntity.getFleetId(),
                                              vehicleEntity.isHasEngine(),
                                              acoplamentoAtualEntity.getCodPosicao());
    }
}
