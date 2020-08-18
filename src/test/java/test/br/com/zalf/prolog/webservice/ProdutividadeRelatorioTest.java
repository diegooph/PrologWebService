package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeRelatorioDao;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProdutividadeRelatorioTest extends BaseTest {
    private static final LocalDate DATA_INICIAL = LocalDate.parse("2018-03-18");
    private static final LocalDate DATA_FINAL = LocalDate.parse("2018-04-19");

    @Test
    public void testGetProdutividadeColaboradorRelatorio() throws SQLException {
        final ProdutividadeRelatorioDao dao = Injection.provideProdutividadeRelatorioDao();
        final List<ProdutividadeColaboradorRelatorio> relatorio =
                dao.getRelatorioProdutividadeColaborador(5L, "03383283194", DATA_INICIAL, DATA_FINAL);

        System.out.println(relatorio.toString());
    }
}
