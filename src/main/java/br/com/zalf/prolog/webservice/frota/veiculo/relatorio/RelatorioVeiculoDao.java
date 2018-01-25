package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface RelatorioVeiculoDao {
    int getQtdVeiculosAtivosComPneuAplicado(@NotNull final List<Long> codUnidades) throws SQLException;
}