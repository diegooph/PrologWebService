package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;
import static br.com.zalf.prolog.webservice.commons.util.StringUtils.trimToNull;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TipoVeiculoDaoImpl extends DatabaseConnection implements TipoVeiculoDao {

    @NotNull
    @Override
    public Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "INSERT INTO VEICULO_TIPO(COD_EMPRESA, COD_DIAGRAMA, NOME, STATUS_ATIVO, COD_AUXILIAR) " +
                            "VALUES (?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, tipoVeiculo.getCodEmpresa());
            stmt.setLong(2, tipoVeiculo.getCodDiagrama());
            stmt.setString(3, tipoVeiculo.getNome().trim());
            stmt.setBoolean(4, true);
            bindValueOrNull(stmt, 5, trimToNull(tipoVeiculo.getCodAuxiliar()), SqlType.TEXT);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir o tipo de veículo");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_UPDATE_TIPO_VEICULO(?, ?, ?, ?);");
            stmt.setLong(1, tipoVeiculo.getCodigo());
            stmt.setString(2, tipoVeiculo.getNome().trim());
            stmt.setLong(3, tipoVeiculo.getCodDiagrama());
            bindValueOrNull(stmt, 4, trimToNull(tipoVeiculo.getCodAuxiliar()), SqlType.TEXT);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getInt(1) <= 0) {
                throw new SQLException("Erro ao atualizar o tipo de veículo: " + tipoVeiculo.getCodigo());
            }
        } finally {
            close(conn, stmt, rSet);
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
                            "  VT.COD_DIAGRAMA, " +
                            "  VT.CODIGO, " +
                            "  VT.NOME, " +
                            "  VT.COD_AUXILIAR " +
                            "FROM VEICULO_TIPO VT " +
                            "WHERE VT.COD_EMPRESA = ? " +
                            "      AND VT.STATUS_ATIVO = TRUE " +
                            "ORDER BY VT.NOME;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<TipoVeiculo> tiposVeiculos = new ArrayList<>();
            while (rSet.next()) {
                tiposVeiculos.add(new TipoVeiculo(
                        rSet.getLong("COD_EMPRESA"),
                        NullIf.equalOrLess(rSet.getLong("COD_DIAGRAMA"), 0),
                        rSet.getLong("CODIGO"),
                        rSet.getString("NOME"),
                        rSet.getString("COD_AUXILIAR")));
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
                            "VT.COD_DIAGRAMA, " +
                            "VT.CODIGO, " +
                            "VT.NOME, " +
                            "VT.COD_AUXILIAR " +
                            "FROM VEICULO_TIPO VT " +
                            "WHERE VT.CODIGO = ?;");
            stmt.setLong(1, codTipoVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new TipoVeiculo(
                        rSet.getLong("COD_EMPRESA"),
                        NullIf.equalOrLess(rSet.getLong("COD_DIAGRAMA"), 0),
                        rSet.getLong("CODIGO"),
                        rSet.getString("NOME"),
                        rSet.getString("COD_AUXILIAR"));
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