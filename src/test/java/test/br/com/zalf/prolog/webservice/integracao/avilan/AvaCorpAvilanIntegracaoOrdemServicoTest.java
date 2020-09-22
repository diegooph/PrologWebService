package test.br.com.zalf.prolog.webservice.integracao.avilan;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
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
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OrdemServicoService;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2020-09-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AvaCorpAvilanIntegracaoOrdemServicoTest extends BaseTest {

    @NotNull
    private static final String CPF_TOKEN = "03383283194";
    @Nullable
    private String token;
    private ChecklistModeloService checklistService;
    private OrdemServicoService ordemServicoService;

    @Override
    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        ativarIntegracao();
        checklistService = new ChecklistModeloService();
        ordemServicoService = new OrdemServicoService();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void aberturaOs() throws Throwable {
        final Long codChecklist = insereChecklist();
        assertThat(codChecklist).isNotNull();
        final Long codPendenciaOs = buscaOsPendenteProlog(codChecklist);
        assertThat(codPendenciaOs).isNotNull();
    }

    @Test
    public void fechaUmItemOs() throws Throwable {
        final Long codChecklist = insereChecklist();
        assertThat(codChecklist).isNotNull();
        final Long codPendenciaOs = buscaOsPendenteProlog(codChecklist);
        assertThat(codPendenciaOs).isNotNull();
        setarOsComoEnviada(codPendenciaOs);
        //noinspection ConstantConditions
        boolean osPendente = verificarOsPendente(codPendenciaOs);
        assertThat(osPendente).isNotNull();
        assertThat(osPendente).isFalse();
        final ResolverItemOrdemServico resolverItemOrdemServico = buscaResolucaoItem(codChecklist);
        assertThat(resolverItemOrdemServico).isNotNull();
        ordemServicoService.resolverItem(token, resolverItemOrdemServico);
        //noinspection ConstantConditions
        osPendente = verificarOsPendente(codPendenciaOs);
        assertThat(osPendente).isTrue();
    }

    @Test
    public void fechaVariosItensOs() throws Throwable {
        final Long codChecklist = insereChecklist();
        final Long codPendenciaOs = buscaOsPendenteProlog(codChecklist);
        assertThat(codPendenciaOs).isNotNull();
        setarOsComoEnviada(codPendenciaOs);
        //noinspection ConstantConditions
        boolean osPendente = verificarOsPendente(codPendenciaOs);
        assertThat(osPendente).isNotNull();
        assertThat(osPendente).isFalse();
        final ResolverMultiplosItensOs resolverMultiplosItensOs = buscaResolucaoVariosItens(codChecklist);
        assertThat(resolverMultiplosItensOs).isNotNull();
        ordemServicoService.resolverItens(token, resolverMultiplosItensOs);
        //noinspection ConstantConditions
        osPendente = verificarOsPendente(codPendenciaOs);
        assertThat(osPendente).isTrue();
    }

    @Nullable
    private ResolverItemOrdemServico buscaResolucaoItem(@NotNull final Long codChecklist) throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select cosd.codigo as codigo_os, " +
                    "       cosid.codigo as codigo_item, " +
                    "       cd.placa_veiculo as placa, " +
                    "       cosd.cod_unidade as codigo_unidade " +
                    "from integracao.checklist_ordem_servico_sincronizacao coss " +
                    "inner join checklist_ordem_servico_data cosd on coss.codigo_os_prolog = cosd.codigo_prolog " +
                    "inner join checklist_ordem_servico_itens_data cosid on cosd.codigo = cosid.cod_os and cosd.cod_unidade = cosid.cod_unidade " +
                    "inner join checklist_data cd on cd.codigo = cosd.cod_checklist " +
                    "where cosd.cod_checklist = ?;");
            stmt.setLong(1, codChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ResolverItemOrdemServico(3383283194L,
                        rSet.getLong("codigo_item"),
                        "teste",
                        rSet.getString("placa"),
                        999999L,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        rSet.getLong("codigo_os"),
                        rSet.getLong("codigo_unidade"));
            } else {
                return null;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Nullable
    private ResolverMultiplosItensOs buscaResolucaoVariosItens(@NotNull final Long codChecklist) throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select cosd.codigo as codigo_os, " +
                    "       cosid.codigo as codigo_item, " +
                    "       cd.placa_veiculo as placa, " +
                    "       cosd.cod_unidade as codigo_unidade " +
                    "from integracao.checklist_ordem_servico_sincronizacao coss " +
                    "inner join checklist_ordem_servico_data cosd on coss.codigo_os_prolog = cosd.codigo_prolog " +
                    "inner join checklist_ordem_servico_itens_data cosid on cosd.codigo = cosid.cod_os and cosd.cod_unidade = cosid.cod_unidade " +
                    "inner join checklist_data cd on cd.codigo = cosd.cod_checklist " +
                    "where cosd.cod_checklist = ?;");
            stmt.setLong(1, codChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ResolverMultiplosItensOs(3383283194L,
                        rSet.getString("placa"),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        999999L,
                        "teste",
                        rSet.getLong("codigo_unidade"),
                        Collections.singletonList(rSet.getLong("codigo_item")));
            } else {
                return null;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Nullable
    private Boolean verificarOsPendente(final Long codPendenciaOs) throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select pendente_sincronia " +
                    "from integracao.checklist_ordem_servico_sincronizacao " +
                    "where codigo = ?;");
            stmt.setLong(1, codPendenciaOs);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("pendente_sincronia");
            } else {
                return null;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void setarOsComoEnviada(final Long codPendenciaOs) throws SQLException {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("update integracao.checklist_ordem_servico_sincronizacao " +
                    "set pendente_sincronia = false " +
                    "where codigo = ?;");
            stmt.setLong(1, codPendenciaOs);
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    private Long insereChecklist() {
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
                checklistService.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        /* Agora buscamos o modelo inserido.*/
        final ModeloChecklistVisualizacao modeloBuscado = checklistService.getModeloChecklist(
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

        final ChecklistInsercao insercao = new ChecklistInsercao(
                5L,
                result.getCodModeloChecklistInserido(),
                null,
                2272L,
                3195L,
                "PRO0001",
                TipoChecklist.SAIDA,
                112,
                "uma observacao",
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
        return checklistService.insert(token, insercao);
    }

    private void ativarIntegracao() {
        try {
            inserirTokenIntegracao();
            final Long codigoRecursoOs = inserirSistema();
            inserirMetodoIntegrado(codigoRecursoOs);
        } catch (final Throwable t) {
            Log.e("ERRO", "Olha aqui", t);
        }
    }

    private void inserirTokenIntegracao() throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from integracao.token_integracao " +
                    "where cod_empresa = 3;");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return;
            }
            stmt = conn.prepareStatement("insert into integracao.token_integracao (" +
                    "cod_empresa," +
                    "token_integracao," +
                    "ativo)" +
                    "values (3, 'um_token', true);");
            stmt.execute();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private Long inserirSistema() throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from integracao.empresa_integracao_sistema " +
                    "where cod_empresa = 3 " +
                    "and chave_sistema = 'AVACORP_AVILAN' " +
                    "and recurso_integrado = 'CHECKLIST';");
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                stmt = conn.prepareStatement("insert into integracao.empresa_integracao_sistema (" +
                        "cod_empresa," +
                        "chave_sistema," +
                        "recurso_integrado," +
                        "ativo)" +
                        "values (3, 'AVACORP_AVILAN', 'CHECKLIST', true);");
                stmt.execute();
            }

            stmt = conn.prepareStatement("select * from integracao.empresa_integracao_sistema " +
                    "where cod_empresa = 3 " +
                    "and chave_sistema = 'AVACORP_AVILAN' " +
                    "and recurso_integrado = 'CHECKLIST_OFFLINE';");
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                stmt = conn.prepareStatement("insert into integracao.empresa_integracao_sistema (" +
                        "cod_empresa," +
                        "chave_sistema," +
                        "recurso_integrado," +
                        "ativo)" +
                        "values (3, 'AVACORP_AVILAN', 'CHECKLIST_OFFLINE', true);");
                stmt.execute();
            }

            stmt = conn.prepareStatement("select * from integracao.empresa_integracao_sistema " +
                    "where cod_empresa = 3 " +
                    "and chave_sistema = 'AVACORP_AVILAN' " +
                    "and recurso_integrado = 'CHECKLIST_ORDEM_SERVICO';");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("codigo");
            } else {
                stmt = conn.prepareStatement("insert into integracao.empresa_integracao_sistema (" +
                        "cod_empresa," +
                        "chave_sistema," +
                        "recurso_integrado," +
                        "ativo)" +
                        "values (3, 'AVACORP_AVILAN', 'CHECKLIST_ORDEM_SERVICO', true) returning codigo;");
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    return rSet.getLong("codigo");
                } else {
                    throw new Exception("Erro ao inserir recurso integrado de O.S.");
                }
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void inserirMetodoIntegrado(final Long codIntegracaoSistema) throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from integracao.empresa_integracao_metodos " +
                    "where cod_integracao_sistema = ? " +
                    "and metodo_integrado = 'INSERT_OS';");
            stmt.setLong(1, codIntegracaoSistema);
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                stmt = conn.prepareStatement("insert into integracao.empresa_integracao_metodos (" +
                        "cod_integracao_sistema," +
                        "metodo_integrado," +
                        "url_completa," +
                        "api_token_client," +
                        "api_short_code)" +
                        "values (?," +
                        " 'INSERT_OS'," +
                        " 'http://prolog.avaconcloud.com/Avilan/IntegracaoMobile/api/OrdemServicoIn'," +
                        " 'eusouumtoken'," +
                        " 123456789);");
                stmt.setLong(1, codIntegracaoSistema);
                stmt.execute();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Nullable
    private Long buscaOsPendenteProlog(final Long codChecklist) throws Throwable {
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select coss.codigo " +
                    "from integracao.checklist_ordem_servico_sincronizacao coss " +
                    "inner join checklist_ordem_servico_data cosd on coss.codigo_os_prolog = cosd.codigo_prolog " +
                    "where cosd.cod_checklist = ?;");
            stmt.setLong(1, codChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("codigo");
            } else {
                return null;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
