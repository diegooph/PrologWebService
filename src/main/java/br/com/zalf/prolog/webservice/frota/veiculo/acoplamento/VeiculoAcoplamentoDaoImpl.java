package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.ListUtils;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamento;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoResponse;
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
public final class VeiculoAcoplamentoDaoImpl extends DatabaseConnection implements VeiculoAcoplamentoDao {
    @Override
    public java.util.Optional<List<VeiculoAcoplamentoResponse>> getVeiculoAcoplamentos(@NotNull final List<Long> codUnidades,
                                                                                       @Nullable final List<Long> codVeiculos,
                                                                                       @Nullable final LocalDate dataInicial,
                                                                                       @Nullable final LocalDate dataFinal)
            throws Throwable {
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

            final List<VeiculoAcoplamentoResponse> veiculoAcoplamentosResponse = new ArrayList<>();
            Long codProcessoAnterior = null;
            VeiculoAcoplamento veiculoAcoplamento = null;

            while (rSet.next()) {
                if (codProcessoAnterior == null || !codProcessoAnterior.equals(rSet.getLong("cod_processo"))) {
                    veiculoAcoplamento = VeiculoAcoplamentoConverter.createVeiculoAcoplamento(rSet);
                    veiculoAcoplamentosResponse.add(VeiculoAcoplamentoConverter.createVeiculoAcoplamentoResponse(rSet));
                    veiculoAcoplamentosResponse.get(lastIndex(veiculoAcoplamentosResponse)).getVeiculoAcoplamentos().add(veiculoAcoplamento);
                    codProcessoAnterior = rSet.getLong("cod_processo");
                } else {
                    veiculoAcoplamento = VeiculoAcoplamentoConverter.createVeiculoAcoplamento(rSet);
                    veiculoAcoplamentosResponse.get(lastIndex(veiculoAcoplamentosResponse)).getVeiculoAcoplamentos().add(veiculoAcoplamento);
                    codProcessoAnterior = rSet.getLong("cod_processo");
                }
            }
            if (hasElements(veiculoAcoplamentosResponse)) {
                return Optional.of(veiculoAcoplamentosResponse);
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}