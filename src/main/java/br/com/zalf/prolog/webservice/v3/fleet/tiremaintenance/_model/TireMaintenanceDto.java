package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
public final class TireMaintenanceDto {
    @NotNull
    private final Long tireMaintenanceId;
    @NotNull
    private final TipoServico tireMaintenanceType;
    @NotNull
    private final Long tireMaintenanceBranchId;
    @NotNull
    private final Integer amountTimesPointed;
    @NotNull
    private final Long vehicleId;
    @NotNull
    private final String vehiclePlate;
    @Nullable
    private final String fleetId;
    @NotNull
    private final Long tireId;
    @NotNull
    private final String tireClientNumber;
    @NotNull
    private final Long tireSizeId;
    @NotNull
    private final Long tireInspectionId;
    @Nullable
    private final Integer tireInspectionPosition;
    @Nullable
    private final Double tirePressureInspection;
    @Nullable
    private final Double tireInternalGroove;
    @Nullable
    private final Double tireMiddleInternalGroove;
    @Nullable
    private final Double tireMiddleExternalGroove;
    @Nullable
    private final Double tireExternalGroove;
    @Nullable
    private final Double tirePressure;
    @NotNull
    private final Double tirePressureRecommended;
    private final int timesRethreading;
    private final int maxRetreads;
    @NotNull
    private final LocalDateTime openedAtUtc;
    @NotNull
    private final TireMaintenanceStatus maintenanceStatus;
    private final boolean resolvedAutomatically;
    @Nullable
    private final LocalDateTime resolvedAtUtc;
    @Nullable
    private final Long resolverUserId;
    @Nullable
    private final String resolverUserCpf;
    @Nullable
    private final String resolverUserName;
    @Nullable
    private final Long vehicleKmAtResolution;
    @Nullable
    private final Long tireProblemId;
    @Nullable
    private final String tireProblemDescription;
    @Nullable
    private final Double tirePressureAfterMaintenance;
    @Nullable
    private final FormaColetaDadosAfericaoEnum dataInspectionType;
}
