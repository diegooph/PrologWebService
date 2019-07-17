package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelDaoImpl extends DatabaseConnection implements DispositivoMovelDao {

    @NotNull
    @Override
    public List<MarcaDispositivoMovelSelecao> getMarcasDispositivos() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_GERAL_GET_MARCAS_DISPOSITIVO_MOVEL();");
            rSet = stmt.executeQuery();
            final List<MarcaDispositivoMovelSelecao> marcas = new ArrayList<>();
            while (rSet.next()) {
                marcas.add(DispositivoMovelConverter.createMarcaCelularSelecao(rSet));
            }
            return marcas;
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_GERAL_GET_DISPOSITIVOS_MOVEIS(F_COD_EMPRESA := ?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            final List<DispositivoMovel> dispositivos = new ArrayList<>();
            while (rSet.next()) {
                dispositivos.add(DispositivoMovelConverter.createDispositivoMovel(rSet));
            }
            return dispositivos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public DispositivoMovel getDispositivoMovel(@NotNull final Long codEmpresa, @NotNull final Long codDispositivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_GERAL_GET_DISPOSITIVO_MOVEL(F_COD_EMPRESA := ?, F_COD_DISPOSITIVO := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codDispositivo);
            rSet = stmt.executeQuery();
            DispositivoMovel dispositivo;
            if (rSet.next()) {
                dispositivo = DispositivoMovelConverter.createDispositivoMovel(rSet);
            } else {
                throw new SQLException("Erro ao buscar dispositivo de código: " + codDispositivo);
            }
            return dispositivo;
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
            stmt = conn.prepareStatement("SELECT FUNC_GERAL_EDITA_DISPOSITIVO_MOVEL(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_COD_DISPOSITIVO   := ?, " +
                    "F_COD_MARCA  := ?, " +
                    "F_IMEI  := ?, " +
                    "F_MODELO  := ?, " +
                    "F_DESCRICAO       := ?);");
            stmt.setLong(1, dispositivo.getCodEmpresa());
            stmt.setLong(2, dispositivo.getCodDispositivo());
            stmt.setLong(3, dispositivo.getCodMarca());
            stmt.setString(4, dispositivo.getNumeroImei());
            stmt.setString(5, dispositivo.getModelo());
            stmt.setString(6, dispositivo.getDescricao());
            rSet = stmt.executeQuery();
            if (!rSet.next() || rSet.getInt(1) <= 0) {
                throw new SQLException("Erro ao atualizar o dispositivo móvel: " + dispositivo.getCodDispositivo());
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}