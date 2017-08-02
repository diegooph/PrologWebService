package test.integracao.avilan;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfQuestionarioVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static test.integracao.avilan.AvaCorpAvilanConstants.*;

/**
 * Created by luiz on 02/08/17.
 */
public class AvaCorpAvilanRequesterTest {
    private final AvaCorpAvilanRequester requester = new AvaCorpAvilanRequesterImpl();

    @Before
    public void setup() {
        // Printa no console todos os logs das requisições HTTP. Headers, Body...
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testIncluirMedidaAfericao() throws Exception {
        assertTrue(requester.insertAfericao(createIncluirMedida(), USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarVeiculosAtivos() throws Exception {
        final ArrayOfVeiculo veiculos = requester.getVeiculosAtivos(USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO);
        assertNotNull(veiculos);
        assertTrue(!veiculos.getVeiculo().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPneusVeiculo() throws Exception {
        final ArrayOfPneu pneus = requester.getPneusVeiculo(VEICULO_TEST_PLACA, USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO);
        assertNotNull(pneus);
        assertTrue(!pneus.getPneu().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarQuestionariosColaborador() throws Exception {
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO);
        assertNotNull(questionarios);
        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPerguntasAlternativasQuestionario() throws Exception {
        final ArrayOfVeiculoQuestao veiculoQuestao =
                requester.getQuestoesVeiculo(1, VEICULO_TEST_PLACA, USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO);
        assertNotNull(veiculoQuestao);
        assertTrue(!veiculoQuestao.getVeiculoQuestao().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS, expected = Exception.class)
    public void testEnviarChecklist() throws Exception {
        requester.insertChecklist(null, USER_TEST_CPF, USER_TEST_DATA_NASCIMENTO);
    }

    private IncluirMedida2 createIncluirMedida() {
        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // seta valores
        incluirMedida2.setVeiculo(VEICULO_TEST_PLACA);
        incluirMedida2.setTipoMarcador(AvaCorpAvilanTipoMarcador.HODOMETRO);
        incluirMedida2.setMarcador(542666);
        incluirMedida2.setDataMedida(AvaCorpAvilanUtils.createDatePattern(new Date(System.currentTimeMillis())));
        // Placas carreta 1, 2 e 3 nunca serão setadas. No ProLog apenas um veículo será aferido por vez. Caso a carreta
        // seja aferida, então a placa dela será setada em .setVeiculo()

        final ArrayOfMedidaPneu medidas = new ArrayOfMedidaPneu();
        final MedidaPneu medidaPneu = new MedidaPneu();
        medidaPneu.setCalibragem(100);
        medidaPneu.setNumeroFogoPneu("AVL1505");
        medidaPneu.setTriangulo1PrimeiroSulco(8);
        medidaPneu.setTriangulo1SegundoSulco(8);
        medidaPneu.setTriangulo1TerceiroSulco(8);
        medidaPneu.setTriangulo1QuartoSulco(8);
        medidas.getMedidaPneu().add(medidaPneu);

        incluirMedida2.setMedidas(medidas);

        return incluirMedida2;
    }
}