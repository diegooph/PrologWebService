package br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.*;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class InspectionMapper {
    @NotNull
    public List<VehicleInspectionDto> toVehicleInspectionDto(
            @NotNull final List<VehicleInspectionProjection> inspections) {
        final Map<Long, List<MeasureDto>> measures = groupMeasuresByVehicleInspections(inspections);
        return measures.keySet().stream()
                .map(inspectionId -> {
                    final VehicleInspectionProjection firstInspection = inspections.stream()
                            .filter(vehicleInspection -> Objects.equals(vehicleInspection.getCodigo(), inspectionId))
                            .findFirst()
                            .orElseThrow();
                    return toDto(firstInspection, measures.get(firstInspection.getCodigo()));
                })
                .collect(toList());
    }

    @NotNull
    public List<TireInspectionDto> toTireInspectionDto(@NotNull final List<TireInspectionProjection> inspections) {
        final Map<Long, List<MeasureDto>> measures = groupMeasuresByTireInspections(inspections);
        return measures.keySet().stream()
                .map(inspectionId -> {
                    final TireInspectionProjection firstInspection = inspections.stream()
                            .filter(tireInspection -> Objects.equals(tireInspection.getCodigo(), inspectionId))
                            .findFirst()
                            .orElseThrow();
                    return toDto(firstInspection, measures.get(firstInspection.getCodigo()));
                })
                .collect(toList());
    }

    @NotNull
    private TireInspectionDto toDto(@NotNull final TireInspectionProjection inspection,
                                    @NotNull final List<MeasureDto> measures) {
        return TireInspectionDto.of(inspection.getCodigo(),
                                    inspection.getCodUnidade(),
                                    inspection.getCodColaboradroAferidor(),
                                    inspection.getCpfAferidor(),
                                    inspection.getNomeAferidor(),
                                    inspection.getDataHoraAfericaoUtc(),
                                    inspection.getDataHoraAfericaoTzAplicado(),
                                    inspection.getTipoMedicaoColetadaAfericao(),
                                    inspection.getTipoProcessoColetaAfericao(),
                                    inspection.getTempoRealizacaoAfericaoInMillis(),
                                    inspection.getFormaColetaDadosAfericao(),
                                    measures.isEmpty() ? null : measures);
    }

    @NotNull
    private VehicleInspectionDto toDto(@NotNull final VehicleInspectionProjection inspection,
                                       @NotNull final List<MeasureDto> measures) {
        return VehicleInspectionDto.of(inspection.getCodigo(),
                                       inspection.getCodUnidade(),
                                       inspection.getCodColaboradorAferidor(),
                                       inspection.getCpfAferidor(),
                                       inspection.getNomeAferidor(),
                                       inspection.getCodVeiculo(),
                                       inspection.getPlacaVeiculo(),
                                       inspection.getIdentificadorFrota(),
                                       inspection.getKmVeiculo(),
                                       inspection.getDataHoraAfericaoUtc(),
                                       inspection.getDataHoraAfericaoTzAplicado(),
                                       inspection.getTipoMedicaoColetadaAfericao(),
                                       inspection.getTipoProcessoColetaAfericao(),
                                       inspection.getTempoRealizacaoAfericaoInMillis(),
                                       inspection.getFormaColetaDadosAfericao(),
                                       measures.isEmpty() ? null : measures);
    }

    @NotNull
    private Map<Long, List<MeasureDto>> groupMeasuresByTireInspections(
            @NotNull final List<TireInspectionProjection> inspections) {
        return inspections.stream()
                .collect(groupingBy(TireInspectionProjection::getCodigo,
                                    mapping(this::generateMeasureFromInspection, toUnmodifiableList())));
    }

    @NotNull
    private Map<Long, List<MeasureDto>> groupMeasuresByVehicleInspections(
            @NotNull final List<VehicleInspectionProjection> inspections) {
        return inspections.stream()
                .collect(groupingBy(VehicleInspectionProjection::getCodigo,
                                    mapping(this::generateMeasureFromInspection, toUnmodifiableList())));
    }

    @NotNull
    private MeasureDto generateMeasureFromInspection(@NotNull final TireInspectionProjection inspection) {
        return MeasureDto.of(inspection.getCodPneu(),
                             inspection.getCodigClientePneu(),
                             inspection.getPosicao(),
                             inspection.getVidaMomentoAfericao(),
                             inspection.getPsi(),
                             inspection.getAlturaSulcoInterno(),
                             inspection.getAlturaSulcoCentralInterno(),
                             inspection.getAlturaSulcoCentralExterno(),
                             inspection.getAlturaSulcoExterno());
    }

    @NotNull
    private MeasureDto generateMeasureFromInspection(@NotNull final VehicleInspectionProjection inspection) {
        return MeasureDto.of(inspection.getCodPneu(),
                             inspection.getCodigClientePneu(),
                             inspection.getPosicao(),
                             inspection.getVidaMomentoAfericao(),
                             inspection.getPsi(),
                             inspection.getAlturaSulcoInterno(),
                             inspection.getAlturaSulcoCentralInterno(),
                             inspection.getAlturaSulcoCentralExterno(),
                             inspection.getAlturaSulcoExterno());
    }
}
