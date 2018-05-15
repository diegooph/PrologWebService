package test.frota;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * Created on 10/3/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class PneuTest {

    @Test(timeout = 10 * 1000)
    public void updateSulcos() throws SQLException {
        final PneuDao dao = Injection.providePneuDao();

        final Pneu pneu = new Pneu();
        pneu.setCodigo(16304L);

        final Sulcos sulcos = new Sulcos();
        sulcos.setExterno(10.0);
        sulcos.setCentralExterno(11.0);
        sulcos.setCentralInterno(12.0);
        sulcos.setInterno(13.0);

        pneu.setSulcosAtuais(sulcos);

        dao.updateSulcos(pneu.getCodigo(), pneu.getSulcosAtuais(), 14L, null /* Alterar */);

        final Pneu pneuAtualizado = dao.getPneuByCod(16304L, 14L);
        final Sulcos sulcosAtualizados = pneuAtualizado.getSulcosAtuais();
        assertEquals(sulcos.getExterno(), sulcosAtualizados.getExterno(), 0);
        assertEquals(sulcos.getCentralExterno(), sulcosAtualizados.getCentralExterno(), 0);
        assertEquals(sulcos.getCentralInterno(), sulcosAtualizados.getCentralInterno(), 0);
        assertEquals(sulcos.getInterno(), sulcosAtualizados.getInterno(), 0);
    }
}
