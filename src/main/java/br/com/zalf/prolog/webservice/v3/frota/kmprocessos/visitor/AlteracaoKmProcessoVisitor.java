package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AlteracaoKmProcessoVisitor {

    @NotNull
    AlteracaoKmResponse visit(@NotNull final AfericaoKmProcesso afericaoKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final ServicoPneuKmProcesso afericaoKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final ChecklistKmProcesso checklistKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso);

    @NotNull
    AlteracaoKmResponse visit(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso);
}
