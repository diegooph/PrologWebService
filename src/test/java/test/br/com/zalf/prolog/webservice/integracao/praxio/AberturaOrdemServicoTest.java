package test.br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineService;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoResolvido;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.integracao.praxio.ChecklistItensNokGlobusTask;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import br.com.zalf.prolog.webservice.integracao.praxio.data.GlobusPiccoloturRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.data.SistemaGlobusPiccoloturDaoImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.*;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 12/11/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AberturaOrdemServicoTest extends BaseTest {
    @NotNull
    private static final Random RANDOM = new Random();
    private static final String TOKEN_INTEGRACAO = "teste";
    private static final Long COD_UNIDADE = 215L;
    private ChecklistModeloService modeloChecklistService;
    private ChecklistService checklistService;
    private ChecklistOfflineService checklistOfflineService;
    private String tokenIntegrado;

    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        modeloChecklistService = new ChecklistModeloService();
        checklistService = new ChecklistService();
        checklistOfflineService = new ChecklistOfflineService();
        tokenIntegrado = getValidToken("3383283194");
    }

    @AfterAll
    public void destroy() {
        modeloChecklistService = null;
        checklistService = null;
        checklistOfflineService = null;
        DatabaseManager.finish();
    }

    /**
     * Método para testar o Roteamento da Inserção de Checklist.
     * <p>
     * Esse teste deve simular e validar a realização de um checklit por um colaborador de uma empresa que possua
     * integração de Ordem de Serviço ativa. Para isso o teste deve:
     * 1 - Criar um modelo de checklist para simular a realização.
     * 2 - Criar um checklist realizado a partir do modelo criado.
     * 3 - Inserir o checklist na base de dados utilizando o método integrado e roteado.
     * <p>
     * Para esse teste ser considerado sucesso, devemos validar:
     * 1 - Se o checklist foi inserido na base do ProLog corretamente.
     * 2 - Se o código do checklist inserido está presente na tabela da integração como pendente de sincronização.
     *
     * @throws Throwable Teste executado apresentou erro
     */
    @Test
    void testInsercaoChecklistRoteamentoIntegracao() throws Throwable {
        // ################################### ETAPA 1 - Cria um modelo de checklist ###################################
        final ResultInsertModeloChecklist resultModeloChecklist =
                criaModeloChecklist(COD_UNIDADE, "Modelo Teste Inserção Checklist Roteado ");

        // ################################## ETAPA 2 - Insere um checklist do modelo ##################################
        final ChecklistInsercao checklistInsercao = insertChecklistModeloCriado(COD_UNIDADE, resultModeloChecklist);

        // Configura modelo para ser integrado
        permiteSincronizarModeloChecklist(resultModeloChecklist);

        final Long codChecklistInserido = checklistService.insert(tokenIntegrado, checklistInsercao);
        assertThat(codChecklistInserido).isNotNull();
        assertThat(codChecklistInserido).isGreaterThan(0);

        // ################################## ETAPA 3 - VALIDAR INFORMAÇÕES INSERIDAS ##################################
        { // region Validações do teste
            final Checklist checklist = checklistService.getByCod(codChecklistInserido, tokenIntegrado);
            assertThat(checklist).isNotNull();

            final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = connectionProvider.provideDatabaseConnection();
                stmt = conn.prepareStatement("select * " +
                        "from piccolotur.checklist_pendente_para_sincronizar " +
                        "where cod_checklist_para_sincronizar = ?;");
                stmt.setLong(1, codChecklistInserido);
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    assertThat(rSet.getBoolean("SINCRONIZADO")).isFalse();
                    assertThat(rSet.getBoolean("PRECISA_SER_SINCRONIZADO")).isTrue();
                } else {
                    throw new SQLException("Erro! Checklist não foi mapeado como pendente");
                }
            } finally {
                connectionProvider.closeResources(conn, stmt, rSet);
            }
        }
    }

    /**
     * Método utilizado para testar a Abertura de uma Ordem de Serviço a partir da integração de O.S.
     * <p>
     * Esse método deve simular e validar a abertura de Ordens de Serviço pelo sistema parceiro.
     * Para isso o teste deve:
     * 1 - Criar um modelo de checklist.
     * 2 - Realizar um checklist do modelo criado.
     * 3 - Inserir o checklist na base do ProLog utilizando o roteamento.
     * 4 - A partir do checklist realizado, utilizar os itens NOK para montar uma O.S;
     * 5 - Utilizar o método de integração para inserir a O.S na base de dados.
     * <p>
     * Para esse teste ser considerado sucesso, devemos validar:
     * 1 - Se existe uma O.S aberta no ProLog com os itens apontados como NOK.
     * 2 - Se existe nas tabelas de mapeamento, os vinculos entre a O.S aberta e as informações do sistema parceiro.
     *
     * @throws Throwable Teste executado apresentou erro
     */
    @Test
    void testAberturaOrdemServicoIntegracao() throws Throwable {
        final ResultInsertModeloChecklist resultModeloChecklist =
                criaModeloChecklist(COD_UNIDADE, "Modelo Abertura Ordem Serviço Integração ");

        // ################################### ETAPA 2 - Cria um checklist do modelo ###################################
        final ChecklistInsercao checklistInsercao = insertChecklistModeloCriado(COD_UNIDADE, resultModeloChecklist);

        // Configura modelo para ser integrado
        permiteSincronizarModeloChecklist(resultModeloChecklist);

        final Long codChecklistInserido = checklistService.insert(tokenIntegrado, checklistInsercao);
        Thread.sleep(100);
        // ############################### ETAPA 3 - Marcar checklist como sincronizado ################################
        final SistemaGlobusPiccoloturDaoImpl sistemaGlobusPiccoloturDao = new SistemaGlobusPiccoloturDaoImpl();
        marcarChecklistComoSincronizado(codChecklistInserido, sistemaGlobusPiccoloturDao);

        // ################################# ETAPA 4 - Insere uma O.S Globus no ProLog #################################
        final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = new ArrayList<>();
        final long nextCodOs = getNextCodOsUnidade(COD_UNIDADE);
        final List<Long> codsAlaternativasOS = new ArrayList<>();

        { // region Insere a Ordem de Serviço Globus a partir do checklist
            final ChecklistItensNokGlobus checklistItensNokGlobus =
                    getChecklistToSyncGlobus(codChecklistInserido, sistemaGlobusPiccoloturDao)
                            .getChecklistItensNokGlobus();

            final PerguntaNokGlobus pergunta1 = checklistItensNokGlobus.getPerguntasNok().get(0);
            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta1.getCodContextoPerguntaNok(),
                            pergunta1.getAlternativasNok().get(0).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta1.getAlternativasNok().get(0).getCodContextoAlternativaNok());

            final PerguntaNokGlobus pergunta2 = checklistItensNokGlobus.getPerguntasNok().get(1);
            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta2.getCodContextoPerguntaNok(),
                            pergunta2.getAlternativasNok().get(0).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta2.getAlternativasNok().get(0).getCodContextoAlternativaNok());

            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta2.getCodContextoPerguntaNok(),
                            pergunta2.getAlternativasNok().get(1).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta2.getAlternativasNok().get(1).getCodContextoAlternativaNok());
        }

        final OrdemServicoAbertaGlobus ordemServicoAbertaGlobus =
                new OrdemServicoAbertaGlobus(
                        nextCodOs,
                        COD_UNIDADE,
                        codChecklistInserido,
                        itensOSAbertaGlobus);

        final IntegracaoPraxioService integracaoPraxioService = new IntegracaoPraxioService();
        final SuccessResponseIntegracao successResponseIntegracao =
                integracaoPraxioService
                        .inserirOrdensServicoGlobus(
                                TOKEN_INTEGRACAO,
                                Collections.singletonList(ordemServicoAbertaGlobus));

        // ################################## ETAPA 4 - VALIDAR INFORMAÇÕES INSERIDAS ##################################
        { // region Validação das informações
            assertThat(successResponseIntegracao).isNotNull();

            final HolderResolucaoOrdemServico ordemServico =
                    Injection
                            .provideOrdemServicoDao()
                            .getHolderResolucaoOrdemServico(COD_UNIDADE, nextCodOs);

            assertThat(ordemServico).isNotNull();
            assertThat(ordemServico.getOrdemServico()).isNotNull();
            assertThat(ordemServico.getKmAtualVeiculo()).isEqualTo(checklistInsercao.getKmColetadoVeiculo());
            assertThat(ordemServico.getPlacaVeiculo()).isEqualTo(checklistInsercao.getPlacaVeiculo());
            assertThat(ordemServico.getOrdemServico().getCodOrdemServico()).isEqualTo(nextCodOs);
            final List<ItemOrdemServicoVisualizacao> itens = ordemServico.getOrdemServico().getItens();
            assertThat(itens).isNotNull();
            assertThat(itens).isNotEmpty();
            assertThat(itens).hasSize(itensOSAbertaGlobus.size());

            final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = provider.provideDatabaseConnection();
                stmt = conn.prepareStatement("SELECT * " +
                        "FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO " +
                        "WHERE COD_UNIDADE = ? " +
                        "  AND COD_OS_GLOBUS = ?;");
                stmt.setLong(1, COD_UNIDADE);
                stmt.setLong(2, nextCodOs);
                rSet = stmt.executeQuery();

                boolean rSetHasData = false;
                while (rSet.next()) {
                    rSetHasData = true;
                    assertThat(rSet.getLong("COD_CONTEXTO_ALTERNATIVA_OS_PROLOG"))
                            .isIn(codsAlaternativasOS);
                }
                assertThat(rSetHasData).isTrue();

            } finally {
                provider.closeResources(conn, stmt, rSet);
            }
        }
    }

    /**
     * Método para testar o fechamento de uma Ordem de Serviço a partir do sistema parceiro, utilizando a integração.
     * <p>
     * Esse método deve simular e validar as informações necessárias para que o fechamento de uma Ordem de Serviço
     * aconteça através da integração desenvolvida com o parceiro.
     * Para isso o teste deve:
     * 1 - Criar um modelo de checklist para utilizar no teste.
     * 2 - Realizar um checklist utilizando o modelo de checklist criado.
     * 3 - Inserir esse checklist na base do ProLog e nas tabelas de mapeamento.
     * 4 - Utilizando os itens NOK apontados no checklist, montar uma Ordem de Serviço.
     * 5 - Inserir a Ordem de Serviço utilizando os métodos disponíveis na integração.
     * 6 - A partir O.S Aberta, criar uma O.S Fechada.
     * 7 - Utilizar os métodos da integração para inserir os Itens da O.S fechada.
     * <p>
     * Para esse teste ser considerado um sucesso, devemos validar:
     * 1 - Se existe no ProLog aquela O.S e se está com status fechada.
     * 2 - Se existe nas tabelas de mapeamento, os vinculos indicando que a O.S está reamente fechada.
     *
     * @throws Throwable Teste executado apresentou erro
     */
    @Test
    public void testFechamentoOrdemServicoIntegracao() throws Throwable {
        // ################################### ETAPA 1 - Cria um modelo de checklist ###################################
        final ResultInsertModeloChecklist resultModeloChecklist =
                criaModeloChecklist(COD_UNIDADE, "Modelo Abertura Ordem Serviço Integração ");

        // ################################### ETAPA 2 - Cria um checklist do modelo ###################################
        final ChecklistInsercao checklistInsercao = insertChecklistModeloCriado(COD_UNIDADE, resultModeloChecklist);

        // Configura modelo para ser integrado
        permiteSincronizarModeloChecklist(resultModeloChecklist);

        final Long codChecklistInserido = checklistService.insert(tokenIntegrado, checklistInsercao);

        // ############################### ETAPA 3 - Marcar checklist como sincronizado ################################
        final SistemaGlobusPiccoloturDaoImpl sistemaGlobusPiccoloturDao = new SistemaGlobusPiccoloturDaoImpl();
        marcarChecklistComoSincronizado(codChecklistInserido, sistemaGlobusPiccoloturDao);

        // ################################# ETAPA 4 - Insere uma O.S Globus no ProLog #################################
        final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = new ArrayList<>();
        final long nextCodOs = getNextCodOsUnidade(COD_UNIDADE);
        final List<Long> codsAlaternativasOS = new ArrayList<>();

        { // region Insere a Ordem de Serviço Globus a partir do checklist
            final ChecklistItensNokGlobus checklistItensNokGlobus =
                    getChecklistToSyncGlobus(codChecklistInserido, sistemaGlobusPiccoloturDao)
                            .getChecklistItensNokGlobus();

            final PerguntaNokGlobus pergunta1 = checklistItensNokGlobus.getPerguntasNok().get(0);
            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta1.getCodContextoPerguntaNok(),
                            pergunta1.getAlternativasNok().get(0).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta1.getAlternativasNok().get(0).getCodContextoAlternativaNok());

            final PerguntaNokGlobus pergunta2 = checklistItensNokGlobus.getPerguntasNok().get(1);
            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta2.getCodContextoPerguntaNok(),
                            pergunta2.getAlternativasNok().get(0).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta2.getAlternativasNok().get(0).getCodContextoAlternativaNok());

            // Adiciona item 1
            itensOSAbertaGlobus.add(
                    new ItemOSAbertaGlobus(
                            getRandomCodItenGlobus(),
                            pergunta2.getCodContextoPerguntaNok(),
                            pergunta2.getAlternativasNok().get(1).getCodContextoAlternativaNok()));
            codsAlaternativasOS.add(pergunta2.getAlternativasNok().get(1).getCodContextoAlternativaNok());
        }
        final OrdemServicoAbertaGlobus ordemServicoAbertaGlobus =
                new OrdemServicoAbertaGlobus(
                        nextCodOs,
                        COD_UNIDADE,
                        codChecklistInserido,
                        itensOSAbertaGlobus);

        final IntegracaoPraxioService integracaoPraxioService = new IntegracaoPraxioService();
        integracaoPraxioService
                .inserirOrdensServicoGlobus(
                        TOKEN_INTEGRACAO,
                        Collections.singletonList(ordemServicoAbertaGlobus));

        // ################################## ETAPA 5 - Fecha a O.S Globus no ProLog ###################################
        final List<ItemResolvidoGlobus> itensResolvidos = new ArrayList<>();

        { // region Informações para fechamento de O.S Globus no Prolog
            for (final ItemOSAbertaGlobus itemGlobus : itensOSAbertaGlobus) {
                itensResolvidos.add(new ItemResolvidoGlobus(
                        COD_UNIDADE,
                        nextCodOs,
                        itemGlobus.getCodItemGlobus(),
                        "3383283194",
                        "PRO0006",
                        11222L,
                        30000L,
                        "Item consertado no teste",
                        Now.localDateTimeUtc(),
                        Now.localDateTimeUtc().minusDays(1L),
                        Now.localDateTimeUtc()));
            }

        }
        final SuccessResponseIntegracao responseIntegracao =
                integracaoPraxioService.resolverMultiplosItens(TOKEN_INTEGRACAO, itensResolvidos);

        // ################################ ETAPA 6 - Valida informações do fechamento #################################
        { // region Validação das informações
            assertThat(responseIntegracao).isNotNull();

            final HolderResolucaoOrdemServico ordemServico =
                    Injection
                            .provideOrdemServicoDao()
                            .getHolderResolucaoOrdemServico(COD_UNIDADE, nextCodOs);

            assertThat(ordemServico).isNotNull();
            assertThat(ordemServico.getOrdemServico()).isNotNull();
            assertThat(ordemServico.getKmAtualVeiculo()).isEqualTo(checklistInsercao.getKmColetadoVeiculo());
            assertThat(ordemServico.getPlacaVeiculo()).isEqualTo(checklistInsercao.getPlacaVeiculo());
            assertThat(ordemServico.getOrdemServico().getCodOrdemServico()).isEqualTo(nextCodOs);
            final List<ItemOrdemServicoVisualizacao> itens = ordemServico.getOrdemServico().getItens();
            assertThat(itens).isNotNull();
            assertThat(itens).isNotEmpty();
            assertThat(itens).hasSize(itensOSAbertaGlobus.size());
            assertThat(itens).hasSize(itensResolvidos.size());
            itens.forEach(item -> {
                assertThat(item).isInstanceOf(ItemOrdemServicoResolvido.class);
                final ItemOrdemServicoResolvido itemFechado = (ItemOrdemServicoResolvido) item;
                assertThat(itemFechado.getCodColaboradorResolucao()).isNotNull();
                assertThat(itemFechado.getNomeColaboradorResolucao()).isNotNull();
                assertThat(itemFechado.getDataHoraResolvidoProLog()).isNotNull();
                assertThat(itemFechado.getDataHoraInicioResolucao()).isNotNull();
                assertThat(itemFechado.getDataHoraFimResolucao()).isNotNull();
                assertThat(itemFechado.getDuracaoResolucao()).isNotNull();
                assertThat(itemFechado.getKmVeiculoColetadoResolucao()).isNotNull();
            });

            // Valida informações de mapeamento das O.S.s fechadas
            final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = provider.provideDatabaseConnection();
                stmt = conn.prepareStatement("SELECT * " +
                        "FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO " +
                        "WHERE COD_UNIDADE = ? " +
                        "  AND COD_OS_GLOBUS = ?;");
                stmt.setLong(1, COD_UNIDADE);
                stmt.setLong(2, nextCodOs);
                rSet = stmt.executeQuery();

                boolean rSetHasData = false;
                while (rSet.next()) {
                    rSetHasData = true;
                    assertThat(rSet.getLong("COD_CHECKLIST_OS_PROLOG")).isEqualTo(codChecklistInserido);
                    assertThat(rSet.getLong("COD_CONTEXTO_ALTERNATIVA_OS_PROLOG"))
                            .isIn(codsAlaternativasOS);
                    assertThat(rSet.getTimestamp("DATA_HORA_SINCRONIA_RESOLUCAO")).isNotNull();
                }
                assertThat(rSetHasData).isTrue();

            } finally {
                provider.closeResources(conn, stmt, rSet);
            }
        }
    }

    /**
     * Método que testa o incremento da quantidade de tentativas de sincronia do checklist. Valida também se as
     * mensagens são salvas da forma correta.
     * <p>
     * Esse método deve simular a realização de um checklist, tentativa de sincronia, e após isso, validar se as
     * informações estão de acordo com o que deve ser.
     * Para isso o teste deve:
     * 1 - Criar um modelo de checklist para utilizar no teste.
     * 2 - Realizar um checklist utilizando o modelo de checklist criado.
     * 3 - Inserir esse checklist na base do ProLog e nas tabelas de mapeamento.
     * 4 - Simular uma tentativa de sincronia.
     * 5 - Validar se a contagem de tentativas aumentou.
     *
     * @throws Throwable Teste executado apresentou erro
     */
    @Test
    void testIncrementaQtdTentativasSincronia() throws Throwable {
        // ################################### ETAPA 1 - Cria um modelo de checklist ###################################
        final ResultInsertModeloChecklist resultModeloChecklist =
                criaModeloChecklist(COD_UNIDADE, "Modelo Abertura Ordem Serviço Integração ");

        // ################################### ETAPA 2 - Cria um checklist do modelo ###################################
        final ChecklistInsercao checklistInsercao = insertChecklistModeloCriado(COD_UNIDADE, resultModeloChecklist);

        // Configura modelo para ser integrado
        permiteSincronizarModeloChecklist(resultModeloChecklist);

        final Long codChecklistInserido = checklistService.insert(tokenIntegrado, checklistInsercao);

        Thread.sleep(1000);

        Executors.newSingleThreadExecutor().execute(
                new ChecklistItensNokGlobusTask(
                        codChecklistInserido,
                        true,
                        new SistemaGlobusPiccoloturDaoImpl(),
                        new GlobusPiccoloturRequesterImpl(),
                        null));

        Thread.sleep(1000);

        Executors.newSingleThreadExecutor().execute(
                new ChecklistItensNokGlobusTask(
                        codChecklistInserido,
                        true,
                        new SistemaGlobusPiccoloturDaoImpl(),
                        new GlobusPiccoloturRequesterImpl(),
                        null));

        Thread.sleep(1000);

        Executors.newSingleThreadExecutor().execute(
                new ChecklistItensNokGlobusTask(
                        codChecklistInserido,
                        true,
                        new SistemaGlobusPiccoloturDaoImpl(),
                        new GlobusPiccoloturRequesterImpl(),
                        null));
        Thread.sleep(1000);
    }

    private void marcarChecklistComoSincronizado(final Long codChecklistInserido,
                                                 final SistemaGlobusPiccoloturDaoImpl sistemaGlobusPiccoloturDao) throws Throwable {
        // region Atualiza informações do checklist integrado
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            conn = provider.provideDatabaseConnection();
            final ChecklistToSyncGlobus checklistToSyncGlobus =
                    sistemaGlobusPiccoloturDao.getChecklistToSyncGlobus(conn, codChecklistInserido);
            final ChecklistItensNokGlobus checklistItensNokGlobus =
                    checklistToSyncGlobus.getChecklistItensNokGlobus();
            sistemaGlobusPiccoloturDao.insertItensNokEnviadosGlobus(conn, checklistItensNokGlobus);
            sistemaGlobusPiccoloturDao.marcaChecklistSincronizado(conn, codChecklistInserido);
        } finally {
            provider.closeResources(conn);
        }
    }

    @NotNull
    private ChecklistToSyncGlobus getChecklistToSyncGlobus(final Long codChecklistInserido,
                                                           final SistemaGlobusPiccoloturDaoImpl sistemaGlobusPiccoloturDao) throws Throwable {
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        Connection conn = null;
        try {
            conn = provider.provideDatabaseConnection();
            return sistemaGlobusPiccoloturDao.getChecklistToSyncGlobus(conn, codChecklistInserido);
        } finally {
            provider.closeResources(conn);
        }
    }

    @NotNull
    private ResultInsertModeloChecklist criaModeloChecklist(final long codUnidade, final String nomeModeloChecklist) {
        // ################################### ETAPA 1 - Cria um modelo de checklist ###################################
        final List<PerguntaModeloChecklistInsercao> perguntasModelo = new ArrayList<>();
        { // region Criação Pergunta 1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true));

            perguntasModelo.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    alternativas));
        }

        { // region Criação Pergunta 2.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    true));

            perguntasModelo.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    alternativas));
        }

        // Inserimos o modelo de checklist
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                nomeModeloChecklist + RANDOM.nextInt(999),
                codUnidade,
                Arrays.asList(13L, 63L, 64L, 65L),
                Collections.singletonList(951L),
                perguntasModelo);
        return modeloChecklistService.insertModeloChecklist(modelo, tokenIntegrado);
    }

    @NotNull
    private ChecklistInsercao insertChecklistModeloCriado(final long codUnidade,
                                                          final ResultInsertModeloChecklist resultModeloChecklist) {
        final ModeloChecklistVisualizacao modeloBuscado = modeloChecklistService.getModeloChecklist(
                codUnidade,
                resultModeloChecklist.getCodModeloChecklistInserido());

        final List<ChecklistResposta> respostas = new ArrayList<>();
        { // region Responde a P1 - ela É single_choice.

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

        { // region Responde a P2 - ela é multi_choice.
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

        return new ChecklistInsercao(
                codUnidade,
                resultModeloChecklist.getCodModeloChecklistInserido(),
                resultModeloChecklist.getCodVersaoModeloChecklistInserido(),
                2272L,
                705L,
                "PRO0006",
                TipoChecklist.SAIDA,
                11222,
                10000,
                respostas,
                ProLogDateParser.toLocalDateTime("2019-12-11T09:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                80,
                83,
                "device didID",
                "deviceImei",
                10000,
                11000);
    }

    private void permiteSincronizarModeloChecklist(
            @NotNull final ResultInsertModeloChecklist resultModeloChecklist) throws Throwable {
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement(
                    "insert into piccolotur.modelo_checklist_integrado (cod_unidade, cod_modelo_checklist) " +
                            "values (?, ?);");
            stmt.setLong(1, COD_UNIDADE);
            stmt.setLong(2, resultModeloChecklist.getCodModeloChecklistInserido());
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível configurar o modelo para abrir OS integrada.");
            }
        } finally {
            provider.closeResources(conn, stmt);
        }
    }

    private long getNextCodOsUnidade(final long codUnidade) throws Throwable {
        final DatabaseConnectionProvider provider = new DatabaseConnectionProvider();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = provider.provideDatabaseConnection();
            stmt = conn.prepareStatement("SELECT CODIGO " +
                    "FROM CHECKLIST_ORDEM_SERVICO " +
                    "WHERE COD_UNIDADE = ? " +
                    "ORDER BY CODIGO DESC " +
                    "LIMIT 1;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO") + 1;
            } else {
                throw new SQLException("Não foi possível buscar o próximo codigo de O.S");
            }
        } finally {
            provider.closeResources(conn, stmt, rSet);
        }
    }

    @NotNull
    private Long getRandomCodItenGlobus() {
        return RANDOM.nextLong();
    }
}
