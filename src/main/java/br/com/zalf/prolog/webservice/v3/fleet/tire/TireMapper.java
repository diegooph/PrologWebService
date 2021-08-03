package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementDestinationEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class TireMapper {
    @NotNull
    public TireEntity toEntity(@NotNull final TireCreateDto dto, @NotNull final OrigemAcaoEnum registerOrigin) {
        return TireEntity.builder()
                .withCompanyId(dto.getCodEmpresaAlocado())
                .withBranchEntity(createBranchEntity(dto.getCodUnidadeAlocado()))
                .withClientNumber(dto.getCodigoCliente())
                .withTireModelEntity(createTireModelEntity(dto.getCodModeloPneu()))
                .withTireSizeEntity(createTireSizeEntity(dto.getCodDimensaoPneu()))
                .withRecommendedPressure(dto.getPressaoRecomendadaPneu())
                .withTireStatus(StatusPneu.ESTOQUE)
                .withTimesRetreaded(dto.getVidaAtualPneu())
                .withMaxRetreads(dto.getVidaTotalPneu())
                .withTreadModelEntity(
                        dto.getCodModeloBanda() == null ? null : createTreadModelEntity(dto.getCodModeloBanda()))
                .withDot(dto.getDotPneu())
                .withPrice(dto.getValorPneu())
                .withCreatedAt(Now.getOffsetDateTimeUtc())
                .withIsTireNew(dto.getPneuNovoNuncaRodado())
                .withBranchIdRegister(dto.getCodUnidadeAlocado())
                .withRegisterOrigin(registerOrigin)
                .build();
    }

    @NotNull
    public List<TireDto> toDto(@NotNull final List<TireEntity> tires) {
        return tires.stream().map(this::toDto).collect(Collectors.toList());
    }

    @NotNull
    private TireDto toDto(@NotNull final TireEntity tireEntity) {
        final TreadModelEntity treadModelEntity = tireEntity.getTreadModelEntity();
        final VehicleEntity vehicleApplied = tireEntity.getVehicleApplied();
        final TireMovementDestinationEntity analysisMovement =
                tireEntity.getTireStatus().equals(StatusPneu.ANALISE)
                        ? tireEntity.getLastTireMovementByStatus(OrigemDestinoEnum.ANALISE)
                        : null;
        final TireMovementDestinationEntity scrapMovement =
                tireEntity.getTireStatus().equals(StatusPneu.DESCARTE)
                        ? tireEntity.getLastTireMovementByStatus(OrigemDestinoEnum.DESCARTE)
                        : null;
        return TireDto.of(tireEntity.getId(),
                          tireEntity.getClientNumber(),
                          tireEntity.getBranchEntity().getGroupEntity().getId(),
                          tireEntity.getBranchEntity().getGroupEntity().getName(),
                          tireEntity.getBranchEntity().getId(),
                          tireEntity.getBranchEntity().getName(),
                          tireEntity.getTimesRetreaded(),
                          tireEntity.getMaxRetreads(),
                          tireEntity.getRecommendedPressure(),
                          tireEntity.getCurrentPressure(),
                          tireEntity.getExternalGroove(),
                          tireEntity.getMiddleExternalGroove(),
                          tireEntity.getMiddleInternalGroove(),
                          tireEntity.getInternalGroove(),
                          tireEntity.getDot(),
                          tireEntity.getTireSizeEntity().getId(),
                          tireEntity.getTireSizeEntity().getWidth().doubleValue(),
                          tireEntity.getTireSizeEntity().getAspectRation().doubleValue(),
                          tireEntity.getTireSizeEntity().getDiameter(),
                          tireEntity.getTireModelEntity().getTireBrandEntity().getId(),
                          tireEntity.getTireModelEntity().getTireBrandEntity().getName(),
                          tireEntity.getTireModelEntity().getId(),
                          tireEntity.getTireModelEntity().getName(),
                          tireEntity.getTireModelEntity().getGroovesQuantity().intValue(),
                          tireEntity.getTireModelEntity().getGroovesWidth(),
                          tireEntity.getPrice(),
                          treadModelEntity == null ? null : treadModelEntity.getTreadBrandEntity().getId(),
                          treadModelEntity == null ? null : treadModelEntity.getTreadBrandEntity().getName(),
                          treadModelEntity == null ? null : treadModelEntity.getId(),
                          treadModelEntity == null ? null : treadModelEntity.getName(),
                          treadModelEntity == null ? null : treadModelEntity.getGroovesQuantity().intValue(),
                          treadModelEntity == null ? null : treadModelEntity.getGroovesWidth(),
                          treadModelEntity == null ? null : tireEntity.getPriceLastTreadApplied(),
                          tireEntity.isTireNew(),
                          tireEntity.getTireStatus(),
                          vehicleApplied == null ? null : vehicleApplied.getId(),
                          vehicleApplied == null ? null : vehicleApplied.getPlate(),
                          vehicleApplied == null ? null : vehicleApplied.getFleetId(),
                          tireEntity.getPositionApplied(),
                          analysisMovement == null ? null : analysisMovement.getRetreaderEntity().getId(),
                          analysisMovement == null ? null : analysisMovement.getRetreaderEntity().getName(),
                          analysisMovement == null ? null : analysisMovement.getAdditionalInformation(),
                          scrapMovement == null ? null : scrapMovement.getScrapReasonId(),
                          scrapMovement == null ? null : scrapMovement.getUrlScrapImage1(),
                          scrapMovement == null ? null : scrapMovement.getUrlScrapImage2(),
                          scrapMovement == null ? null : scrapMovement.getUrlScrapImage3());
    }

    @NotNull
    private BranchEntity createBranchEntity(@NotNull final Long codUnidadeAlocado) {
        return BranchEntity.builder().withId(codUnidadeAlocado).build();
    }

    @NotNull
    private TireSizeEntity createTireSizeEntity(@NotNull final Long codDimensaoPneu) {
        return TireSizeEntity.builder().withId(codDimensaoPneu).build();
    }

    @NotNull
    private TireModelEntity createTireModelEntity(@NotNull final Long codModeloPneu) {
        return TireModelEntity.builder().withId(codModeloPneu).build();
    }

    @NotNull
    private TreadModelEntity createTreadModelEntity(@NotNull final Long codModeloBanda) {
        return TreadModelEntity.builder().withId(codModeloBanda).build();
    }
}
