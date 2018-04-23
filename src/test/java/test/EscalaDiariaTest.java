package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.imports.escala_diaria.EscalaDiaria;
import br.com.zalf.prolog.webservice.imports.escala_diaria.EscalaDiariaDao;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 18/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaTest extends BaseTest {

    @Override
    public void initialize() {
        // do nothing
    }

    @Test
    public void testGetEscalasDiarias() throws SQLException {
        final EscalaDiariaDao escalaDiariaDao = Injection.provideEscalaDiariaDao();
        final List<EscalaDiaria> escalasDiarias =
                escalaDiariaDao.getEscalasDiarias(COD_UNIDADE, DATA_INICIAL, DATA_FINAL);

        System.out.println(escalasDiarias);
        Assert.assertFalse(escalasDiarias.isEmpty());
    }
}
