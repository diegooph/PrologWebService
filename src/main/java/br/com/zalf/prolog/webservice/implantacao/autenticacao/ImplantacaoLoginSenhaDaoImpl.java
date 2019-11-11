package br.com.zalf.prolog.webservice.implantacao.autenticacao;

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
public final class ImplantacaoLoginSenhaDaoImpl implements ImplantacaoLoginSenhaDao {

    @Override
    public String verifyUsernamePassword(@NotNull String authorization) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            String encrypted[] = authorization.split(" ");
            String[] splitUsernamePassword = new String(new BASE64Decoder().decodeBuffer(encrypted[1])).split(":");

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_VERIFICA_LOGIN_SENHA(" +
                    "F_USERNAME := ?," +
                    "F_PASSWORD := ?);");
            stmt.setString(1, splitUsernamePassword[0]);
            stmt.setString(2, splitUsernamePassword[1]);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("USERNAME");
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