package br.com.zalf.prolog.webservice.geral.dispositivomovel;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.DispositivoMovelInsercao;
import br.com.zalf.prolog.webservice.geral.dispositivomovel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelDaoImpl extends DatabaseConnection implements DispositivoMovelDao {

    @NotNull
    @Override
    public Long insertDispositivoMovel(@NotNull final DispositivoMovelInsercao dispositivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL_COM_IMEI(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_MARCA   := ?, " +
                    "F_MODELO      := ?, " +
                    "F_DESCRICAO   := ?, " +
                    "F_IMEI        := ?) AS CODIGO;");
            stmt.setLong(1, dispositivo.getCodEmpresa());
            bindValueOrNull(stmt, 2, dispositivo.getCodMarca(), SqlType.BIGINT);
            stmt.setString(3, dispositivo.getModelo());
            stmt.setString(4, dispositivo.getDescricao());
            stmt.setArray(5, PostgresUtils.listToArray(conn, SqlType.TEXT, dispositivo.getNumerosImei()));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir dispositivo móvel");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateDispositivoMovel(@NotNull final DispositivoMovel dispositivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_DISPOSITIVO_EDITA_DISPOSITIVO_MOVEL(" +
                    "F_COD_EMPRESA     := ?, " +
                    "F_COD_DISPOSITIVO := ?, " +
                    "F_COD_MARCA       := ?, " +
                    "F_MODELO          := ?, " +
                    "F_DESCRICAO       := ?, " +
                    "F_IMEI            := ?);");
            stmt.setLong(1, dispositivo.getCodEmpresa());
            stmt.setLong(2, dispositivo.getCodDispositivo());
            bindValueOrNull(stmt, 3, dispositivo.getCodMarca(), SqlType.BIGINT);
            stmt.setString(4, dispositivo.getModelo());
            stmt.setString(5, dispositivo.getDescricao());
            stmt.setArray(6, PostgresUtils.listToArray(conn, SqlType.TEXT, dispositivo.getNumerosImei()));
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getInt(1) <= 0) {
                throw new SQLException("Erro ao atualizar o dispositivo móvel: " + dispositivo.getCodDispositivo());
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<DispositivoMovel> getDispositivosPorEmpresa(@NotNull final Long codEmpresa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_DISPOSITIVO_GET_DISPOSITIVOS_MOVEIS(F_COD_EMPRESA := ?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            return DispositivoMovelConverter.createDispositivoMovelListagem(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa,
                                                @NotNull final Long codDispositivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_DISPOSITIVO_GET_DISPOSITIVO_MOVEL(" +
                    "F_COD_EMPRESA     := ?, " +
                    "F_COD_DISPOSITIVO := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codDispositivo);
            rSet = stmt.executeQuery();
            return DispositivoMovelConverter.createDispositivoMovelVisualizacao(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_DISPOSITIVO_GET_MARCAS_DISPOSITIVO_MOVEL();");
            rSet = stmt.executeQuery();
            final List<MarcaDispositivoMovelSelecao> marcas = new ArrayList<>();
            while (rSet.next()) {
                marcas.add(DispositivoMovelConverter.createMarcaDispositivoSelecao(rSet));
            }
            return marcas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void deleteDispositivoMovel(@NotNull final Long codEmpresa,
                                       @NotNull final Long codDispositivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT FUNC_DISPOSITIVO_DELETA_DISPOSITIVO_MOVEL(" +
                    "F_COD_EMPRESA     := ?, " +
                    "F_COD_DISPOSITIVO := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codDispositivo);
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getInt(1) <= 0) {
                throw new SQLException("Erro ao deletar o dispositivo móvel: " + codDispositivo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}