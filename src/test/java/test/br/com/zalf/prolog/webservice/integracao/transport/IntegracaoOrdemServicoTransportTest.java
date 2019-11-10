package test.br.com.zalf.prolog.webservice.integracao.transport;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created on 01/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegracaoOrdemServicoTransportTest extends BaseTest {
    private static final String TOKEN_TRANSLECCHI = "pdgcvsvt1bnhbaqt4ldlhq6i4d5v2ve1jd6g36gmtqgfpikpi7q";
    private IntegracaoTransportService service;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        service = new IntegracaoTransportService();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    public void testBuscaItensPendentes() throws ProLogException {
        final List<ItemPendenteIntegracaoTransport> itensPendentes =
                service.getItensPendentes(TOKEN_TRANSLECCHI, 354083L);

        assertNotNull(itensPendentes);
        assertTrue(itensPendentes.size() > 0);

        itensPendentes.forEach(itemPendenteIntegracaoTransport -> {
            assertNotNull(itemPendenteIntegracaoTransport.getPlacaVeiculo());
            assertFalse(itemPendenteIntegracaoTransport.getPlacaVeiculo().isEmpty());
            assertNotNull(itemPendenteIntegracaoTransport.getKmAberturaServico());
            assertTrue(itemPendenteIntegracaoTransport.getKmAberturaServico() >= 0);
            assertNotNull(itemPendenteIntegracaoTransport.getCodOrdemServico());
            assertTrue(itemPendenteIntegracaoTransport.getCodOrdemServico() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getCodUnidadeOrdemServico());
            assertTrue(itemPendenteIntegracaoTransport.getCodUnidadeOrdemServico() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getStatusOrdemServico());
            assertEquals(itemPendenteIntegracaoTransport.getStatusOrdemServico(), StatusOrdemServico.ABERTA);
            assertNotNull(itemPendenteIntegracaoTransport.getDataHoraAberturaServico());
            assertNotNull(itemPendenteIntegracaoTransport.getCodItemOrdemServico());
            assertTrue(itemPendenteIntegracaoTransport.getCodItemOrdemServico() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getCodUnidadeItemOrdemServico());
            assertTrue(itemPendenteIntegracaoTransport.getCodUnidadeItemOrdemServico() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getDataHoraPrimeiroApontamento());
            assertNotNull(itemPendenteIntegracaoTransport.getStatusItemOrdemServico());
            assertEquals(itemPendenteIntegracaoTransport.getStatusItemOrdemServico(), StatusItemOrdemServico.PENDENTE);
            assertNotNull(itemPendenteIntegracaoTransport.getPrazoResolucaoItemHoras());
            final Integer prazo = itemPendenteIntegracaoTransport.getPrazoResolucaoItemHoras();
            assertTrue(
                    prazo == PrioridadeAlternativa.CRITICA.getPrazoResolucaoHoras()
                            || prazo == PrioridadeAlternativa.ALTA.getPrazoResolucaoHoras()
                            || prazo == PrioridadeAlternativa.BAIXA.getPrazoResolucaoHoras());
            assertNotNull(itemPendenteIntegracaoTransport.getQtdApontamentos());
            assertTrue(itemPendenteIntegracaoTransport.getQtdApontamentos() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getCodChecklistPrimeiroApontamento());
            assertTrue(itemPendenteIntegracaoTransport.getCodChecklistPrimeiroApontamento() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getCodPergunta());
            assertTrue(itemPendenteIntegracaoTransport.getCodPergunta() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getDescricaoPergunta());
            assertFalse(itemPendenteIntegracaoTransport.getDescricaoPergunta().isEmpty());
            assertNotNull(itemPendenteIntegracaoTransport.getCodAlternativaPergunta());
            assertTrue(itemPendenteIntegracaoTransport.getCodAlternativaPergunta() > 0);
            assertNotNull(itemPendenteIntegracaoTransport.getDescricaoAlternativa());
            assertFalse(itemPendenteIntegracaoTransport.getDescricaoAlternativa().isEmpty());
            assertNotNull(itemPendenteIntegracaoTransport.getTipoOutros());
            if (itemPendenteIntegracaoTransport.getTipoOutros()) {
                assertNotNull(itemPendenteIntegracaoTransport.getDescricaoTipoOutros());
            } else {
                assertNull(itemPendenteIntegracaoTransport.getDescricaoTipoOutros());
            }
            assertNotNull(itemPendenteIntegracaoTransport.getPrioridadeAlternativa());
            assertTrue(
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

        assertNotNull(successResponseIntegracao);
        assertNotNull(successResponseIntegracao.getMsg());
        System.out.println(GsonUtils.getGson().toJson(itensResolvidos));
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
        return new ItemResolvidoIntegracaoTransport(
                itemPendente.getCodUnidadeOrdemServico(),
                itemPendente.getCodOrdemServico(),
                itemPendente.getCodItemOrdemServico(),
                "39476386800",
                itemPendente.getPlacaVeiculo(),
                itemPendente.getKmAberturaServico() + 100,
                Duration.ofMinutes(15L).toMillis(),
                "Fechando item através de teste de integração",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2));
    }
}
