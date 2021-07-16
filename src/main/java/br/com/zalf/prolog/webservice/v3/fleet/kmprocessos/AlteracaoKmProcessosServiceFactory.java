package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.v3.fleet.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder.ChecklistWorkOrderService;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.AlteracaoKmProcesso;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao.MovimentacaoProcessoService;
import br.com.zalf.prolog.webservice.v3.fleet.servicopneu.ServicoPneuService;
import br.com.zalf.prolog.webservice.v3.fleet.socorrorota.SocorroRotaService;
import br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo.TransferenciaVeiculoService;
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
    private final AfericaoService afericaoService;
    @NotNull
    private final ServicoPneuService servicoPneuService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistWorkOrderService checklistWorkOrderService;
    @NotNull
    private final MovimentacaoProcessoService movimentacaoProcessoService;
    @NotNull
    private final SocorroRotaService socorroRotaService;
    @NotNull
    private final TransferenciaVeiculoService transferenciaVeiculoService;

    @NotNull
    public KmProcessoAtualizavel createService(@NotNull final AlteracaoKmProcesso alteracaoKmProcesso) {
        switch (alteracaoKmProcesso.getTipoProcesso()) {
            case AFERICAO:
                return afericaoService;
            case FECHAMENTO_SERVICO_PNEU:
                return servicoPneuService;
            case CHECKLIST:
                return checklistService;
            case FECHAMENTO_ITEM_CHECKLIST:
                return checklistWorkOrderService;
            case MOVIMENTACAO:
                return movimentacaoProcessoService;
            case SOCORRO_EM_ROTA:
                return socorroRotaService;
            case TRANSFERENCIA_DE_VEICULOS:
                return transferenciaVeiculoService;
            case ACOPLAMENTO:
            case EDICAO_DE_VEICULOS:
            default:
                throw new GenericException(
                        "Não é possível alterar o km de processos de acoplamento e de edição de veículo.");
        }
    }
}
