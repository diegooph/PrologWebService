package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model.VeiculoAcoplamentoHistorico;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model.VeiculoAcoplamentoHistoricoResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created on 2020-11-04
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoHistoricoConverter {

    public VeiculoAcoplamentoHistoricoConverter() {
        throw new IllegalStateException(VeiculoAcoplamentoHistoricoConverter.class.getSimpleName()
                                                + " cannot be instantiated!");
    }

    @NotNull
    public static VeiculoAcoplamentoHistorico createVeiculoAcoplamentoHistorico(@NotNull final ResultSet rSet)
            throws SQLException {
        return new VeiculoAcoplamentoHistorico(
                rSet.getString("placa"),
                rSet.getString("identificador_frota"),
                rSet.getLong("km"),
                rSet.getString("nome_posicao"),
                rSet.getString("acao"));
    }

    @NotNull
    public static VeiculoAcoplamentoHistoricoResponse createVeiculoAcoplamentoHistoricoResponse(
            @NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAcoplamentoHistoricoResponse(
                rSet.getLong("cod_processo"),
                rSet.getString("nome_unidade"),
                rSet.getString("nome_colaborador"),
                rSet.getObject("data_hora", LocalDateTime.class),
                rSet.getString("observacao"),
                new ArrayList<>());
    }
}