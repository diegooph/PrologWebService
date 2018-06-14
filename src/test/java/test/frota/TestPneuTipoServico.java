package test.frota;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.PneuTipoServicoService;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuTipoServico;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

/**
 * Created on 08/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TestPneuTipoServico extends BaseTest {

    private PneuTipoServicoService service;

    @Override
    public void initialize() {
        service = new PneuTipoServicoService();
    }

    @Test
    public void testInsertMovimentacaoEstoqueToAnalise() throws ProLogException {
        final PneuTipoServico servicoRecapadora = createTipoServicoRecapadora();
        final AbstractResponse response =
                service.insertPneuTipoServico("2uuik5td5710delsfn6u2mi3i8", servicoRecapadora);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
    }

    private PneuTipoServico createTipoServicoRecapadora() {
        final PneuTipoServico servico = new PneuTipoServico();
        servico.setCodEmpresa(3L);
        servico.setNome("Teste");
        return servico;
    }
}
