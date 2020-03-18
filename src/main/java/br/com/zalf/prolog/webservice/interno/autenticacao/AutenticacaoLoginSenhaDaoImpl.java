package br.com.zalf.prolog.webservice.interno.autenticacao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        final ResultSet rSet = null;

        try {
            final String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

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
    public void verifyUsernamePassword(@NotNull final PrologInternalUser internalUser) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTERNO.FUNC_BUSCA_DADOS_USUARIO(" +
                    "F_USERNAME := ?);");
            stmt.setString(1, internalUser.getUsername());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!internalUser.doesPasswordMatch(rSet.getString("PASSWORD"))) {
                    throw new NotAuthorizedException("Senha inválida");
                }
            } else {
                throw new NotAuthorizedException("Usuário e senha não encontrados");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}