package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamento;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoResponse;
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
public final class VeiculoAcoplamentoConverter {
    @NotNull
    public static VeiculoAcoplamento createVeiculoAcoplamento(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAcoplamento(
                rSet.getString("placa"),
                rSet.getString("identificador_frota"),
                rSet.getLong("km"),
                rSet.getString("nome_posicao"),
                rSet.getString("acao"));
    }

    public static VeiculoAcoplamentoResponse createVeiculoAcoplamentoResponse(final ResultSet rSet) throws SQLException {
        return new VeiculoAcoplamentoResponse(
                rSet.getLong("cod_processo"),
                rSet.getString("nome_unidade"),
                rSet.getString("nome_colaborador"),
                rSet.getObject("data_hora", LocalDateTime.class),
                rSet.getString("observacao"),
                new ArrayList<>());
    }
}