package test.frota;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

import java.sql.SQLException;
import java.util.List;

public class ChecklistTest extends BaseTest {

    private ChecklistModeloDao dao;

    @Override
    public void initialize() {
        dao = Injection.provideChecklistModeloDao();
    }

    @Test
    public void testGetModeloChecklist() throws SQLException {
        final ModeloChecklist modeloChecklist = dao.getModeloChecklist(5L, 40L);

        System.out.println(GsonUtils.getGson().toJson(modeloChecklist));
        Assert.assertNotNull(modeloChecklist);
    }

    @Test
    public void testGetModelosChecklistListagem() throws SQLException {
        final List<ModeloChecklistListagem> listagem =
                dao.getModelosChecklistListagemByCodUnidadeByCodFuncao(5L, "%");

        System.out.println(GsonUtils.getGson().toJson(listagem));
        Assert.assertFalse(listagem.isEmpty());
        Assert.assertEquals(2, listagem.size());
    }
}
