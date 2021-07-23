package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoV2;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicoHolderBuscaFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicosAbertosBuscaFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicosFechadosVeiculoFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.VeiculoAberturaServicoFiltro;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoBackwardHelper;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {
    private static final String TAG = ServicoDaoImpl.class.getSimpleName();

    @NotNull
    @Override
    public Long criaServico(@NotNull final Connection conn,
                            @NotNull final Long codUnidade,
                            @NotNull final Long codPneu,
                            @NotNull final Long codAfericao,
                            @NotNull final TipoServico tipoServico) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, " +
                                                 "COD_UNIDADE, TIPO_SERVICO) VALUES(?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, codAfericao);
            stmt.setLong(2, codPneu);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, tipoServico.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao criar serviço");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public void incrementaQtdApontamentosServico(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Long codPneu,
                                                 @NotNull final TipoServico tipoServico) throws SQLException {
        Log.d(TAG, "Atualizando quantidade de apontamos do pneu: " + codPneu + " da unidade: " + codUnidade);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                                                 + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU =" +
                                                 " ? AND COD_UNIDADE = ? AND "
                                                 + "TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
                                                 + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND " +
                                                 "DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, tipoServico.asString());
            stmt.setLong(4, codPneu);
            stmt.setLong(5, codUnidade);
            stmt.setString(6, tipoServico.asString());
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @Override
    public void convertServico(@NotNull final Connection conn,
                               @NotNull final Long codUnidade,
                               @NotNull final Long codPneu,
                               @NotNull final TipoServico tipoServicoOriginal,
                               @NotNull final TipoServico tipoServicoNovo) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO " +
                                                 "SET QT_APONTAMENTOS = QT_APONTAMENTOS + 1, " +
                                                 "TIPO_SERVICO = ? " +
                                                 "WHERE COD_PNEU = ? " +
                                                 "AND COD_UNIDADE = ? " +
                                                 "AND TIPO_SERVICO = ? " +
                                                 "AND DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setString(1, tipoServicoNovo.asString());
            stmt.setLong(2, codPneu);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, tipoServicoOriginal.asString());
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    @Override
    public List<TipoServico> getServicosCadastradosByPneu(@NotNull final Long codUnidade,
                                                          @NotNull final Long codPneu) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<TipoServico> servicos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TIPO_SERVICO, COUNT(TIPO_SERVICO) "
                                                 + "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? " +
                                                 "AND DATA_HORA_RESOLUCAO IS NULL "
                                                 + "GROUP BY TIPO_SERVICO "
                                                 + "ORDER BY TIPO_SERVICO");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                servicos.add(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return servicos;
    }

    @Override
    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getQuantidadeServicosAbertosVeiculo(conn, codUnidade);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicosAbertosHolder(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ServicoHolder getServicoHolder(@NotNull final ServicoHolderBuscaFiltro filtro)
            throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            final List<Servico> servicos = internalGetServicosAbertosByCodVeiculo(
                    conn,
                    filtro.getCodVeiculo(),
                    null);
            //  Se não existirem serviços para a placa buscada, nada mais será setado no Holder.
            Restricao restricao = null;
            FormaColetaDadosAfericaoEnum formaColetaDadosAfericaoEnum = null;
            List<Alternativa> alternativasInspecao = null;
            if (!servicos.isEmpty()) {
                Log.d(TAG, "Existem serviços para o codVeículo: " + filtro.getCodVeiculo());
                final AfericaoDaoV2 afericaoDao = Injection.provideAfericaoDao();
                restricao = afericaoDao.getRestricaoByCodUnidade(conn, filtro.getCodUnidade());
                formaColetaDadosAfericaoEnum = getFormaColetaDadosFechamentoServico(conn, filtro.getCodVeiculo());
                if (contains(servicos, TipoServico.INSPECAO)) {
                    Log.d(TAG, "Contém inspeção");
                    alternativasInspecao = getAlternativasInspecao(conn);
                }
            }

            return new ServicoHolder(
                    filtro.getCodVeiculo(),
                    filtro.getPlacaVeiculo(),
                    servicos,
                    restricao,
                    formaColetaDadosAfericaoEnum,
                    alternativasInspecao);
        } finally {
            close(conn);
        }
    }

    @Override
    public List<Servico> getServicosAbertos(@NotNull final ServicosAbertosBuscaFiltro filtro) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return internalGetServicosAbertosByCodVeiculo(conn, filtro.getCodVeiculo(), filtro.getTipoServico());
        } finally {
            close(conn);
        }
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            servico.setCodVeiculo(getCodVeiculoServico(servico, getColaboradorServico(servico)));
            final PneuDao pneuDao = Injection.providePneuDao();
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            final Colaborador colaborador = Injection.provideColaboradorDao().getByCpf(
                    servico.getColaboradorResponsavelFechamento().getCpf(),
                    true);
            final Long kmFinal =
                    updateKm(codUnidade, dataHorafechamentoServico, servico, conn, veiculoDao, colaborador);
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    fechaCalibragem(conn, pneuDao, dataHorafechamentoServico, (ServicoCalibragem) servico, kmFinal);
                    break;
                case INSPECAO:
                    fechaInspecao(conn, pneuDao, dataHorafechamentoServico, (ServicoInspecao) servico, kmFinal);
                    break;
                case MOVIMENTACAO:
                    final ServicoMovimentacao movimentacao = (ServicoMovimentacao) servico;
                    final MovimentacaoDao movimentacaoDao = Injection.provideMovimentacaoDao();
                    // Atualiza o pneuNovo com os valores referentes ao serviço executado.
                    movimentacao.getPneuNovo().setSulcosAtuais(movimentacao.getSulcosColetadosFechamento());
                    movimentacao.getPneuNovo().setPosicao(movimentacao.getPneuComProblema().getPosicao());
                    final ProcessoMovimentacao processoMovimentacao =
                            convertServicoToProcessoMovimentacao(codUnidade, movimentacao);
                    final Long codProcessoMovimentacao = movimentacaoDao.insertMovimentacaoServicoAfericao(
                            conn,
                            this,
                            Injection.provideCampoPersonalizadoDao(),
                            processoMovimentacao,
                            dataHorafechamentoServico,
                            false);
                    movimentacao.setCodProcessoMovimentacao(codProcessoMovimentacao);

                    // Como impedimos o processo de movimentação de fechar automaticamente todos os serviços,
                    // nós agora fechamos o de movimentação como sendo fechado pelo usuário e depois os demais que
                    // ficarem pendentes do mesmo pneu. Essa ordem de execução dos métodos é necessária e não deve
                    // ser alterada!

                    /* TODO esse km final é coletado aqui em cima.
                     * */
                    fechaMovimentacao(conn, pneuDao, dataHorafechamentoServico, movimentacao, kmFinal);
                    final Long codPneu = servico.getPneuComProblema().getCodigo();
                    final int qtdServicosEmAbertoPneu = getQuantidadeServicosEmAbertoPneu(
                            codUnidade,
                            codPneu,
                            conn);
                    if (qtdServicosEmAbertoPneu > 0) {
                        final int qtdServicosFechadosPneu = fecharAutomaticamenteTodosServicosPneu(
                                conn,
                                codUnidade,
                                codPneu,
                                codProcessoMovimentacao,
                                dataHorafechamentoServico,
                                servico.getKmVeiculoMomentoFechamento(),
                                OrigemFechamentoAutomaticoEnum.MOVIMENTACAO);
                        if (qtdServicosEmAbertoPneu != qtdServicosFechadosPneu) {
                            throw new IllegalStateException("Erro ao fechar os serviços do pneu: " + codPneu + ". " +
                                                                    "Deveriam ser fechados "
                                                                    + qtdServicosEmAbertoPneu + " serviços mas foram " +
                                                                    "fechados " + qtdServicosFechadosPneu + "!");
                        }
                    } else {
                        Log.d(TAG, "Não existem serviços em aberto para o pneu: " + codPneu);
                    }
                    break;
            }
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Override
    public Servico getServicoByCod(final Long codUnidade, final Long codServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicoByCod(conn, codUnidade, codServico);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ServicoConverter.createServico(rSet, true);
            } else {
                throw new SQLException("Erro ao buscar serviço com código: " + codServico);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getQuantidadeServicosFechadosByVeiculo(final Long codUnidade,
                                                                         final long dataInicial,
                                                                         final long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getQuantidadeServicosFechadosVeiculo(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            final ServicosFechadosHolder servicosFechadosHolder = new ServicosFechadosHolder();
            final List<QuantidadeServicos> quantidadeServicosFechados = new ArrayList<>();
            while (rSet.next()) {
                quantidadeServicosFechados.add(ServicoConverter.createQtdServicosVeiculo(rSet));
            }
            servicosFechadosHolder.setServicosFechados(quantidadeServicosFechados);
            return servicosFechadosHolder;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getQuantidadeServicosFechadosByPneu(final Long codUnidade,
                                                                      final long dataInicial,
                                                                      final long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getQuantidadeServicosFechadosPneu(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            final ServicosFechadosHolder servicosFechadosHolder = new ServicosFechadosHolder();
            final List<QuantidadeServicos> quantidadeServicosFechados = new ArrayList<>();
            while (rSet.next()) {
                quantidadeServicosFechados.add(ServicoConverter.createQtdServicosPneu(rSet));
            }
            servicosFechadosHolder.setServicosFechados(quantidadeServicosFechados);
            return servicosFechadosHolder;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechados(final Long codUnidade,
                                             final long dataInicial,
                                             final long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechados(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechadosPneu(final Long codUnidade,
                                                 final Long codPneu,
                                                 final long dataInicial,
                                                 final long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechadosPneu(conn, codUnidade, codPneu, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull List<Servico> getServicosFechadosVeiculo(@NotNull final ServicosFechadosVeiculoFiltro filtro)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechadosVeiculo(conn,
                                                                 filtro.getCodUnidade(),
                                                                 filtro.getCodVeiculo(),
                                                                 filtro.getDataInicial(),
                                                                 filtro.getDataFinal());
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public int getQuantidadeServicosEmAbertoPneu(final Long codUnidade,
                                                 final Long codPneu,
                                                 final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.getQuantidadeServicosEmAbertoPneu(conn, codUnidade, codPneu);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ServicoConverter.getQuantidadeServicosEmAbertoPneu(rSet);
            } else {
                throw new SQLException("Erro ao buscar quantidade de serviços em aberto para o pneu: " + codPneu);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @Override
    public int fecharAutomaticamenteTodosServicosPneu(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final Long codProcesso,
            @NotNull final OffsetDateTime dataHorafechamentoServico,
            final long kmColetadoVeiculo,
            @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException {
        int qtdServicosFechados = 0;
        qtdServicosFechados += fecharAutomaticamenteServicosInspecaoPneu(conn,
                                                                         codUnidade,
                                                                         codPneu,
                                                                         codProcesso,
                                                                         dataHorafechamentoServico,
                                                                         kmColetadoVeiculo,
                                                                         origemFechamentoServico);
        qtdServicosFechados += fecharAutomaticamenteServicosCalibragemPneu(conn,
                                                                           codUnidade,
                                                                           codPneu,
                                                                           codProcesso,
                                                                           dataHorafechamentoServico,
                                                                           kmColetadoVeiculo,
                                                                           origemFechamentoServico);
        qtdServicosFechados += fecharAutomaticamenteServicosMovimentacaoPneu(conn,
                                                                             codUnidade,
                                                                             codPneu,
                                                                             codProcesso,
                                                                             dataHorafechamentoServico,
                                                                             kmColetadoVeiculo,
                                                                             origemFechamentoServico);
        return qtdServicosFechados;
    }

    @Override
    public int fecharAutomaticamenteServicosInspecaoPneu(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final Long codProcesso,
            @NotNull final OffsetDateTime dataHorafechamentoServico,
            final long kmColetadoVeiculo,
            @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fecharAutomaticamenteServicosPneu(
                    conn,
                    codUnidade,
                    codProcesso,
                    codPneu,
                    dataHorafechamentoServico,
                    kmColetadoVeiculo,
                    origemFechamentoServico,
                    TipoServico.INSPECAO);
            return stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @Override
    public int fecharAutomaticamenteServicosCalibragemPneu(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final Long codProcesso,
            @NotNull final OffsetDateTime dataHorafechamentoServico,
            final long kmColetadoVeiculo,
            @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fecharAutomaticamenteServicosPneu(
                    conn,
                    codUnidade,
                    codProcesso,
                    codPneu,
                    dataHorafechamentoServico,
                    kmColetadoVeiculo,
                    origemFechamentoServico,
                    TipoServico.CALIBRAGEM);
            return stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @Override
    public int fecharAutomaticamenteServicosMovimentacaoPneu(@NotNull final Connection conn,
                                                             @NotNull final Long codUnidade,
                                                             @NotNull final Long codPneu,
                                                             @NotNull final Long codProcesso,
                                                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                                                             final long kmColetadoVeiculo,
                                                             @NotNull final OrigemFechamentoAutomaticoEnum origemFechamentoServico)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fecharAutomaticamenteServicosPneu(
                    conn,
                    codUnidade,
                    codProcesso,
                    codPneu,
                    dataHorafechamentoServico,
                    kmColetadoVeiculo,
                    origemFechamentoServico,
                    TipoServico.MOVIMENTACAO);
            return stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final VeiculoAberturaServicoFiltro filtro)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getVeiculoAberturaServico(conn, filtro.getCodVeiculo(), filtro.getCodServico());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final VeiculoServico veiculo = ServicoConverter.createVeiculoAberturaServico(rSet);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                final Optional<DiagramaVeiculo> diagrama =
                        veiculoDao.getDiagramaVeiculoByPlaca(veiculo.getPlaca(), veiculo.getCodUnidadeAlocado());
                // Fazemos direto um get() no Optional pois se não existir diagrama é melhor dar crash aqui do que no
                // aplicativo, por exemplo.
                veiculo.setDiagrama(diagrama.get());
                return veiculo;
            } else {
                throw new SQLException("Erro ao buscar veículo do serviço");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private Long updateKm(final @NotNull Long codUnidade,
                          final @NotNull OffsetDateTime dataHorafechamentoServico,
                          final @NotNull Servico servico,
                          final Connection conn,
                          final VeiculoDao veiculoDao,
                          final Colaborador colaborador) throws Throwable {
        return veiculoDao.updateKmByCodVeiculo(conn,
                                               codUnidade,
                                               VeiculoBackwardHelper.getCodVeiculoByPlaca(
                                                       Injection.provideColaboradorDao()
                                                               .getCodColaboradorByCpfAndCodEmpresa(
                                                                       conn,
                                                                       colaborador.getCodEmpresa(),
                                                                       colaborador.getCpfAsString()),
                                                       servico.getPlacaVeiculo()),
                                               servico.getCodigo(),
                                               VeiculoTipoProcesso.FECHAMENTO_SERVICO_PNEU,
                                               dataHorafechamentoServico,
                                               servico.getKmVeiculoMomentoFechamento(),
                                               true);
    }

    @NotNull
    private Long getCodVeiculoServico(final @NotNull Servico servico, final Colaborador colaborador) {
        return VeiculoBackwardHelper.getCodVeiculoByPlaca(
                colaborador.getCodigo(),
                servico.getPlacaVeiculo());
    }

    private Colaborador getColaboradorServico(final @NotNull Servico servico) throws SQLException {
        return Injection.provideColaboradorDao().getByCpf(
                servico.getColaboradorResponsavelFechamento().getCpf(),
                true);
    }

    @NotNull
    private FormaColetaDadosAfericaoEnum getFormaColetaDadosFechamentoServico(@NotNull final Connection conn,
                                                                              @NotNull final Long codVeiculo)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select " +
                                                 "actav.forma_coleta_dados_fechamento_servico " +
                                                 "from afericao_configuracao_tipo_afericao_veiculo actav " +
                                                 "join veiculo v on actav.cod_tipo_veiculo = v.cod_tipo and actav" +
                                                 ".cod_unidade = v.cod_unidade " +
                                                 "where v.codigo = ?;");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return FormaColetaDadosAfericaoEnum.fromString(rSet.getString(1));
            } else {
                // Se não houver parametrização definida, retornamos a default que é EQUIPAMENTO.
                return FormaColetaDadosAfericaoEnum.EQUIPAMENTO;
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private List<Servico> internalGetServicosAbertosByCodVeiculo(@NotNull final Connection conn,
                                                                 @NotNull final Long codVeiculo,
                                                                 @Nullable final TipoServico tipoServico)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.getServicosAbertosByCodVeiculo(conn, codVeiculo, tipoServico);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    private ProcessoMovimentacao convertServicoToProcessoMovimentacao(@NotNull final Long codUnidade,
                                                                      @NotNull final ServicoMovimentacao servico) {
        final Veiculo veiculo = new Veiculo();
        veiculo.setCodigo(servico.getCodVeiculo());
        veiculo.setPlaca(servico.getPlacaVeiculo());
        veiculo.setKmAtual(servico.getKmVeiculoMomentoFechamento());

        // O pneu com problema saiu do veículo e deve ser adicionado em estoque.
        final OrigemVeiculo origemVeiculo = new OrigemVeiculo(veiculo, servico.getPneuComProblema().getPosicao());
        final DestinoEstoque destinoEstoque = new DestinoEstoque();
        final Movimentacao movimentacaoPneuRemovido = new Movimentacao(
                null,
                servico.getPneuComProblema(),
                origemVeiculo,
                destinoEstoque,
                null,
                null);

        // O pneu inserido foi selecionado do estoque e deve ser movido para o veículo na mesma posição onde o pneu com
        // problema se encontra atualmente.
        final OrigemEstoque origemEstoque = new OrigemEstoque();
        final DestinoVeiculo destinoVeiculo = new DestinoVeiculo(veiculo, servico.getPneuComProblema().getPosicao());
        final Movimentacao movimentacaoPneuInserido = new Movimentacao(
                null,
                servico.getPneuNovo(),
                origemEstoque,
                destinoVeiculo,
                null,
                null);

        // Cria o processo da movimentação.
        final List<Movimentacao> movimentacoes = new ArrayList<>();
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(servico.getCpfResponsavelFechamento());
        final Unidade unidade = new Unidade();
        unidade.setCodigo(codUnidade);
        movimentacoes.add(movimentacaoPneuRemovido);
        movimentacoes.add(movimentacaoPneuInserido);
        return new ProcessoMovimentacao(
                null,
                unidade,
                movimentacoes,
                colaborador,
                Now.getTimestampUtc(),
                "Fechamento de serviço");
    }

    private boolean contains(@NotNull final List<Servico> servicos,
                             @NotNull final TipoServico tipoServico) {
        for (final Servico servico : servicos) {
            if (servico.getTipoServico().equals(tipoServico)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private List<Alternativa> getAlternativasInspecao(@NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.getAlternativasInspecao(conn);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Alternativa> alternativas = new ArrayList<>();
                do {
                    final AlternativaChecklist alternativa = new AlternativaChecklist();
                    alternativa.codigo = rSet.getLong("CODIGO");
                    alternativa.alternativa = rSet.getString("ALTERNATIVA");
                    alternativas.add(alternativa);
                } while (rSet.next());
                return alternativas;
            }
        } finally {
            close(stmt, rSet);
        }
        return Collections.emptyList();
    }

    private Long fechaCalibragem(@NotNull final Connection conn,
                                 @NotNull final PneuDao pneuDao,
                                 @NotNull final OffsetDateTime dataHorafechamentoServico,
                                 @NotNull final ServicoCalibragem servico,
                                 @NotNull final Long kmFinal) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.fechaCalibragem(conn, dataHorafechamentoServico, servico, kmFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                pneuDao.updatePressao(
                        conn,
                        servico.getPneuComProblema().getCodigo(),
                        servico.getPressaoColetadaFechamento());
                return rSet.getLong("codigo");
            } else {
                throw new SQLException("Erro ao inserir o item consertado");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private Long fechaInspecao(@NotNull final Connection conn,
                               @NotNull final PneuDao pneuDao,
                               @NotNull final OffsetDateTime dataHorafechamentoServico,
                               @NotNull final ServicoInspecao servico,
                               @NotNull final Long kmFinal) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.fechaInspecao(conn, dataHorafechamentoServico, servico, kmFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                pneuDao.updatePressao(
                        conn,
                        servico.getPneuComProblema().getCodigo(),
                        servico.getPressaoColetadaFechamento());
                return rSet.getLong("codigo");
            } else {
                throw new SQLException("Erro ao inserir o item consertado");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private Long fechaMovimentacao(@NotNull final Connection conn,
                                   @NotNull final PneuDao pneuDao,
                                   @NotNull final OffsetDateTime dataHorafechamentoServico,
                                   @NotNull final ServicoMovimentacao servico,
                                   @NotNull final Long kmFinal) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = ServicoQueryBinder.fechaMovimentacao(conn, dataHorafechamentoServico, servico, kmFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                // No caso da movimentação precisamos atualizar o Pneu Novo.
                pneuDao.updatePressao(
                        conn,
                        servico.getPneuNovo().getCodigo(),
                        servico.getPressaoColetadaFechamento());
                pneuDao.updateSulcos(
                        conn,
                        servico.getPneuNovo().getCodigo(),
                        servico.getSulcosColetadosFechamento());
                return rSet.getLong("codigo");
            } else {
                throw new SQLException("Erro ao inserir o item consertado");
            }
        } finally {
            close(stmt, rSet);
        }
    }
}