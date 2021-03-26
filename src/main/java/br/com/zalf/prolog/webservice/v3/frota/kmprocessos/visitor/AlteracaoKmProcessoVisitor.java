package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AlteracaoKmProcessoVisitor {

    void visitAfericao(@NotNull final AfericaoKmProcesso afericaoKmProcesso);

    void visitServicoPneu(@NotNull final ServicoPneuKmProcesso afericaoKmProcesso);

    void visitChecklist(@NotNull final ChecklistKmProcesso checklistKmProcesso);

    void visitChecklistOrdemServicoItem(
            @NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso);

    void visitMovimentacao(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso);

    void visitSocorroRota(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso);

    void visitTransferenciaVeiculo(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso);
}
