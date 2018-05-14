package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class ChecklistTest extends BaseTest {
    @Override
    public void initialize() {
        // do nothing
    }

    @Test
    public void testGetModelosChecklistListagem() throws SQLException {
        final ChecklistModeloDao dao = Injection.provideChecklistModeloDao();
        final List<ModeloChecklistListagem> listagem =
                dao.getModelosChecklistListagemByCodUnidadeByCodFuncao(5L, "%");

        System.out.println(GsonUtils.getGson().toJson(listagem));
        Assert.assertFalse(listagem.isEmpty());
        Assert.assertEquals(2, listagem.size());
    }
}
