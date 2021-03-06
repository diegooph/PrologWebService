package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.trimToNull;
import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

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
                    "select vt.cod_empresa, " +
                            "vt.cod_diagrama, " +
                            "vt.codigo, " +
                            "vt.nome, " +
                            "vt.cod_auxiliar, " +
                            "vd.motorizado " +
                            "from " +
                            "veiculo_tipo vt " +
                            "join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo " +
                            "where vt.cod_empresa = ? " +
                            "order by vt.nome;");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<TipoVeiculo> tiposVeiculos = new ArrayList<>();
            while (rSet.next()) {
                tiposVeiculos.add(new TipoVeiculo(
                        rSet.getLong("COD_EMPRESA"),
                        NullIf.equalOrLess(rSet.getLong("COD_DIAGRAMA"), 0),
                        rSet.getLong("CODIGO"),
                        rSet.getString("NOME"),
                        rSet.getString("COD_AUXILIAR"),
                        rSet.getBoolean("MOTORIZADO")));
            }
            return tiposVeiculos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public TipoVeiculo getTipoVeiculo(@NotNull final Long codTipoVeiculo) throws Throwable {

        final String sql = "select " +
                "vt.cod_empresa, " +
                "vt.cod_diagrama, " +
                "vt.codigo, " +
                "vt.nome, " +
                "vt.cod_auxiliar," +
                "vd.motorizado " +
                "from veiculo_tipo vt " +
                "join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo " +
                "where vt.codigo = ?;";

        try (final Connection conn = getConnection();
             final PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, codTipoVeiculo);

            try (final ResultSet rSet = stmt.executeQuery()) {
                if (rSet.next()) {
                    return new TipoVeiculo(
                            rSet.getLong("COD_EMPRESA"),
                            NullIf.equalOrLess(rSet.getLong("COD_DIAGRAMA"), 0),
                            rSet.getLong("CODIGO"),
                            rSet.getString("NOME"),
                            rSet.getString("COD_AUXILIAR"),
                            rSet.getBoolean("MOTORIZADO"));
                } else {
                    throw new IllegalStateException("Tipo de veículo não encontrado com o código: " + codTipoVeiculo);
                }
            }
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