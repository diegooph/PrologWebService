package br.com.zalf.prolog.webservice.v3.fleet.processeskm;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder.ChecklistWorkOrderService;
import br.com.zalf.prolog.webservice.v3.fleet.helponroad.HelpOnRoadService;
import br.com.zalf.prolog.webservice.v3.fleet.inspection.InspectionService;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.ProcessKmUpdatable;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.UpdateProcessKm;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance.TireMaintenanceService;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement.TireMovementProcessService;
import br.com.zalf.prolog.webservice.v3.fleet.transfer.VehicleTransferService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateProcessKmServiceFactory {
    @NotNull
    private final InspectionService inspectionService;
    @NotNull
    private final TireMaintenanceService tireMaintenanceService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistWorkOrderService checklistWorkOrderService;
    @NotNull
    private final TireMovementProcessService tireMovementProcessService;
    @NotNull
    private final HelpOnRoadService helpOnRoadService;
    @NotNull
    private final VehicleTransferService vehicleTransferService;

    @NotNull
    public ProcessKmUpdatable createService(@NotNull final UpdateProcessKm updateProcessKm) {
        switch (updateProcessKm.getProcessType()) {
            case AFERICAO:
                return inspectionService;
            case FECHAMENTO_SERVICO_PNEU:
                return tireMaintenanceService;
            case CHECKLIST:
                return checklistService;
            case FECHAMENTO_ITEM_CHECKLIST:
                return checklistWorkOrderService;
            case MOVIMENTACAO:
                return tireMovementProcessService;
            case SOCORRO_EM_ROTA:
                return helpOnRoadService;
            case TRANSFERENCIA_DE_VEICULOS:
                return vehicleTransferService;
            case ACOPLAMENTO:
            case EDICAO_DE_VEICULOS:
            default:
                throw new GenericException(
                        "It is not possible to update the collected km of 'coupling' and 'update vehicle' processes.");
        }
    }
}
