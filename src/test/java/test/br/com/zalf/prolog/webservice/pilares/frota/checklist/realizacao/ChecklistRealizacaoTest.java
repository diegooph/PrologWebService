package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.realizacao;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a realização (responder) um checklist, tanto pelo fluxo offline quanto pelo online.
 *
 * Essa classe não testa o processemento das alternativas para abertura (ou não) de OSs. O foco é verificar se os dados
 * respondidos são salvos como deveriam.
 *
 * Created on 2019-10-14
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ChecklistRealizacaoTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
    // Token da unidade 5.
    private static final String TOKEN_CHECK_OFF = "token_teste_unidade_5";
    private ChecklistModeloService service;
    private String token;

    @Override
    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    //region Chamadas dos testes
    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insereChecklistOnline_buscaParaComparar_deveTerInfosIguais(final boolean comVersaoModeloSetada) {
        //region Insere modelo de checklist
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        {
            // P2.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final String nomeModelo = UUID.randomUUID().toString();
        // 4 - Então inserimos o modelo.
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        /* Agora buscamos o modelo inserido.*/
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());
        //endregion

        //region Insere a realização do checklist.
        final List<ChecklistResposta> respostas = new ArrayList<>();

        {
            // Responde a P1 - ela É single_choice.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // A1.
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    true,
                    false,
                    null));

            // A2.
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    false,
                    true,
                    null));

            respostas.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        {
            // Responde a P2 - ela NÃO É single_choice.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // B1.
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    true,
                    false,
                    null));

            // B2.
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostas.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }

        final Long codVersaoModeloChecklistInserido = comVersaoModeloSetada
                ? result.getCodVersaoModeloChecklistInserido()
                : null;

        final ChecklistInsercao insercao = new ChecklistInsercao(
                5L,
                result.getCodModeloChecklistInserido(),
                codVersaoModeloChecklistInserido,
                2272L,
                3195L,
                "PRO0001",
                TipoChecklist.SAIDA,
                112,
                "uma observacao",
                10000,
                respostas,
                PrologDateParser.toLocalDateTime("2019-10-14T09:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                80,
                83,
                "device didID",
                "deviceImei",
                10000,
                11000,
                0,
                0);

        final ChecklistService checklistService = new ChecklistService();
        final Long codChecklistInserido = checklistService.insert(token, insercao);
        //endregion

        //region Realiza os testes de comparação
        {
            // Compara as propriedades do checklist inserido com o buscado.
            final Checklist checklist = checklistService.getByCod(codChecklistInserido, token);

            // A data/hora de realização não é comparada pois para um checklist online, o WS ignora a data/hora do
            // objeto e pega a atual.
            assertThat(checklist).isNotNull();
            assertThat(checklist.getCodigo()).isEqualTo(codChecklistInserido);
            assertThat(checklist.getCodModelo()).isEqualTo(result.getCodModeloChecklistInserido());
            assertThat(checklist.getCodVersaoModeloChecklist()).isEqualTo(result.getCodVersaoModeloChecklistInserido());
            assertThat(checklist.getColaborador().getCpf()).isEqualTo(Long.parseLong(CPF_TOKEN));
            assertThat(checklist.getPlacaVeiculo()).isEqualTo("PRO0001");
            assertThat(checklist.getTipo()).isEqualTo(TipoChecklist.SAIDA.asChar());
            assertThat(checklist.getKmAtualVeiculo()).isEqualTo(112);
            assertThat(checklist.getTempoRealizacaoCheckInMillis()).isEqualTo(10000);
            assertThat(checklist.getQtdItensOk()).isEqualTo(0);
            assertThat(checklist.getQtdItensNok()).isEqualTo(2);
            assertThat(checklist.getQtdAlternativasOk()).isEqualTo(1);
            assertThat(checklist.getQtdAlternativasNok()).isEqualTo(3);
            {
                // Compara a P1.
                final PerguntaRespostaChecklist p1 = checklist.getListRespostas().get(0);

                // A1.
                final AlternativaChecklist a1 = p1.getAlternativasResposta().get(0);
                assertThat(a1.getAlternativa()).isEqualTo("A1");
                assertThat(a1.getRespostaOutros()).isNull();
                assertThat(a1.isTipoOutros()).isFalse();
                assertThat(a1.isSelected()).isTrue();
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);

                // A2.
                final AlternativaChecklist a2 = p1.getAlternativasResposta().get(1);
                assertThat(a2.getAlternativa()).isEqualTo("Outros");
                assertThat(a2.getRespostaOutros()).isNull();
                assertThat(a2.isTipoOutros()).isTrue();
                assertThat(a2.isSelected()).isFalse();
                assertThat(a2.getOrdemExibicao()).isEqualTo(2);
            }
            {
                // Compara a P2.
                final PerguntaRespostaChecklist p2 = checklist.getListRespostas().get(1);

                // B1.
                final AlternativaChecklist b1 = p2.getAlternativasResposta().get(0);
                assertThat(b1.getAlternativa()).isEqualTo("B1");
                assertThat(b1.getRespostaOutros()).isNull();
                assertThat(b1.isTipoOutros()).isFalse();
                assertThat(b1.isSelected()).isTrue();
                assertThat(b1.getOrdemExibicao()).isEqualTo(1);

                // B2.
                final AlternativaChecklist b2 = p2.getAlternativasResposta().get(1);
                assertThat(b2.getAlternativa()).isEqualTo("Outros");
                assertThat(b2.getRespostaOutros()).isEqualTo("Está com problema...");
                assertThat(b2.isTipoOutros()).isTrue();
                assertThat(b2.isSelected()).isTrue();
                assertThat(b2.getOrdemExibicao()).isEqualTo(2);
            }
        }
        //endregion
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insereChecklistOffline_buscaParaComparar_deveTerInfosIguais(final boolean comVersaoModeloSetada) {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        {
            // P2.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final String nomeModelo = UUID.randomUUID().toString();
        // 4 - Então inserimos o modelo.
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        // 5 - Agora buscamos o modelo inserido.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());


        // Responde o checklist.
        final List<ChecklistResposta> respostas = new ArrayList<>();

        {
            // Responde a P1 - ela É single_choice.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // A1.
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    true,
                    false,
                    null));

            // A2.
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    false,
                    true,
                    null));

            respostas.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        {
            // Responde a P2 - ela NÃO É single_choice.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // B1.
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    true,
                    false,
                    null));

            // B2.
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostas.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }

        final Long codVersaoModeloChecklistInserido = comVersaoModeloSetada
                ? result.getCodVersaoModeloChecklistInserido()
                : null;

        final ChecklistInsercao insercao = new ChecklistInsercao(
                5L,
                result.getCodModeloChecklistInserido(),
                codVersaoModeloChecklistInserido,
                2272L,
                3195L,
                "PRO0001",
                TipoChecklist.SAIDA,
                112,
                "uma observacao",
                10000,
                respostas,
                PrologDateParser.toLocalDateTime("2019-10-14T09:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                80,
                83,
                "device didID",
                "deviceImei",
                10000,
                11000,
                0,
                0);

        final ChecklistOfflineService checklistOfflineService = new ChecklistOfflineService();
        final Long codChecklistInserido = checklistOfflineService.insertChecklistOffline(
                TOKEN_CHECK_OFF,
                insercao).getCodigo();

        final ChecklistService checklistService = new ChecklistService();
        {
            // Compara as propriedades do checklist inserido com o buscado.
            final Checklist checklist = checklistService.getByCod(codChecklistInserido, token);

            assertThat(checklist).isNotNull();
            assertThat(checklist.getCodigo()).isEqualTo(codChecklistInserido);
            assertThat(checklist.getCodModelo()).isEqualTo(result.getCodModeloChecklistInserido());
            assertThat(checklist.getCodVersaoModeloChecklist()).isEqualTo(result.getCodVersaoModeloChecklistInserido());
            assertThat(checklist.getColaborador().getCpf()).isEqualTo(Long.parseLong(CPF_TOKEN));
            assertThat(checklist.getPlacaVeiculo()).isEqualTo("PRO0001");
            assertThat(checklist.getTipo()).isEqualTo(TipoChecklist.SAIDA.asChar());
            assertThat(checklist.getKmAtualVeiculo()).isEqualTo(112);
            assertThat(checklist.getTempoRealizacaoCheckInMillis()).isEqualTo(10000);
            assertThat(checklist.getData()).isEqualTo(PrologDateParser.toLocalDateTime("2019-10-14T09:35:10"));
            assertThat(checklist.getQtdItensOk()).isEqualTo(0);
            assertThat(checklist.getQtdItensNok()).isEqualTo(2);
            assertThat(checklist.getQtdAlternativasOk()).isEqualTo(1);
            assertThat(checklist.getQtdAlternativasNok()).isEqualTo(3);
            {
                // Compara a P1.
                final PerguntaRespostaChecklist p1 = checklist.getListRespostas().get(0);

                // A1.
                final AlternativaChecklist a1 = p1.getAlternativasResposta().get(0);
                assertThat(a1.getAlternativa()).isEqualTo("A1");
                assertThat(a1.getRespostaOutros()).isNull();
                assertThat(a1.isTipoOutros()).isFalse();
                assertThat(a1.isSelected()).isTrue();
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);

                // A2.
                final AlternativaChecklist a2 = p1.getAlternativasResposta().get(1);
                assertThat(a2.getAlternativa()).isEqualTo("Outros");
                assertThat(a2.getRespostaOutros()).isNull();
                assertThat(a2.isTipoOutros()).isTrue();
                assertThat(a2.isSelected()).isFalse();
                assertThat(a2.getOrdemExibicao()).isEqualTo(2);
            }
            {
                // Compara a P2.
                final PerguntaRespostaChecklist p2 = checklist.getListRespostas().get(1);

                // B1.
                final AlternativaChecklist b1 = p2.getAlternativasResposta().get(0);
                assertThat(b1.getAlternativa()).isEqualTo("B1");
                assertThat(b1.getRespostaOutros()).isNull();
                assertThat(b1.isTipoOutros()).isFalse();
                assertThat(b1.isSelected()).isTrue();
                assertThat(b1.getOrdemExibicao()).isEqualTo(1);

                // B2.
                final AlternativaChecklist b2 = p2.getAlternativasResposta().get(1);
                assertThat(b2.getAlternativa()).isEqualTo("Outros");
                assertThat(b2.getRespostaOutros()).isEqualTo("Está com problema...");
                assertThat(b2.isTipoOutros()).isTrue();
                assertThat(b2.isSelected()).isTrue();
                assertThat(b2.getOrdemExibicao()).isEqualTo(2);
            }
        }
    }
}