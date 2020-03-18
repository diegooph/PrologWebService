package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.gente.colaborador.model.AmazonCredentials;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;
import br.com.zalf.prolog.webservice.errorhandling.error.ProLogErrorCodes;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AmazonCredentialsProvider extends DatabaseConnection {

    public AmazonCredentials getAmazonCredentials() throws SQLException, AmazonCredentialsException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AMAZON_CREDENTIALS");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final AmazonCredentials amazonCredentials = new AmazonCredentials();
                amazonCredentials.setAccessKeyId(rSet.getString("ACCESS_KEY_ID"));
                amazonCredentials.setSecretAccessKey(rSet.getString("SECRET_KEY"));
                amazonCredentials.setUser(rSet.getString("USER_ID"));
                return amazonCredentials;
            } else {
                throw new AmazonCredentialsException(
                        Response.Status.NOT_FOUND.getStatusCode(),
                        ProLogErrorCodes.AMAZON_CREDENTIALS.errorCode(),
                        "Sem credencial cadastrada",
                        "Tabela AMAZON_CREDENTIALS não possui dados");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}