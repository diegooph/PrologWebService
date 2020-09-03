package test.br.com.zalf.prolog.webservice.integracao.avilan;

import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data.AvaCorpAvilanDaoImpl;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data.AvaCorpAvilanSincronizadorTiposVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data.TipoVeiculoAvilanProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvaCorpAvilanRequesterImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static test.br.com.zalf.prolog.webservice.integracao.avilan.AvaCorpAvilanConstants.*;

/**
 * Created by luiz on 02/08/17.
 */
public class AvaCorpAvilanRequesterTest {
    private final AvaCorpAvilanRequester requester = new AvaCorpAvilanRequesterImpl();
    private final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

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
        assertNotNull(requester.insertAfericao(createIncluirMedida(), CPF, DATA_NASCIMENTO));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarAfericao() throws Exception {
        final AfericaoFiltro afericaoFiltro = requester.getAfericaoByCodigo(
                328,
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(afericaoFiltro);
        assertNotNull(afericaoFiltro.getPneus());
        assertTrue(!afericaoFiltro.getPneus().getPneuFiltro().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarAfericoes() throws Exception {
        final ArrayOfAfericaoFiltro afericoes = requester.getAfericoes(
                8,
                1,
                "",
                "",
                "2017-09-25",
                "2017-10-25",
                1,
                0,
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
    public void testBuscarVeiculoAtivo() throws Exception {
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo veiculo =
                requester.getVeiculoAtivo("LRN9162", CPF, DATA_NASCIMENTO);
        assertNotNull(veiculo);
        System.out.println(GsonUtils.getGson().toJson(veiculo));
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
                45354,
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(checklistFiltro);
        assertNotNull(checklistFiltro.getAvaliacao());
        System.out.println(PRETTY_GSON.toJson(checklistFiltro));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarChecklists() throws Exception {
        final ArrayOfChecklistFiltro checklistFiltro = requester.getChecklists(
                8,
                1,
                "",
                "",
                "2018-01-31",
                "2018-01-31",
                CPF,
                DATA_NASCIMENTO);
        assertNotNull(checklistFiltro);
        assertTrue(!checklistFiltro.getChecklistFiltro().isEmpty());
        System.out.println(PRETTY_GSON.toJson(checklistFiltro));
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarChecklistsPorColaborador() throws Exception {
        final String dataInicial = "2018-01-01";
        final String dataFinal = "2018-01-31";

        final ArrayOfChecklistFiltro checklists = requester.getChecklistsByColaborador(
                8,
                1,
                "",
                "",
                dataInicial,
                dataFinal,
                "82511357020",
                "1985-05-25");

        checklists.getChecklistFiltro().forEach(
                checklistFiltro -> assertEquals(checklistFiltro.getColaborador().getCpf(), "82511357020"));

        System.out.println(GsonUtils.getGson().toJson(checklists));
        assertNotNull(checklists);
        assertTrue(!checklists.getChecklistFiltro().isEmpty());
        System.out.println(checklists.getChecklistFiltro().size());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarTodosChecklists() throws Exception {
        final String dataInicial = "2017-12-01";
        final String dataFinal = "2017-12-23";

        final ArrayOfChecklistFiltro checklists = requester.getChecklists(
                8,
                1,
                "",
                "",
                dataInicial,
                dataFinal,
                CPF,
                DATA_NASCIMENTO);
        System.out.println(GsonUtils.getGson().toJson(checklists));
        assertNotNull(checklists);
        assertTrue(!checklists.getChecklistFiltro().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPneusVeiculo() throws Exception {
        final ArrayOfPneu pneus = requester.getPneusVeiculo("LRN9162", CPF, DATA_NASCIMENTO);
        assertNotNull(pneus);
        assertTrue(!pneus.getPneu().isEmpty());
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarQuestionariosColaborador() throws Exception {
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(CPF, DATA_NASCIMENTO);
        assertNotNull(questionarios);
        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
        assertNotNull(questionarios.getQuestionarioVeiculos().get(0).getQuestionario());
        assertNotNull(questionarios.getQuestionarioVeiculos().get(0).getVeiculos());
        System.out.println(questionarios);
        List<QuestionarioVeiculos> questionarios2 = questionarios.getQuestionarioVeiculos();
        for(QuestionarioVeiculos questionadio : questionarios2) {
            System.out.println(questionadio.getQuestionario().getCodigoQuestionario()
                    + ";" + questionadio.getQuestionario().getDescricao()
                    + questionadio.getVeiculos().getVeiculo().get(0).getPlaca());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testColaboradorPodeFazerChecklist() throws Exception {
        final Long codUnidadeSapucaia = 2L;
        final Long codUnidadeSantaMaria = 3L;
        final Long codUnidadeSantaCruz = 4L;
        final Long codMotoristaRota = 22L;
        final ColaboradorService service = new ColaboradorService();

        // Sapucaia.
        System.out.println("Colaboradores Sapucaia:");
        final List<Colaborador> colaboradoresSapucaia = service
                .getAllByUnidade(codUnidadeSapucaia, true)
                .stream()
                .filter(colaborador -> colaborador.getFuncao().getCodigo().equals(codMotoristaRota))
                .collect(Collectors.toList());
        verificaColaboradores401(colaboradoresSapucaia);

        // Santa Maria.
        System.out.println("\nColaboradores Santa Maria:");
        final List<Colaborador> colaboradoresSantaMaria = service
                .getAllByUnidade(codUnidadeSantaMaria, true)
                .stream()
                .filter(colaborador -> colaborador.getFuncao().getCodigo().equals(codMotoristaRota))
                .collect(Collectors.toList());
        verificaColaboradores401(colaboradoresSantaMaria);

        // Santa Cruz.
        System.out.println("\nColaboradores Santa Cruz:");
        final List<Colaborador> colaboradoresSantaCruz = service
                .getAllByUnidade(codUnidadeSantaCruz, true)
                .stream()
                .filter(colaborador -> colaborador.getFuncao().getCodigo().equals(codMotoristaRota))
                .collect(Collectors.toList());
        verificaColaboradores401(colaboradoresSantaCruz);

    }

    private void verificaColaboradores401(List<Colaborador> colaboradores) {
        for (final Colaborador colaborador : colaboradores) {
            try {

                final ArrayOfQuestionarioVeiculos questionarios = requester.getSelecaoModeloChecklistPlacaVeiculo(
                        colaborador.getCpfAsString(),
                        AvaCorpAvilanUtils.createDatePattern(colaborador.getDataNascimento()));
                assertNotNull(questionarios);
                assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
                assertNotNull(questionarios.getQuestionarioVeiculos().get(0).getQuestionario());
                assertNotNull(questionarios.getQuestionarioVeiculos().get(0).getVeiculos());
            } catch (Throwable e) {
                System.out.println("******* NOME: " + colaborador.getNome() + " -- CPF: " + colaborador.getCpfAsString()
                        + " -- DATA: " + colaborador.getDataNascimento().toString());
            }
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void testBuscarPerguntasAlternativasQuestionario() throws Exception {
        final ArrayOfVeiculoQuestao veiculoQuestao =
                requester.getQuestoesVeiculo(
                        999,
                        "EMP2254",
                        AvacorpAvilanTipoChecklist.SAIDA,
                        CPF,
                        DATA_NASCIMENTO);
        assertNotNull(veiculoQuestao);
        assertTrue(!veiculoQuestao.getVeiculoQuestao().isEmpty());
//        System.out.println(veiculoQuestao);
        List<Questao> questoes = veiculoQuestao.getVeiculoQuestao().get(0).getQuestoes().getQuestao();
        for(Questao questao : questoes){
            System.out.println(questao.getSequenciaQuestao() + ";" + questao.getDescricao());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void buscarFarolChecklist() throws Exception {
        final ArrayOfFarolDia farolDia =
                requester.getFarolChecklist(
                        8,
                        1,
                        "2017-12-13",
                        "2017-12-13",
                        false,
                        CPF,
                        DATA_NASCIMENTO);
        assertNotNull(farolDia);
        assertTrue(!farolDia.getFarolDia().isEmpty());
        System.out.println(PRETTY_GSON.toJson(farolDia));
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
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.ArrayOfVeiculo veiculos =
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
    public void testeMapeamentoPlacasEspecificas() throws Exception {
        final TipoVeiculoAvilan tipoVeiculo = new TipoVeiculoAvilan();
        tipoVeiculo.setCodigo("CT");
        tipoVeiculo.setNome("Caminhão Truck");

        testePlaca("ISR2076", tipoVeiculo);
        testePlaca("ISR2081", tipoVeiculo);
    }

    @Test
    public void testeMapeamentoTodasAsPlacas() throws Exception {
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo veiculo : veiculosAtivos.getVeiculo()) {
            if (veiculo.getQuantidadePneu() > 0) {
                testePlaca(veiculo.getPlaca(), veiculo.getTipo());
            }
        }
    }

    @Test
    public void testeBuscarTodasAsPlacasPorUnidade() throws Exception {
        final Long codTruck = 5L;
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        final String codTipoAvilan = new AvaCorpAvilanDaoImpl().getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(codTruck);
        final List<String> placas = new ArrayList<>();

        veiculosAtivos.getVeiculo().forEach(veiculo -> {
            if (veiculo.getTipo().getCodigo().equals(codTipoAvilan)) {
                System.out.println("Veiculo:" +veiculo.getPlaca()+ " Tipo: "+veiculo.getTipo().getCodigo());
                placas.add(veiculo.getPlaca());
            }
        });
        System.out.println("Placas ativas: "+veiculosAtivos.getVeiculo().size());
        System.out.println("Placas by tipo size: "+placas.size());
        assertNotEquals(null, placas);
        assertTrue(!placas.isEmpty());
    }

    @Test
    public void testeBuscarTiposVeiculosComPlacasAssociadas() throws Exception {
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        final List<TipoVeiculoAvilan> tiposVeiculosAvilan = new ArrayList<>();

        veiculosAtivos.getVeiculo().forEach(veiculo -> {
            if (!tiposVeiculosAvilan.contains(veiculo.getTipo())) {
                tiposVeiculosAvilan.add(veiculo.getTipo());
            }
        });

        System.out.println("Veiculos Ativos da Avilan: "+veiculosAtivos.getVeiculo().size());
        System.out.println("Tipos Veiculos Ativos da Avilan: "+tiposVeiculosAvilan.size());

        // Sincroniza os tipos buscados com o nosso banco de dados.
        final List<TipoVeiculoAvilanProLog> tiposVeiculosAvilanProLog =
                new AvaCorpAvilanSincronizadorTiposVeiculos(new AvaCorpAvilanDaoImpl()).sync(tiposVeiculosAvilan);

        System.out.println("Tipos Veiculos Prolog sem filtro: "+tiposVeiculosAvilanProLog.size());

        final List<TipoVeiculoAvilanProLog> tipoFiltrado = new ArrayList<>();

        for (TipoVeiculoAvilanProLog tipoVeiculoAvilanProLog : tiposVeiculosAvilanProLog) {
            for (TipoVeiculoAvilan tipoVeiculoAvilan : tiposVeiculosAvilan) {
                if (tipoVeiculoAvilan.getCodigo().equals(tipoVeiculoAvilanProLog.getCodigoAvilan())) {
                    tipoFiltrado.add(tipoVeiculoAvilanProLog);
                }
            }
        }

        System.out.println("Tipos Veiculos Prolog com filtro: "+tipoFiltrado.size());
    }

    private void testePlaca(String placa, TipoVeiculoAvilan tipoVeiculo) throws Exception {
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
            for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo veiculo : veiculosAtivos.getVeiculo()) {
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
        incluirMedida2.setDataMedida("yyyy-MM-dd");
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