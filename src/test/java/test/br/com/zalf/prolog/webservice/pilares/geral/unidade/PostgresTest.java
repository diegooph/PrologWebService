package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

@Testcontainers
public class PostgresTest {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    @Test
    public void testPostgresContainerRunning()
            throws Exception {
        final String jdbcUrl = postgresContainer.getJdbcUrl();
        final String username = postgresContainer.getUsername();
        final String password = postgresContainer.getPassword();
        final Connection conn = DriverManager
                .getConnection(jdbcUrl, username, password);
        final ResultSet resultSet =
                conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();
        final int result = resultSet.getInt(1);

        assertEquals(1, result);
    }
}
