package test.br.com.zalf.prolog.webservice.pilares.frota.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.PneuService;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created on 10/3/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class PneuTest extends BaseTest {

    private PneuService service;

    @Override
    public void initialize() {
        service = new PneuService();
    }

    @Test(timeout = 10 * 1000)
    public void updateSulcos() throws Throwable {
        final PneuDao dao = Injection.providePneuDao();

        final PneuComum pneu = new PneuComum();
        pneu.setCodigo(16304L);

        final Sulcos sulcos = new Sulcos();
        sulcos.setExterno(10.0);
        sulcos.setCentralExterno(11.0);
        sulcos.setCentralInterno(12.0);
        sulcos.setInterno(13.0);

        pneu.setSulcosAtuais(sulcos);

        dao.updateSulcos(null /* Alterar */, pneu.getCodigo(), pneu.getSulcosAtuais());

        final Pneu pneuAtualizado = dao.getPneuByCod(16304L, 14L);
        final Sulcos sulcosAtualizados = pneuAtualizado.getSulcosAtuais();
        assertEquals(sulcos.getExterno(), sulcosAtualizados.getExterno(), 0);
        assertEquals(sulcos.getCentralExterno(), sulcosAtualizados.getCentralExterno(), 0);
        assertEquals(sulcos.getCentralInterno(), sulcosAtualizados.getCentralInterno(), 0);
        assertEquals(sulcos.getInterno(), sulcosAtualizados.getInterno(), 0);
    }

    @Test
    public void getPneusMovimentacao() throws Exception {
        final List<Pneu> pneusAnalise = service.getPneusByCodUnidadesByStatus(Collections.singletonList(5L), StatusPneu.ANALISE.asString());
        System.out.println(pneusAnalise);
        Assert.assertNotNull(pneusAnalise);
        Assert.assertFalse(pneusAnalise.isEmpty());
        Assert.assertNotNull(pneusAnalise.get(0));
    }
}
