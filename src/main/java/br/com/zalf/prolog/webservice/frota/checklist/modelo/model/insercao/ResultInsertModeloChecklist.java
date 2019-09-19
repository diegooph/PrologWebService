package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResultInsertModeloChecklist {
    @NotNull
    private final Long codModeloChecklistInserido;
    @NotNull
    private final Long codVersaoModeloChecklistInserido;

    public ResultInsertModeloChecklist(@NotNull final Long codModeloChecklistInserido,
                                       @NotNull final Long codVersaoModeloChecklistInserido) {
        this.codModeloChecklistInserido = codModeloChecklistInserido;
        this.codVersaoModeloChecklistInserido = codVersaoModeloChecklistInserido;
    }

    @NotNull
    public Long getCodModeloChecklistInserido() {
        return codModeloChecklistInserido;
    }

    @NotNull
    public Long getCodVersaoModeloChecklistInserido() {
        return codVersaoModeloChecklistInserido;
    }
}
