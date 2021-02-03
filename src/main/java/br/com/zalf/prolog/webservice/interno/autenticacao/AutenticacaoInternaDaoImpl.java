package br.com.zalf.prolog.webservice.interno.autenticacao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class AutenticacaoInternaDaoImpl implements AutenticacaoInternaDao {

    @Override
    public void createUsernamePassword(@NotNull final String userName,
                                       @NotNull final String password) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            final String encryptedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            conn = getConnection();
            stmt = conn.prepareStatement("select * from interno.func_create_login_senha(f_username => ?, " +
                    "f_password => ?);");
            stmt.setString(1, userName);
            stmt.setString(2, encryptedPassword);
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    @NotNull
    public Optional<PrologInternalUser> getPrologInternalUserByUsername(@NotNull final String username)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from interno.func_usuario_busca_dados_by_username(" +
                    "f_username => ?);");
            stmt.setString(1, username);
            rSet = stmt.executeQuery();
            return AutenticacaoInternaConverter.createPrologInternalUser(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public Optional<PrologInternalUser> getPrologInternalUserByToken(@NotNull final String token)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from interno.func_usuario_busca_dados_by_token(" +
                    "f_token => ?);");
            stmt.setString(1, token);
            rSet = stmt.executeQuery();
            return AutenticacaoInternaConverter.createPrologInternalUser(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void createPrologInternalUserSession(@NotNull final Long codUsuarioProlog,
                                                @NotNull final String token) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from interno.func_usuario_iniciar_sessao(" +
                    "f_cod_usuario => ?, " +
                    "f_token_usuario => ?, " +
                    "f_data_hora_atual => ?);");
            stmt.setLong(1, codUsuarioProlog);
            stmt.setString(2, token);
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            stmt.execute();
        } catch (final Throwable t) {
            throw new RuntimeException("Erro ao criar sessão para o usuário Prolog de código: " + codUsuarioProlog, t);
        } finally {
            close(conn, stmt);
        }
    }
}