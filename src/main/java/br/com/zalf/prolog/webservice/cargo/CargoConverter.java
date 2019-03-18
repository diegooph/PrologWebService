package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoTodos;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

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
    static CargoTodos createCargoTodos(@NotNull final ResultSet rSet) throws Throwable {
        return new CargoTodos(
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
}