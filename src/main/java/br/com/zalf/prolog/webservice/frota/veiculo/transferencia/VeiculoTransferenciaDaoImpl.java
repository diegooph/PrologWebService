package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.TipoVeiculoDiagrama;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.VeiculoSemDiagramaException;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.VeiculoTransferenciaConverter;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.AvisoDelecaoTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.StatusDelecaoTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.PneuVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.VeiculoTransferidoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final Long codProcessoTransferencia =
                    internalInsertProcessoTranseferenciaVeiculo(
                            conn,
                            processoTransferenciaVeiculo,
                            checklistOfflineListener);
            conn.commit();
            return codProcessoTransferencia;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn);
        }
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final Connection conn,
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        return internalInsertProcessoTranseferenciaVeiculo(
                conn,
                processoTransferenciaVeiculo,
                checklistOfflineListener);
    }

    @NotNull
    @Override
    public List<VeiculoSelecaoTransferencia> getVeiculosParaSelecaoTransferencia(
            @NotNull final Long codUnidadeOrigem) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_TRANSFERENCIA_VEICULOS_SELECAO(" +
                    "F_COD_UNIDADE_ORIGEM := ?);");
            stmt.setLong(1, codUnidadeOrigem);
            rSet = stmt.executeQuery();
            final List<VeiculoSelecaoTransferencia> veiculosSelecao = new ArrayList<>();
            while (rSet.next()) {
                veiculosSelecao.add(VeiculoTransferenciaConverter.createVeiculoSelecaoTransferencia(rSet));
            }
            return veiculosSelecao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ProcessoTransferenciaVeiculoListagem> getProcessosTransferenciaVeiculoListagem(
            @NotNull final List<Long> codUnidadesOrigem,
            @NotNull final List<Long> codUnidadesDestino,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_VEICULO_TRANSFERENCIA_LISTAGEM_PROCESSOS(?, ?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesOrigem));
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidadesDestino));
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            final List<ProcessoTransferenciaVeiculoListagem> processosTransferencia = new ArrayList<>();
            List<String> placasTransferidas = new ArrayList<>();
            Long codProcessoAntigo = null, codProcessoAtual;
            boolean isFirstLine = true;
            while (rSet.next()) {
                codProcessoAtual = rSet.getLong("COD_PROCESSO_TRANFERENCIA");
                if (codProcessoAntigo == null) {
                    codProcessoAntigo = codProcessoAtual;
                }

                if (isFirstLine) {
                    // Processa a primeira linha do ResultSet.
                    processosTransferencia.add(VeiculoTransferenciaConverter.createProcessoTransferenciaVeiculoListagem(
                            rSet,
                            codProcessoAtual,
                            placasTransferidas));
                    isFirstLine = false;
                }

                // Trocou o processo de transferencia.
                if (!codProcessoAntigo.equals(codProcessoAtual)) {
                    placasTransferidas = new ArrayList<>();
                    processosTransferencia.add(VeiculoTransferenciaConverter.createProcessoTransferenciaVeiculoListagem(
                            rSet,
                            codProcessoAtual,
                            placasTransferidas));
                }
                placasTransferidas.add(rSet.getString("PLACA_TRANSFERIDA"));
                codProcessoAntigo = codProcessoAtual;
            }
            return processosTransferencia;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            @NotNull final Long codProcessoTransferencia) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_TRANSFERENCIA_VISUALIZACAO_PROCESSO(?);");
            stmt.setLong(1, codProcessoTransferencia);
            rSet = stmt.executeQuery();
            ProcessoTransferenciaVeiculoVisualizacao processoTransferenciaVeiculo = null;
            final List<VeiculoTransferidoVisualizacao> veiculosTransferidos = new ArrayList<>();
            List<String> codPneusTransferidos = new ArrayList<>();
            boolean isFirstLine = true;
            Long codVeiculoAntigo = null, codVeiculoAtual;
            while (rSet.next()) {
                codVeiculoAtual = rSet.getLong("COD_VEICULO_TRANSFERIDO");
                if (codVeiculoAntigo == null) {
                    codVeiculoAntigo = codVeiculoAtual;
                }

                if (isFirstLine) {
                    // Processa a primeira linha do ResultSet.
                    processoTransferenciaVeiculo =
                            VeiculoTransferenciaConverter
                                    .createProcessoTransferenciaVeiculo(rSet, veiculosTransferidos);
                    veiculosTransferidos.add(
                            VeiculoTransferenciaConverter
                                    .createVeiculoTransferidoVisualizacao(rSet, codVeiculoAtual, codPneusTransferidos));
                    isFirstLine = false;
                }

                if (!codVeiculoAntigo.equals(codVeiculoAtual)) {
                    // Trocou de Veículo.
                    codPneusTransferidos = new ArrayList<>();
                    veiculosTransferidos.add(
                            VeiculoTransferenciaConverter
                                    .createVeiculoTransferidoVisualizacao(rSet, codVeiculoAtual, codPneusTransferidos));
                }
                final String codClientePneuTransferido = rSet.getString("COD_CLIENTE_PNEU_TRANSFERIDO");
                if (codClientePneuTransferido != null) {
                    codPneusTransferidos.add(codClientePneuTransferido);
                }
                // Atualiza placa que foi processada.
                codVeiculoAntigo = codVeiculoAtual;
            }
            if (processoTransferenciaVeiculo == null) {
                throw new IllegalStateException("Nenhum dado retornado no ResultSet para processamento");
            }
            return processoTransferenciaVeiculo;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public DetalhesVeiculoTransferido getDetalhesVeiculoTransferido(
            @NotNull final Long codProcessoTransferencia,
            @NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_VEICULO_TRANSFERENCIA_DETALHES_PLACA_TRANSFERIDA(?, ?);");
            stmt.setLong(1, codProcessoTransferencia);
            stmt.setLong(2, codVeiculo);
            rSet = stmt.executeQuery();
            DetalhesVeiculoTransferido detalhesVeiculoTransferido = null;
            final List<PneuVeiculoTransferido> pneusAplicadosMomentoTransferencia = new ArrayList<>();
            boolean isFirstLine = true;
            while (rSet.next()) {
                if (isFirstLine) {
                    detalhesVeiculoTransferido = VeiculoTransferenciaConverter
                            .createDetalhesVeiculoTransferido(rSet, pneusAplicadosMomentoTransferencia);
                    isFirstLine = false;
                }
                // Garantimos que só vamos adicionar informação do pneu, se ele não for null.
                final long codPneu = rSet.getLong("COD_PNEU");
                if (codPneu > 0) {
                    pneusAplicadosMomentoTransferencia.add(
                            VeiculoTransferenciaConverter.createPneuVeiculoTransferido(rSet));
                }
            }
            if (detalhesVeiculoTransferido == null) {
                throw new IllegalStateException("Nenhum dado retornado no ResultSet para processamento.");
            }
            return detalhesVeiculoTransferido;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public AvisoDelecaoTransferenciaVeiculo buscaAvisoDelecaoAutomaticaPorTransferencia(@NotNull final Long codEmpresa)
            throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalBuscaAvisoDelecaoAutomaticaPorTransferencia(conn, codEmpresa);
        } finally {
            close(conn);
        }
    }

    @NotNull
    private AvisoDelecaoTransferenciaVeiculo internalBuscaAvisoDelecaoAutomaticaPorTransferencia(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_TRANSFERENCIA_BUSCA_AVISO_BLOQUEIO(" +
                    "F_COD_EMPRESA := ?);");
            stmt.setLong(1, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new AvisoDelecaoTransferenciaVeiculo(
                        StatusDelecaoTransferenciaVeiculo.create(
                                rSet.getBoolean("BLOQUEAR_DELECAO_OS_CHECKLIST"),
                                rSet.getBoolean("BLOQUEAR_DELECAO_SERVICOS_PNEU")),
                        rSet.getString("AVISO_BLOQUEIO_TELA_TRANSFERENCIA"));
            } else {
                throw new IllegalStateException("Aviso de transferência não encontrado para a empresa: " + codEmpresa);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long internalInsertProcessoTranseferenciaVeiculo(
            @NotNull final Connection conn,
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // É importante que esta verificação seja executa antes de qualquer outro processamento para inserir uma
            // Transferência de veículo.
            // Essa verificação nos garantirá que nenhum dos veículos transferidos está SEM DIAGRAMA aplicado.
            // Retornamos uma Exception específica para tratar estes casos.
            final Optional<List<TipoVeiculoDiagrama>> tipoVeiculoSemDiagramas = getVeiculosSemDiagramaAplicado(
                    conn,
                    processoTransferenciaVeiculo.getCodVeiculosTransferencia());
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

            final OffsetDateTime dataHoraRealizacaoProcesso = Now.getOffsetDateTimeUtc();
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
            stmt.setObject(5, dataHoraRealizacaoProcesso);
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
                final PneuTransferenciaDao pneuTransferenciaDao = Injection.providePneuTransferenciaDao();
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();

                final AvisoDelecaoTransferenciaVeiculo avisoDelecao = internalBuscaAvisoDelecaoAutomaticaPorTransferencia(
                        conn,
                        processoTransferenciaVeiculo.getCodEmpresa());

                // Transfere cada placa do Processo.
                for (final Long codVeiculoTransferido : processoTransferenciaVeiculo.getCodVeiculosTransferencia()) {
                    // Insere informações da transferência da Placa.
                    final Long codTransferenciaInformacoes =
                            insertTransferenciaVeiculoInformacoes(
                                    conn,
                                    codProcessoTransferenciaVeiculo,
                                    codVeiculoTransferido);

                    if (avisoDelecao.deveDeletarItensOrdemServicoChecklist()) {
                        // Deleta os itens de O.S. em aberto do veículo transferido.
                        deletaItensOrdemServicoChecklistVeiculoTransferido(
                                conn,
                                codVeiculoTransferido,
                                codTransferenciaInformacoes,
                                dataHoraRealizacaoProcesso);
                    }

                    // Transfere o veículo da Unidade Origem para a Unidade Destino.
                    tranfereVeiculo(conn, codUnidadeOrigem, codUnidadeDestino, codVeiculoTransferido);

                    // Se o veículo tiver pneus, eles também serão transferidos.
                    final Optional<List<Long>> codPneusAplicadosVeiculo = veiculoDao
                            .getCodPneusAplicadosVeiculo(conn, codVeiculoTransferido);
                    if (codPneusAplicadosVeiculo.isPresent()) {

                        if (avisoDelecao.deveDeletarServicosPneus()) {
                            // Deleta os serviços de pneus em aberto do pneu transferido.
                            // TODO: Esse FOR aqui, dentro da func ou utilizar um batch?
                            for (final Long codPneu : codPneusAplicadosVeiculo.get()) {
                                deletaServicosPneusTransferido(
                                        conn,
                                        codVeiculoTransferido,
                                        codPneu,
                                        codTransferenciaInformacoes,
                                        dataHoraRealizacaoProcesso);
                            }
                        }

                        // Transfere os pneus aplicados na placa da Unidade Origem para a Unidade Destino.
                        final Long codProcessoTransferenciaPneu = pneuTransferenciaDao.insertTransferencia(
                                conn,
                                VeiculoTransferenciaConverter.toPneuTransferenciaRealizacao(
                                        codUnidadeOrigem,
                                        codUnidadeDestino,
                                        codColaboradorRealizacaoTransferencia,
                                        codPneusAplicadosVeiculo.get()),
                                dataHoraRealizacaoProcesso,
                                true);

                        // Atualiza o vínculo entre os pneus transferidos e o veículo transferido.
                        atualizaVinculoPneuVeiculo(
                                conn,
                                codUnidadeOrigem,
                                codUnidadeDestino,
                                codVeiculoTransferido,
                                codPneusAplicadosVeiculo.get());

                        // Insere vínculo entre a Transferência do veículo com a Transferência dos Pneus.
                        insereVinculoTransferenciaVeiculoPneu(
                                conn,
                                codTransferenciaInformacoes,
                                codProcessoTransferenciaPneu);
                    }
                }

                // Listener irá atualizar a versão dos dados do checklist offline para as unidades envolvidas no
                // processo.
                checklistOfflineListener.onVeiculosTransferidos(
                        conn,
                        processoTransferenciaVeiculo.getCodUnidadeOrigem(),
                        processoTransferenciaVeiculo.getCodUnidadeDestino());
                return codProcessoTransferenciaVeiculo;
            } else {
                throw new SQLException("Não foi possível salvar processo de transferência de veículo");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Optional<List<TipoVeiculoDiagrama>> getVeiculosSemDiagramaAplicado(
            @NotNull final Connection conn,
            @NotNull final List<Long> codVeiculosTransferidos) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_GET_VEICULOS_DIAGRAMAS(" +
                    "F_COD_VEICULOS := ?, " +
                    "F_FILTRO_VEICULO_POSSUI_DIAGRAMA := ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codVeiculosTransferidos));
            stmt.setBoolean(2, false);
            rSet = stmt.executeQuery();
            // Usando if com do/while nós só criamos o ArrayList se existir algo no ResultSet.
            if (rSet.next()) {
                final List<TipoVeiculoDiagrama> tiposVeiculosDiagrama = new ArrayList<>();
                do {
                    tiposVeiculosDiagrama.add(VeiculoTransferenciaConverter.createVeiculoSemDiagrama(rSet));
                } while (rSet.next());
                return Optional.of(tiposVeiculosDiagrama);
            } else {
                return Optional.empty();
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private Long insertTransferenciaVeiculoInformacoes(
            @NotNull final Connection conn,
            final long codProcessoTransferenciaVeiculo,
            @NotNull final Long codVeiculoTransferido) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO" +
                    "  VEICULO_TRANSFERENCIA_INFORMACOES(" +
                    "    COD_PROCESSO_TRANSFERENCIA, " +
                    "    COD_VEICULO, " +
                    "    COD_DIAGRAMA_VEICULO, " +
                    "    COD_TIPO_VEICULO," +
                    "    KM_VEICULO_MOMENTO_TRANSFERENCIA) " +
                    "VALUES (?, " +
                    "        ?, " +
                    "        (SELECT VT.COD_DIAGRAMA " +
                    "         FROM VEICULO V " +
                    "           JOIN VEICULO_TIPO VT " +
                    "             ON V.COD_TIPO = VT.CODIGO " +
                    "         WHERE V.CODIGO = ?), " +
                    "        (SELECT V.COD_TIPO FROM VEICULO V WHERE V.CODIGO = ?)," +
                    "        (SELECT V.KM FROM VEICULO V WHERE V.CODIGO = ?)) " +
                    "RETURNING CODIGO;");
            stmt.setLong(1, codProcessoTransferenciaVeiculo);
            stmt.setLong(2, codVeiculoTransferido);
            stmt.setLong(3, codVeiculoTransferido);
            stmt.setLong(4, codVeiculoTransferido);
            stmt.setLong(5, codVeiculoTransferido);
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

    private void deletaItensOrdemServicoChecklistVeiculoTransferido(
            @NotNull final Connection conn,
            @NotNull final Long codVeiculoTransferido,
            @NotNull final Long codTransferenciaInformacoes,
            @NotNull final OffsetDateTime dataHoraRealizacaoProcesso) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(" +
                    "F_COD_VEICULO                           := ?," +
                    "F_COD_TRANSFERENCIA_VEICULO_INFORMACOES := ?," +
                    "F_DATA_HORA_REALIZACAO_TRANSFERENCIA    := ?)}");
            stmt.setLong(1, codVeiculoTransferido);
            stmt.setLong(2, codTransferenciaInformacoes);
            stmt.setObject(3, dataHoraRealizacaoProcesso);
            stmt.execute();
        } finally {
            close(stmt);
        }
    }

    private void deletaServicosPneusTransferido(
            @NotNull final Connection conn,
            @NotNull final Long codVeiculoTransferido,
            @NotNull final Long codPneu,
            @NotNull final Long codTransferenciaInformacoes,
            @NotNull final OffsetDateTime dataHoraRealizacaoProcesso) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall("{CALL FUNC_VEICULO_TRANSFERENCIA_DELETA_SERVICOS_PNEU(" +
                    "F_COD_VEICULO                           := ?," +
                    "F_COD_PNEU                              := ?," +
                    "F_COD_TRANSFERENCIA_VEICULO_INFORMACOES := ?," +
                    "F_DATA_HORA_REALIZACAO_TRANSFERENCIA    := ?)}");
            stmt.setLong(1, codVeiculoTransferido);
            stmt.setLong(2, codPneu);
            stmt.setLong(3, codTransferenciaInformacoes);
            stmt.setObject(4, dataHoraRealizacaoProcesso);
            stmt.execute();
        } finally {
            close(stmt);
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

    private void atualizaVinculoPneuVeiculo(@NotNull final Connection conn,
                                            @NotNull final Long codUnidadeOrigem,
                                            @NotNull final Long codUnidadeDestino,
                                            @NotNull final Long codveiculo,
                                            @NotNull final List<Long> codPneusAplicadosVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU " +
                    "SET COD_UNIDADE = ? " +
                    "WHERE COD_VEICULO = ? " +
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
