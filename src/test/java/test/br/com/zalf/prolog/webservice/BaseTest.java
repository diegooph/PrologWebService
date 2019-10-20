package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class BaseTest {
    protected static Long COD_UNIDADE = 5L;
    protected static String USER_TOKEN = "TOKEN";
    protected static LocalDate DATA_INICIAL = LocalDate.parse("2018-03-18");
    protected static LocalDate DATA_FINAL = LocalDate.parse("2018-04-19");

    @BeforeAll
    public void initialize() throws Throwable {
        // Do nothing.
    }

    @AfterAll
    public void destroy() {
        // Do nothing.
    }

    @NotNull
    protected String getValidToken(@NotNull final String cpf) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement("SELECT TOKEN FROM TOKEN_AUTENTICACAO " +
                    "WHERE CPF_COLABORADOR = ? " +
                    "ORDER BY DATA_HORA DESC " +
                    "LIMIT 1;");
            stmt.setLong(1, Long.parseLong(cpf));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("TOKEN");
            } else {
                throw new SQLException("Nenhum token encontrado para o cpf: " + cpf);
            }
        } finally {
            DatabaseConnection.closeConnection(conn, stmt, rSet);
        }
    }
}