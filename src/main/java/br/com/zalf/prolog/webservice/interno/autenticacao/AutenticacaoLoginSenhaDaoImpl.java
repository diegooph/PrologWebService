package br.com.zalf.prolog.webservice.interno.autenticacao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import org.jetbrains.annotations.NotNull;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class AutenticacaoLoginSenhaDaoImpl implements AutenticacaoLoginSenhaDao {


    @Override
    public void createUsernamePassword(@NotNull final String userName,
                                       @NotNull final String password) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTERNO.FUNC_CREATE_LOGIN_SENHA(F_USERNAME := ?, " +
                    "F_PASSWORD := ?);");
            stmt.setString(1, userName);
            stmt.setString(2, encryptedPassword);
            stmt.execute();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public String verifyUsernamePassword(@NotNull String authorization) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            String encrypted[] = authorization.split(" ");
            String[] splitUsernamePassword =
                    new String(new BASE64Decoder().decodeBuffer(encrypted[1])).split(":");

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTERNO.FUNC_VERIFICA_LOGIN_SENHA(" +
                    "F_USERNAME := ?);");
            stmt.setString(1, splitUsernamePassword[0]);

            rSet = stmt.executeQuery();

            if (rSet.next()) {
                BCrypt.Result result = BCrypt.verifyer().verify(splitUsernamePassword[1].toCharArray(),
                        rSet.getString("PASSWORD"));
                if (result.verified) {
                    return rSet.getString("USERNAME");
                } else {
                    throw new NotAuthorizedException("Senha inválida");
                }
            } else {
                throw new NotAuthorizedException("Usuário e senha não encontrados");
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Erro ao verificar usuário a senha", ex);
        } finally {
            close(conn, stmt, rSet);
        }
    }
}