package br.com.zalf.prolog.webservice.interno.apresentacao;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ApresentacaoDaoImpl implements ApresentacaoDao {

    @Override
    public String getResetaClonaEmpresaApresentacao(@NotNull final String username,
                                                    @NotNull final Long codEmpresaBase,
                                                    @NotNull final Long codEmpresaUsuario) throws Throwable {

        //Verificar se empresa pertence a usuário
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            final boolean verificaUsuarioEmpresa = verifyUsuarioEmpresa(conn, username, codEmpresaUsuario);
            if (verificaUsuarioEmpresa) {
                stmt = conn.prepareStatement("SELECT * FROM INTERNO.FUNC_RESETA_EMPRESA_APRESENTACAO(" +
                        "F_COD_EMPRESA_BASE := ?," +
                        "F_COD_EMPRESA_USUARIO := ? );");
                stmt.setLong(1, codEmpresaBase);
                stmt.setLong(2, codEmpresaUsuario);
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    return rSet.getString("MENSAGEM_SUCESSO");
                } else {
                    throw new SQLException("Erro ao resetar empresa de apresentação.");
                }
            } else {
                throw new GenericException("Usuário e empresa não correspondem.");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private boolean verifyUsuarioEmpresa(@NotNull final Connection conn,
                                         @NotNull final String username,
                                         @NotNull final Long codEmpresaUsuario) throws Throwable {

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTERNO.USUARIO_EMPRESA WHERE COD_EMPRESA = ? AND " +
                    "USERNAME = ?;");
            stmt.setLong(1, codEmpresaUsuario);
            stmt.setString(2, username);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return true;
            } else {
                return false;
            }
        } finally {
            close(stmt, rSet);
        }
    }
}