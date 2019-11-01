package test.br.com.zalf.prolog.webservice.pilares.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.PneuTipoServicoService;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuTipoServico;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

/**
 * Created on 08/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuTipoServicoTest extends BaseTest {

    private PneuTipoServicoService service;

    @Override
    public void initialize() {
        service = new PneuTipoServicoService();
    }

    @Test
    public void testInsertPneuTipoServico() throws ProLogException {
        final PneuTipoServico servicoRecapadora = createTipoServicoRecapadora();
        final AbstractResponse response =
                service.insertPneuTipoServico("evshhe25lalkoesgi14ahdjv86", servicoRecapadora);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals("OK", response.getStatus());

        final Long codServico = ((ResponseWithCod) response).getCodigo();
        final PneuTipoServico servicoRetorno = service.getPneuTipoServico(3L, codServico);

        Assert.assertNotNull(servicoRetorno);
        Assert.assertNotNull(servicoRetorno.getCodigo());
        Assert.assertEquals(codServico, servicoRetorno.getCodigo());
        Assert.assertEquals(servicoRecapadora.getCodEmpresa(), servicoRetorno.getCodEmpresa());
        Assert.assertEquals(servicoRecapadora.getNome(), servicoRetorno.getNome());
    }

    private PneuTipoServico createTipoServicoRecapadora() {
        final PneuTipoServico servico = new PneuTipoServico();
        servico.setCodEmpresa(3L);
        servico.setNome("Vulcanização");
        return servico;
    }
}
