package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ChecklistItensNokGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaGlobusPiccoloturDao {

    void insertItensNokEnviadosGlobus(
            @NotNull final Connection conn,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable;
}
