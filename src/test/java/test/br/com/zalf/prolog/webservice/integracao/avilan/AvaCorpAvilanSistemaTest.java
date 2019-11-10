package test.br.com.zalf.prolog.webservice.integracao.avilan;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static test.br.com.zalf.prolog.webservice.integracao.avilan.AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS;
import static test.br.com.zalf.prolog.webservice.integracao.avilan.AvaCorpAvilanConstants.PROLOG_TOKEN;

/**
 * Created by luiz on 01/08/17.
 */
public class AvaCorpAvilanSistemaTest {
    private final Sistema sistema = SistemasFactory.createSistema(
            SistemaKey.AVACORP_AVILAN,
            IntegradorProLog.full(PROLOG_TOKEN),
            PROLOG_TOKEN);

    @Before
    public void setup() {
        // Printa no console todos os logs das requisições HTTP. Headers, Body...
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarVeiculosAtivos() throws Exception {
        assertNotNull(sistema.getVeiculosAtivosByUnidade(0L, null));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarNovaAfericao() throws Throwable {
        final NovaAfericao novaAfericao = sistema.getNovaAfericaoPlaca(1L,"LRN9162", TipoMedicaoColetadaAfericao.SULCO_PRESSAO.asString());
        assertNotNull(novaAfericao);
        System.out.println(GsonUtils.getGson().toJson(novaAfericao));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarAfericaoByCodigo() throws Throwable {
        assertNotNull(sistema.getAfericaoByCodigo(4L, 328L));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS, expected = Exception.class)
    public void testInsertAfericao() throws Throwable {
        sistema.insertAfericao(0L, new AfericaoPlaca(), true);
    }

    @Test
    public void testBuscarSelecaoModeloChecklistPlacaVeiculo() throws Throwable {
        assertNotNull(ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaSelecaoModelo(
                sistema.getModelosSelecaoRealizacao(0L, 0L)));
    }

    @Test
    public void testBuscarNovoChecklistHolder() throws Throwable {
        final Map<ModeloChecklist, List<String>> map =
                ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaSelecaoModelo(
                        sistema.getModelosSelecaoRealizacao(0L, 0L));
        // Não pode ser nulo
        assertNotNull(map);
        // Esperamos que venha algum questionário
        assertTrue(!map.isEmpty());

        // Já que não está vazio pegamos o primeiro elemento
        final Map.Entry<ModeloChecklist ,List<String>> entry = map.entrySet().iterator().next();
        final ModeloChecklist modeloChecklist = entry.getKey();
        final List<String> placas= entry.getValue();

        assertNotNull(modeloChecklist);
        assertNotNull(placas);
        // Deve ter pelo menos um veículo apto a realizar esse modelo de checklist
        assertTrue(!placas.isEmpty());


        assertNotNull(sistema.getModeloChecklistRealizacao(
                modeloChecklist.getCodigo(),
                -1L,
                /* Já que deve existir pelo menos um veículo, pegamos o primeiro da lista */
                placas.get(0),
                TipoChecklist.SAIDA));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS, expected = Throwable.class)
    public void testInsertChecklist() throws Throwable {
        sistema.insertChecklist(null, false, false);
    }
}