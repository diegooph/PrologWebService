package test.br.com.zalf.prolog.webservice.integracao.avilan;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConverter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanTipoMarcador;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvacorpAvilanTipoChecklist;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanDao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanDaoImpl;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequesterImpl;
import com.google.common.collect.MoreCollectors;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

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
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Vehicle_Transformation_Diverges() throws Exception {
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo veiculosAvilan =
                requester.getVeiculosAtivos(AvaCorpAvilanConstants.CPF, AvaCorpAvilanConstants.DATA_NASCIMENTO);
        assertNotNull(veiculosAvilan);
        assertTrue(!veiculosAvilan.getVeiculo().isEmpty());

        final List<br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo> veiculosProLog =
                AvaCorpAvilanConverter.convert(veiculosAvilan, 4L);
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

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Tires_Transformation_Diverges() throws Exception {
        final ArrayOfPneu pneusAvilan = requester.getPneusVeiculo(AvaCorpAvilanConstants.VEICULO_COM_PNEUS, AvaCorpAvilanConstants.CPF, AvaCorpAvilanConstants.DATA_NASCIMENTO);
        assertNotNull(pneusAvilan);
        assertTrue(!pneusAvilan.getPneu().isEmpty());

        final AvaCorpAvilanDao dao = new AvaCorpAvilanDaoImpl();
        final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(dao
                .getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan("COD_TIPO_AQUI"));
        final List<Pneu> pneusProLog = AvaCorpAvilanConverter.convert(
                posicaoPneuMapper,
                pneusAvilan);
        assertTrue(pneusAvilan.getPneu().size() == pneusProLog.size());

        for (int i = 0; i < pneusProLog.size(); i++) {
            final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Pneu pneuAvilan =
                    pneusAvilan.getPneu().get(i);
            final Pneu pneuProLog = pneusProLog.get(i);
            assertNotNull(pneuAvilan);
            assertNotNull(pneuProLog);

            assertTrue(pneuAvilan.getNumeroFogo().equals(pneuProLog.getCodigo()));
            assertTrue(pneuAvilan.getSulco1() == pneuProLog.getSulcosAtuais().getExterno());
            assertTrue(pneuAvilan.getSulco2() == pneuProLog.getSulcosAtuais().getCentralExterno());
            assertTrue(pneuAvilan.getSulco3() == pneuProLog.getSulcosAtuais().getCentralInterno());
            assertTrue(pneuAvilan.getSulco4() == pneuProLog.getSulcosAtuais().getInterno());
            assertTrue(pneuAvilan.getVidaPneu() + 1 == pneuProLog.getVidaAtual());
        }
    }

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Tire_Measurements_Transformation_Diverges() throws Exception {
        final long kmVeiculo = 42;
        final long tempoRealizacaoMillis = TimeUnit.MINUTES.toMillis(5);

        final AfericaoPlaca afericaoPlaca = new AfericaoPlaca();
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(Long.parseLong(AvaCorpAvilanConstants.CPF));
        colaborador.setDataNascimento(parseDate(AvaCorpAvilanConstants.DATA_NASCIMENTO));
        afericaoPlaca.setColaborador(colaborador);
        afericaoPlaca.setDataHora(LocalDateTime.now(Clock.systemUTC()));
        afericaoPlaca.setKmMomentoAfericao(kmVeiculo);
        afericaoPlaca.setTempoRealizacaoAfericaoInMillis(tempoRealizacaoMillis);

        final AvaCorpAvilanDao dao = new AvaCorpAvilanDaoImpl();
        final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(dao
                .getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan("COD_TIPO_AQUI"));
        final List<Pneu> pneus =
                AvaCorpAvilanConverter.convert(
                        posicaoPneuMapper,
                        requester.getPneusVeiculo(AvaCorpAvilanConstants.VEICULO_COM_PNEUS, AvaCorpAvilanConstants.CPF, AvaCorpAvilanConstants.DATA_NASCIMENTO));
        assertNotNull(pneus);
        assertFalse(pneus.isEmpty());

        final br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo veiculoProLog = new br.com.zalf.prolog
                .webservice.frota.veiculo.model.Veiculo();
        veiculoProLog.setKmAtual(kmVeiculo);
        veiculoProLog.setPlaca(AvaCorpAvilanConstants.VEICULO_COM_PNEUS);
        veiculoProLog.setListPneus(pneus);
        afericaoPlaca.setVeiculo(veiculoProLog);

        final IncluirMedida2 incluirMedida = AvaCorpAvilanConverter.convert(afericaoPlaca);
        assertNotNull(incluirMedida);

        assertTrue(incluirMedida.getVeiculo().equals(veiculoProLog.getPlaca()));
        assertTrue(incluirMedida.getMarcador() == kmVeiculo);
        assertTrue(incluirMedida.getMarcador() == veiculoProLog.getKmAtual());
        assertTrue(incluirMedida.getTipoMarcador() == AvaCorpAvilanTipoMarcador.HODOMETRO);
//        assertTrue(incluirMedida.getDataMedida().equals(AvaCorpAvilanUtils.createDatePattern(afericao.getDataHora())));
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

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Quizzes_Transformation_Diverges() throws Exception {
        final ArrayOfQuestionarioVeiculos questionarios =
                requester.getSelecaoModeloChecklistPlacaVeiculo(AvaCorpAvilanConstants.CPF, AvaCorpAvilanConstants.DATA_NASCIMENTO);
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

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Questions_And_Answers_Transformation_Diverges() throws Exception {
        final long codQuestionario = 1L;
        final String veiculoUtilizado = AvaCorpAvilanConstants.VEICULO_COM_CHECK_VINCULADO;
        final ArrayOfVeiculoQuestao veiculosQuestoes =
                requester.getQuestoesVeiculo(
                        Math.toIntExact(codQuestionario),
                        veiculoUtilizado,
                        AvacorpAvilanTipoChecklist.SAIDA,
                        AvaCorpAvilanConstants.CPF,
                        AvaCorpAvilanConstants.DATA_NASCIMENTO);
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

        final Map<Long, String> mapCodPerguntUrlImagem = new AvaCorpAvilanDaoImpl().getMapeamentoCodPerguntaUrlImagem(codQuestionario);

        final NovoChecklistHolder novoChecklistHolder = AvaCorpAvilanConverter.convert(
                veiculosQuestoes,
                5L,
                mapCodPerguntUrlImagem,
                veiculoUtilizado);
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
            assertTrue(Long.valueOf(perguntaAvilan.getCodigoAvaliacao()).equals(novoChecklistHolder.getCodigoModeloChecklist()));
            assertTrue(perguntaAvilan.getDescricao().equals(perguntaProLog.getPergunta()));
            assertTrue(Long.valueOf(perguntaAvilan.getSequenciaQuestao()).equals(perguntaProLog.getCodigo()));
            // No AvaCorp sempre são SingleChoice
            assertTrue(perguntaProLog.isSingleChoice());

            final List<AlternativaChecklist> alternativas = perguntaProLog.getAlternativasResposta();
            assertNotNull(alternativas);
            // Sepre vai possuir apenas uma alternativa
            assertTrue(alternativas.size() == 1);

            final AlternativaChecklist alternativa = alternativas.get(0);
            assertNotNull(alternativa);
            assertTrue(alternativa.getTipo() == Alternativa.TIPO_OUTROS);

            // TODO: Verificar se as duas respostas da Avilan são OK e NOK
        }
    }

    @Test(timeout = AvaCorpAvilanConstants.DEFAULT_TIMEOUT_MILLIS)
    public void Should_Fail_If_Checklist_Transformation_Diverges() throws Exception {
        // Uso interno
        final int codigoQuestionarioModelo = 1;
        final LocalDateTime now = LocalDateTime.now();
        final long tempoRealizacaoMillis = TimeUnit.MINUTES.toMillis(2);
        final long kmVeiculo = 42;
        final String veiculoUtilizado = AvaCorpAvilanConstants.VEICULO_COM_CHECK_VINCULADO;

        final ArrayOfVeiculoQuestao arrayOfVeiculoQuestao = requester.getQuestoesVeiculo(
                codigoQuestionarioModelo,
                veiculoUtilizado,
                AvacorpAvilanTipoChecklist.SAIDA,
                AvaCorpAvilanConstants.CPF,
                AvaCorpAvilanConstants.DATA_NASCIMENTO);
        assertNotNull(arrayOfVeiculoQuestao);
        assertFalse(arrayOfVeiculoQuestao.getVeiculoQuestao().isEmpty());
        final Map<Long, String> mapCodPerguntUrlImagem = new AvaCorpAvilanDaoImpl().getMapeamentoCodPerguntaUrlImagem((long) codigoQuestionarioModelo);
        final NovoChecklistHolder holder = AvaCorpAvilanConverter.convert(
                arrayOfVeiculoQuestao,
                5L,
                mapCodPerguntUrlImagem,
                veiculoUtilizado);
        assertNotNull(holder);
        assertNotNull(holder.getVeiculo());
        assertNotNull(holder.getListPerguntas());
        assertNotNull(holder.getCodigoModeloChecklist());
        assertTrue(holder.getCodigoModeloChecklist() > 0);
        assertFalse(holder.getListPerguntas().isEmpty());

        final Checklist checklist = new Checklist();
        checklist.setCodModelo(holder.getCodigoModeloChecklist());
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(Long.parseLong(AvaCorpAvilanConstants.CPF));
        colaborador.setDataNascimento(parseDate(AvaCorpAvilanConstants.DATA_NASCIMENTO));
        checklist.setColaborador(colaborador);
        checklist.setData(now);
        checklist.setTempoRealizacaoCheckInMillis(tempoRealizacaoMillis);
        checklist.setKmAtualVeiculo(kmVeiculo);
        checklist.setPlacaVeiculo(veiculoUtilizado);
        assertEquals(holder.getCodigoModeloChecklist(), checklist.getCodModelo());
        // Não precisamos setar o tipo pois o ERP da Avilan não lida com essa informação. Ele seta automaticamente
        // o tipo do checklist
//        checklist.setTipo();

        // Responde as perguntas
        for (final PerguntaRespostaChecklist respostas : holder.getListPerguntas()) {
            final AlternativaChecklist alternativa = respostas.getAlternativasResposta().get(0);
            alternativa.selected = true;
            if (alternativa.getTipo() == Alternativa.TIPO_OUTROS) {
                alternativa.setRespostaOutros("TESTE RESPOSTA OUTROS");
            }
        }
        checklist.setListRespostas(holder.getListPerguntas());

        final RespostasAvaliacao respostasAvaliacao = AvaCorpAvilanConverter.convert(checklist, AvaCorpAvilanConstants.CPF, AvaCorpAvilanConstants.DATA_NASCIMENTO);
        assertNotNull(respostasAvaliacao);
        assertNotNull(respostasAvaliacao.getCpf());
        assertNotNull(respostasAvaliacao.getDtNascimento());
        assertNotNull(respostasAvaliacao.getRespostas());
        assertFalse(respostasAvaliacao.getRespostas().getRespostaAval().isEmpty());
        assertTrue(Long.valueOf(respostasAvaliacao.getCpf()).equals(checklist.getColaborador().getCpf()));
//        assertTrue(respostasAvaliacao.getDtNascimento().equals(
//                AvaCorpAvilanUtils.createDatePattern(checklist.getColaborador().getDataNascimento())));
        assertTrue(respostasAvaliacao.getOdometro() == kmVeiculo);
        assertTrue(respostasAvaliacao.getOdometro() == checklist.getKmAtualVeiculo());


        final List<RespostaAval> respostasAvilan = respostasAvaliacao.getRespostas().getRespostaAval();
        final List<PerguntaRespostaChecklist> respostasProLog = checklist.getListRespostas();
        assertTrue(respostasAvilan.size() == respostasProLog.size());

        for (int i = 0; i < respostasAvilan.size(); i++) {
            final RespostaAval respostaAvilan = respostasAvilan.get(i);
            final PerguntaRespostaChecklist respostaProLog = respostasProLog.get(i);
            assertNotNull(respostaAvilan);
            assertNotNull(respostaProLog);
            assertTrue(respostaAvilan.getSequenciaQuestao() == respostaProLog.getCodigo());

            final List<AlternativaChecklist> alternativas = respostaProLog.getAlternativasResposta();
            assertNotNull(alternativas);
            assertTrue(alternativas.size() == 1);
            final Alternativa alternativa = alternativas.get(0);
            assertNotNull(alternativa);
            if (respostaProLog.respondeuOk()) {
                assertTrue(alternativa.getOrdemExibicao() == respostaAvilan.getCodigoResposta());
            } else {
                assertTrue(alternativa.getCodigo() == respostaAvilan.getCodigoResposta());
                assertTrue(alternativa.getTipo() == Alternativa.TIPO_OUTROS);
                assertNotNull(alternativa.getRespostaOutros());
                assertNotNull(respostaAvilan.getObservacao());
                assertTrue(alternativa.getRespostaOutros().equals(respostaAvilan.getObservacao()));
            }
        }
    }

    private Date parseDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }
}