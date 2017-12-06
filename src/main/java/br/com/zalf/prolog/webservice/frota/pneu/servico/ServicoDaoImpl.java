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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {
    private static final String TAG = ServicoDaoImpl.class.getSimpleName();

    @Override
    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT" +
                    "  A.PLACA_VEICULO, AM.COD_PNEU," +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                    "FROM AFERICAO_MANUTENCAO AM " +
                    "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                    "WHERE AM.COD_UNIDADE = ? " +
                    "      AND AM.DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setString(1, TipoServico.MOVIMENTACAO.asString());
            stmt.setString(2, TipoServico.CALIBRAGEM.asString());
            stmt.setString(3, TipoServico.INSPECAO.asString());
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            final ServicosAbertosHolder holder = new ServicosAbertosHolder();
            final List<QuantidadeServicos> servicos = new ArrayList<>();
            while (rSet.next()) {
                servicos.add(ServicoConverter.createQtdServicosVeiculo(rSet));
            }
            holder.setServicosAbertos(servicos);
            return holder;
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
            stmt = conn.prepareStatement("SELECT V.PLACA, V.KM,V.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, AM.TIPO_SERVICO, AM.QT_APONTAMENTOS, P.CODIGO, VP.POSICAO, MAP" +
                    ".NOME AS MARCA, MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, MP.CODIGO AS COD_MODELO, MP.QT_SULCOS AS QT_SULCOS_MODELO, DP.*, P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MB.QT_SULCOS AS QT_SULCOS_BANDA, " +
                    "MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA\n "
                    + "FROM AFERICAO_MANUTENCAO AM "
                    + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A" +
                    ".PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U" +
                    ".cod_empresa\n "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
                    + "WHERE A.PLACA_VEICULO = ? AND AM.DATA_HORA_RESOLUCAO IS NULL AND AM.TIPO_SERVICO LIKE ? "
                    + "ORDER BY AM.TIPO_SERVICO");
            stmt.setString(1, placa);
            stmt.setString(2, tipoServico != null ? tipoServico.asString() : "%");
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void insertManutencao(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final PneuDao pneuDao = Injection.providePneuDao();
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    insertCalibragem((ServicoCalibragem) servico, codUnidade, pneuDao, conn);
                    break;
                case INSPECAO:
                    insertInspecao((ServicoInspecao) servico, codUnidade, pneuDao, conn);
                    break;
                case MOVIMENTACAO:
                    final MovimentacaoDaoImpl movimentacaoDao = new MovimentacaoDaoImpl();
                    final ServicoMovimentacao movimentacao = (ServicoMovimentacao) servico;
                    final ProcessoMovimentacao processoMovimentacao =
                            convertServicoToProcessoMovimentacao(movimentacao, codUnidade);
                    final Long codigoProcesso = movimentacaoDao.insert(processoMovimentacao);
                    movimentacao.setCodProcessoMovimentacao(codigoProcesso);
                    insertMovimentacao(movimentacao, conn);
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
        final PneuDao pneuDao = Injection.providePneuDao();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT "
                    + "AM.CODIGO, "
                    + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
                    + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
                    + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
                    + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
                    + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
                    + "V.PLACA AS PLACA_VEICULO, "
                    + "AM.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, "
                    + "AM.TIPO_SERVICO, "
                    + "AM.QT_APONTAMENTOS, "
                    + "VP.POSICAO, "
                    + "MAP.NOME AS MARCA, "
                    + "MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, "
                    + "MP.CODIGO AS COD_MODELO, "
                    + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
                    + "DP.*, "
                    + "P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, "
                    + "MB.nome AS NOME_MODELO_BANDA, "
                    + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
                    + "MAB.codigo AS COD_MARCA_BANDA, "
                    + "MAB.nome AS NOME_MARCA_BANDA "
                    + "FROM AFERICAO_MANUTENCAO AM "
                    + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND "
                    + "A.PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa "
                    + "WHERE AM.COD_UNIDADE = ? AND "
                    + "AM.CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codServico);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServico(rSet, pneuDao, true);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getQuantidadeServicosFechadosByPlaca(Long codUnidade, long dataInicial, long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getServicosFechadosStatement(codUnidade, dataInicial, dataFinal, conn);
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
            stmt = getServicosFechadosStatement(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            final ServicosFechadosHolder servicosFechadosHolder = new ServicosFechadosHolder();
            final List<QuantidadeServicos> quantidadeServicosFechados = new ArrayList<>();
            while (rSet.next()) {
                quantidadeServicosFechados.add(ServicoConverter.createQtdServicosPneu(rSet));
            }
            servicosFechadosHolder.setServicosFechados(quantidadeServicosFechados);
            return servicosFechadosHolder;
        } finally {
            closeConnection(null, null, rSet);
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
            stmt = conn.prepareStatement("SELECT "
                    + "AM.CODIGO, "
                    + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
                    + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
                    + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
                    + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
                    + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
                    + "V.PLACA AS PLACA_VEICULO, "
                    + "AM.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, "
                    + "AM.TIPO_SERVICO, "
                    + "AM.QT_APONTAMENTOS, "
                    + "VP.POSICAO, "
                    + "MAP.NOME AS MARCA, "
                    + "MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, "
                    + "MP.CODIGO AS COD_MODELO, "
                    + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
                    + "DP.*, "
                    + "P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, "
                    + "MB.nome AS NOME_MODELO_BANDA, "
                    + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
                    + "MAB.codigo AS COD_MARCA_BANDA, "
                    + "MAB.nome AS NOME_MARCA_BANDA "
                    + "FROM AFERICAO_MANUTENCAO AM "
                    + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A" +
                    ".PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
                    + "WHERE AM.COD_UNIDADE = ? "
                    + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                    + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, dataInicial);
            stmt.setLong(3, dataFinal);
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
            stmt = conn.prepareStatement("SELECT "
                    + "AM.CODIGO, "
                    + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
                    + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
                    + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
                    + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
                    + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
                    + "V.PLACA AS PLACA_VEICULO, "
                    + "AM.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, "
                    + "AM.TIPO_SERVICO, "
                    + "AM.QT_APONTAMENTOS, "
                    + "VP.POSICAO, "
                    + "MAP.NOME AS MARCA, "
                    + "MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, "
                    + "MP.CODIGO AS COD_MODELO, "
                    + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
                    + "DP.*, "
                    + "P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, "
                    + "MB.nome AS NOME_MODELO_BANDA, "
                    + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
                    + "MAB.codigo AS COD_MARCA_BANDA, "
                    + "MAB.nome AS NOME_MARCA_BANDA "
                    + "FROM AFERICAO_MANUTENCAO AM "
                    + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A" +
                    ".PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
                    + "WHERE AM.COD_UNIDADE = ? "
                    + "WHERE AM.COD_PNEU = ? "
                    + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                    + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codPneu);
            stmt.setLong(3, dataInicial);
            stmt.setLong(4, dataFinal);
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
            stmt = conn.prepareStatement("SELECT "
                    + "AM.CODIGO, "
                    + "AM.CPF_MECANICO AS CPF_RESPONSAVEL_FECHAMENTO, "
                    + "A.DATA_HORA AS DATA_HORA_ABERTURA, "
                    + "AM.DATA_HORA_RESOLUCAO AS DATA_HORA_FECHAMENTO, "
                    + "AM.KM_MOMENTO_CONSERTO AS KM_VEICULO_MOMENTO_FECHAMENTO, "
                    + "AM.TEMPO_REALIZACAO_MILLIS AS TEMPO_REALIZACAO_MILLIS, "
                    + "V.PLACA AS PLACA_VEICULO, "
                    + "AM.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, "
                    + "AM.TIPO_SERVICO, "
                    + "AM.QT_APONTAMENTOS, "
                    + "VP.POSICAO, "
                    + "MAP.NOME AS MARCA, "
                    + "MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, "
                    + "MP.CODIGO AS COD_MODELO, "
                    + "MP.QT_SULCOS AS QT_SULCOS_MODELO, "
                    + "DP.*, "
                    + "P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, "
                    + "MB.nome AS NOME_MODELO_BANDA, "
                    + "MB.QT_SULCOS AS QT_SULCOS_BANDA, "
                    + "MAB.codigo AS COD_MARCA_BANDA, "
                    + "MAB.nome AS NOME_MARCA_BANDA "
                    + "FROM AFERICAO_MANUTENCAO AM "
                    + "JOIN PNEU P ON AM.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A" +
                    ".PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
                    + "WHERE AM.COD_UNIDADE = ? "
                    + "WHERE A.PLACA_VEICULO = ? "
                    + "AND AM.DATA_HORA_RESOLUCAO IS NOT NULL "
                    + "AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, placaVeiculo);
            stmt.setLong(3, dataInicial);
            stmt.setLong(4, dataFinal);
            rSet = stmt.executeQuery();
            return ServicoConverter.createServicos(rSet, pneuDao);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getServicosFechadosStatement(Long codUnidade,
                                                           long dataInicial,
                                                           long dataFinal,
                                                           Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT" +
                "  A.PLACA_VEICULO, AM.COD_PNEU," +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_INSPECOES, " +
                "  SUM(CASE WHEN AM.TIPO_SERVICO = ? THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES " +
                "FROM AFERICAO_MANUTENCAO AM " +
                "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO " +
                "WHERE AM.COD_UNIDADE = ?" +
                "      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL " +
                "      AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ? " +
                "GROUP BY A.PLACA_VEICULO, AM.COD_PNEU;");
        stmt.setString(1, TipoServico.CALIBRAGEM.asString());
        stmt.setString(2, TipoServico.INSPECAO.asString());
        stmt.setString(3, TipoServico.MOVIMENTACAO.asString());
        stmt.setLong(4, codUnidade);
        stmt.setDate(5, new java.sql.Date(dataInicial));
        stmt.setDate(6, new java.sql.Date(dataFinal));
        return stmt;
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
            stmt = conn.prepareStatement("SELECT * FROM AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO A "
                    + "WHERE A.STATUS_ATIVO = TRUE");
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

    private void insertCalibragem(ServicoCalibragem servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                    + "DATA_HORA_RESOLUCAO = ?, "
                    + "CPF_MECANICO = ?, "
                    + "PSI_APOS_CONSERTO = ?, "
                    + "KM_MOMENTO_CONSERTO = ?, "
                    + "TEMPO_REALIZACAO_MILLIS = ? "
                    + "WHERE COD_AFERICAO = ? AND "
                    + "DATA_HORA_RESOLUCAO IS NULL AND "
                    + "COD_PNEU = ? "
                    + "AND TIPO_SERVICO = ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(2, servico.getCpfResponsavelFechamento());
            stmt.setDouble(3, servico.getPneuComProblema().getPressaoAtual());
            stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
            stmt.setLong(5, servico.getTempoRealizacaoServicoInMillis());
            stmt.setLong(6, servico.getCodAfericao());
            stmt.setString(7, servico.getPneuComProblema().getCodigo());
            stmt.setString(8, servico.getTipoServico().asString());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(servico.getPneuComProblema(), codUnidade, conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void insertInspecao(ServicoInspecao servico, Long codUnidade, PneuDao pneuDao, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                    + "DATA_HORA_RESOLUCAO = ?, "
                    + "CPF_MECANICO = ?, "
                    + "PSI_APOS_CONSERTO = ?, "
                    + "KM_MOMENTO_CONSERTO = ?, "
                    + "COD_ALTERNATIVA = ?, "
                    + "TEMPO_REALIZACAO_MILLIS = ? "
                    + "WHERE COD_AFERICAO = ? AND "
                    + "COD_PNEU = ? AND "
                    + "DATA_HORA_RESOLUCAO IS NULL "
                    + "AND TIPO_SERVICO = ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(2, servico.getCpfResponsavelFechamento());
            stmt.setDouble(3, servico.getPneuComProblema().getPressaoAtual());
            stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
            stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
            stmt.setLong(6, servico.getTempoRealizacaoServicoInMillis());
            stmt.setLong(7, servico.getCodAfericao());
            stmt.setString(8, servico.getPneuComProblema().getCodigo());
            stmt.setString(9, servico.getTipoServico().asString());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
            pneuDao.updatePressao(servico.getPneuComProblema(), codUnidade, conn);
        } finally {
            closeConnection(null, stmt, null);
        }

    }

    private void insertMovimentacao(ServicoMovimentacao servico, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
                    + "DATA_HORA_RESOLUCAO = ?, "
                    + "CPF_MECANICO = ?, "
                    + "KM_MOMENTO_CONSERTO = ?, "
                    + "COD_PROCESSO_MOVIMENTACAO = ?, "
                    + "TEMPO_REALIZACAO_MILLIS = ? "
                    + "WHERE COD_AFERICAO = ? AND "
                    + "COD_PNEU = ? AND "
                    + "DATA_HORA_RESOLUCAO IS NULL "
                    + "AND TIPO_SERVICO = ?");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(2, servico.getCpfResponsavelFechamento());
            stmt.setLong(3, servico.getKmVeiculoMomentoFechamento());
            stmt.setLong(4, servico.getCodProcessoMovimentacao());
            stmt.setLong(5, servico.getTempoRealizacaoServicoInMillis());
            stmt.setLong(6, servico.getCodAfericao());
            stmt.setString(7, servico.getPneuComProblema().getCodigo());
            stmt.setString(8, servico.getTipoServico().asString());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o item consertado");
            }
        } finally {
            closeConnection(null, stmt, null);
        }
    }
}