package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResponseInsertModeloChecklist extends AbstractResponse {
    @NotNull
    private final ResultInsertModeloChecklist resultInsert;

    private ResponseInsertModeloChecklist(@NotNull final ResultInsertModeloChecklist resultInsert,
                                          @NotNull final String msg) {
        this.resultInsert = resultInsert;
        setStatus(OK);
        setMsg(msg);
    }

    @NotNull
    public static ResponseInsertModeloChecklist ok(@NotNull final ResultInsertModeloChecklist resultInsert,
                                                   @NotNull final String msg) {
        return new ResponseInsertModeloChecklist(resultInsert, msg);
    }

    @NotNull
    public ResultInsertModeloChecklist getResultInsert() {
        return resultInsert;
    }
}
