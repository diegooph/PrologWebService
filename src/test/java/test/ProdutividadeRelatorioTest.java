package test;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeColaboradorRelatorio;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio.ProdutividadeRelatorioDao;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProdutividadeRelatorioTest extends BaseTest {

    private List<Long> cpfs;

    @Override
    public void initialize() {
        cpfs = new ArrayList<>();
        cpfs.add(3383283194L);
        cpfs.add(1606943537L);
        DATA_INICIAL = LocalDate.parse("2018-02-01");
        DATA_FINAL = LocalDate.parse("2018-02-28");
    }

    @Test
    public void testGetProdutividadeColaboradorRelatorio() throws SQLException {
        final ProdutividadeRelatorioDao dao = Injection.provideProdutividadeRelatorioDao();
        final List<ProdutividadeColaboradorRelatorio> relatorio =
                dao.getRelatorioProdutividadeColaborador(cpfs, COD_UNIDADE, DATA_INICIAL, DATA_FINAL);

        System.out.println(relatorio);
        Assert.assertFalse(relatorio.isEmpty());
        Assert.assertEquals(1, relatorio.size());
        Assert.assertNotNull(relatorio.get(0).getProdutividadeDias());
        Assert.assertEquals(14, relatorio.get(0).getProdutividadeDias().size());
//        Assert.assertNotNull(relatorio.get(1).getProdutividadeDias());
//        Assert.assertEquals(14, relatorio.get(1).getProdutividadeDias().size());
    }
}
