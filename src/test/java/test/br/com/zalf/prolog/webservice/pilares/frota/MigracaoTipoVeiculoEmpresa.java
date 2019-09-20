package test.br.com.zalf.prolog.webservice.pilares.frota;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Teste para validar a migração dos tipos de veículos que antes eram cadastrados por Unidade para agora serem a nível
 * de Empresa.
 * <p>
 * Esse teste deve garantir que nenhum tipo de veículo se perdeu no processo de migração de dados.
 * <p>
 * Buscamos fazer essa validação utilizando tabelas de backup e comparações com os dados após a migraçõa ser rodada.
 * <p>
 * A tabela <code>VEICULO_TIPO_BACKUP</code> contém as informações pré migração. A estrutura dessa tabela é a antiga
 * onde os dados são referentes à Unidade.
 * <p>
 * A tabela <code>VEICULO_TIPO</code> contém os dados pós migração. Essa passa a ser a tabela oficial para a utilização
 * no ProLog. Ela possui seus dados já a nível de Empresa.
 *
 * <i>Sobre o Processo de validação:</i>
 * <p>
 * Para criar a migração de tipos de veículos foi necessária uma extensa análise, onde, para cada Unidade de cada
 * Empresa avaliamos se havia tipos de veículos iguais ou nomes idênticos que remetiam ao mesmo tipo.
 * Para os casos em que os nomes eram iguais foi necessário apenas a alteração do código da Unidade para o código da
 * Empresa e um código único para a identificação daquele tipo dentro da empresa.
 * Por outro lado, os casos em que Unidades possuiam tipos de veículos semelhantes porém com nomes diferentes, foi
 * necessário, além da alteração do código de Unidade para codígo de Empresa e do código único de identificação, a
 * alteração do nome do tipo de veículo.
 * <p>
 * Os casos em que a alteração de nome fez-se necessário estão mapeados na estrutura de mapeamento dentro do teste,
 * chamada {@code TIPOS_TROCARAM_DE_NOME}. Ela vincula o código do tipo de veículo que trocou de nome e a qual empresa
 * ele pertence, respectivamente.
 * <p>
 * O método de validação garante que um tipo de veículo que se manteve na nova tabela, tem os mesmos parâmetros de
 * nome, diagrama e status do tipo de mesmo código na tabela antiga.
 * <p>
 * Created on 18/02/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class MigracaoTipoVeiculoEmpresa {
    // CodTipo e CodEmpresa, respectivamente.
    private static final Map<Long, Long> TIPOS_TROCARAM_DE_NOME;

    static {
        TIPOS_TROCARAM_DE_NOME = new HashMap<>();
        TIPOS_TROCARAM_DE_NOME.put(19L, 4L);
        TIPOS_TROCARAM_DE_NOME.put(24L, 4L);
        TIPOS_TROCARAM_DE_NOME.put(40L, 7L);
        TIPOS_TROCARAM_DE_NOME.put(77L, 9L);
        TIPOS_TROCARAM_DE_NOME.put(78L, 9L);
        TIPOS_TROCARAM_DE_NOME.put(230L, 6L);
        TIPOS_TROCARAM_DE_NOME.put(349L, 7L);
        TIPOS_TROCARAM_DE_NOME.put(358L, 7L);
        TIPOS_TROCARAM_DE_NOME.put(376L, 22L);
        TIPOS_TROCARAM_DE_NOME.put(377L, 22L);
        TIPOS_TROCARAM_DE_NOME.put(6L, 2L);
        TIPOS_TROCARAM_DE_NOME.put(18L, 1L);
        TIPOS_TROCARAM_DE_NOME.put(31L, 5L);
        TIPOS_TROCARAM_DE_NOME.put(35L, 5L);
        TIPOS_TROCARAM_DE_NOME.put(83L, 9L);
        TIPOS_TROCARAM_DE_NOME.put(98L, 9L);
        TIPOS_TROCARAM_DE_NOME.put(100L, 1L);
        TIPOS_TROCARAM_DE_NOME.put(182L, 1L);
        TIPOS_TROCARAM_DE_NOME.put(217L, 1L);
        TIPOS_TROCARAM_DE_NOME.put(224L, 9L);
        TIPOS_TROCARAM_DE_NOME.put(236L, 6L);
        TIPOS_TROCARAM_DE_NOME.put(237L, 6L);
        TIPOS_TROCARAM_DE_NOME.put(255L, 6L);
        TIPOS_TROCARAM_DE_NOME.put(268L, 1L);
        TIPOS_TROCARAM_DE_NOME.put(277L, 6L);
        TIPOS_TROCARAM_DE_NOME.put(352L, 20L);
        TIPOS_TROCARAM_DE_NOME.put(455L, 10L);
    }

    @Before
    public void initialize() {
        DatabaseManager.init();
    }

    @After
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void testMigracaoTiposVeiculos() throws Throwable {
        final Connection connection = DatabaseConnection.getConnection();
        final Map<Long, TipoVeiculoTest> tiposAntesMigration = getTiposAntesMigration(connection);
        final Map<Long, TipoVeiculoTest> tiposDepoisMigration = getTiposDepoisMigration(connection);

        try {
            assertNotNull(tiposAntesMigration);

            assertNotNull(tiposDepoisMigration);
            assertTrue(tiposAntesMigration.size() > 0);
            assertTrue(tiposDepoisMigration.size() > 0);

            // Alguns tipos foram deletados pois existiam em mais de uma unidade.
            assertNotEquals(tiposAntesMigration.size(), tiposDepoisMigration.size());

            tiposDepoisMigration.forEach((codTipo, depois) -> {
                final TipoVeiculoTest antes = tiposAntesMigration.get(codTipo);

                assertNotNull("O código se perdeu na migração, COD: " + codTipo, antes);

                assertEquals("COD: " + depois.getCodigo(), antes.getCodigo(), depois.getCodigo());
                assertEquals("COD: " + depois.getCodigo(), antes.getCodEmpresa(), depois.getCodEmpresa());
                final Long codEmpresaTrocaNome = TIPOS_TROCARAM_DE_NOME.get(depois.getCodigo());
                if (codEmpresaTrocaNome != null && codEmpresaTrocaNome.equals(depois.getCodEmpresa())) {
                    System.out.println("Tipo "
                            + depois.getCodigo()
                            + " da Empresa "
                            + depois.getCodEmpresa()
                            + " trocou de nome e não foi verificado.\n"
                            + "  " + antes.getNome() + " --> " + depois.getNome() + "\n\n");
                } else {
                    assertEquals("COD: " + depois.getCodigo() + " COD EMPRESA: " + depois.getCodEmpresa() , antes.getNome(), depois.getNome());
                }
                assertEquals("COD: " + depois.getCodigo(), antes.isStatusAtivo(), depois.isStatusAtivo());
                assertEquals("COD: " + depois.getCodigo(), antes.getCodDiagrama(), depois.getCodDiagrama());
            });
        } finally {
            DatabaseConnection.close(connection);
        }
    }

    @NotNull
    private Map<Long, TipoVeiculoTest> getTiposAntesMigration(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "  VTB.CODIGO AS CODIGO, " +
                    "  U.COD_EMPRESA AS COD_EMPRESA, " +
                    "  VTB.NOME AS NOME, " +
                    "  VTB.STATUS_ATIVO AS STATUS_ATIVO, " +
                    "  VTB.COD_DIAGRAMA AS COD_DIAGRAMA " +
                    "FROM VEICULO_TIPO_BACKUP VTB " +
                    "  JOIN UNIDADE U ON VTB.COD_UNIDADE = U.CODIGO;");
            rSet = stmt.executeQuery();
            final Map<Long, TipoVeiculoTest> tipos = new HashMap<>();
            while (rSet.next()) {
                tipos.put(
                        rSet.getLong("CODIGO"),
                        new TipoVeiculoTest(
                                rSet.getLong("CODIGO"),
                                rSet.getLong("COD_EMPRESA"),
                                rSet.getString("NOME"),
                                rSet.getBoolean("STATUS_ATIVO"),
                                rSet.getLong("COD_DIAGRAMA")));
            }
            return tipos;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private Map<Long, TipoVeiculoTest> getTiposDepoisMigration(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM VEICULO_TIPO;");
            rSet = stmt.executeQuery();
            final Map<Long, TipoVeiculoTest> tipos = new HashMap<>();
            while (rSet.next()) {
                tipos.put(
                        rSet.getLong("CODIGO"),
                        new TipoVeiculoTest(
                                rSet.getLong("CODIGO"),
                                rSet.getLong("COD_EMPRESA"),
                                rSet.getString("NOME"),
                                rSet.getBoolean("STATUS_ATIVO"),
                                rSet.getLong("COD_DIAGRAMA")));
            }
            return tipos;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private final class TipoVeiculoTest {
        private final Long codigo;
        private final Long codEmpresa;
        private final String nome;
        private final boolean statusAtivo;
        private final Long codDiagrama;

        TipoVeiculoTest(final Long codigo,
                        final Long codEmpresa,
                        final String nome,
                        final boolean statusAtivo,
                        final Long codDiagrama) {
            this.codigo = codigo;
            this.codEmpresa = codEmpresa;
            this.nome = nome;
            this.statusAtivo = statusAtivo;
            this.codDiagrama = codDiagrama;
        }

        public Long getCodigo() {
            return codigo;
        }

        public Long getCodEmpresa() {
            return codEmpresa;
        }

        public String getNome() {
            return nome;
        }

        public boolean isStatusAtivo() {
            return statusAtivo;
        }

        public Long getCodDiagrama() {
            return codDiagrama;
        }

        @Override
        public String toString() {
            return "TipoVeiculoTest{" +
                    "codigo=" + codigo +
                    ", codEmpresa=" + codEmpresa +
                    ", nome='" + nome + '\'' +
                    ", statusAtivo=" + statusAtivo +
                    ", codDiagrama=" + codDiagrama +
                    '}';
        }
    }
}