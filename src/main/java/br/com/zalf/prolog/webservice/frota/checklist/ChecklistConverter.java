package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.model.farol.FarolChecklist;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 01/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ChecklistConverter {

    private ChecklistConverter() {
        throw new IllegalStateException(ChecklistConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static FarolChecklist createFarolChecklist(@NotNull final ResultSet rSet) throws Throwable {
        // TODO:
        return null;
    }
}