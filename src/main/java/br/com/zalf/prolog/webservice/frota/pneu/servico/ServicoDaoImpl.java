package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDaoImpl;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {

    @Override
    public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        PlacaServicoHolder placaServicoHolder = new PlacaServicoHolder();
        List<PlacaServicoHolder.PlacaServico> listaServicos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT V.PLACA, MOV.TOTAL_MOVIMENTACAO, CAL.TOTAL_CALIBRAGEM, INSP" +
                    ".TOTAL_INSPECAO\n" +
                    "FROM VEICULO V  JOIN\n" +
                    "    (SELECT VP.PLACA AS PLACA_TOT, COUNT(VP.PLACA) AS TOTAL_SERVICOS\n" +
                    "    FROM AFERICAO A\n" +
                    "    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
                    "    JOIN veiculo_pneu VP ON VP.placa = A.placa_veiculo AND ITS.cod_pneu = VP.cod_pneu AND ITS" +
					".cod_unidade = VP.cod_unidade\n" +
                    "    WHERE ITS.TIPO_SERVICO LIKE '%' AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
                    "    GROUP BY VP.PLACA) AS TOT ON PLACA_TOT = V.PLACA\n" +
                    "FULL OUTER JOIN\n" +
                    "    (SELECT VP.PLACA AS PLACA_MOV, COUNT(ITS.TIPO_SERVICO) AS TOTAL_MOVIMENTACAO\n" +
                    "    FROM AFERICAO A\n" +
                    "    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
                    "    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS" +
					".cod_unidade = VP.cod_unidade\n" +
                    "    WHERE ITS.TIPO_SERVICO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
                    "    GROUP BY 1,ITS.TIPO_SERVICO) AS MOV ON PLACA_MOV = V.PLACA\n" +
                    "FULL OUTER JOIN\n" +
                    "    (SELECT VP.PLACA AS PLACA_CAL, COUNT(ITS.TIPO_SERVICO) AS TOTAL_CALIBRAGEM\n" +
                    "    FROM AFERICAO A\n" +
                    "    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
                    "    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS" +
					".cod_unidade = VP.cod_unidade\n" +
                    "    WHERE ITS.TIPO_SERVICO = ?  AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
                    "    GROUP BY 1,ITS.TIPO_SERVICO) AS CAL ON PLACA_CAL = V.PLACA\n" +
                    "FULL OUTER JOIN\n" +
                    "    (SELECT VP.PLACA AS PLACA_INSP, COUNT(ITS.TIPO_SERVICO) AS TOTAL_INSPECAO\n" +
                    "    FROM AFERICAO A\n" +
                    "    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
                    "    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS" +
					".cod_unidade = VP.cod_unidade\n" +
                    "    WHERE ITS.TIPO_SERVICO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
                    "    GROUP BY 1,ITS.TIPO_SERVICO) AS INSP ON PLACA_INSP = V.PLACA\n" +
                    "WHERE V.COD_UNIDADE = ?");
            stmt.setString(1, TipoServico.MOVIMENTACAO.asString());
            stmt.setString(2, TipoServico.CALIBRAGEM.asString());
            stmt.setString(3, TipoServico.INSPECAO.asString());
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                PlacaServicoHolder.PlacaServico item = new PlacaServicoHolder.PlacaServico();
                item.placa = rSet.getString("PLACA");
                item.qtCalibragem = rSet.getInt("TOTAL_CALIBRAGEM");
                item.qtMovimentacao = rSet.getInt("TOTAL_MOVIMENTACAO");
                item.qtInspecao = rSet.getInt("TOTAL_INSPECAO");

                listaServicos.add(item);
                placaServicoHolder.setQtCalibragemTotal(placaServicoHolder.getQtCalibragemTotal() + item.qtCalibragem);
                placaServicoHolder.setQtMovimentacaoTotal(placaServicoHolder.getQtMovimentacaoTotal() + item
						.qtMovimentacao);
                placaServicoHolder.setQtInspecaoTotal(placaServicoHolder.getQtInspecaoTotal() + item.qtInspecao);
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        placaServicoHolder.setListPlacas(listaServicos);
        return placaServicoHolder;
    }

    @Override
    public ServicoHolder getServicosByPlaca(String placa, Long codUnidade) throws SQLException {
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final PneuDao pneuDao = Injection.providePneuDao();
        ServicoHolder holder = new ServicoHolder();
        holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, true));
        holder.setListServicos(getServicosAbertosByPlaca(placa, "%"));
        if (containInspecao(holder.getListServicos())) {
            holder.setListAlternativaInspecao(getListAlternativasInspecao());
        }
        if (containMovimentacao(holder.getListServicos())) {
            Log.d("teste", "contem movimentacao");
            holder.setPneusDisponiveis(pneuDao.getPneuByCodUnidadeByStatus(codUnidade, Pneu.ESTOQUE));
        }
        AfericaoDao afericaoDao = Injection.provideAfericaoDao();
        holder.setRestricao(afericaoDao.getRestricoesByPlaca(placa));

        return holder;
    }

    @Override
    public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Servico> listServicos = new ArrayList<>();
        PneuDao pneuDao = Injection.providePneuDao();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT V.PLACA, V.KM,V.COD_UNIDADE AS COD_UNIDADE, "
                    + "A.CODIGO AS COD_AFERICAO, ITS.TIPO_SERVICO, ITS.QT_APONTAMENTOS, P.CODIGO, VP.POSICAO, MAP" +
					".NOME AS MARCA, MAP.CODIGO AS COD_MARCA, "
                    + "MP.NOME AS MODELO, MP.CODIGO AS COD_MODELO, MP.QT_SULCOS AS QT_SULCOS_MODELO, DP.*, P.*, "
                    + "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MB.QT_SULCOS AS QT_SULCOS_BANDA, " +
					"MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA\n "
                    + "FROM AFERICAO_MANUTENCAO ITS "
                    + "JOIN PNEU P ON ITS.COD_PNEU = P.CODIGO "
                    + "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
                    + "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
                    + "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
                    + "JOIN AFERICAO A ON A.CODIGO = ITS.COD_AFERICAO "
                    + "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A" +
					".PLACA_VEICULO = VP.PLACA "
                    + "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
                    + "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
                    + "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U" +
					".cod_empresa\n "
                    + "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
                    + "WHERE A.PLACA_VEICULO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL AND ITS.TIPO_SERVICO LIKE ? "
                    + "ORDER BY ITS.TIPO_SERVICO");
            stmt.setString(1, placa);
            stmt.setString(2, tipoServico);
            rSet = stmt.executeQuery();
            while (rSet.next()) {

                final TipoServico tipo = TipoServico.fromString(rSet.getString("TIPO_SERVICO"));
                switch (tipo) {
                    case CALIBRAGEM:
                        listServicos.add(createCalibragem(pneuDao, rSet));
                        break;
                    case MOVIMENTACAO:
                        listServicos.add(createMovimentacao(pneuDao, rSet));
                        break;
                    case INSPECAO:
                        listServicos.add(createInspecao(pneuDao, rSet));
                        break;
                }
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return listServicos;
    }

    @Override
    public void insertManutencao(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException {
        Connection conn = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            PneuDao pneuDao = Injection.providePneuDao();
            switch (servico.getTipoServico()) {
                case CALIBRAGEM:
                    insertCalibragem((ServicoCalibragem) servico, codUnidade, pneuDao, conn);
                    break;
                case INSPECAO:
                    insertInspecao((ServicoInspecao) servico, codUnidade, pneuDao, conn);
                    break;
                case MOVIMENTACAO:
                    MovimentacaoDaoImpl movimentacaoDao = new MovimentacaoDaoImpl();
                    movimentacaoDao.insert(convertServicoToProcessoMovimentacao((ServicoMovimentacao) servico, codUnidade));
                    break;
            }
            // atualiza KM do veículo
            VeiculoDao veiculoDao = new VeiculoDaoImpl();
            veiculoDao.updateKmByPlaca(servico.getPlacaVeiculo(), servico.getKmVeiculoMomentoFechamento(), conn);
            conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
            conn.rollback();
        }finally{
            closeConnection(conn, null, null);
        }
    }

    @Override
    public ServicosFechadosHolder getServicosFechadosByPlaca(Long codUnidade, long dataInicial, long dataFinal)
            throws SQLException {
        ResultSet rSet = null;
        try {
            rSet = getServicosFechadosResultSet(codUnidade, dataInicial, dataFinal);
            final ServicosFechadosHolder servicosFechadosHolder = new ServicosFechadosHolder();
            final List<QuantidadeServicosFechados> quantidadeServicosFechados = new ArrayList<>();
            while (rSet.next()) {
                quantidadeServicosFechados.add(createQtdServicosFechadosVeiculo(rSet));
            }
            servicosFechadosHolder.setServicosFechados(quantidadeServicosFechados);
            return servicosFechadosHolder;
        } finally {
            closeConnection(null, null, rSet);
        }
    }

    @Override
    public ServicosFechadosHolder getServicosFechadosByPneu(Long codUnidade, long dataInicial, long dataFinal)
            throws SQLException {
        ResultSet rSet = null;
        try {
            rSet = getServicosFechadosResultSet(codUnidade, dataInicial, dataFinal);
            final ServicosFechadosHolder servicosFechadosHolder = new ServicosFechadosHolder();
            final List<QuantidadeServicosFechados> quantidadeServicosFechados = new ArrayList<>();
            while (rSet.next()) {
                quantidadeServicosFechados.add(createQtdServicosFechadosPneu(rSet));
            }
            servicosFechadosHolder.setServicosFechados(quantidadeServicosFechados);
            return servicosFechadosHolder;
        } finally {
            closeConnection(null, null, rSet);
        }
    }

    private ResultSet getServicosFechadosResultSet(Long codUnidade, long dataInicial, long dataFinal)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT" +
                    "  A.PLACA_VEICULO, AM.COD_PNEU," +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = 'calibragem' THEN 1 ELSE 0 END) AS TOTAL_CALIBRAGENS," +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = 'inspecao' THEN 1 ELSE 0 END) AS TOTAL_INSPECOES," +
                    "  SUM(CASE WHEN AM.TIPO_SERVICO = 'movimentacao' THEN 1 ELSE 0 END) AS TOTAL_MOVIMENTACOES" +
                    "FROM AFERICAO_MANUTENCAO AM" +
                    "  JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO" +
                    "WHERE AM.COD_UNIDADE = ?" +
                    "      AND AM.DATA_HORA_RESOLUCAO IS NOT NULL" +
                    "      AND AM.DATA_HORA_RESOLUCAO::DATE BETWEEN ? AND ?" +
                    "GROUP BY A.PLACA_VEICULO, AM.COD_PNEU;");
            stmt.setLong(1, codUnidade);
            stmt.setDate(2, new java.sql.Date(dataInicial));
            stmt.setDate(3, new java.sql.Date(dataFinal));
            return stmt.executeQuery();
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    private QuantidadeServicosFechadosVeiculo createQtdServicosFechadosVeiculo(ResultSet resultSet) throws SQLException {
        final QuantidadeServicosFechadosVeiculo qtdServicosFechados = new QuantidadeServicosFechadosVeiculo();
        qtdServicosFechados.setPlacaVeiculo(resultSet.getString("PLACA_VEICULO"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        return qtdServicosFechados;
    }

    private QuantidadeServicosFechadosPneu createQtdServicosFechadosPneu(ResultSet resultSet) throws SQLException {
        final QuantidadeServicosFechadosPneu qtdServicosFechados = new QuantidadeServicosFechadosPneu();
        qtdServicosFechados.setCodigoPneu(resultSet.getString("COD_PNEU"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosFechadosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        return qtdServicosFechados;
    }

    private ProcessoMovimentacao convertServicoToProcessoMovimentacao(ServicoMovimentacao servico, Long codUnidade){
        List<br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao> movimentacoes = new ArrayList<>();
        Colaborador colaborador = new Colaborador();
        colaborador.setCpf(servico.getCpfResponsavelFechamento());
        Unidade unidade = new Unidade();
        unidade.setCodigo(codUnidade);
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(servico.getPlacaVeiculo());
        veiculo.setKmAtual(servico.getKmVeiculoMomentoFechamento());
        OrigemEstoque origemEstoque = new OrigemEstoque();
        DestinoVeiculo destinoVeiculo = new DestinoVeiculo(veiculo, servico.getPneuComProblema().getPosicao());
        br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao movimentacaoPneuInserido =
                new br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao(
                        null,servico.getPneuNovo(), origemEstoque, destinoVeiculo, null);
        OrigemVeiculo origemVeiculo = new OrigemVeiculo(veiculo, servico.getPneuComProblema().getPosicao());
        DestinoEstoque destinoEstoque = new DestinoEstoque();
        br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao movimentacaoPneuRemovido =
                new br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao(null, servico.getPneuComProblema(), origemVeiculo, destinoEstoque, null);
        movimentacoes.add(movimentacaoPneuRemovido);
        movimentacoes.add(movimentacaoPneuInserido);
        ProcessoMovimentacao processoMovimentacao = new ProcessoMovimentacao(null, movimentacoes, colaborador, null, "Fechamento de serviço");
        processoMovimentacao.setUnidade(unidade);
        return processoMovimentacao;
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
        List<Alternativa> listAlternativas = new ArrayList<>();

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
            closeConnection(conn, stmt, null);
        }
        return listAlternativas;
    }

    private ServicoCalibragem createCalibragem(PneuDao pneuDao, ResultSet rSet) throws SQLException {
        ServicoCalibragem calibragem = new ServicoCalibragem();
        calibragem.setPneuComProblema(pneuDao.createPneu(rSet));
        calibragem.setCodAfericao(rSet.getLong("COD_AFERICAO"));
        calibragem.setTipoServico(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
        calibragem.setQtdApontamentos(rSet.getInt("QT_APONTAMENTOS"));
        return calibragem;
    }

    private ServicoInspecao createInspecao(PneuDao pneuDao, ResultSet rSet) throws SQLException {
        ServicoInspecao inspecao = new ServicoInspecao();
        inspecao.setPneuComProblema(pneuDao.createPneu(rSet));
        inspecao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
        inspecao.setTipoServico(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
        inspecao.setQtdApontamentos(rSet.getInt("QT_APONTAMENTOS"));
        return inspecao;
    }

    private ServicoMovimentacao createMovimentacao(PneuDao pneuDao, ResultSet rSet) throws SQLException {
        ServicoMovimentacao movimentacao = new ServicoMovimentacao();
        movimentacao.setPneuComProblema(pneuDao.createPneu(rSet));
        movimentacao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
        movimentacao.setTipoServico(TipoServico.fromString(rSet.getString("TIPO_SERVICO")));
        movimentacao.setQtdApontamentos(rSet.getInt("QT_APONTAMENTOS"));
        return movimentacao;
    }

	private void insertCalibragem(ServicoCalibragem servico, Long codUnidade, PneuDao pneuDao, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "DATA_HORA_RESOLUCAO IS NULL AND "
					+ "COD_PNEU = ? "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfResponsavelFechamento());
			stmt.setDouble(3, servico.getPneuComProblema().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
			stmt.setLong(5, servico.getCodAfericao());
			stmt.setString(6, servico.getPneuComProblema().getCodigo());
			stmt.setString(7, servico.getTipoServico().asString());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updatePressao(servico.getPneuComProblema(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}

	}

	private void insertInspecao(ServicoInspecao servico, Long codUnidade, PneuDao pneuDao, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ?, "
					+ "COD_ALTERNATIVA = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "COD_PNEU = ? AND "
					+ "DATA_HORA_RESOLUCAO IS NULL "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfResponsavelFechamento());
			stmt.setDouble(3, servico.getPneuComProblema().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculoMomentoFechamento());
			stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
			stmt.setLong(6, servico.getCodAfericao());
			stmt.setString(7, servico.getPneuComProblema().getCodigo());
			stmt.setString(8, servico.getTipoServico().asString());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updatePressao(servico.getPneuComProblema(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}

	}
}
