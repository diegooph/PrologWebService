package test.integracao.avilan;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConverter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoResposta;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import com.google.common.collect.MoreCollectors;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static test.integracao.avilan.AvaCorpAvilanConstants.*;

/**
 * Created by luiz on 04/08/17.
 */
public class AvaCorpAvilanConverterTest {
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
    public void Should_Fail_If_Vehicle_Transformation_Diverges() throws Exception {
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo veiculosAvilan =
                requester.getVeiculosAtivos(CPF, DATA_NASCIMENTO);
        assertNotNull(veiculosAvilan);
        assertTrue(!veiculosAvilan.getVeiculo().isEmpty());

        final List<br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo> veiculosProLog =
                AvaCorpAvilanConverter.convert(veiculosAvilan);
        assertNotNull(veiculosProLog);
        assertTrue(!veiculosProLog.isEmpty());

        assertTrue(veiculosAvilan.getVeiculo().size() == veiculosProLog.size());

        for (int i = 0; i < veiculosProLog.size(); i++) {
            final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculoAvilan =
                    veiculosAvilan.getVeiculo().get(i);
            final br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo veiculoProLog =
                    veiculosProLog.get(i);
            assertNotNull(veiculoAvilan);
            assertNotNull(veiculoProLog);

            assertTrue(veiculoAvilan.getPlaca().equals(veiculoProLog.getPlaca()));
            assertTrue(veiculoAvilan.getMarcador() == veiculoProLog.getKmAtual());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Tires_Transformation_Diverges() throws Exception {
        final ArrayOfPneu pneusAvilan = requester.getPneusVeiculo(VEICULO_COM_PNEUS, CPF, DATA_NASCIMENTO);
        assertNotNull(pneusAvilan);
        assertTrue(!pneusAvilan.getPneu().isEmpty());

        final List<Pneu> pneusProLog = AvaCorpAvilanConverter.convert(pneusAvilan);
        assertTrue(pneusAvilan.getPneu().size() == pneusProLog.size());

        for (int i = 0; i < pneusProLog.size(); i++) {
            final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Pneu pneuAvilan =
                    pneusAvilan.getPneu().get(i);
            final Pneu pneuProLog = pneusProLog.get(i);
            assertNotNull(pneuAvilan);
            assertNotNull(pneuProLog);

            assertTrue(pneuAvilan.getNumeroFogo().equals(pneuProLog.getCodigo()));
            assertTrue(pneuAvilan.getPosicao().equals(String.valueOf(pneuProLog.getPosicao())));
            assertTrue(pneuAvilan.getSulco1() == pneuProLog.getSulcosAtuais().getExterno());
            assertTrue(pneuAvilan.getSulco2() == pneuProLog.getSulcosAtuais().getCentralExterno());
            assertTrue(pneuAvilan.getSulco3() == pneuProLog.getSulcosAtuais().getCentralInterno());
            assertTrue(pneuAvilan.getSulco4() == pneuProLog.getSulcosAtuais().getInterno());
            assertTrue(pneuAvilan.getVidaPneu() == pneuProLog.getVidaAtual());
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Tire_Measurements_Transformation_Diverges() throws Exception {
        final Date now = new Date(System.currentTimeMillis());
        final long kmVeiculo = 42;
        final long tempoRealizacao = 5 * 60 * 1000;

        final Afericao afericao = new Afericao();
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(Long.parseLong(CPF));
        colaborador.setDataNascimento(parseDate(DATA_NASCIMENTO));
        afericao.setColaborador(colaborador);
        afericao.setDataHora(now);
        afericao.setKmMomentoAfericao(kmVeiculo);
        afericao.setTempoRealizacaoAfericaoInMillis(tempoRealizacao);

        final List<Pneu> pneus =
                AvaCorpAvilanConverter.convert(requester.getPneusVeiculo(VEICULO_COM_PNEUS, CPF, DATA_NASCIMENTO));
        assertNotNull(pneus);
        assertFalse(pneus.isEmpty());

        final br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo veiculoProLog = new br.com.zalf.prolog
                .webservice.frota.veiculo.model.Veiculo();
        veiculoProLog.setKmAtual(kmVeiculo);
        veiculoProLog.setPlaca(VEICULO_COM_PNEUS);
        veiculoProLog.setListPneus(pneus);
        afericao.setVeiculo(veiculoProLog);

        final IncluirMedida2 incluirMedida = AvaCorpAvilanConverter.convert(afericao);
        assertNotNull(incluirMedida);

        assertTrue(incluirMedida.getVeiculo().equals(veiculoProLog.getPlaca()));
        assertTrue(incluirMedida.getMarcador() == kmVeiculo);
        assertTrue(incluirMedida.getMarcador() == veiculoProLog.getKmAtual());
        assertTrue(incluirMedida.getTipoMarcador() == AvaCorpAvilanTipoMarcador.HODOMETRO);
        assertTrue(incluirMedida.getDataMedida().equals(AvaCorpAvilanUtils.createDatePattern(afericao.getDataHora())));
        assertNotNull(incluirMedida.getMedidas());

        final List<MedidaPneu> medidas = incluirMedida.getMedidas().getMedidaPneu();
        assertFalse(medidas.isEmpty());
        assertTrue(veiculoProLog.getListPneus().size() == medidas.size());
        for (int i = 0; i < medidas.size(); i++) {
            final MedidaPneu medidaPneu = medidas.get(i);
            final Pneu pneu = veiculoProLog.getListPneus().get(i);
            assertNotNull(medidaPneu);
            assertNotNull(pneu);
            assertTrue(medidaPneu.getNumeroFogoPneu().equals(pneu.getCodigo()));
            assertTrue(medidaPneu.getCalibragem() == pneu.getPressaoAtual());
            assertTrue(Double.valueOf(medidaPneu.getTriangulo1PrimeiroSulco()).equals(pneu.getSulcosAtuais().getExterno()));
            assertTrue(Double.valueOf(medidaPneu.getTriangulo1SegundoSulco()).equals(pneu.getSulcosAtuais().getCentralExterno()));
            assertTrue(Double.valueOf(medidaPneu.getTriangulo1TerceiroSulco()).equals(pneu.getSulcosAtuais().getCentralInterno()));
            assertTrue(Double.valueOf(medidaPneu.getTriangulo1QuartoSulco()).equals(pneu.getSulcosAtuais().getInterno()));
        }
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Quizzes_Transformation_Diverges() throws Exception {
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(CPF, DATA_NASCIMENTO);
        assertNotNull(questionarios);
        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());

        final Map<ModeloChecklist, List<String>> map = AvaCorpAvilanConverter.convert(questionarios);
        assertNotNull(map);
        assertTrue(!map.isEmpty());

        assertTrue(map.size() == questionarios.getQuestionarioVeiculos().size());

        map.forEach((modeloChecklist, placas) -> {
            assertNotNull(modeloChecklist);
            assertNotNull(placas);
            assertTrue(!placas.isEmpty());

            for (QuestionarioVeiculos questionarioVeiculos : questionarios.getQuestionarioVeiculos()) {
                final Questionario questionario = questionarioVeiculos.getQuestionario();
                assertNotNull(questionario);

                assertTrue(Long.valueOf(questionario.getCodigoQuestionario()).equals(modeloChecklist.getCodigo()));
                assertTrue(questionario.getDescricao().equals(modeloChecklist.getNome()));

                final List<Veiculo> veiculosAvilan = questionarioVeiculos.getVeiculos().getVeiculo();
                assertNotNull(veiculosAvilan);
                assertFalse(veiculosAvilan.isEmpty());

                assertTrue(placas.size() == veiculosAvilan.size());
                for (int i = 0; i < veiculosAvilan.size(); i++) {
                    final Veiculo veiculoAvilan = veiculosAvilan.get(i);
                    final String placaVeiculoProLog = placas.get(i);
                    assertNotNull(veiculoAvilan);
                    assertNotNull(placaVeiculoProLog);

                    assertTrue(veiculoAvilan.getPlaca().equals(placaVeiculoProLog));
                }
            }
        });
    }

    @Test(timeout = DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Questions_And_Answers_Transformation_Diverges() throws Exception {
        final String veiculoUtilizado = VEICULO_COM_CHECK_VINCULADO;
        final ArrayOfVeiculoQuestao veiculosQuestoes =
                requester.getQuestoesVeiculo(
                        1,
                        veiculoUtilizado,
                        CPF,
                        DATA_NASCIMENTO);
        assertNotNull(veiculosQuestoes);
        assertTrue(!veiculosQuestoes.getVeiculoQuestao().isEmpty());

        final VeiculoQuestao veiculoQuestao = veiculosQuestoes
                .getVeiculoQuestao()
                .stream()
                .filter(v -> v.getVeiculo().getPlaca().equals(veiculoUtilizado))
                .collect(MoreCollectors.onlyElement());
        assertNotNull(veiculoQuestao);
        assertNotNull(veiculoQuestao.getVeiculo());
        assertNotNull(veiculoQuestao.getQuestoes());

        final NovoChecklistHolder novoChecklistHolder = AvaCorpAvilanConverter.convert(veiculosQuestoes, veiculoUtilizado);
        assertNotNull(novoChecklistHolder);
        assertNotNull(novoChecklistHolder.getVeiculo());
        assertNotNull(novoChecklistHolder.getListPerguntas());
        assertFalse(novoChecklistHolder.getListPerguntas().isEmpty());

        final List<Questao> perguntasAvilan = veiculoQuestao.getQuestoes().getQuestao();
        final List<PerguntaRespostaChecklist> perguntasProLog = novoChecklistHolder.getListPerguntas();
        assertTrue(perguntasAvilan.size() == perguntasProLog.size());

        for (int i = 0; i < perguntasProLog.size(); i++) {
            final Questao perguntaAvilan = perguntasAvilan.get(i);
            final PerguntaRespostaChecklist perguntaProLog = perguntasProLog.get(i);
            assertNotNull(perguntaAvilan);
            assertNotNull(perguntaProLog);
            assertTrue(perguntaAvilan.getSequenciaQuestao() == perguntaProLog.getOrdemExibicao());
            assertTrue(perguntaAvilan.getDescricao().equals(perguntaProLog.getPergunta()));
            assertTrue(Long.valueOf(perguntaAvilan.getCodigoAvaliacao()).equals(perguntaProLog.getCodigo()));
            // No AvaCorp sempre são SingleChoice
            assertTrue(perguntaProLog.isSingleChoice());

            final List<AlternativaChecklist> alternativas = perguntaProLog.getAlternativasResposta();
            assertNotNull(alternativas);

            // Se for descritiva, não irá possuir opções de resposta e terá apenas uma alternativa no lado do ProLog:
            // Outros (especificar).
            if (perguntaAvilan.getTipoResposta() == AvaCorpAvilanTipoResposta.DESCRITIVA) {
                assertTrue(alternativas.size() == 1);

                final AlternativaChecklist alternativa = alternativas.get(0);
                assertNotNull(alternativa);
                assertTrue(alternativa.getTipo() == Alternativa.TIPO_OUTROS);
                assertNotNull(alternativa.getAlternativa());
                assertFalse(alternativa.getAlternativa().isEmpty());
            } else { // Seleção única
                final List<Resposta> respostas = perguntaAvilan.getRespostas().getResposta();
                assertFalse(respostas.isEmpty());

                assertTrue(alternativas.size() == respostas.size());
                for (int j = 0; j < alternativas.size(); j++) {
                    final AlternativaChecklist alternativa = alternativas.get(j);
                    final Resposta resposta = respostas.get(j);
                    assertNotNull(alternativa);
                    assertNotNull(resposta);

                    assertTrue(resposta.getDescricao().equals(alternativa.getAlternativa()));
                    assertTrue(Long.valueOf(resposta.getCodigoResposta()).equals(alternativa.getCodigo()));
                }
            }
        }
    }

//    @Test(timeout = 7 * 60 * 1000)
//    public void testEnviarChecklist() throws Exception {
//        //////////////////////////////////////////////////////////////////////////////
//        // BUSCA OS QUESTIONÁRIOS DISPONÍVEIS PARA UM VEÍCULO
//        //////////////////////////////////////////////////////////////////////////////
//        final ArrayOfQuestionarioVeiculos questionarios =
//                requester.getSelecaoModeloChecklistPlacaVeiculo(CPF, DATA_NASCIMENTO);
//        assertNotNull(questionarios);
//        assertTrue(!questionarios.getQuestionarioVeiculos().isEmpty());
//        final Questionario questionario = questionarios.getQuestionarioVeiculos().get(0).getQuestionario();
//        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculo veiculos =
//                questionarios.getQuestionarioVeiculos().get(0).getVeiculos();
//        assertNotNull(questionario);
//        assertNotNull(veiculos);
//        assertTrue(!veiculos.getVeiculo().isEmpty());
//
//        //////////////////////////////////////////////////////////////////////////////
//        // BUSCA AS QUESTÕES DE UM QUESTIONÁRIO PARA UM VEÍCULO
//        //////////////////////////////////////////////////////////////////////////////
//        final Veiculo veiculo = veiculos.getVeiculo().get(0);
//        assertNotNull(veiculo);
//        assertNotNull(veiculo.getPlaca());
//        assertTrue(veiculo.getMarcador() >= 0);
//        final ArrayOfVeiculoQuestao arrayOfVeiculoQuestao = requester.getQuestoesVeiculo(
//                questionario.getCodigoQuestionario(),
//                veiculo.getPlaca(),
//                CPF,
//                DATA_NASCIMENTO);
//        assertNotNull(arrayOfVeiculoQuestao);
//        assertTrue(!arrayOfVeiculoQuestao.getVeiculoQuestao().isEmpty());
//
//        //////////////////////////////////////////////////////////////////////////////
//        // RESPONDE TODAS AS PERGUNTAS DO QUESTIONÁRIO SELECIONADO E ENVIA
//        //////////////////////////////////////////////////////////////////////////////
//        int codigoAvaliacao = -1;
//        final ArrayOfRespostaAval arrayOfRespostaAval = new ArrayOfRespostaAval();
//        for (VeiculoQuestao veiculoQuestao : arrayOfVeiculoQuestao.getVeiculoQuestao()) {
//            assertNotNull(veiculoQuestao);
//            assertNotNull(veiculoQuestao.getQuestoes());
//
//            final List<Questao> questoes = veiculoQuestao.getQuestoes().getQuestao();
//            assertNotNull(questoes);
//            for (Questao questao : questoes) {
//                assertNotNull(questao);
//                codigoAvaliacao = questao.getCodigoAvaliacao();
//
//                final ArrayOfResposta respostas = questao.getRespostas();
//                assertNotNull(respostas);
//                assertTrue(!respostas.getResposta().isEmpty());
//
//                final RespostaAval respostaAval = new RespostaAval();
//                // Responde sempre a primeira alternativa
//                respostaAval.setCodigoResposta(respostas.getResposta().get(0).getCodigoResposta());
//                respostaAval.setSequenciaQuestao(questao.getSequenciaQuestao());
//                arrayOfRespostaAval.getRespostaAval().add(respostaAval);
//            }
//        }
//
//        assertTrue(codigoAvaliacao != -1);
//        final RespostasAvaliacao respostasAvaliacao = new RespostasAvaliacao();
//        respostasAvaliacao.setCodigoAvaliacao(codigoAvaliacao);
//        respostasAvaliacao.setOdometro(veiculo.getMarcador());
//        respostasAvaliacao.setDtNascimento(DATA_NASCIMENTO);
//        respostasAvaliacao.setCpf(CPF);
//        respostasAvaliacao.setRespostas(arrayOfRespostaAval);
//        assertTrue(requester.insertChecklist(respostasAvaliacao, CPF, DATA_NASCIMENTO));
//    }

    private Date parseDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }
}