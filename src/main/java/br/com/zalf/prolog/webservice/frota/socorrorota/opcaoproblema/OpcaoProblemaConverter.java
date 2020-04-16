package br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema;

import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.OpcaoProblemaAberturaSocorro;
import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.OpcaoProblemaSocorroRotaListagem;
import br.com.zalf.prolog.webservice.frota.socorrorota.opcaoproblema._model.OpcaoProblemaSocorroRotaVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.VeiculoTransferenciaConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 12/19/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class OpcaoProblemaConverter {
    private OpcaoProblemaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    public static OpcaoProblemaAberturaSocorro createOpcaoProblemaAberturaSocorro(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaAberturaSocorro(
                rSet.getLong("CODIGO_OPCAO_PROBLEMA"),
                rSet.getString("DESCRICAO_OPCAO_PROBLEMA"),
                rSet.getBoolean("OBRIGA_DESCRICAO_OPCAO_PROBLEMA"));
    }

    @NotNull
    public static OpcaoProblemaSocorroRotaListagem createOpcaoProblemaSocorroRota(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaSocorroRotaListagem(
                rSet.getLong("COD_OPCAO_PROBLEMA"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"),
                rSet.getBoolean("STATUS_ATIVO"));
    }

    public static OpcaoProblemaSocorroRotaVisualizacao createOpcaoProblemaSocorroRotaVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        return new OpcaoProblemaSocorroRotaVisualizacao(
                rSet.getLong("COD_OPCAO_PROBLEMA"),
                rSet.getString("DESCRICAO"),
                rSet.getBoolean("OBRIGA_DESCRICAO"),
                rSet.getBoolean("STATUS_ATIVO"),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ATUALIZACAO"),
                rSet.getString("DATA_HORA_ULTIMA_ATUALIZACAO"));
    }
}