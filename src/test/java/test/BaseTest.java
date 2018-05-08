package test;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import org.junit.Before;

import java.time.LocalDate;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class BaseTest extends DatabaseConnection {

    protected static long COD_UNIDADE = 5;
    protected static LocalDate DATA_INICIAL = LocalDate.parse("2018-03-18");
    protected static LocalDate DATA_FINAL = LocalDate.parse("2018-04-19");

    public BaseTest() {
        DatabaseManager.init();
    }

    @Before
    public abstract void initialize();
}
