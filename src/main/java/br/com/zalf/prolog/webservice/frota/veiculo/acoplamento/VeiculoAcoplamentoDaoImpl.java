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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.VeiculoAcoplamentoConverter.createVeiculoAcoplamento;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoDaoImpl extends DatabaseConnection implements VeiculoAcoplamentoDao {
    @Override
    @NotNull
    public Optional<VeiculoAcoplamentoResponse> getVeiculoAcoplamentos(@NotNull final List<Long> codUnidades,
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
            if (rSet.next()) {
                final Long codProcesso = rSet.getLong("cod_processo");
                final String unidade = rSet.getString("nome_unidade");
                final String colaborador = rSet.getString("nome_colaborador");
                final LocalDateTime dataHora = rSet.getObject("data_hora", LocalDateTime.class);
                final String observacao = rSet.getString("observacao");

                final List<VeiculoAcoplamento> veiculoAcoplamentos = new ArrayList<>();
                do {
                    veiculoAcoplamentos.add(createVeiculoAcoplamento(rSet));
                } while (rSet.next());
                return Optional.of(
                        new VeiculoAcoplamentoResponse(codProcesso,
                                unidade,
                                colaborador,
                                dataHora,
                                observacao,
                                veiculoAcoplamentos));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}