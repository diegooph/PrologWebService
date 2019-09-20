package br.com.zalf.prolog.webservice.pilares.frota;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.errorhandling.exception.RecapadoraException;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.RecapadoraService;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import br.com.zalf.prolog.webservice.BaseTest;

/**
 * Created on 15/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RecapadoraTest extends BaseTest {

    private RecapadoraService service;

    @Override
    public void initialize() {
        service = new RecapadoraService();
    }

    @Test
    public void testInsertRecapadora() throws RecapadoraException {
        final Recapadora recapadora = createRecapadora();
        final AbstractResponse response = service.insertRecapadora("jbr9e3rsq9c0dqknrtv03b04rn", recapadora);

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals("OK", response.getStatus());

        final Long codRecapadora = ((ResponseWithCod) response).getCodigo();
        final Recapadora recapadoraRetorno = service.getRecapadora(3L, codRecapadora);

        Assert.assertNotNull(recapadoraRetorno);
        Assert.assertNotNull(recapadoraRetorno.getCodigo());
        Assert.assertEquals(codRecapadora, recapadoraRetorno.getCodigo());
        Assert.assertEquals(recapadora.getCodEmpresa(), recapadoraRetorno.getCodEmpresa());
        Assert.assertEquals(recapadora.getNome(), recapadoraRetorno.getNome());
    }

    @NotNull
    private Recapadora createRecapadora() {
        final Recapadora recapadora = new Recapadora();
        recapadora.setNome("TESTE");
        recapadora.setCodEmpresa(3L);
        return recapadora;
    }
}
