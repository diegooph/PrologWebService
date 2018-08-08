package test.frota.checklist;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistConverter;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServicoConverter;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A realização de checklists está crescendo rápido no ProLog. Estamos recebendo aproximadamente 1k de checklists por
 * dia, isso significa um aumento de quase 100k de linhas na tabela CHECKLIST_RESPOSTAS por dia. As buscas para criação
 * da tela de farol e também para a tela de OS se baseiam no uso da mesma VIEW: ESTRATIFICACAO_OS. Essa VIEW faz join
 * com praticamente todas as tabelas de checklists, para poder ser flexível o suficiente e ser utilizada tanto para
 * construção do farol quanto para listagem de OS ou relatórios.
 * <p>
 * Como a maioria das tabelas do checklist não possuem chaves primárias únicas (um AUTOINCREMENT ou BIGSERIAL) os joins
 * ou buscas se tornam lerdos, pois não temos índeces. Quase todas as tabelas possuem chaves compostas que por sua vez
 * são até mesmo compostas por uma coluna BIGSERIAL, o que não faz o menor sentido. Além disso, algumas tabelas também
 * setam o valor dessa coluna BIGSERIAL na mão, fazendo com que tenhamos valores repeditos que não causam problemas pois
 * a PK é composta.
 * <p>
 * Todos esses problemas levaram à funcionalidade de farol do checklist a parar de funcionar, pois além das queries e
 * tabelas estarem mau otimizadas, a própria busca no servidor também estava, fazendo uma consulta com nova connection
 * ao BD para cada placa da unidade e essa busca por sua vez utilizando a ESTRATIFICAO_OS, que faz joins, como já dito,
 * com quase todas as tabelas de checklist.
 * <p>
 * Para resolver esse problema toda a estrutura de chaves das tabelas de checklist foram alteradas, onde possível, as
 * tabelas passaram a ter uma PK única (BIGSERIAL) e onde necessário uma tabela de vínculo, como é o caso da
 * CHECKLIST_RESPOSTAS, o menor número de colunas possível foi utilizado na construção da PK composta.
 * Mesmo com todas essas otimizações a nível de banco, a busca do farol também foi alterada no servidor para utilizar
 * uma function que retorna o status de todas as placas da operação (já com os itens críticos caso existam). Assim,
 * agora apenas uma consulta ao banco de dados é necessária.
 * <p>
 * Validar o funcionamento da nova implementação do farol não é difícil, mas garantir que não quebramos nenhum vínculo
 * entre as tabelas ao realizar essa migração complexa para criação de novas PKs, é muito!
 * Por isso, implementamos esse teste que compara todos os objetos pertinentes numa base antes da migração com os mesmos
 * objetos em uma base após a migração, para garantir que tudo continua sendo criado do modo certo.
 * <p>
 * Created on 06/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@SuppressWarnings("Duplicates")
public class MigracaoPksChecklistTest extends BaseTest {
    private static final String TAG = MigracaoPksChecklistTest.class.getSimpleName();
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String TIMEZONE_DEFAULT = "America/Sao_Paulo";
    private static final String TESTE_URL_PRE = "jdbc:postgresql://localhost:5432/prolog_farol_1";
    private static final String TESTE_USUARIO_PRE = "postgres";
    private static final String TESTE_SENHA_PRE = "postgres";

    private static final String TESTE_URL_POS = "jdbc:postgresql://localhost:5432/prolog_farol_1";
    private static final String TESTE_USUARIO_POS = "postgres";
    private static final String TESTE_SENHA_POS = "postgres";

    private static final int LIMIT = 10000;
    private Connection preMigration;
    private Connection posMigration;

    @Override
    public void initialize() {
        preMigration = createConnection(TESTE_URL_PRE, TESTE_USUARIO_PRE, TESTE_SENHA_PRE);
        posMigration = createConnection(TESTE_URL_POS, TESTE_USUARIO_POS, TESTE_SENHA_POS);
    }

    @Test
    public void testTodosChecklistsIguais() throws Throwable {
        final long totalChecklistsPre = getNumeroTotalChecklist(preMigration);
        final long totalChecklistPos = getNumeroTotalOrdensServico(posMigration);
        assertEquals(totalChecklistsPre, totalChecklistPos);

        long offset = 0;
        while (offset <= totalChecklistPos) {
            final List<Checklist> antes = getTodosChecklistCompletos(preMigration, offset);
            final List<Checklist> depois = getTodosChecklistCompletos(posMigration, offset);
            assertNotNull(antes);
            assertNotNull(depois);
            assertEquals(antes.size(), depois.size());

            for (int i = 0; i < antes.size(); i++) {
                final Checklist pre = antes.get(i);
                final Checklist pos = depois.get(i);
                assertNotNull(pre);
                assertNotNull(pos);

                // Compara atributos simples.
                assertEquals(pre.getCodigo(), pos.getCodigo());
                assertEquals(pre.getPlacaVeiculo(), pos.getPlacaVeiculo());
                assertEquals(pre.getData(), pos.getData());
                assertEquals(pre.getCodModelo(), pos.getCodModelo());
                assertEquals(pre.getKmAtualVeiculo(), pos.getKmAtualVeiculo());
                assertEquals(pre.getTipo(), pos.getTipo());
                assertEquals(pre.getTempoRealizacaoCheckInMillis(), pos.getTempoRealizacaoCheckInMillis());
                assertEquals(pre.getQtdItensOk(), pos.getQtdItensOk());
                assertEquals(pre.getQtdItensNok(), pos.getQtdItensNok());
                assertEquals(pre.getColaborador().getCpf(), pos.getColaborador().getCpf());

                // Compara respostas.
                final List<PerguntaRespostaChecklist> rPre = pre.getListRespostas();
                final List<PerguntaRespostaChecklist> rPos = pos.getListRespostas();
                assertEquals(rPre.size(), rPos.size());
                for (int j = 0; j < rPre.size(); j++) {
                    final PerguntaRespostaChecklist p1 = rPre.get(j);
                    final PerguntaRespostaChecklist p2 = rPos.get(j);
                    assertNotNull(p1);
                    assertNotNull(p2);

                    // Compara atributos da pergunta.
                    assertEquals(p1.getCodigo(), p2.getCodigo());
                    assertEquals(p1.getPergunta(), p2.getPergunta());
                    assertEquals(p1.getPrioridade(), p2.getPrioridade());
                    assertEquals(p1.getCodImagem(), p2.getCodImagem());
                    assertEquals(p1.getUrl(), p2.getUrl());
                    assertEquals(p1.getOrdemExibicao(), p2.getOrdemExibicao());

                    // Alternativas.
                    final List<AlternativaChecklist> listA1 = p1.getAlternativasResposta();
                    final List<AlternativaChecklist> listA2 = p2.getAlternativasResposta();
                    assertNotNull(listA1);
                    assertNotNull(listA2);
                    assertEquals(listA1.size(), listA2.size());

                    for (int k = 0; k < listA1.size(); k++) {
                        final AlternativaChecklist a1 = listA1.get(k);
                        final AlternativaChecklist a2 = listA2.get(k);
                        assertNotNull(a1);
                        assertNotNull(a2);

                        // Atributos da Alternativa.
                        assertEquals(a1.getCodigo(), a2.getCodigo());
                        assertEquals(a1.getAlternativa(), a2.getAlternativa());
                        assertEquals(a1.getTipo(), a2.getTipo());
                        assertEquals(a1.getOrdemExibicao(), a2.getOrdemExibicao());
                        assertEquals(a1.getRespostaOutros(), a2.getRespostaOutros());
                        assertEquals(a1.isSelected(), a2.isSelected());
                    }
                }

            }

            offset += LIMIT;
        }
    }

    @Test
    public void testTodasOrdensServicosIguais() throws Throwable {
        final long totalOrdensServicoPre = getNumeroTotalOrdensServico(preMigration);
        final long totalOrdensServicoPos = getNumeroTotalOrdensServico(posMigration);
        assertEquals(totalOrdensServicoPre, totalOrdensServicoPos);

        long offset = 0;
        while (offset <= totalOrdensServicoPos) {
            final List<OrdemServico> antes = getTodasOrdensServicosCompletas(preMigration, offset);
            final List<OrdemServico> depois = getTodasOrdensServicosCompletas(posMigration, offset);
            assertNotNull(antes);
            assertNotNull(depois);
            assertEquals(antes.size(), depois.size());

            for (int i = 0; i < antes.size(); i++) {
                final OrdemServico o1 = antes.get(i);
                final OrdemServico o2 = depois.get(i);
                assertNotNull(o1);
                assertNotNull(o2);

                // Compara atributos da Ordem de Serviço.
                assertEquals(o1.getCodigo(), o2.getCodigo());
                assertEquals(o1.getCodChecklist(), o2.getCodChecklist());
                assertEquals(o1.getDataAbertura(), o2.getDataAbertura());
                assertEquals(o1.getDataFechamento(), o2.getDataFechamento());
                assertEquals(o1.getStatus(), o2.getStatus());
                assertEquals(o1.getVeiculo().getPlaca(), o2.getVeiculo().getPlaca());

                // Compara os itens da OS.
                final List<ItemOrdemServico> itens1 = o1.getItens();
                final List<ItemOrdemServico> itens2 = o2.getItens();
                assertNotNull(itens1);
                assertNotNull(itens2);
                assertEquals(itens1.size(), itens2.size());

                for (int j = 0; j < itens1.size(); j++) {
                    final ItemOrdemServico i1 = itens1.get(j);
                    final ItemOrdemServico i2 = itens2.get(j);
                    assertNotNull(i1);
                    assertNotNull(i2);

                    assertEquals(i1.getCodOs(), i2.getCodOs());
                    assertEquals(i1.getKmVeiculoFechamento(), i2.getKmVeiculoFechamento());
                    assertEquals(i1.getDataApontamento(), i2.getDataApontamento());
                    assertEquals(i1.getDataHoraConserto(), i2.getDataHoraConserto());
                    assertEquals(i1.getFeedbackResolucao(), i2.getFeedbackResolucao());
                    assertEquals(i1.getCodigo(), i2.getCodigo());
                    assertEquals(i1.getStatus(), i2.getStatus());
                    assertEquals(i1.getPlaca(), i2.getPlaca());
                    assertEquals(i1.getQtdApontamentos(), i2.getQtdApontamentos());
                    assertEquals(i1.getTempoRestante(), i2.getTempoRestante());
                    assertEquals(i1.getTempoLimiteResolucao(), i2.getTempoLimiteResolucao());
                    assertEquals(i1.getTempoRealizacaoConserto(), i2.getTempoRealizacaoConserto());
                    if (i1.getMecanico() != null) {
                        assertEquals(i1.getMecanico().getCpf(), i2.getMecanico().getCpf());
                    } else {
                        assertEquals(i1.getMecanico(), i2.getMecanico());
                    }

                    // Compara a pergunta.
                    final PerguntaRespostaChecklist p1 = i1.getPergunta();
                    final PerguntaRespostaChecklist p2 = i2.getPergunta();
                    assertNotNull(p1);
                    assertNotNull(p2);

                    assertEquals(p1.getCodigo(), p2.getCodigo());
                    assertEquals(p1.getPergunta(), p2.getPergunta());
                    assertEquals(p1.getPrioridade(), p2.getPrioridade());
                    assertEquals(p1.getCodImagem(), p2.getCodImagem());
                    assertEquals(p1.getUrl(), p2.getUrl());
                    assertEquals(p1.getOrdemExibicao(), p2.getOrdemExibicao());

                    // Alternativas.
                    final List<AlternativaChecklist> listA1 = p1.getAlternativasResposta();
                    final List<AlternativaChecklist> listA2 = p2.getAlternativasResposta();
                    assertNotNull(listA1);
                    assertNotNull(listA2);
                    assertEquals(listA1.size(), listA2.size());

                    for (int k = 0; k < listA1.size(); k++) {
                        final AlternativaChecklist a1 = listA1.get(k);
                        final AlternativaChecklist a2 = listA2.get(k);
                        assertNotNull(a1);
                        assertNotNull(a2);

                        // Atributos da Alternativa.
                        assertEquals(a1.getCodigo(), a2.getCodigo());
                        assertEquals(a1.getAlternativa(), a2.getAlternativa());
                        assertEquals(a1.getTipo(), a2.getTipo());
                        assertEquals(a1.getOrdemExibicao(), a2.getOrdemExibicao());
                        assertEquals(a1.getRespostaOutros(), a2.getRespostaOutros());
                        assertEquals(a1.isSelected(), a2.isSelected());
                    }
                }
            }

            offset += LIMIT;
        }
    }

    private long getNumeroTotalChecklist(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT COUNT(CODIGO) AS TOTAL FROM CHECKLIST;");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("TOTAL");
            } else {
                throw new IllegalStateException();
            }
        } finally {
            DatabaseConnection.closeConnection(conn, stmt, rSet);
        }
    }

    private long getNumeroTotalOrdensServico(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT COUNT(CODIGO) AS TOTAL FROM CHECKLIST_ORDEM_SERVICO;");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("TOTAL");
            } else {
                throw new IllegalStateException();
            }
        } finally {
            DatabaseConnection.closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<Checklist> getTodosChecklistCompletos(@NotNull final Connection conn,
                                                       final long offset) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT C.CODIGO, C.DATA_HORA AT TIME ZONE ? AS DATA_HORA, "
                    + "C.cod_checklist_modelo, C.KM_VEICULO, "
                    + "C.TEMPO_REALIZACAO,C.CPF_COLABORADOR, C.PLACA_VEICULO, "
                    + "C.TIPO, CO.NOME FROM CHECKLIST C "
                    + "JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR "
                    + "JOIN EQUIPE E ON E.CODIGO = CO.COD_EQUIPE "
                    + "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO "
                    + "ORDER BY DATA_HORA DESC "
                    + "LIMIT ? OFFSET ?");
            stmt.setString(1, TIMEZONE_DEFAULT);
            stmt.setInt(2, LIMIT);
            stmt.setLong(3, offset);
            rSet = stmt.executeQuery();
            final List<Checklist> checklists = new ArrayList<>();
            if (rSet.next()) {
                do {
                    checklists.add(ChecklistConverter.createChecklist(rSet));
                } while (rSet.next());
            } else {
                throw new IllegalStateException();
            }
            return checklists;
        } finally {
            DatabaseConnection.closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private List<OrdemServico> getTodasOrdensServicosCompletas(@NotNull final Connection conn,
                                                               final long offset) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "COS.CODIGO AS COD_OS, " +
                    "COS.COD_CHECKLIST, " +
                    "COS.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                    "COS.STATUS, " +
                    "C.PLACA_VEICULO, " +
                    "V.KM, " +
                    "C.DATA_HORA AT TIME ZONE ? AS DATA_HORA " +
                    "FROM CHECKLIST_ORDEM_SERVICO COS JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO " +
                    "AND C.COD_UNIDADE = COS.COD_UNIDADE " +
                    "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO " +
                    "JOIN VEICULO_TIPO VT ON VT.COD_UNIDADE = C.COD_UNIDADE AND V.COD_TIPO = VT.CODIGO " +
                    "ORDER BY COS.CODIGO DESC " +
                    "LIMIT ? OFFSET ?;");
            stmt.setString(1, TIMEZONE_DEFAULT);
            stmt.setString(2, TIMEZONE_DEFAULT);
            stmt.setInt(3, LIMIT);
            stmt.setLong(4, offset);
            rSet = stmt.executeQuery();
            final List<OrdemServico> checklists = new ArrayList<>();
            if (rSet.next()) {
                do {
                    checklists.add(OrdemServicoConverter.createOrdemServicoSemItens(rSet));
                } while (rSet.next());
            } else {
                throw new IllegalStateException();
            }
            return checklists;
        } finally {
            DatabaseConnection.closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private Connection createConnection(@NotNull final String url,
                                        @NotNull final String usuario,
                                        @NotNull final String senha) {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(url, usuario, senha);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao abrir conexão com o banco: %s", url), throwable);
            throw new RuntimeException(throwable);
        }
    }
}