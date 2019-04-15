package br.com.zalf.prolog.webservice.database;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

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
//    private static final String TESTE_URL = "jdbc:postgresql://192.168.0.180:5432/prolog_integracao_transport_1";
    private static final String TESTE_URL = "jdbc:postgresql://0.tcp.ngrok.io:15544/prolog_integracao_transport_1";
//    private static final String TESTE_URL = "jdbc:postgresql://localhost:5432/bd_local";
    private static final String TESTE_USUARIO = "postgres";
    private static final String TESTE_SENHA = "postgres";

    public static Connection getConnection() {
        Connection conexao = null;
        try {
            Class.forName(DRIVER);
//			conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            conexao = DriverManager.getConnection(TESTE_URL, TESTE_USUARIO, TESTE_SENHA);
        } catch(Exception e) {
            Log.e(TAG, String.format("Erro ao abrir conexão com o banco: %s", URL), e);
        }
        return conexao;
    }
}