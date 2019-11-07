package br.com.zalf.prolog.webservice.implantacao.autenticacao;

import org.jetbrains.annotations.NotNull;
import sun.misc.BASE64Decoder;

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
public final class ImplantacaoLoginSenhaDaoImpl implements ImplantacaoLoginSenhaDao {

    @Override
    public String verifyUsernamePassword(@NotNull String usernamePassword) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {

            String[] splitUsernamePassword = new String(new BASE64Decoder().decodeBuffer(usernamePassword)).split(":", 0);

            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM IMPLANTACAO.FUNC_VERIFICA_LOGIN_SENHA(" +
                    "F_LOGIN := ?," +
                    "F_SENHA := ?);");
            stmt.setString(1, splitUsernamePassword[0]);
            stmt.setString(2, splitUsernamePassword[1]);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("USERNAME");
            } else {
                throw new IllegalStateException("Não foi possível retornar o código da aferição realizada");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
