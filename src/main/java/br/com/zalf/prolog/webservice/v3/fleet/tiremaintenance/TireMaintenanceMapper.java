package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionMeasureEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceProblemEntity;
import br.com.zalf.prolog.webservice.v3.user.ColaboradorEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class TireMaintenanceMapper {
    @NotNull
    public List<TireMaintenanceDto> toDto(@NotNull final List<TireMaintenanceEntity> tireMaintenances) {
        return tireMaintenances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private TireMaintenanceDto toDto(@NotNull final TireMaintenanceEntity tireMaintenance) {
        final Optional<InspectionMeasureEntity> valor = tireMaintenance.getValorAfericaoRelatedToPneu();
        final Optional<ColaboradorEntity> resolverUser = tireMaintenance.getResolverUser();
        final Optional<TireMaintenanceProblemEntity> tireMaintenanceProblem =
                tireMaintenance.getTireMaintenanceProblemEntity();
        return new TireMaintenanceDto(
                tireMaintenance.getId(),
                tireMaintenance.getMaintenanceType(),
                tireMaintenance.getBranchId(),
                tireMaintenance.getAmountTimesPointed(),
                tireMaintenance.getInspectionEntity().getVehicleEntity().getId(),
                tireMaintenance.getInspectionEntity().getVehicleEntity().getPlate(),
                tireMaintenance.getInspectionEntity().getVehicleEntity().getFleetId(),
                tireMaintenance.getTire().getId(),
                tireMaintenance.getTire().getClientNumber(),
                tireMaintenance.getTire().getTireSizeEntity().getId(),
                tireMaintenance.getInspectionEntity().getId(),
                valor.map(InspectionMeasureEntity::getTirePositionApplied).orElse(null),
                valor.map(InspectionMeasureEntity::getMeasuredPressure).orElse(null),
                tireMaintenance.getTire().getInternalGroove(),
                tireMaintenance.getTire().getMiddleInternalGroove(),
                tireMaintenance.getTire().getMiddleExternalGroove(),
                tireMaintenance.getTire().getExternalGroove(),
                tireMaintenance.getTire().getCurrentPressure(),
                tireMaintenance.getTire().getRecommendedPressure(),
                tireMaintenance.getTire().getTimesRetreaded(),
                tireMaintenance.getTire().getMaxRetreads(),
                tireMaintenance.getInspectionEntity().getInspectedAt(),
                tireMaintenance.getTireMaintenanceStatus(),
                tireMaintenance.isResolvedAutomatically(),
                tireMaintenance.getResolvedAt(),
                resolverUser.map(ColaboradorEntity::getCodigo).orElse(null),
                resolverUser.map(ColaboradorEntity::getCpfFormatado).orElse(null),
                resolverUser.map(ColaboradorEntity::getNome).orElse(null),
                tireMaintenance.getVehicleKmAtResolution(),
                tireMaintenanceProblem.map(TireMaintenanceProblemEntity::getId).orElse(null),
                tireMaintenanceProblem.map(TireMaintenanceProblemEntity::getName).orElse(null),
                tireMaintenance.getTirePressureAfterMaintenance(),
                tireMaintenance.getDataInspectionType());
    }
}
