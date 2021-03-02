package br.com.zalf.prolog.webservice.frota.veiculo.historico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.EstadoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.HistoricoEdicaoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.frota.veiculo.historico.HistoricoEdicaoVeiculoConverter.createEstadoVeiculo;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class HistoricoEdicaoVeiculoDaoImpl extends DatabaseConnection implements HistoricoEdicaoVeiculoDao {
    @Override
    @NotNull
    public List<HistoricoEdicaoVeiculo> getHistoricoEdicaoVeiculo(@NotNull final Long codEmpresa,
                                                                  @NotNull final Long codVeiculo) throws Throwable {
        final String sql = "select * " +
                "from func_veiculo_listagem_historico_edicoes(" +
                "f_cod_empresa => ?," +
                "f_cod_veiculo => ?);";
        try (final Connection conn = getConnection();
             final PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codVeiculo);
            try (final ResultSet rSet = stmt.executeQuery()) {
                if (rSet.next()) {
                    final List<HistoricoEdicaoVeiculo> historicoEdicoesVeiculo = new ArrayList<>();
                    do {
                        final EstadoVeiculo estadoNovo = createEstadoVeiculo(rSet);
                        rSet.next();
                        final EstadoVeiculo estadoAntigo = createEstadoVeiculo(rSet);
                        historicoEdicoesVeiculo.add(
                                HistoricoEdicaoVeiculoConverter.createHistoricoEdicaoVeiculo(estadoAntigo,
                                                                                             estadoNovo));
                    } while (rSet.next());
                    return historicoEdicoesVeiculo;
                } else {
                    return Collections.emptyList();
                }
            }
        }
    }
}