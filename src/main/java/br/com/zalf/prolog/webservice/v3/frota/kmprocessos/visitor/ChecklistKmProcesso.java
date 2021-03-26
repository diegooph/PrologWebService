package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistKmProcesso extends AlteracaoKmProcesso<ChecklistEntity> {

    public ChecklistKmProcesso(@NotNull final Long codEmpresa,
                               @NotNull final Long codProcesso,
                               final long novoKm) {
        super(codEmpresa, codProcesso, novoKm);
    }

    public void accept(@NotNull final AlterarKmProcessoVisitor visitor) {
        visitor.visitChecklist(this);
    }
}
