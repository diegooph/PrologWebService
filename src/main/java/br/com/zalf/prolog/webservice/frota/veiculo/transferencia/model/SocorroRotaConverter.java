package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class SocorroRotaConverter {
    private SocorroRotaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static UnidadeAberturaSocorro createUnidadeAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new UnidadeAberturaSocorro(
                rSet.getLong("CODIGO_UNIDADE"),
                rSet.getString("NOME_UNIDADE"));
    }

    @NotNull
    public static VeiculoAberturaSocorro createVeiculoAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAberturaSocorro(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("KM"));
    }

    @NotNull
    public static OpcaoProblemaAberturaSocorro createOpcaoProblemaAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaAberturaSocorro(
                rSet.getLong("CODIGO"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"));
    }
    @NotNull
    public static SocorroRotaListagem createSocorroRotaListagem(
            @NotNull final ResultSet rSet) throws SQLException {
        return new SocorroRotaListagem(
                rSet.getLong("COD_SOCORRO_ROTA"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getString("NOME_RESPONSAVEL_ABERTURA_SOCORRO"),
                rSet.getString("DESCRICAO_FORNECIDA_ABERTURA_SOCORRO"),
                rSet.getString("DESCRICAO_OPCAO_PROBLEMA_ABERTURA_SOCORRO"),
                rSet.getObject("DATA_HORA_ABERTURA_SOCORRO", LocalDateTime.class),
                rSet.getString("ENDERECO_AUTOMATICO_ABERTURA_SOCORRO"),
                StatusSocorroRota.fromString(rSet.getString("STATUS_ATUAL_SOCORRO_ROTA"))
        );
    }
}