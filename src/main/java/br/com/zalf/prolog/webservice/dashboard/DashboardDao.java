package br.com.zalf.prolog.webservice.dashboard;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface DashboardDao {

    @NotNull
    ComponentDataHolder getComponenteByCodigo(@NotNull final Integer codigo) throws SQLException;

    @NotNull
    List<DashComponentResumido> getComponentesColaborador(@NotNull final String userToken) throws SQLException;
}