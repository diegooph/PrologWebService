package test.integracao.transport;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.integracao.transport.IntegracaoTransportService;
import br.com.zalf.prolog.webservice.integracao.transport.ItemPendenteIntegracaoTransport;
import br.com.zalf.prolog.webservice.integracao.transport.ItemResolvidoIntegracaoTransport;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class IntegracaoOrdemServicoTransportTest {
    private static final String TOKEN_TRANSLECCHI = "pdgcvsvt1bnhbaqt4ldlhq6i4d5v2ve1jd6g36gmtqgfpikpi7q";
    private IntegracaoTransportService service;

    @Before
    public void initialize() {
        DatabaseManager.init();
        service = new IntegracaoTransportService();
    }

    @Test
    public void testBuscaItensPendentes() throws ProLogException {
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
            Assert.assertTrue(
                    prazo == PrioridadeAlternativa.CRITICA.getPrazoResolucaoHoras()
                            || prazo == PrioridadeAlternativa.ALTA.getPrazoResolucaoHoras()
                            || prazo == PrioridadeAlternativa.BAIXA.getPrazoResolucaoHoras());
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
            // TODO: Verificar esse if e o tratamento do NOK.
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

    @Test
    public void testResolveItensPendentes() throws ProLogException {
        // Fazemos -1 para utilizar o código na busca.
        final Long codUltimoItemPendenteSincronizado = getCodUltimoItemPendente() - 1;
        final List<ItemPendenteIntegracaoTransport> itensPendentes =
                service.getItensPendentes(TOKEN_TRANSLECCHI, codUltimoItemPendenteSincronizado);

        final List<ItemResolvidoIntegracaoTransport> itensResolvidos = new ArrayList<>();
        for (final ItemPendenteIntegracaoTransport itemPendente : itensPendentes) {
            itensResolvidos.add(convert(itemPendente));
        }

        final SuccessResponseIntegracao successResponseIntegracao =
                service.resolverMultiplosItens(TOKEN_TRANSLECCHI, itensResolvidos);

        Assert.assertNotNull(successResponseIntegracao);
        Assert.assertNotNull(successResponseIntegracao.getMsg());
        System.out.println(GsonUtils.getGson().toJson(itensPendentes));
    }

    @NotNull
    private Long getCodUltimoItemPendente() throws ProLogException {
        Long codigo = 0L;
        final List<ItemPendenteIntegracaoTransport> itensPendentes =
                service.getItensPendentes(TOKEN_TRANSLECCHI, 0L);

        for (final ItemPendenteIntegracaoTransport itemPendente : itensPendentes) {
            if (itemPendente.getCodItemOrdemServico() > codigo) {
                codigo = itemPendente.getCodItemOrdemServico();
            }
        }

        return codigo;
    }

    @NotNull
    private ItemResolvidoIntegracaoTransport convert(@NotNull final ItemPendenteIntegracaoTransport itemPendente) {
        final ItemResolvidoIntegracaoTransport item = new ItemResolvidoIntegracaoTransport();
        item.setCodUnidadeOrdemServico(itemPendente.getCodUnidadeOrdemServico());
        item.setCodOrdemServico(itemPendente.getCodOrdemServico());
        item.setCodItemResolvido(itemPendente.getCodItemOrdemServico());
        item.setCpfColaboradoResolucao("39476386800");
        item.setPlacaVeiculo(itemPendente.getPlacaVeiculo());
        item.setKmColetadoVeiculo(itemPendente.getKmAberturaServico() + 100);
        item.setDuracaoResolucaoItemEmMilissegundos(Duration.ofMinutes(15L).toMillis());
        item.setFeedbackResolucao("Fechando item através de teste de integração");
        item.setDataHoraResolucao(LocalDateTime.now());
        return item;
    }
}
