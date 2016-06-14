package br.com.zalf.prolog.webservice.servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.servico.Calibragem;
import br.com.zalf.prolog.models.pneu.servico.Inspecao;
import br.com.zalf.prolog.models.pneu.servico.Movimentacao;
import br.com.zalf.prolog.models.pneu.servico.PlacaServicoHolder;
import br.com.zalf.prolog.models.pneu.servico.Servico;
import br.com.zalf.prolog.models.pneu.servico.ServicoHolder;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.veiculo.VeiculoDaoImpl;

public class ServicoDaoImpl extends DatabaseConnection implements ServicoDao{

	PneuDaoImpl pneuDao;
	VeiculoDaoImpl veiculoDao;
	Long codUnidade;

	@Override
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		PlacaServicoHolder placaServicoHolder = new PlacaServicoHolder();
		List<PlacaServicoHolder.PlacaServico> listaServicos = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, MOV.TOTAL_MOVIMENTACAO, CAL.TOTAL_CALIBRAGEM, INSP.TOTAL_INSPECAO " 
					+ "FROM VEICULO V  join " 
					+ "(SELECT VP.PLACA AS PLACA_MOV, COUNT(ITS.TIPO_SERVICO) AS TOTAL_MOVIMENTACAO " 
					+ "FROM VEICULO_PNEU VP " 
					+ "JOIN AFERICAO_MANUTENCAO ITS ON ITS.COD_PNEU = VP.COD_PNEU "
					+ "WHERE ITS.TIPO_SERVICO = ? " 
					+ "GROUP BY 1,ITS.TIPO_SERVICO) AS MOV ON PLACA_MOV = V.PLACA " 
					+ "LEFT JOIN (SELECT VP.PLACA AS PLACA_CAL, COUNT(ITS.TIPO_SERVICO) AS TOTAL_CALIBRAGEM " 
					+ "FROM VEICULO_PNEU VP " 
					+ "JOIN AFERICAO_MANUTENCAO ITS ON ITS.COD_PNEU = VP.COD_PNEU WHERE ITS.TIPO_SERVICO = ? " 
					+ "GROUP BY 1,ITS.TIPO_SERVICO) AS CAL ON PLACA_CAL = V.PLACA " 
					+ "LEFT JOIN (SELECT VP.PLACA AS PLACA_CAL, COUNT(ITS.TIPO_SERVICO) AS TOTAL_INSPECAO "
					+ "FROM VEICULO_PNEU VP " 
					+ "JOIN AFERICAO_MANUTENCAO ITS ON ITS.COD_PNEU = VP.COD_PNEU WHERE ITS.TIPO_SERVICO = ? " 
					+ "GROUP BY 1,ITS.TIPO_SERVICO) AS INSP ON PLACA_MOV = V.PLACA "
					+ "WHERE V.COD_UNIDADE = ? ");
			stmt.setString(1, Servico.TIPO_MOVIMENTACAO);
			stmt.setString(2, Servico.TIPO_CALIBRAGEM);
			stmt.setString(3, Servico.TIPO_INSPECAO);
			stmt.setLong(4, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				PlacaServicoHolder.PlacaServico item = new PlacaServicoHolder.PlacaServico();
				item.placa = rSet.getString("PLACA");
				item.qtCalibragem = rSet.getInt("TOTAL_CALIBRAGEM");
				item.qtMovimentacao = rSet.getInt("TOTAL_MOVIMENTACAO");
				item.qtInspecaoTotal = rSet.getInt("TOTAL_INSPECAO");

				listaServicos.add(item);
				placaServicoHolder.setQtCalibragemTotal(placaServicoHolder.getQtCalibragemTotal() + item.qtCalibragem);
				placaServicoHolder.setQtMovimentacaoTotal(placaServicoHolder.getQtMovimentacaoTotal() + item.qtMovimentacao);
				placaServicoHolder.setQtInspecaoTotal(placaServicoHolder.getQtInspecaoTotal() + item.qtInspecaoTotal);
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		placaServicoHolder.setListPlacas(listaServicos);
		return placaServicoHolder;
	}

	public ServicoHolder getServicosByPlaca (String placa) throws SQLException{
		ServicoHolder holder = new ServicoHolder();
		veiculoDao = new VeiculoDaoImpl();
		holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa));
		setServicos(holder);
		if(containInspecao(holder.getListServicos())){
			holder.setListAlternativaInspecao(getListAlternativasInspecao());
		}
		AfericaoDaoImpl afericaoDaoImpl = new AfericaoDaoImpl();
		holder.setRestricao(afericaoDaoImpl.getRestricoesByPlaca(placa));

		return holder;
	}

	private boolean containInspecao(List<Servico> listServicos){

		for (Servico servico : listServicos) {
			if (servico instanceof Inspecao) {
				return true;
			}
		}
		return false;
	}

	private List<PerguntaRespostaChecklist.Alternativa> getListAlternativasInspecao() throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<PerguntaRespostaChecklist.Alternativa> listAlternativas = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO A "
					+ "WHERE A.STATUS_ATIVO = TRUE");
			rSet = stmt.executeQuery();
			while(rSet.next()){
				PerguntaRespostaChecklist.Alternativa alternativa = new PerguntaRespostaChecklist.Alternativa();
				alternativa.codigo = rSet.getLong("CODIGO");
				alternativa.alternativa = rSet.getString("ALTERNATIVA");
				listAlternativas.add(alternativa);
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		return listAlternativas;
	}

	private void setServicos(ServicoHolder holder) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Servico> listServicos = new ArrayList<>();
		pneuDao = new PneuDaoImpl();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, V.KM,V.COD_UNIDADE AS COD_UNIDADE, "
					+ "A.CODIGO AS COD_AFERICAO, ITS.TIPO_SERVICO, ITS.QT_APONTAMENTOS, P.CODIGO, VP.POSICAO, MAP.NOME AS MARCA, "
					+ "MP.NOME AS MODELO, DP.*, P.* "
					+ "FROM AFERICAO_MANUTENCAO ITS "
					+ "JOIN PNEU P ON ITS.COD_PNEU = P.CODIGO "
					+ "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
					+ "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
					+ "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
					+ "JOIN AFERICAO A ON A.CODIGO = ITS.COD_AFERICAO "
					+ "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
					+ "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = ITS.COD_PNEU "
					+ "WHERE A.PLACA_VEICULO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL "
					+ "ORDER BY ITS.TIPO_SERVICO");
			stmt.setString(1, holder.getVeiculo().getPlaca());
			rSet = stmt.executeQuery();
			while(rSet.next()){

				String tipoServico = rSet.getString("TIPO_SERVICO");

				switch (tipoServico) {
				case Servico.TIPO_CALIBRAGEM:
					listServicos.add(createCalibragem(rSet));
					break;
				case Servico.TIPO_MOVIMENTACAO:
					listServicos.add(createMovimentacao(rSet));
					break;
				case Servico.TIPO_INSPECAO:
					listServicos.add(createInspecao(rSet));
					break;
				}
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		holder.setListServicos(listServicos);
	}

	private Calibragem createCalibragem(ResultSet rSet) throws SQLException{
		Calibragem calibragem = new Calibragem();
		calibragem.setPneu(pneuDao.createPneu(rSet));
		calibragem.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		calibragem.setTipo(rSet.getString("TIPO_SERVICO"));
		calibragem.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return calibragem;
	}

	private Inspecao createInspecao(ResultSet rSet) throws SQLException{
		Inspecao inspecao = new Inspecao();
		inspecao.setPneu(pneuDao.createPneu(rSet));
		inspecao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		inspecao.setTipo(rSet.getString("TIPO_SERVICO"));
		inspecao.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return inspecao;
	}

	private Movimentacao createMovimentacao(ResultSet rSet) throws SQLException{
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setPneu(pneuDao.createPneu(rSet));
		movimentacao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		movimentacao.setTipo(rSet.getString("TIPO_SERVICO"));
		movimentacao.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return movimentacao;
	}

	public boolean insertManutencao(Servico servico, Long codUnidade) throws SQLException {

		this.codUnidade = codUnidade;
		Connection conn = getConnection();
		pneuDao = new PneuDaoImpl();

		try{
			switch (servico.getTipo()) {
			case Servico.TIPO_CALIBRAGEM:
				insertCalibragem((Calibragem) servico, conn);
				break;
			case Servico.TIPO_INSPECAO:
				insertInspecao((Inspecao) servico, conn);
				break;
			case Servico.TIPO_MOVIMENTACAO:
				insertMovimentacao((Movimentacao) servico, conn);
				break;
			}
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally{
			closeConnection(conn, null, null);
		}
		return true;
	}

	private boolean insertCalibragem(Calibragem servico, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "COD_PNEU = ? "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfMecanico());
			stmt.setDouble(3, servico.getPneu().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculo());
			stmt.setLong(5, servico.getCodAfericao());
			stmt.setLong(6, servico.getPneu().getCodigo());
			stmt.setString(7, servico.getTipo());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updateCalibragem(servico.getPneu(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}
		return true;
	}

	private boolean insertInspecao(Inspecao servico, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ?, "
					+ "COD_ALTERNATIVA = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "COD_PNEU = ? "
					+ "STATUS_RESOLUCAO IS NULL "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfMecanico());
			stmt.setDouble(3, servico.getPneu().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculo());
			stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
			stmt.setLong(6, servico.getCodAfericao());
			stmt.setLong(7, servico.getPneu().getCodigo());
			stmt.setString(8, servico.getTipo());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updateCalibragem(servico.getPneu(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}
		return true;
	}


	private boolean insertMovimentacao(Movimentacao servico, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ?, "
					+ "COD_PNEU_INSERIDO = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "COD_PNEU = ? AND "
					+ "STATUS_RESOLUCAO IS NULL AND "
					+ "TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfMecanico());
			stmt.setDouble(3, servico.getPneuNovo().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculo());
			stmt.setLong(5, servico.getPneuNovo().getCodigo());
			stmt.setLong(6, servico.getCodAfericao());
			stmt.setLong(7, servico.getPneu().getCodigo());
			stmt.setString(8, servico.getTipo());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updateCalibragem(servico.getPneuNovo(), codUnidade, conn);

			if (servico.getPneu().getVidaAtual() == servico.getPneu().getVidasTotal()) {
				pneuDao.updateStatus(servico.getPneu(), codUnidade, Pneu.DESCARTE, conn);
			}else{
				pneuDao.updateStatus(servico.getPneu(), codUnidade, Pneu.RECAPAGEM, conn);
			}
			pneuDao.updateStatus(servico.getPneuNovo(), codUnidade, Pneu.EM_USO, conn);

			
		}finally {
			closeConnection(null, stmt, null);
		}
		return true;
	}


}
