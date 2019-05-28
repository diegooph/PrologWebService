package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoSelecao;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncionalidadeProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.PermissaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.PilarProlog;
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

    @Override
    public List<PilarProlog> getPermissoesDetalhadasUnidade(Long codUnidade) throws SQLException {
        final List<PilarProlog> pilares;
        ResultSet rSet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CARGOS_GET_PERMISSOES_DETALHADAS(F_COD_UNIDADE := ? )");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            pilares = createPilaresDetalhados(rSet);
        } finally {
            close(conn, stmt, rSet);
        }

        return pilares;
    }

    //    @Override
    public List<PilarProlog> createPilaresDetalhados(ResultSet rSet) throws SQLException {
        final List<PilarProlog> pilares = new ArrayList<>();
        List<FuncionalidadeProLog> funcionalidades = new ArrayList<>();
        List<PermissaoProLog> permissoes = new ArrayList<>();

        PilarProlog pilar = null;
        FuncionalidadeProLog funcionalidade = null;

        while (rSet.next()) {
            if (pilar == null) {
                pilar = createPilarDetalhado(rSet, funcionalidades);
                funcionalidade = createFuncionalidadeProLog(rSet, permissoes);

                permissoes.add(createPermissaoDetalhadaProLog(rSet));
            } else {
                if (rSet.getInt("COD_PILAR") == pilar.getCodigo()) {
                    if (rSet.getInt("COD_FUNCIONALIDADE") == funcionalidade.getCodigo()) {
                        permissoes.add(createPermissaoDetalhadaProLog(rSet));
                    }else{
                        funcionalidades.add(funcionalidade);

                        permissoes = new ArrayList<>();
                        permissoes.add(createPermissaoDetalhadaProLog(rSet));

                        funcionalidade = createFuncionalidadeProLog(rSet, permissoes);
                    }
                }else{
                    funcionalidades.add(funcionalidade);

                    pilares.add(pilar);
                    permissoes = new ArrayList<>();
                    permissoes.add(createPermissaoDetalhadaProLog(rSet));

                    funcionalidades = new ArrayList<>();
                    funcionalidade = createFuncionalidadeProLog(rSet, permissoes);


                    pilar = createPilarDetalhado(rSet, funcionalidades);
                }
            }
            System.out.println("teste");
        }

        if (pilar != null) {
            funcionalidades.add(funcionalidade);
            pilares.add(pilar);
        }

        return pilares;
    }

    private FuncionalidadeProLog createFuncionalidadeProLog(ResultSet rSet,
                                                            List<PermissaoProLog> permissoes) throws SQLException {
        return new FuncionalidadeProLog(
                rSet.getInt("COD_FUNCIONALIDADE"),
                rSet.getString("FUNCIONALIDADE"),
                permissoes);
    }

    private PermissaoProLog createPermissaoDetalhadaProLog(ResultSet rSet) throws SQLException {
        return new PermissaoProLog(
                rSet.getInt("COD_PERMISSAO"),
                rSet.getString("PERMISSAO"),
                rSet.getInt("CRITICIDADE"),
                rSet.getString("DESCRICAO"));
    }

    private PilarProlog createPilarDetalhado(ResultSet rSet,
                                             List<FuncionalidadeProLog> funcionalidades) throws SQLException {
        return new PilarProlog(
                rSet.getInt("COD_PILAR"),
                rSet.getString("PILAR"),
                funcionalidades);
    }

}