package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            closeConnection(null, stmt, rSet);
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
                    + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND "
                    + "TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
                    + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, tipoServico.asString());
            stmt.setLong(4, codPneu);
            stmt.setLong(5, codUnidade);
            stmt.setString(6, tipoServico.asString());
            stmt.executeUpdate();
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    public void calibragemToInspecao(@NotNull final Connection conn,
                                     @NotNull final Long codUnidade,
                                     @NotNull final Long codPneu) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                    + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND "
                    + "TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1, TIPO_SERVICO = ? "
                    + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            stmt.setString(3, TipoServico.CALIBRAGEM.asString());
            stmt.setString(4, TipoServico.INSPECAO.asString());
            stmt.setLong(5, codPneu);
            stmt.setLong(6, codUnidade);
            stmt.setString(7, TipoServico.CALIBRAGEM.asString());
            stmt.executeUpdate();
        } finally {
            closeStatement(stmt);
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
                    + "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND DATA_HORA_RESOLUCAO IS NULL "
                    + "GROUP BY TIPO_SERVICO "
                    + "ORDER BY TIPO_SERVICO");
            stmt.setLong(1, codPneu);
            stmt.setLong(2, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                servicos.add(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return servicos;
    }

    @Override
    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getQuantidadeServicosAbertosVeiculo(conn, codUnidade);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicosAbertosHolder(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public ServicoHolder getServicoHolder(String placa, Long codUnidade) throws Throwable {
        final ServicoHolder holder = new ServicoHolder();
        holder.setPlacaVeiculo(placa);

        final List<Servico> servicos = getServicosAbertosByPlaca(placa, null);
        holder.setServicos(servicos);
        //  Se não existirem serviços para a placa buscada, nada mais será setado no Holder.
        if (!servicos.isEmpty()) {
            Log.d(TAG, "Existem serviços para a placa: " + placa);

            final AfericaoDao afericaoDao = Injection.provideAfericaoDao();
            holder.setRestricao(afericaoDao.getRestricoesByPlaca(placa));
            if (contains(servicos, TipoServico.INSPECAO)) {
                Log.d(TAG, "Contém inspeção");
                holder.setAlternativasInspecao(getAlternativasInspecao());
            }
            if (contains(servicos, TipoServico.MOVIMENTACAO)) {
                Log.d(TAG, "Contém movimentação");
                final PneuDao pneuDao = Injection.providePneuDao();
                holder.setPneusDisponiveis(pneuDao.getPneusByCodUnidadeByStatus(codUnidade, StatusPneu.ESTOQUE));
            }
        }

        return holder;
    }

    @Override
    public List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosAbertosByPlaca(conn, placa, tipoServico);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final LocalDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final PneuDao pneuDao = Injection.providePneuDao();
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    fechaCalibragem(conn, pneuDao, dataHorafechamentoServico, (ServicoCalibragem) servico);
                    break;
                case INSPECAO:
                    fechaInspecao(conn, pneuDao, dataHorafechamentoServico, (ServicoInspecao) servico);
                    break;
                case MOVIMENTACAO:
                    final ServicoMovimentacao movimentacao = (ServicoMovimentacao) servico;
                    final MovimentacaoDao movimentacaoDao = Injection.provideMovimentacaoDao();
                    // Atualiza o pneuNovo com os valores referentes ao serviço executado.
                    movimentacao.getPneuNovo().setSulcosAtuais(movimentacao.getSulcosColetadosFechamento());
                    movimentacao.getPneuNovo().setPosicao(movimentacao.getPneuComProblema().getPosicao());
                    final ProcessoMovimentacao processoMovimentacao =
                            convertServicoToProcessoMovimentacao(codUnidade, movimentacao);
                    final Long codProcessoMovimentacao = movimentacaoDao.insert(
                            conn,
                            this,
                            processoMovimentacao,
                            dataHorafechamentoServico,
                            false);
                    movimentacao.setCodProcessoMovimentacao(codProcessoMovimentacao);

                    // Como impedimos o processo de movimentação de fechar automaticamente todos os serviços,
                    // nós agora fechamos o de movimentação como sendo fechado pelo usuário e depois os demais que
                    // ficarem pendentes do mesmo pneu. Essa ordem de execução dos métodos é necessária e não deve
                    // ser alterada!
                    fechaMovimentacao(conn, pneuDao, dataHorafechamentoServico, movimentacao);
                    final Long codPneu = servico.getPneuComProblema().getCodigo();
                    final int qtdServicosEmAbertoPneu = getQuantidadeServicosEmAbertoPneu(
                            codUnidade,
                            codPneu,
                            conn);
                    if (qtdServicosEmAbertoPneu > 0) {
                        final int qtdServicosFechadosPneu = fecharAutomaticamenteServicosPneu(
                                conn,
                                codUnidade,
                                codPneu,
                                codProcessoMovimentacao,
                                dataHorafechamentoServico,
                                servico.getKmVeiculoMomentoFechamento());
                        if (qtdServicosEmAbertoPneu != qtdServicosFechadosPneu) {
                            throw new IllegalStateException("Erro ao fechar os serviços do pneu: " + codPneu + ". Deveriam ser fechados "
                                    + qtdServicosEmAbertoPneu + " serviços mas foram fechados " + qtdServicosFechadosPneu + "!");
                        }

                    } else {
                        Log.d(TAG, "Não existem serviços em aberto para o pneu: " + codPneu);
                    }
                    break;
            }
            // Atualiza KM do veículo.
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            veiculoDao.updateKmByPlaca(servico.getPlacaVeiculo(), servico.getKmVeiculoMomentoFechamento(), conn);
            conn.commit();
        } catch (Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn);
        }
    }

    @Override
    public Servico getServicoByCod(Long codUnidade, Long codServico) throws SQLException {
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getQuantidadeServicosFechadosByVeiculo(Long codUnidade, long dataInicial, long dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getQuantidadeServicosFechadosByPneu(Long codUnidade, long dataInicial, long dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechados(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechados(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechadosPneu(Long codUnidade, Long codPneu, long dataInicial, long dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechadosVeiculo(Long codUnidade, String placaVeiculo, long dataInicial, long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechadosVeiculo(conn, codUnidade, placaVeiculo, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
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
            closeConnection(null, stmt, rSet);
        }
    }

    @Override
    public int fecharAutomaticamenteServicosPneu(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final Long codPneu,
                                                 @NotNull final Long codProcessoMovimentacao,
                                                 @NotNull final LocalDateTime dataHorafechamentoServico,
                                                 final long kmColetadoVeiculo) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fecharAutomaticamenteServicosPneu(
                    conn,
                    codUnidade,
                    codProcessoMovimentacao,
                    codPneu,
                    dataHorafechamentoServico,
                    kmColetadoVeiculo);
            return stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getVeiculoAberturaServico(conn, codServico, placaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final VeiculoServico veiculo = ServicoConverter.createVeiculoAberturaServico(rSet);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                final Optional<DiagramaVeiculo> diagrama = veiculoDao.getDiagramaVeiculoByPlaca(veiculo.getPlaca());
                // Fazemos direto um get() no Optional pois se não existir diagrama é melhor dar crash aqui do que no
                // aplicativo, por exemplo.
                //noinspection OptionalGetWithoutIsPresent,ConstantConditions
                veiculo.setDiagrama(diagrama.get());
                return veiculo;
            } else {
                throw new SQLException("Erro ao buscar veículo do serviço");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private ProcessoMovimentacao convertServicoToProcessoMovimentacao(@NotNull final Long codUnidade,
                                                                      @NotNull final ServicoMovimentacao servico) {
        final Veiculo veiculo = new Veiculo();
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
                Now.timestampUtc(),
                "Fechamento de serviço");
    }

    private boolean contains(@NotNull final List<Servico> servicos, @NotNull final TipoServico tipoServico) {
        for (final Servico servico : servicos) {
            if (servico.getTipoServico().equals(tipoServico)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private List<Alternativa> getAlternativasInspecao() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> alternativas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getAlternativasInspecao(conn);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final AlternativaChecklist alternativa = new AlternativaChecklist();
                alternativa.codigo = rSet.getLong("CODIGO");
                alternativa.alternativa = rSet.getString("ALTERNATIVA");
                alternativas.add(alternativa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return alternativas;
    }

    private void fechaCalibragem(@NotNull final Connection conn,
                                 @NotNull final PneuDao pneuDao,
                                 @NotNull final LocalDateTime dataHorafechamentoServico,
                                 @NotNull final ServicoCalibragem servico) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaCalibragem(conn, dataHorafechamentoServico, servico);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    conn,
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPressaoColetadaFechamento());
        } finally {
            close(stmt);
        }

    }

    private void fechaInspecao(@NotNull final Connection conn,
                               @NotNull final PneuDao pneuDao,
                               @NotNull final LocalDateTime dataHorafechamentoServico,
                               @NotNull final ServicoInspecao servico) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaInspecao(conn, dataHorafechamentoServico, servico);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    conn,
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPressaoColetadaFechamento());
        } finally {
            close(stmt);
        }

    }

    private void fechaMovimentacao(@NotNull final Connection conn,
                                   @NotNull final PneuDao pneuDao,
                                   @NotNull final LocalDateTime dataHorafechamentoServico,
                                   @NotNull final ServicoMovimentacao servico) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaMovimentacao(conn, dataHorafechamentoServico, servico);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }

            // No caso da movimentação precisamos atualizar o Pneu Novo.
            pneuDao.updatePressao(
                    conn,
                    servico.getPneuNovo().getCodigo(),
                    servico.getPressaoColetadaFechamento());
            pneuDao.updateSulcos(
                    conn,
                    servico.getPneuNovo().getCodigo(),
                    servico.getSulcosColetadosFechamento());
        } finally {
            close(stmt);
        }
    }
}