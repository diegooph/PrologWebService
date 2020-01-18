package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.entrega.escaladiaria.EscalaDiaria;
import br.com.zalf.prolog.webservice.entrega.escaladiaria.EscalaDiariaDao;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 18/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaTest extends BaseTest {
    private static final LocalDate DATA_INICIAL = LocalDate.parse("2018-03-18");
    private static final LocalDate DATA_FINAL = LocalDate.parse("2018-04-19");

    @Override
    public void initialize() {
        // do nothing
    }

    @Test
    public void testGetEscalasDiarias() throws SQLException {
        final EscalaDiariaDao escalaDiariaDao = Injection.provideEscalaDiariaDao();
        final List<EscalaDiaria> escalasDiarias =
                escalaDiariaDao.getEscalasDiarias(5L, DATA_INICIAL, DATA_FINAL);

        System.out.println(escalasDiarias);
        Assert.assertFalse(escalasDiarias.isEmpty());
    }
}
