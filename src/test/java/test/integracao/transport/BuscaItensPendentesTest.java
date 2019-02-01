package test.integracao.transport;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.integracao.transport.IntegracaoTransportService;
import br.com.zalf.prolog.webservice.integracao.transport.ItemPendenteIntegracaoTransport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created on 01/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BuscaItensPendentesTest {
    private static final String TOKEN_TRANSLECCHI = "pdgcvsvt1bnhbaqt4ldlhq6i4d5v2ve1jd6g36gmtqgfpikpi7q";
    private IntegracaoTransportService service;

    @Before
    public void initialize() {
        DatabaseManager.init();
        service = new IntegracaoTransportService();
    }

    @Test
    public void testBuscaAfericoes() throws ProLogException {
        final List<ItemPendenteIntegracaoTransport> itensPendentes =
                service.getItensPendentes(TOKEN_TRANSLECCHI, 10L);

        Assert.assertNotNull(itensPendentes);
        Assert.assertTrue(itensPendentes.size() > 0);

        itensPendentes.forEach(itemPendenteIntegracaoTransport -> {
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getPlacaVeiculo());
            Assert.assertFalse(itemPendenteIntegracaoTransport.getPlacaVeiculo().isEmpty());
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getKmAberturaServico());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getKmAberturaServico() >= 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodOrdemServico());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodOrdemServico() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodUnidadeOrdemServico());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodUnidadeOrdemServico() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getStatusOrdemServico());
            Assert.assertEquals(itemPendenteIntegracaoTransport.getStatusOrdemServico(), StatusOrdemServico.ABERTA);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getDataHoraAberturaServico());
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodItemOrdemServico());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodItemOrdemServico() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodUnidadeItemOrdemServico());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodUnidadeItemOrdemServico() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getDataHoraPrimeiroApontamento());
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getStatusItemOrdemServico());
            Assert.assertEquals(itemPendenteIntegracaoTransport.getStatusItemOrdemServico(), StatusItemOrdemServico.PENDENTE);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getPrazoResolucaoItemHoras());
            final Integer prazo = itemPendenteIntegracaoTransport.getPrazoResolucaoItemHoras();
            Assert.assertTrue(prazo == 1 || prazo == 48 || prazo == 720);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getQtdApontamentos());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getQtdApontamentos() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodChecklistPrimeiroApontamento());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodChecklistPrimeiroApontamento() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodPergunta());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodPergunta() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getDescricaoPergunta());
            Assert.assertFalse(itemPendenteIntegracaoTransport.getDescricaoPergunta().isEmpty());
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getCodAlternativaPergunta());
            Assert.assertTrue(itemPendenteIntegracaoTransport.getCodAlternativaPergunta() > 0);
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getDescricaoAlternativa());
            Assert.assertFalse(itemPendenteIntegracaoTransport.getDescricaoAlternativa().isEmpty());
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getTipoOutros());
            if (!itemPendenteIntegracaoTransport.getTipoOutros()) {
                Assert.assertEquals(itemPendenteIntegracaoTransport.getDescricaoTipoOutros(), "NOK");
            }
            Assert.assertNotNull(itemPendenteIntegracaoTransport.getPrioridadeAlternativa());
            Assert.assertTrue(
                    itemPendenteIntegracaoTransport.getPrioridadeAlternativa() == PrioridadeAlternativa.BAIXA
                    || itemPendenteIntegracaoTransport.getPrioridadeAlternativa() == PrioridadeAlternativa.ALTA
                    || itemPendenteIntegracaoTransport.getPrioridadeAlternativa() == PrioridadeAlternativa.CRITICA);
        });
    }
}
