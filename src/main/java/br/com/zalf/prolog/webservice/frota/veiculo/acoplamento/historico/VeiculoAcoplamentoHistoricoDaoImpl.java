package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model.VeiculoAcoplamentoHistorico;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model.VeiculoAcoplamentoHistoricoResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.ListUtils.hasElements;
import static br.com.zalf.prolog.webservice.commons.util.ListUtils.lastIndex;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoHistoricoDaoImpl extends DatabaseConnection implements VeiculoAcoplamentoHistoricoDao {
    @Override
    @NotNull
    public Optional<List<VeiculoAcoplamentoHistoricoResponse>> getVeiculoAcoplamentosHistorico(
            @NotNull final List<Long> codUnidades,
            @Nullable final List<Long> codVeiculos,
            @Nullable final LocalDate dataInicial,
            @Nullable final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_veiculo_busca_veiculo_acoplamento_historico(" +
                    "f_cod_unidades => ?," +
                    "f_cod_veiculos => ?," +
                    "f_data_inicial => ?," +
                    "f_data_final => ? );");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            if (ListUtils.hasNoElements(codVeiculos)) {
                stmt.setNull(2, Types.NULL);
            } else {
                stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codVeiculos));
            }
            if (dataInicial == null) {
                stmt.setNull(3, Types.NULL);
            } else {
                stmt.setObject(3, dataInicial);
            }
            if (dataFinal == null) {
                stmt.setNull(4, Types.NULL);
            } else {
                stmt.setObject(4, dataFinal);
            }
            rSet = stmt.executeQuery();

            final List<VeiculoAcoplamentoHistoricoResponse> veiculoAcoplamentosHistoricoResponse = new ArrayList<>();
            Long codProcessoAnterior = null;
            VeiculoAcoplamentoHistorico veiculoAcoplamentoHistorico = null;

            while (rSet.next()) {
                if (codProcessoAnterior == null || !codProcessoAnterior.equals(
                        rSet.getLong("cod_processo"))) {
                    veiculoAcoplamentoHistorico = VeiculoAcoplamentoHistoricoConverter.
                            createVeiculoAcoplamentoHistorico(rSet);
                    veiculoAcoplamentosHistoricoResponse.add(VeiculoAcoplamentoHistoricoConverter.
                            createVeiculoAcoplamentoHistoricoResponse(rSet));
                    veiculoAcoplamentosHistoricoResponse.get(lastIndex(veiculoAcoplamentosHistoricoResponse)).
                            getVeiculoAcoplamentoHistoricos().add(veiculoAcoplamentoHistorico);
                    codProcessoAnterior = rSet.getLong("cod_processo");
                } else {
                    veiculoAcoplamentoHistorico = VeiculoAcoplamentoHistoricoConverter.
                            createVeiculoAcoplamentoHistorico(rSet);
                    veiculoAcoplamentosHistoricoResponse.get(lastIndex(veiculoAcoplamentosHistoricoResponse)).
                            getVeiculoAcoplamentoHistoricos().add(veiculoAcoplamentoHistorico);
                    codProcessoAnterior = rSet.getLong("cod_processo");
                }
            }
            if (hasElements(veiculoAcoplamentosHistoricoResponse)) {
                return Optional.of(veiculoAcoplamentosHistoricoResponse);
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}