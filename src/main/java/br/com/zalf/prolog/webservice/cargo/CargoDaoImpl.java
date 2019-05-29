package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.*;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<CargoSelecao> getTodosCargosUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_TODOS_CARGOS(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<CargoSelecao> cargos = new ArrayList<>();
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

    @NotNull
    @Override
    public CargoVisualizacao getPermissoesDetalhadasUnidade(@NotNull final Long codUnidade,
                                                            @NotNull final Long codCargo) throws SQLException {
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_PERMISSOES_DETALHADAS(F_COD_UNIDADE := ?, " +
                    "F_COD_CARGO := ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codCargo);
            rSet = stmt.executeQuery();
            return createCargoVisualizacao(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private CargoVisualizacao createCargoVisualizacao(@NotNull final ResultSet rSet) throws SQLException {
        final List<CargoPilarProLog> pilares = new ArrayList<>();
        List<CargoFuncionalidadeProLog> funcionalidades = new ArrayList<>();
        List<CargoPermissaoProLog> permissoes = new ArrayList<>();
        CargoPilarProLog pilar = null;
        CargoFuncionalidadeProLog funcionalidade = null;
        CargoVisualizacao cargoVisualizacao;
        if (rSet.next()) {
            cargoVisualizacao = CargoConverter.createCargoVisualizacao(rSet, pilares);
            do {
                if (pilar == null) {
                    pilar = CargoConverter.createPilarDetalhado(rSet, funcionalidades);
                    funcionalidade = CargoConverter.createFuncionalidadeProLog(rSet, permissoes);
                    permissoes.add(CargoConverter.createPermissaoDetalhadaProLog(rSet));
                } else {
                    if (rSet.getInt("COD_PILAR") == pilar.getCodigo()) {
                        if (rSet.getInt("COD_FUNCIONALIDADE") == funcionalidade.getCodigo()) {
                            permissoes.add(CargoConverter.createPermissaoDetalhadaProLog(rSet));
                        } else {
                            funcionalidades.add(funcionalidade);
                            permissoes = new ArrayList<>();
                            permissoes.add(CargoConverter.createPermissaoDetalhadaProLog(rSet));
                            funcionalidade = CargoConverter.createFuncionalidadeProLog(rSet, permissoes);
                        }
                    } else {
                        funcionalidades.add(funcionalidade);
                        pilares.add(pilar);
                        permissoes = new ArrayList<>();
                        permissoes.add(CargoConverter.createPermissaoDetalhadaProLog(rSet));
                        funcionalidades = new ArrayList<>();
                        funcionalidade = CargoConverter.createFuncionalidadeProLog(rSet, permissoes);
                        pilar = CargoConverter.createPilarDetalhado(rSet, funcionalidades);
                    }
                }
            } while (rSet.next());
        } else {
            throw new IllegalStateException("Nenhum dado de permissão encontrado para o cargo buscado");
        }

        return cargoVisualizacao;
    }
}