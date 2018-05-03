package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoAfericaoDaoImpl extends DatabaseConnection implements ConfiguracaoAfericaoDao {
    @Override
    public void updateConfiguracao(@NotNull final Long codUnidade,
                                   @NotNull final ConfiguracaoTipoVeiculoAfericao configuracao) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO CONFIGURACAO_TIPO_AFERICAO_VEICULO" +
                    "(COD_UNIDADE, COD_TIPO_VEICULO, DEVE_AFERIR_PRESSAO, DEVE_AFERIR_SULCO, DEVE_AFERIR_ESTEPE) " +
                    "    VALUES (?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, configuracao.getTipoVeiculo().getCodigo());
            stmt.setBoolean(3, configuracao.isDeveAferirPressao());
            stmt.setBoolean(4, configuracao.isDeveAferirSulco());
            stmt.setBoolean(5, configuracao.isDeveAferirEstepe());
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar configuração: " + configuracao.getCodigo());
            }
        } finally {
            closeConnection(conn, stmt, null);
        }

    }

    @Override
    public List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws SQLException {
        return null;
    }
}
