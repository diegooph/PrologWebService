package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import com.google.common.annotations.VisibleForTesting;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created on 25/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final String DRIVER = "org.postgresql.Driver";

    // Informações de acesso ao BD oficial
    private static final String USUARIO = EnvironmentHelper.PROLOG_RDS_USERNAME;
    private static final String SENHA = EnvironmentHelper.PROLOG_RDS_PASSWORD;
    private static final String URL = "jdbc:postgresql://"
            + EnvironmentHelper.PROLOG_RDS_HOSTNAME
            + ":"
            + EnvironmentHelper.PROLOG_RDS_PORT
            + "/"
            + EnvironmentHelper.PROLOG_RDS_DB_NAME;

    // Informações de acesso ao BD de testes
//    private static final String TESTE_URL = "jdbc:postgresql://192.168.0.180:5432/prolog_ajuste_marcacao_4";
//    private static final String TESTE_URL = "jdbc:postgresql://192.168.0.180:5432/prolog_delecao_checklist_1";
//    private static final String TESTE_URL = "jdbc:postgresql://0.tcp.ngrok.io:16464/prolog_integracao_piccolotur_globus_1";
//    private static final String TESTE_URL = "jdbc:postgresql://192.168.0.180:5432/prolog_integracao_rodalog_2";
//    private static final String TESTE_URL = "jdbc:postgresql://prolog-testes.c9sc1w2qsese.us-east-1.rds.amazonaws.com/prolog_database_julho";
//    private static final String TESTE_URL = "jdbc:postgresql://prolog-db-instance-prod.c9sc1w2qsese.us-east-1.rds.amazonaws.com/prolog_database_julho";
//    private static final String TESTE_URL = "jdbc:postgresql://testes-prolog.c9sc1w2qsese.us-east-1.rds.amazonaws.com/prolog_database_julho";
//    private static final String TESTE_USUARIO = "prolog_user_wellington";
//    private static final String TESTE_SENHA = "Zalfsistemas123";
    private static final String TESTE_URL = "jdbc:postgresql://teste-prolog-15.c9sc1w2qsese.us-east-1.rds.amazonaws.com:5432/prolog_database_julho";
    private static final String TESTE_USUARIO = "prolog_user_gustavo";
    private static final String TESTE_SENHA = "aferequevai";

    private static DatabaseManager singleton;
    @NotNull
    private final DataSource dataSource;

    private DatabaseManager(@NotNull final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @VisibleForTesting
    public static void init() {
        if (singleton == null) {
            final PoolProperties poolProperties = getPoolProperties();
            singleton = new DatabaseManager(new org.apache.tomcat.jdbc.pool.DataSource(poolProperties));
        } else {
            throw new IllegalStateException("You cannot init an already initialized manager");
        }
        Log.d(TAG, "DatabaseManager initialized");
    }

    @VisibleForTesting
    public static void finish() {
        if (singleton == null) {
            throw new IllegalStateException("You cannot finish a not initialized manager");
        }
        ((org.apache.tomcat.jdbc.pool.DataSource) singleton.dataSource).close();
        final Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                Log.d(TAG, "JDBC driver (" + driver.toString() + ") successfully deregistered");
            } catch (final SQLException ex) {
                Log.e(TAG, "Error when deregistering JDBC driver (" + driver.toString() + ")", ex);
            }
        }
        // Prevent any access to this instance.
        singleton = null;
        Log.d(TAG, "DatabaseManager finished");
    }

    public static DatabaseManager getInstance() {
        if (singleton == null) {
            throw new IllegalStateException("You should call init(DataSource) first!");
        }

        return singleton;
    }

    @NotNull
    private static PoolProperties getPoolProperties() {
        final PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(DRIVER);
        poolProperties.setUrl(BuildConfig.DEBUG ? TESTE_URL : URL);
        poolProperties.setUsername(BuildConfig.DEBUG ? TESTE_USUARIO : USUARIO);
        poolProperties.setPassword(BuildConfig.DEBUG ? TESTE_SENHA : SENHA);
        poolProperties.setInitSQL("SET application_name = 'ProLog WS'");
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(false);
        poolProperties.setTestOnReturn(false);
        poolProperties.setMaxActive(250);
        poolProperties.setInitialSize(25 /* 10% of maxActive */);
        poolProperties.setMinIdle(25 /* 10% of maxActive */);
        poolProperties.setMaxIdle(125 /* maxActive / 2 */);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxWait(10000);
        // Tempo em segundos até uma connection ser bruscamente encerrada.
        poolProperties.setRemoveAbandonedTimeout(60 * 6);
        // The number of milliseconds a connection must be idle to be eligible for eviction.
        poolProperties.setMinEvictableIdleTimeMillis(60000);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);

        // Usando o autoCommit default em true o interceptor ConnectionState, a conexão sempre será setada com
        // autoCommit true ao ser emprestada da pool. Do contrário o autoCommit seria setado apenas quando a conexão
        // fosse estabelecida.
        poolProperties.setDefaultAutoCommit(true);
        poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        return poolProperties;
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}