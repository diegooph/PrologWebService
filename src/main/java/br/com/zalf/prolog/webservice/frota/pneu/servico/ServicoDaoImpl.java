package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDaoImpl;
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
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {
    private static final String TAG = ServicoDaoImpl.class.getSimpleName();

    @Override
    public Long criaServico(String codPneu, Long codAfericao, TipoServico tipoServico, Long codUnidade, Connection conn)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, " +
                "COD_UNIDADE, TIPO_SERVICO) VALUES(?, ?, ?, ?) RETURNING CODIGO;");
        stmt.setLong(1, codAfericao);
        stmt.setString(2, codPneu);
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
    public void incrementaQtdApontamentosServico(String codPneu, Long codUnidade, TipoServico tipoServico, Connection conn)
            throws SQLException {
        Log.d(TAG, "Atualizando quantidade de apontamos do pneu: " + codPneu + " da unidade: " + codUnidade);
        PreparedStatement stmt =
                conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                        + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND "
                        + "TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
                        + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setString(1, codPneu);
        stmt.setLong(2, codUnidade);
        stmt.setString(3, tipoServico.asString());
        stmt.setString(4, codPneu);
        stmt.setLong(5, codUnidade);
        stmt.setString(6, tipoServico.asString());
        stmt.executeUpdate();
    }

    @Override
    public void calibragemToInspecao(String codPneu, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
                + "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND "
                + "TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1, TIPO_SERVICO = ? "
                + "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL;");
        stmt.setString(1, codPneu);
        stmt.setLong(2, codUnidade);
        stmt.setString(3, TipoServico.CALIBRAGEM.asString());
        stmt.setString(4, TipoServico.INSPECAO.asString());
        stmt.setString(5, codPneu);
        stmt.setLong(6, codUnidade);
        stmt.setString(7, TipoServico.CALIBRAGEM.asString());
        stmt.executeUpdate();
    }

    @Override
    public List<TipoServico> getServicosCadastradosByPneu(String codPneu, Long codUnidade) throws SQLException {
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
            stmt.setString(1, codPneu);
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
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final PneuDao pneuDao = Injection.providePneuDao();
        final ServicoHolder holder = new ServicoHolder();
        holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, true));
        holder.setListServicos(getServicosAbertosByPlaca(placa, null));
        if (containInspecao(holder.getListServicos())) {
            holder.setListAlternativaInspecao(getListAlternativasInspecao());
        }
        if (containMovimentacao(holder.getListServicos())) {
            Log.d(TAG, "Contém movimentação");
            holder.setPneusDisponiveis(pneuDao.getPneuByCodUnidadeByStatus(codUnidade, Pneu.ESTOQUE));
        }
        final AfericaoDao afericaoDao = Injection.provideAfericaoDao();
        holder.setRestricao(afericaoDao.getRestricoesByPlaca(placa));

        return holder;
    }

    @Override
    public List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosAbertosByPlaca(placa, tipoServico, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void consertaServico(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final PneuDao pneuDao = Injection.providePneuDao();
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    consertaCalibragem((ServicoCalibragem) servico, codUnidade, pneuDao, conn);
                    break;
                case INSPECAO:
                    consertaInspecao((ServicoInspecao) servico, codUnidade, pneuDao, conn);
                    break;
                case MOVIMENTACAO:
                    final MovimentacaoDaoImpl movimentacaoDao = new MovimentacaoDaoImpl();
                    final ServicoMovimentacao movimentacao = (ServicoMovimentacao) servico;
                    final ProcessoMovimentacao processoMovimentacao =
                            convertServicoToProcessoMovimentacao(movimentacao, codUnidade);
                    final Long codigoProcesso = movimentacaoDao.insert(processoMovimentacao, conn);
                    movimentacao.setCodProcessoMovimentacao(codigoProcesso);
                    consertaMovimentacao(movimentacao, conn);
                    break;
            }
            // Atualiza KM do veículo.
            final VeiculoDao veiculoDao = new VeiculoDaoImpl();
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
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechados(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<Servico> getServicosFechadosPneu(Long codUnidade, String codPneu, long dataInicial, long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
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
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getServicosFechadosVeiculo(codUnidade, placaVeiculo, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Veiculo getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getVeiculoAberturaServico(codServico, placaVeiculo, conn);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Veiculo veiculo = ServicoConverter.createVeiculoAberturaServico(rSet);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                final Optional<DiagramaVeiculo> diagrama = veiculoDao.getDiagramaVeiculoByPlaca(veiculo.getPlaca());
                // Fazemos direto um get() no Optional pois se não existir diagrama é melhor da crash aqui do que no
                // aplicativo, por exemplo.
                //noinspection ConstantConditions
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

    private boolean containInspecao(List<Servico> listServicos) {
        for (Servico servico : listServicos) {
            if (servico instanceof ServicoInspecao) {
                return true;
            }
        }
        return false;
    }

    private boolean containMovimentacao(List<Servico> listServicos) {
        for (Servico servico : listServicos) {
            if (servico instanceof ServicoMovimentacao) {
                return true;
            }
        }
        return false;
    }

    private List<Alternativa> getListAlternativasInspecao() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<Alternativa> listAlternativas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = ServicoQueryBinder.getAlternativasInspecao(conn);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                AlternativaChecklist alternativa = new AlternativaChecklist();
                alternativa.codigo = rSet.getLong("CODIGO");
                alternativa.alternativa = rSet.getString("ALTERNATIVA");
                listAlternativas.add(alternativa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return listAlternativas;
    }

    private void consertaCalibragem(ServicoCalibragem servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.consertaCalibragem(servico, conn);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPneuComProblema().getPressaoAtual(),
                    codUnidade,
                    conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void consertaInspecao(ServicoInspecao servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.consertaInspecao(servico, conn);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(
                    servico.getPneuComProblema().getCodigo(),
                    servico.getPneuComProblema().getPressaoAtual(),
                    codUnidade,
                    conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void consertaMovimentacao(ServicoMovimentacao servico, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = ServicoQueryBinder.consertaMovimentacao(servico, conn);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}