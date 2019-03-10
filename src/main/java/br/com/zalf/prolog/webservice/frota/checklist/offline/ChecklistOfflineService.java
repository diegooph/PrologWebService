package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineService {
    @NotNull
    private static final String TAG = ChecklistOfflineService.class.getSimpleName();
    @NotNull
    private final ChecklistOfflineDao dao = Injection.provideChecklistOfflineDao();

    public boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long cpfColaborador) throws ProLogException {
        try {
            return dao.getChecklistOfflineAtivoEmpresa(cpfColaborador);
        } catch (Throwable t) {
            final String msg =
                    "Erro ao busca informação se empresa do colaborador está liberada para realizar checklist offline";
            Log.e(TAG, msg);
            throw Injection.provideProLogExceptionHandler().map(t, msg);
        }
    }
}
