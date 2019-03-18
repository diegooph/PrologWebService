package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoTodos;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoDaoImpl extends DatabaseConnection implements CargoDao {

    @NotNull
    @Override
    public List<CargoTodos> getTodosCargosUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_TODOS_CARGOS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<CargoTodos> cargos = new ArrayList<>();
            while (rSet.next()) {
                cargos.add(CargoConverter.createCargoTodos(rSet));
            }
            return cargos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<CargoEmUso> getCargosEmUsoUnidade(@NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_CARGOS_EM_USO(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<CargoEmUso> cargos = new ArrayList<>();
            while (rSet.next()) {
                cargos.add(CargoConverter.createCargoEmUso(rSet));
            }
            return cargos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(@NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_CARGOS_NAO_UTILIZADOS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<CargoNaoUtilizado> cargos = new ArrayList<>();
            while (rSet.next()) {
                cargos.add(CargoConverter.createCargoNaoUtilizado(rSet));
            }
            return cargos;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}