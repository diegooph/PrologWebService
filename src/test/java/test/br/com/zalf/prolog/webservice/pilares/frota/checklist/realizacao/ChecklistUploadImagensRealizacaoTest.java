package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.realizacao;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.RandomUtils;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.*;
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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a realização (responder) um checklist, tanto pelo fluxo offline quanto pelo online.
 * <p>
 * Essa classe não testa o processemento das alternativas para abertura (ou não) de OSs. O foco é verificar se os dados
 * respondidos são salvos como deveriam.
 * <p>
 * Created on 2019-10-14
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@SuppressWarnings("SqlResolve")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ChecklistUploadImagensRealizacaoTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
    // Token da unidade 5.
    private static final String TOKEN_CHECK_OFF = "token_teste_unidade_5";
    private ChecklistModeloService service;
    private String tokenUsuario;

    @Override
    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        tokenUsuario = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @NotNull
    private ChecklistInsercao criaChecklistRespondidoDefault() {
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
        final String nomeModelo = "ModeloChecklist - " + RandomUtils.randomAlphanumeric(10);
        // 4 - Então inserimos o modelo.
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        tokenUsuario);

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
        //endregion

        return new ChecklistInsercao(
                5L,
                result.getCodModeloChecklistInserido(),
                result.getCodVersaoModeloChecklistInserido(),
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
    }

    @NotNull
    private InputStream getImagemFromResources(@NotNull final String fileName) throws IOException {
        final Path path = Paths.get("src", "test", "resources", fileName);
        final InputStream imagem = Files.newInputStream(path);
        assertThat(imagem).isNotNull();
        return imagem;
    }

    //region Chamadas dos testes
    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insertImagensPerguntasChecklistRealizado_buscaParaComparar_devemSerIguais(
            final boolean online) throws IOException, SQLException {
        final ChecklistInsercao insercao = criaChecklistRespondidoDefault();
        final ChecklistService checklistService = new ChecklistService();
        final Long codChecklistInserido;
        if (online) {
            codChecklistInserido = checklistService.insert(tokenUsuario, insercao);
        } else {
            codChecklistInserido = new ChecklistOfflineService()
                    .insertChecklistOffline(TOKEN_CHECK_OFF, insercao)
                    .getCodigo();
        }

        final Long codPergunta = insercao.getRespostas().get(0).getCodPergunta();

        final String uuidMidia1 = UUID.randomUUID().toString();
        final String uuidMidia2 = UUID.randomUUID().toString();
        //region Salva imagens para uma pergunta do modelo.
        {
            final SuccessResponseChecklistUploadMidia upload1 = checklistService.uploadMidiaRealizacaoChecklist(
                    getImagemFromResources("imagem_pergunta_checklist.png"),
                    FormDataContentDisposition
                            .name("file")
                            .fileName("imagem_pergunta_checklist.png")
                            .build(),
                    new ChecklistUploadMidiaRealizacao(
                            uuidMidia1,
                            codChecklistInserido,
                            codPergunta,
                            null));

            final SuccessResponseChecklistUploadMidia upload2 = checklistService.uploadMidiaRealizacaoChecklist(
                    getImagemFromResources("imagem_pergunta_checklist.png"),
                    FormDataContentDisposition
                            .name("file")
                            .fileName("imagem_pergunta_checklist.png")
                            .build(),
                    new ChecklistUploadMidiaRealizacao(
                            uuidMidia2,
                            codChecklistInserido,
                            codPergunta,
                            null));
        }
        //endregion

        //region Realiza os testes de comparação.
        {
            final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = connectionProvider.provideDatabaseConnection();
                stmt = conn.prepareStatement("select count(*) " +
                        "from checklist_respostas_midias_perguntas_ok cripo " +
                        "where uuid::text = any(?)");
                stmt.setArray(
                        1,
                        PostgresUtils.listToArray(conn, SqlType.TEXT, Arrays.asList(uuidMidia1, uuidMidia2)));
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    assertThat(rSet.getLong(1)).isEqualTo(2);
                } else {
                    throw new IllegalStateException("Erro! Mídias não encontradas!");
                }
            } finally {
                connectionProvider.closeResources(conn, stmt, rSet);
            }
        }
        //endregion
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"})
    void insertImagensAlternativasChecklistRealizado_buscaParaComparar_devemSerIguais(
            final boolean online) throws IOException, SQLException {
        final ChecklistInsercao insercao = criaChecklistRespondidoDefault();
        final ChecklistService checklistService = new ChecklistService();
        final Long codChecklistInserido;
        if (online) {
            codChecklistInserido = checklistService.insert(tokenUsuario, insercao);
        } else {
            codChecklistInserido = new ChecklistOfflineService()
                    .insertChecklistOffline(TOKEN_CHECK_OFF, insercao)
                    .getCodigo();
        }

        final Long codAlternativa = insercao.getRespostas().get(0).getAlternativasRespostas().get(0).getCodAlternativa();

        final String uuidMidia1 = UUID.randomUUID().toString();
        final String uuidMidia2 = UUID.randomUUID().toString();
        //region Salva imagens para uma pergunta do modelo.
        {
            final SuccessResponseChecklistUploadMidia upload1 = checklistService.uploadMidiaRealizacaoChecklist(
                    getImagemFromResources("imagem_alternativa_checklist.png"),
                    FormDataContentDisposition
                            .name("file")
                            .fileName("imagem_alternativa_checklist.png")
                            .build(),
                    new ChecklistUploadMidiaRealizacao(
                            uuidMidia1,
                            codChecklistInserido,
                            null,
                            codAlternativa));

            final SuccessResponseChecklistUploadMidia upload2 = checklistService.uploadMidiaRealizacaoChecklist(
                    getImagemFromResources("imagem_alternativa_checklist.png"),
                    FormDataContentDisposition
                            .name("file")
                            .fileName("imagem_alternativa_checklist.png")
                            .build(),
                    new ChecklistUploadMidiaRealizacao(
                            uuidMidia2,
                            codChecklistInserido,
                            null,
                            codAlternativa));
        }
        //endregion

        //region Realiza os testes de comparação.
        {
            final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = connectionProvider.provideDatabaseConnection();
                stmt = conn.prepareStatement("select count(*) " +
                        "from checklist_respostas_midias_alternativas_nok cripo " +
                        "where uuid::text = any(?)");
                stmt.setArray(
                        1,
                        PostgresUtils.listToArray(conn, SqlType.TEXT, Arrays.asList(uuidMidia1, uuidMidia2)));
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    assertThat(rSet.getLong(1)).isEqualTo(2);
                } else {
                    throw new IllegalStateException("Erro! Mídias não encontradas!");
                }
            } finally {
                connectionProvider.closeResources(conn, stmt, rSet);
            }
        }
        //endregion
    }
}