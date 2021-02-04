package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@Testcontainers
public class PostgresTest {
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");
    private final Connection conn = getConnection();

    @Test
    public void testPostgresContainerRunning() throws Exception {
        final ResultSet resultSet = conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();
        final int result = resultSet.getInt(1);

        assertEquals(1, result);
    }

    @Test
    public void testFlywayMigration() throws Exception {
        Flyway.configure()
                .dataSource(postgresContainer.getJdbcUrl(),
                            postgresContainer.getUsername(),
                            postgresContainer.getPassword())
                .load()
                .migrate();

        final ResultSet resultSet = conn.createStatement().executeQuery("SELECT * " +
                                                                                "FROM CUSTOMERS " +
                                                                                "WHERE ID = 1");
        resultSet.next();
        final int result = resultSet.getInt(1);

        assertEquals(1, result);
    }

    @NotNull
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(postgresContainer.getJdbcUrl(),
                                               postgresContainer.getUsername(),
                                               postgresContainer.getPassword());
        } catch (final SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
