package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class TipoVeiculoDaoImpl extends DatabaseConnection implements TipoVeiculoDao {

    @Override
    public void insertTipoVeiculoPorEmpresa(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO VEICULO_TIPO(COD_EMPRESA, NOME, STATUS_ATIVO) VALUES (?, ?, ?)");
            stmt.setLong(1, tipoVeiculo.getCodEmpresa());
            stmt.setString(2, tipoVeiculo.getNome().trim());
            stmt.setBoolean(3, true);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao cadastrar o tipo de veículo");
            }
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE VEICULO_TIPO SET NOME = ? WHERE CODIGO = ?;");
            stmt.setString(1, tipoVeiculo.getNome().trim());
            stmt.setLong(2, tipoVeiculo.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao atualizar o tipo de veículo: " + tipoVeiculo.getCodigo());
            }
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT " +
                            "  VT.COD_EMPRESA, " +
                            "  VT.CODIGO, " +
                            "  VT.NOME " +
                            "FROM VEICULO_TIPO VT " +
                            "WHERE VT.COD_EMPRESA = ? " +
                            "      AND VT.STATUS_ATIVO = TRUE;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<TipoVeiculo> tiposVeiculos = new ArrayList<>();
            while (rSet.next()) {
                tiposVeiculos.add(new TipoVeiculo(
                        rSet.getLong("COD_EMPRESA"),
                        rSet.getLong("CODIGO"),
                        rSet.getString("NOME")));
            }
            return tiposVeiculos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public TipoVeiculo getTipoVeiculo(@NotNull final Long codTipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT " +
                            "VT.COD_EMPRESA, " +
                            "VT.CODIGO, " +
                            "VT.NOME " +
                            "FROM VEICULO_TIPO VT " +
                            "WHERE VT.CODIGO = ?;");
            stmt.setLong(1, codTipoVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final TipoVeiculo tipoVeiculo = new TipoVeiculo();
                tipoVeiculo.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
                tipoVeiculo.setCodigo(rSet.getLong("CODIGO"));
                tipoVeiculo.setNome(rSet.getString("NOME"));
                return tipoVeiculo;
            } else {
                throw new IllegalStateException("Tipo de veículo não encontrado com o código: " + codTipoVeiculo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void deleteTipoVeiculoByEmpresa(@NotNull final Long codEmpresa,
                                           @NotNull final Long codTipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM VEICULO_TIPO WHERE CODIGO = ? AND COD_EMPRESA = ?");
            stmt.setLong(1, codTipoVeiculo);
            stmt.setLong(2, codEmpresa);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao deletar o tipo de veículo: " + codTipoVeiculo);
            }
        } finally {
            close(conn, stmt);
        }
    }
}
