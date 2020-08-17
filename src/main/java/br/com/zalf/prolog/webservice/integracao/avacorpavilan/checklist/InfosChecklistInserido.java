package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-07-31
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class InfosChecklistInserido {

    @NotNull
    private final Long codChecklist;
    private final boolean checklistJaExiste;
    @Nullable
    private final Long codOsAberta;

    public boolean abriuOs() {
        return codOsAberta != null;
    }

}
