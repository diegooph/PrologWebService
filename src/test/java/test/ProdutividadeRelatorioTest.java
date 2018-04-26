package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeColaboradorRelatorio;
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

    @Override
    public void initialize() {
        DATA_INICIAL = LocalDate.parse("2018-02-01");
        DATA_FINAL = LocalDate.parse("2018-02-28");
    }

    @Test
    public void testGetProdutividadeColaboradorRelatorio() throws SQLException {
        final ProdutividadeRelatorioDao dao = Injection.provideProdutividadeRelatorioDao();
        final List<ProdutividadeColaboradorRelatorio> relatorio =
                dao.getRelatorioProdutividadeColaborador(COD_UNIDADE, "03383283194", DATA_INICIAL, DATA_FINAL);

        System.out.println(relatorio.toString());
    }
}
