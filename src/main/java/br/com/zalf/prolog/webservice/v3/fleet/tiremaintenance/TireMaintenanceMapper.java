package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoAlternativaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.ColaboradorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
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
        final Optional<AfericaoPneuValorEntity> valor = tireMaintenance.getValorAfericaoRelatedToPneu();
        final Optional<ColaboradorEntity> resolverUser = tireMaintenance.getResolverUser();
        final Optional<AfericaoAlternativaEntity> tireMaintenanceProblem = tireMaintenance.getTireMaintenanceProblem();
        return new TireMaintenanceDto(
                tireMaintenance.getId(),
                tireMaintenance.getMaintenanceType(),
                tireMaintenance.getBranchId(),
                tireMaintenance.getAmountTimesPointed(),
                tireMaintenance.getTireInspection().getVeiculo().getId(),
                tireMaintenance.getTireInspection().getVeiculo().getPlate(),
                tireMaintenance.getTireInspection().getVeiculo().getFleetId(),
                tireMaintenance.getTire().getCodigo(),
                tireMaintenance.getTire().getCodigoCliente(),
                tireMaintenance.getTire().getDimensaoPneu().getCodigo(),
                tireMaintenance.getTireInspection().getCodigo(),
                valor.map(AfericaoPneuValorEntity::getPosicao).orElse(null),
                valor.map(AfericaoPneuValorEntity::getPsi).orElse(null),
                tireMaintenance.getTire().getAlturaSulcoInterno(),
                tireMaintenance.getTire().getAlturaSulcoCentralInterno(),
                tireMaintenance.getTire().getAlturaSulcoCentralExterno(),
                tireMaintenance.getTire().getAlturaSulcoExterno(),
                tireMaintenance.getTire().getPressaoAtual(),
                tireMaintenance.getTire().getPressaoRecomendada(),
                tireMaintenance.getTire().getVidaAtual(),
                tireMaintenance.getTire().getVidaTotal(),
                tireMaintenance.getTireInspection().getDataHora(),
                tireMaintenance.getTireMaintenanceStatus(),
                tireMaintenance.isResolvedAutomatically(),
                tireMaintenance.getResolvedAt(),
                resolverUser.map(ColaboradorEntity::getCodigo).orElse(null),
                resolverUser.map(ColaboradorEntity::getCpfFormatado).orElse(null),
                resolverUser.map(ColaboradorEntity::getNome).orElse(null),
                tireMaintenance.getVehicleKmAtResolution(),
                tireMaintenanceProblem.map(AfericaoAlternativaEntity::getCodigo).orElse(null),
                tireMaintenanceProblem.map(AfericaoAlternativaEntity::getAlternativa).orElse(null),
                tireMaintenance.getTirePressureAfterMaintenance(),
                tireMaintenance.getDataInspectionType());
    }
}
