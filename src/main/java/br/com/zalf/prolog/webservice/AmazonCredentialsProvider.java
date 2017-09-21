package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.colaborador.model.AmazonCredentials;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;

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
                throw new AmazonCredentialsException();
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}