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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_veiculo_listagem_historico_edicoes(" +
                    "F_COD_EMPRESA => ?," +
                    "F_COD_VEICULO => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<HistoricoEdicaoVeiculo> historicoEdicaoVeiculo = new ArrayList<>();
                // Ordenamos o select em ordem descrescente. Por conta disso, os resultados acima são sempre os mais
                // novos e os resultados abaixo os mais antigos. Dessa forma, a primeira linha do resultSet vai ser
                // sempre o veículo atualmente (ou o último estado dele antes de deixar de pertencer a empresa)
                // e a segunda, sempre o estado anterior ao atual. Depois de realizada a comparação, a segunda linha
                // será sempre o estadoNovo (o mais atual tirando o que já foi processado) e a terceira o estadoAntigo,
                // e assim por diante. Se por ventura a ordenação do select for alterada, essa lógica precisa ser
                // alterada igualmente.
                EstadoVeiculo estadoNovo = createEstadoVeiculo(rSet);
                while (rSet.next()) {
                    final EstadoVeiculo estadoAntigo = createEstadoVeiculo(rSet);
                    historicoEdicaoVeiculo.add(
                            HistoricoEdicaoVeiculoConverter.createHistoricoEdicaoVeiculo(estadoAntigo, estadoNovo));
                    estadoNovo = estadoAntigo;
                }

                return historicoEdicaoVeiculo;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
