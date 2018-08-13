package test;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 09/08/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class OperacoesNaoPermitidasViewsTest extends BaseTest {
    private static final String TAG = OperacoesNaoPermitidasViewsTest.class.getSimpleName();
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String TESTE_URL = "jdbc:postgresql://192.168.1.180:5432/prolog_mov_julho_2";
    private static final String TESTE_USUARIO = "postgres";
    private static final String TESTE_SENHA = "postgres";
    private static final String TARGET_NAME = "DaoImpl.java";
    private static final String TARGET_REPO = System.getProperty("user.dir");
    private static final int LINES_AHEAD_TO_SEARCH = 3;

    private Connection connection;
    private List<String> viewNames;

    @Override
    public void initialize() {
        connection = createConnection();
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection(connection);
    }

    @Test
    public void testSqlViewValidation() throws SQLException, IOException {
        viewNames = getViewNames();
        for (final String viewName : viewNames) {
            walk(TARGET_REPO, viewName);
        }
    }

    private void walk(@NotNull final String path, @NotNull final String viewName) throws IOException {
        final File root = new File(path);
        final File[] list = root.listFiles();

        if (list == null) {
            return;
        }

        for (File file : list) {
            if (file.isDirectory()) {
                walk(file.getAbsolutePath(), viewName);
            } else {
                if (file.getPath().contains(TARGET_NAME)) {
                    readAllLines(file, viewName);
                }
            }
        }
    }

    private void readAllLines(@NotNull final File file, @NotNull final String viewName) throws IOException {
        final List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).startsWith("//") && lines.get(i).contains(viewName)) {
                for (int j = 0; j < lines.size(); j++) {
                    if (lines.get(j).toUpperCase().contains("UPDATE") || lines.get(j).toUpperCase().contains("INSERT")) {
                        Assert.assertFalse(
                                "Erro encontrado no arquivo '" + file.getName() + "' na linha: " + (j + 1) + ""
                                        + "\nNão é possível atualizar/inserir uma view",
                                i >= j && (j + LINES_AHEAD_TO_SEARCH) >= i);
                    }
                }
            }
        }
    }

    @NotNull
    private List<String> getViewNames() throws SQLException {
        final List<String> viewNames = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement(
                    "SELECT TABLE_NAME " +
                            "FROM INFORMATION_SCHEMA.VIEWS " +
                            "WHERE TABLE_SCHEMA = ANY(CURRENT_SCHEMAS(FALSE));");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                viewNames.add(rSet.getString("TABLE_NAME"));
            }
        } finally {
            DatabaseConnection.closeConnection(null, stmt, rSet);
        }
        return viewNames;
    }

    @NotNull
    private Connection createConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(TESTE_URL, TESTE_USUARIO, TESTE_SENHA);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao abrir conexão com o banco: %s", TESTE_URL), throwable);
            throw new RuntimeException(throwable);
        }
    }
}
