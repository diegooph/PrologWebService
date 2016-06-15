package br.com.zalf.prolog.webservice.afericao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Restricao;
import br.com.zalf.prolog.models.pneu.afericao.Afericao;
import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.models.pneu.afericao.SelecaoPlacaAfericao;
import br.com.zalf.prolog.models.pneu.servico.Servico;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.veiculo.VeiculoDaoImpl;

public class AfericaoDaoImpl extends DatabaseConnection implements AfericaoDao{

	@Override
	public boolean insert (Afericao afericao, Long codUnidade) throws SQLException{
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		VeiculoDaoImpl veiculoDaoImpl = new VeiculoDaoImpl();
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO AFERICAO(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO) "
					+ "VALUES (?, ?, ?, ?) RETURNING CODIGO");
			stmt.setTimestamp(1, DateUtils.toTimestamp(afericao.getDataHora()));
			stmt.setString(2, afericao.getVeiculo().getPlaca());
			stmt.setLong(3, afericao.getCpfAferidor());
			stmt.setLong(4, afericao.getKmMomentoAfericao());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				afericao.setCodigo(rSet.getLong("CODIGO"));
				insertValores(afericao, codUnidade, conn);
				veiculoDaoImpl.updateKilometragem(afericao.getVeiculo().getPlaca(), afericao.getKmMomentoAfericao());
			}
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally {
			closeConnection(conn, stmt, rSet);
		}

		return true;
	}

	private void insertValores (Afericao afericao, Long codUnidade, Connection conn) throws SQLException{
		
		PreparedStatement stmt = null;
		PneuDaoImpl pneuDaoImpl = new PneuDaoImpl();

		stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
				+ "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, ALTURA_SULCO_CENTRAL,ALTURA_SULCO_EXTERNO, ALTURA_SULCO_INTERNO) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?)");
		for(Pneu pneu : afericao.getVeiculo().getListPneus()){
			stmt.setLong(1, afericao.getCodigo());
			stmt.setLong(2, pneu.getCodigo());
			stmt.setLong(3, codUnidade);
			stmt.setDouble(4, pneu.getPressaoAtual());
			stmt.setDouble(5, pneu.getSulcoAtual().getCentral());
			stmt.setDouble(6, pneu.getSulcoAtual().getExterno());
			stmt.setDouble(7, pneu.getSulcoAtual().getInterno());
			//Atualiza as informações de Sulco atual e calibragem atual na tabela Pneu do BD
			pneuDaoImpl.update(pneu, codUnidade, conn);
			stmt.executeUpdate();
			Restricao restricao = getRestricoesByCodUnidade(codUnidade);
			List<String> listServicosACadastrar = getServicosACadastrar(pneu, codUnidade, restricao);
			if(listServicosACadastrar.size() > 0){// verifica se o pneu tem alguma anomalia e deve ser inserido na base de serviços
				insertOrUpdateServico(pneu, afericao.getCodigo(), codUnidade, conn, listServicosACadastrar);
			}
		}

	}


	/**
	 * Cria uma lista com os serviços necessários que sejam cadastrados para o pneu verificado
	 * @param pneu
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	public List<String> getServicosACadastrar(Pneu pneu, Long codUnidade, Restricao restricao) throws SQLException{


		List<String> servicos = new ArrayList<>();

		if(pneu.getPressaoAtual() <= (pneu.getPressaoCorreta()*(1-restricao.getToleranciaInspecao()))){
			servicos.add(Servico.TIPO_INSPECAO);
		}else{
			if(pneu.getPressaoAtual() <= (pneu.getPressaoCorreta()*(1-restricao.getToleranciaCalibragem())) ||
					pneu.getPressaoAtual() >= (pneu.getPressaoCorreta()*(1+restricao.getToleranciaCalibragem()))){
				servicos.add(Servico.TIPO_CALIBRAGEM);
			}
		}
		if (pneu.getVidaAtual() == pneu.getVidasTotal()){//verifica se o pneu esta na ultima vida, o que reduz o limite de mm 
			if (pneu.getSulcoAtual().getCentral() <= restricao.getSulcoMinimoDescarte()) {// sulco atual é inferior ao minimo para descarte
				servicos.add(Servico.TIPO_MOVIMENTACAO);				// insere a movimentação na lista de serviços pendentes
			}
		}else{
			if (pneu.getSulcoAtual().getCentral() <= restricao.getSulcoMinimoRecape()) {// sulco atual é inferior ao minimo para recapar
				servicos.add(Servico.TIPO_MOVIMENTACAO);				// insere a movimentação na lista de serviços pendentes
			}
		}
		return servicos;
	}


	private void insertOrUpdateServico(Pneu pneu, long codAfericao, Long codUnidade, Connection conn, List<String> servicosPendentes) throws SQLException{

		List<String> servicosCadastrados = getServicosCadastradosByPneu(new Long(pneu.getCodigo()), codUnidade);

		for (String servicoPendente : servicosPendentes) {
			if(servicosCadastrados.contains(servicoPendente)){
				incrementaQtApontamentos(pneu, codUnidade, servicoPendente, conn);		
			}else{
				insertServico(pneu, codAfericao, servicoPendente, codUnidade, conn);
			}
		}
	}

	/**
	 * retorna uma lista com os serviços ja cadastrados para uma placa, resutados distintos, apenas serviços em aberto aparecerão
	 * @param codPneu
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	public List<String> getServicosCadastradosByPneu(Long codPneu, Long codUnidade)throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<String> listServico = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT TIPO_SERVICO, COUNT(TIPO_SERVICO) "
					+ "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND DATA_HORA_RESOLUCAO IS NULL "
					+ "GROUP BY TIPO_SERVICO "
					+ "ORDER BY TIPO_SERVICO");

			stmt.setLong(1, codPneu);
			stmt.setLong(2, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listServico.add(rSet.getString("TIPO_SERVICO"));
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}

		return listServico;
	}

	private void insertServico (Pneu pneu, long codAfericao, String tipoServico, Long codUnidade, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, COD_UNIDADE, TIPO_SERVICO)"
				+ " VALUES(?,?,?,?)");
		stmt.setLong(1, codAfericao);
		stmt.setLong(2, pneu.getCodigo());
		stmt.setLong(3, codUnidade);
		stmt.setString(4, tipoServico);
		stmt.executeUpdate();
	}

	private void incrementaQtApontamentos (Pneu pneu, Long codUnidade,String tipoServico, Connection conn) throws SQLException{

		PreparedStatement stmt = null;
		stmt = conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
				+ "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
				+ "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
		stmt.setLong(1, pneu.getCodigo());
		stmt.setLong(2, codUnidade);
		stmt.setString(3, tipoServico);
		stmt.setLong(4, pneu.getCodigo());
		stmt.setLong(5, codUnidade);
		stmt.setString(6, tipoServico);
		stmt.executeUpdate();
	}


	public Restricao getRestricoesByCodUnidade(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Restricao restricao = new Restricao();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM, ER.TOLERANCIA_CALIBRAGEM, ER.TOLERANCIA_INSPECAO "
					+ "FROM UNIDADE U JOIN "
					+ "EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
					+ "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO "
					+ "WHERE U.CODIGO = ?");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
				restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPAGEM"));
				restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
				restricao.setToleranciaInspecao(rSet.getDouble("TOLERANCIA_INSPECAO"));
			}else{
				new SQLException("Erro ao buscar os dados de restrição");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return restricao;
	}

	public Restricao getRestricoesByPlaca(String placa) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Restricao restricao = new Restricao();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM,ER.TOLERANCIA_INSPECAO, ER.TOLERANCIA_CALIBRAGEM "
					+ "FROM VEICULO V JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
					+ "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
					+ "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO "
					+ "WHERE V.PLACA = ?");
			stmt.setString(1, placa);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
				restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPAGEM"));
				restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
				restricao.setToleranciaInspecao(rSet.getDouble("TOLERANCIA_INSPECAO"));
			}else{
				new SQLException("Erro ao buscar os dados de restrição");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return restricao;
	}

	@Override
	public NovaAfericao getNovaAfericao(String placa) throws SQLException{
		VeiculoDaoImpl veiculoDaoImpl = new VeiculoDaoImpl();
		NovaAfericao afericaoHolder = new NovaAfericao();
		afericaoHolder.setVeiculo(veiculoDaoImpl.getVeiculoByPlaca(placa));
		if(afericaoHolder.getVeiculo().getPlaca() != null){
			afericaoHolder.setRestricao(getRestricoesByPlaca(placa));
			return afericaoHolder;
		}
		return new NovaAfericao();
	}
	
	public SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codEmpresa, Long codUnidade) throws SQLException{

		List<Veiculo> veiculos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Map<String, String> mapPlacasAfericao = new LinkedHashMap<>();
		int afericoes = 0;
		int metaAfericao = 20;
		int afericoesRealizadas = 0;
		String status = null;
		SelecaoPlacaAfericao selecaoPlacaAfericao = new SelecaoPlacaAfericao();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("	SELECT V.PLACA,M.NOME,coalesce(INTERVALO.INTERVALO, 0) as INTERVALO	"
					+ "FROM VEICULO V JOIN MODELO_VEICULO M ON M.CODIGO = V.COD_MODELO	"
					+ "LEFT JOIN (SELECT PLACA_VEICULO AS PLACA_INTERVALO,  EXTRACT(DAYS FROM ? -  MAX(DATA_HORA)) AS INTERVALO "
					+ "FROM AFERICAO "
					+ "GROUP BY PLACA_VEICULO) AS INTERVALO ON PLACA_INTERVALO = V.PLACA	"
					+ "WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ? "
					+ "ORDER BY M.NOME, INTERVALO DESC");
			
			stmt.setDate(1, DateUtils.toSqlDate(new java.util.Date(System.currentTimeMillis())));
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			
			
			
			
			
			
			
			
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		selecaoPlacaAfericao.setAfericoesRealizadas(afericoesRealizadas);
		selecaoPlacaAfericao.setMetaAfericao(metaAfericao);

		return selecaoPlacaAfericao;
	}
	
	
	public java.util.Date getPrimeiroDiaMes(Date date){

		Calendar first = Calendar.getInstance();
		first.setTime(DateUtils.toSqlDate(date));
		first.set(Calendar.DAY_OF_MONTH, 1);
		return new java.sql.Date(first.getTimeInMillis());
	}

	public java.util.Date getUltimoDiaMes(Date date){

		Calendar last = Calendar.getInstance();
		last.setTime(DateUtils.toSqlDate(date));
		last.set(Calendar.DAY_OF_MONTH, 1);
		last.add(Calendar.MONTH, 1);
		last.add(Calendar.DAY_OF_MONTH, -1);

		return new java.sql.Date(last.getTimeInMillis());
	}
}


