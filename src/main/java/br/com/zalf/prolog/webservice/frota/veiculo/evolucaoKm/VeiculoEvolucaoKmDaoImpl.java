package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.VeiculoEvolucaoKm;
import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.VeiculoEvolucaoKmResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm.VeiculoEvolucaoKmConverter.createVeiculoEvolucaoKm;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoEvolucaoKmDaoImpl extends DatabaseConnection implements VeiculoEvolucaoKmDao {

    @Override
    @NotNull
    public Optional<VeiculoEvolucaoKmResponse> getVeiculoEvolucaoKm(
            @NotNull final Long codEmpresa,
            @NotNull final Long codVeiculo,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_veiculo_busca_evolucao_km_consolidado(" +
                    "f_cod_empresa => ?," +
                    "f_cod_veiculo => ?," +
                    "f_data_inicial => ?," +
                    "f_data_final => ? );");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codVeiculo);
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Long km_atual = rSet.getLong("km_atual");
                final List<VeiculoEvolucaoKm> veiculoEvolucaoKms = new ArrayList<>();
                do {
                    veiculoEvolucaoKms.add(createVeiculoEvolucaoKm(rSet));
                } while (rSet.next());
                return Optional.of(
                        new VeiculoEvolucaoKmResponse(
                                km_atual,
                                veiculoEvolucaoKms));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}