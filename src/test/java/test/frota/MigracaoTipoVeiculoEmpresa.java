package test.frota;

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
 * Created on 18/02/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class MigracaoTipoVeiculoEmpresa {
    private static final String TAG = MigracaoTipoVeiculoEmpresa.class.getSimpleName();
    // CodTipo e CodUnidade, respectivamente.
    private static final Map<Long, Long> TIPOS_TROCARAM_DE_NOME;

    static {
        TIPOS_TROCARAM_DE_NOME = new HashMap<>();
        TIPOS_TROCARAM_DE_NOME.put(19L, 7L);
        TIPOS_TROCARAM_DE_NOME.put(24L, 7L);
        TIPOS_TROCARAM_DE_NOME.put(40L, 16L);
        TIPOS_TROCARAM_DE_NOME.put(77L, 23L);
        TIPOS_TROCARAM_DE_NOME.put(78L, 23L);
        TIPOS_TROCARAM_DE_NOME.put(230L, 73L);
        TIPOS_TROCARAM_DE_NOME.put(349L, 97L);
        TIPOS_TROCARAM_DE_NOME.put(358L, 99L);
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

        assertNotNull(tiposAntesMigration);
        assertNotNull(tiposDepoisMigration);
        assertTrue(tiposAntesMigration.size() > 0);
        assertTrue(tiposDepoisMigration.size() > 0);

        // Alguns tipos foram deletados pois existiam em mais de uma unidade
        assertNotEquals(tiposAntesMigration.size(), tiposDepoisMigration.size());

        tiposDepoisMigration.forEach((codTipo, depois) -> {
            final TipoVeiculoTest antes = tiposAntesMigration.get(codTipo);
            // Verifica se tipo existe, pode ter sido deletado.
            if (antes != null) {
                assertEquals("COD: " + depois.getCodigo(), antes.getCodigo(), depois.getCodigo());
                assertEquals("COD: " + depois.getCodigo(), antes.getCodUnidade(), depois.getCodUnidade());
                final Long codUnidadeTrocaNome = TIPOS_TROCARAM_DE_NOME.get(depois.getCodigo());
                if (codUnidadeTrocaNome != null && codUnidadeTrocaNome.equals(depois.getCodUnidade())) {
                    System.out.println("Tipo "
                            + depois.getCodigo()
                            + " da Unidade "
                            + depois.getCodUnidade()
                            + " trocou de nome e não foi verificado.\n"
                            + "  " + antes.getNome() + " --> " + depois.getNome() + "\n\n");
                } else {
                    assertEquals("COD: " + depois.getCodigo(), antes.getNome(), depois.getNome());
                }
                assertEquals("COD: " + depois.getCodigo(), antes.isStatusAtivo(), depois.isStatusAtivo());
                assertEquals("COD: " + depois.getCodigo(), antes.getCodDiagrama(), depois.getCodDiagrama());
            }
        });
    }

    @NotNull
    private Map<Long, TipoVeiculoTest> getTiposAntesMigration(@NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM VEICULO_TIPO_BACKUP;");
            rSet = stmt.executeQuery();
            final Map<Long, TipoVeiculoTest> tipos = new HashMap<>();
            while (rSet.next()) {
                tipos.put(
                        rSet.getLong("CODIGO"),
                        new TipoVeiculoTest(
                                rSet.getLong("CODIGO"),
                                rSet.getLong("COD_UNIDADE"),
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
                                rSet.getLong("COD_UNIDADE"),
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
        private final Long codUnidade;
        private final String nome;
        private final boolean statusAtivo;
        private final Long codDiagrama;

        TipoVeiculoTest(final Long codigo,
                        final Long codUnidade,
                        final String nome,
                        final boolean statusAtivo,
                        final Long codDiagrama) {
            this.codigo = codigo;
            this.codUnidade = codUnidade;
            this.nome = nome;
            this.statusAtivo = statusAtivo;
            this.codDiagrama = codDiagrama;
        }

        public Long getCodigo() {
            return codigo;
        }

        public Long getCodUnidade() {
            return codUnidade;
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
                    ", codUnidade=" + codUnidade +
                    ", nome='" + nome + '\'' +
                    ", statusAtivo=" + statusAtivo +
                    ", codDiagrama=" + codDiagrama +
                    '}';
        }
    }
}