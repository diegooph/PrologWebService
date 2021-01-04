package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoAfericaoDaoImpl extends DatabaseConnection implements ConfiguracaoAfericaoDao {
    private static final int EXECUTE_BATCH_SUCCESS = 0;

    @Override
    public void insertOrUpdateConfiguracoesTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivelInsercao> configuracoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            if (configuracoes.isEmpty()) {
                return;
            }
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT FUNC_AFERICAO_CONFIGURACOES_VEICULO_AFERIVEL_INSERE(" +
                    "F_COD_CONFIGURACAO => ?," +
                    "F_COD_UNIDADE => ?," +
                    "F_COD_TIPO_VEICULO => ?," +
                    "F_PODE_AFERIR_ESTEPE => ?," +
                    "F_FORMA_COLETA_DADOS_PRESSAO => ?," +
                    "F_FORMA_COLETA_DADOS_SULCO => ?," +
                    "F_FORMA_COLETA_DADOS_SULCO_PRESSAO => ?," +
                    "F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO => ?);");
            int totalUpserts = 0;
            for (final ConfiguracaoTipoVeiculoAferivelInsercao configuracao : configuracoes) {
                bindValueOrNull(stmt, 1, configuracao.getCodConfiguracao(), SqlType.BIGINT);
                stmt.setLong(2, codUnidade);
                stmt.setLong(3, configuracao.getCodTipoVeiculo());
                stmt.setBoolean(4, configuracao.isPodeAferirEstepe());
                stmt.setString(5, configuracao.getFormaColetaDadosPressao().toString());
                stmt.setString(6, configuracao.getFormaColetaDadosSulco().toString());
                stmt.setString(7, configuracao.getFormaColetaDadosSulcoPressao().toString());
                stmt.setString(8, configuracao.getFormaColetaDadosFechamentoServico().toString());
                stmt.addBatch();
                totalUpserts++;
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    totalUpserts,
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao inserir configurações de tipo de veículo aferível");
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<ConfiguracaoTipoVeiculoAferivelListagem> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(" +
                    "F_COD_UNIDADE => ?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<ConfiguracaoTipoVeiculoAferivelListagem> configsTipoAfericao = new ArrayList<>();
                do {
                    configsTipoAfericao.add(ConfiguracaoConverter.createConfiguracaoTipoVeiculoAfericaoListagem(rSet));
                } while (rSet.next());
                return configsTipoAfericao;
            }
            return Collections.emptyList();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void insertOrUpdateConfiguracoesAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(" +
                    "F_CODIGO := ?, " +
                    "F_COD_UNIDADE := ?, " +
                    "F_VARIACAO_SULCO_MENOR := ?, " +
                    "F_VARIACAO_SULCO_MAIOR := ?, " +
                    "F_BLOQUEAR_VALORES_MENORES := ?, " +
                    "F_BLOQUEAR_VALORES_MAIORES := ?);");
            for (final ConfiguracaoAlertaColetaSulco configuracao : configuracoes) {
                bindValueOrNull(stmt, 1, configuracao.getCodigo(), SqlType.BIGINT);
                stmt.setLong(2, configuracao.getCodUnidadeReferente());
                stmt.setDouble(3, configuracao.getVariacaoAceitaSulcoMenorMilimetros());
                stmt.setDouble(4, configuracao.getVariacaoAceitaSulcoMaiorMilimetros());
                stmt.setBoolean(5, configuracao.isBloqueiaValoresMenores());
                stmt.setBoolean(6, configuracao.isBloqueiaValoresMaiores());
                stmt.addBatch();
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao atualizar configurações da unidade");
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
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
    public void upsertConfiguracoesCronogramaServicos(
            @NotNull final Long codColaborador,
            @NotNull final List<ConfiguracaoCronogramaServicoUpsert> configuracoes) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareCall("{CALL FUNC_AFERICAO_UPSERT_CONFIGURACAO_CRONOGRAMA_SERVICO(" +
                    "F_CODIGO_EMPRESA := ?," +
                    "F_CODIGO_UNIDADE := ?," +
                    "F_TOLERANCIA_CALIBRAGEM := ?," +
                    "F_TOLERANCIA_INSPECAO := ?," +
                    "F_SULCO_MINIMO_RECAPAGEM := ?," +
                    "F_SULCO_MINIMO_DESCARTE := ?," +
                    "F_PERIODO_AFERICAO_PRESSAO := ?," +
                    "F_PERIODO_AFERICAO_SULCO := ?," +
                    "F_COD_COLABORADOR := ?," +
                    "F_DATA_HORA_ATUAL_UTC := ?)}");
            final OffsetDateTime now = Now.getOffsetDateTimeUtc();
            for (final ConfiguracaoCronogramaServicoUpsert configuracao : configuracoes) {
                stmt.setLong(1, configuracao.getCodEmpresaReferente());
                stmt.setLong(2, configuracao.getCodUnidadeReferente());
                stmt.setObject(3, configuracao.getToleranciaCalibragem(), SqlType.NUMERIC.asIntTypeJava());
                stmt.setObject(4, configuracao.getToleranciaInspecao(), SqlType.NUMERIC.asIntTypeJava());
                stmt.setObject(5, configuracao.getSulcoMinimoRecape(), SqlType.NUMERIC.asIntTypeJava());
                stmt.setObject(6, configuracao.getSulcoMinimoDescarte(), SqlType.NUMERIC.asIntTypeJava());
                stmt.setInt(7, configuracao.getPeriodoAfericaoPressao());
                stmt.setInt(8, configuracao.getPeriodoAfericaoSulco());
                stmt.setLong(9, codColaborador);
                stmt.setObject(10, now);
                stmt.addBatch();
            }
            final int[] batchResult = stmt.executeBatch();
            final boolean tudoOk = IntStream
                    .of(batchResult)
                    .allMatch(result -> result == EXECUTE_BATCH_SUCCESS);
            if (!tudoOk || batchResult.length != configuracoes.size()) {
                throw new IllegalStateException("Erro ao atualizar configurações de cronograma e serviços da unidade");
            }
            conn.commit();
        } catch (final Throwable throwable) {
            if (conn != null) {
                conn.rollback();
            }
            throw throwable;
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public List<ConfiguracaoCronogramaServico> getConfiguracoesCronogramaServicos(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_BY_COLABORADOR(F_COD_COLABORADOR := ?);");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            final List<ConfiguracaoCronogramaServico> configuracoes = new ArrayList<>();
            while (rSet.next()) {
                configuracoes.add(ConfiguracaoConverter.createConfiguracaoAberturaServico(rSet));
            }
            return configuracoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ConfiguracaoCronogramaServicoHistorico> getConfiguracoesCronogramaServicosHistorico(
            @NotNull final Long codRestricaoUnidadePneu) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_HISTORICO(F_COD_RESTRICAO_UNIDADE_PNEU := ?);");
            stmt.setLong(1, codRestricaoUnidadePneu);
            rSet = stmt.executeQuery();
            final List<ConfiguracaoCronogramaServicoHistorico> historicos = new ArrayList<>();
            while (rSet.next()) {
                historicos.add(ConfiguracaoConverter.createConfiguracaoAberturaServicoHistorico(rSet));
            }
            return historicos;
        } finally {
            close(conn, stmt, rSet);
        }
    }

}