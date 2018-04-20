package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.imports.escala_diaria.EscalaDiaria;
import br.com.zalf.prolog.webservice.imports.escala_diaria.EscalaDiariaDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 18/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaTest extends DatabaseConnection {

    private static final long COD_UNIDADE = 5;
    private static final LocalDate DATA_INICIAL = LocalDate.parse("2018-03-18");
    private static final LocalDate DATA_FINAL = LocalDate.parse("2018-04-19");

    @Before
    public void initialize() {
        DatabaseManager.init();
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
