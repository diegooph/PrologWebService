package br.com.zalf.prolog.webservice.v3.fleet.tireservice;

import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceIncreaseLifeCycleEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model.TireServiceMovementEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model.TireServiceRetreaderEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.register.TireServiceRegisterEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.servicetype.TireServiceTypeEntity;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class TireServiceCreator {
    @NotNull
    public static TireServiceEntity createTireService(@NotNull final TireServiceTypeEntity tireServiceTypeEntity,
                                                      @NotNull final TireEntity tireEntity,
                                                      @NotNull final String tireServiceOrigin,
                                                      @NotNull final BigDecimal tireTreadPrice) {
        return TireServiceEntity.builder()
                .withTireServiceTypeEntity(tireServiceTypeEntity)
                .withBranchId(tireEntity.getBranchEntity().getId())
                .withTireEntity(createTireEntity(tireEntity.getId()))
                .withServiceCost(tireTreadPrice)
                .withTireLifeCycle(tireEntity.getPreviousRetread())
                .withTireServiceOrigin(tireServiceOrigin)
                .build();
    }

    @NotNull
    public static TireServiceIncreaseLifeCycleEntity createTireServiceIncreaseLifeCycle(
            @NotNull final TireEntity tireEntity,
            @NotNull final TireServiceEntity tireServiceEntity,
            @NotNull final String tireServiceOrigin) {
        //noinspection ConstantConditions
        return TireServiceIncreaseLifeCycleEntity.builder()
                .withTireServiceId(tireServiceEntity.getId())
                .withTreadModelId(tireEntity.getTreadModelEntity().getId())
                .withNewTireLifeCycle(tireEntity.getTimesRetreaded())
                .withTireServiceOrigin(tireServiceOrigin)
                .build();
    }

    @NotNull
    public static TireServiceRegisterEntity createTireServiceRegister(
            @NotNull final TireServiceEntity tireServiceEntity,
            @NotNull final String tireServiceOrigin) {
        return TireServiceRegisterEntity.builder()
                .withTireId(tireServiceEntity.getTireServiceTypeEntity().getId())
                .withTireServiceId(tireServiceEntity.getId())
                .withTireServiceOrigin(tireServiceOrigin)
                .build();
    }

    @NotNull
    public static TireServiceMovementEntity createTireServiceMovement(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado,
            @NotNull final String fonteServicoRealizado) {
        return TireServiceMovementEntity.builder()
                .withTireMovementId(codMovimentacao)
                .withTireServiceId(codPneuServicoRealizado)
                .withTireServiceOrigin(fonteServicoRealizado)
                .build();
    }

    @NotNull
    public static TireServiceRetreaderEntity createTireServiceRetreader(
            @NotNull final Long codMovimentacao,
            @NotNull final Long codPneuServicoRealizado,
            @NotNull final Long codRecapadora) {
        return TireServiceRetreaderEntity.builder()
                .withTireMovementId(codMovimentacao)
                .withTireServiceMovementId(codPneuServicoRealizado)
                .withRetreaderId(codRecapadora)
                .build();
    }

    @NotNull
    private static TireEntity createTireEntity(@NotNull final Long tireId) {
        return TireEntity.builder().withId(tireId).build();
    }
}