package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.VeiculoEnvioTransferencia;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoTransferenciaDaoImpl extends DatabaseConnection implements VeiculoTransferenciaDao {

    @NotNull
    @Override
    public Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // É importante que esta verificação seja executa antes de qualquer outro processamento para inserir uma
            // Transferência de veículo.
            // Essa verificação nos garantirá que nenhum dos veículos transferidos está SEM DIAGRAMA aplicado.
            // Retornamos uma Exception específica para tratar estes casos.
            final Optional<List<TipoVeiculoDiagrama>> tipoVeiculoSemDiagramas = getPlacasSemDiagramaAplicado(
                    conn,
                    processoTransferenciaVeiculo.getCodUnidadeOrigem(),
                    processoTransferenciaVeiculo.getCodVeiculosTransferidos());
            if (tipoVeiculoSemDiagramas.isPresent()) {
                throw new VeiculoSemDiagramaException(
                        "Veículos identificados que não possuem diagrama associado",
                        tipoVeiculoSemDiagramas.get());
            }

            // Seta propriedade na connection para verificar constraints entre as tabelas do banco somente na chamada
            // conn.commit(). Assim temos mais flexibilidade de trabalhar sem sermos interrompidos por vínculos.
            // https://begriffs.com/posts/2017-08-27-deferrable-sql-constraints.html
            stmt = conn.prepareStatement("SET CONSTRAINTS ALL DEFERRED;");
            stmt.execute();

            final OffsetDateTime dataHoraSincronizacao = Now.offsetDateTimeUtc();
            stmt = conn.prepareStatement("INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(" +
                    "  COD_UNIDADE_ORIGEM," +
                    "  COD_UNIDADE_DESTINO," +
                    "  COD_UNIDADE_COLABORADOR," +
                    "  COD_COLABORADOR_REALIZACAO," +
                    "  DATA_HORA_TRANSFERENCIA_PROCESSO," +
                    "  OBSERVACAO)" +
                    " VALUES (?, ?, (SELECT C.COD_UNIDADE FROM COLABORADOR C WHERE C.CODIGO = ?), ?, ?, ?)" +
                    " RETURNING CODIGO;");
            stmt.setLong(1, processoTransferenciaVeiculo.getCodUnidadeOrigem());
            stmt.setLong(2, processoTransferenciaVeiculo.getCodUnidadeDestino());
            stmt.setLong(3, processoTransferenciaVeiculo.getCodColaboradorRealizacaoTransferencia());
            stmt.setLong(4, processoTransferenciaVeiculo.getCodColaboradorRealizacaoTransferencia());
            stmt.setObject(5, dataHoraSincronizacao);
            stmt.setString(6, StringUtils.trimToNull(processoTransferenciaVeiculo.getObservacao()));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codProcessoTransferenciaVeiculo = rSet.getLong("CODIGO");
                if (codProcessoTransferenciaVeiculo <= 0) {
                    throw new SQLException("Erro ao inserir processo de transferência de veículo:\n" +
                            "codProcessoTransferenciaVeiculo: " + codProcessoTransferenciaVeiculo);
                }

                final Long codUnidadeOrigem = processoTransferenciaVeiculo.getCodUnidadeOrigem();
                final Long codUnidadeDestino = processoTransferenciaVeiculo.getCodUnidadeDestino();
                final Long codColaboradorRealizacaoTransferencia =
                        processoTransferenciaVeiculo.getCodColaboradorRealizacaoTransferencia();
                final List<VeiculoEnvioTransferencia> veiculosTransferencia =
                        processoTransferenciaVeiculo.getVeiculosTransferencia();
                final PneuTransferenciaDao pneuTransferenciaDao = Injection.providePneuTransferenciaDao();
                // Transfere cada placa do Processo.
                for (final VeiculoEnvioTransferencia veiculoTransferencia : veiculosTransferencia) {
                    final Long codveiculo = veiculoTransferencia.getCodVeiculo();
                    // Insere informações da transferência da Placa.
                    final Long codTransferenciaInformacoes =
                            insertTransferenciaVeiculoInformacoes(
                                    conn,
                                    codProcessoTransferenciaVeiculo,
                                    veiculoTransferencia);

                    // Transfere o veículo da Unidade Origem para a Unidade Destino.
                    tranfereVeiculo(conn, codUnidadeOrigem, codUnidadeDestino, codveiculo);

                    // Transfere Pneus, se o veículo tem algum aplicado.
                    if (veiculoTransferencia.temPneusParaTransferir()) {
                        // Verifica se não houve movimentação de pneus no veículo enquanto o processo de
                        // transferência era realziado.
                        verificaPneusVeiculo(
                                conn,
                                codUnidadeOrigem,
                                codveiculo,
                                veiculoTransferencia.getCodPneusAplicadosVeiculo());

                        // Transfere os pneus aplicados na placa da Unidade Origem para a Unidade Destino.
                        final Long codProcessoTransferenciaPneu = pneuTransferenciaDao.insertTransferencia(
                                conn,
                                VeiculoTransferenciaConverter.toPneuTransferenciaRealizacao(
                                        codUnidadeOrigem,
                                        codUnidadeDestino,
                                        codColaboradorRealizacaoTransferencia,
                                        veiculoTransferencia),
                                dataHoraSincronizacao,
                                true);

                        // Atualiza o vínculo entre os pneus transferidos e o veículo transferido.
                        atualizaVinculoPneuVeiculo(
                                conn,
                                codUnidadeOrigem,
                                codUnidadeDestino,
                                codveiculo,
                                veiculoTransferencia.getCodPneusAplicadosVeiculo());

                        // Insere vínculo entre a Transferência do veículo com a Transferência dos Pneus.
                        insereVinculoTransferenciaVeiculoPneu(
                                conn,
                                codTransferenciaInformacoes,
                                codProcessoTransferenciaPneu);
                    }
                }
                conn.commit();
                return codProcessoTransferenciaVeiculo;
            } else {
                throw new SQLException("Não foi possível salvar processo de transferência de veículo");
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private Optional<List<TipoVeiculoDiagrama>> getPlacasSemDiagramaAplicado(
            @NotNull final Connection conn,
            @NotNull final Long codUnidadeOrigem,
            @NotNull final List<Long> codVeiculosTransferidos) throws Throwable {
        final List<TipoVeiculoDiagrama> tiposVeiculoDiagrama =
                getTiposVeiculosDiagramas(conn, codUnidadeOrigem, codVeiculosTransferidos);
        if (tiposVeiculoDiagrama.isEmpty()) {
            return Optional.empty();
        } else {
            final List<TipoVeiculoDiagrama> veiculosSemDiagrama = new ArrayList<>();
            tiposVeiculoDiagrama.forEach(tipoVeiculoDiagrama -> {
                if (!tipoVeiculoDiagrama.isTemDiagramaAssociado()) {
                    veiculosSemDiagrama.add(tipoVeiculoDiagrama);
                }
            });
            return veiculosSemDiagrama.isEmpty()
                    ? Optional.empty()
                    : Optional.of(veiculosSemDiagrama);
        }
    }

    @NotNull
    private List<TipoVeiculoDiagrama> getTiposVeiculosDiagramas(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final List<Long> codVeiculosTransferidos) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_VEICULOS_DIAGRAMAS(?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codVeiculosTransferidos));
            rSet = stmt.executeQuery();
            final List<TipoVeiculoDiagrama> tiposVeiculosDiagrama = new ArrayList<>();
            while (rSet.next()) {
                tiposVeiculosDiagrama.add(VeiculoTransferenciaConverter.createVeiculoSemDiagrama(rSet));
            }
            return tiposVeiculosDiagrama;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long insertTransferenciaVeiculoInformacoes(
            @NotNull final Connection conn,
            final long codProcessoTransferenciaVeiculo,
            @NotNull final VeiculoEnvioTransferencia veiculoEnvioTransferencia) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO" +
                    "  VEICULO_TRANSFERENCIA_INFORMACOES(" +
                    "    COD_PROCESSO_TRANSFERENCIA, " +
                    "    COD_VEICULO, " +
                    "    COD_DIAGRAMA_VEICULO, " +
                    "    COD_TIPO_VEICULO) " +
                    "VALUES (?, " +
                    "        ?, " +
                    "        (SELECT VT.COD_DIAGRAMA " +
                    "         FROM VEICULO V " +
                    "           JOIN VEICULO_TIPO VT " +
                    "             ON V.COD_TIPO = VT.CODIGO " +
                    "         WHERE V.CODIGO = ?), " +
                    "        (SELECT V.COD_TIPO FROM VEICULO V WHERE V.CODIGO = ?)) " +
                    "RETURNING CODIGO;");
            stmt.setLong(1, codProcessoTransferenciaVeiculo);
            stmt.setLong(2, veiculoEnvioTransferencia.getCodVeiculo());
            stmt.setLong(3, veiculoEnvioTransferencia.getCodVeiculo());
            stmt.setLong(4, veiculoEnvioTransferencia.getCodVeiculo());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codTransferenciaInformacoes = rSet.getLong("CODIGO");
                if (codTransferenciaInformacoes <= 0) {
                    throw new SQLException("Não foi possível inserir as informações do processo de transferência:\n" +
                            "codTransferenciaInformacoes: " + codTransferenciaInformacoes);
                }
                return codTransferenciaInformacoes;
            } else {
                throw new SQLException("Não foi possível inserir informações de transferência de veículo:\n" +
                        "codProcessoTransferenciaVeiculo: " + codProcessoTransferenciaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void tranfereVeiculo(@NotNull final Connection conn,
                                 @NotNull final Long codUnidadeOrigem,
                                 @NotNull final Long codUnidadeDestino,
                                 @NotNull final Long codVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE VEICULO " +
                    "SET COD_UNIDADE = ? " +
                    "WHERE CODIGO = ? AND COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidadeDestino);
            stmt.setLong(2, codVeiculo);
            stmt.setLong(3, codUnidadeOrigem);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Erro ao realizar update do código da unidade do veículo:\n" +
                        "codUnidadeOrigem: " + codUnidadeOrigem + "\n" +
                        "codUnidadeDestino: " + codUnidadeDestino + "\n" +
                        "codVeiculo: " + codVeiculo);
            }
        } finally {
            close(stmt);
        }
    }

    private void verificaPneusVeiculo(@NotNull final Connection conn,
                                      @NotNull final Long codUnidadeOrigem,
                                      @NotNull final Long codVeiculo,
                                      @NotNull final List<Long> codPneusAplicadosVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "  COUNT(*) AS QTD_PNEUS_APLICADOS " +
                    "FROM VEICULO_PNEU VP " +
                    "WHERE VP.COD_UNIDADE = ?" +
                    "      AND VP.COD_PNEU = ANY (?)" +
                    "      AND VP.PLACA = (SELECT V.PLACA FROM VEICULO V WHERE V.CODIGO = ?);");
            stmt.setLong(1, codUnidadeOrigem);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codPneusAplicadosVeiculo));
            stmt.setLong(3, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getLong("QTD_PNEUS_APLICADOS") != codPneusAplicadosVeiculo.size()) {
                    // Utilizamos uma GenericException para que a mensagem seja mapeada e mostrada para o usuário.
                    throw new GenericException(
                            "Os pneus do veículo sofreram alterações enquanto a transferência estava sendo realizada");
                }
            } else {
                throw new SQLException("Erro ao verificar se os pneus estão aplicados no veículo correto:\n" +
                        "codUnidadeOrigem: " + codUnidadeOrigem + "\n" +
                        "codVeiculo: " + codVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void atualizaVinculoPneuVeiculo(@NotNull final Connection conn,
                                            @NotNull final Long codUnidadeOrigem,
                                            @NotNull final Long codUnidadeDestino,
                                            @NotNull final Long codveiculo,
                                            @NotNull final List<Long> codPneusAplicadosVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU " +
                    "SET COD_UNIDADE = ? " +
                    "WHERE PLACA = (SELECT PLACA FROM VEICULO WHERE CODIGO = ?) " +
                    "      AND COD_UNIDADE = ? " +
                    "      AND COD_PNEU = ANY(?);");
            stmt.setLong(1, codUnidadeDestino);
            stmt.setLong(2, codveiculo);
            stmt.setLong(3, codUnidadeOrigem);
            stmt.setArray(4, PostgresUtils.listToArray(conn, SqlType.BIGINT, codPneusAplicadosVeiculo));
            if (stmt.executeUpdate() != codPneusAplicadosVeiculo.size()) {
                throw new SQLException("Não foi possível atualizar os vínculos entre pneus e a placa transferida:\n" +
                        "codUnidadeOrigem: " + codUnidadeOrigem + "\n" +
                        "codUnidadeDestino:" + codUnidadeDestino + "\n" +
                        "codveiculo:" + codveiculo + "\n" +
                        "codPneusAplicadosVeiculo:" + codPneusAplicadosVeiculo.toString());
            }
        } finally {
            close(stmt);
        }
    }

    private void insereVinculoTransferenciaVeiculoPneu(
            @NotNull final Connection conn,
            final long codTransferenciaInformacoes,
            @NotNull final Long codProcessoTransferenciaPneu) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "  VEICULO_TRANSFERENCIA_VINCULO_PROCESSO_PNEU(" +
                    "    COD_VEICULO_TRANSFERENCIA_INFORMACOES, " +
                    "    COD_PROCESSO_TRANSFERENCIA_PNEU) " +
                    "VALUES (?, ?);");
            stmt.setLong(1, codTransferenciaInformacoes);
            stmt.setLong(2, codProcessoTransferenciaPneu);
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível inserir vínculo de transferência do veículo com os pneus: \n" +
                        "codTransferenciaInformacoes: " + codTransferenciaInformacoes + "\n" +
                        "codProcessoTransferenciaPneu: " + codProcessoTransferenciaPneu);
            }
        } finally {
            close(stmt);
        }
    }
}
