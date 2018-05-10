package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoAfericaoDaoImpl extends DatabaseConnection implements ConfiguracaoAfericaoDao {

    public ConfiguracaoAfericaoDaoImpl() {
    }

    @Override
    public void insertOrUpdateConfiguracao(@NotNull final Long codUnidade,
                                           @NotNull final List<ConfiguracaoTipoVeiculoAfericao> configuracoes)
            throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final ConfiguracaoTipoVeiculoAfericao configuracao : configuracoes) {
                // Garantimos que se um código for == NULL se trata de uma configuração NOVA
                // Então fazemos um insert, caso contrário um update.
                if (configuracao.getCodigo() == null) {
                    insertConfiguracao(conn, codUnidade, configuracao);
                } else {
                    updateCondiguracao(conn, codUnidade, configuracao);
                }
            }
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ConfiguracaoTipoVeiculoAfericao> configTipoAfericao = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO " +
                    "WHERE COD_UNIDADE = ? AND STATUS_ATIVO = TRUE;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                configTipoAfericao.add(createTipoVeiculoAfericao(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return configTipoAfericao;
    }

    private void insertConfiguracao(@NotNull final Connection conn,
                                    @NotNull final Long codUnidade,
                                    @NotNull final ConfiguracaoTipoVeiculoAfericao configuracao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO CONFIGURACAO_TIPO_AFERICAO_VEICULO " +
                    "(COD_UNIDADE, COD_TIPO_VEICULO, PODE_AFERIR_SULCO, " +
                    "PODE_AFERIR_PRESSAO, PODE_AFERIR_SULCO_PRESSAO, PODE_AFERIR_ESTEPE) " +
                    "VALUES (?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, configuracao.getTipoVeiculo().getCodigo());
            stmt.setBoolean(3, configuracao.isPodeAferirSulco());
            stmt.setBoolean(4, configuracao.isPodeAferirPressao());
            stmt.setBoolean(5, configuracao.isPodeAferirSulcoPressao());
            stmt.setBoolean(6, configuracao.isPodeAferirEstepe());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir item na tabela escala");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private boolean updateCondiguracao(@NotNull final Connection conn,
                                       @NotNull final Long codUnidade,
                                       @NotNull final ConfiguracaoTipoVeiculoAfericao configuracao) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CONFIGURACAO_TIPO_AFERICAO_VEICULO " +
                    "SET PODE_AFERIR_SULCO = ?, " +
                    "  PODE_AFERIR_PRESSAO = ?, " +
                    "  PODE_AFERIR_SULCO_PRESSAO = ?, " +
                    "  PODE_AFERIR_ESTEPE = ? " +
                    "WHERE COD_UNIDADE = ? AND COD_TIPO_VEICULO = ?;");
            stmt.setBoolean(1, configuracao.isPodeAferirSulco());
            stmt.setBoolean(2, configuracao.isPodeAferirPressao());
            stmt.setBoolean(3, configuracao.isPodeAferirSulcoPressao());
            stmt.setBoolean(4, configuracao.isPodeAferirEstepe());
            stmt.setLong(5, codUnidade);
            stmt.setLong(6, configuracao.getTipoVeiculo().getCodigo());
            if (stmt.executeUpdate() == 0) {
                return false;
            }
        } finally {
            closeStatement(stmt);
        }
        return true;
    }

    @NotNull
    private ConfiguracaoTipoVeiculoAfericao createTipoVeiculoAfericao(@NotNull final ResultSet rSet) throws SQLException {
        final ConfiguracaoTipoVeiculoAfericao config = new ConfiguracaoTipoVeiculoAfericao();
        config.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        config.setTipoVeiculo(createTipoVeiculo(rSet));
        config.setPodeAferirSulco(rSet.getBoolean("PODE_AFERIR_SULCO"));
        config.setPodeAferirPressao(rSet.getBoolean("PODE_AFERIR_PRESSAO"));
        config.setPodeAferirSulcoPressao(rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"));
        config.setPodeAferirEstepe(rSet.getBoolean("PODE_AFERIR_ESTEPE"));
        return config;
    }

    @NotNull
    private TipoVeiculo createTipoVeiculo(@NotNull final ResultSet rSet) throws SQLException {
        final TipoVeiculo tipoVeiculo = new TipoVeiculo();
        tipoVeiculo.setCodigo(rSet.getLong("COD_TIPO_VEICULO"));
        tipoVeiculo.setNome(rSet.getString("NOME_TIPO_VEICULO"));
        return tipoVeiculo;
    }
}
