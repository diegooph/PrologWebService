package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAberturaServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoConverter;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ConfiguracaoAfericaoDaoImpl extends DatabaseConnection implements ConfiguracaoAfericaoDao {

    public ConfiguracaoAfericaoDaoImpl() {

    }

    @Override
    public void insertOrUpdateConfiguracoesTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final ConfiguracaoTipoVeiculoAferivel configuracao : configuracoes) {
                // Garantimos que se um código for == NULL se trata de uma configuração NOVA
                // Então fazemos um insert, caso contrário um update.
                if (configuracao.getCodigo() == null) {
                    insertConfiguracaoTipoVeiculo(conn, codUnidade, configuracao);
                } else {
                    updateCondiguracaoTipoVeiculo(conn, codUnidade, configuracao);
                }
            }
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            final List<ConfiguracaoTipoVeiculoAferivel> configTipoAfericao = new ArrayList<>();
            while (rSet.next()) {
                configTipoAfericao.add(ConfiguracaoConverter.createConfiguracaoTipoVeiculoAfericao(rSet));
            }
            return configTipoAfericao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void insertOrUpdateConfiguracoesAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            for (final ConfiguracaoAlertaColetaSulco configuracao : configuracoes) {
                stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(?, ?, ?, ?);");
                bindValueOrNull(stmt, 1, configuracao.getCodigo(), SqlType.BIGINT);
                stmt.setLong(2, configuracao.getCodUnidadeReferente());
                stmt.setDouble(3, configuracao.getVariacaoAceitaSulcoMenorMilimetros());
                stmt.setDouble(4, configuracao.getVariacaoAceitaSulcoMaiorMilimetros());
                rSet = stmt.executeQuery();
                if (rSet.next() && rSet.getBoolean(1)) {
                    conn.commit();
                } else {
                    throw new IllegalStateException("Erro ao atualizar configurações da unidade: "
                            + configuracao.getCodUnidadeReferente());
                }
            }
        } finally {
            if (conn != null) {
                conn.rollback();
            }
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CONFIG_ALERTA_COLETA_SULCO(?);");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            final List<ConfiguracaoAlertaColetaSulco> configuracoes = new ArrayList<>();
            while (rSet.next()) {
                configuracoes.add(ConfiguracaoConverter.createConfiguracaoAlertaColetaSulco(rSet));
            }
            return configuracoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void updateConfiguracaoAberturaServico(
            @NotNull final List<ConfiguracaoAberturaServico> configuracoes) throws Throwable {
        //TODO: ESCREVE O CÓDIGO DE UPSERT DAS INFORMAÇÕES DE FUNC_PNEU_GET_CONFIGURACAO_POR_COLABORADOR(F_COD_COLABORADOR)
    }

    @NotNull
    @Override
    public List<ConfiguracaoAberturaServico> getConfiguracaoAberturaServico(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_GET_CONFIGURACAO_POR_COLABORADOR(F_COD_COLABORADOR := ?);");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            final List<ConfiguracaoAberturaServico> configuracoes = new ArrayList<>();
            while (rSet.next()) {
                configuracoes.add(ConfiguracaoConverter.createConfiguracaoAberturaServico(rSet));
            }
            return configuracoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertConfiguracaoTipoVeiculo(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final ConfiguracaoTipoVeiculoAferivel configuracao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO " +
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
            close(stmt);
        }
    }

    private boolean updateCondiguracaoTipoVeiculo(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final ConfiguracaoTipoVeiculoAferivel configuracao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO " +
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
            close(stmt);
        }
        return true;
    }
}