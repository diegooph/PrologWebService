package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AlteracaoKmProcessoVisitor {

    void visit(@NotNull final AfericaoKmProcesso afericaoKmProcesso);

    void visit(@NotNull final ServicoPneuKmProcesso afericaoKmProcesso);

    void visit(@NotNull final ChecklistKmProcesso checklistKmProcesso);

    void visit(@NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso);

    void visit(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso);

    void visit(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso);

    void visit(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso);
}
