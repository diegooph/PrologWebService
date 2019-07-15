package test.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

import java.sql.SQLException;
import java.util.List;

public class ChecklistTest extends BaseTest {

    private ChecklistModeloDao dao;
    private ChecklistService service;

    @Override
    public void initialize() {
        dao = Injection.provideChecklistModeloDao();
        service = new ChecklistService();
    }

    @Test
    public void testGetModeloChecklist() throws Throwable {
        final ModeloChecklistVisualizacao modeloChecklist = dao.getModeloChecklist(5L, 40L);

        System.out.println(GsonUtils.getGson().toJson(modeloChecklist));
        Assert.assertNotNull(modeloChecklist);
    }

    @Test
    public void testGetModelosChecklistListagem() throws Throwable {
        final List<ModeloChecklistListagem> listagem = dao.getModelosChecklistListagemByCodUnidade(5L);

        System.out.println(GsonUtils.getGson().toJson(listagem));
        Assert.assertFalse(listagem.isEmpty());
        Assert.assertEquals(2, listagem.size());
    }

    @Test
    public void testFarolChecklist() throws SQLException, ProLogException {
        final DeprecatedFarolChecklist farolChecklist =
                service.getFarolChecklist(5L, true, getValidToken("03383283194"));

        System.out.println(GsonUtils.getGson().toJson(farolChecklist));
        Assert.assertNotNull(farolChecklist);
        Assert.assertNotNull(farolChecklist.getFarolVeiculos());
        Assert.assertEquals(38, farolChecklist.getFarolVeiculos().size());

    }
}
