package test.integracao.avilan;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvilanPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfAfericaoFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanDaoImpl;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanSincronizadorTiposVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void testBuscarAfericoes() throws Exception {
        final ArrayOfAfericaoFiltro afericoes = requester.getAfericoes(
                11,
                1,
                "",
                "",
                "2017-09-28",
                "2017-10-11",
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(afericoes);
        assertTrue(!afericoes.getAfericaoFiltro().isEmpty());
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
    public void testBuscarChecklistByCodigo() throws Exception {
        final ChecklistFiltro checklistFiltro = requester.getChecklistByCodigo(
                34360,
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(checklistFiltro);
        assertNotNull(checklistFiltro.getAvaliacao());
        System.out.println(GsonUtils.getGson().toJson(checklistFiltro));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarChecklists() throws Exception {
        final ArrayOfChecklistFiltro checklistFiltro = requester.getChecklists(
                11,
                1,
                "",
                "",
                "2017-09-28",
                "2017-10-11",
                CPF,
                DATA_NASCIMENTO);
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
                        1,
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

    @Test
    public void testeSicronizadorTipoVeiculo() throws Exception{
        new AvaCorpAvilanSincronizadorTiposVeiculos(new AvaCorpAvilanDaoImpl())
                .sync(requester.getTiposVeiculo(CPF, DATA_NASCIMENTO).getTipoVeiculo());
    }

    @Test
    public void testeRotinaMapeamentoPneuAvilanProlog() throws Exception {
        final ArrayOfTipoVeiculo tiposVeiculo = requester.getTiposVeiculo(CPF, DATA_NASCIMENTO);
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);

        final Map<TipoVeiculoAvilan, String> tiposPlacas = new HashMap<>();

        System.out.println("TOTAL DE TIPOS ENCONTRADOS: "+tiposVeiculo.getTipoVeiculo().size());
        System.out.println("TOTAL DE VEICULOS ATIVOS: "+veiculosAtivos.getVeiculo().size());

        for (TipoVeiculoAvilan veiculoAvilan : tiposVeiculo.getTipoVeiculo()) {
            int maxNumPneu = 0;
            final ArrayOfString placasVeiculoByTipo = requester.getPlacasVeiculoByTipo(veiculoAvilan.getCodigo(), CPF, DATA_NASCIMENTO);
            final int placasAtivasAssociadas = getplacasAtivasAssociadas(placasVeiculoByTipo, veiculosAtivos);
            System.out.println("Tipo Veiculo Avila: "+veiculoAvilan.getNome() +" Placas associadas: "+placasAtivasAssociadas);

            for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculo : veiculosAtivos.getVeiculo()) {
                for (String placa : placasVeiculoByTipo.getString()) {
                    if (placa.equals(veiculo.getPlaca())) {
                        if (veiculo.getQuantidadePneu() > maxNumPneu) {
                            // para cada tipo de veículo o Map registrará a placa com maior quantidade de pneus
                            tiposPlacas.put(veiculoAvilan, veiculo.getPlaca());
                            maxNumPneu = veiculo.getQuantidadePneu();
                        }
                    }
                }
            }
        }

        System.out.println(GsonUtils.getGson().toJson(tiposPlacas));

        for (String placa : tiposPlacas.values()) {
            final ArrayOfPneu pneusVeiculo = requester.getPneusVeiculo(placa, CPF, DATA_NASCIMENTO);
            for (Pneu pneu : pneusVeiculo.getPneu()) {
                final TipoVeiculoAvilan tipoVeiculoAvilan = tiposPlacas.entrySet()
                        .stream()
                        .filter(value -> value.getValue().equals(placa))
                        .findFirst()
                        .map(Map.Entry::getKey).orElse(null);
                final String posicaoAvilan = pneu.getPosicao();
                final int posicaoProlog = AvilanPosicaoPneuMapper.mapToProLog(posicaoAvilan);
                if (tipoVeiculoAvilan != null) {
                    insertIntoPneuPosicaoAvilanProlog(tipoVeiculoAvilan.getCodigo(), posicaoAvilan, posicaoProlog);
                } else {
                    System.out.println("Tipo é null para placa: " + placa);
                }
            }
        }
    }

    @Test
    public void testeMapeamentoPlacasEspecificas() throws Exception {
//        final TipoVeiculoAtivo tipoVeiculo = new TipoVeiculoAtivo();
//        tipoVeiculo.setCodigo();
//        tipoVeiculo.setNome("CT");
//        testePlaca("ISR2076", "CT");
//        testePlaca("ISR2081", "CT");
    }

    @Test
    public void testeMapeamentoTodasAsPlacas() throws Exception {
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculo : veiculosAtivos.getVeiculo()) {
            if (veiculo.getQuantidadePneu() > 0) {
                testePlaca(veiculo.getPlaca(), veiculo.getTipo());
            }
        }
    }

    private void testePlaca(String placa, TipoVeiculoAtivo tipoVeiculo) throws Exception {
        final ArrayOfPneu pneus = requester.getPneusVeiculo(placa, CPF, DATA_NASCIMENTO);
        if (pneus.getPneu().size() <= 0)
            return;

        System.out.println("TESTE PARA PLACA: "+placa+" TIPO: "+tipoVeiculo.getNome()+"\n");
        PosicaoPneuMapper pneuMapper = new PosicaoPneuMapper(
                new AvaCorpAvilanDaoImpl().getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(tipoVeiculo.getCodigo()));

//        System.out.println(GsonUtils.getGson().toJson(pneus) + "\n\n");
        for (Pneu pneu : pneus.getPneu()) {
            final String posicaoAvilan = pneu.getPosicao();
            try {
                final int posicaoProlog = pneuMapper.mapToProLog(posicaoAvilan);
                System.out.println(posicaoAvilan+ " " +posicaoProlog);
            } catch (Exception e) {
                System.out.println(posicaoAvilan+ " Não possui mapeamente no Prolog");
            }
        }
        System.out.println("\n\n");
    }

    private int getplacasAtivasAssociadas(
            ArrayOfString placasVeiculoByTipo,
            ArrayOfVeiculo veiculosAtivos) {

        int contagem = 0;

        for (String placa : placasVeiculoByTipo.getString()) {
            for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculo : veiculosAtivos.getVeiculo()) {
                if (placa.equals(veiculo.getPlaca()))
                    contagem++;
            }
        }
        return contagem;
    }

    private void insertIntoPneuPosicaoAvilanProlog(
            String codTipoVeiculoAvilan,
            String posicaoAvilan,
            int posicaoProlog) {

            System.out.println("INSERT INTO AVILAN.PNEU_POSICAO_AVILAN_PROLOG(POSICAO_PNEU_AVILAN, POSICAO_PNEU_PROLOG, COD_VEICULO_TIPO) " +
                    "VALUES('"+posicaoAvilan+"', "+posicaoProlog+", '"+codTipoVeiculoAvilan+"');");

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