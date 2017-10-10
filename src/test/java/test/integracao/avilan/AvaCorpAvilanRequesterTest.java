package test.integracao.avilan;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfString;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfTipoVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfFarolDia;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

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
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testIncluirMedidaAfericao() throws Exception {
        requester.getVeiculoAtivo("MLK7250", CPF, DATA_NASCIMENTO);
        assertTrue(requester.insertAfericao(createIncluirMedida(), CPF, DATA_NASCIMENTO));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarVeiculosAtivos() throws Exception {
        final ArrayOfVeiculo veiculos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        assertNotNull(veiculos);
        assertTrue(!veiculos.getVeiculo().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarTiposVeiculo() throws Exception {
        final ArrayOfTipoVeiculo tiposVeiculo = requester.getTiposVeiculo(CPF, DATA_NASCIMENTO);
        assertNotNull(tiposVeiculo);
        assertTrue(!tiposVeiculo.getTipoVeiculo().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPlacasVeiculosByTipo() throws Exception {
        final ArrayOfString placas = requester.getPlacasVeiculoByTipo("", CPF, DATA_NASCIMENTO);
        assertNotNull(placas);
        assertTrue(!placas.getString().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarChecklists() throws Exception {
        final ArrayOfChecklistFiltro checklistFiltro = requester.getChecklists(
                1,
                "",
                "",
                "2017-09-28",
                "2017-10-10",
                "07011527966",
                "1992-09-25");
        assertNotNull(checklistFiltro);
        assertTrue(!checklistFiltro.getChecklistFiltro().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPneusVeiculo() throws Exception {
        final ArrayOfPneu pneus = requester.getPneusVeiculo(VEICULO_COM_PNEUS, CPF, DATA_NASCIMENTO);
        assertNotNull(pneus);
        assertTrue(!pneus.getPneu().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarQuestionariosColaborador() throws Exception {
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(CPF, DATA_NASCIMENTO);
        assertNotNull(questionarios);
        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPerguntasAlternativasQuestionario() throws Exception {
        final ArrayOfVeiculoQuestao veiculoQuestao =
                requester.getQuestoesVeiculo(
                        1,
                        VEICULO_COM_CHECK_VINCULADO,
                        AvacorpAvilanTipoChecklist.SAIDA,
                        CPF,
                        DATA_NASCIMENTO);
        assertNotNull(veiculoQuestao);
        assertTrue(!veiculoQuestao.getVeiculoQuestao().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void buscarFarolChecklist() throws Exception {
        final ArrayOfFarolDia farolDia =
                requester.getFarolChecklist(
                        8,
                        "2017-09-28",
                        "2017-09-28",
                        false,
                        CPF,
                        DATA_NASCIMENTO);
        assertNotNull(farolDia);
        assertTrue(!farolDia.getFarolDia().isEmpty());
    }

    @Test(timeout = 7 * 60 * 1000)
    public void testEnviarChecklist() throws Exception {
        //////////////////////////////////////////////////////////////////////////////
        // BUSCA OS QUESTIONÁRIOS DISPONÍVEIS PARA UM VEÍCULO
        //////////////////////////////////////////////////////////////////////////////
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(CPF, DATA_NASCIMENTO);
        assertNotNull(questionarios);
        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
        final Questionario questionario = questionarios.getQuestionarioVeiculos().get(0).getQuestionario();
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculo veiculos =
                questionarios.getQuestionarioVeiculos().get(0).getVeiculos();
        assertNotNull(questionario);
        assertNotNull(veiculos);
        assertTrue(!veiculos.getVeiculo().isEmpty());

        //////////////////////////////////////////////////////////////////////////////
        // BUSCA AS QUESTÕES DE UM QUESTIONÁRIO PARA UM VEÍCULO
        //////////////////////////////////////////////////////////////////////////////
        final Veiculo veiculo = veiculos.getVeiculo().get(0);
        assertNotNull(veiculo);
        assertNotNull(veiculo.getPlaca());
        assertTrue(veiculo.getMarcador() >= 0);
        final ArrayOfVeiculoQuestao arrayOfVeiculoQuestao = requester.getQuestoesVeiculo(
                questionario.getCodigoQuestionario(),
                veiculo.getPlaca(),
                AvacorpAvilanTipoChecklist.SAIDA,
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(arrayOfVeiculoQuestao);
        assertTrue(!arrayOfVeiculoQuestao.getVeiculoQuestao().isEmpty());

        //////////////////////////////////////////////////////////////////////////////
        // RESPONDE TODAS AS PERGUNTAS DO QUESTIONÁRIO SELECIONADO E ENVIA
        //////////////////////////////////////////////////////////////////////////////
        int codigoAvaliacao = -1;
        final ArrayOfRespostaAval arrayOfRespostaAval = new ArrayOfRespostaAval();
        for (VeiculoQuestao veiculoQuestao : arrayOfVeiculoQuestao.getVeiculoQuestao()) {
            assertNotNull(veiculoQuestao);
            assertNotNull(veiculoQuestao.getQuestoes());

            final List<Questao> questoes = veiculoQuestao.getQuestoes().getQuestao();
            assertNotNull(questoes);
            for (Questao questao : questoes) {
                assertNotNull(questao);
                codigoAvaliacao = questao.getCodigoAvaliacao();

                final ArrayOfResposta respostas = questao.getRespostas();
                assertNotNull(respostas);
                assertTrue(!respostas.getResposta().isEmpty());

                final RespostaAval respostaAval = new RespostaAval();
                // Responde sempre a primeira alternativa
                respostaAval.setCodigoResposta(respostas.getResposta().get(0).getCodigoResposta());
                respostaAval.setSequenciaQuestao(questao.getSequenciaQuestao());
                arrayOfRespostaAval.getRespostaAval().add(respostaAval);
            }
        }

        assertTrue(codigoAvaliacao != -1);
        final RespostasAvaliacao respostasAvaliacao = new RespostasAvaliacao();
        respostasAvaliacao.setCodigoAvaliacao(codigoAvaliacao);
        respostasAvaliacao.setOdometro(veiculo.getMarcador());
        respostasAvaliacao.setDtNascimento(DATA_NASCIMENTO);
        respostasAvaliacao.setCpf(CPF);
        respostasAvaliacao.setRespostas(arrayOfRespostaAval);
        assertNotNull(requester.insertChecklist(respostasAvaliacao, CPF, DATA_NASCIMENTO));
    }

    private IncluirMedida2 createIncluirMedida() {
        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // seta valores
        incluirMedida2.setVeiculo(VEICULO_COM_PNEUS);
        incluirMedida2.setTipoMarcador(AvaCorpAvilanTipoMarcador.HODOMETRO);
        incluirMedida2.setMarcador(642666);
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