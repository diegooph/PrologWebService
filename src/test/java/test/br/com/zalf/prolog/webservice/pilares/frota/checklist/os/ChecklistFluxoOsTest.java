package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.os;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
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
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.PerguntaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2019-10-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChecklistFluxoOsTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
    // Token da unidade 5.
    private static final String TOKEN_CHECK_OFF = "6jylp6yo2Cx5V1tgolZo0dMX5nHWyYP6IOs9UrxX4wdaxXHnJrcKVyrbmA9mjYs2";
    private ChecklistModeloService service;
    private String token;

    @NotNull
    private static List<PerguntaModeloChecklistEdicao> jsonToCollection(@NotNull final Gson gson,
                                                                        @NotNull final String json) {
        final Type type = new TypeToken<List<PerguntaModeloChecklistEdicao>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

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

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insereChecklistOnline_deveCriarOs(final boolean comVersaoModeloSetada) {
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
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

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
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

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
                    false,
                    false,
                    null));

            // A2.
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

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
                    false,
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
                10000,
                respostas,
                ProLogDateParser.toLocalDateTime("2019-10-14T09:35:10"),
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
            assertThat(checklist.getQtdAlternativasOk()).isEqualTo(2);
            assertThat(checklist.getQtdAlternativasNok()).isEqualTo(2);
            {
                // Compara a P1.
                final PerguntaRespostaChecklist p1 = checklist.getListRespostas().get(0);

                // A1.
                final AlternativaChecklist a1 = p1.getAlternativasResposta().get(0);
                assertThat(a1.getAlternativa()).isEqualTo("A1");
                assertThat(a1.getRespostaOutros()).isNull();
                assertThat(a1.isTipoOutros()).isFalse();
                assertThat(a1.isSelected()).isFalse();
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);

                // A2.
                final AlternativaChecklist a2 = p1.getAlternativasResposta().get(1);
                assertThat(a2.getAlternativa()).isEqualTo("Outros");
                assertThat(a2.getRespostaOutros()).isEqualTo("Está com problema...");
                assertThat(a2.isTipoOutros()).isTrue();
                assertThat(a2.isSelected()).isTrue();
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
                assertThat(b1.isSelected()).isFalse();
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

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insereDoisChecklistsOnlineMesmoModelo_deveIncrementarItemOs(final boolean comVersaoModeloSetada)
            throws SQLException {
        //region Introdução e dependências
        /*
         * Este teste depende de alguns dados pré estabelecidos na base a ser testada.
         * Segue abaixo a declaração das variáveis que serão utilizadas:
         * */
        final Long codColaborador = 2272L;
        final Long codVeiculo = 3195L;
        final String placa = "PRO0001";
        final int kmColetadoVeiculo = 112;
        final int tempoRealizacaoCheckInMillis = 10000;
        final Integer versaoAppMomentoRealizacao = 80;
        final Integer versaoAppMomentoSincronizacao = 83;
        final String deviceId = "device didID";
        final String deviceImei = "deviceImei";
        final int deviceUptimeRealizacaoMillis = 10000;
        final int deviceUptimeSincronizacaoMillis = 11000;
        //endregion

        //region Insere modelo de checklist
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();

        /* Cra a pergunta 1. */
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
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        /* Cra a pergunta 2. */
        {

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        /* Então inserimos o modelo. */
        final Long codUnidade = 5L;
        final String nomeModelo = UUID.randomUUID().toString();
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        /* Agora buscamos o modelo inserido. */
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());

        /* Armazena os códigos de contexto das alternativas que abrem O.S. */
        final long codigoContextoA1 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(1).getCodigoContexto();
        final long codigoContextoA2 = modeloBuscado.getPerguntas().get(1).getAlternativas().get(1).getCodigoContexto();
        final Long codVersaoModeloChecklistInserido = comVersaoModeloSetada
                ? modeloBuscado.getCodVersaoModelo()
                : null;
        //endregion

        //region Responde o checklist C1.
        final List<ChecklistResposta> respostasC1 = new ArrayList<>();

        /* Responde a P1 do modelo criado, ela é single_choice. */
        {

            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa A1, não selecionada. */
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa A2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC1.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        /* Responde a P2 do modelo criado, ela é single_choice. */
        {
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa B1, não selecionada. */
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa B2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC1.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }
        //endregion

        //region Insere o checklist C1 respondido
        final ChecklistInsercao insercao = new ChecklistInsercao(
                codUnidade,
                modeloBuscado.getCodModelo(),
                codVersaoModeloChecklistInserido,
                codColaborador,
                codVeiculo,
                placa,
                TipoChecklist.SAIDA,
                kmColetadoVeiculo,
                tempoRealizacaoCheckInMillis,
                respostasC1,
                ProLogDateParser.toLocalDateTime("2019-10-22T01:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                versaoAppMomentoRealizacao,
                versaoAppMomentoSincronizacao,
                deviceId,
                deviceImei,
                deviceUptimeRealizacaoMillis,
                deviceUptimeSincronizacaoMillis,
                0,
                0);

        final ChecklistService checklistService = new ChecklistService();
        checklistService.insert(token, insercao);
        //endregion

        //region Verificar se as O.S. foram abertas com o item pendente relacionado a alternativa
        /* Verifica se existe o item de ordem de serviço pelo código de contexto do C1 A1*/
        {
            assertThat(verifyIfContextoAlternativaExists(codigoContextoA1)).isTrue();
        }
        /* Verifica se existe o item de ordem de serviço pelo código de contexto do C1 A2*/
        {
            assertThat(verifyIfContextoAlternativaExists(codigoContextoA2)).isTrue();
        }
        //endregion

        //region Responde o checklist C2.
        final List<ChecklistResposta> respostasC2 = new ArrayList<>();

        /* Responde a P1 do modelo criado, ela é single_choice. */
        {

            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa A1, não selecionada. */
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa A2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC2.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        /* Responde a P2 do modelo criado, ela é single_choice. */
        {
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa B1, não selecionada. */
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa B2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC2.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }
        //endregion

        //region Insere o checklist C2 respondido
        final ChecklistInsercao insercaoC2 = new ChecklistInsercao(
                codUnidade,
                modeloBuscado.getCodModelo(),
                codVersaoModeloChecklistInserido,
                codColaborador,
                codVeiculo,
                placa,
                TipoChecklist.SAIDA,
                kmColetadoVeiculo,
                tempoRealizacaoCheckInMillis,
                respostasC2,
                ProLogDateParser.toLocalDateTime("2019-10-22T02:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                versaoAppMomentoRealizacao,
                versaoAppMomentoSincronizacao,
                deviceId,
                deviceImei,
                deviceUptimeRealizacaoMillis,
                deviceUptimeSincronizacaoMillis,
                0,
                0);

        final ChecklistService checklistServiceC2 = new ChecklistService();
        final Long codChecklistInseridoC2 = checklistServiceC2.insert(token, insercaoC2);
        //endregion

        // Verifica o incremento na quantidade de apontamentos do item de O.S. pelo código de contexto do C2 A1
        // e do C2 A2.
        {
            assertThat(getQuantidadeApontamentosAlternativaItemOs(codigoContextoA1)).isEqualTo(2);
            assertThat(getQuantidadeApontamentosAlternativaItemOs(codigoContextoA2)).isEqualTo(2);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insereChecklistOnlineEditaModeloMantendoContexto_deveIncrementarItemOs(final boolean comVersaoModeloSetada)
            throws SQLException {
        //region Introdução e dependências
        /*
         * Este teste depende de alguns dados pré estabelecidos na base a ser testada.
         * Segue abaixo a declaração das variáveis que serão utilizadas:
         * */
        final Long codColaborador = 2272L;
        final Long codVeiculo = 3195L;
        final String placa = "PRO0001";
        final int kmColetadoVeiculo = 112;
        final int tempoRealizacaoCheckInMillis = 10000;
        final Integer versaoAppMomentoRealizacao = 80;
        final Integer versaoAppMomentoSincronizacao = 83;
        final String deviceId = "device didID";
        final String deviceImei = "deviceImei";
        final int deviceUptimeRealizacaoMillis = 10000;
        final int deviceUptimeSincronizacaoMillis = 11000;
        //endregion

        //region Insere modelo de checklist
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();

        /* Cra a pergunta 1. */
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
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        /* Cra a pergunta 2. */
        {

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        /* Então inserimos o modelo. */
        final Long codUnidade = 5L;
        final String nomeModelo = UUID.randomUUID().toString();
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        /* Agora buscamos o modelo inserido. */
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());

        /* Armazena os códigos de contexto das alternativas que abrem O.S. */
        final long codigoContextoP1A2 = modeloBuscado.getPerguntas().get(0).getAlternativas().get(1).getCodigoContexto();
        final long codigoContextoP2B2 = modeloBuscado.getPerguntas().get(1).getAlternativas().get(1).getCodigoContexto();
        final Long codVersaoModeloChecklistInseridoM1 = comVersaoModeloSetada ? modeloBuscado.getCodVersaoModelo() : null;
        //endregion

        //region Insere checklist C1 com alternativas que devem abrir O.S.

        //region Responde o checklist C1.
        final List<ChecklistResposta> respostasC1 = new ArrayList<>();

        /* Responde a P1 do modelo criado, ela é single_choice. */
        {

            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa A1, não selecionada. */
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa A2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC1.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        /* Responde a P2 do modelo criado, ela não é single_choice. */
        {
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa B1, não selecionada. */
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa B2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC1.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }
        //endregion

        //region Insere o checklist C1 respondido
        final ChecklistInsercao insercao = new ChecklistInsercao(
                codUnidade,
                modeloBuscado.getCodModelo(),
                codVersaoModeloChecklistInseridoM1,
                codColaborador,
                codVeiculo,
                placa,
                TipoChecklist.SAIDA,
                kmColetadoVeiculo,
                tempoRealizacaoCheckInMillis,
                respostasC1,
                ProLogDateParser.toLocalDateTime("2019-10-22T01:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                versaoAppMomentoRealizacao,
                versaoAppMomentoSincronizacao,
                deviceId,
                deviceImei,
                deviceUptimeRealizacaoMillis,
                deviceUptimeSincronizacaoMillis,
                0,
                0);

        final ChecklistService checklistService = new ChecklistService();
        final Long codChecklistInserido = checklistService.insert(token, insercao);
        //endregion

        //endregion

        /* Verifica se existe o item de ordem de serviço pelo código de contexto do C1 A1*/
        {
            assertThat(verifyIfContextoAlternativaExists(codigoContextoP1A2)).isTrue();
        }
        /* Verifica se existe o item de ordem de serviço pelo código de contexto do C1 A2*/
        {
            assertThat(verifyIfContextoAlternativaExists(codigoContextoP2B2)).isTrue();
        }

        //region Alterar a alternativa no modelo sem alterar o contexto.
        // 4, 5 - Então, removemos a P2 e atualizamos.
        final List<PerguntaModeloChecklistEdicao> perguntasEditado = toPerguntasEdicao(modeloBuscado);
        final List<Long> cargosEditado = getCodigosCargos(modeloBuscado);
        final List<Long> tiposVeiculoEditado = getCodigosTiposVeiculos(modeloBuscado);

        // Removemos a última pergunta.
        perguntas.remove(perguntas.size() - 1);

        final ModeloChecklistEdicao editado = createModeloEdicao(modeloBuscado, perguntasEditado, cargosEditado, tiposVeiculoEditado);
        service.updateModeloChecklist(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                editado,
                token);

        // 6 - Por último, buscamos novamente o modelo e comparamos com o base. Tudo deve bater.
        final ModeloChecklistVisualizacao novoModeloEditado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());
        //endregion

        //region Responde o checklist C2.
        final List<ChecklistResposta> respostasC2 = new ArrayList<>();

        /* Responde a P1 do modelo criado, ela é single_choice. */
        {

            final PerguntaModeloChecklistVisualizacao p1 = novoModeloEditado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            /* Responde a alternativa A1, não selecionada. */
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    false,
                    false,
                    null));

            /* Responde a alternativa A2 (Abre O.S.), tipo outros, selecionada. */
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostasC2.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }
        //endregion

        //region Insere o checklist C2 respondido
        final Long codVersaoModeloChecklistInseridoM2 = comVersaoModeloSetada
                ? novoModeloEditado.getCodVersaoModelo()
                : null;
        final ChecklistInsercao insercaoC2 = new ChecklistInsercao(
                codUnidade,
                novoModeloEditado.getCodModelo(),
                codVersaoModeloChecklistInseridoM2,
                codColaborador,
                codVeiculo,
                placa,
                TipoChecklist.SAIDA,
                kmColetadoVeiculo,
                tempoRealizacaoCheckInMillis,
                respostasC2,
                ProLogDateParser.toLocalDateTime("2019-10-22T02:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                versaoAppMomentoRealizacao,
                versaoAppMomentoSincronizacao,
                deviceId,
                deviceImei,
                deviceUptimeRealizacaoMillis,
                deviceUptimeSincronizacaoMillis,
                0,
                0);

        final ChecklistService checklistServiceC2 = new ChecklistService();
        final Long codChecklistInseridoC2 = checklistServiceC2.insert(token, insercaoC2);
        //endregion

        // Garante que a A2 foi a incrementada pois manteve o código de contexto e a B2 não pois foi removida e não
        // realizada no checklist C2.
        {
            assertThat(getQuantidadeApontamentosAlternativaItemOs(codigoContextoP1A2)).isEqualTo(2);
            assertThat(getQuantidadeApontamentosAlternativaItemOs(codigoContextoP2B2)).isEqualTo(1);
        }
    }

    //region Métodos de auxílio para os testes
    private boolean verifyIfContextoAlternativaExists(@NotNull final Long codigoContextoAlternativa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT * FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA " +
                    "WHERE COD_CONTEXTO_ALTERNATIVA IN (?)) AS EXISTE_ITEM;");
            stmt.setLong(1, codigoContextoAlternativa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_ITEM");
            } else {
                throw new SQLException(
                        "Não foi possível encontrar a O.S. da alternativa com o código de contexto: " + codigoContextoAlternativa);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private long getQuantidadeApontamentosAlternativaItemOs(@NotNull final Long codigoContextoAlternativa)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("SELECT QT_APONTAMENTOS FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA " +
                    "WHERE COD_CONTEXTO_ALTERNATIVA IN (?);");
            stmt.setLong(1, codigoContextoAlternativa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("QT_APONTAMENTOS");
            } else {
                throw new SQLException(
                        "Não foi possível encontrar a O.S. da alternativa com o código de contexto: "
                                + codigoContextoAlternativa);
            }
        } finally {
            DatabaseConnection.close(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<PerguntaModeloChecklistEdicao> toPerguntasEdicao(
            @NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return jsonToCollection(
                GsonUtils.getGson(),
                GsonUtils.getGson().toJson(modeloBuscado.getPerguntas()));
    }

    @NotNull
    private List<Long> getCodigosTiposVeiculos(@NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return modeloBuscado
                .getTiposVeiculoLiberados()
                .stream()
                .map(TipoVeiculo::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<Long> getCodigosCargos(@NotNull final ModeloChecklistVisualizacao modeloBuscado) {
        return modeloBuscado
                .getCargosLiberados()
                .stream()
                .map(Cargo::getCodigo)
                .collect(Collectors.toList());
    }

    @NotNull
    private ModeloChecklistEdicao createModeloEdicao(
            @NotNull final ModeloChecklistVisualizacao modeloBuscado,
            @NotNull final List<PerguntaModeloChecklistEdicao> perguntas,
            @NotNull final List<Long> cargos,
            @NotNull final List<Long> tiposVeiculo) {
        return new ModeloChecklistEdicao(
                modeloBuscado.getCodUnidade(),
                modeloBuscado.getCodModelo(),
                modeloBuscado.getCodVersaoModelo(),
                modeloBuscado.getNome(),
                tiposVeiculo,
                cargos,
                perguntas,
                modeloBuscado.isAtivo());
    }
    //endregion
}
