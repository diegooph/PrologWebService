package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder.ChecklistWorkOrderService;
import br.com.zalf.prolog.webservice.v3.fleet.helponroad.HelpOnRoadService;
import br.com.zalf.prolog.webservice.v3.fleet.inspection.InspectionService;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.AlteracaoKmProcesso;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao.MovimentacaoProcessoService;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance.TireMaintenanceService;
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
public class AlteracaoKmProcessosServiceFactory {
    @NotNull
    private final InspectionService inspectionService;
    @NotNull
    private final TireMaintenanceService tireMaintenanceService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistWorkOrderService checklistWorkOrderService;
    @NotNull
    private final MovimentacaoProcessoService movimentacaoProcessoService;
    @NotNull
    private final HelpOnRoadService helpOnRoadService;
    @NotNull
    private final VehicleTransferService vehicleTransferService;

    @NotNull
    public KmProcessoAtualizavel createService(@NotNull final AlteracaoKmProcesso alteracaoKmProcesso) {
        switch (alteracaoKmProcesso.getTipoProcesso()) {
            case AFERICAO:
                return inspectionService;
            case FECHAMENTO_SERVICO_PNEU:
                return tireMaintenanceService;
            case CHECKLIST:
                return checklistService;
            case FECHAMENTO_ITEM_CHECKLIST:
                return checklistWorkOrderService;
            case MOVIMENTACAO:
                return movimentacaoProcessoService;
            case SOCORRO_EM_ROTA:
                return helpOnRoadService;
            case TRANSFERENCIA_DE_VEICULOS:
                return vehicleTransferService;
            case ACOPLAMENTO:
            case EDICAO_DE_VEICULOS:
            default:
                throw new GenericException(
                        "Não é possível alterar o km de processos de acoplamento e de edição de veículo.");
        }
    }
}
