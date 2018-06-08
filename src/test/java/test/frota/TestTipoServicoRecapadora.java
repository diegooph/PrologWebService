package test.frota;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.TipoServicoRecapadora;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.TipoServicoRecapadoraService;
import org.junit.Assert;
import org.junit.Test;
import test.BaseTest;

/**
 * Created on 08/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TestTipoServicoRecapadora extends BaseTest {

    private TipoServicoRecapadoraService service;

    @Override
    public void initialize() {
        service = new TipoServicoRecapadoraService();
    }

    @Test
    public void testInsertMovimentacaoEstoqueToAnalise() throws GenericException {
        final TipoServicoRecapadora servicoRecapadora = createTipoServicoRecapadora();
        final AbstractResponse response =
                service.insertTipoServicoRecapadora("2uuik5td5710delsfn6u2mi3i8", servicoRecapadora);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
    }

    private TipoServicoRecapadora createTipoServicoRecapadora() {
        final TipoServicoRecapadora servico = new TipoServicoRecapadora();
        servico.setCodEmpresa(3L);
        servico.setNome("Teste");
        return servico;
    }
}
