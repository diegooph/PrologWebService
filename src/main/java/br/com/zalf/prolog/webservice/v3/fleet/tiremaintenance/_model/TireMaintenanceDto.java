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
public class TireMaintenanceDto {
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
    @Nullable
    private final Long tireId;
    @Nullable
    private final String tireClientNumber;
    @Nullable
    private final Long tireSizeId;
    @NotNull
    private final Long tireInspectionId;
    @Nullable
    private final Double tirePressureInspection;
    @Nullable
    private final Integer tireInspectionPosition;
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
    @Nullable
    private final Double tirePressureRecommended;
    private final int vidaAtual;
    private final int vidaTotal;
    @NotNull
    private final LocalDateTime openedAtUtc;
    @NotNull
    private final ServicoPneuStatus maintenanceStatus;
    @Nullable
    private final LocalDateTime resolvedAtUtc;
    @Nullable
    private final Double psiInserida;
    @Nullable
    private final Long kmConserto;
    @Nullable
    private final String problemaApontado;
    private final boolean fechadoAutomaticamente;
    @Nullable
    private final FormaColetaDadosAfericaoEnum formaColetaDados;
    @Nullable
    private final String nomeMecanico;
    @Nullable
    private final String cpfMecanico;
    @Nullable
    private final Long codMecanico;
}
