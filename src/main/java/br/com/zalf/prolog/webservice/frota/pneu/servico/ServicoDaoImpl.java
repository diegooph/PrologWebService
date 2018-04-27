package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.DestinoVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemEstoque;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.OrigemVeiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {
    private static final String TAG = ServicoDaoImpl.class.getSimpleName();

    @Override
    public Long criaServico(Long codPneu, Long codAfericao, TipoServico tipoServico, Long codUnidade, Connection conn)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, " +
                "COD_UNIDADE, TIPO_SERVICO) VALUES(?, ?, ?, ?) RETURNING CODIGO;");
        stmt.setLong(1, codAfericao);
        stmt.setLong(2, codPneu);
        stmt.setLong(3, codUnidade);
        stmt.setString(4, tipoServico.asString());
        final ResultSet rSet = stmt.executeQuery();
        if (rSet.next()) {
            return rSet.getLong("CODIGO");
        } else {
            throw new SQLException("Erro ao criar serviço");
        }
    }

    @Override
    public void incrementaQtdApontamentosServico(Long codPneu, Long codUnidade, TipoServico tipoServico, Connection conn)
            throws SQLException {
        Log.d(TAG, "Atualizando quantidade de apontamos do pneu: " + codPneu + " da unidade: " + codUnidade);
        PreparedStatement stmt =
                conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
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
    }

    @Override
    public void calibragemToInspecao(Long codPneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt =
                conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
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
    }

    @Override
    public List<TipoServico> getServicosCadastradosByPneu(Long codPneu, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<TipoServico> listServico = new ArrayList<>();
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
                listServico.add(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        return listServico;
    }

    @Override
    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getQuantidadeServicosAbertosVeiculo(codUnidade, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicosAbertosHolder(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public ServicoHolder getServicoHolder(String placa, Long codUnidade) throws SQLException {
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
                holder.setPneusDisponiveis(pneuDao.getPneusByCodUnidadeByStatus(codUnidade, Pneu.ESTOQUE));
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
            stmt = ServicoQueryBinder.getServicosAbertosByPlaca(placa, tipoServico, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void fechaServico(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final PneuDao pneuDao = Injection.providePneuDao();
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    fechaCalibragem((ServicoCalibragem) servico, codUnidade, pneuDao, conn);
                    break;
                case INSPECAO:
                    fechaInspecao((ServicoInspecao) servico, codUnidade, pneuDao, conn);
                    break;
                case MOVIMENTACAO:
                    final ServicoMovimentacao movimentacao = (ServicoMovimentacao) servico;
                    final MovimentacaoDao movimentacaoDao = Injection.provideMovimentacaoDao();
                    // Atualiza o pneuNovo com os valores referentes ao serviço executado.
                    movimentacao.getPneuNovo().setSulcosAtuais(movimentacao.getSulcosColetadosFechamento());
                    movimentacao.getPneuNovo().setPosicao(movimentacao.getPneuComProblema().getPosicao());
                    final ProcessoMovimentacao processoMovimentacao =
                            convertServicoToProcessoMovimentacao(movimentacao, codUnidade);
                    final Long codProcessoMovimentacao = movimentacaoDao.insert(
                            processoMovimentacao,
                            this,
                            false,
                            conn);
                    movimentacao.setCodProcessoMovimentacao(codProcessoMovimentacao);

                    // Como impedimos o processo de movimentação de fechar automaticamente todos os serviços,
                    // nós agora fechamos o de movimentação como sendo fechado pelo usuário e depois os demais que
                    // ficarem pendentes do mesmo pneu. Essa ordem de execução dos métodos é necessária e não deve
                    // ser alterada!
                    fechaMovimentacao(movimentacao, codUnidade, pneuDao, conn);
                    final Long codPneu = servico.getPneuComProblema().getCodigo();
                    final int qtdServicosEmAbertoPneu = getQuantidadeServicosEmAbertoPneu(
                            codUnidade,
                            codPneu,
                            conn);
                    if (qtdServicosEmAbertoPneu > 0) {
                        final int qtdServicosFechadosPneu = fecharAutomaticamenteServicosPneu(
                                codUnidade,
                                codPneu,
                                codProcessoMovimentacao,
                                servico.getKmVeiculoMomentoFechamento(),
                                conn);
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
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Override
    public Servico getServicoByCod(Long codUnidade, Long codServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicoByCod(codUnidade, codServico, conn);
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
            stmt = ServicoQueryBinder.getQuantidadeServicosFechadosVeiculo(codUnidade, dataInicial, dataFinal, conn);
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
            stmt = ServicoQueryBinder.getQuantidadeServicosFechadosPneu(codUnidade, dataInicial, dataFinal, conn);
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
            stmt = ServicoQueryBinder.getServicosFechados(codUnidade, dataInicial, dataFinal, conn);
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
            stmt = ServicoQueryBinder.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal, conn);
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
            stmt = ServicoQueryBinder.getServicosFechadosVeiculo(codUnidade, placaVeiculo, dataInicial, dataFinal, conn);
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
            stmt = ServicoQueryBinder.getQuantidadeServicosEmAbertoPneu(codUnidade, codPneu, conn);
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
    public int fecharAutomaticamenteServicosPneu(final Long codUnidade,
                                                 final Long codPneu,
                                                 final Long codProcessoMovimentacao,
                                                 final long kmColetadoVeiculo,
                                                 final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fecharAutomaticamenteServicosPneu(
                    codUnidade,
                    codPneu,
                    codProcessoMovimentacao,
                    kmColetadoVeiculo,
                    conn);
            return stmt.executeUpdate();
        } finally {
            closeConnection(null, stmt, null);
        }
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getVeiculoAberturaServico(codServico, placaVeiculo, conn);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final VeiculoServico veiculo = ServicoConverter.createVeiculoAberturaServico(rSet);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                final Optional<DiagramaVeiculo> diagrama = veiculoDao.getDiagramaVeiculoByPlaca(veiculo.getPlaca());
                // Fazemos direto um get() no Optional pois se não existir diagrama é melhor da crash aqui do que no
                // aplicativo, por exemplo.
                //noinspection OptionalGetWithoutIsPresent
                veiculo.setDiagrama(diagrama.get());
                return veiculo;
            } else {
                throw new SQLException("Erro ao buscar veículo do serviço");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private ProcessoMovimentacao convertServicoToProcessoMovimentacao(ServicoMovimentacao servico, Long codUnidade) {
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
                null,
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

    private List<Alternativa> getAlternativasInspecao() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> listAlternativas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getAlternativasInspecao(conn);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final AlternativaChecklist alternativa = new AlternativaChecklist();
                alternativa.codigo = rSet.getLong("CODIGO");
                alternativa.alternativa = rSet.getString("ALTERNATIVA");
                listAlternativas.add(alternativa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listAlternativas;
    }

    private void fechaCalibragem(ServicoCalibragem servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaCalibragem(servico, conn);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPressaoColetadaFechamento(),
                    codUnidade,
                    conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void fechaInspecao(ServicoInspecao servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaInspecao(servico, conn);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPressaoColetadaFechamento(),
                    codUnidade,
                    conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void fechaMovimentacao(ServicoMovimentacao servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.fechaMovimentacao(servico, conn);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }

            // No caso da movimentação precisamos atualizar do Pneu Novo.
            pneuDao.updatePressao(
                    servico.getPneuNovo().getCodigo(),
                    servico.getPressaoColetadaFechamento(),
                    codUnidade,
                    conn);
            pneuDao.updateSulcos(
                    servico.getPneuNovo().getCodigo(),
                    servico.getSulcosColetadosFechamento(),
                    codUnidade,
                    conn);
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}