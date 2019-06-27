package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.*;
import br.com.zalf.prolog.webservice.permissao.pilares.ImpactoPermissaoProLog;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CargoConverter {

    private CargoConverter() {
        throw new IllegalStateException(CargoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static CargoSelecao createCargoTodosUnidade(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoSelecao(
                rSet.getLong("COD_CARGO"),
                rSet.getString("NOME_CARGO"));
    }

    @NotNull
    static CargoListagemEmpresa createCargoTodosEmpresa(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoListagemEmpresa(
                rSet.getLong("COD_CARGO"),
                rSet.getString("NOME_CARGO"),
                rSet.getLong("QTD_COLABORADORES_VINCULADOS"));
    }

    @NotNull
    static CargoEdicao createCargoEdicao(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoEdicao(
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("COD_CARGO"),
                rSet.getString("NOME_CARGO"));
    }

    @NotNull
    static CargoEmUso createCargoEmUso(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoEmUso(
                rSet.getLong("COD_CARGO"),
                rSet.getString("NOME_CARGO"),
                rSet.getInt("QTD_COLABORADORES_VINCULADOS"),
                rSet.getInt("QTD_PERMISSOES_VINCULADAS"));
    }

    @NotNull
    static CargoNaoUtilizado createCargoNaoUtilizado(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoNaoUtilizado(
                rSet.getLong("COD_CARGO"),
                rSet.getString("NOME_CARGO"),
                rSet.getInt("QTD_PERMISSOES_VINCULADAS"));
    }

    @NotNull
    static CargoVisualizacao createCargoVisualizacao(@NotNull final ResultSet rSet,
                                                     @NotNull final List<CargoPilarProLog> pilaresCargo)
            throws SQLException {
        return new CargoVisualizacao(
                rSet.getLong("COD_CARGO"),
                rSet.getLong("COD_UNIDADE_CARGO"),
                rSet.getString("NOME_CARGO"),
                pilaresCargo);
    }

    @NotNull
    static CargoFuncionalidadeProLog createFuncionalidadeProLog(@NotNull final ResultSet rSet,
                                                                @NotNull final List<CargoPermissaoProLog> permissoes)
            throws SQLException {
        return new CargoFuncionalidadeProLog(
                rSet.getInt("COD_FUNCIONALIDADE"),
                rSet.getString("NOME_FUNCIONALIDADE"),
                permissoes);
    }

    @NotNull
    static CargoPermissaoProLog createPermissaoDetalhadaProLog(@NotNull final ResultSet rSet) throws SQLException {
        return new CargoPermissaoProLog(
                rSet.getInt("COD_PERMISSAO"),
                rSet.getString("NOME_PERMISSAO"),
                ImpactoPermissaoProLog.fromString(rSet.getString("IMPACTO_PERMISSAO")),
                rSet.getString("DESCRICAO_PERMISSAO"),
                rSet.getBoolean("PERMISSAO_LIBERADA"));
    }

    @NotNull
    static CargoPilarProLog createPilarDetalhado(@NotNull final ResultSet rSet,
                                                 @NotNull final List<CargoFuncionalidadeProLog> funcionalidades)
            throws SQLException {
        return new CargoPilarProLog(
                rSet.getInt("COD_PILAR"),
                rSet.getString("NOME_PILAR"),
                funcionalidades);
    }
}