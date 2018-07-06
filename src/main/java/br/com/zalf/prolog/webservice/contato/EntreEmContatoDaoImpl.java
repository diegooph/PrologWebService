package br.com.zalf.prolog.webservice.contato;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EntreEmContatoDaoImpl extends DatabaseConnection implements EntreEmContatoDao {

    public EntreEmContatoDaoImpl() {

    }

    @NotNull
    @Override
    public Long insertNovaMensagemContato(@NotNull final MensagemContato contato) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO COMERCIAL.MENSAGEM_CONTATO "
                    + "(NOME, EMAIL, TELEFONE, EMPRESA, MENSAGEM) VALUES "
                    + "(?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getEmail());
            stmt.setString(3, contato.getTelefone());
            stmt.setString(4, contato.getEmpresa());
            stmt.setString(5, contato.getMensagem());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao salvar mensagem de contato");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}