package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RecapadoraDaoImpl extends DatabaseConnection implements RecapadoraDao {

    @NotNull
    @Override
    public Long insertRecapadora(@NotNull final String token,
                                 @NotNull final Recapadora recapadora) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO RECAPADORA (NOME, COD_EMPRESA, DATA_HORA_CADASTRO, CPF_CADASTRO) " +
                    "VALUES (?, ?, ?, (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?)) RETURNING CODIGO;");
            stmt.setString(1, recapadora.getNome());
            stmt.setLong(2, recapadora.getCodEmpresa());
            stmt.setTimestamp(3, Now.getTimestampUtc());
            stmt.setString(4, token);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir a recapadora");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void atualizaRecapadoras(@NotNull final Long codEmpresa,
                                    @NotNull final Recapadora recapadora) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE RECAPADORA SET NOME = ?" +
                    " WHERE CODIGO = ? AND COD_EMPRESA = ?");
            stmt.setString(1, recapadora.getNome());
            stmt.setLong(2, recapadora.getCodigo());
            stmt.setLong(3, codEmpresa);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar recapadora : " + recapadora.getNome());
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }


    @SuppressWarnings("Duplicates")
    @Override
    public List<Recapadora> getRecapadoras(@NotNull final Long codEmpresa, final Boolean ativas) throws SQLException {
        final List<Recapadora> recapadoras = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT R.CODIGO, R.COD_EMPRESA, R.NOME, R.ATIVA " +
                    "FROM RECAPADORA AS R " +
                    "WHERE R.COD_EMPRESA = ? " +
                    "AND (? = 1 OR R.ATIVA = ?) " +
                    "ORDER BY R.ATIVA DESC, R.CODIGO;");
            stmt.setLong(1, codEmpresa);
            if (ativas == null) {
                stmt.setInt(2, 1);
                stmt.setBoolean(3, true);
            } else {
                stmt.setInt(2, 0);
                stmt.setBoolean(3, ativas);
            }
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                recapadoras.add(createRecapadora(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return recapadoras;
    }

    @Override
    public Recapadora getRecapadora(final Long codEmpresa, final Long codRecapadora) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT R.CODIGO, R.COD_EMPRESA, R.NOME, R.ATIVA " +
                    "FROM RECAPADORA AS R " +
                    "WHERE R.CODIGO = ? AND R.COD_EMPRESA = ?");
            stmt.setLong(1, codRecapadora);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRecapadora(rSet);
            } else {
                throw new SQLException("Nenhuma Recapadora encontrada para o código: " + codRecapadora);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void alterarStatusRecapadoras(@NotNull final String token,
                                         @NotNull final Long codEmpresa,
                                         @NotNull final List<Recapadora> recapadoras) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final Recapadora recapadora : recapadoras) {
                atualizaRecapadora(conn, token, codEmpresa, recapadora);
            }
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @NotNull
    private Recapadora createRecapadora(final ResultSet rSet) throws SQLException {
        final Recapadora recapadora = new Recapadora();
        recapadora.setCodigo(rSet.getLong("CODIGO"));
        recapadora.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
        recapadora.setNome(rSet.getString("NOME"));
        recapadora.setAtiva(rSet.getBoolean("ATIVA"));
        return recapadora;
    }

    private void atualizaRecapadora(@NotNull final Connection conn,
                                    @NotNull final String token,
                                    @NotNull final Long codEmpresa,
                                    @NotNull final Recapadora recapadora) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RECAPADORA SET ATIVA = ?, " +
                    "  CPF_ALTERACAO_STATUS = (SELECT TA.CPF_COLABORADOR FROM TOKEN_AUTENTICACAO AS TA WHERE TA.TOKEN = ?) " +
                    "WHERE CODIGO = ? AND COD_EMPRESA = ?");
            stmt.setBoolean(1, recapadora.isAtiva());
            stmt.setString(2, token);
            stmt.setLong(3, recapadora.getCodigo());
            stmt.setLong(4, codEmpresa);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar status da recapadora : " + recapadora.getNome());
            }
        } finally {
            closeStatement(stmt);
        }
    }
}