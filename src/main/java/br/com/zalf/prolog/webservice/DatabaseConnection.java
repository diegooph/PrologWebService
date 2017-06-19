package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.commons.util.L;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe responsável por conter os métodos de criar e fechar o banco de dados
 *
 * @version 1.0
 * @since 5 de dez de 2015 11:42:13
 * @author Luiz Felipe <luiz.felipe_95@hotmail.com>
 */
public class DatabaseConnection {
    private static final String TAG = DatabaseConnection.class.getSimpleName();

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
//	private static final String TESTE_URL = "jdbc:postgresql://"
//			+ "prolog-db-instance.csg59phgb0xp.sa-east-1.rds.amazonaws.com:"
//			+ "5432/prolog_database_testes_janeiro";
	private static final String TESTE_URL = "jdbc:postgresql://192.168.15.11:5432/postgresMaio";
	private static final String TESTE_USUARIO = "postgres";
	private static final String TESTE_SENHA = "postgres";

    /**
	 * Método responsável por criar conexão com o banco
	 * 
	 * @return Connection
	 * @version 1.0
	 * @since 5 de dez de 2015 11:42:04
	 * @author Luiz Felipe
	 */
	public static Connection getConnection() {
		Connection conexao = null;
		try {
			Class.forName(DRIVER);
			conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
		} catch(Exception e) {
		    L.e(TAG, String.format("Erro ao abrir conexão com o banco: %s", URL), e);
		}
		return conexao;
	}
	
	/**
	 * Método responsável por fechar a conexão com o banco
	 * 
	 * @return void
	 * @version 1.0
	 * @since 5 de dez de 2015 11:42:22
	 * @author Luiz Felipe
	 */
	public static void closeConnection(Connection conn, PreparedStatement stmt, ResultSet rSet) {
		try {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (rSet != null) {
				rSet.close();
			}
		} catch(Exception e) {
            L.e(TAG, String.format("Erro ao fechar conexão com o banco: %s", URL), e);
		}
	}
}