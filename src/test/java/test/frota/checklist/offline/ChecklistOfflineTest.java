package test.frota.checklist.offline;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineTest extends BaseTest {
    private final Long CPF_COLABORADOR = 3383283194L;
    private ChecklistOfflineService service;

    @Override
    public void initialize() throws Throwable {
        service = new ChecklistOfflineService();
        DatabaseManager.init();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void getChecklistOfflineAtivoEmpresaTest() throws ProLogException {
        final boolean checklistOfflineAtivoEmpresa = service.getChecklistOfflineAtivoEmpresa(CPF_COLABORADOR);

        Assert.assertTrue(checklistOfflineAtivoEmpresa);
    }
}
